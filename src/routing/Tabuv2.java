package routing;

import java.util.Random;

import network.Node;
import network.Topology;
import routing.Packet.PacketType;

public class Tabuv2 extends Routing{
	
	int startTabuSize;

	public boolean linear = true; // tabu list increase linear
	public int factor = 2; // tabu list size multiplier

	double infinite = 99999.0;
	int whenRandom;

// version of tabu where tabu list is used only when we are sure that 
//a node is certainly not useful
	
	public Tabuv2(Topology t, int s, int d, int maxH, int startTabuSize) {
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
				//System.out.println("Sono il nodo sorgente");
				p.addField("dstX", topo.get(DESTINATION_ID).x);
				p.addField("dstY", topo.get(DESTINATION_ID).y);
				p.addField("dstZ", topo.get(DESTINATION_ID).z);
				p.addField("countWorse", 0);
				p.addField("minReachedDist", 99999.0);
				p.addField("randomMode", false);
				p.addField("tabu", new TabuList(startTabuSize));
				p.addField("ptabu", new TabuList(3));
			}
			
			// add this node to tabulist
			TabuList tl = (TabuList) p.getField("tabu");
			tl.add(c.id);
			p.updateField("tabu", tl);

			TabuList ptl = (TabuList) p.getField("ptabu");
					
			double minDist = 999999;
			int nextNodeId = c.id;
			
			double dstX = (double) p.getField("dstX");
			double dstY = (double) p.getField("dstY");
			double dstZ = (double) p.getField("dstZ");

/*
			// verify if im in a dead end, if yes i add my id in the permanent tabu list
			int freeNodes = c.n;
			for(int i = 0; i < c.n; i++)
			{
				if(ptl.check(c.getNeighborId(i)))
					freeNodes--;
			}
			if(freeNodes == 1)
			{
				ptl.add(c.id);
				p.updateField("ptabu", ptl);
			}
			*/
			// count how many free neighbors I have (not in tabu)
			int freeNodes = c.n;
			for(int i = 0; i < c.n; i++)
			{
				if(tl.check(c.getNeighborId(i)) || ptl.check(c.getNeighborId(i)))
					freeNodes--;
			}
			
			boolean randomMode = (boolean) p.getField("randomMode");
			
			// vicinato vuoto (nessun nodo disponibile) resetto tutto
			if(freeNodes == 0)
			{
				tl.clear();
				tl.add(c.id);
				p.updateField("tabu", tl);
				ptl.add(c.id);
				p.updateField("ptabu", ptl);
				freeNodes = c.n;
				for(int i = 0; i < c.n; i++) // remove those in permanent list
				{
					if(ptl.check(c.getNeighborId(i)))
						freeNodes--;
				}
			}
			
			// se siamo in random e i nodi liberi sono piu di 1
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
					if(tl.check(c.getNeighborId(i)) || ptl.check(c.getNeighborId(i)) )
						continue;
					double currDist = c.distanceNeighbor(i, dstX, dstY, dstZ);
					if(currDist < minDist)
					{
						minDist = currDist;
						nextNodeId = c.getNeighborId(i);
					}
				}
			}
			
			// se ho trovato un next node valido
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
						if(linear)
							tl = tl.resize(tl.size + this.startTabuSize);
						else
							tl = tl.resize(tl.size*factor);
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
			//System.out.println("["+c.id+"], invio a " +nextNodeId);
			p.nextId = nextNodeId;
			//p.BROADCAST = true;
			send(p);
			dataMemory += tl.size*4;
		}
	}

}
