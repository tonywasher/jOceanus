package uk.co.tolcroft.finance.sheets;

import jxl.*;
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
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {

		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int		myID 		= loadInteger(0);
			int 	myControlId	= loadInteger(1);
			int		myActTypeId	= loadInteger(2);
			Integer	myParentId	= loadInteger(3);
			Integer	myAliasId 	= loadInteger(4);
		
			/* Access the dates */
			java.util.Date 	myClose		= loadDate(5);
			java.util.Date	myMaturity 	= loadDate(6);
		
			/* Access the binary values  */
			byte[] 	myName 		= loadBytes(7);
			byte[]	myDesc 		= loadBytes(8);
			byte[]	myWebSite	= loadBytes(9);
			byte[]	myCustNo 	= loadBytes(10);
			byte[]	myUserId 	= loadBytes(11);
			byte[]	myPassword	= loadBytes(12);
			byte[]	myAccount 	= loadBytes(13);
			byte[]	myNotes 	= loadBytes(14);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myName, myActTypeId, myDesc,
							myMaturity, myClose, myParentId, myAliasId,
							myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			int	   myID 		= loadInteger(0);
			String myName		= loadString(1);
			String myActType	= loadString(2);
			String myDesc		= loadString(3);
			String myParent		= loadString(4);
			String myAlias		= loadString(5);
		
			/* Access the date and name and description bytes */
			java.util.Date 	myClose		= loadDate(6);
			java.util.Date 	myMaturity 	= loadDate(7);
		
			/* Access the binary values  */
			char[] 	myWebSite 	= loadChars(8);
			char[]	myCustNo 	= loadChars(9);
			char[]	myUserId 	= loadChars(10);
			char[]	myPassword	= loadChars(11);
			char[]	myAccount	= loadChars(12);
			char[]	myNotes		= loadChars(13);
		
			/* Load the item */
			theList.addItem(myID, myName, myActType, myDesc, myMaturity, myClose, 
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
			writeInteger(1, pItem.getControlKey().getId());				
			writeInteger(2, pItem.getActType().getId());	
			if (pItem.getParent() != null)
				writeInteger(3, pItem.getParent().getId());				
			if (pItem.getAlias() != null)
				writeInteger(4, pItem.getAlias().getId());				
			writeDate(5, pItem.getClose());
			writeDate(6, pItem.getMaturity());
			writeBytes(7, pItem.getNameBytes());
			writeBytes(8, pItem.getDescBytes());
			writeBytes(9, pItem.getWebSiteBytes());
			writeBytes(10, pItem.getCustNoBytes());
			writeBytes(11, pItem.getUserIdBytes());
			writeBytes(12, pItem.getPasswordBytes());
			writeBytes(13, pItem.getAccountBytes());
			writeBytes(14, pItem.getNotesBytes());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeString(1, pItem.getName());			
			writeValidatedString(2, pItem.getActType().getName(), SheetAccountType.ActTypeNames);				
			writeString(3, pItem.getDesc());
			if (pItem.getParent() != null)
				writeString(4, pItem.getParent().getName());				
			if (pItem.getAlias() != null)
				writeString(5, pItem.getAlias().getName());				
			writeDate(6, pItem.getClose());			
			writeDate(7, pItem.getMaturity());			
			writeChars(8, pItem.getWebSite());			
			writeChars(9, pItem.getCustNo());			
			writeChars(10, pItem.getUserId());			
			writeChars(11, pItem.getPassword());			
			writeChars(12, pItem.getAccount());			
			writeChars(13, pItem.getNotes());			
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, Account.fieldName(Account.FIELD_ID));
		writeString(1, Account.fieldName(Account.FIELD_NAME));
		writeString(2, Account.fieldName(Account.FIELD_TYPE));			
		writeString(3, Account.fieldName(Account.FIELD_DESC));			
		writeString(4, Account.fieldName(Account.FIELD_PARENT));			
		writeString(5, Account.fieldName(Account.FIELD_ALIAS));			
		writeString(6, Account.fieldName(Account.FIELD_CLOSE));			
		writeString(7, Account.fieldName(Account.FIELD_MATURITY));			
		writeString(8, Account.fieldName(Account.FIELD_WEBSITE));			
		writeString(9, Account.fieldName(Account.FIELD_CUSTNO));			
		writeString(10, Account.fieldName(Account.FIELD_USERID));			
		writeString(11, Account.fieldName(Account.FIELD_PASSWORD));			
		writeString(12, Account.fieldName(Account.FIELD_ACCOUNT));			
		writeString(13, Account.fieldName(Account.FIELD_NOTES));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fifteen columns as the range */
			nameRange(15);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the fourteen columns as the range */
			nameRange(14);

			/* Set the Id column as hidden */
			setHiddenColumn(0);

			/* Set the name column width and range */
			nameColumnRange(1, AccountNames);
			
			/* Set the Account column width */
			setColumnWidth(1, Account.NAMELEN);
			setColumnWidth(2, StaticClass.NAMELEN);
			setColumnWidth(3, Account.DESCLEN);
			setColumnWidth(4, Account.NAMELEN);
			setColumnWidth(5, Account.NAMELEN);
			
			/* Set Date columns */
			setDateColumn(6);
			setDateColumn(7);
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
					myList.addItem(0,
								   myAccount,
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
