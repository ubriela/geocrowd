package org.geocrowd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import static org.geocrowd.AlgorithmEnum.MAX_COVER_BASIC;
import static org.geocrowd.Geocrowd.algorithm;
import static org.geocrowd.Geocrowd.taskList;

import org.geocrowd.common.crowd.GenericTask;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdConstants;
import org.geocrowd.datasets.synthetic.Parser;
import org.geocrowd.maxcover.MaxCover;
import org.geocrowd.maxcover.MaxCoverAdapt;
import org.geocrowd.maxcover.MaxCoverAdaptS;
import org.geocrowd.maxcover.MaxCoverAdaptT;
import org.geocrowd.maxcover.MaxCoverBasic;
import org.geocrowd.maxcover.MaxCoverBasicMO;
import org.geocrowd.maxcover.MaxCoverBasicS;
import org.geocrowd.maxcover.MaxCoverBasicS2;
import org.geocrowd.maxcover.MaxCoverBasicSMO;
import org.geocrowd.maxcover.MaxCoverBasicT;
import org.geocrowd.maxcover.MaxCoverBasicT2;
import org.geocrowd.maxcover.MaxCoverST;

public class OnlineMTC extends GeocrowdSensing {

	public int totalBudget = 0;
	public HashMap<String, Integer> workerCounts = new HashMap<String, Integer>();

	public final int totalNumberTasks;

	public int numberArrivalTask = 0;
	public int totalNumberArrivalTask = 0;

	public double avgLamda;
	public int beta = 2;
	public int usedBudget;
	public double epsGain = 0.5;
	public double epsBudget = 0.2;
	public int[] preBudgets;

	public OnlineMTC() throws IOException {
		if (AlgorithmEnum.BASIC != Geocrowd.algorithm) {
			this.totalNumberTasks = computeTotalTasks();
		} else {
			this.totalNumberTasks = -1;
		}
	}


	/**
	 * Read tasks from file.
	 * 
	 * @param fileName
	 *            the file name
	 */
	@Override
	public void readTasks(String fileName) {
		this.numberArrivalTask = Parser.parseSensingTasks(fileName, taskList);
		totalNumberArrivalTask += numberArrivalTask;
		TaskCount += this.numberArrivalTask;
	}

	public HashSet<Integer> maxCoverage() {
		MaxCover maxCover = null;
		HashSet<Integer> assignedWorker = new HashSet<Integer>();
		switch (algorithm) {

		case MAX_COVER_BASIC:
		case MAX_COVER_BASIC_WORKLOAD:
		case MAX_COVER_PRO_B:
			MaxCoverBasic maxCoverBsic = new MaxCoverBasic(
					getContainerWithDeadline(), TimeInstance);
			maxCoverBsic.budget = getBudget(algorithm);
//			System.out.println("xxxx" + maxCoverPro.budget); 
			assignedWorker = maxCoverBsic.maxCover();

			TotalAssignedTasks += maxCoverBsic.assignedTasks;
			TotalCoveredUtility += maxCoverBsic.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverBsic;
//			System.out.print("\t" + maxCoverPro.gain);
			break;
			
		case MAX_COVER_BASIC_MO:
		case MAX_COVER_BASIC_W_MO:
			MaxCoverBasicMO mcBasicMo = new MaxCoverBasicMO();
			int budget = getBudget(algorithm);
			int[] _workerCounts = new int[workerList.size()];
			int i = 0;
			for (GenericWorker w : workerList) {
				if (workerCounts.containsKey(w.getId()))
					_workerCounts[i++] = workerCounts.get(w.getId());
				else
					_workerCounts[i++] = 0;
			}
			assignedWorker = mcBasicMo.maxCover(getContainerWithDeadline(), TimeInstance, _workerCounts, budget);

			TotalAssignedTasks += mcBasicMo.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = mcBasicMo;
			break;
			
		case MAX_COVER_BASIC_S_MO:
			MaxCoverBasicSMO mcBasicSMO = new MaxCoverBasicSMO();
			int budgetSMO = getBudget(algorithm);
			assignedWorker = mcBasicSMO.maxCover(getContainerWithDeadline(), TimeInstance, budgetSMO);

			TotalAssignedTasks += mcBasicSMO.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = mcBasicSMO;
			break;
			

		case MAX_COVER_ADAPT_B:
			MaxCoverAdapt maxCoverAdaptB = new MaxCoverAdapt(
					getContainerWithDeadline(), TimeInstance);
			maxCoverAdaptB.epsGain = epsGain;
			maxCoverAdaptB.epsBudget = epsBudget;
			if (TimeInstance == GeocrowdConstants.TIME_INSTANCE - 1) {
				maxCoverAdaptB.deltaBudget = totalBudget - usedBudget;
			} else {
				int preAggBudget = 0;
				for (int ti = 0; ti < TimeInstance; ti++)
					preAggBudget += totalBudget / GeocrowdConstants.TIME_INSTANCE;;
				maxCoverAdaptB.deltaBudget = preAggBudget - usedBudget;
//				System.out.println("xxx" + maxCoverAdaptB.deltaBudget);
			}
			maxCoverAdaptB.budget = totalBudget - usedBudget;
			
			maxCoverAdaptB.lambda = avgLamda;
			assignedWorker = maxCoverAdaptB.maxCover();

			TotalAssignedTasks += maxCoverAdaptB.assignedTasks;
			TotalCoveredUtility += maxCoverAdaptB.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();
			
			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptB.gain + 0.0)/(TimeInstance + 1);
//			avgLamda = maxCoverAdapt.gain;
			//System.out.print("\t" + maxCoverAdaptB.deltaBudget + "\t" + avgLamda);

			maxCover = maxCoverAdaptB;
			
			break;	
		case MAX_COVER_ADAPT_B_W:
			
			MaxCoverAdapt maxCoverAdapt = new MaxCoverAdapt(
					getContainerWithDeadline(), TimeInstance);
			maxCoverAdapt.epsGain = epsGain;
			maxCoverAdapt.epsBudget = epsBudget;
			if (TimeInstance == GeocrowdConstants.TIME_INSTANCE - 1) {
				maxCoverAdapt.deltaBudget = totalBudget - usedBudget;
			} else {
				int preAggBudget = 0;
				for (int ti = 0; ti < TimeInstance; ti++)
					preAggBudget += preBudgets[ti];
				maxCoverAdapt.deltaBudget = preAggBudget - usedBudget;
			}
			maxCoverAdapt.budget = totalBudget - usedBudget;
			
			maxCoverAdapt.lambda = avgLamda;
			assignedWorker = maxCoverAdapt.maxCover();

			TotalAssignedTasks += maxCoverAdapt.assignedTasks;
			TotalCoveredUtility += maxCoverAdapt.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();
			
			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdapt.gain + 0.0)/(TimeInstance + 1);
//			avgLamda = maxCoverAdapt.gain;
			//System.out.print("\t" + maxCoverAdapt.deltaBudget + "\t" + avgLamda);

			maxCover = maxCoverAdapt;

			break;	
		case MAX_COVER_ADAPT_T:
			MaxCoverAdaptT maxCoverAdaptT = new MaxCoverAdaptT(
					getContainerWithDeadline(), TimeInstance);
			maxCoverAdaptT.epsGain = epsGain;
			maxCoverAdaptT.epsBudget = epsBudget;
			if (TimeInstance == GeocrowdConstants.TIME_INSTANCE - 1) {
				maxCoverAdaptT.deltaBudget = totalBudget - usedBudget;
			} else {
				int preAggBudget = 0;
				for (int ti = 0; ti < TimeInstance; ti++)
					preAggBudget += totalBudget / GeocrowdConstants.TIME_INSTANCE;;
					maxCoverAdaptT.deltaBudget = preAggBudget - usedBudget;
			}
			maxCoverAdaptT.budget = totalBudget - usedBudget;
			
			maxCoverAdaptT.lambda = avgLamda;
			assignedWorker = maxCoverAdaptT.maxCover();

			TotalAssignedTasks += maxCoverAdaptT.assignedTasks;
			TotalCoveredUtility += maxCoverAdaptT.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();
			
			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptT.gain + 0.0)/(TimeInstance + 1);
			//System.out.print("\t" + maxCoverAdaptT.deltaBudget + "\t" + avgLamda);

			maxCover = maxCoverAdaptT;
			break;
		case MAX_COVER_ADAPT_T_W:
			MaxCoverAdaptT maxCoverAdaptTW = new MaxCoverAdaptT(
					getContainerWithDeadline(), TimeInstance);
			maxCoverAdaptTW.epsGain = epsGain;
			maxCoverAdaptTW.epsBudget = epsBudget;
			if (TimeInstance == GeocrowdConstants.TIME_INSTANCE - 1) {
				maxCoverAdaptTW.deltaBudget = totalBudget - usedBudget;
			} else {
				int preAggBudget = 0;
				for (int ti = 0; ti < TimeInstance; ti++)
					preAggBudget += preBudgets[ti];
				maxCoverAdaptTW.deltaBudget = preAggBudget - usedBudget;
			}
			maxCoverAdaptTW.budget = totalBudget - usedBudget;
			
			maxCoverAdaptTW.lambda = avgLamda;
			assignedWorker = maxCoverAdaptTW.maxCover();

			TotalAssignedTasks += maxCoverAdaptTW.assignedTasks;
			TotalCoveredUtility += maxCoverAdaptTW.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();
			
			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptTW.gain + 0.0)/(TimeInstance + 1);
			//System.out.print("\t" + maxCoverAdaptTW.deltaBudget + "\t" + avgLamda);

			maxCover = maxCoverAdaptTW;
			break;
		case MAX_COVER_BASIC_T:
		case MAX_COVER_BASIC_WORKLOAD_T:
		case MAX_COVER_PRO_T:
			MaxCoverBasicT maxCoverBasicT = new MaxCoverBasicT(
					getContainerWithDeadline(), TimeInstance);
			maxCoverBasicT.budget = getBudget(algorithm);
			assignedWorker = maxCoverBasicT.maxCover();

			TotalAssignedTasks += maxCoverBasicT.assignedTasks;
			TotalCoveredUtility += maxCoverBasicT.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverBasicT;
//			}
			break;
		case MAX_COVER_BASIC_T2:
			MaxCoverBasicT2 maxCoverBasicT2 = new MaxCoverBasicT2(
					getContainerWithDeadline(), TimeInstance);
			maxCoverBasicT2.budget = getBudget(algorithm);
			assignedWorker = maxCoverBasicT2.maxCover();

			TotalAssignedTasks += maxCoverBasicT2.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverBasicT2;
//			}
			break;

		case MAX_COVER_BASIC_S:
		case MAX_COVER_PRO_S:
			MaxCoverBasicS maxCoverS = new MaxCoverBasicS(getContainerWithDeadline(),
					TimeInstance);
			maxCoverS.budget = getBudget(algorithm);
			maxCoverS.setTaskList(taskList);
			/**
			 * compute entropy for workers
			 */
			createGrid();
			readEntropy();
			HashMap<Integer, Double> worker_entropies = new HashMap<Integer, Double>();
			
			for (int idx = 0; idx < containerWorker.size(); idx++)
				worker_entropies.put(idx, computeCost(workerList.get(idx)));
			
			HashMap<Integer, Double> task_entropies = new HashMap<Integer, Double>();
			for (int idx = 0; idx < taskList.size(); idx++)
				task_entropies.put(idx, computeCost(taskList.get(idx)));

			maxCoverS.setWorkerEntropies(worker_entropies);
			maxCoverS.setTaskEntropies(task_entropies);
			maxCoverS.maxEntropy = maxEntropy;
			maxCoverS.meanEntropy = meanEntropy;
			maxCoverS.totalEntropy = totalEntropy;
			assignedWorker = maxCoverS.maxCover();
			
			TotalAssignedTasks += maxCoverS.assignedTasks;
			TotalCoveredUtility += maxCoverS.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();

			maxCover = maxCoverS;
			break;
		case MAX_COVER_BASIC_S2:
			MaxCoverBasicS2 maxCoverS2 = new MaxCoverBasicS2(getContainerWithDeadline(),
					TimeInstance);
			maxCoverS2.budget = getBudget(algorithm);
			maxCoverS2.setTaskList(taskList);
			/**
			 * compute entropy for workers
			 */
//			printBoundaries();
			createGrid();
			readEntropy();
			HashMap<Integer, Double> worker_entropies2 = new HashMap<Integer, Double>();
			
			for (int idx = 0; idx < containerWorker.size(); idx++)
				worker_entropies2.put(idx, computeCost(workerList.get(idx)));
			
			HashMap<Integer, Double> task_entropies2 = new HashMap<Integer, Double>();
			for (int idx = 0; idx < taskList.size(); idx++)
				task_entropies2.put(idx, computeCost(taskList.get(idx)));

			maxCoverS2.setWorkerEntropies(worker_entropies2);
			maxCoverS2.setTaskEntropies(task_entropies2);
			maxCoverS2.maxRegionEntropy = maxEntropy;
			assignedWorker = maxCoverS2.maxCover();
			
			TotalAssignedTasks += maxCoverS2.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();

			maxCover = maxCoverS2;
			break;
		case MAX_COVER_BASIC_ST:
			MaxCoverST maxCoverST = new MaxCoverST(getContainerWithDeadline(),
					TimeInstance);
			maxCoverST.budget = getBudget(algorithm);
			maxCoverST.setTaskList(taskList);
			/**
			 * compute entropy for workers
			 */
			createGrid();
			readEntropy();
			HashMap<Integer, Double> worker_entropies4 = new HashMap<Integer, Double>();
			
			for (int idx = 0; idx < containerWorker.size(); idx++)
				worker_entropies4.put(idx, computeCost(workerList.get(idx)));

			maxCoverST.setWorkerEntropies(worker_entropies4);
			assignedWorker = maxCoverST.maxCover();
			
			TotalAssignedTasks += maxCoverST.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();

			maxCover = maxCoverST;
			break;
		case MAX_COVER_ADAPT_S:
		case MAX_COVER_ADAPT_S_W:
			MaxCoverAdaptS maxCoverAdaptS = new MaxCoverAdaptS(
					getContainerWithDeadline(), TimeInstance);
			maxCoverAdaptS.eps = epsGain;
			maxCoverAdaptS.eps = epsBudget;
			if (TimeInstance == GeocrowdConstants.TIME_INSTANCE - 1) {
				maxCoverAdaptS.deltaBudget = totalBudget - usedBudget;
				maxCoverAdaptS.budget = totalBudget - usedBudget;
			} else {
				int preAggBudget = 0;
				for (int ti = 0; ti < TimeInstance; ti++)
					preAggBudget += preBudgets[ti];
				maxCoverAdaptS.deltaBudget = usedBudget - preAggBudget;
				maxCoverAdaptS.budget = getBudget(algorithm);
			}
			
			maxCoverAdaptS.lambda = avgLamda;
			
			maxCoverAdaptS.setTaskList(taskList);
			/**
			 * compute entropy for workers
			 */
			createGrid();
			readEntropy();
			HashMap<Integer, Double> worker_entropiesS = new HashMap<Integer, Double>();
			
			for (int idx = 0; idx < containerWorker.size(); idx++)
				worker_entropiesS.put(idx, computeCost(workerList.get(idx)));
			
			HashMap<Integer, Double> task_entropiesS = new HashMap<Integer, Double>();
			for (int idx = 0; idx < taskList.size(); idx++)
				task_entropiesS.put(idx, computeCost(taskList.get(idx)));

			maxCoverAdaptS.setWorkerEntropies(worker_entropiesS);
			maxCoverAdaptS.setTaskEntropies(task_entropiesS);
			maxCoverAdaptS.maxEntropy = maxEntropy;
			maxCoverAdaptS.meanEntropy = meanEntropy;
			maxCoverAdaptS.totalEntropy = totalEntropy;
			
			assignedWorker = maxCoverAdaptS.maxCover();

			TotalAssignedTasks += maxCoverAdaptS.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();
			
			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptS.gain + 0.0)/(TimeInstance + 1);
			//System.out.print("\t" + maxCoverAdaptS.deltaBudget + "\t" + avgLamda);

			maxCover = maxCoverAdaptS;
			break;
		}

		/**
		 * As all the tasks in the container are assigned, we need to remove
		 * them from task list.
		 */
		ArrayList<Integer> assignedTasks = new ArrayList<Integer>();
		// Iterator it = sc.universe.iterator();
//		System.out.println(maxCover.assignedTaskSet);
		if (maxCover.assignedTaskSet.size() > 0) {
		Iterator it = maxCover.assignedTaskSet.iterator();
		while (it.hasNext()) {
			Integer candidateIndex = (Integer) it.next();
			assignedTasks.add(candidateTaskIndices.get(candidateIndex));
		}}

		/**
		 * sorting is necessary to make sure that we don't mess things up when
		 * removing elements from a list
		 */
		Collections.sort(assignedTasks);
		for (int i = assignedTasks.size() - 1; i >= 0; i--) {
			/* remove the last elements first */
			taskList.remove((int) assignedTasks.get(i));
		}

		/**
		 * update workerCounts
		 */
		updateWorkerCounts(assignedWorker);

		return assignedWorker;
	}

	// update worker counts
	private void updateWorkerCounts(HashSet<Integer> assignedWorker) {
		for (Integer i : assignedWorker) {
			GenericWorker w = workerList.get(i);
			if (workerCounts.containsKey(w.getId())) {
				workerCounts.put(w.getId(),
						workerCounts.get(w.getId()) + 1);
			} else {
				workerCounts.put(w.getId(), 1);
			}
		}
	}
	
	/**
	 * Debug the number of requests for each worker.
	 */
	public int printWorkerCounts() {
		System.out.println("\nWorker counts:");
		int max = 0;
		HashMap<Integer, Integer> h = new HashMap<Integer, Integer>();
		for (String key : workerCounts.keySet()) {
			int count = workerCounts.get(key);
			if (max < count)
				max = count;
			if (h.containsKey(count))
				h.put(count, h.get(count) + 1);
			else
				h.put(count, 1);
		}
		System.out.println("count\tfreq");

		for (Integer i : h.keySet()) {
			System.out.println(i + "\t" + i*h.get(i));
		}
		System.out.println("\nMax count: " + max);
		return max;
	}

	private int getBudget(AlgorithmEnum algorithm) {
		switch (algorithm) {

		case MAX_COVER_BASIC:
		case MAX_COVER_BASIC_MO:
		case MAX_COVER_BASIC_S_MO:
		case MAX_COVER_BASIC_S:
		case MAX_COVER_BASIC_S2:
		case MAX_COVER_BASIC_T:
		case MAX_COVER_BASIC_T2:
		case MAX_COVER_BASIC_ST:
		case MAX_COVER_ADAPT_B:
		case MAX_COVER_ADAPT_T:
		case MAX_COVER_ADAPT_S:
			if (TimeInstance < GeocrowdConstants.TIME_INSTANCE - 1) {
				return totalBudget / GeocrowdConstants.TIME_INSTANCE;
			} else {
				return totalBudget - usedBudget;
			}
			
		case MAX_COVER_BASIC_WORKLOAD:
		case MAX_COVER_ADAPT_B_W:
		case MAX_COVER_ADAPT_T_W:
		case MAX_COVER_ADAPT_S_W:
		case MAX_COVER_BASIC_WORKLOAD_T:
		case MAX_COVER_BASIC_W_MO:
			return preBudgets[TimeInstance];
		case MAX_COVER_PRO_B:
		case MAX_COVER_PRO_S:
		case MAX_COVER_PRO_T:
		case MAX_COVER_PRO_ST:
			return totalBudget * numberArrivalTask / totalNumberTasks;
			// return (totalBudget - usedBudget) * taskList.size()
			// / (totalNumberTasks - numberCoveredTask);

		}
		return 0;
	}

	private int computeTotalTasks() throws IOException {
		int numberTasks = 0;
		for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
			String taskPath = Utils.datasetToTaskPath(DATA_SET);
			numberTasks += Parser
					.readNumberOfTasks(taskPath
							+ i + ".txt");
		}
		return numberTasks;
	}
}
