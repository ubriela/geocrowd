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
import org.geocrowd.common.Cell;
import org.geocrowd.common.GenericTask;
import org.geocrowd.common.GenericWorker;
import org.geocrowd.common.MBR;
import org.geocrowd.common.MatchPair;
import org.geocrowd.common.SpecializedTask;
import org.geocrowd.common.SpecializedWorker;
import org.geocrowd.common.entropy.Coord;
import org.geocrowd.common.entropy.EntropyRecord;
import org.geocrowd.matching.Hungarian;
import org.geocrowd.matching.Utility;
import org.geocrowd.matching.online.OnlineBipartiteMatching;
import org.geocrowd.util.Constants;
import org.geocrowd.util.Utils;

import cplex.BPMatchingCplex;

/**
 * 
 * @author Leyla
 */
public class Geocrowd extends GenericCrowd {
	public double minLatitude = Double.MAX_VALUE;
	public double maxLatitude = -Double.MAX_VALUE;
	public double minLongitude = Double.MAX_VALUE;
	public double maxLongitude = -Double.MAX_VALUE;


	public int workerNo = 100; // 100 // number of workers when workers are to
								// be generated #
	public Cell[][] grid;
	public HashMap<Integer, HashMap<Integer, Double>> entropies = null;

	public double maxEntropy = 0;
	public int rowCount = 0; // number of rows for the grid
	public int colCount = 0; // number of cols for the grid

	public ArrayList<EntropyRecord> entropyList = new ArrayList();
	public int sumMaxT = 0;
	public double TotalScore = 0;
	public int TotalTasksExpertiseMatch = 0; // number of assigned tasks, from exact
											// match
	public int sumEntropy = 0;
	public int taskExpiredNo = 0;


	public ArrayList<double[]> allTasks = new ArrayList();

	public double resolution = 0;

	public Geocrowd() {
		String boundaryFile = "";
		switch (DATA_SET) {
			case GOWALLA:
				boundaryFile = Constants.gowallaBoundary;
				break;
			case SKEWED:
				boundaryFile = Constants.skewedBoundary;
				break;
			case UNIFORM:
				boundaryFile = Constants.uniBoundary;
				break;
			case SMALL:
				boundaryFile = Constants.smallBoundary;
				break;
			case YELP:
				boundaryFile = Constants.yelpBoundary;
				break;
		}

		PreProcess prep = new PreProcess();
		prep.DATA_SET = DATA_SET;
		prep.readBoundary(prep.DATA_SET);
		minLatitude = prep.minLat;
		maxLatitude = prep.maxLat;
		minLongitude = prep.minLng;
		maxLongitude = prep.maxLng;
	}

	public void printBoundaries() {
		System.out.println("minLat:" + minLatitude + "   maxLat:" + maxLatitude
				+ "   minLng:" + minLongitude + "   maxLng:" + maxLongitude);
	}

	public void createGrid() {
		resolution = 0;
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
		case SMALL:
			resolution = Constants.smallResolution;
			break;
		case YELP:
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
		case GOWALLA:
			filePath = Constants.gowallaLocationEntropyFileName;
			break;
		case SKEWED:
			filePath = Constants.skewedLocationDensityFileName;
			break;
		case UNIFORM:
			filePath = Constants.uniLocationDensityFileName;
			break;
		case SMALL:
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
				EntropyRecord dR = new EntropyRecord(entropy, new Coord(row, col));
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
	 * for a given row , converts it back to latitude
	 * @param row
	 * @return
	 */
	public double rowToLat(int row) { 
		return (((double) row) * resolution) + minLatitude;
	}

	/**
	 * for a given col , converts it back to longitude
	 * @param col
	 * @return
	 */
	public double colToLng(int col) {
		return (((double) col) * resolution) + minLongitude;
	}

	public int latToRowIdx(double lat) {
		return (int) ((lat - minLatitude) / resolution);
	}

	public int lngToColIdx(double lng) {
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
	 * Read workers from file
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

				SpecializedWorker w = new SpecializedWorker(userId, lat, lng, maxT, mbr);

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
	 * Read tasks from file
	 * @param fileName
	 */
	public void readTasks(String fileName) {
		int listCount = taskList.size();
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
				SpecializedTask t = new SpecializedTask(lat, lng, time, entropy, type);
				taskList.add(listCount, t);
				listCount++;
				TaskCount++;
			}
			in.close();
		} catch (Exception e) {
		}
	}

	/**
	 * Get all the task (lat/lon only) from gowalla file
	 */
	public void readTaskLocations() {
		try {
			allTasks = new ArrayList();
			HashMap HashMap = new HashMap();
			FileReader reader = new FileReader(Constants.gowallaFileName_CA);
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
				int row = dR.getCoord().getRowId();
				double startLat = rowToLat(row);
				double endLat = rowToLat(row + 1);
				double lat = UniformGenerator.randomValue(new Range(startLat,
						endLat), false);
				int col = dR.getCoord().getColId();
				double startLng = colToLng(col);
				double endLng = colToLng(col + 1);
				double lng = UniformGenerator.randomValue(new Range(startLng,
						endLng), false);
				double entropy = dR.getEntropy();
				int time = time_instance;
				int taskType = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				SpecializedTask t = new SpecializedTask(lat, lng, time, entropy, taskType);
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
				int time = time_instance;
				int taskType = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				SpecializedTask t = new SpecializedTask(lat, lng, time, -1, taskType);
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
				int time = time_instance;
				int taskType = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				SpecializedTask t = new SpecializedTask(lat, lng, time, -1, taskType);
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
				int row = dR.getCoord().getRowId();
				double startLat = rowToLat(row);
				double endLat = rowToLat(row + 1);
				double lat = UniformGenerator.randomValue(new Range(startLat,
						endLat), false);
				int col = dR.getCoord().getColId();
				double startLng = colToLng(col);
				double endLng = colToLng(col + 1);
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
				SpecializedWorker w = new SpecializedWorker("dump", lat, lng, maxT, mbr);
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
				SpecializedWorker w = new SpecializedWorker(userId, lat, lng, maxT, mbr);
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
		candidateTasks = new ArrayList();
		
		container2 = new ArrayList[workerList.size()];

		// remove expired task from tasklist
		pruneExpiredTasks();

		for (int idx = 0; idx < workerList.size(); idx++) {
			SpecializedWorker w = (SpecializedWorker) workerList.get(idx);
			rangeQuery(idx, w.getMBR());
		}

		// remove workers with no tasks
		sumMaxT = 0;
		container = new ArrayList<ArrayList>();
		for (int i = container2.length - 1; i >= 0; i--) {
			if (container2[i] == null || container2[i].size() == 0) {
				workerList.remove(i);
			} else
				sumMaxT += workerList.get(i).getMaxTaskNo();
		}
		for (int i = 0; i < container2.length; i++) {
			if (container2[i] != null && container2[i].size() > 0) {
				container.add(container2[i]);
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
			SpecializedTask task = (SpecializedTask) taskList.get(i);

			// tick expired task
			if ((time_instance - task.getEntryTime()) >= Constants.TaskDuration) {
				task.setExpired();
			} else

			// if the task is not assigned and in the worker's working region
			// AND not assigned
			if (task.isCoveredBy(mbr)) {
				if (container2[workerIdx] == null) {
					container2[workerIdx] = new ArrayList();
				}

				if (taskSet == null)
					taskSet = new HashSet<Integer>();
				
				if (!taskSet.contains(t)) {
					candidateTasks.add(t);
					taskSet.add(t);
				}
				container2[workerIdx].add(candidateTasks.indexOf(t));

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


	// this methods compute max weighted matching using the second Hungarian
	// algorithm with heuristics, thus faster than the other one
	// assuming maxT = 1 for all workers
	public double maxWeightedMatching2() {
		double[][] array = new double[container.size()][taskList.size()]; // row
																			// represents
																			// workers,
																			// column
																			// represents
																			// tasks
		for (int i = 0; i < container.size(); i++) {
			ArrayList<Integer> tasks = container.get(i);
			if (tasks != null)
				for (int j : tasks) {
					array[i][j] = computeScore((SpecializedWorker)workerList.get(i),
							(SpecializedTask)taskList.get(j));
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
		Hungarian HA = new Hungarian(array);
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
				if (origin[i][r[i]] == Constants.EXPERTISE_MATCH_SCORE)
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
		TotalTasksExpertiseMatch += totalTasksExactMatch;
		return sum;
	}

	// any number of maxT
	public double maxWeightedMatching() {
		
		if (algorithm == AlgorithmEnum.ONLINE)
			return onlineMatching();
					
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
		for (int i = 0; i < container.size(); i++) {
			ArrayList<Integer> tasks = container.get(i);
			if (tasks != null)
				for (int j : tasks) {
					array[row][j] = computeScore((SpecializedWorker)workerList.get(i),
							(SpecializedTask)taskList.get(candidateTasks.get(j)));
				}
			logicalWorkerToWorker.put(row, i);
			row++;
			// create logical workers
			SpecializedWorker w = (SpecializedWorker) workerList.get(i);
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

		Hungarian HA = new Hungarian(array);
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

		if (algorithm == AlgorithmEnum.LLEP || algorithm == AlgorithmEnum.NNP) {
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
			for (ArrayList<Integer> tasks : container) {
				SpecializedWorker worker = (SpecializedWorker) workerList.get(w);
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
								objectiveCoeff.add(distanceWorkerTask(worker,
										taskList.get(candidateTasks.get(t))));
								break;
							}
							matchingCoeff.add(computeScore((SpecializedWorker)worker,
									(SpecializedTask)taskList.get(candidateTasks.get(t))));
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
				SpecializedWorker worker = (SpecializedWorker) workerList.get(logicalWorkerToWorker.get(pair
						.getW()));
				SpecializedTask task = (SpecializedTask) taskList.get(candidateTasks.get(pair.getT()));
				double score = computeScore(worker, task);
				totalDistance += distanceWorkerTask(worker, task);
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

		if (algorithm == AlgorithmEnum.BASIC) {
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
						SpecializedWorker worker = (SpecializedWorker) workerList.get(logicalWorkerToWorker
								.get(r[i]));
						SpecializedTask task = (SpecializedTask) taskList.get(candidateTasks.get(i));
						totalDistance += distanceWorkerTask(worker, task);
						// exact match?
						if (worker.isExactMatch(task))
							totalTasksExactMatch++;
					} else {
						solvedTasks.add(candidateTasks.get(r[i]));
						SpecializedWorker worker = (SpecializedWorker) workerList.get(logicalWorkerToWorker
								.get(i));
						SpecializedTask task = (SpecializedTask) taskList.get(candidateTasks.get(r[i]));
						totalDistance += distanceWorkerTask(worker, task);
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
		} else if (algorithm == AlgorithmEnum.LLEP || algorithm == AlgorithmEnum.NNP) {
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
		TotalTasksExpertiseMatch += totalTasksExactMatch;
		TotalTravelDistance += totalDistance;

		System.out.printf("Maximum score: %.2f\n", totalScore);
		System.out.println("#Assigned tasks: " + totalTasksAssigned);
		System.out.println("#Exact assigned tasks: " + totalTasksExactMatch);
		System.out.println("#Travel distance: " + totalDistance);

		// check correctness
		if (TotalExpiredTask + taskList.size() + TotalTasksAssigned != TaskCount) {
			System.out.println("Logic error!!!");
			System.out.println("#Expired tasks: " + TotalExpiredTask);
			System.out.println("#Remained tasks: " + taskList.size());
			System.out.println("#Assigned tasks: " + taskList.size());
			System.out.println("#Task count: " + TaskCount);
		}

		return totalScore;
	}

	/**
	 * 
	 * @return
	 */
	public double onlineMatching() {
		// replicates workers based on their maxT
		
		// apply online bipartite matching
		ArrayList<Integer> workers = new ArrayList<>();
		
		Iterator it = container.iterator();
		int id = 0;
		while (it.hasNext()) {
			workers.add(new Integer(id));
			id ++;
		}
		
		OnlineBipartiteMatching obm = new OnlineBipartiteMatching(workers);
		
		int assignedTasks = obm.onlineMatching(container);
		System.out.println(assignedTasks);
		
		return assignedTasks;
	}

	// compute score of a tuple <w,t>
	private double computeScore(SpecializedWorker w, SpecializedTask t) {
		if (w.isExactMatch(t))
			return Constants.EXPERTISE_MATCH_SCORE;
		else
			return Constants.NON_EXPERTISE_MATCH_SCORE;
	}

	private double computeCost(GenericTask t) {
		int row = latToRowIdx(t.getLat());
		int col = lngToColIdx(t.getLng());
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

}
