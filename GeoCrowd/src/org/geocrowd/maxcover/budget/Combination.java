package org.geocrowd.maxcover.budget;

public class Combination {
	
	
	public static void main(String[] args) throws Exception {
		Integer[] set = {1,2,3,4,5};
		int k = 3;
		Integer[][] c = combinations(k, set);
	}

	
	public static Integer[][] combinations(int k, Integer[] set) {
	    // binomial(N, K)
	    int c = (int) binomial(set.length, k);
	    // where all sets are stored
	    Integer[][] res = new Integer[c][Math.max(0, k)];
	    // the k indexes (from set) where the red squares are
	    // see image above
	    int[] ind = k < 0 ? null : new int[k];
	    // initialize red squares
	    for (int i = 0; i < k; ++i) { ind[i] = i; }
	    // for every combination
	    for (int i = 0; i < c; ++i) {
	        // get its elements (red square indexes)
	        for (int j = 0; j < k; ++j) {
	            res[i][j] = set[ind[j]];
	        }
	        // update red squares, starting by the last
	        int x = ind.length - 1;
	        boolean loop;
	        do {
	            loop = false;
	            // move to next
	            ind[x] = ind[x] + 1;
	            // if crossing boundaries, move previous
	            if (ind[x] > set.length - (k - x)) {
	                --x;
	                loop = x >= 0;
	            } else {
	                // update every following square
	                for (int x1 = x + 1; x1 < ind.length; ++x1) {
	                    ind[x1] = ind[x1 - 1] + 1;
	                }
	            }
	        } while (loop);
	    }
	    return res;
	}
	
	private static long binomial(int n, int k) {
	    if (k < 0 || k > n) return 0;
	    if (k > n - k) {    // take advantage of symmetry
	        k = n - k;
	    }
	    long c = 1;
	    for (int i = 1; i < k+1; ++i) {
	        c = c * (n - (k - i));
	        c = c / i;
	    }
	    return c;
	}
}