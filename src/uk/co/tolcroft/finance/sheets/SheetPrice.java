package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.DilutionEvent;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetPrice {
	/**
	 * NamedArea for Prices
	 */
	private static final String Prices 	   	= "Prices";

	/**
	 * Alternate NamedArea for Prices
	 */
	private static final String Prices1		= "SpotPricesData";

	/**
	 *  Load the Prices from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @param pDilution the dilution events to modify the prices with
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 			pThread,
										 Workbook			pWorkbook,
							   	  		 DataSet			pData,
							   	  		 DilutionEvent.List pDilution) throws Exception {
		/* Local variables */
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myActRow;
		int       		myDateCol;
		String    		myAccount;
		String    		myPrice; 
		java.util.Date  myDate;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Prices1);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Prices)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myActRow  = myTop.getRow();
				myDateCol = myTop.getColumn();
		
				/* Count the number of tax classes */
				myTotal  = (myBottom.getRow() - myTop.getRow() + 1);
				myTotal *= (myBottom.getColumn() - myTop.getColumn() - 1);
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow() + 1;
			     	 i <= myBottom.getRow();
			     	 i++) {
				
					/* Access date */
					myDateCell = (DateCell)mySheet.getCell(myDateCol, i);
					myDate     = myDateCell.getDate();
			    
					/* Loop through the columns of the table */
					for (int j = myTop.getColumn() + 2;
				     	 j <= myBottom.getColumn();
				     	 j++) {
					
						/* Access account */
						myAccount = mySheet.getCell(j, myActRow).getContents();
				
						/* Handle price which may be missing */
						myCell    = mySheet.getCell(j, i);
						myPrice   = null;
						if (myCell.getType() != CellType.EMPTY) {
							double myDouble = ((NumberCell)myCell).getValue();
							myPrice = Double.toString(myDouble);
				
							/* If the price is non-zero */
							if (!myPrice.equals("0.0")) {
								/* Add the item to the data set */
								pDilution.addPrice(myAccount,
					        		               myDate,
					        		               myPrice);
							}
						}
					
						/* Report the progress */
						myCount++;
						if ((myCount % mySteps) == 0) 
							if (!pThread.setStepsDone(myCount)) return false;
					}
				}
			}
		}
		
		/* Handle exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to Load Prices",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Prices from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		Price.List		myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		int      		myID;
		int      		myAccountID;
		String    		myPrice; 
		java.util.Date  myDate;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Prices);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Prices)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myCol     = myTop.getColumn();
		
				/* Count the number of prices */
				myTotal  = (myBottom.getRow() - myTop.getRow() + 1);
			
				/* Access the list of prices */
				myList = pData.getPrices();
			
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
				
					/* Handle Price */
					myCell     	= mySheet.getCell(myCol+3, i);
					myPrice		= myCell.getContents();
				
					/* Handle Date */
					myCell     = mySheet.getCell(myCol+2, i);
					myDateCell = (DateCell)myCell;
					myDate     = myDateCell.getDate();
				
					/* Add the price into the finance tables */
					myList.addItem(myID,
		        		           myDate,
		        		           myAccountID,
		        		           myPrice);
					
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Prices",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Write the Prices to a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to write to
	 *  @param pData the data set to write from
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean writeBackup(statusCtl 			pThread,
										 WritableWorkbook	pWorkbook,
		   	        					 DataSet			pData) throws Exception {
		WritableSheet 					mySheet;
		DataList<Price>.ListIterator	myIterator;
		Price.List						myPrices;
		Price							myCurr;
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
			if (!pThread.setNewStage(Prices)) return false;
		
			/* Create the formats */
			myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
	
			/* Create the sheet */
			mySheet    = pWorkbook.createSheet(Prices, 0);
	
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Access the prices */
			myPrices  = pData.getPrices();
			
			/* Count the number of prices */
			myTotal   = myPrices.size();
		
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
		
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
		
			/* Access the iterator */
			myIterator = myPrices.listIterator();
			
			/* Loop through the account */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier cell */
				myCell = new jxl.write.Label(0, myRow, 
											 Integer.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(1, myRow, 
											 Integer.toString(myCurr.getAccount().getId()));
				mySheet.addCell(myCell);
			
				/* Create the Price cells */
				myCell = new jxl.write.Label(3, myRow, 
						 					 myCurr.getPrice().format(false));
				mySheet.addCell(myCell);
				
				/* Create the Date cells */
				myDate = new jxl.write.DateTime(2, myRow, 
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
				pWorkbook.addNameArea(Prices, mySheet, 0, 0, 3, myRow-1);
		}

		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create Prices",
								e);
		}
	
		/* Return to caller */
		return true;
	}	
}
