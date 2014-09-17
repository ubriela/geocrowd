package test.geocrowd;

import java.util.ArrayList;
import java.util.Comparator;
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
public class Movie implements Comparator<Movie>{
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
		PriorityQueue<Movie> topKMovies = new PriorityQueue<Movie>(initialCapacity, comparator)
		return null;
	}

	@Override
	public int compare(Movie o1, Movie o2) {
		// TODO Auto-generated method stub
		return 0;
	}
}