/*******************************************************************************
 * @ Year 2013
 * This is the source code of the following papers. 
 * 
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla Kazemi, Cyrus Shahabi.
 * 
 * 
 * Please contact the author Hien To, ubriela@gmail.com if you have any question.
 *
 * Contributors:
 * Hien To - initial implementation
 *******************************************************************************/

package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.geocrowd.common.Constants;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Luan
 */
public class SetCoverGreedy_CloseToDeadline extends SetCoverGreedy {

	private int k = 3;

	/**
	 * Instantiates a new sets the cover greedy combine deadline.
	 * 
	 * @param container
	 *            the container
	 * @param current_time_instance
	 *            the current_time_instance
	 */
	
	public SetCoverGreedy_CloseToDeadline(ArrayList container,
			Integer current_time_instance) {
		super(container, current_time_instance);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geocrowd.setcover.SetCoverGreedyWaitTillDeadline#minSetCover()
	 */
	@Override
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
			double smallestAvgTimeToDead = 10000000;

			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
			 */
			for (int j = 0; j < S.size(); j++) {
				HashMap<Integer, Integer> s = S.get(j);
				double avgTimeToDead = weight(s, currentTimeInstance,
						assignedTaskSet);
				if (avgTimeToDead < smallestAvgTimeToDead
						&& (avgTimeToDead <= k || currentTimeInstance == Constants.TIME_INSTANCE - 1)) {
					smallestAvgTimeToDead = avgTimeToDead;
					bestWorkerIndex = j;
				}
			}
			if (bestWorkerIndex == -1)
				break;

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
							- (taskSet.get(taskId) - Constants.TaskDuration)
							+ 1;
					assignedTaskSet.add(taskId);
				}
			}
		}
		assignedTasks = assignedTaskSet.size();
		System.out.println("#Task assigned: " + assignedTasks);
		return assignWorkers;
	}

	/**
	 * larger average time to dead for its covered task, smaller worker's weight
	 * 
	 * 
	 */
	private double weight(HashMap<Integer, Integer> s,
			int current_time_instance, HashSet<Integer> C) {
		double w = 0;
		int numElem = 0;
		double d = 0;
		for (Integer t : s.keySet()) {
			if (!C.contains(t)) {
				if (s.get(t) - current_time_instance == 1) // If worker cover
															// task will dead at
															// next time
															// instance
					return 1;
				numElem++;
				d += s.get(t) - current_time_instance;
			}
		}
		/**
		 *  average time to deadline of new covered task
		 */
		return d / numElem; 
	}
}
