/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.finance.sheets;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.DilutionEvent;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetDilution {
	/**
	 * NamedArea for Dilution Details
	 */
	private static final String DilutionDtl	= "DilutionDetails";

	/**
	 *  Load the Dilution Details from an archive
	 *  @param pThread   the thread status control
	 *  @param pHelper the sheet helper
	 *  @param pData the data set to link to
	 *  @param pList the dilution list to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 SheetHelper				pHelper,
							   	  		 FinanceData				pData,
							   	  		 DilutionEvent.List 		pList) throws ModelException {
		/* Local variables */
		AreaReference	myRange;
		Sheet     		mySheet;
		CellReference	myTop;
		CellReference	myBottom;
		int       		myCol;
		String    		myAccount;
		String    		myFactor; 
		Date  			myDate;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pHelper.resolveAreaReference(DilutionDtl);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(DilutionDtl)) return false;
		
			/* If we found the range OK */
			if (myRange != null)  {
				/* Access the relevant sheet and Cell references */
				myTop    	= myRange.getFirstCell();
				myBottom 	= myRange.getLastCell();
				mySheet  	= pHelper.getSheetByName(myTop.getSheetName());
				myCol		= myTop.getCol();
				
				/* Count the number of dilutions */
				myTotal   = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
			     	 i <= myBottom.getRow();
			     	 i++) {
					/* Access the row */
					Row myRow 	= mySheet.getRow(i);
				
					/* Access account */
					myCell    = myRow.getCell(myCol);
					myAccount = myCell.getStringCellValue();

					/* Access date */
					myCell     = myRow.getCell(myCol+1);
					myDate     = myCell.getDateCellValue();
					
					/* Access Factor */
					myCell   = myRow.getCell(myCol+2);
					myFactor = pHelper.formatNumericCell(myCell);
				
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
			throw new ModelException(ExceptionClass.EXCEL, 
								"Failed to Load Dilution Details",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
