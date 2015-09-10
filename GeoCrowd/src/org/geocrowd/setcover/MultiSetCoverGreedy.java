package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.geocrowd.Geocrowd;

public abstract class MultiSetCoverGreedy extends SetCoverGreedy{


	/**
	 * <taskid, the number of workers cover the task with that id>
	 */
	public HashMap<Integer, Integer> assignedTaskMap = new HashMap<>();
	
	public MultiSetCoverGreedy(ArrayList container,
			Integer current_time_instance) {
		super(container, current_time_instance);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @return
	 */
	public void filterNonqualifedTasks() {
		/**
		 * a set of covered tasks.
		 */
		assignedTaskSet = new HashSet<Integer>();

		/**
		 * make sure the tasks are covered by at least k workers
		 */
		for (Integer key : assignedTaskMap.keySet())
			if (assignedTaskMap.get(key) >= Geocrowd.taskList.get(key).getRequirement())
				assignedTaskSet.add(key);
		
		assignedTasks = assignedTaskSet.size();
		System.out.println("#Task assigned: " + assignedTasks);
	}
}
