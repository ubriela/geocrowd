/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd;

/**
 * 
 * @author Leyla
 */

/**
 * This class is used to compute location entropy. It stores a user and the
 * number of time the user check in a location
 */
public class Observation {
	private int userId;
	private int observeCount;

	public Observation(int u) {
		userId = u;
		observeCount = 1;
	}

	public int getUserId() {
		return userId;
	}

	public int getObservationCount() {
		return observeCount;
	}

	public void incObserveCount() {
		observeCount++;
	}
}
