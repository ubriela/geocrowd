package org.geocrowd.matching.online;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class OnlineBipartiteMatching {
	public ArrayList<Integer> workers = null;		// worker idx
	
	public ArrayList<Integer> ranks;		// the smaller index (index in workers), the higher rank
	
	/**
	 * Initialize variables
	 * @param container
	 */
	public OnlineBipartiteMatching(ArrayList<Integer> _workers) {
		 workers = _workers;	// workerids
		 java.util.Collections.shuffle(workers); // permute workers

		 // assign random rank for each worker
		 ranking();
	}
	
	/**
	 * Rank the workers
	 */
	public void ranking() {
		 for (int i = 0; i < workers.size(); i++) {
			 ranks.add(new Integer(i));
		 }
		 
		 java.util.Collections.shuffle(ranks);
	}
	

	/**
	 * Online algorithm
	 * @param container: an array of workerids
	 * @return the number of assigned tasks
	 */
	public int onlineMatching(ArrayList<ArrayList<Integer>> container) {
		int assignedTasks = 0;
		Iterator it = container.iterator();
		
		// iterate through task list
		while (it.hasNext()) {
			ArrayList<Integer> workerids = (ArrayList<Integer>) it.next();	//	list of workers eligible to perform this task
			
			// put all workerids into a hashset
			HashSet<Integer> hashids = new HashSet<Integer>();
			for (int i = 0; i < workerids.size(); i++) {
				hashids.add(workerids.get(i));
			}
			
			// find the worker of highest rank by iterate through ranks
			for (int i = 0; i < ranks.size(); i++) {
				if (hashids.contains(new Integer(i))) {
					assignedTasks++;
					
					// remove the task & rank from workers and ranks
					workers.remove(ranks.get(i));
					ranks.remove(i);
				}
			}
		}
		
		return assignedTasks;
	}
	
}
