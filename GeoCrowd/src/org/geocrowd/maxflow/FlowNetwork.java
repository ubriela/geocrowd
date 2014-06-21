package org.geocrowd.maxflow;

import java.io.*;
import java.util.*;

import org.geocrowd.AlgorithmEnum;
import org.geocrowd.common.SpecializedTask;
import org.geocrowd.common.SpecializedWorker;

/*************************************************************************
 * Compilation: javac FlowNetwork.java Execution: java FlowNetwork V E
 * Dependencies: Bag.java FlowEdge.java
 * 
 * A capacitated flow network, implemented using adjacency lists.
 * 
 *************************************************************************/

public class FlowNetwork {
	private final int V;
	private int E;
	private ArrayList<FlowEdge>[] adj;

	// empty graph with V vertices
	public FlowNetwork(int V) {
		this.V = V;
		this.E = 0;
		adj = new ArrayList[V];
		for (int v = 0; v < V; v++)
			adj[v] = new ArrayList();
	}

	// random graph with V vertices and E edges
	public FlowNetwork(int V, ArrayList[] List, ArrayList<SpecializedWorker> workerList,
			ArrayList<SpecializedTask> taskList, AlgorithmEnum assign_type) {
		this(V + 2);
		double capacity = 1;
		for (int i = 0; i < List.length; i++) {
			int maxTask = workerList.get(i).getMaxTaskNo();
			double workerLat = workerList.get(i).getLatitude();
			double workerLng = workerList.get(i).getLongitude();
			ArrayList tasks = (ArrayList) List[i];
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


	// number of vertices and edges
	public int V() {
		return V;
	}

	public int E() {
		return E;
	}

	// add edge e in both v's and w's adjacency lists
	public void addEdge(FlowEdge e) {
		E++;
		int v = e.from();
		int w = e.to();
		adj[v].add(e);
		adj[w].add(e);
	}

	// return list of edges incident to v
	public ArrayList<FlowEdge> adj(int v) {
		return adj[v];
	}

	// return list of all edges
	public Iterable<FlowEdge> edges() {
		ArrayList<FlowEdge> list = new ArrayList<FlowEdge>();
		for (int v = 0; v < V; v++)
			for (FlowEdge e : adj(v))
				list.add(e);
		return list;
	}

	// string representation of Graph - takes quadratic time
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
}
