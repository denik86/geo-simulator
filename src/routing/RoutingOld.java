package routing;
import java.util.ArrayList;

import network.Node;
import network.Topology;
import trace.Trace;

public class RoutingOld {
		
	public enum State {
		SUCCESS,
		FAILED,
		NOTFINISHED;
	}
	static final int NOTFOUND = -1; 
	static final int ROUTINGERROR = -2;
	static String errormessage = "";
	Topology topo; // topology
	final int S_ID; // source id
	final int D_ID; // destination id
	int c_id; // current id
	final int MAX_HOPS; // max hops allowed
	int hops; // current hops
	ArrayList<Event> eventList;
	int eventId;
	ArrayList<Integer> packetSizes;
	State state;
	Trace trace;
	
	/* TODO ***********
	 * SI DOVREBBE FARE UNA LISTA CHE CONTIENE LA STORIA 
	 * DEL PROTOCOLLO LANCIATO, IN MODO POI DA STAMPARE
	 * LE COSE. ARRAYLIST DI UN TEMPLATE, DI UNA CLASSE
	 * CHE VIENE RIDEFINITA PER OGNI ROUTIN PROTOCOL
	 */
	
	public RoutingOld(Topology t, int s, int d, int maxH)
	{
		topo = t;
		S_ID = s;
		D_ID = d;
		MAX_HOPS = maxH;
		state = State.NOTFINISHED;
		hops = 0;
		c_id = S_ID;
		trace = new Trace();
		eventList = new ArrayList<Event>();
		eventId = 0;
		packetSizes = new ArrayList<Integer>();
	}
	
	void init() {
		// to override
		// for example to define templates of history
	}
	
	public void run() {

		System.out.println("sid " + c_id);
		
		while(state == State.NOTFINISHED)
		{	
			int next_id = nextNode();
		
			// check the next node and state
						
			// FAILED - ROUTING PROTOCOL ERROR
			if(next_id == ROUTINGERROR) {
				state = State.FAILED;
				System.out.println("PROTOCOL FAILED: " + errormessage);
			}
			
			// FAILED - NODE NOT FOUND
			else if(next_id == NOTFOUND) {
				state = State.FAILED;
				System.out.println("FAILED: Next node not found!");
			}
			
			// FAILED - NEXT NODE IS THE CURRENT NODE
			else if(next_id == c_id ) {
				state = State.FAILED;
				System.out.println("ROUTING ERROR: loop on node itself");
			}
			
			// Packet can be forwarded
			else {
				
				// SUCCESS - DESTINATION FOUND
				if(next_id == D_ID) {
					state = State.SUCCESS;
					System.out.println("SUCCESS");
				}
				
				// update (packet sent to the next node)
				hops++;
				packetSizes.add(calculatePacketSize());
				//trace.forward(topo.get(c_id), topo.get(next_id), hops, calculatePacketSize(), state);
				c_id = next_id;
			}
		}
	}
	
	// To override according to the routing protocol
	public int nextNode()
	{
		Node c = topo.get(c_id);
		Node d = topo.get(D_ID); 
		double minDist = c.distance(d);
		int min_id = NOTFOUND;
		
		// Greedy process
		for(int i = 0; i < c.n; i++)
		{
			int nId = c.getNeighborId(i);
			if(nId == d.id) // case destination found
				return nId;
			double currDist = c.distanceNeighbor(i, d);
			if(currDist < minDist)
			{
				//System.out.println("minode found");
				minDist = currDist;
				min_id = nId;
				//hops++;
			}
		}		
		return min_id;
	}


	public int getSuccess() {
		if(state == State.SUCCESS)
			return 1;
		return 0;
	}
	
	public boolean success() {
		if(state == State.SUCCESS)
			return true;
		return false;
	}
	
	public int getHops() {
		return hops;
	}
	
	// to override
	public int calculatePacketSize()
	{
		// S_ID (4 bytes)
		// D_ID (4 bytes)
		int size = 4 + 4;
		return size;
	}
	
	public ArrayList<Integer> getPacketSizes() {
		return packetSizes;
	}
	
	public int getHopPacketSize(int i) {
		return packetSizes.get(i-1);
	}
}
