package test.geocrowd;

import org.geocrowd.Constants;
import org.geocrowd.GeoCrowd;
import org.geocrowd.AlgoEnums;
import org.junit.Test;

public class GeoCrowdTest {

	@Test
	public void testGeoCrowd() {
		GeoCrowd.DATA_SET = 3;
		GeoCrowd.algorithm = AlgoEnums.GR;
		GeoCrowd geoCrowd = new GeoCrowd();
		geoCrowd.printBoundaries();
		geoCrowd.createGrid();
		// geoCrowd.readEntropy();

		geoCrowd.readWorkers(Constants.smallWorkerFileNamePrefix + "0.txt");
		geoCrowd.readTasks(Constants.smallTaskFileNamePrefix + "0.txt");
		geoCrowd.matchingTasksWorkers();
		geoCrowd.computeAverageTaskPerWorker();
		System.out.println("avgTW " + geoCrowd.avgTW);
		System.out.println("varTW " + geoCrowd.varTW);

		geoCrowd.computeAverageWorkerPerTask();
		System.out.println("avgWT " + geoCrowd.avgWT);
		System.out.println("varWT " + geoCrowd.varWT);

		geoCrowd.maxWeightedMatching();

		System.out.println("Score: " + geoCrowd.TotalScore);
		System.out.println("Assigned task: " + geoCrowd.TotalTasksAssigned);
		System.out.println("Exact assigned task: "
				+ geoCrowd.TotalTasksExactMatch);
	}

	@Test
	public void testGenerateTasks() {
		GeoCrowd.DATA_SET = 0;
		GeoCrowd geoCrowd = new GeoCrowd();
		System.out.println(geoCrowd.algorithm + "  assignment    with "
				+ Constants.TaskDuration + "  task duration");
		geoCrowd.printBoundaries();
		geoCrowd.createGrid();
		geoCrowd.readEntropy();
		System.out.println("entropy list size: " + geoCrowd.entropyList.size());
		for (int i = 0; i < Constants.RoundCnt; i++) {
			geoCrowd.readTasksWithEntropy(Constants.gowallaTaskFileNamePrefix
					+ i + ".txt");
			geoCrowd.timeCounter++;
		}
	}

	@Test
	public void testGeoCrowd2() {
		int totalScore = 0;
		double totalAssignedTasks = 0;
		double totalExactAssignedTasks = 0;
		long totalTime = 0;
		double totalSumDist = 0;
		double avgAvgWT = 0;
		double avgVarWT = 0;
		double totalAvgWT = 0;
		double totalVARWT = 0;

		for (int k = 0; k < 1; k++) {

			System.out.println("+++++++ Iteration: " + (k + 1));
			GeoCrowd.DATA_SET = 1;
			GeoCrowd.algorithm = AlgoEnums.NNP ;
			GeoCrowd geoCrowd = new GeoCrowd();
			geoCrowd.printBoundaries();
			geoCrowd.createGrid();
			geoCrowd.readEntropy();
			for (int i = 0; i < Constants.RoundCnt; i++) {
				System.out.println("---------- Time instance: " + (i + 1));
				// compute average W/T
				// GeoCrowd g = new GeoCrowd();
				// switch (GeoCrowd.DATA_SET) {
				// case 0:
				// g.readTasks(Constants.gowallaTaskFileNamePrefix + i
				// + ".txt");
				// g.readWorkers(Constants.gowallaWorkerFileNamePrefix + i
				// + ".txt");
				// break;
				// case 1:
				// g.readTasks(Constants.syncTaskFileNamePrefix + i + ".txt");
				// g.readWorkers(Constants.syncWorkerFileNamePrefix + i
				// + ".txt");
				// break;
				// case 2:
				// g.readTasks(Constants.uniTaskFileNamePrefix + i + ".txt");
				// g.readWorkers(Constants.uniWorkerFileNamePrefix + i
				// + ".txt");
				// break;
				// case 3:
				// g.readTasks(Constants.smallTaskFileNamePrefix + i + ".txt");
				// g.readWorkers(Constants.smallWorkerFileNamePrefix + i
				// + ".txt");
				// break;
				// case 4:
				// g.readTasks(Constants.yelpTaskFileNamePrefix + i + ".txt");
				// g.readWorkers(Constants.yelpWorkerFileNamePrefix + i
				// + ".txt");
				// break;
				// }
				// g.matchingTasksWorkers();
				// g.computeAverageWorkerPerTask();
				// totalAvgWT += g.avgWT;
				// totalVARWT += g.varWT;
				// System.out.println("avgWT " + g.avgWT);
				// System.out.println("varWT " + g.varWT);

				switch (GeoCrowd.DATA_SET) {
				case 0:
					geoCrowd.readTasks(Constants.gowallaTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.gowallaWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case 1:
					geoCrowd.readTasks(Constants.syncTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.syncWorkerFileNamePrefix + i
							+ ".txt");
					break;
				case 2:
					geoCrowd.readTasks(Constants.uniTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.uniWorkerFileNamePrefix + i
							+ ".txt");
					break;
				case 3:
					geoCrowd.readTasks(Constants.smallTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.smallWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case 4:
					geoCrowd.readTasks(Constants.yelpTaskFileNamePrefix + i + ".txt");
					geoCrowd.readWorkers(Constants.yelpWorkerFileNamePrefix + i
							+ ".txt");
					break;
				}

				geoCrowd.matchingTasksWorkers();

				// debug
				System.out.println("#Tasks: " + geoCrowd.taskList.size());
				System.out.println("#Workers: " + geoCrowd.workerList.size());
				System.out.println("scheduling...");
				double startTime = System.nanoTime();
				geoCrowd.maxWeightedMatching();
				double runtime = (System.nanoTime() - startTime) / 1000000000.0;
				totalTime += runtime;
				System.out.println("Time: " + runtime);
				geoCrowd.timeCounter++;
			}

			System.out.println("*************SUMMARY ITERATION " + (k + 1)
					+ " *************");
			System.out.println("#Total workers: " + geoCrowd.WorkerCount);
			System.out.println("#Total tasks: " + geoCrowd.TaskCount);
			totalScore += geoCrowd.TotalScore;
			totalAssignedTasks += geoCrowd.TotalTasksAssigned;
			totalExactAssignedTasks += geoCrowd.TotalTasksExactMatch;
			totalSumDist += geoCrowd.TotalTravelDistance;

			double avgScore = ((double) totalScore) / (k + 1);
			double avgAssignedTasks = ((double) totalAssignedTasks)
					/ ((k + 1) * Constants.RoundCnt);
			double avgExactAssignedTasks = ((double) totalExactAssignedTasks)
					/ ((k + 1) * Constants.RoundCnt);
			long avgTime = ((long) totalTime) / ((k + 1) * Constants.RoundCnt);
			System.out.println("Total score: " + totalScore
					+ "   # of rounds: " + (k + 1) + "  avg: " + avgScore);
			System.out.println("Total assigned task: " + totalAssignedTasks
					+ "   # of rounds:" + (k + 1) + "  avg: "
					+ avgAssignedTasks);
			System.out.println("Total exact assigned task: "
					+ totalExactAssignedTasks + "   # of rounds: " + (k + 1)
					+ "  avg: " + avgExactAssignedTasks);
			System.out.println("Total time: " + totalTime + "   # of rounds: "
					+ (k + 1) + "  avg time:" + avgTime);
			double avgDist = totalSumDist / totalAssignedTasks;
			System.out.println("Total distances: " + totalSumDist
					+ "   # of rounds: " + (k + 1) + "  avg: " + avgDist);

			avgAvgWT = totalAvgWT / ((k + 1) * Constants.RoundCnt);
			avgVarWT = totalVARWT / ((k + 1) * Constants.RoundCnt);
			System.out.println("Average worker per task: " + avgAvgWT
					+ "   with variance: " + avgVarWT);
		} // end of for loop
	}
}