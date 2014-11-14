package test.geocrowd;

import java.io.IOException;
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
		test();
	}

	private static void test() throws IOException {

		Integer[] listBudgetTest = new Integer[] { 1000 };

		for (Integer j = 0; j < listBudgetTest.length; j++) {

			Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
			Geocrowd.algorithm = AlgorithmEnum.MAX_COVER_BASIC_MO;
			OnlineMTC onlineMTC = new OnlineMTC();
			/**
			 * clear worker, task list
			 */
			OnlineMTC.taskList.clear();
			OnlineMTC.workerList.clear();
			onlineMTC.totalBudget = listBudgetTest[j];
			System.out.println("ttasks\ttasks\tworkers\tT/W");
			for (int i = 0; i < Constants.TIME_INSTANCE; i++) {

				switch (Geocrowd.DATA_SET) {
				case GOWALLA:
					onlineMTC.readTasks(Constants.gowallaTaskFileNamePrefix + i
							+ ".txt");
					onlineMTC.readWorkers(Constants.gowallaWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case YELP:
					onlineMTC.readTasks(Constants.yelpTaskFileNamePrefix + i
							+ ".txt");
					onlineMTC.readWorkers(Constants.yelpWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case UNIFORM:
					onlineMTC.readTasks(Constants.uniTaskFileNamePrefix + i
							+ ".txt");
					onlineMTC.readWorkers(Constants.uniWorkerFileNamePrefix + i
							+ ".txt");
					break;
				case SKEWED:
					onlineMTC.readTasks(Constants.skewedTaskFileNamePrefix + i
							+ ".txt");
					onlineMTC.readWorkers(Constants.skewedWorkerFileNamePrefix
							+ i + ".txt");
					break;
				case SMALL_TEST:
					onlineMTC.readTasks(Constants.smallTaskFileNamePrefix + i
							+ ".txt");
					onlineMTC.readWorkers(Constants.smallWorkerFileNamePrefix
							+ i + ".txt");
					break;
				}

				//
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

			System.out.printf("\n%-15s %-15s %-15s %-15s %-15s", "TotalTask",
					"CoveredTask", "TotalWorker", "SelectedWorker", "W/T");

			System.out.printf("\n%-15d %-15d %-15d %-15d %-15d",
					onlineMTC.TaskCount, onlineMTC.numberCoveredTask,
					onlineMTC.totalBudget, onlineMTC.numberSelectedWorker,
					onlineMTC.numberCoveredTask/onlineMTC.numberSelectedWorker);
			
			onlineMTC.printWorkerCounts();
		}
	}

}
