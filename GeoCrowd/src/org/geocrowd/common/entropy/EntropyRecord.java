package org.geocrowd.common.entropy;

/**
 * 
 * @author Leyla & Hien To
 * 
 * location entropy for each grid cell
 */
public class EntropyRecord {
	private double entropy;
	private Coord coord;
	private int workerNo; // number of people chosen as workers; starts with 0
							// and is density as max

	public EntropyRecord() {
	}

	public EntropyRecord(double d, Coord coord) {
		entropy = d;
		this.coord = coord;
		workerNo = 0;
	}

	public void setEntropy(int d) {
		entropy = d;
	}

	public double getEntropy() {
		return entropy;
	}

	public Coord getCoord() {
		return coord;
	}

	public void setCoord(Coord coord) {
		this.coord = coord;
	}

	public int getWorkerNo() {
		return workerNo;
	}

	public void incWorkerNo() {
		workerNo++;
	}
}