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

import java.util.ArrayList;
import java.util.Arrays;

import org.geocrowd.AlgorithmEnum;
import org.geocrowd.common.crowd.ExpertTask;

// TODO: Auto-generated Javadoc
/*************************************************************************
 * Compilation: javac FordFulkerson.java Execution: java FordFulkerson V E
 * Dependencies: FlowNetwork.java FlowEdge.java Queue.java
 * 
 * Ford-Fulkerson algorithm for computing a max flow and a min cut using
 * shortest augmenthing path rule.
 * 
 *********************************************************************/

public class FordFulkerson {
	
	/** The marked. */
	private boolean[] marked; // marked[v] = true iff s->v path in residual
								// graph
	/** The edge to. */
								private FlowEdge[] edgeTo; // edgeTo[v] = last edge on shortest residual
								// s->v path
	/** The value. */
								private double value; // current value of max flow
	
	/** The best. */
	private double[] best;
	
	/** The min cost. */
	public double minCost = 0; // minimum cost of the maximum flow
	
	/** The min cost2. */
	public double minCost2 = 0; // minimum cost of the maximum flow
	
	/** The sum dist. */
	public double sumDist = 0;// sum of the distances
	// public static int minCap; //minimum augmented capacity
	/** The aug cost. */
	private double augCost = 0;// minimum augmented cost during every
								// augmentation;

	// private boolean[] taskAssigned;
	// max flow in flow network G from s to t
	/**
								 * Instantiates a new ford fulkerson.
								 * 
								 * @param G
								 *            the g
								 * @param s
								 *            the s
								 * @param t
								 *            the t
								 * @param assign_type
								 *            the assign_type
								 * @param workerNo
								 *            the worker no
								 * @param taskList
								 *            the task list
								 */
								public FordFulkerson(FlowNetwork G, int s, int t, AlgorithmEnum assign_type,
			int workerNo, ArrayList<ExpertTask> taskList) {
		best = new double[G.V()];
		value = excess(G, t);
		if (!isFeasible(G, s, t)) {
			throw new RuntimeException("Initial flow is infeasible");
		}

		if (assign_type == AlgorithmEnum.BASIC) {
			// while there exists an augmenting path, use it
			while (hasAugmentingPath(G, s, t)) {

				// compute bottleneck capacity
				double bottle = Double.POSITIVE_INFINITY;
				for (int v = t; v != s; v = edgeTo[v].other(v)) {
					bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
				}

				// augment flow
				for (int v = t; v != s; v = edgeTo[v].other(v)) {
					minCost2 += edgeTo[v].getCost();
					sumDist += edgeTo[v].distance;
					edgeTo[v].addResidualFlowTo(v, bottle);
					if ((v >= workerNo) && (v != t)) {
						// System.out.print(v+" ");
						ExpertTask task = taskList.get(v - workerNo);
						task.incAssigned();
					}
				}

				value += bottle;
				minCost += bottle * augCost;
			}
			// System.out.println();
			// check optimality conditions
			assert check(G, s, t);
		} else {// if(assign_type == type.TIME){
			// while there exists an augmenting path, use it
			while (hasAugmentingPathMinCost(G, s, t)) {

				// compute bottleneck capacity
				double bottle = Double.POSITIVE_INFINITY;
				for (int v = t; v != s; v = edgeTo[v].other(v)) {
					bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
				}

				// augment flow
				for (int v = t; v != s; v = edgeTo[v].other(v)) {
					edgeTo[v].addResidualFlowTo(v, bottle);
					minCost2 += edgeTo[v].getCost();
					sumDist += edgeTo[v].distance;
					if ((v >= workerNo) && (v != t)) {
						// System.out.print(v+" ");
						ExpertTask task = taskList.get(v - workerNo);
						task.incAssigned();// .setAssigned();
					}
				}

				value += bottle;
				minCost += bottle * augCost;
			}

			// check optimality conditions
			assert check(G, s, t);
		}
	}

	// check optimality conditions
	/**
	 * Check.
	 * 
	 * @param G
	 *            the g
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return true, if successful
	 */
	private boolean check(FlowNetwork G, int s, int t) {

		// check that flow is feasible
		if (!isFeasible(G, s, t)) {
			System.err.println("Flow is infeasible");
			return false;
		}

		// check that s is on the source side of min cut and that t is not on
		// source side
		if (!inCut(s)) {
			System.err.println("source " + s
					+ " is not on source side of min cut");
			return false;
		}
		if (inCut(t)) {
			System.err.println("sink " + t + " is on source side of min cut");
			return false;
		}

		// check that value of min cut = value of max flow
		double mincutValue = 0.0;
		for (int v = 0; v < G.V(); v++) {
			for (FlowEdge e : G.adj(v)) {
				if ((v == e.from()) && inCut(e.from()) && !inCut(e.to()))
					mincutValue += e.capacity();
			}
		}

		double EPSILON = 1E-11;
		if (Math.abs(mincutValue - value) > EPSILON) {
			System.err.println("Max flow value = " + value
					+ ", min cut value = " + mincutValue);
			return false;
		}

		return true;
	}

	// return excess flow at vertex v
	/**
	 * Excess.
	 * 
	 * @param G
	 *            the g
	 * @param v
	 *            the v
	 * @return the double
	 */
	private double excess(FlowNetwork G, int v) {
		double excess = 0.0;
		for (FlowEdge e : G.adj(v)) {
			if (v == e.from())
				excess -= e.flow();
			else
				excess += e.flow();
		}
		return excess;
	}

	// return an augmenting path if one exists, otherwise return null
	/**
	 * Checks for augmenting path.
	 * 
	 * @param G
	 *            the g
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return true, if successful
	 */
	private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
		edgeTo = new FlowEdge[G.V()];
		marked = new boolean[G.V()];

		// breadth-first search
		Queue<Integer> q = new Queue<Integer>();
		q.enqueue(s);
		marked[s] = true;
		while (!q.isEmpty()) {
			int v = q.dequeue();
			int size = G.adj(v).size();
			for (int i = size - 1; i >= 0; i--) {
				FlowEdge e = G.adj(v).get(i);
				// for (FlowEdge e : G.adj(v)) {
				int w = e.other(v);
				int soosk = 0;
				if (w > 99 && (w != 1100))
					soosk = 1;
				// if residual capacity from v to w
				if (e.residualCapacityTo(w) > 0) {
					if (!marked[w]) {
						edgeTo[w] = e;
						marked[w] = true;
						q.enqueue(w);
					}
				}
			}

		}

		// is there an augmenting path?
		return marked[t];
	}

	// return an augmenting path if one exists, otherwise return null
	/**
	 * Checks for augmenting path min cost.
	 * 
	 * @param G
	 *            the g
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return true, if successful
	 */
	private boolean hasAugmentingPathMinCost(FlowNetwork G, int s, int t) {
		edgeTo = new FlowEdge[G.V()];
		marked = new boolean[G.V()];
		Arrays.fill(best, Double.MAX_VALUE);
		// breadth-first search
		Queue<Integer> q = new Queue<Integer>();
		q.enqueue(s);
		marked[s] = true;
		best[s] = 0;
		while (!q.isEmpty()) {
			int v = q.dequeue();
			double cb = best[v];
			for (FlowEdge e : G.adj(v)) {
				int w = e.other(v);

				// if residual capacity from v to w
				if (e.residualCapacityTo(w) > 0) {
					/*
					 * if (!marked[w]) { edgeTo[w] = e; marked[w] = true;
					 * q.enqueue(w); }
					 */
					if ((cb + e.getCost() < best[w])) {
						// if(best[w] <Integer.MAX_VALUE)
						// System.out.println("cost decreased:"+best[w]);
						best[w] = cb + e.getCost();
						edgeTo[w] = e;
						marked[w] = true;
						q.enqueue(w);
					}
				}
			}

		}

		// is there an augmenting path?
		augCost = best[t];
		return marked[t];
	}

	// is v in the s side of the min s-t cut?
	/**
	 * In cut.
	 * 
	 * @param v
	 *            the v
	 * @return true, if successful
	 */
	public boolean inCut(int v) {
		return marked[v];
	}

	// return excess flow at vertex v
	/**
	 * Checks if is feasible.
	 * 
	 * @param G
	 *            the g
	 * @param s
	 *            the s
	 * @param t
	 *            the t
	 * @return true, if is feasible
	 */
	private boolean isFeasible(FlowNetwork G, int s, int t) {
		double EPSILON = 1E-11;

		// check that capacity constraints are satisfied
		for (int v = 0; v < G.V(); v++) {
			for (FlowEdge e : G.adj(v)) {
				if (e.flow() < 0 || e.flow() > e.capacity()) {
					System.err
							.println("Edge does not satisfy capacity constraints: "
									+ e);
					return false;
				}
			}
		}

		// check that net flow into a vertex equals zero, except at source and
		// sink
		if (Math.abs(value + excess(G, s)) > EPSILON) {
			System.err.println("Excess at source = " + excess(G, s));
			System.err.println("Max flow         = " + value);
			return false;
		}
		if (Math.abs(value - excess(G, t)) > EPSILON) {
			System.err.println("Excess at sink   = " + excess(G, t));
			System.err.println("Max flow         = " + value);
			return false;
		}
		for (int v = 0; v < G.V(); v++) {
			if (v == s || v == t)
				continue;
			else if (Math.abs(excess(G, v)) > EPSILON) {
				System.err.println("Net flow out of " + v
						+ " doesn't equal zero");
				return false;
			}
		}
		return true;
	}

	// return cost of max flow
	/**
	 * Min cost.
	 * 
	 * @return the double
	 */
	public double minCost() {
		return minCost;
	}

	// return value of max flow
	/**
	 * Value.
	 * 
	 * @return the double
	 */
	public double value() {
		return value;
	}
}
