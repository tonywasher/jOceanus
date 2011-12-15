package uk.co.tolcroft.models.sheets;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.sheets.SheetWriter.CellStyleType;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.Decimal;

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
	 * The output sheet
	 */
	private SheetWriter<?> 		theWriter		= null;
	
	/**
	 * The workbook
	 */
	private Workbook 			theWorkBook		= null;
	
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
	 * DataFormatter
	 */
	private DataFormatter 		theFormatter	= null;
	
	/**
	 * The Active row
	 */
	private Row					theActiveRow	= null;
		
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
		theFormatter	= new DataFormatter();
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
		theWriter 		= pWriter;
		theWorkBook 	= pWriter.getWorkBook();
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
		AreaReference		myRange	= null;
		CellReference		myTop;
		CellReference		myBottom;
		int     			myTotal;
		int					mySteps;
		int     			myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Access the workbook */
			theWorkBook = theReader.getWorkBook();
			
			/* Find the range of cells */
			Name myName = theWorkBook.getName(theRangeName);
			if (myName != null) myRange = new AreaReference(myName.getRefersToFormula());
		
			/* Declare the new stage */
			if (!theThread.setNewStage(theRangeName)) return false;
		
			/* Access the number of reporting steps */
			mySteps = theThread.getReportingSteps();
			
			/* If we found the range OK */
			if (myRange != null) {
				/* Access the relevant sheet and Cell references */
				myTop    		= myRange.getFirstCell();
				myBottom 		= myRange.getLastCell();
				theWorkSheet  	= theWorkBook.getSheet(myTop.getSheetName());
				theBaseCol 		= myTop.getCol();
		
				/* Count the number of data items */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the range */
				for (theCurrRow = myTop.getRow();
					 theCurrRow <= myBottom.getRow();
					 theCurrRow++) {
					/* Access the row */
					theActiveRow = theWorkSheet.getRow(theCurrRow);
					
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
			theWorkSheet	= theWorkBook.createSheet(theRangeName);
		
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

			/* PreProcess the write */
			preProcessOnWrite();
			
			/* Access the iterator */
			myIterator = theList.listIterator();
			
			/* Loop through the data items */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the new row */
				newRow();
				
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
	protected abstract void preProcessOnWrite() throws Throwable;
	
	/* PostProcess on Write */
	protected abstract void postProcessOnWrite() throws Throwable;

	/**
	 * Adjust for header
	 */
	protected void adjustForHeader() {
		/* Adjust rows */
		theCurrRow++;
		theBaseRow++;
	}
	
	/**
	 * Create a new row
	 */
	protected void newRow() {
		/* Create the new row */
		theActiveRow = theWorkSheet.createRow(theCurrRow);		
	}
	
	/**
	 * Name the basic range
	 * @param pNumCols number of columns in range
	 */
	protected void nameRange(int pNumCols) throws Throwable {		
		/* Build the basic name */
		Name 	myName 	= theWorkBook.createName();
		String 	mySheet = theWorkSheet.getSheetName();
		myName.setNameName(theRangeName);
		
		/* Build the area reference */
		CellReference myFirst 	= new CellReference(mySheet, theBaseRow, theBaseCol, true, true);
		CellReference myLast	= new CellReference(mySheet, theCurrRow-1, theBaseCol+pNumCols-1, true, true);
		AreaReference myArea 	= new AreaReference(myFirst, myLast);
		String		  myRef		= myArea.formatAsString();
		
		/* Set into Name */
		myName.setRefersToFormula(myRef);
		//writeString(pNumCols-1, "EndOfData");
	}
	
	/**
	 * Name the column range
	 * @param pOffset offset of column
	 * @param pName name of range
	 */
	protected void nameColumnRange(int pOffset, String pName) throws Throwable {		
		/* Build the basic name */
		Name 	myName 	= theWorkBook.createName();
		String 	mySheet = theWorkSheet.getSheetName();
		myName.setNameName(pName);
		
		/* Build the area reference */
		CellReference myFirst 	= new CellReference(mySheet, theBaseRow, theBaseCol+pOffset, true, true);
		CellReference myLast	= new CellReference(mySheet, theCurrRow-1, theBaseCol+pOffset, true, true);
		AreaReference myArea 	= new AreaReference(myFirst, myLast);
		String		  myRef		= myArea.formatAsString();
		
		/* Set into Name */
		myName.setRefersToFormula(myRef);
	}
	
	/**
	 * Apply Data Validation
	 * @param pOffset offset of column
	 * @param pList name of validation range
	 */
	public void applyDataValidation(int pOffset, String pList) {
		/* Create the CellAddressList */
		CellRangeAddressList myRange = new CellRangeAddressList(theBaseRow, theCurrRow-1, pOffset, pOffset);
		
		/* Create the constraint */
		DVConstraint myConstraint = DVConstraint.createFormulaListConstraint(pList);
		
		/* Link the two and use drip down arrow */
		DataValidation myValidation = new HSSFDataValidation(myRange, myConstraint);
		myValidation.setSuppressDropDownArrow(false);
		
		/* Apply to the sheet */
		theWorkSheet.addValidationData(myValidation);
	}
	
	/**
	 * Freeze titles 
	 */
	protected void freezeTitles() {
		/* Freeze the top row */
		theWorkSheet.createFreezePane(theBaseCol+2, theBaseRow);
	}
	
	/**
	 * Set Hidden column
	 * @param pOffset the offset of the column 
	 */
	protected void setHiddenColumn(int pOffset) {
		/* Apply to the sheet */
		theWorkSheet.setColumnHidden(theBaseCol+pOffset, true);
	}
	
	/**
	 * Set Date column
	 * @param pOffset the offset of the column 
	 */
	protected void setDateColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Date));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 11*256);
	}
	
	/**
	 * Set Money column
	 * @param pOffset the offset of the column 
	 */
	protected void setMoneyColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Money));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 13*256);
	}
	
	/**
	 * Set Price column
	 * @param pOffset the offset of the column 
	 */
	protected void setPriceColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Price));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 15*256);
	}
	
	/**
	 * Set Units column
	 * @param pOffset the offset of the column 
	 */
	protected void setUnitsColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Units));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 13*256);
	}
	
	/**
	 * Set Rate column
	 * @param pOffset the offset of the column 
	 */
	protected void setRateColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Rate));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 13*256);
	}
	
	/**
	 * Set Dilution column
	 * @param pOffset the offset of the column 
	 */
	protected void setDilutionColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Dilution));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 13*256);
	}
	
	/**
	 * Set Boolean column
	 * @param pOffset the offset of the column 
	 */
	protected void setBooleanColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Boolean));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 8*256);
	}
	
	/**
	 * Set Integer column
	 * @param pOffset the offset of the column 
	 */
	protected void setIntegerColumn(int pOffset) {
		/* Apply the style to the sheet */
		theWorkSheet.setDefaultColumnStyle(theBaseCol+pOffset, theWriter.getCellStyle(CellStyleType.Integer));
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, 8*256);
	}
	
	/**
	 * Set Column width
	 * @param pOffset the offset of the column 
	 */
	protected void setColumnWidth(int pOffset, int pNumChars) {
		/* Apply to the sheet */
		theWorkSheet.setColumnWidth(theBaseCol+pOffset, pNumChars*256);
	}
	
	/**
	 * Access an integer from the WorkSheet
	 * @param pOffset the column offset
	 * @return the integer
	 */
	protected Integer loadInteger(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 	myCell	= theActiveRow.getCell(theBaseCol+pOffset);
		Integer	myInt	= null;
		if (myCell != null) {
			myInt   = Integer.parseInt(myCell.getStringCellValue());
		}
		
		/* Return the value */
		return myInt;
	}

	/**
	 * Access a boolean from the WorkSheet
	 * @param pOffset the column offset
	 * @return the date
	 */
	protected Boolean loadBoolean(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 	myCell	= theActiveRow.getCell(theBaseCol+pOffset);
		Boolean	myValue	= null;
		if (myCell != null) {
			myValue		= myCell.getBooleanCellValue();
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
		Cell 			myCell	= theActiveRow.getCell(theBaseCol+pOffset);
		java.util.Date	myDate	= null;
		if (myCell != null) {
			myDate	= myCell.getDateCellValue();
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
		Cell 	myCell	= theActiveRow.getCell(theBaseCol+pOffset);
		String	myValue	= null;
		if (myCell != null) {
			/* If we are trying for a string representation of a non-string field */
			if (myCell.getCellType() != Cell.CELL_TYPE_STRING) {
				/* Pick up the formatted value */
				myValue = theFormatter.formatCellValue(myCell);
			}
			
			/* Else pick up the standard value */
			else myValue	= myCell.getStringCellValue();
		}
		
		/* Return the value */
		return myValue;
	}

	/**
	 * Access a byte array from the WorkSheet
	 * @param pOffset the column offset
	 * @return the byte array
	 */
	protected byte[] loadBytes(int pOffset) throws Throwable {
		/* Access the cells by reference */
		Cell 	myCell	= theActiveRow.getCell(theBaseCol+pOffset);
		byte[]	myBytes	= null;
		if (myCell != null) {
			myBytes = Utils.BytesFromHexString(myCell.getStringCellValue());
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
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and set its value */
			Cell myCell = theActiveRow.createCell(theBaseCol+pOffset);
			myCell.setCellValue(pValue.toString()); 
			myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Integer));
		}
	}
	
	/**
	 * Write a boolean to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the boolean
	 */
	protected void writeBoolean(int pOffset, Boolean pValue) throws Throwable {
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and set its value */
			Cell myCell = theActiveRow.createCell(theBaseCol+pOffset);
			myCell.setCellValue(pValue.booleanValue()); 
			myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Boolean));
		}
	}
	
	/**
	 * Write a date to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the date
	 */
	protected void writeDate(int pOffset, DateDay pValue) throws Throwable {
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and set its value */
			Cell myCell = theActiveRow.createCell(theBaseCol+pOffset);
			myCell.setCellValue(pValue.getDate()); 
			myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Date));
		}
	}
	
	/**
	 * Write a number to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the number
	 */
	protected void writeNumber(int pOffset, Decimal pValue) throws Throwable {
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and set its value */
			Cell myCell = theActiveRow.createCell(theBaseCol+pOffset);
			myCell.setCellValue(pValue.convertToDouble()); 
			myCell.setCellStyle(theWriter.getCellStyle(pValue));
		}
	}
	
	/**
	 * Write a Header to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the header text
	 */
	protected void writeHeader(int pOffset, String pHeader) throws Throwable {
		/* If we have non-null value */
		if (pHeader != null) {
			/* Create the cell and set its value */
			Cell myCell = theActiveRow.createCell(theBaseCol+pOffset);
			myCell.setCellValue(pHeader);
			myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.Header));
		}
	}
	
	/**
	 * Write a string to the WorkSheet
	 * @param pOffset the column offset
	 * @param pValue the string
	 */
	protected void writeString(int pOffset, String pValue) throws Throwable {
		/* If we have non-null value */
		if (pValue != null) {
			/* Create the cell and set its value */
			Cell myCell = theActiveRow.createCell(theBaseCol+pOffset);
			myCell.setCellValue(pValue); 
			myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.String));
		}
	}
	
	/**
	 * Write a byte array to the WorkSheet
	 * @param pOffset the column offset
	 * @param pBytes the byte array
	 */
	protected void writeBytes(int pOffset, byte[] pBytes) throws Throwable {
		/* If we have non-null bytes */
		if (pBytes != null) {
			/* Create the cell and set its value */
			Cell myCell = theActiveRow.createCell(theBaseCol+pOffset);
			myCell.setCellValue(Utils.HexStringFromBytes(pBytes)); 
			myCell.setCellStyle(theWriter.getCellStyle(CellStyleType.String));
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
