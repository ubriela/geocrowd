/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.geocrowd.util.Constants;

/**
 *
 * @author Luan
 */
public class SetCoverGreedyWaitTillDeadline {

    // hashmap<task, deadline of task >
    ArrayList<HashMap<Integer, Integer>> setOfSets = null;

    HashSet<Integer> universe = null;
    Integer currentTimeInstance = 0;
    Integer k = 3;
    public int assignedTasks = 0;

    public double averageTasksPerWorker;
    public double averageWorkersPerTask;

    /**
     * Initialize variables
     *
     * @param container
     * @param deadAtTime
     * @param current_time_instance
     */
    public SetCoverGreedyWaitTillDeadline(ArrayList<HashMap<Integer, Integer>> container,
            Integer current_time_instance) {
        currentTimeInstance = current_time_instance;
        universe = new HashSet<>();
        setOfSets = new ArrayList<>();
        for (int i = 0; i < container.size(); i++) {
            HashMap<Integer, Integer> item = container.get(i);
            if (item != null) {
                HashMap<Integer, Integer> itemSet = new HashMap<>(item);
                setOfSets.add(itemSet);
                universe.addAll(item.keySet());

            }

        }
    }

    /*
     Check worker contain elemenst will not available at next time
     */
    private boolean containElementDeadAtNextTime(HashMap<Integer, Integer> s,
            int current_time_instance) {
        return s.values().contains(current_time_instance) || (current_time_instance == Constants.TIME_INSTANCE - 1);
    }

    /**
     * Greedy algorithm
     *
     * @return number of assigned workers
     */
    public int minSetCover() {
        ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) setOfSets.clone();
        HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
        HashSet<Integer> C = new HashSet<Integer>();

//        ArrayList<HashMap<Integer, Integer>> AW = new ArrayList<>();
//        int totalTasks = 0;
//        int totalAssignedWorkers = 0;

        int set_size = S.size();

        while (!Q.isEmpty()) {
            HashMap<Integer, Integer> maxSet = null;
            int maxElem = 0;
            for (HashMap<Integer, Integer> s : S) {

                //
                // select the item set that maximize coverage
                // how many elements in s that are not in C
                int newElem = 0;
                for (Integer i : s.keySet()) {
                    if (!C.contains(i)) {
                        newElem++;
                    }
                }
                if (newElem > maxElem
                        && (newElem >= k || containElementDeadAtNextTime(s, currentTimeInstance))) // check condition: only select workers that either cover at least K (e.g., k=2,3..)
                //tasks or cover any task that will not available in the next time instance
                {
                    maxElem = newElem;
                    maxSet = s;
                }
            }
            if (maxSet == null) {
                break;
            }

            //update total task 
//            totalTasks += maxSet.size();
//            AW.add(maxSet);

            S.remove(maxSet);
            Q.removeAll(maxSet.keySet());
            C.addAll(maxSet.keySet());
        }
        /*
        assignedTasks = C.size();
        
        //compute tasks per worker
        totalAssignedWorkers = set_size - S.size();
        if (totalAssignedWorkers > 0) {
            averageTasksPerWorker = totalTasks * 1.0 / totalAssignedWorkers;
        }
        //compute workers per task
        int totalWorkers = 0;
        for (Integer indexTid : C) {
            int numWorkerCoverTask = 0;
            for (Object set : AW) {
                if (((HashMap) set).containsKey(indexTid)) {
                    numWorkerCoverTask += 1;
                }
            }
            totalWorkers += numWorkerCoverTask;
        }
        if (!C.isEmpty()) {
            averageWorkersPerTask = totalWorkers * 1.0 / C.size();
        }
        */
        return set_size - S.size();
    }
}
