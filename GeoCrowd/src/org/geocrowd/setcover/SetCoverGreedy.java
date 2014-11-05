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

import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.VirtualWorker;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCover.
 */
public abstract class SetCoverGreedy {

    /**
     * Each element in the list is associated with a worker, and each worker
     * contains a set of task ids that he is eligible to perform
	 *
     */
    public ArrayList listOfSets = null;

    /**
     * All the task index in the candidate tasks (not the task list).
     */
    public HashSet<Integer> universe = null;
    public HashSet<Integer> assignedTaskSet = null;

    public double averageDelayTime=0;
    /**
     * The number of assigned tasks.
     */
    public int assignedTasks = 0;
    
    /**
     * The assigned workers
     */
    public HashSet<Integer> assignWorkers = new HashSet<>();
    /**
     * The current time instance.
     */
    Integer currentTimeInstance = 0;
    /*
     /**
     * Initialize variables.
     * 
     * @param container
     *            the container
     */
//	public SetCover(ArrayList<ArrayList> container) {
//		listOfSets = new ArrayList<>();
//		universe = new HashSet<>();
//
//		for (int i = 0; i < container.size(); i++) {
//			ArrayList<Integer> items = container.get(i);
//			if (items != null) {
//				HashSet<Integer> itemSet = new HashSet<Integer>(items);
//				listOfSets.add(itemSet);
//				universe.addAll(itemSet);
//			}
//		}
//	}


    public SetCoverGreedy(ArrayList container, Integer current_time_instance) {
        listOfSets = new ArrayList<>();
        universe = new HashSet<>();
        currentTimeInstance = current_time_instance;
        if (container.size() > 0 && container.get(0).getClass().isInstance(new ArrayList())) {
            for (int i = 0; i < container.size(); i++) {

                ArrayList<Integer> items = (ArrayList<Integer>) container.get(i);
                if (items != null) {
                    HashSet<Integer> itemSet = new HashSet<Integer>(items);
                    listOfSets.add(itemSet);
                    universe.addAll(itemSet);
                }
            }
        } else {
            for (int i = 0; i < container.size(); i++) {

                HashMap<Integer, Integer> items = (HashMap<Integer, Integer>) container.get(i);
                if (items != null) {
                    HashMap<Integer, Integer> itemSet = new HashMap<>(items);
                    listOfSets.add(itemSet);
                    universe.addAll(itemSet.keySet());
                }
            }
        }
    }

    /**
     * Min set cover.
     *
     * Note that all the tasks will be assigned after this.
     *
     * @return the number of assigned workers
     */
    public abstract HashSet<Integer> minSetCover();

}
