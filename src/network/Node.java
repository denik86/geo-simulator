package network;
import java.util.ArrayList;


class Neighbor
{
	int id; // id of neighbor
	double x, y, z; // nodes's coordinates
	
	Neighbor(int id_node, double xx, double yy, double zz)
	{
		id = id_node;
		x = xx;
		y = yy;
		z = zz;
	}
}

public class Node
{
	public int id; // id of node
	public double x; // node's coordinate
	public double y;
	public double z;
	public ArrayList<Neighbor> neighbors; // list of neighbors
	public int n; // number of neighbors

	public Node(int id_node, double xx, double yy, double zz)
	{
		id = id_node;
		neighbors = new ArrayList<Neighbor>();
		n = 0;
		x = xx;
		y = yy;
		z = zz;
	}
	
	public int getNeighborId(int i)
	{
		return neighbors.get(i).id;
	}
	
	public double distanceNeighbor(int i, double x, double y, double z)
	{
		return Geom.EuclideanDistance(neighbors.get(i).x, neighbors.get(i).y ,neighbors.get(i).z, x, y, z);
	}
	
	// calculate the distance between the neighbor i of this node and a node n
	public double distanceNeighbor(int i, Node n)
	{
		return Geom.EuclideanDistance(neighbors.get(i).x, neighbors.get(i).y ,neighbors.get(i).z, n.x, n.y, n.z);
	}
	
	public void addNeighbor(int id, double x, double y, double z)
	{
		Neighbor ne = new Neighbor(id,x,y,z);
		neighbors.add(ne);
		n++;
	}
	
	public double distance(Node n)
	{
		return Geom.EuclideanDistance(x, y, z, n.x, n.y, n.z);
	}
	
	public double distance(double nx, double ny, double nz)
	{
		return Geom.EuclideanDistance(x, y, z, nx, ny, nz);
	}
	
	public void delNeighborById(int id)
	{
		// TODO
	}
}