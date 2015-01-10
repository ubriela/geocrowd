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

import java.io.*;
import java.util.ArrayList;
import java.sql.Date;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.datasets.yelp.Constant;
import org.geocrowd.common.Constants;
import org.geocrowd.common.MBR;
import org.geocrowd.common.Point;
import org.geocrowd.common.PointTime;
import org.geocrowd.common.Range;
import org.geocrowd.common.UniformGenerator;
import org.geocrowd.common.Utils;
import org.geocrowd.common.crowdsource.SpecializedTask;
import org.geocrowd.common.crowdsource.SpecializedWorker;
import org.geocrowd.common.entropy.Coord;
import org.geocrowd.common.entropy.Observation;

/**
 * The Class PreProcess.
 * 
 * @author Leyla & Hien To
 */
public class PreProcess {

	/** The min lat. */
	public static double minLat = Double.MAX_VALUE;

	/** The max lat. */
	public static double maxLat = (-1) * Double.MAX_VALUE;

	/** The min lng. */
	public static double minLng = Double.MAX_VALUE;

	/** The max lng. */
	public static double maxLng = (-1) * Double.MAX_VALUE;

	/** The row count. */
	public static int rowCount = 0; // number of rows for the grid

	/** The col count. */
	public static int colCount = 0; // number of cols for the grid

	/** The time counter. */
	public static int timeCounter = 0; // works as the clock for task generation

	/** The resolution. */
	public double resolution = 0.00002;

	/** The data set. */
	public static DatasetEnum DATA_SET;

	/**
	 * Instantiates a new pre process.
	 */
	public PreProcess() {
	}

	/**
	 * Check boundary mbr.
	 * 
	 * @param mbr
	 *            the mbr
	 */
	private void checkBoundaryMBR(MBR mbr) {
		if (mbr.getMinLat() < minLat)
			mbr.setMinLat(minLat);
		if (mbr.getMaxLat() > maxLat)
			mbr.setMaxLat(maxLat);
		if (mbr.getMinLng() < minLng)
			mbr.setMinLng(minLng);
		if (mbr.getMaxLng() > maxLng)
			mbr.setMaxLng(maxLng);
	}

	/**
	 * Giving a set of points, compute the MBR covering all the points.
	 * 
	 * @param datafile
	 *            the datafile
	 */
	public void computeBoundary(String datafile) {
		switch (DATA_SET) {
		case GOWALLA:
			try {
				FileReader reader = new FileReader(datafile);
				BufferedReader in = new BufferedReader(reader);
				int cnt = 0;
				while (in.ready()) {
					String line = in.readLine();
					String[] parts = line.split("\\s");
					Double lat = Double.parseDouble(parts[2]);
					Double lng = Double.parseDouble(parts[3]);

					if (lat < minLat)
						minLat = lat;
					if (lat > maxLat)
						maxLat = lat;
					if (lng < minLng)
						minLng = lng;
					if (lng > maxLng)
						maxLng = lng;
					cnt++;
				}

				FileWriter writer = new FileWriter(Constants.gowallaBoundary);
				BufferedWriter out = new BufferedWriter(writer);
				out.write(minLat + " " + minLng + " " + maxLat + " " + maxLng);
				out.close();

				System.out.println("Boundary [minLat:" + minLat + "   maxLat:"
						+ maxLat + "   minLng:" + minLng + "   maxLng:"
						+ maxLng + "]");
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case SKEWED:
			int cnt = 0;
			for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
				try {
					FileReader reader = new FileReader(
							Constants.skewedMatlabWorkerFilePath + i + ".txt");
					BufferedReader in = new BufferedReader(reader);
					while (in.ready()) {
						String line = in.readLine();
						String[] parts = line.split(",");
						Double lat = Double.parseDouble(parts[0]);
						Double lng = Double.parseDouble(parts[1]);

						if (lat < minLat)
							minLat = lat;
						if (lat > maxLat)
							maxLat = lat;
						if (lng < minLng)
							minLng = lng;
						if (lng > maxLng)
							maxLng = lng;
						cnt++;
					}

					FileWriter writer = new FileWriter(Constants.skewedBoundary);
					BufferedWriter out = new BufferedWriter(writer);
					out.write(minLat + " " + minLng + " " + maxLat + " "
							+ maxLng);
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Boundary [minLat:" + minLat + "   maxLat:"
					+ maxLat + "   minLng:" + minLng + "   maxLng:" + maxLng
					+ "]");
			break;

		case UNIFORM:
			cnt = 0;
			for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
				try {
					FileReader reader = new FileReader(
							Constants.uniMatlabWorkerFilePath + i + ".txt");
					BufferedReader in = new BufferedReader(reader);
					while (in.ready()) {
						String line = in.readLine();
						String[] parts = line.split(",");
						Double lat = Double.parseDouble(parts[0]);
						Double lng = Double.parseDouble(parts[1]);

						if (lat < minLat)
							minLat = lat;
						if (lat > maxLat)
							maxLat = lat;
						if (lng < minLng)
							minLng = lng;
						if (lng > maxLng)
							maxLng = lng;
						cnt++;
					}

					FileWriter writer = new FileWriter(Constants.uniBoundary);
					BufferedWriter out = new BufferedWriter(writer);
					out.write(minLat + " " + minLng + " " + maxLat + " "
							+ maxLng);
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("Boundary [minLat:" + minLat + "   maxLat:"
					+ maxLat + "   minLng:" + minLng + "   maxLng:" + maxLng
					+ "]");
			break;
		}

		MBR mbr = new MBR(minLat, minLng, maxLat, maxLng);
		double x = Utils.distance(minLat, minLng, maxLat, minLng);
		double y = Utils.distance(minLat, minLng, minLat, maxLng);
		System.out.println("Area: " + x * y);
		System.out.println("Region MBR size: " + mbr.diagonalLength());

	}

	/**
	 * Compute location entropy for each location and save into a file.
	 * 
	 * @param hashTable
	 *            a list of locations with corresponding entropy
	 */
	public void computeLocationEntropy(
			Hashtable<Integer, ArrayList<Observation>> hashTable) {
		try {
			FileWriter writer = new FileWriter(Constants.gowallaEntropyFileName);
			BufferedWriter out = new BufferedWriter(writer);

			Iterator<Integer> itr = hashTable.keySet().iterator();
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

	private void computeRegionEntropy(
			Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> regionOccurances) {
		try {
			FileWriter writer = new FileWriter(Constants.gowallaEntropyFileName);
			BufferedWriter out = new BufferedWriter(writer);

			for (Integer row : regionOccurances.keySet()) {
				for (Integer col : regionOccurances.get(row).keySet()) {
					Hashtable<Integer, Integer> obs = regionOccurances.get(row)
							.get(col);
					int totalObservation = 0;
					double entropy = 0;
					for (Integer val : obs.values()) {
						totalObservation += val;
					}

					for (Integer val : obs.values()) {
						if (val != 0) {
							// System.out.println(totalObservation + " " + val);
							double p = (val + 0.0) / totalObservation;
							entropy -= p * Math.log(p) / Math.log(2);
						}
					}
					out.write(row + "," + col + "," + entropy + "\n");
				}
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Compute sync location density.
	 * 
	 * @return the hashtable
	 */
	public Hashtable<Integer, Hashtable<Integer, Integer>> computeSyncLocationDensity() {
		String workerFilePath = "";
		switch (DATA_SET) {
		case SKEWED:
			workerFilePath = Constants.skewedMatlabWorkerFilePath;
			break;
		case UNIFORM:
			workerFilePath = Constants.uniMatlabWorkerFilePath;
			break;
		case SMALL_TEST:
			workerFilePath = Constants.smallWorkerFilePath;
		}

		Hashtable<Integer, Hashtable<Integer, Integer>> densities = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			try {
				FileReader file = new FileReader(workerFilePath + i + ".txt");
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

	/**
	 * Compute sync location density.
	 * 
	 * @return the hashtable
	 */
	public Hashtable<Integer, Hashtable<Integer, Integer>> computeLocationDensity() {
		String filePath = "";
		switch (DATA_SET) {
		case GOWALLA:
			filePath = Constants.gowallaFileName_CA_loc;
			break;
		}

		Hashtable<Integer, Hashtable<Integer, Integer>> densities = new Hashtable<Integer, Hashtable<Integer, Integer>>();
		try {
			FileReader file = new FileReader(filePath);
			BufferedReader in = new BufferedReader(file);
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\t");
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
		return densities;
	}

	/**
	 * compute grid granularity.
	 * 
	 * @param dataset
	 *            the dataset
	 */
	public void createGrid(DatasetEnum dataset) {
		switch (dataset) {
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
		rowCount = colCount = (int) (1.0 / resolution);
		System.out
				.println("rowcount: " + rowCount + "    colCount:" + colCount);
	}

	/**
	 * Extract coordinate from datafile.
	 * 
	 * @param filename
	 *            the filename
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
	 * Extract MBR of the workers from datafile Export recent location of each
	 * user to a file.
	 * 
	 * @param filename
	 *            the filename
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
				if (id.equals(prev_id)) { // add to current list
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
			sb.delete(0, sb.length());

			// iterate through HashMap keys Enumeration
			double sum = 0;
			int count = 0;
			double maxMBR = 0;
			Iterator<Integer> it = data.keySet().iterator();
			while (it.hasNext()) {
				Integer t = it.next();
				ArrayList<Point> pts = data.get(t);
				MBR mbr = MBR.computeMBR(pts);
				double d = mbr.diagonalLength();
				sum += d;
				count++;
				if (d > maxMBR)
					maxMBR = d;
				double mcd = Utils.MCD(pts.get(0), pts);
				sb.append(t.toString() + "\t" + mbr.getMinLat() + "\t"
						+ mbr.getMinLng() + "\t" + mbr.getMaxLat() + "\t"
						+ mbr.getMaxLng() + "\t" + d + "\t" + mcd + "\n");
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
	 * partition all_data_time into T instances based on timestamp
	 * 
	 * @param filename
	 * @param instance
	 *            : the number of time instance
	 */
	public void extractWorkersInstances(String filename, String outputPath,
			int instance) {
		try {
			FileReader reader = new FileReader(filename);
			BufferedReader in = new BufferedReader(reader);
			StringBuffer sb = new StringBuffer();
			HashMap<Integer, ArrayList<PointTime>> data = new HashMap<Integer, ArrayList<PointTime>>();
			ArrayList<PointTime> points = new ArrayList<PointTime>();
			PriorityQueue<PointTime> sortedData = new PriorityQueue<>();
			Integer prev_id = -1;
			while (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split("\\s");
				Integer id = Integer.parseInt(parts[0]);
				String time = parts[1];
				String timeParts[] = time.split("-");
				int year = Integer.valueOf(timeParts[0]);
				int month = Integer.valueOf(timeParts[1]);
				int day = Integer.valueOf(timeParts[2].substring(0, 2));
				int hour = Integer.valueOf(timeParts[2].substring(3, 5));
				int timestamp = ((year - 2005) * 365 + month * 30 + day) * 24
						+ hour;

				Double lat = Double.parseDouble(parts[2]);
				Double lng = Double.parseDouble(parts[3]);

				/**
				 * Add point to queue
				 */
				PointTime pt = new PointTime(id, timestamp, lat, lng);
				sortedData.add(pt);

				if (id.equals(prev_id)) { // add to current list
					points.add(pt);
				} else {
					// create new list
					points = new ArrayList<PointTime>();
					points.add(pt);

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

			// iterate through HashMap keys Enumeration
			double sum = 0;
			int count = 0;
			double maxMBR = 0;
			HashMap<Integer, PointTime> userLocs = new HashMap<Integer, PointTime>();
			Iterator<Integer> it = data.keySet().iterator();
			while (it.hasNext()) {
				Integer t = it.next();
				ArrayList<PointTime> pts = data.get(t);
				MBR mbr = MBR.computeMBR2(pts);
				userLocs.put(
						t,
						new PointTime(t, 0,
								(mbr.getMaxLat() + mbr.getMinLat()) / 2.0, (mbr
										.getMaxLng() + mbr.getMinLng()) / 2.0));
			}

			/**
			 * Create data for each time instance
			 */
			int width = sortedData.size() / instance;
			ArrayList<PointTime> allDataArr = new ArrayList<PointTime>();
			while (!sortedData.isEmpty()) {
				allDataArr.add(sortedData.poll());
			}

			for (int t = 0; t < instance; t++) {
				HashMap<Integer, PointTime> currUserLocs = (HashMap<Integer, PointTime>) userLocs
						.clone();

				/**
				 * construct a dictionary of users that update their locations
				 */
				HashMap<Integer, ArrayList<PointTime>> userUpdates = new HashMap<Integer, ArrayList<PointTime>>();
				for (PointTime point : allDataArr.subList(t * width, t * width
						+ width)) {
					if (userUpdates.containsKey(point.getUserid()))
						userUpdates.get(point.getUserid()).add(point);
					else {
						ArrayList<PointTime> arr = new ArrayList<PointTime>();
						arr.add(point);
						userUpdates.put(point.getUserid(), arr);
					}
				}

				/**
				 * For each user
				 */
				int updateCount = 0; // number of users update their locations
				Iterator currIter = currUserLocs.keySet().iterator();
				while (currIter.hasNext()) {
					Integer key = (Integer) currIter.next();
					ArrayList<PointTime> new_locs = null;
					if (userUpdates.containsKey(key))
						new_locs = userUpdates.get(key);
					else
						continue;

					updateCount++;
					MBR mbr = MBR.computeMBR2(new_locs);

					PointTime p = new PointTime(key, 0,
							(mbr.getMaxLat() + mbr.getMinLat()) / 2.0,
							(mbr.getMaxLng() + mbr.getMinLng()) / 2.0);
					currUserLocs.put(key, p);
				}

				System.out.println("updates count " + updateCount);
				saveWorkerInstance(outputPath, t, currUserLocs);

			}

			System.out.println("Number of users: " + data.keySet().size());
			System.out.println("Average users' MBR size: " + sum / count);
			System.out.println("Max users' MBR size: " + maxMBR);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveWorkerInstance(String path, int t,
			HashMap<Integer, PointTime> userLocs) {
		StringBuffer sb = new StringBuffer();
		for (PointTime p : userLocs.values()) {
			// p.debug();
			// System.out.println(p.getY());
			// System.out.println(p.getX() + "\t" + p.getY());
			sb.append(p.getUserid() + "\t" + p.getX() + "\t" + p.getY() + "\n");
		}

		FileWriter writer;
		try {
			writer = new FileWriter(path + String.format("%04d", t)
					+ Constant.suffix);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(sb.toString(), 0, sb.length() - 1);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Get subset of the gowalla dataset, within a rectangle.
	 * 
	 * @param filename
	 *            the filename
	 * @param min_x
	 *            the min_x
	 * @param min_y
	 *            the min_y
	 * @param max_x
	 *            the max_x
	 * @param max_y
	 *            the max_y
	 */
	public void filterInput(String filename, double min_x, double min_y,
			double max_x, double max_y) {
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

				if ((lat < min_x) || (lat > max_x)
						|| (lng < (min_y) || (lng > (max_y))))
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
	 * Assuming Gowalla users are the workers, we assume all users who checked
	 * in during the day as available workers for that. The method returns a
	 * hashtable <day, workers>
	 * 
	 * This function is used as an input for saveWorkers()
	 * 
	 * @param datasetfile
	 *            the datasetfile
	 * @return the hashtable
	 */
	public Hashtable<Date, ArrayList<SpecializedWorker>> generateRealWorkers(
			String datasetfile) {
		Hashtable<Date, ArrayList<SpecializedWorker>> hashTable = new Hashtable();
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
				if (mbr.minLat < minLat)
					mbr.minLat = minLat;
				if (mbr.maxLat > maxLat)
					mbr.maxLat = maxLat;
				if (mbr.minLng < minLng)
					mbr.minLng = minLng;
				if (mbr.maxLng > maxLng)
					mbr.maxLng = maxLng;
				Integer pointID = Integer.parseInt(parts[4]);
				int exp = (int) UniformGenerator.randomValue(new Range(0,
						Constants.TaskTypeNo), true);
				SpecializedWorker w = new SpecializedWorker(userID, lat, lng,
						0, mbr);
				w.addExpertise(exp);
				if (!hashTable.containsKey(date)) {
					ArrayList<SpecializedWorker> workers = new ArrayList<SpecializedWorker>();
					workers.add(w);
					hashTable.put(date, workers);
				} else {
					ArrayList<SpecializedWorker> workers = hashTable.get(date);
					boolean found = false; // check if the worker is already in
											// the worker list, if yes -->
											// update his maxTass and R
					for (SpecializedWorker o : workers) {
						if (o.getUserID().equals(userID)) {
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
	 * Generate SYN dataset.
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
				SpecializedTask t = new SpecializedTask(lat, lng, time, -1,
						taskType);
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
	 * Generate SYN dataset.
	 * 
	 * @param fileName
	 *            : output
	 * @param matlabFile
	 *            : the workers are formed into four Gaussian clusters
	 * @param isConstantMBR
	 *            the is constant mbr
	 * @param isConstantMaxT
	 *            the is constant max t
	 * @maxT is randomly generated
	 */
	private void generateSyncWorkersFromMatlab(String fileName,
			String matlabFile, boolean isConstantMBR, boolean isConstantMaxT) {
		int maxSumTaskWorkers = 0;
		System.out.println("Workers:");
		double maxRangeX = (maxLat - minLat) * (Constants.MaxRangePerc);
		double maxRangeY = (maxLng - minLng) * Constants.MaxRangePerc;
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
				SpecializedWorker w = new SpecializedWorker("dump", lat, lng,
						maxT, mbr);
				w.addExpertise(exp);
				out.write(-1 + "," + lat + "," + lng + "," + maxT + "," + "["
						+ mbr.getMinLat() + "," + mbr.getMinLng() + ","
						+ mbr.getMaxLat() + "," + mbr.getMaxLng() + "],[" + exp
						+ "]\n");
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Sum of all maxTask:" + maxSumTaskWorkers);
	}

	/**
	 * Generate syn tasks.
	 */
	public void generateSynTasks() {
		timeCounter = 0;
		String outputFileFrefix = "";
		switch (DATA_SET) {
		case SKEWED:
			outputFileFrefix = Constants.skewedTaskFileNamePrefix;
			break;
		case UNIFORM:
			outputFileFrefix = Constants.uniTaskFileNamePrefix;
			break;
		}
		for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			generateSyncTasksFromMatlab(outputFileFrefix + i + ".txt",
					Constants.matlabTaskFilePath + i + ".txt");
			timeCounter++;
		}
	}

	/**
	 * Generate syn workers.
	 * 
	 * @param isConstantMBR
	 *            the is constant mbr
	 * @param isConstantMaxT
	 *            the is constant max t
	 */
	public void generateSynWorkers(boolean isConstantMBR, boolean isConstantMaxT) {
		switch (DATA_SET) {
		case SKEWED:
			for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
				generateSyncWorkersFromMatlab(
						Constants.skewedWorkerFileNamePrefix + i + ".txt",
						Constants.skewedMatlabWorkerFilePath + i + ".txt",
						isConstantMBR, isConstantMaxT);
			}
			break;
		case UNIFORM:
			for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
				generateSyncWorkersFromMatlab(Constants.uniWorkerFileNamePrefix
						+ i + ".txt", Constants.uniMatlabWorkerFilePath + i
						+ ".txt", isConstantMBR, isConstantMaxT);
			}
			break;
		}
	}

	/**
	 * Gets the col idx.
	 * 
	 * @param lng
	 *            the lng
	 * @return the col idx
	 */
	public int getColIdx(double lng) {
		return (int) ((lng - minLng) / (resolution * (maxLng - minLng)));
	}

	public double colIdxToLng(int col) {
		return col * resolution * (maxLng - minLng) + minLng;
	}

	/**
	 * Gets the row idx.
	 * 
	 * @param lat
	 *            the lat
	 * @return the row idx
	 */
	public int getRowIdx(double lat) {
		return (int) (1 / resolution * (lat - minLat) / (maxLat - minLat));
	}

	public double rowIdxToLat(int row) {
		return row * resolution * (maxLat - minLat) + minLat;
	}

	/**
	 * Used as an input for saveLocationEntropy.
	 * 
	 * @return which location belongs to which grid cell
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
			System.out.println("Hashtable<location, grid> size: "
					+ hashTable.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashTable;
	}

	/**
	 * Read boundary from file.
	 * 
	 * @param dataset
	 *            the dataset
	 */
	public void readBoundary(DatasetEnum dataset) {
		String boundaryFile = "";
		switch (dataset) {
		case GOWALLA:
			boundaryFile = Constants.gowallaBoundary;
			break;
		case SKEWED:
			boundaryFile = Constants.skewedBoundary;
			break;
		case UNIFORM:
			boundaryFile = Constants.uniBoundary;
			break;
		case SMALL_TEST:
			boundaryFile = Constants.smallBoundary;
			break;
		case YELP:
			boundaryFile = Constants.yelpBoundary;
			break;
		}
		try {
			FileReader reader = new FileReader(boundaryFile);
			BufferedReader in = new BufferedReader(reader);
			if (in.ready()) {
				String line = in.readLine();
				String[] parts = line.split(" ");
				minLat = Double.valueOf(parts[0]);
				minLng = Double.valueOf(parts[1]);
				maxLat = Double.valueOf(parts[2]);
				maxLng = Double.valueOf(parts[3]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read data, e.g., gowalla file
	 * 
	 * @param datasetfile
	 *            the datasetfile
	 * @return a hashtable <location id, occurrences>
	 */
	public Hashtable<Integer, ArrayList<Observation>> readRealEntropyData(
			String datasetfile) {
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
			System.out
					.println("Hashtable <location, occurrences> size: "
							+ hashTable.size()
							+ " (the number of cells with checkins)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hashTable;
	}

	/**
	 * compute REGION entropy
	 * 
	 * @param datasetfile
	 *            the datasetfile
	 * @return a hashtable <row, <col, [observations]>>
	 */
	public Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> readEntropyData(
			String datasetfile) {

		String workerFilePath = "";
		switch (DATA_SET) {
		case GOWALLA:
			workerFilePath = Constants.gowallaWorkerFileNamePrefix;
			break;
		case SKEWED:
			workerFilePath = Constants.skewedMatlabWorkerFilePath;
			break;
		case UNIFORM:
			workerFilePath = Constants.uniMatlabWorkerFilePath;
			break;
		case SMALL_TEST:
			workerFilePath = Constants.smallWorkerFilePath;
		}

		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> hashTable = new Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>>();

		for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			try {
				FileReader file = new FileReader(workerFilePath + i + ".txt");
				BufferedReader in = new BufferedReader(file);
				while (in.ready()) {
					String line = in.readLine();
					String[] parts = line.split(",");
					Integer userID = Integer.parseInt(parts[0]);
					Double lat = Double.parseDouble(parts[1]);
					Double lng = Double.parseDouble(parts[2]);
					int row = getRowIdx(lat);
					int col = getColIdx(lng);

					// System.out.println(row + "\t" + col);

					if (!hashTable.containsKey(row)) {
						Hashtable<Integer, Hashtable<Integer, Integer>> cols = new Hashtable<Integer, Hashtable<Integer, Integer>>();
						Hashtable<Integer, Integer> obs = new Hashtable<Integer, Integer>();
						obs.put(userID, 1);
						cols.put(col, obs);
						hashTable.put(row, cols);
					} else {
						Hashtable<Integer, Hashtable<Integer, Integer>> cols = hashTable
								.get(row);
						if (!cols.containsKey(col)) {
							Hashtable<Integer, Integer> obs = new Hashtable<Integer, Integer>();
							obs.put(userID, 1);
							cols.put(col, obs);
						} else {
							Hashtable<Integer, Integer> obs = cols.get(col);
							if (obs.containsKey(userID))
								obs.put(userID, obs.get(userID) + 1);
							else
								obs.put(userID, 1);
							cols.put(col, obs);
						}
						hashTable.put(row, cols);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return hashTable;
	}

	/**
	 * Save what location in what grid and its corresponding location entropy,
	 * the entropy information is get from a file which was generated before.
	 * 
	 * @param hashTable
	 *            the hash table
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

	/**
	 * Save real workers.
	 * 
	 * Fix the number of time instances
	 * 
	 * @param hashTable
	 *            the hash table
	 */
	public void saveRealWorkers(
			Hashtable<Date, ArrayList<SpecializedWorker>> hashTable) {
		try {
			// sort key and iterate based on key
			List<Date> dates = new ArrayList<Date>(hashTable.keySet());
			Collections.sort(dates);

			Integer instanceCnt = 0;
			Integer dayCnt = 0;
			int workerCount = 0;
			int daysPerInstance = dates.size() / Constants.TIME_INSTANCE;
			System.out.println("days per one instance: " + daysPerInstance);
			BufferedWriter out = null;
			for (Date date : dates) {
				if (dayCnt == 0) {
					FileWriter writer = new FileWriter(
							Constants.gowallaWorkerFileNamePrefix
									+ instanceCnt.toString() + ".txt");
					out = new BufferedWriter(writer);
				} else if (dayCnt == daysPerInstance) {
					instanceCnt++;
					dayCnt = 0;
					System.out.println("worker count: " + workerCount);
					workerCount = 0;
					out.close();
					continue;
				}

				dayCnt++;

				ArrayList<SpecializedWorker> workers = hashTable.get(date);
				for (SpecializedWorker o : workers) {
					out.write(o.toStr() + "\n");
					workerCount++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save real workers.
	 * 
	 * Given max online worker per instance
	 * 
	 * @param hashTable
	 *            the hash table
	 */
	public void saveRealWorkersMax(
			Hashtable<Date, ArrayList<SpecializedWorker>> hashTable) {
		try {
			// sort key and iterate based on key
			List<Date> dates = new ArrayList<Date>(hashTable.keySet());
			Collections.sort(dates);

			Integer instanceCnt = 0;
			Integer workerCnt = 0;
			BufferedWriter out = null;
			int i = 0;
			for (Date date : dates) {
				i++;
				if (i < Constants.MIN_TIME)
					continue;
				if (workerCnt == 0) {
					FileWriter writer = new FileWriter(
							Constants.gowallaWorkerFileNamePrefix
									+ instanceCnt.toString() + ".txt");
					out = new BufferedWriter(writer);
				} else if (workerCnt > Constants.WorkerNo) {
					instanceCnt++;
					System.out.println("worker count: " + workerCnt);
					workerCnt = 0;
					out.close();
					continue;
				}

				workerCnt++;

				ArrayList<SpecializedWorker> workers = hashTable.get(date);
				for (SpecializedWorker o : workers) {
					out.write(o.toStr() + "\n");
					workerCnt++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save syn location density.
	 * 
	 * @param densities
	 *            the densities
	 */
	public void saveLocationDensity(
			Hashtable<Integer, Hashtable<Integer, Integer>> densities) {
		String locationDensityFileName = "";
		switch (DATA_SET) {
		case GOWALLA:
			locationDensityFileName = Constants.gowallaLocationDensityFileName;
			break;
		case SKEWED:
			locationDensityFileName = Constants.skewedLocationDensityFileName;
			break;
		case UNIFORM:
			locationDensityFileName = Constants.uniLocationDensityFileName;
			break;
		case SMALL_TEST:
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

	public void debug() {
		System.out.println("minLat " + minLat);
		System.out.println("minLng " + minLng);
		System.out.println("maxLat " + maxLat);
		System.out.println("maxLng " + maxLng);
		System.out.println("resolution " + resolution);
	}

	/**
	 * Read data from multiple time instances. Then,
	 */
	public void regionEntropy() {
		readBoundary(PreProcess.DATA_SET);
		createGrid(PreProcess.DATA_SET);

		// compute occurrences of each location id from Gowalla
		// each location id is associated with a grid
		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> occurances = readEntropyData(Constants.gowallaFileName_CA);

		// for (Hashtable<Integer, Hashtable<Integer, Integer>> cols :
		// occurances
		// .values()) {
		// for (Hashtable<Integer, Integer> col : cols.values()) {
		// System.out.println(col.keySet() + " " + col.values());
		// }
		// }

		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> regionOccurances = regionEntropy(occurances);

		// compute entropy of each location id
		computeRegionEntropy(regionOccurances);

		// compute index (row, col) of each location id
		// debug();
		// Hashtable<Integer, Coord> gridIndices = locIdToCellIndices();
		// saveLocationEntropy(gridIndices);

	}

	private Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> regionEntropy(
			Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> occurances) {

		Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>> regionOccurances = new Hashtable<Integer, Hashtable<Integer, Hashtable<Integer, Integer>>>();

		/**
		 * init regionOccurances
		 */
		for (Integer row : occurances.keySet()) {
			if (!regionOccurances.containsKey(row))
				regionOccurances.put(row,
						new Hashtable<Integer, Hashtable<Integer, Integer>>());
			for (Integer col : occurances.get(row).keySet()) {
				if (!regionOccurances.get(row).containsKey(col))
					regionOccurances.get(row).put(col,
							new Hashtable<Integer, Integer>());
				for (Integer userid : occurances.get(row).get(col).keySet()) {
					if (!regionOccurances.get(row).get(col).containsKey(userid))
						regionOccurances.get(row).get(col).put(userid, 0);
				}
			}
		}

		/**
		 * For every cell, search for all cells that cover this
		 */
		for (Integer row : occurances.keySet()) {
			Hashtable<Integer, Hashtable<Integer, Integer>> cols = occurances
					.get(row);
			for (Integer col : cols.keySet()) {
				Hashtable<Integer, Integer> obs = cols.get(col);
				double lat = rowIdxToLat(row);
				double lng = colIdxToLng(col);

				for (Integer row2 : occurances.keySet()) {
					Hashtable<Integer, Hashtable<Integer, Integer>> cols2 = occurances
							.get(row2);
					for (Integer col2 : cols2.keySet()) {
						// Hashtable<Integer, Integer> obs2 = cols.get(col2);
						double lat2 = rowIdxToLat(row2);
						double lng2 = colIdxToLng(col2);

						// System.out.println(lat +" "+ lng +" "+ lat2 +" "+
						// lng2);
						if (Utils.distance(lat, lng, lat2, lng2) < (Constants.diameter + 0.0) / 2) {
							// System.out.println(Utils.distance(lat, lng, lat2,
							// lng2));
							for (Integer userid : obs.keySet()) {
								if (regionOccurances.get(row2).get(col2)
										.containsKey(userid)) {
									regionOccurances
											.get(row2)
											.get(col2)
											.put(userid,
													regionOccurances.get(row2)
															.get(col2)
															.get(userid)
															+ obs.get(userid));
								} else {
									regionOccurances.get(row2).get(col2)
											.put(userid, obs.get(userid));
									// System.out.println(regionOccurances.get(row2).get(col2));
								}
							}
						}
					}
				}
			}
		}
		return regionOccurances;
	}
}
