/*******************************************************************************
* @ Year 2013
* This is the source code of the following papers. 
* 
* 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla Kazemi, Cyrus Shahabi.
* 
* 
* Please contact the author Hien To, ubriela@gmail.com if you have any question.
*
* Contributors:
* Hien To - initial implementation
*******************************************************************************/
package org.geocrowd.maxflow;

import java.util.*;

import org.geocrowd.AlgorithmEnum;
import org.geocrowd.common.crowdsource.SpecializedTask;
import org.geocrowd.common.crowdsource.SpecializedWorker;

// TODO: Auto-generated Javadoc
/*************************************************************************
 * Compilation: javac FlowNetwork.java Execution: java FlowNetwork V E
 * Dependencies: Bag.java FlowEdge.java
 * 
 * A capacitated flow network, implemented using adjacency lists.
 * 
 *************************************************************************/

public class FlowNetwork {
	
	/** The v. */
	private final int V;
	
	/** The e. */
	private int E;
	
	/** The adj. */
	private ArrayList<FlowEdge>[] adj;

	// empty graph with V vertices
	/**
	 * Instantiates a new flow network.
	 * 
	 * @param V
	 *            the v
	 */
	public FlowNetwork(int V) {
		this.V = V;
		this.E = 0;
		adj = new ArrayList[V];
		for (int v = 0; v < V; v++)
			adj[v] = new ArrayList();
	}

	// random graph with V vertices and E edges
	/**
	 * Instantiates a new flow network.
	 * 
	 * @param V
	 *            the v
	 * @param List
	 *            the list
	 * @param workerList
	 *            the worker list
	 * @param taskList
	 *            the task list
	 * @param assign_type
	 *            the assign_type
	 */
	public FlowNetwork(int V, ArrayList[] List, ArrayList<SpecializedWorker> workerList,
			ArrayList<SpecializedTask> taskList, AlgorithmEnum assign_type) {
		this(V + 2);
		double capacity = 1;
		for (int i = 0; i < List.length; i++) {
			int maxTask = workerList.get(i).getMaxTaskNo();
			double workerLat = workerList.get(i).getLatitude();
			double workerLng = workerList.get(i).getLongitude();
			ArrayList tasks = List[i];
			if (tasks != null) {
				for (int j = 0; j < tasks.size(); j++) {
					int t = (Integer) tasks.get(j);
					SpecializedTask task = taskList.get(t);
					double taskLat = task.getLat();
					double taskLng = task.getLng();
					double dist = Math
							.sqrt(((workerLat - taskLat) * (workerLat - taskLat))
									+ ((workerLng - taskLng) * (workerLng - taskLng)));

					if (assign_type == AlgorithmEnum.LLEP)
						addEdge(new FlowEdge(i, List.length + t, capacity,
								task.getEntropy(), dist));
					else if (assign_type == AlgorithmEnum.NNP)
						addEdge(new FlowEdge(i, List.length + t, capacity,
								dist, dist));
					else
						addEdge(new FlowEdge(i, List.length + t, capacity,
								task.getEntryTime(), dist));
				}
			}
			addEdge(new FlowEdge(V, i, maxTask, 0, 0)); // this is for adding
														// edges from source to
														// all points
		}
		for (int i = 0; i < taskList.size(); i++) {
			SpecializedTask task = taskList.get(i);
			addEdge(new FlowEdge(List.length + i, V + 1, capacity, 0, 0));
		}
	}


	// add edge e in both v's and w's adjacency lists
	/**
	 * Adds the edge.
	 * 
	 * @param e
	 *            the e
	 */
	public void addEdge(FlowEdge e) {
		E++;
		int v = e.from();
		int w = e.to();
		adj[v].add(e);
		adj[w].add(e);
	}

	// return list of edges incident to v
	/**
	 * Adj.
	 * 
	 * @param v
	 *            the v
	 * @return the array list
	 */
	public ArrayList<FlowEdge> adj(int v) {
		return adj[v];
	}

	/**
	 * E.
	 * 
	 * @return the int
	 */
	public int E() {
		return E;
	}

	// return list of all edges
	/**
	 * Edges.
	 * 
	 * @return the iterable
	 */
	public Iterable<FlowEdge> edges() {
		ArrayList<FlowEdge> list = new ArrayList<FlowEdge>();
		for (int v = 0; v < V; v++)
			for (FlowEdge e : adj(v))
				list.add(e);
		return list;
	}

	// string representation of Graph - takes quadratic time
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String NEWLINE = System.getProperty("line.separator");
		StringBuilder s = new StringBuilder();
		s.append(V + " " + E + NEWLINE);
		for (int v = 0; v < (V - 2) / 2; v++) {
			s.append(v + ":  ");
			for (FlowEdge e : adj[v]) {
				s.append(e + "  ");
			}
			s.append(NEWLINE);
		}
		return s.toString();
	}

	// number of vertices and edges
	/**
	 * V.
	 * 
	 * @return the int
	 */
	public int V() {
		return V;
	}
}
