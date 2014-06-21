package org.geocrowd.common;

/**
 * 
 * @author HT186011
 * 
 * A workerid is matched with a taskid
 *
 */
public class MatchPair {
	int w;
	int t;
	public MatchPair(int w, int t) {
		super();
		this.w = w;
		this.t = t;
	}
	public int getW() {
		return w;
	}
	public int getT() {
		return t;
	}
}
