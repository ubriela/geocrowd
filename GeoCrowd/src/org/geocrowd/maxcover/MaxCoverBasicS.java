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

import static org.geocrowd.Geocrowd.candidateTaskIndices;
import static org.geocrowd.Geocrowd.taskList;
import static org.geocrowd.Geocrowd.workerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.Constants;
import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdConstants;
import org.geocrowd.TaskUtility;
import org.geocrowd.common.crowd.GenericTask;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.crowd.SensingWorker;
import org.geocrowd.common.utils.Utils;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Hien
 */
public class MaxCoverBasicS extends MaxCoverBasicT {

	private HashMap<Integer, Double> worker_entropies;
	private HashMap<Integer, Double> task_entropies;
	private ArrayList<GenericTask> taskList;
	public double alpha = Constants.alpha;
	public double maxEntropy = 0.0;
	public double totalEntropy = 0.0;
	public double meanEntropy = 0.0;

	// public double maxNoUncoveredTasks = 0;

	public void setWorkerEntropies(HashMap<Integer, Double> worker_entropies) {
		this.worker_entropies = worker_entropies;
	}

	public void setTaskEntropies(HashMap<Integer, Double> task_entropies) {
		this.task_entropies = task_entropies;
	}

	public void setTaskList(ArrayList<GenericTask> taskList) {
		this.taskList = taskList;
	}

	public HashMap<Integer, Double> getWorkerEntropies() {
		return worker_entropies;
	}

	public ArrayList<GenericTask> getTaskList() {
		return taskList;
	}

	/**
	 * Instantiates a new sets the cover greedy combine deadline.
	 * 
	 * @param container
	 *            the container
	 * @param current_time_instance
	 *            the current_time_instance
	 */

	public MaxCoverBasicS(ArrayList container, Integer currentTI) {
		super(container, currentTI);
	}

	/**
	 * maxCover inherits from MaxCoverT class
	 */

	/**
	 * At each stage, chooses the worker whose covering unassigned tasks have
	 * smallest Average Region Entropy
	 * 
	 * @param tasksWithDeadlines
	 *            <taskid, deadline>
	 * @param currentTI
	 * @param completedTasks
	 *            [taskid]
	 * @return
	 */

	@Override
	public WeightGain weight(int workeridx,
			HashMap<Integer, Integer> tasksWithDeadlines, int currentTI,
			HashSet<Integer> completedTasks) {
		/**
		 * denotes the number of unassigned tasks covered by worker
		 */
		double uncoveredUtility = 0.0;
		double totalTaskEntropy = 0;

		for (Integer taskIdx : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(taskIdx)) {
				GenericWorker worker = workerList.get(workeridx);
				SensingTask task = (SensingTask) taskList
						.get(candidateTaskIndices.get(taskIdx));
				double utility = TaskUtility.utility(Geocrowd.DATA_SET, worker, task);
				uncoveredUtility += utility;
				
				if (task_entropies.get(taskIdx) != 0) {
					// System.out.println(meanEntropy + " " + maxEntropy + " " +
					// task_entropies.get(taskIdx));
					
					totalTaskEntropy += utility / (1 + task_entropies.get(taskIdx));
				}
			}
		}
		/**
		 * average region entropy of new covered tasks
		 */
		double weight = -totalTaskEntropy;
		// System.out.println(regionEntropy/maxRegionEntropy + " " +
		// uncoveredTasks/10.0);
//		 System.out.println(uncoveredUtility);
		return new WeightGain(weight, uncoveredUtility);
	}

	@Override
	public WeightGain weight_old(int workeridx,
			HashMap<Integer, Integer> tasksWithDeadlines, int currentTI,
			HashSet<Integer> completedTasks) {
		/**
		 * denotes the number of unassigned tasks covered by worker
		 */
		int uncoveredTasks = 0;
		double regionWorkerEntropy = worker_entropies.get(workeridx);

		// if (regionEntropy > maxRegionEntropy)
		// maxRegionEntropy = regionEntropy;

		for (Integer t : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(t))
				uncoveredTasks++;
		}
		// if (uncoveredTasks > maxNoUncoveredTasks) {
		// maxNoUncoveredTasks = uncoveredTasks;
		// }
		/**
		 * average region entropy of new covered tasks
		 */
		double weight = alpha * regionWorkerEntropy / maxEntropy - (1 - alpha)
				* uncoveredTasks / 10.0;
		// System.out.println(regionEntropy/maxRegionEntropy + " " +
		// uncoveredTasks/10.0);
		// System.out.println(weight);
		return new WeightGain(weight, uncoveredTasks);
	}
}
