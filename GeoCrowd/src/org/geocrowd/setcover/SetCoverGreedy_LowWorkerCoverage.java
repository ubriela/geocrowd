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
package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.geocrowd.common.Constants;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedySmallestAssociateSet.
 *
 * @author Luan
 */
public class SetCoverGreedy_LowWorkerCoverage extends SetCoverGreedy {

    public SetCoverGreedy_LowWorkerCoverage(ArrayList container, Integer current_time_instance) {
        super(container, current_time_instance);
    }

    /**
     * Compute number of sets associates with uncovered elements in a set.
     *
     * @param S the s
     * @param s the s
     * @param C the c
     * @return the int
     */
    private int computeAssociateSets(ArrayList<HashMap<Integer, Integer>> S, HashMap<Integer, Integer> s,
            HashSet<Integer> C) {
        int numAssociateSet = 0; //initialize  varibale
        //loop for uncovered elements
        for (Integer i : s.keySet()) {
            if (!C.contains(i)) { //check uncovered condition. 
                for (HashMap<Integer, Integer> s2 : S) {
                    if (s2.keySet().contains(i)) {
                        numAssociateSet++;
                    }
                }
            }
        }
        return numAssociateSet;
    }

    /**
     * Greedy algorithm.
     *
     * @return the int
     */
    @Override
    public HashSet<Integer> minSetCover() {
        ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) listOfSets.clone();
        HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
        assignedTaskSet = new HashSet<Integer>();

        ArrayList<HashSet<Integer>> AW = new ArrayList<>();

        int set_size = S.size();

        while (!Q.isEmpty()) {
            HashMap<Integer, Integer> maxSet = null;
            int maxElem = 0;
            int numAssociateSet = 0; // number of sets associates with maxSet.
            for ( int o=0 ; o < S.size();o++) {
                // select the item set that maximize coverage
                // how many elements in s that are not in C
            	HashMap<Integer, Integer> s = S.get(o);
                int newElem = 0;

                for (Integer i : s.keySet()) {
                    if (!assignedTaskSet.contains(i)) {
                        newElem++;
                    }
                }
                if (newElem > maxElem) {
                    maxElem = newElem;
                    maxSet = s;
                    numAssociateSet = computeAssociateSets(S, s, assignedTaskSet);
                    assignWorkers.add(o);
                } else if (newElem == maxElem) //compare associate sets , choose the smaller
                {
                    int n = computeAssociateSets(S, s, assignedTaskSet);
                    if (n < numAssociateSet) {
                        maxElem = newElem;
                        maxSet = s;
                        numAssociateSet = n;
                        assignWorkers.add(o);
                    }
                }
            }

            S.remove(maxSet);
            Q.removeAll(maxSet.keySet());
            //compute average time to assign tasks 
            Set assignedSet = maxSet.keySet();
            for (Object kt : assignedSet) {
                Integer key = (Integer)kt;
                if (!assignedTaskSet.contains(key)) {
                    
                    averageDelayTime += currentTimeInstance - (maxSet.get(key) - Constants.TaskDuration) + 1;
                    assignedTaskSet.add(key);
                }
            }
        }

        //compute workers per task
        assignedTasks = assignedTaskSet.size();
//        averageTime = averageTime*1.0/assignedTasks;
        System.out.println("#Task assigned: "+assignedTasks);
        return assignWorkers;
    }
}
