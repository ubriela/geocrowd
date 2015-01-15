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
		task_dist();
//		vary_a();
//		vary_eps();
//		vary_d();
//		vary_b();
//		vary_en();
	}
	
	public static int[] computeHistoryBudgets() {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		
		OfflineMTC offlineMTC = new OfflineMTC();
		offlineMTC.isFixed = false;
		offlineMTC.budget = 42;
		offlineMTC.TaskCount = 0;
		OfflineMTC.TotalAssignedTasks = 0;
		OfflineMTC.TotalAssignedWorkers = 0;
		OfflineMTC.workerList = null;
		OfflineMTC.workerList = new ArrayList<>();;
		OfflineMTC.taskList = new ArrayList<>();
	
		for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			switch (Geocrowd.DATA_SET) {
			case GOWALLA:
				offlineMTC
						.readTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
				offlineMTC.readWorkers(
						Constants.gowallaWorkerFileNamePrefix + i
								+ ".txt", i);
				break;
			case YELP:
				offlineMTC.readTasks(Constants.yelpTaskFileNamePrefix
						+ i + ".txt");
				offlineMTC
						.readWorkers(Constants.yelpWorkerFileNamePrefix
								+ i + ".txt", i);
				break;
			case UNIFORM:
				offlineMTC.readTasks(Constants.uniTaskFileNamePrefix
						+ i + ".txt");
				offlineMTC.readWorkers(
						Constants.uniWorkerFileNamePrefix + i + ".txt",
						i);
				break;
			case SKEWED:
				offlineMTC.readTasks(Constants.skewedTaskFileNamePrefix
						+ i + ".txt");
				offlineMTC.readWorkers(
						Constants.skewedWorkerFileNamePrefix + i
								+ ".txt", i);
				break;
			case SMALL_TEST:
				offlineMTC.readTasks(Constants.smallTaskFileNamePrefix
						+ i + ".txt");
				offlineMTC.readWorkers(
						Constants.smallWorkerFileNamePrefix + i
								+ ".txt", i);
				break;
			}
		}
		
		offlineMTC.matchingTasksWorkers();

		HashSet<Integer> workerSet = offlineMTC.maxTaskCoverage();
		
		for (int count : offlineMTC.counts)
			System.out.println(count);
		return offlineMTC.counts;
	}
	
	private static void task_dist() throws IOException {

		int totalBudget = 42;

		System.out.println("Diameter = " + Constants.radius);
		System.out.println("F = " + Constants.F);
		System.out.println("Budget = " + totalBudget);

//		GeocrowdTest.main(null);
		
		
//		int[] counts = computeHistoryBudgets();
//		int[] counts = {
//				0,
//				0,
//				0,
//				0,
//				6,
//				3,
//				3,
//				1,
//				0,
//				1,
//				4,
//				2,
//				4,
//				3,
//				0,
//				2,
//				2,
//				1,
//				1,
//				1,
//				8};
		
		int[] counts = {
				0,
				0,
				0,
				1,
				5,
				4,
				3,
				0,
				2,
				2,
				2,
				2,
				3,
				2,
				3,
				2,
				2,
				0,
				0,
				3,
				6
		};


//		Double[] epss = new Double[] {0.05, 0.1, 0.15, 0.2, 0.25};
		Double[] epss = new Double[] {0.1};
		
		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD2
				};

		Integer[][] coveredTasks = new Integer[epss.length][algorithms.length];
		Integer[][] assignedWorkers = new Integer[epss.length][algorithms.length];

		for (int eps = 0; eps < epss.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];

				// update alpha for temporal, spatial algorithms.

				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				OnlineMTC.TimeInstance = 0;
				onlineMTC.eps = epss[eps];
				OnlineMTC.TotalAssignedTasks = 0;
				OnlineMTC.TotalAssignedWorkers = 0;
				onlineMTC.TaskCount = 0;
				OnlineMTC.TotalAssignedTasks = 0;
				OnlineMTC.TotalAssignedWorkers = 0;
				OnlineMTC.workerList = null;
				OnlineMTC.workerList = new ArrayList<>();
				OnlineMTC.taskList = new ArrayList<>();
				
				onlineMTC.budgets = counts;
				// update dataset
//				Constants.MIN_TIME += Constants.TIME_INSTANCE;
				
//				GeocrowdTest.main(null);

				
				onlineMTC.totalBudget = totalBudget;
				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

					switch (Geocrowd.DATA_SET) {
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
											/ Math.max(1, onlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[eps][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[eps][g] = OnlineMTC.TotalAssignedWorkers;
			}
		}
		
		/**
		 * print result
		 */
		System.out.println("##################");
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

//		Double[] epss = new Double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8,
//				0.9 };

		Double[] epss = new Double[] {0.3, 0.5, 0.7};
		
		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
//				AlgorithmEnum.MAX_COVER_BASIC
//				,
				AlgorithmEnum.MAX_COVER_ADAPT_B
				};

		Integer[][] coveredTasks = new Integer[epss.length][algorithms.length];
		Integer[][] assignedWorkers = new Integer[epss.length][algorithms.length];

		int totalBudget = 400;
		Constants.F = 1.0;

		System.out.println("Diameter = " + Constants.radius);
		System.out.println("F = " + Constants.F);
		System.out.println("Budget = " + totalBudget);

//		 GeocrowdTest.main(null);

		for (int eps = 0; eps < epss.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				AlgorithmEnum algorithm = algorithms[g];

				// update alpha for temporal, spatial algorithms.

				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.eps = epss[eps];
				OnlineMTC.TotalAssignedTasks = 0;
				OnlineMTC.TotalAssignedWorkers = 0;
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
		System.out.println("##################");
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

		Double[] listAlpha = new Double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		Integer[][] coveredTasks = new Integer[listAlpha.length][algorithms.length];
		Integer[][] assignedWorkers = new Integer[listAlpha.length][algorithms.length];

		int totalBudget = 160;
		Constants.F = 1.0;

		System.out.println("Diameter = " + Constants.radius);
		System.out.println("F = " + Constants.F);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		for (int al = 0; al < listAlpha.length; al++)
			for (int g = 0; g < algorithms.length; g++) {
				Double alpha = listAlpha[al];
				AlgorithmEnum algorithm = algorithms[g];

				// update alpha for temporal, spatial algorithms.
				Constants.alpha = alpha;

				Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
				Geocrowd.algorithm = algorithm;
				OnlineMTC onlineMTC = new OnlineMTC();

				onlineMTC.TaskCount = 0;
				OnlineMTC.TotalAssignedTasks = 0;
				OnlineMTC.TotalAssignedWorkers = 0;
				OnlineMTC.workerList = null;
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
		System.out.println("##################");
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

	
	private static void vary_d() throws IOException {

		Double[] listAlpha = new Double[] { 0.1, 0.3, 0.5, 0.7 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		Integer[][] coveredTasks = new Integer[listAlpha.length][algorithms.length];
		Integer[][] assignedWorkers = new Integer[listAlpha.length][algorithms.length];

		int totalBudget = 160;
		Double[] listDiameter = new Double[] { 0.1, 0.5, 1.0, 2.0};
		Constants.F = 1.0;

		System.out.println("F = " + Constants.F);
		System.out.println("Budget = " + totalBudget);

		for (double d : listDiameter) {
			Constants.radius = d;
			System.out.println("\nDiameter = " + Constants.radius);
			GeocrowdTest.main(null);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					Double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];

					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;

					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
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
						HashSet<Integer> workerSet = onlineMTC.maxCoverage();

						onlineMTC.TimeInstance++;
					}

					coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
					assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
				}

			/**
			 * print result
			 */
			System.out.println("##################");
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

		Double[] listAlpha = new Double[] { 0.1, 0.3, 0.5, 0.7 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		Integer[][] coveredTasks = new Integer[listAlpha.length][algorithms.length];
		Integer[][] assignedWorkers = new Integer[listAlpha.length][algorithms.length];

		int totalBudget = 160;
		Double[] listEn = new Double[] { 0.1, 0.4, 0.7, 1.0 };
		Constants.radius = 2.0;

		System.out.println("Diameter = " + Constants.radius);
		System.out.println("Budget = " + totalBudget);

		for (double en : listEn) {
			Constants.F = en;
			System.out.println("En = " + Constants.F);
			GeocrowdTest.main(null);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					Double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];

					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;

					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
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
						HashSet<Integer> workerSet = onlineMTC.maxCoverage();

						onlineMTC.TimeInstance++;
					}

					coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
					assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
				}

			/**
			 * print result
			 */
			System.out.println("##################");
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

		Double[] listAlpha = new Double[] { 0.1, 0.2, 0.3, 0.4, 0.5 };

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		Integer[][] coveredTasks = new Integer[listAlpha.length][algorithms.length];
		Integer[][] assignedWorkers = new Integer[listAlpha.length][algorithms.length];

		int totalBudget = 0;
		// Constants.diameter = 2.0;
		Constants.F = 1.0;

		Integer[] listBudget = new Integer[] { 40, 80, 160, 320, 640, 1280,
				2560 };

		System.out.println("F = " + Constants.F);
		System.out.println("Diameter = " + Constants.radius);

		GeocrowdTest.main(null);

		for (int b : listBudget) {
			totalBudget = b;
			System.out.println("\nBudget = " + totalBudget);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					Double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];

					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;

					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
					Geocrowd.algorithm = algorithm;
					OnlineMTC onlineMTC = new OnlineMTC();

					onlineMTC.TaskCount = 0;
					OnlineMTC.TotalAssignedTasks = 0;
					OnlineMTC.TotalAssignedWorkers = 0;
					OnlineMTC.workerList = null;
					OnlineMTC.workerList = new ArrayList<>();
					;
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
			System.out.println("##################");
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

	private static void test_all() throws IOException {

		Double[] listAlpha = new Double[] { 0.05 };

		Integer[] listBudgetTest = new Integer[] { 2000 };

		Double[] listDiameter = new Double[] {};

		Double[] listF = new Double[] {};

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_T,
				AlgorithmEnum.MAX_COVER_BASIC_S };

		// Iterate over alpha and algorithm, diameter, f, budget
		int numLoops = listDiameter.length + listF.length
				+ listBudgetTest.length;
		for (int iter = 0; iter < numLoops; iter++) {

			Integer[][] coveredTasks = new Integer[listAlpha.length][algorithms.length];
			Integer[][] assignedWorkers = new Integer[listAlpha.length][algorithms.length];

			int totalBudget = 1000;
			Double diameter = 0.2;
			Double F = 1.0;

			// vary diameter
			if (iter < listDiameter.length) {
				diameter = listDiameter[iter];
			} else if (iter >= listDiameter.length
					&& iter < listDiameter.length + listF.length)
				F = listF[iter - listDiameter.length];
			else if (iter >= listDiameter.length + listF.length)
				totalBudget = listBudgetTest[iter - listDiameter.length
						- listF.length];

			Constants.F = F;
			Constants.radius = diameter;

			// Regenerate data
			if (iter < listDiameter.length + listF.length)
				GeocrowdTest.main(null);

			System.out.println("Diameter = " + Constants.radius);
			System.out.println("F = " + Constants.F);
			System.out.println("Budget = " + totalBudget);
			for (int al = 0; al < listAlpha.length; al++)
				for (int g = 0; g < algorithms.length; g++) {
					Double alpha = listAlpha[al];
					AlgorithmEnum algorithm = algorithms[g];
					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;
					// System.out.println("\nAlpha = "+Constants.alpha);
					// System.out.println("\nAlgorithm = "+algorithm);

					// for (Integer j = 0; j < listBudgetTest.length; j++) {

					Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
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
					System.out.println("##################");
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
			System.out.println("##################");
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