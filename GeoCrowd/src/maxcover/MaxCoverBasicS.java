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
import org.geocrowd.common.crowdsource.GenericWorker;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Hien
 */
public class MaxCoverBasicS extends MaxCoverBasicT {

	private HashMap<Integer, Double> entropies;
	private ArrayList<GenericTask> taskList;
	public double alpha = Constants.alpha;
	public double maxRegionEntropy = 0;
//	public double maxNoUncoveredTasks = 0;
	public void setWorkerEntropies(HashMap<Integer, Double> entropies) {
		this.entropies = entropies;
	}

	public void setTaskList(ArrayList<GenericTask> taskList) {
		this.taskList = taskList;
	}
	

	public HashMap<Integer, Double> getWorkerEntropies() {
		return entropies;
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
	public WeightGain weight(int workeridx, HashMap<Integer, Integer> tasksWithDeadlines,
			int currentTI, HashSet<Integer> completedTasks) {
		/**
		 * denotes the number of unassigned tasks covered by worker
		 */
		int uncoveredTasks = 0;
		double regionEntropy = entropies.get(workeridx);
		
//		if (regionEntropy > maxRegionEntropy)
//			maxRegionEntropy = regionEntropy;
		

		for (Integer t : tasksWithDeadlines.keySet()) {
			/**
			 * Only consider uncovered tasks
			 */
			if (!completedTasks.contains(t))
				uncoveredTasks++;
		}
//		if (uncoveredTasks > maxNoUncoveredTasks) {
//			maxNoUncoveredTasks = uncoveredTasks;
//		}
		/**
		 * average region entropy of new covered tasks
		 */
		double weight = alpha * regionEntropy/maxRegionEntropy - (1-alpha)*uncoveredTasks/10.0;
//		System.out.println(regionEntropy/maxRegionEntropy + " " + uncoveredTasks/10.0);
//		System.out.println(weight);
		return new WeightGain(weight, uncoveredTasks);
	}
}
