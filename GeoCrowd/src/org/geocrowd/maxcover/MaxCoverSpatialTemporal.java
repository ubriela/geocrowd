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
import java.util.Set;

import org.geocrowd.common.crowd.GenericTask;
import org.geocrowd.datasets.params.GeocrowdConstants;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Hien
 */
public class MaxCoverSpatialTemporal extends MaxCoverSpatial {

	/**
	 * Instantiates a new sets the cover greedy combine deadline.
	 * 
	 * @param container
	 *            the container
	 * @param current_time_instance
	 *            the current_time_instance
	 */

	public MaxCoverSpatialTemporal(ArrayList container, Integer currentTI) {
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
	public WeightGain weight(int workeridx, HashMap<Integer, Integer> tasksWithDeadlines,
			int currentTI, HashSet<Integer> completedTasks) {
		/**
		 * denotes the number of unassigned tasks covered by worker
		 */
		int uncoveredTasks = 0;
		double regionEntropy = getWorkerEntropies().get(workeridx);
		double totalElapsedTime = 0;
		
		if (regionEntropy > maxEntropy)
			maxEntropy = regionEntropy;
		
		for (Integer t : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(t)) {
				/**
				 * if the task will dead at next time instance, return
				 * 1 so that it will be assigned
				 */
//				if (tasksWithDeadlines.get(t) - currentTI == 1)
//					return 1;
				uncoveredTasks++;

				// the smaller the better
				double elapsedTime = tasksWithDeadlines.get(t) - currentTI;
				totalElapsedTime += elapsedTime;
			}
		}
		/**
		 * At each stage, chooses the worker based on a linear combination of spatial and temporal
		 */
		
//		System.out.println(maxRegionEntropy);
		double weight = 0.15*totalElapsedTime / (GeocrowdConstants.MAX_TASK_DURATION*uncoveredTasks) + 0.05*regionEntropy/maxEntropy - 0.8*uncoveredTasks/10.0;
//		System.out.println(weight);
		return new WeightGain(weight, uncoveredTasks);
	}
}