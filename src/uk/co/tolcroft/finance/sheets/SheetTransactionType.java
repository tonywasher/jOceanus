package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetTransactionType {

	/**
	 * NamedArea for Transaction Types
	 */
	private final static String TransTypes   = "TransactionTypes";
	
	/**
	 * Alternative NamedArea for Transaction Types
	 */
	private final static String TransTypes1  = "TransType";
	
	/**
	 *  Load the Transaction Types from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
			 					  		 Workbook	pWorkbook,
			 					  		 DataSet	pData) throws Exception {
		/* Local variables */
		TransactionType.List 	myList;
		Range[] 				myRange;
		Sheet   				mySheet;
		Cell    				myTop;
		Cell    				myBottom;
		Cell    				myCell;
		int     				myCol;
		int     				myTotal;
		int						mySteps;
		int     				myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(TransTypes1);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(TransTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of transaction types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of transaction types */
				myList = pData.getTransTypes();
			
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
								"Failed to Load Transaction Types",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Transaction Types from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
										Workbook	pWorkbook,
										DataSet		pData) throws Exception {
		/* Local variables */
		TransactionType.List 	myList;
		Range[] 				myRange;
		Sheet   				mySheet;
		Cell    				myTop;
		Cell    				myBottom;
		Cell    				myCell;
		byte[]					myNameBytes;
		int						myID;
		int     				myCol;
		int     				myTotal;
		int						mySteps;
		int     				myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(TransTypes);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(TransTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of transaction types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of transaction types */
				myList = pData.getTransTypes();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
					/* Access the cell by reference */
					myCell = mySheet.getCell(myCol, i);
					myID   = Integer.parseInt(myCell.getContents());
					myCell = mySheet.getCell(myCol+1, i);
				
					/* Access the name bytes */
					myNameBytes = Utils.BytesFromHexString(myCell.getContents());
					
					/* Add the value into the finance tables */
					myList.addItem(myID, myNameBytes);
				
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Transaction Types",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Write the Transaction Types to a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to write to
	 *  @param pData the data set to write from
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean writeBackup(statusCtl 			pThread,
			 						  	 WritableWorkbook	pWorkbook,
			 						  	 DataSet			pData) throws Exception {
		WritableSheet 							mySheet;
		DataList<TransactionType>.ListIterator	myIterator;
		TransactionType.List					myTranTypes;
		TransactionType							myCurr;
		int										myRow;
		int										myCount;
		int										myTotal;
		int										mySteps;
		jxl.write.Label							myCell;
	
		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(TransTypes)) return false;
			
			/* Create the sheet */
			mySheet	= pWorkbook.createSheet(TransTypes, 0);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Access the transaction Types */
			myTranTypes = pData.getTransTypes();
		
			/* Count the number of transaction types */
			myTotal   = myTranTypes.size();
			
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
			
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
			
			/* Access the iterator */
			myIterator = myTranTypes.listIterator();
			
			/* Loop through the transaction types */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier and Name cells */
				myCell = new jxl.write.Label(0, myRow, 
											 Integer.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				
				/* Add the name to the list */
				myCell = new jxl.write.Label(1, myRow,
											 Utils.HexStringFromBytes(myCurr.getNameBytes()));
				mySheet.addCell(myCell);
				
				/* Report the progress */
				myCount++;
				myRow++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
		
			/* Add the Range name */
			if (myRow > 0)
				pWorkbook.addNameArea(TransTypes, mySheet, 0, 0, 1, myRow-1);
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create TransTypes",
								e);
		}
		
		/* Return to caller */
		return true;
	}	
}
