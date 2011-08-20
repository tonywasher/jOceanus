package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

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
	 * @param pOutput the output spreadsheet
	 */
	protected SheetAccountType(InputSheet pInput) {
		/* Call super-constructor */
		super(pInput, AccountTypes);
				
		/* Access the Account Type list */
		theList = pInput.getData().getAccountTypes();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pOutput the output spreadsheet
	 */
	protected SheetAccountType(OutputSheet pOutput) {
		/* Call super-constructor */
		super(pOutput, AccountTypes, ActTypeNames);
				
		/* Access the Account Type list */
		theList = pOutput.getData().getAccountTypes();
		setDataList(theList);
	}

	/**
	 * Obtain the AccountType name list
	 */
	protected static String getNameList() { return AccountType.objName + "Names"; }
	
	/**
	 * Load encrypted 
	 */
	protected void loadEncryptedItem(int pId, int pControlId, int pClassId, byte[] pName, byte[] pDesc) throws Exception {
		/* Create the item */
		theList.addItem(pId, pControlId, pClassId, pName, pDesc);		
	}

	/**
	 * Load clear text 
	 */
	protected void loadClearTextItem(int uId, int pClassId, String pName, String pDesc) throws Exception {
		/* Create the item */
		theList.addItem(uId, pClassId, pName, pDesc);		
	}
	
	/**
	 *  Load the Account Types from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		AccountType.List 	myList;
		Range[] 			myRange;
		Sheet   			mySheet;
		Cell    			myTop;
		Cell    			myBottom;
		Cell    			myCell;
		int     			myCol;
		int     			myTotal;
		int					mySteps;
		int     			myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(AccountTypes);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(AccountTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
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
					myCell = mySheet.getCell(myCol, i);
				
					/* Add the value into the finance tables */
					myList.addItem(myCell.getContents());
				
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
