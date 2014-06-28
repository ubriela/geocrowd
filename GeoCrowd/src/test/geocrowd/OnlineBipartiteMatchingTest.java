package test.geocrowd;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.geocrowd.matching.online.OnlineBipartiteMatching;
import org.geocrowd.setcover.SetCoverGreedy;
import org.junit.Test;

public class OnlineBipartiteMatchingTest {

	@Test
	public void test() {
		ArrayList<Integer> workers = new ArrayList<>();
		
		workers.add(new Integer(10));
		workers.add(new Integer(11));
		workers.add(new Integer(12));
		workers.add(new Integer(13));
		
		OnlineBipartiteMatching obm = new OnlineBipartiteMatching(workers);
		
		ArrayList<ArrayList> container = new ArrayList<>();
		container.add(new ArrayList<Integer>(Arrays.asList(10)));
		container.add(new ArrayList<Integer>(Arrays.asList(10)));
		container.add(new ArrayList<Integer>(Arrays.asList(11, 12)));
		
		int assignedTasks = obm.onlineMatching(container);
		System.out.println(assignedTasks);
		
		container = new ArrayList<>();
		container.add(new ArrayList<Integer>(Arrays.asList(11,12)));
		container.add(new ArrayList<Integer>(Arrays.asList(11,13)));
		container.add(new ArrayList<Integer>(Arrays.asList(10)));
		
		assignedTasks = obm.onlineMatching(container);
		System.out.println(assignedTasks);
	}
}