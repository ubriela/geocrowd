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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.GenericWorker;
import org.geocrowd.common.entropy.Coord;
import org.geocrowd.common.entropy.EntropyRecord;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericCrowd.
 */
public abstract class Geocrowd {

	/** The min latitude. */
	public double minLatitude = Double.MAX_VALUE;

	/** The max latitude. */
	public double maxLatitude = -Double.MAX_VALUE;

	/** The min longitude. */
	public double minLongitude = Double.MAX_VALUE;

	/** The max longitude. */
	public double maxLongitude = -Double.MAX_VALUE;
	
	/** The resolution. */
	public double resolution = 0.0002;
	
	/** The row count. */
	public int rowCount = 0; // number of rows for the grid

	/** The col count. */
	public int colCount = 0; // number of cols for the grid
	
	/** The entropies. */
	public HashMap<Integer, HashMap<Integer, Double>> entropies = null;

	/** The max entropy. */
	public double maxEntropy = 0;
	
	/** The sum entropy. */
	public int sumEntropy = 0;
	
	// ---------------

	/** The entropy list. */
	public ArrayList<EntropyRecord> entropyList = new ArrayList();
	
	
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
	
        /** average time to assign tasks. */
        public static double AverageTimeToAssignTask = 0.0;
        
        /** num time instace tasks assigned */
        public static int numTimeInstanceTaskAssign = 0;
        
	/** The Total expired task. */
	public int TotalExpiredTask = 0;

	/** The Total travel distance. */
	public double TotalTravelDistance = 0;

	/** works as the clock for task generation. */
	public int TimeInstance = 0;

	/** current workers at one time instance. */
	public static ArrayList<GenericWorker> workerList = new ArrayList<>();

	/** maintain a set of current task list, not include expired ones. */
	public static ArrayList<GenericTask> taskList = new ArrayList<>();

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
			return worker.distanceToTask(task);

		// not geographical coordinates
		double distance = Math.sqrt((worker.getLatitude() - task.getLat())
				* (worker.getLatitude() - task.getLat())
				+ (worker.getLongitude() - task.getLng())
				* (worker.getLongitude() - task.getLng()));
		return distance;
	}

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

public void createGrid(DatasetEnum dataset) {
	switch (DATA_SET) {
	case GOWALLA:
		resolution = Constants.gowallaResolution;
		break;
	case SKEWED:
		resolution = Constants.skewedResolution;
		break;
	case UNIFORM:
		resolution = Constants.uniResolution;
		break;
	case SMALL_TEST:
		resolution = Constants.smallResolution;
	case YELP:
		resolution = Constants.yelpResolution;
	}
	rowCount = colCount = (int)(1.0/resolution);
	System.out
			.println("rowcount: " + rowCount + "    colCount:" + colCount);
}
	
	/**
	 * Get a list of entropy records.
	 */
	public void readEntropy() {
		String filePath = "";
		switch (DATA_SET) {
		case GOWALLA:
			filePath = Constants.gowallaLocationEntropyFileName;
			break;
		case SKEWED:
			filePath = Constants.skewedLocationDensityFileName;
			break;
		case UNIFORM:
			filePath = Constants.uniLocationDensityFileName;
			break;
		case SMALL_TEST:
			filePath = Constants.smallLocationDensityFileName;
			break;
		case YELP:
			filePath = Constants.yelpLocationEntropyFileName;
			break;
		}

		entropies = new HashMap<Integer, HashMap<Integer, Double>>();
		try {
			FileReader file = new FileReader(filePath);
			BufferedReader in = new BufferedReader(file);
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split(",");
				int row = Integer.parseInt(parts[0]);
				int col = Integer.parseInt(parts[1]);
				double entropy = Double.parseDouble(parts[2]);
				if (entropy > maxEntropy)
					maxEntropy = entropy;

				if (entropies.containsKey(row))
					entropies.get(row).put(col, entropy);
				else {
					HashMap<Integer, Double> rows = new HashMap<Integer, Double>();
					rows.put(col, entropy);
					entropies.put(row, rows);
				}
				EntropyRecord dR = new EntropyRecord(entropy, new Coord(row,
						col));
				entropyList.add(dR);
				sumEntropy += entropy;
			}
			System.out.println("Sum of entropy: " + sumEntropy
					+ "; Max entropy: " + maxEntropy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * compute grid granularity.
	 * 
	 * @param dataset
	 *            the dataset
	 */
	public void createGrid() {
		switch (DATA_SET) {
		case GOWALLA:
			resolution = Constants.gowallaResolution;
			break;
		case SKEWED:
			resolution = Constants.skewedResolution;
			break;
		case UNIFORM:
			resolution = Constants.uniResolution;
			break;
		case SMALL_TEST:
			resolution = Constants.smallResolution;
		case YELP:
			resolution = Constants.yelpResolution;
		}
		rowCount = colCount = (int)(1.0/resolution);
		System.out
				.println("rowcount: " + rowCount + "    colCount:" + colCount);
	}
	/**
	 * Lat to row idx.
	 * 
	 * @param lat
	 *            the lat
	 * @return the int
	 */
	public int latToRowIdx(double lat) {
		return (int) (1/resolution * (lat - minLatitude) / (maxLatitude - minLatitude));
	}

	/**
	 * Lng to col idx.
	 * 
	 * @param lng
	 *            the lng
	 * @return the int
	 */
	public int lngToColIdx(double lng) {
		return (int) ((lng - minLongitude) / (resolution * (maxLongitude- minLongitude)));
	}
	
	/**
	 * Compute cost.
	 * 
	 * @param t
	 *            the t
	 * @return the double
	 */
	public double computeCost(GenericTask t) {
		int row = latToRowIdx(t.getLat());
		int col = lngToColIdx(t.getLng());
		// System.out.println(row + " " + col);
		double entropy = 0;
		if (entropies.containsKey(row)) {
			HashMap h = entropies.get(row);
			Iterator it = h.keySet().iterator();

			if (entropies.get(row).containsKey(col)) {
				// System.out.println(row + " !!!!!!!  " + col);
				entropy = entropies.get(row).get(col);
			}
		}
		// System.out.println(score / (1.0 + entropy));
		return entropy;
	}

	/**
	 * Prints the boundaries.
	 */
	public void printBoundaries() {
		System.out.println("minLat:" + minLatitude + "   maxLat:" + maxLatitude
				+ "   minLng:" + minLongitude + "   maxLng:" + maxLongitude);
	}
	
	/**
	 * Matching tasks workers.
	 */
	public abstract void matchingTasksWorkers();

	/**
	 * Read tasks per time instance
	 * 
	 * @param fileName
	 *            the file name
	 */
	public abstract void readTasks(String fileName);

	/**
	 * Read workers per time instance
	 * 
	 * @param fileName
	 *            the file name
	 */
	public abstract void readWorkers(String fileName);

}
