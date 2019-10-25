package routing;

import network.Node;
import network.Topology;
import routing.Packet.PacketType;


public class GCR extends Routing {
	
	public int nodesInvolved;
	public int ttlR;

	public GCR(Topology t, int s, int d, int maxH, int ttlR) {
		super(t, s, d, maxH);
		nodesInvolved = 0;
		this.ttlR = ttlR;
	}
		
	public void receive(Packet p, Node c)
	{
		if(!c.involved) {
			nodesInvolved++;
			c.involved = true;
		}
		if(p.type == PacketType.DATA)
		{
			System.out.println("-- Arrivato DATA "+ p);
			Node d = topo.get(DESTINATION_ID);
			if(c.id == DESTINATION_ID)
			{
				System.out.println("SUCESSSSSSSSSSSSS.");
				return;
			}
			
			// We are in the source node, so initialize the data packet 
			if(c.id == SOURCE_ID && p.getHops() == 0)
			{				
				p.addField("greedyMode", true);
				p.addField("srcX", c.x);
				p.addField("srcY", c.y);
				p.addField("srcZ", c.z);
				
				p.addField("dstX", d.x);
				p.addField("dstY", d.y);
				p.addField("dstZ", d.z);
				
				p.addField("closestDist", c.distance(d));
			}
			
			int nextNodeId = NOTFOUND;
			double minDist = c.distance(d);
			
			if((boolean)p.getField("greedyMode") == false)
			{
				nextNodeId = currentNode.rt.getNext(p.getDstId());
			}
			
			if(nextNodeId == NOTFOUND || (boolean)p.getField("greedyMode") == true)
			{
				// Greedy process
				
				for(int i = 0; i < c.n; i++)
				{
					int nId = c.getNeighborId(i);
					if(nId == d.id) {// case destination found
						nextNodeId = nId;
						break;
					}
					double currDist = c.distanceNeighbor(i, d);
					if(currDist < minDist)
					{
						//System.out.println("minode found");
						minDist = currDist;
						nextNodeId = nId;
						//hops++;
					}
				}
			}
			
			if(nextNodeId == NOTFOUND)
			{
				System.out.println("Closer node not found, send a RREQ");
				c.data = p;
				// create a broadcast RREQ Packet
				Packet broad = new Packet(c.id, -1);
				broad.addField("dstId", d.id);
				broad.addField("dstX", d.x);
				broad.addField("dstY", d.y);
				broad.addField("dstZ", d.z);
				broad.addField("minDist", minDist);
				broad.addField("ttl", ttlR);
				broad.addField("CGRType", "RREQ");
				broad.broad = true;
				//broad.nextId = BROADCAST;
				broad.type = PacketType.ROUTING;
				send(broad);
				
			} else
			{
			
				p.nextId = nextNodeId;
				//p.BROADCAST = true;
				send(p);
			}
		} // END - DATA PKT
		
		// ROUTING Packet
		else if(p.type == PacketType.ROUTING)
		{
			if(p.getField("CGRType") == "RREQ" && !c.RREQ_received)
			{
				c.RREQ_received = true;
				//System.out.println("["+currentNode.id+"]-- Arrivato un RREQ "+p);

				
				c.rt.addEntry(p.getSrcId(), p.getFromId(), p.getHops());
				
				if(c.id == (int)p.getField("dstId")) 
				{// i am the destination
					//System.out.println("-----SONO DESTINAZIONE-------------produco e invio un RREP");
					sendRREP(p.getSrcId(), (int)p.getField("dstId"));
						
					//........
					return;
				}
					
				double currDist = c.distance((double)p.getField("dstX"), (double)p.getField("dstY"), (double)p.getField("dstY"));
				
				//System.out.println(currDist + " e " +  (double)p.getField("minDist"));
				if(currDist < (double)p.getField("minDist"))
				{
					//System.out.println("---------produco e invio un RREP");
					sendRREP(p.getSrcId(), (int)p.getField("dstId"));
					return;
				}
				
				if(p.getHops() < (int)p.getField("ttl"))
				{
					//System.out.println("["+currentNode.id+"]-- -- invio ancora RREQ - "+p.broad);
					send(p);
				}
				
			} else if (p.getField("CGRType") == "RREP")
			{
				//System.out.print("["+currentNode.id+"] -- Arrivato un RREP ");
				int dest_id = (int)p.getField("dstId");
				currentNode.rt.addEntry(dest_id, p.getFromId(), p.getHops());
				if(currentNode.id == p.getDstId())
				{
					if(!currentNode.dataSent)
					{
						//System.out.println("Arrivato RREP da "+p.getSrcId()+" dove VOLEVO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						currentNode.dataSent = true;
						currentNode.data.nextId = currentNode.rt.getNext(dest_id);
						currentNode.data.updateField("greedyMode", false);
						send(currentNode.data);
					}
				}
				else {
					//System.out.println(" ... forwardo RREP");
					p.nextId = currentNode.rt.getNext(p.getDstId());
					send(p);
				}
				
			} else {
				
				//System.out.println("Getto il packet " +p);
				
			}
			
		}
		else
		{
			System.out.println("-- Paccketto ERROR TYPE");
		}
		
		
		
	} // END - receive()
		

	void sendRREP(int to_id, int data_dest_id)
	{
		Packet rrep = new Packet(currentNode.id, to_id);
		rrep.type = PacketType.ROUTING;
		rrep.addField("dstId", data_dest_id);
		rrep.addField("CGRType", "RREP");
		rrep.nextId = currentNode.rt.getNext(to_id);
		if(rrep.nextId == -1)
		{
			System.out.println("Error RREP cannot be sent");
			return;
		}
		send(rrep);
	}

	

}

