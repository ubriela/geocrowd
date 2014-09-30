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
import java.util.HashSet;

import org.geocrowd.setcover.SetCoverGreedy;
import org.geocrowd.setcover.SetCoverGreedy_HighTaskCoverage;
import org.junit.Test;

// TODO: Auto-generated Javadoc
/**
 * The Class SetCoverTest.
 * 
 * @author ubriela
 */
public class SetCoverTest {
	

	/**
	 * Test.
	 */
	@Test
	public void test() {
		ArrayList<ArrayList> container = new ArrayList<>();
		
		container.add(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));
		container.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
		container.add(new ArrayList<Integer>(Arrays.asList(3, 4)));
		container.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
		
		SetCoverGreedy scg = new SetCoverGreedy_HighTaskCoverage(container,1);
		HashSet<Integer> no_set = scg.minSetCover();
		System.out.println(no_set.size());
	}
}