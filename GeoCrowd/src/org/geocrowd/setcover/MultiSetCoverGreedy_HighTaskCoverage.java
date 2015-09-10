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
import java.util.Set;

import org.geocrowd.Geocrowd;
import org.geocrowd.datasets.params.GeocrowdConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedy.
 */
public class MultiSetCoverGreedy_HighTaskCoverage extends MultiSetCoverGreedy {

	public MultiSetCoverGreedy_HighTaskCoverage(ArrayList container,
			Integer current_time_instance) {
		super(container, current_time_instance);
		// TODO Auto-generated constructor stub
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
			int maxNoUncoveredTasks = 0;

			/**
			 * Iterate over worker set to select the item set that maximize the
			 * uncovered tasks
			 */
			for (int k = 0; k < S.size(); k++) {
				HashMap<Integer, Integer> s = S.get(k);
				int noUncoveredTasks = 0;
				/**
				 * check if the task with id i is covered by < k workers
				 */
				for (Integer i : s.keySet()) {
					if (assignedTaskMap.get(i) == null
							|| assignedTaskMap.get(i) < Geocrowd.taskList
									.get(i).getRequirement()) {
						noUncoveredTasks++;
					}
				}

				if (noUncoveredTasks > maxNoUncoveredTasks) {
					maxNoUncoveredTasks = noUncoveredTasks;
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
