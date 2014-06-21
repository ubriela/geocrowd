/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd.common;

import org.geocrowd.util.Utils;

/**
 * 
 * @author Leyla
 */
public class MBR {
	public double minLat;
	public double maxLat;
	public double minLng;
	public double maxLng;

	public MBR(double lR, double lC, double hR, double hC) {
		minLat = lR;
		maxLat = hR;
		minLng = lC;
		maxLng = hC;
	}

	public MBR(MBR m) {
		minLat = m.minLat;
		maxLat = m.maxLat;
		minLng = m.minLng;
		maxLng = m.maxLng;
	}

	public void setMBR(double lR, double lC, double hR, double hC) {
		minLat = lR;
		maxLat = hR;
		minLng = lC;
		maxLng = hC;
	}

	public double getMinLat() {
		return minLat;
	}

	public double getMaxLat() {
		return maxLat;
	}

	public double getMinLng() {
		return minLng;
	}

	public double getMaxLng() {
		return maxLng;
	}

	public void setMinLat(double m) {
		minLat = m;
	}

	public void setMaxLat(double m) {
		maxLat = m;
	}

	public void setMinLng(double m) {
		minLng = m;
	}

	public void setMaxLng(double m) {
		maxLng = m;
	}

	public static MBR createMBR(double centerLat, double centerLng,
			double rangeX, double rangeY) {
		MBR mbr = new MBR(centerLat - (rangeX / 2), centerLng - (rangeY / 2),
				centerLat + (rangeX / 2), centerLng + (rangeY / 2));
		return mbr;
	}
	
	public double diagonalLength() {
		return Utils.distance(minLat, minLng, maxLat, maxLng);
	}
	
	public void print() {
		System.out.println("minLat:" + minLat + "   maxLat:" + maxLat
				+ "   minLng:" + minLng + "   maxLng:" + maxLng);
	}
	
	public double area() {
		MBR mbr = new MBR(minLat, minLng, maxLat, maxLng);
		double x = Utils.distance(minLat, minLng, maxLat, minLng);
		double y = Utils.distance(minLat, minLng, minLat, maxLng);
		return x*y;
	}
}
