package org.geocrowd.common;


import java.util.HashSet;
import java.util.Iterator;

/**
 * Each worker has a working region and a set of expertise
 * 
 * @author Leyla
 */
public class SpecializedWorker extends RegionWorker {

	private HashSet<Integer> expertise = new HashSet<>();
	
	public SpecializedWorker(String id, double lt, double ln, int maxT, MBR mbr) {
		super(id, lt, ln, maxT, mbr);
	}
	
	
	// init expertise with one value
	public SpecializedWorker(int value) {
		if (!expertise.contains(value))
			expertise.add(value);
	}

	public void addExpertise(int exp) {
		expertise.add(exp);
	}

	public boolean isExactMatch(SpecializedTask t) {
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

	public String toStr() {
		String str = getUserID() + "," + getLatitude() + "," + getLongitude() + ","
				+ getMaxTaskNo() + ",[" + getMBR().getMinLat() + "," + getMBR().getMinLng()
				+ "," + getMBR().getMaxLat() + "," + getMBR().getMaxLng() + "],[" + super.toString() + "]";
		return str;
	}
}
