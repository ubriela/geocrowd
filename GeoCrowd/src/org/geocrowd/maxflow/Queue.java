/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.geocrowd.maxflow;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The <tt>Queue</tt> class represents a first-in-first-out (FIFO) queue of
 * generic items. It supports the usual <em>enqueue</em> and <em>dequeue</em>
 * operations, along with methods for peeking at the top item, testing if the
 * queue is empty, and iterating through the items in FIFO order.
 * <p>
 * All queue operations except iteration are constant time.
 * <p>
 * For additional documentation, see <a href="/algs4/13stacks">Section 1.3</a>
 * of <i>Algorithms in Java, 4th Edition</i> by Robert Sedgewick and Kevin
 * Wayne.
 */
public class Queue<Item> implements Iterable<Item> {
	private int N; // number of elements on queue
	private Node first; // beginning of queue
	private Node last; // end of queue

	// helper linked list class
	private class Node {
		private Item item;
		private Node next;
	}

	/**
	 * Create an empty queue.
	 */
	public Queue() {
		first = null;
		last = null;
	}

	/**
	 * Is the queue empty?
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * Return the number of items in the queue.
	 */
	public int size() {
		return N;
	}

	/**
	 * Return the item least recently added to the queue. Throw an exception if
	 * the queue is empty.
	 */
	public Item peek() {
		if (isEmpty())
			throw new RuntimeException("Queue underflow");
		return first.item;
	}

	/**
	 * Add the item to the queue.
	 */
	public void enqueue(Item item) {
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
	 * Remove and return the item on the queue least recently added. Throw an
	 * exception if the queue is empty.
	 */
	public Item dequeue() {
		if (isEmpty())
			throw new RuntimeException("Queue underflow");
		Item item = first.item;
		first = first.next;
		N--;
		return item;
	}

	/**
	 * Return string representation.
	 */
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Item item : this)
			s.append(item + " ");
		return s.toString();
	}

	/**
	 * Return an iterator that iterates over the items on the queue in FIFO
	 * order.
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
		 * A queue of strings
		 ***********************************************/
		Queue<String> q1 = new Queue<String>();
		q1.enqueue("Vertigo");
		q1.enqueue("Just Lose It");
		q1.enqueue("Pieces of Me");
		System.out.println(q1.dequeue());
		q1.enqueue("Drop It Like It's Hot");
		while (!q1.isEmpty())
			System.out.println(q1.dequeue());
		System.out.println();

		/*********************************************************
		 * A queue of integers. Illustrates autoboxing and auto-unboxing.
		 *********************************************************/
		Queue<Integer> q2 = new Queue<Integer>();
		for (int i = 0; i < 10; i++)
			q2.enqueue(i);

		// test out iterator
		for (int i : q2)
			System.out.print(i + " ");
		System.out.println();

		// test out dequeue and enqueue
		while (q2.size() >= 2) {
			int a = q2.dequeue();
			int b = q2.dequeue();
			int c = a + b;
			System.out.println(c);
			q2.enqueue(a + b);
		}

	}
}