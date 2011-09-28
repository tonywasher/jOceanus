package uk.co.tolcroft.models.sheets;

import jxl.BooleanCell;
import jxl.Cell;
import jxl.CellType;
import jxl.CellView;
import jxl.DateCell;
import jxl.Range;
import jxl.Sheet;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.write.DateFormat;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WritableCellFeatures;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.Number;

public abstract class SheetDataItem<T extends DataItem<T>> {
	/**
	 * The thread control
	 */
	private ThreadStatus<?>		theThread 		= null;
	
	/**
	 * The input sheet
	 */
	private SheetReader<?> 		theReader		= null;
	
	/**
	 * The workbook
	 */
	private Workbook 			theWorkBook		= null;
	
	/**
	 * The writable workbook
	 */
	private WritableWorkbook 	theWriteBook	= null;
	
	/**
	 * The DataList
	 */
	private DataList<?,T> 		theList			= null;
	
	/**
	 * The name of the related range
	 */
	private String 				theRangeName 	= null;
	
	/**
	 * The WorkSheet of the range
	 */
	private Sheet				theWorkSheet	= null;
	
	/**
	 * The writable WorkSheet of the range
	 */
	private WritableSheet		theWriteSheet	= null;
	
	/**
	 * The Row number of the current row
	 */
	private int					theCurrRow		= 0;
		
	/**
	 * The Row number of the base row
	 */
	private int					theBaseRow		= 0;

	/**
	 * The Column number of the base column
	 */
	private int					theBaseCol		= 0;
	
	/**
	 * Constructor for a load operation
	 *  @param pReader   the spreadsheet reader
	 *  @param pRange 	 the range to load
	 */
	protected SheetDataItem(SheetReader<?> 	pReader,
							String			pRange) {
		/* Store parameters */
		theThread 		= pReader.getThread();
		theReader 		= pReader;
		theRangeName	= pRange;
	}

	/**
	 * Constructor for a write operation
	 *  @param pWriter   the spreadsheet writer
	 *  @param pRange 	 the range to create
	 */
	protected SheetDataItem(SheetWriter<?>	pWriter,
							String			pRange) {
		/* Store parameters */
		theThread 		= pWriter.getThread();
		theWriteBook 	= pWriter.getWorkBook();
		theRangeName	= pRange;
	}

	/**
	 * Set the DataList
	 *  @param pList the Data list
	 */
	protected void setDataList(DataList<?,T> pList) {
		/* Store parameters */
		theList	= pList;
	}

	/**
	 *  Load the DataItems from a spreadsheet 
	 *  @return continue to load <code>true/false</code> 
	 */
	public boolean loadSpreadSheet() throws Exception {
		/* Local variables */
		Range[] 			myRange;
		Cell    			myTop;
		Cell    			myBottom;
		int     			myTotal;
		int					mySteps;
		int     			myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Access the workbook */
			theWorkBook = theReader.getWorkBook();
			
			/* Find the range of cells */
			myRange = theWorkBook.findByName(theRangeName);
		
			/* Declare the new stage */
			if (!theThread.setNewStage(theRangeName)) return false;
		
			/* Access the number of reporting steps */
			mySteps = theThread.getReportingSteps();
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				theWorkSheet  	= theWorkBook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    		= myRange[0].getTopLeft();
				myBottom 		= myRange[0].getBottomRight();
				theBaseCol 		= myTop.getColumn();
		
				/* Count the number of data items */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the single column range */
				for (theCurrRow = myTop.getRow();
					 theCurrRow <= myBottom.getRow();
					 theCurrRow++) {
					/* load the item */
					loadItem();

					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
				
				/* Post process the load */
				postProcessOnLoad();
			}
		}
		
		/* Handle exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to Load " + theRangeName,
								e);
		}
		
		/* Return to caller */
		return true;
	}	
	
	/**
	 *  Write the DataItems to a spreadsheet
	 *  @return continue to write <code>true/false</code> 
	 */
	protected boolean writeSpreadSheet() throws Exception {
		DataList<?,T>.ListIterator	myIterator;
		T							myCurr;
		int							myCount;
		int							myTotal;
		int							mySteps;
	
		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!theThread.setNewStage(theRangeName)) return false;
			
			/* Create the sheet */
			theWriteSheet	= theWriteBook.createSheet(theRangeName, 0);
		
			/* Access the number of reporting steps */
			mySteps = theThread.getReportingSteps();

			/* Count the number of items */
			myTotal   = theList.size();
			
			/* Declare the number of steps */
			if (!theThread.setNumSteps(myTotal)) return false;
			
			/* Initialise counts */
			theBaseRow  = 0;
			theBaseCol	= 0;
			theCurrRow	= theBaseRow;
			myCount 	= 0;
	
			if (preProcessOnWrite()) {
				/* Adjust rows */
				theCurrRow++;
				theBaseRow++;
			}
			
			/* Access the iterator */
			myIterator = theList.listIterator();
			
			/* Loop through the account types */
			while ((myCurr  = myIterator.next()) != null) {
				/* Insert the item into the spreadsheet */
				insertItem(myCurr);

				/* Report the progress */
				myCount++;
				theCurrRow++;
				if ((myCount % mySteps) == 0) 
					if (!theThread.setStepsDone(myCount)) return false;
			}
					
			/* Freeze the titles */
			freezeTitles();
			
			/* If data was written then post-process */
			if (theCurrRow > theBaseRow)
				postProcessOnWrite();				
		}
	
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create " + theRangeName,
								e);
		}
		
		/* Return to caller */
		return true;
	}

	/* Load item from spreadsheet */
	protected abstract void loadItem() throws Throwable;

	/* Insert item into spreadsheet */
	protected abstract void insertItem(T pItem) throws Throwable;

	/* PostProcess on Load */
	protected void postProcessOnLoad() throws Throwable {}
	
	/* PreProcess on Write */
	protected abstract boolean preProcessOnWrite() throws Throwable;
	
	/* PostProcess on Write */
	protected abstract void postProcessOnWrite() throws Throwable;
	
	/**
	 * Name the basic range
	 * @param pNumCols number of columns in range
	 */
	protected void nameRange(int pNumCols) throws Throwable {		
		/* Add the Range name */
		theWriteBook.addNameArea(theRangeName, theWriteSheet, 
								 theBaseCol, theBaseRow, 
								 theBaseCol+pNumCols-1, theCurrRow-1);
		writeString(pNumCols-1, "EndOfData");
	}
	
	/**
	 * Name the column range
	 * @param pOffset offset of column
	 * @param pName name of range
	 */
	protected void nameColumnRange(int pOffset, String pName) throws Throwable {		
		/* Add the Range name for the column */
		theWriteBook.addNameArea(pName, 
							     theWriteSheet, 
							     theBaseCol+pOffset, theBaseRow, 
							     theBaseCol+pOffset, theCurrRow-1);
	}
	
	
	/**
	 * Obtain a Cell Features structure for a validated list 
	 * @param pList the validation list
	 * @return the cell features
	 */
	private WritableCellFeatures obtainCellValidation(String pList) {
		/* Set the validation list for the cell */
		WritableCellFeatures myFeatures = new WritableCellFeatures();
		myFeatures.setDataValidationRange(pList);
		return myFeatures;
	}
	
	/**
	 * Freeze titles 
	 */
	protected void freezeTitles() {
		/* Freeze the top row */
		SheetSettings mySettings = theWriteSheet.getSettings();
		mySettings.setHorizontalFreeze(theBaseCol+2);
		mySettings.setVerticalFreeze(theBaseRow);
	}
	
	/**
	 * Set Hidden column
	 * @param pOffset the offset of the column 
	 */
	protected void setHiddenColumn(int pOffset) {
		/* Create the CellView */
		CellView myView = new CellView();
		myView.setHidden(true);

		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, myView);
	}
	
	/**
	 * Set Boolean column
	 * @param pOffset the offset of the column 
	 */
	protected void setBooleanColumn(int pOffset) {
		/* Set Width of 8 characters */
		setColumnWidth(pOffset, 8);
	}
	
	/**
	 * Set Date column
	 * @param pOffset the offset of the column 
	 */
	protected void setDateColumn(int pOffset) {
		/* Create the Cell format */
		DateFormat myDateFormat = new DateFormat ("dd-MMM-yy"); 
		WritableCellFormat myCellFormat = new WritableCellFormat(myDateFormat);
		
		/* Create the CellView */
		CellView myView = new CellView();
		myView.setSize(11 * 256);
		myView.setFormat(myCellFormat);
		
		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, myView);
	}
	
	/**
	 * Set Money column
	 * @param pOffset the offset of the column 
	 */
	protected void setMoneyColumn(int pOffset) {
		/* Create the Cell format */
		NumberFormat myNumFormat = new NumberFormat ("£#,##0.00"); 
		WritableCellFormat myCellFormat = new WritableCellFormat(myNumFormat);
		
		/* Create the CellView */
		CellView myView = new CellView();
		myView.setSize(13 * 256);
		myView.setFormat(myCellFormat);
		
		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, myView);
	}
	
	/**
	 * Set Price column
	 * @param pOffset the offset of the column 
	 */
	protected void setPriceColumn(int pOffset) {
		/* Create the Cell format */
		NumberFormat myNumFormat = new NumberFormat ("£#,##0.0000"); 
		WritableCellFormat myCellFormat = new WritableCellFormat(myNumFormat);
		
		/* Create the CellView */
		CellView myView = new CellView();
		myView.setSize(15 * 256);
		myView.setFormat(myCellFormat);
		
		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, myView);
	}
	
	/**
	 * Set Units column
	 * @param pOffset the offset of the column 
	 */
	protected void setUnitsColumn(int pOffset) {
		/* Create the Cell format */
		NumberFormat myNumFormat = new NumberFormat ("#,##0.0000"); 
		WritableCellFormat myCellFormat = new WritableCellFormat(myNumFormat);
		
		/* Create the CellView */
		CellView myView = new CellView();
		myView.setSize(13 * 256);
		myView.setFormat(myCellFormat);
		
		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, myView);
	}
	
	/**
	 * Set Rate column
	 * @param pOffset the offset of the column 
	 */
	protected void setRateColumn(int pOffset) {
		/* Create the Cell format */
		NumberFormat myNumFormat = new NumberFormat ("0.00%"); 
		WritableCellFormat myCellFormat = new WritableCellFormat(myNumFormat);
		
		/* Create the CellView */
		CellView myView = new CellView();
		myView.setSize(7 * 256);
		myView.setFormat(myCellFormat);
		
		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, myView);
	}
	
	/**
	 * Set Dilution column
	 * @param pOffset the offset of the column 
	 */
	protected void setDilutionColumn(int pOffset) {
		/* Create the Cell format */
		NumberFormat myNumFormat = new NumberFormat ("0.000000"); 
		WritableCellFormat myCellFormat = new WritableCellFormat(myNumFormat);
		
		/* Create the CellView */
		CellView myView = new CellView();
		myView.setSize(8 * 256);
		myView.setFormat(myCellFormat);
		
		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, myView);
	}
	
	/**
	 * Set Column width
	 * @param pOffset the offset of the column 
	 */
	protected void setColumnWidth(int pOffset, int pNumChars) {
		/* Apply to the sheet */
		theWriteSheet.setColumnView(theBaseCol+pOffset, pNumChars);
	}
	
	/**
	 * Access an integer from the WorkSheet
	 * @param pOffset the column offset
	 * @return the integer
	 */
	protected Integer loadInteger(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 	myCell 	= theWorkSheet.getCell(theBaseCol+pOffset, theCurrRow);
		Integer	myInt	= null;
		if (myCell.getType() != CellType.EMPTY) {
			myInt   = Integer.parseInt(myCell.getContents());
		}
		
		/* Return the value */
		return myInt;
	}

	/**
	 * Access a date from the WorkSheet
	 * @param pOffset the column offset
	 * @return the date
	 */
	protected Boolean loadBoolean(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 	myCell	= theWorkSheet.getCell(theBaseCol+pOffset, theCurrRow);
		Boolean	myValue	= null;
		if (myCell.getType() != CellType.EMPTY) {
			BooleanCell	myValueCell = (BooleanCell)myCell;
			myValue					= myValueCell.getValue();
		}
		
		/* Return the value */
		return myValue;
	}

	/**
	 * Access a date from the WorkSheet
	 * @param pOffset the column offset
	 * @return the date
	 */
	protected java.util.Date loadDate(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 			myCell	= theWorkSheet.getCell(theBaseCol+pOffset, theCurrRow);
		java.util.Date	myDate	= null;
		if (myCell.getType() != CellType.EMPTY) {
			DateCell	myDateCell 	= (DateCell)myCell;
			myDate		= myDateCell.getDate();
		}
		
		/* Return the value */
		return myDate;
	}

	/**
	 * Access a string from the WorkSheet
	 * @param pOffset the column offset
	 * @return the string
	 */
	protected String loadString(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 	myCell 		= theWorkSheet.getCell(theBaseCol+pOffset, theCurrRow);
		String	myString	= null;
		if (myCell.getType() != CellType.EMPTY) {
			myString = myCell.getContents();
		}
		
		/* Return the value */
		return myString;
	}

	/**
	 * Access a byte array from the WorkSheet
	 * @param pOffset the column offset
	 * @return the byte array
	 */
	protected byte[] loadBytes(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 	myCell 	= theWorkSheet.getCell(theBaseCol+pOffset, theCurrRow);
		byte[]	myBytes	= null;
		if (myCell.getType() != CellType.EMPTY) {
			myBytes = Utils.BytesFromHexString(myCell.getContents());
		}
		
		/* Return the value */
		return myBytes;
	}

	/**
	 * Access a char array from the WorkSheet
	 * @param pOffset the column offset
	 * @return the char array
	 */
	protected char[] loadChars(int pOffset) throws Throwable {
		/* Access the bytes */
		byte[]	myBytes	= loadBytes(pOffset);
		char[]	myChars = null;
		if (myBytes != null) {
			myChars = Utils.byteToCharArray(myBytes);
		}
		
		/* Return the value */
		return myChars;
	}

	/**
	 * Write an integer to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the integer
	 */
	protected void writeInteger(int pOffset, Integer pValue) throws Throwable {
		jxl.write.Label myCell;
		
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and add to the sheet */
			myCell = new jxl.write.Label(theBaseCol+pOffset, theCurrRow, 
										 pValue.toString()); 
			theWriteSheet.addCell(myCell);
		}
	}
	
	/**
	 * Write a boolean to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the boolean
	 */
	protected void writeBoolean(int pOffset, Boolean pValue) throws Throwable {
		jxl.write.Boolean myCell;
		
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and add to the sheet */
			myCell = new jxl.write.Boolean(theBaseCol+pOffset, theCurrRow, 
										   pValue.booleanValue()); 
			theWriteSheet.addCell(myCell);
		}
	}
	
	/**
	 * Write a date to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the date
	 */
	protected void writeDate(int pOffset, Date pValue) throws Throwable {
		jxl.write.DateTime myCell;
		
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and add to the sheet */
			myCell = new jxl.write.DateTime(theBaseCol+pOffset, theCurrRow, 
										    pValue.getDate()); 
			theWriteSheet.addCell(myCell);
		}
	}
	
	/**
	 * Write a number to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the number
	 */
	protected void writeNumber(int pOffset, Number pValue) throws Throwable {
		jxl.write.Number myCell;

		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and add to the sheet */
			myCell = new jxl.write.Number(theBaseCol+pOffset, theCurrRow, pValue.convertToDouble()); 
			theWriteSheet.addCell(myCell);
		}
	}
	
	/**
	 * Write a string to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the string
	 */
	protected void writeString(int pOffset, String pValue) throws Throwable {
		jxl.write.Label myCell;
		
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and add to the sheet */
			myCell = new jxl.write.Label(theBaseCol+pOffset, theCurrRow, pValue); 
			theWriteSheet.addCell(myCell);
		}
	}
	
	/**
	 * Write a string that is validated by a list to the WorkSheet 
	 * @param pOffset the column offset
	 * @param pValue the string
	 * @param pFeatures the cell features
	 */
	protected void writeValidatedString(int pOffset, String pValue, String pRange) throws Throwable {
		jxl.write.Label 		myCell;
		WritableCellFeatures	myFeatures;
		
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and add to the sheet */
			myCell = new jxl.write.Label(theBaseCol+pOffset, theCurrRow, pValue);
			myFeatures = obtainCellValidation(pRange);
			myCell.setCellFeatures(myFeatures);
			theWriteSheet.addCell(myCell);
		}
	}
	
	/**
	 * Write a byte array to the WorkSheet
	 * @param pOffset the column offset
	 * @param pBytes the byte array
	 */
	protected void writeBytes(int pOffset, byte[] pBytes) throws Throwable {
		jxl.write.Label myCell;
		
		/* If we have non-null bytes */
		if (pBytes != null) {
			/* Create the cell and add to the sheet */
			myCell = new jxl.write.Label(theBaseCol+pOffset, theCurrRow, 
									 	 Utils.HexStringFromBytes(pBytes));
			theWriteSheet.addCell(myCell);
		}
	}

	/**
	 * Write a char array to the WorkSheet
	 * @param pOffset the column offset
	 * @param pChars the char array
	 */
	protected void writeChars(int pOffset, char[] pChars) throws Throwable {
		/* If we have non-null chars */
		if (pChars != null) {
			/* Create the cell and add to the sheet */
			byte[] myBytes = Utils.charToByteArray(pChars);
			writeBytes(pOffset, myBytes);
		}
	}
}
