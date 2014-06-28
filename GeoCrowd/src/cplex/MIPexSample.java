package cplex;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.concert.IloObjective;
import ilog.concert.IloRange;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplexModeler;

public class MIPexSample {
	public static void main(String[] args) {
		try {
			IloCplex cplex = new IloCplex();

			IloNumVar[][] var = new IloNumVar[1][];
			IloRange[][] rng = new IloRange[1][];

			populatedByCol(cplex, var, rng);

			if (cplex.solve()) {
				double[] x = cplex.getValues(var[0]);
				double[] slack = cplex.getSlacks(rng[0]);

				System.out.println("Solution status: " + cplex.getStatus());
				System.out.println("Solution value: " + cplex.getObjValue());

				for (int i = 0; i < x.length; i++) {
					System.out.println("Variable " + i + " Value = " + x[i]);
				}
			}

			cplex.exportModel("mipex.lp");
			cplex.end();
		} catch (IloException e) {
			System.err.println("Exception " + e);
		}
	}

	private static void populateByRow(IloCplexModeler model, IloNumVar[][] var,
			IloRange[][] rng) throws IloException {

		// unknown
		IloNumVar[] x = model.boolVarArray(5);
		var[0] = x;

		// coefficient
		int[] coeffvals = { 1, 1, 1, 1, 2 };

		// objective function
		model.addMinimize(model.scalProd(x, coeffvals));

		// inequality constraints
		rng[0] = new IloRange[7];
		rng[0][0] = model.addLe(x[0], 1);
		rng[0][1] = model.addLe(
				model.sum(model.prod(1, x[1]), model.prod(1, x[2]),
						model.prod(1, x[3])), 1);
		rng[0][2] = model.addLe(x[4], 1);
		rng[0][3] = model.addLe(x[1], 1);
		rng[0][4] = model.addLe(
				model.sum(model.prod(1, x[0]), model.prod(1, x[2]),
						model.prod(1, x[4])), 1);
		rng[0][5] = model.addLe(x[3], 1);

		// maximum matching constraint
		rng[0][6] = model.addEq(
				model.sum(model.prod(2, x[0]), model.prod(2, x[1]),
						model.prod(3, x[2]), model.prod(2, x[3]),
						model.prod(2, x[4])), 4);
	}

	private static void populatedByCol(IloCplexModeler model,
			IloNumVar[][] var, IloRange[][] rng) throws IloException {

		// objective type
		IloObjective obj = model.addMinimize();

		// unknown
		IloNumVar[] x = model.boolVarArray(5);
		var[0] = x;

		// inequality constraints
		rng[0] = new IloRange[7];
		rng[0][0] = model.addRange(0, 1);
		rng[0][1] = model.addRange(0, 1);
		rng[0][2] = model.addRange(0, 1);
		rng[0][3] = model.addRange(0, 1);
		rng[0][4] = model.addRange(0, 1);
		rng[0][5] = model.addRange(0, 1);
		rng[0][6] = model.addRange(4, 4);

		IloRange r0 = rng[0][0];
		IloRange r1 = rng[0][1];
		IloRange r2 = rng[0][2];
		IloRange r3 = rng[0][3];
		IloRange r4 = rng[0][4];
		IloRange r5 = rng[0][5];
		IloRange r6 = rng[0][6];

		// row matrix
		var[0][0] = model.boolVar(model.column(obj, 1).and(
				model.column(r0, 1).and(
						model.column(r4, 1).and(model.column(r6, 2)))));
		var[0][1] = model.boolVar(model.column(obj, 1).and(
				model.column(r1, 1).and(
						model.column(r3, 1).and(model.column(r6, 2)))));
		var[0][2] = model.boolVar(model.column(obj, 1).and(
				model.column(r1, 1).and(
						model.column(r4, 1).and(model.column(r6, 3)))));
		var[0][3] = model.boolVar(model.column(obj, 1).and(
				model.column(r1, 1).and(
						model.column(r5, 1).and(model.column(r6, 2)))));
		var[0][4] = model.boolVar(model.column(obj, 2).and(
				model.column(r2, 1).and(
						model.column(r4, 1).and(model.column(r6, 2)))));
	}
}
