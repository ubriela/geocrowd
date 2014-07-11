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
 * The Class SetCover.
 */
public abstract class SetCover {

	/** 
	 * Each element in the list is associated with a worker, 
	 * and each worker contains a set of task ids that he is 
	 * eligible to perform
	 **/
	ArrayList<HashSet<Integer>> listOfSets = null;

	/** All the task index in the candidate tasks (not the task list). */
	public HashSet<Integer> universe = null;

	/**
	 * Initialize variables.
	 * 
	 * @param container
	 *            the container
	 */
	public SetCover(ArrayList<ArrayList> container) {
		listOfSets = new ArrayList<>();
		universe = new HashSet<>();

		for (int i = 0; i < container.size(); i++) {
			ArrayList<Integer> items = container.get(i);
			if (items != null) {
				HashSet<Integer> itemSet = new HashSet<Integer>(items);
				listOfSets.add(itemSet);
				universe.addAll(itemSet);
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
	public abstract int minSetCover();

}
