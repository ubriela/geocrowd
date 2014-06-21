/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.geocrowd;

import java.util.ArrayList;
import java.util.Arrays;
import org.geocrowd.setcover.SetCoverGreedy;
import org.geocrowd.setcover.SetCoverGreedySmallestAssociateSet;

/**
 *
 * @author Luan
 */
public class SetCoverGreedySmallestAssociateSetTest {
    @org.junit.Test
	public void test() {
		ArrayList<ArrayList> container = new ArrayList<>();
		
		container.add(new ArrayList<Integer>(Arrays.asList(1, 2)));
		container.add(new ArrayList<Integer>(Arrays.asList(2, 4)));
		container.add(new ArrayList<Integer>(Arrays.asList(3, 4)));
		container.add(new ArrayList<Integer>(Arrays.asList(4, 5)));
		container.add(new ArrayList<Integer>(Arrays.asList(3, 5)));
		
		SetCoverGreedySmallestAssociateSet scg = new SetCoverGreedySmallestAssociateSet(container);
		int no_set = scg.minSetCover();
		System.out.println(no_set);
	}
}