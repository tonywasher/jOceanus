package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.WritableCellFeatures;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

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
	 * Validation control for Account Name
	 */
	private WritableCellFeatures theAccountCtl	= null;
	
	/**
	 * Rates data list
	 */
	private AcctRate.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pInput the input spreadsheet
	 */
	protected SheetRate(InputSheet	pInput) {
		/* Call super constructor */
		super(pInput, Rates);
		
		/* Note whether this is a backup */
		isBackup = (pInput.getType() == SheetType.BACKUP);
		
		/* Access the Rates list */
		theList = pInput.getData().getRates();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 */
	protected SheetRate(OutputSheet	pOutput) {
		/* Call super constructor */
		super(pOutput, Rates);
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
				
		/* Access the Rates list */
		theList = pOutput.getData().getRates();
		setDataList(theList);
		
		/* Obtain validation for the Account Name */
		theAccountCtl = obtainCellValidation(SheetAccount.AccountNames);
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
			byte[] 			myRateBytes 	= loadBytes(3);
			byte[] 			myBonusBytes 	= loadBytes(4);
			java.util.Date	myEndDate 		= loadDate(5);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myActId, myRateBytes, myEndDate, myBonusBytes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the account */
			String myAccount	= loadString(0);
		
			/* Access the name and description bytes */
			String 			myRate 		= loadString(1);
			String 			myBonus		= loadString(2);
			java.util.Date	myEndDate	= loadDate(3);
		
			/* Load the item */
			theList.addItem(myAccount, myRate, myEndDate, myBonus);
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
			writeValidatedString(0, pItem.getAccount().getName(), theAccountCtl);				
			writeNumber(1, pItem.getRate());
			writeNumber(2, pItem.getBonus());			
			writeDate(3, pItem.getEndDate());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, AcctRate.fieldName(AcctRate.FIELD_ACCOUNT));
		writeString(1, AcctRate.fieldName(AcctRate.FIELD_RATE));
		writeString(2, AcctRate.fieldName(AcctRate.FIELD_BONUS));			
		writeString(3, AcctRate.fieldName(AcctRate.FIELD_ENDDATE));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the six columns as the range */
			nameRange(6);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the four columns as the range */
			nameRange(4);

			/* Set the Account column width */
			setColumnWidth(0, Account.NAMELEN);
			
			/* Set Rate and Date columns */
			setRateColumn(1);
			setRateColumn(2);
			setDateColumn(3);
		}
	}
	
	/**
	 *  Load the Rates from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		AcctRate.List	myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		String    		myAccount;
		String    		myRate;
		String    		myBonus;
		java.util.Date	myExpiry;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Rates);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Rates)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
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
				
					/* Access account */
					myAccount = mySheet.getCell(myCol, i).getContents();
				
					/* Handle Rate */
					myCell = mySheet.getCell(myCol+1, i);
					myRate = myCell.getContents();
				
					/* Handle bonus which may be missing */
					myCell  = mySheet.getCell(myCol+2, i);
					myBonus = null;
					if (myCell.getType() != CellType.EMPTY) {
						myBonus = myCell.getContents();
					}
				
					/* Handle expiration which may be missing */
					myCell     = mySheet.getCell(myCol+3, i);
					myExpiry = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDateCell = (DateCell)myCell;
						myExpiry = myDateCell.getDate();
					}
				
					/* Add the value into the finance tables */
					myList.addItem(myAccount,
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
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to Load Rates",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
