package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetFrequency extends SheetStaticData<Frequency> {

	/**
	 * NamedArea for Frequencies
	 */
	private static final String Frequencies  = Frequency.listName;
	
	/**
	 * NameList for Frequencies
	 */
	protected static final String FrequencyNames = Frequency.objName + "Names";
	
	/**
	 * Frequencies data list
	 */
	private Frequency.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pOutput the output spreadsheet
	 */
	protected SheetFrequency(InputSheet pInput) {
		/* Call super-constructor */
		super(pInput, Frequencies);
				
		/* Access the Frequency list */
		theList = pInput.getData().getFrequencys();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pOutput the output spreadsheet
	 */
	protected SheetFrequency(OutputSheet pOutput) {
		/* Call super-constructor */
		super(pOutput, Frequencies, FrequencyNames);
				
		/* Access the Frequency list */
		theList = pOutput.getData().getFrequencys();
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
	 *  Load the Frequencies from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		Frequency.List 	myList;
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
			myRange = pWorkbook.findByName(Frequencies);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(Frequencies)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of frequencies */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of frequencies */
				myList = pData.getFrequencys();
			
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
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Frequencies",
								e);
		}
		
		/* Return to caller */
		return true;
	}	
}
