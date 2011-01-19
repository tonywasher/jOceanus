package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetAccountType {
	/**
	 * NamedArea for Accounts
	 */
	private static final String AccountTypes = "AccountTypes";
	
	/**
	 *  Load the Account Types from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		AccountType.List 	myList;
		Range[] 			myRange;
		Sheet   			mySheet;
		Cell    			myTop;
		Cell    			myBottom;
		Cell    			myCell;
		int     			myCol;
		int     			myTotal;
		int					mySteps;
		int     			myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(AccountTypes);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(AccountTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of account types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of account types */
				myList = pData.getAccountTypes();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
			     	 i <= myBottom.getRow();
			     	 i++) {
					/* Access the cell by reference */
					myCell = mySheet.getCell(myCol, i);
				
					/* Add the value into the finance tables */
					myList.addItem(0, myCell.getContents());
				
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
								"Failed to Load Account Types",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Account Types from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		AccountType.List 	myList;
		Range[] 			myRange;
		Sheet   			mySheet;
		Cell    			myTop;
		Cell    			myBottom;
		Cell    			myCell;
		long    			myID;
		int     			myCol;
		int     			myTotal;
		int					mySteps;
		int     			myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(AccountTypes);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(AccountTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of account types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of account types */
				myList = pData.getAccountTypes();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
					/* Access the cells by reference */
					myCell = mySheet.getCell(myCol, i);
					myID   = Long.parseLong(myCell.getContents());
					myCell = mySheet.getCell(myCol+1, i);
				
					/* Add the value into the finance tables */
					myList.addItem(myID, myCell.getContents());
				
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
								"Failed to Load Account Types",
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
		WritableSheet 						mySheet;
		DataList<AccountType>.ListIterator	myIterator;
		AccountType.List 					myAcTypes;
		AccountType							myCurr;
		int									myRow;
		int									myCount;
		int									myTotal;
		int									mySteps;
		jxl.write.Label						myCell;
	
		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(AccountTypes)) return false;
			
			/* Create the sheet */
			mySheet	= pWorkbook.createSheet(AccountTypes, 0);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Access the account Types */
			myAcTypes = pData.getAccountTypes();
		
			/* Count the number of account types */
			myTotal   = myAcTypes.size();
			
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
			
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
	
			/* Access the iterator */
			myIterator = myAcTypes.listIterator();
			
			/* Loop through the account types */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier and Name cells */
				myCell = new jxl.write.Label(0, myRow, 
											 Long.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				
				/* Add the name to the list */
				myCell = new jxl.write.Label(1, myRow, myCurr.getName());
				mySheet.addCell(myCell);
				
				/* Report the progress */
				myCount++;
				myRow++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
		
			/* Add the Range name */
			if (myRow > 0)
				pWorkbook.addNameArea(AccountTypes, mySheet, 0, 0, 1, myRow-1);				
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create AccountTypes",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
