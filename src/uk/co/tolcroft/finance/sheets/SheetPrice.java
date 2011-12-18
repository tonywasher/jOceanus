package uk.co.tolcroft.finance.sheets;

import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.DilutionEvent;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

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
	 * Prices data list
	 */
	private AcctPrice.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetPrice(FinanceReader	pReader) {
		/* Call super constructor */
		super(pReader, Prices);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Prices list */
		theList = pReader.getData().getPrices();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetPrice(FinanceWriter	pWriter) {
		/* Call super constructor */
		super(pWriter, Prices);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Prices list */
		theList = pWriter.getData().getPrices();
		setDataList(theList);
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
			Date	myDate 			= loadDate(3);
			byte[]	myPriceBytes 	= loadBytes(4);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myDate, myActId, myPriceBytes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			int myID 			= loadInteger(0);
			String myAccount	= loadString(1);
		
			/* Access the name and description bytes */
			Date	myDate 	= loadDate(2);
			String	myPrice	= loadString(3);
		
			/* Load the item */
			theList.addItem(myID, myDate, myAccount, myPrice);
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
			writeInteger(0, pItem.getId());
			writeString(1, pItem.getAccount().getName());				
			writeDate(2, pItem.getDate());
			writeNumber(3, pItem.getPrice());			
		}
	}

	@Override
	protected void preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return;

		/* Create a new row */
		newRow();

		/* Write titles */
		writeHeader(0, AcctPrice.fieldName(AcctPrice.FIELD_ID));
		writeHeader(1, AcctPrice.fieldName(AcctPrice.FIELD_ACCOUNT));
		writeHeader(2, AcctPrice.fieldName(AcctPrice.FIELD_DATE));
		writeHeader(3, AcctPrice.fieldName(AcctPrice.FIELD_PRICE));			
		
		/* Adjust for Header */
		adjustForHeader();
	}	

	@Override
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the five columns as the range */
			nameRange(5);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the four columns as the range */
			nameRange(4);

			/* Hide the ID Column */
			setHiddenColumn(0);
			setIntegerColumn(0);
			
			/* Set the Account column width */
			setColumnWidth(1, Account.NAMELEN);
			applyDataValidation(1, SheetAccount.AccountNames);
			
			/* Set Price and Date columns */
			setDateColumn(2);
			setPriceColumn(3);
		}
	}
	
	/**
	 *  Load the Prices from an archive
	 *  @param pThread   the thread status control
	 *  @param pHelper the sheet helper
	 *  @param pData the data set to load into
	 *  @param pDilution the dilution events to modify the prices with
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 SheetHelper				pHelper,
							   	  		 FinanceData				pData,
							   	  		 DilutionEvent.List 		pDilution) throws ModelException {
		/* Local variables */
		AreaReference	myRange;
		Sheet     		mySheet;
		CellReference	myTop;
		CellReference	myBottom;
		Row       		myActRow;
		int       		myDateCol;
		String    		myAccount;
		String    		myPrice; 
		Date  			myDate;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pHelper.resolveAreaReference(Prices1);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Prices)) return false;
		
			/* If we found the range OK */
			if (myRange != null) {
				/* Access the relevant sheet and Cell references */
				myTop    	= myRange.getFirstCell();
				myBottom 	= myRange.getLastCell();
				mySheet  	= pHelper.getSheetByName(myTop.getSheetName());
				myDateCol	= myTop.getCol();
				myActRow  	= mySheet.getRow(myTop.getRow());
		
				/* Count the number of tax classes */
				myTotal  = (myBottom.getRow() - myTop.getRow() + 1);
				myTotal *= (myBottom.getCol() - myTop.getCol() - 1);
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow() + 1;
			     	 i <= myBottom.getRow();
			     	 i++) {
				
					/* Access the row */
					Row myRow 	= mySheet.getRow(i);
				
					/* Access date */
					myCell 	= myRow.getCell(myDateCol);
					myDate	= myCell.getDateCellValue();
			    
					/* Loop through the columns of the table */
					for (int j = myTop.getCol() + 2;
				     	 j <= myBottom.getCol();
				     	 j++) {
					
						/* Access account */
						myCell	  = myActRow.getCell(j);
						myAccount = myCell.getStringCellValue();
				
						/* Handle price which may be missing */
						myCell    = myRow.getCell(j);
						myPrice   = null;
						if (myCell != null) {
							/* Access the formatted cell */
							myPrice = pHelper.formatNumericCell(myCell);
				
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
			throw new ModelException(ExceptionClass.EXCEL, 
								"Failed to Load Prices",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
