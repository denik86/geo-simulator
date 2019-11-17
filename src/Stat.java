import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Stat {
	
	final int N_RUNS;
	int hops[];
	int success[];
	int localMinima[];
	String NAMEPAR1;
	int PAR1;
	String NAMEPAR2;
	int PAR2;
	
	public static double mean(int[] v)
	{
	    double tot = 0.0;
	    for (int i = 0; i < v.length; i++)
	    	tot += v[i];
	    return tot / v.length;
	}
	
	public static double mean(int[] v, int[] consider)
	{
		int count = 0;
	    double sum = 0.0;
	    for (int i = 0; i < v.length; i++) {
	      if(consider[i] == 1) {
	    	count++;
	    	sum += v[i];
	      }
	    }
	    return sum / count;
	}
	
	public static double var(int[] v) {
		double mu = mean(v);
		double sumsq = 0.0;
		for (int i = 0; i < v.length; i++)
		   sumsq += (mu - v[i])*(mu - v[i]);
		return sumsq / (v.length);
	}
		
	public static double var(int[] v, int[] consider) 
	{	
		int count = 0;
		double mu = mean(v);
		double sumsq = 0.0;
		for (int i = 0; i < v.length; i++) {
			if(consider[i] == 1) {
				count++;
		    	sumsq += (mu - v[i])*(mu - v[i]);
			}
		}
		return sumsq / count;
	}
	
	public static double devst(double v) {
		return Math.sqrt(v); 
	}
	
	public Stat(int n_runs, String namePar1, int par1, String namePar2, int par2)
	{
		N_RUNS = n_runs;
		hops = new int[N_RUNS];
		success = new int[N_RUNS];
		localMinima = new int[N_RUNS];
		NAMEPAR1 = namePar1;
		PAR1 = par1;
		NAMEPAR2 = namePar2;
		PAR2 = par2;
	}
	
	public int nSuccess()
	{
		int count = 0;
		for(int i = 0; i < success.length; i++)
		{
			count += success[i];
		}
		return count;	
	}
	
	public double meanHops()
	{
		return mean(hops, success);
	}
	
	public double varHops()
	{
		return var(hops, success);
	}
	
	public double meanLocalMinima()
	{
		return mean(localMinima, success);
	}
	
	public double varLocalMinima()
	{
		return var(localMinima, success);
	}
	
	public String printStat()
	{
		String s = "";
		s = PAR1 + "\t" + PAR2 + "\t" + nSuccess() + "\t" + meanHops() + "\t" + devst(varHops()) + "\t" + meanLocalMinima() + "\t" + devst(varLocalMinima());
		return s;
	}
	
}
