/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd;

import java.sql.Date;
import java.util.HashSet;

/**
 * 
 * @author Leyla
 */
public class Worker extends Expertise {
	private String userID;
	private double lat;
	private double lng;
	private int maxTaskNo;
	private MBR mbr;

	public Worker(String id, double lt, double ln, MBR m) {
		userID = id;
		lat = lt;
		lng = ln;
		maxTaskNo = 1;
		mbr = new MBR(m);
	}

	public Worker(String id, double lt, double ln, int maxT, MBR m) {
		userID = id;
		lat = lt;
		lng = ln;
		maxTaskNo = maxT;
		mbr = new MBR(m);
	}

	
	public Worker(double lt, double ln, int maxT, MBR m) {
		lat = lt;
		lng = ln;
		maxTaskNo = maxT;
		mbr = new MBR(m);
	}

	public int getMaxTaskNo() {
		return maxTaskNo;
	}

	public String getUserID() {
		return userID;
	}

	public void incMaxTaskNo() {
		maxTaskNo++;
	}

	public MBR getMBR() {
		return mbr;
	}

	public double getLatitude() {
		return lat;
	}

	public double getLongitude() {
		return lng;
	}

	public void print() {
		System.out.println("lat:" + lat + "   lng:" + lng + "   maxTaskNo:"
				+ maxTaskNo + "   mbr:[" + mbr.getMinLat() + ","
				+ mbr.getMinLng() + "," + mbr.getMaxLat() + ","
				+ mbr.getMaxLng());
	}

	public String toStr() {
		String str = userID + "," + lat + "," + lng + ","
				+ maxTaskNo + ",[" + mbr.getMinLat() + "," + mbr.getMinLng()
				+ "," + mbr.getMaxLat() + "," + mbr.getMaxLng() + "],[" + super.toString() + "]";
		return str;
	}

	public void setMinLat(double l) {
		mbr.minLat = l;
	}

	public void setMaxLat(double l) {
		mbr.maxLat = l;
	}

	public void setMinLng(double l) {
		mbr.minLng = l;
	}

	public void setMaxLng(double l) {
		mbr.maxLng = l;
	}
}
