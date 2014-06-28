package test.geocrowd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.geocrowd.common.SpecializedTask;

public class Test {
	public static void main(String[] args) {
		//test3();//
                (new CrowdsensingTest()).test();
	}
	
	public void test1() {
		ArrayList<SpecializedTask> taskList = new ArrayList<SpecializedTask>();
		SpecializedTask t1 = new SpecializedTask();
		SpecializedTask t2 = new SpecializedTask();
		t2.setExpired();
		SpecializedTask t3 = new SpecializedTask();
		SpecializedTask t4 = new SpecializedTask();
		t4.setExpired();

		taskList.add(0,t1);
		taskList.add(1,t2);
		taskList.add(2,t3);
		taskList.add(3,t4);
		
		for (SpecializedTask t : taskList)
			System.out.println(t.toString());
		
		for (int i = taskList.size() - 1; i >= 0; i--) {
			// remove the solved task from task list
			if (taskList.get(i).isExpired()) {
				taskList.remove(i);
			}
		}
		
		for (SpecializedTask t : taskList)
			System.out.println(t.toString());
	}
	
	public static void test2() {
		int[][] M = new int[4][4];
		int[] m1 = {1,2,3,4};
		int[] m2 = {1,2,3,4};
		M[0] = m1;
		M[2] = Arrays.copyOf(m1, m1.length);
		M[0][0] = 5;
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[0].length; j++)
				System.out.print(M[i][j] + "\t");
			System.out.println();
		}
	}
	
	public static void test3() {
		HashSet<Integer> hs = new HashSet<Integer>();
		hs.add(1);
		hs.add(2);
		hs.add(3);
		hs.add(4);
		
		Integer i = 1;
		if (hs.contains(i))
			System.out.println(i);
		if (hs.contains(5))
			System.out.println(5);
	}
}