package org.geocrowd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import maxcover.MaxCoverBasic;
import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.SensingTask;
import org.geocrowd.common.crowdsource.SensingWorker;

public class OfflineMTC extends GeocrowdSensing {

    public int budget = 0;

    /**
     * read worker and then add entry time
     *
     * @param fileName
     * @param entryTime
     */
    public void readWorkers(String fileName, int entryTime) {
        WorkerCount += Parser.parseSensingWorkers(fileName, workerList, entryTime);
    }

    /**
     * Compute input for one time instance, including container and
     * invertedTable.
     *
     * Find the tasks whose regions contain the worker
     *
     * @param workerIdx the worker idx
     */
    @Override
    public void reverseRangeQuery(final int workerIdx) {
        /* actual worker */
        SensingWorker w = (SensingWorker) workerList.get(workerIdx);
        int workerOnlineTime = w.getOnlineTime();
        /* task id, increasing from 0 to the number of task - 1 */
        int tid = 0;
        for (int i = 0; i < taskList.size(); i++) {
            SensingTask task = (SensingTask) taskList.get(i);

            /**
             * worker covers only task at the same time instance or deferred and
             * not expired at worker's time instance
             */
            if ((workerOnlineTime - task.getEntryTime()) < Constants.TaskDuration
                    && distanceWorkerTask(w, task) <= task.getRadius()) {

                /* compute a list of candidate tasks */
                if (!taskSet.contains(tid)) {
                    candidateTaskIndices.add(tid);
                    taskSet.add(tid);
                }

                if (containerPrune[workerIdx] == null) {
                    containerPrune[workerIdx] = new ArrayList();
                }
                containerPrune[workerIdx].add(candidateTaskIndices.indexOf(tid));

            }// if not overlapped

            tid++;
        }// for loop
    }

    @Override
    public void matchingTasksWorkers() {
        invertedContainer = new HashMap<Integer, ArrayList>();
        candidateTaskIndices = new ArrayList();
        taskSet = new HashSet<Integer>();
        containerWorker = new ArrayList<>();
        containerPrune = new ArrayList[workerList.size()];

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

    /**
     * Solve the offline MTC problem
     * @return
     */
    public HashSet<Integer> maxTaskCoverage() {
        MaxCoverBasic maxCover = new MaxCoverBasic(getContainerWithDeadline(), TimeInstance);
        maxCover.budget = budget;
        return maxCover.maxCover();
    }
}
