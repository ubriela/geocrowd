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
 * The Class Constants.
 */
public class Constants {

	public static double alpha = 0.5;
	
	/** The Task no. */
	public static int TaskNo = 1000; // 200; // number of tasks
	
	public static int WorkerNo = 1000;

	/** The time instance. */
	public static int TIME_INSTANCE = 20;
	
	/** The diameter. */
	public static double diameter = 4; // task circle diameter
	/** enable random k */
	public static boolean IS_RANDOM_K = false;
	
	/** required number of responses */
	public static int K = 1;
	
	/**
	 * Only choose worker covers at least k tasks.
	 */
	public static int M = 4;
	
	/**
	 * Time to deadline
	 */
	public static int T = 5;
	
	public static boolean useLocationEntropy = true;
	
	// ------------------------------------------------------------
	
	// shared parameters
	/** The Task type no. */
	public static double TaskTypeNo = 0; // number of task types/expertise

	/** The Constant EXPERTISE_MATCH_SCORE. */
	public static final double EXPERTISE_MATCH_SCORE = 1.5;

	/** The Constant NON_EXPERTISE_MATCH_SCORE. */
	public static final double NON_EXPERTISE_MATCH_SCORE = 1;


	/** The Task duration. */
	public static int TaskDuration = 1;// 20000; //duration of all tasks before
										// they expire are fixed to 1000ms
	/** The Max tasks per worker. */
	public static int MaxTasksPerWorker = 20; // maximum # of tasks that a
	// worker want to get

	/** The Max range perc. */
	public static double MaxRangePerc = 0.05; // maximum range of an mbr is 5%
	// of the entire x or y
	// dimensionF

	// small dataset
	/** The small task file name prefix. */
	public static String smallTaskFileNamePrefix = "dataset/small/maxcover/task/tasks";

	/** The small worker file path. */
	public static String smallWorkerFilePath = "dataset/small/worker/locations";

	/** The small worker file name prefix. */
	public static String smallWorkerFileNamePrefix = "dataset/small/maxcover/worker/workers";

	/** The small boundary. */
	public static String smallBoundary = "dataset/small/small_boundary.txt";

	/** The small location density file name. */
	public static String smallLocationDensityFileName = "dataset/small/small_loc_entropy.txt";

	/** The small resolution. */
	public static double smallResolution = 1;

	// real dataset yelp
	/** The yelp resolution. */
	public static double yelpResolution = 0.001;

	/** The yelp entropy file name. */
	public static String yelpEntropyFileName = "dataset/real/yelp/yelp_entropy.txt";

	/** The yelp location entropy file name. */
	public static String yelpLocationEntropyFileName = "dataset/real/yelp/yelp_entropy.txt";

	/** The yelp task file name prefix. */
	public static String yelpTaskFileNamePrefix = "dataset/real/yelp/task/yelp_tasks";

	/** The yelp worker file name prefix. */
	public static String yelpWorkerFileNamePrefix = "dataset/real/yelp/worker/yelp_workers";

	/** The yelp boundary. */
	public static String yelpBoundary = "dataset/real/yelp/yelp_boundary.txt";

	// real dataset gowalla
	/** The gowalla resolution. */
	public static double gowallaResolution = 0.00002; // 0.00002 is approximately 30x30 metres
	/** The gowalla file name. */
	public static String gowallaFileName = "dataset/real/gowalla/gowalla_totalCheckins.txt";

	/** The gowalla file name_ ca. */
	public static String gowallaFileName_CA = "dataset/real/gowalla/gowalla_CA";

	/** The gowalla file name_ sa. */
	public static String gowallaFileName_SA = "dataset/real/gowalla/gowalla_totalCheckins_SA.txt";

	/** The gowalla entropy file name. */
	public static String gowallaEntropyFileName = "dataset/real/gowalla/gowalla_entropy.txt";

	/** The gowalla location entropy file name. */
	public static String gowallaLocationEntropyFileName = "dataset/real/gowalla/gowalla_loc_entropy.txt";

	public static String gowallaLocationDensityFileName = "dataset/real/gowalla/gowalla_loc_density.txt";
	
	/** The gowalla task file name prefix. */
	public static String gowallaTaskFileNamePrefix = "dataset/real/gowalla/task/gowalla_tasks";

	/** The gowalla worker file name prefix. */
	public static String gowallaWorkerFileNamePrefix = "dataset/real/gowalla/worker/gowalla_workers";

	/** The gowalla boundary. */
	public static String gowallaBoundary = "dataset/real/gowalla/gowalla_CA_boundary.txt";

	public static String gowallaFileName_CA_loc = "dataset/real/gowalla/gowalla_CA.dat";
	
	// synthetic dataset
	/** The skewed resolution. */
	public static double skewedResolution = 0.01;

	/** The uni resolution. */
	public static double uniResolution = 0.01;

	/** The skewed matlab worker file path. */
	public static String skewedMatlabWorkerFilePath = "C:/Users/ubriela/Documents/MATLAB/sync-workers";

	/** The uni matlab worker file path. */
	public static String uniMatlabWorkerFilePath = "C:/Users/ubriela/Documents/MATLAB/uni-workers";

	/** The matlab task file path. */
	public static String matlabTaskFilePath = "C:/Users/ubriela/Documents/MATLAB/uni-tasks";

	// public static String skewedMatlabWorkerFilePath =
	// "B:/Dropbox/_USC/_Research/_Crowdsourcing/Privacy/PrivateGeoCrowd/src/dataset/taskworker/workers";
	// public static String matlabTaskFilePath =
	// "B:/Dropbox/_USC/_Research/_Crowdsourcing/Privacy/PrivateGeoCrowd/src/dataset/taskworker/tasks";

	/** The skewed location density file name. */
	public static String skewedLocationDensityFileName = "dataset/skew/skew_loc_entropy.txt";

	/** The skewed task file name prefix. */
	public static String skewedTaskFileNamePrefix = "dataset/skew/task/skew_tasks";

	/** The skewed worker file name prefix. */
	public static String skewedWorkerFileNamePrefix = "dataset/skew/worker/skew_workers";

	/** The skewed boundary. */
	public static String skewedBoundary = "dataset/skew/skew_boundary.txt";

	/** The uni location density file name. */
	public static String uniLocationDensityFileName = "dataset/uni/uni_loc_entropy.txt";

	/** The uni task file name prefix. */
	public static String uniTaskFileNamePrefix = "dataset/uni/task/uni_tasks";

	/** The uni worker file name prefix. */
	public static String uniWorkerFileNamePrefix = "dataset/uni/worker/uni_workers";

	/** The uni boundary. */
	public static String uniBoundary = "dataset/uni/uni_boundary.txt";

}