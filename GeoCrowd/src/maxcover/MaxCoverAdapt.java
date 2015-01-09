package maxcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.geocrowd.common.Constants;

/**
 * Change the stopping condition of max cover problem.
 *
 * The algorithm stop when the gain (i.e., the number of newly covered tasks) of
 * adding a worker is less than a threshold.
 *
 * @author ubriela
 *
 */
public class MaxCoverAdapt extends MaxCover {

	public double lambda; // algorithm stops when gain is less than lambda
	public int deltaBudget;
	public double eps;

	public MaxCoverAdapt(ArrayList container, Integer currentTI) {
		super(container, currentTI);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Greedy algorithm.
	 *
	 * @return the assigned workers
	 */
	@Override
	public HashSet<Integer> maxCover() {
		HashMap<Integer, HashMap<Integer, Integer>> S = (HashMap<Integer, HashMap<Integer, Integer>>) mapSets
				.clone();

		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
		assignedTaskSet = new HashSet<Integer>();

		/**
		 * Run until either the gain of adding one worker is less than a
		 * threshold or no more tasks to cover
		 */
		while (assignWorkers.size() < budget && !Q.isEmpty()) {
			int bestWorkerIndex = 0; // track index of the best worker in S
			int maxNoUncoveredTasks = 0;
			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
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
					bestWorkerIndex = k;
				}
			}

			// Check gain threshold
			double deltaGain = maxNoUncoveredTasks - lambda;
			deltaBudget -= 1;
			if (currentTimeInstance != Constants.TIME_INSTANCE - 1) {
				if (deltaGain < 0 && deltaBudget < 0) {
					break;
				} else if (deltaGain < 0 && deltaBudget > 0) {
//					System.out.println("xxxxx");
					Random r = new Random();
					r.setSeed(System.nanoTime());
					if (r.nextFloat() < 0.5)
						break;
				} else if (deltaGain > 0 && deltaBudget < 0) {
					Random r = new Random();	
					r.setSeed(System.nanoTime());
					if (r.nextFloat() < eps)
						break;
				}
			}

			gain = maxNoUncoveredTasks;

			assignWorkers.add(bestWorkerIndex);
			HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
			S.remove(bestWorkerIndex);
			Q.removeAll(taskSet.keySet());

			/**
			 * compute average time to assign tasks in taskSet
			 */
			for (Integer taskidx : taskSet.keySet()) {
				if (!assignedTaskSet.contains(taskidx)) {

					averageDelayTime += currentTimeInstance
							- (taskSet.get(taskidx) - Constants.TaskDuration)
							+ 1;
					assignedTaskSet.add(taskidx);
				}
			}
		}
		assignedTasks = assignedTaskSet.size();
		// System.out.println("#Task assigned: " + assignedTasks);

		return assignWorkers;
	}
}
