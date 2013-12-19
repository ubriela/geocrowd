/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd;

/**
 * 
 * @author Leyla
 */
public class Coord {

	private int rowId;
	private int colId;

	public Coord(int r, int c) {
		rowId = r;
		colId = c;
	}

	public int getRowId() {
		return rowId;
	}

	public int getColId() {
		return colId;
	}

}
