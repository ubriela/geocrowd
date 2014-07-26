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
package org.geocrowd.matching;

import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class RunTest.
 * 
 * @author dkh
 */
public class RunTest {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		// Below enter "max" or "min" to find maximum sum or minimum sum
		// assignment.

		// <UNCOMMENT> BELOW AND COMMENT BLOCK ABOVE TO USE A RANDOMLY GENERATED
		// MATRIX

		int numOfRows = Utility.readInput("How many rows for the matrix? ");
		int numOfCols = Utility.readInput("How many columns for the matrix? ");
		double[][] array = new double[numOfRows][numOfCols];
		// Utility.scan(array);
		Utility.generateRandomArray(array, "random");

		// </UNCOMMENT>

		if (array.length > array[0].length) {
			System.out.println("Array transposed (because rows>columns).\n"); // Cols
																				// must
																				// be
																				// >=
																				// Rows.
			array = Utility.transpose(array);
		}

		// <COMMENT> TO AVOID PRINTING THE MATRIX FOR WHICH THE ASSIGNMENT IS
		// CALCULATED
		// Utility.print(array);
		/*
		 * for (int i = 0; i < array.length; i++) { for (int j = 0; j <
		 * array[i].length; j++) { System.out.print(array[i][j] + "\t"); }
		 * System.out.println(); }
		 * System.out.println("-----------------------");
		 */
		// </COMMENT>*/

		double startTime = System.nanoTime();
		int rows = array.length;
		int col = array[0].length;

		double[][] cost = new double[col][col];
		for (int w = 0; w < col; w++) {
			if (w < rows) {
				cost[w] = Arrays.copyOf(array[w], col);
			} else {
				cost[w] = new double[col];
			}
		}
		
		for (int i = 0; i < cost.length; i++) {
			for (int j = 0; j < cost[i].length; j++) {
				System.out.print(cost[i][j] + "\t");
			}
			System.out.println();
		}

		double largest = 0;
		double smallest = 1000;
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				if (array[i][j] > largest) {
					largest = array[i][j];

				}
				if (array[i][j] < smallest) {
					smallest = array[i][j];
				}
			}
		}
		double maxWeight = largest;

		for (int i = 0; i < col; i++) // Generate cost by subtracting.
		{
			for (int j = 0; j < col; j++) {
				cost[i][j] = (maxWeight - cost[i][j]);
			}
		}
		double sum = 0;
		double LB = 50;
		double UB = 0;

		Hungarian HA = new Hungarian(cost);
		int[] r = HA.execute(cost);

		for (int i = 0; i < array.length; i++) {
			System.out.println((i + 1) + "->" + (r[i] + 1) + " : "
					+ array[i][r[i]]);
			sum += array[i][r[i]];
			if (array[i][r[i]] > UB) {
				UB = array[i][r[i]];
			}
			if (array[i][r[i]] < LB) {
				LB = array[i][r[i]];
			}
		}

		double endTime = System.nanoTime();

		System.out.println(' ');
		System.out.println("sum is: " + sum);
		System.out.println("The range of weight is [" + smallest + "->"
				+ largest + "]");
		System.out.println("The range of MATCHING WEIGHT is [" + LB + "->" + UB
				+ "]");
		System.out
				.println("------------------------------------------------------");
		Utility.printTime((endTime - startTime) / 1000000000.0);
		System.out.println("COMPLETED");

	}
}
