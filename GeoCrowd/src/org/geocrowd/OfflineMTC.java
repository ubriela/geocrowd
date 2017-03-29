package org.geocrowd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.crowd.SensingWorker;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdConstants;
import org.geocrowd.datasets.params.GeocrowdSensingConstants;
import org.geocrowd.datasets.synthetic.Parser;
import org.geocrowd.maxcover.MaxCover;
import org.geocrowd.maxcover.MaxCoverBasic;
import org.geocrowd.maxcover.MaxCoverDynamicOffline;
import org.geocrowd.maxcover.MaxCoverFixedOffline;

public class OfflineMTC extends GeocrowdSensing {

    public int budget = 0;
    public boolean isFixed = true;
    public int [] counts;

    /**
     * read worker and then add entry time
     *
     * @param fileName
     * @param entryTime
     */
    public void readWorkers(String fileName, int entryTime) {
        WorkerCount += Parser.parseSensingWorkers(fileName, workerList, entryTime);
    }

//    public void readWorkersWithLimit(String fileName, int entryTime, int limit) {
////        WorkerCount += Parser.parseSensingWorkersWithLimit(fileName, workerList, entryTime, limit);
////    	Parser.parseSensingWorkers(fileName, workerList, entryTime);
////        workerSampling(workerList, limit);
////        WorkerCount += limit;
//        GeocrowdSensing geocrowd = new GeocrowdSensing();
//        geocrowd.readWorkersWithLimit(fileName, entryTime, limit);
//    }
    

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
            if ((workerOnlineTime - task.getArrivalTime()) < GeocrowdConstants.MAX_TASK_DURATION
                    && (workerOnlineTime - task.getArrivalTime()) >=0 && 
                    		GeocrowdTaskUtility.distanceWorkerTask(DATA_SET, w, task) <= task.getRadius()) {

            	
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

    
    
    public void matchingTaskWorkers2(){
    	//build tasksMap and containerWorkerWithTaskDealine 
    			tasksMap.clear();
    			for(int i = 0;  i < taskList.size(); i++){
    				if(!assignedTasks.contains((int) taskList.get(i).getId()))
    					tasksMap.put((int) taskList.get(i).getId(), taskList.get(i));
    			}
    			containerWorkerWithTaskDeadline = new ArrayList<>();
    			invertedContainer = new HashMap<>();
    			
    			for(int i = 0; i < workerList.size(); i++){
    				
    				HashMap<Integer, Integer> tasks_deadlines = new HashMap<>();
    				for(Integer j: tasksMap.keySet()){
    					GenericWorker w = workerList.get(i);
    					SensingTask task = (SensingTask) tasksMap.get(j);
    					if (GeocrowdTaskUtility.distanceWorkerTask(DATA_SET, w, task) <= GeocrowdSensingConstants.TASK_RADIUS
    							&&  w.getOnlineTime() < task.getArrivalTime()+ task.lifetime
    							&& w.getOnlineTime() >= task.getArrivalTime()
    							){
    						
    						tasks_deadlines.put((int) task.getId(), task.getArrivalTime()+task.lifetime);
    						if(invertedContainer.containsKey((int) task.getId())){
    							invertedContainer.get((int) task.getId()).add(i);
    						}
    						else{
    							ArrayList<Integer> workerIndices = new ArrayList<>();
    							workerIndices.add(i);
    							invertedContainer.put((int) task.getId(), workerIndices);
    						}
    					}
    				}
    				containerWorkerWithTaskDeadline.add(tasks_deadlines);
    			}
    }
    
    @Override
    public void matchingTasksWorkers() {
        invertedContainer = new HashMap<Integer, ArrayList<Integer>>();
        candidateTaskIndices = new ArrayList();
        taskSet = new HashSet<Integer>();
        containerWorker = new ArrayList<>();
        containerPrune = new ArrayList[workerList.size()];

        System.out.println("Worker size ="+workerList.size());
        System.out.println("Task size =" + taskList.size());
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
    	MaxCover maxCover = null;
    	
    	if (isFixed) {
    		maxCover = new MaxCoverFixedOffline(containerWorkerWithTaskDeadline, TimeInstance);
    	} else {
    		maxCover = new MaxCoverDynamicOffline(containerWorkerWithTaskDeadline, TimeInstance);
    	}

    	maxCover.budget = budget;
//        maxCover.numberTimeInstance = Constants.TIME_INSTANCE;
        HashSet<Integer> workerSet = maxCover.maxCover();
        TotalAssignedTasks = maxCover.assignedTasks;
        TotalCoveredUtility = maxCover.assignedUtility;
        
        // print instances of the assigned workers
		counts = new int[GeocrowdConstants.TIME_INSTANCE];
		
		for (int i : workerSet) {
			int time = ((SensingWorker) workerList.get(i)).getOnlineTime();
			counts[time]++;
		}
        
        TotalAssignedWorkers = workerSet.size();
        return workerSet;
    }
}
