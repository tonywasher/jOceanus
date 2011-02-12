package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetRate {
	/**
	 * NamedArea for Rates
	 */
	private static final String Rates 	   = "Rates";
	
	/**
	 *  Load the Rates from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		Rate.List		myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		String    		myAccount;
		String    		myRate;
		String    		myBonus;
		java.util.Date	myExpiry;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Rates);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Rates)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of rates */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of rates */
				myList = pData.getRates();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
			     	 i <= myBottom.getRow();
			     	 i++) {
				
					/* Access account */
					myAccount = mySheet.getCell(myCol, i).getContents();
				
					/* Handle Rate */
					myCell = mySheet.getCell(myCol+1, i);
					myRate = myCell.getContents();
				
					/* Handle bonus which may be missing */
					myCell  = mySheet.getCell(myCol+2, i);
					myBonus = null;
					if (myCell.getType() != CellType.EMPTY) {
						myBonus = myCell.getContents();
					}
				
					/* Handle expiration which may be missing */
					myCell     = mySheet.getCell(myCol+3, i);
					myExpiry = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDateCell = (DateCell)myCell;
						myExpiry = myDateCell.getDate();
					}
				
					/* Add the value into the finance tables */
					myList.addItem(0,
					               myAccount,
					               myRate,
					               myExpiry,
					               myBonus);
				
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
								"Failed to Load Rates",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Rates from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		Rate.List		myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		String    		myRate;
		String    		myBonus;
		java.util.Date	myEndDate;
		int      		myID;
		int      		myAccountID;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Rates);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Rates)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of rates */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of rates */
				myList = pData.getRates();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
				
					/* Access id and account id */
					myCell    	= mySheet.getCell(myCol, i);
					myID      	= Integer.parseInt(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+1, i);
					myAccountID	= Integer.parseInt(myCell.getContents());
				
					/* Handle Rate */
					myCell     	= mySheet.getCell(myCol+2, i);
					myRate 		= myCell.getContents();
				
					/* Handle bonus which may be missing */
					myCell     = mySheet.getCell(myCol+3, i);
					myBonus	   = null;
					if (myCell.getType() != CellType.EMPTY) {
						myBonus = myCell.getContents();
					}
				
					/* Handle endDate which may be missing */
					myCell     = mySheet.getCell(myCol+4, i);
					myEndDate  = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDateCell = (DateCell)myCell;
						myEndDate  = myDateCell.getDate();
					}
				
					/* Add the value into the finance tables */
					myList.addItem(myID,
						           myAccountID,
						           myRate,
						           myEndDate,
						           myBonus);
				
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Rates",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Write the Rates to a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to write to
	 *  @param pData the data set to write from
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean writeBackup(statusCtl 			pThread,
										 WritableWorkbook	pWorkbook,
		   	        					 DataSet			pData) throws Exception {
		WritableSheet 				mySheet;
		DataList<Rate>.ListIterator	myIterator;
		Rate.List					myRates;
		Rate						myCurr;
		int							myRow;
		int							myCount;
		int							myTotal;
		int							mySteps;
		jxl.write.Label				myCell;
		jxl.write.DateTime			myDate;
		WritableCellFormat			myFormat;

		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(Rates)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Create the formats */
			myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
	
			/* Create the sheet */
			mySheet    = pWorkbook.createSheet(Rates, 0);
	
			/* Access the Rates */
			myRates = pData.getRates();
	
			/* Count the number of rates */
			myTotal   = myRates.size();
		
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
		
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
		
			/* Access the iterator */
			myIterator = myRates.listIterator();
			
			/* Loop through the rates */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier cell */
				myCell = new jxl.write.Label(0, myRow, 
											 Integer.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(1, myRow,
											 Integer.toString(myCurr.getAccount().getId()));
				mySheet.addCell(myCell);
			
				/* Create the Rate cells */
				myCell = new jxl.write.Label(2, myRow, 
											 myCurr.getRate().format(false));
				mySheet.addCell(myCell);
				if (myCurr.getBonus() != null) {
					myCell = new jxl.write.Label(3, myRow, 
												 myCurr.getBonus().format(false));
					mySheet.addCell(myCell);
				}
				
				/* Create the Date cells */
				if (myCurr.getEndDate() != null) {
					myDate = new jxl.write.DateTime(4, myRow, 
													myCurr.getEndDate().getDate(),
													myFormat);
					mySheet.addCell(myDate);
				}
			
				/* Report the progress */
				myCount++;
				myRow++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
	
			/* Add the Range name */
			if (myRow > 0)
				pWorkbook.addNameArea(Rates, mySheet, 0, 0, 4, myRow-1);
		}

		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create Rates",
								e);
		}
	
		/* Return to caller */
		return true;
	}
	
}
