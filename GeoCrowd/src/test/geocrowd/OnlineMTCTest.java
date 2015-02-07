package test.geocrowd;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.geocrowd.AlgorithmEnum;
import org.geocrowd.Constants;
import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdConstants;
import org.geocrowd.OfflineMTC;
import org.geocrowd.OnlineMTC;
import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.utils.Utils;
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
	static Logger logger = Logger.getLogger(OnlineMTCTest.class.getName());

	public static void main(String[] args) throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		GeocrowdConstants.TIME_INSTANCE = 28;
		// overloading();

		int[] budgets = new int[] { 28, 56, 112, 224, 448, 896, 1792, 3586 };
		// int[] budgets = { 24, 48, 96, 192, 384, 768, 1536, 3072 };
		// int[] budgets = { 24, 48, 96};
		double radius = 5.0;
		int start_time = 0;
		workload_vary_budget(radius, budgets, start_time);

		// double[] radii = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5 };
		double[] radii = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int totalBudget = 28;
		start_time = 0;
		workload_vary_radius(radii, totalBudget, start_time);

		// int[] delta = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int[] delta = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		radius = 5.0;
		start_time = 0;
		workload_vary_delta(delta, totalBudget, radius, start_time);

		int cycle_length = 7;
		int workload_size = GeocrowdConstants.TIME_INSTANCE / cycle_length;
		int workloadCount = 8;
		totalBudget = 28;
		radius = 5.0;
		start_time = 0;
		workload_vary_time(cycle_length, workload_size, totalBudget, radius,
				start_time, workloadCount);

		// workload();
		// vary_a();
		// vary_eps();
		// vary_r();
		// vary_b();
		// vary_en();
	}

	@Test
	public void testOneWorkload() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
		AlgorithmEnum.MAX_COVER_BASIC,
		AlgorithmEnum.MAX_COVER_ADAPT_B,
		AlgorithmEnum.MAX_COVER_ADAPT_B_W,
		AlgorithmEnum.MAX_COVER_ADAPT_T,
		AlgorithmEnum.MAX_COVER_ADAPT_T_W,
		};

		// Double[] epss = new Double[] { 0.05, 0.1, 0.15, 0.2, 0.25 };
		double[] epss = new double[] { 0.5 };

		int[][] coveredTasks = new int[epss.length][algorithms.length];
		int[][] assignedWorkers = new int[epss.length][algorithms.length];

		// GeocrowdTest.main(null);

		GeocrowdConstants.TIME_INSTANCE = 14;
		int totalBudget = 28;
		GeocrowdConstants.radius = 1.0;
		int start_time = 0;
		int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

		System.out.println("\nRadius = " + GeocrowdConstants.radius);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null); // generate set of tasks for next period

		// apply offline method to next period
		int next_time_period = start_time + GeocrowdConstants.TIME_INSTANCE;
		computeHistoryBudgets(false, totalBudget, next_time_period);

		// use the same set of workers/tasks for all following period
		for (int eps = 0; eps < epss.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.eps = epss[eps];
				onlineMTC.preBudgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					int next_time = next_time_period + i;
					onlineMTC.readWorkloadTasks(
							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
									+ next_time + ".txt", next_time_period);
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ next_time
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
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

	/**
	 * Varying all tasks
	 * 
	 * @throws IOException
	 */
	private static void workload_vary_time(int cycle_length, int workload_size,
			int totalBudget, double radius, int starttime, int w_count)
			throws IOException {

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD };

		int[][] coveredTasks = new int[w_count][algorithms.length + 2];
		int[][] assignedWorkers = new int[w_count][algorithms.length + 2];

		GeocrowdConstants.TIME_INSTANCE = cycle_length * workload_size;
		int start_time = starttime;

		GeocrowdConstants.radius = radius;
		System.out.println("\nRadius = " + GeocrowdConstants.radius);
		System.out.println("Budget = " + totalBudget);

		/**
		 * Iterate all possible workloads
		 */
		for (int w = 0; w < w_count; w++) {
			Geocrowd.TimeInstance = 0;
			// GeocrowdTest.main(null);

			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			// GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + GeocrowdConstants.TIME_INSTANCE;

			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.preBudgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					int next_time = next_time_period + i;
					onlineMTC.readWorkloadTasks(
							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
									+ next_time + ".txt", next_time_period);
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ next_time
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
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
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n##################Varying time");
		pw.println("Budget = " + totalBudget);
		pw.println("radius = " + GeocrowdConstants.radius);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int w = 0; w < w_count; w++) {
			pw.printf("\n%-20d \t", w);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				pw.printf("%-20d \t", coveredTasks[w][g2]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	private static void workload_vary_delta(int[] delta, int totalBudget,
			double radius, int starttime) throws IOException {

		GeocrowdConstants.radius = radius;
		System.out.println("\nRadius = " + GeocrowdConstants.radius);
		System.out.println("Budget = " + totalBudget);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD };

		int[][] coveredTasks = new int[delta.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[delta.length][algorithms.length + 2];

		for (int d = 0; d < delta.length; d++) {
			GeocrowdConstants.TaskDuration = delta[d];
			System.out.println("\n----\ndelta: "
					+ GeocrowdConstants.TaskDuration);
			Geocrowd.TimeInstance = 0;
			// GeocrowdTest.main(null);

			int start_time = starttime;
			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			// GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + GeocrowdConstants.TIME_INSTANCE;
			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.preBudgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					int next_time = next_time_period + i;
					onlineMTC.readWorkloadTasks(
							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
									+ next_time + ".txt", next_time_period);
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ next_time
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
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
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n##################Varying delta");
		pw.println("Budget = " + totalBudget);
		pw.println("\nRadius = " + GeocrowdConstants.radius);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int d = 0; d < delta.length; d++) {
			pw.printf("\n%-20d \t", delta[d]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				pw.printf("%-20d \t", coveredTasks[d][g2]);
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	private static void workload_vary_radius(double[] radii, int totalBudget,
			int starttime) throws IOException {

		System.out.println("Budget = " + totalBudget);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD };

		int[][] coveredTasks = new int[radii.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[radii.length][algorithms.length + 2];

		for (int d = 0; d < radii.length; d++) {
			GeocrowdConstants.radius = radii[d];
			System.out.println("\n----\nRadius " + GeocrowdConstants.radius);
			Geocrowd.TimeInstance = 0;
			// GeocrowdTest.main(null);

			int start_time = starttime;
			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			// GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + GeocrowdConstants.TIME_INSTANCE;
			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.preBudgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					int next_time = next_time_period + i;
					onlineMTC.readWorkloadTasks(
							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
									+ next_time + ".txt", next_time_period);
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ next_time
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
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
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n##################Varying radius");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int d = 0; d < radii.length; d++) {
			pw.printf("\n%-20f \t", radii[d]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				pw.printf("%-20d \t", coveredTasks[d][g2]);
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	private static void workload_vary_budget(double radius, int[] budgets,
			int starttime) throws IOException {

		GeocrowdConstants.radius = radius;
		System.out.println("radius = " + GeocrowdConstants.radius);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_ADAPT_T,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD,
				AlgorithmEnum.MAX_COVER_ADAPT_B_W,
				AlgorithmEnum.MAX_COVER_BASIC_WORKLOAD_T
				};

		int[][] coveredTasks = new int[budgets.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[budgets.length][algorithms.length + 2];

		for (int b = 0; b < budgets.length; b++) {
			int totalBudget = budgets[b];
			System.out.println("\n----\nBudget = " + totalBudget);
			Geocrowd.TimeInstance = 0;

			int start_time = starttime;
			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			// apply offline method to next period
			int next_time_period = start_time + GeocrowdConstants.TIME_INSTANCE;
			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();

				onlineMTC.preBudgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					int next_time = next_time_period + i;
					onlineMTC.readWorkloadTasks(
							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
									+ next_time + ".txt", next_time_period);
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ next_time
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
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
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n################## Varying budget");
		pw.println("radius = " + GeocrowdConstants.radius);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int b = 0; b < budgets.length; b++) {
			pw.printf("\n%-20d \t", budgets[b]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				pw.printf("%-20d \t", coveredTasks[b][g2]);
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());

	}

	private static int[] computeHistoryBudgets(boolean isFixed, int budget,
			int start_time) {
		OfflineMTC offlineMTC = new OfflineMTC();
		offlineMTC.reset();
		OfflineMTC.taskList = new ArrayList<GenericTask>();

		offlineMTC.budget = budget;
		offlineMTC.isFixed = isFixed;

		for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
			offlineMTC.readWorkloadTasks(
					Utils.datasetToTaskPath(Geocrowd.DATA_SET)
							+ (i + start_time) + ".txt", start_time);
			offlineMTC.readWorkers(Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
					+ (i + start_time) + ".txt", i);
		}

		offlineMTC.matchingTasksWorkers();

		offlineMTC.maxTaskCoverage();
		System.out.println(isFixed + " " + start_time);
		System.out.println("\nCoverage : " + OfflineMTC.TotalAssignedTasks);
		System.out.println("Budget counts :");
		for (int count : offlineMTC.counts)
			System.out.print(count + "\t");
		System.out.println();
		return offlineMTC.counts;
	}

	@Test
	public void testLocalVaryAlpha() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
//				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_BASIC_T,
				// AlgorithmEnum.MAX_COVER_BASIC_T2,
				AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		double[] alphas = new double[] { 0.1 };
		int[][] coveredTasks = new int[alphas.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[alphas.length][algorithms.length + 2];

		GeocrowdConstants.TIME_INSTANCE = 28;
		int totalBudget = 56;
		GeocrowdConstants.radius = 5.0;
		System.out.println("Radius = " + GeocrowdConstants.radius);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

//		computeHistoryBudgets(true, totalBudget, 0);
		int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
//		computeHistoryBudgets(false, totalBudget, 0);
		int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

		for (int al = 0; al < alphas.length; al++) {
			for (int g = 0; g < algorithms.length; g++) {
				// update alpha for temporal, spatial algorithms.
				Constants.alpha = alphas[al];
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.createGrid();
				onlineMTC.readBoundary();
				 onlineMTC.readEntropy();

				onlineMTC.totalBudget = totalBudget;
				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
				}
				coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
			}
			coveredTasks[al][algorithms.length] = fixed_offline_cov;
			coveredTasks[al][algorithms.length + 1] = dynamic_offline_cov;
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying alpha");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int al2 = 0; al2 < alphas.length; al2++) {
			pw.printf("\n%-20f \t", alphas[al2]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				pw.printf("%-20d \t", coveredTasks[al2][g2]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	
	@Test
	public void testLocalVaryDelta() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SKEWED;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
//				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_T2,
		 AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		int[] delta = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int[][] coveredTasks = new int[delta.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[delta.length][algorithms.length + 2];

		GeocrowdConstants.TIME_INSTANCE = 28;
		int totalBudget = 56;
		double alpha = 0.2;
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		for (int d = 0; d < delta.length; d++) {
			GeocrowdConstants.TaskDuration = delta[d];
//			computeHistoryBudgets(true, totalBudget, 0);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
//			computeHistoryBudgets(false, totalBudget, 0);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			for (int g = 0; g < algorithms.length; g++) {
				// update alpha for temporal, spatial algorithms.
				Constants.alpha = alpha;
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.totalBudget = totalBudget;
				onlineMTC.createGrid();
				onlineMTC.readBoundary();

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;
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
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying delta");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int a = 0; a < algorithms.length; a++)
			pw.printf("%-20s \t", algorithms[a]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int r = 0; r < delta.length; r++) {
			pw.printf("\n%-20d \t",delta[r]);
			for (int al = 0; al < algorithms.length + 2; al++)
				pw.printf("%-20d \t", coveredTasks[r][al]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	
	@Test
	public void testLocalVaryRadius() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SKEWED;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
//				AlgorithmEnum.MAX_COVER_BASIC, 
				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_T2,
		 AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		double[] radii = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		int[][] coveredTasks = new int[radii.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[radii.length][algorithms.length + 2];

		GeocrowdConstants.TIME_INSTANCE = 28;
		int totalBudget = 56;
		double alpha = 0.2;
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		for (int r = 0; r < radii.length; r++) {
			GeocrowdConstants.radius = radii[r];
//			computeHistoryBudgets(true, totalBudget, 0);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
//			computeHistoryBudgets(false, totalBudget, 0);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			for (int g = 0; g < algorithms.length; g++) {
				// update alpha for temporal, spatial algorithms.
				Constants.alpha = alpha;
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.totalBudget = totalBudget;
				onlineMTC.createGrid();
				onlineMTC.readBoundary();

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;
				}

				coveredTasks[r][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[r][g] = OnlineMTC.TotalAssignedWorkers;
			}
			coveredTasks[r][algorithms.length] = fixed_offline_cov;
			coveredTasks[r][algorithms.length + 1] = dynamic_offline_cov;
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying radius");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int a = 0; a < algorithms.length; a++)
			pw.printf("%-20s \t", algorithms[a]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int r = 0; r < radii.length; r++) {
			pw.printf("\n%-20f \t", radii[r]);
			for (int al = 0; al < algorithms.length + 2; al++)
				pw.printf("%-20d \t", coveredTasks[r][al]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public void testLocalVaryBudget() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SKEWED;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
//				AlgorithmEnum.MAX_COVER_BASIC, 
				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_T2,
		 AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		// int[] budgets = new int[] { 24, 48, 96, 192, 384, 768, 1536, 3072 };
		 int[] budgets = new int[] { 28, 56, 112, 224, 448, 896, 1792, 3586 };
//		int[] budgets = new int[] { 28, 56 };
		int[][] coveredTasks = new int[budgets.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[budgets.length][algorithms.length + 2];

		// int[] budgets = new int[] { 40, 80, 160, 320, 640, 1280,
		// 2560 };
		GeocrowdConstants.TIME_INSTANCE = 28;
		GeocrowdConstants.radius = 5.0;
		Constants.alpha = 0.2;
		System.out.println("Radius = " + GeocrowdConstants.radius);

		// GeocrowdTest.main(null);

		for (int b = 0; b < budgets.length; b++) {
//			computeHistoryBudgets(true, budgets[b], 0);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
//			computeHistoryBudgets(false, budgets[b], 0);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			for (int a = 0; a < algorithms.length; a++) {
				// update alpha for temporal, spatial algorithms.
				Geocrowd.algorithm = algorithms[a];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.totalBudget = budgets[b];
				onlineMTC.createGrid();
				onlineMTC.readBoundary();

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[b][a] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[b][a] = OnlineMTC.TotalAssignedWorkers;
			}

			coveredTasks[b][algorithms.length] = fixed_offline_cov;
			coveredTasks[b][algorithms.length + 1] = dynamic_offline_cov;
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying budget");
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int a = 0; a < algorithms.length; a++)
			pw.printf("%-20s \t", algorithms[a]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int b = 0; b < budgets.length; b++) {
			pw.printf("\n%-20d \t", budgets[b]);
			for (int a = 0; a < algorithms.length + 2; a++)
				pw.printf("%-20d \t", coveredTasks[b][a]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public void varyAdaptThreshold() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SKEWED;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
		// AlgorithmEnum.MAX_COVER_BASIC
		// ,
		AlgorithmEnum.MAX_COVER_ADAPT_B };

		Double[] epss = new Double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8,
				0.9 };
		// double[] epss = new double[] { 0.3, 0.5, 0.7 };
		int[][] coveredTasks = new int[epss.length][algorithms.length];
		int[][] assignedWorkers = new int[epss.length][algorithms.length];

		int totalBudget = 56;
		GeocrowdConstants.radius = 5.0;
		System.out.println("Radius = " + GeocrowdConstants.radius);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		for (int eps = 0; eps < epss.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				// update alpha for temporal, spatial algorithms.
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.eps = epss[eps];
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[eps][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[eps][g] = OnlineMTC.TotalAssignedWorkers;
			}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		for (int eps = 0; eps < epss.length; eps++) {
			pw.printf("\n%-20f \t", epss[eps]);
			for (int g2 = 0; g2 < algorithms.length; g2++) {
				pw.printf("%-20d \t", coveredTasks[eps][g2]);
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public void testOverloading() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SKEWED;
		GeocrowdConstants.radius = 5.0;
		GeocrowdConstants.TIME_INSTANCE = 28;
		int budget = 896;
		// double[] alpha = new double[] { 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 };
		double[] alpha = new double[] { 0.1 };
		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_BASIC_MO };

		int[][] coveredTasks = new int[alpha.length][algorithms.length];
		int[][] maxAssignments = new int[alpha.length][algorithms.length];
		int[][] assignedWorkers = new int[alpha.length][algorithms.length];

		System.out.printf(
				"\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
				"Time", "TTask", "CTask", "TWorker", "SWorker", "W/T");
		for (int d = 0; d < alpha.length; d++) {
			Constants.alpha = alpha[d];
			for (int g = 0; g < algorithms.length; g++) {
				OnlineMTC onlineMTC = new OnlineMTC();
				Geocrowd.algorithm = algorithms[g];
				onlineMTC.reset();

				if (Geocrowd.algorithm == AlgorithmEnum.MAX_COVER_BASIC
						&& d > 0)
					continue;

				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();
				onlineMTC.totalBudget = budget;
				for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;
				}
				System.out.println("##################");

				System.out.printf("\n%-15s %-15s %-15s %-15s %-15s",
						"TotalTask", "CoveredTask", "TotalWorker",
						"SelectedWorker", "W/T");

				System.out.printf(
						"\n%-15d %-15d %-15d %-15d %-15d",
						onlineMTC.TaskCount,
						OnlineMTC.TotalAssignedTasks,
						onlineMTC.totalBudget,
						OnlineMTC.TotalAssignedWorkers,
						OnlineMTC.TotalAssignedTasks
								/ Math.max(1, OnlineMTC.TotalAssignedWorkers));

				maxAssignments[d][g] = onlineMTC.printWorkerCounts();

				coveredTasks[d][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[d][g] = OnlineMTC.TotalAssignedWorkers;
			}

			/**
			 * print result
			 */
			StringWriter stringWriter = new StringWriter();
			PrintWriter pw = new PrintWriter(stringWriter);
			pw.println("\n\n##################Overloading");
			pw.println("Budget = " + budget);
			pw.println("#Covered Tasks");
			pw.printf("%-20s \t", " ");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				pw.printf("%-20s \t", algorithms[j2]);
			for (int a = 0; a < alpha.length; a++) {
				pw.printf("\n%-20f \t", alpha[a]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					pw.printf("%-20d \t", coveredTasks[d][g2]);
				}
			}

			pw.printf("\n");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				pw.printf("%-20s \t", algorithms[j2]);
			for (int a = 0; a < alpha.length; a++) {
				pw.printf("\n%-20f \t", alpha[a]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					pw.printf("%-20d \t", maxAssignments[d][g2]);
				}
			}

			logger.info(stringWriter.toString());
			System.out.println(stringWriter.toString());

		}
	}
}