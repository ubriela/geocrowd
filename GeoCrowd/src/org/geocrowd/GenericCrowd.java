/*******************************************************************************
 * @ Year 2013
 * This is the source code of the following papers. 
 * 
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla Kazemi, Cyrus Shahabi.
 * 
 * 
 * Please contact the author Hien To, ubriela@gmail.com if you have any question.
 *
 * Contributors:
 * Hien To - initial implementation
 *******************************************************************************/
package org.geocrowd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.geocrowd.common.GenericTask;
import org.geocrowd.common.GenericWorker;
import org.geocrowd.util.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericCrowd.
 */
public abstract class GenericCrowd {

	/** The data set. */
	public static DatasetEnum DATA_SET;

	/** The algorithm. */
	public static AlgorithmEnum algorithm = AlgorithmEnum.BASIC;

	/** number of tasks generated so far. */
	public int TaskCount = 0;

	/** number of workers generated so far. */
	public int WorkerCount = 0;
        /** number of assigned tasks. */
	public static int TotalAssignedTasks = 0;

        /** number of assigned tasks. */
	public static int TotalAssignedWorkers = 0;
	
	/** The Total expired task. */
	public int TotalExpiredTask = 0;

	/** The Total travel distance. */
	public double TotalTravelDistance = 0;

	/** works as the clock for task generation. */
	public int TimeInstance = 0;

	/** current workers at one time instance. */
	public ArrayList<GenericWorker> workerList = new ArrayList<>();

	/** maintain a set of current task list, not include expired ones. */
	public ArrayList<GenericTask> taskList = new ArrayList<>();

	/**
	 * maintain tasks that participate in one time instance task assignment
	 * (i.e., at least one worker can perform this task). The values in this
	 * list point to an index in taskList.
	 */
	public ArrayList<Integer> candidateTaskIndices = null;

	/**
	 * Each worker has a list of task index that he is eligible to perform. The
	 * container contains task index of the elements in candidate tasks indices
	 * (not in the task list)
	 */
	ArrayList<ArrayList> containerWorker;

	/**
	 * used to prune workers with no task in container. Similar to
	 * containerWorker, containerPrune contains task index of elements in
	 * candidate tasks indices (not in the task list)
	 */
	ArrayList[] containerPrune = null;

	/** is used to compute average worker/task. */
	HashMap<Integer, ArrayList> invertedContainer;

	/**
	 * The task set at one time instance, to quickly find candidate tasks.
	 * Taskset contains task index of the tasks covered by worker
	 */
	public HashSet<Integer> taskSet = null;

	/** The avg wt. */
	public double avgWT = 0;

	/** The var wt. */
	public double varWT = 0;

	/** The avg tw. */
	public double avgTW = 0;

	/** The var tw. */
	public double varTW = 0;

	/**
	 * Compute average number of spatial task which are inside the spatial
	 * region of a given worker. This method computes both avgTW and varTW;
	 */
	public void computeAverageTaskPerWorker() {
		double totalNoTasks = 0;
		double sum_sqr = 0;
		for (ArrayList T : containerWorker) {
			if (T != null) {
				int size = T.size();
				totalNoTasks += size;
				sum_sqr += size * size;
			}
		}
		avgTW = totalNoTasks / containerWorker.size();
		varTW = (sum_sqr - ((totalNoTasks * totalNoTasks) / containerWorker
				.size())) / (containerWorker.size());
	}

	/**
	 * Compute average number of worker whose spatial region contain a given
	 * spatial task. This function returns avgWT and varWT
	 */
	public void computeAverageWorkerPerTask() {
		double totalNoTasks = 0;
		double sum_sqr = 0;
		Iterator<Integer> it = invertedContainer.keySet().iterator();

		// iterate through HashMap keys Enumeration
		while (it.hasNext()) {
			Integer t = it.next();
			GenericTask task = taskList.get(t);
			ArrayList W = invertedContainer.get(t);
			int size = W.size();
			totalNoTasks += size;
			sum_sqr += size * size;
		}

		avgWT = totalNoTasks / taskList.size();
		varWT = (sum_sqr - ((totalNoTasks * totalNoTasks) / taskList.size()))
				/ (taskList.size());
	}

	/**
	 * Euclidean distance between worker and task.
	 * 
	 * @param worker
	 *            the worker
	 * @param task
	 *            the task
	 * @return the double
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
	 * Matching tasks workers.
	 */
	public abstract void matchingTasksWorkers();

	/**
	 * remove expired task from tasklist.
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
	 * Read tasks.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public abstract void readTasks(String fileName);

	/**
	 * Read workers.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public abstract void readWorkers(String fileName);

}
