package routing;
import java.util.ArrayList;

import network.Node;
import network.Topology;
import routing.Event.EventType;
import trace.Trace;

public class Routing {
		
	public enum State {
		SUCCESS,
		FAILED,
		NOTFINISHED;
	}
	static final int NOTFOUND = -1; 
	static final int ROUTINGERROR = -2;
	static String errormessage = "";
	Topology topo; // topology
	final int SOURCE_ID; // source id
	final int DESTINATION_ID; // destination id
	int c_id; // current id
	final int MAX_HOPS; // max hops allowed
	int hops; // current hops
	ArrayList<Event> eventList;
	int eventId;
	Event e;
	ArrayList<Integer> packetSizes;
	State state;
	Trace trace;
	
	public Routing(Topology t, int s, int d, int maxH)
	{
		topo = t;
		SOURCE_ID = s;
		DESTINATION_ID = d;
		MAX_HOPS = maxH;
		state = State.NOTFINISHED;
		hops = 0;
		c_id = SOURCE_ID;
		trace = new Trace();
		eventList = new ArrayList<Event>();
		eventId = 0;
		e = null;
		packetSizes = new ArrayList<Integer>();
	}
	
	// metho executed at first step (source node)
	public void init() {
		// to override
	}
	
	public void run() {

		System.out.println("sid " + c_id);
		Packet p = new Packet();

		init();
		// First event: source node receive pkt from upper layer
		Event first_e = new Event(EventType.DATAPKT, SOURCE_ID, -1, 0, p);
		addEvent(first_e);
		
		while(state == State.NOTFINISHED)
		{	
			System.out.println("event list size = " + eventList.size());
			e = eventList.get(eventId);
			eventId++;
			
			int nextNodeId = e.receiverId;
			int fromNodeId = e.fromId;
			
			// -------- DATA PACKET --------------------------------------------
			if(e.type == EventType.DATAPKT)
			{		
				// check the next node and state
				
				// FAILED - ROUTING PROTOCOL ERROR
				if(nextNodeId == ROUTINGERROR) {
					state = State.FAILED;
					System.out.println("PROTOCOL FAILED: " + errormessage);
				}
				
				// FAILED - NODE NOT FOUND
				else if(nextNodeId == NOTFOUND) {
					state = State.FAILED;
					System.out.println("FAILED: Next node not found!");
				}
				
				// FAILED - NEXT NODE IS THE CURRENT NODE
				else if(nextNodeId == fromNodeId) {
					state = State.FAILED;
					System.out.println("ROUTING ERROR: loop on node itself");
				}
				
				// Packet can be received by receiverNode
				else {
					
					// SUCCESS - DESTINATION FOUND
					if(nextNodeId == DESTINATION_ID) {
						state = State.SUCCESS;
						System.out.println("SUCCESS");
					}
					
					//packetSizes.add(calculatePacketSize());
					//trace.forward(topo.get(c_id), topo.get(nextNodeId), hops, calculatePacketSize(), state);
					
					// EXTRACT PACKET FROM EVENT
					Packet recvP = e.pkt;
					receive(recvP, e);
				}	
			}
			
			// -------- OTHER EVENT TYPES ----------------------------------------
			else
			{
				//TODO	
			}

		}
	}
	
	public void addEvent(Event e)
	{
		eventList.add(e);
	}

	// To override according to the routing protocol
	public void receive(Packet p, Event e)
	{
		// TODO HERE
		// SISTEMARE QUESTO METODO BISOGNA FARE UN ALTRO METODO DA OVERRIDARE. PACCHETTO? UTENTE LO USA
		// PER PRENDERE LE INFO. FROM NODE? DOVE? VIENE MESSO NEL PKT? PENSO NO. CLASSE HEADER DA ESTENDERE IN BASE A
		// PROTOCOLLO.
		// QUINDI RECEIVER E SEND DA OVERRIDE, IL NEXT NODE VA NEL PKT, E POI SEND(PKT).
		Node c = topo.get(e.receiverId);
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
		send(p, min_id);	
	}

	// TODO IMPLEMENTARE I TRE METODI SEND
	// TODO INSERIRE PACKET.TYPE NEL PACCHETTO
	// TODO QUINDI POI METTERE EVENT.TYPE COME TIPO EVENTO
	public void sendBroadcast(Packet p)
	{

	}

	public void sendMulticast(Packet p, ArrayList<Integer> toNodeIds)
	{

	}

	public void send(Packet p, int toNodeId)
	{
		Event recvEvent = new Event(EventType.DATAPKT, toNodeId, e.receiverId, e.hops++, p);
		addEvent(recvEvent);
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
