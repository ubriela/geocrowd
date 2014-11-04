package org.geocrowd.common.crowdsource;

public class SensingWorker extends GenericWorker {

	/** The entry time. */
	private int onlineTime;
	
	
	public SensingWorker(String userID, double lat, double lng, int maxTaskNo, int onlineTime) {
		// TODO Auto-generated constructor stub
		this.onlineTime = onlineTime;
	}


	public int getOnlineTime() {
		return onlineTime;
	}


	public void setOnlineTime(int onlineTime) {
		this.onlineTime = onlineTime;
	}
}
