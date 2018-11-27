/**
 * CS 5V81.001. Implementation of data structures and algorithms 
 * Long Project LP2: Skip List Implementation
 * @author Rahul Nalawade (rsn170330)
 * @author Arpita Agrawal (aua170030)
 * @author Simran Rawlani (sxr174130)
 * @author Yash Madane (yxm172130)
 * 
 */

1. Extract the rsn170330.zip 

2. Compile: 
	$javac rsn170330/*.java

3. Run: 
	$java rsn170330.SkipListDriver lp2-inputs/lp2-in14.txt

---------------------------------------------------------------------------
#OVERVIEW
As you go through the code, we've tried to do necessary comments for 
almost all concepts. Here are some important things to note:

- We have the same public interface as in the Starter code provided.

ENTRY ATTRIBUTES:
- Every Entry has height {which tells us how many non-null entries it's 
  next[] has}
- It also has span[], span[i]: storing distance of the Entry in next[i] 
  from the current Entry

SKIPLIST ATTRIBUTES: 
- last[]: an array of Entry<T>, 
  last[i]: Entry at which search came down from level i

- distanceTraversed[i]: an array of integers, 
  distanceTraversed[i]: distance traversed on level i, as search came down 
  from last[i] to level i-1

- There are few more attributes which are described at the time of their 
  definition, in the code itself.

- for rebuild(), we have used iterative approach rather than a recursive 
  one (divide-and-conquer). Initially, when we started we couldn't think 
  of it that way, and hope it doesn't affect any EXCELLENCE CREDIT.

- There are some private methods which you may use to print the skip list 
  - call printList() to print the list with next[] references. 
  - call printListSpan() to print the list with span[] values for each Entry

----------------------------------------------------------------------------
#OBSERVATION:
- Our span[] is of type integer, which could have maximum value as 
  (size+1, when head pointing to tail). 
  
  Now, limit of int is 2^31 - 1. So, size can be no greater than 2^31 - 2. 
  
  But we had our POSSIBLE_LEVELS as 33, which could fit in 2^33 - 1 elements.
  Hence, our top two levels will always have pointers from head to tail. 
  And we could've set POSSIBLE_LEVELS to 31, instead of 33.


