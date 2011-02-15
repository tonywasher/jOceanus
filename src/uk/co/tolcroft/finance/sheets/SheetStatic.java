package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import uk.co.tolcroft.finance.core.Threads.statusCtl;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.DataList;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class SheetStatic {	
	/**
	 * SheetName for Static
	 */
	private static final String Static	   			= "Static";

	/**
	 * NamedRange for Static
	 */
	private static final String YearRange			= "YearRange";

	/**
	 * Simple class to hold YearRange 
	 */
	protected static class YearRange {
		private int theMinYear = 0;
		private int theMaxYear = 0;
		protected int getMinYear() { return theMinYear; }
		protected int getMaxYear() { return theMaxYear; }
		protected void setMinYear(int pYear) { theMinYear = pYear; }
		protected void setMaxYear(int pYear) { theMaxYear = pYear; }
	}
	
	/**
	 *  Load the Static from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @param pRange the range of tax years
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
			 							 Workbook	pWorkbook,
			 							 DataSet	pData,
			 							 YearRange	pRange) throws Exception {
		/* Local variables */
		Range[] 		myRange;
		Sheet   		mySheet;
		Cell    		myTop;
		Cell    		myCell;
		int     		myCol;
		int     		myRow;
		int     		myStages;
		Static.List 	myStatic;

		/* Find the range of cells */
		myRange = pWorkbook.findByName(YearRange);
	
		/* If we found the range OK */
		if ((myRange != null) && (myRange.length == 1)) {
			
			/* Access the relevant sheet and Cell references */
			mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
			
			myTop    = myRange[0].getTopLeft();
			myRow    = myTop.getRow();
			myCol    = myTop.getColumn();
		
			/* Access the Year Range */
			myCell = mySheet.getCell(myCol, myRow+1);
			pRange.setMinYear(Integer.parseInt(myCell.getContents()));
			myCell = mySheet.getCell(myCol+1, myRow+1);
			pRange.setMaxYear(Integer.parseInt(myCell.getContents()));
			
			/* Access the static */
			myStatic = pData.getStatic();
		
			/* Add the value into the finance tables (with no security as yet) */
			myStatic.addItem(0, Properties.CURRENTVERSION);
		}

		/* Calculate the number of stages */
		myStages = 14 + pRange.getMaxYear() - pRange.getMinYear();
		
		/* Declare the number of stages */
		return pThread.setNumStages(myStages);
	}
	
	/**
	 *  Load the Static Data from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		Range[] 			myRange;
		Sheet   			mySheet;
		Static.List 		myList;
		int					myID;
		int					myVersion;
		String				myControlKey;
		byte[]				mySecurityKey;
		byte[]				myInitVector;
		Cell    			myTop;
		Cell    			myBottom;
		Cell    			myCell;
		int     			myCol;
		int     			myTotal;
		int					mySteps;
		int     			myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the DataVersion */
			myRange = pWorkbook.findByName(Static);
					
			/* Declare the new stage */
			if (!pThread.setNewStage(Static)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {				
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of statics */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of statics */
				myList = pData.getStatic();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the range */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
					/* Access the id */
					myCell = mySheet.getCell(myCol, i);
					myID   = Integer.parseInt(myCell.getContents());

					/* Access the version */
					myCell = mySheet.getCell(myCol+1, i);
					myVersion = Integer.parseInt(myCell.getContents());

					/* Access the ControlKey */
					myCell = mySheet.getCell(myCol+2, i);
					myControlKey = myCell.getContents();

					/* Access the SecurityKey */
					myCell = mySheet.getCell(myCol+3, i);
					mySecurityKey = Utils.BytesFromHexString(myCell.getContents());

					/* Access the Initialisation Vector */
					myCell = mySheet.getCell(myCol+4, i);
					myInitVector  = Utils.BytesFromHexString(myCell.getContents());

					/* Add the value into the finance tables */
					myList.addItem(myID, myVersion, myControlKey, mySecurityKey, myInitVector);
				
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
								"Failed to Load Static",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Write the Static to a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to write to
	 *  @param pData the data set to write from
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean writeBackup(statusCtl 			pThread,
										 WritableWorkbook	pWorkbook,
		   	        					 DataSet			pData) throws Exception {
		WritableSheet 					mySheet;
		DataList<Static>.ListIterator	myIterator;
		Static.List 					myStatic;
		Static							myCurr;
		int								myRow;
		int								myCount;
		int								myTotal;
		int								mySteps;
		jxl.write.Label					myCell;
	
		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(Static)) return false;
			
			/* Create the sheet */
			mySheet	= pWorkbook.createSheet(Static, 0);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Access the static */
			myStatic = pData.getStatic();
		
			/* Count the number of account types */
			myTotal   = myStatic.size();
			
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
			
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
	
			/* Access the iterator */
			myIterator = myStatic.listIterator();
			
			/* Loop through the account types */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier and Name cells */
				myCell = new jxl.write.Label(0, myRow, 
											 Integer.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				
				/* Add the data version to the list */
				myCell = new jxl.write.Label(1, myRow, 
											 Integer.toString(myCurr.getDataVersion()));
				mySheet.addCell(myCell);
				
				/* Add the Control Key */
				myCell = new jxl.write.Label(2, myRow, 
											 myCurr.getControlKey());
				mySheet.addCell(myCell);
			
				/* Add the Security Key */
				myCell = new jxl.write.Label(3, myRow, 
											 Utils.HexStringFromBytes(myCurr.getSecurityKey()));
				mySheet.addCell(myCell);
			
				/* Add the Initialisation Vector */
				myCell = new jxl.write.Label(4, myRow, 
											 Utils.HexStringFromBytes(myCurr.getInitVector()));
				mySheet.addCell(myCell);
			
				/* Report the progress */
				myCount++;
				myRow++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
		
			/* Add the Range name */
			if (myRow > 0)
				pWorkbook.addNameArea(Static, mySheet, 0, 0, 4, myRow-1);				
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create Static",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
