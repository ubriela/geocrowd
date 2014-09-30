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
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
		assignedTaskSet = new HashSet<Integer>();

		int set_size = S.size();

		while (!Q.isEmpty()) {
			HashMap<Integer, Integer> maxSet = null;
			int maxElem = 0;
			for (int k = 0; k < S.size(); k++) {
				HashMap<Integer, Integer> s = S.get(k);
				int newElem = 0;
				for (Integer i : s.keySet()) {
					if (!assignedTaskSet.contains(i)) {
						newElem++;
					}
				}
				if (newElem > maxElem) {
					maxElem = newElem;
					maxSet = s;
					assignWorkers.add(k);
				}

			}

			S.remove(maxSet);
			Q.removeAll(maxSet.keySet());

			Set assignedSet = maxSet.keySet();
			for (Object kt : assignedSet) {
				Integer key = (Integer) kt;
				if (!assignedTaskSet.contains(key)) {

					averageTime += currentTimeInstance
							- (maxSet.get(key) - Constants.TaskDuration) + 1;
					assignedTaskSet.add(key);
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
