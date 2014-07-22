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
import java.util.HashSet;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedySmallestAssociateSet.
 *
 * @author Luan
 */
public class SetCoverGreedy_LowWorkerCoverage extends SetCover {

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
    private int computeAssociateSets(ArrayList<HashSet<Integer>> S, HashSet<Integer> s, HashSet<Integer> C) {
        int numAssociateSet = 0; //initialize  varibale
        //loop for uncovered elements
        for (Integer i : s) {
            if (!C.contains(i)) { //check uncovered condition. 
                for (HashSet<Integer> s2 : S) {
                    if (s2.contains(i)) {
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
    public int minSetCover() {
        ArrayList<HashSet<Integer>> S = (ArrayList<HashSet<Integer>>) listOfSets.clone();
        HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
        assignedTaskSet = new HashSet<Integer>();

        ArrayList<HashSet<Integer>> AW = new ArrayList<>();

        int set_size = S.size();

        while (!Q.isEmpty()) {
            HashSet<Integer> maxSet = null;
            int maxElem = 0;
            int numAssociateSet = 0; // number of sets associates with maxSet.
            for (HashSet<Integer> s : S) {
                // select the item set that maximize coverage
                // how many elements in s that are not in C
                int newElem = 0;

                for (Integer i : s) {
                    if (!assignedTaskSet.contains(i)) {
                        newElem++;
                    }
                }
                if (newElem > maxElem) {
                    maxElem = newElem;
                    maxSet = s;
                    numAssociateSet = computeAssociateSets(S, s, assignedTaskSet);
                } else if (newElem == maxElem) //compare associate sets , choose the smaller
                {
                    int n = computeAssociateSets(S, s, assignedTaskSet);
                    if (n < numAssociateSet) {
                        maxElem = newElem;
                        maxSet = s;
                        numAssociateSet = n;
                    }
                }
            }

            S.remove(maxSet);
            Q.removeAll(maxSet);
            assignedTaskSet.addAll(maxSet);
            
        }

        //compute workers per task
        return set_size - S.size();
    }
}
