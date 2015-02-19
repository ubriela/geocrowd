package test.geocrowd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdConstants;
import org.geocrowd.OfflineMTC;
import org.geocrowd.common.utils.Utils;

public class OfflineMTCTest {
	static Logger logger = Logger.getLogger(OnlineMTCTest.class.getName());

	public static void main(String[] args) {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;

//		int[] budgets = new int[] { 5, 10, 15, 20, 25, 30}; // gowalla
//		int[] budgets = { 24, 48, 96, 192, 384, 768, 1536, 3072 }; //
		// foursquare
		// int[] budgets = { 20, 40, 80, 160, 320, 640, 1280, 2560 }; //

//		varying_budget(0, 8, budgets, 5);

		// double[] radii = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5 };
		// double[] radii = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45,
		// 0.5};
		 double[] radii = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		 varying_radius(0, 8, radii, 56);
		 
//		 int[] delta = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
//		 varying_delta(0, 8, delta, 56);
	}

	public static void varying_radius(int starttime, int times, double[] radii,
			int budget) {

		boolean[] isFixes = { true, false };
		int[][] coveredTasks = new int[radii.length][isFixes.length];
		int[][] assignedWorkers = new int[radii.length][isFixes.length];
		
		for (int t = 0; t < times; t++) {
			int next_time_period = starttime + t
					* GeocrowdConstants.TIME_INSTANCE;

			for (int r = 0; r < radii.length; r++)
				for (int fix = 0; fix < isFixes.length; fix++) {

					OfflineMTC offlineMTC = new OfflineMTC();
					offlineMTC.isFixed = isFixes[fix];
					offlineMTC.budget = budget;
					GeocrowdConstants.radius = radii[r];
					offlineMTC.reset();

					for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						offlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						offlineMTC.readWorkers(
								Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", i);
					}

					System.out.print("\nradius = " + radii[r] + ", isFixed = "
							+ isFixes[fix]);
					System.out.printf(
							"\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s",
							"TotalTask", "CoveredTask", "TotalWorker",
							"SelectedWorker", "W/T");

					offlineMTC.matchingTasksWorkers();

					offlineMTC.maxTaskCoverage();

					assignedWorkers[r][fix] = OfflineMTC.TotalAssignedWorkers;
					coveredTasks[r][fix] += OfflineMTC.TotalAssignedTasks;

					System.out.printf(
							"\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
							offlineMTC.TaskCount,
							OfflineMTC.TotalAssignedTasks,
							offlineMTC.budget,
							OfflineMTC.TotalAssignedWorkers,
							OfflineMTC.TotalAssignedTasks
									/ Math.max(1,
											OfflineMTC.TotalAssignedWorkers));
				}
		}
		
		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Offline varying radius ");
		pw.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			pw.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < radii.length; b++) {
			pw.printf("\n%-20f \t", radii[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				pw.printf("%-20d \t", (int)(coveredTasks[b][j2]/times));
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}
	
	public static void varying_delta(int starttime, int times, int[] delta,
			int budget) {

		boolean[] isFixes = { true, false };
		int[][] coveredTasks = new int[delta.length][isFixes.length];
		int[][] assignedWorkers = new int[delta.length][isFixes.length];
		
		for (int t = 0; t < times; t++) {

			int next_time_period = starttime + t
					* GeocrowdConstants.TIME_INSTANCE;

			for (int d = 0; d < delta.length; d++)
				for (int fix = 0; fix < isFixes.length; fix++) {

					OfflineMTC offlineMTC = new OfflineMTC();
					offlineMTC.isFixed = isFixes[fix];
					offlineMTC.budget = budget;
					GeocrowdConstants.TaskDuration = delta[d];
					offlineMTC.reset();

					for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						offlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						offlineMTC.readWorkers(
								Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", i);
					}

					System.out.print("\ndelta = " + delta[d] + ", isFixed = "
							+ isFixes[fix]);
					System.out.printf(
							"\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s",
							"TotalTask", "CoveredTask", "TotalWorker",
							"SelectedWorker", "W/T");

					offlineMTC.matchingTasksWorkers();

					offlineMTC.maxTaskCoverage();

					assignedWorkers[d][fix] = OfflineMTC.TotalAssignedWorkers;
					coveredTasks[d][fix] += OfflineMTC.TotalAssignedTasks;

					System.out.printf(
							"\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
							offlineMTC.TaskCount,
							OfflineMTC.TotalAssignedTasks,
							offlineMTC.budget,
							OfflineMTC.TotalAssignedWorkers,
							OfflineMTC.TotalAssignedTasks
									/ Math.max(1,
											OfflineMTC.TotalAssignedWorkers));
				}
		}
		
		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Offline varying delta ");
		pw.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			pw.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < delta.length; b++) {
			pw.printf("\n%-20d \t", delta[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				pw.printf("%-20d \t", (int)(coveredTasks[b][j2]/times));
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	public static void varying_budget(int starttime, int times, int[] budgets,
			double radius) {

		boolean[] isFixes = { true, false };
		int[][] coveredTasks = new int[budgets.length][isFixes.length];
		int[][] assignedWorkers = new int[budgets.length][isFixes.length];
		
		for (int t = 0; t < times; t++) {
			GeocrowdConstants.radius = radius;

			int next_time_period = starttime + t
					* GeocrowdConstants.TIME_INSTANCE;

			for (int b = 0; b < budgets.length; b++)
				for (int fix = 0; fix < isFixes.length; fix++) {
					OfflineMTC offlineMTC = new OfflineMTC();
					offlineMTC.isFixed = isFixes[fix];
					offlineMTC.budget = budgets[b];
					offlineMTC.reset();

					for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						offlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						offlineMTC.readWorkers(
								Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", i);
					}

					System.out.print("\nbudget = " + budgets[b]
							+ ", isFixed = " + isFixes[fix]);
					System.out.printf(
							"\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s",
							"TotalTask", "CoveredTask", "TotalWorker",
							"SelectedWorker", "W/T");

					offlineMTC.matchingTasksWorkers();
					offlineMTC.maxTaskCoverage();

					assignedWorkers[b][fix] = OfflineMTC.TotalAssignedWorkers;
					coveredTasks[b][fix] += OfflineMTC.TotalAssignedTasks;

					System.out.printf(
							"\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
							offlineMTC.TaskCount,
							OfflineMTC.TotalAssignedTasks,
							offlineMTC.budget,
							OfflineMTC.TotalAssignedWorkers,
							OfflineMTC.TotalAssignedTasks
									/ Math.max(1,
											OfflineMTC.TotalAssignedWorkers));
				}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Offline varying budget");
		pw.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			pw.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < budgets.length; b++) {
			pw.printf("\n%-20d \t", budgets[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				pw.printf("%-20d \t", (int)(coveredTasks[b][j2]/times));
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}
}
