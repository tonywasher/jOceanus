package uk.co.tolcroft.finance.sheets;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SheetStaticData;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetAccountType extends SheetStaticData<AccountType> {
	/**
	 * NamedArea for AccountTypes
	 */
	private static final String AccountTypes = AccountType.listName;
	
	/**
	 * NameList for AccountTypes
	 */
	protected static final String ActTypeNames = AccountType.objName + "Names";
	
	/**
	 * AccountTypes data list
	 */
	private AccountType.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetAccountType(FinanceReader pReader) {
		/* Call super-constructor */
		super(pReader, AccountTypes);
				
		/* Access the Account Type list */
		theList = pReader.getData().getAccountTypes();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pWriter the spreadsheet writer
	 */
	protected SheetAccountType(FinanceWriter pWriter) {
		/* Call super-constructor */
		super(pWriter, AccountTypes, ActTypeNames);
				
		/* Access the Account Type list */
		theList = pWriter.getData().getAccountTypes();
		setDataList(theList);
	}

	/**
	 * Obtain the AccountType name list
	 */
	protected static String getNameList() { return AccountType.objName + "Names"; }
	
	/**
	 * Load encrypted 
	 */
	protected void loadEncryptedItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pName, byte[] pDesc) throws Exception {
		/* Create the item */
		theList.addItem(pId, pControlId, isEnabled, iOrder, pName, pDesc);		
	}

	/**
	 * Load clear text 
	 */
	protected void loadClearTextItem(int uId, boolean isEnabled, int iOrder, String pName, String pDesc) throws Exception {
		/* Create the item */
		theList.addItem(uId, isEnabled, iOrder, pName, pDesc);		
	}
	
	/**
	 *  Load the Account Types from an archive
	 *  @param pThread the thread status control
	 *  @param pHelper the sheet helper
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 SheetHelper				pHelper,
							   	  		 FinanceData				pData) throws Exception {
		/* Local variables */
		AccountType.List 	myList;
		AreaReference		myRange;
		Sheet   			mySheet;
		CellReference		myTop;
		CellReference		myBottom;
		Cell    			myCell;
		int     			myCol;
		int     			myTotal;
		int					mySteps;
		int     			myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pHelper.resolveAreaReference(AccountTypes);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(AccountTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if (myRange != null) {
				/* Access the relevant sheet and Cell references */
				myTop    	= myRange.getFirstCell();
				myBottom 	= myRange.getLastCell();
				mySheet  	= pHelper.getSheetByName(myTop.getSheetName());
				myCol		= myTop.getCol();
		
				/* Count the number of account types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of account types */
				myList = pData.getAccountTypes();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
			     	 i <= myBottom.getRow();
			     	 i++) {
					/* Access the cell by reference */
					Row myRow 	= mySheet.getRow(i);
					myCell 		= myRow.getCell(myCol);
				
					/* Add the value into the finance tables */
					myList.addItem(myCell.getStringCellValue());
				
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
								"Failed to Load Account Types",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
