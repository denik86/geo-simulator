package trace;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import network.Node;
import routing.Routing.State;

public class Trace {
	
	class TRecord {
		char action;
		Node m_node; // main node
		Node p_node; // possible second node (- if no node)
		int hop; // number of hop
		int packetSize;
		State state;
	}
	
	ArrayList<TRecord> traceList;
	
	public Trace()
	{
		traceList = new ArrayList<TRecord>();
	}
	
	/*
	 * Forward action
	 * forwarder, receiver, number of hop, packet size, state
	 */
	public void forward(Node forw, Node recv, int hop, int size, State state)
	{
		TRecord tr = new TRecord();
		tr.action = 'f';
		tr.m_node = forw;
		tr.p_node = recv;
		tr.hop = hop;
		tr.packetSize = size;
		tr.state = state;
		
		traceList.add(tr);
	}
	
	/*
	 * Receive action
	 * receiver, from node, number of hop, packet size, state
	 */
	public void receive(Node recv, Node from, int hop, int size, State state)
	{
		TRecord tr = new TRecord();
		tr.action = 'r';
		tr.m_node = recv;
		tr.p_node = from;
		tr.hop = hop;
		tr.packetSize = size;
		tr.state = state;
		
		traceList.add(tr);
	}
	
	public void process(Node node)
	{
		TRecord tr = new TRecord();
		tr.action = 'p';
		tr.m_node = node;
		tr.p_node = null;
		
		traceList.add(tr);
	}
	
	public void printTrace(String dir, boolean dim2D)
	{
		BufferedWriter traceFile = null;
		try {
			traceFile = new BufferedWriter(new FileWriter(dir));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(TRecord tr : traceList)
		{
			if(dim2D)
			{
				try {
					traceFile.write(tr.action + " " + 
									tr.m_node.id + " " + tr.m_node.x + " " + tr.m_node.y + " " + 
									tr.p_node.id + " " + tr.p_node.x + " " + tr.p_node.y + " " +
									tr.hop + " " + tr.packetSize + " " + tr.state.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	
	}

}
