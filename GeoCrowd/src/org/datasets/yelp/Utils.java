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
 package org.datasets.yelp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;



// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 * 
 * @author dkh
 */
public class Utils {
	
	/**
	 * Writefile.
	 * 
	 * @param s
	 *            the s
	 * @param filename
	 *            the filename
	 */
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

	/**
	 * Writefile2.
	 * 
	 * @param s
	 *            the s
	 * @param filename
	 *            the filename
	 */
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
