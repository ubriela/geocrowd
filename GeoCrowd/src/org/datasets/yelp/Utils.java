/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
 package org.datasets.yelp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;



/**
 * 
 * @author dkh
 */
public class Utils {
	public static void writefile(String s, String filename) {

		File file = new File(filename);
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

	public static void writefile2(String s, String filename) {
		try {
			FileWriter writer = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(writer);
			out.write(s);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
