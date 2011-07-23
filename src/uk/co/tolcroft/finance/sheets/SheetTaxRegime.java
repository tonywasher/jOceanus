package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetTaxRegime extends SheetStaticData<TaxRegime> {

	/**
	 * NamedArea for Tax Regimes
	 */
	private static final String TaxRegimes   = TaxRegime.listName;
	
	/**
	 * NameList for Tax Regime Names
	 */
	protected static final String TaxRegNames = TaxRegime.objName + "Names";
	
	/**
	 * TaxRegime data list
	 */
	private TaxRegime.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pOutput the output spreadsheet
	 */
	protected SheetTaxRegime(InputSheet pInput) {
		/* Call super-constructor */
		super(pInput, TaxRegimes);
				
		/* Access the TaxRegime list */
		theList = pInput.getData().getTaxRegimes();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pOutput the output spreadsheet
	 */
	protected SheetTaxRegime(OutputSheet pOutput) {
		/* Call super-constructor */
		super(pOutput, TaxRegimes, TaxRegNames);
				
		/* Access the TaxRegime list */
		theList = pOutput.getData().getTaxRegimes();
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
	protected void loadClearTextItem(int pClassId, String pName, String pDesc) throws Exception {
		/* Create the item */
		theList.addItem(pClassId, pName, pDesc);		
	}
	
	/**
	 *  Load the Tax Regimes from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		TaxRegime.List 	myList;
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
			myRange = pWorkbook.findByName(TaxRegimes);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
		
			/* Declare the new stage */
			if (!pThread.setNewStage(TaxRegimes)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of tax classes */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of tax regimes */
				myList = pData.getTaxRegimes();
			
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
		
		catch (Exception e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Tax Regimes",
								e);
		}
		
		/* Return to caller */
		/* Return to caller */
		return true;
	}	
}
