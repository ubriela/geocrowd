package org.geocrowd.maxcover;

import static org.geocrowd.Geocrowd.candidateTaskIndices;
import static org.geocrowd.Geocrowd.taskList;
import static org.geocrowd.Geocrowd.workerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdTaskUtility;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdConstants;

/**
 * Change the stopping condition of max cover problem.
 *
 * The algorithm stop when the gain (i.e., the number of newly covered tasks) of
 * adding a worker is less than a threshold.
 *
 * @author ubriela
 *
 */
public class MaxCoverAdaptB extends MaxCover {

	public double lambda; // algorithm stops when gain is less than lambda
	public int deltaBudget;	// > 0 means over-utilization; < 0 otherwise
	public double epsGain;
	public double epsBudget;

	public MaxCoverAdaptB(ArrayList container, Integer currentTI) {
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
//		assignedTaskSet = new HashSet<Integer>();

		/**
		 * Run until either the gain of adding one worker is less than a
		 * threshold or no more tasks to cover
		 */
		while (assignWorkers.size() < budget && !Q.isEmpty()) {
			int bestWorkerIndex = 0; // track index of the best worker in S
			double maxUncoveredUtility = 0.0;
			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
			 */
			for (int k : S.keySet()) {
				GenericWorker w = workerList.get(k);
				HashMap<Integer, Integer> s = S.get(k); // task set covered by
				// current worker
				double uncoveredUtility = 0.0;
				for (Integer i : s.keySet()) {
					if (!assignedTaskSet.contains(i)) {
						SensingTask t = (SensingTask) taskList
								.get(candidateTaskIndices.get(i));
						double utility = GeocrowdTaskUtility.utility(Geocrowd.DATA_SET, w, t);
//						System.out.println(utility);
						uncoveredUtility += utility;
					}
				}
				if (uncoveredUtility > maxUncoveredUtility) {
					maxUncoveredUtility = uncoveredUtility;
					bestWorkerIndex = k;
				}
			}

			// Check gain threshold
			double deltaGain = maxUncoveredUtility - lambda;
			if (currentTimeInstance != GeocrowdConstants.TIME_INSTANCE - 1) {
				if (deltaGain <= 0 && deltaBudget <= 0) {
					break;	// stop allocating budget
				} else if (deltaGain <= 0 && deltaBudget > 0) {
					Random r = new Random();
					r.setSeed(System.nanoTime());
					
					if (r.nextFloat() < epsBudget)
						break;
				} else if (deltaGain > 0 && deltaBudget <= 0) {
					Random r = new Random();	
					r.setSeed(System.nanoTime());
						
					if (r.nextFloat() < epsGain)
						break;
				}
				
				// otherwise (deltaGain > 0 && deltaBudget < 0) --> increase budget by 1
			}

			deltaBudget -= 1;
			gain = maxUncoveredUtility;
			assignedUtility += gain;

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
							- (taskSet.get(taskidx) - GeocrowdConstants.MAX_TASK_DURATION)
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