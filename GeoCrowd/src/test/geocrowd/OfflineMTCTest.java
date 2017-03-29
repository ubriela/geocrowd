package test.geocrowd;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.log4j.Logger;
import org.geocrowd.ArrivalRateEnum;
import org.geocrowd.DatasetEnum;
import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdSensing;
import org.geocrowd.OfflineMTC;
import org.geocrowd.OnlineMTC;
import org.geocrowd.common.crowd.GenericTask;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdSensingConstants;
import org.geocrowd.datasets.synthetic.ArrivalRateGenerator;
import org.geocrowd.maxcover.MaxCoverIntegerLinearProgramming;
import org.gnu.glpk.GLPK;

import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactory;
import net.sf.javailp.SolverFactoryGLPK;

public class OfflineMTCTest {
	static Logger logger = Logger.getLogger(OnlineMTCTest.class.getName());

	public static void main(String[] args) {
		
		
//		testGLPK();
//		testLinearProgramming();
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		
//		int[] budgets = new int[] {28, 896, 1792, 3586 }; // gowalla
		int[] budgets = { 24, 48, 96, 192, 384, 768, 1536}; // foursquare
//		int[] budgets = new int[] { 28, 56, 112, 224, 448, 896, 1792 };
//		int[] budgets = new int[] { 56};
//		varying_budget(0, 1, budgets, 5);
//		varying_budgetLinearProgramming(0, 1, budgets,5);

//		 double[] radii = {0.5, 1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5 };
		// double[] radii = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45,
		// 0.5};
		
//		 double[] radii = {  1,2,3,4,5,6,7,8, 9, 10 };
//		 double[] radii = {  1};
//		 varying_radius(0, 1, radii, 56);
//		 for(double r: radii){
//		varying_radius_ILP(0, 1,radii, 56);
//		 
//		}
		 int[] delta = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
		//varying_delta(0,1, delta, 56);
		 varying_delta_ILP(0, 1, delta);
	}

	private static void testGLPK() {
		SolverFactory factory = new SolverFactoryGLPK(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 0); 
		factory.setParameter(Solver.TIMEOUT, 100); // set timeout to 100 seconds

		/**
		* Constructing a Problem: 
		* Maximize: 143x+60y 
		* Subject to: 
		* 120x+210y <= 15000 
		* 110x+30y <= 4000 
		* x+y <= 75
		* 
		* With x,y being integers
		* 
		*/
		Problem problem = new Problem();

		Linear linear = new Linear();
		linear.add(143, "x");
		linear.add(60, "y");

		problem.setObjective(linear, OptType.MAX);

		linear = new Linear();
		linear.add(120, "x");
		linear.add(210, "y");

		problem.add(linear, "<=", 15000);

		linear = new Linear();
		linear.add(110, "x");
		linear.add(30, "y");

		problem.add(linear, "<=", 4000);

		linear = new Linear();
		linear.add(1, "x");
		linear.add(1, "y");

		problem.add(linear, "<=", 75);

		problem.setVarType("x", Integer.class);
		problem.setVarType("y", Integer.class);

		Solver solver = factory.get(); // you should use this solver only once for one problem
		Result result = solver.solve(problem);

		System.out.println(result);
		
	}
	
	public static void varying_radius_ILP(int starttime, int times, double[] radii, int butget){
		ArrayList<Integer> numWorkers = OnlineMTCTest.numWorkers;
		OfflineMTC offlineMTC = new OfflineMTC();
		offlineMTC.isFixed = false;
		offlineMTC.budget = 192;
		offlineMTC.reset();
		
		
		
		
		for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
//			int next_time = next_time_period + i;
			offlineMTC.readWorkloadTasks(
					Utils.datasetToTaskPath(Geocrowd.DATA_SET)
							+ i + ".txt", 0);
//			offlineMTC.readWorkersWithLimit(
//					Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
//							+ i + ".txt", i,numWorkers.get(i));
			
			Geocrowd.workerList.addAll(OnlineMTCTest.listworkers.get(i));
			
		}
		
		//------------------------------
		HashMap<Integer, Integer> mapTasks = new HashMap<>();
		for( GenericTask t: Geocrowd.taskList){
			int time = t.getArrivalTime();
			if(mapTasks.containsKey(time)){
				mapTasks.put(time, mapTasks.get(time)+1);
			}
			else mapTasks.put(time, 1);
		}
		for(Integer timeInstance: mapTasks.keySet()){
			System.out.println("Time Task"+ timeInstance+" :"+ mapTasks.get(timeInstance));
		}
		//-------------------------------
		HashMap<Integer, Integer> mapWorkers = new HashMap<>();
		for(GenericWorker w: Geocrowd.workerList){
			int time = w.getOnlineTime();
			if(mapWorkers.containsKey(time)){
				mapWorkers.put(time, mapWorkers.get(time)+1);
			}
			else mapWorkers.put(time, 1);
		}
		for(Integer timeInstance: mapWorkers.keySet()){
			System.out.println("Time "+ timeInstance+" :"+ mapWorkers.get(timeInstance));
		}	
		//--------------------------------------------------	
		
		for(int i = 0; i < radii.length; i++){
			GeocrowdSensingConstants.MAX_TASK_DURATION = 5;
			GeocrowdSensingConstants.TASK_RADIUS = radii[i];
			offlineMTC.matchingTaskWorkers2();

			//compute the #tasks/worker
			
			ArrayList<HashMap<Integer, Integer>> tasksWithDeadline = offlineMTC.containerWorkerWithTaskDeadline;
			double sum = 0;
			for(HashMap<Integer, Integer> taskD : tasksWithDeadline){
				sum += taskD.size();
			}
			
			mapWorkers.clear();
			for(GenericWorker w: Geocrowd.workerList){
				int time = w.getOnlineTime();
				if(mapWorkers.containsKey(time)){
					mapWorkers.put(time, mapWorkers.get(time)+1);
				}
				else mapWorkers.put(time, 1);
			}
			for(Integer timeInstance: mapWorkers.keySet()){
				System.out.println("Time "+ timeInstance+" :"+ mapWorkers.get(timeInstance));
			}
			System.out.println("Number of workers = "+ tasksWithDeadline.size());
			System.out.println("#Task/worker = "+ sum/tasksWithDeadline.size());
			
			
		
		
			long start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
			MaxCoverIntegerLinearProgramming itlm = new MaxCoverIntegerLinearProgramming(tasksWithDeadline, 
					offlineMTC.invertedContainer,offlineMTC.budget);
	
			
			System.out.println("radius  = "+GeocrowdSensingConstants.TASK_RADIUS);
			itlm.runIntegerLinearProgrammingFixedBudgeILPSolver();
			double elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
			
			System.out.println("Running time of ILP="+elapsedTimeInSec+" seconds");
			System.out.println("-------------------");
			
			
			start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
			System.out.println("radius = "+GeocrowdSensingConstants.TASK_RADIUS );
			itlm.runIntegerLinearProgrammingILPSolver();
			elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
			
			System.out.println("Running time of Dynamic offline ILP="+elapsedTimeInSec+" seconds");
			
		}
	}

	public static void varying_radius(int starttime, int times, double[] radii,
			int budget) {

		boolean[] isFixes = { true, false };
		int[][] coveredTasks = new int[radii.length][isFixes.length];
		int[][] assignedWorkers = new int[radii.length][isFixes.length];
		
		for (int t = 0; t < times; t++) {
			int next_time_period = starttime + t
					* GeocrowdSensingConstants.TIME_INSTANCE;

			for (int r = 0; r < radii.length; r++)
				for (int fix = 0; fix < isFixes.length; fix++) {

					OfflineMTC offlineMTC = new OfflineMTC();
					offlineMTC.isFixed = isFixes[fix];
					offlineMTC.budget = budget;
					GeocrowdSensingConstants.TASK_RADIUS = radii[r];
					offlineMTC.reset();

					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						offlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						offlineMTC.readWorkers(
								Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", i);
					}

					System.out.print("\nradius = " + radii[r] + ", isFixed = "
							+ isFixes[fix]);
					System.out.printf(
							"\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s",
							"TotalTask", "CoveredTask", "TotalWorker",
							"SelectedWorker", "W/T");

					offlineMTC.matchingTasksWorkers();

					offlineMTC.maxTaskCoverage();

					assignedWorkers[r][fix] = OfflineMTC.TotalAssignedWorkers;
					coveredTasks[r][fix] += OfflineMTC.TotalAssignedTasks;

					System.out.printf(
							"\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
							offlineMTC.TaskCount,
							OfflineMTC.TotalAssignedTasks,
							offlineMTC.budget,
							OfflineMTC.TotalAssignedWorkers,
							OfflineMTC.TotalAssignedTasks
									/ Math.max(1,
											OfflineMTC.TotalAssignedWorkers));
				}
		}
		
		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Offline varying radius ");
		pw.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			pw.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < radii.length; b++) {
			pw.printf("\n%-20f \t", radii[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				pw.printf("%-20d \t", (int)(coveredTasks[b][j2]/times));
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}
	
	public static void varying_delta_ILP(int starttime, int times, int[] delta){
//		ArrayList<Integer> numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.COSINE);
		ArrayList<Integer> numWorkers = OnlineMTCTest.numWorkers;
		OfflineMTC offlineMTC = new OfflineMTC();
		offlineMTC.isFixed = false;
		offlineMTC.budget = 56;
		offlineMTC.reset();
		
		
		
		
		for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
//			int next_time = next_time_period + i;
			offlineMTC.readWorkloadTasks(
					Utils.datasetToTaskPath(Geocrowd.DATA_SET)
							+ i + ".txt", 0);
			offlineMTC.readWorkersWithLimit(
					Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i + ".txt", i,numWorkers.get(i));
			
		}
		
		//------------------------------
		HashMap<Integer, Integer> mapTasks = new HashMap<>();
		for( GenericTask t: Geocrowd.taskList){
			int time = t.getArrivalTime();
			if(mapTasks.containsKey(time)){
				mapTasks.put(time, mapTasks.get(time)+1);
			}
			else mapTasks.put(time, 1);
		}
		for(Integer timeInstance: mapTasks.keySet()){
			System.out.println("Time Task"+ timeInstance+" :"+ mapTasks.get(timeInstance));
		}
		//-------------------------------
		HashMap<Integer, Integer> mapWorkers = new HashMap<>();
		for(GenericWorker w: Geocrowd.workerList){
			int time = w.getOnlineTime();
			if(mapWorkers.containsKey(time)){
				mapWorkers.put(time, mapWorkers.get(time)+1);
			}
			else mapWorkers.put(time, 1);
		}
		for(Integer timeInstance: mapWorkers.keySet()){
			System.out.println("Time "+ timeInstance+" :"+ mapWorkers.get(timeInstance));
		}	
		//--------------------------------------------------	
		
		for(int i = 0; i < delta.length; i++){
			GeocrowdSensingConstants.MAX_TASK_DURATION = delta[i];
			offlineMTC.matchingTaskWorkers2();

			//compute the #tasks/worker
			
			ArrayList<HashMap<Integer, Integer>> tasksWithDeadline = offlineMTC.containerWorkerWithTaskDeadline;
			double sum = 0;
			for(HashMap<Integer, Integer> taskD : tasksWithDeadline){
				sum += taskD.size();
			}
			
			mapWorkers.clear();
			for(GenericWorker w: Geocrowd.workerList){
				int time = w.getOnlineTime();
				if(mapWorkers.containsKey(time)){
					mapWorkers.put(time, mapWorkers.get(time)+1);
				}
				else mapWorkers.put(time, 1);
			}
			for(Integer timeInstance: mapWorkers.keySet()){
				System.out.println("Time "+ timeInstance+" :"+ mapWorkers.get(timeInstance));
			}
			System.out.println("Number of workers = "+ tasksWithDeadline.size());
			System.out.println("#Task/worker = "+ sum/tasksWithDeadline.size());
			
			
		
		
			long start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
			MaxCoverIntegerLinearProgramming itlm = new MaxCoverIntegerLinearProgramming(tasksWithDeadline, 
					offlineMTC.invertedContainer,offlineMTC.budget);
	
			
			System.out.println("delta = "+delta[i]);
			itlm.runIntegerLinearProgrammingFixedBudgeILPSolver();
			double elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
			
			System.out.println("Running time of ILP="+elapsedTimeInSec+" seconds");
			System.out.println("-------------------");
			
			
			start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
			System.out.println("delta= "+delta[i] );
			itlm.runIntegerLinearProgrammingILPSolver();
			elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
			
			System.out.println("Running time of Dynamic offline ILP="+elapsedTimeInSec+" seconds");
			
		}
	}
	
	public static void varying_delta(int starttime, int times, int[] delta,
			int budget) {

		boolean[] isFixes = { true, false };
		int[][] coveredTasks = new int[delta.length][isFixes.length];
		int[][] assignedWorkers = new int[delta.length][isFixes.length];
		
		for (int t = 0; t < times; t++) {

			int next_time_period = starttime + t
					* GeocrowdSensingConstants.TIME_INSTANCE;

			for (int d = 0; d < delta.length; d++)
				for (int fix = 0; fix < isFixes.length; fix++) {

					OfflineMTC offlineMTC = new OfflineMTC();
					offlineMTC.isFixed = isFixes[fix];
					offlineMTC.budget = budget;
					GeocrowdSensingConstants.MAX_TASK_DURATION = delta[d];
					offlineMTC.reset();

					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						offlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						offlineMTC.readWorkers(
								Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", i);
					}

					System.out.print("\ndelta = " + delta[d] + ", isFixed = "
							+ isFixes[fix]);
					System.out.printf(
							"\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s",
							"TotalTask", "CoveredTask", "TotalWorker",
							"SelectedWorker", "W/T");

					offlineMTC.matchingTasksWorkers();

					offlineMTC.maxTaskCoverage();

					assignedWorkers[d][fix] = OfflineMTC.TotalAssignedWorkers;
					coveredTasks[d][fix] += OfflineMTC.TotalAssignedTasks;

					System.out.printf(
							"\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
							offlineMTC.TaskCount,
							OfflineMTC.TotalAssignedTasks,
							offlineMTC.budget,
							OfflineMTC.TotalAssignedWorkers,
							OfflineMTC.TotalAssignedTasks
									/ Math.max(1,
											OfflineMTC.TotalAssignedWorkers));
				}
		}
		
		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Offline varying delta ");
		pw.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			pw.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < delta.length; b++) {
			pw.printf("\n%-20d \t", delta[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				pw.printf("%-20d \t", (int)(coveredTasks[b][j2]/times));
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}
	
	
	public static void testLinearProgramming(){
		ArrayList<HashMap<Integer, Integer>> wContainer = new ArrayList<>();
		HashMap<Integer, Integer> hm = new HashMap<>();
		//w0
		hm.put(0, 5);
		wContainer.add(hm);
		//w1
		hm= new HashMap<>();
		hm.put(2, 5);
		hm.put(4, 5);
		hm.put(5, 5);
		wContainer.add(hm);
		//w2
		hm= new HashMap<>();
		hm.put(1, 5);
		wContainer.add(hm);
		//w3
		hm= new HashMap<>();
		hm.put(3, 5);
		wContainer.add(hm);
		//w4
		hm= new HashMap<>();
		hm.put(1, 5);
		hm.put(2, 5);
		hm.put(3, 5);
		wContainer.add(hm);
		
		
		HashMap<Integer, ArrayList<Integer>> tContainer = new HashMap<>();
		//t0
		ArrayList<Integer> wl = new ArrayList<>();
		wl.add(0);
		tContainer.put(0, wl);
		//t1
		wl = new ArrayList<>();
		wl.add(2);
		wl.add(4);
		tContainer.put(1, wl);
		//t2
		wl = new ArrayList<>();
		wl.add(1);
		wl.add(4);
		tContainer.put(2, wl);
		//t3
		wl = new ArrayList<>();
		wl.add(3);
		wl.add(4);
		tContainer.put(3, wl);
		//t4
		wl = new ArrayList<>();
		wl.add(1);
		tContainer.put(4, wl);
		
		//t5
		wl = new ArrayList<>();
		wl.add(1);
		tContainer.put(5, wl);
		
		MaxCoverIntegerLinearProgramming itlm = new MaxCoverIntegerLinearProgramming(wContainer, tContainer,2);
		
		itlm.runIntegerLinearProgramming();
	}
	
	
	
	
	
	public static void varying_budgetLinearProgramming(int starttime, int times, int[] budgets,
			double radius){
//		if(Onli)
//		ArrayList<Integer> numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.COSINE);
		ArrayList<Integer> numWorkers = OnlineMTCTest.numWorkers;
		OfflineMTC offlineMTC = new OfflineMTC();
		offlineMTC.isFixed = false;
	//	offlineMTC.budget = 28;
		offlineMTC.reset();
		GeocrowdSensingConstants.TASK_RADIUS = 5.0;
		
		
		
		
		for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
//			int next_time = next_time_period + i;
			offlineMTC.readWorkloadTasks(
					Utils.datasetToTaskPath(Geocrowd.DATA_SET)
							+ (i) + ".txt", 0);
//			offlineMTC.readWorkersWithLimit(
//					Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
//							+ i + ".txt", i,numWorkers.get(i));
			
//			ArrayList<GenericWorker> workers = onlineMTC.readWorkersWithLimit(Utils
//					.datasetToWorkerPath(Geocrowd.DATA_SET)
//					+ next_time + ".txt",i,numWorkers.get(i));
//			Geocrowd.workerList.addAll(OnlineMTCTest.listworkers.get(i));
			//onlineMTC.WorkerCount+=listworkers.get(i).size();
			
			
			Geocrowd.workerList.addAll(OnlineMTCTest.listworkers.get(i));
//			Geocrowd.taskList.addAll(OnlineMTCTest.tasksLists[i]);
			

			
		}
		
		//------------------------------
		HashMap<Integer, Integer> mapTasks = new HashMap<>();
		for( GenericTask t: Geocrowd.taskList){
			int time = t.getArrivalTime();
			if(mapTasks.containsKey(time)){
				mapTasks.put(time, mapTasks.get(time)+1);
			}
			else mapTasks.put(time, 1);
		}
//		for(Integer timeInstance: mapTasks.keySet()){
//			System.out.println("Time Task"+ timeInstance+" :"+ mapTasks.get(timeInstance));
//		}
		//-------------------------------
		HashMap<Integer, Integer> mapWorkers = new HashMap<>();
		for(GenericWorker w: Geocrowd.workerList){
			int time = w.getOnlineTime();
			if(mapWorkers.containsKey(time)){
				mapWorkers.put(time, mapWorkers.get(time)+1);
			}
			else mapWorkers.put(time, 1);
		}
//		for(Integer timeInstance: mapWorkers.keySet()){
//			System.out.println("Time "+ timeInstance+" :"+ mapWorkers.get(timeInstance));
//		}	
		//--------------------------------------------------	
		offlineMTC.matchingTaskWorkers2();
		//compute the #tasks/worker
		
		ArrayList<HashMap<Integer, Integer>> tasksWithDeadline = offlineMTC.containerWorkerWithTaskDeadline;
		double sum = 0;
		for(HashMap<Integer, Integer> taskD : tasksWithDeadline){
			sum += taskD.size();
		}
		
		
		
		mapWorkers.clear();
		for(GenericWorker w: Geocrowd.workerList){
			int time = w.getOnlineTime();
			if(mapWorkers.containsKey(time)){
				mapWorkers.put(time, mapWorkers.get(time)+1);
			}
			else mapWorkers.put(time, 1);
		}
		for(Integer timeInstance: mapWorkers.keySet()){
			System.out.println("Time "+ timeInstance+" :"+ mapWorkers.get(timeInstance));
		}
		System.out.println("Number of workers = "+ tasksWithDeadline.size());
		System.out.println("#Task/worker = "+ sum/tasksWithDeadline.size());
		
		
		
		for(int i = 0; i < budgets.length; i++){
			long start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
			MaxCoverIntegerLinearProgramming itlm = new MaxCoverIntegerLinearProgramming(offlineMTC.containerWorkerWithTaskDeadline, 
					offlineMTC.invertedContainer,budgets[i]);
	
			
			System.out.println("Fix budget = "+budgets[i]);
			itlm.runIntegerLinearProgrammingFixedBudgeILPSolver();
			double elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
			
			System.out.println("Running time of ILP="+elapsedTimeInSec+" seconds");
			System.out.println("-------------------");
			
			
			start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
			System.out.println("Dynamic budget = "+budgets[i] );
			itlm.runIntegerLinearProgrammingILPSolver();
			elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
			
			System.out.println("Running time of Dynamic offline ILP="+elapsedTimeInSec+" seconds");
			
		}
		}

	public static void varying_budget(int starttime, int times, int[] budgets,
			double radius) {

		boolean[] isFixes = { true, false };
		int[][] coveredTasks = new int[budgets.length][isFixes.length];
		double[][] coveredUtility = new double[budgets.length][isFixes.length];
		int[][] assignedWorkers = new int[budgets.length][isFixes.length];
		
		for (int t = 0; t < times; t++) {
			GeocrowdSensingConstants.TASK_RADIUS = radius;

			int next_time_period = starttime + t
					* GeocrowdSensingConstants.TIME_INSTANCE;

			for (int b = 0; b < budgets.length; b++)
				for (int fix = 0; fix < isFixes.length; fix++) {
					OfflineMTC offlineMTC = new OfflineMTC();
					offlineMTC.isFixed = isFixes[fix];
					offlineMTC.budget = budgets[b];
					offlineMTC.reset();

					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						offlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						offlineMTC.readWorkers(
								Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", i);
					}

					System.out.print("\nbudget = " + budgets[b]
							+ ", isFixed = " + isFixes[fix]);
					System.out.printf(
							"\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s",
							"TotalTask", "CoveredTask", "TotalWorker",
							"SelectedWorker", "W/T");

					offlineMTC.matchingTasksWorkers();
					
					long start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
					offlineMTC.maxTaskCoverage();
					double elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
					
					System.out.println("Running time of fixed =  "+isFixes[fix]+" is: " +elapsedTimeInSec+" seconds");

					assignedWorkers[b][fix] = OfflineMTC.TotalAssignedWorkers;
					coveredTasks[b][fix] += OfflineMTC.TotalAssignedTasks;
					coveredUtility[b][fix] += OfflineMTC.TotalCoveredUtility;

					System.out.printf(
							"\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d\n",
							offlineMTC.TaskCount,
							OfflineMTC.TotalAssignedTasks,
							offlineMTC.budget,
							OfflineMTC.TotalAssignedWorkers,
							OfflineMTC.TotalAssignedTasks
									/ Math.max(1,
											OfflineMTC.TotalAssignedWorkers));
				}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Offline varying budget");
		pw.printf("\n%-20s \t", "");
		for (int j2 = 0; j2 < isFixes.length; j2++)
			pw.printf("%-20s \t", isFixes[j2]);
		for (int b = 0; b < budgets.length; b++) {
			pw.printf("\n%-20d \t", budgets[b]);
			for (int j2 = 0; j2 < isFixes.length; j2++)
				pw.printf("%-20f \t", coveredUtility[b][j2]/times);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}
}
