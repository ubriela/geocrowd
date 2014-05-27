/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datasets.gowalla;

import java.io.*;
import java.util.ArrayList;
import java.sql.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.geocrowd.Constants;
import org.geocrowd.Coord;
import org.geocrowd.Expertise;
import org.geocrowd.MBR;
import org.geocrowd.Observation;
import org.geocrowd.Task;
import org.geocrowd.Utils;
import org.geocrowd.Worker;

/**
 * 
 * @author Leyla & Hien To
 */
public class PreProcess {
	public static double minLatitude = Double.MAX_VALUE;
	public static double maxLatitude = (-1) * Double.MAX_VALUE;
	public static double minLongitude = Double.MAX_VALUE;
	public static double maxLongitude = (-1) * Double.MAX_VALUE;

	public static int rowCount = 0; // number of rows for the grid
	public static int colCount = 0; // number of cols for the grid
	public static int timeCounter = 0; // works as the clock for task generation
	public double resolution = 0;
	public static int DATA_SET = 0;

	public PreProcess() {
	}

	/**
	 * Extract coordinate from datafile
	 * @param filename
	 */
	public void extractCoords(String filename) {
		try {
			FileReader reader = new FileReader(filename);
			BufferedReader in = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			int cnt = 0;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\\s");
				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);
				sb.append(lat + "\t" + lng + "\n");
				cnt++;
			}

			FileWriter writer = new FileWriter(filename + ".dat");
			BufferedWriter out = new BufferedWriter(writer);
			out.write(sb.toString());
			out.close();

			System.out.println("Number of checkins: " + cnt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Extract MBR of the workers from datafile
	 * Export recent location of each user to a file
	 * @param filename
	 */
	public void extractMBRs(String filename) {
		try {
			FileReader reader = new FileReader(filename);
			BufferedReader in = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			HashMap<Integer, ArrayList<Point>> data = new HashMap<Integer, ArrayList<Point>>();
			ArrayList<Point> points = new ArrayList<Point>();
			Integer prev_id = -1;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\\s");
				Integer id = Integer.parseInt(parts[0]);
				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);
				if (id.equals(prev_id)) {	// add to current list
					points.add(new Point(lat, lng));
				} else {
					// create new list
					points = new ArrayList<Point>();
					points.add(new Point(lat, lng));
					
					// add current list to data
					data.put(prev_id, points);
					
					sb.append(lat + "\t" + lng + "\n");
				}
				
				prev_id = id;
			}
			data.put(prev_id, points);
			
			FileWriter writer = new FileWriter(filename + ".dat");
			BufferedWriter out = new BufferedWriter(writer);
			out.write(sb.toString());
			out.close();
			sb.delete(0,  sb.length());
			

			// iterate through HashMap keys Enumeration
			double sum = 0;
			int count = 0;
			double maxMBR = 0;
			Iterator<Integer> it = data.keySet().iterator();
			while (it.hasNext()) {
				Integer t = (Integer) it.next();
				ArrayList<Point> pts = data.get(t);
				MBR mbr = Utils.computeMBR(pts);
				double d = mbr.diagonalLength();
				sum += d;
				count ++;
				if (d > maxMBR)
					maxMBR = d;
				double mcd = Utils.MCD(pts.get(0), pts);
				sb.append(t.toString() + "\t" + mbr.getMinLat() + "\t" + mbr.getMinLng() + "\t" + mbr.getMaxLat() + "\t" + mbr.getMaxLng() + "\t" + d + "\t" + mcd +  "\n");
			}
			
			writer = new FileWriter(filename + ".mbr.txt");
			out = new BufferedWriter(writer);
			out.write(sb.toString());
			out.close();

			System.out.println("Number of users: " + data.keySet().size());
			System.out.println("Average users' MBR size: " + sum / count);
			System.out.println("Max users' MBR size: " + maxMBR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Giving a set of points, compute the MBR covering all the points
	 */
	public void computeBoundary(String datafile) {
		switch (DATA_SET) {
		case 0:// real
			try {
				FileReader reader = new FileReader(datafile);
				BufferedReader in = new BufferedReader(reader);
				int cnt = 0;
				while (in.ready()) {
					String line = in.readLine();
					String[] parts = line.split("\\s");
					Double lat = Double.parseDouble(parts[2]);
					Double lng = Double.parseDouble(parts[3]);

					if (lat < minLatitude)
						minLatitude = lat;
					if (lat > maxLatitude)
						maxLatitude = lat;
					if (lng < minLongitude)
						minLongitude = lng;
					if (lng > maxLongitude)
						maxLongitude = lng;
					cnt++;
				}

				FileWriter writer = new FileWriter(datafile + "_boundary.txt");
				BufferedWriter out = new BufferedWriter(writer);
				out.write(minLatitude + " " + minLongitude + " " + maxLatitude
						+ " " + maxLongitude);
				out.close();

				System.out.println("Boundary [minLat:" + minLatitude
						+ "   maxLat:" + maxLatitude + "   minLng:"
						+ minLongitude + "   maxLng:" + maxLongitude + "]");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case 1:// sync
			int cnt = 0;
			for (int i = 0; i < Constants.RoundCnt; i++) {
				try {
					FileReader reader = new FileReader(
							Constants.skewedMatlabWorkerFilePath + i + ".txt");
					BufferedReader in = new BufferedReader(reader);
					while (in.ready()) {
						String line = in.readLine();
						String[] parts = line.split(",");
						Double lat = Double.parseDouble(parts[0]);
						Double lng = Double.parseDouble(parts[1]);

						if (lat < minLatitude)
							minLatitude = lat;
						if (lat > maxLatitude)
							maxLatitude = lat;
						if (lng < minLongitude)
							minLongitude = lng;
						if (lng > maxLongitude)
							maxLongitude = lng;
						cnt++;
					}

					FileWriter writer = new FileWriter(Constants.skewedBoundary);
					BufferedWriter out = new BufferedWriter(writer);
					out.write(minLatitude + " " + minLongitude + " "
							+ maxLatitude + " " + maxLongitude);
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Boundary [minLat:" + minLatitude + "   maxLat:"
					+ maxLatitude + "   minLng:" + minLongitude + "   maxLng:"
					+ maxLongitude + "]");
			break;

		case 2:// uniform
			cnt = 0;
			for (int i = 0; i < Constants.RoundCnt; i++) {
				try {
					FileReader reader = new FileReader(
							Constants.uniMatlabWorkerFilePath + i + ".txt");
					BufferedReader in = new BufferedReader(reader);
					while (in.ready()) {
						String line = in.readLine();
						String[] parts = line.split(",");
						Double lat = Double.parseDouble(parts[0]);
						Double lng = Double.parseDouble(parts[1]);

						if (lat < minLatitude)
							minLatitude = lat;
						if (lat > maxLatitude)
							maxLatitude = lat;
						if (lng < minLongitude)
							minLongitude = lng;
						if (lng > maxLongitude)
							maxLongitude = lng;
						cnt++;
					}

					FileWriter writer = new FileWriter(Constants.uniBoundary);
					BufferedWriter out = new BufferedWriter(writer);
					out.write(minLatitude + " " + minLongitude + " "
							+ maxLatitude + " " + maxLongitude);
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Boundary [minLat:" + minLatitude + "   maxLat:"
					+ maxLatitude + "   minLng:" + minLongitude + "   maxLng:"
					+ maxLongitude + "]");
			break;
		}
		
		MBR mbr = new MBR(minLatitude, minLongitude, maxLatitude, maxLongitude);
		double x = Utils.distance(minLatitude, minLongitude, maxLatitude, minLongitude);
		double y = Utils.distance(minLatitude, minLongitude, minLatitude, maxLongitude);
		System.out.println("Area: " + x*y);
		System.out.println("Region MBR size: " + mbr.diagonalLength());

	}

	/**
	 * Read boundary from file
	 */
	public void readBoundary(int dataset) {
		String boundaryFile = "";
		switch (dataset) {
		case 0:
			boundaryFile = Constants.gowallaBoundary;
			break;
		case 1:
			boundaryFile = Constants.skewedBoundary;
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
		try {
			FileReader reader = new FileReader(boundaryFile);
			BufferedReader in = new BufferedReader(reader);
			if (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split(" ");
				minLatitude = Double.valueOf(parts[0]);
				minLongitude = Double.valueOf(parts[1]);
				maxLatitude = Double.valueOf(parts[2]);
				maxLongitude = Double.valueOf(parts[3]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get subset of the gowalla dataset, within a rectangle
	 */
	public void filterInput(String filename, double min_x, double min_y, double max_x, double max_y) {
		System.out.println("Filtering location data...");
		try {
			FileReader reader = new FileReader(Constants.gowallaFileName);
			BufferedReader in = new BufferedReader(reader);
			FileWriter writer = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(writer);

			int cnt = 0;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\t");
				Integer userID = Integer.parseInt(parts[0]);
				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);
				Integer pointID = Integer.parseInt(parts[4]);
				// 114° 8' W to 124° 24' W
				// Latitude: 32° 30' N to 42° N

				if ((lat < min_x) || (lat > max_x) || (lng < (min_y) || (lng > (max_y))))
					continue;
				
				out.write(line + "\n");

				cnt++;
			}
			out.close();
		
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * compute grid granularity
	 * @param dataset
	 */
	public void createGrid(int dataset) {
		resolution = 0;
		switch (dataset) {
		case 0:
			resolution = Constants.gowallaResolution;
			break;
		case 1:
			resolution = Constants.skewedResolution;
			break;
		case 2:
			resolution = Constants.uniResolution;
			break;
		case 3:
			resolution = Constants.smallResolution;
		case 4:
			resolution = Constants.yelpResolution;
		}
		rowCount = (int) ((maxLatitude - minLatitude) / resolution);
		colCount = (int) ((maxLongitude - minLongitude) / resolution);
		System.out
				.println("rowcount: " + rowCount + "    colCount:" + colCount);
	}

	/**
	 * Read data, e.g., gowalla file
	 * 
	 * @return a hashtable <location id, occurrences>
	 */
	public Hashtable<Integer, ArrayList<Observation>> readRealEntropyData(String datasetfile) {
		Hashtable hashTable = new Hashtable();
		try {
			FileReader reader = new FileReader(datasetfile);
			BufferedReader in = new BufferedReader(reader);
			int cnt = 0;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\\s");
				Integer userID = Integer.parseInt(parts[0]);
				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);
				Integer pointID = Integer.parseInt(parts[4]);
				if (!hashTable.containsKey(pointID)) {
					ArrayList<Observation> obs = new ArrayList<Observation>();
					Observation o = new Observation(userID);
					obs.add(o);
					hashTable.put(pointID, obs);
				} else {
					ArrayList<Observation> u = (ArrayList<Observation>) hashTable
							.get(pointID);
					boolean found = false;
					for (Observation o : u) {
						if (o.getUserId() == userID) {
							o.incObserveCount();
							found = true;
							break;
						}
					}
					if (!found) {
						Observation o = new Observation(userID);
						u.add(o);
					}
				}
				cnt++;
			}
			System.out.println("Hashtable <location, occurrences> size: "
					+ hashTable.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashTable;
	}

	/**
	 * Assuming Gowalla users are the workers, we assume all users who checked
	 * in during the day as available workers for that. The method returns a
	 * hashtable <day, workers>
	 * 
	 * This function is used as an input for saveWorkers()
	 * @param datasetfile
	 * @return
	 */
	public Hashtable<Date, ArrayList<Worker>> generateRealWorkers(String datasetfile) {
		Hashtable<Date, ArrayList<Worker>> hashTable = new Hashtable();
		try {
			FileReader reader = new FileReader(Constants.gowallaFileName_CA);
			BufferedReader in = new BufferedReader(reader);
			int cnt = 0;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\t");
				String userID = parts[0];
				String[] DateTimeStr = parts[1].split("T");
				Date date = Date.valueOf(DateTimeStr[0]);
				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);

				// init MBR of each worker
				MBR mbr = new MBR(lat - 2 * resolution, lng - 2 * resolution,
						lat + 2 * resolution, lng + 2 * resolution);

				// make sure the MBR is within the boundary
				if (mbr.minLat < minLatitude)
					mbr.minLat = minLatitude;
				if (mbr.maxLat > maxLatitude)
					mbr.maxLat = maxLatitude;
				if (mbr.minLng < minLongitude)
					mbr.minLng = minLongitude;
				if (mbr.maxLng > maxLongitude)
					mbr.maxLng = maxLongitude;
				Integer pointID = Integer.parseInt(parts[4]);
				int exp = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				Worker w = new Worker(userID, lat, lng, mbr);
				w.addExpertise(exp);
				if (!hashTable.containsKey(date)) {
					ArrayList<Worker> workers = new ArrayList<Worker>();
					workers.add(w);
					hashTable.put(date, workers);
				} else {
					ArrayList<Worker> workers = hashTable.get(date);
					boolean found = false; // check if the worker is already in
											// the worker list, if yes -->
											// update his maxTass and R
					for (Worker o : workers) {
						if (o.getUserID() == userID) {
							o.incMaxTaskNo(); // set maxTask as the number of
												// check-ins

							// set working region R of each worker as MBR of his
							// check-ins locations
							if (lat < o.getMBR().minLat)
								o.setMinLat(lat);
							if (lat > o.getMBR().maxLat)
								o.setMaxLat(lat);
							if (lng < o.getMBR().minLng)
								o.setMinLng(lng);
							if (lng > o.getMBR().maxLng)
								o.setMaxLng(lng);

							found = true;
							break;
						}
					}
					if (!found) {
						workers.add(w);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashTable;
	}

	/**
	 * @param isConstantMBR
	 * @param isConstantMaxT
	 */
	public void generateSynWorkers(boolean isConstantMBR,
			boolean isConstantMaxT) {
		switch (DATA_SET) {
		case 1:
			for (int i = 0; i < Constants.RoundCnt; i++) {
				generateSyncWorkersFromMatlab(
						Constants.skewedWorkerFileNamePrefix + i + ".txt",
						Constants.skewedMatlabWorkerFilePath + i + ".txt",
						isConstantMBR, isConstantMaxT);
			}
			break;
		case 2:
			for (int i = 0; i < Constants.RoundCnt; i++) {
				generateSyncWorkersFromMatlab(Constants.uniWorkerFileNamePrefix
						+ i + ".txt", Constants.uniMatlabWorkerFilePath + i
						+ ".txt", isConstantMBR, isConstantMaxT);
			}
			break;
		}
	}

	/**
	 * Generate SYN dataset
	 * 
	 * @maxT is randomly generated
	 * @param fileName
	 *            : output
	 * @param matlabFile
	 *            : the workers are formed into four Gaussian clusters
	 */
	private void generateSyncWorkersFromMatlab(String fileName,
			String matlabFile, boolean isConstantMBR, boolean isConstantMaxT) {
		int maxSumTaskWorkers = 0;
		System.out.println("Workers:");
		double maxRangeX = (maxLatitude - minLatitude)
				* (Constants.MaxRangePerc);
		double maxRangeY = (maxLongitude - minLongitude)
				* Constants.MaxRangePerc;
		try {
			FileWriter writer = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(writer);
			FileReader reader = new FileReader(matlabFile);
			BufferedReader in = new BufferedReader(reader);
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split(",");
				double lat = Double.parseDouble(parts[0]);
				double lng = Double.parseDouble(parts[1]);
				int maxT = 0;
				if (isConstantMaxT)
					maxT = Constants.MaxTasksPerWorker;
				else
					maxT = (int) UniformGenerator.randomValue(new Range(0,
							Constants.MaxTasksPerWorker), true) + 1;
				maxSumTaskWorkers += maxT;
				double rangeX = 0;
				double rangeY = 0;
				if (isConstantMBR) {
					rangeX = maxRangeX;
					rangeY = maxRangeY;
				} else {
					rangeX = UniformGenerator.randomValue(new Range(0,
							maxRangeX), false);
					rangeY = UniformGenerator.randomValue(new Range(0,
							maxRangeY), false);
				}
				MBR mbr = MBR.createMBR(lat, lng, rangeX, rangeY);
				checkBoundaryMBR(mbr);
				int exp = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				Worker w = new Worker(lat, lng, maxT, mbr);
				w.addExpertise(exp);
				out.write(-1 + "," + lat + "," + lng + "," + maxT
						+ "," + "[" + mbr.getMinLat() + "," + mbr.getMinLng()
						+ "," + mbr.getMaxLat() + "," + mbr.getMaxLng() + "],["
						+ exp + "]\n");
			}
			out.close();
		} catch (Exception e) {
		}
		System.out.println("Sum of all maxTask:" + maxSumTaskWorkers);
	}

	public void generateSynTasks() {
		timeCounter = 0;
		String outputFileFrefix = "";
		switch (DATA_SET) {
		case 1:
			outputFileFrefix = Constants.skewedTaskFileNamePrefix;
			break;
		case 2:
			outputFileFrefix = Constants.uniTaskFileNamePrefix;
			break;
		}
		for (int i = 0; i < Constants.RoundCnt; i++) {
			generateSyncTasksFromMatlab(outputFileFrefix + i + ".txt",
					Constants.matlabTaskFilePath + i + ".txt");
			timeCounter++;
		}
	}

	/**
	 * Generate SYN dataset
	 * 
	 * @param fileName
	 *            : output
	 * @param matlabFile
	 *            : distributing tasks into four Gaussian clusters
	 */
	private void generateSyncTasksFromMatlab(String fileName, String matlabFile) {
		System.out.println("Tasks:");
		int countTask = 0;
		try {
			FileWriter writer = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(writer);
			FileReader reader = new FileReader(matlabFile);
			BufferedReader in = new BufferedReader(reader);
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split(",");
				double lat = Double.parseDouble(parts[0]);
				double lng = Double.parseDouble(parts[1]);
				int time = timeCounter;
				int taskType = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				Task t = new Task(lat, lng, time, -1, taskType);
				out.write(lat + "," + lng + "," + time + "," + -1 + ","
						+ taskType + "\n");
				countTask++;
			}
			out.close();
		} catch (Exception e) {
		}
		System.out.println(countTask + " tasks generated");
	}

	/**
	 * Used as an input for saveLocationEntropy
	 * 
	 * @return which location belongs to which grid cell
	 * 
	 */
	public Hashtable<Integer, Coord> locIdToCellIndices() {
		Hashtable hashTable = new Hashtable();
		try {
			FileReader reader = new FileReader(Constants.gowallaFileName_CA);
			BufferedReader in = new BufferedReader(reader);
			int cnt = 0;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\\s");
				Integer userID = Integer.parseInt(parts[0]);
				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);
				Integer pointID = Integer.parseInt(parts[4]);
				int row = getRowIdx(lat);
				int col = getColIdx(lng);
				Coord g = new Coord(row, col);
				if (!hashTable.containsKey(pointID)) {
					hashTable.put(pointID, g);
				}

				cnt++;
			}
			System.out.println("Hashtable<location, grid> size: " + hashTable.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashTable;
	}

	/**
	 * Compute location entropy for each location and save into a file
	 * 
	 * @param hashTable
	 *            a list of locations with corresponding entropy
	 */
	public void computeLocationEntropy(
			Hashtable<Integer, ArrayList<Observation>> hashTable) {
		try {
			FileWriter writer = new FileWriter(Constants.gowallaEntropyFileName);
			BufferedWriter out = new BufferedWriter(writer);
			Set<Integer> set = hashTable.keySet();

			Iterator<Integer> itr = set.iterator();
			while (itr.hasNext()) {
				int pointId = itr.next();
				ArrayList<Observation> obs = hashTable.get(pointId);
				int totalObservation = 0;
				double entropy = 0;
				for (Observation o : obs) {
					totalObservation += o.getObservationCount();
				}
				for (Observation o : obs) {
					int observeCount = o.getObservationCount();
					double p = (double) observeCount / totalObservation;
					entropy -= p * Math.log(p) / Math.log(2);
				}
				out.write(pointId + "," + entropy + "\n");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save what location in what grid and its corresponding location entropy,
	 * the entropy information is get from a file which was generated before
	 * 
	 * @param hashTable
	 */
	public void saveLocationEntropy(Hashtable<Integer, Coord> hashTable) {
		try {
			FileReader reader = new FileReader(Constants.gowallaEntropyFileName);
			BufferedReader in = new BufferedReader(reader);

			FileWriter writer = new FileWriter(
					Constants.gowallaLocationEntropyFileName);
			BufferedWriter out = new BufferedWriter(writer);

			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split(",");
				int pointId = Integer.parseInt(parts[0]);
				double entropy = Double.parseDouble(parts[1]);
				Coord g = hashTable.get(pointId);
				out.write(g.getRowId() + "," + g.getColId() + "," + entropy
						+ "\n");
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Hashtable<Integer, Hashtable<Integer, Integer>> computeSyncLocationDensity() {
		String matlabWorkerFilePath = "";
		switch (DATA_SET) {
		case 1:
			matlabWorkerFilePath = Constants.skewedMatlabWorkerFilePath;
			break;
		case 2:
			matlabWorkerFilePath = Constants.uniMatlabWorkerFilePath;
			break;
		case 3:
			matlabWorkerFilePath = Constants.smallWorkerFilePath;
		}

		Hashtable<Integer, Hashtable<Integer, Integer>> densities = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		int locId = 0;
		for (int i = 0; i < Constants.RoundCnt; i++) {
			try {
				FileReader file = new FileReader(matlabWorkerFilePath + i
						+ ".txt");
				BufferedReader in = new BufferedReader(file);
				while (in.ready()) {
					String line = in.readLine();
					String[] parts = line.split(",");
					Double lat = Double.parseDouble(parts[0]);
					Double lng = Double.parseDouble(parts[1]);
					int row = getRowIdx(lat);
					int col = getColIdx(lng);
					if (densities.containsKey(row)) {
						if (densities.get(row).containsKey(col))
							densities.get(row).put(col,
									densities.get(row).get(col) + 1);
						else
							densities.get(row).put(col, 1);
					} else {
						Hashtable<Integer, Integer> rows = new Hashtable<Integer, Integer>();
						rows.put(col, 1);
						densities.put(row, rows);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return densities;
	}

	public void saveSynLocationDensity(
			Hashtable<Integer, Hashtable<Integer, Integer>> densities) {
		String locationDensityFileName = "";
		switch (DATA_SET) {
		case 1:
			locationDensityFileName = Constants.skewedLocationDensityFileName;
			break;
		case 2:
			locationDensityFileName = Constants.uniLocationDensityFileName;
			break;
		case 3:
			locationDensityFileName = Constants.smallLocationDensityFileName;
			break;
		}

		try {
			FileWriter writer = new FileWriter(locationDensityFileName);
			BufferedWriter out = new BufferedWriter(writer);

			Iterator row_it = densities.keySet().iterator();
			while (row_it.hasNext()) {
				int row = (Integer) row_it.next();
				Iterator col_it = densities.get(row).keySet().iterator();
				while (col_it.hasNext()) {
					int col = (Integer) col_it.next();
					out.write(row + "," + col + ","
							+ densities.get(row).get(col) + "\n");
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveRealWorkers(Hashtable<Date, ArrayList<Worker>> hashTable) {
		try {
			Set<Date> set = hashTable.keySet();

			Iterator<Date> itr = set.iterator();
			Integer cnt = 0;
			while (itr.hasNext()) {
				FileWriter writer = new FileWriter(
						Constants.gowallaWorkerFileNamePrefix + cnt.toString()
								+ ".txt");
				BufferedWriter out = new BufferedWriter(writer);

				Date date = itr.next();
				ArrayList<Worker> workers = hashTable.get(date);
				for (Worker o : workers) {
					out.write(o.toStr() + "\n");
				}

				cnt++;
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getRowIdx(double lat) {
		return (int) ((lat - minLatitude) / resolution);
	}

	public int getColIdx(double lng) {
		return (int) ((lng - minLongitude) / resolution);
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
}