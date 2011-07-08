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

public class SheetAccount extends SheetDataItem<Account> {
	/**
	 * NamedArea for Accounts
	 */
	private static final String Accounts 	   = Account.listName;	
	
	/**
	 * NameList for Accounts
	 */
	protected static final String AccountNames = Account.objName + "Names";
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup	= false;
	
	/**
	 * Validation control for Account Type
	 */
	private WritableCellFeatures theTypeCtl	= null;
	
	/**
	 * Account data list
	 */
	private Account.List theList		= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pInput the input spreadsheet
	 */
	protected SheetAccount(InputSheet	pInput) {
		/* Call super constructor */
		super(pInput, Accounts);
		
		/* Note whether this is a backup */
		isBackup = (pInput.getType() == SheetType.BACKUP);
				
		/* Access the Lists */
		DataSet myData = pInput.getData();
		theList 	= myData.getAccounts();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 */
	protected SheetAccount(OutputSheet	pOutput) {
		/* Call super constructor */
		super(pOutput, Accounts);
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
				
		/* Access the Accounts list */
		theList = pOutput.getData().getAccounts();
		setDataList(theList);
		
		/* If this is not a backup */
		if (!isBackup) {
			/* Obtain validation for the Account Name */
			theTypeCtl 	= obtainCellValidation(SheetAccountType.ActTypeNames);
		}
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {

		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int		myID 		= loadInteger(0);
			int		myActTypeId	= loadInteger(1);
			Integer	myParentId	= loadInteger(2);
			Integer	myAliasId 	= loadInteger(3);
		
			/* Access the dates */
			java.util.Date 	myClose		= loadDate(4);
			java.util.Date	myMaturity 	= loadDate(5);
		
			/* Access the binary values  */
			byte[] 	myName 		= loadBytes(6);
			byte[]	myDesc 		= loadBytes(7);
			byte[]	myWebSite	= loadBytes(8);
			byte[]	myCustNo 	= loadBytes(9);
			byte[]	myUserId 	= loadBytes(10);
			byte[]	myPassword	= loadBytes(11);
			byte[]	myAccount 	= loadBytes(12);
			byte[]	myNotes 	= loadBytes(13);
		
			/* Load the item */
			theList.addItem(myID, myName, myActTypeId, myDesc,
							myMaturity, myClose, myParentId, myAliasId,
							myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			String myName		= loadString(0);
			String myActType	= loadString(1);
			String myDesc		= loadString(2);
			String myParent		= loadString(3);
			String myAlias		= loadString(4);
		
			/* Access the date and name and description bytes */
			java.util.Date 	myClose		= loadDate(5);
			java.util.Date 	myMaturity 	= loadDate(6);
		
			/* Access the binary values  */
			char[] 	myWebSite 	= loadChars(7);
			char[]	myCustNo 	= loadChars(8);
			char[]	myUserId 	= loadChars(9);
			char[]	myPassword	= loadChars(10);
			char[]	myAccount	= loadChars(11);
			char[]	myNotes		= loadChars(12);
		
			/* Load the item */
			theList.addItem(myName, myActType, myDesc, myMaturity, myClose, 
							myParent, myAlias, myWebSite, myCustNo,
							myUserId, myPassword, myAccount, myNotes);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(Account	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getActType().getId());	
			if (pItem.getParent() != null)
				writeInteger(2, pItem.getParent().getId());				
			if (pItem.getAlias() != null)
				writeInteger(3, pItem.getAlias().getId());				
			writeDate(4, pItem.getClose());
			writeDate(5, pItem.getMaturity());
			writeBytes(6, pItem.getNameBytes());
			writeBytes(7, pItem.getDescBytes());
			writeBytes(8, pItem.getWebSiteBytes());
			writeBytes(9, pItem.getCustNoBytes());
			writeBytes(10, pItem.getUserIdBytes());
			writeBytes(11, pItem.getPasswordBytes());
			writeBytes(12, pItem.getAccountBytes());
			writeBytes(13, pItem.getNotesBytes());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeString(0, pItem.getName());			
			writeValidatedString(1, pItem.getActType().getName(), theTypeCtl);				
			writeString(2, pItem.getDesc());
			if (pItem.getParent() != null)
				writeString(3, pItem.getParent().getName());				
			if (pItem.getAlias() != null)
				writeString(4, pItem.getAlias().getName());				
			writeDate(5, pItem.getClose());			
			writeDate(6, pItem.getMaturity());			
			writeChars(7, pItem.getWebSite());			
			writeChars(8, pItem.getCustNo());			
			writeChars(9, pItem.getUserId());			
			writeChars(10, pItem.getPassword());			
			writeChars(11, pItem.getAccount());			
			writeChars(12, pItem.getNotes());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, Account.fieldName(Account.FIELD_NAME));
		writeString(1, Account.fieldName(Account.FIELD_TYPE));			
		writeString(2, Account.fieldName(Account.FIELD_DESC));			
		writeString(3, Account.fieldName(Account.FIELD_PARENT));			
		writeString(4, Account.fieldName(Account.FIELD_ALIAS));			
		writeString(5, Account.fieldName(Account.FIELD_CLOSE));			
		writeString(6, Account.fieldName(Account.FIELD_MATURITY));			
		writeString(7, Account.fieldName(Account.FIELD_WEBSITE));			
		writeString(8, Account.fieldName(Account.FIELD_CUSTNO));			
		writeString(9, Account.fieldName(Account.FIELD_USERID));			
		writeString(10, Account.fieldName(Account.FIELD_PASSWORD));			
		writeString(11, Account.fieldName(Account.FIELD_ACCOUNT));			
		writeString(12, Account.fieldName(Account.FIELD_NOTES));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fourteen columns as the range */
			nameRange(14);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the thirteen columns as the range */
			nameRange(10);

			/* Set the name column width and range */
			nameColumnRange(0, AccountNames);
			
			/* Set the Account column width */
			setColumnWidth(0, Account.NAMELEN);
			setColumnWidth(1, StaticClass.NAMELEN);
			setColumnWidth(2, Account.DESCLEN);
			setColumnWidth(3, Account.NAMELEN);
			setColumnWidth(4, Account.NAMELEN);
			
			/* Set Number columns */
			setDateColumn(5);
			setDateColumn(6);
		}
	}

	/**
	 *  Load the Accounts from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		Account.List 	myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		String    		myAccount;
		String    		myAcType; 
		String    		myParent;
		String			myAlias;
		java.util.Date  myMaturity;
		java.util.Date  myClosed;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Accounts);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Accounts)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of accounts */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of accounts */
				myList = pData.getAccounts();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table in reverse order */
				for (int i = myBottom.getRow();
			     	 i >= myTop.getRow();
			     	 i--) {
				
					/* Access account and account type */
					myAccount = mySheet.getCell(myCol, i).getContents();
					myAcType  = mySheet.getCell(myCol+1, i).getContents();
				
					/* Handle maturity which may be missing */
					myCell     = mySheet.getCell(myCol+2, i);
					myMaturity = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDateCell = (DateCell)myCell;
						myMaturity = myDateCell.getDate();
					}
			
					/* Handle parent which may be missing */
					myCell     = mySheet.getCell(myCol+3, i);
					myParent = null;
					if (myCell.getType() != CellType.EMPTY) {
						myParent = myCell.getContents();
					}
			
					/* Handle alias which may be missing */
					myCell     = mySheet.getCell(myCol+4, i);
					myAlias = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAlias = myCell.getContents();
					}
			
					/* Handle closed which may be missing */
					myCell     = mySheet.getCell(myCol+5, i);
					myClosed = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDateCell = (DateCell)myCell;
						myClosed = myDateCell.getDate();
					}
				
					/* Add the value into the finance tables */
					myList.addItem(myAccount,
						           myAcType,
						           null,
						           myMaturity,
						           myClosed,
						           myParent,
						           myAlias,
						           null, null, null, null, null, null);
				
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
								"Failed to Load Accounts",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
