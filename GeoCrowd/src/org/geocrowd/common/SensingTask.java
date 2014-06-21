package org.geocrowd.common;


/**
 * 
 * @author HT186011
 * 
 * Each task has a region (e.g., circle whose center is task location), in which any worker within the 
 * region can perform the task
 *
 */
public class SensingTask extends GenericTask {

	private double radius;		// of the task region
	
	public SensingTask(double lt, double ln, int entry, double ent) {
		super(lt, ln, entry, ent);
	}
	
	public SensingTask(double radius) {
		this.radius = radius;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	
}