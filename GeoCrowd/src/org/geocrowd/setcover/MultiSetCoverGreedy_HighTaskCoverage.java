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
import org.geocrowd.common.Constants;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedy.
 */
public class MultiSetCoverGreedy_HighTaskCoverage extends SetCoverGreedy {

	/**
	 * <taskid, the number of workers cover the task with that id>
	 */
	public HashMap<Integer, Integer> assignedTaskMap = new HashMap<>();

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
			HashMap<Integer, Integer> maxSet = null;
			int maxElem = 0;
			/**
			 * Iterate over worker set to select the item set that maximize
			 * the uncovered tasks
			 */
			for (int k = 0; k < S.size(); k++) {
				HashMap<Integer, Integer> s = S.get(k);
				int noIncompletedTasks = 0;
				/**
				 * check if the task with id i is covered by < k workers
				 */
				for (Integer i : s.keySet()) {
					if (assignedTaskMap.get(i) == null
							|| assignedTaskMap.get(i) < Geocrowd.taskList
									.get(i).getK()) {
						noIncompletedTasks++;
					}
				}
				if (noIncompletedTasks > maxElem) {
					maxElem = noIncompletedTasks;
					maxSet = s;
					assignWorkers.add(k);
				}

			}

			S.remove(maxSet);
			Q.removeAll(maxSet.keySet());

			Set assignedSet = maxSet.keySet();
			for (Object kt : assignedSet) {
				Integer key = (Integer) kt;
				if (!assignedTaskMap.keySet().contains(key)) {

					// put task to assignedTaskMap
					assignedTaskMap.put(key, 1);
				} else {
					// put taskid-number workers covered task to assignedTaskMap
					assignedTaskMap.put(key, assignedTaskMap.get(key) + 1);
				}
			}
			// compute average time to assign tasks in maxSet

		}

		/**
		 * a set of covered tasks.
		 */
		assignedTaskSet = new HashSet<Integer>();

		// compute assignedTasks
		for (Integer key : assignedTaskMap.keySet()) {
			// task is assigned only when number workers covered it >= k
			if (assignedTaskMap.get(key) >= Geocrowd.taskList.get(key).getK())
				assignedTaskSet.add(key);
		}
		assignedTasks = assignedTaskSet.size();
		System.out.println("#Task assigned: " + assignedTasks);

		return assignWorkers;
	}
}
