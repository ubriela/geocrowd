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
package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashSet;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedy.
 */
public class SetCoverGreedy extends SetCover {

    public SetCoverGreedy(ArrayList container, Integer current_time_instance) {
        super(container, current_time_instance);
    }

   

    /**
	 * Greedy algorithm.
	 * 
	 * @return the number of assigned workers
	 */
    @Override
	public int minSetCover() {
        ArrayList<HashSet<Integer>> S = (ArrayList<HashSet<Integer>>) listOfSets.clone();
        HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
        assignedTaskSet = new HashSet<Integer>();
        
        int set_size = S.size();

        while (!Q.isEmpty()) {
            HashSet<Integer> maxSet = null;
            int maxElem = 0;
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
                }
            }

            
            S.remove(maxSet);
            Q.removeAll(maxSet);
            assignedTaskSet.addAll(maxSet);
            //compute average time to assign tasks in maxSet
            
        }
     

        return set_size - S.size();
    }
}
