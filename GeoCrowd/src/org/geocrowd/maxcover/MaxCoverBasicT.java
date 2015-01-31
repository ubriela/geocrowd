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

package org.geocrowd.maxcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.Constants;
import org.geocrowd.GeocrowdConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Hien
 */
public class MaxCoverBasicT extends MaxCover {

	/**
	 * Instantiates a new sets the cover greedy combine deadline.
	 * 
	 * @param container
	 *            the container
	 * @param current_time_instance
	 *            the currentTI
	 */
	public double alpha = Constants.alpha;
	public int maxNoUncoveredTasks = 0;

	public MaxCoverBasicT(ArrayList container, Integer currentTI) {
		super(container, currentTI);
	}

	@Override
	public HashSet<Integer> maxCover() {
		HashMap<Integer, HashMap<Integer, Integer>> S = (HashMap<Integer, HashMap<Integer, Integer>>) mapSets
				.clone();
		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
		/**
		 * store all assigned tasks
		 */
//		assignedTaskSet = new HashSet<Integer>();
		while (assignWorkers.size() < budget && !Q.isEmpty()) {
			int bestWorkerIndex = 0;
			double smallestAvgTimeToDead = 10000000;

			/**
			 * find the maximum of number of uncovered task
			 */
			for (int k : S.keySet()) {
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
				}
			}
			/**
			 * Iterate all workers, find the one with the smallest weight
			 */
			for (int k : S.keySet()) {
				HashMap<Integer, Integer> s = S.get(k);
				WeightGain wg = weight(k, s, currentTimeInstance,
						assignedTaskSet);
                               
				if (wg.weight < smallestAvgTimeToDead) {
					smallestAvgTimeToDead = wg.weight;
					bestWorkerIndex = k;
				}
			}
//                        System.out.println("weight:"+smallestAvgTimeToDead);
                        
			assignWorkers.add(bestWorkerIndex);

//			System.out.println(S.size() + " " + bestWorkerIndex);
			HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
//                        System.out.println("Task set size: "+taskSet.size());
			S.remove(bestWorkerIndex);
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
//		System.out.println(universe.size() + "\t" + assignedTasks  + "\t" + assignWorkers.size() + "\t"  + assignedTasks/assignWorkers.size() );
		return assignWorkers;
	}

	/**
	 * At each stage, chooses the worker whose covering unassigned tasks have
	 * smallest Average Time-to-Deadline
	 * 
	 * @param tasksWithDeadlines
	 *            <taskid, deadline>
	 * @param currentTI
	 * @param completedTasks
	 *            [taskid]
	 * @return
	 */
	public WeightGain weight(int workeridx, HashMap<Integer, Integer> tasksWithDeadlines,
			int currentTI, HashSet<Integer> completedTasks) {
		/**
		 * denotes the number of unassigned tasks covered by worker
		 */
		int uncoveredTasks = 0;
		double totalElapsedTime = 0;
		for (Integer t : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(t)) {
				uncoveredTasks++;
				double elapsedTime = tasksWithDeadlines.get(t) - currentTI; // the
																			// smaller,
																			// the
																			// better
//				System.out.println(elapsedTime);
				totalElapsedTime += 1/(1 + elapsedTime);
			}
		}
		/**
		 * average time to deadline of new covered task
		 */
		double weight = -totalElapsedTime;
		return new WeightGain(weight, uncoveredTasks);
	}
	
	
	public WeightGain weight_old(int workeridx, HashMap<Integer, Integer> tasksWithDeadlines,
			int currentTI, HashSet<Integer> completedTasks) {
		/**
		 * denotes the number of unassigned tasks covered by worker
		 */
		int uncoveredTasks = 0;
		double totalElapsedTime = 0;
		for (Integer t : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(t)) {
				uncoveredTasks++;
				double elapsedTime = tasksWithDeadlines.get(t) - currentTI; // the
																			// smaller,
																			// the
																			// better
				totalElapsedTime += elapsedTime;
			}
		}
		/**
		 * average time to deadline of new covered task
		 */
		double weight = alpha*totalElapsedTime / (GeocrowdConstants.TaskDuration*uncoveredTasks) - (1-alpha) * uncoveredTasks/maxNoUncoveredTasks;
		return new WeightGain(weight, uncoveredTasks);
	}
	

	
}
