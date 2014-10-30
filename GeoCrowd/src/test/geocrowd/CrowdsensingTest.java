/**
 * *****************************************************************************
 * @ Year 2013 This is the source code of the following papers.
 *
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla
 * Kazemi, Cyrus Shahabi.
 *
 *
 * Please contact the author Hien To, ubriela@gmail.com if you have any
 * question.
 *
 * Contributors: Hien To - initial implementation
 * *****************************************************************************
 */
package test.geocrowd;

import org.geocrowd.AlgorithmEnum;
import org.geocrowd.GeocrowdSensing;
import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.common.Constants;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class CrowdsensingTest.
 */
public class CrowdsensingTest {

	/**
	 * Test.
	 */
	@Test
	public void test() {
		long totalTime = 0;
		double avgTW = 0.0;
		double avgWT = 0.0;

		for (int k = 0; k < 1; k++) {

			System.out.println("+++++++ Iteration: " + (k + 1));
			Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
			Geocrowd.algorithm = AlgorithmEnum.GREEDY_CLOSE_TO_DEADLINE;
			GeocrowdSensing crowdsensing = new GeocrowdSensing();
			
			crowdsensing.printBoundaries();
			crowdsensing.createGrid();
			crowdsensing.readEntropy();
			
			// for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
				System.out.println("---------- Time instance: " + (i + 1));

				switch (Geocrowd.DATA_SET) {
				case GOWALLA:
					crowdsensing.readTasks(Constants.gowallaTaskFileNamePrefix
							+ i + ".txt");
					crowdsensing
							.readWorkers(Constants.gowallaWorkerFileNamePrefix
									+ k + ".txt");
					break;
				case YELP:
					crowdsensing.readTasks(Constants.yelpTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.yelpWorkerFileNamePrefix
							+ k + ".txt");
					break;
				case UNIFORM:
					crowdsensing.readTasks(Constants.uniTaskFileNamePrefix + i
							+ ".txt");
					crowdsensing.readWorkers(Constants.uniWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case SKEWED:
					crowdsensing.readTasks(Constants.skewedTaskFileNamePrefix
							+ i + ".txt");
					crowdsensing
							.readWorkers(Constants.skewedWorkerFileNamePrefix
									+ i + ".txt");
					break;
				case SMALL_TEST:
					crowdsensing.readTasks(Constants.smallTaskFileNamePrefix
							+ i + ".txt");
					crowdsensing
							.readWorkers(Constants.smallWorkerFileNamePrefix
									+ i + ".txt");
					break;
				}

				crowdsensing.matchingTasksWorkers();
				crowdsensing.computeAverageTaskPerWorker();
				crowdsensing.computeAverageWorkerPerTask();
				avgTW += crowdsensing.avgTW;
				avgWT += crowdsensing.avgWT;

				/** populate virtual workers */
				if (Constants.K != 1)
					if (Geocrowd.algorithm != AlgorithmEnum.GREEDY_HIGH_TASK_COVERAGE_MULTI
					&& Geocrowd.algorithm != AlgorithmEnum.GREEDY_LARGE_WORKER_FANOUT_MULTI
					&& Geocrowd.algorithm != AlgorithmEnum.GREEDY_CLOSE_TO_DEADLINE_MULTI)
						crowdsensing.populateVitualWorkers();

				// debug
				System.out.println("#Tasks: " + crowdsensing.taskList.size());
				System.out.println("#Workers: "
						+ crowdsensing.workerList.size());
				System.out.println("scheduling...");
				double startTime = System.nanoTime();

				crowdsensing.minimizeWorkersMaximumTaskCoverage();

				double runtime = (System.nanoTime() - startTime) / 1000000000.0;
				totalTime += runtime;
				System.out.println("Time: " + runtime);
				crowdsensing.TimeInstance++;
			}

			System.out.println("*************SUMMARY ITERATION " + (k + 1)
					+ " *************");
			System.out.println("#Total workers: " + crowdsensing.WorkerCount);
			System.out.println("#Total tasks: " + crowdsensing.TaskCount);

			double avgAssignedWorkers = ((double) GeocrowdSensing.TotalAssignedWorkers)
					/ ((k + 1) * Constants.TIME_INSTANCE);
			double avgAssignedTasks = ((double) GeocrowdSensing.TotalAssignedTasks)
					/ ((k + 1) * Constants.TIME_INSTANCE);
			long avgTime = (totalTime) / ((k + 1) * Constants.TIME_INSTANCE);

			System.out.println("Total assigned workers: "
					+ GeocrowdSensing.TotalAssignedWorkers + "   #of rounds:"
					+ (k + 1) + "  avg: " + avgAssignedWorkers);
			System.out.println("Total assigned tasks: "
					+ GeocrowdSensing.TotalAssignedTasks + "   #of rounds:"
					+ (k + 1) + "  avg: " + avgAssignedTasks);
			System.out.println("Total time: " + totalTime + "   # of rounds: "
					+ (k + 1) + "  avg time:" + avgTime);

			System.out.println("average time to assign tasks: "
					+ GeocrowdSensing.AverageTimeToAssignTask * 1.0
					/ GeocrowdSensing.TotalAssignedTasks);

			System.out.println("avgTW/instance: " + avgTW
					/ ((k + 1) * Constants.TIME_INSTANCE));
			System.out.println("avgWT/instance: " + avgWT
					/ ((k + 1) * Constants.TIME_INSTANCE));
		} // end of for loop
	}

}
