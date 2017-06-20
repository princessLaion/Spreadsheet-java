package com.lea.coding.challenge;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Pattern;

public class Operator {
	private final static String ADD = "+";
	private final static String MULTIPLY = "*";
	private final static String DIVIDE = "/";
	private final static String SUBTRACT = "-";
	
	
	private Operator(){
		
	}
	
	public static Operator getInstance(){
		return new Operator();
	}
	
	public int evaluate(LinkedList<String> rawValue) throws Exception{
		return evaluate(rawValue, new HashMap<String, Integer>());
	}
	
	public int evaluate(LinkedList<String> rawValue, Map<String, Integer> rowColumnReference) throws Exception{
		int output = 0;
		int ctr = 0;
		
		if(rawValue == null || rawValue.isEmpty()) {
			return output;
		}
		
		if(rawValue.size() == 1) {
			output = Integer.parseInt(rawValue.get(0));
		} else {
			for(String value : rawValue) {	
				if(value == null ) {
					continue;
				}
				
				if(ADD.equals(value.trim())) {					
					output = getFirstValue(rawValue, rowColumnReference, ctr)  + 
							 getSecondValue(rawValue, rowColumnReference, ctr);
				} else if(SUBTRACT.equals(value.trim())) {
					output = getFirstValue(rawValue, rowColumnReference, ctr)  - 
							 getSecondValue(rawValue, rowColumnReference, ctr);
				} else if(MULTIPLY.equals(value.trim())) {
					output = getFirstValue(rawValue, rowColumnReference, ctr)  * 
							 getSecondValue(rawValue, rowColumnReference, ctr);
				} else if(DIVIDE.equals(value.trim())) {
					output = getFirstValue(rawValue, rowColumnReference, ctr)  / 
							 getSecondValue(rawValue, rowColumnReference, ctr);
				}
				ctr++;
			}
		}

		return output;
	}
	
	/**
	 * 
	 * @param value
	 * @param rowColumnReference
	 * @param ctr
	 * @return 0 if reference was not found
	 */
	private static int getFirstRefVal (LinkedList<String> value, Map<String, Integer> rowColumnReference, int ctr) {
		return rowColumnReference.get(value.get(ctr - 1).toUpperCase()) != null ? rowColumnReference.get(value.get(ctr - 1).toUpperCase()) : 0;
	}
	
	private static int getSecondRefVal (LinkedList<String> value, Map<String, Integer> rowColumnReference, int ctr) {
		return rowColumnReference.get(value.get(ctr + 1).toUpperCase()) != null ? rowColumnReference.get(value.get(ctr + 1).toUpperCase()) : 0;
	}
	
	private int getFirstValue(LinkedList<String> value, Map<String, Integer> rowColumnReference, int ctr) throws Exception{
		int firstVal = 0;
		try{
			firstVal = isReference(value.get(ctr - 1)) ? 
					getFirstRefVal(value, rowColumnReference, ctr) :  
					Integer.parseInt(value.get(ctr - 1));
		} catch (Exception e) {
			throw new Exception("Invalid Reference Value, cannot convert to numeric value: " + rowColumnReference.get(value.get(ctr - 1)));
		}
		return firstVal;
	}
		
	private int getSecondValue(LinkedList<String> value, Map<String, Integer> rowColumnReference, int ctr) throws Exception{
		int secondVal = 0;
		try{
			secondVal = isReference(value.get(ctr + 1)) ? 
					getSecondRefVal(value, rowColumnReference, ctr) :  
					Integer.parseInt(value.get(ctr + 1));
		} catch (Exception e) {
			throw new Exception("Invalid Reference Value, cannot convert to numeric value: " + rowColumnReference.get(value.get(ctr + 1)));
		}
		return secondVal;
	}
	
	/**
	 * 
	 * @param value
	 * @return true, if the value contains alphabet, meaning it is a reference from other column on the worksheet
	 */
	private boolean isReference(String value) {
		Pattern p = Pattern.compile("[a-zA-Z]");
		return p.matcher(value).find();
	}
	
}
