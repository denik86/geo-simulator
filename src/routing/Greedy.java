package routing;

import network.Node;
import network.Topology;
import routing.Packet.PacketType;

public class Greedy extends Routing {

	public Greedy(Topology t, int s, int d, int maxH, int var) {
		super(t, s, d, maxH);
	}
	
	public void receive(Packet p, Node c)
	{
		//System.out.println("Node "+ c.id + " receives the "+p.toString());
		if(p.type == PacketType.DATA)
		{

			if(c.id == DESTINATION_ID)
				return;
		
			Node d = topo.get(DESTINATION_ID); 
			double minDist = c.distance(d);
			int min_id = NOTFOUND;
			
			// Greedy process
			for(int i = 0; i < c.n; i++)
			{
				int nId = c.getNeighborId(i);
				if(nId == d.id) {// case destination found
					min_id = nId;
					break;
				}
				double currDist = c.distanceNeighbor(i, d);
				if(currDist < minDist)
				{
					//System.out.println("minode found");
					minDist = currDist;
					min_id = nId;
					//hops++;
				}
			}
			
			// Metodi da implementare sempre
			p.nextId = min_id;
			//p.BROADCAST = true;
			send(p);
		}
	}

	

}
