package org.geocrowd;

public class Utils {

	/**
	 * distance bwn two geographical coord in km
	 * @param worker
	 * @param task
	 * @return
	 */
	public static double computeDistance(Worker worker, Task task) {

		float pk = (float) (180 / 3.14169);
		double a1 = worker.getLatitude() / pk;
		double a2 = worker.getLongitude() / pk;
		double b1 = task.getLat() / pk;
		double b2 = task.getLng() / pk;

		float t1 = (float) (Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math
				.cos(b2));
		float t2 = (float) (Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math
				.sin(b2));
		float t3 = (float) (Math.sin(a1) * Math.sin(b1));
		double tt = Math.acos(t1 + t2 + t3);
		return 6366 * tt;
	}

}
