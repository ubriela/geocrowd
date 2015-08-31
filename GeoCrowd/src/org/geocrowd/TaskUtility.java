package org.geocrowd;

import org.geocrowd.common.crowd.GenericTask;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.utils.Utils;

/**
 * This class uses Mediator pattern to reduce communication complexity between multiple
 * objects or classes. This pattern provides a mediator class which normally
 * handles all the communications between different classes and supports easy
 * maintenance of the code by loose coupling. Mediator pattern falls under
 * behavioral pattern category.
 * 
 * @author ubriela
 *
 */
public class TaskUtility {

	/**
	 * Euclidean distance between worker and task.
	 * 
	 * @param worker
	 *            the worker
	 * @param task
	 *            the task
	 * @return the double
	 */
	public static double distanceWorkerTask(DatasetEnum dataset,
			GenericWorker worker, GenericTask task) {
		if (dataset == DatasetEnum.GOWALLA || dataset == DatasetEnum.YELP
				|| dataset == DatasetEnum.FOURSQUARE)
			return worker.distanceToTask(task);

		// not geographical coordinates
		double distance = Math.sqrt((worker.getLat() - task.getLat())
				* (worker.getLat() - task.getLat())
				+ (worker.getLng() - task.getLng())
				* (worker.getLng() - task.getLng()));

		// System.out.println(distance);
		return distance;
	}

	/**
	 * Distance-based utility
	 * 
	 * @param w
	 * @param t
	 * @return
	 */
	public static double utility(DatasetEnum dataset, GenericWorker w,
			SensingTask t) {
		double dist = distanceWorkerTask(dataset, w, t);
		if (GeocrowdConstants.UTILITY_FUNCTION == "zipf") {
			int k = Math.max(
					1,
					(int) Math.floor(dist * GeocrowdConstants.ZIPF_STEPS
							/ GeocrowdConstants.radius)); // rank
			double val = Utils.zipf_pmf(GeocrowdConstants.ZIPF_STEPS, k,
					GeocrowdConstants.s) * GeocrowdConstants.MU;
			// System.out.println(k);
			// System.out.println(val);
			return val;
		}

		if (GeocrowdConstants.UTILITY_FUNCTION == "linear") {
			// System.out.println(dist);
			return Math.max(0, (1 - (dist + 0.0) / GeocrowdConstants.radius)
					* GeocrowdConstants.MU);
		}

		if (GeocrowdConstants.UTILITY_FUNCTION == "const") {
			return GeocrowdConstants.MU;
		}

		return GeocrowdConstants.MU;
	}

}
