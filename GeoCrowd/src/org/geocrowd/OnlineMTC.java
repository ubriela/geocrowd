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

import org.datasets.yelp.Constant;

import static org.geocrowd.AlgorithmEnum.MAX_COVER_BASIC;
import static org.geocrowd.Geocrowd.algorithm;
import static org.geocrowd.Geocrowd.taskList;

import org.geocrowd.common.Constants;
import org.geocrowd.common.crowdsource.GenericTask;
import org.geocrowd.common.crowdsource.GenericWorker;

public class OnlineMTC extends GeocrowdSensing {

	public int totalBudget = 0;
	public HashMap<String, Integer> workerCounts = new HashMap<String, Integer>();

	public final int totalNumberTasks;

	public int numberArrivalTask = 0;
	public int totalNumberArrivalTask = 0;

	public int lamda;
	public int beta = 2;
	public int usedBudget;
	public int budgetOneInstance = 0;

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
		case MAX_COVER_PRO_B:
			MaxCoverBasic maxCoverPro = new MaxCoverBasic(
					getContainerWithDeadline(), TimeInstance);
			maxCoverPro.budget = getBudget(algorithm);
			assignedWorker = maxCoverPro.maxCover();

			TotalAssignedTasks += maxCoverPro.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			usedBudget += assignedWorker.size();
			maxCover = maxCoverPro;
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
			if (TimeInstance < 10) {
				MaxCoverBasic maxCoverPro2 = new MaxCoverBasic(
						getContainerWithDeadline(), TimeInstance);
				maxCoverPro2.budget = getBudget(AlgorithmEnum.MAX_COVER_BASIC);
				assignedWorker = maxCoverPro2.maxCover();
				lamda += maxCoverPro2.gain;

				usedBudget += assignedWorker.size();
				if (TimeInstance == 0)
					budgetOneInstance = assignedWorker.size();
				if (TimeInstance >= 1) {
					beta = beta + assignedWorker.size() - budgetOneInstance;
					budgetOneInstance = assignedWorker.size();
				}

				TotalAssignedTasks += maxCoverPro2.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();
				maxCover = maxCoverPro2;
			} else {
				if (TimeInstance == 10) {
					lamda = lamda / 10;
					beta = beta / 9;
					if (beta == 0)
						beta = 1;
				}

				MaxCoverAdapt maxCoverAdapt = new MaxCoverAdapt(
						getContainerWithDeadline(), TimeInstance);
				maxCoverAdapt.budget = totalBudget - usedBudget;
				maxCoverAdapt.lambda = lamda;
				assignedWorker = maxCoverAdapt.maxCover();

				TotalAssignedTasks += maxCoverAdapt.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();

				maxCover = maxCoverAdapt;
				usedBudget += assignedWorker.size();

				if (usedBudget - totalBudget * (TimeInstance + 1)
						/ Constants.TIME_INSTANCE > 0) {
					lamda += beta;
				} else {
					lamda = lamda - beta;
					if (lamda < 0)
						lamda = 0;
				}
			}

			break;
		case MAX_COVER_BASIC_T:
		case MAX_COVER_PRO_T:
			
			MaxCoverBasicT maxCoverBasicT = new MaxCoverBasicT(
					getContainerWithDeadline(), TimeInstance);
			maxCoverBasicT.budget = getBudget(algorithm);
//			if(TimeInstance < Constants.TaskDuration)
//			{
//				MaxCoverBasic maxCoverPro2 = new MaxCoverBasic(
//						getContainerWithDeadline(), TimeInstance);
//				maxCoverPro2.budget = getBudget(algorithm);
//				HashSet<Integer> assignedWorker2 = maxCoverPro2.maxCover();
//
//				TotalAssignedTasks += maxCoverPro2.assignedTasks;
//				TotalAssignedWorkers += assignedWorker2.size();
//				usedBudget += assignedWorker2.size(); 
//				for(Integer s: assignedWorker2)
//					assignedWorker.add(s);
//				maxCover = maxCoverPro2;
//			}
//			else{
			assignedWorker = maxCoverBasicT.maxCover();

			TotalAssignedTasks += maxCoverBasicT.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();
			maxCover = maxCoverBasicT;
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

				lamda = maxCoverPro2.gain;
				usedBudget += assignedWorker.size();

				maxCover = maxCoverPro2;
			} else {
				MaxCoverAdaptT maxCoverAdapt = new MaxCoverAdaptT(
						getContainerWithDeadline(), TimeInstance);
				maxCoverAdapt.lambda = lamda;

				maxCoverAdapt.budget = totalBudget - usedBudget;
				assignedWorker = maxCoverAdapt.maxCover();

				TotalAssignedTasks += maxCoverAdapt.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();

				maxCover = maxCoverAdapt;
				usedBudget += assignedWorker.size();

				if (usedBudget - totalBudget * totalNumberArrivalTask
						/ totalNumberTasks > 0) {
					lamda += beta;
				} else {
					lamda = lamda - beta;
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
			 * compute entropy for tasks
			 */
//			printBoundaries();
			createGrid();
			readEntropy();
			HashMap<GenericTask, Double> task_entropies = new HashMap<GenericTask, Double>();
			
			for (GenericTask t : taskList) {
				task_entropies.put(t, computeCost(t));
				if(computeCost(t)!=0)
					System.out.println("fdsfs");
			}

			maxCoverS.setEntropies(task_entropies);
			assignedWorker = maxCoverS.maxCover();

			TotalAssignedTasks += maxCoverS.assignedTasks;
			TotalAssignedWorkers += assignedWorker.size();

			maxCover = maxCoverS;
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
				HashMap<GenericTask, Double> task_entropies2 = new HashMap<>();
				for (GenericTask t : taskList) {
					task_entropies2.put(t, computeCost(t));
				}
				maxCoverProS.setEntropies(task_entropies2);
				assignedWorker = maxCoverProS.maxCover();

				TotalAssignedTasks += maxCoverProS.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();

				lamda = maxCoverProS.gain;
				usedBudget += assignedWorker.size();

				maxCover = maxCoverProS;
			} else {
				MaxCoverAdaptS maxCoverAdaptS = new MaxCoverAdaptS(
						getContainerWithDeadline(), TimeInstance);
				maxCoverAdaptS.lambda = lamda;

				maxCoverAdaptS.budget = totalBudget - usedBudget;
				maxCoverAdaptS.setTaskList(taskList);
				/**
				 * compute entropy for tasks
				 */
				printBoundaries();
				createGrid();
				readEntropy();
				HashMap<GenericTask, Double> task_entropies2 = new HashMap<>();
				for (GenericTask t : taskList) {
					task_entropies2.put(t, computeCost(t));
				}
				maxCoverAdaptS.setEntropies(task_entropies2);

				assignedWorker = maxCoverAdaptS.maxCover();

				TotalAssignedTasks += maxCoverAdaptS.assignedTasks;
				TotalAssignedWorkers += assignedWorker.size();
				maxCover = maxCoverAdaptS;
				usedBudget += assignedWorker.size();

				if (usedBudget - totalBudget * totalNumberArrivalTask
						/ totalNumberTasks > 0) {
					lamda += beta;
				} else {
					lamda = lamda - beta;
				}
			}

		}

		/**
		 * As all the tasks in the container are assigned, we need to remove
		 * them from task list.
		 */
		ArrayList<Integer> assignedTasks = new ArrayList<Integer>();
		// Iterator it = sc.universe.iterator();
		Iterator it = maxCover.assignedTaskSet.iterator();
		while (it.hasNext()) {
			Integer candidateIndex = (Integer) it.next();
			assignedTasks.add(candidateTaskIndices.get(candidateIndex));
		}

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
	public void printWorkerCounts() {
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
//		int totalCount = 0;
		for (Integer i : h.keySet()) {
//			totalCount += i * h.get(i);
			System.out.println(i + "\t" + i*h.get(i));
		}
//		System.out.println("total count : " + totalCount);
		System.out.println("\nMax count: " + max);
	}

	private int getBudget(AlgorithmEnum algorithm) {
		switch (algorithm) {

		case MAX_COVER_BASIC:
		case MAX_COVER_BASIC_MO:
		case MAX_COVER_BASIC_S_MO:
		case MAX_COVER_BASIC_S:
		case MAX_COVER_BASIC_T:
		case MAX_COVER_BASIC_ST:
			if (TimeInstance < Constants.TIME_INSTANCE - 1) {
				return totalBudget / Constants.TIME_INSTANCE;
			} else {
				return totalBudget - totalBudget / Constants.TIME_INSTANCE
						* (Constants.TIME_INSTANCE - 1);
			}
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
			switch (Geocrowd.DATA_SET) {
			case GOWALLA:
				numberTasks += Parser
						.readNumberOfTasks(Constants.gowallaTaskFileNamePrefix
								+ i + ".txt");
				break;
			case YELP:
				numberTasks += Parser
						.readNumberOfTasks(Constants.yelpTaskFileNamePrefix + i
								+ ".txt");
				break;
			case UNIFORM:
				numberTasks += Parser
						.readNumberOfTasks(Constants.uniTaskFileNamePrefix + i
								+ ".txt");
				break;
			case SKEWED:
				numberTasks += Parser
						.readNumberOfTasks(Constants.skewedTaskFileNamePrefix
								+ i + ".txt");
				break;
			case SMALL_TEST:
				numberTasks += Parser
						.readNumberOfTasks(Constants.smallTaskFileNamePrefix
								+ i + ".txt");
				break;
			}
		}
		return numberTasks;
	}
}
