package test.geocrowd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.geocrowd.AlgorithmEnum;
import org.geocrowd.ArrivalRateEnum;
import org.geocrowd.Constants;
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
import org.junit.Test;

import com.sun.javafx.scene.paint.GradientUtils.Parser;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 * @author Luan
 */
public class OnlineMTCTest {
	static Logger logger = Logger.getLogger(OnlineMTCTest.class.getName());
	static ArrayList<Integer> numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.COSINE);
	
	
	static ArrayList<GenericTask>[] tasksLists = new ArrayList[28];
	
	public static ArrayList<ArrayList<GenericWorker>> listworkers = new ArrayList<>();
	
	public static int _g;
	public static double[] runningTime = new double[11];
	public static void initializationRunningTime(){
		for(int i =0; i < runningTime.length; i++){
			runningTime[i]=0;
		}
	}
	
	public static void readWokers() throws IOException{
		for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
			OnlineMTC onlineMTC = new OnlineMTC();
			onlineMTC.reset();
			ArrayList<GenericWorker> workers = onlineMTC.readWorkersWithLimit(Utils
					.datasetToWorkerPath(Geocrowd.DATA_SET)
					+ (i) + ".txt",i,numWorkers.get(i));
			listworkers.add(workers);
		}
	}
	public static void readTasks(){
		for(int i = 0 ; i< GeocrowdSensingConstants.TIME_INSTANCE; i++){
			ArrayList<GenericTask> taskList = new ArrayList<>();
			taskList =OnlineMTC.readTasks(Utils.datasetToTaskPath(Geocrowd.DATA_SET)
					+ i + ".txt", 0);
			tasksLists[i] = taskList;
		}
	}
	
	
	public static void computeAverageSelectedTimes(String algorithm, int[] budgets, double[] thetas) throws IOException{

		for(int i =0; i < budgets.length; i++)
		{
			System.out.print(budgets[i]+"\t");
			for(int j = 0; j < thetas.length; j++){
				int max = 0;
				String filename1 = algorithm  +budgets[i] +"_" +thetas[j]+".txt";
				BufferedReader bfr = new BufferedReader(new FileReader(filename1));
				String line = "";
				int sum = 0;
				int count = 0;
				while((line = bfr.readLine())!=null){
					String[] parts = line.split(":");
					sum += Integer.valueOf(parts[1]);
					if(max < Integer.valueOf(parts[1])) max = Integer.valueOf(parts[1]);
					count++;
				}
				double avg1 = sum*1.0/count;
				bfr.close();
				
				System.out.print(avg1+"\t");
			}
			System.out.println();
			
		}
	}
	
	public static void printStatistic(String alg, int budget, double alpha) throws IOException{
		System.out.println(alg);
		System.out.println("alpha = "+alpha +",budget = "+budget);
		System.out.println("----------------");
		String filename1 = alg  +budget +"_"+alpha+".txt";
		BufferedReader bfr = new BufferedReader(new FileReader(filename1));
		HashMap<Integer, Integer> counts = new HashMap<>();
		String line = "";
		while((line = bfr.readLine())!=null){
			String[] parts = line.split(":");
			Integer c = Integer.valueOf(parts[1]);
			if(counts.containsKey(c)) counts.put(c, counts.get(c)+1);
			else counts.put(c, 1);
		}
		for(Integer i: counts.keySet()){
			System.out.println(i +":"+ counts.get(i));
		}
	}
	
	public static void computeAverageSelectedTimes2( int[] budgets, String[] algorithms, double[] alphas) throws IOException{

		for(int k = 0; k < alphas.length; k++)
		for(int i =0; i < budgets.length; i++)
		{
			System.out.print(alphas[k]+"\t");
			for(int j = 0; j < algorithms.length; j++){
				int max = 0;
				String filename1 = algorithms[j]  +budgets[i] +"_"+alphas[k]+".txt";
				BufferedReader bfr = new BufferedReader(new FileReader(filename1));
				String line = "";
				int sum = 0;
				int count = 0;
				while((line = bfr.readLine())!=null){
					String[] parts = line.split(":");
					sum += Integer.valueOf(parts[1]);
					if(max < Integer.valueOf(parts[1])) max = Integer.valueOf(parts[1]);
					count++;
				}
				double avg1 = sum*1.0/count;
				bfr.close();
				
				System.out.print(avg1+"\t");
			}
			System.out.println();
			
		}
	}
	
	public static void computeCommonSelectedWorkers(String algorithm, int times) throws IOException{
		
		
		
		for(int time = 0 ; time < times; time++){
			
			double ratio2_1 = 0;
			double ratio3_1 = 0;
			for(int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++){
				
				int startLine = time*3;
				//read file 
				
				FileReader fr = new FileReader(i +algorithm+"_selectedWorker.txt");
				BufferedReader bfr = new BufferedReader(fr);
				int count=0; 
				while(count <= startLine){
					String line1 = bfr.readLine();
					String line2 = bfr.readLine();
					String line3 = bfr.readLine();
					if(count == startLine){
						String[] selectedWorkers1 = line1.split(",");
						String[] selectedWorkers2 = line2.split(",");
						String[] selectedWorkers3 = line3.split(",");
						
						HashSet<Integer> selected1 = new HashSet<>();
						HashSet<Integer> selected2 = new HashSet<>();
						HashSet<Integer> selected3 = new HashSet<>();
						
						for(String s: selectedWorkers1){
							if(s!=null && !s.equals(""))
							selected1.add(Integer.valueOf(s));
						}
						for(String s: selectedWorkers2){
							if(s!=null && !s.equals(""))
							selected2.add(Integer.valueOf(s));
						}
						for(String s: selectedWorkers3){
							if(s!=null && !s.equals(""))
							selected3.add(Integer.valueOf(s));
						}
						
						//compare alg 2 to alg1
						int count21 = 0;
						for(Integer s: selected2){
							if(selected1.contains(s)) count21 ++;
						}
						
						//compare alg3 to 1
						int count31 =0;
						for(Integer s: selected3){
							if(selected1.contains(s)) count31 ++;
						}
						
						//update ratio 21, 31
						if(selected1.size()!=0){
						ratio2_1 = (ratio2_1*i+ (count21*1.0/selected1.size()))/(i+1);
						ratio3_1 = (ratio3_1*i+ (count31*1.0/selected1.size()))/(i+1);
						}
						else{
							ratio2_1 = (ratio2_1*i+ 1)/(i+1);
							ratio3_1 = (ratio3_1*i+ 1)/(i+1);
						}
					}
					
					
					count +=3;
					
				}
				
				
			
			}
			
			
			System.out.println("Ratio 2_1 = "+ ratio2_1);
			System.out.println("Ratio 3_1 = "+ ratio3_1);
		}
	}
	public static void main(String[] args) throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		initializationRunningTime();
		//print count
		for(int i: numWorkers){
			System.out.print(i+",");
		}
		
//		computeCommonSelectedWorkers("MAX_COVER_ADAPT_T",6);
		
		
////		// overloading();
//
//		int[] budgets = new int[] {
//				 96,
//				 192,
//				  384, 
//				 768,
//				 1536
//				 };
		 int[] budgets = new int[] {
				 56
				 , 112,224,
				 448,
				 896, 
				 1288
				 };
		 double[] alpha = new double[] { 0.0, 0.1, 0.3, 0.5, 0.7, 0.9, 1.0 };
		 double[] thetas = new double[]{0, 0.01, 0.02, 0.03, 0.05, 0.07, 0.1,0.3,0.5,0.7,0.9,1
		 };
//		 double[] thetas = new double[]{0};
//		 computeAverageSelectedTimes("MAX_COVER_ADAPT_T_W",  budgets, alpha);
//		 computeAverageSelectedTimes2(budgets, new String[]{"MAX_COVER_ADAPT_T"}, alpha);
////		 int[] budgets = { 24, 48, 96, 192, 384, 768, 1536, 3072 };
////		 int[] budgets = { 24, 48, 96};
		
//		 printStatistic("MAX_COVER_ADAPT_T", 448,0.9);
//		 printStatistic("MAX_COVER_BASIC", 448,1000;
//		 printStatistic("MAX_COVER_BASIC_W_MO", 1288,0.2);
		 
		 double radius = 5.0;
//		// int start_time = 0;
		 readWokers();
//		 System.out.println("Done reading workers");
		readTasks();
////		
		 System.out.println("Done reading tasks");
		 
		 
//			workload_vary_budget(radius, budgets);
//		testOverloading();
//	 
		double[] radii = new double[] { 
				1, 2,
				3, 4, 5, 6, 7,
				8, 9, 
				10 
				};
		int totalBudget = 448;
//		// start_time = 0;
		workload_vary_radius(radii, totalBudget);

//		 int[] delta = new int[] { 1,2,3,4,5,6,7,8,9,10 };
//		 double radius = 5.0;
//		 int start_time = 0;
//		 workload_vary_delta(delta, totalBudget, radius);
		//
		// int cycle_length = 7;
		// int workload_size = GeocrowdSensingConstants.TIME_INSTANCE / cycle_length;
		// int workloadCount = 8;
		// totalBudget = 56;
		// radius = 5.0;
		// start_time = 0;
		// workload_vary_time(cycle_length, workload_size, totalBudget, radius,
		// start_time, workloadCount);

		// workload();
		// vary_a();
		// vary_eps();
		// vary_r();
		// vary_b();
		// vary_en();

//		 int[] budgets = new int[] {  448, 896, 1792};
//		 OfflineMTCTest.varying_radius_ILP(0, 1, radii, totalBudget);
//		OfflineMTCTest.varying_budgetLinearProgramming(0, 1, budgets,5);
//		 OfflineMTCTest.varying_delta_ILP(0, 1, delta);
		
	}

	@Test
	public void testOneWorkload() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_ADAPT_B,
				AlgorithmEnum.MAX_COVER_ADAPT_B_W,
				AlgorithmEnum.MAX_COVER_ADAPT_T,
				AlgorithmEnum.MAX_COVER_ADAPT_T_W,
		// AlgorithmEnum.MAX_COVER_ADAPT_S,
		// AlgorithmEnum.MAX_COVER_ADAPT_S_W
		};

		// Double[] epsGains = new Double[] { 0.05, 0.1, 0.15, 0.2, 0.25 };
		double[] epsGains = new double[] { 0.5 };
		double[] epsBudgets = new double[] { 0.8 };

		int[][] coveredTasks = new int[epsGains.length][algorithms.length];
		int[][] assignedWorkers = new int[epsGains.length][algorithms.length];

		// GeocrowdTest.main(null);

		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		int totalBudget = 28;
		GeocrowdSensingConstants.TASK_RADIUS = 5.0;
		int start_time = 0;
		int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

		System.out.println("\nRadius = " + GeocrowdSensingConstants.TASK_RADIUS);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null); // generate set of tasks for next period

		// apply offline method to next period
		int next_time_period = start_time + GeocrowdSensingConstants.TIME_INSTANCE;
		computeHistoryBudgets(false, totalBudget, next_time_period);

		// use the same set of workers/tasks for all following period
		for (int eps = 0; eps < epsGains.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.epsGain = epsGains[eps];
				onlineMTC.epsBudget = epsBudgets[eps];
				onlineMTC.preBudgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
					int next_time = next_time_period + i;
					onlineMTC.readWorkloadTasks(
							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
									+ next_time + ".txt", next_time_period);
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ next_time
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[eps][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[eps][g] = OnlineMTC.TotalAssignedWorkers;
			}
		}

		/**
		 * print result
		 */
		System.out.println("\n\n##################");
		System.out.println("Budget = " + totalBudget);
		System.out.println("#Covered Tasks");
		System.out.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			System.out.printf("%-20s \t", algorithms[j2]);
		for (int eps = 0; eps < epsGains.length; eps++) {
			System.out.printf("\n%-20f \t", epsGains[eps]);
			for (int g2 = 0; g2 < algorithms.length; g2++) {
				System.out.printf("%-20d \t", coveredTasks[eps][g2]);
			}
		}
	}

	/**
	 * Varying all tasks
	 * 
	 * @throws IOException
	 */
	private static void workload_vary_time(int cycle_length, int workload_size,
			int totalBudget, double radius, int starttime, int w_count)
			throws IOException {

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_ADAPT_B,
				AlgorithmEnum.MAX_COVER_ADAPT_B_W,
				AlgorithmEnum.MAX_COVER_ADAPT_T,
				AlgorithmEnum.MAX_COVER_ADAPT_T_W };

		int[][] coveredTasks = new int[w_count][algorithms.length + 2];
		int[][] assignedWorkers = new int[w_count][algorithms.length + 2];

		GeocrowdSensingConstants.TIME_INSTANCE = cycle_length * workload_size;
		int start_time = starttime;

		GeocrowdSensingConstants.TASK_RADIUS = radius;
		System.out.println("\nRadius = " + GeocrowdSensingConstants.TASK_RADIUS);
		System.out.println("Budget = " + totalBudget);

		/**
		 * Iterate all possible workloads
		 */
		for (int w = 0; w < w_count; w++) {
			Geocrowd.TimeInstance = 0;
			// GeocrowdTest.main(null);

			int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

			// GeocrowdTest.main(null); // generate set of tasks for next period

			// apply offline method to next period
			int next_time_period = start_time + GeocrowdSensingConstants.TIME_INSTANCE;

			computeHistoryBudgets(true, totalBudget, next_time_period);
			int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
			computeHistoryBudgets(false, totalBudget, next_time_period);
			int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

			// use the same set of workers/tasks for all following period
			for (int g = 0; g < algorithms.length; g++) {
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.preBudgets = counts;
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
					int next_time = next_time_period + i;
					onlineMTC.readWorkloadTasks(
							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
									+ next_time + ".txt", next_time_period);
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ next_time
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[w][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[w][g] = OnlineMTC.TotalAssignedWorkers;
			}

			coveredTasks[w][algorithms.length] = fixed_offline_cov;
			coveredTasks[w][algorithms.length + 1] = dynamic_offline_cov;

			// update start_time
			start_time = next_time_period;
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n##################Varying time");
		pw.println("Budget = " + totalBudget);
		pw.println("radius = " + GeocrowdSensingConstants.TASK_RADIUS);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int w = 0; w < w_count; w++) {
			pw.printf("\n%-20d \t", w);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				pw.printf("%-20d \t", coveredTasks[w][g2]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	
	private static void workload_vary_budget(double radius, int[] budgets)
			throws IOException {
		
		 double[] thetas = new double[]{0,0.1,0.3,0.5,0.7,0.9,1
		 };
		 double[] alpha = new double[] { 0.0, 0.1, 0.3, 0.5, 0.7, 0.9, 1.0 };
		if(numWorkers==null) numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.COSINE);
		GeocrowdSensingConstants.TASK_RADIUS = radius;
		System.out.println("radius = " + GeocrowdSensingConstants.TASK_RADIUS);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_ADAPT_B,
				AlgorithmEnum.MAX_COVER_ADAPT_T,
				AlgorithmEnum.MAX_COVER_ADAPT_T_W,
//				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_NAIVE_B,
//				AlgorithmEnum.MAX_COVER_RANDOM_B
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
				
				
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_BASIC,
				
				
				
				
				
//				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_BASIC, //linear utility
//				AlgorithmEnum.MAX_COVER_BASIC, //zipfian 
//				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_ADAPT_B,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta =0
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //linear
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //zipfian
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta = 0.5
//				
//				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_BASIC, //linear utility
//				AlgorithmEnum.MAX_COVER_BASIC, //zipfian 
//				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_ADAPT_B,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta =0
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //linear
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //zipfian
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta = 0.5
//				
//				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_BASIC, //linear utility
//				AlgorithmEnum.MAX_COVER_BASIC, //zipfian 
//				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_ADAPT_B,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta =0
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //linear
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //zipfian
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta = 0.1
//				
//				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_BASIC, //linear utility
//				AlgorithmEnum.MAX_COVER_BASIC, //zipfian 
//				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_MO, 
//				AlgorithmEnum.MAX_COVER_ADAPT_B,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta =0
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //linear
//				AlgorithmEnum.MAX_COVER_ADAPT_T, //zipfian
//				AlgorithmEnum.MAX_COVER_ADAPT_T_W, //theta = 0.1
//			
				};

		int[][] coveredTasks = new int[budgets.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[budgets.length][algorithms.length + 2];

		int times = 25;
		for (int t = 0; t < times; t++) {
			int start_time = 0 + 0 * GeocrowdSensingConstants.TIME_INSTANCE;
			for (int b = 0; b < budgets.length; b++) {
				int totalBudget = budgets[b];
				
				System.out.println("\n----\nBudget = " + totalBudget);
				Geocrowd.TimeInstance = 0;

//				int[] counts = computeHistoryBudgets(false, totalBudget,
//						start_time);

				// apply offline method to next period
				int next_time_period = start_time
						+ 0*GeocrowdSensingConstants.TIME_INSTANCE;
				// computeHistoryBudgets(true, totalBudget, next_time_period);
				//int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
//				int[] preBudget = computeHistoryBudgets(false, totalBudget, next_time_period);
				//int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

				// use the same set of workers/tasks for all following period
				for (int g = 0; g < algorithms.length; g++) {
//					
//					if(g == 0) Constants.UTILITY_FUNCTION = "const";
//					else if(g == 1) Constants.UTILITY_FUNCTION = "linear";
//					else if(g == 2) Constants.UTILITY_FUNCTION = "zipf";
//					if(g == 0) Constants.workerOverload = false;
//					if(g==1) Constants.workerOverload = true;
//					if(g <7)
//					Constants.theta = thetas[g];
//					if(g >=7 && g < 14)
//					Constants.alpha = alpha[g-7];
//					if(g == 14){
//						Constants.theta = 0;
//						Constants.alpha = 1;
//					}
//					if(g == 1) Constants.alpha = 0.9;
//					else Constants.alpha = 0;
//					if( g == 4 || g == 7) Constants.theta = 0.1;
//					else Constants.theta = 0;
					
					
//					if(g ==0) numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.POISSON);
//					if(g ==1) numWorkers = ArrivalRateGenerator.generateCounts(28, 100, ArrivalRateEnum.POISSON);
//					if(g ==2) numWorkers = ArrivalRateGenerator.generateCounts(28, 150, ArrivalRateEnum.POISSON);
//					if(g ==3) numWorkers = ArrivalRateGenerator.generateCounts(28, 200, ArrivalRateEnum.POISSON);
//					if(g ==4) numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.POISSON);
//					if(g ==5) numWorkers = ArrivalRateGenerator.generateCounts(28, 100, ArrivalRateEnum.POISSON);
//					if(g ==6) numWorkers = ArrivalRateGenerator.generateCounts(28, 150, ArrivalRateEnum.POISSON);
//					if(g ==7) numWorkers = ArrivalRateGenerator.generateCounts(28, 200, ArrivalRateEnum.POISSON);
					//update taskList
//					for(int i =0 ; i < tasksLists.length; i++){
//						tasksLists[i] = new ArrayList(tasksLists[i].subList(0, 1200-200*(4-i%4)));
//						
//					}
//					if(g%11 == 1 || g%11 ==8) Constants.UTILITY_FUNCTION = "linear";
//					else if(g%11 == 2  || g%11 == 9) Constants.UTILITY_FUNCTION = "zipf";
//					else Constants.UTILITY_FUNCTION = "const";
//					
//					if(g%11 == 10)Constants.theta = 0;
//					else Constants.theta = 0.5;
					
					
					Geocrowd.algorithm = algorithms[g];
					String filename = "";
					if(g < 7)
					filename = algorithms[g].toString()+totalBudget+"_"+Constants.theta+".txt";
					else if(g >=7 && g <14)
						filename = algorithms[g].toString()+totalBudget+"_"+Constants.alpha+".txt";
					else filename = algorithms[g].toString()+totalBudget+"_"+".txt";
//					String filename = algorithms[g].toString()+totalBudget+".txt";
					OnlineMTC onlineMTC = new OnlineMTC();
					onlineMTC.reset();
					
//					if(Geocrowd.algorithm == AlgorithmEnum.MAX_COVER_ADAPT_T_W
//							|| Geocrowd.algorithm == AlgorithmEnum.MAX_COVER_BASIC_W_MO )
//					preBudget = computeHistoryBudgets(false, totalBudget, next_time_period);
					
//					onlineMTC.preBudgets = preBudget;
					onlineMTC.totalBudget = totalBudget;

					System.out
							.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
									"Time", "TTask", "CTask", "TWorker",
									"SWorker", "W/T");
					
					
					long start = test.geocrowd.Utils.getCPUTime(); //start measuring time
					if(Geocrowd.algorithm == AlgorithmEnum.MAX_COVER_ADAPT_T_W
							|| Geocrowd.algorithm == AlgorithmEnum.MAX_COVER_BASIC_W_MO )
					onlineMTC.preBudgets=computeHistoryBudgets(false, totalBudget, next_time_period); 
					Geocrowd.workerList.clear();
					Geocrowd.taskList.clear();
					Geocrowd.assignedTasks.clear();
					OnlineMTC.randomBudget= null;
					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						
						int next_time = next_time_period + i;
						Geocrowd.workerList.clear();
//						onlineMTC.readWorkloadTasks(
//								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
//										+ next_time + ".txt", next_time_period);
//						onlineMTC.readWorkers(Utils
//								.datasetToWorkerPath(Geocrowd.DATA_SET)
//								+ next_time + ".txt");
//						ArrayList<GenericWorker> workers = onlineMTC.readWorkersWithLimit(Utils
//								.datasetToWorkerPath(Geocrowd.DATA_SET)
//								+ next_time + ".txt",i,numWorkers.get(i));
						Geocrowd.workerList.addAll(listworkers.get(i));
						Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 1000)));
						
//						if(g < 11)
//							Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 500)));
//						else if(g < 22)
//							Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 1000)));
//						else if(g < 33)
//							Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 1500)));
//						else 
//							Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 2000)));
						onlineMTC.TaskCount += tasksLists[i].size();
						onlineMTC.WorkerCount+=listworkers.get(i).size();

//						onlineMTC.matchingTasksWorkers();
						_g = g;
						onlineMTC.matchingTaskWorkers2();
						
					//	onlineMTC.printStatistics();
						
						onlineMTC.maxCoverage();
						OnlineMTC.TimeInstance++;
						

						System.out
								.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
										(i + 1),
										onlineMTC.TaskCount,
										OnlineMTC.TotalAssignedTasks,
										onlineMTC.totalBudget,
										OnlineMTC.TotalAssignedWorkers,
										OnlineMTC.TotalAssignedTasks
												/ Math.max(
														1,
														OnlineMTC.TotalAssignedWorkers));
					}
					
					
					
					//start = test.geocrowd.Utils.getCPUTime(); // requires java 1.5
			
					double elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
					
					//update time 
					runningTime[g%11] = (runningTime[g%11]*t+ elapsedTimeInSec)/(t+1);
					System.out.println("Algorithm "+algorithms[g%11]);
					System.out.println("Task size "+(1000*(1+g/11)));
					System.out.println("Running time = "+ runningTime[g%11] );
					

					coveredTasks[b][g] += OnlineMTC.TotalAssignedTasks;
					assignedWorkers[b][g] += OnlineMTC.TotalAssignedWorkers;
					printToFileWorkerCounts(filename);
					OnlineMTC.workerCounts.clear();

				}
//
//				coveredTasks[b][algorithms.length] += fixed_offline_cov;
//				coveredTasks[b][algorithms.length + 1] += dynamic_offline_cov;

			}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n################## Varying budget");
		pw.println("radius = " + GeocrowdSensingConstants.TASK_RADIUS);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int b = 0; b < budgets.length; b++) {
			pw.printf("\n%-20d \t", budgets[b]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				pw.printf("%-20d \t", (int) (coveredTasks[b][g2] / times));
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());

	}

	private static void printToFileWorkerCounts(String filename) throws IOException {
		FileWriter fw = new FileWriter(filename);
		BufferedWriter bfw = new BufferedWriter(fw);
		for(String workerId: OnlineMTC.workerCounts.keySet()){
			bfw.write(workerId +":"+OnlineMTC.workerCounts.get(workerId)+"\n");
		}
		bfw.close();
		
	}

	private static void workload_vary_delta(int[] delta, int totalBudget,
			double radius) throws IOException {

		GeocrowdSensingConstants.TASK_RADIUS = radius;
		System.out.println("\nRadius = " + GeocrowdSensingConstants.TASK_RADIUS);
		System.out.println("Budget = " + totalBudget);

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC,
//				AlgorithmEnum.MAX_COVER_ADAPT_B,
				AlgorithmEnum.MAX_COVER_BASIC_T,
//				AlgorithmEnum.MAX_COVER_BASIC_T2,
//				AlgorithmEnum.MAX_COVER_BASIC_S,
				AlgorithmEnum.MAX_COVER_BASIC_S2,
				// AlgorithmEnum.MAX_COVER_ADAPT_B_W,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
		// AlgorithmEnum.MAX_COVER_ADAPT_T_W
		};
		if(numWorkers==null)
		 numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.COSINE);
		int[][] coveredTasks = new int[delta.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[delta.length][algorithms.length + 2];
		int times = 1;
		for (int t = 0; t < times; t++) {
			int start_time = 0 + t * GeocrowdSensingConstants.TIME_INSTANCE;
			for (int d = 0; d < delta.length; d++) {
				GeocrowdSensingConstants.MAX_TASK_DURATION = delta[d];
				System.out.println("\n----\ndelta: "
						+ GeocrowdSensingConstants.MAX_TASK_DURATION);
				Geocrowd.TimeInstance = 0;
				// GeocrowdTest.main(null);
//
//				int[] counts = computeHistoryBudgets(false, totalBudget,
//						start_time);

				// GeocrowdTest.main(null); // generate set of tasks for next
				// period

				// apply offline method to next period
				int next_time_period = start_time
						+ t*GeocrowdSensingConstants.TIME_INSTANCE;
//				computeHistoryBudgets(true, totalBudget, next_time_period);
			//	int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
//				computeHistoryBudgets(false, totalBudget, next_time_period);
			//	int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;

				// use the same set of workers/tasks for all following period
				for (int g = 0; g < algorithms.length; g++) {
					Geocrowd.algorithm = algorithms[g];
					OnlineMTC onlineMTC = new OnlineMTC();
					onlineMTC.reset();
//					onlineMTC.preBudgets = counts;
					onlineMTC.totalBudget = totalBudget;

					System.out
							.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
									"Time", "TTask", "CTask", "TWorker",
									"SWorker", "W/T");
					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						//Geocrowd.taskList.clear();
						//remove expired tasks 
//						for(int tindex = Geocrowd.taskList.size() -1; tindex >=0; tindex-- ){
//							if(Geocrowd.taskList.get(tindex).getArrivalTime() + GeocrowdSensingConstants.MAX_TASK_DURATION
//									<=OnlineMTC.TimeInstance){
//								Geocrowd.taskList.remove(tindex);
//							}
//						}
					//	Geocrowd.taskList.clear();
						Geocrowd.workerList.clear();
						onlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ i + ".txt", 0);
//						onlineMTC.readWorkers(Utils
//								.datasetToWorkerPath(Geocrowd.DATA_SET)
//								+ next_time + ".txt");

						onlineMTC.readWorkersWithLimit(Utils
								.datasetToWorkerPath(Geocrowd.DATA_SET)
								+ next_time + ".txt",i,numWorkers.get(i));
						
						
						//onlineMTC.matchingTasksWorkers(); //luan test here
						onlineMTC.matchingTaskWorkers2();
						onlineMTC.maxCoverage();
						OnlineMTC.TimeInstance++;

						System.out
								.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
										(i + 1),
										onlineMTC.TaskCount,
										OnlineMTC.TotalAssignedTasks,
										onlineMTC.totalBudget,
										OnlineMTC.TotalAssignedWorkers,
										OnlineMTC.TotalAssignedTasks
												/ Math.max(
														1,
														OnlineMTC.TotalAssignedWorkers));
					}

					coveredTasks[d][g] += OnlineMTC.TotalAssignedTasks;
					assignedWorkers[d][g] += OnlineMTC.TotalAssignedWorkers;

				}

		//		coveredTasks[d][algorithms.length] += fixed_offline_cov;
				//coveredTasks[d][algorithms.length + 1] += dynamic_offline_cov;

			}
		}
		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n##################Varying delta");
		pw.println("Budget = " + totalBudget);
		pw.println("\nRadius = " + GeocrowdSensingConstants.TASK_RADIUS);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int d = 0; d < delta.length; d++) {
			pw.printf("\n%-20d \t", delta[d]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				pw.printf("%-20d \t", coveredTasks[d][g2] / times);
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	private static void workload_vary_radius(double[] radii, int totalBudget)
			throws IOException {

		System.out.println("Budget = " + totalBudget);
		if(numWorkers==null) numWorkers = ArrivalRateGenerator.generateCounts(28, 50, ArrivalRateEnum.COSINE);
		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				
				AlgorithmEnum.MAX_COVER_BASIC,
				AlgorithmEnum.MAX_COVER_ADAPT_B,
				AlgorithmEnum.MAX_COVER_ADAPT_T,
				AlgorithmEnum.MAX_COVER_ADAPT_T_W,
				
//				AlgorithmEnum.MAX_COVER_BASIC,
////				AlgorithmEnum.MAX_COVER_BASIC, 
////				AlgorithmEnum.MAX_COVER_BASIC, 
//				AlgorithmEnum.MAX_COVER_BASIC_T,
////				AlgorithmEnum.MAX_COVER_BASIC_S,
////				AlgorithmEnum.MAX_COVER_BASIC_S2,
//				 AlgorithmEnum.MAX_COVER_ADAPT_B,
//				// AlgorithmEnum.MAX_COVER_ADAPT_B_W,
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
//		 AlgorithmEnum.MAX_COVER_ADAPT_T_W
		};

		int[][] coveredTasks = new int[radii.length][algorithms.length + 2];
		double[][] coveredUtility = new double[radii.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[radii.length][algorithms.length + 2];
		int times = 25;
		for (int t = 0; t < times; t++) {
			int start_time = 0 + t * GeocrowdSensingConstants.TIME_INSTANCE;
			for (int d = 0; d < radii.length; d++) {
				GeocrowdSensingConstants.TASK_RADIUS = radii[d];
				System.out
						.println("\n----\nRadius " + GeocrowdSensingConstants.TASK_RADIUS);
				Geocrowd.TimeInstance = 0;
				// GeocrowdTest.main(null);

//				int[] counts = computeHistoryBudgets(false, totalBudget,
//						start_time);

				// GeocrowdTest.main(null); // generate set of tasks for next
				// period

				// apply offline method to next period
				int next_time_period = start_time
						+ t*GeocrowdSensingConstants.TIME_INSTANCE;
//				computeHistoryBudgets(true, totalBudget, next_time_period);
//				 int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
				 double fixed_offline_utility = Geocrowd.TotalCoveredUtility;
				 int[] preBudget = computeHistoryBudgets(false, totalBudget, next_time_period);
				int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;
				double dynamic_offline_utility = Geocrowd.TotalCoveredUtility;

				// use the same set of workers/tasks for all following period
				for (int g = 0; g < algorithms.length; g++) {
					
//					if(g == 0) Constants.UTILITY_FUNCTION = "const";
//					else if(g == 1) Constants.UTILITY_FUNCTION = "linear";
//					else if(g == 2) Constants.UTILITY_FUNCTION = "zipf";
					Geocrowd.algorithm = algorithms[g];
					OnlineMTC onlineMTC = new OnlineMTC();
					onlineMTC.reset();
//					onlineMTC.preBudgets = counts;
					onlineMTC.totalBudget = totalBudget;

					System.out
							.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
									"Time", "TTask", "CTask", "TWorker",
									"SWorker", "W/T");
					
					long start = test.geocrowd.Utils.getCPUTime(); //start measuring time
					Geocrowd.workerList.clear();
					Geocrowd.taskList.clear();
					Geocrowd.assignedTasks.clear();
					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
//						onlineMTC.readWorkloadTasks(
//								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
//										+ next_time + ".txt", next_time_period);
//						onlineMTC.readWorkers(Utils
//								.datasetToWorkerPath(Geocrowd.DATA_SET)
//								+ next_time + ".txt");
						
						Geocrowd.workerList.clear();
						onlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ i + ".txt", 0);
//						onlineMTC.readWorkers(Utils
//								.datasetToWorkerPath(Geocrowd.DATA_SET)
//								+ next_time + ".txt");

//						onlineMTC.readWorkersWithLimit(Utils
//								.datasetToWorkerPath(Geocrowd.DATA_SET)
//								+ next_time + ".txt",i,numWorkers.get(i));
						Geocrowd.workerList.addAll(listworkers.get(i));
						onlineMTC.WorkerCount+=listworkers.get(i).size();

						onlineMTC.matchingTaskWorkers2();
						onlineMTC.preBudgets = preBudget;
						onlineMTC.maxCoverage();
						OnlineMTC.TimeInstance++;

						System.out
								.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
										(i + 1),
										onlineMTC.TaskCount,
										OnlineMTC.TotalAssignedTasks,
										onlineMTC.totalBudget,
										OnlineMTC.TotalAssignedWorkers,
										OnlineMTC.TotalAssignedTasks
												/ Math.max(
														1,
														OnlineMTC.TotalAssignedWorkers));
					}
					
					
					double elapsedTimeInSec = (test.geocrowd.Utils.getCPUTime() - start) * 1.0 / 1000000000;
					System.out.println("Algorithm "+algorithms[g]);
					System.out.println("radius "+radii[d]);
					System.out.println("Running time = "+ elapsedTimeInSec);

					coveredTasks[d][g] += OnlineMTC.TotalAssignedTasks;
					coveredUtility[d][g] += OnlineMTC.TotalCoveredUtility;
					assignedWorkers[d][g] += OnlineMTC.TotalAssignedWorkers;

				}

				// coveredTasks[d][algorithms.length] += fixed_offline_cov;
				coveredTasks[d][algorithms.length + 1] += dynamic_offline_cov;

				// coveredUtility[d][algorithms.length] +=
				// fixed_offline_utility;
				coveredUtility[d][algorithms.length + 1] += dynamic_offline_utility;
			}
		}
		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n\n##################Varying radius");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		// pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int d = 0; d < radii.length; d++) {
			pw.printf("\n%-20f \t", radii[d]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				pw.printf("%-20d \t", coveredTasks[d][g2] / times);
			}
		}

		for (int d = 0; d < radii.length; d++) {
			pw.printf("\n%-20f \t", radii[d]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++) {
				pw.printf("%-20f \t", coveredUtility[d][g2] / times);
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}
	
	
	

	private static int[] computeHistoryBudgets(boolean isFixed, int budget,
			int start_time) {
		OfflineMTC offlineMTC = new OfflineMTC();
		offlineMTC.reset();
		OfflineMTC.taskList = new ArrayList<GenericTask>();

		offlineMTC.budget = budget;
		offlineMTC.isFixed = isFixed;
		
		Geocrowd.workerList.clear();

		for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
//			offlineMTC.readWorkloadTasks(
//					Utils.datasetToTaskPath(Geocrowd.DATA_SET)
//							+ (i + start_time) + ".txt", start_time);
//			offlineMTC.readWorkers(Utils.datasetToWorkerPath(Geocrowd.DATA_SET)
//					+ (i + start_time) + ".txt", i);
			Geocrowd.workerList.addAll(OnlineMTCTest.listworkers.get(i));
			
			if(_g < 11)
				Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 500)));
			else if(_g < 22)
				Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 1000)));
			else if(_g < 33)
				Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 1500)));
			else 
				Geocrowd.taskList.addAll(new ArrayList(tasksLists[i].subList(0, 2000)));
		}

//		offlineMTC.matchingTasksWorkers();
		offlineMTC.matchingTaskWorkers2();
		
		

		offlineMTC.maxTaskCoverage();
		System.out.println(isFixed + " " + start_time);
		System.out.println("\nCoverage : " + OfflineMTC.TotalAssignedTasks);
		System.out.println("Budget counts :");
		for (int count : offlineMTC.counts)
			System.out.print(count + "\t");
		System.out.println();
		
		
		//fill up counts if under utilize budget
		int sum = 0;
		for(int b: offlineMTC.counts)sum+=b;
		for(int i =0; i < offlineMTC.counts.length; i++){
			offlineMTC.counts[i]+= (budget - sum)/offlineMTC.counts.length;
		}
		return offlineMTC.counts;
	}
	
	
	@Test
	public void testRuntime() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				 AlgorithmEnum.MAX_COVER_ADAPT_T_W,

				// AlgorithmEnum.MAX_COVER_BASIC_T2,
//				AlgorithmEnum.MAX_COVER_BASIC, 
//				AlgorithmEnum.MAX_COVER_ADAPT_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T,
//		 AlgorithmEnum.MAX_COVER_ADAPT_T_W,
		// AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		double[] alphas = new double[] { 0.1 };
		int[][] coveredTasks = new int[alphas.length][algorithms.length + 2];
		double[][] coveredUtility = new double[alphas.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[alphas.length][algorithms.length + 2];

		double[][] elapsedTime = new double[alphas.length][algorithms.length + 2];

		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		int totalBudget = 56;
		GeocrowdSensingConstants.TASK_RADIUS = 5.0;
		GeocrowdSensingConstants.MAX_TASK_DURATION = 5;
		System.out.println("Radius = " + GeocrowdSensingConstants.TASK_RADIUS);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		// computeHistoryBudgets(true, totalBudget, 0);
		int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
		double fixed_offline_utility = Geocrowd.TotalCoveredUtility;
		long start = System.currentTimeMillis();
		computeHistoryBudgets(false, totalBudget, 0);
		long elapsedDynamic = System.currentTimeMillis() - start;
		int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;
		System.out.println(elapsedDynamic);
		double dynamic_offline_utility = Geocrowd.TotalCoveredUtility;

		for (int al = 0; al < alphas.length; al++) {
			for (int g = 0; g < algorithms.length; g++) {
				// update alpha for temporal, spatial algorithms.
				Constants.alpha = alphas[al];
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.createGrid();
				onlineMTC.readBoundary();
				onlineMTC.readEntropy();

				onlineMTC.totalBudget = totalBudget;
				// System.out
				// .printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
				// "Time", "TTask", "CTask", "TWorker", "SWorker",
				// "W/T");

				long elapsed_online = 0;
				for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
					long startTime = System.currentTimeMillis();
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					long start_online = System.currentTimeMillis();
					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					// computeHistoryBudgets(false, totalBudget, 0);
					elapsed_online = System.currentTimeMillis() - start_online;
					OnlineMTC.TimeInstance++;

					long elapsed = System.currentTimeMillis() - startTime;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers),
									elapsed);
				}

				coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
				coveredUtility[al][g] = OnlineMTC.TotalCoveredUtility;
				assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
				elapsedTime[al][g] = elapsed_online;
			}

			coveredTasks[al][algorithms.length] = fixed_offline_cov;
			coveredTasks[al][algorithms.length + 1] = fixed_offline_cov;

			coveredUtility[al][algorithms.length] = fixed_offline_utility;
			coveredUtility[al][algorithms.length + 1] = dynamic_offline_utility;
			elapsedTime[al][algorithms.length + 1] = elapsedDynamic;
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Runtime");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int al2 = 0; al2 < alphas.length; al2++) {
			pw.printf("\n%-20f \t", alphas[al2]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				pw.printf("%-20f \t", coveredUtility[al2][g2]);
		}

		pw.println("\n#Elapsed time");
		for (int al2 = 0; al2 < alphas.length; al2++) {
			pw.printf("\n%-20f \t", alphas[al2]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				pw.printf("%-20f \t", elapsedTime[al2][g2]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public void testLocalVaryAlpha() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SCALE;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				// AlgorithmEnum.MAX_COVER_ADAPT_T_W,

				// AlgorithmEnum.MAX_COVER_BASIC_T2,
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_ADAPT_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T,
		// AlgorithmEnum.MAX_COVER_ADAPT_T_W,
		// AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		double[] alphas = new double[] { 0.1 };
		int[][] coveredTasks = new int[alphas.length][algorithms.length + 2];
		double[][] coveredUtility = new double[alphas.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[alphas.length][algorithms.length + 2];

		double[][] elapsedTime = new double[alphas.length][algorithms.length + 2];

		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		int totalBudget = 56;
		GeocrowdSensingConstants.TASK_RADIUS = 5.0;
		GeocrowdSensingConstants.MAX_TASK_DURATION = 5;
		System.out.println("Radius = " + GeocrowdSensingConstants.TASK_RADIUS);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		// computeHistoryBudgets(true, totalBudget, 0);
		int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
		double fixed_offline_utility = Geocrowd.TotalCoveredUtility;
		long start = System.currentTimeMillis();
		// computeHistoryBudgets(false, totalBudget, 0);
		long elapsedDynamic = System.currentTimeMillis() - start;
		int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;
		double dynamic_offline_utility = Geocrowd.TotalCoveredUtility;

		for (int al = 0; al < alphas.length; al++) {
			for (int g = 0; g < algorithms.length; g++) {
				// update alpha for temporal, spatial algorithms.
				Constants.alpha = alphas[al];
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.createGrid();
				onlineMTC.readBoundary();
				onlineMTC.readEntropy();

				onlineMTC.totalBudget = totalBudget;
				// System.out
				// .printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
				// "Time", "TTask", "CTask", "TWorker", "SWorker",
				// "W/T");

				for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
					long startTime = System.currentTimeMillis();
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					long elapsed = System.currentTimeMillis() - startTime;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers),
									elapsed);
				}

				coveredTasks[al][g] = OnlineMTC.TotalAssignedTasks;
				coveredUtility[al][g] = OnlineMTC.TotalCoveredUtility;
				assignedWorkers[al][g] = OnlineMTC.TotalAssignedWorkers;
				elapsedTime[al][g] = 0;
			}

			coveredTasks[al][algorithms.length] = fixed_offline_cov;
			coveredTasks[al][algorithms.length + 1] = fixed_offline_cov;

			coveredUtility[al][algorithms.length] = fixed_offline_utility;
			coveredUtility[al][algorithms.length + 1] = dynamic_offline_utility;
			elapsedTime[al][algorithms.length + 1] = elapsedDynamic;
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying alpha");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int al2 = 0; al2 < alphas.length; al2++) {
			pw.printf("\n%-20f \t", alphas[al2]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				pw.printf("%-20f \t", coveredUtility[al2][g2]);
		}

		for (int al2 = 0; al2 < alphas.length; al2++) {
			pw.printf("\n%-20f \t", alphas[al2]);
			for (int g2 = 0; g2 < algorithms.length + 2; g2++)
				pw.printf("%-20f \t", elapsedTime[al2][g2]);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public void testLocalVaryBudget() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SCALE;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] { AlgorithmEnum.MAX_COVER_BASIC,
		 AlgorithmEnum.MAX_COVER_ADAPT_B,
		 AlgorithmEnum.MAX_COVER_ADAPT_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T2,
		// AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		// int[] budgets = new int[] { 24, 48, 96, 192, 384, 768, 1536, 3072 };
		// int[] budgets = new int[] { 28, 56, 112, 224, 448, 896, 1792, 3586 };
		int[] budgets = new int[] { 28, 56, 112, 224 };
		int[][] coveredTasks = new int[budgets.length][algorithms.length + 2];
		double[][] coveredUtility = new double[budgets.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[budgets.length][algorithms.length + 2];

		// int[] budgets = new int[] { 40, 80, 160, 320, 640, 1280,
		// 2560 };
		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		GeocrowdSensingConstants.TASK_RADIUS = 5.0;
		Constants.alpha = 0.2;
		System.out.println("Radius = " + GeocrowdSensingConstants.TASK_RADIUS);

		int times = 1;
		for (int t = 0; t < times; t++) {
			int next_time_period = 0 + t * GeocrowdSensingConstants.TIME_INSTANCE;
			for (int b = 0; b < budgets.length; b++) {
				// computeHistoryBudgets(true, budgets[b], 0);
				int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
				double fixed_offline_utility = Geocrowd.TotalCoveredUtility;

				long startTime = System.currentTimeMillis();
				computeHistoryBudgets(false, budgets[b], 0);
				long elapsed = System.currentTimeMillis() - startTime;

				System.out.println("xxx" + elapsed);
				int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;
				double dynamic_offline_utility = Geocrowd.TotalCoveredUtility;

				for (int a = 0; a < algorithms.length; a++) {
					// update alpha for temporal, spatial algorithms.
					Geocrowd.algorithm = algorithms[a];
					OnlineMTC onlineMTC = new OnlineMTC();
					onlineMTC.reset();
					onlineMTC.totalBudget = budgets[b];
					onlineMTC.createGrid();
					onlineMTC.readBoundary();

					// System.out
					// .printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
					// "Time", "TTask", "CTask", "TWorker",
					// "SWorker", "W/T");
					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						onlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						onlineMTC.readWorkers(Utils
								.datasetToWorkerPath(Geocrowd.DATA_SET)
								+ next_time + ".txt");

						// onlineMTC.readTasks(Utils
						// .datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
						// onlineMTC.readWorkers(Utils
						// .datasetToWorkerPath(Geocrowd.DATA_SET)
						// + i
						// + ".txt");

						onlineMTC.matchingTasksWorkers();
						onlineMTC.maxCoverage();
						OnlineMTC.TimeInstance++;

						// System.out
						// .printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
						// (i + 1),
						// onlineMTC.TaskCount,
						// OnlineMTC.TotalAssignedTasks,
						// onlineMTC.totalBudget,
						// OnlineMTC.TotalAssignedWorkers,
						// OnlineMTC.TotalAssignedTasks
						// / Math.max(
						// 1,
						// OnlineMTC.TotalAssignedWorkers));
					}

					coveredTasks[b][a] += OnlineMTC.TotalAssignedTasks;
					assignedWorkers[b][a] += OnlineMTC.TotalAssignedWorkers;
					coveredUtility[b][a] += OnlineMTC.TotalCoveredUtility;
				}

				coveredTasks[b][algorithms.length] = fixed_offline_cov;
				coveredTasks[b][algorithms.length + 1] = dynamic_offline_cov;

				coveredUtility[b][algorithms.length] = fixed_offline_utility;
				coveredUtility[b][algorithms.length + 1] = dynamic_offline_utility;
			}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying budget");
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int a = 0; a < algorithms.length; a++)
			pw.printf("%-20s \t", algorithms[a]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int b = 0; b < budgets.length; b++) {
			pw.printf("\n%-20d \t", budgets[b]);
			for (int a = 0; a < algorithms.length + 2; a++)
				pw.printf("%-20d \t", coveredTasks[b][a] / times);
		}

		for (int b = 0; b < budgets.length; b++) {
			pw.printf("\n%-20d \t", budgets[b]);
			for (int a = 0; a < algorithms.length + 2; a++)
				pw.printf("%-20d \t", coveredUtility[b][a] / times);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public void testLocalVaryDelta() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SCALE;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_ADAPT_B,
				AlgorithmEnum.MAX_COVER_ADAPT_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T2,
		// AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		int[] delta = new int[] { 1 };
		int[][] coveredTasks = new int[delta.length][algorithms.length + 2];
		double[][] coveredUtility = new double[delta.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[delta.length][algorithms.length + 2];

		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		int totalBudget = 56;
		double alpha = 0.2;
		System.out.println("Budget = " + totalBudget);

		int times = 1;
		for (int t = 0; t < times; t++) {
			int next_time_period = 0 + t * GeocrowdSensingConstants.TIME_INSTANCE;
			for (int d = 0; d < delta.length; d++) {
				GeocrowdSensingConstants.MAX_TASK_DURATION = delta[d];
				// computeHistoryBudgets(true, totalBudget, next_time_period);
				int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
				double fixed_offline_utility = Geocrowd.TotalCoveredUtility;
				// computeHistoryBudgets(false, totalBudget, next_time_period);
				int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;
				double dynamic_offline_utility = Geocrowd.TotalCoveredUtility;

				for (int g = 0; g < algorithms.length; g++) {
					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;
					Geocrowd.algorithm = algorithms[g];
					OnlineMTC onlineMTC = new OnlineMTC();
					onlineMTC.reset();
					onlineMTC.totalBudget = totalBudget;
					onlineMTC.createGrid();
					onlineMTC.readBoundary();

					// System.out
					// .printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
					// "Time", "TTask", "CTask", "TWorker",
					// "SWorker", "W/T");
					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						onlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						onlineMTC.readWorkers(Utils
								.datasetToWorkerPath(Geocrowd.DATA_SET)
								+ next_time + ".txt");

						// onlineMTC.readTasks(Utils
						// .datasetToTaskPath(Geocrowd.DATA_SET)
						// + i
						// + ".txt");
						// onlineMTC.readWorkers(Utils
						// .datasetToWorkerPath(Geocrowd.DATA_SET)
						// + i
						// + ".txt");

						onlineMTC.matchingTasksWorkers();
						onlineMTC.maxCoverage();
						OnlineMTC.TimeInstance++;

						// System.out
						// .printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
						// (i + 1),
						// onlineMTC.TaskCount,
						// OnlineMTC.TotalAssignedTasks,
						// onlineMTC.totalBudget,
						// OnlineMTC.TotalAssignedWorkers,
						// OnlineMTC.TotalAssignedTasks
						// / Math.max(
						// 1,
						// OnlineMTC.TotalAssignedWorkers));
					}

					coveredTasks[d][g] += OnlineMTC.TotalAssignedTasks;
					coveredUtility[d][g] += OnlineMTC.TotalCoveredUtility;
					assignedWorkers[d][g] += OnlineMTC.TotalAssignedWorkers;
				}
				coveredTasks[d][algorithms.length] = fixed_offline_cov;
				coveredTasks[d][algorithms.length + 1] = dynamic_offline_cov;

				coveredUtility[d][algorithms.length] = fixed_offline_utility;
				coveredUtility[d][algorithms.length + 1] = dynamic_offline_utility;
			}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying delta");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int a = 0; a < algorithms.length; a++)
			pw.printf("%-20s \t", algorithms[a]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int r = 0; r < delta.length; r++) {
			pw.printf("\n%-20d \t", delta[r]);
			for (int al = 0; al < algorithms.length + 2; al++)
				pw.printf("%-20d \t", coveredTasks[r][al] / times);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}
	
	@Test
	public void testLocalVaryRadius() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SCALE;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_ADAPT_B,
				AlgorithmEnum.MAX_COVER_ADAPT_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T,
		// AlgorithmEnum.MAX_COVER_BASIC_T2,
		// AlgorithmEnum.MAX_COVER_BASIC_S,
		// AlgorithmEnum.MAX_COVER_BASIC_S2
		};

		double[] radii = new double[] { 1.0, 5.0, 10.0};
		int[][] coveredTasks = new int[radii.length][algorithms.length + 2];
		double[][] coveredUtility = new double[radii.length][algorithms.length + 2];
		int[][] assignedWorkers = new int[radii.length][algorithms.length + 2];

		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		int totalBudget = 56;
		double alpha = 0.2;
		System.out.println("Budget = " + totalBudget);

		int times = 1;
		for (int t = 0; t < times; t++) {
			int next_time_period = 0 + t * GeocrowdSensingConstants.TIME_INSTANCE;
			for (int r = 0; r < radii.length; r++) {
				System.out.println(r);
				GeocrowdSensingConstants.TASK_RADIUS = radii[r];
				// computeHistoryBudgets(true, totalBudget, 0);
				int fixed_offline_cov = Geocrowd.TotalAssignedTasks;
				double fixed_offline_utility = Geocrowd.TotalCoveredUtility;
				// computeHistoryBudgets(false, totalBudget, 0);
				int dynamic_offline_cov = Geocrowd.TotalAssignedTasks;
				double dynamic_offline_utility = Geocrowd.TotalCoveredUtility;

				for (int g = 0; g < algorithms.length; g++) {
					// update alpha for temporal, spatial algorithms.
					Constants.alpha = alpha;
					Geocrowd.algorithm = algorithms[g];
					OnlineMTC onlineMTC = new OnlineMTC();
					onlineMTC.reset();
					onlineMTC.totalBudget = totalBudget;
					onlineMTC.createGrid();
					onlineMTC.readBoundary();

					// System.out
					// .printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
					// "Time", "TTask", "CTask", "TWorker",
					// "SWorker", "W/T");
					for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
						int next_time = next_time_period + i;
						onlineMTC.readWorkloadTasks(
								Utils.datasetToTaskPath(Geocrowd.DATA_SET)
										+ next_time + ".txt", next_time_period);
						onlineMTC.readWorkers(Utils
								.datasetToWorkerPath(Geocrowd.DATA_SET)
								+ next_time + ".txt");

						// onlineMTC.readTasks(Utils
						// .datasetToTaskPath(Geocrowd.DATA_SET)
						// + i
						// + ".txt");
						// onlineMTC.readWorkers(Utils
						// .datasetToWorkerPath(Geocrowd.DATA_SET)
						// + i
						// + ".txt");

						onlineMTC.matchingTasksWorkers();
						onlineMTC.maxCoverage();
						OnlineMTC.TimeInstance++;

						// System.out
						// .printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
						// (i + 1),
						// onlineMTC.TaskCount,
						// OnlineMTC.TotalAssignedTasks,
						// onlineMTC.totalBudget,
						// OnlineMTC.TotalAssignedWorkers,
						// OnlineMTC.TotalAssignedTasks
						// / Math.max(
						// 1,
						// OnlineMTC.TotalAssignedWorkers));
					}

					coveredTasks[r][g] += OnlineMTC.TotalAssignedTasks;
					coveredUtility[r][g] += OnlineMTC.TotalCoveredUtility;
					assignedWorkers[r][g] += OnlineMTC.TotalAssignedWorkers;
				}
				coveredTasks[r][algorithms.length] = fixed_offline_cov;
				coveredTasks[r][algorithms.length + 1] = dynamic_offline_cov;

				coveredUtility[r][algorithms.length] = fixed_offline_utility;
				coveredUtility[r][algorithms.length + 1] = dynamic_offline_utility;
			}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################Local varying radius");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int a = 0; a < algorithms.length; a++)
			pw.printf("%-20s \t", algorithms[a]);
		pw.printf("%-20s \t", "FixedOff");
		pw.printf("%-20s \t", "DynamicOff");
		for (int r = 0; r < radii.length; r++) {
			pw.printf("\n%-20f \t", radii[r]);
			for (int al = 0; al < algorithms.length + 2; al++)
				pw.printf("%-20d \t", coveredTasks[r][al] / times);
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public void varyAdaptThreshold() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.SKEWED;

		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
				AlgorithmEnum.MAX_COVER_BASIC, AlgorithmEnum.MAX_COVER_ADAPT_B };

		Double[] epsGains = new Double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7,
				0.8, 0.9 };
		Double[] epsBudgets = new Double[] { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7,
				0.8, 0.9 };
		// double[] epsGains = new double[] { 0.3, 0.5, 0.7 };
		int[][] coveredTasks = new int[epsGains.length][algorithms.length];
		int[][] assignedWorkers = new int[epsGains.length][algorithms.length];

		int totalBudget = 224;
		GeocrowdSensingConstants.TASK_RADIUS = 5.0;
		System.out.println("Radius = " + GeocrowdSensingConstants.TASK_RADIUS);
		System.out.println("Budget = " + totalBudget);

		// GeocrowdTest.main(null);

		for (int eps = 0; eps < epsGains.length; eps++) {
			for (int g = 0; g < algorithms.length; g++) {
				// update alpha for temporal, spatial algorithms.
				Geocrowd.algorithm = algorithms[g];
				OnlineMTC onlineMTC = new OnlineMTC();
				onlineMTC.reset();
				onlineMTC.epsGain = epsGains[eps];
				onlineMTC.epsBudget = epsBudgets[eps];
				onlineMTC.totalBudget = totalBudget;

				System.out
						.printf("\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
								"Time", "TTask", "CTask", "TWorker", "SWorker",
								"W/T");
				for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
					onlineMTC.readTasks(Utils
							.datasetToTaskPath(Geocrowd.DATA_SET) + i + ".txt");
					onlineMTC.readWorkers(Utils
							.datasetToWorkerPath(Geocrowd.DATA_SET)
							+ i
							+ ".txt");

					onlineMTC.matchingTasksWorkers();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;

					System.out
							.printf("\n%-10d \t %-10d \t %-10d \t %-10d \t %-10d \t %-10d",
									(i + 1),
									onlineMTC.TaskCount,
									OnlineMTC.TotalAssignedTasks,
									onlineMTC.totalBudget,
									OnlineMTC.TotalAssignedWorkers,
									OnlineMTC.TotalAssignedTasks
											/ Math.max(
													1,
													OnlineMTC.TotalAssignedWorkers));
				}

				coveredTasks[eps][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[eps][g] = OnlineMTC.TotalAssignedWorkers;
			}
		}

		/**
		 * print result
		 */
		StringWriter stringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(stringWriter);
		pw.println("\n##################");
		pw.println("Budget = " + totalBudget);
		pw.println("#Covered Tasks");
		pw.printf("%-20s \t", " ");
		for (int j2 = 0; j2 < algorithms.length; j2++)
			pw.printf("%-20s \t", algorithms[j2]);
		for (int eps = 0; eps < epsGains.length; eps++) {
			pw.printf("\n%-20f \t", epsGains[eps]);
			for (int g2 = 0; g2 < algorithms.length; g2++) {
				pw.printf("%-20d \t", coveredTasks[eps][g2]);
			}
		}

		logger.info(stringWriter.toString());
		System.out.println(stringWriter.toString());
	}

	@Test
	public static void testOverloading() throws IOException {
		Geocrowd.DATA_SET = DatasetEnum.GOWALLA;
		GeocrowdSensingConstants.TASK_RADIUS = 5.0;
		GeocrowdSensingConstants.TIME_INSTANCE = 28;
		int totalBudget = 488;
		int start_time = 0;
		 double[] alpha = new double[] { 0.0, 0.2, 0.4, 0.6, 0.8, 1.0 };
//		double[] alpha = new double[] { 0.1 };
		AlgorithmEnum[] algorithms = new AlgorithmEnum[] {
//				AlgorithmEnum.MAX_COVER_BASIC, 
//				AlgorithmEnum.MAX_COVER_BASIC_S
		 AlgorithmEnum.MAX_COVER_BASIC_MO,
//		 AlgorithmEnum.MAX_COVER_BASIC_W_MO
		};

		int[][] coveredTasks = new int[alpha.length][algorithms.length];
		int[][] maxAssignments = new int[alpha.length][algorithms.length];
		int[][] assignedWorkers = new int[alpha.length][algorithms.length];

		 int[] counts = computeHistoryBudgets(false, totalBudget, start_time);

		int next_time_period = start_time + GeocrowdSensingConstants.TIME_INSTANCE;

		System.out.printf(
				"\n\n%-10s \t %-10s \t %-10s \t %-10s \t %-10s \t %-10s",
				"Time", "TTask", "CTask", "TWorker", "SWorker", "W/T");
		
		
		for (int d = 0; d < alpha.length; d++) {
			Constants.alpha = alpha[d];
			for (int g = 0; g < algorithms.length; g++) {
				OnlineMTC onlineMTC = new OnlineMTC();
				Geocrowd.algorithm = algorithms[g];
				onlineMTC.reset();

				 onlineMTC.preBudgets = counts;

				if (Geocrowd.algorithm == AlgorithmEnum.MAX_COVER_BASIC
						&& d > 0)
					continue;

				/**
				 * clear worker, task list
				 */
				OnlineMTC.taskList.clear();
				OnlineMTC.workerList.clear();
				
				Geocrowd.workerList.clear();
				Geocrowd.taskList.clear();
				Geocrowd.assignedTasks.clear();
				onlineMTC.totalBudget = totalBudget;
				for (int i = 0; i < GeocrowdSensingConstants.TIME_INSTANCE; i++) {
					Geocrowd.workerList.clear();
					int next_time = next_time_period + i;
//					onlineMTC.readWorkloadTasks(
//							Utils.datasetToTaskPath(Geocrowd.DATA_SET)
//									+ next_time + ".txt", next_time_period);
//					onlineMTC.readWorkers(Utils
//							.datasetToWorkerPath(Geocrowd.DATA_SET)
//							+ next_time
//							+ ".txt");
					
					
					Geocrowd.workerList.addAll(listworkers.get(i));
					Geocrowd.taskList.addAll(tasksLists[i]);
					onlineMTC.TaskCount += tasksLists[i].size();
					onlineMTC.WorkerCount+=listworkers.get(i).size();
					
					
					onlineMTC.matchingTaskWorkers2();
					onlineMTC.maxCoverage();
					OnlineMTC.TimeInstance++;
				}
				System.out.println("##################");

				System.out.printf("\n%-15s %-15s %-15s %-15s %-15s",
						"TotalTask", "CoveredTask", "TotalWorker",
						"SelectedWorker", "W/T");

				System.out.printf(
						"\n%-15d %-15d %-15d %-15d %-15d",
						onlineMTC.TaskCount,
						OnlineMTC.TotalAssignedTasks,
						onlineMTC.totalBudget,
						OnlineMTC.TotalAssignedWorkers,
						OnlineMTC.TotalAssignedTasks
								/ Math.max(1, OnlineMTC.TotalAssignedWorkers));

				maxAssignments[d][g] = onlineMTC.printWorkerCounts();

				coveredTasks[d][g] = OnlineMTC.TotalAssignedTasks;
				assignedWorkers[d][g] = OnlineMTC.TotalAssignedWorkers;
				
				String filename = algorithms[g].toString()+totalBudget+"_"+Constants.alpha+".txt";
				printToFileWorkerCounts(filename);
				OnlineMTC.workerCounts.clear();
			}

			/**
			 * print result
			 */
			StringWriter stringWriter = new StringWriter();
			PrintWriter pw = new PrintWriter(stringWriter);
			pw.println("\n\n##################Overloading");
			pw.println("Budget = " + totalBudget);
			pw.println("#Covered Tasks");
			pw.printf("%-20s \t", " ");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				pw.printf("%-20s \t", algorithms[j2]);
			for (int a = 0; a < alpha.length; a++) {
				pw.printf("\n%-20f \t", alpha[a]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					pw.printf("%-20d \t", coveredTasks[d][g2]);
				}
			}

			pw.printf("\n");
			for (int j2 = 0; j2 < algorithms.length; j2++)
				pw.printf("%-20s \t", algorithms[j2]);
			for (int a = 0; a < alpha.length; a++) {
				pw.printf("\n%-20f \t", alpha[a]);
				for (int g2 = 0; g2 < algorithms.length; g2++) {
					pw.printf("%-20d \t", maxAssignments[d][g2]);
				}
			}

			logger.info(stringWriter.toString());
			System.out.println(stringWriter.toString());

		}
	}

	@Test
	public void testOnlineMTC()
		throws Exception {
		OnlineMTC onlineMTC = new OnlineMTC();
	}
}