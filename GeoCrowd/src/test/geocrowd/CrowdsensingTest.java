package test.geocrowd;

import static org.junit.Assert.*;

import org.geocrowd.AlgorithmEnum;
import org.geocrowd.Crowdsensing;
import org.geocrowd.DatasetEnum;
import org.geocrowd.util.Constants;
import org.junit.Test;

public class CrowdsensingTest {

	@Test
	public void test() {
		int totalAssignedTasks = 0;
		int totalAssignedWorkers = 0;
		long totalTime = 0;
		double avgAvgWT = 0;
		double avgVarWT = 0;
		double totalAvgWT = 0;
		double totalVARWT = 0;
		for (int k = 0; k < 1; k++) {

			System.out.println("+++++++ Iteration: " + (k + 1));
			Crowdsensing.DATA_SET = DatasetEnum.SMALL;
			Crowdsensing.algorithm = AlgorithmEnum.BASIC;
			Crowdsensing crowdsensing = new Crowdsensing();
			//for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			for (int i = 1; i < 2; i++) {
				System.out.println("---------- Time instance: " + (i + 1));

				switch (Crowdsensing.DATA_SET) {
				case GOWALLA:
					crowdsensing.readTasks(Constants.gowallaTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.gowallaWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case SMALL:
					crowdsensing.readTasks(Constants.smallTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.smallWorkerFileNamePrefix
							+ i + ".txt");
					break;
				}

				crowdsensing.matchingTasksWorkers();

				// debug
				System.out.println("#Tasks: " + crowdsensing.taskList.size());
				System.out.println("#Workers: " + crowdsensing.workerList.size());
				System.out.println("scheduling...");
				double startTime = System.nanoTime();
				
				crowdsensing.maxTasksMinimumWorkers();
				
				double runtime = (System.nanoTime() - startTime) / 1000000000.0;
				totalTime += runtime;
				System.out.println("Time: " + runtime);
				crowdsensing.time_instance++;
			}

			System.out.println("*************SUMMARY ITERATION " + (k + 1)
					+ " *************");
			System.out.println("#Total workers: " + crowdsensing.WorkerCount);
			System.out.println("#Total tasks: " + crowdsensing.TaskCount);
			totalAssignedWorkers += crowdsensing.TotalAssignedWorkers;

			double avgAssignedWorkers = ((double) totalAssignedWorkers)
					/ ((k + 1) * Constants.TIME_INSTANCE);

			long avgTime = ((long) totalTime) / ((k + 1) * Constants.TIME_INSTANCE);

			System.out.println("Total assigned workers: " + totalAssignedWorkers
					+ "   # of rounds:" + (k + 1) + "  avg: "
					+ avgAssignedWorkers);
			System.out.println("Total time: " + totalTime + "   # of rounds: "
					+ (k + 1) + "  avg time:" + avgTime);


			avgAvgWT = totalAvgWT / ((k + 1) * Constants.TIME_INSTANCE);
			avgVarWT = totalVARWT / ((k + 1) * Constants.TIME_INSTANCE);
			System.out.println("Average worker per task: " + avgAvgWT
					+ "   with variance: " + avgVarWT);
		} // end of for loop
	}

}