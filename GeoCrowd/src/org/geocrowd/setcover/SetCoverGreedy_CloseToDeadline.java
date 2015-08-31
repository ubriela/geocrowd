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

import org.geocrowd.Constants;
import org.geocrowd.GeocrowdConstants;
import org.geocrowd.common.crowd.GenericTask;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Luan
 */
public class SetCoverGreedy_CloseToDeadline extends SetCoverGreedy {

	public HashMap<GenericTask, Double> entropies;
	private ArrayList<GenericTask> taskList;
	
	public void setEntropies(HashMap<GenericTask, Double> entropies) {
		this.entropies = entropies;
	}
	
	public void setTaskList(ArrayList<GenericTask> taskList) {
		this.taskList = taskList;	
	}
	
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
				double avgTimeToDead = avgTimeToDead(s, currentTimeInstance,
						assignedTaskSet);
				if (avgTimeToDead < smallestAvgTimeToDead
						&& (avgTimeToDead <= GeocrowdConstants.TaskDuration || currentTimeInstance == GeocrowdConstants.TIME_INSTANCE - 1)) {
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
							- (taskSet.get(taskId) - GeocrowdConstants.TaskDuration)
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
	 * @param tasksWithDeadlines <taskid, deadline>
	 * @param current_time_instance
	 * @param completedTasks [taskid]
	 * @return
	 */
	private double avgTimeToDead(HashMap<Integer, Integer> tasksWithDeadlines,
			int current_time_instance, HashSet<Integer> completedTasks) {
		int uncoveredTasks = 0;
		double totalElapsedTime = 0;
		for (Integer t : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(t)) {
				/**
				 * if the task will dead at next time instance, return 1 so that it will be assigned
				 */
				if (tasksWithDeadlines.get(t) - current_time_instance == 1)
					return 1;
				uncoveredTasks++;
				double elapsedTime = tasksWithDeadlines.get(t) - current_time_instance; // the smaller, the better
				
				if (Constants.useLocationEntropy) {
					elapsedTime = elapsedTime*entropies.get(taskList.get(t));
				}
				totalElapsedTime += elapsedTime;
			}
		}
		/**
		 * average time to deadline of new covered task
		 */
		return totalElapsedTime / uncoveredTasks;
	}
}
