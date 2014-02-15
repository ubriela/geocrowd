package org.datasets.gowalla;

public class Range {
	private double start, end;

	public Range(double start, double end) {
		super();
		this.start = start;
		this.end = end;
	}

	public double getStart() {
		return start;
	}

	public double getEnd() {
		return end;
	}
	
	public double delta() {
		return end - start;
	}

	public void debug() {
		// TODO Auto-generated method stub
		System.out.println("select lon from mcdonaldoned where lon >=" + start
				+ " and lon <=" + end + ";");
	}

}
