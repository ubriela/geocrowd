/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd.common.entropy;

/**
 * 
 * @author Leyla
 * 
 * Coordinate of a grid cell
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
