package test.geocrowd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.OfflineMTC;
import org.geocrowd.common.Constants;

public class OfflineMTCTest {

	public static void main(String[] args) {
		Geocrowd.DATA_SET = DatasetEnum.SKEWED;
		
		int[] budgets = { 24, 48, 96, 192, 384, 768, 1536, 3072 };	// foursquare
//		int[] budgets = { 20, 40, 80, 160, 320, 640, 1280, 2560 };	// 
		
//		varying_budget(budgets);
		
		
//		double[] radii = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5 };
//		double[] radii = {0.01, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5};
		double[] radii = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		varying_radius(radii, 48);
	}
	
	public static void varying_radius(double[] radii, int budget) {
//		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		
		boolean[] isFixes = { true , false};
		
		Integer[][] coveredTasks = new Integer[radii.length][isFixes.length];
		Integer[][] assignedWorkers = new Integer[radii.length][isFixes.length];

		for (int r = 0; r < radii.length; r++)
			for (int fix = 0 ; fix < isFixes.length; fix++) {
				
				OfflineMTC offlineMTC = new OfflineMTC();
				offlineMTC.isFixed = isFixes[fix];
				offlineMTC.TaskCount = 0;
				offlineMTC.budget = budget;
				OfflineMTC.TotalAssignedTasks = 0;
				OfflineMTC.TotalAssignedWorkers = 0;
				OfflineMTC.workerList = null;
				OfflineMTC.workerList = new ArrayList<>();
				OfflineMTC.taskList = new ArrayList<>();
				
				Constants.radius = radii[r];
			
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						offlineMTC
						.readTasks(Constants.foursquareTaskFileNamePrefix
								+ i + ".txt");
						offlineMTC.readWorkers(
						Constants.foursquareWorkerFileNamePrefix + i
								+ ".txt", i);
						break;
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

				System.out.print("\nbudget = " + radii[r] + ", isFixed = " + isFixes[fix]);
				System.out.printf("\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s", "TotalTask",
						"CoveredTask", "TotalWorker", "SelectedWorker", "W/T");
				
				offlineMTC.matchingTasksWorkers();

				HashSet<Integer> workerSet = offlineMTC.maxTaskCoverage();
				
				assignedWorkers[r][fix] = offlineMTC.TotalAssignedWorkers;
				coveredTasks[r][fix] = offlineMTC.TotalAssignedTasks;

				System.out
				.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
						offlineMTC.TaskCount, OfflineMTC.TotalAssignedTasks,
						offlineMTC.budget, OfflineMTC.TotalAssignedWorkers,
						OfflineMTC.TotalAssignedTasks
								/ Math.max(1, OfflineMTC.TotalAssignedWorkers));
			}

		
		System.out.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			System.out.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < radii.length; b++) {
			System.out.printf("\n%-20f \t", radii[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				System.out.printf("%-20d \t", coveredTasks[b][j2]);
		}
	}

	public static void varying_budget(int[] budgets) {

//		Geocrowd.DATA_SET = DatasetEnum.FOURSQUARE;
		boolean[] isFixes = { true , false };
		
		Integer[][] coveredTasks = new Integer[budgets.length][isFixes.length];
		Integer[][] assignedWorkers = new Integer[budgets.length][isFixes.length];
		
		for (int b = 0; b < budgets.length; b++)
			for (int fix = 0 ; fix < isFixes.length; fix++) {
				
				OfflineMTC offlineMTC = new OfflineMTC();
				offlineMTC.isFixed = isFixes[fix];
				offlineMTC.budget = budgets[b];
				offlineMTC.TaskCount = 0;
				OfflineMTC.TotalAssignedTasks = 0;
				OfflineMTC.TotalAssignedWorkers = 0;
				OfflineMTC.workerList = null;
				OfflineMTC.workerList = new ArrayList<>();;
				OfflineMTC.taskList = new ArrayList<>();
			
				for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
					switch (Geocrowd.DATA_SET) {
					case FOURSQUARE:
						offlineMTC
						.readTasks(Constants.foursquareTaskFileNamePrefix
								+ i + ".txt");
						offlineMTC.readWorkers(
						Constants.foursquareWorkerFileNamePrefix + i
								+ ".txt", i);
						break;
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

//					System.out.println("Number of workers: "
//							+ OfflineMTC.workerList.size());
//
//					System.out.println("Number of tasks: "
//							+ OfflineMTC.taskList.size());
				}

				System.out.print("\nbudget = " + budgets[b] + ", isFixed = " + isFixes[fix]);
				System.out.printf("\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s", "TotalTask",
						"CoveredTask", "TotalWorker", "SelectedWorker", "W/T");
				
				offlineMTC.matchingTasksWorkers();

				HashSet<Integer> workerSet = offlineMTC.maxTaskCoverage();
				
				assignedWorkers[b][fix] = offlineMTC.TotalAssignedWorkers;
				coveredTasks[b][fix] = offlineMTC.TotalAssignedTasks;

				System.out
				.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
						offlineMTC.TaskCount, OfflineMTC.TotalAssignedTasks,
						offlineMTC.budget, OfflineMTC.TotalAssignedWorkers,
						OfflineMTC.TotalAssignedTasks
								/ Math.max(1, OfflineMTC.TotalAssignedWorkers));
			}

		
		System.out.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			System.out.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < budgets.length; b++) {
			System.out.printf("\n%-20d \t", budgets[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				System.out.printf("%-20d \t", coveredTasks[b][j2]);
		}
	}
}
