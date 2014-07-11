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
 * 
 * @param <Item>
 *            the generic type
 */
public class Queue<Item> implements Iterable<Item> {
	
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

	/** The n. */
	private int N; // number of elements on queue

	/** The first. */
	private Node first; // beginning of queue

	/** The last. */
	private Node last; // end of queue

	/**
	 * Create an empty queue.
	 */
	public Queue() {
		first = null;
		last = null;
	}

	/**
	 * Remove and return the item on the queue least recently added. Throw an
	 * exception if the queue is empty.
	 * 
	 * @return the item
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
	 * Add the item to the queue.
	 * 
	 * @param item
	 *            the item
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
	 * Is the queue empty?.
	 * 
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return first == null;
	}

	/**
	 * Return an iterator that iterates over the items on the queue in FIFO
	 * order.
	 * 
	 * @return the iterator
	 */
	@Override
	public Iterator<Item> iterator() {
		return new FIFOIterator();
	}

	/**
	 * Return the item least recently added to the queue. Throw an exception if
	 * the queue is empty.
	 * 
	 * @return the item
	 */
	public Item peek() {
		if (isEmpty())
			throw new RuntimeException("Queue underflow");
		return first.item;
	}

	/**
	 * Return the number of items in the queue.
	 * 
	 * @return the int
	 */
	public int size() {
		return N;
	}

	/**
	 * Return string representation.
	 * 
	 * @return the string
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Item item : this)
			s.append(item + " ");
		return s.toString();
	}
}