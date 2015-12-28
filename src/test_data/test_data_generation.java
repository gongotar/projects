/**
 * 
 */
package test_data;

import java.util.ArrayList;
import java.util.Random;
import org.apache.commons.math3.stat.StatUtils;
import enums.node_type;
import model.coordinate;
import model.edge;
import model.network;
import model.node;

/**
 * @author Masoud Gholami
 *
 */
public class test_data_generation {

	public static void main(String args[]){
		int bus = 0, car = 0, train = 0;
		for (int i = 0; i < 1000; i++) {
			node_type t = random_node_type();
			if(t.equals(node_type.bus_station))
				bus++;
			if(t.equals(node_type.car_station))
				car++;
			if(t.equals(node_type.train_station))
				train++;
		}
		System.out.println("bus: " + bus + ", car: " + car + ", train: " + train);
	}
	
	/*
	 * Generates a random network of nodes and edges 
	 * with the given size
	 * 
	 * @param	size	no. of the nodes in the network
	 * @return	network of the given size
	 * @see 	test_data_generation
	 */
	public static network generate_netw(int size){
		network netw = new network();
		ArrayList<coordinate> coordinations =
				compute_node_coordinations(size);
		ArrayList<node> nodes = new ArrayList<node>();
		ArrayList<edge> edges = new ArrayList<edge>();
		
		for (int i = 0; i < size; i++) {
			node node = new node();
			node.setId(i);
			node.setType(random_node_type());
			node.setCoordinate(coordinations.get(i));
			edges.addAll(assign_random_outgoing_edges(nodes, i));
			nodes.add(node);
		}
		
		assign_netw_lines(nodes);
		netw.setNodes(nodes);
		netw.setEdges(edges);
		return netw;
	}

	/*
	 * Assigns randomly generated lines and timetables to
	 * each edge
	 * 
	 * @param	nodes	list of all of the nodes
	 * @see		test_data_generation
	 */
	private static void assign_netw_lines(ArrayList<node> nodes) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * Assigns random outgoing edges from the i-th node in the 
	 * node list (nodes) to the other nodes in the list. 
	 * the nodes in the near by have more chances to get 
	 * connected through an edge
	 * 
	 * @param	nodes	the list of all of the nodes
	 * @param	i		index of the node in the list whose edges are 
	 * 					to be assigned
	 * @see		test_data_generation
	 */
	private static ArrayList<edge> assign_random_outgoing_edges(ArrayList<node> nodes, int i) {
		// greater values results shorter edges
		double propagation_degree = 4;
		// minimum no. of nodes to be considered for being target_node
		int	minimum_node_consideration = 3;
		
		node node = nodes.get(i);
		double[] p = new double[nodes.size() - 1];
		int index = 0;
		double max_distance = Double.MIN_VALUE;
		double min_distance = Double.MAX_VALUE;
		for (int j = 0; j < nodes.size(); j++) {
			if(j == i)		// ignore this node 
				continue;
			node target_node = nodes.get(i);
			p[index] = node.getCoordinate()
					.getDistanceTo(target_node.getCoordinate());
			if(max_distance < p[index])
				max_distance = p[index];
			if(min_distance > p[index])
				min_distance = p[index];
			index++;
		}
		
		// p preparation so that it contains more values (probability)
		// for the nodes in the nearby
		
		for (int j = 0; j < p.length; j++)
			p[j] = Math.pow(max_distance + min_distance - p[j], propagation_degree);
		
		// eliminate nodes that are far away
		double treshold = StatUtils.mean(p);
		int more_than_treshold = 0;
		
		if(minimum_node_consideration < nodes.size()){
			while(more_than_treshold <= minimum_node_consideration){
				more_than_treshold = 0;
				for (double d : p)
					if(d >= treshold--)
						more_than_treshold++;
			}
			treshold++;
			for (int j = 0; j < p.length; j++)
				if(p[i] < treshold)
					p[i] = 0;	// set the probability to zero (far away nodes)
		}

		
		stochastic_choice(p);
		
		return null;
	}

	/*
	 * Gets the size of the network as the input
	 * and generates randomly node coordinations
	 * in a normal distributed manner
	 * 
	 * @param	size	size of the network
	 * @return	an array of n randomly generated coordinations
	 * @see		test_data_generation 
	 */
	private static ArrayList<coordinate> compute_node_coordinations(int size) {
		// TODO Auto-generated method stub
		int weight = (int) Math.sqrt(size / 1.6);
		int height = size / weight;
		@SuppressWarnings("unused")
		int extra = size - weight * height;
		return null;
	}

	/*
	 * Returns randomly different node types
	 * with different probabilities
	 * 
	 * @return	randomly chosen node type
	 * @see		test_data_generation  
	 */
	private static node_type random_node_type() {
		double[] p = new double[node_type.values().length];
		p[0] = 3;	// 3 buses
		p[1] = 1;	// 1 car
		p[2] = 6;	// 6 trains
		int choice = stochastic_choice(p);
		return node_type.values()[choice];
	}
	
	/*
	 * Gets an array of n probabilities as input and chooses
	 * a number between 0 to n - 1 randomly based on the given
	 * probabilities
	 * 
	 * @param	p	array of probabilities of n numbers
	 * @return	randomly chosen number
	 * @see		test_data_generation 
	 */
	private static int stochastic_choice(double[] p){
		double sum = 0;
		for (double d : p)
			sum += d;
		Random rn = new Random();
		double rand = rn.nextDouble() * sum;
		double flag = 0;
		for (int i = 0; i < p.length; i++) {
			flag += p[i];
			if(rand <= flag)
				return i;
		}
		return -1;
	}
	
}