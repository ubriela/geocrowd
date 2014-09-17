/**
 * *****************************************************************************
 * @ Year 2013 This is the source code of the following papers.
 * 
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla
 * Kazemi, Cyrus Shahabi.
 * 
 *
 * Please contact the author Hien To, ubriela@gmail.com if you have any
 * question.
 * 
 * Contributors: Hien To - initial implementation
 ******************************************************************************
 */
package org.geocrowd.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils {

	/**
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	public static double distance(double lat1, double lon1, double lat2,
			double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344;
		return (dist);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	public static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	public static double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
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
			sum += distance(contributer.getX(), contributer.getY(), pt.getX(),
					pt.getY());
		}

		if (contributions.size() == 0) {
			return 0.0;
		}
		return sum / contributions.size();
	}

	private static void getSubsets(List<Integer> superSet, int k, int idx,
			Set<Integer> current, List<Set<Integer>> solution) {
		// successful stop clause
		if (current.size() == k) {
			solution.add(new HashSet<>(current));
			return;
		}
		// unseccessful stop clause
		if (idx == superSet.size()) {
			return;
		}
		Integer x = superSet.get(idx);
		current.add(x);
		// "guess" x is in the subset
		getSubsets(superSet, k, idx + 1, current, solution);
		current.remove(x);
		// "guess" x is not in the subset
		getSubsets(superSet, k, idx + 1, current, solution);
	}
	
	private static void getSubsets2(List<Integer> superSet, int k, int idx,
			Set<Integer> current, List<LinkedList<Integer>> solution) {
		// successful stop clause
		if (current.size() == k) {
			LinkedList<Integer> ll = new LinkedList<>(current);
			Collections.sort(ll);
			solution.add(ll);
			return;
		}
		// unseccessful stop clause
		if (idx == superSet.size()) {
			return;
		}
		Integer x = superSet.get(idx);
		current.add(x);
		// "guess" x is in the subset
		getSubsets2(superSet, k, idx + 1, current, solution);
		current.remove(x);
		// "guess" x is not in the subset
		getSubsets2(superSet, k, idx + 1, current, solution);
	}
	
	public static List<LinkedList<Integer>> getSubsets2(List<Integer> superSet, int k) {
		List<LinkedList<Integer>> res = new ArrayList<>();
		getSubsets2(superSet, k, 0, new HashSet<Integer>(), res);
		return res;
	}

	public static List<Set<Integer>> getSubsets(List<Integer> superSet, int k) {
		List<Set<Integer>> res = new ArrayList<>();
		getSubsets(superSet, k, 0, new HashSet<Integer>(), res);
		return res;
	}
}
