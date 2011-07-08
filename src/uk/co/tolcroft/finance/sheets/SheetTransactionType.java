package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetTransactionType extends SheetStaticData<TransactionType> {

	/**
	 * NamedArea for Transaction Types
	 */
	private final static String TransTypes   = TransactionType.listName;
	
	/**
	 * NameList for TranTypes
	 */
	protected static final String TranTypeNames = TransactionType.objName + "Names";
	
	/**
	 * Alternative NamedArea for Transaction Types
	 */
	private final static String TransTypes1  = "TransType";
	
	/**
	 * TransactionTypes data list
	 */
	private TransactionType.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pOutput the output spreadsheet
	 */
	protected SheetTransactionType(InputSheet pInput) {
		/* Call super-constructor */
		super(pInput, TransTypes);
				
		/* Access the Transaction Type list */
		theList = pInput.getData().getTransTypes();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pOutput the output spreadsheet
	 */
	protected SheetTransactionType(OutputSheet pOutput) {
		/* Call super-constructor */
		super(pOutput, TransTypes, TranTypeNames);
				
		/* Access the Transaction Type list */
		theList = pOutput.getData().getTransTypes();
		setDataList(theList);
	}

	/**
	 * Load encrypted 
	 */
	protected void loadEncryptedItem(int pId, int pClassId, byte[] pName, byte[] pDesc) throws Exception {
		/* Create the item */
		theList.addItem(pId, pClassId, pName, pDesc);		
	}

	/**
	 * Load clear text 
	 */
	protected void loadClearTextItem(int pClassId, String pName, String pDesc) throws Exception {
		/* Create the item */
		theList.addItem(pClassId, pName, pDesc);		
	}
	
	/**
	 *  Load the Transaction Types from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
			 					  		 Workbook	pWorkbook,
			 					  		 DataSet	pData) throws Exception {
		/* Local variables */
		TransactionType.List 	myList;
		Range[] 				myRange;
		Sheet   				mySheet;
		Cell    				myTop;
		Cell    				myBottom;
		Cell    				myCell;
		int     				myCol;
		int     				myTotal;
		int						mySteps;
		int     				myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(TransTypes1);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(TransTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of transaction types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of transaction types */
				myList = pData.getTransTypes();
			
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
								"Failed to Load Transaction Types",
								e);
		}
		
		/* Return to caller */
		return true;
	}	
}
