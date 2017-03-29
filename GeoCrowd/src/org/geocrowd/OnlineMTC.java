package org.geocrowd;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import static org.geocrowd.AlgorithmEnum.MAX_COVER_BASIC;
import static org.geocrowd.Geocrowd.algorithm;
import static org.geocrowd.Geocrowd.taskList;

import org.geocrowd.common.crowd.GenericTask;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdConstants;
import org.geocrowd.datasets.synthetic.Parser;
import org.geocrowd.maxcover.MaxCover;
import org.geocrowd.maxcover.MaxCoverAdaptB;
import org.geocrowd.maxcover.MaxCoverAdaptS;
import org.geocrowd.maxcover.MaxCoverAdaptT;
import org.geocrowd.maxcover.MaxCoverBasic;
import org.geocrowd.maxcover.MaxCoverEqualGA;
import org.geocrowd.maxcover.MaxCoverSpatial;
import org.geocrowd.maxcover.MaxCoverSpatial2;
import org.geocrowd.maxcover.MaxCoverEqualSMO;
import org.geocrowd.maxcover.MaxCoverTemporal;
import org.geocrowd.maxcover.Temporal2;
import org.geocrowd.maxcover.MaxCoverSpatialTemporal;

public class OnlineMTC extends GeocrowdSensing {

	public int totalBudget = 0;
	public static HashMap<String, Integer> workerCounts = new HashMap<String, Integer>();
	
	

	public final int totalNumberTasks;

	public int numberArrivalTask = 0;
	public int totalNumberArrivalTask = 0;

	public double avgLamda;
	public int beta = 2;
	public int usedBudget;
	public double epsGain = 0.2;
	public double epsBudget = 0.2;
	public int[] preBudgets;

	// public static HashMap<Integer, Integer> workerCounts = new Hash

	public static ArrayList<Integer> randomBudget;
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

	public void printSelectedWorker(String algorithm, HashSet<Integer> assignWorkers) {

		ArrayList<Integer> selectedWorkers = new ArrayList<>();
		for (Integer workerId : assignWorkers) {
			selectedWorkers.add(workerId);
		}
		Collections.sort(selectedWorkers);
		try (FileWriter fw = new FileWriter(Geocrowd.TimeInstance + algorithm + "_selectedWorker.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			for (int i = 0; i < selectedWorkers.size(); i++)
				out.write(selectedWorkers.get(i) + ",");
			out.write("\n");
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		}

	}

	public HashSet<Integer> maxCoverage() {
		MaxCover maxCover = null;
		HashSet<Integer> assignedWorker = new HashSet<Integer>();
		switch (algorithm) {

		case MAX_COVER_BASIC:
		case MAX_COVER_BASIC_WORKLOAD:
		case MAX_COVER_PRO_B:
			// MaxCoverBasic maxCoverBsic = new MaxCoverBasic(
			// getContainerWithDeadline(), TimeInstance); //luan test here
			MaxCoverBasic maxCoverBsic = new MaxCoverBasic(containerWorkerWithTaskDeadline, TimeInstance);
			maxCoverBsic.budget = getBudget(algorithm);
			// System.out.println("xxxx" + maxCoverPro.budget);
			assignedWorker = maxCoverBsic.maxCover();
			// printSelectedWorker(algorithm.toString(), assignedWorker);
			TotalAssignedTasks += maxCoverBsic.assignedTasks;
			TotalCoveredUtility += maxCoverBsic.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverBsic;
			// System.out.print("\t" + maxCoverPro.gain);
			break;
			
		case MAX_COVER_NAIVE_B:
			MaxCoverBasic maxCoverBsic2 = new MaxCoverBasic(containerWorkerWithTaskDeadline, TimeInstance);

			maxCoverBsic2.budget = totalBudget - usedBudget;

			assignedWorker = maxCoverBsic2.maxCover();
			// printSelectedWorker(algorithm.toString(), assignedWorker);

			// TotalAssignedTasks+= maxCoverBasicT3.assignedTasks;

			TotalAssignedTasks = Geocrowd.assignedTasks.size();
			TotalCoveredUtility += maxCoverBsic2.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();

			maxCover = maxCoverBsic2;
			break;
			
		case MAX_COVER_RANDOM_B:
			MaxCoverBasic maxCoverBsic3 = new MaxCoverBasic(containerWorkerWithTaskDeadline, TimeInstance);

			maxCoverBsic3.budget = getBudget(algorithm);

			assignedWorker = maxCoverBsic3.maxCover();
			// printSelectedWorker(algorithm.toString(), assignedWorker);
			TotalAssignedTasks += maxCoverBsic3.assignedTasks;
			TotalCoveredUtility += maxCoverBsic3.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();

			maxCover = maxCoverBsic3;
			break;

		case MAX_COVER_BASIC_MO:
		case MAX_COVER_BASIC_W_MO:
			MaxCoverEqualGA mcBasicMo = new MaxCoverEqualGA();
			int budget = getBudget(algorithm);
			int[] _workerCounts = new int[workerList.size()];
			int i = 0;
			for (GenericWorker w : workerList) {
				if (workerCounts.containsKey(w.getId()))
					_workerCounts[i++] = workerCounts.get(w.getId());
				else
					_workerCounts[i++] = 0;
			}
			assignedWorker = mcBasicMo.maxCover(containerWorkerWithTaskDeadline, TimeInstance, _workerCounts, budget);
			// printSelectedWorker(algorithm.toString(), assignedWorker);
			TotalAssignedTasks += mcBasicMo.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = mcBasicMo;
			break;

		case MAX_COVER_BASIC_S_MO:
			MaxCoverEqualSMO mcBasicSMO = new MaxCoverEqualSMO();
			int budgetSMO = getBudget(algorithm);
			assignedWorker = mcBasicSMO.maxCover(containerWorkerWithTaskDeadline, TimeInstance, budgetSMO);

			TotalAssignedTasks += mcBasicSMO.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = mcBasicSMO;
			break;

		case MAX_COVER_ADAPT_B:
			MaxCoverAdaptB maxCoverAdaptB = new MaxCoverAdaptB(containerWorkerWithTaskDeadline, TimeInstance);
			maxCoverAdaptB.epsGain = epsGain;
			maxCoverAdaptB.epsBudget = epsBudget;
			if (TimeInstance == GeocrowdConstants.TIME_INSTANCE - 1) {
				maxCoverAdaptB.deltaBudget = totalBudget - usedBudget;
			} else {
				int preAggBudget = 0;
				for (int ti = 0; ti < TimeInstance; ti++)
					preAggBudget += totalBudget / GeocrowdConstants.TIME_INSTANCE;
				;
				maxCoverAdaptB.deltaBudget = preAggBudget - usedBudget;
				// System.out.println("xxx" + maxCoverAdaptB.deltaBudget);
			}
			maxCoverAdaptB.budget = totalBudget - usedBudget;

			maxCoverAdaptB.lambda = avgLamda;
			assignedWorker = maxCoverAdaptB.maxCover();

			TotalAssignedTasks += maxCoverAdaptB.assignedTasks;
			TotalCoveredUtility += maxCoverAdaptB.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();

			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptB.gain + 0.0) / (TimeInstance + 1);
			// avgLamda = maxCoverAdapt.gain;
			// System.out.print("\t" + maxCoverAdaptB.deltaBudget + "\t" +
			// avgLamda);

			maxCover = maxCoverAdaptB;

			break;
		case MAX_COVER_ADAPT_B_W:

			MaxCoverAdaptB maxCoverAdapt = new MaxCoverAdaptB(containerWorkerWithTaskDeadline, TimeInstance);
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
			avgLamda = (avgLamda * TimeInstance + maxCoverAdapt.gain + 0.0) / (TimeInstance + 1);
			// avgLamda = maxCoverAdapt.gain;
			// System.out.print("\t" + maxCoverAdapt.deltaBudget + "\t" +
			// avgLamda);

			maxCover = maxCoverAdapt;

			break;

		case MAX_COVER_ADAPT_T:
			MaxCoverAdaptT maxCoverAdaptT = new MaxCoverAdaptT(containerWorkerWithTaskDeadline, TimeInstance);
			maxCoverAdaptT.epsGain = epsGain;
			maxCoverAdaptT.epsBudget = epsBudget;
			if (TimeInstance == GeocrowdConstants.TIME_INSTANCE - 1) {
				maxCoverAdaptT.deltaBudget = totalBudget - usedBudget;
			} else {
				int preAggBudget = 0;
				for (int ti = 0; ti < TimeInstance; ti++)
					preAggBudget += totalBudget / GeocrowdConstants.TIME_INSTANCE;
				maxCoverAdaptT.deltaBudget = preAggBudget - usedBudget;
			}
			maxCoverAdaptT.budget = totalBudget - usedBudget;

			maxCoverAdaptT.lambda = avgLamda;
			assignedWorker = maxCoverAdaptT.maxCover();
			// printSelectedWorker(algorithm.toString(), assignedWorker);
			TotalAssignedTasks += maxCoverAdaptT.assignedTasks;
			TotalCoveredUtility += maxCoverAdaptT.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();

			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptT.gain + 0.0) / (TimeInstance + 1);
			// System.out.print("\t" + maxCoverAdaptT.deltaBudget + "\t" +
			// avgLamda);

			maxCover = maxCoverAdaptT;
			break;

		case MAX_COVER_ADAPT_T_W:
			MaxCoverAdaptT maxCoverAdaptTW = new MaxCoverAdaptT(containerWorkerWithTaskDeadline, TimeInstance);
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
			// printSelectedWorker(algorithm.toString(), assignedWorker);
			TotalAssignedTasks += maxCoverAdaptTW.assignedTasks;
			TotalCoveredUtility += maxCoverAdaptTW.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();

			// update average gain
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptTW.gain + 0.0) / (TimeInstance + 1);
			// System.out.print("\t" + maxCoverAdaptTW.deltaBudget + "\t" +
			// avgLamda);

			maxCover = maxCoverAdaptTW;
			break;
			
		
		case MAX_COVER_NAIVE_T:
			MaxCoverTemporal maxCoverBasicT3 = new MaxCoverTemporal(containerWorkerWithTaskDeadline, TimeInstance);

			maxCoverBasicT3.budget = totalBudget - usedBudget;

			assignedWorker = maxCoverBasicT3.maxCover();
			// printSelectedWorker(algorithm.toString(), assignedWorker);

			// TotalAssignedTasks+= maxCoverBasicT3.assignedTasks;

			TotalAssignedTasks = Geocrowd.assignedTasks.size();
			TotalCoveredUtility += maxCoverBasicT3.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();

			maxCover = maxCoverBasicT3;
			break;

		case MAX_COVER_RANDOM_T:
			MaxCoverTemporal maxCoverBasicT4 = new MaxCoverTemporal(containerWorkerWithTaskDeadline, TimeInstance);

			maxCoverBasicT4.budget = getBudget(algorithm);

			assignedWorker = maxCoverBasicT4.maxCover();
			// printSelectedWorker(algorithm.toString(), assignedWorker);
			TotalAssignedTasks += maxCoverBasicT4.assignedTasks;
			TotalCoveredUtility += maxCoverBasicT4.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();

			usedBudget += assignedWorker.size();

			maxCover = maxCoverBasicT4;
			break;
		case MAX_COVER_BASIC_T:
		case MAX_COVER_BASIC_WORKLOAD_T:
		case MAX_COVER_PRO_T:
			MaxCoverTemporal maxCoverBasicT = new MaxCoverTemporal(containerWorkerWithTaskDeadline, TimeInstance);
			maxCoverBasicT.budget = getBudget(algorithm);
			assignedWorker = maxCoverBasicT.maxCover();
			// printSelectedWorker(algorithm.toString(), assignedWorker);
			TotalAssignedTasks += maxCoverBasicT.assignedTasks;
			TotalCoveredUtility += maxCoverBasicT.assignedUtility;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverBasicT;
			// }
			break;
		case MAX_COVER_BASIC_T2:
			Temporal2 maxCoverBasicT2 = new Temporal2(containerWorkerWithTaskDeadline, TimeInstance);
			maxCoverBasicT2.budget = getBudget(algorithm);
			assignedWorker = maxCoverBasicT2.maxCover();

			TotalAssignedTasks += maxCoverBasicT2.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverBasicT2;
			// }
			break;

		case MAX_COVER_BASIC_S:
		case MAX_COVER_PRO_S:
			MaxCoverSpatial maxCoverS = new MaxCoverSpatial(containerWorkerWithTaskDeadline, TimeInstance);
			maxCoverS.budget = getBudget(algorithm);
			// maxCoverS.setTaskList(taskList);
			maxCoverS.setTaskList(tasksMap);
			/**
			 * compute entropy for workers
			 */
			createGrid();
			readBoundary();
			readEntropy();
			HashMap<Integer, Double> worker_entropies = new HashMap<Integer, Double>();
			// luan change container worker to containerWorkerWithTaskDeadline
			for (int idx = 0; idx < containerWorkerWithTaskDeadline.size(); idx++)
				worker_entropies.put(idx, computeCost(workerList.get(idx)));

			HashMap<Integer, Double> task_entropies = new HashMap<Integer, Double>();
			for (int idx = 0; idx < maxCoverS.taskList.size(); idx++)
				task_entropies.put(idx, computeCost(maxCoverS.taskList.get(idx)));

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
			MaxCoverSpatial2 maxCoverS2 = new MaxCoverSpatial2(containerWorkerWithTaskDeadline, TimeInstance);
			maxCoverS2.budget = getBudget(algorithm);
			// maxCoverS2.setTaskList(taskList);
			maxCoverS2.setTaskList(tasksMap);
			/**
			 * compute entropy for workers
			 */
			// printBoundaries();
			createGrid();
			readEntropy();
			HashMap<Integer, Double> worker_entropies2 = new HashMap<Integer, Double>();
			// luan change container worker to containerWorkerWithTaskDeadline
			for (int idx = 0; idx < containerWorkerWithTaskDeadline.size(); idx++)
				worker_entropies2.put(idx, computeCost(workerList.get(idx)));

			HashMap<Integer, Double> task_entropies2 = new HashMap<Integer, Double>();
			for (int idx = 0; idx < maxCoverS2.taskList.size(); idx++)
				task_entropies2.put(idx, computeCost(maxCoverS2.taskList.get(idx)));

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
			MaxCoverSpatialTemporal maxCoverST = new MaxCoverSpatialTemporal(getContainerWithDeadline(), TimeInstance);
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
			MaxCoverAdaptS maxCoverAdaptS = new MaxCoverAdaptS(getContainerWithDeadline(), TimeInstance);
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
			avgLamda = (avgLamda * TimeInstance + maxCoverAdaptS.gain + 0.0) / (TimeInstance + 1);
			// System.out.print("\t" + maxCoverAdaptS.deltaBudget + "\t" +
			// avgLamda);

			maxCover = maxCoverAdaptS;
			break;
		}

		assignedTasks.addAll(maxCover.assignedTaskSet);
//		System.out.println("Assinged Task = " + assignedTasks.size());
		/**
		 * As all the tasks in the container are assigned, we need to remove
		 * them from task list.
		 */
		/**
		 * ArrayList<Integer> assignedTasks = new ArrayList<Integer>();
		 * 
		 * if (maxCover.assignedTaskSet.size() > 0) { Iterator it =
		 * maxCover.assignedTaskSet.iterator(); while (it.hasNext()) { Integer
		 * candidateIndex = (Integer) it.next();
		 * assignedTasks.add(candidateTaskIndices.get(candidateIndex)); }}
		 * 
		 * /** sorting is necessary to make sure that we don't mess things up
		 * when removing elements from a list
		 */
		/**
		 * Collections.sort(assignedTasks); for (int i = assignedTasks.size() -
		 * 1; i >= 0; i--) { /* remove the last elements first
		 */ /**
			 * taskList.remove((int) assignedTasks.get(i)); }
			 **/
		// for(Integer taskID: taskap)

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
				workerCounts.put(w.getId(), workerCounts.get(w.getId()) + 1);
			} else {
				workerCounts.put(w.getId(), 1);
			}
		}
		// workerCounts=assignedTasks.
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
			System.out.println(i + "\t" + i * h.get(i));
		}
		System.out.println("\nMax count: " + max);
		return max;
	}

	private int getBudget(AlgorithmEnum algorithm) {
		switch (algorithm) {

		case MAX_COVER_BASIC:
			if (TimeInstance < GeocrowdConstants.TIME_INSTANCE - 1) {
				return totalBudget / GeocrowdConstants.TIME_INSTANCE;
			} else {
				return totalBudget
						- totalBudget / GeocrowdConstants.TIME_INSTANCE * (GeocrowdConstants.TIME_INSTANCE - 1);
			}
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
				return totalBudget
						- totalBudget / GeocrowdConstants.TIME_INSTANCE * (GeocrowdConstants.TIME_INSTANCE - 1);
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
		case MAX_COVER_RANDOM_B:
		case MAX_COVER_RANDOM_T:
			if(randomBudget == null){
				randomBudget = new ArrayList<>();
				Random r = new Random();
				int sum =0;
				ArrayList<Integer> tem = new ArrayList<>();
				for(int i = 0 ; i < GeocrowdConstants.TIME_INSTANCE; i++){
					Integer k = r.nextInt(28);
					tem.add(k);
					sum +=k;
				}
				int sumBudget = 0;
				for(int i =0 ; i < GeocrowdConstants.TIME_INSTANCE-1; i++){
					int b = totalBudget * tem.get(i)/sum;
					sumBudget +=b;
					randomBudget.add(b);
				}
				randomBudget.add(totalBudget - sumBudget);
			}
			return randomBudget.get(TimeInstance);
		
		}
		
			
		return 0;
	}

	private int computeTotalTasks() throws IOException {
		int numberTasks = 0;
		for (int i = 0; i < GeocrowdConstants.TIME_INSTANCE; i++) {
			String taskPath = Utils.datasetToTaskPath(DATA_SET);
			numberTasks += Parser.readNumberOfTasks(taskPath + i + ".txt");
		}
		return numberTasks;
	}

	public void printStatistics() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		// TODO Auto-generated method stub
		ArrayList<Integer> tasksPerWorkers = new ArrayList<>();
		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(TimeInstance + "tasksPerWorker.txt"), "utf-8"))) {

			for (HashMap<Integer, Integer> taskWithDeadline : containerWorkerWithTaskDeadline) {
				tasksPerWorkers.add(taskWithDeadline.size());
			}
			Collections.sort(tasksPerWorkers);
			for (int i = 0; i < tasksPerWorkers.size(); i++) {
				writer.write(tasksPerWorkers.get(i) + ",");
			}

		}
		ArrayList<Integer> workersPerTask = new ArrayList<>();

		try (Writer writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(TimeInstance + "workersPerTask.txt"), "utf-8"))) {

			for (ArrayList workers : invertedContainer.values()) {
				workersPerTask.add(workers.size());
			}
			Collections.sort(workersPerTask);
			for (int i = 0; i < workersPerTask.size(); i++) {
				writer.write(workersPerTask.get(i) + ",");
			}
		}
	}
}
