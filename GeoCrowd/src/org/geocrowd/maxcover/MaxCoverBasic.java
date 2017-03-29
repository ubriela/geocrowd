/**
 * *****************************************************************************
 * @ Year 2013 This is the source code of the following papers.
 * 
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla
 * Kazemi, Cyrus Shahabi.
 * 
 *
 * Please contact the author Hien To, ubriela@gmail.com if you have any
 * question.
 * 
 * Contributors: Hien To - initial implementation
 ******************************************************************************
 */
package org.geocrowd.maxcover;

import static org.geocrowd.Geocrowd.candidateTaskIndices;
import static org.geocrowd.Geocrowd.taskList;
import static org.geocrowd.Geocrowd.workerList;
import static org.geocrowd.Geocrowd.tasksMap;

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

import org.geocrowd.Constants;
import org.geocrowd.Geocrowd;
import org.geocrowd.GeocrowdTaskUtility;
import org.geocrowd.OnlineMTC;
import org.geocrowd.common.crowd.GenericWorker;
import org.geocrowd.common.crowd.SensingTask;
import org.geocrowd.common.crowd.SensingWorker;
import org.geocrowd.common.utils.Utils;
import org.geocrowd.datasets.params.GeocrowdConstants;

import net.sf.javailp.Constraint;

/**
 * The Class SetCoverGreedy.
 * 
 * The budget k is fixed, algorithm stops when running out of budget
 * 
 */
public class MaxCoverBasic extends MaxCover {

	public MaxCoverBasic() {
		super();
	}

	public MaxCoverBasic(ArrayList container, Integer currentTI) {
		super(container, currentTI);
	}

	public void printGain(double gain) throws UnsupportedEncodingException, FileNotFoundException, IOException{


		try(FileWriter fw = new FileWriter(Geocrowd.TimeInstance +"gainPerWorker.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			   out.print(gain+",");
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
		
		
	
	}
	/**
	 * Greedy algorithm.
	 * 
	 * @return the assigned workers
	 */
	@Override
	public HashSet<Integer> maxCover() {
		HashMap<Integer, HashMap<Integer, Integer>> S = (HashMap<Integer, HashMap<Integer, Integer>>) mapSets
				.clone();
	//	System.out.println("S size = " + S.size());
		/**
		 * Q is the universe of tasks
		 */
		HashSet<Integer> Q = (HashSet<Integer>) universe.clone();

		
		//
		ArrayList<Integer> tasksPerWorkers = new ArrayList<>();
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
	              new FileOutputStream(Geocrowd.TimeInstance +"tasksPerWorker.txt"), "utf-8"))) {
			
			for(HashMap<Integer, Integer> taskWithDeadline: S.values()){
				tasksPerWorkers.add(taskWithDeadline.size());
			}
			Collections.sort(tasksPerWorkers);
			for(int i = 0; i < tasksPerWorkers.size(); i++){
				writer.write(tasksPerWorkers.get(i)+",");
			}
			
				
		}

		 catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		
		
		
		
		//
		/**
		 * Run until either running out of budget or no more tasks to cover
		 */
		while (assignWorkers.size() < budget && !Q.isEmpty()) {
			int bestWorkerIndex = -1; // track index of the best worker in S
			double maxUncoveredUtility = 0.0;
			/**
			 * Iterate all workers, find the one which covers maximum number of
			 * uncovered tasks
			 */
			for (int k : S.keySet()) {
				GenericWorker w = workerList.get(k);
				
				HashMap<Integer, Integer> s = S.get(k); // task set covered by
														// current worker
				double uncoveredUtility = 0.0;
				for (Integer i : s.keySet()) {
					if (!assignedTaskSet.contains(i)) {
//						SensingTask t = (SensingTask) taskList
//								.get(candidateTaskIndices.get(i)); //luan test here
						SensingTask t = (SensingTask) tasksMap.get(i);
						double utility = GeocrowdTaskUtility.utility(Geocrowd.DATA_SET, w, t);
						
						//	System.out.println(utility);
						uncoveredUtility += utility;
//						uncoveredUtility++;
					}
				}
				
				
				//using worker overloading
				
				if(Constants.workerOverload) {
					int count = 0;
					if(OnlineMTC.workerCounts.containsKey(w.getId())) count = OnlineMTC.workerCounts.get(w.getId());
					
					uncoveredUtility = uncoveredUtility *(1-Constants.theta)/taskList.size() - count*Constants.theta/GeocrowdConstants.TIME_INSTANCE;
				}
				
				
				if (uncoveredUtility > maxUncoveredUtility) {
					maxUncoveredUtility = uncoveredUtility;
					bestWorkerIndex = k;
					
				}
			}

			// System.out.print(S.get(bestWorkerIndex));
			// System.out.println(maxNoUncoveredTasks);
			if (bestWorkerIndex > -1) {
				/**
				 * gain is reduced at every stage
				 */
				gain = maxUncoveredUtility;
				assignedUtility += gain;
				
				//
				try {
//					boolean check= false;
//					for(HashMap h: S.values()){
//						if(h.size() >= gain) {
//							check = true;
//						}
//					}
//					if(!check){
//						System.out.println("dsfsfsf");
//					}
					
					printGain(gain);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//
				assignWorkers.add(bestWorkerIndex);
				HashMap<Integer, Integer> taskSet = S.get(bestWorkerIndex);
				S.remove(bestWorkerIndex);
				Q.removeAll(taskSet.keySet());

				/**
				 * compute average time to assign tasks in taskSet
				 */
				for (Integer taskidx : taskSet.keySet())
					if (!assignedTaskSet.contains(taskidx)) {

						averageDelayTime += currentTimeInstance
								- (taskSet.get(taskidx) - GeocrowdConstants.MAX_TASK_DURATION)
								+ 1;
						assignedTaskSet.add(taskidx);
					}
			}
			else break;
		}

		assignedTasks = assignedTaskSet.size();
//		System.out.println(universe.size() + "\t" + assignedTasks + "\t"
//				+ assignWorkers.size() + "\t" + assignedTasks
//				/ assignWorkers.size());
		return assignWorkers;
	}

	
}
