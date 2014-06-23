package org.geocrowd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.common.GenericWorker;
import org.geocrowd.common.MBR;
import org.geocrowd.common.SensingTask;
import org.geocrowd.setcover.SetCover;
import org.geocrowd.setcover.SetCoverGreedy;
import org.geocrowd.setcover.SetCoverGreedySmallestAssociateSet;
import org.geocrowd.util.Constants;

public class Crowdsensing extends GenericCrowd {

	public int TotalAssignedWorkers = 0; // over all time instance, (the number of push notification in practice)
	
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
				SensingTask t = new SensingTask(lat, lng, time, entropy);
				t.setRadius(Constants.radius);
				taskList.add(listCount, t);
				listCount++;
				TaskCount++;
			}
			in.close();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Compute which worker within which task region and vice versa
	 */
	public void matchingTasksWorkers() {
		invertedTable = new HashMap<Integer, ArrayList>();
		candidateTasks = new ArrayList();
		container = new ArrayList<ArrayList>();
		container2 = new ArrayList[workerList.size()];

		// remove expired task from tasklist
		pruneExpiredTasks();

		for (int idx = 0; idx < workerList.size(); idx++) {
			reverseRangeQuery(idx, workerList.get(idx));
		}

		// remove workers with no tasks
		for (int i = container2.length - 1; i >= 0; i--) {
			if (container2[i] == null || container2[i].size() == 0) {
				workerList.remove(i);
			}
		}
		for (int i = 0; i < container2.length; i++) {
			if (container2[i] != null && container2[i].size() > 0) {
				container.add(container2[i]);
			}
		}
	}

	/**
	 * Compute input for one time instance, including container and
	 * invertedTable
	 */
	private void reverseRangeQuery(int workerIdx, GenericWorker w) {
		int tid = 0; // task id, increasing from 0 to the number of task - 1
		for (int i = 0; i < taskList.size(); i++) {
			SensingTask task = (SensingTask) taskList.get(i);

			// tick expired task
			if ((time_instance - task.getEntryTime()) >= Constants.TaskDuration) {
				task.setExpired();
			} else

			// if worker in task region
			if (distanceWorkerTask(w, task) <= task.getRadius()) {
				if (container2[workerIdx] == null) {
					container2[workerIdx] = new ArrayList();
				}

				if (taskSet == null)
					taskSet = new HashSet<Integer>();

				if (!taskSet.contains(tid)) {
					candidateTasks.add(tid);
					taskSet.add(tid);
				}
				container2[workerIdx].add(candidateTasks.indexOf(tid));

				if (!invertedTable.containsKey(tid)) {
					ArrayList arr = new ArrayList();
					arr.add(workerIdx);
					invertedTable.put(tid, arr);
				} else {
					ArrayList arr = invertedTable.get(tid);
					arr.add(workerIdx);
					invertedTable.put(tid, arr);
				}

			}// if not overlapped

			tid++;
		}// for loop
	}

	/**
	 * Select minimum number of workers that cover maximum number of tasks
	 */
	public void maxTasksMinimumWorkers() {
		
		SetCover sc = null;
		int assignedWorkers = 0;
		
		switch (algorithm){
			case GREEDY1:
				sc = new SetCoverGreedy(container);
				assignedWorkers = sc.minSetCover();
				TotalAssignedWorkers += assignedWorkers;
				break;
			case GREEDY2:
				sc = new SetCoverGreedySmallestAssociateSet(container);
				assignedWorkers = sc.minSetCover();
				TotalAssignedWorkers += assignedWorkers;
				break;
				
			
		}
		//System.out.println(assignedWorkers);
	}
}