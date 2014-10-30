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
import org.geocrowd.common.Constants;

/**
 * @author Luan
 */
public class SetCoverGreedy_LargeWorkerFanout extends SetCoverGreedy {

	/**
	 * The average tasks per worker.
	 */
	public double averageTasksPerWorker;

	/**
	 * The average workers per task.
	 */
	public double averageWorkersPerTask;

	public SetCoverGreedy_LargeWorkerFanout(ArrayList container,
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
				|| (current_time_instance == Constants.TIME_INSTANCE - 1);
	}

	/**
	 * Greedy algorithm.
	 * 
	 * @return number of assigned workers
	 */
	public HashSet<Integer> minSetCover() {
		ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) listOfSets
				.clone();
		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
		/**
		 * store all assigned tasks
		 */
		assignedTaskSet = new HashSet<Integer>();

		while (!Q.isEmpty()) {
			int bestWorkerIndex = -1;
			int maxNoUncoveredTask = 0;

			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
			 */
			for (int j = 0; j < S.size(); j++) {
				HashMap<Integer, Integer> s = S.get(j);
				int noUncoveredTasks = 0;
				for (Integer i : s.keySet()) {
					if (!assignedTaskSet.contains(i)) {
						noUncoveredTasks++;
					}
				}
				/**
				 * check condition: only select workers that either cover 
				 * at least K (e.g.,= k=2,3..) tasks or
				 * cover any task that will not available in the next time instance
				 */
				if (noUncoveredTasks > maxNoUncoveredTask
						&& (noUncoveredTasks >= Constants.M || containElementDeadAtNextTime(
								s, currentTimeInstance))) 
				{
					maxNoUncoveredTask = noUncoveredTasks;
					bestWorkerIndex = j;

				}
			}
			
			/**
			 * why this?
			 */
			if (bestWorkerIndex == -1) {
				break;
			}
			assignWorkers.add(bestWorkerIndex);

			HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
			S.remove(taskSet);
			Q.removeAll(taskSet.keySet());
			/**
			 * compute average time to assign tasks in taskSet
			 */
			for (Integer taskId : taskSet.keySet()) {
				if (!assignedTaskSet.contains(taskId)) {

					averageDelayTime += currentTimeInstance
							- (taskSet.get(taskId) - Constants.TaskDuration) + 1;
					assignedTaskSet.add(taskId);
				}
			}

		}

		assignedTasks = assignedTaskSet.size();
		System.out.println("#Task assigned: " + assignedTasks);
		return assignWorkers;
	}
}
