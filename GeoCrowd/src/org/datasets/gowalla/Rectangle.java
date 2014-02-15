package org.datasets.gowalla;

import java.io.Serializable;

/**
 * A range query in two-dimensional system
 * 
 * @author HT186010
 * 
 */
public class Rectangle implements Serializable {
	private Point lowPoint;
	private Point highPoint;

	public Rectangle(Point lowPoint, Point highPoint) {
		super();
		this.lowPoint = lowPoint;
		this.highPoint = highPoint;
	}

	public Rectangle(double min_x, double min_y, double max_x, double max_y) {
		super();
		this.lowPoint = new Point(min_x, min_y);
		this.highPoint = new Point(max_x, max_y);
	}

	public final Point getLowPoint() {
		return lowPoint;
	}

	public final Point getHighPoint() {
		return highPoint;
	}

	public void setLowPoint(Point lowPoint) {
		this.lowPoint = lowPoint;
	}

	public void setHighPoint(Point highPoint) {
		this.highPoint = highPoint;
	}

	public double area() {
		return (deltaX() * deltaY());
	}

	public double deltaX() {
		return highPoint.getX() - lowPoint.getX();
	}

	public double deltaY() {
		return highPoint.getY() - lowPoint.getY();
	}

	/**
	 * Calculate intersected area bwn two rectangles
	 * 
	 * @param rec
	 * @return
	 */
	public double intersectArea(Rectangle rec) {
		// if this rectangle area == 0 or the rec's area == 0 or not intersect
		if (area() == 0 || rec.area() == 0 || !isIntersect(rec))
			return 0.0;

		double min_x = Math.min(lowPoint.getX(), rec.getLowPoint().getX());
		double min_y = Math.min(lowPoint.getY(), rec.getLowPoint().getY());
		double max_x = Math.max(highPoint.getX(), rec.getHighPoint().getX());
		double max_y = Math.max(highPoint.getY(), rec.getHighPoint().getY());
		double deltaMaxX = max_x - min_x;
		double deltaMaxY = max_y - min_y;

		return (area() + rec.area() + (deltaMaxX - deltaX())
				* (deltaMaxY - rec.deltaY()) + (deltaMaxX - rec.deltaX())
				* (deltaMaxY - deltaY()) - deltaMaxX * deltaMaxY);
	}

	/**
	 * If this rectangle intersects rec --> return true
	 * 
	 * @param rec
	 * @return
	 */
	public boolean isIntersect(Rectangle rec) {
		double min_x = Math.min(lowPoint.getX(), rec.getLowPoint().getX());
		double min_y = Math.min(lowPoint.getY(), rec.getLowPoint().getY());
		double max_x = Math.max(highPoint.getX(), rec.getHighPoint().getX());
		double max_y = Math.max(highPoint.getY(), rec.getHighPoint().getY());

		if ((max_x - min_x < deltaX() + rec.deltaX())
				&& (max_y - min_y < deltaY() + rec.deltaY()))
			return true;	//	intersect
		return false;
	}

	/**
	 * Does this rectangle cover the other rec
	 * 
	 * @param rec
	 * @return
	 */
	public boolean isCover(Rectangle rec) {
		if (lowPoint.getX() <= rec.lowPoint.getX()
				&& lowPoint.getY() <= rec.lowPoint.getY()
				&& highPoint.getX() >= rec.highPoint.getX()
				&& highPoint.getY() >= rec.highPoint.getY())
			return true;
		return false;
	}

	/**
	 * Does this rectangle cover a point
	 * 
	 * @param rec
	 * @return
	 */
	public boolean isCover(Point point) {
		if (lowPoint.getX() <= point.getX() && lowPoint.getY() <= point.getY()
				&& highPoint.getX() >= point.getX()
				&& highPoint.getY() >= point.getY())
			return true;
		return false;
	}

	// public String toString() {
	// return "(" + lowPoint.getX() + "," + lowPoint.getY() + ")->("
	// + highPoint.getX() + "," + highPoint.getY() + ")";
	// }

	public void debug() {
		System.out.println(lowPoint.getX() + ":" + lowPoint.getY() + ":"
				+ highPoint.getX() + ":" + highPoint.getY());
		// System.out.println("select lat, lon from distinctint where lat>="
		// + lowPoint.getX() + " and lat <=" + highPoint.getX()
		// + " and lon >=" + lowPoint.getY() + " and lon <="
		// + highPoint.getY() + ";");
	}

	/**
	 * rounding the rectangle to integer coords
	 */
	public void roundingRectangle() {
		lowPoint.setX(Math.floor(lowPoint.getX()));
		lowPoint.setY(Math.floor(lowPoint.getY()));
		highPoint.setX(Math.ceil(highPoint.getX()));
		highPoint.setY(Math.ceil(highPoint.getY()));
	}
}
