package com.lea.coding.challenge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Simple excel application
 * @author Lea
 *
 */
public class Spreadsheet {

	private final static String EMPTY_STRING = "";
	private final static String EQUAL_SIGN = "=";
	private final static Pattern splitRegex = Pattern.compile ("[a-zA-Z0-9]");
	private final static Pattern operationRegex = Pattern.compile("[+-/*=//]");
	private static String TAB = "\t";
	private static String NEWLINE = "\n";
	
	private static Spreadsheet getInstance() {
		return new Spreadsheet();
	}
	
	public static void main(String[] args) {
		List<String> worksheetList = new ArrayList<String> ();
		Scanner sc = new Scanner(System.in);
		Spreadsheet spreadSheet = Spreadsheet.getInstance();
		
		try {
			println("Enter size of worksheet (row * column):");		
			String userInputWorksheetSize = sc.nextLine();			
			
			if(spreadSheet.isWorksheetSizeValid(userInputWorksheetSize)) {
		        for (int i=0; i < getWorksheetSize(userInputWorksheetSize) + 1; i++) {
		        	String scNext = sc.nextLine();
		        	if(!EMPTY_STRING.equals(scNext)) {
		        		worksheetList.add(scNext);
		        	}
		        }
	   
		        spreadSheet.process(worksheetList, userInputWorksheetSize);				
			} else {
				println("Invalid worksheet size. Please input correct value (e.g. 2*2)");
			}

		} catch (Exception e) {
			print("Exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			sc.close();
		}
	}
	
	/**
	 * Validate if worksheet value is numeric
	 * @param userInput
	 * @return
	 */
	private boolean isWorksheetSizeValid(String userInput) {
		boolean isNumeric = true;
		
		try{
			getRowSize(userInput);
			getColumnSize(userInput);
		} catch (Exception e) {
			isNumeric = false;
		}
		
		return isNumeric;
	}
	
	private void process(List<String> worksheetList, String input) throws Exception {

        Map<String, LinkedList<String>> rawWorksheet = convertToMap(worksheetList);

        Map<String, Integer> worksheet = evaluateSpreadsheet (rawWorksheet);
        
        //Print details
        prettyPrint(worksheet, getRowSize(input), getColumnSize(input));
	}
	
	/**
	 * 
	 * @param value
	 * @return Row size
	 */
	private int getRowSize(String value) {
		String[] output = operationRegex.split(value);
		return Integer.parseInt(output[0].trim());
	}
	
	/**
	 * 
	 * @param value
	 * @return Column size
	 */
	private int getColumnSize(String value) {
		String[] output = operationRegex.split(value);
		return Integer.parseInt(output[1].trim());
	}
	
	
	/**
	 * Print the processed worksheet
	 * @param rowSize
	 * @param columnSize
	 * @param worksheet
	 */
	private void prettyPrint(Map<String, Integer> worksheet, int rowSize, int columnSize) {
		char alphabet = 'A';
		int ctrRow = 0;
		int ctr = 1;
		
		//Print Header
		//============
		print(TAB);
		while (ctrRow < columnSize) {
			print(alphabet++ + TAB);
			ctrRow++;
		}
		
		//Print Details
		//=============
		print(NEWLINE);
		alphabet = 'a';//reset
		
		for(; ctr < rowSize + 1; ctr++) {
			print(ctr + TAB);
			for(int alphaCtr = 1; alphaCtr < columnSize + 1; alphaCtr++) {
				print(getFieldValue(worksheet, alphabet, ctr) );
				alphabet++;
			}
			
			alphabet = 'a';//reset
			print(NEWLINE );
			
		}
	}
	
	/**
	 * Should be case insensitive when retrieving field values
	 * 		ex: a1 is the same as A1
	 * @param worksheet
	 * @param alphabet
	 * @param ctr
	 * @return
	 */
	private String getFieldValue(Map<String, Integer> worksheet, char alphabet, int ctr) {
		Integer fieldVal = worksheet.get("" + alphabet + ctr) != null ? 
							worksheet.get("" + alphabet + ctr) : 
							worksheet.get(("" + alphabet).toUpperCase() + ctr);//check against uppercase
		return (fieldVal != null ? fieldVal : "0") + TAB;
	}
	
	private static void print(String valueToPrint) {
		System.out.print(valueToPrint);
	}
	
	private static void println(String valueToPrint) {
		System.out.println(valueToPrint);
	}
	
	/**
	 * Calculate the worksheet size based on the row and column provided by user
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private static int getWorksheetSize (String input) throws Exception{
		String operator = null;
		
		LinkedList<String> worksheetSize = new LinkedList<String>();
		
		for (String value : splitRegex.split(input)) {
			if(!EMPTY_STRING.equals(value)) {
				operator = value.trim();
			}
		}
		
		boolean isOperatorEmpty = true;
		for (String s : operationRegex.split(input)) {
			worksheetSize.add(s.trim());
			
			if(isOperatorEmpty) {
				worksheetSize.add(operator);
				isOperatorEmpty = false;
			}						
		}
		
		return (int)Operator.getInstance().evaluate(worksheetSize);
	}

	/**
	 * Processed the value and compute formula based on user input
	 * @param worksheet
	 * @return Map that contains key and value only
	 * @throws Exception
	 */
	private Map<String, Integer> evaluateSpreadsheet (Map<String, LinkedList<String>> worksheet) throws Exception{
		Map<String, Integer> processedWorksheet = new HashMap<String, Integer>();
		
		for (String key : worksheet.keySet()) {
			processedWorksheet.put(key, Operator.getInstance().evaluate(worksheet.get(key), processedWorksheet));
		}
		
		return processedWorksheet;
	}
	
	/**
	 * Convert the raw data to Map before computing the value
	 * @param worksheet
	 * @return
	 */
	private Map<String, LinkedList<String>> convertToMap(List<String> worksheet) {
		LinkedList<String> row = null;  
		Map<String, LinkedList<String>> worksheetMap = new LinkedHashMap<String, LinkedList<String>>();
		String[] strOperators = null;
		String[] strFieldsAndValue = null;
		
		for(String contents : worksheet) {
			row = new LinkedList<String>();
			int ctr=0;
			strOperators = splitRegex.split(contents);
			strFieldsAndValue = operationRegex.split(contents);
			
			//Retrieve value
			for (String s : strFieldsAndValue) {
				row.add(s.trim());
				ctr++;
			}
			
			//Retrieve operators
			ctr=1;//reset
			for (String s : strOperators) {
				if(!EMPTY_STRING.equals(s)) {
					row.add(ctr, s);
					ctr+=2;
				}
			}
			
			//Save the number, reference and operator to linkedlist
			ctr=1;//reset
			String key = null;
			for (String s : row) {
				if(EQUAL_SIGN.equals(s.trim())) {
					row.remove(0);//remove the key
					row.remove(0);//Remove the =
					worksheetMap.put(key.toUpperCase(), row);
					break;
				}
				key = s;
			}
		}
		
		return worksheetMap;

	}
	
}
