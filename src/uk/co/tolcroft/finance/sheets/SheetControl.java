package uk.co.tolcroft.finance.sheets;

import jxl.*;
import uk.co.tolcroft.finance.core.Threads.statusCtl;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.SpreadSheet.InputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.OutputSheet;
import uk.co.tolcroft.finance.sheets.SpreadSheet.SheetType;
import uk.co.tolcroft.models.Exception;

public class SheetControl extends SheetDataItem<ControlData> {	
	/**
	 * SheetName for Static
	 */
	private static final String Static	   		= ControlData.listName;

	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean isBackup					= false;
	
	/**
	 * ControlData data list
	 */
	private ControlData.List theList			= null;

	/**
	 * DataSet
	 */
	private DataSet theData						= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pInput the input spreadsheet
	 */
	protected SheetControl(InputSheet	pInput) {
		/* Call super constructor */
		super(pInput, Static);
		
		/* Note whether this is a backup */
		isBackup = (pInput.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		theData	= pInput.getData();
		theList = theData.getControlData();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pOutput the output spreadsheet
	 */
	protected SheetControl(OutputSheet	pOutput) {
		/* Call super constructor */
		super(pOutput, Static);
		
		/* Note whether this is a backup */
		isBackup = (pOutput.getType() == SheetType.BACKUP);
				
		/* Access the Control list */
		theList = pOutput.getData().getControlData();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {
		/* If this is a backup */
		if (isBackup) {
			/* Access the IDs */
			int	myID 		= loadInteger(0);
			int	myVersion	= loadInteger(1);

			/* Access the Control Key  */
			int 	myControl		= loadInteger(2);

			/* Add the Control */
			theList.addItem(myID, myVersion, myControl);
		}
		
		/* else this is plain text */
		else {
			/* Access the Version */
			int	myVersion	= loadInteger(0);

			/* Add the Control */
			theList.addItem(myVersion);			
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(ControlData	pItem) throws Throwable  {
		/* If this is a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getDataVersion());
			writeInteger(2, pItem.getControlKey().getId());
		}
		
		/* else just write the data version */
		else writeInteger(0, pItem.getDataVersion());
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, ControlData.fieldName(ControlData.FIELD_VERS));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the three columns as the range */
			nameRange(3);
		}
		
		/* else */
		else {
			/* Set the one column as the range */
			nameRange(1);
			setColumnWidth(0, 8);
		}
	}

	/**
	 * NamedRange for Static
	 */
	private static final String YearRange			= "YearRange";

	/**
	 * Simple class to hold YearRange 
	 */
	protected static class YearRange {
		private int theMinYear = 0;
		private int theMaxYear = 0;
		protected int getMinYear() { return theMinYear; }
		protected int getMaxYear() { return theMaxYear; }
		protected void setMinYear(int pYear) { theMinYear = pYear; }
		protected void setMaxYear(int pYear) { theMaxYear = pYear; }
	}
	
	/**
	 *  Load the Static from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @param pRange the range of tax years
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
			 							 Workbook	pWorkbook,
			 							 DataSet	pData,
			 							 YearRange	pRange) throws Exception {
		/* Local variables */
		Range[] 		myRange;
		Sheet   		mySheet;
		Cell    		myTop;
		Cell    		myCell;
		int     		myCol;
		int     		myRow;
		int     		myStages;
		ControlData.List 	myStatic;

		/* Find the range of cells */
		myRange = pWorkbook.findByName(YearRange);
	
		/* If we found the range OK */
		if ((myRange != null) && (myRange.length == 1)) {
			
			/* Access the relevant sheet and Cell references */
			mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
			
			myTop    = myRange[0].getTopLeft();
			myRow    = myTop.getRow();
			myCol    = myTop.getColumn();
		
			/* Access the Year Range */
			myCell = mySheet.getCell(myCol, myRow+1);
			pRange.setMinYear(Integer.parseInt(myCell.getContents()));
			myCell = mySheet.getCell(myCol+1, myRow+1);
			pRange.setMaxYear(Integer.parseInt(myCell.getContents()));
			
			/* Access the static */
			myStatic = pData.getControlData();
		
			/* Add the value into the finance tables (with no security as yet) */
			myStatic.addItem(Properties.CURRENTVERSION);
		}

		/* Calculate the number of stages */
		myStages = 14 + pRange.getMaxYear() - pRange.getMinYear();
		
		/* Declare the number of stages */
		return pThread.setNumStages(myStages);
	}
}
