package org.geocrowd.maxcover.budget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Find selection of workers with maximum profit given limited budget (each worker is associated with a weight)
 * @author ubriela
 *
 */
public abstract class BudgetedMaxCover {

	/**
	 * <workerid, <taskid, {cost,profit}>>
	 *
	 */
	public HashMap<Integer, HashMap<Integer, CostProfit>> wtCostProfit = null;
	
	
	/**
	 * <workerid, cost>
	 */
	public HashMap<Integer, Double> workerCost = null;
	
	/**
	 * <taskid, profit>
	 */
	public HashMap<Integer, Double> taskProfit = null;
	
	public double budget = 0.0;

	/**
	 * All the task index in the candidate tasks (not the task list).
	 */
	public HashSet<Integer> Universe = null;
	public HashSet<Integer> CoveredTasks = new HashSet<Integer>();;

	/**
	 * The maximum profit
	 */
	public double maxProfit = 0;

	/**
	 * The selected workers
	 */
	public HashSet<Integer> SelectedWorkers = new HashSet<>();
	
	/**
	 * The current time instance.
	 */
	int timeTnstance = 0;
	
	public BudgetedMaxCover() {
		
	}
	
	public BudgetedMaxCover(HashMap<Integer, HashMap<Integer, CostProfit>> cost_profit, HashMap<Integer, Double> cost, HashMap<Integer, Double> profit, int currentTI) {
		wtCostProfit = cost_profit;
		workerCost = cost;
		taskProfit = profit;
		timeTnstance = currentTI;
	}

	/**
	 * Max set cover.
	 *
	 * @return the total profit
	 */
	public abstract double maxCover();
}