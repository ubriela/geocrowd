package test.geocrowd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.datasets.yelp.Constant;
import org.geocrowd.AlgorithmEnum;
import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.OfflineMTC;
import org.geocrowd.OnlineMTC;
import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.GenericTask;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 * @author Luan
 */
public class OnlineMTCTest {

	public static void main(String[] args) throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.FOURSQUARE;
//		overloading();
		
		
//		double[] radii = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5 };
//		int totalBudget = 42;
//		int start_time = 200;
		
		double[] radii = {0.01, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5};
		int totalBudget = 144;
		int start_time = 0;
		workload_vary_r(radii, totalBudget, start_time);
		
//		int[] budgets = new int[] { 21, 42, 84, 168, 336, 672, 1344, 2688 };
//		start_time = 200;
		
		int[] budgets = { 24, 48, 96, 192, 384, 768, 1536, 3072 };
		start_time = 0;
		workload_vary_b(budgets, start_time);
		
		
//		int[] delta = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
//		totalBudget = 42;
//		start_time = 200;
		
		int[] delta = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		totalBudget = 144;
		start_time = 0;
		workload_vary_delta(delta, totalBudget, start_time);
		
//		int cycle_length = 7;
//		int workload_size = 4;
//		int totalBudget = 168;
//		int workloadCount = 12;
//		start_time = 0;

		
		int cycle_length = 24;
		int workload_size = 1;
		totalBudget = 144;
		int workloadCount = 40;
		start_time = 0;
		workload_all(cycle_length, workload_size, totalBudget, start_time, workloadCount);
		
		
//		workload();
//		vary_a();
//		vary_eps();
//		vary_r();
//		vary_b();
//		vary_en();
	}

	private static int[] computeHistoryBudgets(boolean isFixed, int budget,
			int start_time) {
//		Geocrowd.DATA_SET = DatasetEnum.FOURSQUARE;
		OfflineMTC offlineMTC = new OfflineMTC();
		offlineMTC.TimeInstance = 0;
		offlineMTC.isFixed = isFixed;
		offlineMTC.budget = budget;
		offlineMTC.TaskCount = 0;
		offlineMTC.TotalAssignedTasks = 0;
		offlineMTC.TotalAssignedWorkers = 0;
		offlineMTC.workerList = new ArrayList<>();
		offlineMTC.taskList = new ArrayList<GenericTask>();

		for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			switch (Geocrowd.DATA_SET) {
			case FOURSQUARE:
				offlineMTC.readTasks(Constants.foursquareTaskFileNamePrefix + i
						+ ".txt");
				offlineMTC.readWorkers(Constants.foursquareWorkerFileNamePrefix
						+ (i + start_time) + ".txt", i);
				break;
			case GOWALLA:
				offlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix + i
						+ ".txt");
				offlineMTC.readWorkers(Constants.gowallaWorkerFileNamePrefix
						+ (i + start_time) + ".txt", i);
				break;
			case YELP:
				offlineMTC.readTasks(Constants.yelpTaskFileNamePrefix + i
						+ ".txt");
				offlineMTC.readWorkers(Constants.yelpWorkerFileNamePrefix
						+ (i + start_time) + ".txt", i);
				break;
			case UNIFORM:
				offlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
						+ ".txt");
				offlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
						+ (i + start_time) + ".txt", i);
				break;
			case SKEWED:
				offlineMTC.readTasks(Constants.skewedTaskFileNamePrefix + i
						+ ".txt");
				offlineMTC.readWorkers(Constants.skewedWorkerFileNamePrefix
						+ (i + start_time) + ".txt", i);
				break;
			case SMALL_TEST:
				offlineMTC.readTasks(Constants.smallTaskFileNamePrefix + i
						+ ".txt");
				offlineMTC.readWorkers(Constants.smallWorkerFileNamePrefix
						+ (i + start_time) + ".txt", i);
				break;
			}
		}

		offlineMTC.matchingTasksWorkers();

		HashSet<Integer> workerSet = offlineMTC.maxTaskCoverage();
		System.out.println("\nCoverage : " + offlineMTC.TotalAssignedTasks);
		System.out.println("Counts :");
		for (int count : offlineMTC.counts)
			System.out.print(count + "\t");
		System.out.println();
		return offlineMTC.counts;
	}

	/**
	 * Varying all tasks
	 * 
	 * @throws IOException
	 */
	private static void workload_all(int cycle_length, int workload_size, int totalBudget, int starttime, int w_count) throws IOException {

		Constants.TIME_INSTANCE = cycle_length * workload_size;
		int start_time = starttime;
		int workloadCount = w_count;

		System.out.println("\nRadius = " + Constants.radius);
		System.out.println("Budget = " + totalBudget);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD2 };

		int[][] coveredTasks = new int[workloadCount][algorithms.length + 2];
		int[][] assignedWorkers = new int[workloadCount][algorithms.length + 2];

		/**
		 * Iterate all possible workloads
		 */
		for (int w = 0; w < workloadCount; w++) {

			Geocrowd.TimeInstance = 0;
			GeocrowdTest.main(null);

			System.out.println("start time " + start_time);

			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + Constants.TIME_INSTANCE + 1;

			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];

//				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.TimeInstance = 0;
				onlineMTC.eps = 0; // unused
				onlineMTC.TaskCount = 0;
				onlineMTC.TotalAssignedTasks = 0;
				onlineMTC.TotalAssignedWorkers = 0;
				onlineMTC.workerList = null;
				onlineMTC.taskList = null;
				onlineMTC.workerList = new ArrayList<>();
				onlineMTC.taskList = new ArrayList<>();
				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();

				onlineMTC.budgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
					int next_time = (i + start_time + Constants.TIME_INSTANCE);

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC.readTasks(Constants.foursquareTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.foursquareWorkerFileNamePrefix
								+ (i + next_time) + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ next_time + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();
					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									onlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									onlineMTC.TotalAssignedWorkers,
									onlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													onlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[w][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[w][g] = OnlineMTC.TotalAssignedWorkers;
			}

			coveredTasks[w][algorithms.length] = fixed_offline_cov;
			coveredTasks[w][algorithms.length + 1] = dynamic_offline_cov;

			// update start_time
			start_time = next_time_period;
		}

		/**
		 * print result
		 */
		System.out.println("\n\n##################");
		System.out.println("Budget = " + totalBudget);
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		System.out.printf("%-20s \t", "FixedOffline");
		System.out.printf("%-20s \t", "DynamicOffline");
		for (int w = 0; w < workloadCount; w++) {
			System.out.printf("\n%-20d \t", w);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				System.out.printf("%-20d \t", coveredTasks[w][g2]);
		}
	}

	private static void workload_vary_delta(int[] delta, int totalBudget, int starttime) throws IOException {

		System.out.println("\nRadius = " + Constants.radius);
		System.out.println("Budget = " + totalBudget);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD2 };

		int[][] coveredTasks = new int[delta.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[delta.length][algorithms.length + 2];

		for (int d = 0; d < delta.length; d++) {
			Constants.TaskDuration = delta[d];
			System.out.println("\n----\ndelta: " + Constants.TaskDuration);
			Geocrowd.TimeInstance = 0;
			GeocrowdTest.main(null);

			int start_time = starttime;
			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + Constants.TIME_INSTANCE + 1;
			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];

//				Geocrowd.DATA_SET = DatasetEnum.FOURSQUARE;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.TimeInstance = 0;
				onlineMTC.TaskCount = 0;
				onlineMTC.TotalAssignedTasks = 0;
				onlineMTC.TotalAssignedWorkers = 0;
				onlineMTC.workerList = new ArrayList<>();
				onlineMTC.taskList = new ArrayList<>();
				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();

				onlineMTC.budgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
					int next_time = (i + start_time + Constants.TIME_INSTANCE + 1);

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC
								.readTasks(Constants.foursquareTaskFileNamePrefix
										+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.foursquareWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ next_time + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();
					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									onlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									onlineMTC.TotalAssignedWorkers,
									onlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													onlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[d][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[d][g] = OnlineMTC.TotalAssignedWorkers;

			}

			coveredTasks[d][algorithms.length] = fixed_offline_cov;
			coveredTasks[d][algorithms.length + 1] = dynamic_offline_cov;

		}

		/**
		 * print result
		 */
		System.out.println("\n\n##################");
		System.out.println("Budget = " + totalBudget);
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		System.out.printf("%-20s \t", "FixedOffline");
		System.out.printf("%-20s \t", "DynamicOffline");
		for (int d = 0; d < delta.length; d++) {
			System.out.printf("\n%-20d \t", delta[d]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				System.out.printf("%-20d \t", coveredTasks[d][g2]);
			}
		}
	}

	private static void workload_vary_r(double[] radii, int totalBudget, int starttime) throws IOException {

		System.out.println("Budget = " + totalBudget);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD2 };

		int[][] coveredTasks = new int[radii.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[radii.length][algorithms.length + 2];

		for (int d = 0; d < radii.length; d++) {
			Constants.radius = radii[d];
			System.out.println("\n----\nRadius " + Constants.radius);
			Geocrowd.TimeInstance = 0;
			GeocrowdTest.main(null);

			int start_time = starttime;
			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + Constants.TIME_INSTANCE + 1;
			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];

//				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.TimeInstance = 0;
				onlineMTC.eps = 0;
				onlineMTC.TaskCount = 0;
				onlineMTC.TotalAssignedTasks = 0;
				onlineMTC.TotalAssignedWorkers = 0;
				onlineMTC.workerList = null;
				onlineMTC.taskList = null;
				onlineMTC.workerList = new ArrayList<>();
				onlineMTC.taskList = new ArrayList<>();
				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();

				onlineMTC.budgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
					int next_time = (i + start_time + Constants.TIME_INSTANCE + 1);

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC
								.readTasks(Constants.foursquareTaskFileNamePrefix
										+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.foursquareWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ next_time + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();
					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									onlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									onlineMTC.TotalAssignedWorkers,
									onlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													onlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[d][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[d][g] = OnlineMTC.TotalAssignedWorkers;

			}

			coveredTasks[d][algorithms.length] = fixed_offline_cov;
			coveredTasks[d][algorithms.length + 1] = dynamic_offline_cov;

		}

		/**
		 * print result
		 */
		System.out.println("\n\n##################");
		System.out.println("Budget = " + totalBudget);
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		System.out.printf("%-20s \t", "FixedOffline");
		System.out.printf("%-20s \t", "DynamicOffline");
		for (int d = 0; d < radii.length; d++) {
			System.out.printf("\n%-20f \t", radii[d]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				System.out.printf("%-20d \t", coveredTasks[d][g2]);
			}
		}
	}

	private static void workload_vary_b(int[] budgets, int starttime) throws IOException {

		System.out.println("radius = " + Constants.radius);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD2 };

		int[][] coveredTasks = new int[budgets.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[budgets.length][algorithms.length + 2];

		for (int b = 0; b < budgets.length; b++) {
			int totalBudget = budgets[b];
			System.out.println("\n----\nBudget = " + totalBudget);
			System.out.println("radius " + Constants.radius);
			Geocrowd.TimeInstance = 0;
			GeocrowdTest.main(null);

			int start_time = starttime;
			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + Constants.TIME_INSTANCE + 1;
			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];

//				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.TimeInstance = 0;
				onlineMTC.eps = 0;
				onlineMTC.TaskCount = 0;
				onlineMTC.TotalAssignedTasks = 0;
				onlineMTC.TotalAssignedWorkers = 0;
				onlineMTC.workerList = null;
				onlineMTC.taskList = null;
				onlineMTC.workerList = new ArrayList<>();
				onlineMTC.taskList = new ArrayList<>();
				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();

				onlineMTC.budgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
					int next_time = (i + start_time + Constants.TIME_INSTANCE + 1);

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC
								.readTasks(Constants.foursquareTaskFileNamePrefix
										+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.foursquareWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ next_time + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();
					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									onlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									onlineMTC.TotalAssignedWorkers,
									onlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													onlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[b][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[b][g] = OnlineMTC.TotalAssignedWorkers;

			}

			coveredTasks[b][algorithms.length] = fixed_offline_cov;
			coveredTasks[b][algorithms.length + 1] = dynamic_offline_cov;

		}

		/**
		 * print result
		 */
		System.out.println("\n\n##################");
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		System.out.printf("%-20s \t", "FixedOffline");
		System.out.printf("%-20s \t", "DynamicOffline");
		for (int b = 0; b < budgets.length; b++) {
			System.out.printf("\n%-20d \t", budgets[b]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				System.out.printf("%-20d \t", coveredTasks[b][g2]);
			}
		}
	}

	@Test
	public void testWorkload() throws IOException {

		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		
		GeocrowdTest.main(null);

		int totalBudget = 168;
		int start_time = 200;
		int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

		System.out.println("\nRadius = " + Constants.radius);
		System.out.println("Budget = " + totalBudget);

		 Double[] epss = new Double[] {0.05, 0.1, 0.15, 0.2, 0.25};
//		double[] epss = new double[] { 0.1 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
//				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD2,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD_T};

		int[][] coveredTasks = new int[epss.length][algorithms.length];
		int[][] assignedWorkers = new int[epss.length][algorithms.length];

		GeocrowdTest.main(null); // generate set of tasks for next period

		// apply offline method to next period
		int next_time_period = start_time + Constants.TIME_INSTANCE + 1;
//		computeHistoryBudgets(true, totalBudget, next_time_period);
//		int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
		computeHistoryBudgets(false, totalBudget, next_time_period);
		int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

		// use the same set of workers/tasks for all following period
		for (int eps = 0; eps < epss.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.TimeInstance = 0;
				onlineMTC.eps = epss[eps];
				onlineMTC.TaskCount = 0;
				onlineMTC.TotalAssignedTasks = 0;
				onlineMTC.TotalAssignedWorkers = 0;
				onlineMTC.workerList = null;
				onlineMTC.taskList = null;
				onlineMTC.workerList = new ArrayList<>();
				onlineMTC.taskList = new ArrayList<>();
				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();

				onlineMTC.budgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
					int next_time = (i + start_time + Constants.TIME_INSTANCE + 1);

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC
								.readTasks(Constants.foursquareTaskFileNamePrefix
										+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.foursquareWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ next_time + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ next_time + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();
					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									onlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									onlineMTC.TotalAssignedWorkers,
									onlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													onlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[eps][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[eps][g] = OnlineMTC.TotalAssignedWorkers;
			}
		}

		/**
		 * print result
		 */
		System.out.println("\n\n##################");
		System.out.println("Budget = " + totalBudget);
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		for (int eps = 0; eps < epss.length; eps++) {
			System.out.printf("\n%-20f \t", epss[eps]);
			for (int g2 = 0; g2 < algorithms.length; g2++) {
				System.out.printf("%-20d \t", coveredTasks[eps][g2]);
			}
		}
	}

	private static void vary_eps() throws IOException {

		// Double[] epss = new Double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7,
		// 0.8,
		// 0.9 };

		double[] epss = new double[] { 0.3, 0.5, 0.7 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
		// AlgorithmEnum.MAX_COVER_BASIC
		// ,
		AlgorithmEnum.MAX_COVER_ADAPT_B };

		int[][] coveredTasks = new int[epss.length][algorithms.length];
		int[][] assignedWorkers = new int[epss.length][algorithms.length];

		int totalBudget = 400;
		Constants.F = 1.0;

		System.out.println("Radius = " + Constants.radius);
		System.out.println("F = " + Constants.F);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		for (int eps = 0; eps < epss.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];

				// update alpha for temporal, spatial algorithms.

//				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.eps = epss[eps];
				onlineMTC.TaskCount = 0;
				OnlineMTC.TotalAssignedTasks = 0;
				OnlineMTC.TotalAssignedWorkers = 0;
				OnlineMTC.workerList = null;
				OnlineMTC.workerList = new ArrayList<>();
				OnlineMTC.taskList = new ArrayList<>();

				onlineMTC.totalBudget = totalBudget;
				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC
								.readTasks(Constants.foursquareTaskFileNamePrefix
										+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.foursquareWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ i + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ i + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();
					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1), onlineMTC.TaskCount,
									onlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									onlineMTC.TotalAssignedWorkers,
									onlineMTC.TotalAssignedTasks
											/ onlineMTC.TotalAssignedWorkers);
				}

				coveredTasks[eps][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[eps][g] = OnlineMTC.TotalAssignedWorkers;
			}
		}

		/**
		 * print result
		 */
		System.out.println("\n##################");
		System.out.println("Budget = " + totalBudget);
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		for (int eps = 0; eps < epss.length; eps++) {
			System.out.printf("\n%-20f \t", epss[eps]);
			for (int g2 = 0; g2 < algorithms.length; g2++) {
				System.out.printf("%-20d \t", coveredTasks[eps][g2]);
			}
		}
	}

	private static void vary_a() throws IOException {

		double[] listAlpha = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		int[][] coveredTasks = new int[listAlpha.length][algorithms.length];
		int[][] assignedWorkers = new int[listAlpha.length][algorithms.length];

		int totalBudget = 160;
		Constants.F = 1.0;

		System.out.println("Radius = " + Constants.radius);
		System.out.println("F = " + Constants.F);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		for (int al = 0; al < listAlpha.length; al++)
			for (int g = 0; g < algorithms.length; g++) {
				double alpha = listAlpha[al];
				AlgorithmEnum algorithm = algorithms[g];

				// update alpha for temporal, spatial algorithms.
				Constants.alpha = alpha;

//				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();

				onlineMTC.TaskCount = 0;
				OnlineMTC.TotalAssignedTasks = 0;
				OnlineMTC.TotalAssignedWorkers = 0;
				OnlineMTC.workerList = new ArrayList<>();
				OnlineMTC.taskList = new ArrayList<>();

				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();
				onlineMTC.totalBudget = totalBudget;
				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC
								.readTasks(Constants.foursquareTaskFileNamePrefix
										+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.foursquareWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ i + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ i + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();
					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1), onlineMTC.TaskCount,
									onlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									onlineMTC.TotalAssignedWorkers,
									onlineMTC.TotalAssignedTasks
											/ onlineMTC.TotalAssignedWorkers);
				}

				coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
			}

		/**
		 * print result
		 */
		System.out.println("\n##################");
		System.out.println("Budget = " + totalBudget);
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		for (int al2 = 0; al2 < listAlpha.length; al2++) {
			System.out.printf("\n%-20f \t", listAlpha[al2]);
			for (int g2 = 0; g2 < algorithms.length; g2++) {
				System.out.printf("%-20d \t", coveredTasks[al2][g2]);
			}
		}
	}

	private static void vary_r() throws IOException {

		double[] listAlpha = new double[] { 0.1, 0.3, 0.5, 0.7 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		int[][] coveredTasks = new int[listAlpha.length][algorithms.length];
		int[][] assignedWorkers = new int[listAlpha.length][algorithms.length];

		int totalBudget = 160;
		double[] listRadius = new double[] { 0.1, 0.5, 1.0, 2.0 };
		Constants.F = 1.0;

		System.out.println("F = " + Constants.F);
		System.out.println("Budget = " + totalBudget);

		for (double d : listRadius) {
			Constants.radius = d;
			System.out.println("\nRadius = " + Constants.radius);
			GeocrowdTest.main(null);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];

					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;

//					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
					Geocrowd.algorithm = algorithm;
					OnlineMTC onlineMTC = new OnlineMTC();
					OnlineMTC.TotalAssignedTasks = 0;
					OnlineMTC.TotalAssignedWorkers = 0;
					/**
					 * clear worker, task list
					 */
					OnlineMTC.taskList.clear();
					OnlineMTC.workerList.clear();
					onlineMTC.totalBudget = totalBudget;
					// System.out.println("ttasks\ttasks\tworkers\tT/W");
					for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

						switch (Geocrowd.DATA_SET) {
						case FOURSQUARE:
							onlineMTC
									.readTasks(Constants.foursquareTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.foursquareWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case GOWALLA:
							onlineMTC
									.readTasks(Constants.gowallaTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.gowallaWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case YELP:
							onlineMTC
									.readTasks(Constants.yelpTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.yelpWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case UNIFORM:
							onlineMTC.readTasks(Constants.uniTaskFileNamePrefix
									+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.uniWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SKEWED:
							onlineMTC
									.readTasks(Constants.skewedTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.skewedWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SMALL_TEST:
							onlineMTC
									.readTasks(Constants.smallTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.smallWorkerFileNamePrefix
											+ i + ".txt");
							break;
						}

						onlineMTC.matchingTasksWorkers();
						HashSet<Integer> workerSet = onlineMTC.maxCoverage();

						onlineMTC.TimeInstance++;
					}

					coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
					assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
				}

			/**
			 * print result
			 */
			System.out.println("\n##################");
			System.out.println("Budget = " + totalBudget);
			System.out.println("#Covered Tasks");
			System.out.printf("%-20s \t", " ");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				System.out.printf("%-20s \t", algorithms[j2]);
			for (int al2 = 0; al2 < listAlpha.length; al2++) {
				System.out.printf("\n%-20s \t", "Alpha=" + listAlpha[al2]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					System.out.printf("%-20d \t", coveredTasks[al2][g2]);
				}
			}
		}
	}

	private static void vary_en() throws IOException {

		double[] listAlpha = new double[] { 0.1, 0.3, 0.5, 0.7 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		int[][] coveredTasks = new int[listAlpha.length][algorithms.length];
		int[][] assignedWorkers = new int[listAlpha.length][algorithms.length];

		int totalBudget = 160;
		double[] listEn = new double[] { 0.1, 0.4, 0.7, 1.0 };
		Constants.radius = 2.0;

		System.out.println("Radius = " + Constants.radius);
		System.out.println("Budget = " + totalBudget);

		for (double en : listEn) {
			Constants.F = en;
			System.out.println("En = " + Constants.F);
			GeocrowdTest.main(null);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];

					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;

//					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
					Geocrowd.algorithm = algorithm;
					OnlineMTC onlineMTC = new OnlineMTC();
					OnlineMTC.TotalAssignedTasks = 0;
					OnlineMTC.TotalAssignedWorkers = 0;
					/**
					 * clear worker, task list
					 */
					OnlineMTC.taskList.clear();
					OnlineMTC.workerList.clear();
					onlineMTC.totalBudget = totalBudget;
					// System.out.println("ttasks\ttasks\tworkers\tT/W");
					for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

						switch (Geocrowd.DATA_SET) {
						case FOURSQUARE:
							onlineMTC
									.readTasks(Constants.foursquareTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.foursquareWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case GOWALLA:
							onlineMTC
									.readTasks(Constants.gowallaTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.gowallaWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case YELP:
							onlineMTC
									.readTasks(Constants.yelpTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.yelpWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case UNIFORM:
							onlineMTC.readTasks(Constants.uniTaskFileNamePrefix
									+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.uniWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SKEWED:
							onlineMTC
									.readTasks(Constants.skewedTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.skewedWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SMALL_TEST:
							onlineMTC
									.readTasks(Constants.smallTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.smallWorkerFileNamePrefix
											+ i + ".txt");
							break;
						}

						onlineMTC.matchingTasksWorkers();
						HashSet<Integer> workerSet = onlineMTC.maxCoverage();

						onlineMTC.TimeInstance++;
					}

					coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
					assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
				}

			/**
			 * print result
			 */
			System.out.println("\n##################");
			System.out.println("Budget = " + totalBudget);
			System.out.println("#Covered Tasks");
			System.out.printf("%-20s \t", " ");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				System.out.printf("%-20s \t", algorithms[j2]);
			for (int al2 = 0; al2 < listAlpha.length; al2++) {
				System.out.printf("\n%-20s \t", "Alpha=" + listAlpha[al2]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					System.out.printf("%-20d \t", coveredTasks[al2][g2]);
				}
			}
		}
	}

	private static void vary_b() throws IOException {

		double[] listAlpha = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		int[][] coveredTasks = new int[listAlpha.length][algorithms.length];
		int[][] assignedWorkers = new int[listAlpha.length][algorithms.length];

		int totalBudget = 0;
		// Constants.Radius = 2.0;
		Constants.F = 1.0;

		int[] listBudget = new int[] { 40, 80, 160, 320, 640, 1280,
				2560 };

		System.out.println("F = " + Constants.F);
		System.out.println("Radius = " + Constants.radius);

		GeocrowdTest.main(null);

		for (int b : listBudget) {
			totalBudget = b;
			System.out.println("\nBudget = " + totalBudget);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];

					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;

//					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
					Geocrowd.algorithm = algorithm;
					OnlineMTC onlineMTC = new OnlineMTC();

					onlineMTC.TaskCount = 0;
					OnlineMTC.TotalAssignedTasks = 0;
					OnlineMTC.TotalAssignedWorkers = 0;
					OnlineMTC.workerList = new ArrayList<>();
					OnlineMTC.taskList = new ArrayList<>();

					/**
					 * clear worker, task list
					 */
					OnlineMTC.taskList.clear();
					OnlineMTC.workerList.clear();
					onlineMTC.totalBudget = totalBudget;
					// System.out.println("ttasks\ttasks\tworkers\tT/W");
					for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

						switch (Geocrowd.DATA_SET) {
						case FOURSQUARE:
							onlineMTC
									.readTasks(Constants.foursquareTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.foursquareWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case GOWALLA:
							onlineMTC
									.readTasks(Constants.gowallaTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.gowallaWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case YELP:
							onlineMTC
									.readTasks(Constants.yelpTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.yelpWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case UNIFORM:
							onlineMTC.readTasks(Constants.uniTaskFileNamePrefix
									+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.uniWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SKEWED:
							onlineMTC
									.readTasks(Constants.skewedTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.skewedWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SMALL_TEST:
							onlineMTC
									.readTasks(Constants.smallTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.smallWorkerFileNamePrefix
											+ i + ".txt");
							break;
						}

						onlineMTC.matchingTasksWorkers();
						HashSet<Integer> workerSet = onlineMTC.maxCoverage();

						onlineMTC.TimeInstance++;
					}

					coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
					assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
				}

			/**
			 * print result
			 */
			System.out.println("\n##################");
			System.out.println("Budget = " + totalBudget);
			System.out.println("#Covered Tasks");
			System.out.printf("%-20s \t", " ");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				System.out.printf("%-20s \t", algorithms[j2]);
			for (int al2 = 0; al2 < listAlpha.length; al2++) {
				System.out.printf("\n%-20s \t", "Alpha=" + listAlpha[al2]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					System.out.printf("%-20d \t", coveredTasks[al2][g2]);
				}
			}
		}
	}

	private static void overloading() throws IOException {

		int budget = 1000;
		double[] alpha = new double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8,
				0.9, 1.0 };
		// double[] alpha = new double[] { 0.1 };
		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
		// AlgorithmEnum.MAX_COVER_BASIC,
		AlgorithmEnum.MAX_COVER_BASIC_MO };

		int[][] coveredTasks = new int[alpha.length][algorithms.length];
		int[][] maxAssignments = new int[alpha.length][algorithms.length];
		int[][] assignedWorkers = new int[alpha.length][algorithms.length];

		System.out.println("ttasks\ttasks\tworkers\tT/W");
		for (int d = 0; d < alpha.length; d++) {
			Constants.alpha = alpha[d];

			for (int g = 0; g < algorithms.length; g++) {

				OnlineMTC onlineMTC = new OnlineMTC();

//				Geocrowd.DATA_SET = DatasetEnum.FOURSQUARE;
				Geocrowd.algorithm = algorithms[g];
				onlineMTC.TimeInstance = 0;
				onlineMTC.TaskCount = 0;
				onlineMTC.TotalAssignedTasks = 0;
				onlineMTC.TotalAssignedWorkers = 0;
				onlineMTC.workerList = new ArrayList<>();
				onlineMTC.taskList = new ArrayList<>();

				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();
				onlineMTC.totalBudget = budget;
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						onlineMTC
								.readTasks(Constants.foursquareTaskFileNamePrefix
										+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.foursquareWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case GOWALLA:
						onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.gowallaWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case YELP:
						onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.yelpWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case UNIFORM:
						onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
						onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix
								+ i + ".txt");
						break;
					case SKEWED:
						onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.skewedWorkerFileNamePrefix
										+ i + ".txt");
						break;
					case SMALL_TEST:
						onlineMTC.readTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
						onlineMTC
								.readWorkers(Constants.smallWorkerFileNamePrefix
										+ i + ".txt");
						break;
					}

					onlineMTC.matchingTasksWorkers();

					HashSet<Integer> workerSet = onlineMTC.maxCoverage();

					onlineMTC.TimeInstance++;
				}
				System.out.println("##################");

				System.out.printf("\n%-15s %-15s %-15s %-15s %-15s",
						"TotalTask", "CoveredTask", "TotalWorker",
						"SelectedWorker", "W/T");

				System.out.printf("\n%-15d %-15d %-15d %-15d %-15d",
						onlineMTC.TaskCount, onlineMTC.TotalAssignedTasks,
						onlineMTC.totalBudget, onlineMTC.TotalAssignedWorkers,
						onlineMTC.TotalAssignedTasks
								/ onlineMTC.TotalAssignedWorkers);

				maxAssignments[d][g] = onlineMTC.printWorkerCounts();

				coveredTasks[d][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[d][g] = OnlineMTC.TotalAssignedWorkers;

			}

			/**
			 * print result
			 */
			System.out.println("\n\n##################");
			System.out.println("Budget = " + budget);
			System.out.println("#Covered Tasks");
			System.out.printf("%-20s \t", " ");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				System.out.printf("%-20s \t", algorithms[j2]);
			for (int a = 0; a < alpha.length; a++) {
				System.out.printf("\n%-20f \t", alpha[a]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					System.out.printf("%-20d \t", coveredTasks[d][g2]);
				}
			}

			for (int j2 = 0; j2 < algorithms.length; j2++)
				System.out.printf("%-20s \t", algorithms[j2]);
			for (int a = 0; a < alpha.length; a++) {
				System.out.printf("\n%-20f \t", alpha[a]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					System.out.printf("%-20d \t", maxAssignments[d][g2]);
				}
			}
		}
	}

	private static void test_all() throws IOException {

		double[] listAlpha = new double[] { 0.05 };

		int[] listBudgetTest = new int[] { 2000 };

		double[] listRadius = new double[] {};

		double[] listF = new double[] {};

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		// Iterate over alpha and algorithm, Radius, f, budget
		int numLoops = listRadius.length + listF.length + listBudgetTest.length;
		for (int iter = 0; iter < numLoops; iter++) {

			int[][] coveredTasks = new int[listAlpha.length][algorithms.length];
			int[][] assignedWorkers = new int[listAlpha.length][algorithms.length];

			int totalBudget = 1000;
			double Radius = 0.2;
			double F = 1.0;

			// vary Radius
			if (iter < listRadius.length) {
				Radius = listRadius[iter];
			} else if (iter >= listRadius.length
					&& iter < listRadius.length + listF.length)
				F = listF[iter - listRadius.length];
			else if (iter >= listRadius.length + listF.length)
				totalBudget = listBudgetTest[iter - listRadius.length
						- listF.length];

			Constants.F = F;
			Constants.radius = Radius;

			// Regenerate data
			if (iter < listRadius.length + listF.length)
				GeocrowdTest.main(null);

			System.out.println("Radius = " + Constants.radius);
			System.out.println("F = " + Constants.F);
			System.out.println("Budget = " + totalBudget);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];
					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;
					// System.out.println("\nAlpha = "+Constants.alpha);
					// System.out.println("\nAlgorithm = "+algorithm);

					// for (Integer j = 0; j < listBudgetTest.length; j++) {

//					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
					Geocrowd.algorithm = algorithm;
					OnlineMTC onlineMTC = new OnlineMTC();
					OnlineMTC.TotalAssignedTasks = 0;
					OnlineMTC.TotalAssignedWorkers = 0;
					/**
					 * clear worker, task list
					 */
					OnlineMTC.taskList.clear();
					OnlineMTC.workerList.clear();
					onlineMTC.totalBudget = totalBudget;
					// System.out.println("ttasks\ttasks\tworkers\tT/W");
					for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

						switch (Geocrowd.DATA_SET) {
						case GOWALLA:
							onlineMTC
									.readTasks(Constants.gowallaTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.gowallaWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case YELP:
							onlineMTC
									.readTasks(Constants.yelpTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.yelpWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case UNIFORM:
							onlineMTC.readTasks(Constants.uniTaskFileNamePrefix
									+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.uniWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SKEWED:
							onlineMTC
									.readTasks(Constants.skewedTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.skewedWorkerFileNamePrefix
											+ i + ".txt");
							break;
						case SMALL_TEST:
							onlineMTC
									.readTasks(Constants.smallTaskFileNamePrefix
											+ i + ".txt");
							onlineMTC
									.readWorkers(Constants.smallWorkerFileNamePrefix
											+ i + ".txt");
							break;
						}

						onlineMTC.matchingTasksWorkers();
						// System.out.println("Number of workers: " +
						// onlineMTC.workerList.size());
						//
						// System.out.println("Number of tasks: " +
						// onlineMTC.taskList.size());
						//
						// System.out.println("Number of arrival tasks: "+onlineMTC.numberArrivalTask);

						HashSet<Integer> workerSet = onlineMTC.maxCoverage();
						// System.out.println(workerSet);

						onlineMTC.TimeInstance++;
					}
					System.out.println("\n##################");
					//
					// System.out.printf("\n%-10s %-10s %-10s %-10s %-10s",
					// "TTask",
					// "CTask", "TWorker", "SWorker", "W/T");
					//
					// System.out.printf("\n%-10d %-10d %-10d %-10d %-10d",
					// onlineMTC.TaskCount, onlineMTC.TotalAssignedTasks,
					// onlineMTC.totalBudget, onlineMTC.TotalAssignedWorkers,
					// onlineMTC.TotalAssignedTasks/onlineMTC.TotalAssignedWorkers);

					//
					coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
					assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
					// onlineMTC.printWorkerCounts();

					// }
				}

			/**
			 * print result
			 */
			System.out.println("\n##################");
			System.out.println("Budget = " + totalBudget);
			System.out.println("#Covered Tasks");
			System.out.printf("%-20s", " ");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				System.out.printf("%-20s", algorithms[j2]);
			for (int al2 = 0; al2 < listAlpha.length; al2++) {
				System.out.printf("\n%-20s", "Alpha=" + listAlpha[al2]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					System.out.printf("%-20d", coveredTasks[al2][g2]);
				}
			}
		}

	}

}