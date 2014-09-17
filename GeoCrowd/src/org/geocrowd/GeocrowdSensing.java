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
package org.geocrowd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.geocrowd.common.Constants;
import org.geocrowd.common.Utils;
import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.GenericWorker;
import org.geocrowd.common.crowdsource.SensingTask;
import org.geocrowd.common.crowdsource.VirtualWorker;
import org.geocrowd.setcover.SetCover;
import org.geocrowd.setcover.SetCoverGreedy;
import org.geocrowd.setcover.SetCoverGreedy_CloseToDeadline;
import org.geocrowd.setcover.SetCoverGreedy_LargeTaskCoverage;
import org.geocrowd.setcover.SetCoverGreedy_LowWorkerCoverage;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

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
	 * Gets the container with deadline.
	 *
	 * @return a container with task deadline
	 */
	private ArrayList<HashMap<Integer, Integer>> getContainerWithDeadline() {
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
								.getEntryTime() + Constants.TaskDuration);
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
		containerWorker = new ArrayList<ArrayList>();
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

		// find tasks with no worker
		// Iterator it = candidateTasks.iterator();
		// while (it.hasNext()) {
		// Integer taskid = (Integer) it.next();
		// ArrayList workers = invertedContainer.get(taskid);
		// if (workers.size() == 0) {
		// System.out.println("task with no worker!!!!!!!!!!!!");
		// }
		// }
	}

	final Comparator<GenericTask> TASK_ORDER = new Comparator<GenericTask>() {
		public int compare(GenericTask t1, GenericTask t2) {
			if (t1.getK() > t2.getK())
				return -1;
			else if (t1.getK() < t2.getK())
				return 1;
			else {
				int idx1 = taskList.indexOf(t1);
				int idx2 = taskList.indexOf(t2);
				HashSet<Integer> workerIdxs1 = null;
				if (invertedContainer.containsKey(idx1))
					workerIdxs1 = new HashSet<Integer>(
							invertedContainer.get(idx1));
				HashSet<Integer> workerIdxs2 = null;
				if (invertedContainer.containsKey(idx2))
					workerIdxs2 = new HashSet<Integer>(
							invertedContainer.get(idx2));

				if (workerIdxs1 != null) {
					if (workerIdxs2 != null) {
						if (workerIdxs1.size() > workerIdxs2.size())
							return -1;
						else if (workerIdxs1.size() < workerIdxs2.size())
							return 1;
						else
							return 0;
					} else
						return -1;
				} else {
					if (workerIdxs2 != null)
						return 1;
					else
						return 0;
				}
			}
		}
	};

	Funnel<VirtualWorker> vworkerFunnel = new Funnel<VirtualWorker>() {
		@Override
		public void funnel(VirtualWorker w, PrimitiveSink into) {
//			into.putInt(w.getWorkerIds().hashCode());
			for (Integer i : w.getWorkerIds())
				into.putInt(i);
		}
	};

	public void populateVitualWorkers() {

		/**
		 * sort tasks by k in descending order *
		 */
		ArrayList<GenericTask> sortedTaskList = (ArrayList<GenericTask>) taskList
				.clone();
		Collections.sort(sortedTaskList, TASK_ORDER);

		HashMap<GenericTask, Integer> mapTaskIndices = new HashMap<GenericTask, Integer>();
		for (GenericTask t : sortedTaskList)
			mapTaskIndices.put(t, taskList.indexOf(t));

		/**
		 * create virtual worker, using priority queue
		 */
		BloomFilter<VirtualWorker> bf = BloomFilter.create(vworkerFunnel,
				1000000, 0.01);
		PriorityQueue<VirtualWorker> vWorkerList = new PriorityQueue<>();

		HashMap<Integer, ArrayList<SensingTask>> traversedTasks = new HashMap<Integer, ArrayList<SensingTask>>();
		int i = 0;
		for (final GenericTask t : sortedTaskList) {
			// get workers cover task
			int idx = mapTaskIndices.get(t);
			System.out.println("#task = " + i++ + " #k=" + t.getK()
					+ " $vtasks = " + vWorkerList.size());

			HashSet<Integer> workerIdxs = null;
			if (invertedContainer.containsKey(idx))
				workerIdxs = new HashSet<Integer>(invertedContainer.get(idx));
			/* tasks that are not covered by any worker */
			if (workerIdxs == null)
				continue;

			/**
			 * Check condition 1: if the worker set of a task of k responses is
			 * covered by the worker set of another traversed task of the same k
			 */
			boolean isContinue = false;
			if (traversedTasks.containsKey(t.getK())) {
				for (SensingTask st : traversedTasks.get(t.getK())) {
					int idxj = mapTaskIndices.get(st);
					HashSet<Integer> workerIdxsj = null;
					if (invertedContainer.containsKey(idxj))
						workerIdxsj = new HashSet<Integer>(
								invertedContainer.get(idxj));

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

			if (isContinue)
				continue;

			/**
			 * Check condition 2 (not often happen): if the worker set is
			 * covered by an existing logical worker --> do not need to worry
			 * about
			 */

			long start = System.nanoTime();
			List<LinkedList<Integer>> res = Utils.getSubsets2(new ArrayList<>(
					workerIdxs), t.getK());
			long period = System.nanoTime() - start;
			System.out.println(workerIdxs.size() + " " + res.size() + " "
					+ period / 1000000.0);

			/**
			 * Do not need to check if the first set
			 */
			if (vWorkerList.size() == 0) {
				for (LinkedList<Integer> r : res) {
					vWorkerList.add(new VirtualWorker(r));
					bf.put(new VirtualWorker(r));
				}
				continue;
			}

			start = System.nanoTime();
			for (LinkedList<Integer> r : res) {
				// check exist or covered by existing virtual worker
				VirtualWorker v = new VirtualWorker(r);
				if (bf.mightContain(v)) {
					if (!vWorkerList.contains(v))
						vWorkerList.add(v);
				} else {
					vWorkerList.add(v);
				}
			}
			period = System.nanoTime() - start;
			System.out.println("time (ms) " + period / 1000000.0);
		}

		/**
		 * update connection between virtual worker and task why not update at
		 * the right after creating them?
		 */
		ArrayList<ArrayList> containerVirtualWorker = new ArrayList<>();

		Iterator<VirtualWorker> it = vWorkerList.iterator();
		while (it.hasNext()) {
			VirtualWorker vw = it.next();
			ArrayList<Integer> taskids = new ArrayList<>();
			for (Integer j : vw.getWorkerIds()) {
				taskids.addAll(containerWorker.get(j));
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

		SetCover sc = null;
		int minAssignedWorkers = 0;

		switch (algorithm) {
		case GREEDY_HIGH_TASK_COVERAGE:
			sc = new SetCoverGreedy(getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			TotalAssignedWorkers += minAssignedWorkers;
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageTime > 0) {
				AverageTimeToAssignTask += sc.averageTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageTime);
				System.out.println("total assign tasks: " + sc.assignedTasks);
			}
			break;
		case GREEDY_LOW_WORKER_COVERAGE:
			sc = new SetCoverGreedy_LowWorkerCoverage(
					getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			TotalAssignedWorkers += minAssignedWorkers;
			TotalAssignedTasks += sc.universe.size();
			if (sc.averageTime > 0) {
				AverageTimeToAssignTask += sc.averageTime;
				numTimeInstanceTaskAssign += 1;
				System.out.println("average time: " + sc.averageTime);
			}
			break;
		case GREEDY_LARGE_WORKER_FANOUT_PRIORITY:

			sc = new SetCoverGreedy_LargeTaskCoverage(
					getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			TotalAssignedWorkers += minAssignedWorkers;
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageTime > 0) {
				AverageTimeToAssignTask += sc.averageTime;
				System.out.println("average time: " + sc.averageTime);
				numTimeInstanceTaskAssign += 1;
			}
			break;
		case GREEDY_CLOSE_TO_DEADLINE:

			sc = new SetCoverGreedy_CloseToDeadline(getContainerWithDeadline(),
					TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			TotalAssignedWorkers += minAssignedWorkers;
			TotalAssignedTasks += sc.assignedTasks;
			if (sc.averageTime > 0) {
				AverageTimeToAssignTask += sc.averageTime;
				numTimeInstanceTaskAssign += 1;
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
	private void reverseRangeQuery(final int workerIdx) {
		/* actual worker */
		GenericWorker w = workerList.get(workerIdx);

		/* task id, increasing from 0 to the number of task - 1 */
		int tid = 0;
		for (int i = 0; i < taskList.size(); i++) {
			SensingTask task = (SensingTask) taskList.get(i);

			/* tick expired task */
			if ((TimeInstance - task.getEntryTime()) >= Constants.TaskDuration) {
				task.setExpired();
			} else /* if worker in task region */if (distanceWorkerTask(w,
					task) <= task.getRadius()) {

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

				/* inverted container */
				if (!invertedContainer.containsKey(tid)) {
					invertedContainer.put(tid, new ArrayList() {
						{
							add(workerIdx);
						}
					});
				} else {
					invertedContainer.get(tid).add(workerIdx);
				}

			}// if not overlapped

			tid++;
		}// for loop
	}

}
