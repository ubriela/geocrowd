package org.datasets.syn;

import java.util.ArrayList;



public class WTCountGenerator {
	
	public static int cycles = 10;
	public static int cosine_height_scale = 1;

	public static ArrayList<Integer> generateCounts(int instances, int mean, WTCycleEnum f) {
		ArrayList<Integer> counts = new ArrayList<>();

		switch (f) {
		case CONSTANT:
			for (int i = 0; i < instances; i++)
				counts.add(mean);
			break;
		case INCREASING:
			for (int i = 1; i <= instances; i++)
				counts.add(2*mean*i/instances);
			break;
		case DECREASING:
			for (int i = instances; i > 0; i--)
				counts.add(2*mean*i/instances);
			break;
		case COSINE:
			for (int i = 1; i <= instances; i++) {
				int val = mean - (int)(mean/cosine_height_scale * Math.sin((i / (instances/cycles + 0.0)) * 2 * Math.PI));
				counts.add(val);
			}
			break;
		}
		return counts;
	}
}
