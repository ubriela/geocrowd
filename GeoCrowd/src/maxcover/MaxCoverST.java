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

package maxcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.GenericTask;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Hien
 */
public class MaxCoverST extends MaxCoverS {

	/**
	 * Instantiates a new sets the cover greedy combine deadline.
	 * 
	 * @param container
	 *            the container
	 * @param current_time_instance
	 *            the current_time_instance
	 */

	public MaxCoverST(ArrayList container, Integer currentTI) {
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
	public WeightGain weight(HashMap<Integer, Integer> tasksWithDeadlines,
			int currentTI, HashSet<Integer> completedTasks) {
		/**
		 * denotes the number of unassigned tasks covered by worker
		 */
		int uncoveredTasks = 0;
		double totalEntropy = 0;
		double totalElapsedTime = 0;
		for (Integer t : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(t)) {
				/**
				 * !!!!!!!!! if the task will dead at next time instance, return
				 * 1 so that it will be assigned
				 */
//				if (tasksWithDeadlines.get(t) - currentTI == 1)
//					return 1;
				uncoveredTasks++;

				totalEntropy += getEntropies().get(getTaskList().get(t));

				// the smaller the better
				double elapsedTime = tasksWithDeadlines.get(t) - currentTI;
				totalElapsedTime += elapsedTime;
			}
		}
		/**
		 * At each stage, chooses the worker based on a linear combination of
		 * ATD and ARE
		 */
		double ATD = totalElapsedTime*1.0 / uncoveredTasks;
		double ARE = totalEntropy *1.0/ uncoveredTasks;
		double alpha = 0.5;
		double maxATD = 10;
		double maxARE = 10;
		double weight = alpha*ATD/maxATD + (1-alpha)*ARE/maxARE;
		return new WeightGain(weight, uncoveredTasks);
	}
}