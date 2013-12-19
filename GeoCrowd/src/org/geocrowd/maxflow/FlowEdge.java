/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geocrowd.maxflow;

/*************************************************************************
 *  Compilation:  javac FlowEdge.java
 *  Execution:    java Flow
 *
 *  Capacitated edge with a flow in a flow network.
 *
 *************************************************************************/

/**
 * The <tt>FlowEdge</tt> class represents a capacitated edge with a flow in a
 * digraph.
 * <p>
 * For additional documentation, see <a href="/algs4/74or">Section 7.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 */

public class FlowEdge {
	private final int v; // from
	private final int w; // to
	private final double capacity; // capacity
	public double distance;// the spatial distance between the two worker and
							// task
	private double flow; // flow
	private double cost; // this is the entry time for a given task

	public FlowEdge(int v, int w, double capacity, double c, double dist) {
		this.v = v;
		this.w = w;
		this.capacity = capacity;
		this.flow = 0;
		this.cost = c;
		this.distance = dist;
	}

	/*
	 * public FlowEdge(int v, int w, double capacity, double flow) { this.v = v;
	 * this.w = w; this.capacity = capacity; this.flow = flow; }
	 */
	// accessor methods
	public int from() {
		return v;
	}

	public int to() {
		return w;
	}

	public double capacity() {
		return capacity;
	}

	public double flow() {
		return flow;
	}

	public double getCost() {
		return cost;
	}

	public int other(int vertex) {
		if (vertex == v)
			return w;
		else if (vertex == w)
			return v;
		else
			throw new RuntimeException("Illegal endpoint");
	}

	public double residualCapacityTo(int vertex) {
		if (vertex == v)
			return flow;
		else if (vertex == w)
			return capacity - flow;
		else
			throw new RuntimeException("Illegal endpoint");
	}

	public void addResidualFlowTo(int vertex, double delta) {
		if (vertex == v)
			flow -= delta;
		else if (vertex == w)
			flow += delta;
		else
			throw new RuntimeException("Illegal endpoint");
	}

	public String toString() {
		if (v == 196)
			return "s ->" + w + " " + flow + "/" + capacity;
		else if (w == 197)
			return v + "-> t " + flow + "/" + capacity;
		// else if(v>97)
		// return (v-98) + "->" + w + " " + flow + "/" + capacity;
		else
			return v + "->" + (w - 98) + " " + flow + "/" + capacity;
	}

	/**
	 * Test client.
	 */
	/*
	 * public static void main(String[] args) { FlowEdge e = new FlowEdge(12,
	 * 23, 3.14); System.out.println(e); }
	 */
}
