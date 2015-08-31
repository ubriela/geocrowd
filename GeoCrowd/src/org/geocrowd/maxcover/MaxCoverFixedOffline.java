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
import org.geocrowd.TaskUtility;

import static org.geocrowd.Geocrowd.workerList;
import static org.geocrowd.Geocrowd.taskList;
import static org.geocrowd.Geocrowd.candidateTaskIndices;

import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.crowd.SensingWorker;
import org.geocrowd.common.utils.Utils;

/**
 * 
 * @author Luan
 */
public class MaxCoverFixedOffline extends MaxCover {

	// public int numberTimeInstance = 0;
	public int[] budgetPerInstance;
	/**
	 * limit number of workers selected at each time instance
	 */
	HashMap<Integer, Integer> selectedWorkerAtTimeInstance = new HashMap<>();

	public MaxCoverFixedOffline() {
		super();
	}

	public MaxCoverFixedOffline(ArrayList container, Integer currentTI) {
		super(container, currentTI);
	}

	/**
	 * Greedy algorithm.
	 * 
	 * @return the assigned workers
	 */
	@Override
	public HashSet<Integer> maxCover() {
		budgetPerInstance = new int[GeocrowdConstants.TIME_INSTANCE];
		for (int i = 0; i < budgetPerInstance.length - 1; i++) {
			budgetPerInstance[i] = budget / GeocrowdConstants.TIME_INSTANCE;
		}
		budgetPerInstance[budgetPerInstance.length - 1] = budget - budget
				/ GeocrowdConstants.TIME_INSTANCE
				* (GeocrowdConstants.TIME_INSTANCE - 1);

		HashMap<Integer, HashMap<Integer, Integer>> S = (HashMap<Integer, HashMap<Integer, Integer>>) mapSets
				.clone();

		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
		// assignedTaskSet = new HashSet<Integer>();

		/**
		 * Run until either running out of budget or no more tasks to cover
		 */
		while (assignWorkers.size() < budget && !Q.isEmpty()) {
			int bestWorkerIndex = -1; // track index of the best worker in S
			double maxUncoveredUtility = 0.0;
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
				SensingWorker w = (SensingWorker) workerList.get(k);
				if (selectedWorkerAtTimeInstance.get(w.getOnlineTime()) != null
						&& selectedWorkerAtTimeInstance.get(w.getOnlineTime()) >= budgetPerInstance[w
								.getOnlineTime()]) {
					continue;
				}
				double uncoveredUtility = 0.0; // number of uncovered tasks when DBU
											// = false
				for (Integer i : s.keySet()) {
					if (!assignedTaskSet.contains(i)) {
						// compute utility (w,task i)
						SensingTask t = (SensingTask) taskList
								.get(candidateTaskIndices.get(i));
						double utility = TaskUtility.utility(Geocrowd.DATA_SET, w, t);
						uncoveredUtility += utility;
					}
				}

				if (uncoveredUtility > maxUncoveredUtility) {
					maxUncoveredUtility = uncoveredUtility;
					bestWorkerIndex = k;
				}
			}

			// System.out.print(S.get(bestWorkerIndex));
			// System.out.println(maxNoUncoveredTasks);
			/**
			 * gain is reduced at every stage
			 */
			if (bestWorkerIndex > -1) {
				gain = maxUncoveredUtility;
				assignedUtility += gain;

				assignWorkers.add(bestWorkerIndex);
				HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
				S.remove(bestWorkerIndex);
				Q.removeAll(taskSet.keySet());
				/* increase # selected worker of time instace */
				SensingWorker w = (SensingWorker) workerList
						.get(bestWorkerIndex);
				if (selectedWorkerAtTimeInstance.get(w.getOnlineTime()) != null) {
					selectedWorkerAtTimeInstance
							.put(w.getOnlineTime(),
									selectedWorkerAtTimeInstance.get(w
											.getOnlineTime()) + 1);
				} else {
					selectedWorkerAtTimeInstance.put(w.getOnlineTime(), 1);
				}

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
		// for (Integer i : selectedWorkerAtTimeInstance.keySet()) {
		// System.out.println("#Selected workers in Time instance " + (i + 1)
		// + ":" + selectedWorkerAtTimeInstance.get(i));
		// }
		return assignWorkers;
	}

}
