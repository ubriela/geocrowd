package test.geocrowd;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.geocrowd.matching.online.OnlineBipartiteMatching;
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