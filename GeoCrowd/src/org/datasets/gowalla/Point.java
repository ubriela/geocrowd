package org.datasets.gowalla;

import java.io.Serializable;

/**
 * two dimensional data type
 * @author HT186010
 *
 */
public class Point implements Serializable {
	private double X, Y;

	public Point(double x, double y) {
		super();
		X = x;
		Y = y;
	}

	public final double getX() {
		return X;
	}

	public final double getY() {
		return Y;
	}

	public void setX(double x) {
		X = x;
	}

	public void setY(double y) {
		Y = y;
	}
	
	
}
