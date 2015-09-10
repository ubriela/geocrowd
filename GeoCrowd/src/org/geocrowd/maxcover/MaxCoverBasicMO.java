/* Copyright 2009-2014 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geocrowd.maxcover;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.geocrowd.Constants;
import org.geocrowd.datasets.params.GeocrowdConstants;
import org.geocrowd.dtype.CoverageIndex;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

public class MaxCoverBasicMO extends MaxCover implements Problem {

	// private ArrayList<Integer> indices = null;

	int[] workerCounts = null;

	public MaxCoverBasicMO() {
		super();
	}

	public MaxCoverBasicMO(ArrayList container, Integer currentTI,
			int[] selected, int budget) {
		super(container, currentTI);

		// indices = new ArrayList<Integer>(mapSets.keySet());
		this.workerCounts = selected;
		this.budget = budget;
	}

	@Override
	public void evaluate(Solution solution) {
		boolean[] d = EncodingUtils.getBinary(solution.getVariable(0));
		double[] f = new double[2];
		double[] g = new double[1];
		int usedBudget = 0;
		int max = 0;
		HashSet<Integer> coverage = new HashSet<Integer>();

		for (int i = 0; i < mapSets.size(); i++) {
			if (d[i]) {
				usedBudget++;
				HashMap<Integer, Integer> map = mapSets.get(i);
				coverage.addAll(map.keySet());

				if (workerCounts[i] + 1 > max)
					max = workerCounts[i] + 1;
			}
		}

		f[0] = coverage.size();
		f[1] = max;

		if (usedBudget <= budget)
			g[0] = 0.0;
		else {
			g[0] = usedBudget - budget;
			// System.out.println(g);
		}

		// negate this objectives since maxcover is maximization
		solution.setObjective(0, -f[0]);
		solution.setObjective(1, f[1]);
		solution.setConstraints(g);
	}

	@Override
	public String getName() {
		return "MaxCoverBasicMO";
	}

	@Override
	public int getNumberOfConstraints() {
		return 1;
	}

	@Override
	public int getNumberOfObjectives() {
		return 2;
	}

	@Override
	public int getNumberOfVariables() {
		return mapSets.size();
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2, 1);
		solution.setVariable(0, EncodingUtils.newBinary(mapSets.size()));
		return solution;
	}

	@Override
	public void close() {
		// do nothing
	}

	/**
	 * 
	 * @param container
	 * @param currentTI
	 * @param workerCounts
	 * @param budget
	 * @return
	 */
	public HashSet<Integer> maxCover(ArrayList container, Integer currentTI,
			int[] workerCounts, int budget) {
		// solve using NSGA-II
		NondominatedPopulation result = new Executor()
				.withProblemClass(MaxCoverBasicMO.class, container, currentTI,
						workerCounts, budget).withAlgorithm("NSGAII")
				.withMaxEvaluations(200000).distributeOnAllCores().run();

		int bestIndex = bestSolution(result);

		// pick the first solution
		Solution solution = result.get(bestIndex);
		String val = solution.getVariable(0).toString();
		for (int i = 0; i < val.length(); i++) {
			if (val.charAt(i) == '1')
				assignWorkers.add(i);
		}

		// update assignedTasks
//		assignedTaskSet = new HashSet<Integer>();
		for (Integer i : assignWorkers) {
			assignedTaskSet.addAll(((HashMap<Integer, Integer>) container
					.get(i)).keySet());
		}

		/**
		 * If do not use all budget --> use the rest to select the worker with
		 * less than MAX selected and maximize the total coverage
		 */
		int budgetLeft = budget - assignWorkers.size();
		if (budgetLeft > 0) {
			
			int maxCount = 0;
			int[] updatedWorkerCounts = new int[workerCounts.length];
			HashSet<Integer> unselected = new HashSet<Integer>();
			for (int i = 0; i < workerCounts.length; i++) {
				// if the worker is not selected
				updatedWorkerCounts[i] = workerCounts[i];
				if (assignWorkers.contains(i)) {
					updatedWorkerCounts[i] = updatedWorkerCounts[i] + 1;
					if (updatedWorkerCounts[i] > maxCount)
						maxCount = updatedWorkerCounts[i];
				} else		
					unselected.add(i);
			}

			PriorityQueue<CoverageIndex> pq = new PriorityQueue<CoverageIndex>();
			for (int i : unselected) {
				/**
				 * do not select the worker with maximum selection
				 */
				if (updatedWorkerCounts[i] > maxCount)
					continue;
				HashSet<Integer> _assignedTasks = (HashSet<Integer>) assignedTaskSet
						.clone();
				_assignedTasks.addAll(((HashMap<Integer, Integer>) container
						.get(i)).keySet());
				int cover = _assignedTasks.size();
				CoverageIndex ci = new CoverageIndex(cover, i);

				pq.add(ci);
				if (pq.size() > budgetLeft)
					pq.poll();
			}

			for (CoverageIndex ci : pq) {
				assignedTaskSet.addAll(((HashMap<Integer, Integer>) container
						.get(ci.getIndex())).keySet());
				assignWorkers.add(ci.getIndex());
			}
		}
		
		assignedTasks = assignedTaskSet.size();

		System.out.println("N/A\t" + assignedTasks + "\t"
				+ assignWorkers.size() + "\t" + assignedTasks
				/ Math.max(1, assignWorkers.size()) + "\t" + result.size());
		return assignWorkers;
	}

	private int bestSolution(NondominatedPopulation result) {
		int bestSol = 0;
		double largestWeight = Double.MIN_VALUE;
		for (int i = 0; i < result.size(); i++) {
			Solution sol = result.get(i);
			double[] objectives = sol.getObjectives();

			double coverage = -objectives[0];
			double maxAssign = objectives[1];

			// System.out.println(coverage + "\t" + maxAssign);
			// the smaller weight, the better
//			System.out.println("here");
			double weight = Constants.alpha
					* ((coverage + 0.0) / GeocrowdConstants.TASK_NUMBER)
					- (1 - Constants.alpha) * (maxAssign + 0.0)
					/ currentTimeInstance;
			if (weight > largestWeight) {
				largestWeight = weight;
				bestSol = i;
			}
		}

		return bestSol;
	}

	@Override
	public HashSet<Integer> maxCover() {
		// TODO Auto-generated method stub
		return null;
	}
}