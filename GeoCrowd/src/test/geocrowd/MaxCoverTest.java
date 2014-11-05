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
package test.geocrowd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import maxcover.MaxCoverAdapt;
import maxcover.MaxCoverBasic;

import org.geocrowd.setcover.SetCoverGreedy;
import org.geocrowd.setcover.SetCoverGreedy_HighTaskCoverage;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverTest.
 * 
 * @author ubriela
 */
public class MaxCoverTest {
	
	
	public ArrayList<HashMap<Integer, Integer>> getContainer() {
		ArrayList<HashMap<Integer, Integer>> container = new ArrayList<>();
		
		HashMap<Integer, Integer> s1 = new HashMap<Integer, Integer>();
		s1.put(1, 0);
		s1.put(2, 0);
		s1.put(3, 0);
		s1.put(8, 0);
		s1.put(9, 0);
		s1.put(10, 0);
		container.add(s1);
		
		HashMap<Integer, Integer> s2 = new HashMap<Integer, Integer>();
		s2.put(1, 0);
		s2.put(2, 0);
		s2.put(3, 0);
		s2.put(4, 0);
		s2.put(5, 0);
		container.add(s2);
		
		
		HashMap<Integer, Integer> s3 = new HashMap<Integer, Integer>();
		s3.put(4, 0);
		s3.put(5, 0);
		s3.put(7, 0);
		container.add(s3);
		
		HashMap<Integer, Integer> s4 = new HashMap<Integer, Integer>();
		s4.put(5, 0);
		s4.put(6, 0);
		s4.put(7, 0);
		container.add(s4);
		
		HashMap<Integer, Integer> s5 = new HashMap<Integer, Integer>();
		s5.put(6, 0);
		s5.put(7, 0);
		s5.put(8, 0);
		s5.put(9, 0);
		s5.put(10, 0);
		container.add(s5);
		return container;
	}
	
	@Test
	public void testMaxCoverBasic() {
		ArrayList<HashMap<Integer, Integer>> container = getContainer();
		MaxCoverBasic mc = new MaxCoverBasic(container, 0);
		mc.k = 2;
		
		System.out.println(mc.universe);
		System.out.println(mc.listOfSets);
		
		HashSet<Integer> no_set = mc.maxCover();
		System.out.println(no_set);
	}
	
//	@Test
	public void testMaxCoverAdapt() {
		ArrayList<HashMap<Integer, Integer>> container = getContainer();
		MaxCoverAdapt mc = new MaxCoverAdapt(container, 0);
		mc.lambda = 1;
		
		System.out.println(mc.universe);
		System.out.println(mc.listOfSets);
		
		HashSet<Integer> no_set = mc.maxCover();
		System.out.println(no_set);
	}
}