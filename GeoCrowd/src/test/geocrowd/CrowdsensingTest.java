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
                double avgTW=0.0;
                double avgWT=0.0;
		
		for (int k = 0; k < 1; k++) {

			System.out.println("+++++++ Iteration: " + (k + 1));
			Crowdsensing.DATA_SET = DatasetEnum.GOWALLA;
			Crowdsensing.algorithm = AlgorithmEnum.GREEDY1;
			Crowdsensing crowdsensing = new Crowdsensing();
			//for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			for (int i = 0; i < 20; i++) {
				System.out.println("---------- Time instance: " + (i + 1));

				switch (Crowdsensing.DATA_SET) {
				case GOWALLA:
					crowdsensing.readTasks(Constants.gowallaTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.gowallaWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case YELP:
					crowdsensing.readTasks(Constants.yelpTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.yelpWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case UNIFORM:
					crowdsensing.readTasks(Constants.uniTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.uniWorkerFileNamePrefix
							+ i + ".txt");
					break;					
				case SKEWED:
					crowdsensing.readTasks(Constants.skewedTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.skewedWorkerFileNamePrefix
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
                                crowdsensing.computeAverageTaskPerWorker();
                                crowdsensing.computeAverageWorkerPerTask();
                                avgTW += crowdsensing.avgTW;
                                avgWT +=crowdsensing.avgWT;
                                
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
                        totalAssignedTasks += crowdsensing.TotalTasksAssigned;

			double avgAssignedWorkers = ((double) totalAssignedWorkers)
					/ ((k + 1) * Constants.TIME_INSTANCE);
                        double avgAssignedTasks = ((double) totalAssignedTasks)
					/ ((k + 1) * Constants.TIME_INSTANCE);
			long avgTime = ((long) totalTime) / ((k + 1) * Constants.TIME_INSTANCE);

			System.out.println("Total assigned workers: " + totalAssignedWorkers
					+ "   # of rounds:" + (k + 1) + "  avg: "
					+ avgAssignedWorkers);
                        System.out.println("Total assigned tasks: " + totalAssignedTasks
					+ "   # of rounds:" + (k + 1) + "  avg: "
					+ avgAssignedTasks);
			System.out.println("Total time: " + totalTime + "   # of rounds: "
					+ (k + 1) + "  avg time:" + avgTime);

                        
                       
                        System.out.println("Average task per worker per time instance: " + avgTW/((k+1)*Constants.TIME_INSTANCE));
                        System.out.println("Average worker per task per time instance: " + avgWT/((k+1)*Constants.TIME_INSTANCE));
		} // end of for loop
	}

}