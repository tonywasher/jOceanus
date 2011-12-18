package uk.co.tolcroft.finance.sheets;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.FinanceSheet.YearRange;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetEvent extends SheetDataItem<Event> { 
	/**
	 * NamedArea for Events
	 */
	private static final String Events 	   		= Event.listName;
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup	= false;
	
	/**
	 * Events data list
	 */
	private Event.List theList		= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetEvent(FinanceReader	pReader) {
		/* Call super constructor */
		super(pReader, Events);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		FinanceData myData = pReader.getData();
		theList 	= myData.getEvents();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetEvent(FinanceWriter	pWriter) {
		/* Call super constructor */
		super(pWriter, Events);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Events list */
		theList = pWriter.getData().getEvents();
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
			int	myDebitId	= loadInteger(3);
			int	myCreditId	= loadInteger(4);
			int	myTranId	= loadInteger(5);
		
			/* Access the date and years */
			Date 	myDate 		= loadDate(2);
			Integer myYears 	= loadInteger(11);
		
			/* Access the binary values  */
			byte[] 	myDesc 		= loadBytes(6);
			byte[]	myAmount 	= loadBytes(7);
			byte[]	myTaxCredit	= loadBytes(10);
			byte[]	myUnits 	= loadBytes(8);
			byte[]	myDilution 	= loadBytes(9);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myDate, myDesc, myAmount, myDebitId, myCreditId, myUnits, myTranId, myTaxCredit, myDilution, myYears);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			int	   myID 		= loadInteger(0);
			String myDebit		= loadString(4);
			String myCredit		= loadString(5);
			String myTransType	= loadString(8);
		
			/* Access the date and name and description bytes */
			Date 	myDate 	= loadDate(1);
			Integer	myYears	= loadInteger(10);
		
			/* Access the binary values  */
			String 	myDesc 		= loadString(2);
			String	myAmount 	= loadString(3);
			String	myUnits 	= loadString(6);
			String	myTaxCredit	= loadString(9);
			String	myDilution	= loadString(7);
		
			/* Load the item */
			theList.addItem(myID, myDate, myDesc, myAmount, myDebit, myCredit, myUnits, myTransType, myTaxCredit, myDilution, myYears);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(Event	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getControlKey().getId());				
			writeDate(2, pItem.getDate());
			writeInteger(3, pItem.getDebit().getId());				
			writeInteger(4, pItem.getCredit().getId());				
			writeInteger(5, pItem.getTransType().getId());				
			writeBytes(6, pItem.getDescBytes());
			writeBytes(7, pItem.getAmountBytes());
			writeBytes(8, pItem.getUnitsBytes());
			writeBytes(9, pItem.getDilutionBytes());
			writeBytes(10, pItem.getTaxCredBytes());
			writeInteger(11, pItem.getYears());				
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeDate(1, pItem.getDate());
			writeString(2, pItem.getDesc());			
			writeNumber(3, pItem.getAmount());			
			writeString(4, pItem.getDebit().getName());				
			writeString(5, pItem.getCredit().getName());				
			writeNumber(6, pItem.getUnits());			
			writeNumber(7, pItem.getDilution());			
			writeString(8, pItem.getTransType().getName());				
			writeNumber(9, pItem.getTaxCredit());				
			writeInteger(10, pItem.getYears());			
		}
	}

	@Override
	protected void preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return;

		/* Create a new row */
		newRow();
		
		/* Write titles */
		writeHeader(0, Event.fieldName(Event.FIELD_ID));
		writeHeader(1, Event.fieldName(Event.FIELD_DATE));
		writeHeader(2, Event.fieldName(Event.FIELD_DESC));			
		writeHeader(3, Event.fieldName(Event.FIELD_AMOUNT));			
		writeHeader(4, Event.fieldName(Event.FIELD_DEBIT));			
		writeHeader(5, Event.fieldName(Event.FIELD_CREDIT));			
		writeHeader(6, Event.fieldName(Event.FIELD_UNITS));			
		writeHeader(7, Event.fieldName(Event.FIELD_DILUTION));			
		writeHeader(8, Event.fieldName(Event.FIELD_TRNTYP));			
		writeHeader(9, Event.fieldName(Event.FIELD_TAXCREDIT));			
		writeHeader(10, Event.fieldName(Event.FIELD_YEARS));			
				
		/* Adjust for Header */
		adjustForHeader();
	}	

	@Override
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the twelve columns as the range */
			nameRange(12);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the eleven columns as the range */
			nameRange(11);

			/* Hide the ID column */
			setHiddenColumn(0);
			setIntegerColumn(0);
			
			/* Set the Account column width */
			setColumnWidth(2, Event.DESCLEN);
			setColumnWidth(4, Account.NAMELEN);
			applyDataValidation(4, SheetAccount.AccountNames);
			setColumnWidth(5, Account.NAMELEN);
			applyDataValidation(5, SheetAccount.AccountNames);
			setColumnWidth(8, StaticData.NAMELEN);
			applyDataValidation(8, SheetTransactionType.TranTypeNames);
			
			/* Set Number columns */
			setDateColumn(1);
			setMoneyColumn(3);
			setUnitsColumn(6);
			setDilutionColumn(7);
			setMoneyColumn(9);
			setIntegerColumn(10);
		}
	}

	/**
	 *  Load the Accounts from an archive
	 *  @param pThread   the thread status control
	 *  @param pHelper the sheet helper
	 *  @param pData the data set to load into
	 *  @param pRange the range of tax years
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 SheetHelper				pHelper,
							   	  		 FinanceData				pData,
							   	  		 YearRange					pRange) throws ModelException {
		/* Local variables */
		Event.List		myList;
		String    		myRangeName;
		AreaReference	myRange;
		Sheet     		mySheet;
		CellReference	myTop;
		CellReference	myBottom;
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
		Date  			myDate;
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
				myRangeName = j.toString();
				myRangeName = "Finance" + myRangeName.substring(2);
				myRange = pHelper.resolveAreaReference(myRangeName);
		
				/* Declare the new stage */
				if (!pThread.setNewStage("Events from " + j)) return false;
		
				/* If we found the range OK */
				if (myRange != null) {
					/* Access the relevant sheet and Cell references */
					myTop    	= myRange.getFirstCell();
					myBottom 	= myRange.getLastCell();
					mySheet  	= pHelper.getSheetByName(myTop.getSheetName());
					myCol		= myTop.getCol();
			
					/* Count the number of Events */
					myTotal  = myBottom.getRow() - myTop.getRow();
			
					/* Declare the number of steps */
					if (!pThread.setNumSteps(myTotal)) return false;
			
					/* Loop through the rows of the table */
					for (int i = myTop.getRow()+1;
			     	 	 i <= myBottom.getRow();
			     	 	 i++) {
						/* Access the row */
						Row myRow 	= mySheet.getRow(i);
						
						/* Access date */
						myDate     = myRow.getCell(myCol).getDateCellValue();
			    
						/* Access the values */
						myDesc         = myRow.getCell(myCol+1).getStringCellValue();
						myAmount       = pHelper.formatNumericCell(myRow.getCell(myCol+2));
						myDebit        = myRow.getCell(myCol+3).getStringCellValue();
						myCredit       = myRow.getCell(myCol+4).getStringCellValue();
						myTranType     = myRow.getCell(myCol+7).getStringCellValue();
			    
						/* Handle Dilution which may be missing */
						myCell    	= myRow.getCell(myCol+5);
						myDilution  = null;
						if (myCell != null) {
							myDilution = pHelper.formatNumericCell(myCell);
							if (!myDilution.startsWith("0."))
								myDilution = null;
						}

						/* Handle Units which may be missing */
						myCell  = myRow.getCell(myCol+6);
						myUnits	= null;
						if (myCell != null) {
							myUnits = pHelper.formatNumericCell(myCell);
						}

						/* Handle Tax Credit which may be missing */
						myCell      = myRow.getCell(myCol+8);
						myTaxCredit = null;
						if (myCell != null) {
							myTaxCredit = pHelper.formatNumericCell(myCell);
						}

						/* Handle Years which may be missing */
						myCell    = myRow.getCell(myCol+9);
						myYears   = null;
						if (myCell != null) {
							myYears = pHelper.parseIntegerCell(myCell);
						}

						/* Add the event */
						myList.addItem(0,
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
		}
		
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.EXCEL, 
								"Failed to load Events",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}	

