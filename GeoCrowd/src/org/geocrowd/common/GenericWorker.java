/*******************************************************************************
* @ Year 2013
* This is the source code of the following papers. 
* 
* 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla Kazemi, Cyrus Shahabi.
* 
* 
* Please contact the author Hien To, ubriela@gmail.com if you have any question.
*
* Contributors:
* Hien To - initial implementation
*******************************************************************************/
package org.geocrowd.common;

// TODO: Auto-generated Javadoc
/**
 * The Class GenericWorker.
 * 
 * @author HT186011
 */
public class GenericWorker {
	
	/** The user id. */
	private String userID;
	
	/** The lat. */
	private double lat;
	
	/** The lng. */
	private double lng;
	
	/** The max task no. */
	private int maxTaskNo;
	

	/**
	 * Instantiates a new generic worker.
	 */
	public GenericWorker() {
		super();
	}
	
	/**
	 * Instantiates a new generic worker.
	 * 
	 * @param userID
	 *            the user id
	 * @param lat
	 *            the lat
	 * @param lng
	 *            the lng
	 * @param maxTaskNo
	 *            the max task no
	 */
	public GenericWorker(String userID, double lat, double lng, int maxTaskNo) {
		super();
		this.userID = userID;
		this.lat = lat;
		this.lng = lng;
		this.maxTaskNo = maxTaskNo;
	}

	/**
	 * Gets the latitude.
	 * 
	 * @return the latitude
	 */
	public double getLatitude() {
		return lat;
	}
	
	/**
	 * Gets the longitude.
	 * 
	 * @return the longitude
	 */
	public double getLongitude() {
		return lng;
	}
	
	/**
	 * Gets the max task no.
	 * 
	 * @return the max task no
	 */
	public int getMaxTaskNo() {
//		return 1;
		return maxTaskNo;
	}
	
	/**
	 * Gets the user id.
	 * 
	 * @return the user id
	 */
	public String getUserID() {
		return userID;
	}
	
	/**
	 * Inc max task no.
	 */
	public void incMaxTaskNo() {
		maxTaskNo++;
	}
	
	
}
