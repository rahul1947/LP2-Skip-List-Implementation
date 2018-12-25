package rsn170330.lp2;

/**
 * CS 5V81.001. Implementation of data structures and algorithms 
 * Long Project LP2: Skip List Implementation
 * @author Rahul Nalawade (rsn170330)
 * @author Dhwani Raval (dsr170230)
 * @author Varun Parashar (vxp171830)
 * @author Arpita Agrawal (aua170030)
 * 
 * Date: Sunday, October 14, 2018
 */

import java.util.Random;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SkipList<T extends Comparable<? super T>> {
	static final int POSSIBLE_LEVELS = 33;
	
	static class Entry<E> {
		E element;
		Entry<E>[] next;
		Entry<E> prev; // prev is optional? NOPE!
		private int height;
		
		// span[i]: storing distance of the Entry in next[i] 
		// from the current Entry
		int[] span; // for indexing
		
		// Parameterized Constructor:
		public Entry(E x, int level) {
			element = x;
			next = new Entry[level];
			height = level;
			
			span = new int[level];
		}
		
		// Returns the element of this Entry
		public E getElement() {
			return element;
		}
	}
	
	// Dummy header & tail is created.
	private Entry<T> head, tail;
	
	// maxLevel is the level equal to the longest next[]
	public int size, maxLevel; 
	
	// last[i]: Entry at which search came down from level i
	private Entry<T>[] last; // used by find
	
	// distanceTraversed[i]: distance traversed on level i, 
	// as search came down from last[i] to level i-1
	private int[] distanceTraversed; // used for updating span[]
	
	private Random rand; // for random height (like using coin-flip)
	
	// Default Constructor
	public SkipList() {
		head = new Entry<T>(null, POSSIBLE_LEVELS);
		tail = new Entry<T>(null, POSSIBLE_LEVELS);
		
		size = 0;
		maxLevel = 1;
		
		last = new Entry[POSSIBLE_LEVELS];
		distanceTraversed = new int[POSSIBLE_LEVELS];
		
		rand = new Random();
		
		// Each entry in head.next[] points to tail
		for (int i = 0; i < POSSIBLE_LEVELS; i++) {
			head.next[i] = tail;
			head.span[i] = 1; // tail 1 distance away
		}
		
		// When list is empty, head is previous of tail
		tail.prev = head;
	}
	
	// SkipListIterator class implementation to traverse the Skip list
	private class SkipListIterator implements Iterator<T> {
		Entry<T> cursor, prevEntry; 
		boolean ready; // is Cursor ready to be removed
		
		SkipListIterator() {
			cursor = head;
			prevEntry = null;
			ready = false;
		}
		
		/**
		 * Returns true if Iterator has more element to iterate.
		 */
		public boolean hasNext() {
			return (cursor.next[0] != null && 
					cursor.next[0].element != null);
		}
		
		/**
		 * Returns the next element in the collection until 
		 * the hasNext()method return true. This method 
		 * throws 'NoSuchElementException' if there is no next element.
		 */
		public T next() {
			// When there is no next element
			if (!hasNext())
				throw new NoSuchElementException("There is no next element.");
			
			// When there is next element
			prevEntry = cursor; // prevEntry is cursor now
			cursor = cursor.next[0]; // cursor++
			ready = true; // we can do remove after this next()
			return cursor.element; 
		}
		
		/**
		 * Removes the current element in the collection. 
		 * This method throws 'NoSuchElementException' if 
		 * this function is called before next() is invoked.
		 */
		public void remove() {
			// When remove operation cannot be done!
			if (!ready) 
				throw new NoSuchElementException("Illegal State.");
			
			find(cursor.element);
			
			int i=0;
			
			// Merging links which pointed to cursor with links where 
			// cursor's next[] were pointed to, at each level 
			while (i < cursor.height) {
				// bypassing at level i
				last[i].next[i] = cursor.next[i]; 
				// adding two spans, -1 for removed
				last[i].span[i] = last[i].span[i] + cursor.span[i] - 1; 
				i++;
			}
			
			// update spans above height for last[] 
			// but, unaffected next[] pointers!
			while (last[i] != null) {
				last[i].span[i]--;
				i++;
			} 
			
			// NOTE: last[] is null for levels above maxLevel
			// So, update span of un-used last[] Entries
			for(i = 0; i < POSSIBLE_LEVELS; i++){
				if(head.next[i] == tail)
					head.span[i] = size + 1;
			}
			// Now, cursor is removed, whoosh! :o
			size--;
			
			cursor = prevEntry; // after removal, cursor is prevEntry
			
			// Calling remove again without calling next... 
			ready = false; // ...will cause in exception thrown
		}
	}
	
	/**
	 * Insert x in the Skip list. return true on successful insertion.
	 * else return false.
	 * @param x the element to be added (generic type T)
	 * @return true on successful insertion, false otherwise
	 */
	public boolean add(T x) {
		
		// When x is already present
		if (contains(x)) 
			return false;
		
		int i = 0;
		int level = chooseLevel(); // length of next[] of x's Entry
		Entry<T> ent = new Entry(x, level);
		
		// position = index + 1 = distance from head
		// prevPosition: distance of last[i] from head on find(x)
		// newPosition: prevPosition + 1 (where we'll add the x)
		int prevPosition = 0, newPosition = 0;
		
		// updating the total distance traversed 
		for (i = 0; i < distanceTraversed.length; i++) {
			prevPosition += distanceTraversed[i];
		}
		// will do insertion at newPosition
		newPosition = prevPosition + 1; 
		
		// for each level of newly created Entry ent
		for (i = 0; i < level; i++) {
			
			// When no Entry in last[], to avoid NPE
			if (last[i] == null)
				break;
			
			// re-establishing the next links
			ent.next[i] = last[i].next[i];
			last[i].next[i] = ent;
			
			// re-establishing the span's prevPosition
			ent.span[i] = prevPosition + last[i].span[i] - newPosition + 1;
			last[i].span[i] = newPosition - prevPosition;
			
			// reducing the prevPosition's distance traversed 
			// at level i (doing this for level i+1)
			prevPosition = prevPosition - distanceTraversed[i];
		}
		// Updating the proper height
		ent.height = i;
		
		// re-establishing the previous links
		ent.next[0].prev = ent;
		ent.prev = last[0];
		
		// Increment the span of the last array elements 
		// by 1 if no re-pointing is done
		while (i < last.length){
			// When no Entry in last[i]
			if (last[i] == null)
				break;
			
			last[i].span[i]++;
			i++;
		}
		size++;
		
		// Update the unused head span with the size+1 
		// (tail will be at size + 1)
		for (int j = 0; j < POSSIBLE_LEVELS; j++){
			if (head.next[j] == tail)
				head.span[j] = size + 1;
		}
		
		return true;
	}
		
	/**
	 * Helper method - add(x)
	 * Chooses a random level
	 * @return return the level
	 */
	public int chooseLevel() {
		// fast method:
		int lev = 1 + Integer.numberOfLeadingZeros(rand.nextInt());
		// floor(log2(x)) = 31 - numberOfLeadingZeros(x)
		
		// optionally (to allow maxLevel to grow gradually) - 
		lev = Math.min(lev,  maxLevel + 1);
		
		if (maxLevel < lev) 
			maxLevel = lev;
		
		return lev; 
	}

	/**
	 * Find the smallest element that is greater than or equal to x.
	 * @param x the input element
	 * @return the element immediate next to x  
	 */
	public T ceiling(T x) {
		
		// When x greater than the last element
		if (x.compareTo(last()) > 0)
			return null;
		
		// When x is smaller than the first element
		if (x.compareTo(first()) < 0)
			return first();
		
		// When there is no such element in the list
		if (!contains(x)) 
			// just return the next of last[0] from find(x)
			return last[0].next[0].element;
		
		return x; // We have x
	}
	
	/**
	 * Does the Skip list contains x? 
	 * @param x the element to be searched 
	 * @return true when x is present, false if not
	 */
	public boolean contains(T x) {
		
		// When x is null
		if (x == null) 
			return false;
		
		// Tries to find x, updating last[] accordingly
		find(x);
		
		// avoiding NPE, and checking if we have x or not
		if(last[0].next[0].element != null && 
				last[0].next[0].element.compareTo(x) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Helper method to search for x. 
	 * Sets last[i] = node at which search came down from 
	 * level i to i-1
	 * @param x the element to be searched
	 */
	private void find(T x) {
		
		Entry<T> p = head; // like cursor
		
		distanceTraversed = new int[POSSIBLE_LEVELS];
		
		// No of useful levels of head.next[]: 1 to maxLevel
		for (int i = 0; i < maxLevel; i++) {
			int in = maxLevel - 1 - i; // starting from top
			
			// watch out for NPE because of null element in tail***
			// while there is an entry AND an element AND ... 
			while(	p.next[in] != null && 
					p.next[in].element != null && 
					p.next[in].element.compareTo(x) < 0) {
				
				// updating distance traversed
				distanceTraversed[in] += p.span[in]; 
				p = p.next[in]; // moving further
			}
			last[in] = p; // entry at which search came down
		}
	}
	
	/**
	 * Returns the first element of the Skip list.
	 * @return the element
	 */
	public T first() {
		return head.next[0].element; // immediate next of head
	}
	
	/**
	 * Find the largest element that is less than or equal to x.
	 * @param x the input element
	 * @return the element previous to x  
	 */
	public T floor(T x) {
		
		// When x greater than the last element
		if (x.compareTo(last()) > 0)
			return last();
		
		// When x is smaller than the first element
		if (x.compareTo(first()) < 0)
			return null;
					
		// When there is no such element in the list
		if (!contains(x)) 
			// just return the last[0].element from find(x)
			return last[0].element;
		
		return x; // We have x
	}
		
		
	// Indexing in the Skip Lists: 
	
	private int getPosition(T x) {
		Entry<T> p = head; // like cursor
		
		// distance of x from the head
		int distance = 0;
		
		// No of useful levels of head.next[]: 1 to maxLevel
		for (int i = 0; i < maxLevel; i++) {
			int in = maxLevel - 1 - i; // starting from top
			
			// watch out for NPE because of null element in tail***
			// while there is an entry AND an element AND ... 
			while(	p.next[in] != null && 
					p.next[in].element != null && 
					p.next[in].element.compareTo(x) < 0) {
				
				// updating distance traversed
				distance += p.span[in]; 
				p = p.next[in]; // moving further
			}
			last[in] = p; // entry at which search came down
		}
		return (distance + 1);
	}
	
	/**
	 * Return element at index n of list. 
	 * First element is at index 0.
	 * @param n the input index
	 * @return the element at index n
	 */
	public T get(int n) {
		boolean linear = false;
		
		// When the search is using the lowest link, next[0].
		if (linear)
			return getLinear(n);
		
		// default access: Running Time = O(log n)
		return getLog(n);
	}
	
	/**
	 * Helper method - get():
	 * RT: O(n) algorithm expected time for get(n)
	 * @param n the input index
	 * @return the element at index n
	 * @throws NoSuchElementException When n is invalid index
	 */
	public T getLinear(int n) throws NoSuchElementException {
		// When invalid input index
		if (n < 0 || size - 1 < n) 
			throw new NoSuchElementException();
		
		Entry<T> p = head; // like cursor
		
		// traversing through slowest/lowest express-way
		for (int i = 0; i < n; i++) 
			p = p.next[0]; 
		
		return p.next[0].element; // worst case RT = O(n)
	}
	
	/**
	 * Optional operation: Eligible for EXCELLENCE CREDIT.
	 * Requires maintenance of spans, as discussed in class.
	 * 
	 * Helper method - get():
	 * RT: O(log n) algorithm expected time for get(n)
	 * @param n the input index
	 * @return the element at index n
	 * @throws NoSuchElementException When n is invalid index
	 */
	public T getLog(int n) throws NoSuchElementException {
		// position: index + 1
		int position = n + 1;
		
		// When invalid input index
		if (n < 0 || size - 1 < n) 
			throw new NoSuchElementException();
		
		// visitedPosition: counter of distance traversed horizontally
		// to reach value n (position - 1, just previous of our result)
		int visitedPosition = 0;
		Entry<T> p = head; // like cursor/ pointer
		
		int i = POSSIBLE_LEVELS - 1; // level to begin with
		
		// to denote if there exists a non-tail link from head
		boolean trainStarted = false;
		
		// Going down until head points to some non-tail
		while (!trainStarted) {
			
			// When head points to non-tail, don't lower i
			if(p.next[i].element != null) {
				trainStarted = true;
			}
			// else just lower the level i
			else
				i--;
		}
		// Now, we can start our search from i
		
		// Iterating top-down from i to 0
		while (i > -1) {
			
			// Traverse level i until we didn't go past our position
			while ((p.span[i] + visitedPosition) < position) { 
				// updating traversed distance from head
				visitedPosition += p.span[i]; 
				p = p.next[i]; // traversing p on the same level
			}
			i--;
			// If we see that we went past out position, continue to 
			// lower the level, until we reach to the lowest level.
		}
		// p is at (position-1), so return just immediate next element
		return p.next[0].element;
	} // worst case RT: O(POSSIBLE_LEVELS*log n) = O(log n)

	/**
	 * Is the list empty?
	 * @return true when empty Skip list, otherwise false
	 */
	public boolean isEmpty() { 
		if (size() < 1) return true;
		return false;
	}
	
	/**
	 * Iterate through the elements of list in SORTED ORDER.
	 * @return iterator
	 */
	public Iterator<T> iterator() {
		return new SkipListIterator();
	}
	
	/**
	 * Returns the last element of List.
	 * @return the element
	 */
	public T last() {
		return tail.prev.element; // previous of tail
	}
	
	/**
	 * Prints Skip list for each express-way horizontally
	 */
	private void printLevels() {
		
		System.out.println("printLevels(): maxLevel = "+maxLevel);
		
		System.out.print("Level \tElements");
		// Iterating top-down through each level of the Skip list
		for (int i = 0; i < maxLevel; i++) {
			int in = maxLevel - 1 - i; // top-down
			
			System.out.print((in+1)+":\t");
			
			Entry<T> q = head; // starting from head
			
			// Traversing all the way through level 'in'
			while (q.next[in] != null) {
				System.out.print(q.element+" ");
				q = q.next[in]; // next entry on the same level 'in'
			}
			System.out.println();
		}
	}
	
	/**
	 * Prints skip list, 
	 * giving next[] horizontally for each entry.
	 */
	private void printList() {
		
		System.out.println("printList(): maxLevel = "+maxLevel);
		System.out.println("Index\tElement\tnext[]");
		Entry<T> p = head;
		int i=0;
		
		// for all entries + head and tail
		while (i < size + 2) {
			System.out.print((i-1)+"\t"+p.element+": \t[");
			//while (j < p.height && j < maxLevel)
			
			// Printing elements in next[]
			for (int j = 0; j < p.height; j++) {
				
				// In the beginning of each next[] building
				if (j == 0) {
					// when you (p) reached at tail
					if (p.next[0] == null) {
						System.out.print("]");
						break;
					}
				}
				// when you are (p is) at non-tail
				// your p would have at-least one entry
				
				// When you reach at the last, i.e. p is the last
				if (j == p.height - 1 || j == maxLevel - 1) {
					// When p is not null
					if (p.next[j] != null)
						System.out.print(p.next[j].element);
					System.out.print("]"); // closes next[]
					break; // loop on j must be closed 
				}
				// When j is not the last
				else {
					// When Entry after p is null -> just print p 
					// [NO ArrayOutOfBoundException :) 
					// as we already checked if 'j' is the last]
					if (p.next[j+1] == null) {
						System.out.print(p.next[j].element);
						System.out.print("]"); // closes next[]
						break; // loop on j must be closed 
					}
					// When the Entry after p is not null -> print p+", "
					else {
						System.out.print(p.next[j].element+", ");
					}
				}	
			}
			
			System.out.println();
			p = p.next[0]; // next entry in the Skip list
			i++;
		}		
	}
	
	/**
	 * Prints skip list, 
	 * giving span[] horizontally for each entry.
	 * SIMILAR to printList()
	 */
	private void printListSpan() {
		
		System.out.println("printListSpan(): maxLevel = "+maxLevel);
		System.out.println("Height\tElement\tspan[]");
		Entry<T> p = head;
		int i=0;
		
		// for all entries + head and tail
		while (i < size+2) {
			System.out.print("h = "+p.height+"\t"+p.element+": \t[");
			
			// Printing elements in span[]
			for (int j = 0; j < p.height; j++) {
				
				// In the beginning of each span[] building
				if (j == 0) {
					// when you (p) reached at tail
					if (p.next[0] == null) {
						System.out.print("]");
						break;
					}
				}
				// when you are (p is) at non-tail
				// your p would have at-least one entry
				
				// When you reach at the last, i.e. p is the last
				if (j == p.height - 1 || j == maxLevel - 1) {
					// When p is not null
					if (p.next[j] != null)
						System.out.print(p.span[j]);
					System.out.print("]"); // closes span[]
					break; // loop on j must be closed 
				}
				// When j is not the last
				else {
					// When Entry after p is null -> just print p 
					// [NO ArrayOutOfBoundException :) 
					// as we already checked if 'j' is the last]
					if (p.next[j+1] == null) {
						System.out.print(p.span[j]);
						System.out.print("]"); // closes span[]
						break; // loop on j must be closed 
					}
					// When the Entry after p is not null -> print p+", "
					else {
						System.out.print(p.span[j]+", ");
					}
				}	
			}
			
			System.out.println();
			p = p.next[0]; // next entry in the Skip list
			i++;
		}	
	}
	
	/**
	 * Eligible for EXCELLENCE CREDIT.
	 * Not a standard operation in skip lists.
	 * Optional operation: Reorganize the elements of the list 
	 * into a perfect skip list. 
	 */
	public void rebuild() {
		// computing maxLevel required for rebuild()
		maxLevel = (int) (Math.log10(size) / Math.log10(2)) + 1; 
		
		// position (= index + 1) of the current entry, starting at head
		int position = 0;  
		
		// storing just like next[] for each entry
		Entry<T>[] previous = new Entry[maxLevel];
		
		// storing number of powers of 2. e.g. [1,2,4,8,...]
		int[] powersOfTwo = new int[maxLevel];
		
		// Initializing all entries of previous[] as head, 
		// and computing powers of 2.
		for (int i = 0; i < maxLevel; i++) { 
			previous[i] = head; 
			powersOfTwo[i] = (int) Math.pow(2, i); 
		}
		
		// p is the first entry
		Entry<T> p = head.next[0];
		
		// While p reaches tail
		while (p != null) { 
			position++; 
			
			// For odd positions, head positioned at 0.
			if (!(position % 2 == 0)) {
				// exact no of entries in new p.next[] = 1 :)
				p.height = 1;
				
				Entry <T>[] newNext = new Entry[1];
				int[] newSpan = new int[1];
				
				// only correctly referencing at lowest level
				newNext[0] = p.next[0]; 
				newSpan[0] = 1;
				// replacing the old next with the new next[] 
				p.next = newNext; 
				p.span = newSpan; // & old span with new span[]
				
				previous[0].next[0] = p; 
				previous[0] = p;
			} 
			
			// For even positions, need to recreate next[] and span[]
			// with proper height of each entry
			else {
				// exponent = the largest power
				int exponent = maxLevel - 1; 
				
				// reducing exponent as it reaches exact max exponent 
				// required for that entry
				while (position % powersOfTwo[exponent] != 0)
					exponent--;
				
				// exact no of entries in new p.next[] = exponent + 1
				p.height = exponent + 1;
				
				Entry <T>[] newNext = new Entry[p.height];
				int[] newSpan = new int[p.height];
				
				// only correctly referencing at lowest level
				newNext[0] = p.next[0]; 
				newSpan[0] = p.span[0];
				// replacing the old next with the new next[] 
				p.next = newNext; 
				p.span = newSpan; // & old span with new span[]
				
				// so filling up entries in top-down manner 
				// in all such entries which points to p
				while (exponent > -1) {
					// Entry at level exponent which points to p = p 
					previous[exponent].next[exponent] = p;
					previous[exponent].span[exponent] = powersOfTwo[exponent];
					
					// Now that entry is replaced by p, ...
					previous[exponent] = p; // ...for some future entry
					exponent--;
				}
			}
			p = p.next[0]; // moving on to the immediate next
		}
		
		// Now whatever entries we have in previous[] 
		// are the end-stops at each express level. :)
		// So, point them to tail if they aren't pointing to it.
		for (int i = 0; i < maxLevel; i++) {
			int in = maxLevel - 1 - i;
			
			if (previous[in] != tail) {
				previous[in].next[in] = tail;
				
				// When size = 2^maxLevel - 1
				if (size() == Math.pow(2, maxLevel) - 1) {
					// in perfect skip list span[i] = 2^i
					previous[in].span[in] = powersOfTwo[in];
				}
				// When size != 2^maxLevel - 1
				else {
					// prevDist: distance of previous[in] from head
					int prevDist = 0;
					//getPosition(previous[i].element);
					Entry<T> q = head;
					
					while ( q.next[in] != null && 
					q.next[in].element != null && 
					q.next[in].element.compareTo(previous[in].element) <= 0) {
						
						prevDist += q.span[in]; // summing up distance strode
						q = q.next[in]; // on the same level
					}
					// distance to tail = size + 1 - prevDist
					previous[in].span[in] = size + 1 - prevDist;
				}
 			}
		}
	}
	
	/**
	 * Removes x from the list, if present. 
	 * Removed element is returned.
	 * @param x the element to be removed
	 * @return removed element, if present, else null
	 */
	public T remove(T x) {
		// When there is no x in the list
		if (!contains(x)) return null;
		
		// ent is the element to be removed
		Entry<T> ent = last[0].next[0]; 
		
		int i=0;
		// Merging links which pointed to cursor with links where 
		// cursor's next[] were pointed to, at each level 
		while (i < ent.height) {
			// bypassing at level i
			last[i].next[i] = ent.next[i]; 
			// adding two spans, -1 for removed
			last[i].span[i] = last[i].span[i] + ent.span[i] - 1; 
			i++;
		}
		
		// update spans above height for last[] 
		// but, unaffected next[] pointers!
		while (last[i] != null) {
			last[i].span[i]--;
			i++;
		}
		
		// NOTE: last[] is null for levels above maxLevel
		// So, update span of un-used last[] Entries
		for(i = 0; i < POSSIBLE_LEVELS; i++){
			if(head.next[i] == tail)
				head.span[i] = size + 1;
		}
		// Successfully removed and merged 
		// the proper links in next[] and values in span[].
		size--;
		
		return ent.element;
	}
	
	/**
	 * Return the number of elements in the list
	 * @return the size of the list
	 */
	public int size() {
		return this.size;
	}
	
	//---------------------------- MAIN ----------------------------//
	public static void main(String[] args) {
		
		SkipList<Integer> sk = new SkipList <>();
		for (int i = 1; i < 130; i++) sk.add(i);
		
		//sk.add('c'); sk.add('b'); sk.add('f'); 
		// sk.add('e'); sk.add('d'); sk.add('a'); sk.add('g');
		
		System.out.println("Original Skip list: ");
		// sk.printLevels();
		//System.out.println();
		sk.printList();
		// System.out.println();
		// sk.printListSpan();
		System.out.println();
		int n = 0;
		
		//System.out.println("Linear: "+sk.getLinear(n));;
		//System.out.println("Log: \t"+sk.getLog(n));;
		sk.rebuild();
		
		System.out.println("Rebuilt Skip list: ");
		// sk.printLevels();
		//System.out.println();
		sk.printList();
		// System.out.println();
		// sk.printListSpan();
	}
}
/**
 * Future work:
 * 1. rebuild() - make it recursive
 * 2. rebuild() - use Integer.numberOfLeadingZeros() instead of powersOfTwo?
 */
