package routing;

import network.Node;
import network.Topology;
import routing.Packet.PacketType;


public class AODV extends Routing {
	
	public int nodesInvolved;

	public AODV(Topology t, int s, int d, int maxH, int b) {
		super(t, s, d, maxH);
		nodesInvolved = 0;
	}
		
	public void receive(Packet p, Node c)
	{

		if(p.type == PacketType.DATA)
		{
			System.out.println("-- Arrivato DATA "+ p);
			Node d = topo.get(DESTINATION_ID);
			if(c.id == DESTINATION_ID)
			{
				//System.out.println("SUCESSSSSSSSSSSSS.");
				return;
			}
			
			// We are in the source node, so I send the RREQ 
			if(c.id == SOURCE_ID && p.getHops() == 0)
			{
				//System.out.println("Send a RREQ packet");
				c.RREQ_id = 1;
				c.data = p; // memorizzo il pacchetto nel nodo
				Packet broad = new Packet(c.id, -1);
				broad.addField("dstId", d.id);
				broad.addField("AODVType", "RREQ");
				broad.broad = true;
				broad.type = PacketType.ROUTING;
				send(broad);
				return;
			}
			
			int nextNodeId = NOTFOUND;
			nextNodeId = currentNode.rt.getNext(p.getDstId());

			
			if(nextNodeId == NOTFOUND)
			{
				System.out.println("something wrong with AODV: next node not found");

			}
					
			p.nextId = nextNodeId;
			send(p);
			
		} // END - DATA PKT
		
		// ROUTING Packet
		else if(p.type == PacketType.ROUTING)
		{
			if(p.getField("AODVType") == "RREQ" && c.RREQ_id < 0)
			{
				c.RREQ_id = 1; // flag che un RREQ è arrivato. Se ricevo ancora, non lo processo più.
				c.rt.addEntry(p.getSrcId(), p.getFromId(), p.getHops());
				
				if(c.id == (int)p.getField("dstId")) 
				{// i am the destination
					System.out.println("["+currentNode.id+"]-- RREQ "+p+ "-----SONO DESTINAZIONE-------------produco e invio un RREP");					
					Packet rrep = new Packet(currentNode.id, p.getSrcId());
					rrep.type = PacketType.ROUTING;
					rrep.addField("AODVType", "RREP");
					rrep.nextId = currentNode.rt.getNext(p.getSrcId());
					if(rrep.nextId == -1)
					{
						System.out.println("Error RREP cannot be sent");
						return;
					}
					send(rrep);
					return;
				} else
				{
					send(p);
				}
									
			} else if (p.getField("AODVType") == "RREP")
			{
				//System.out.print("["+currentNode.id+"] -- RREP arrivato " + p);
				
				//currentNode.RREQ_id = (int)p.getField("nMin");
				
				int dest_id = p.getSrcId();
				currentNode.rt.addEntry(dest_id, p.getFromId(), p.getHops());
				if(currentNode.id == p.getDstId()) // se sono colui che ha inviato il RREQ
				{
					if(!currentNode.dataSent)
					{
						//System.out.println("Arrivato RREP da "+p.getSrcId()+" dove VOLEVO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						currentNode.dataSent = true;
						currentNode.data.nextId = currentNode.rt.getNext(dest_id);
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
			System.out.println("-- Paket ERROR TYPE");
		}
		
		
		
	} // END - receive()
}

