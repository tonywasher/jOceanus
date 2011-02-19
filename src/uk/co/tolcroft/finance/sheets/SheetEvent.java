package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetEvent {
	/**
	 * NamedArea for Events
	 */
	private static final String Events 	   = "Events";
	
	/**
	 *  Load the Accounts from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @param pRange the range of tax years
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 				pThread,
										 Workbook				pWorkbook,
							   	  		 DataSet				pData,
							   	  		 SheetStatic.YearRange	pRange) throws Exception {
		/* Local variables */
		Event.List		myList;
		String    		myName;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		String    		myDesc;
		String    		myAmount;
		String    		myDebit;
		String    		myCredit; 
		String    		myUnits;
		String    		myTranType;
		String	  		myTaxCredit;
		String			myDilution;
		Integer	  		myYears;
		Cell      		myCell;
		DateCell  		myDateCell;
		java.util.Date  myDate;
		int       		myCol;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Access the list of events */
			myList = pData.getEvents();
		
			/* Loop through the columns of the table */
			for (Integer j = pRange.getMinYear();
				 j <= pRange.getMaxYear();
				 j++) {				
				/* Find the range of cells */
				myName  = j.toString();
				myName  = "Finance" + myName.substring(2);
				myRange = pWorkbook.findByName(myName);
		
				/* Declare the new stage */
				if (!pThread.setNewStage("Events from " + j)) return false;
		
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
					/* Access the relevant sheet and Cell references */
					mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop     = myRange[0].getTopLeft();
					myBottom  = myRange[0].getBottomRight();
					myCol     = myTop.getColumn();
			
					/* Count the number of Events */
					myTotal  = myBottom.getRow() - myTop.getRow();
			
					/* Declare the number of steps */
					if (!pThread.setNumSteps(myTotal)) return false;
			
					/* Loop through the rows of the table */
					for (int i = myTop.getRow()+1;
			     	 	 i <= myBottom.getRow();
			     	 	 i++) {
						/* Access date */
						myDateCell = (DateCell)mySheet.getCell(myCol, i);
						myDate     = myDateCell.getDate();
			    
						/* Access the values */
						myDesc         = mySheet.getCell(myCol+1, i).getContents();
						myAmount       = mySheet.getCell(myCol+2, i).getContents();
						myDebit        = mySheet.getCell(myCol+3, i).getContents();
						myCredit       = mySheet.getCell(myCol+4, i).getContents();
						myTranType     = mySheet.getCell(myCol+7, i).getContents();
			    
						/* Handle Dilution which may be missing */
						myCell    	= mySheet.getCell(myCol+5, i);
						myDilution  = null;
						if ((myCell.getType() != CellType.EMPTY) &&
							(myCell.getContents().startsWith("0."))) {
							double myDouble = ((NumberCell)myCell).getValue();
							myDilution = Double.toString(myDouble);
						}

						/* Handle Dilution which may be missing */
						myCell  = mySheet.getCell(myCol+6, i);
						myUnits	= null;
						if (myCell.getType() != CellType.EMPTY) {
							double myDouble = ((NumberCell)myCell).getValue();
							myUnits = Double.toString(myDouble);
						}

						/* Handle Tax Credit which may be missing */
						myCell      = mySheet.getCell(myCol+8, i);
						myTaxCredit = null;
						if (myCell.getType() != CellType.EMPTY) {
							myTaxCredit = myCell.getContents();
						}

						/* Handle Years which may be missing */
						myCell    = mySheet.getCell(myCol+9, i);
						myYears   = null;
						if (myCell.getType() != CellType.EMPTY) {
							myYears = new Integer(myCell.getContents());
						}

						/* Add the event */
						myList.addItem(myDate,
						               myDesc,
						               myAmount,
						               myDebit,
						               myCredit,
						               myUnits,
						               myTranType,
						               myTaxCredit,
						               myDilution,
						               myYears);
				
						/* Report the progress */
						myCount++;
						if ((myCount % mySteps) == 0) 
							if (!pThread.setStepsDone(myCount)) return false;
					}
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Events",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Accounts from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		Event.List		myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		byte[]    		myDesc;
		byte[]    		myAmount;
		int		  		myDebit;
		int	      		myCredit; 
		byte[]    		myUnits;
		byte[]    		myTaxCredit;
		String			myDilution;
		String			myHexString;
		int		   		myTranType;
		int      		myID;
		Cell      		myCell;
		DateCell  		myDateCell;
		java.util.Date	myDate;
		Integer	  		myYears;
		int       		myCol;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Events);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Events)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myCol     = myTop.getColumn();
			
				/* Count the number of Events */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of events */
				myList = pData.getEvents();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
				
					/* Access IDs */
					myCell    	= mySheet.getCell(myCol, i);
					myID      	= Integer.parseInt(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+3, i);
					myDebit    	= Integer.parseInt(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+4, i);
					myCredit   	= Integer.parseInt(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+6, i);
					myTranType 	= Integer.parseInt(myCell.getContents());
				
					/* Access date */
					myDateCell = (DateCell)mySheet.getCell(myCol+1, i);
					myDate     = myDateCell.getDate();
			    
					/* Access the values */
					myHexString	= mySheet.getCell(myCol+2, i).getContents();
					myDesc      = Utils.BytesFromHexString(myHexString);
					myHexString	= mySheet.getCell(myCol+5, i).getContents();
					myAmount    = Utils.BytesFromHexString(myHexString);
			    
					/* Handle Units which may be missing */
					myCell    = mySheet.getCell(myCol+7, i);
					myUnits   = null;
					if (myCell.getType() != CellType.EMPTY) {
						myHexString	= myCell.getContents();
						myUnits     = Utils.BytesFromHexString(myHexString);
					}

					/* Handle TaxCredit which may be missing */
					myCell      = mySheet.getCell(myCol+8, i);
					myTaxCredit = null;
					if (myCell.getType() != CellType.EMPTY) {
						myHexString	= myCell.getContents();
						myTaxCredit = Utils.BytesFromHexString(myHexString);
					}

					/* Handle Dilution which may be missing */
					myCell      = mySheet.getCell(myCol+9, i);
					myDilution = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDilution = myCell.getContents();
					}

					/* Handle Years which may be missing */
					myCell    = mySheet.getCell(myCol+10, i);
					myYears   = null;
					if (myCell.getType() != CellType.EMPTY) {
						myYears = new Integer(myCell.getContents());
					}

					/* Add the event */
					myList.addItem(myID,
						           myDate,
						           myDesc,
						           myAmount,
						           myDebit,
						           myCredit,
						           myUnits,
						           myTranType,
						           myTaxCredit,
						           myDilution,
						           myYears);
				
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Events",										  	  
								e);
		}
		
		/* Return to caller */
		return true;
	}

	/**
	 *  Write the Accounts to a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to write to
	 *  @param pData the data set to write from
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean writeBackup(statusCtl 			pThread,
										 WritableWorkbook	pWorkbook,
		   	        					 DataSet			pData) throws Exception {
		WritableSheet 					mySheet;
		DataList<Event>.ListIterator	myIterator;
		Event.List						myEvents;
		Event							myCurr;
		int								myRow;
		int								myCount;
		int								myTotal;
		int								mySteps;
		jxl.write.Label					myCell;
		jxl.write.DateTime				myDate;
		WritableCellFormat				myFormat;

		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(Events)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Create the formats */
			myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
	
			/* Create the sheet */
			mySheet    = pWorkbook.createSheet(Events, 0);
	
			/* Access the events */
			myEvents = pData.getEvents();
	
			/* Count the number of events */
			myTotal   = myEvents.size();
		
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
		
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
		
			/* Access the iterator */
			myIterator = myEvents.listIterator();
			
			/* Loop through the events */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier cell */
				myCell = new jxl.write.Label(0, myRow, 
											 Long.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(3, myRow, 
											 Long.toString(myCurr.getDebit().getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(4, myRow, 
											 Long.toString(myCurr.getCredit().getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(6, myRow, 
											 Long.toString(myCurr.getTransType().getId()));
				mySheet.addCell(myCell);
			
				/* Create the Amount cells */
				myCell = new jxl.write.Label(5, myRow,
											 Utils.HexStringFromBytes(myCurr.getAmountBytes()));
				mySheet.addCell(myCell);
				if (myCurr.getUnits() != null) {
					myCell = new jxl.write.Label(7, myRow, 
												 Utils.HexStringFromBytes(myCurr.getUnitsBytes()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getTaxCredit() != null) {
					myCell = new jxl.write.Label(8, myRow, 
												 Utils.HexStringFromBytes(myCurr.getTaxCredBytes()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getDilution() != null) {
					myCell = new jxl.write.Label(9, myRow, 
												 myCurr.getDilution().format(false));
					mySheet.addCell(myCell);
				}
				if (myCurr.getYears() != null) {
					myCell = new jxl.write.Label(10, myRow, 
												 myCurr.getYears().toString());
					mySheet.addCell(myCell);
				}
				
				/* Create the Desc cells */
				myCell = new jxl.write.Label(2, myRow, 
											 Utils.HexStringFromBytes(myCurr.getDescBytes()));
				mySheet.addCell(myCell);
				
				/* Create the Date cells */
				myDate = new jxl.write.DateTime(1, myRow, 
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
				pWorkbook.addNameArea(Events, mySheet, 0, 0, 10, myRow-1);
		}

		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create Events",
								e);
		}
	
		/* Return to caller */
		return true;
	}
}	

