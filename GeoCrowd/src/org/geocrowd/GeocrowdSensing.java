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
package org.geocrowd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.geocrowd.common.Constants;
import org.geocrowd.common.Utils;
import org.geocrowd.common.crowdsource.GenericWorker;
import org.geocrowd.common.crowdsource.SensingTask;
import org.geocrowd.common.crowdsource.VirtualWorker;
import org.geocrowd.setcover.SetCover;
import org.geocrowd.setcover.SetCoverGreedy;
import org.geocrowd.setcover.SetCoverGreedy_CloseToDeadline;
import org.geocrowd.setcover.SetCoverGreedy_LargeTaskCoverage;
import org.geocrowd.setcover.SetCoverGreedy_LowWorkerCoverage;

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
        
        public void populateVitualWorkers()
        {
            /** sort task by k **/
            int[] taskId=new int[taskList.size()];
            for(int i=0;i<taskId.length;i++)
                taskId[i]=i;
            for(int i=0;i<taskList.size()-1;i++)
            {
                for(int j=i+1;j<taskList.size();j++)
                {
                    if(((SensingTask)taskList.get(i)).getK() <
                           ((SensingTask)taskList.get(j)).getK())
                    {
                        int temp =taskId[i];
                        taskId[i]=taskId[j];
                        taskId[j]=temp;
                    }
                        
                }
            }
            
            /** create virtual worker */
            for(int i=0;i<taskId.length;i++)
            {
                
                int k= ((SensingTask)taskList.get(i)).getK();
                ArrayList workerIdxs = invertedContainer.get(taskId[i]);
                List<Set<Integer>> res = Utils.getSubsets(workerIdxs, k);
                
            }
        
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
                        if(sc.averageTime > 0)
                        {
                            AverageTimeToAssignTask += sc.averageTime;
                            numTimeInstanceTaskAssign +=1;
                            System.out.println("average time: "+sc.averageTime);
                            System.out.println("total assign tasks: "+sc.assignedTasks );
                        }
			break;
		case GREEDY_LOW_WORKER_COVERAGE:
			sc = new SetCoverGreedy_LowWorkerCoverage(getContainerWithDeadline(),
					TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			TotalAssignedWorkers += minAssignedWorkers;
			TotalAssignedTasks += sc.universe.size();
                        if(sc.averageTime > 0)
                        {
                            AverageTimeToAssignTask += sc.averageTime;
                            numTimeInstanceTaskAssign +=1;
                            System.out.println("average time: "+sc.averageTime);
                        }
			break;
		case GREEDY_LARGE_WORKER_FANOUT_PRIORITY:

			sc = new SetCoverGreedy_LargeTaskCoverage(
					getContainerWithDeadline(), TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			TotalAssignedWorkers += minAssignedWorkers;
			TotalAssignedTasks += sc.assignedTasks;
                        if(sc.averageTime > 0)
                        {
                            AverageTimeToAssignTask += sc.averageTime;
                            System.out.println("average time: "+sc.averageTime);
                            numTimeInstanceTaskAssign +=1;
                        }
			break;
		case GREEDY_CLOSE_TO_DEADLINE:

			sc = new SetCoverGreedy_CloseToDeadline(getContainerWithDeadline(),
					TimeInstance);
			minAssignedWorkers = sc.minSetCover();
			TotalAssignedWorkers += minAssignedWorkers;
			TotalAssignedTasks += sc.assignedTasks;
                        if(sc.averageTime > 0)
                        {
                            AverageTimeToAssignTask += sc.averageTime;
                            numTimeInstanceTaskAssign +=1;
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
			} else /* if worker in task region */
			if (distanceWorkerTask(w, task) <= task.getRadius()) {

				/* compute a list of candidate tasks */

				if (!taskSet.contains(tid)) {
					candidateTaskIndices.add(tid);
					taskSet.add(tid);
				}

				if (containerPrune[workerIdx] == null)
					containerPrune[workerIdx] = new ArrayList();
				containerPrune[workerIdx]
						.add(candidateTaskIndices.indexOf(tid));

				/* inverted container */
				if (!invertedContainer.containsKey(tid))
					invertedContainer.put(tid, new ArrayList() {
						{
							add(workerIdx);
						}
					});
				else
					invertedContainer.get(tid).add(workerIdx);

			}// if not overlapped

			tid++;
		}// for loop
	}
}
