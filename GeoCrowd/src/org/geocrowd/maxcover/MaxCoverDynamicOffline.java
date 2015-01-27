/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd.maxcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdConstants;
import org.geocrowd.OfflineMTC;

import static org.geocrowd.Geocrowd.workerList;

import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.SensingTask;
import org.geocrowd.common.crowdsource.SensingWorker;

/**
 * 
 * @author Luan
 */
public class MaxCoverDynamicOffline extends MaxCover {

//	public int numberTimeInstance = 0;
	public int[] budgetPerInstance;
	/**
	 * limit number of workers selected at each time instance
	 */
	HashMap<Integer, Integer> selectedWorkerAtTimeInstance = new HashMap<>();

	public MaxCoverDynamicOffline() {
		super();
	}

	public MaxCoverDynamicOffline(ArrayList container, Integer currentTI) {
		super(container, currentTI);
	}

	/**
	 * Greedy algorithm.
	 * 
	 * @return the assigned workers
	 */
	@Override
	public HashSet<Integer> maxCover() {

		HashMap<Integer, HashMap<Integer, Integer>> S = (HashMap<Integer, HashMap<Integer, Integer>>) mapSets
				.clone();

		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
//		assignedTaskSet = new HashSet<Integer>();

		/**
		 * Run until either running out of budget or no more tasks to cover
		 */
		while (assignWorkers.size() < budget && !Q.isEmpty()) {
			int bestWorkerIndex = -1; // track index of the best worker in S
			int maxNoUncoveredTasks = 0;
			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
			 */
			for (int k : S.keySet()) {
				HashMap<Integer, Integer> s = S.get(k); // task set covered by
				// current worker

				/**
				 * check if the #selected workers at time instance of current
				 * worker <limit
				 */
				/* actual worker */
				int noUncoveredTasks = 0;
				for (Integer i : s.keySet()) {
					if (!assignedTaskSet.contains(i)) {
						noUncoveredTasks++;
					}
				}
				if (noUncoveredTasks > maxNoUncoveredTasks) {
					maxNoUncoveredTasks = noUncoveredTasks;
					bestWorkerIndex = k;
				}
			}

			/**
			 * gain is reduced at every stage
			 */
			if (bestWorkerIndex > -1) {
				gain = maxNoUncoveredTasks;

				assignWorkers.add(bestWorkerIndex);
				HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
				S.remove(bestWorkerIndex);
				Q.removeAll(taskSet.keySet());
				

				/**
				 * compute average time to assign tasks in taskSet
				 */
				for (Integer taskidx : taskSet.keySet()) {
					if (!assignedTaskSet.contains(taskidx)) {

						averageDelayTime += currentTimeInstance
								- (taskSet.get(taskidx) - GeocrowdConstants.TaskDuration)
								+ 1;
						assignedTaskSet.add(taskidx);
					}
				}
			} else {
				System.out.println("Break here because best index = "
						+ bestWorkerIndex);
				break;
			}
		}

		assignedTasks = assignedTaskSet.size();
		// System.out.println(universe.size() + "\t" + assignedTasks + "\t" +
		// assignWorkers.size() + "\t" + assignedTasks / assignWorkers.size());
		
		return assignWorkers;
	}

}
