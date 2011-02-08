package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.statusCtl;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.DilutionEvent;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetDilution {
	/**
	 * NamedArea for Dilution Details
	 */
	private static final String DilutionDtl	= "DilutionDetails";

	/**
	 *  Load the Dilution Details from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to link to
	 *  @param pList the dilution list to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 			pThread,
										 Workbook			pWorkbook,
							   	  		 DataSet			pData,
							   	  		 DilutionEvent.List pList) throws Exception {
		/* Local variables */
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		String    		myAccount;
		String    		myFactor; 
		java.util.Date  myDate;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(DilutionDtl);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(DilutionDtl)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myCol     = myTop.getColumn();
				
				/* Count the number of dilutions */
				myTotal   = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow() + 1;
			     	 i <= myBottom.getRow();
			     	 i++) {
				
					/* Access account */
					myCell    = mySheet.getCell(myCol, i);
					myAccount = myCell.getContents();

					/* Access date */
					myCell     = mySheet.getCell(myCol+1, i);
					myDateCell = (DateCell)myCell;
					myDate     = myDateCell.getDate();
					
					/* Access Factor */
					myCell   = mySheet.getCell(myCol+2, i);
					myFactor = myCell.getContents();
				
					/* Add any non-zero prices into the finance tables */
					pList.addDilution(myAccount, 
									  myDate,
									  myFactor);
					
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
								"Failed to Load Dilution Details",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
