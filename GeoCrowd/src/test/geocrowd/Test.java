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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.geocrowd.common.Utils;
import org.geocrowd.common.crowdsource.SpecializedTask;

import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.ConcurrentRadixTree;
import com.googlecode.concurrenttrees.radix.RadixTree;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.SmartArrayBasedNodeFactory;
import com.googlecode.concurrenttrees.radix.node.util.PrettyPrintable;

// TODO: Auto-generated Javadoc
/**
 * The Class Test.
 */
public class Test {

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		RadixTree<Integer> tree = new ConcurrentRadixTree<Integer>(
				new SmartArrayBasedNodeFactory());

		tree.put("123", 1);
		tree.put("235", 2);
		tree.put("125", 3);
		tree.put("45", 4);

		System.out.println("Tree structure:");
		// PrettyPrintable is a non-public API for testing, prints
		// semi-graphical representations of trees...
		PrettyPrinter.prettyPrint((PrettyPrintable) tree, System.out);

		System.out.println();
		System.out.println("Value for 'TEST' (exact match): "
				+ tree.getValueForExactKey("TEST"));
		System.out.println();
		System.out.println("Keys starting with 'T': "
				+ Iterables.toString(tree.getKeysStartingWith("13")));
		System.out.println();
		System.out.println("Values for keys starting with 'TE': "
				+ Iterables.toString(tree.getValuesForKeysStartingWith("12")));
		System.out.println();

		// HashSet<Integer> h1 = new HashSet<Integer>();
		// h1.add(1);
		// h1.add(2);
		// h1.add(3);
		// h1.add(4);
		// h1.add(3);
		// HashSet<Integer> h2 = new HashSet<Integer>();
		// h2.add(1);
		// h2.add(2);
		// h2.add(3);
		// h2.add(4);
		//
		// System.out.println(h1.equals(h2));

		// ArrayList<Integer> w1 = new ArrayList<Integer>(h1);
		// ArrayList<Integer> w2 = new ArrayList<Integer>(h2);
		//
		// Collections.sort(w1);
		// Collections.sort(w2);
		//
		// int k = 10;
		// int max = Math.min(w1.size() - 1, w2.size() - 1);
		// for (int i = 0; i <= max; i++) {
		// if (w1.get(i) > w2.get(i))
		// k = -1;
		// else if (w1.get(i) < w2.get(i))
		// k = 1;
		// else if (i < max)
		// continue;
		// else if (i == w1.size() - 1 && i == w2.size() - 1)
		// k= 0;
		// else if (i == w1.size() - 1)
		// k= 1;
		// else if (i == w2.size() - 1)
		// k= -1;
		// }
		//
		// System.out.println(k);
	}

	/**
	 * Test2.
	 */
	public static void test2() {
		int[][] M = new int[4][4];
		int[] m1 = { 1, 2, 3, 4 };
		int[] m2 = { 1, 2, 3, 4 };
		M[0] = m1;
		M[2] = Arrays.copyOf(m1, m1.length);
		M[0][0] = 5;
		for (int i = 0; i < M.length; i++) {
			for (int j = 0; j < M[0].length; j++)
				System.out.print(M[i][j] + "\t");
			System.out.println();
		}
	}

	/**
	 * Test3.
	 */
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

	/**
	 * Test1.
	 */
	public void test1() {
		ArrayList<SpecializedTask> taskList = new ArrayList<SpecializedTask>();
		SpecializedTask t1 = new SpecializedTask();
		SpecializedTask t2 = new SpecializedTask();
		t2.setExpired();
		SpecializedTask t3 = new SpecializedTask();
		SpecializedTask t4 = new SpecializedTask();
		t4.setExpired();

		taskList.add(0, t1);
		taskList.add(1, t2);
		taskList.add(2, t3);
		taskList.add(3, t4);

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

	public static void testGetSubsets() {
		ArrayList<Integer> l = new ArrayList<Integer>();
		for (int i = 1; i <= 5; i++)
			l.add(i * 2);
		List<Set<Integer>> res = Utils.getSubsets(l, 3);
		for (Set<Integer> r : res) {
			System.out.print("(");
			for (Integer a : r) {
				System.out.print(a);
			}
			System.out.println(")");

		}

	}
}