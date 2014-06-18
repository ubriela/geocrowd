/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 *
 * @author Luan
 */
public class SetCoverGreedyWaitTillDeadline {

    ArrayList<HashSet<Integer>> setOfSets = null;
    HashSet<Integer> universe = null;
    ArrayList<Integer> deadlineAtInstance = null;
    Integer currentInstance=0;

    /**
     * Initialize variables
     *
     * @param container
     * @param deadlineInstances
     * @param current_instance
     */
    public SetCoverGreedyWaitTillDeadline(ArrayList<ArrayList> container,
            ArrayList<Integer> deadlineInstances , Integer current_instance) {
        if(deadlineInstances.size() != container.size())
            return;
        currentInstance = current_instance;
        setOfSets = new ArrayList<>();
        universe = new HashSet<>();

        for (int i = 0; i < container.size(); i++) {
            ArrayList<Integer> items = container.get(i);
            if (items != null) {
                HashSet<Integer> itemSet = new HashSet<Integer>(items);
                setOfSets.add(itemSet);
                universe.addAll(itemSet);
                deadlineAtInstance.add(deadlineInstances.get(i));
            }
        }
    }

    /**
     * Greedy algorithm
     */
    public int minSetCover() {
        ArrayList<HashSet<Integer>> S = new ArrayList<>();
        HashSet<Integer> Q = new HashSet<>();
        HashSet<Integer> C = new HashSet<Integer>();
        
        // filter out tasks do not pass deadline in the next time instance
        for(int i=0;i<setOfSets.size(); i++)
        {
            if(Objects.equals(deadlineAtInstance.get(i), currentInstance))
            {
                HashSet<Integer> set = setOfSets.get(i);
                S.add(set);
                Q.addAll(set);
                
            }
        }
        int set_size = S.size();

        while (!Q.isEmpty()) {
            HashSet<Integer> maxSet = null;
            int maxElem = 0;
            for (HashSet<Integer> s : S) {
				// select the item set that maximize coverage
                // how many elements in s that are not in C
                int newElem = 0;
                for (Integer i : s) {
                    if (!C.contains(i)) {
                        newElem++;
                    }
                }
                if (newElem > maxElem) {
                    maxElem = newElem;
                    maxSet = s;
                }
            }

            S.remove(maxSet);
            Q.removeAll(maxSet);
            C.addAll(maxSet);
        }

        return set_size - S.size();
    }
}
