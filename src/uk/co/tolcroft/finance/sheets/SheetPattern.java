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

public class SheetPattern extends SheetDataItem<Pattern> {
	/**
	 * NamedArea for Patterns
	 */
	private static final String Patterns 	   = Pattern.listName;
	
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
	 * Validation control for Frequency
	 */
	private WritableCellFeatures theFreqCtl		= null;
	
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
	 * @param pInput the input spreadsheet
	 */
	protected SheetPattern(InputSheet	pInput) {
		/* Call super constructor */
		super(pInput, Patterns);
		
		/* Note whether this is a backup */
		isBackup = (pInput.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		DataSet myData = pInput.getData();
		theAccounts = myData.getAccounts();
		theList 	= myData.getPatterns();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 */
	protected SheetPattern(OutputSheet	pOutput) {
		/* Call super constructor */
		super(pOutput, Patterns);
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
				
		/* Access the Patterns list */
		theList = pOutput.getData().getPatterns();
		setDataList(theList);
		
		/* If this is not a backup */
		if (!isBackup) {
			/* Obtain validation for the Account Name */
			theAccountCtl 	= obtainCellValidation(SheetAccount.AccountNames);

			/* Obtain validation for the Transaction Types */
			theTransCtl 	= obtainCellValidation(SheetTransactionType.TranTypeNames);
			
			/* Obtain validation for the Frequencies */
			theFreqCtl 		= obtainCellValidation(SheetFrequency.FrequencyNames);
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
			String myAccount	= loadString(0);
			String myPartner	= loadString(5);
			String myTransType	= loadString(6);
			String myFrequency	= loadString(7);
		
			/* Access the name and description bytes */
			java.util.Date 	myDate 		= loadDate(1);
			Boolean 		isCredit	= loadBoolean(3);
		
			/* Access the binary values  */
			String 	myDesc 		= loadString(2);
			String	myAmount 	= loadString(4);
		
			/* Load the item */
			theList.addItem(myDate, myDesc, myAmount, myAccount, myPartner, myTransType, myFrequency, isCredit);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(Pattern	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getControlKey().getId());				
			writeInteger(2, pItem.getAccount().getId());				
			writeInteger(3, pItem.getPartner().getId());				
			writeInteger(6, pItem.getTransType().getId());				
			writeInteger(7, pItem.getFrequency().getId());				
			writeDate(4, pItem.getDate());
			writeBoolean(5, pItem.isCredit());
			writeBytes(8, pItem.getDescBytes());
			writeBytes(9, pItem.getAmountBytes());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeValidatedString(0, pItem.getAccount().getName(), theAccountCtl);				
			writeValidatedString(5, pItem.getPartner().getName(), theAccountCtl);				
			writeValidatedString(6, pItem.getTransType().getName(), theTransCtl);				
			writeValidatedString(7, pItem.getFrequency().getName(), theFreqCtl);				
			writeDate(1, pItem.getDate());
			writeBoolean(3, pItem.isCredit());			
			writeString(2, pItem.getDesc());			
			writeNumber(4, pItem.getAmount());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, Pattern.fieldName(Pattern.FIELD_ACCOUNT));
		writeString(1, Pattern.fieldName(Pattern.FIELD_DATE));
		writeString(2, Pattern.fieldName(Pattern.FIELD_DESC));			
		writeString(3, Pattern.fieldName(Pattern.FIELD_CREDIT));			
		writeString(4, Pattern.fieldName(Pattern.FIELD_AMOUNT));			
		writeString(5, Pattern.fieldName(Pattern.FIELD_PARTNER));			
		writeString(6, Pattern.fieldName(Pattern.FIELD_TRNTYP));			
		writeString(7, Pattern.fieldName(Pattern.FIELD_FREQ));			
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
			/* Set the eight columns as the range */
			nameRange(8);

			/* Set the Account column width */
			setColumnWidth(0, Account.NAMELEN);
			setColumnWidth(2, Pattern.DESCLEN);
			setColumnWidth(5, Account.NAMELEN);
			setColumnWidth(6, StaticClass.NAMELEN);
			setColumnWidth(7, StaticClass.NAMELEN);
			
			/* Set Number columns */
			setDateColumn(1);
			setBooleanColumn(3);
			setMoneyColumn(4);
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
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
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
					myList.addItem(myDate,
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
