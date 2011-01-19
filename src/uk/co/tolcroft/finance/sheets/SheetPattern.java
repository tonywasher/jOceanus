package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.WritableWorkbook;
import jxl.write.WritableSheet;
import jxl.write.WritableCellFormat;
import jxl.write.DateFormats;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetPattern {
	/**
	 * NamedArea for Patterns
	 */
	private static final String Patterns 	   = "Patterns";
	
	/**
	 *  Load the Patterns from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		Pattern.List		myList;
		Range[]   			myRange;
		Sheet     			mySheet;
		Cell      			myTop;
		Cell      			myBottom;
		int       			myCol;
		java.util.Date		myDate;
		String    			myAccount;
		String    			myDesc;
		String    			myPartner;
		String    			myTransType;
		String    			myAmount;
		String    			myFrequency;
		boolean   			isCredit;
		DateCell  			myDateCell;
		BooleanCell 		myBoolCell;
		int       			myTotal;
		int					mySteps;
		int       			myCount = 0;
		
		/* Protect against exceptions*/
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Patterns);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Patterns)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of patterns */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of patterns */
				myList = pData.getPatterns();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
				
					/* Access strings */
					myAccount 	= mySheet.getCell(myCol, i).getContents();
					myDesc    	= mySheet.getCell(myCol+2, i).getContents();
					myAmount  	= mySheet.getCell(myCol+3, i).getContents();
					myPartner 	= mySheet.getCell(myCol+4, i).getContents();
					myTransType = mySheet.getCell(myCol+5, i).getContents();
					myFrequency = mySheet.getCell(myCol+7, i).getContents();
				
					/* Handle Date */
					myDateCell = (DateCell)mySheet.getCell(myCol+1, i);
					myDate     = myDateCell.getDate();
				
					/* Handle isCredit */
					myBoolCell 	= (BooleanCell)mySheet.getCell(myCol+6, i);
					isCredit 	= myBoolCell.getValue();
				
					/* Add the value into the finance tables */
					myList.addItem(0,
					               myDate,
					               myDesc,
					               myAmount,
					               myAccount,
					               myPartner,
					               myTransType,
					               myFrequency,
					               isCredit);
				
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		/* Handle exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to Load Patterns",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Patterns from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		Pattern.List		myList;
		Range[]     		myRange;
		Sheet       		mySheet;
		Cell        		myTop;
		Cell        		myBottom;
		String      		myDesc;
		String      		myAmount;
		long	    		myAccount;
		long        		myPartner; 
		long        		myFrequency;
		boolean     		isCredit;
		long        		myTranType;
		long        		myID;
		Cell        		myCell;
		DateCell    		myDateCell;
		BooleanCell 		myBoolCell;
		java.util.Date      myDate;
		int         		myCol;
		int         		myTotal;
		int					mySteps;
		int         		myCount = 0;
		
		/* Protect against exceptions*/
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Patterns);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Patterns)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myCol     = myTop.getColumn();
			
				/* Count the number of Patterns */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of patterns */
				myList = pData.getPatterns();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
				
					/* Access ids */
					myCell    	= mySheet.getCell(myCol, i);
					myID      	= Long.parseLong(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+1, i);
					myAccount  	= Long.parseLong(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+2, i);
					myPartner  	= Long.parseLong(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+5, i);
					myTranType 	= Long.parseLong(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+6, i);
					myFrequency	= Long.parseLong(myCell.getContents());
				
					/* Access date */
					myDateCell = (DateCell)mySheet.getCell(myCol+3, i);
					myDate     = myDateCell.getDate();
			    
					/* Access Flag */
					myBoolCell = (BooleanCell)mySheet.getCell(myCol+4, i);
					isCredit   = myBoolCell.getValue();
			    
					/* Access the values */
					myDesc         = mySheet.getCell(myCol+7, i).getContents();
					myAmount       = mySheet.getCell(myCol+8, i).getContents();
			    
					/* Add the pattern */
					myList.addItem(myID,
						           myDate,
						           myDesc,
				  		           myAmount,
						           myAccount,
						           myPartner,
						           myTranType,
						           myFrequency,
						           isCredit);
				
					/* Report the progress */
				    myCount++;
				    if ((myCount % mySteps) == 0) 
				    	if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Patterns",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Write the Account Types to a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to write to
	 *  @param pData the data set to write from
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean writeBackup(statusCtl 			pThread,
										 WritableWorkbook	pWorkbook,
		   	        					 DataSet			pData) throws Exception {
		WritableSheet 					mySheet;
		DataList<Pattern>.ListIterator	myIterator;
		Pattern.List					myPatterns;
		Pattern							myCurr;
		int								myRow;
		int								myCount;
		int								myTotal;
		int								mySteps;
		jxl.write.Label					myCell;
		jxl.write.Boolean				myCredit;
		jxl.write.DateTime				myDate;
		WritableCellFormat				myFormat;

		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(Patterns)) return false;
		
			/* Create the formats */
			myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
	
			/* Create the sheet */
			mySheet    = pWorkbook.createSheet(Patterns, 0);
	
			/* Access the patterns */
			myPatterns = pData.getPatterns();
	
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Count the number of patterns */
			myTotal   = myPatterns.size();
		
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
		
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
		
			/* Access the iterator */
			myIterator = myPatterns.listIterator();
			
			/* Loop through the patterns */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier cells */
				myCell = new jxl.write.Label(0, myRow, 
											 Long.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(1, myRow, 
											 Long.toString(myCurr.getAccount().getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(2, myRow, 
											 Long.toString(myCurr.getPartner().getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(5, myRow, 
											 Long.toString(myCurr.getTransType().getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(6, myRow,
											 Long.toString(myCurr.getFrequency().getId()));
				mySheet.addCell(myCell);
			
				/* Create the Amount cells */
				myCell = new jxl.write.Label(8, myRow, 
											 myCurr.getAmount().format(false));
				mySheet.addCell(myCell);
			
				/* Create the IsCredit cells */
				myCredit = new jxl.write.Boolean(4, myRow, myCurr.isCredit(), myFormat);
				mySheet.addCell(myCredit);
				
				/* Create the Desc cells */
				myCell = new jxl.write.Label(7, myRow, myCurr.getDesc());
				mySheet.addCell(myCell);
				
				/* Create the Date cells */
				myDate = new jxl.write.DateTime(3, myRow, 
												myCurr.getDate().getDate(),
												myFormat);
				mySheet.addCell(myDate);
			
				/* Report the progress */
				myCount++;
				myRow++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
	
			/* Add the Range name */
			if (myRow > 0)
				pWorkbook.addNameArea(Patterns, mySheet, 0, 0, 8, myRow-1);
		}

		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create Patterns",
								e);
		}
	
		/* Return to caller */
		return true;
	}	
}
