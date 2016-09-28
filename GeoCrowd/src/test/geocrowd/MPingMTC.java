package test.geocrowd;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.geocrowd.AlgorithmEnum;
import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.OfflineMTC;
import org.geocrowd.OnlineMTC;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdSensingConstants;

public class MPingMTC {

	static Logger logger = Logger.getLogger(OnlineMTCTest.class.getName());

	public static void main(String[] args) throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.MPING;
		boolean isOffline = Boolean.valueOf(args[0]);
		boolean isFixed = Boolean.valueOf(args[1]);
		int instances = Integer.valueOf(args[2]);
		int budget = Integer.valueOf(args[3]) * instances;
		int radius = Integer.valueOf(args[4]);

		int totalTask = 0;
		int coveredTasks = 0;
		int totalWorker = 0;
		int assignedWorkers = 0;

		GeocrowdSensingConstants.TASK_RADIUS = radius;
		GeocrowdSensingConstants.TIME_INSTANCE = instances;

		System.out.print("budget = " + budget + ", isOffline = " + isOffline + ", isFixed = " + isFixed + "\n");
		if (isOffline) {
			OfflineMTC offlineMTC = new OfflineMTC();
			offlineMTC.isFixed = isFixed;
			offlineMTC.budget = budget;
			offlineMTC.reset();

			for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
				offlineMTC
						.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET) + i
										+ ".txt", i);
				offlineMTC.readWorkers(
						Utils.datasetToWorkerPath(Geocrowd.DATA_SET) + i
								+ ".txt", i);
			}

			offlineMTC.matchingTasksWorkers();
			offlineMTC.maxTaskCoverage();

			assignedWorkers = OfflineMTC.TotalAssignedWorkers;
			coveredTasks = OfflineMTC.TotalAssignedTasks;

			totalTask = offlineMTC.TaskCount;
			totalWorker = offlineMTC.budget;
			
			
			StringWriter stringWriter = new StringWriter();
			PrintWriter pw = new PrintWriter(stringWriter);
			System.out.printf("%-10s \t %-10s \t %-10s \t %-10s \t %-10s\n",
					"TTasks", "TCoveredTask", "TWorker", "TSelectWorker", "T/W");
			pw.printf("%-10d \t %-10d \t %-10d \t %-10d \t %-10d", totalTask, coveredTasks, totalWorker, assignedWorkers, coveredTasks/assignedWorkers);

			PrintWriter writer = new PrintWriter("src/test/geocrowd/offline.txt");
			writer.print(stringWriter.toString());
			writer.close();
			
			logger.info(stringWriter.toString());
			System.out.println(stringWriter.toString());

		} else {
			System.out.printf("\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s\n",
					"Time", "TotalTask", "CoveredTask", "TCoveredTask", "TotalWorker",
					"SelectWorker", "TSelectWorker", "W/T");
			
			Geocrowd.algorithm = AlgorithmEnum.MAX_COVER_ADAPT_T;
			OnlineMTC onlineMTC = new OnlineMTC();
			onlineMTC.reset();
			onlineMTC.totalBudget = budget;
			onlineMTC.createGrid();
			onlineMTC.readBoundary();
			
			int totalCoveredTask = 0;
			int totalSelectedWorker = 0;
			
			StringWriter stringWriter = new StringWriter();
			for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
				onlineMTC
						.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET) + i
										+ ".txt", i);
				onlineMTC.readWorkers(Utils
						.datasetToWorkerPath(Geocrowd.DATA_SET) + i + ".txt");

				onlineMTC.matchingTasksWorkers();
				onlineMTC.maxCoverage();
				OnlineMTC.TimeInstance++;
				
				PrintWriter pw = new PrintWriter(stringWriter);
				pw.printf(
						"%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
						(i + 1),
						onlineMTC.TaskCount,
						OnlineMTC.TotalAssignedTasks - totalCoveredTask,
						OnlineMTC.TotalAssignedTasks,
						onlineMTC.totalBudget,
						OnlineMTC.TotalAssignedWorkers - totalSelectedWorker,
						OnlineMTC.TotalAssignedWorkers,
						OnlineMTC.TotalAssignedTasks
								/ Math.max(1, OnlineMTC.TotalAssignedWorkers));
				
				
				totalCoveredTask = OnlineMTC.TotalAssignedTasks;
				totalSelectedWorker = OnlineMTC.TotalAssignedWorkers;
				
				totalTask = onlineMTC.TaskCount;
				totalWorker = onlineMTC.totalBudget;
			}
			
			PrintWriter writer = new PrintWriter("src/test/geocrowd/online.txt");
			writer.print(stringWriter.toString());
			writer.close();
			
			logger.info(stringWriter.toString());
			System.out.println(stringWriter.toString());
			
//			assignedWorkers = OnlineMTC.TotalAssignedWorkers;
//			coveredTasks = OnlineMTC.TotalAssignedTasks;
		}

	}

}
