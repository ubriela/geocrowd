/**
 * 
 */
package test.geocrowd;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.geocrowd.setcover.SetCoverGreedy;
import org.junit.Test;

/**
 * @author ubriela
 *
 */
public class SetCoverTest {

	@Test
	public void test() {
		ArrayList<ArrayList> container = new ArrayList<>();
		
		container.add(new ArrayList<Integer>(Arrays.asList(1, 2, 3)));
		container.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
		container.add(new ArrayList<Integer>(Arrays.asList(3, 4)));
		container.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
		
		SetCoverGreedy scg = new SetCoverGreedy(container);
		int no_set = scg.minSetCover();
		System.out.println(no_set);
	}
}
