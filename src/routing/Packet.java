package routing;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


class PacketException extends Exception {

	String message;
	   public PacketException(String m) { message = m; }

	   public String toString() {
	      return message;
	   }
	}

public class Packet
{
	private int srcId; // source node id
	private int dstId; // destination node id
	
	private int fromId; // forwarder node id
	public int nextId; // next node id
	
	private int hops;
	public PacketType type;
	
	public boolean broad;
	
	
	private Map<String, Object> fields;
	
	public enum PacketType {
		DATA,
		ROUTING,
		ACK;
	}
	
	/**
	 * Creates a new Packet.
	 * @param sourceId The source node
	 * @param destinationId The destination node
	 * @param fromNodeId The node from which the packet comes
	 */
	public Packet(int sourceId, int destinationId, int fromNodeId)
	{
		srcId = sourceId;
		dstId = destinationId;
		fromId = fromNodeId;
		hops = 0;
		broad = false;
		fields = null;
	}
	
	/**
	 * Creates a new Packet copying another packet's fields.
	 * @param p The packet from which the fields are copied
	 */
	public Packet(Packet p)
	{
		this.srcId = p.srcId;
		this.dstId = p.dstId;
		this.nextId = p.nextId;
		this.fromId = p.fromId;
		this.hops = p.hops;
		this.type = p.type;
		this.broad = p.broad;
		
		if (p.fields != null) {
			Set<String> keys = p.fields.keySet();
			for (String key : keys) {
				updateField(key, p.getField(key));
			}
		}
	}
	
	/**
	 * Adds a new field for this packet. The key can be any string but 
	 * it should be such that no other class accidently uses the same value.
	 * The value can be any object but it's good idea to store only immutable
	 * objects because when packet is replicated, only a shallow copy of the
	 * fields is made.  
	 * @param key The key which is used to lookup the value
	 * @param value The value to store
	 * @throws SimError if the packet already has a value for the given key
	 */
	public void addField(String key, Object value) throws SimError {
		if (this.fields != null && this.fields.containsKey(key)) {
			/* check to prevent accidental name space collisions */
			throw new SimError("Packet " + this + " already contains value " + 
					"for a key " + key);
		}
		
		this.updateField(key, value);
	}
	
	/**
	 * Returns an object of field using the given key.
	 * If such object is not found, null is returned.
	 * @param key The key used to lookup the object
	 * @return The stored object or null if it isn't found
	 */
	public Object getField(String key) {
		if (this.fields == null) {
			return null;
		}
		return this.fields.get(key);
	}
	
	/**
	 * Updates a value for an existing field. For storing the value first 
	 * time, {@link #addField(String, Object)} should be used which
	 * checks for name space clashes.
	 * @param key The key which is used to lookup the value
	 * @param value The new value to store
	 */
	public void updateField(String key, Object value) {
		if (this.fields == null) {
			/* lazy creation to prevent performance overhead for classes
			   that don't use the property feature  */
			this.fields = new HashMap<String, Object>();
		}		

		this.fields.put(key, value);
	}
	
	
	public Packet(int sourceId, int destinationId)
	{
		this(sourceId, destinationId, sourceId);
	}
	
	public Packet()
	{
		this(-1, -1, -1);
	}
	
	public int getSrcId()
	{
		return srcId;
	}

	public int getDstId()
	{
		return dstId;
	}
	public int getFromId()
	{
		return fromId;
	}
	
	public void setFromId(int id)
	{
		fromId = id;
	}
	
	public int getHops()
	{
		return hops;
	}
	
	public int incrHops()
	{
		hops++;
		return hops;
	}
	
	public String toString()
	{
		String s = "";
		if(broad)
			s = "(B) ";
		s += "PACKET (S="+srcId+", D="+dstId+" | "+fromId+"->"+nextId+" | H="+hops+")";
		return s;
	}

	
	
	
	
}