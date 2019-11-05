package routing;

import network.Node;
import network.Topology;

public class Tabu extends Routing{
	
	int startTabuSize;
	int countWorse;
	double minReachedDist;
	boolean randomMode;
	boolean dinamic;
	boolean greedyEmpty;
	boolean inc1;
	int whenRandom;
	
	public Tabu(Topology t, int s, int d, int maxH, int ttlR) {
		super(t, s, d, maxH);
	}
	
	public void receive(Packet p, Node c)
	{
	}

}
