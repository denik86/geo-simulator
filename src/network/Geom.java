package network;

public class Geom {
	
	
	public static double EuclideanDistance(double x1, double y1, double z1, double x2, double y2, double z2)
	{
		double diff_x = Math.abs(x1 - x2);
		double diff_y = Math.abs(y1 - y2);
		double diff_z = Math.abs(z1 - z2);
		double dist = Math.sqrt(diff_x*diff_x + diff_y*diff_y + diff_z*diff_z);

		return dist;
	}

}
