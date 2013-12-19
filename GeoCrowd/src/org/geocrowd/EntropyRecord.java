package org.geocrowd;

/**
 * 
 * @author Leyla
 */
public class EntropyRecord {
	private double entropy;
	private int rowIdx;
	private int colIdx;
	private int workerNo; // number of people chosen as workers; starts with 0
							// and is density as max

	public EntropyRecord() {
	}

	public EntropyRecord(double d, int r, int c) {
		entropy = d;
		rowIdx = r;
		colIdx = c;
		workerNo = 0;
	}

	public void setEntropy(int d) {
		entropy = d;
	}

	public double getEntropy() {
		return entropy;
	}

	public void setRowIdx(int row, int col) {
		rowIdx = row;
		colIdx = col;
	}

	public int getRowIdx() {
		return rowIdx;
	}

	public int getColIdx() {
		return colIdx;
	}

	public int getWorkerNo() {
		return workerNo;
	}

	public void incWorkerNo() {
		workerNo++;
	}
}