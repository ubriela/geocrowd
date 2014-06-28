/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd;

public enum AlgorithmEnum {
	BASIC, LLEP, NNP, ONLINE,	// geocrowd journal
	
	GREEDY1,	//	Set Cover Greedy
	GREEDY2,		//	Smallest associated set
	GREEDY3,	//	wait till deadline
	GREEDY4		// combine deadline and number of covered tasks
};
