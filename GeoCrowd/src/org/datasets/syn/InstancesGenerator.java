package org.datasets.syn;

import java.util.ArrayList;

import org.datasets.syn.dtype.Range;
import org.datasets.syn.dtype.Rectangle;

public class InstancesGenerator {

	public static int gaussianCluster = 4;
	
	private int instances = 0;
	private Distribution2DEnum workerDist;
	private Distribution2DEnum taskDist;
	private WTCycleEnum workerCycle;
	private WTCycleEnum taskCycle;
	private int wMean = 0;
	private int tMean = 0;
	
	private String workerPath = "";
	private String taskPath = "";
	
	public Rectangle boundary = null;

	public InstancesGenerator(int instances, WTCycleEnum wc, WTCycleEnum tc, int wMean, int tMean, Rectangle boundary, Distribution2DEnum wd, Distribution2DEnum td, String workerPath, String taskPath) {
		super();
		this.instances = instances;
		this.workerCycle = wc;
		this.taskCycle = tc;
		this.wMean = wMean;
		this.tMean = tMean;
		this.boundary = boundary;
		this.workerDist = wd;
		this.taskDist = td;
		this.workerPath = workerPath;
		this.taskPath = taskPath;
		
		generateData();
	}
	
	public void generateData() {
		ArrayList<Integer> workerCounts = WTCountGenerator.generateCounts(instances, wMean, workerCycle);
		ArrayList<Integer> taskCounts = WTCountGenerator.generateCounts(instances, tMean, taskCycle);
		
		ArrayList<Long> seeds = new ArrayList<>();
		// compute seed for gaussian cluster
		DatasetGenerator.gaussianCluster = gaussianCluster;
		for (int i = 0; i < gaussianCluster; i++)
			seeds.add((long) UniformGenerator.randomValue(new Range(0, 1000000), true));
		DatasetGenerator.seeds = seeds;
		
		for (int i = 0; i < instances; i++) {
			// update time instance
			DatasetGenerator.time = i;
			
			// worker
			DatasetGenerator wdg = new DatasetGenerator(workerPath + "workers" + i + ".txt");
			wdg.generate2DDataset(workerCounts.get(i), boundary, workerDist);
			
			// task
			DatasetGenerator tdg = new DatasetGenerator(taskPath + "tasks" + i + ".txt");
			tdg.generate2DDataset(taskCounts.get(i), boundary, taskDist);
		}
	}
	
}
