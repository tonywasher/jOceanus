package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetPattern extends SheetDataItem<Event> {
	/**
	 * NamedArea for Patterns
	 */
	private static final String Patterns 	   = Pattern.listName;
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup	= false;
	
	/**
	 * Patterns data list
	 */
	private Pattern.List theList		= null;

	/**
	 * Accounts data list
	 */
	private Account.List theAccounts	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetPattern(FinanceReader	pReader) {
		/* Call super constructor */
		super(pReader, Patterns);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		FinanceData myData = pReader.getData();
		theAccounts = myData.getAccounts();
		theList 	= myData.getPatterns();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetPattern(FinanceWriter	pWriter) {
		/* Call super constructor */
		super(pWriter, Patterns);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Patterns list */
		theList = pWriter.getData().getPatterns();
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
			int	myActId		= loadInteger(2);
			int	myPartId	= loadInteger(3);
			int	myTranId	= loadInteger(6);
			int	myFreqId	= loadInteger(7);
		
			/* Access the date and credit flag */
			java.util.Date 	myDate 		= loadDate(4);
			boolean 		isCredit 	= loadBoolean(5);
		
			/* Access the binary values  */
			byte[] 	myDesc 		= loadBytes(8);
			byte[]	myAmount 	= loadBytes(9);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myDate, myDesc, myAmount, myActId, myPartId, myTranId, myFreqId, isCredit);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			int	   myID 		= loadInteger(0);
			String myAccount	= loadString(1);
			String myPartner	= loadString(6);
			String myTransType	= loadString(7);
			String myFrequency	= loadString(8);
		
			/* Access the name and description bytes */
			java.util.Date 	myDate 		= loadDate(2);
			Boolean 		isCredit	= loadBoolean(4);
		
			/* Access the binary values  */
			String 	myDesc 		= loadString(3);
			String	myAmount 	= loadString(5);
		
			/* Load the item */
			theList.addItem(myID, myDate, myDesc, myAmount, myAccount, myPartner, myTransType, myFrequency, isCredit);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(Event	pItem) throws Throwable  {
		Pattern myItem = (Pattern)pItem;
		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getControlKey().getId());				
			writeInteger(2, myItem.getAccount().getId());				
			writeInteger(3, myItem.getPartner().getId());				
			writeInteger(6, pItem.getTransType().getId());				
			writeInteger(7, myItem.getFrequency().getId());				
			writeDate(4, pItem.getDate());
			writeBoolean(5, myItem.isCredit());
			writeBytes(8, pItem.getDescBytes());
			writeBytes(9, pItem.getAmountBytes());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeValidatedString(1, myItem.getAccount().getName(), SheetAccount.AccountNames);				
			writeValidatedString(6, myItem.getPartner().getName(), SheetAccount.AccountNames);				
			writeValidatedString(7, pItem.getTransType().getName(), SheetTransactionType.TranTypeNames);				
			writeValidatedString(8, myItem.getFrequency().getName(), SheetFrequency.FrequencyNames);				
			writeDate(2, pItem.getDate());
			writeBoolean(4, myItem.isCredit());			
			writeString(3, pItem.getDesc());			
			writeNumber(5, pItem.getAmount());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, Pattern.fieldName(Pattern.FIELD_ID));
		writeString(1, Pattern.fieldName(Pattern.VFIELD_ACCOUNT));
		writeString(2, Pattern.fieldName(Pattern.FIELD_DATE));
		writeString(3, Pattern.fieldName(Pattern.FIELD_DESC));			
		writeString(4, Pattern.fieldName(Pattern.FIELD_ISCREDIT));			
		writeString(5, Pattern.fieldName(Pattern.FIELD_AMOUNT));			
		writeString(6, Pattern.fieldName(Pattern.VFIELD_PARTNER));			
		writeString(7, Pattern.fieldName(Pattern.FIELD_TRNTYP));			
		writeString(8, Pattern.fieldName(Pattern.FIELD_FREQ));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the ten columns as the range */
			nameRange(10);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the nine columns as the range */
			nameRange(9);

			/* Hide the ID column */
			setHiddenColumn(0);
			
			/* Set the Account column width */
			setColumnWidth(1, Account.NAMELEN);
			setColumnWidth(3, Pattern.DESCLEN);
			setColumnWidth(6, Account.NAMELEN);
			setColumnWidth(7, StaticData.NAMELEN);
			setColumnWidth(8, StaticData.NAMELEN);
			
			/* Set Number columns */
			setDateColumn(2);
			setBooleanColumn(4);
			setMoneyColumn(5);
		}
	}

	/**
	 * postProcess on Load
	 */
	protected void postProcessOnLoad() throws Throwable {
		theAccounts.validateLoadedAccounts();
	}
	
	/**
	 *  Load the Patterns from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 Workbook					pWorkbook,
							   	  		 FinanceData				pData) throws Exception {
		/* Local variables */
		Pattern.List		myList;
		Range[]   			myRange;
		Sheet     			mySheet;
		Cell      			myTop;
		Cell      			myBottom;
		int       			myCol;
		java.util.Date		myDate;
		String    			myAccount;
		String    			myDesc;
		String    			myPartner;
		String    			myTransType;
		String    			myAmount;
		String    			myFrequency;
		boolean   			isCredit;
		DateCell  			myDateCell;
		BooleanCell 		myBoolCell;
		int       			myTotal;
		int					mySteps;
		int       			myCount = 0;
		
		/* Protect against exceptions*/
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Patterns);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Patterns)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of patterns */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of patterns */
				myList = pData.getPatterns();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
				
					/* Access strings */
					myAccount 	= mySheet.getCell(myCol, i).getContents();
					myDesc    	= mySheet.getCell(myCol+2, i).getContents();
					myAmount  	= mySheet.getCell(myCol+3, i).getContents();
					myPartner 	= mySheet.getCell(myCol+4, i).getContents();
					myTransType = mySheet.getCell(myCol+5, i).getContents();
					myFrequency = mySheet.getCell(myCol+7, i).getContents();
				
					/* Handle Date */
					myDateCell = (DateCell)mySheet.getCell(myCol+1, i);
					myDate     = myDateCell.getDate();
				
					/* Handle isCredit */
					myBoolCell 	= (BooleanCell)mySheet.getCell(myCol+6, i);
					isCredit 	= myBoolCell.getValue();
				
					/* Add the value into the finance tables */
					myList.addItem(0,
								   myDate,
					               myDesc,
					               myAmount,
					               myAccount,
					               myPartner,
					               myTransType,
					               myFrequency,
					               isCredit);
				
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
								"Failed to Load Patterns",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
