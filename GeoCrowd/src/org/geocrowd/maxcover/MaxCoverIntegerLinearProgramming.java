package org.geocrowd.maxcover;

import java.util.ArrayList;
import java.util.HashMap;

import org.geocrowd.Geocrowd;
import org.geocrowd.datasets.params.GeocrowdConstants;
import org.geocrowd.datasets.params.GeocrowdSensingConstants;

import net.sf.javailp.Constraint;
import net.sf.javailp.Linear;
import net.sf.javailp.OptType;
import net.sf.javailp.Problem;
import net.sf.javailp.Result;
import net.sf.javailp.Solver;
import net.sf.javailp.SolverFactoryGLPK;
import scpsolver.lpsolver.LinearProgramSolver;
import scpsolver.lpsolver.SolverFactory;
import scpsolver.problems.LPSolution;
import scpsolver.problems.LPWizard;
import scpsolver.problems.LPWizardConstraint;
import scpsolver.problems.LinearProgram;


public class MaxCoverIntegerLinearProgramming {
	public ArrayList<HashMap<Integer, Integer>> workerContainer;
	public HashMap<Integer, ArrayList<Integer>> taskContainer;
	public int budget;
	
	@SuppressWarnings({ "unchecked" })
	public  MaxCoverIntegerLinearProgramming(ArrayList<HashMap<Integer, Integer>> _workerContainer, HashMap<Integer, 
			ArrayList<Integer>> _taskContainer, int _budget){
		
		//create y variables for task
		this.workerContainer = (ArrayList<HashMap<Integer, Integer>>) _workerContainer.clone();
		this.taskContainer = (HashMap<Integer, ArrayList<Integer>>) _taskContainer.clone();
		this.budget = _budget;
		
	}
	
	
	public void runIntegerLinearProgrammingFixedBudgeILPSolver(){
		
		int[] budgetPerInstance = new int[GeocrowdConstants.TIME_INSTANCE];
		for (int i = 0; i < budgetPerInstance.length - 1; i++) {
			budgetPerInstance[i] = budget / GeocrowdConstants.TIME_INSTANCE;
		}
		budgetPerInstance[budgetPerInstance.length - 1] = budget - budget
				/ GeocrowdConstants.TIME_INSTANCE
				* (GeocrowdConstants.TIME_INSTANCE - 1);
		
		//find workers in a time instances
		HashMap<Integer, ArrayList<Integer>> workersInTimeInstances = new HashMap<>();
		for(Integer i = 0; i < workerContainer.size(); i++){
			int timeArrival = Geocrowd.workerList.get(i).getOnlineTime();
			if(workersInTimeInstances.get(timeArrival)!=null){
				ArrayList<Integer> workers = workersInTimeInstances.get(timeArrival);
				workers.add(i);
				
			}
			else{
				ArrayList<Integer> workers = new ArrayList<>();
				workers.add(i);
				workersInTimeInstances.put(timeArrival, workers);
			}
		}
		
		SolverFactoryGLPK factory = new SolverFactoryGLPK(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 1); 
		factory.setParameter(Solver.TIMEOUT, 5000); // set timeout to 100 seconds
	
		Problem problem = new Problem();
		Linear  linear = new Linear();
		//objective
		for(Integer tid: taskContainer.keySet()){
			linear.add(1,"y"+tid);
		}
		problem.setObjective( linear, OptType.MAX);
		
		//constraints
		//create x variables for workers, constraints sum(x_i) <= B
		linear = new Linear();
		for(Integer i = 0; i < workerContainer.size(); i++){
			linear.add(1,"x"+i);;
			
		}
		problem.add(new Constraint("budget",linear,"<=", budget));
		//cover constraint
		for(int tid:taskContainer.keySet()){
			linear = new Linear();
			ArrayList<Integer> workersId = taskContainer.get(tid);
			for(Integer workerId: workersId){
				linear.add(1,"x"+workerId);
			}
			linear.add(-1,"y"+tid);
			problem.add(new Constraint("cover"+tid,linear, ">=",0));
		}
		//x_i, y_i in range 0,1
		for(int tid:taskContainer.keySet()){
			linear = new Linear();
			linear.add(1, "y"+tid);
			problem.add(new Constraint("ge0"+tid,linear, ">=",0));
			
			linear = new Linear();
			linear.add(1, "y"+tid);
			problem.add(new Constraint("le1"+tid,linear, "<=",1));
			problem.setVarType("y"+tid, Integer.class);
		}
		
		for(Integer i = 0; i < workerContainer.size(); i++){
			linear = new Linear();
			linear.add(1,"x"+i);
			problem.add(new Constraint("xge0"+i,linear, ">=", 0));
			
			linear = new Linear();
			linear.add(1,"x"+i);
			problem.add(new Constraint("xle1",linear, "<=", 1));
			
			problem.setVarType("x"+i, Integer.class);
			
		}
		
		//budget constraints
		for(Integer timeInstance: workersInTimeInstances.keySet()){
			linear = new Linear();
			for(Integer workerId: workersInTimeInstances.get(timeInstance)){
				linear.add(1,"x"+workerId);
			}
			
			problem.add(new Constraint("fixbudget"+timeInstance,linear, "<=", 
					budgetPerInstance[timeInstance%GeocrowdSensingConstants.TIME_INSTANCE]));
			
		}
		
		Solver solver = factory.get(); // you should use this solver only once for one problem
		Result result = solver.solve(problem);
		if(result!=null)
		System.out.println(result.getObjective());
		else {
			System.out.println("Time out!");
		}
		
		
	}
	
	public void runIntegerLinearProgrammingILPSolver(){
		SolverFactoryGLPK factory = new SolverFactoryGLPK(); // use lp_solve
		factory.setParameter(Solver.VERBOSE, 0); 
		factory.setParameter(Solver.TIMEOUT, 5000); // set timeout to 100 seconds
	
		Problem problem = new Problem();
		Linear  linear = new Linear();
		//objective
		for(Integer tid: taskContainer.keySet()){
			linear.add(1,"y"+tid);
		}
		problem.setObjective( linear, OptType.MAX);
		
		//constraints
		//create x variables for workers, constraints sum(x_i) <= B
		linear = new Linear();
		for(Integer i = 0; i < workerContainer.size(); i++){
			linear.add(1,"x"+i);;
			
		}
		problem.add(new Constraint("budget",linear,"<=", budget));
		//cover constraint
		for(int tid:taskContainer.keySet()){
			linear = new Linear();
			ArrayList<Integer> workersId = taskContainer.get(tid);
			for(Integer workerId: workersId){
				linear.add(1,"x"+workerId);
			}
			linear.add(-1,"y"+tid);
			problem.add(new Constraint("cover"+tid,linear, ">=",0));
		}
		//x_i, y_i in range 0,1
		for(int tid:taskContainer.keySet()){
			linear = new Linear();
			linear.add(1, "y"+tid);
			problem.add(new Constraint("ge0"+tid,linear, ">=",0));
			
			linear = new Linear();
			linear.add(1, "y"+tid);
			problem.add(new Constraint("le1"+tid,linear, "<=",1));
			problem.setVarType("y"+tid, Integer.class);
		}
		
		for(Integer i = 0; i < workerContainer.size(); i++){
			linear = new Linear();
			linear.add(1,"x"+i);
			problem.add(new Constraint("xge0"+i,linear, ">=", 0));
			
			linear = new Linear();
			linear.add(1,"x"+i);
			problem.add(new Constraint("xle1",linear, "<=", 1));
			
			problem.setVarType("x"+i, Integer.class);
			
		}
		
		Solver solver = factory.get(); // you should use this solver only once for one problem
		Result result = solver.solve(problem);

		if(result!=null)
		System.out.println(result.getObjective());
		else {
			System.out.println("Time out!");
		}
	//	System.out.println(result.toString());
				
	
	
	
	
	
	
	
	
	}
	
	public void runIntegerLinearProgramming(){
		
		LPWizard linear = new LPWizard();
		
		
		//create y varibales for tasks
		for(Integer tid: taskContainer.keySet()){
			linear.plus( "y"+tid,1);
		}
		
		//create x variables for workers, constraints sum(x_i) <= B
		LPWizardConstraint c1 = linear.addConstraint("c1",budget,">=");
		for(Integer i = 0; i < workerContainer.size(); i++){
			c1.plus("x"+i,1);
			
		}
		c1.setAllVariablesBoolean();

		
		//cover constraint
		
		for(int tid:taskContainer.keySet()){
			LPWizardConstraint c  = linear.addConstraint("cc"+tid, 0, "<=");
			ArrayList<Integer> workersId = taskContainer.get(tid);
			for(Integer workerId: workersId){
				c.plus("x"+workerId,1);
			}
			c.plus( "y"+tid,-1);
			c.setAllVariablesBoolean();

		}
		
		//x_i, y_i in range 0,1
		
		for(int i=0;i < workerContainer.size(); i++){
			LPWizardConstraint c  = linear.addConstraint("ccc"+i, 1, ">=");
			c.plus("x"+i,1);
			c.setAllVariablesBoolean();
			c  = linear.addConstraint("cccc"+i, 0, "<=");
			c.plus("x"+i,1);
			c.setAllVariablesBoolean();
		}
		for(int tid:taskContainer.keySet()){
			LPWizardConstraint c  = linear.addConstraint("ccccc"+tid, 1, ">=");
			c.plus("y"+tid,1);
			c.setAllVariablesBoolean();
			c  = linear.addConstraint("cccccd"+tid, 0, "<=");
			c.plus("y"+tid,1);
			c.setAllVariablesBoolean();
		}
		
		linear.setMinProblem(false);
		
		
		
		LPSolution sol = linear.solve();
		System.out.println("Objective value = "+sol.getObjectiveValue());
	}

}
