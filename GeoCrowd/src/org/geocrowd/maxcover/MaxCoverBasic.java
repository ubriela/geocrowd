/**
 * *****************************************************************************
 * @ Year 2013 This is the source code of the following papers.
 * 
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla
 * Kazemi, Cyrus Shahabi.
 * 
 *
 * Please contact the author Hien To, ubriela@gmail.com if you have any
 * question.
 * 
 * Contributors: Hien To - initial implementation
 ******************************************************************************
 */
package org.geocrowd.maxcover;

import static org.geocrowd.Geocrowd.candidateTaskIndices;
import static org.geocrowd.Geocrowd.taskList;
import static org.geocrowd.Geocrowd.workerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdTaskUtility;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.crowd.SensingWorker;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdConstants;

/**
 * The Class SetCoverGreedy.
 * 
 * The budget k is fixed, algorithm stops when running out of budget
 * 
 */
public class MaxCoverBasic extends MaxCover {

	public MaxCoverBasic() {
		super();
	}

	public MaxCoverBasic(ArrayList container, Integer currentTI) {
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
			double maxUncoveredUtility = 0.0;
			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
			 */
			for (int k : S.keySet()) {
				GenericWorker w = workerList.get(k);
				
				HashMap<Integer, Integer> s = S.get(k); // task set covered by
														// current worker
				double uncoveredUtility = 0.0;
				for (Integer i : s.keySet()) {
					if (!assignedTaskSet.contains(i)) {
						SensingTask t = (SensingTask) taskList
								.get(candidateTaskIndices.get(i));
						double utility = GeocrowdTaskUtility.utility(Geocrowd.DATA_SET, w, t);
//						System.out.println(utility);
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
			if (bestWorkerIndex > -1) {
				/**
				 * gain is reduced at every stage
				 */
				gain = maxUncoveredUtility;
				assignedUtility += gain;

				assignWorkers.add(bestWorkerIndex);
				HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
				S.remove(bestWorkerIndex);
				Q.removeAll(taskSet.keySet());

				/**
				 * compute average time to assign tasks in taskSet
				 */
				for (Integer taskidx : taskSet.keySet())
					if (!assignedTaskSet.contains(taskidx)) {

						averageDelayTime += currentTimeInstance
								- (taskSet.get(taskidx) - GeocrowdConstants.MAX_TASK_DURATION)
								+ 1;
						assignedTaskSet.add(taskidx);
					}
			}
			else break;
		}

		assignedTasks = assignedTaskSet.size();
//		System.out.println(universe.size() + "\t" + assignedTasks + "\t"
//				+ assignWorkers.size() + "\t" + assignedTasks
//				/ assignWorkers.size());
		return assignWorkers;
	}
}
