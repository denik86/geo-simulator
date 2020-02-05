package routing;

import java.util.Random;

import network.Node;
import network.Topology;
import routing.Packet.PacketType;

public class TabuStatic extends Routing{
	
	int tabuSize;

	public boolean greedyEmpty = false;

	double infinite = 99999.0;


	public TabuStatic(Topology t, int s, int d, int maxH, int tabuSize) {
		super(t, s, d, maxH);
		this.tabuSize = tabuSize;
	}
	
	public void receive(Packet p, Node c)
	{
		if(p.type == PacketType.DATA)
		{
			if(c.id == p.getDstId())
			{
				//System.out.println("SUCESSSSSSSSSSSSS.");
				return;
			}
			
			if(c.id == SOURCE_ID && p.getHops() == 0)
			{
				//System.out.println("Sono il nodo sorgente");
				p.addField("dstX", topo.get(DESTINATION_ID).x);
				p.addField("dstY", topo.get(DESTINATION_ID).y);
				p.addField("dstZ", topo.get(DESTINATION_ID).z);
				p.addField("minReachedDist", 99999.0);
				p.addField("tabu", new TabuList(tabuSize));
			}
			
			// add this node to tabulist
			TabuList tl = (TabuList) p.getField("tabu");
			tl.add(c.id);
			p.updateField("tabu", tl);
				
			double minDist = 999999;
			int nextNodeId = c.id;
			
			double dstX = (double) p.getField("dstX");
			double dstY = (double) p.getField("dstY");
			double dstZ = (double) p.getField("dstZ");
			
			// count how many free neighbors I have (not in tabu)
			int freeNodes = c.n;
			for(int i = 0; i < c.n; i++)
			{
				if(tl.check(c.getNeighborId(i)))
					freeNodes--;
			}

					
			// vicinato vuoto (nessun nodo disponibile) resetto tutto
			if(freeNodes == 0)
			{
				tl.clear();
				tl.add(c.id);
				p.updateField("tabu", tl);
				freeNodes = c.n;
			}
			
			// cerco il migliore
			for(int i = 0; i < c.n; i++)
			{
				if(tl.check(c.getNeighborId(i)))
					continue;
				double currDist = c.distanceNeighbor(i, dstX, dstY, dstZ);
				if(currDist < minDist)
				{
					minDist = currDist;
					nextNodeId = c.getNeighborId(i);
				}
			}
			
			// se vicinato non vuoto (ho trovato un next node valido)
			if(nextNodeId != c.id)
			{
				double minReachedDist = (double) p.getField("minReachedDist");
				if(minDist < minReachedDist) {
					minReachedDist = minDist;
				}

				// aggiorno i campi
				p.updateField("minReachedDist", minReachedDist);
				p.updateField("tabu", tl);
			}
			
			//  vicinato vuoto (nessun nodo valido)
			else
			{
				System.out.println("non dovrebbe andare qui!!!");
			}
			//System.out.println("["+c.id+"], invio a " +nextNodeId);
			p.nextId = nextNodeId;
			//p.BROADCAST = true;
			dataMemory += tabuSize*4;
			send(p);
		}
	}

}
