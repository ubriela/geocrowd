package org.geocrowd.knapsack;

/*************************************************************************
 * Compilation: javac Knapsack.java Execution: java Knapsack N W
 *
 * Generates an instance of the 0/1 knapsack problem with N items and maximum
 * weight W and solves it in time and space proportional to N * W using dynamic
 * programming.
 *
 * For testing, the inputs are generated at random with weights between 0 and W,
 * and profits between 0 and 1000.
 *
 * % java Knapsack 6 2000 item profit weight take 1 874 580 true 2 620 1616
 * false 3 345 1906 false 4 369 1942 false 5 360 50 true 6 470 294 true
 *
 *************************************************************************/

public class Knapsack {

	public static void main(String[] args) {
		int N = 1000;
		int W = 1000;

		float[] profit = new float[N + 1];
		int[] weight = new int[N + 1];

		// generate random instance, items 1..N
		for (int n = 1; n <= N; n++) {
			profit[n] = (int) (Math.random() * 1000);
			weight[n] = (int) (Math.random() * 10);
		}

		// opt[n][w] = max profit of packing items 1..n with weight limit w
		// sol[n][w] = does opt solution to pack items 1..n with weight limit w
		// include item n?
		float[][] opt = new float[N + 1][W + 1];
		boolean[][] sol = new boolean[N + 1][W + 1];

		for (int n = 1; n <= N; n++) {
			for (int w = 1; w <= W; w++) {

				// don't take item n
				float option1 = opt[n - 1][w];

				// take item n
				float option2 = Float.MIN_VALUE;
				if (weight[n] <= w)
					option2 = profit[n] + opt[n - 1][w - weight[n]];

				// select better of two options
				opt[n][w] = Math.max(option1, option2);
				sol[n][w] = (option2 > option1);
			}
		}

		// determine which items to take
		boolean[] take = new boolean[N + 1];
		for (int n = N, w = W; n > 0; n--) {
			if (sol[n][w]) {
				take[n] = true;
				w = w - weight[n];
			} else {
				take[n] = false;
			}
		}

		// print results
		System.out.println("item" + "\t" + "profit" + "\t" + "weight" + "\t"
				+ "take");
		for (int n = 1; n <= N; n++) {
			System.out.println(n + "\t" + profit[n] + "\t" + weight[n] + "\t"
					+ take[n]);
		}
	}
}
