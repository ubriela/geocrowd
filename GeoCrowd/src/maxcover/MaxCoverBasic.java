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
package maxcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.common.Constants;

/**
 * The Class SetCoverGreedy.
 * 
 * The budget k is fixed, algorithm stops when running out of budget
 * 
 */
public class MaxCoverBasic extends MaxCover {

	public MaxCoverBasic(ArrayList container,
			Integer currentTI) {
		super(container, currentTI);
	}

	/**
	 * Greedy algorithm.
	 * 
	 * @return the assigned workers
	 */
	@Override
	public HashSet<Integer> maxCover() {
		ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) listOfSets
				.clone();

		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
		assignedTaskSet = new HashSet<Integer>();

		/**
		 * Run until either running out of budget or no more tasks to cover
		 */
		while (assignWorkers.size() < k && !Q.isEmpty()) {
			int bestWorkerIndex = 0; // track index of the best worker in S
			int maxNoUncoveredTasks = 0;
			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
			 */
			for (int k = 0; k < S.size(); k++) {
				HashMap<Integer, Integer> s = S.get(k); // task set covered by
														// current worker
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

			
			System.out.println(S.get(bestWorkerIndex));
			System.out.println(maxNoUncoveredTasks);
			
			assignWorkers.add(bestWorkerIndex);
			HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
			S.remove(taskSet);
			Q.removeAll(taskSet.keySet());

			/**
			 * compute average time to assign tasks in taskSet
			 */
			for (Integer taskidx : taskSet.keySet())
				if (!assignedTaskSet.contains(taskidx)) {

					averageDelayTime += currentTimeInstance
							- (taskSet.get(taskidx) - Constants.TaskDuration)
							+ 1;
					assignedTaskSet.add(taskidx);
				}
		}
		assignedTasks = assignedTaskSet.size();
		System.out.println("#Task assigned: " + assignedTasks);

		return assignWorkers;
	}
}
