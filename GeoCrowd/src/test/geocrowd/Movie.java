package test.geocrowd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Implement a function to return the N highest rated movies that are considered
 * similar to a given movie. Each movie has a rating and a list of movies it is
 * most similar to. The full list of recommendation candidates for a given movie
 * is its entire similarity network (similarities, similarities of similarities,
 * etc.). The order of the returned movies does not matter.
 * 
 * @author ubriela
 *
 */
public class Movie implements Comparable<Movie>{
	static final double VERY_SMALL_NUMBER = 0.000001;
	private final int movieId;
	private final float rating;
	private List<Movie> similarMovies; // Similarity is bidirectional

	public Movie(int movieId, float rating) {
		this.movieId = movieId;
		this.rating = rating;
		similarMovies = new ArrayList<Movie>();
	}

	public int getId() {
		return movieId;
	}

	public float getRating() {
		return rating;
	}

	public void addSimilarMovie(Movie movie) {
		similarMovies.add(movie);
		movie.similarMovies.add(this);
	}

	public List<Movie> getSimilarMovies() {
		return similarMovies;
	}
	
	
	
	@Override
	public String toString() {
		return "Movie [movieId=" + movieId + ", rating=" + rating
				+ ", similarMovies=" + similarMovies + "]";
	}

	public static void main(String[] args) {
		final Movie A = new Movie(1, (float)1.2);
		final Movie B = new Movie(2, (float)2.4);
		final Movie C = new Movie(3, (float)3.6);
		final Movie D = new Movie(4, (float)4.8);
		A.similarMovies = new ArrayList<Movie>(){{add(B);add(C);}};
		B.similarMovies = new ArrayList<Movie>(){{add(A);add(D);}};
		C.similarMovies = new ArrayList<Movie>(){{add(A);add(D);}};
		D.similarMovies = new ArrayList<Movie>(){{add(B);add(C);}};
		
		for (Movie m : Movie.getMovieRecommendations(A, 1))
			System.out.println(m.getId());
	}

    /*
     * Implement a function to return top rated movies in the network of movies 
     * reachable from the current movie
     * eg:             A(Rating 1.2)
     *               /   \
     *            B(2.4)  C(3.6)
     *              \     /
     *                D(4.8)
     * In the above example edges represent similarity and the number is rating.
     * getMovieRecommendations(A,2) should return C and D (sorting order doesn't matter so it can also return D and C)
     * getMovieRecommendations(A,4) should return A, B, C, D (it can also return these in any order eg: B,C,D,A)
     * getMovieRecommendations(A,1) should return D. Note distance from A to D doesn't matter, return the highest rated.
     *     
     *     @param movie
     *     @param numTopRatedSimilarMovies 
     *                      number of movies we want to return
     *     @return List of top rated similar movies
     */
	public static List<Movie> getMovieRecommendations(Movie movie,
			int numTopRatedSimilarMovies) {
		/**
		 * Assumptions: 
		 * 1) distance does not matter
		 * 
		 */
		
		HashSet<Movie> allSimilarMovies = new HashSet<Movie>();
		allSimilarMovies.add(movie);
		LinkedList<Movie> traversingMovies = new LinkedList<Movie>();
		traversingMovies.add(movie);
		PriorityQueue<Movie> topKMovies = new PriorityQueue<Movie>(numTopRatedSimilarMovies);
		topKMovies.add(movie);

		while (!traversingMovies.isEmpty()) {
			Movie m = traversingMovies.pollFirst();
			for (Movie n : m.getSimilarMovies()) {
				if (!allSimilarMovies.contains(n)) {
					allSimilarMovies.add(n);
					topKMovies.add(n);
					traversingMovies.addFirst(n);	// DFS
					
					/** keep queue size less than k */
					if (topKMovies.size() > numTopRatedSimilarMovies)
						topKMovies.poll();
				} 
			}
		}
		
		return new ArrayList<Movie>(topKMovies);
	}


	@Override
	public int compareTo(Movie o) {
		if (this.getRating() > o.getRating())
			return 1;
		else if (this.getRating() < o.getRating())
			return -1;
		else 
			return 0;
	}
}