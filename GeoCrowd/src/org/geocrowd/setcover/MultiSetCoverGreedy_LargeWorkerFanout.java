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
package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.Constants;
import org.geocrowd.Geocrowd;
import org.geocrowd.datasets.params.GeocrowdConstants;

/**
 * @author Hien To
 */
public class MultiSetCoverGreedy_LargeWorkerFanout extends MultiSetCoverGreedy {

	/**
	 * Only choose worker covers at least k tasks.
	 */
	Integer k = 4;

	/**
	 * The average tasks per worker.
	 */
	public double averageTasksPerWorker;

	/**
	 * The average workers per task.
	 */
	public double averageWorkersPerTask;

	public MultiSetCoverGreedy_LargeWorkerFanout(ArrayList container,
			Integer current_time_instance) {
		super(container, current_time_instance);
	}

	/**
	 * Input: hashmap<taskid, deadline>, current time instance
	 * 
	 * @return true, if there is at least one task dead at next time instance
	 */
	private boolean containElementDeadAtNextTime(HashMap<Integer, Integer> s,
			int current_time_instance) {
		return s.values().contains(current_time_instance + 1)
				|| (current_time_instance == GeocrowdConstants.TIME_INSTANCE - 1);
	}

	/**
	 * Greedy algorithm.
	 * 
	 * @return the number of assigned workers
	 */
	@Override
	public HashSet<Integer> minSetCover() {
		ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) listOfSets
				.clone();
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();

		while (!Q.isEmpty()) {
			int bestWorkerIndex = 0; // track index of the best worker in S
			int maxUncoveredTasks = 0;

			/**
			 * Iterate over worker set to select the item set that maximize the
			 * uncovered tasks
			 */
			for (int k = 0; k < S.size(); k++) {
				HashMap<Integer, Integer> s = S.get(k);
				int uncoveredTasks = 0;
				/**
				 * check if the task with id i is covered by < k workers
				 */
				for (Integer i : s.keySet()) {
					if (assignedTaskMap.get(i) == null
							|| assignedTaskMap.get(i) < Geocrowd.taskList
									.get(i).getRequirement()) {
						uncoveredTasks++;
					}
				}

				/**
				 * check condition: only select workers that either cover 
				 * at least K (e.g.,= k=2,3..) tasks or
				 * cover any task that will not available in the next time instance
				 */
				if (uncoveredTasks > maxUncoveredTasks
						&& (uncoveredTasks >= Constants.M || containElementDeadAtNextTime(
								s, currentTimeInstance))) {
					maxUncoveredTasks = uncoveredTasks;
					bestWorkerIndex = k;
				}
			}

			assignWorkers.add(bestWorkerIndex);
			HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
			S.remove(taskSet);
			Q.removeAll(taskSet.keySet());

			for (Integer key : taskSet.keySet()) {
				if (!assignedTaskMap.keySet().contains(key))

					/**
					 * put task to assignedTaskMap
					 */
					assignedTaskMap.put(key, 1);
				else
					/**
					 * put taskid-number workers covered task to assignedTaskMap
					 */
					assignedTaskMap.put(key, assignedTaskMap.get(key) + 1);
			}
			
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

		/**
		 * Compute the number of (qualified) assigned tasks
		 */
		filterNonqualifedTasks();
		
		return assignWorkers;
	}
}
