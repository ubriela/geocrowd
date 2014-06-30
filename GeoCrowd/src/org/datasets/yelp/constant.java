/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.datasets.yelp;

/**
 * 
 * @author dkh
 */
public class constant {
	public static String tasks_loc = "dataset/real/yelp/yelp_task.dat";
	public static String workers_loc = "dataset/real/yelp/yelp.dat";
	
	public static String business = "dataset/real/yelp/business.json";
	public static String review = "dataset/real/yelp/review.json";
	public static String user = "dataset/real/yelp/user.json";
	public static String entropy = "dataset/real/yelp/yelp_entropy.txt";
	public static String boundary = "dataset/real/yelp/yelp_boundary.txt";
	public static String curtail_review = "dataset/real/yelp/test.json";
	public static String SaveStatistic = "dataset/real/yelp/yelp_statistic.txt";

	public static String SplitWorkerByTime = "dataset/real/yelp/worker/yelp_workers";

	public static String SaveWorker = "dataset/real/yelp/worker/yelp_workers";
	public static String suffix = ".txt";
	public static String SaveTask = "dataset/real/yelp/task/yelp_tasks";

	public static int MaxReview = 20;
	public static double realResolution = 0.001;
	public static int WorkerPerFile = 1000;
	public static int TaskPerFile = 500;

}
