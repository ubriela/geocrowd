package org.geocrowd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.SpecializedTask;
import org.geocrowd.common.crowdsource.SpecializedWorker;
import org.geocrowd.matching.Hungarian;
import org.geocrowd.matching.OnlineBipartiteMatching;
import org.geocrowd.matching.Utility;

public class GeocrowdOnline extends Geocrowd {

	// apply online bipartite matching
	OnlineBipartiteMatching obm = null;

	public GeocrowdOnline(String fileName) {
		super();

		readOnlineWorkers(fileName);

		ArrayList<Integer> orders = new ArrayList();
		for (int i = 0; i < workerList.size(); i++)
			orders.add(i);
		obm = new OnlineBipartiteMatching(orders);
	}

	/**
	 * Online matching.
	 * 
	 * @return the number of task assigned
	 */
	public double onlineMatching() {

		switch (algorithm) {
		case ONLINE:
			return online();
		case BASIC:
			return basic();
		case LLEP:
			return llep();
		case NNP:
			return nnp();
		default:
			System.out.println("The algorithm is not supported!");
			return -1;
		}
	}

	private double nnp() {
		// TODO Auto-generated method stub
		return 0;
	}

	private double llep() {
		// TODO Auto-generated method stub
		return 0;
	}

	private double basic() {
		/* rows represent workers, columns represent tasks */
		double[][] array = new double[containerWorker.size()][candidateTaskIndices
				.size()];
		int row = 0;
		for (int i = 0; i < containerWorker.size(); i++) {
			ArrayList<Integer> tasks = containerWorker.get(i);
			if (tasks != null)
				for (int j : tasks)
					array[row][j] = -1;
			row++;
		}
		
		Utility.print(array);

		double[][] origin = Utility.copyOf(array);

		/*
		 * transpose the matrix if #workers < #tasks (because
		 * rows(worker)>columns(task))
		 */
		boolean isTranpose = false;
		if (array.length > array[0].length) {
			array = Utility.transpose(array);
			origin = Utility.transpose(origin);
			isTranpose = true;
			System.out.println("transpose!");
		}

		Hungarian HA = new Hungarian(array);
		int[] r = HA.execute(array);

		ArrayList<Integer> assignedTasks = new ArrayList<Integer>();
		/* remove the solved task from task list */
		for (int i = r.length - 1; i >= 0; i--)
			if (origin[i][r[i]] < 0)
				assignedTasks.add(r[i]);

		removeAssignedTasks(assignedTasks);

		TotalAssignedTasks += assignedTasks.size();

		System.out.println("#Total assigned tasks: " + TotalAssignedTasks);
		System.out.println("#Remained workers: " + workerList.size());
		System.out.println("#Expired tasks: " + TotalExpiredTask);

		return assignedTasks.size();
	}

	/**
	 * Basic online algorithm
	 * 
	 * @return
	 */
	private double online() {
		HashMap<Integer, Integer> assignment = obm
				.onlineMatching(invertedContainer);

		// remove the assigned tasks from task list
		ArrayList<Integer> assignedTasks = new ArrayList(assignment.keySet());
		removeAssignedTasks(assignedTasks);

		// remove assigned workers from worker list
		ArrayList<Integer> assignedWorkers = new ArrayList(assignment.values());
		removeAssignedWorkers(assignedWorkers);

		TotalAssignedTasks += assignedTasks.size();

		System.out.println("#Total assigned tasks: " + TotalAssignedTasks);
		System.out.println("#Remained workers: " + workerList.size());
		System.out.println("#Expired tasks: " + TotalExpiredTask);

		// check correctness
		if (TotalExpiredTask + taskList.size() + TotalAssignedTasks != TaskCount) {
			System.out.println("Logic error!!!");
			System.out.println("#Expired tasks: " + TotalExpiredTask);
			System.out.println("#Remained tasks: " + taskList.size());
			System.out.println("#Task count: " + TaskCount);
		}

		return assignedTasks.size();
	}

	private void removeAssignedWorkers(ArrayList<Integer> assignedWorkers) {
		Collections.sort(assignedWorkers);

		for (int i = assignedWorkers.size() - 1; i >= 0; i--)
			workerList.remove((int) assignedWorkers.get(i));

	}

	private void removeAssignedTasks(ArrayList<Integer> assignedTasks) {
		Collections.sort(assignedTasks);

		for (int i = assignedTasks.size() - 1; i >= 0; i--)
			taskList.remove((int) assignedTasks.get(i));
	}

	/**
	 * Compute which tasks within working region of which worker and vice versa.
	 */
	@Override
	public void matchingTasksWorkers() {
		containerWorker = new ArrayList<ArrayList>();
		invertedContainer = new HashMap<Integer, ArrayList>();
		candidateTaskIndices = new ArrayList();
		taskSet = new HashSet<Integer>();
		containerPrune = new ArrayList[workerList.size()];

		// remove expired task from task list
		pruneExpiredTasks();

		for (int idx = 0; idx < workerList.size(); idx++) {
			SpecializedWorker w = (SpecializedWorker) workerList.get(idx);
			rangeQuery(idx, w);
		}
		
		// remove workers with no tasks
		for (int i = containerPrune.length - 1; i >= 0; i--) {
			if (containerPrune[i] == null || containerPrune[i].size() == 0) {
				/* remove from worker list */
				workerList.remove(i);
			}
		}
		
		for (int i = 0; i < containerPrune.length; i++) {
			if (containerPrune[i] != null && containerPrune[i].size() > 0)
				/* add non-empty elements to containerWorker */
				containerWorker.add(containerPrune[i]);
		}

		System.out.println();
	}

	/**
	 * Compute input for one time instance, including container and
	 * invertedTable.
	 * 
	 * @param workerIdx
	 *            the worker idx
	 * @param mbr
	 *            the mbr
	 */
	private void rangeQuery(final int workerIdx, SpecializedWorker w) {
		/* task id, increasing from 0 to the number of task - 1 */
		int t = 0;
		for (int i = 0; i < taskList.size(); i++) {
			SpecializedTask task = (SpecializedTask) taskList.get(i);

			/* tick expired task */
			if ((TimeInstance - task.getEntryTime()) >= Constants.TaskDuration) {
				task.setExpired();
			} else

			/**
			 * if the task is not assigned and in the worker's working region
			 */
			if (task.isCoveredBy(w.getMBR())) {

				if (!taskSet.contains(t)) {
					candidateTaskIndices.add(t);
					taskSet.add(t);
				}

				if (containerPrune[workerIdx] == null)
					containerPrune[workerIdx] = new ArrayList();
				/*
				 * the container contains task index of elements in candidate
				 * tasks
				 */
				containerPrune[workerIdx].add(candidateTaskIndices.indexOf(t));

				if (!invertedContainer.containsKey(t))
					invertedContainer.put(t, new ArrayList() {
						{
							add(workerIdx);
						}
					});
				else
					invertedContainer.get(t).add(workerIdx);

			}// if not overlapped

			t++;
		}// for loop
	}

	/**
	 * Read all task once time when start-up
	 * 
	 * @param fileName
	 */
	private void readOnlineWorkers(String fileName) {
		if (workerList == null)
			workerList = new ArrayList();
		WorkerCount += Parser.parseSpecializedWorkers(fileName, workerList);
	}

	@Override
	public void readTasks(String fileName) {
		TaskCount += Parser.parseSpecializedTasks(fileName, taskList);
	}

	@Override
	public void readWorkers(String fileName) {
		// DO NOTHING
	}

}
