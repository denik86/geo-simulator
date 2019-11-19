package routing;

import java.util.Random;

import network.Node;
import network.Topology;
import routing.Packet.PacketType;

public class Tabu extends Routing{
	
	int startTabuSize;

	public boolean dinamic = false;
	public boolean greedyEmpty = false;
	public boolean inc1 = false;

	double infinite = 999999;
	int whenRandom;


	
	public Tabu(Topology t, int s, int d, int maxH, int startTabuSize) {
		super(t, s, d, maxH);
		this.startTabuSize = startTabuSize;
		whenRandom = 2 * startTabuSize;
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
				p.addField("dstX", topo.get(DESTINATION_ID).x);
				p.addField("dstY", topo.get(DESTINATION_ID).y);
				p.addField("dstZ", topo.get(DESTINATION_ID).z);
				p.addField("countWorse", 0);
				p.addField("minReachedDist", 99999.0);
				p.addField("randomMode", false);
				p.addField("tabu", new TabuList(startTabuSize));
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
			
			boolean randomMode = (boolean) p.getField("randomMode");
			
			// vicinato vuoto (nessun nodo disponibile) resetto tutto
			if(freeNodes == 0)
			{
				tl.clear();
				tl.add(c.id);
				p.updateField("tabu", tl);
				freeNodes = c.n;
			}
			
			// se siamo in random e i nodi liberi sono più di 1
			if(randomMode && freeNodes > 1)
			{
				Random random = new Random();
				int rand_i = random.nextInt(c.n-1);
				while(tl.check(c.getNeighborId(rand_i)))
					rand_i = random.nextInt(c.n-1);
				
				minDist = c.distanceNeighbor(rand_i, dstX, dstY, dstZ);
				nextNodeId = c.getNeighborId(rand_i);
			}
			
			// invece se siamo in greedy oppure abbiamo solo un nodo libero
			else
			{
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
			}
			
			// se vicinato non vuoto (ho trovato un next node valido)
			if(nextNodeId != c.id)
			{
				double minReachedDist = (double) p.getField("minReachedDist");
				int countWorse = (int) p.getField("countWorse");
				if(minDist < minReachedDist) {
					minReachedDist = minDist;
					countWorse = 0;
					tl.resize(startTabuSize);
					randomMode = false;
				}
				else
				{
					countWorse++;
					if(countWorse > tl.size)
					{
						tl = tl.resize(tl.size * 2);
					}
				}
				
				if(countWorse > whenRandom)
					randomMode = true;
				
				// aggiorno i campi
				p.updateField("countWorse", countWorse);
				p.updateField("tabu", tl);
				p.updateField("randomMode", randomMode);
				p.updateField("minReachedDist", minReachedDist);
			}
			
			//  vicinato vuoto (nessun nodo valido)
			else
			{
				System.out.println("non dovrebbe andare qui!!!");
			}
			
			p.nextId = nextNodeId;
			//p.BROADCAST = true;
			send(p);
		}
	}

}
