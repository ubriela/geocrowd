package org.geocrowd.common;


/**
 * 
 * @author HT186011
 *
 */
public class GenericTask {
	private double lat;
	private double lng;
	private int entryTime;
	private int assigned = 0;		// how many times this task is assigned
	private boolean expired = false;
	private double entropy;
	
	public GenericTask() {
		
	}
	
	public GenericTask(double lt, double ln, int entry, double ent) {
		lat = lt;
		lng = ln;
		entryTime = entry;
		entropy = ent;
	}

	public int getEntryTime() {
		return entryTime;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}

	public void incAssigned() {
		assigned++;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired() {
		expired = true;
	}

	public double getEntropy() {
		return entropy;
	}
	
	
	public String toString() {
		return "lat: " + lat + "   lng: " + lng + "   time: "
				+ entryTime + "    assigned: " + assigned + "    expired : " + expired;
	}

	/**
	 * check if the task is covered by a MBR
	 * @param mbr
	 * @return
	 */
	public boolean isCoveredBy(MBR mbr) {
		if ((lat >= mbr.minLat) && (lat <= mbr.maxLat) && (lng >= mbr.minLng)
				&& (lng <= mbr.maxLng))
			return true;
		return false;
	}
}