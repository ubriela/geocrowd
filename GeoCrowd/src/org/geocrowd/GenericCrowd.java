package org.geocrowd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.geocrowd.common.GenericTask;
import org.geocrowd.common.GenericWorker;
import org.geocrowd.util.Utils;

public abstract class GenericCrowd {
	public static DatasetEnum DATA_SET;
	public static AlgorithmEnum algorithm = AlgorithmEnum.BASIC;
	
	
	public int TaskCount = 0; // number of tasks generated so far
	public int WorkerCount = 0; // number of workers generated so far
	public int TotalTasksAssigned = 0; // number of assigned tasks
	public int TotalExpiredTask = 0;
	public double TotalTravelDistance = 0;
	public int time_instance = 0; // works as the clock for task generation

	
	public ArrayList<GenericWorker> workerList = new ArrayList();
	public ArrayList<GenericTask> taskList = new ArrayList();
	public ArrayList<Integer> candidateTasks = null;
	
	
	ArrayList<ArrayList> container; // for every worker, maintain a set of taskid that he can perform
	ArrayList[] container2;
	
	HashMap<Integer, ArrayList> invertedTable; // is used to compute average worker/task
	
	public HashSet<Integer> taskSet = null;
	
	
	public double avgWT = 0;
	public double varWT = 0;
	public double avgTW = 0;
	public double varTW = 0;
	
	
	/**
	 * Compute average number of spatial task which are inside the spatial
	 * region of a given worker. This method computes both avgTW and varTW;
	 */
	public void computeAverageTaskPerWorker() {
		double totalNoTasks = 0;
		double sum_sqr = 0;
		for (ArrayList T : container) {
			if (T != null) {
				int size = T.size();
				totalNoTasks += size;
				sum_sqr += size * size;
			}
		}
		avgTW = totalNoTasks / container.size();
		varTW = (sum_sqr - ((totalNoTasks * totalNoTasks) / container.size()))
				/ (container.size());
	}

	/**
	 * Compute average number of worker whose spatial region contain a given
	 * spatial task. This function returns avgWT and varWT
	 */
	public void computeAverageWorkerPerTask() {
		double totalNoTasks = 0;
		double sum_sqr = 0;
		Iterator<Integer> it = invertedTable.keySet().iterator();

		// iterate through HashMap keys Enumeration
		while (it.hasNext()) {
			Integer t = (Integer) it.next();
			GenericTask task = taskList.get(t);
			ArrayList W = invertedTable.get(t);
			int size = W.size();
			totalNoTasks += size;
			sum_sqr += size * size;
		}

		avgWT = totalNoTasks / taskList.size();
		varWT = (sum_sqr - ((totalNoTasks * totalNoTasks) / taskList.size()))
				/ (taskList.size());
	}

	
	/**
	 * remove expired task from tasklist
	 */
	public void pruneExpiredTasks() {
		for (int i = taskList.size() - 1; i >= 0; i--) {
			// remove the solved task from task list
			if (taskList.get(i).isExpired()) {
				taskList.remove(i);
				TotalExpiredTask++;
			}
		}
	}
	
	
	/**
	 * Euclidean distance between worker and task
	 * 
	 * @param worker
	 * @param task
	 * @return
	 */
	public double distanceWorkerTask(GenericWorker worker, GenericTask task) {
		if (DATA_SET == DatasetEnum.GOWALLA || DATA_SET == DatasetEnum.YELP)
			return Utils.computeDistance(worker, task);
		
		// not geographical coordinates
		double distance = Math.sqrt((worker.getLatitude() - task.getLat())
				* (worker.getLatitude() - task.getLat())
				+ (worker.getLongitude() - task.getLng())
				* (worker.getLongitude() - task.getLng()));
		return distance;
	}

	/**
	 * 
	 * @param fileName
	 */
	public abstract void readWorkers(String fileName);
	
	/**
	 * 
	 * @param fileName
	 */
	public abstract void readTasks(String fileName);
	
	/**
	 * 
	 */
	public abstract void matchingTasksWorkers();
	
}
