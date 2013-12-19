/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.geocrowd.maxmatching.BPWMatching;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author dkh
 */
import static java.lang.Math.*;

public class Utility {

	public static int readInput(String prompt) // Reads input,returns double.
	{
		Scanner in = new Scanner(System.in);
		System.out.print(prompt);
		int input = in.nextInt();
		return input;
	}

	public static double[][] transpose // Transposes a double[][] array.
	(double[][] array) {
		double[][] transposedArray = new double[array[0].length][array.length];
		for (int i = 0; i < transposedArray.length; i++) {
			for (int j = 0; j < transposedArray[i].length; j++) {
				transposedArray[i][j] = array[j][i];
			}
		}
		return transposedArray;
	}

	public static void generateRandomArray // Generates random 2-D array.
	(double[][] array, String randomMethod) {
		Random generator;
		generator = new Random();
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				if (randomMethod.equals("random")) {
					array[i][j] = (int)generator.nextInt(5);

				} else if (randomMethod.equals("gaussian")) {
					array[i][j] = generator.nextGaussian() / 4; // range length
																// to 1.
					if (array[i][j] > 0.5) {
						array[i][j] = 0.5;
					} // eliminate outliers.
					if (array[i][j] < -0.5) {
						array[i][j] = -0.5;
					} // eliminate outliers.
					array[i][j] = array[i][j] + 0.5; // make elements positive.
				}

			}
		}
		System.out.println("generated");
	}

	public static void printTime(double time) // Formats time output.
	{
		String timeElapsed = "";
		int days = (int) floor(time) / (24 * 3600);
		int hours = (int) floor(time % (24 * 3600)) / (3600);
		int minutes = (int) floor((time % 3600) / 60);
		int seconds = (int) round(time % 60);

		if (days > 0) {
			timeElapsed = Integer.toString(days) + "d:";
		}
		if (hours > 0) {
			timeElapsed = timeElapsed + Integer.toString(hours) + "h:";
		}
		if (minutes > 0) {
			timeElapsed = timeElapsed + Integer.toString(minutes) + "m:";
		}

		timeElapsed = timeElapsed + Integer.toString(seconds) + "s";
		System.out.println("\nTotal time required: " + timeElapsed + "\n\n");
	}

	public static double[][] copyOf // Copies all elements of an array to a new
									// array.
	(double[][] original) {
		double[][] copy = new double[original.length][original[0].length];
		for (int i = 0; i < original.length; i++) {
			// Need to do it this way, otherwise it copies only memory location
			System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
		}

		return copy;
	}

	public static void writefile(String s) {

		File file = new File("C:\\result 2.txt");
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			PrintWriter printer = new PrintWriter(writer);
			// printer.append("\n\n\n"+s);
			printer.println(s);
			printer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeMatrix(String s) {

		File file = new File("C:\\Matrix.txt");
		FileWriter writer;
		try {
			writer = new FileWriter(file, true);
			PrintWriter printer = new PrintWriter(writer);
			// printer.append("\n\n\n"+s);
			printer.print(s);
			printer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void print(double[][] array) {
		// writeMatrix("\n(Printing out only 2 decimals)\n");
		// writeMatrix ("The matrix is");
		for (int i = 0; i < array.length; i++) {
			// System.out.print("{");
			for (int j = 0; j < array[i].length; j++) {
				writeMatrix("\t" + array[i][j]);

			}
			writeMatrix(" ");
		}
		writeMatrix(" ");
	}

	public static void scan(double[][] array) {
		java.io.File file = new java.io.File("C:\\Matrix.txt");
		try {
			Scanner input = new Scanner(file);
			for (int i = 0; i < array.length; i++) {
				// System.out.print("{");
				for (int j = 0; j < array[i].length; j++) {
					array[i][j] = input.nextDouble();

				}
				writeMatrix(" ");
			}
			System.out.println("Scan DONE");
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}