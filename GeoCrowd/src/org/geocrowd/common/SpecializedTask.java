/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd.common;


/**
 * 
 * @author Hien To
 * 
 * each task has a task type, e.g., rating a dish at a restaurant 
 * or taking a high quality picture
 */
public class SpecializedTask extends GenericTask {

	private int type;

	public SpecializedTask() {
		super();
	}

	public SpecializedTask(double lt, double ln, int entry, double dens, int taskType) {
		super(lt, ln, entry, dens);
		type = taskType;
	}

	public int getTaskType() {
		return type;
	}
}
