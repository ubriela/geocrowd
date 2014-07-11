/*******************************************************************************
 * @ Year 2013
 * This is the source code of the following papers. 
 * 
 * 1) Geocrowd: A Server-Assigned Crowdsourcing Framework. Hien To, Leyla Kazemi, Cyrus Shahabi.
 * 
 * 
 * Please contact the author Hien To, ubriela@gmail.com if you have any question.
 *
 * Contributors:
 * Hien To - initial implementation
 *******************************************************************************/
package cplex;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geocrowd.common.MatchPair;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.CplexStatus;
import ilog.cplex.IloCplexModeler;

// TODO: Auto-generated Javadoc
/**
 * The Class BPMatchingCplex.
 */
public class BPMatchingCplex {

	/** The num cols. */
	private int numCols = 0;

	/** The num rows. */
	private int numRows = 0;

	/** The num workers. */
	private int numWorkers = 0;

	/** The num tasks. */
	private int numTasks = 0;

	/** The cplex. */
	private IloCplex cplex;

	/** The objective coeff. */
	private List<Double> objectiveCoeff;

	/** The matching coeff. */
	private List<Double> matchingCoeff;

	/** The map col to match. */
	private HashMap<Integer, MatchPair> mapColToMatch;

	/** The max matching. */
	private double maxMatching = 0;

	/**
	 * Instantiates a new BP matching cplex.
	 * 
	 * @param numWorker
	 *            the num worker
	 * @param numTask
	 *            the num task
	 * @param objCoeff
	 *            the obj coeff
	 * @param matchCoeff
	 *            the match coeff
	 * @param maximumMatching
	 *            the maximum matching
	 * @param map
	 *            the map
	 */
	public BPMatchingCplex(int numWorker, int numTask, List<Double> objCoeff,
			List<Double> matchCoeff, double maximumMatching,
			HashMap<Integer, MatchPair> map) {
		this.cplex = null;

		this.numWorkers = numWorker;
		this.numTasks = numTask;
		this.objectiveCoeff = objCoeff;
		this.matchingCoeff = matchCoeff;
		this.maxMatching = maximumMatching;
		this.mapColToMatch = map;

		this.initialize();
	}

	/**
	 * Gets the MIP stats.
	 * 
	 * @return the MIP stats
	 */
	private int getMIPStats() {
		try {
			return this.cplex.getNMIPStarts();
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("MIP stats could not be obtained");
		}
	}

	/**
	 * Returns the number of iterations in the last solve.
	 * 
	 * @return the number iterations
	 */
	private int getNumberIterations() {
		return this.cplex.getNiterations();
	}

	/**
	 * Returns the number of phrase I simplex iterations from the last solve.
	 * 
	 * @return the simplex iteration
	 */
	private int getSimplexIteration() {
		return this.cplex.getNphaseOneIterations();
	}

	/**
	 * Gets the solve status.
	 * 
	 * @return the solve status
	 */
	private STATUS_TYPE getSolveStatus() {
		CplexStatus cplexStat;
		try {
			cplexStat = this.cplex.getCplexStatus();
			if (cplexStat == CplexStatus.Optimal)
				return STATUS_TYPE.OPTIMAL;
			else if (cplexStat == CplexStatus.Infeasible)
				return STATUS_TYPE.INFEASIBLE;
			else if (cplexStat == CplexStatus.Unbounded)
				return STATUS_TYPE.UNBOUNDED;
			else if (cplexStat == CplexStatus.Unknown) {
				return STATUS_TYPE.UNKNOWN;
			} else {
				System.out.println("Cplex status: " + cplex.getCplexStatus());
				return STATUS_TYPE.UNKNOWN;
			}
		} catch (IloException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to get cplex status");
		}

	}

	/**
	 * Import cplex model from file.
	 * 
	 * @param fileName
	 *            the file name
	 */
	private void importFile(String fileName) {
		try {
			this.cplex = new IloCplex();
			cplex.importModel(fileName);
		} catch (IloException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Error reading file");
		}
	}

	/**
	 * Initialize.
	 */
	private void initialize() {
		try {
			if (cplex != null) {
				cplex.end();
			}
			cplex = new IloCplex();
			cplex.setName("BPMatchingCplex");

			this.redirectOutput(null);
		} catch (IloException e) {
			e.printStackTrace();

			throw new RuntimeException(e.getMessage());
		}
		numCols = objectiveCoeff.size();
		numRows = numWorkers + numTasks + 1;
	}

	/**
	 * Max matching min cost.
	 * 
	 * @return assigned task
	 */
	public ArrayList<MatchPair> maxMatchingMinCost() {
		double[] vars = solve();
		ArrayList<MatchPair> assignment = new ArrayList<MatchPair>();
		if (vars != null)
			for (int i = 0; i < vars.length; i++) {
				if (vars[i] == 1.0) {
					if (mapColToMatch.containsKey(i)) {
						MatchPair pair = mapColToMatch.get(i);
						assignment.add(pair);
					}
				}
			}
		else {
			getNumberIterations();
			System.out.println("MIP stats: " + getMIPStats());
			System.out.println("Status: " + getSolveStatus());
		}
		return assignment;
	}

	/**
	 * Populated by col.
	 * 
	 * @param model
	 *            the model
	 * @param var
	 *            the var
	 * @param rng
	 *            the rng
	 * @throws IloException
	 *             the ilo exception
	 */
	private void populatedByCol(IloCplexModeler model, IloNumVar[][] var,
			IloRange[][] rng) throws IloException {

		// objective type
		IloObjective obj = model.addMinimize();

		// unknown edge values
		IloNumVar[] x = model.boolVarArray(numCols);
		var[0] = x;

		// inequality constraints
		rng[0] = new IloRange[numRows];
		for (int i = 0; i < numRows - 1; i++) {
			rng[0][i] = model.addRange(0, 1);
		}

		rng[0][numRows - 1] = model.addRange(maxMatching, maxMatching);

		int w = 0; // worker var
		int t = 0; // task var
		// row matrix
		for (int i = 0; i < numCols; i++) {
			// compute rw and rt
			IloRange rw = null;
			IloRange rt = null;
			if (mapColToMatch.containsKey(i)) {
				MatchPair pair = mapColToMatch.get(i);
				w = pair.getW();
				t = pair.getT();
				rw = rng[0][w];
				rt = rng[0][t + numWorkers];
			} else {
				System.out.println("This map should contain all columns!");
			}

			// each column
			var[0][i] = model.boolVar(model.column(obj, objectiveCoeff.get(i))
					.and(model.column(rw, 1).and(
							model.column(rt, 1).and(
									model.column(rng[0][numRows - 1],
											matchingCoeff.get(i))))));
		}
	}

	/**
	 * Redirect output.
	 * 
	 * @param stream
	 *            can be <code>null</code> if no output should be provided.
	 */
	private void redirectOutput(OutputStream stream) {
		cplex.setOut(stream);
		cplex.setWarning(stream);
	}

	/**
	 * return which edge is assigned.
	 * 
	 * @return the double[]
	 */
	private double[] solve() {
		double[] vars = null;
		try {
			IloCplex cplex = new IloCplex();

			IloNumVar[][] var = new IloNumVar[1][];
			IloRange[][] rng = new IloRange[1][];

			populatedByCol(cplex, var, rng);

			if (cplex.solve()) {
				// cplex.setParam(IloCplex.DoubleParam.TiLim, 1);
				vars = cplex.getValues(var[0]);
				// double[] slack = cplex.getSlacks(rng[0]);

				System.out.println("Solution status: " + cplex.getStatus());
				System.out.println("Solution value: " + cplex.getObjValue());

				// for (int i = 0; i < vars.length; i++) {
				// System.out.println("Variable " + i + " Value = " + vars[i]);
				// }
			}

			cplex.exportModel("mipex.lp");
			cplex.end();
		} catch (IloException e) {
			System.err.println("Exception " + e);
		}
		return vars;
	}

}
