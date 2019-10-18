package network;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Topology {
	
	BufferedReader topologyRead;
	Node[] nodes;
	final int N_NODES;
	final double RANGE;
	
	
	public Topology(int n, double r) {
		
		topologyRead = null;
		N_NODES = n;
		RANGE = r;
	}
	
	public Node get(int id)
	{
		return nodes[id];
	}
	
	public void load(String dir) throws NumberFormatException, IOException
	{
		topologyRead = new BufferedReader(new FileReader(dir));
		nodes = new Node[N_NODES];	
		
		String st;
		if(topologyRead == null)
			System.out.println("nullo");
		while((st = topologyRead.readLine()) != null)
		{
			// controllo che la prima riga non sia #
			if(st.contains("#") || st.isEmpty())
				continue;
			
			// creo array di valori
			String[] v = st.split(" ");
			int id_t = Integer.parseInt(v[0]);
			double x_t = Double.parseDouble(v[1]);
			double y_t = Double.parseDouble(v[2]);
			double z_t = Double.parseDouble(v[3]);
			nodes[id_t] = new Node(id_t, x_t, y_t, z_t);
			//System.out.println("("+id_t+","+x_t+","+y_t+","+z_t+")\n");
		}
		
		// assigning neighbors
		for(int i = 0; i < N_NODES-1; i++)
		{
			for(int j = i+1; j < N_NODES; j++)
			{
				double dist = nodes[i].distance(nodes[j]);
				//System.out.println("distance before comparing with range = " + dist);
				if(dist <= RANGE)
				{				
					nodes[j].addNeighbor(nodes[i].id, nodes[i].x, nodes[i].y, nodes[i].z);
					nodes[i].addNeighbor(nodes[j].id, nodes[j].x, nodes[j].y, nodes[j].z);
				}
			}
			
			//System.out.println("nodes["+i+"].n " + nodes[i].n);
		}
	}
}
