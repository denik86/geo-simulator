package routing;

import network.Node;
import network.Topology;


public class GreedyRouting extends Routing {

	public GreedyRouting(Topology t, int s, int d, int maxH) {
		super(t, s, d, maxH);
	}
	
	/*
	 * TODO BISOGNA CREARE UN MODO PER POTER INCLUDERE DELLE
	 * STRUTTURE DATI NEI NODE. SE AD ESEMPIO VOGLIAMO UNA ROUTING
	 * TABLE, OGNI NODO DEVE AVERE UNA ROUTING TABLE, QUINDI
	 * CI SARA UN CICLO FOR CHE AGGIUNGE UNA RT IN OGNI NODO
	 * DELLA TOPOLOGIA. (non-Javadoc)
	 * @see routing.Routing#receive(routing.Packet, network.Node)
	 */
	
	public void receive(Packet p, Node c)
	{
		// TODO SPIEGARE QUI CHE BISOGNA FARE UN 
		// TEST PER CAPIRE SE SIAMO IL SOURCE NODE O NO
		// SE LO SIAMO, ALLORA BISOGNA INIZIALIZZARE I CAMPI
		// DEL PACCHETTO E LE PROPRIETÀ DEL NODO.
		System.out.println("Node "+ c.id + " receives the "+p.toString());
		
		
		if(c.id == DESTINATION_ID)
		{
			System.out.println("SUCESSSSSSSSSSSSS.");
			return;
		}
		
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
