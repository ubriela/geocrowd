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
	GREEDY_LARGE_WORKER_FANOUT_PRIORITY,
	/** combine deadline and High task coverage. */
	GREEDY_CLOSE_TO_DEADLINE,
	/** Set Cover Greedy using alive concept **/
	GREEDY_HIGH_TASK_COVERAGE_ALIVE_TASK
};
