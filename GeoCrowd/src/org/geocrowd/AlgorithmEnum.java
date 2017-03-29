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
package org.geocrowd;

// TODO: Auto-generated Javadoc
/**
 * The Enum AlgorithmEnum.
 */
public enum AlgorithmEnum {

	/** The basic. */
	BASIC,
	/** The llep. */
	LLEP,
	/** The nnp. */
	NNP,
	/** The online. */
	ONLINE, 
	// geocrowd journal

	/** Set Cover Greedy. */
	GREEDY_HIGH_TASK_COVERAGE,
	/** Low worker coverage priority. */
	GREEDY_LOW_WORKER_COVERAGE,
	/** High task coverage. */
	GREEDY_LARGE_WORKER_FANOUT,
	/** combine deadline and High task coverage. */
	GREEDY_CLOSE_TO_DEADLINE,
	/** Set Cover Greedy using alive concept **/
	GREEDY_HIGH_TASK_COVERAGE_MULTI,
	GREEDY_LARGE_WORKER_FANOUT_MULTI,
	GREEDY_CLOSE_TO_DEADLINE_MULTI,
        
        
        /** Max cover greedy **/
        MAX_COVER_BASIC,
        MAX_COVER_BASIC_WORKLOAD,
        MAX_COVER_BASIC_WORKLOAD_T,
        MAX_COVER_BASIC_MO,
        MAX_COVER_BASIC_W_MO,
        MAX_COVER_RANDOM_T,
        MAX_COVER_RANDOM_B,
        MAX_COVER_BASIC_T,
        MAX_COVER_BASIC_T2,
        MAX_COVER_BASIC_S,
        MAX_COVER_BASIC_S2,
        MAX_COVER_BASIC_S_MO,
        MAX_COVER_BASIC_ST,
        
        MAX_COVER_PRO_B,
        MAX_COVER_PRO_T,
        MAX_COVER_PRO_S,
        MAX_COVER_PRO_ST,
        
        MAX_COVER_ADAPT_B,
        MAX_COVER_ADAPT_B_W,
        MAX_COVER_NAIVE_T,
        MAX_COVER_NAIVE_B,
        MAX_COVER_ADAPT_T,
        MAX_COVER_ADAPT_T_W,
        MAX_COVER_ADAPT_S,
        MAX_COVER_ADAPT_S_W,
        MAX_COVER_ADAPT_ST,
        
        MAX_COVER_GA
        
       
	
	
};
