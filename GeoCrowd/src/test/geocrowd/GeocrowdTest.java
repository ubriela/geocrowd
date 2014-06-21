package test.geocrowd;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.AlgorithmEnum;
import org.geocrowd.common.MBR;
import org.geocrowd.common.SpecializedWorker;
import org.geocrowd.util.Constants;
import org.junit.Test;

public class GeocrowdTest {

	@Test
	public void testGeoCrowd_Small() {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		Geocrowd.algorithm = AlgorithmEnum.BASIC;
		Geocrowd geoCrowd = new Geocrowd();
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
				+ geoCrowd.TotalTasksExpertiseMatch);
	}

	@Test
	public void testGenerateGowallaTasks() {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		Geocrowd geoCrowd = new Geocrowd();
		System.out.println(geoCrowd.algorithm + "  assignment    with "
				+ Constants.TaskDuration + "  task duration");
		geoCrowd.printBoundaries();
		geoCrowd.createGrid();
		geoCrowd.readEntropy();
		System.out.println("entropy list size: " + geoCrowd.entropyList.size());
		for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			geoCrowd.readTasksWithEntropy(Constants.gowallaTaskFileNamePrefix
					+ i + ".txt");
			geoCrowd.time_instance++;
		}
	}

	@Test
	public void testGenerateYelpWorkers() {
		Geocrowd.DATA_SET = DatasetEnum.YELP;
		Geocrowd geoCrowd = new Geocrowd();
		geoCrowd.printBoundaries();
		geoCrowd.readWorkers("dataset/real/yelp/worker/yelp_workers0.txt");
		try {

			FileWriter writer = new FileWriter("dataset/real/yelp/yelp.dat");
			BufferedWriter out = new BufferedWriter(writer);

			StringBuffer sb = new StringBuffer();
			double sum = 0;
			int count = 0;
			double maxMBR = 0;
			for (int i = 0; i < geoCrowd.workerList.size(); i++) {
				SpecializedWorker w = (SpecializedWorker) geoCrowd.workerList.get(i);
				sb.append(w.getLatitude() + "\t" + w.getLongitude() + "\n");
				double d = w.getMBR().diagonalLength();
				sum += d;
				count++;
				if (d > maxMBR)
					maxMBR = d;
			}
			
			out = new BufferedWriter(writer);
			out.write(sb.toString());
			out.close();
			MBR mbr = new MBR(geoCrowd.minLatitude, geoCrowd.minLongitude, geoCrowd.maxLatitude, geoCrowd.maxLongitude);
			System.out.println("Region MBR size: " + mbr.diagonalLength());
			
			System.out.println("Area: " + mbr.area());
			System.out.println("Number of users: " + count);
			System.out.println("Average users' MBR size: " + sum / count);
			System.out.println("Max users' MBR size: " + maxMBR);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGeoCrowd() {
		int totalScore = 0;
		double totalAssignedTasks = 0;
		double totalExpertiseAssignedTasks = 0;
		long totalTime = 0;
		double totalSumDist = 0;
		double avgAvgWT = 0;
		double avgVarWT = 0;
		double totalAvgWT = 0;
		double totalVARWT = 0;

		for (int k = 0; k < 20; k++) {	// k is the number of time instance

			System.out.println("+++++++ Iteration: " + (k + 1));
			Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
			Geocrowd.algorithm = AlgorithmEnum.BASIC;
			Geocrowd geoCrowd = new Geocrowd();
			geoCrowd.printBoundaries();
			geoCrowd.createGrid();
			geoCrowd.readEntropy();
			for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
				System.out.println("---------- Time instance: " + (i + 1));

				switch (Geocrowd.DATA_SET) {
				case GOWALLA:
					geoCrowd.readTasks(Constants.gowallaTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.gowallaWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case SKEWED:
					geoCrowd.readTasks(Constants.skewedTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.skewedWorkerFileNamePrefix + i
							+ ".txt");
					break;
				case UNIFORM:
					geoCrowd.readTasks(Constants.uniTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.uniWorkerFileNamePrefix + i
							+ ".txt");
					break;
				case SMALL:
					geoCrowd.readTasks(Constants.smallTaskFileNamePrefix + i
							+ ".txt");
					geoCrowd.readWorkers(Constants.smallWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case YELP:
					geoCrowd.readTasks(Constants.yelpTaskFileNamePrefix + i
							+ ".txt");
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
				geoCrowd.time_instance++;
			}

			System.out.println("*************SUMMARY ITERATION " + (k + 1)
					+ " *************");
			System.out.println("#Total workers: " + geoCrowd.WorkerCount);
			System.out.println("#Total tasks: " + geoCrowd.TaskCount);
			totalScore += geoCrowd.TotalScore;
			totalAssignedTasks += geoCrowd.TotalTasksAssigned;
			totalExpertiseAssignedTasks += geoCrowd.TotalTasksExpertiseMatch;
			totalSumDist += geoCrowd.TotalTravelDistance;

			double avgScore = ((double) totalScore) / (k + 1);
			double avgAssignedTasks = ((double) totalAssignedTasks)
					/ ((k + 1) * Constants.TIME_INSTANCE);
			double avgExpertiseAssignedTasks = ((double) totalExpertiseAssignedTasks)
					/ ((k + 1) * Constants.TIME_INSTANCE);
			long avgTime = ((long) totalTime) / ((k + 1) * Constants.TIME_INSTANCE);
			System.out.println("Total score: " + totalScore
					+ "   # of rounds: " + (k + 1) + "  avg: " + avgScore);
			System.out.println("Total assigned taskes: " + totalAssignedTasks
					+ "   # of rounds:" + (k + 1) + "  avg: "
					+ avgAssignedTasks);
			System.out.println("Total expertise matches: "
					+ totalExpertiseAssignedTasks + "   # of rounds: " + (k + 1)
					+ "  avg: " + avgExpertiseAssignedTasks);
			System.out.println("Total time: " + totalTime + "   # of rounds: "
					+ (k + 1) + "  avg time:" + avgTime);
			double avgDist = totalSumDist / totalAssignedTasks;
			System.out.println("Total distances: " + totalSumDist
					+ "   # of rounds: " + (k + 1) + "  avg: " + avgDist);

			avgAvgWT = totalAvgWT / ((k + 1) * Constants.TIME_INSTANCE);
			avgVarWT = totalVARWT / ((k + 1) * Constants.TIME_INSTANCE);
			System.out.println("Average worker per task: " + avgAvgWT
					+ "   with variance: " + avgVarWT);
		} // end of for loop
	}
}