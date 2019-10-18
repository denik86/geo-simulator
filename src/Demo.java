/* package whatever; // don't place package name! */

import java.util.*;

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
	static int MAXTABU = 1000; // max number of nodes in tabu list
	int tabuSize;
	int run_i;
	String topo_dir;
	static int FAILHOPS = 100;
	
	// STATS VARIABLES
	int totSucc;
	int totGreedyFails;
	int [] totHops;
	int [] localMinima;

	public Config(int _n_nodes, double _max_x, double _max_y, double _max_z, double _range, int startTabuSize, boolean dyn, int runs, String topologies_dir)
	{
		N_NODES = _n_nodes;
		MAX_X = _max_x;
		MAX_Y = _max_y;
		MAX_Z = _max_z;
		RANGE = _range;
		totHops = new int[runs];
		tabuSize = startTabuSize;
		run_i = 0;
		localMinima = new int[runs];
		totGreedyFails = 0;
		topo_dir = topologies_dir;
	}


	public void run(int iteration) throws Exception
	{
		System.out.println("RUN " + iteration);
				
		BufferedWriter file = null;
		BufferedWriter stateFile = null;
		//BufferedWriter topologyWrite = null;
		BufferedReader topologyRead = null;
		String topo = "topo_750x750_n"+N_NODES+"_r"+(int)RANGE+"/topo_"+iteration+".topo";
		Topology t = new Topology(N_NODES, RANGE);
		try {
			file = new BufferedWriter(new FileWriter("draw_geo/coordinates.txt"));
			stateFile = new BufferedWriter(new FileWriter("draw_geo/states.txt"));
			t.load(topo_dir+"/"+topo);
			
			//topologyRead = new BufferedReader(new FileReader(topo_dir+"/"+topo));
			
			stateFile.write(topo);
			stateFile.newLine();
			stateFile.write(""+RANGE);
			stateFile.newLine();
			stateFile.write(""+N_NODES);
			stateFile.newLine();
		} 
		catch (Exception e) {
			System.out.println("errore impossibile trovare "+topo_dir+"/"+topo);
			System.exit(1);
		} 
		
		// assigning source and destination
		int s_id = 0;
		int d_id = 1;

		ProvaRouting routing = new ProvaRouting(t, s_id, d_id, FAILHOPS);
		routing.run();
			
		// TERMINATO
		
		if(routing.success()) {
			totHops[run_i] = routing.getHops();
			totSucc += routing.getSuccess();
		}
		
		// TODO FARE SISTEMA DI TRACING
		// PER ANIMAZIONE
		// DIREI METODO DI UNA CLASSE ANIMATION CHE AGIGUNGE IL PACCHETTO CHE SI SPOSTA
		
		
	}
}

	
	
public class Demo2
{	

	public static void main(String args[]) throws Exception
	{
		BufferedWriter stats = null;
		
		double r = 100; // transmission range
		double dim = 750;
		
		int runs = 100;
		int []tabu_sizes = {5, 10, 150, 20};
		int []nodes = {50, 100, 150, 200};
		
		boolean one_run = true;
		int run = 20;
		int t = 30;
		int n = 100;
		
		String topodir = "topologies";
		
	
		if(one_run) {
			Config conf = new Config(n, dim, dim, 0, r, t, true, runs, topodir);
			conf.run(run);
			System.exit(0);
		}
		/*
		// MultiRUN
		for(int t_id = 0; t_id < tabu_sizes.length; t_id++)
		{
			//Stat stat = new Stat(runs, nodes[n_id], tabu_sizes[t_id])
			

				stats = new BufferedWriter(new FileWriter("draw_geo/statsTabu_"+ts+"_reset.txt"));
			} catch (Exception e) {} 
			
			try {
				stats.write("Nodes\tDeliveryTabu\tAverageHops\tVarHops\tAverageLocalMinima\t"+
							"VarLocalMinima\tDeliveryGreedy");
				stats.newLine();
			} catch (IOException e) {}
			
			// for each density ...
			for(int n_id = 0; n_id < nodes.length; n_id ++)
			{		
				System.out.println("Configuration: Dim = "+dim+", range = "+r+", n = "+nodes[n_id]+", tabu length = "+ts);
				Config conf = new Config(nodes[n_id], dim, dim, 0, r, ts, true, runs, topodir);
				
				if(one_run)
				{
					System.out.println("SINGLE RUN: "+ run);
					conf.run(run);
				}
				else
				{
					for(int i = 1; i <= runs; i++)
					{
						System.out.println("ITER: "+ i);
						conf.run(i);
					}
					
					try {
						stats.write(""+nodes[n_id]+"\t"+
								conf.totSucc+"\t"+
								mean(conf.totHops)+"\t"+devst(var(conf.totHops))+"\t"+
								mean(conf.localMinima)+"\t"+devst(var(conf.localMinima))+"\t"+
								conf.totGreedyFails);
						stats.newLine();
					} catch (IOException e) {}
					//System.out.println("SUCC: "+succ);
				}
			}}
			
			try {
				stats.close();
			} catch (Exception e) {} 
			
		}
		
		
		*/
	}
}

