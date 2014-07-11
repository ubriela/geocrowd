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

import org.geocrowd.matching.online.OnlineBipartiteMatching;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class OnlineBipartiteMatchingTest.
 */
public class OnlineBipartiteMatchingTest {

	/**
	 * Test.
	 */
	@Test
	public void test() {
		ArrayList<Integer> workers = new ArrayList<>();
		
		workers.add(new Integer(10));
		workers.add(new Integer(11));
		workers.add(new Integer(12));
		workers.add(new Integer(13));
		
		OnlineBipartiteMatching obm = new OnlineBipartiteMatching(workers);
		
		HashMap<Integer, ArrayList> container = new HashMap<>();
		container.put(0, new ArrayList<Integer>(Arrays.asList(10)));
		container.put(1, new ArrayList<Integer>(Arrays.asList(10)));
		container.put(2, new ArrayList<Integer>(Arrays.asList(11, 12)));
		
		int assignedTasks = obm.onlineMatching(container).size();
		System.out.println(assignedTasks);
		
		container = new HashMap<>();
		container.put(0, new ArrayList<Integer>(Arrays.asList(11,12)));
		container.put(1, new ArrayList<Integer>(Arrays.asList(11,13)));
		container.put(2, new ArrayList<Integer>(Arrays.asList(10)));
		
		assignedTasks = obm.onlineMatching(container).size();
		System.out.println(assignedTasks);
	}
}