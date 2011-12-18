package uk.co.tolcroft.finance.sheets;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.*;
import uk.co.tolcroft.models.sheets.SheetStaticData;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.threads.ThreadStatus;

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
	 * @param pReader the spreadsheet reader
	 */
	protected SheetTaxRegime(FinanceReader pReader) {
		/* Call super-constructor */
		super(pReader, TaxRegimes);
				
		/* Access the TaxRegime list */
		theList = pReader.getData().getTaxRegimes();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pWriter the spreadsheet writer
	 */
	protected SheetTaxRegime(FinanceWriter pWriter) {
		/* Call super-constructor */
		super(pWriter, TaxRegimes, TaxRegNames);
				
		/* Access the TaxRegime list */
		theList = pWriter.getData().getTaxRegimes();
		setDataList(theList);
	}

	/**
	 * Load encrypted 
	 */
	protected void loadEncryptedItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pName, byte[] pDesc) throws ModelException {
		/* Create the item */
		theList.addItem(pId, pControlId, isEnabled, iOrder, pName, pDesc);		
	}

	/**
	 * Load clear text 
	 */
	protected void loadClearTextItem(int pId, boolean isEnabled, int iOrder, String pName, String pDesc) throws ModelException {
		/* Create the item */
		theList.addItem(pId, isEnabled, iOrder, pName, pDesc);		
	}
	
	/**
	 *  Load the Tax Regimes from an archive
	 *  @param pThread   the thread status control
	 *  @param pHelper the sheet helper
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 SheetHelper				pHelper,
							   	  		 FinanceData				pData) throws ModelException {
		/* Local variables */
		TaxRegime.List 	myList;
		AreaReference	myRange;
		Sheet   		mySheet;
		CellReference	myTop;
		CellReference	myBottom;
		Cell    		myCell;
		int     		myCol;
		int     		myTotal;
		int				mySteps;
		int     		myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Find the range of cells */
			myRange = pHelper.resolveAreaReference(TaxRegimes);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
		
			/* Declare the new stage */
			if (!pThread.setNewStage(TaxRegimes)) return false;
		
			/* If we found the range OK */
			if (myRange != null) {
				/* Access the relevant sheet and Cell references */
				myTop    	= myRange.getFirstCell();
				myBottom 	= myRange.getLastCell();
				mySheet  	= pHelper.getSheetByName(myTop.getSheetName());
				myCol		= myTop.getCol();
		
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
		
		catch (ModelException e) {
			throw new ModelException(ExceptionClass.EXCEL, 
								"Failed to load Tax Regimes",
								e);
		}
		
		/* Return to caller */
		/* Return to caller */
		return true;
	}	
}
