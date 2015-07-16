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

import java.util.Arrays;
import java.util.LinkedList;

// TODO: Auto-generated Javadoc
/**
 * The Class MinimumCostFlow.
 * 
 * @author Leyla
 */
public class MinimumCostFlow {

	/**
	 * The Class AugRe.
	 */
	private static class AugRe {
		
		/** The min cap. */
		int minCost, minCap;

		/**
		 * Instantiates a new aug re.
		 * 
		 * @param minCost
		 *            the min cost
		 * @param minCap
		 *            the min cap
		 */
		public AugRe(int minCost, int minCap) {
			this.minCost = minCost;
			this.minCap = minCap;
		}
	}
	
	/** The cap. */
	private int[][] cap;
	
	/** The cost. */
	private int[][] cost;
	
	/** The n. */
	private int n;
	
	/** The back. */
	private int[] back;
	
	/** The best. */
	private int[] best;

	/** The max flow. */
	private int maxFlow = 0;

	/**
	 * Instantiates a new minimum cost flow.
	 * 
	 * @param cap
	 *            the cap
	 * @param cost
	 *            the cost
	 */
	public MinimumCostFlow(int[][] cap, int[][] cost) {
		this.cap = cap;
		this.cost = cost;
		this.n = cap.length;
		init();
		back = new int[n];
		best = new int[n];
	}

	/**
	 * Aug.
	 * 
	 * @param src
	 *            the src
	 * @param dest
	 *            the dest
	 * @return the aug re
	 */
	private AugRe aug(int src, int dest) {
		Arrays.fill(back, -1);
		back[src] = src;
		Arrays.fill(best, Integer.MAX_VALUE);
		best[src] = 0;
		LinkedList<Integer> q = new LinkedList<Integer>();
		q.add(src);
		while (!q.isEmpty()) {
			int cur = q.remove(0);
			int cb = best[cur];
			for (int i = 0; i < n; i++)
				if (cap[cur][i] > 0) {
					if (cb + cost[cur][i] < best[i]) {
						best[i] = cb + cost[cur][i];
						back[i] = cur;
						q.add(i);
					}
				}
		}
		if (best[dest] == Integer.MAX_VALUE)
			return null;
		int minCap = Integer.MAX_VALUE;
		int cur = dest;
		while (back[cur] != cur) {
			minCap = Math.min(cap[back[cur]][cur], minCap);
			cur = back[cur];
		}
		cur = dest;
		while (back[cur] != cur) {
			cap[back[cur]][cur] -= minCap;
			cap[cur][back[cur]] += minCap;
			cur = back[cur];
		}
		AugRe augre = new AugRe(best[dest] * minCap, minCap);
		return augre;
	}

	/**
	 * Gets the.
	 * 
	 * @param src
	 *            the src
	 * @param dest
	 *            the dest
	 * @return the int
	 */
	public int get(int src, int dest) {
		int re = 0;
		while (true) {
			AugRe ar = aug(src, dest);
			if (ar == null)
				break;
			re += ar.minCost;
			maxFlow += ar.minCap;
		}
		return re;
	}

	/**
	 * Gets the max flow.
	 * 
	 * @return the max flow
	 */
	public int getMaxFlow() {
		return maxFlow;
	}

	/**
	 * Inits the.
	 */
	private void init() {
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				if (cap[i][j] > 0)
					cost[j][i] = -cost[i][j];
		maxFlow = 0;
	}

}
