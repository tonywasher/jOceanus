package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;

import java.util.Calendar;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetTaxYear {
	/**
	 * NamedArea for TaxYears
	 */
	private static final String TaxYears 	   = "TaxParameters";
	
	/**
	 *  Load the TaxYears from an archive
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
		TaxYear.List	myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		String    		myAllowance;
		String    		myRentalAllow;
		String    		myCapitalAllow;
		String    		myLoTaxBand;
		String    		myBasicTaxBand;
		String    		myLoAgeAllow;
		String    		myHiAgeAllow;
		String    		myAgeAllowLimit;
		String    		myAddAllowLimit;
		String    		myAddIncBound;
		String    		myLoTaxRate; 
		String    		myBasicTaxRate;
		String    		myHiTaxRate;
		String    		myIntTaxRate;
		String    		myDivTaxRate;
		String    		myHiDivTaxRate;
		String    		myAddTaxRate;
		String    		myAddDivTaxRate;
		String 	  		myCapTaxRate;
		String    		myHiCapTaxRate;
		String    		myTaxRegime;
		Calendar  		myYear;
		Cell      		myCell;
		int       		myAllRow;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(TaxYears);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(TaxYears)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myAllRow  = myTop.getRow();
			
				/* Count the number of TaxYears */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of tax years */
				myList = pData.getTaxYears();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Create the calendar instance */
				myYear    = Calendar.getInstance();
				myYear.set(pRange.getMaxYear(), Calendar.APRIL, 5, 0, 0, 0);
		
				/* Loop through the columns of the table */
				for (int i = myTop.getColumn();
			     	 i <= myBottom.getColumn();
			     	 i++, myYear.add(Calendar.YEAR, -1)) {
				
					/* Access the values */
					myAllowance     = mySheet.getCell(i, myAllRow).getContents();
					myLoTaxBand     = mySheet.getCell(i, myAllRow+1).getContents();
					myBasicTaxBand  = mySheet.getCell(i, myAllRow+2).getContents();
					myRentalAllow   = mySheet.getCell(i, myAllRow+3).getContents();
					myLoTaxRate     = mySheet.getCell(i, myAllRow+4).getContents();
					myBasicTaxRate  = mySheet.getCell(i, myAllRow+5).getContents();
					myIntTaxRate    = mySheet.getCell(i, myAllRow+6).getContents();
					myDivTaxRate    = mySheet.getCell(i, myAllRow+7).getContents();
					myHiTaxRate     = mySheet.getCell(i, myAllRow+8).getContents();
					myHiDivTaxRate  = mySheet.getCell(i, myAllRow+9).getContents();
					myTaxRegime     = mySheet.getCell(i, myAllRow+10).getContents();
					myLoAgeAllow    = mySheet.getCell(i, myAllRow+13).getContents();
					myHiAgeAllow    = mySheet.getCell(i, myAllRow+14).getContents();
					myAgeAllowLimit = mySheet.getCell(i, myAllRow+15).getContents();
					myCapitalAllow  = mySheet.getCell(i, myAllRow+18).getContents();
				
					/* Handle AddTaxRate which may be missing */
					myCell    	 = mySheet.getCell(i, myAllRow+11);
					myAddTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {					
						myAddTaxRate    = myCell.getContents();
					}
				
					/* Handle AddDivTaxRate which may be missing */
					myCell    		= mySheet.getCell(i, myAllRow+12);
					myAddDivTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddDivTaxRate = myCell.getContents();
					}
				
					/* Handle AddAllowLimit which may be missing */
					myCell          = mySheet.getCell(i, myAllRow+16);
					myAddAllowLimit = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddAllowLimit = myCell.getContents();
					}
			    
					/* Handle AddAllowLimit which may be missing */
					myCell        = mySheet.getCell(i, myAllRow+17);
					myAddIncBound = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddIncBound = myCell.getContents();
					}
			    
					/* Handle CapTaxRate which may be missing */
					myCell        = mySheet.getCell(i, myAllRow+19);
					myCapTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myCapTaxRate = myCell.getContents();
					}
			    
					/* Handle HiCapTaxRate which may be missing */
					myCell         = mySheet.getCell(i, myAllRow+20);
					myHiCapTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myHiCapTaxRate = myCell.getContents();
					}
			    
					/* Add the Tax Year */
					myList.addItem(0,
		        				   myTaxRegime,
		        		           myYear.getTime(),
		    	   				   myAllowance,
		    	   				   myRentalAllow,
		    					   myLoAgeAllow,
		    					   myHiAgeAllow,
		    					   myCapitalAllow,
		    					   myAgeAllowLimit,
		    					   myAddAllowLimit,
		    					   myLoTaxBand,
		    					   myBasicTaxBand,
		    					   myAddIncBound,
		    					   myLoTaxRate,
		    					   myBasicTaxRate,
		    					   myHiTaxRate,
		    					   myIntTaxRate,
		    					   myDivTaxRate,
		    					   myHiDivTaxRate,
		    					   myAddTaxRate,
		    					   myAddDivTaxRate,
		    					   myCapTaxRate,
		    					   myHiCapTaxRate);
				
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
								"Failed to Load TaxYears",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Tax Years from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		TaxYear.List	myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		String    		myAllowance;
		String    		myRentalAllow;
		String    		myLoTaxBand;
		String    		myBasicTaxBand;
		String    		myLoAgeAllow;
		String    		myHiAgeAllow;
		String	  		myCapitalAllow;
		String    		myAgeAllowLimit;
		String    		myAddAllowLimit;
		String    		myAddIncBound;
		String    		myLoTaxRate; 
		String    		myBasicTaxRate;
		String    		myHiTaxRate;
		String    		myIntTaxRate;
		String    		myDivTaxRate;
		String    		myHiDivTaxRate;
		String    		myAddTaxRate;
		String    		myAddDivTaxRate;
		String	  		myCapTaxRate;
		String	  		myHiCapTaxRate;
		java.util.Date  myYear;
		Cell	  		myCell;
		DateCell  		myDateCell;
		long      		myID;
		long      		myRegimeID;
		int		  		myCol;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(TaxYears);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(TaxYears)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myCol  	  = myTop.getColumn();
			
				/* Count the number of TaxYears */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of tax years */
				myList = pData.getTaxYears();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
				
					/* Access IDs */
					myCell    	= mySheet.getCell(myCol, i);
					myID      	= Long.parseLong(myCell.getContents());
					myCell    	= mySheet.getCell(myCol+1, i);
					myRegimeID 	= Long.parseLong(myCell.getContents());
				
					/* Handle Date */
					myCell     = mySheet.getCell(myCol+2, i);
					myDateCell = (DateCell)myCell;
					myYear     = myDateCell.getDate();
				
					/* Access the values */
					myAllowance     = mySheet.getCell(myCol+3, i).getContents();
					myLoAgeAllow    = mySheet.getCell(myCol+4, i).getContents();
					myHiAgeAllow    = mySheet.getCell(myCol+5, i).getContents();
					myCapitalAllow  = mySheet.getCell(myCol+6, i).getContents();
					myRentalAllow   = mySheet.getCell(myCol+7, i).getContents();
					myAgeAllowLimit = mySheet.getCell(myCol+8, i).getContents();
					myLoTaxBand     = mySheet.getCell(myCol+9, i).getContents();
					myBasicTaxBand  = mySheet.getCell(myCol+10, i).getContents();
					myLoTaxRate     = mySheet.getCell(myCol+11, i).getContents();
					myBasicTaxRate  = mySheet.getCell(myCol+12, i).getContents();
					myHiTaxRate     = mySheet.getCell(myCol+13, i).getContents();
					myIntTaxRate    = mySheet.getCell(myCol+14, i).getContents();
					myDivTaxRate    = mySheet.getCell(myCol+15, i).getContents();
					myHiDivTaxRate  = mySheet.getCell(myCol+16, i).getContents();

					/* Handle AddAllowLimit which may be missing */
					myCell 		 	= mySheet.getCell(myCol+17, i);
					myAddAllowLimit = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddAllowLimit = myCell.getContents();
					}
					
					/* Handle AddIncBound which may be missing */
					myCell 		  = mySheet.getCell(myCol+18, i);
					myAddIncBound = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddIncBound = myCell.getContents();
					}
					
					/* Handle AddTaxRate which may be missing */
					myCell 		 = mySheet.getCell(myCol+19, i);
					myAddTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddTaxRate    = myCell.getContents();
					}
					
					/* Handle AddDivTaxRate which may be missing */
					myCell    		= mySheet.getCell(myCol+20, i);
					myAddDivTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddDivTaxRate = myCell.getContents();
					}
					
					/* Handle CapTaxRate which may be missing */
					myCell 		 = mySheet.getCell(myCol+21, i);
					myCapTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myCapTaxRate    = myCell.getContents();
					}
					
					/* Handle HiCapTaxRate which may be missing */
					myCell   	   = mySheet.getCell(myCol+22, i);
					myHiCapTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myHiCapTaxRate = myCell.getContents();
					}
			
					/* Add the Tax Year */
					myList.addItem(myID,
								   myRegimeID,
		        		           myYear,
		        		           myAllowance,
		        		           myRentalAllow,
		        		           myLoAgeAllow,
		        		           myHiAgeAllow,
		        		           myCapitalAllow,
		        		           myAgeAllowLimit,
		        		           myAddAllowLimit,
		        		           myLoTaxBand,
		        		           myBasicTaxBand,
		        		           myAddIncBound,
		        		           myLoTaxRate,
		        		           myBasicTaxRate,
		        		           myHiTaxRate,
		        		           myIntTaxRate,
		        		           myDivTaxRate,
		        		           myHiDivTaxRate,
		        		           myAddTaxRate,
		        		           myAddDivTaxRate,
		        		           myCapTaxRate,
		        		           myHiCapTaxRate);
				
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load TaxYears",
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
		DataList<TaxYear>.ListIterator	myIterator;
		TaxYear.List					myYears;
		TaxYear							myCurr;
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
			if (!pThread.setNewStage(TaxYears)) return false;
			
			/* Create the formats */
			myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
		
			/* Create the sheet */
			mySheet   = pWorkbook.createSheet(TaxYears, 0);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Access the tax years */
			myYears	  = pData.getTaxYears();
		
			/* Count the number of years */
			myTotal   = myYears.size();
			
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
			
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
			
			/* Access the iterator */
			myIterator = myYears.listIterator();
			
			/* Loop through the years */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier cell */
				myCell = new jxl.write.Label(0, myRow, 
						 					 Long.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(1, myRow, 
	 					 					 Long.toString(myCurr.getTaxRegime().getId()));
				mySheet.addCell(myCell);
				
				/* Create the Date cells */
				myDate = new jxl.write.DateTime(2, myRow, 
						   						myCurr.getDate().getDate(),
						   						myFormat);
				mySheet.addCell(myDate);
				
				/* Create the Money cells */
				myCell = new jxl.write.Label(3, myRow, 
									   		 myCurr.getAllowance().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(4, myRow, 
						 					 myCurr.getLoAgeAllow().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(5, myRow, 
						 					 myCurr.getHiAgeAllow().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(6, myRow, 
						 					 myCurr.getCapitalAllow().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(7, myRow, 
				         					 myCurr.getRentalAllowance().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(8, myRow, 
						 					 myCurr.getAgeAllowLimit().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(9, myRow, 
						   			         myCurr.getLoBand().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(10, myRow, 
									         myCurr.getBasicBand().format(false));
				mySheet.addCell(myCell);
				
				/* Create the Rate cells */
				myCell = new jxl.write.Label(11, myRow, 
									         myCurr.getLoTaxRate().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(12, myRow, 
						   			         myCurr.getBasicTaxRate().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(13, myRow, 
						   			         myCurr.getHiTaxRate().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(14, myRow, 
						   			         myCurr.getIntTaxRate().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(15, myRow, 
								             myCurr.getDivTaxRate().format(false));
				mySheet.addCell(myCell);
				myCell = new jxl.write.Label(16, myRow, 
									         myCurr.getHiDivTaxRate().format(false));
				mySheet.addCell(myCell);
				if (myCurr.getAddAllowLimit() != null) {
					myCell = new jxl.write.Label(17, myRow, 
												 myCurr.getAddAllowLimit().format(false));
					mySheet.addCell(myCell);
				}
				if (myCurr.getAddIncBound() != null) {
					myCell = new jxl.write.Label(18, myRow, 
												 myCurr.getAddIncBound().format(false));
					mySheet.addCell(myCell);
				}
				if (myCurr.getAddTaxRate() != null) {
					myCell = new jxl.write.Label(19, myRow, 
												 myCurr.getAddTaxRate().format(false));
					mySheet.addCell(myCell);
				}
				if (myCurr.getAddDivTaxRate() != null) {
					myCell = new jxl.write.Label(20, myRow, 
												 myCurr.getAddDivTaxRate().format(false));
					mySheet.addCell(myCell);
				}
				if (myCurr.getCapTaxRate() != null) {
					myCell = new jxl.write.Label(21, myRow, 
												 myCurr.getCapTaxRate().format(false));
					mySheet.addCell(myCell);
				}
				if (myCurr.getHiCapTaxRate() != null) {
					myCell = new jxl.write.Label(22, myRow, 
												 myCurr.getHiCapTaxRate().format(false));
					mySheet.addCell(myCell);
				}
				
				/* Report the progress */
				myCount++;
				myRow++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
		
			/* Add the Range name */
			if (myRow > 0)
				pWorkbook.addNameArea(TaxYears, mySheet, 0, 0, 22, myRow-1);
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create TaxYears",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
