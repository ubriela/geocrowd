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

import java.util.ArrayList;
import java.util.Iterator;


// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils {

	
	/**
	 * Distance.
	 * 
	 * @param minLat
	 *            the min lat
	 * @param minLng
	 *            the min lng
	 * @param maxLat
	 *            the max lat
	 * @param maxLng
	 *            the max lng
	 * @return the double
	 */
	public static double distance(double minLat, double minLng, double maxLat, double maxLng) {
		float pk = (float) (180 / 3.14169);
		double a1 = minLat / pk;
		double a2 = minLng / pk;
		double b1 = maxLat / pk;
		double b2 = maxLng / pk;
		
		float t1 = (float) (Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math
				.cos(b2));
		float t2 = (float) (Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math
				.sin(b2));
		float t3 = (float) (Math.sin(a1) * Math.sin(b1));
		double tt = Math.acos(t1 + t2 + t3);
		return 6366 * tt;
	}
	
	/**
	 * Compute Mean Contribution Distance
	 * 
	 * Ref: On the "localness" of user-generated content.
	 * 
	 * @param contributer
	 *            the contributer
	 * @param contributions
	 *            the contributions
	 * @return the double
	 */
	public static double MCD(Point contributer, ArrayList<Point> contributions) {
		double sum = 0;
		Iterator<Point> it = contributions.iterator();
		while (it.hasNext()) {
			Point pt = it.next();
			sum += distance(contributer.getX(), contributer.getY(), pt.getX(), pt.getY());
		}
		
		if (contributions.size() == 0)
			return 0.0;
		return sum/contributions.size();
	}

}
