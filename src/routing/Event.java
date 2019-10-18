package routing;

/*
/ Classe che definisce un evento in un nodo receiverNode da un fromNode
*/
public class Event
{
	public enum EventType {
		DATAPKT,
		ROUTINGPKT,
		TIMER;
	}
	int receiverId;
	int fromId;
	int hops;
	EventType type;
	Packet pkt;

	public Event(EventType t, int rId, int fId, int h, Packet p) {
		type = t;
		receiverId = rId;
		fromId = fId;
		hops = h;
		pkt = p;
		// TODO: OVVIAMENTE FROM ID E HOPS ANDRANNO SULLA CLASSE PACKET 
	}
}