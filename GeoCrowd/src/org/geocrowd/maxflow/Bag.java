/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geocrowd.maxflow;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
 */
public class Bag<Item> implements Iterable<Item> {
	private int N; // number of elements in bag
	private Node first; // beginning of bag
	private Node last; // end of bag

	// helper linked list class
	private class Node {
		private Item item;
		private Node next;
	}

	/**
	 * Create an empty bag.
	 */
	public Bag() {
		first = null;
		last = null;
	}

	/**
	 * Is the bag empty?
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * Return the number of items in the bag.
	 */
	public int size() {
		return N;
	}

	/**
	 * Add the item to the bag.
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
	 * Return an iterator that iterates over the items in the bag.
	 */
	public Iterator<Item> iterator() {
		return new FIFOIterator();
	}

	// an iterator, doesn't implement remove() since it's optional
	private class FIFOIterator implements Iterator<Item> {
		private Node current = first;

		public boolean hasNext() {
			return current != null;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public Item next() {
			if (!hasNext())
				throw new NoSuchElementException();
			Item item = current.item;
			current = current.next;
			return item;
		}
	}

	/**
	 * A test client.
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
}
