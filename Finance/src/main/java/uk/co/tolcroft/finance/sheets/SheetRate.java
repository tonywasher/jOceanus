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

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetRate extends SheetDataItem<AcctRate> {
	/**
	 * NamedArea for Rates
	 */
	private static final String Rates 	   = AcctRate.listName;
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup	= false;
	
	/**
	 * Rates data list
	 */
	private AcctRate.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetRate(FinanceReader	pReader) {
		/* Call super constructor */
		super(pReader, Rates);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Rates list */
		theList = pReader.getData().getRates();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetRate(FinanceWriter	pWriter) {
		/* Call super constructor */
		super(pWriter, Rates);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Rates list */
		theList = pWriter.getData().getRates();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {
		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int	myID 		= loadInteger(0);
			int myControlId	= loadInteger(1);
			int myActId		= loadInteger(2);
		
			/* Access the rates and end-date */
			byte[] 	myRateBytes 	= loadBytes(3);
			byte[] 	myBonusBytes 	= loadBytes(4);
			Date	myEndDate 		= loadDate(5);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myActId, myRateBytes, myEndDate, myBonusBytes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the account */
			int		myID 		= loadInteger(0);
			String 	myAccount	= loadString(1);
		
			/* Access the name and description bytes */
			String 	myRate 		= loadString(2);
			String 	myBonus		= loadString(3);
			Date	myEndDate	= loadDate(4);
		
			/* Load the item */
			theList.addItem(myID, myAccount, myRate, myEndDate, myBonus);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(AcctRate	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getControlKey().getId());				
			writeInteger(2, pItem.getAccount().getId());				
			writeBytes(3, pItem.getRateBytes());
			writeBytes(4, pItem.getBonusBytes());
			writeDate(5, pItem.getEndDate());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeString(1, pItem.getAccount().getName());				
			writeNumber(2, pItem.getRate());
			writeNumber(3, pItem.getBonus());			
			writeDate(4, pItem.getEndDate());			
		}
	}

	@Override
	protected void preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return;

		/* Create a new row */
		newRow();
		
		/* Write titles */
		writeHeader(0, AcctRate.fieldName(AcctRate.FIELD_ID));
		writeHeader(1, AcctRate.fieldName(AcctRate.FIELD_ACCOUNT));
		writeHeader(2, AcctRate.fieldName(AcctRate.FIELD_RATE));
		writeHeader(3, AcctRate.fieldName(AcctRate.FIELD_BONUS));			
		writeHeader(4, AcctRate.fieldName(AcctRate.FIELD_ENDDATE));			
	
		/* Adjust for Header */
		adjustForHeader();
	}	

	@Override
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the six columns as the range */
			nameRange(6);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the five columns as the range */
			nameRange(5);

			/* Hide the ID column */
			setHiddenColumn(0);
			setIntegerColumn(0);
			
			/* Set the Account column width */
			setColumnWidth(1, Account.NAMELEN);
			applyDataValidation(1, SheetAccount.AccountNames);
			
			/* Set Rate and Date columns */
			setRateColumn(2);
			setRateColumn(3);
			setDateColumn(4);
		}
	}
	
	/**
	 *  Load the Rates from an archive
	 *  @param pThread   the thread status control
	 *  @param pHelper the sheet helper
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 SheetHelper				pHelper,
							   	  		 FinanceData				pData) throws ModelException {
		/* Local variables */
		AcctRate.List	myList;
		AreaReference	myRange;
		Sheet     		mySheet;
		CellReference	myTop;
		CellReference	myBottom;
		int       		myCol;
		String    		myAccount;
		String    		myRate;
		String    		myBonus;
		Date			myExpiry;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pHelper.resolveAreaReference(Rates);

			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Rates)) return false;
		
			/* If we found the range OK */
			if (myRange != null) {
				/* Access the relevant sheet and Cell references */
				myTop    	= myRange.getFirstCell();
				myBottom 	= myRange.getLastCell();
				mySheet  	= pHelper.getSheetByName(myTop.getSheetName());
				myCol		= myTop.getCol();
		
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
					/* Access the row */
					Row myRow 	= mySheet.getRow(i);
				
					/* Access account */
					myCell 		= myRow.getCell(myCol);
					myAccount 	= myCell.getStringCellValue();
				
					/* Handle Rate */
					myCell 		= myRow.getCell(myCol+1);
					myRate 		= pHelper.formatRateCell(myCell);
				
					/* Handle bonus which may be missing */
					myCell 		= myRow.getCell(myCol+2);
					myBonus 	= null;
					if (myCell != null) {
						myBonus = pHelper.formatRateCell(myCell);
					}
				
					/* Handle expiration which may be missing */
					myCell 		= myRow.getCell(myCol+3);
					myExpiry = null;
					if (myCell != null) {
						myExpiry = myCell.getDateCellValue();
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
			throw new ModelException(ExceptionClass.EXCEL, 
								"Failed to Load Rates",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
