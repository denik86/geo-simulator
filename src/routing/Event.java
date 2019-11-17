package routing;

/*
/ Classe che definisce un evento in un nodo receiverNode da un fromNode
*/
public class Event
{
	public enum EventType {
		PACKETRECEIVE,
		TIMER;
	}
	int nodeId;
	EventType type;
	Packet pkt;
	double time;
	
	int var1;
	double var2;
	
	// nodeId current node of the event

	public Event(EventType t, int nId, Packet p, double time, double v2) {
		type = t;
		nodeId = nId;
		pkt = p;
		this.time = time;
		var2 = v2;
		// TODO: OVVIAMENTE FROM ID E HOPS ANDRANNO SULLA CLASSE PACKET 
	}
	
	public String toString()
	{
		String s = "Event: " + type + ", node=" + nodeId + ", pkt=" + pkt + ", time=" + time;
		return s;
	}
}