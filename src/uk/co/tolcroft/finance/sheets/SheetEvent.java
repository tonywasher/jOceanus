package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

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
	 * Validation control for Account Name
	 */
	private WritableCellFeatures theAccountCtl	= null;
	
	/**
	 * Validation control for Transaction Type
	 */
	private WritableCellFeatures theTransCtl	= null;
	
	/**
	 * Events data list
	 */
	private Event.List theList		= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pInput the input spreadsheet
	 */
	protected SheetEvent(InputSheet	pInput) {
		/* Call super constructor */
		super(pInput, Events);
		
		/* Note whether this is a backup */
		isBackup = (pInput.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		DataSet myData = pInput.getData();
		theList 	= myData.getEvents();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 */
	protected SheetEvent(OutputSheet	pOutput) {
		/* Call super constructor */
		super(pOutput, Events);
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
				
		/* Access the Patterns list */
		theList = pOutput.getData().getEvents();
		setDataList(theList);
		
		/* If this is not a backup */
		if (!isBackup) {
			/* Obtain validation for the Account Name */
			theAccountCtl 	= obtainCellValidation(SheetAccount.AccountNames);

			/* Obtain validation for the Transaction Types */
			theTransCtl 	= obtainCellValidation(SheetTransactionType.TranTypeNames);
		}
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {

		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int	myID 		= loadInteger(0);
			int	myDebitId	= loadInteger(2);
			int	myCreditId	= loadInteger(3);
			int	myTranId	= loadInteger(4);
		
			/* Access the date and years */
			java.util.Date 	myDate 		= loadDate(1);
			Integer 		myYears 	= loadInteger(10);
		
			/* Access the binary values  */
			byte[] 	myDesc 		= loadBytes(5);
			byte[]	myAmount 	= loadBytes(6);
			byte[]	myTaxCredit	= loadBytes(9);
			byte[]	myUnits 	= loadBytes(7);
			byte[]	myDilution 	= loadBytes(8);
		
			/* Load the item */
			theList.addItem(myID, myDate, myDesc, myAmount, myDebitId, myCreditId, myUnits, myTranId, myTaxCredit, myDilution, myYears);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			String myDebit		= loadString(3);
			String myCredit		= loadString(4);
			String myTransType	= loadString(7);
		
			/* Access the date and name and description bytes */
			java.util.Date 	myDate 	= loadDate(0);
			Integer			myYears	= loadInteger(9);
		
			/* Access the binary values  */
			String 	myDesc 		= loadString(1);
			String	myAmount 	= loadString(2);
			String	myUnits 	= loadString(5);
			String	myTaxCredit	= loadString(8);
			String	myDilution	= loadString(6);
		
			/* Load the item */
			theList.addItem(myDate, myDesc, myAmount, myDebit, myCredit, myUnits, myTransType, myTaxCredit, myDilution, myYears);
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
			writeDate(1, pItem.getDate());
			writeInteger(2, pItem.getDebit().getId());				
			writeInteger(3, pItem.getCredit().getId());				
			writeInteger(4, pItem.getTransType().getId());				
			writeBytes(5, pItem.getDescBytes());
			writeBytes(6, pItem.getAmountBytes());
			writeBytes(7, pItem.getUnitsBytes());
			writeBytes(8, pItem.getDilutionBytes());
			writeBytes(9, pItem.getTaxCredBytes());
			writeInteger(10, pItem.getYears());				
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeDate(0, pItem.getDate());
			writeString(1, pItem.getDesc());			
			writeNumber(2, pItem.getAmount());			
			writeValidatedString(3, pItem.getDebit().getName(), theAccountCtl);				
			writeValidatedString(4, pItem.getCredit().getName(), theAccountCtl);				
			writeNumber(5, pItem.getUnits());			
			writeNumber(6, pItem.getDilution());			
			writeValidatedString(7, pItem.getTransType().getName(), theTransCtl);				
			writeNumber(8, pItem.getTaxCredit());				
			writeInteger(9, pItem.getYears());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, Event.fieldName(Event.FIELD_DATE));
		writeString(1, Event.fieldName(Event.FIELD_DESC));			
		writeString(2, Event.fieldName(Event.FIELD_AMOUNT));			
		writeString(3, Event.fieldName(Event.FIELD_DEBIT));			
		writeString(4, Event.fieldName(Event.FIELD_CREDIT));			
		writeString(5, Event.fieldName(Event.FIELD_UNITS));			
		writeString(6, Event.fieldName(Event.FIELD_DILUTION));			
		writeString(7, Event.fieldName(Event.FIELD_TRNTYP));			
		writeString(8, Event.fieldName(Event.FIELD_TAXCREDIT));			
		writeString(9, Event.fieldName(Event.FIELD_YEARS));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the eleven columns as the range */
			nameRange(11);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the ten columns as the range */
			nameRange(10);

			/* Set the Account column width */
			setColumnWidth(1, Event.DESCLEN);
			setColumnWidth(3, Account.NAMELEN);
			setColumnWidth(4, Account.NAMELEN);
			setColumnWidth(7, StaticClass.NAMELEN);
			setColumnWidth(9, 8);
			
			/* Set Number columns */
			setDateColumn(0);
			setMoneyColumn(2);
			setUnitsColumn(5);
			setDilutionColumn(6);
			setMoneyColumn(8);
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
	protected static boolean loadArchive(statusCtl 				pThread,
										 Workbook				pWorkbook,
							   	  		 DataSet				pData,
							   	  		 SheetControl.YearRange	pRange) throws Exception {
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
}	

