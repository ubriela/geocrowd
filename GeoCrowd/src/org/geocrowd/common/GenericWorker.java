package org.geocrowd.common;

/**
 * 
 * @author HT186011
 *
 */
public class GenericWorker {
	private String userID;
	private double lat;
	private double lng;
	private int maxTaskNo;
	

	public GenericWorker() {
		super();
	}
	
	public GenericWorker(String userID, double lat, double lng, int maxTaskNo) {
		super();
		this.userID = userID;
		this.lat = lat;
		this.lng = lng;
		this.maxTaskNo = maxTaskNo;
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
	
	public double getLatitude() {
		return lat;
	}
	
	public double getLongitude() {
		return lng;
	}
	
	
}
