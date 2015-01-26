package org.geocrowd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import maxcover.MaxCover;
import maxcover.MaxCoverAdapt;
import maxcover.MaxCoverAdaptS;
import maxcover.MaxCoverAdaptT;
import maxcover.MaxCoverBasic;
import maxcover.MaxCoverBasicMO;
import maxcover.MaxCoverBasicS;
import maxcover.MaxCoverBasicSMO;
import maxcover.MaxCoverBasicT;
import maxcover.MaxCoverBasicT2;
import maxcover.MaxCoverST;

import org.datasets.yelp.Constant;

import static org.geocrowd.AlgorithmEnum.MAX_COVER_BASIC;
import static org.geocrowd.Geocrowd.algorithm;
import static org.geocrowd.Geocrowd.taskList;

import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.GenericWorker;
import org.geocrowd.common.utils.Utils;

public class OnlineMTC extends GeocrowdSensing {

	public int totalBudget = 0;
	public HashMap<String, Integer> workerCounts = new HashMap<String, Integer>();

	public final int totalNumberTasks;

	public int numberArrivalTask = 0;
	public int totalNumberArrivalTask = 0;

	public double avgLamda;
	public int beta = 2;
	public int usedBudget;
	public double eps;
	public int[] budgets;

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
		case MAX_COVER_BASIC_WORKLOAD2:
		case MAX_COVER_PRO_B:
			MaxCoverBasic maxCoverPro = new MaxCoverBasic(
					getContainerWithDeadline(), TimeInstance);
			maxCoverPro.budget = getBudget(algorithm);
//			System.out.println("xxxx" + maxCoverPro.budget); 
			assignedWorker = maxCoverPro.maxCover();

			TotalAssignedTasks += maxCoverPro.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverPro;
//			System.out.print("\t" + maxCoverPro.gain);
			break;
			
		case MAX_COVER_BASIC_MO:
			MaxCoverBasicMO mcBasicMo = new MaxCoverBasicMO();
			int budget = getBudget(algorithm);
			int[] _workerCounts = new int[workerList.size()];
			int i = 0;
			for (GenericWorker w : workerList) {
				if (workerCounts.containsKey(w.getUserID()))
					_workerCounts[i++] = workerCounts.get(w.getUserID());
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

			/**
			 * compute lamda0
			 */
			if (TimeInstance == 0) {
				MaxCoverBasic maxCoverPro2 = new MaxCoverBasic(
						getContainerWithDeadline(), TimeInstance);
				maxCoverPro2.budget = getBudget(AlgorithmEnum.MAX_COVER_BASIC);
				assignedWorker = maxCoverPro2.maxCover();
				
				avgLamda = maxCoverPro2.gain;
				usedBudget = assignedWorker.size();

				TotalAssignedTasks += maxCoverPro2.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();
				maxCover = maxCoverPro2;
				
			} else {

				MaxCoverAdapt maxCoverAdapt = new MaxCoverAdapt(
						getContainerWithDeadline(), TimeInstance);
				maxCoverAdapt.eps = eps;
				if (TimeInstance == Constants.TIME_INSTANCE - 1) {
					maxCoverAdapt.deltaBudget = totalBudget - usedBudget;
					maxCoverAdapt.budget = totalBudget - usedBudget;
				} else {
					maxCoverAdapt.deltaBudget = getBudget(AlgorithmEnum.MAX_COVER_BASIC) * (TimeInstance + 1) - usedBudget;
					maxCoverAdapt.budget = (int)1.3*getBudget(AlgorithmEnum.MAX_COVER_BASIC);
				}
				
				maxCoverAdapt.lambda = avgLamda;
				assignedWorker = maxCoverAdapt.maxCover();

				TotalAssignedTasks += maxCoverAdapt.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();

				usedBudget += assignedWorker.size();
				avgLamda = (avgLamda * TimeInstance + maxCoverAdapt.gain + 0.0)/(TimeInstance + 1);
//				avgLamda = maxCoverAdapt.gain;
				System.out.print("\t" + maxCoverAdapt.deltaBudget + "\t" + avgLamda);

				maxCover = maxCoverAdapt;
			}

			break;
		case MAX_COVER_BASIC_T:
		case MAX_COVER_BASIC_WORKLOAD_T:
		case MAX_COVER_PRO_T:
			MaxCoverBasicT maxCoverBasicT = new MaxCoverBasicT(
					getContainerWithDeadline(), TimeInstance);
			maxCoverBasicT.budget = getBudget(algorithm);
			assignedWorker = maxCoverBasicT.maxCover();

			TotalAssignedTasks += maxCoverBasicT.assignedTasks;
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
		case MAX_COVER_ADAPT_T:
			/**
			 * compute lamda0
			 */
			if (TimeInstance == 0) {
				MaxCoverBasicT maxCoverPro2 = new MaxCoverBasicT(
						getContainerWithDeadline(), TimeInstance);
				maxCoverPro2.budget = getBudget(AlgorithmEnum.MAX_COVER_PRO_T);
				assignedWorker = maxCoverPro2.maxCover();

				TotalAssignedTasks += maxCoverPro2.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();

				avgLamda = maxCoverPro2.gain;
				usedBudget += assignedWorker.size();

				maxCover = maxCoverPro2;
			} else {
				MaxCoverAdaptT maxCoverAdapt = new MaxCoverAdaptT(
						getContainerWithDeadline(), TimeInstance);
				maxCoverAdapt.lambda = avgLamda;

				maxCoverAdapt.budget = totalBudget - usedBudget;
				assignedWorker = maxCoverAdapt.maxCover();

				TotalAssignedTasks += maxCoverAdapt.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();

				maxCover = maxCoverAdapt;
				usedBudget += assignedWorker.size();

				if (usedBudget - totalBudget * totalNumberArrivalTask
						/ totalNumberTasks > 0) {
					avgLamda += beta;
				} else {
					avgLamda = avgLamda - beta;
				}
			}
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
//			printBoundaries();
//			createGrid();
//			readEntropy();
			HashMap<Integer, Double> worker_entropies = new HashMap<Integer, Double>();
			
			for (int idx = 0; idx < containerWorker.size(); idx++)
				worker_entropies.put(idx, computeCost(workerList.get(idx)));

			maxCoverS.setWorkerEntropies(worker_entropies);
			maxCoverS.maxRegionEntropy = maxEntropy;
			assignedWorker = maxCoverS.maxCover();
			
			TotalAssignedTasks += maxCoverS.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();

			maxCover = maxCoverS;
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
			/**
			 * compute lamda0
			 */
			if (TimeInstance == 0) {
				MaxCoverBasicS maxCoverProS = new MaxCoverBasicS(
						getContainerWithDeadline(), TimeInstance);
				maxCoverProS.budget = getBudget(AlgorithmEnum.MAX_COVER_PRO_S);
				maxCoverProS.setTaskList(taskList);
				/**
				 * compute entropy for tasks
				 */
//				printBoundaries();
				createGrid();
				readEntropy();
				HashMap<Integer, Double> worker_entropies2 = new HashMap<Integer, Double>();
				
				for (int idx = 0; idx < containerWorker.size(); idx++)
					worker_entropies2.put(idx, computeCost(workerList.get(idx)));
				
				maxCoverProS.setWorkerEntropies(worker_entropies2);
				assignedWorker = maxCoverProS.maxCover();

				TotalAssignedTasks += maxCoverProS.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();

				avgLamda = maxCoverProS.gain;
				usedBudget += assignedWorker.size();

				maxCover = maxCoverProS;
			} else {
				MaxCoverAdaptS maxCoverAdaptS = new MaxCoverAdaptS(
						getContainerWithDeadline(), TimeInstance);
				maxCoverAdaptS.lambda = avgLamda;

				maxCoverAdaptS.budget = totalBudget - usedBudget;
				maxCoverAdaptS.setTaskList(taskList);
				/**
				 * compute entropy for tasks
				 */
				printBoundaries();
				createGrid();
				readEntropy();
				HashMap<Integer, Double> worker_entropies3 = new HashMap<Integer, Double>();
				
				for (int idx = 0; idx < containerWorker.size(); idx++)
					worker_entropies3.put(idx, computeCost(workerList.get(idx)));
				
				maxCoverAdaptS.setWorkerEntropies(worker_entropies3);

				assignedWorker = maxCoverAdaptS.maxCover();

				TotalAssignedTasks += maxCoverAdaptS.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();
				maxCover = maxCoverAdaptS;
				usedBudget += assignedWorker.size();

				if (usedBudget - totalBudget * totalNumberArrivalTask
						/ totalNumberTasks > 0) {
					avgLamda += beta;
				} else {
					avgLamda = avgLamda - beta;
				}
			}

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
			if (workerCounts.containsKey(w.getUserID())) {
				workerCounts.put(w.getUserID(),
						workerCounts.get(w.getUserID()) + 1);
			} else {
				workerCounts.put(w.getUserID(), 1);
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
		case MAX_COVER_BASIC_T:
		case MAX_COVER_BASIC_T2:
		case MAX_COVER_BASIC_ST:
			if (TimeInstance < Constants.TIME_INSTANCE - 1) {
				return totalBudget / Constants.TIME_INSTANCE;
			} else {
				return totalBudget - usedBudget;
			}
		case MAX_COVER_BASIC_WORKLOAD:
//			System.out.println(TimeInstance);
			if (TimeInstance < Constants.TIME_INSTANCE - 1) {
				if (TimeInstance % 7 == 6 || TimeInstance % 7 == 0)
					return (int) ((totalBudget / Constants.TIME_INSTANCE * (1 + eps*5)));
				else
					return (int) (totalBudget / Constants.TIME_INSTANCE * (1-eps*2));
			} else {
				return totalBudget - usedBudget;
			}
			
		case MAX_COVER_BASIC_WORKLOAD2:
		case MAX_COVER_BASIC_WORKLOAD_T:
			return budgets[TimeInstance];
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
		for (int i = 0; i < Constants.TIME_INSTANCE; i++) {
			String taskPath = Utils.datasetToTaskPath(DATA_SET);
			numberTasks += Parser
					.readNumberOfTasks(taskPath
							+ i + ".txt");
		}
		return numberTasks;
	}
}
