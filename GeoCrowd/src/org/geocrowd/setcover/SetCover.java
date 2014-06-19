package org.geocrowd.setcover;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class SetCover {
	ArrayList<HashSet<Integer>> setOfSets = null;
	HashSet<Integer> universe = null;
	
	
	/**
	 * Initialize variables
	 * @param container: a set of set
	 */
	public SetCover(ArrayList<ArrayList> container) {
		setOfSets = new ArrayList<>();
		universe = new HashSet<>();
		
		for (int i = 0; i < container.size(); i++) {
			ArrayList<Integer> items = container.get(i);
			if (items != null) {
				HashSet<Integer> itemSet = new HashSet<Integer>(items);
				setOfSets.add(itemSet);
				universe.addAll(itemSet);
			}
		}
	}
	
	public abstract int minSetCover();
	
}
