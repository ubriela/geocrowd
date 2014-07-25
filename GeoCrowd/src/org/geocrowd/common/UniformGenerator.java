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
package org.geocrowd.common;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * Provide various of methods for generating uniform datasets as well as
 * queries.
 * 
 * @author HT186010
 */
public class UniformGenerator {

	/**
	 * Generate a list of random distinct values.
	 * 
	 * @param n
	 *            the n
	 * @param boundary
	 *            the boundary
	 * @param isInteger
	 *            the is integer
	 * @return the hash set
	 */
	public static HashSet<Double> randomDistinctValues(int n, Range boundary, boolean isInteger) {
		HashSet<Double> values = new HashSet<Double>();
		while (values.size() < n) {
			values.add(randomValue(boundary, isInteger));
		}
		return values;
	}

	/**
	 * Generate a one-dimensional random range query.
	 * 
	 * @param number
	 *            the number
	 * @param offset
	 *            the offset
	 * @param isFixOffset
	 *            the is fix offset
	 * @param values
	 *            the values
	 * @param boundary
	 *            the boundary
	 * @return the vector
	 */
	public static Vector<Range> randomRangesWithOffsets(int number,
			double offset, boolean isFixOffset, List<Double> values,
			Range boundary) {
		Vector<Range> ranges = new Vector<Range>();
		int size = values.size();
		Random generator = new Random();
		double _offset = 0.0;
		if (isFixOffset) {
			_offset = offset;
		} else {
			generator.setSeed(System.nanoTime());
			_offset = generator.nextDouble() * offset;
		}
		for (int i = 0; i < number; i++) {
			generator.setSeed(System.nanoTime());
			int r = generator.nextInt(size);
			double start, end;
			start = Math.max(boundary.getStart(), values.get(r) - _offset);
			end = Math.min(boundary.getEnd(), values.get(r) + _offset);
			Range range = new Range(start, end);
			ranges.add(range);
		}
		return ranges;
	}

	/**
	 * generate random rectangles such that their the lower-left points and
	 * high-right points are from the data points.
	 * 
	 * @param number
	 *            the number
	 * @param points
	 *            the points
	 * @return the vector
	 */
	public static Vector<Rectangle> randomRectanglesWithinDataPoints(
			int number, Vector<Point> points) {
		Vector<Rectangle> recs = new Vector<Rectangle>();
		int size = points.size();
		Random generator = new Random();
		for (int i = 0; i < number; i++) {
			generator.setSeed(System.nanoTime());
			int index_1 = generator.nextInt(size);
			int index_2 = generator.nextInt(size);
			double x1, y1, x2, y2;
			x1 = points.get(index_1).getX();
			y1 = points.get(index_1).getY();
			x2 = points.get(index_2).getX();
			y2 = points.get(index_2).getY();
			Rectangle rec = null;
			if (x1 < x2 && y1 < y2)
				rec = new Rectangle(x1, y1, x2, y2);
			else if (x2 < x1 && y2 < y1)
				rec = new Rectangle(x2, y2, x1, y1);
			else {
				i--;
				continue;
			}
			recs.add(rec);
		}
		return recs;
	}

	/**
	 * Generate a random list of values.
	 * 
	 * @param n
	 *            the n
	 * @param min_x
	 *            the min_x
	 * @param max_x
	 *            the max_x
	 * @param isInteger
	 *            the is integer
	 * @return the vector
	 */
	public static Vector<Double> randomSequence(int n, double min_x,
			double max_x, boolean isInteger) {
		Vector<Double> result = new Vector<Double>();
		Random r = new Random();
		r.setSeed(System.nanoTime());
		for (int i = 0; i < n; i++) {
			if (isInteger)
				result.add(Math.floor(r.nextDouble() * (max_x - min_x) + min_x));
			else
				result.add(r.nextDouble() * (max_x - min_x) + min_x);
		}
		return result;
	}

	/**
	 * Generate a random value between min, max.
	 * 
	 * @param boundary
	 *            the boundary
	 * @param isInteger
	 *            the is integer
	 * @return the double
	 */
	public static double randomValue(Range boundary, boolean isInteger) {
		Random r = new Random();
		r.setSeed(System.nanoTime());
		if (isInteger)
			return (Math.round(r.nextDouble() * boundary.delta()
					+ boundary.getStart()));
		else
			return (r.nextDouble() * boundary.delta() + boundary.getStart());
	}
	
	/**
	 * Generate a list of random value in a list, the values can be overlapped.
	 * 
	 * @param test_size
	 *            the test_size
	 * @param values
	 *            the values
	 * @return the vector
	 */
	public static Vector<Double> randomValues(int test_size, List<Double> values) {
		// TODO Auto-generated method stub
		Random r = new Random();
		Vector<Double> list = new Vector<Double>();
		for (int i = 0; i < test_size; i++) {
			r.setSeed(System.nanoTime());
			list.add(values.get(r.nextInt(values.size())));
		}
		return list;
	}
}
