package uk.co.tolcroft.finance.sheets;

import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import uk.co.tolcroft.finance.data.EventInfoType;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.sheets.SheetStaticData;
import uk.co.tolcroft.models.threads.ThreadStatus;

public class SheetEventInfoType extends SheetStaticData<EventInfoType> {
	/**
	 * NamedArea for EventInfoType
	 */
	private static final String EventInfoTypes	= EventInfoType.listName;
	
	/**
	 * NameList for EventInfoType
	 */
	protected static final String EventInfoTypeNames = EventInfoType.objName + "Names";
	
	/**
	 * EventInfoTypes data list
	 */
	private EventInfoType.List theList	= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetEventInfoType(FinanceReader pReader) {
		/* Call super-constructor */
		super(pReader, EventInfoTypes);
				
		/* Access the InfoType list */
		theList = pReader.getData().getInfoTypes();
	}

	/**
	 * Constructor for creating a spreadsheet
	 * @param pWriter the spreadsheet writer
	 */
	protected SheetEventInfoType(FinanceWriter pWriter) {
		/* Call super-constructor */
		super(pWriter, EventInfoTypes, EventInfoTypeNames);
				
		/* Access the InfoType list */
		theList = pWriter.getData().getInfoTypes();
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
	 *  Load the InfoTypes from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(ThreadStatus<FinanceData>	pThread,
										 Workbook					pWorkbook,
							   	  		 FinanceData				pData) throws Exception {
		/* Local variables */
		EventInfoType.List 	myList;
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
			myRange = pWorkbook.findByName(EventInfoTypes);
		
			/* Declare the new stage */
			if (!pThread.setNewStage(EventInfoTypes)) return false;
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of InfoTypes */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of InfoTypes */
				myList = pData.getInfoTypes();
			
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
								"Failed to load EventInfoTypes",
								e);
		}
		
		/* Return to caller */
		return true;
	}	
}
