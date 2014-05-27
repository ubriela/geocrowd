package org.geocrowd;

public class Constants {

	// shared parameters
	public static double TaskTypeNo = 0; // number of task types/expertise
	public static final double EXPERTISE_MATCH_SCORE = 1.5;
	public static final double NON_EXPERTISE_MATCH_SCORE = 1;

	public static int TaskNo = 1000; // 200; // number of tasks
	public static int RoundCnt = 20; // number of instance times
	public static int TaskDuration = 10;// 20000; //duration of all tasks before
										// they expire are fixed to 1000ms
	public static int MaxTasksPerWorker = 20; // maximum # of tasks that a
	// worker want to get

	public static double MaxRangePerc = 0.05; // maximum range of an mbr is 5%
	// of the entire x or y
	// dimensionF

	// small dataset
	public static String smallTaskFileNamePrefix = "dataset/small/task/tasks";
	public static String smallWorkerFilePath = "dataset/small/worker/locations";
	public static String smallWorkerFileNamePrefix = "dataset/small/worker/workers";
	public static String smallBoundary = "dataset/small/small_boundary.txt";
	public static String smallLocationDensityFileName = "dataset/small/small_loc_entropy.txt";
	public static double smallResolution = 1;

	// real dataset yelp
	public static double yelpResolution = 0.001;
	public static String yelpEntropyFileName = "dataset/real/yelp/yelp_entropy.txt";
	public static String yelpLocationEntropyFileName = "dataset/real/yelp/yelp_entropy.txt";
	public static String yelpTaskFileNamePrefix = "dataset/real/yelp/task/yelp_tasks";
	public static String yelpWorkerFileNamePrefix = "dataset/real/yelp/worker/yelp_workers";
	public static String yelpBoundary = "dataset/real/yelp/yelp_boundary.txt";

	// real dataset gowalla
	public static double gowallaResolution = 0.0002; // this means that every grid
													// cell
													// is 0.0002 by 0.0002 in
													// lat
													// and lon formats
	public static String gowallaFileName = "dataset/real/gowalla/gowalla_totalCheckins.txt";
	public static String gowallaFileName_CA = "dataset/real/gowalla/gowalla_totalCheckins_CA.txt";
	public static String gowallaFileName_SA = "dataset/real/gowalla/gowalla_totalCheckins_SA.txt";
	public static String gowallaEntropyFileName = "dataset/real/gowalla/gowalla_entropy.txt";
	public static String gowallaLocationEntropyFileName = "dataset/real/gowalla/gowalla_loc_entropy.txt";
	public static String gowallaTaskFileNamePrefix = "dataset/real/gowalla/task/gowalla_tasks";
	public static String gowallaWorkerFileNamePrefix = "dataset/real/gowalla/worker/gowalla_workers";
	public static String gowallaBoundary = "dataset/real/gowalla/gowalla_CA_boundary.txt";

	// synthetic dataset
	public static double skewedResolution = 0.01;
	public static double uniResolution = 0.01;
	public static String skewedMatlabWorkerFilePath = "C:/Users/ubriela/Documents/MATLAB/sync-workers";
	public static String uniMatlabWorkerFilePath = "C:/Users/ubriela/Documents/MATLAB/uni-workers";
	public static String matlabTaskFilePath = "C:/Users/ubriela/Documents/MATLAB/uni-tasks";
	
//	public static String skewedMatlabWorkerFilePath = "B:/Dropbox/_USC/_Research/_Crowdsourcing/Privacy/PrivateGeoCrowd/src/dataset/taskworker/workers";	
//	public static String matlabTaskFilePath = "B:/Dropbox/_USC/_Research/_Crowdsourcing/Privacy/PrivateGeoCrowd/src/dataset/taskworker/tasks";

	public static String skewedLocationDensityFileName = "dataset/skew/skew_loc_entropy.txt";
	public static String skewedTaskFileNamePrefix = "dataset/skew/task/skew_tasks";
	public static String skewedWorkerFileNamePrefix = "dataset/skew/worker/skew_workers";
	public static String skewedBoundary = "dataset/skew/skew_boundary.txt";
	
	public static String uniLocationDensityFileName = "dataset/uni/uni_loc_entropy.txt";
	public static String uniTaskFileNamePrefix = "dataset/uni/task/uni_tasks";
	public static String uniWorkerFileNamePrefix = "dataset/uni/worker/uni_workers";
	public static String uniBoundary = "dataset/uni/uni_boundary.txt";

}