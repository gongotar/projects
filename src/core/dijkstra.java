package core;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.PriorityQueue;

import model.connection;
import model.edge;
import model.label;
import model.node;
import model.timetable_row;

/**
 * 
 */

/**
 * @author Masoud Gholami
 *
 */

public class dijkstra {

	public static void main(String args[]){
	
		
	}
	
	/*
	 * Computes all of the Pareto-optimal solutions in a network (graph)
	 * using the modified (generalized) Dijkstra algorithm.
	 * 
	 * @param	netw	a digraph of nodes and weighted edges (Time table as the weight)
	 * @param	start	a set of possible start nodes
	 * @return			a set of Pareto-optimal labels defining paths in the graph
	 * @see				dijkstra 
	 */
	
	public static void pareto_opt(ArrayList<node> start){
		
		PriorityQueue<label> pq = new PriorityQueue<label>();
		
		for (node n : start) {
			label l = new label(n);
			pq.add(l);
		}
		
		while(!pq.isEmpty()){
			label l = pq.poll();
			node node = l.getNode();
			for (edge e : node.getOutgoing_edges()) {
				if(!e.isFeasible()) continue;						// ignore this edge
				label new_label = create_label(l, e);
				if(dominated(e.getEnd().getLabels(), new_label))	// if the new label is dominated by old labels 
					continue;
				new_label.setNode(e.getEnd());
				pq.add(new_label);
				remove_dominated_labels(e, new_label);
			}
		}
	}

	private static void remove_dominated_labels(edge e, label new_label) {
		// TODO Auto-generated method stub
		
	}

	private static boolean dominated(ArrayList<label> arrayList, label new_label) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * Creates a new label by using the previous label and 
	 * adding a new edge to it considering the aggregation
	 * of the edge attributes and the label attributes
	 * 
	 * @param	l		the previous label
	 * @param	e		to be added edge
	 * @return	label	new label containing the previous label and the new edge
	 * @see		dijkstra
	 */
	private static label create_label(label l, edge e) {
		
		label new_label = new label();				// create the label
		Integer id = find_edge_id(l, e);
		connection<edge, Integer> conn = new connection<edge, Integer>(e, id);
		ArrayList<connection<edge, Integer>> extended_path = 
				new ArrayList<connection<edge,Integer>>(l.getPath());
		extended_path.add(conn);
		new_label.setPath(extended_path);			// set the new path
		
		ArrayList<timetable_row> timetable = e.getTimetable();
		
		int index = 0;
		for (int i = 0; i < timetable.size(); i++){
			if(timetable.get(i).getId() == id){
				index = i;
				break;
			}
		}
		
		timetable_row row = timetable.get(index);
		
		double new_cost = compute_new_cost(l, row);
		new_label.setCost(new_cost);				// set the new cost
		
		Duration new_duration = compute_new_duration(l, row);
		new_label.setDuration(new_duration);		// set the new duration
		
		int new_change = compute_new_change(l, row);
		new_label.setChange(new_change);			// set the new change
		
		double new_risk = compute_new_risk(l, row);
		new_label.setRisk(new_risk);				// set the new risk
		
		return new_label;
	}

	/*
	 * Computes the new risk of the path, considering the risk
	 * of the label and the variation of the last row of the label
	 * and also using the start time of the new row
	 * 
	 *  @param	l		previous label
	 *  @param	row		a row of the new edge's timetable
	 *  @return	new risk evaluation
	 *  @see dijkstra
	 */
	private static double compute_new_risk(label l, timetable_row row) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * Computes the new change number of the path, considering the number of changes
	 * of the label and deciding if adding the new edge will increase the total
	 * number of changes by one or not.
	 * 
	 *  @param	l		previous label
	 *  @param	row		a row of the new edge's timetable
	 *  @return	new change number
	 *  @see dijkstra
	 */
	private static int compute_new_change(label l, timetable_row row) {
		int change = l.getChange();							// get the change no. of l
		int size = l.getPath().size();
		timetable_row last_row = get_label_row(l, size - 1);// get the last row
		
		if(last_row.getLine() != row.getLine())				// get the last line used in the l and check
			change++;										// whether it's equal to the new row's line no. 
															// if not equal, we have a new change
		return change;
	}

	/*
	 * Gets the used row in the timetable of the row_index-th step of the label
	 * 
	 *  @param	l			the label with many steps and timetables in each step
	 *  @param	row_index	the needed step
	 *  @return	timetable_row of the needed step (all needed information)
	 *  @see dijkstra
	 */
	private static timetable_row get_label_row(label l, int row_index) {
		edge edge = l.getPath().get(row_index).getEdge();	// get the row_index-th edge in the path
		ArrayList<timetable_row> timetable = 				// get the row_index-th timetable of l
				new ArrayList<timetable_row>(edge.getTimetable());
		
		int edge_index = 0;
		for (int i = 0; i < timetable.size(); i++){
			if(timetable.get(i).getId() == l.getPath().get(row_index).getId()){
				edge_index = i;								// find the corresponding row id of the edge
				break;
			}
		}
		timetable_row row = timetable.get(edge_index);
		return row;
	}

	/*
	 * Computes the new duration considering the start time of the label and 
	 * the end time of the edge
	 * 
	 *  @param	l		previous label
	 *  @param	row		a row of the new edge's timetable
	 *  @return	new duration
	 *  @see dijkstra
	 */
	private static Duration compute_new_duration(label l, timetable_row row) {
		timetable_row first_row = get_label_row(l, 0);					// get the first row
		LocalDateTime path_start_time =	first_row.getStart_time();		// get the start time of the first edge using the id 
																		// compute the duration using the start time of
																		// the journey and the end time of the new edge
		Duration duration = Duration.between(row.getEnd_time(), path_start_time);
		return duration;
	}

	/*
	 * Computes the new cost considering the cost of the label and 
	 * the cost of the edge
	 * 
	 *  @param	l		previous label
	 *  @param	row		a row of the new edge's timetable
	 *  @return	new cost
	 *  @see dijkstra
	 */
	private static double compute_new_cost(label l, timetable_row row) {
		double new_cost = row.getCost() + l.getCost();
		return new_cost;
	}

	/*
	 * Defines the correct timetable row of the new edge to be
	 * used when the path of the given label is taken.
	 * It uses the arriving information of the path in the label,
	 * so that means the end time of the last row of the path and 
	 * searches for the nearest departure of the new edge 
	 * 
	 * @param	l	the label containing the path information
	 * @param	e	the new edge with a timetable
	 * @return	the id of the right timetable row
	 * @see dijkstra
	 */
	private static Integer find_edge_id(label l, edge e) {
		long minutes = 1;										// minimum waiting time
		
		int size = l.getPath().size();
		timetable_row last_row = get_label_row(l, size - 1);	// get the last row of the path in the label
		LocalDateTime arrived_at = last_row.getEnd_time();		// get the arriving time of the path
		ArrayList<timetable_row> timetable = 					// get the timetable of the new edge
				new ArrayList<timetable_row>(e.getTimetable());
		
		int index = 0, min_index = index;
		Duration waiting_time = Duration.between(LocalDateTime.MAX, arrived_at);
		for (timetable_row row : timetable) {					// finding the minimum waiting time
			if(arrived_at.isBefore(row.getStart_time().minusMinutes(minutes))
					&& Duration.between(row.getStart_time(), arrived_at).compareTo(waiting_time) < 0){
				waiting_time = Duration.between(row.getStart_time(), arrived_at);
				min_index = index;
			}
			index++;
		}
		
		return min_index;
	}
	
}