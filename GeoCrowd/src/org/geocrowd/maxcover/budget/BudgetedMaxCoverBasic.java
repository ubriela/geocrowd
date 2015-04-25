package org.geocrowd.maxcover.budget;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;

public class BudgetedMaxCoverBasic extends BudgetedMaxCover {

	@Override
	public double maxCover() {
		// enumerate all possible family of set of size < k and totalCost < budget
		double maxH1 = 0.0;
		Set<Integer> workerH1 = new HashSet<Integer>();
		Integer[] workerids = (Integer[]) workerCost.keySet().toArray();
		for (int k = 1; k < 3; k++) {
			Integer[][] c = Combination.combinations(k, workerids);
			for (int i = 0; i < c.length; i++) {
				double totalWeight = 0.0;
				for (int j = 0; j < c[i].length; j++)
					totalWeight += workerCost.get(c[i][j]);
				
				// budget constraint
				if (totalWeight <= budget) {
					double totalProfit = 0.0;
					Set<Integer> coveredTaskids = new HashSet<Integer>();
					for (int j = 0; j < c[i].length; j++)
						coveredTaskids.addAll(wtCostProfit.get(c[i][j]).keySet());

					for (Integer tid : coveredTaskids)
						totalProfit += taskProfit.get(tid);
					// update max profit
					if (totalProfit > maxH1) {
						maxH1 = totalProfit;
						workerH1 = new HashSet<Integer>();
						for (int j = 0; j < c[i].length; j++)
							workerH1.add(c[i][j]);
					}
				}
			}
		}
		
		// enumerate all possible family of set of size = k and totalCost <= budget
		double maxH2 = 0.0;
		Set<Integer> maxG = null;
		Set<Integer> maxG_task = null;
		Integer[][] c = Combination.combinations(3, workerids);
		
		for (int i = 0; i < c.length; i++) {
			double weightG = 0.0;
			Set<Integer> G = new HashSet<Integer>();
			for (int j = 0; j < c[i].length; j++) {
				weightG += workerCost.get(c[i][j]);
				G.add(c[i][j]);
			}
			
			
			// budget constraint
			if (weightG > budget)
				continue;
			
//			Set<Integer> coveredTaskids = new HashSet<Integer>();
//			for (int j = 0; j < c[i].length; j++)
//				coveredTaskids.addAll(wtCostProfit.get(c[i][j]).keySet());
			
			Set<Integer> U = new HashSet<Integer>(workerCost.keySet());
			for (Integer g : G)
				U.remove(g);
			
			Set<Integer> G_task = new HashSet<Integer>();
			for (Integer wid : G)
				G_task.addAll(wtCostProfit.get(wid).keySet());
			
			while (!U.isEmpty()) {
				
				// Find the worker that maximize density (i.e, uncovered weight/cost)
				Iterator it = (Iterator) U.iterator();
				double maxDensity = 0.0;
				int bestWID = 0;
				while (it.hasNext()) {
					int wid = it.next();
					double cost = workerCost.get(wid);
					double W_prime = 0.0;
					for (Integer tid : wtCostProfit.get(wid).keySet()) {
						if (!G_task.contains(tid)) {
							W_prime += taskProfit.get(tid);
						}
					}
					double density = W_prime/cost;
					if (density > maxDensity) {
						maxDensity = density;
						bestWID = wid;
					}
				}
				
				if (weightG + workerCost.get(bestWID) <= budget) {
					G.add(bestWID);
					G_task.addAll(wtCostProfit.get(bestWID).keySet());
				}
				
				U.remove(bestWID);
			}
			
			// compute weight of G
			double G_profit = 0.0;
			for (Integer tid : G_task)
				G_profit += taskProfit.get(tid);
			
			if (G_profit > maxH2) {
				maxH2 = G_profit;
				maxG = new HashSet<Integer>(G);
				maxG_task = new HashSet<Integer>(G_task);
			}
		}
		
		// compare H1 & H2
		if (maxH1 > maxH2) {
			maxProfit = maxH1;
			SelectedWorkers = (HashSet<Integer>) workerH1;
		} else {
			maxProfit = maxH2;
			SelectedWorkers = (HashSet<Integer>) maxG;
		}
		
		return maxProfit;
	}

}
