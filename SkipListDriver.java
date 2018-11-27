package rsn170330.lp2;

import rsn170330.lp2.Timer;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

//Driver program for skip list implementation.

public class SkipListDriver {
	public static void main(String[] args) throws FileNotFoundException {
		Scanner sc;
		
		if (args.length > 0) {
			File file = new File(args[0]);
			sc = new Scanner(file);
		} 
		else {
			sc = new Scanner(System.in);
		}
		
		String operation = "";
		long operand = 0;
		int modValue = 999983;
		long result = 0;
		Long returnValue = null;
		
		SkipList<Long> skipList = new SkipList<>();
		
		// Initialize the timer
		Timer timer = new Timer();

		while (!((operation = sc.next()).equals("End"))) {
			
			switch (operation) {
				case "Add": {
					operand = sc.nextLong();
					if(skipList.add(operand)) {
						result = (result + 1) % modValue;
					}
					break;
				}
				case "Ceiling": {
					operand = sc.nextLong();
					returnValue = skipList.ceiling(operand);
					// System.out.println("Ceiling: " + returnValue);
					
					if (returnValue != null) {
						result = (result + returnValue) % modValue;
					}
					break;
				}
				case "First": {
					returnValue = skipList.first();
					if (returnValue != null) {
						result = (result + returnValue) % modValue;
					}
					break;
				}
				case "Get": {
					int intOperand = sc.nextInt();
					returnValue = skipList.get(intOperand);
					// System.out.println("Get: " + returnValue);
					// skipList.printListSpan();
					
					if (returnValue != null) {
						result = (result + returnValue) % modValue;
					}
					break;
				}
				case "Last": {
					returnValue = skipList.last();
					if (returnValue != null) {
						result = (result + returnValue) % modValue;
					}
					break;
				}
				case "Floor": {
					operand = sc.nextLong();
					returnValue = skipList.floor(operand);
					// System.out.println("Floor: " + returnValue);
					
					if (returnValue != null) {
						result = (result + returnValue) % modValue;
					}
					break;
				}
				case "Remove": {
					operand = sc.nextLong();
					if (skipList.remove(operand) != null) {
						result = (result + 1) % modValue;
					}
					break;
				}
				case "Contains":{
					operand = sc.nextLong();
					if (skipList.contains(operand)) {
						result = (result + 1) % modValue;
					}
					break;
				}

			} 
			// System.out.print(result + "\n");
		}
		
		
		//int n = 33000;
		
		//System.out.println("Linear: "+skipList.getLinear(n));;
		//System.out.println("Log: \t"+skipList.getLog(n));;
		
		// End Time
		timer.end();

		System.out.println(result);
		System.out.println(timer);
				
	}
}
