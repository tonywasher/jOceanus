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
	 * @param pReader the spreadsheet reader
	 */
	protected SheetFrequency(FinanceReader pReader) {
		/* Call super-constructor */
		super(pReader, Frequencies);
				
		/* Access the Frequency list */
		theList = pReader.getData().getFrequencys();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pWriter the spreadsheet writer
	 */
	protected SheetFrequency(FinanceWriter pWriter) {
		/* Call super-constructor */
		super(pWriter, Frequencies, FrequencyNames);
				
		/* Access the Frequency list */
		theList = pWriter.getData().getFrequencys();
		setDataList(theList);
	}

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
	protected void loadClearTextItem(int pId, boolean isEnabled, int iOrder, String pName, String pDesc) throws Exception {
		/* Create the item */
		theList.addItem(pId, isEnabled, iOrder, pName, pDesc);		
	}
	
	/**
	 *  Load the Frequencies from an archive
	 *  @param pThread   the thread status control
	 *  @param pHelper the sheet helper
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 SheetHelper				pHelper,
							   	  		 FinanceData				pData) throws Exception {
		/* Local variables */
		Frequency.List 	myList;
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
			myRange = pHelper.resolveAreaReference(Frequencies);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(Frequencies)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if (myRange != null) {
				/* Access the relevant sheet and Cell references */
				myTop    	= myRange.getFirstCell();
				myBottom 	= myRange.getLastCell();
				mySheet  	= pHelper.getSheetByName(myTop.getSheetName());
				myCol		= myTop.getCol();
		
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
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Frequencies",
								e);
		}
		
		/* Return to caller */
		return true;
	}	
}
