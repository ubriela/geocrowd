package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class SetCoverGreedy extends SetCover {

    public SetCoverGreedy(ArrayList<ArrayList> container) {
        super(container);
    }

    /**
     * Greedy algorithm
     */
    public int minSetCover() {
        ArrayList<HashSet<Integer>> S = (ArrayList<HashSet<Integer>>) setOfSets.clone();
        HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
        HashSet<Integer> C = new HashSet<Integer>();
        

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
