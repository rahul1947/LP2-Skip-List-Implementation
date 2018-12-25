# Long Project LP2: Skip List Implementation

### Authors:
 * [Rahul Nalawade](https://github.com/rahul1947) - rsn170330@utdallas.edu
 * [Dhwani Raval](https://github.com/dhwaniraval) - dsr170230@utdallas.edu
 * Varun Parashar - vxp171830@utdallas.edu
 * [Arpita Agrawal](https://github.com/ArpitaAgrawal1305) - aua170030@utdallas.edu

### End Date: 
 * Sunday, October 14, 2018
 ______________________________________________________________________________

## A. OVERVIEW

Please read the [details](https://github.com/rahul1947/LP2-Skip-List-Implementation/blob/master/SkipListDetails.pdf) about the SkipList. 

### 1. ENTRY ATTRIBUTES: 

- Every Entry has height {which tells us how many non-null entries it's 
  next[] has}
- It also has span[], span[i]: storing distance of the Entry in next[i] 
  from the current Entry

### 2. SKIPLIST ATTRIBUTES: 

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
_______________________________________________________________________________

## B. OBSERVATION:

- Our span[] is of type integer, which could have maximum value as 
  (size+1, when head pointing to tail). 
  
  Now, limit of int is 2^31 - 1. So, size can be no greater than 2^31 - 2. 
  
  But we had our POSSIBLE_LEVELS as 33, which could fit in 2^33 - 1 elements.
  Hence, our top two levels will always have pointers from head to tail. 
  And we could've set POSSIBLE_LEVELS to 31, instead of 33.
_______________________________________________________________________________

## C. RESULTS:

### 1. add-remove-contains operations only:

|    `File`     |`# Operation` |`Time (mSec)` |`Memory (used/avail)`| 
|:------------:|-------------:|------------:|--------------------:| 
| lp2-t01.txt  |           50 |           7 |       1 MB / 117 MB | 
| lp2-t02.txt  |          200 |          11 |       1 MB / 117 MB | 
| lp2-t03.txt  |         1000 |          22 |       3 MB / 117 MB | 
| lp2-t04.txt  |        50000 |         179 |      32 MB / 117 MB | 
| lp2-t05.txt  |       100000 |         292 |      61 MB / 147 MB | 
| lp2-t06.txt  |      1000000 |        2709 |     286 MB / 583 MB | 
 
### 2. add-remove-contains-floor-ceiling-get-first-last operations: 

|    `File`     |`# Operation` |`Time (mSec)` |`Memory (used/avail)`| 
|:------------:|-------------:|------------:|--------------------:| 
| lp2-t11.txt  |           50 |           5 |       1 MB / 117 MB | 
| lp2-t12.txt  |          100 |           8 |       1 MB / 117 MB | 
| lp2-t13.txt  |          200 |          11 |       1 MB / 117 MB | 
| lp2-t14.txt  |         1000 |          18 |       3 MB / 117 MB | 
| lp2-t15.txt  |        50000 |         182 |      29 MB / 117 MB | 
| lp2-t16.txt  |       100000 |         266 |      57 MB / 147 MB | 
| lp2-t17.txt  |      1000000 |        2659 |     247 MB / 583 MB |  

NOTE: 
- Time and Memory might change, as you run the test the program on a 
  different system, but they could be comparable to the above values.
  
Existing Processor: Intel® Core™ i5-8250U CPU @ 1.60GHz × 8 
Memory: 7.5 GiB
_______________________________________________________________________________

## D. How to Run

1. Extract the rsn170330.zip 

2. Compile and Run: 
```
$javac rsn170330/*.java
$java rsn170330.SkipListDriver lp2-test/lp2-t14.txt
```
_______________________________________________________________________________