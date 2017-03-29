package org.geocrowd;

public class Constants {

	public static double alpha = 0.1;
	public static double theta = 0.5;

	/** The Constant EXPERTISE_MATCH_SCORE. */
	public static final double EXPERTISE_MATCH_SCORE = 1.5;

	/** The Constant NON_EXPERTISE_MATCH_SCORE. */
	public static final double NON_EXPERTISE_MATCH_SCORE = 1;

	public static boolean useLocationEntropy = true;

	/**
	 * the value of the exponent characterizing the zipf distribution
	 */
	public static int s = 1;
	
	/**
	 * maximum utility
	 */
	public static final double MU = 1.0;

	public static String UTILITY_FUNCTION = "const";
	public static int ZIPF_STEPS = 100;
	
	public static boolean workerOverload = false;
	//public static double 
	
	/**
	 * Only choose worker covers at least k tasks.
	 */
	public static int M = 4;
}
