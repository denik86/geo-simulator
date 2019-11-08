package routing;

import network.Node;
import network.Topology;

public class Tabu extends Routing{
	
	int startTabuSize;
	int countWorse; // va nel pacchetto
	double minReachedDist; // va nel pacchetto
	boolean randomMode; // va nel pacchetto
	public boolean dinamic = false;
	public boolean greedyEmpty = false;
	public boolean inc1 = false;

	double infinite = 999999;
	int whenRandom;

	TabuList tabuList;
	
	public Tabu(Topology t, int s, int d, int maxH, int startTabuSize) {
		super(t, s, d, maxH);
		this.startTabuSize = startTabuSize;
		whenRandom = 2 * startTabuSize;
		tabuList = new TabuList(startTabuSize);
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


		}
	}

}
