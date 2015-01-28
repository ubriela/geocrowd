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
 * *****************************************************************************
 */
package org.geocrowd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.GenericWorker;
import org.geocrowd.common.crowdsource.SensingTask;
import org.geocrowd.common.crowdsource.VirtualWorker;
import org.geocrowd.setcover.MultiSetCoverGreedy_CloseToDeadline;
import org.geocrowd.setcover.MultiSetCoverGreedy_LargeWorkerFanout;
import org.geocrowd.setcover.SetCoverGreedy;
import org.geocrowd.setcover.MultiSetCoverGreedy_HighTaskCoverage;
import org.geocrowd.setcover.SetCoverGreedy_CloseToDeadline;
import org.geocrowd.setcover.SetCoverGreedy_HighTaskCoverage;
import org.geocrowd.setcover.SetCoverGreedy_LargeWorkerFanout;
import org.geocrowd.setcover.SetCoverGreedy_LowWorkerCoverage;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import org.geocrowd.datasets.Parser;

// TODO: Auto-generated Javadoc
/**
 * The Class Crowdsensing. Used to find the minimum number of workers that cover
 * maximum number of tasks. First, the class fetches workers and tasks by
 * readTasks and readWorkers. These functions fetch worker and task information
 * into workerList and taskList. Then, function matchingTasksWorkers is
 * executed, which compute task set covered by any worker (i.e., container).
 * Note that this function also compute the invertedContainer.
 *
 * The main function minimizeWorkersMaximumTaskCoverage compute maximum
 *
 */
public class GeocrowdSensing extends Geocrowd {

	/**
	 * Ranked by the size of worker set
	 */
	PriorityQueue<VirtualWorker> vWorkerList;

	/**
	 * store all virtual workers to easily get k-th element. similar role to
	 * workerList for redundant task assignment
	 */
	VirtualWorker[] vWorkerArray;

	/**
	 * Gets an array of workers, each worker is associated with a hashmap
	 * <taskid, deadline>.
	 *
	 * The order of the workers in is the same as in containerWorker
	 *
	 * @return a container with task deadline
	 */
	public ArrayList<HashMap<Integer, Integer>> getContainerWithDeadline() {
		ArrayList<HashMap<Integer, Integer>> containerWithDeadline = new ArrayList<>();
		Iterator it = containerWorker.iterator();
		while (it.hasNext()) {
			ArrayList taskids = (ArrayList) it.next();
			Iterator it2 = taskids.iterator();
			HashMap<Integer, Integer> taskidsWithDeadline = new HashMap();
			while (it2.hasNext()) {
				Integer taskid = (Integer) it2.next();
				taskidsWithDeadline.put(taskid,
						taskList.get(candidateTaskIndices.get(taskid))
								.getEntryTime() + GeocrowdConstants.TaskDuration);
			}
			containerWithDeadline.add(taskidsWithDeadline);
		}
		return containerWithDeadline;
	}

	/**
	 * Compute which worker within which task region and vice versa. Also remove
	 * workers with no tasks
	 */
	@Override
	public void matchingTasksWorkers() {
		invertedContainer = new HashMap<Integer, ArrayList>();
		candidateTaskIndices = new ArrayList();
		taskSet = new HashSet<Integer>();
		containerWorker = new ArrayList<>();
		containerPrune = new ArrayList[workerList.size()];

		// remove expired task from task list
		pruneExpiredTasks();
		for (int workeridx = 0; workeridx < workerList.size(); workeridx++) {
			reverseRangeQuery(workeridx);
		}

		// remove workers with no tasks
		for (int i = containerPrune.length - 1; i >= 0; i--) {
			if (containerPrune[i] == null || containerPrune[i].size() == 0) {
				workerList.remove(i);
			}
		}
		for (int i = 0; i < containerPrune.length; i++) {
			if (containerPrune[i] != null && containerPrune[i].size() > 0) {
				containerWorker.add(containerPrune[i]);
			}
		}
		/**
		 * update invertedContainer <taskid, ArrayList<workerIndex>>
		 */
		for (int tid = 0; tid < taskList.size(); tid++) {
			for (int i = 0; i < containerWorker.size(); i++) {
				final int workerIndex = i;
				if (containerWorker.get(workerIndex).contains(tid)) {
					if (!invertedContainer.containsKey(tid)) {
						invertedContainer.put(tid, new ArrayList() {
							{
								add(workerIndex);
							}
						});
					} else {
						invertedContainer.get(tid).add(workerIndex);
					}
				}
			}
		}

	}

	final Comparator<GenericTask> TASK_ORDER = new Comparator<GenericTask>() {
		public int compare(GenericTask t1, GenericTask t2) {
			if (t1.getK() > t2.getK()) {
				return -1;
			} else if (t1.getK() < t2.getK()) {
				return 1;
			} else {
				int idx1 = taskList.indexOf(t1);
				int idx2 = taskList.indexOf(t2);
				HashSet<Integer> workerIdxs1 = null;
				if (invertedContainer.containsKey(idx1)) {
					workerIdxs1 = new HashSet<Integer>(
							invertedContainer.get(idx1));
				}
				HashSet<Integer> workerIdxs2 = null;
				if (invertedContainer.containsKey(idx2)) {
					workerIdxs2 = new HashSet<Integer>(
							invertedContainer.get(idx2));
				}

				if (workerIdxs1 != null) {
					if (workerIdxs2 != null) {
						if (workerIdxs1.size() > workerIdxs2.size()) {
							return -1;
						} else if (workerIdxs1.size() < workerIdxs2.size()) {
							return 1;
						} else {
							return 0;
						}
					} else {
						return -1;
					}
				} else {
					if (workerIdxs2 != null) {
						return 1;
					} else {
						return 0;
					}
				}
			}
		}
	};

	Funnel<VirtualWorker> vworkerFunnel = new Funnel<VirtualWorker>() {
		@Override
		public void funnel(VirtualWorker w, PrimitiveSink into) {
			// into.putInt(w.getWorkerIds().hashCode());
			for (Integer i : w.getWorkerIds()) {
				into.putInt(i);
			}
		}
	};

	/**
	 * Virtual workers includes a set of worker ids that cover the same task.
	 *
	 * This function is only used for the case of redundant task assignment.
	 *
	 * Input: bipartite graph, in which each task has a parameter K - the number
	 * of of needed task responses.
	 *
	 * Output: container of virtual workers, similar to containerWorker so that
	 * this function is plug-able
	 */
	@SuppressWarnings("unchecked")
	public void populateVitualWorkers() {

		/**
		 * sort tasks by k in descending order *
		 */
		ArrayList<GenericTask> sortedTaskList = (ArrayList<GenericTask>) taskList
				.clone();
		Collections.sort(sortedTaskList, TASK_ORDER);

		HashMap<GenericTask, Integer> mapTaskIndices = new HashMap<GenericTask, Integer>();
		for (GenericTask t : sortedTaskList) {
			mapTaskIndices.put(t, taskList.indexOf(t));
		}

		/**
		 * create virtual worker, using priority queue
		 */
		BloomFilter<VirtualWorker> bf = BloomFilter.create(vworkerFunnel,
				1000000, 0.01);
		vWorkerList = new PriorityQueue<>();

		HashMap<Integer, ArrayList<SensingTask>> traversedTasks = new HashMap<Integer, ArrayList<SensingTask>>();
		int i = 0;
		for (final GenericTask t : sortedTaskList) {
			// get workers cover task
			int idx = mapTaskIndices.get(t);
			System.out.println("#task = " + i++ + " #k=" + t.getK()
					+ " $vworkers = " + vWorkerList.size());

			ArrayList<Integer> workerIdxs = null;
			if (invertedContainer.containsKey(idx)) {
				workerIdxs = invertedContainer.get(idx);
			}
			/* remove tasks that are not covered by any worker */
			if (workerIdxs == null) {
				continue;
			}
			/**
			 * remove tasks that are covered by less than k workers
			 */
			if (t.getK() > workerIdxs.size()) {
				continue;
			}

			/**
			 * Check condition 1: if the worker set of a task of k responses is
			 * covered by the worker set of another traversed task of the same k
			 */
			boolean isContinue = false;
			if (traversedTasks.containsKey(t.getK())) {
				for (SensingTask st : traversedTasks.get(t.getK())) {
					int idxj = mapTaskIndices.get(st);
					ArrayList<Integer> workerIdxsj = null;
					if (invertedContainer.containsKey(idxj)) {
						workerIdxsj = invertedContainer.get(idxj);
					}

					if (workerIdxsj != null
							&& workerIdxsj.containsAll(workerIdxs)) {
						isContinue = true;
						break;
					}
				}

				traversedTasks.get(t.getK()).add((SensingTask) t);
			} else {
				traversedTasks.put(t.getK(), new ArrayList<SensingTask>() {
					{
						add((SensingTask) t);
					}
				});
			}

			if (isContinue) {
				continue;
			}

			/**
			 * Check condition 2 (not often happen): if the worker set is
			 * covered by an existing logical worker --> do not need to worry
			 * about
			 */
			ICombinatoricsVector<Integer> initialVector = Factory
					.createVector(workerIdxs);

			/**
			 * Create a multi-combination generator to generate 3-combinations
			 * of the initial vector
			 */
			Generator<Integer> gen = Factory.createSimpleCombinationGenerator(
					initialVector, t.getK());

			long start = System.nanoTime();

			/**
			 * Do not need to check if the first set
			 */
			if (vWorkerList.size() == 0) {
				for (ICombinatoricsVector<Integer> r : gen) {
					vWorkerList.add(new VirtualWorker(r.getVector()));
					bf.put(new VirtualWorker(r.getVector()));
				}
				continue;
			}

			start = System.nanoTime();
			for (ICombinatoricsVector<Integer> r : gen) {
				// check exist or covered by existing virtual worker
				VirtualWorker v = new VirtualWorker(r.getVector());
				if (bf.mightContain(v)) {
					if (!vWorkerList.contains(v)) {
						vWorkerList.add(v);
						bf.put(v);
					}
				} else {
					/**
					 * 100%
					 */
					vWorkerList.add(v);
					bf.put(v);
				}
			}
			long period = System.nanoTime() - start;
			System.out.println("      #workers = " + workerIdxs.size()
					+ " time (ms) = " + period / 1000000.0);
		}

		/**
		 * update connection between virtual worker and task.
		 */
		vWorkerArray = vWorkerList.toArray(new VirtualWorker[0]); // copy all
		// elements
		ArrayList containerVirtualWorker = new ArrayList<>();

		/**
		 * Iterate all worker virtual o
		 */
		for (int o = 0; o < vWorkerArray.length; o++) {
			VirtualWorker vw = vWorkerArray[o];

			/**
			 * <taskid,deadline>
			 */
			HashMap<Integer, Integer> taskids = new HashMap<>();
			/**
			 * Iterate all worker ids of virtual worker o
			 */
			ArrayList<HashMap<Integer, Integer>> cwWithTaskDeadline = getContainerWithDeadline();
			for (Integer j : vw.getWorkerIds()) {
				HashMap<Integer, Integer> tasksWithDeadlines = cwWithTaskDeadline
						.get(j);
				for (Integer t : tasksWithDeadlines.keySet()) {
					int k = taskList.get(candidateTaskIndices.get(t)).getK();
					/**
					 * Check if the virtual worker is qualified for this task
					 */
					if (vw.getWorkerIds().size() >= k
							&& !taskids.containsKey(t)) {
						taskids.put(t, tasksWithDeadlines.get(t));
					}
				}

			}

			containerVirtualWorker.add(taskids);
		}

		containerWorker = containerVirtualWorker;
	}

	/**
	 * Select minimum number of workers that cover maximum number of tasks.
	 * After finding minimum number of workers that covers all tasks, remove all
	 * the assigned tasks from task list!
	 */
	public void minimizeWorkersMaximumTaskCoverage() {

		SetCoverGreedy sc = null;
		HashSet<Integer> minAssignedWorkers;

		switch (algorithm) {

		case GREEDY_HIGH_TASK_COVERAGE:

			// check using virtual worker or not
			if (vWorkerArray != null && vWorkerArray.length > 0) {
				/**
				 * containerWorker has been updated from function
				 * populateVirtualWorker
				 */
				sc = new SetCoverGreedy_HighTaskCoverage(containerWorker,
						TimeInstance);
				minAssignedWorkers = sc.minSetCover();

				HashSet<Integer> assignedWorkerList = new HashSet<>();
				for (Integer i : minAssignedWorkers) {
					// add all worker ID list of a virtual worker
					assignedWorkerList.addAll(vWorkerArray[i].getWorkerIds());
				}
				TotalAssignedWorkers += assignedWorkerList.size();
			} else {
				sc = new SetCoverGreedy_HighTaskCoverage(
						getContainerWithDeadline(), TimeInstance);
				minAssignedWorkers = sc.minSetCover();

				TotalAssignedWorkers += minAssignedWorkers.size();

			}
			TotalAssignedTasks += sc.assignedTasks;
			/**
			 * Why this?
			 */
			if (sc.averageDelayTime > 0) {
				AverageTimeToAssignTask += sc.averageDelayTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageDelayTime);

			}

			break;
		case GREEDY_HIGH_TASK_COVERAGE_MULTI:
			sc = new MultiSetCoverGreedy_HighTaskCoverage(
					getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();

			TotalAssignedWorkers += minAssignedWorkers.size();
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageDelayTime > 0) {
				AverageTimeToAssignTask += sc.averageDelayTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageDelayTime);
			}
			break;
		case GREEDY_LOW_WORKER_COVERAGE:
			sc = new SetCoverGreedy_LowWorkerCoverage(
					getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			// if using virtual workers, compute real assigned workers
			if (vWorkerArray != null && vWorkerArray.length > 0) {
				HashSet<Integer> assignedWorkerList = new HashSet<>();
				for (Integer i : minAssignedWorkers) {
					// add all worker ID list of a virtual worker
					assignedWorkerList.addAll(vWorkerArray[i].getWorkerIds());
				}
				TotalAssignedWorkers += assignedWorkerList.size();
			} else {
				TotalAssignedWorkers += minAssignedWorkers.size();
			}
			TotalAssignedTasks += sc.universe.size();
			if (sc.averageDelayTime > 0) {
				AverageTimeToAssignTask += sc.averageDelayTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageDelayTime);
			}
			break;
		case GREEDY_LARGE_WORKER_FANOUT:

			/**
			 * check using virtual worker or not
			 */
			if (vWorkerArray != null && vWorkerArray.length > 0) {
				/**
				 * containerWorker has been updated from function
				 * populateVirtualWorker
				 */
				sc = new SetCoverGreedy_LargeWorkerFanout(containerWorker,
						TimeInstance);
				minAssignedWorkers = sc.minSetCover();

				HashSet<Integer> assignedWorkerList = new HashSet<>();
				for (Integer i : minAssignedWorkers) {
					// add all worker ID list of a virtual worker
					assignedWorkerList.addAll(vWorkerArray[i].getWorkerIds());
				}
				TotalAssignedWorkers += assignedWorkerList.size();
			} else {
				sc = new SetCoverGreedy_LargeWorkerFanout(
						getContainerWithDeadline(), TimeInstance);
				minAssignedWorkers = sc.minSetCover();

				TotalAssignedWorkers += minAssignedWorkers.size();

			}
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageDelayTime > 0) {
				AverageTimeToAssignTask += sc.averageDelayTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageDelayTime);
			}
			break;
		case GREEDY_LARGE_WORKER_FANOUT_MULTI:
			sc = new MultiSetCoverGreedy_LargeWorkerFanout(
					getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();

			TotalAssignedWorkers += minAssignedWorkers.size();
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageDelayTime > 0) {
				AverageTimeToAssignTask += sc.averageDelayTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageDelayTime);
			}
			break;
		case GREEDY_CLOSE_TO_DEADLINE:

			if (vWorkerArray != null && vWorkerArray.length > 0) {
				/**
				 * containerWorker has been updated from function
				 * populateVirtualWorker
				 */
				sc = new SetCoverGreedy_CloseToDeadline(containerWorker,
						TimeInstance);
				minAssignedWorkers = sc.minSetCover();

				HashSet<Integer> assignedWorkerList = new HashSet<>();
				for (Integer i : minAssignedWorkers) {
					// add all worker ID list of a virtual worker
					assignedWorkerList.addAll(vWorkerArray[i].getWorkerIds());
				}
				TotalAssignedWorkers += assignedWorkerList.size();
			} else {
				HashMap<GenericTask, Double> entropies = new HashMap<GenericTask, Double>();
				for (GenericTask t : taskList) {
					entropies.put(t, computeCost(t));
				}
				sc = new SetCoverGreedy_CloseToDeadline(
						getContainerWithDeadline(), TimeInstance);
				((SetCoverGreedy_CloseToDeadline) sc).setEntropies(entropies);
				((SetCoverGreedy_CloseToDeadline) sc).setTaskList(taskList);
				minAssignedWorkers = sc.minSetCover();

				TotalAssignedWorkers += minAssignedWorkers.size();
			}
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageDelayTime > 0) {
				AverageTimeToAssignTask += sc.averageDelayTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageDelayTime);
			}
			break;
		case GREEDY_CLOSE_TO_DEADLINE_MULTI:
			sc = new MultiSetCoverGreedy_CloseToDeadline(
					getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();

			TotalAssignedWorkers += minAssignedWorkers.size();
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageDelayTime > 0) {
				AverageTimeToAssignTask += sc.averageDelayTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageDelayTime);
			}
			break;
		}

		/**
		 * As all the tasks in the container are assigned, we need to remove
		 * them from task list.
		 */
		ArrayList<Integer> assignedTasks = new ArrayList<Integer>();
		// Iterator it = sc.universe.iterator();
		Iterator it = sc.assignedTaskSet.iterator();
		while (it.hasNext()) {
			Integer candidateIndex = (Integer) it.next();
			assignedTasks.add(candidateTaskIndices.get(candidateIndex));
		}

		/**
		 * sorting is necessary to make sure that we don't mess things up when
		 * removing elements from a list
		 */
		Collections.sort(assignedTasks);
		for (int i = assignedTasks.size() - 1; i >= 0; i--) {
			/* remove the last elements first */
			taskList.remove((int) assignedTasks.get(i));
		}
	}

	/**
	 * Read tasks from file.
	 *
	 * @param fileName
	 *            the file name
	 */
	@Override
	public void readTasks(String fileName) {
		TaskCount += Parser.parseSensingTasks(fileName, taskList);
	}
	
	/**
	 * workload
	 */
	public void readWorkloadTasks(String fileName, int startTime) {
		TaskCount += Parser.parseSensingTasks(fileName, startTime, taskList);
	}

	/**
	 * Read workers from file Working region of each worker is computed from his
	 * past history.
	 *
	 * @param fileName
	 *            the file name
	 */
	@Override
	public void readWorkers(String fileName) {
		WorkerCount += Parser.parseGenericWorkers(fileName, workerList);
	}

	/**
	 * Compute input for one time instance, including container and
	 * invertedTable.
	 *
	 * Find the tasks whose regions contain the worker
	 *
	 * @param workerIdx
	 *            the worker idx
	 */
	public void reverseRangeQuery(final int workerIdx) {
		/* actual worker */
		GenericWorker w = workerList.get(workerIdx);

		/* task id, increasing from 0 to the number of task - 1 */
		int tid = 0;
		for (int i = 0; i < taskList.size(); i++) {
//			System.out.println(i + "xxx" + taskList.size());
//			System.out.println(taskList.get(i).getClass().toString() + i + " " + taskList.size());
			SensingTask task = (SensingTask) taskList.get(i);

			/* tick expired task */
			if ((TimeInstance - task.getEntryTime()) >= (GeocrowdConstants.TaskDuration)
				) {
				task.setExpired();
			}
			/* if worker in task region */
			else if (distanceWorkerTask(w, task) <= task.getRadius()) {

				/* compute a list of candidate tasks */
				if (!taskSet.contains(tid)) {
					candidateTaskIndices.add(tid);
					taskSet.add(tid);
				}

				if (containerPrune[workerIdx] == null) {
					containerPrune[workerIdx] = new ArrayList();
				}
				containerPrune[workerIdx]
						.add(candidateTaskIndices.indexOf(tid));

				/**
				 * inverted container need to update after compute
				 * containerWorker if (!invertedContainer.containsKey(tid)) {
				 * invertedContainer.put(tid, new ArrayList() { {
				 * add(workerIdx); } }); } else {
				 * invertedContainer.get(tid).add(workerIdx); }
				 */
			}// if not overlapped

			tid++;
		}// for loop
	}
	
    /**
     * Re-initialize all parameters
     */
	public void reset() {
		TimeInstance = 0;
		TaskCount = 0;
		WorkerCount = 0;
		TotalAssignedTasks = 0;
		TotalAssignedWorkers = 0;
		workerList = null;
		workerList = new ArrayList<>();
		taskList = new ArrayList<>();
		
	}

}
