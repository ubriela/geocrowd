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

package org.geocrowd.maxflow;

import java.util.Iterator;
import java.util.NoSuchElementException;

// TODO: Auto-generated Javadoc
/**
 * The <tt>Bag</tt> class represents a bag (or multiset) of generic items. It
 * supports insertion and iterating over the items in arbitrary order.
 * <p>
 * The <em>add</em>, <em>isEmpty</em>, and <em>size</em> operation take constant
 * time. Iteration takes time proportional to the number of items.
 * <p>
 * For additional documentation, see <a href="/algs4/13stacks">Section 1.3</a>
 * of <i>Algorithms in Java, 4th Edition</i> by Robert Sedgewick and Kevin
 * Wayne.
 * 
 * @param <Item>
 *            the generic type
 */
public class Bag<Item> implements Iterable<Item> {
	
	// an iterator, doesn't implement remove() since it's optional
	/**
	 * The Class FIFOIterator.
	 */
	private class FIFOIterator implements Iterator<Item> {
		
		/** The current. */
		private Node current = first;

		/* (non-Javadoc)
		 * @see java.util.Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return current != null;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#next()
		 */
		@Override
		public Item next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Item item = current.item;
			current = current.next;
			return item;
		}

		/* (non-Javadoc)
		 * @see java.util.Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	// helper linked list class
	/**
	 * The Class Node.
	 */
	private class Node {
		
		/** The item. */
		private Item item;
		
		/** The next. */
		private Node next;
	}
	
	/**
	 * A test client.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		/***********************************************
		 * A bag of strings
		 ***********************************************/
		Bag<String> bag = new Bag<String>();
		bag.add("Vertigo");
		bag.add("Just Lose It");
		bag.add("Pieces of Me");
		bag.add("Drop It Like It's Hot");
		for (String s : bag) {
			System.out.println(s);
		}
	}

	/** The n. */
	private int N; // number of elements in bag

	/** The first. */
	private Node first; // beginning of bag

	/** The last. */
	private Node last; // end of bag

	/**
	 * Create an empty bag.
	 */
	public Bag() {
		first = null;
		last = null;
	}

	/**
	 * Add the item to the bag.
	 * 
	 * @param item
	 *            the item
	 */
	public void add(Item item) {
		Node x = new Node();
		x.item = item;
		if (isEmpty()) {
			first = x;
			last = x;
		} else {
			last.next = x;
			last = x;
		}
		N++;
	}

	/**
	 * Is the bag empty?.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * Return an iterator that iterates over the items in the bag.
	 * 
	 * @return the iterator
	 */
	@Override
	public Iterator<Item> iterator() {
		return new FIFOIterator();
	}

	/**
	 * Return the number of items in the bag.
	 * 
	 * @return the int
	 */
	public int size() {
		return N;
	}
}
