package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.FinanceSheet.YearRange;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

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
			java.util.Date 	myDate 		= loadDate(2);
			Integer 		myYears 	= loadInteger(11);
		
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
			java.util.Date 	myDate 	= loadDate(1);
			Integer			myYears	= loadInteger(10);
		
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
			writeValidatedString(4, pItem.getDebit().getName(), SheetAccount.AccountNames);				
			writeValidatedString(5, pItem.getCredit().getName(), SheetAccount.AccountNames);				
			writeNumber(6, pItem.getUnits());			
			writeNumber(7, pItem.getDilution());			
			writeValidatedString(8, pItem.getTransType().getName(), SheetTransactionType.TranTypeNames);				
			writeNumber(9, pItem.getTaxCredit());				
			writeInteger(10, pItem.getYears());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, Event.fieldName(Event.FIELD_ID));
		writeString(1, Event.fieldName(Event.FIELD_DATE));
		writeString(2, Event.fieldName(Event.FIELD_DESC));			
		writeString(3, Event.fieldName(Event.FIELD_AMOUNT));			
		writeString(4, Event.fieldName(Event.FIELD_DEBIT));			
		writeString(5, Event.fieldName(Event.FIELD_CREDIT));			
		writeString(6, Event.fieldName(Event.FIELD_UNITS));			
		writeString(7, Event.fieldName(Event.FIELD_DILUTION));			
		writeString(8, Event.fieldName(Event.FIELD_TRNTYP));			
		writeString(9, Event.fieldName(Event.FIELD_TAXCREDIT));			
		writeString(10, Event.fieldName(Event.FIELD_YEARS));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
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
			
			/* Set the Account column width */
			setColumnWidth(2, Event.DESCLEN);
			setColumnWidth(4, Account.NAMELEN);
			setColumnWidth(5, Account.NAMELEN);
			setColumnWidth(8, StaticClass.NAMELEN);
			setColumnWidth(10, 8);
			
			/* Set Number columns */
			setDateColumn(1);
			setMoneyColumn(3);
			setUnitsColumn(6);
			setDilutionColumn(7);
			setMoneyColumn(9);
		}
	}

	/**
	 *  Load the Accounts from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @param pRange the range of tax years
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 		pThread,
										 Workbook		pWorkbook,
							   	  		 FinanceData	pData,
							   	  		 YearRange		pRange) throws Exception {
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
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Events",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}	

