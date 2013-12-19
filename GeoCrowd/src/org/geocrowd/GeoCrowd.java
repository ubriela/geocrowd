package org.geocrowd;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.datasets.gowalla.PreProcess;
import org.datasets.gowalla.Range;
import org.datasets.gowalla.UniformGenerator;
import org.geocrowd.maxmatching.BPWMatching.HungarianAlgorithm;
import org.geocrowd.maxmatching.BPWMatching.Utility;

import cplex.BPMatchingCplex;

/**
 * 
 * @author Leyla
 */
public class GeoCrowd {
	public double minLatitude = Double.MAX_VALUE;
	public double maxLatitude = -Double.MAX_VALUE;
	public double minLongitude = Double.MAX_VALUE;
	public double maxLongitude = -Double.MAX_VALUE;
	public int TaskCount = 0; // number of tasks generated so far
	public int WorkerCount = 0; // number of workers generated so far

	public int workerNo = 100; // 100 // number of workers when workers are to
								// be generated #
	public Cell[][] grid;
	public HashMap<Integer, HashMap<Integer, Double>> entropies = null;

	public double maxEntropy = 0;
	public int rowCount = 0; // number of rows for the grid
	public int colCount = 0; // number of cols for the grid

	public ArrayList<EntropyRecord> entropyList = new ArrayList();
	public ArrayList<Worker> workerList = new ArrayList();
	public ArrayList<Integer> candidateTasks = null;
	public HashSet<Integer> setTasks = null;
	public ArrayList<Task> taskList = new ArrayList();
	public int sumMaxT = 0;
	public double TotalScore = 0;
	public int TotalTasksAssigned = 0; // number of assigned tasks
	public int TotalTasksExactMatch = 0; // number of assigned tasks, from exact
											// match
	public double TotalTravelDistance = 0;
	public int sumEntropy = 0;
	public int timeCounter = 0; // works as the clock for task generation
	public int taskExpiredNo = 0;
	ArrayList<ArrayList> _container; // for the mbr of every user, tells which
										// tasks lay
	ArrayList[] container;
	// inside

	HashMap<Integer, ArrayList> invertedTable; // is used to compute average
												// worker/task

	public ArrayList<double[]> allTasks = new ArrayList();

	public double avgWT = 0;
	public double varWT = 0;
	public double avgTW = 0;
	public double varTW = 0;
	public double resolution = 0;
	public static int DATA_SET;
	public static AlgoEnums algorithm = AlgoEnums.GR;

	private int totalExpiredTask = 0;

	public GeoCrowd() {
		String boundaryFile = "";
		switch (DATA_SET) {
		case 0:
			boundaryFile = Constants.gowallaBoundary;
			break;
		case 1:
			boundaryFile = Constants.syncBoundary;
			break;
		case 2:
			boundaryFile = Constants.uniBoundary;
			break;
		case 3:
			boundaryFile = Constants.smallBoundary;
			break;
		case 4:
			boundaryFile = Constants.yelpBoundary;
			break;
		}

		PreProcess prep = new PreProcess();
		prep.DATA_SET = DATA_SET;
		prep.readBoundary();
		minLatitude = prep.minLatitude;
		maxLatitude = prep.maxLatitude;
		minLongitude = prep.minLongitude;
		maxLongitude = prep.maxLongitude;
	}

	public void printBoundaries() {
		System.out.println("minLat:" + minLatitude + "   maxLat:" + maxLatitude
				+ "   minLng:" + minLongitude + "   maxLng:" + maxLongitude);
	}

	public void createGrid() {
		resolution = 0;
		switch (DATA_SET) {
		case 0:
			resolution = Constants.gowallaResolution;
			break;
		case 1:
			resolution = Constants.syncResolution;
			break;
		case 2:
			resolution = Constants.uniResolution;
			break;
		case 3:
			resolution = Constants.smallResolution;
			break;
		case 4:
			resolution = Constants.yelpResolution;
			break;
		}
		rowCount = (int) ((maxLatitude - minLatitude) / resolution) + 1;
		colCount = (int) ((maxLongitude - minLongitude) / resolution) + 1;
		System.out
				.println("Grid resolution: " + rowCount + "x" + colCount);
	}

	/**
	 * Get a list of entropy records
	 */
	public void readEntropy() {
		String filePath = "";
		switch (DATA_SET) {
		case 0:
			filePath = Constants.gowallaLocationEntropyFileName;
			break;
		case 1:
			filePath = Constants.syncLocationDensityFileName;
			break;
		case 2:
			filePath = Constants.uniLocationDensityFileName;
			break;
		case 3:
			filePath = Constants.smallLocationDensityFileName;
			break;
		case 4:
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
				EntropyRecord dR = new EntropyRecord(entropy, row, col);
				entropyList.add(dR);
				sumEntropy += entropy;
			}
			System.out.println("Sum of entropy: " + sumEntropy
					+ "; Max entropy: " + maxEntropy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double convertToLat(int row) { // for a given row , converts it back
											// to latitude
		return (((double) row) * resolution) + minLatitude;
	}

	public double convertToLng(int col) { // for a given col , converts it back
											// to longitude
		return (((double) col) * resolution) + minLongitude;
	}

	public int getRowIdx(double lat) {
		return (int) ((lat - minLatitude) / resolution);
	}

	public int getColIdx(double lng) {
		return (int) ((lng - minLongitude) / resolution);
	}

	public void printGrid() {
		int existCnt = 0;
		int nonExistCnt = 0;
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < colCount; j++) {
				if (grid[i][j] != null) {
					System.out.print(grid[i][j].getDensity() + " "); // System.out.print("+ ");
					existCnt++;
				} else {
					// grid[i][j] = new Cell(-1);
					// System.out.print(grid[i][j].getDensity()+"  ");
					// System.out.print("  ");
					nonExistCnt++;
				}
			}
			System.out.println();
		}
		System.out.println("rowCount: " + rowCount + "       colcount:"
				+ colCount);
		System.out.println("existing cells count: " + existCnt
				+ "       non-existing cells:" + nonExistCnt);
	}

	public void printStatus() {
		System.out.println("#Tasks remained: " + taskList.size());
	}

	/**
	 * Get all the task (lat/lon only) from gowalla file
	 */
	public void readTaskLocations() {
		try {
			allTasks = new ArrayList();
			HashMap HashMap = new HashMap();
			FileReader reader = new FileReader(Constants.gowallaFileName2);
			BufferedReader in = new BufferedReader(reader);
			int cnt = 0;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\\s");
				Integer userID = Integer.parseInt(parts[0]);
				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);
				Integer locID = Integer.parseInt(parts[4]);
				double[] location = new double[2];
				location[0] = lat;
				location[1] = lng;
				if (!HashMap.containsKey(locID)) {
					HashMap.put(locID, location);
					allTasks.add(location);
					cnt++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("No of tasks in total: " + allTasks.size());
	}

	public void readTasks(String fileName) {
		int listCount = taskList.size();
		int cnt = 0;
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader in = new BufferedReader(reader);
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split(",");
				double lat = Double.parseDouble(parts[0]);
				double lng = Double.parseDouble(parts[1]);
				int time = Integer.parseInt(parts[2]);
				Double entropy = Double.parseDouble(parts[3]);
				int type = Integer.parseInt(parts[4]);
				Task t = new Task(lat, lng, time, entropy, type);
				taskList.add(listCount, t);
				listCount++;
				TaskCount++;
			}
			in.close();
		} catch (Exception e) {
		}
	}

	/**
	 * spatial tasks are randomly generated for the given spots in the area
	 * (with entropy information)
	 * 
	 * @param filenName
	 *            : output
	 */
	public void readTasksWithEntropy(String fileName) {
		int listCount = taskList.size();
		try {
			FileWriter writer = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(writer);
			for (int i = 0; i < Constants.TaskNo; i++) {
				int randomIdx = (int) UniformGenerator.randomValue(new Range(0,
						entropyList.size()), true);
				EntropyRecord dR = entropyList.get(randomIdx);
				// generate a task inside this cell
				int row = dR.getRowIdx();
				double startLat = convertToLat(row);
				double endLat = convertToLat(row + 1);
				double lat = UniformGenerator.randomValue(new Range(startLat,
						endLat), false);
				int col = dR.getColIdx();
				double startLng = convertToLng(col);
				double endLng = convertToLng(col + 1);
				double lng = UniformGenerator.randomValue(new Range(startLng,
						endLng), false);
				double entropy = dR.getEntropy();
				int time = timeCounter;
				int taskType = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				Task t = new Task(lat, lng, time, entropy, taskType);
				out.write(lat + "," + lng + "," + time + "," + entropy + ","
						+ taskType + "\n");
				taskList.add(listCount, t);
				listCount++;
			}
			TaskCount += Constants.TaskNo;
			System.out.println("#Total tasks:" + TaskCount);
			out.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Randomly generate task (without entropy)
	 * 
	 * @param fileName
	 */
	public void generateTasksWOEntropy(String fileName) {
		int listCount = taskList.size();
		try {
			FileWriter writer = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(writer);
			for (int i = 0; i < Constants.TaskNo; i++) {
				double lat = UniformGenerator.randomValue(new Range(
						minLatitude, maxLatitude), false);
				double lng = UniformGenerator.randomValue(new Range(
						minLongitude, maxLongitude), false);
				// printBoundaries();
				// System.out.println(lat + " " + lng);
				int time = timeCounter;
				int taskType = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				Task t = new Task(lat, lng, time, -1, taskType);
				out.write(lat + "," + lng + "," + time + "," + (-1) + "\n");
				taskList.add(listCount, t);
				listCount++;

			}
			TaskCount += Constants.TaskNo;
			System.out.println("#Total tasks:" + TaskCount);
			out.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Location of the tasks are generated randomly from user's location in
	 * gowalla
	 * 
	 * @param fileName
	 */
	public void readTasksGowalla(String fileName) {
		int listCount = taskList.size();
		try {
			FileWriter writer = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(writer);
			for (int i = 0; i < allTasks.size(); i++) {
				double[] loc = allTasks.get(i);
				double lat = loc[0];
				double lng = loc[1];
				int time = timeCounter;
				int taskType = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				Task t = new Task(lat, lng, time, -1, taskType);
				out.write(lat + "," + lng + "," + time + "," + (-1) + ","
						+ "\n");
				taskList.add(listCount, t);
				listCount++;
			}
			TaskCount += allTasks.size();
			System.out.println("#Total tasks:" + TaskCount);
			out.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Randomly generate workers. synthetic dataset
	 * 
	 * @param fileName
	 *            : output
	 * 
	 * @maxT is randomly generated
	 * @MBR's size is randomly generated
	 */
	public void generateWorkersRandomMBR(String fileName) {
		workerList = new ArrayList();
		double maxRangeX = (maxLatitude - minLatitude) * Constants.MaxRangePerc;
		double maxRangeY = (maxLongitude - minLongitude)
				* Constants.MaxRangePerc;
		try {
			FileWriter writer = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(writer);

			for (int i = 0; (i < workerNo); i++) {
				double randomEntropy = UniformGenerator.randomValue(new Range(
						0, sumEntropy), false);
				double sum = 0;
				int randomIdx = -1;
				for (int j = 0; j < entropyList.size(); j++) {
					EntropyRecord dR = entropyList.get(j);
					if (randomEntropy < sum) {
						randomIdx = j;
						break;
					}
					sum += dR.getEntropy();
				}
				EntropyRecord dR = entropyList.get(randomIdx);
				int row = dR.getRowIdx();
				double startLat = convertToLat(row);
				double endLat = convertToLat(row + 1);
				double lat = UniformGenerator.randomValue(new Range(startLat,
						endLat), false);
				int col = dR.getColIdx();
				double startLng = convertToLng(col);
				double endLng = convertToLng(col + 1);
				double lng = UniformGenerator.randomValue(new Range(startLng,
						endLng), false);
				int maxT = (int) UniformGenerator.randomValue(new Range(0,
						Constants.MaxTasksPerWorker), true) + 1;
				double rangeX = UniformGenerator.randomValue(new Range(0,
						maxRangeX), false);
				double rangeY = UniformGenerator.randomValue(new Range(0,
						maxRangeY), false);
				MBR mbr = MBR.createMBR(lat, lng, rangeX, rangeY);
				checkBoundaryMBR(mbr);
				int exp = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				Worker w = new Worker(lat, lng, maxT, mbr);
				w.addExpertise(exp);
				out.write(randomIdx + ",time" + "," + lat + "," + lng + ","
						+ maxT + "," + "[" + mbr.getMinLat() + ","
						+ mbr.getMinLng() + "," + mbr.getMaxLat() + ","
						+ mbr.getMaxLng() + "]\n");
				workerList.add(w);
				dR.incWorkerNo();
			}
			WorkerCount += workerNo;
			out.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Working region of each worker is computed from his past history
	 * 
	 * @param fileName
	 */
	public void readWorkers(String fileName) {
		workerList = new ArrayList();
		int cnt = 0;
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader in = new BufferedReader(reader);

			while (in.ready()) {
				String line = in.readLine();
				line = line.replace("],[", ";");
				String[] parts = line.split(";");
				parts[0] = parts[0].replace(",[", ";");
				String[] parts1 = parts[0].split(";");

				String[] coords = parts1[0].split(",");

				String[] boundary = parts1[1].split(",");
				String userId = coords[0];
				double lat = Double.parseDouble(coords[1]);
				double lng = Double.parseDouble(coords[2]);
				int maxT = Integer.parseInt(coords[3]);

				double mbr_minLat = Double.parseDouble(boundary[0]);
				double mbr_minLng = Double.parseDouble(boundary[1]);
				double mbr_maxLat = Double.parseDouble(boundary[2]);
				double mbr_maxLng = Double.parseDouble(boundary[3]);
				MBR mbr = new MBR(mbr_minLat, mbr_minLng, mbr_maxLat,
						mbr_maxLng);

				Worker w = new Worker(userId, lat, lng, maxT, mbr);

				String experts = parts[1].substring(0, parts[1].length() - 1);
				String[] exps = experts.split(",");
				for (int i = 0; i < exps.length; i++) {
					w.addExpertise(Integer.parseInt(exps[i]));
				}

				workerList.add(w);
				cnt++;
			}

			in.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		WorkerCount += cnt;
	}

	/**
	 * Working region of each worker is computed randomly
	 * 
	 * @param fileName
	 *            : worker file name
	 */
	public void readWorkersRandomMBR(String fileName) {
		workerList = new ArrayList();
		double maxRangeX = (maxLatitude - minLatitude) * Constants.MaxRangePerc;
		double maxRangeY = (maxLongitude - minLongitude)
				* Constants.MaxRangePerc;
		int cnt = 0;
		try {
			FileReader reader = new FileReader(fileName);
			BufferedReader in = new BufferedReader(reader);

			while (in.ready()) {
				String line = in.readLine();
				line = line.replace("[", "");
				line = line.replace("]", "");
				String[] parts = line.split(",");
				String userId = parts[0];
				double lat = Double.parseDouble(parts[2]);
				double lng = Double.parseDouble(parts[3]);
				int maxT = (int) UniformGenerator.randomValue(new Range(0,
						Constants.MaxTasksPerWorker), true);
				double rangeX = UniformGenerator.randomValue(new Range(0,
						maxRangeX), false);
				double rangeY = UniformGenerator.randomValue(new Range(0,
						maxRangeY), false);
				MBR mbr = MBR.createMBR(lat, lng, rangeX, rangeY);
				checkBoundaryMBR(mbr);
				int exp = Integer.parseInt(parts[4]);
				Worker w = new Worker(userId, lat, lng, maxT, mbr);
				w.addExpertise(exp);
				workerList.add(w);
				cnt++;
			}

			in.close();
		} catch (Exception e) {
		}
		WorkerCount += cnt;
		System.out.println("#Total workers: " + WorkerCount);
	}

	private void checkBoundaryMBR(MBR mbr) {
		if (mbr.getMinLat() < minLatitude)
			mbr.setMinLat(minLatitude);
		if (mbr.getMaxLat() > maxLatitude)
			mbr.setMaxLat(maxLatitude);
		if (mbr.getMinLng() < minLongitude)
			mbr.setMinLng(minLongitude);
		if (mbr.getMaxLng() > maxLongitude)
			mbr.setMaxLng(maxLongitude);
	}

	/**
	 * Compute which tasks within working region of which worker and vice versa
	 */
	public void matchingTasksWorkers() {
		invertedTable = new HashMap<Integer, ArrayList>();
		container = new ArrayList[workerList.size()];
		candidateTasks = new ArrayList();
		setTasks = new HashSet<Integer>();

		// remove expired task from tasklist
		for (int i = taskList.size() - 1; i >= 0; i--) {
			// remove the solved task from task list
			if (taskList.get(i).isExpired()) {
				taskList.remove(i);
				totalExpiredTask++;
			}
		}

		for (int i = 0; i < workerList.size(); i++) {
			Worker w = workerList.get(i);
			rangeQuery(i, w.getMBR());
		}

		// remove workers with no tasks
		sumMaxT = 0;
		_container = new ArrayList<ArrayList>();
		for (int i = container.length - 1; i >= 0; i--) {
			if (container[i] == null || container[i].size() == 0) {
				workerList.remove(i);
			} else
				sumMaxT += workerList.get(i).getMaxTaskNo();
		}
		for (int i = 0; i < container.length; i++) {
			if (container[i] != null && container[i].size() > 0) {
				_container.add(container[i]);
			}
		}

		System.out.println();
	}

	/**
	 * Compute input for one time instance, including container and
	 * invertedTable
	 */
	private void rangeQuery(int workerIdx, MBR mbr) {
		int t = 0; // task id, increasing from 0 to the number of task - 1
		for (int i = 0; i < taskList.size(); i++) {
			// task.print();
			// mbr.print();
			// System.out.println(timeCounter);
			Task task = taskList.get(i);

			// tick expired task
			if ((timeCounter - task.getEntryTime()) >= Constants.TaskDuration) {
				task.setExpired();
			} else

			// if the task is not assigned and in the worker's working region
			// AND not assigned
			if (task.isCoveredBy(mbr.minLat, mbr.minLng, mbr.maxLat, mbr.maxLng)) {
				if (container[workerIdx] == null) {
					container[workerIdx] = new ArrayList();
				}

				if (!setTasks.contains(t)) {
					candidateTasks.add(t);
					setTasks.add(t);
				}
				container[workerIdx].add(candidateTasks.indexOf(t));

				if (!invertedTable.containsKey(t)) {
					ArrayList arr = new ArrayList();
					arr.add(workerIdx);
					invertedTable.put(t, arr);
				} else {
					ArrayList arr = invertedTable.get(t);
					arr.add(workerIdx);
					invertedTable.put(t, arr);
				}

			}// if not overlapped

			t++;
		}// for loop
	}

	// public void calculateFlow() {
	// // if (container.length != invertedTable.size())
	// // System.out.println("the two sets are not of equal size!!!!");
	// int V = container.length + taskList.size();
	// int s = V, t = V + 1;
	//
	// FlowNetwork G = new FlowNetwork(V, container, workerList, taskList,
	// assign_type);
	//
	// // compute maximum flow and minimum cut
	// long time1 = System.currentTimeMillis();
	// FordFulkerson maxflow = new FordFulkerson(G, s, t, assign_type,
	// workerList.size(), taskList);
	// long time2 = System.currentTimeMillis();
	// totalTime += (time2 - time1);
	// System.out.println("Max flow from " + s + " to " + t);
	//
	// // print min-cut
	// System.out.print("Min cut: ");
	// System.out.println();
	// TotalAssignedTasks += maxflow.value();
	// SumDistanceTotal += maxflow.sumDist;
	// System.out.println("Max flow value = " + maxflow.value()
	// + "    with min cost: " + maxflow.minCost()
	// + "     with mincost2: " + maxflow.minCost2
	// + "       with sum disntace: " + maxflow.sumDist);
	// System.out.println("Total number of assigned tasks:"
	// + TotalAssignedTasks);
	// }

	// this methods compute max weighted matching using the second Hungarian
	// algorithm with heuristics, thus faster than the other one
	// assuming maxT = 1 for all workers
	public double maxWeightedMatching2() {
		double[][] array = new double[_container.size()][taskList.size()]; // row
																			// represents
																			// workers,
																			// column
																			// represents
																			// tasks
		for (int i = 0; i < _container.size(); i++) {
			ArrayList<Integer> tasks = _container.get(i);
			if (tasks != null)
				for (int j : tasks) {
					array[i][j] = computeScore(workerList.get(i),
							taskList.get(j));
					// if (array[i][j] != 0)
					// System.out.println(array[i][j]);
				}
		}

		// transpose the matrix if #workers < #tasks
		if (array.length > array[0].length) {
			// Array transposed (because rows(worker)>columns(task))
			array = Utility.transpose(array);
		}

		double[][] origin = Utility.copyOf(array);

		for (int i = 0; i < array.length; i++) // Generate cost by subtracting.
		{
			for (int j = 0; j < array[i].length; j++) {
				array[i][j] = -array[i][j];
			}
		}

		// Utility.print2(origin);
		HungarianAlgorithm HA = new HungarianAlgorithm(array);
		int[] r = HA.execute(array);
		double sum = 0;

		int totalTasksAssigned = 0;
		int totalTasksExactMatch = 0;
		ArrayList<Integer> solvedTasks = new ArrayList<Integer>();
		for (int i = r.length - 1; i >= 0; i--) {
			// System.out.println((i + 1) + "->" + (r[i] + 1) + " : "
			// + origin[i][r[i]]);

			// remove the solved task from task list
			if (origin[i][r[i]] > 0) {
				sum += origin[i][r[i]];
				totalTasksAssigned++;
				// exact match?
				if (origin[i][r[i]] == Constants.EXACT_MATCH_SCORE)
					totalTasksExactMatch++;

				solvedTasks.add(r[i]);
			}
		}

		Collections.sort(solvedTasks);

		for (int i = solvedTasks.size() - 1; i >= 0; i--) {
			// remove the solved task from task list
			taskList.remove((int) solvedTasks.get(i)); // remove the last
														// element
														// first
		}

		System.out.printf("\nMaximum score: %.2f\n", sum);
		TotalScore += sum;
		TotalTasksAssigned += totalTasksAssigned;
		TotalTasksExactMatch += totalTasksExactMatch;
		return sum;
	}

	// any number of maxT
	public double maxWeightedMatching() {
		if (sumMaxT == 0 || candidateTasks.size() == 0) {
			System.out.println("No scheduling");
			return 0;
		}
		// sumMaxT is the number of logical workers
		double[][] array = new double[sumMaxT][candidateTasks.size()]; // row
																		// represents
																		// workers,
																		// column
																		// represents
																		// tasks
		HashMap<Integer, Integer> logicalWorkerToWorker = new HashMap<Integer, Integer>();
		int row = 0;
		for (int i = 0; i < _container.size(); i++) {
			ArrayList<Integer> tasks = _container.get(i);
			if (tasks != null)
				for (int j : tasks) {
					array[row][j] = computeScore(workerList.get(i),
							taskList.get(candidateTasks.get(j)));
				}
			logicalWorkerToWorker.put(row, i);
			row++;
			// create logical workers
			Worker w = workerList.get(i);
			for (int j = 0; j < w.getMaxTaskNo() - 1; j++) {
				array[row] = Arrays.copyOf(array[row - 1],
						array[row - 1].length);
				logicalWorkerToWorker.put(row, i);
				row++;
			}

		}

		// if (sumMaxT != row)
		// System.out.println("problem!!!!!");
		// System.out.println("row" + row);

		double[][] origin = null;
		// save original scores
		// if (this.algorithm == AlgoEnums.GR) {
		origin = Utility.copyOf(array);
		// } else {
		// AlgoEnums temp = this.algorithm;
		// this.algorithm = AlgoEnums.GR;
		// row = 0;
		// origin = new double[sumMaxT][candidateTasks.size()];
		// for (int i = 0; i < _container.size(); i++) {
		// ArrayList<Integer> tasks = _container.get(i);
		// if (tasks != null)
		// for (int j : tasks) {
		// origin[row][j] = computeScore(workerList.get(i),
		// taskList.get(candidateTasks.get(j)));
		// }
		// row++;
		// // create logical workers
		// Worker w = workerList.get(i);
		// for (int j = 0; j < w.getMaxTaskNo() - 1; j++) {
		// origin[row] = Arrays.copyOf(origin[row - 1],
		// origin[row - 1].length);
		// row++;
		// }
		// }
		// this.algorithm = temp;
		// }

		// transpose the matrix if #workers < #tasks
		boolean isTranpose = false;
		if (array.length > array[0].length) {
			// Array transposed (because rows(worker)>columns(task))
			array = Utility.transpose(array);
			origin = Utility.transpose(origin);
			isTranpose = true;
		}

		// Utility.print2(array);

		for (int i = 0; i < array.length; i++) // Generate cost by subtracting.
		{
			for (int j = 0; j < array[i].length; j++) {
				array[i][j] = -array[i][j];
			}
		}

		HungarianAlgorithm HA = new HungarianAlgorithm(array);
		int[] r = HA.execute(array);

		// COMPUTE
		double totalScore = 0;
		int totalTasksAssigned = 0;
		int totalTasksExactMatch = 0;
		double totalDistance = 0;

		for (int i = r.length - 1; i >= 0; i--)
			if (origin[i][r[i]] > 0)
				totalScore += origin[i][r[i]];

		// find max matching with minimum cost (if possible)
		ArrayList<MatchPair> taskAssigned = null;

		if (algorithm == AlgoEnums.LLEP || algorithm == AlgoEnums.NNP) {
			// coefficient values are generated from entropies or distance
			List<Double> objectiveCoeff = new ArrayList<Double>();
			// generated from scores<worker,task>
			List<Double> matchingCoeff = new ArrayList<Double>();
			// mapping between variable and edges
			HashMap<Integer, MatchPair> mapColToMatch = new HashMap<Integer, MatchPair>();

			// map between indices of logical workers to workers
			// HashMap<Integer, Integer> logicalWorkerToWorker = new
			// HashMap<Integer, Integer>();
			int var = 0;
			int w = 0; // physical worker
			int numWorker = 0; // logical worker
			for (ArrayList<Integer> tasks : _container) {
				Worker worker = workerList.get(w);
				if (tasks != null) {
					for (int i = 0; i < worker.getMaxTaskNo(); i++) {
						for (int t : tasks) {
							mapColToMatch.put(var++,
									new MatchPair(numWorker, t));
							switch (algorithm) {
							case LLEP:
								objectiveCoeff.add(computeCost(taskList
										.get(candidateTasks.get(t))));
								break;
							case NNP:
								objectiveCoeff.add(computeDistance(worker,
										taskList.get(candidateTasks.get(t))));
								break;
							}
							matchingCoeff.add(computeScore(worker,
									taskList.get(candidateTasks.get(t))));
						}
						// logicalWorkerToWorker.put(numWorker, w);
						numWorker++;
					}
				}
				w++;
			}

			BPMatchingCplex cplex = new BPMatchingCplex(numWorker,
					candidateTasks.size(), objectiveCoeff, matchingCoeff,
					totalScore, mapColToMatch);
			taskAssigned = cplex.maxMatchingMinCost();

			// recompute the maximum matching to make sure the cplex solver is
			// correct
			double _totalScore = 0;
			Iterator<MatchPair> it = taskAssigned.iterator();
			while (it.hasNext()) {
				MatchPair pair = it.next();
				Worker worker = workerList.get(logicalWorkerToWorker.get(pair
						.getW()));
				Task task = taskList.get(candidateTasks.get(pair.getT()));
				double score = computeScore(worker, task);
				totalDistance += computeDistance(worker, task);
				_totalScore += score;
				// if (score == Constants.EXACT_MATCH_SCORE)
				// totalTasksExactMatch++;
				// exact match?
				if (worker.isExactMatch(task))
					totalTasksExactMatch++;
			}
			totalTasksAssigned = taskAssigned.size();

			if (_totalScore != totalScore)
				System.out
						.println("Error: mismatch between Hungarian & Cplex; total score: "
								+ totalScore + " .vs " + _totalScore);
		}

		// the solved task from task list
		ArrayList<Integer> solvedTasks = new ArrayList(); // indices
															// in
															// candidateTasks

		if (algorithm == AlgoEnums.GR) {
			// remove the assigned task
			for (int i = r.length - 1; i >= 0; i--) {
				if (origin[i][r[i]] > 0) {
					// the task i is assigned
					totalTasksAssigned++;

					// exact match?
					// if (origin[i][r[i]] == Constants.EXACT_MATCH_SCORE)
					// totalTasksExactMatch++;

					if (isTranpose) {
						solvedTasks.add(candidateTasks.get(i));
						Worker worker = workerList.get(logicalWorkerToWorker
								.get(r[i]));
						Task task = taskList.get(candidateTasks.get(i));
						totalDistance += computeDistance(worker, task);
						// exact match?
						if (worker.isExactMatch(task))
							totalTasksExactMatch++;
					} else {
						solvedTasks.add(candidateTasks.get(r[i]));
						Worker worker = workerList.get(logicalWorkerToWorker
								.get(i));
						Task task = taskList.get(candidateTasks.get(r[i]));
						totalDistance += computeDistance(worker, task);
						// exact match?
						if (worker.isExactMatch(task))
							totalTasksExactMatch++;
					}
				}
			}
			// remove the assigned task
			Collections.sort(solvedTasks);// to remove the last task first
			for (int i = solvedTasks.size() - 1; i >= 0; i--) {
				taskList.remove((int) solvedTasks.get(i));
			}
		} else if (algorithm == AlgoEnums.LLEP || algorithm == AlgoEnums.NNP) {
			if (taskAssigned != null) {
				for (int i = 0; i < taskAssigned.size(); i++) {
					MatchPair pair = taskAssigned.get(i);
					solvedTasks.add(candidateTasks.get(pair.getT()));
				}
				Collections.sort(solvedTasks);
				for (int i = solvedTasks.size() - 1; i >= 0; i--) {
					taskList.remove((int) solvedTasks.get(i));
				}
			}
		}

		TotalScore += totalScore;
		TotalTasksAssigned += totalTasksAssigned;
		TotalTasksExactMatch += totalTasksExactMatch;
		TotalTravelDistance += totalDistance;

		System.out.printf("Maximum score: %.2f\n", totalScore);
		System.out.println("#Assigned tasks: " + totalTasksAssigned);
		System.out.println("#Exact assigned tasks: " + totalTasksExactMatch);
		System.out.println("#Travel distance: " + totalDistance);

		// check correctness
		if (totalExpiredTask + taskList.size() + TotalTasksAssigned != TaskCount) {
			System.out.println("Logic error!!!");
			System.out.println("#Expired tasks: " + totalExpiredTask);
			System.out.println("#Remained tasks: " + taskList.size());
			System.out.println("#Assigned tasks: " + taskList.size());
			System.out.println("#Task count: " + TaskCount);
		}

		return totalScore;
	}

	/**
	 * Euclidean distance between worker and task
	 * 
	 * @param worker
	 * @param task
	 * @return
	 */
	private double computeDistance(Worker worker, Task task) {
		if (DATA_SET == 0 || DATA_SET == 4 || DATA_SET == 1)
			return Utils.computeDistance(worker, task);
		double distance = Math.sqrt((worker.getLatitude() - task.getLat())
				* (worker.getLatitude() - task.getLat())
				+ (worker.getLongitude() - task.getLng())
				* (worker.getLongitude() - task.getLng()));
		return distance;
	}

	// compute score of a tuple <w,t>
	private double computeScore(Worker w, Task t) {
		if (w.isExactMatch(t))
			return Constants.EXACT_MATCH_SCORE;
		else
			return Constants.NONEXACT_MATCH_SCORE;
	}

	private double computeCost(Task t) {
		int row = getRowIdx(t.getLat());
		int col = getColIdx(t.getLng());
		// System.out.println(row + " " + col);
		double entropy = 0;
		if (entropies.containsKey(row)) {
			HashMap h = entropies.get(row);
			Iterator it = h.keySet().iterator();
			// while (it.hasNext()) {
			// Integer key = (Integer) it.next();
			//
			// System.out.println(key + " " + col);
			// }
			if (entropies.get(row).containsKey(col)) {
				// System.out.println(row + " !!!!!!!  " + col);
				entropy = entropies.get(row).get(col);
			}
		}
		// System.out.println(score / (1.0 + entropy));
		return entropy;
	}

	/**
	 * Compute average number of spatial task which are inside the spatial
	 * region of a given worker. This method computes both avgTW and varTW;
	 */
	public void computeAverageTaskPerWorker() {
		double totalNoTasks = 0;
		double sum_sqr = 0;
		for (ArrayList T : _container) {
			if (T != null) {
				int size = T.size();
				totalNoTasks += size;
				sum_sqr += size * size;
			}
		}
		avgTW = totalNoTasks / _container.size();
		varTW = (sum_sqr - ((totalNoTasks * totalNoTasks) / _container.size()))
				/ (_container.size());
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
			Task task = taskList.get(t);
			ArrayList W = invertedTable.get(t);
			int size = W.size();
			totalNoTasks += size;
			sum_sqr += size * size;
		}

		avgWT = totalNoTasks / taskList.size();
		varWT = (sum_sqr - ((totalNoTasks * totalNoTasks) / taskList.size()))
				/ (taskList.size());
	}

}
