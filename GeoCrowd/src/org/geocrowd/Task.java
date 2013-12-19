/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd;

/**
 * 
 * @author Leyla
 */
public class Task {
	private double lat;
	private double lng;
	private int entryTime;
	private int assigned = 0;
	private boolean expired = false;
	private double entropy;
	private int type;

	public Task() {

	}

	public Task(double lt, double ln, int entry, double dens, int taskType) {
		lat = lt;
		lng = ln;
		entryTime = entry;
		entropy = dens;
		type = taskType;
	}

	public Task(double lt, double ln, int entry, double dens) {
		lat = lt;
		lng = ln;
		entryTime = entry;
		entropy = dens;
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

	public void print() {
		System.out.println("lat:" + lat + "   lng:" + lng + "   time:"
				+ entryTime);
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

	public int getTaskType() {
		return type;
	}

	public boolean isCoveredBy(double minLat_mbr, double minLng_mbr,
			double maxLat_mbr, double maxLng_mbr) {
		if ((lat >= minLat_mbr) && (lat <= maxLat_mbr) && (lng >= minLng_mbr)
				&& (lng <= maxLng_mbr))
			return true;
		return false;
	}
}
