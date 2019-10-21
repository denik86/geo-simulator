package routing;
import java.util.ArrayList;

import network.Node;
import network.Topology;
import routing.Event.EventType;
import routing.Packet.PacketType;
import trace.Trace;

public class Routing {
		
	public enum State {
		SUCCESS,
		FAILED,
		NOTFINISHED;
	}
	static final int NOTFOUND = -1; 
	static final int ROUTINGERROR = -2;
	static final int BROADCAST = -3;
	static String errormessage = "";
	Topology topo; // topology
	final int SOURCE_ID; // source id
	final int DESTINATION_ID; // destination id
	Node currentNode; // current node
	final int MAX_HOPS; // max hops allowed
	ArrayList<Event> eventList;
	int eventId;
	Event e;
	ArrayList<Integer> packetSizes;
	State state;
	Trace trace;
	
	public int hops;
	
	public Routing(Topology t, int s, int d, int maxH)
	{
		topo = t;
		SOURCE_ID = s;
		DESTINATION_ID = d;
		MAX_HOPS = maxH;
		state = State.NOTFINISHED;
		currentNode = topo.get(SOURCE_ID);
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

		System.out.println("sid " + currentNode.id);
		
		// Create the packet from source node
		Packet p = new Packet(SOURCE_ID, DESTINATION_ID, -1);
		p.type = PacketType.DATA;
		p.nextId = SOURCE_ID;

		init();
		// First event: source node receive pkt from upper layer
		Event first_e = new Event(EventType.PACKETRECEIVE, SOURCE_ID, p, -1, -1);
		addEvent(first_e);
		
		while(state == State.NOTFINISHED)
		{	
			if(eventId >= eventList.size())
			{
				System.out.println("!!! EventListener Empty !!!");
				System.exit(1);
			}
			//System.out.println("event list size = " + eventList.size());
			e = eventList.get(eventId);
			eventId++;
			
			// Node in which event happens
			currentNode = topo.get(e.nodeId);
			
			// -------- PACKET IS BEING RECEIVED --------------------------------------------
			if(e.type == EventType.PACKETRECEIVE)
			{
				p = e.pkt;
				int nextId = p.nextId;
				if(!p.BROADCAST && currentNode.id != nextId) {
					System.out.println("ROUTING ERROR: current node is different than the node that received the packet.");
					System.exit(1);
				}
				
				// Check nodes connectivity
				if(p.getFromId() > -1 && topo.get(p.getFromId()).distance(topo.get(nextId)) > topo.getRange())
				{
					System.out.println("Connection bewteen nodes does not exists.");
					continue;
				}
				

				receive(p, topo.get(p.nextId));
				
				// DESTINATION REACHED - STOP
				if(nextId == DESTINATION_ID) {
					state = State.SUCCESS;
					System.out.println("=== Packet delivered. Simulation STOP ===");
					hops = p.getHops();
					return;
				} 
					
				//packetSizes.add(calculatePacketSize());
				//trace.forward(topo.get(c_id), topo.get(nextNodeId), hops, calculatePacketSize(), state);
					
				// EXTRACT PACKET FROM EVENT
							
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

	// TODO: il metodo deve avere anche Node n come parametro. cosi il metodo usa anche le info memorizzate
	// in Node per scegliere il next node (es. routing table).
	// To override according to the routing protocol
	public void receive(Packet p, Node c)
	{
		System.out.println("\n--- ERROR: method 'receive()' must be overriden. ---\n");
		throw new UnsupportedOperationException();
	}

	public void send(Packet p)
	{
		
		// Set of fromId (the current node of this event)
		p.setFromId(e.nodeId);
			
		if(p.BROADCAST) {
			System.out.println("Avvio un broadcast");
			p.incrHops();
			for(int i = 0; i < currentNode.n; i++)
			{
				//System.out.println("Invio a nodo " + i);
				Packet broadPkt = new Packet(p);
				broadPkt.nextId = currentNode.getNeighborId(i);
				Event recvEvent = new Event(EventType.PACKETRECEIVE, broadPkt.nextId, broadPkt, -1, -1);
				addEvent(recvEvent);
			}
			return;
		}
		
		// FAILED - ROUTING PROTOCOL ERROR
		if(p.nextId == ROUTINGERROR) {
			state = State.FAILED;
			System.out.println("ROUTING ERROR: " + errormessage);
			return;
		}
		
		// FAILED - NODE NOT FOUND
		if(p.nextId == NOTFOUND) {
			state = State.FAILED;
			System.out.println("FAILED: Next node not found!");
			return;
		}
		
		// FAILED - NEXT NODE IS THE CURRENT NODE
		else if(p.nextId == p.getFromId()) {
			state = State.FAILED;
			System.out.println("ROUTING ERROR: loop on node itself");
			return;
		}
		
		p.incrHops();
		Event recvEvent = new Event(EventType.PACKETRECEIVE, p.nextId, p, -1, -1);
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
