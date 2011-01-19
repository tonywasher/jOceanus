package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetFrequency {

	/**
	 * NamedArea for Frequencies
	 */
	private static final String Frequencies  = "Frequencies";
	
	/**
	 *  Load the Frequencies from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		Frequency.List 	myList;
		Range[] 		myRange;
		Sheet   		mySheet;
		Cell    		myTop;
		Cell    		myBottom;
		Cell    		myCell;
		int     		myCol;
		int     		myTotal;
		int				mySteps;
		int     		myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Frequencies);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(Frequencies)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of frequencies */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of frequencies */
				myList = pData.getFrequencys();
			
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
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Frequencies",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Frequencies from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		Frequency.List 	myList;
		Range[] 		myRange;
		Sheet   		mySheet;
		Cell    		myTop;
		Cell    		myBottom;
		Cell    		myCell;
		long    		myID;
		int     		myCol;
		int     		myTotal;
		int				mySteps;
		int     		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Frequencies);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Frequencies)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of frequencies */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of frequencies */
				myList = pData.getFrequencys();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
					/* Access the cell by reference */
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
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Frequencies",
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
		DataList<Frequency>.ListIterator	myIterator;
		Frequency.List 						myFreqs;
		Frequency							myCurr;
		int									myRow;
		int									myCount;
		int									myTotal;
		int									mySteps;
		jxl.write.Label						myCell;
	
		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(Frequencies)) return false;
			
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Create the sheet */
			mySheet	= pWorkbook.createSheet(Frequencies, 0);
		
			/* Access the frequencies */
			myFreqs	  = pData.getFrequencys();
		
			/* Count the number of frequencies */
			myTotal   = myFreqs.size();
			
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
			
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
			
			/* Access the iterator */
			myIterator = myFreqs.listIterator();
			
			/* Loop through the frequencies */
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
				pWorkbook.addNameArea(Frequencies, mySheet, 0, 0, 1, myRow-1);
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create Frequencies",
								e);
		}
		
		/* Return to caller */
		return true;
	}	
}
