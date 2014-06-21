package org.geocrowd.common;

/**
 * Each worker is associated with a working region (e.g., MBR)
 * 
 * @author HT186011
 *
 */
public class RegionWorker extends GenericWorker {
	
	private MBR mbr;
	
	public RegionWorker() {
		super();
	}
	
	public RegionWorker(String userID, double lat, double lng, int maxTaskNo, MBR mbr) {
		super(userID, lat, lng, maxTaskNo);
		
		this.mbr = mbr;
	}

	public MBR getMBR() {
		return mbr;
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
