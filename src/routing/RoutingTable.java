package routing;

import java.util.ArrayList;

class TableEntry {
	
	public int dest;
	public int next;
	public int hops;
	
	public TableEntry(int d, int n, int h)
	{
		dest = d;
		next = n;
		hops = h;
	}
	
	public boolean equals(TableEntry e)
	{
		if(e == null) return false;
		
		return (this.dest == e.dest && this.next == e.next);
	}
}


public class RoutingTable {
	
	ArrayList<TableEntry> rt;
	
	public RoutingTable()
	{
		rt = new ArrayList<TableEntry>();
	}
	
	public void addEntry(int d, int n, int h)
	{		
		for(int i = 0; i < rt.size(); i++)
		{
			TableEntry curr_e = rt.get(i);
			if(curr_e.dest == d)
			{
				if(curr_e.hops <= h) return;
				else rt.remove(i);
			}
		}
		TableEntry new_e = new TableEntry(d, n, h);
		rt.add(new_e);
	}
	
	public int getNext(int d)
	{
		
		for(int i = 0; i < rt.size(); i++)
		{
			TableEntry e = rt.get(i);
			if(e.dest == d)
				return e.next;
		}
		
		return -1;
	}

}
