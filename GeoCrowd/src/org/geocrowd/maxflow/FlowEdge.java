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

// TODO: Auto-generated Javadoc
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
	
	/** The v. */
	private final int v; // from
	
	/** The w. */
	private final int w; // to
	
	/** The capacity. */
	private final double capacity; // capacity
	
	/** The distance. */
	public double distance;// the spatial distance between the two worker and
							// task
	/** The flow. */
							private double flow; // flow
	
	/** The cost. */
	private double cost; // this is the entry time for a given task

	/**
	 * Instantiates a new flow edge.
	 * 
	 * @param v
	 *            the v
	 * @param w
	 *            the w
	 * @param capacity
	 *            the capacity
	 * @param c
	 *            the c
	 * @param dist
	 *            the dist
	 */
	public FlowEdge(int v, int w, double capacity, double c, double dist) {
		this.v = v;
		this.w = w;
		this.capacity = capacity;
		this.flow = 0;
		this.cost = c;
		this.distance = dist;
	}

	/**
	 * Adds the residual flow to.
	 * 
	 * @param vertex
	 *            the vertex
	 * @param delta
	 *            the delta
	 */
	public void addResidualFlowTo(int vertex, double delta) {
		if (vertex == v)
			flow -= delta;
		else if (vertex == w)
			flow += delta;
		else
			throw new RuntimeException("Illegal endpoint");
	}

	/**
	 * Capacity.
	 * 
	 * @return the double
	 */
	public double capacity() {
		return capacity;
	}

	/**
	 * Flow.
	 * 
	 * @return the double
	 */
	public double flow() {
		return flow;
	}

	/*
	 * public FlowEdge(int v, int w, double capacity, double flow) { this.v = v;
	 * this.w = w; this.capacity = capacity; this.flow = flow; }
	 */
	// accessor methods
	/**
	 * From.
	 * 
	 * @return the int
	 */
	public int from() {
		return v;
	}

	/**
	 * Gets the cost.
	 * 
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * Other.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return the int
	 */
	public int other(int vertex) {
		if (vertex == v)
			return w;
		else if (vertex == w)
			return v;
		else
			throw new RuntimeException("Illegal endpoint");
	}

	/**
	 * Residual capacity to.
	 * 
	 * @param vertex
	 *            the vertex
	 * @return the double
	 */
	public double residualCapacityTo(int vertex) {
		if (vertex == v)
			return flow;
		else if (vertex == w)
			return capacity - flow;
		else
			throw new RuntimeException("Illegal endpoint");
	}

	/**
	 * To.
	 * 
	 * @return the int
	 */
	public int to() {
		return w;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
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
