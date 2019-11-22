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
	int [] succ;
	int [] totHops;
	int [] totData;
	int [] totRouting;
	int [] txInvolved;
	int [] localMinima;
	int [] bytesOverhead;
	double [] bytesOverheadPerDataHop;

	public Config(int _n_nodes, double _max_x, double _max_y, double _max_z, double _range, int startTabuSize, boolean dyn, int runs, String topologies_dir)
	{
		N_NODES = _n_nodes;
		MAX_X = _max_x;
		MAX_Y = _max_y;
		MAX_Z = _max_z;
		RANGE = _range;
		succ = new int[runs];
		totHops = new int[runs];
		totData = new int[runs];
		totRouting = new int[runs];
		txInvolved = new int[runs];
		tabuSize = startTabuSize;
		localMinima = new int[runs];
		topo_dir = topologies_dir;
		bytesOverhead = new int[runs];
		bytesOverheadPerDataHop = new double[runs];

	}


	public void run(int iteration) throws Exception
	{
		System.out.println("RUN " + iteration);
		int run_i = iteration-1;
				
		//BufferedWriter file = null;
		//BufferedWriter stateFile = null;
		//BufferedWriter topologyWrite = null;
		//BufferedReader topologyRead = null;
		String topo = "topo-750-750-0-n"+N_NODES+"-r"+(int)RANGE+"/topo-750-750-0-n"+N_NODES+"-r"+(int)RANGE+"_"+iteration+".topo";
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

		Tabu routing = new Tabu(t, s_id, d_id, FAILHOPS, 3);
		//GCR routing = new GCR(t, s_id, d_id, FAILHOPS, 10);
		//AODV routing = new AODV(t, s_id, d_id, 100, 10);
		//Greedy routing = new Greedy(t, s_id, d_id, 100, 10);
		routing.run();
		
		//System.out.println(routing.getSumPacketSizes());
		//System.out.println(routing.getPacketSizesPerHop());
			
		// TERMINATO
		
		if(routing.success()) {
			succ[run_i] = 1;
			totHops[run_i] = routing.hops;
			totData[run_i] = routing.dataForwards;
			totRouting[run_i] = routing.routingForwards;
			txInvolved[run_i] = routing.involvedTxNodes;
			totSucc += routing.getSuccess();
			bytesOverhead[run_i] = routing.getSumPacketSizes();
			bytesOverheadPerDataHop[run_i] = routing.getPacketSizesPerHop();
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
		//BufferedWriter stats = null;
		
		double r = 100; // transmission range
		double dim = 750;
		int t = 30;
		int runs = 500;
				
		boolean one_run = false;
		boolean full = true;
		
		String topodir = "../topologies"; // linux
		//String topodir = "./topologies";  // eclipse
		
if(!full) {	

		int n = 50;
		Config conf = new Config(n, dim, dim, 0, r, t, true, runs, topodir);
		conf.ttl = 10;
		if(one_run) {
			int run = 8;
			conf.run(run);
			runs = 1; // just for statistic adjustment
		}
		else 
		{
			for(int i = 1; i <= runs; i++)
			{
				conf.run(i);
			}
		}
		System.out.println("Delivery\tHops\tDataPktSent\tRoutingPktSent\tNodesInvolved\tBytesOverhead\tBytesOverheadPerDataHop");
		System.out.println((double)conf.totSucc/runs + "\t"+ 
			Stat.mean(conf.totHops, conf.succ)+"\t"+
			Stat.mean(conf.totData, conf.succ)+"\t"+
			Stat.mean(conf.totRouting, conf.succ)+"\t"+
			Stat.mean(conf.txInvolved, conf.succ)+"\t"+
			Stat.mean(conf.bytesOverhead, conf.succ)+"\t"+
			Stat.mean(conf.bytesOverheadPerDataHop, conf.succ));
		System.exit(0);
}

else
		{
			BufferedWriter stats = null;
			stats = new BufferedWriter(new FileWriter("stats.txt"));
			stats.write("Nodes\tDelivery\tHops\tDataPktSent\tRoutingPktSent\tNodesInvolved\tBytesOverhead\tBytesOverheadPerDataHop");
			stats.newLine();
			int []nodes = {50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300};
			for(int nit = 0; nit < nodes.length; nit++)
			{
				int n = nodes[nit];
				stats.write(n+"\t");
				Config conf = new Config(n, dim, dim, 0, r, t, true, runs, topodir);
				for(int i = 1; i <= runs; i++)
				{
					conf.run(i);
				}
				stats.write(
					String.format("%.2f", (double)conf.totSucc/runs) + "\t"+ 
					String.format("%.2f", Stat.mean(conf.totHops, conf.succ))+"\t"+
					String.format("%.2f", Stat.mean(conf.totData, conf.succ))+"\t"+
					String.format("%.2f", Stat.mean(conf.totRouting, conf.succ))+"\t"+
					String.format("%.2f", Stat.mean(conf.txInvolved, conf.succ))+"\t"+
					String.format("%.2f", Stat.mean(conf.bytesOverhead, conf.succ))+"\t"+
					String.format("%.2f", Stat.mean(conf.bytesOverheadPerDataHop, conf.succ)));
				stats.newLine();
			}
			stats.close();
		}

	}
}

