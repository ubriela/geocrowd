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
package org.geocrowd.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

// TODO: Auto-generated Javadoc
/**
 * The Class OnlineBipartiteMatching.
 */
public class OnlineBipartiteMatching {

	/** The workers. */
	public HashMap<Integer, Integer> workerOrders = null;		// <order id, worker idx>

	/** The ranks. */
	public ArrayList<Integer> ranks; // values are order id. the smaller index
										// (index in workers), the higher rank

	/**
	 * Initialize variables.
	 * 
	 * @param workers
	 *            the workers
	 */
	public OnlineBipartiteMatching(ArrayList<Integer> workers) {
		workerOrders = new HashMap<>();
		for (int order = 0; order < workers.size(); order++)
			workerOrders.put(order, workers.get(order));

		// assign random rank for each worker
		ranking();
	}

	/**
	 * Online algorithm.
	 * 
	 * @param invertedContainer
	 *            the inverted container
	 * @return assignment, task index to worker index
	 */
	public HashMap<Integer, Integer> onlineMatching(
			HashMap<Integer, ArrayList> invertedContainer) {
		
		

		/* worker order id, task order id */
		HashMap<Integer, Integer> assignment = new HashMap<>();

		Iterator it = invertedContainer.keySet().iterator();

		// iterate through task list
		while (it.hasNext() && workerOrders.size() > 0) {
			Integer taskidx = (Integer) it.next();

			/* eligible workers to perform this task */
			HashSet<Integer> hashids = new HashSet<Integer>(invertedContainer.get(taskidx));

			// find the worker of highest rank by iterate through ranks
			for (int i = 0; i < ranks.size(); i++)
				if (hashids.contains(workerOrders.get(ranks.get(i)))) {
					assignment.put(taskidx, workerOrders.get(ranks.get(i)));

					// remove the task & rank from workers and ranks
					workerOrders.remove(ranks.get(i));
					ranks.remove(i);
					break; // find the worker
				}
		}

		return assignment;
	}

	/**
	 * Rank the workers.
	 */
	public void ranking() {
		ranks = new ArrayList<Integer>();
		for (int i = 0; i < workerOrders.size(); i++)
			ranks.add(new Integer(i));

		java.util.Collections.shuffle(ranks);
	}

}