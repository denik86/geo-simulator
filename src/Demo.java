/* package whatever; // don't place package name! */

import java.util.*;

import network.Node;
import network.Topology;
import routing.*;

//import java.lang.*;
import java.io.*;

class Config //extends JPanel
{
	int N_NODES;
	double MAX_X;
	double MAX_Y;
	double MAX_Z;
	double RANGE;
	final int MAXTABU = 1000; // max number of nodes in tabu list
	int tabuSize;
	public int ttl;
	String topo_dir;
	final int FAILHOPS = 100;
	
	// STATS VARIABLES
	public int totSucc;
	int totGreedyFails;
	int [] totHops;
	int [] totData;
	int [] totRouting;
	int [] txInvolved;
	int [] localMinima;

	public Config(int _n_nodes, double _max_x, double _max_y, double _max_z, double _range, int startTabuSize, boolean dyn, int runs, String topologies_dir)
	{
		N_NODES = _n_nodes;
		MAX_X = _max_x;
		MAX_Y = _max_y;
		MAX_Z = _max_z;
		RANGE = _range;
		totHops = new int[runs];
		totData = new int[runs];
		totRouting = new int[runs];
		txInvolved = new int[runs];
		tabuSize = startTabuSize;
		localMinima = new int[runs];
		totGreedyFails = 0;
		topo_dir = topologies_dir;
	}


	public void run(int iteration) throws Exception
	{
		System.out.println("RUN " + iteration);
		int run_i = iteration-1;
				
		BufferedWriter file = null;
		BufferedWriter stateFile = null;
		//BufferedWriter topologyWrite = null;
		BufferedReader topologyRead = null;
		String topo = "topo_750x750_n"+N_NODES+"_r"+(int)RANGE+"/topo_"+iteration+".topo";
		Topology t = new Topology(N_NODES, RANGE);
		try {
			//file = new BufferedWriter(new FileWriter("draw_geo/coordinates.txt"));
			//stateFile = new BufferedWriter(new FileWriter("draw_geo/states.txt"));
			t.load(topo_dir+"/"+topo);
			
			//topologyRead = new BufferedReader(new FileReader(topo_dir+"/"+topo));
			
			/*
			stateFile.write(topo);
			stateFile.newLine();
			stateFile.write(""+RANGE);
			stateFile.newLine();
			stateFile.write(""+N_NODES);
			stateFile.newLine();
			*/
		} 
		catch (Exception e) {
			System.out.println("errore impossibile trovare "+topo_dir+"/"+topo);
			System.exit(1);
		} 
		
		// assigning source and destination
		int s_id = 0;
		int d_id = 1;

		//GCR routing = new GCR(t, s_id, d_id, FAILHOPS, ttl);
		GCR routing = new GCR(t, s_id, d_id, 100, 10);
		routing.run();
			
		// TERMINATO
		
		if(routing.success()) {
			totHops[run_i] = routing.hops;
			totData[run_i] = routing.dataForwards;
			totRouting[run_i] = routing.routingForwards;
			txInvolved[run_i] = routing.involvedTxNodes;
			totSucc += routing.getSuccess();
			//System.out.println("hops\tDataPkt\tRoutingPkt\tNodesInvolved");
			//System.out.println(totHops[run_i] + "\t" + totData[run_i] + "\t" + totRouting[run_i] + "\t"
			//	+ txInvolved[run_i]);
		}
		
		// TODO FARE SISTEMA DI TRACING
		// PER ANIMAZIONE
		// DIREI METODO DI UNA CLASSE ANIMATION CHE AGIGUNGE IL PACCHETTO CHE SI SPOSTA
		
		
	}
}

	
	
public class Demo
{	

	public static void main(String args[]) throws Exception
	{
		BufferedWriter stats = null;
		
		double r = 100; // transmission range
		double dim = 750;
		
		int runs = 100;
		int []nodes = {50, 100, 150, 200};
		
		boolean one_run = false;
		int run = 12;
		int t = 30;
		int n = 200;
		
		//String topodir = "../topologies";
		String topodir = "./topologies";
			

		Config conf = new Config(n, dim, dim, 0, r, t, true, runs, topodir);
		conf.ttl = 10;
		
		if(one_run)
			conf.run(run);
		else {
			for(int i = 1; i <= runs; i++)
			{
				conf.run(i);
			}
		
		}
		System.out.println("Delivery\tHops\tDataPkt\tRoutingPkt\tNodesTx");
		System.out.println(conf.totSucc + "\t"+ Stat.mean(conf.totHops)+"\t"+Stat.mean(conf.totData)+"\t"+
		Stat.mean(conf.totRouting)+"\t"+Stat.mean(conf.txInvolved));
		System.exit(0);
	}
}

