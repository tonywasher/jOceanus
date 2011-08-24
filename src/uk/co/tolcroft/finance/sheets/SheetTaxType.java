package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.sheets.SheetStaticData;

public class SheetTaxType extends SheetStaticData<TaxType> {
	/**
	 * NamedArea for Tax Types
	 */
	private static String TaxClasses   = TaxType.listName;
	
	/**
	 * NameList for TaxTypes
	 */
	protected static final String TaxTypeNames = TaxType.objName + "Names";
	
	/**
	 * TaxTypes data list
	 */
	private TaxType.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetTaxType(FinanceReader pReader) {
		/* Call super-constructor */
		super(pReader, TaxClasses);
				
		/* Access the Tax Type list */
		theList = pReader.getData().getTaxTypes();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pWriter the spreadsheet writer
	 */
	protected SheetTaxType(FinanceWriter pWriter) {
		/* Call super-constructor */
		super(pWriter, TaxClasses, TaxTypeNames);
				
		/* Access the Tax Type list */
		theList = pWriter.getData().getTaxTypes();
		setDataList(theList);
	}

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
	protected void loadClearTextItem(int pId, int pClassId, String pName, String pDesc) throws Exception {
		/* Create the item */
		theList.addItem(pId, pClassId, pName, pDesc);		
	}
	
	/**
	 *  Load the Tax Types from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 		pThread,
										 Workbook		pWorkbook,
							   	  		 FinanceData	pData) throws Exception {
		/* Local variables */
		TaxType.List	myList;
		Range[] 		myRange;
		Sheet   		mySheet;
		Cell    		myTop;
		Cell    		myBottom;
		Cell    		myCell;
		int     		myCol;
		int     		myTotal;
		int				mySteps;
		int     		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(TaxClasses);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(TaxClasses)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of tax classes */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of tax types */
				myList = pData.getTaxTypes();
			
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
								"Failed to Load Tax Types",
								e);
		}
		
		/* Return to caller */
		return true;
	}	
}