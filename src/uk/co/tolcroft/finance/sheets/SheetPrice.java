package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.WritableCellFeatures;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.finance.views.DilutionEvent;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetPrice extends SheetDataItem<AcctPrice> {
	/**
	 * NamedArea for Prices
	 */
	private static final String Prices 	   	= AcctPrice.listName;

	/**
	 * Alternate NamedArea for Prices
	 */
	private static final String Prices1		= "SpotPricesData";

	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup	= false;
	
	/**
	 * Validation control for Account Name
	 */
	private WritableCellFeatures theAccountCtl	= null;
	
	/**
	 * Prices data list
	 */
	private AcctPrice.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pInput the input spreadsheet
	 */
	protected SheetPrice(InputSheet	pInput) {
		/* Call super constructor */
		super(pInput, Prices);
		
		/* Note whether this is a backup */
		isBackup = (pInput.getType() == SheetType.BACKUP);
		
		/* Access the Prices list */
		theList = pInput.getData().getPrices();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 */
	protected SheetPrice(OutputSheet	pOutput) {
		/* Call super constructor */
		super(pOutput, Prices);
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
				
		/* Access the Prices list */
		theList = pOutput.getData().getPrices();
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
			int myID 		= loadInteger(0);
			int myControlId	= loadInteger(1);
			int myActId		= loadInteger(2);
		
			/* Access the rates and end-date */
			java.util.Date	myDate 			= loadDate(3);
			byte[]			myPriceBytes 	= loadBytes(4);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myDate, myActId, myPriceBytes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			String myAccount	= loadString(0);
		
			/* Access the name and description bytes */
			java.util.Date	myDate 	= loadDate(1);
			String			myPrice	= loadString(2);
		
			/* Load the item */
			theList.addItem(myDate, myAccount, myPrice);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(AcctPrice	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getControlKey().getId());				
			writeInteger(2, pItem.getAccount().getId());				
			writeDate(3, pItem.getDate());
			writeBytes(4, pItem.getPriceBytes());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeValidatedString(0, pItem.getAccount().getName(), theAccountCtl);				
			writeDate(1, pItem.getDate());
			writeNumber(2, pItem.getPrice());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, AcctPrice.fieldName(AcctPrice.FIELD_ACCOUNT));
		writeString(1, AcctPrice.fieldName(AcctPrice.FIELD_DATE));
		writeString(2, AcctPrice.fieldName(AcctPrice.FIELD_PRICE));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the five columns as the range */
			nameRange(5);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the three columns as the range */
			nameRange(3);

			/* Set the Account column width */
			setColumnWidth(0, Account.NAMELEN);
			
			/* Set Price and Date columns */
			setDateColumn(1);
			setPriceColumn(2);
		}
	}
	
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
}
