package org.geocrowd;

import java.util.HashSet;
import java.util.Iterator;

public class Expertise {
	private HashSet<Integer> expertise = new HashSet<>();

	
	public Expertise() {
		super();
	}

	// init expertise with one value
	public Expertise(int value) {
		super();
		if (!expertise.contains(value))
			expertise.add(value);
	}

	public void addExpertise(int exp) {
		expertise.add(exp);
	}

	public boolean isExactMatch(Task t) {
		if (expertise.contains(t.getTaskType()))
			return true;
		return false;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<Integer> it = expertise.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			sb.append(",");
		}
		return sb.substring(0, sb.length() - 1);
	}
}
