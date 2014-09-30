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
import org.geocrowd.common.Constants;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedy.
 */
public class SetCoverGreedy_HighTaskCoverage extends SetCoverGreedy {

	public SetCoverGreedy_HighTaskCoverage(ArrayList container,
			Integer current_time_instance) {
		super(container, current_time_instance);
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
		
		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
		assignedTaskSet = new HashSet<Integer>();

		/**
		 * Run until no more tasks to cover
		 */
		while (!Q.isEmpty()) {
			int bestWorkerIndex = 0;	// track index of the best worker in S
			int maxNoUncoveredTasks = 0;
			/**
			 * Iterate all workers, find the one which covers maximum number of uncovered tasks
			 */
			for (int k = 0; k < S.size(); k++) {
				HashMap<Integer, Integer> s = S.get(k);	// task set covered by current worker
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
			
			assignWorkers.add(bestWorkerIndex);
			HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
			S.remove(taskSet);
			Q.removeAll(taskSet.keySet());

			for (Integer taskidx : taskSet.keySet()) {
				if (!assignedTaskSet.contains(taskidx)) {

					averageDelayTime += currentTimeInstance
							- (taskSet.get(taskidx) - Constants.TaskDuration) + 1;
					assignedTaskSet.add(taskidx);
				}
			}
			// compute average time to assign tasks in maxSet

		}
		assignedTasks = assignedTaskSet.size();
		// averageTime = averageTime*1.0/assignedTasks;
		System.out.println("#Task assigned: " + assignedTasks);

		return assignWorkers;
	}
}
