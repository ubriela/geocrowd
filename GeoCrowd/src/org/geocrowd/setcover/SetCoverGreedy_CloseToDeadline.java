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
import java.util.HashMap;
import java.util.HashSet;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverGreedyCombineDeadline.
 * 
 * @author Luan
 */
public class SetCoverGreedy_CloseToDeadline extends SetCover {

     
   
    /**
	 * Instantiates a new sets the cover greedy combine deadline.
	 * 
	 * @param container
	 *            the container
	 * @param current_time_instance
	 *            the current_time_instance
	 */
    public SetCoverGreedy_CloseToDeadline(ArrayList<HashMap<Integer, Integer>> container, Integer current_time_instance) {
        super(container, current_time_instance);
    }
    
    /* (non-Javadoc)
     * @see org.geocrowd.setcover.SetCoverGreedyWaitTillDeadline#minSetCover()
     */
    @Override
    public int minSetCover(){
        ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) listOfSets.clone();
        HashSet<Integer> Q = (HashSet<Integer>) universe.clone();
        HashSet<Integer> C = new HashSet<Integer>();
       
        int set_size = S.size();

        while (!Q.isEmpty()) {
            HashMap<Integer, Integer> maxSet = null;
            double maxElem = 0;
            for (HashMap<Integer, Integer> s : S) {

                double newElem = weight(s, currentTimeInstance, C);
                if (newElem > maxElem)
                {
                    maxElem = newElem;
                    maxSet = s;
                }
            }
            if(maxSet == null)
                break;

            S.remove(maxSet);
            Q.removeAll(maxSet.keySet());
            C.addAll(maxSet.keySet());
        }
        assignedTasks = C.size();
        return set_size - S.size();
    }
    
    /**
	 * Weight.
	 * 
	 * @param s
	 *            the s
	 * @param current_time_instance
	 *            the current_time_instance
	 * @param C
	 *            the c
	 * @return the double
	 */
    private double weight(HashMap<Integer, Integer> s, int current_time_instance, 
            HashSet<Integer> C)
    {
        double w = 0;
        int numElem =0;
        double d =0;
        for(Integer t:s.keySet())
        {
            if(!C.contains(t))
            {
                numElem ++;
                d += s.get(t)-current_time_instance;
            }
        }
        return numElem*1.0-(Math.pow((d+1),0.5));
    }
}
