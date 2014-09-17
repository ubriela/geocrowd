package org.geocrowd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.geocrowd.common.Constants;
import org.geocrowd.common.MBR;
import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.GenericWorker;
import org.geocrowd.common.crowdsource.SensingTask;
import org.geocrowd.common.crowdsource.SpecializedTask;
import org.geocrowd.common.crowdsource.SpecializedWorker;

public class Parser {

	public static int parseSpecializedWorkers(String fileName,
			ArrayList<GenericWorker> workerList) {
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

				SpecializedWorker w = new SpecializedWorker(userId, lat, lng,
						maxT, mbr);

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
		return cnt;
	}

	public static int parseSpecializedTasks(String fileName,
			ArrayList<GenericTask> taskList) {
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
				SpecializedTask t = new SpecializedTask(lat, lng, time,
						entropy, type);
				taskList.add(listCount, t);
				listCount++;
				cnt++;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cnt;
	}

	public static int parseSensingTasks(String fileName,
			ArrayList<GenericTask> taskList) {
		int cnt = 0;
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
				SensingTask t = new SensingTask(lat, lng, time, entropy);
				t.setRadius(Constants.diameter/2);
				t.setK(Constants.K);
				taskList.add(listCount, t);
				listCount++;
				cnt++;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cnt;
	}

	public static int parseGenericWorkers(String fileName,
			ArrayList<GenericWorker> workerList) {
		workerList.clear(); // luan comment for test
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

				String userId = coords[0];
				double lat = Double.parseDouble(coords[1]);
				double lng = Double.parseDouble(coords[2]);
				int maxT = Integer.parseInt(coords[3]);

				GenericWorker w = new GenericWorker(userId, lat, lng, maxT);

				workerList.add(w);
				cnt++;
			}

			in.close();
		} catch (Exception e) {

			e.printStackTrace();
		}
		return cnt;
	}

}
