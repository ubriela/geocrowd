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
package maxcover;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.geocrowd.common.Constants;
import org.geocrowd.common.WeightedSolution;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.util.Vector;
import org.moeaframework.util.io.CommentedLineReader;

public class MaxCoverBasicMO extends MaxCover  implements Problem {

	private ArrayList<Integer> indices = null;
	
	int[] workerCounts = null;

	public MaxCoverBasicMO() {
		super();
	}
	
	public MaxCoverBasicMO(ArrayList container,
			Integer currentTI, int[] selected, int budget) {
		super(container, currentTI);
		
		indices = new ArrayList<Integer>(mapSets.keySet());
		this.workerCounts = selected;
		this.budget = budget;
	}


	@Override
	public void evaluate(Solution solution) {
		boolean[] d = EncodingUtils.getBinary(solution.getVariable(0));
		double [] f = new double[2];
		double[] g = new double[1];
		int usedBudget = 0;
		int max = 0;
		HashSet<Integer> coverage = new HashSet<Integer>();
		
		for (int i = 0; i < mapSets.size(); i++) {
			if (d[i]) {
				usedBudget++;
				HashMap<Integer, Integer> map = (HashMap<Integer, Integer>) mapSets.get(indices.get(i));
				coverage.addAll(map.keySet());
				
				if (workerCounts[indices.get(i)] + 1 > max)
					max = workerCounts[indices.get(i)] + 1;
			}
		}
		
		f[0] = coverage.size();
		f[1] = max;
		
		if (usedBudget <= budget)
			g[0] = 0.0;
		else {
			g[0] = usedBudget - budget;
			//System.out.println(g);
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
		//do nothing
	}
	

	/**
	 * 
	 * @param container
	 * @param currentTI
	 * @param workerCounts
	 * @param budget
	 * @return
	 */
	public HashSet<Integer> maxCover(ArrayList container,
			Integer currentTI, int[] workerCounts, int budget) {
		// solve using NSGA-II
		NondominatedPopulation result = new Executor()
				.withProblemClass(MaxCoverBasicMO.class, container, currentTI, workerCounts, budget)
				.withAlgorithm("NSGAII")
				.withMaxEvaluations(50000)
				.distributeOnAllCores()
				.run();
		
		int bestIndex = bestSolution(result);
		
		// pick the first solution
		Solution solution = result.get(bestIndex);
		String val = solution.getVariable(0).toString();
		for (int i = 0; i < val.length(); i++) {
			if (val.charAt(i) == '1')
				assignWorkers.add(i);
		}
		
		// update assignedTasks
		assignedTaskSet = new HashSet<Integer>();
		for (Integer i : assignWorkers) {
			assignedTaskSet.addAll(((HashMap<Integer, Integer>) container.get(i)).keySet());
		}
		assignedTasks = assignedTaskSet.size();
		
		System.out.println("N/A\t" + assignedTasks  + "\t" + assignWorkers.size() + "\t"  + assignedTasks/assignWorkers.size() + "\t" + result.size());
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
			
//			System.out.println(coverage + "\t" +  maxAssign);
			// the smaller weight, the better
			double weight = Constants.alpha * (coverage/Constants.TaskNo) - (1-Constants.alpha) * maxAssign/currentTimeInstance;
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
