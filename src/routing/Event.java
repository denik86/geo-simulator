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
	
	int var1;
	double var2;
	
	// nodeId current node of the event

	public Event(EventType t, int nId, Packet p, int v1, double v2) {
		type = t;
		nodeId = nId;
		pkt = p;
		var1 = v1;
		var2 = v2;
		// TODO: OVVIAMENTE FROM ID E HOPS ANDRANNO SULLA CLASSE PACKET 
	}
}