/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Luan
 */
public class SetCoverGreedyCombineDeadline extends SetCoverGreedyWaitTillDeadline {

    public SetCoverGreedyCombineDeadline(ArrayList<HashMap<Integer, Integer>> container, Integer current_time_instance) {
        super(container, current_time_instance);
    }
    
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
        return numElem*1.0/((d+1)*(d+1));
    }
    
    @Override
    public int minSetCover(){
        ArrayList<HashMap<Integer, Integer>> S = (ArrayList<HashMap<Integer, Integer>>) setOfSets.clone();
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
    
    
}
