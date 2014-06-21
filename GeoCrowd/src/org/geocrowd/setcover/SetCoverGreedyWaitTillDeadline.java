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

/**
 *
 * @author Luan
 */
public class SetCoverGreedyWaitTillDeadline {

    // hashmap<task, deadline of task>
    ArrayList<HashMap<Integer, Integer>> setOfSets = null;

    HashSet<Integer> universe = null;
    Integer currentTimeInstance = 0;
    Integer k = 2;

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
        if (s.values().contains(current_time_instance)) {
            return true;
        }

        return false;
    }

    /**
     * Greedy algorithm
     */
    public int minSetCover() {
        ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) setOfSets.clone();
        HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
        HashSet<Integer> C = new HashSet<Integer>();

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
                        && (newElem > k || containElementDeadAtNextTime(s, currentTimeInstance))) // check condition: only select workers that either cover at least K (e.g., k=2,3..)
                //tasks or cover any task that will not available in the next time instance
                {
                    maxElem = newElem;
                    maxSet = s;
                }
            }

            S.remove(maxSet);
            Q.removeAll(maxSet.keySet());
            C.addAll(maxSet.keySet());
        }

        return set_size - S.size();
    }
}
