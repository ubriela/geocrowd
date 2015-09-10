package org.geocrowd;

import org.geocrowd.common.crowd.ExpertTask;
import org.geocrowd.common.crowd.ExpertWorker;
import org.geocrowd.common.crowd.GenericTask;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.utils.TaskUtility;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdConstants;
import org.geocrowd.datasets.params.GeocrowdSensingConstants;

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
public class GeocrowdTaskUtility {

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
			return TaskUtility.distanceToTask(worker, task);

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
		if (Constants.UTILITY_FUNCTION == "zipf") {
			int k = Math.max(
					1,
					(int) Math.floor(dist * Constants.ZIPF_STEPS
							/ GeocrowdSensingConstants.TASK_RADIUS)); // rank
			double val = Utils.zipf_pmf(Constants.ZIPF_STEPS, k,
					Constants.s) * Constants.MU;
			// System.out.println(k);
			// System.out.println(val);
			return val;
		}

		if (Constants.UTILITY_FUNCTION == "linear") {
			// System.out.println(dist);
			return Math.max(0, (1 - (dist + 0.0) / GeocrowdSensingConstants.TASK_RADIUS)
					* Constants.MU);
		}

		if (Constants.UTILITY_FUNCTION == "const") {
			return Constants.MU;
		}

		return Constants.MU;
	}

}
