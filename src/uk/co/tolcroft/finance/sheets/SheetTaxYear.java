package uk.co.tolcroft.finance.sheets;

import jxl.*;

import java.util.Calendar;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.sheets.FinanceSheet.YearRange;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public class SheetTaxYear extends SheetDataItem<TaxYear> {
	/**
	 * NamedArea for TaxYears
	 */
	private static final String TaxYears 	   	= "TaxParameters";
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean 			isBackup		= false;
	
	/**
	 * TaxYear data list
	 */
	private TaxYear.List 		theList			= null;

	/**
	 * DataSet
	 */
	private FinanceData			theData			= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetTaxYear(FinanceReader	pReader) {
		/* Call super constructor */
		super(pReader, TaxYears);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
				
		/* Access the Lists */
		theData	= pReader.getData();
		theList = theData.getTaxYears();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetTaxYear(FinanceWriter	pWriter) {
		/* Call super constructor */
		super(pWriter, TaxYears);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the TaxYears list */
		theList = pWriter.getData().getTaxYears();
		setDataList(theList);
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {

		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int	myID 		= loadInteger(0);
			int	myRegimeId	= loadInteger(2);
		
			/* Access the dates */
			java.util.Date 	myYear		= loadDate(1);
		
			/* Access the String values  */
			String 	myAllowance	= loadString(3);
			String	myLoAgeAllw	= loadString(4);
			String	myHiAgeAllw	= loadString(5);
			String	myCapAllow 	= loadString(6);
			String	myRental 	= loadString(7);
			String 	myAgeLimit	= loadString(8);
			String	myLoBand	= loadString(9);
			String	myBasicBand	= loadString(10);
			String	myLoTax 	= loadString(11);
			String	myBasicTax 	= loadString(12);
			String 	myHiTax		= loadString(13);
			String	myAddTax	= loadString(14);
			String	myIntTax	= loadString(15);
			String	myDivTax 	= loadString(16);
			String	myHiDivTax 	= loadString(17);
			String	myAddDivTax = loadString(18);
			String	myCapTax 	= loadString(19);
			String	myHiCapTax 	= loadString(20);
			String	myAddLimit 	= loadString(21);
			String	myAddBound 	= loadString(22);
		
			/* Add the Tax Year */
			theList.addItem(myID, myRegimeId, myYear, myAllowance, myRental,
        		           	myLoAgeAllw, myHiAgeAllw, myCapAllow, myAgeLimit,
        		           	myAddLimit, myLoBand, myBasicBand, myAddBound,
        		           	myLoTax, myBasicTax, myHiTax, myIntTax,
        		           	myDivTax, myHiDivTax, myAddTax,
        		           	myAddDivTax, myCapTax, myHiCapTax);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the ID */
			int	myID 		= loadInteger(0);
			
			/* Access the Strings */
			String myTaxRegime		= loadString(2);
		
			/* Access the year */
			java.util.Date 	myYear	= loadDate(1);
		
			/* Access the binary values  */
			String 	myAllowance	= loadString(3);
			String	myLoAgeAllw	= loadString(4);
			String	myHiAgeAllw	= loadString(5);
			String	myCapAllow 	= loadString(6);
			String	myRental 	= loadString(7);
			String 	myAgeLimit	= loadString(8);
			String	myLoBand	= loadString(9);
			String	myBasicBand	= loadString(10);
			String	myLoTax 	= loadString(11);
			String	myBasicTax 	= loadString(12);
			String 	myHiTax		= loadString(13);
			String	myAddTax	= loadString(14);
			String	myIntTax	= loadString(15);
			String	myDivTax 	= loadString(16);
			String	myHiDivTax 	= loadString(17);
			String	myAddDivTax = loadString(18);
			String	myCapTax 	= loadString(19);
			String	myHiCapTax 	= loadString(20);
			String	myAddLimit 	= loadString(21);
			String	myAddBound 	= loadString(22);
		
			/* Add the Tax Year */
			theList.addItem(myID,
							myTaxRegime,
        		           	myYear,
        		           	myAllowance,
        		           	myRental,
        		           	myLoAgeAllw,
        		           	myHiAgeAllw,
        		           	myCapAllow,
        		           	myAgeLimit,
        		           	myAddLimit,
        		           	myLoBand,
        		           	myBasicBand,
        		           	myAddBound,
        		           	myLoTax,
        		           	myBasicTax,
        		           	myHiTax,
        		           	myIntTax,
        		           	myDivTax,
        		           	myHiDivTax,
        		           	myAddTax,
        		           	myAddDivTax,
        		           	myCapTax,
        		           	myHiCapTax);
		
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(TaxYear	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeDate(1, pItem.getDate());
			writeInteger(2, pItem.getTaxRegime().getId());	
			writeNumber(3, pItem.getAllowance());
			writeNumber(4, pItem.getLoAgeAllow());
			writeNumber(5, pItem.getHiAgeAllow());
			writeNumber(6, pItem.getCapitalAllow());
			writeNumber(7, pItem.getRentalAllowance());
			writeNumber(8, pItem.getAgeAllowLimit());
			writeNumber(9, pItem.getLoBand());
			writeNumber(10, pItem.getBasicBand());
			writeNumber(11, pItem.getLoTaxRate());
			writeNumber(12, pItem.getBasicTaxRate());
			writeNumber(13, pItem.getHiTaxRate());
			writeNumber(14, pItem.getAddTaxRate());
			writeNumber(15, pItem.getIntTaxRate());
			writeNumber(16, pItem.getDivTaxRate());
			writeNumber(17, pItem.getHiDivTaxRate());
			writeNumber(18, pItem.getAddDivTaxRate());
			writeNumber(19, pItem.getCapTaxRate());
			writeNumber(20, pItem.getHiCapTaxRate());
			writeNumber(21, pItem.getAddAllowLimit());
			writeNumber(22, pItem.getAddIncBound());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeDate(1, pItem.getDate());
			writeValidatedString(2, pItem.getTaxRegime().getName(), SheetTaxRegime.TaxRegNames);				
			writeNumber(3, pItem.getAllowance());
			writeNumber(4, pItem.getLoAgeAllow());
			writeNumber(5, pItem.getHiAgeAllow());
			writeNumber(6, pItem.getCapitalAllow());
			writeNumber(7, pItem.getRentalAllowance());
			writeNumber(8, pItem.getAgeAllowLimit());
			writeNumber(9, pItem.getLoBand());
			writeNumber(10, pItem.getBasicBand());
			writeNumber(11, pItem.getLoTaxRate());
			writeNumber(12, pItem.getBasicTaxRate());
			writeNumber(13, pItem.getHiTaxRate());
			writeNumber(14, pItem.getAddTaxRate());
			writeNumber(15, pItem.getIntTaxRate());
			writeNumber(16, pItem.getDivTaxRate());
			writeNumber(17, pItem.getHiDivTaxRate());
			writeNumber(18, pItem.getAddDivTaxRate());
			writeNumber(19, pItem.getCapTaxRate());
			writeNumber(20, pItem.getHiCapTaxRate());
			writeNumber(21, pItem.getAddAllowLimit());
			writeNumber(22, pItem.getAddIncBound());
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		writeString(0, TaxYear.fieldName(TaxYear.FIELD_ID));
		writeString(1, TaxYear.fieldName(TaxYear.FIELD_YEAR));
		writeString(2, TaxYear.fieldName(TaxYear.FIELD_REGIME));			
		writeString(3, TaxYear.fieldName(TaxYear.FIELD_ALLOW));			
		writeString(4, TaxYear.fieldName(TaxYear.FIELD_LOAGAL));			
		writeString(5, TaxYear.fieldName(TaxYear.FIELD_HIAGAL));			
		writeString(6, TaxYear.fieldName(TaxYear.FIELD_CAPALW));			
		writeString(7, TaxYear.fieldName(TaxYear.FIELD_RENTAL));			
		writeString(8, TaxYear.fieldName(TaxYear.FIELD_AGELMT));			
		writeString(9, TaxYear.fieldName(TaxYear.FIELD_LOBAND));			
		writeString(10, TaxYear.fieldName(TaxYear.FIELD_BSBAND));			
		writeString(11, TaxYear.fieldName(TaxYear.FIELD_LOTAX));			
		writeString(12, TaxYear.fieldName(TaxYear.FIELD_BASTAX));			
		writeString(13, TaxYear.fieldName(TaxYear.FIELD_HITAX));			
		writeString(14, TaxYear.fieldName(TaxYear.FIELD_ADDTAX));			
		writeString(15, TaxYear.fieldName(TaxYear.FIELD_INTTAX));			
		writeString(16, TaxYear.fieldName(TaxYear.FIELD_DIVTAX));			
		writeString(17, TaxYear.fieldName(TaxYear.FIELD_HDVTAX));			
		writeString(18, TaxYear.fieldName(TaxYear.FIELD_ADVTAX));			
		writeString(19, TaxYear.fieldName(TaxYear.FIELD_CAPTAX));			
		writeString(20, TaxYear.fieldName(TaxYear.FIELD_HCPTAX));			
		writeString(21, TaxYear.fieldName(TaxYear.FIELD_ADDLMT));			
		writeString(22, TaxYear.fieldName(TaxYear.FIELD_ADDBDY));			
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the twenty-three columns as the range */
			nameRange(23);
			setRateColumn(11);
			setRateColumn(12);
			setRateColumn(13);
			setRateColumn(14);
			setRateColumn(15);
			setRateColumn(16);
			setRateColumn(17);
			setRateColumn(18);
			setRateColumn(19);
			setRateColumn(20);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the twenty-three columns as the range */
			nameRange(23);

			/* Set the Id column as hidden */
			setHiddenColumn(0);

			/* Set the String column width */
			setColumnWidth(2, StaticClass.NAMELEN);
			
			/* Set Number columns */
			setDateColumn(1);
			setMoneyColumn(3);
			setMoneyColumn(4);
			setMoneyColumn(5);
			setMoneyColumn(6);
			setMoneyColumn(7);
			setMoneyColumn(8);
			setMoneyColumn(9);
			setMoneyColumn(10);
			setMoneyColumn(21);
			setMoneyColumn(22);
			setRateColumn(11);
			setRateColumn(12);
			setRateColumn(13);
			setRateColumn(14);
			setRateColumn(15);
			setRateColumn(16);
			setRateColumn(17);
			setRateColumn(18);
			setRateColumn(19);
			setRateColumn(20);
		}
	}

	/**
	 * postProcess on Load
	 */
	protected void postProcessOnLoad() throws Throwable {
		theData.calculateDateRange();
	}
	
	/**
	 *  Load the TaxYears from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @param pRange the range of tax years
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 		pThread,
										 Workbook		pWorkbook,
							   	  		 FinanceData	pData,
							   	  		 YearRange		pRange) throws Exception {
		/* Local variables */
		TaxYear.List	myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		String    		myAllowance;
		String    		myRentalAllow;
		String    		myCapitalAllow;
		String    		myLoTaxBand;
		String    		myBasicTaxBand;
		String    		myLoAgeAllow;
		String    		myHiAgeAllow;
		String    		myAgeAllowLimit;
		String    		myAddAllowLimit;
		String    		myAddIncBound;
		String    		myLoTaxRate; 
		String    		myBasicTaxRate;
		String    		myHiTaxRate;
		String    		myIntTaxRate;
		String    		myDivTaxRate;
		String    		myHiDivTaxRate;
		String    		myAddTaxRate;
		String    		myAddDivTaxRate;
		String 	  		myCapTaxRate;
		String    		myHiCapTaxRate;
		String    		myTaxRegime;
		Calendar  		myYear;
		Cell      		myCell;
		int       		myAllRow;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(TaxYears);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(TaxYears)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet   = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myAllRow  = myTop.getRow();
			
				/* Count the number of TaxYears */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of tax years */
				myList = pData.getTaxYears();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Create the calendar instance */
				myYear    = Calendar.getInstance();
				myYear.set(pRange.getMaxYear(), Calendar.APRIL, 5, 0, 0, 0);
		
				/* Loop through the columns of the table */
				for (int i = myTop.getColumn();
			     	 i <= myBottom.getColumn();
			     	 i++, myYear.add(Calendar.YEAR, -1)) {
				
					/* Access the values */
					myAllowance     = mySheet.getCell(i, myAllRow).getContents();
					myLoTaxBand     = mySheet.getCell(i, myAllRow+1).getContents();
					myBasicTaxBand  = mySheet.getCell(i, myAllRow+2).getContents();
					myRentalAllow   = mySheet.getCell(i, myAllRow+3).getContents();
					myLoTaxRate     = mySheet.getCell(i, myAllRow+4).getContents();
					myBasicTaxRate  = mySheet.getCell(i, myAllRow+5).getContents();
					myIntTaxRate    = mySheet.getCell(i, myAllRow+6).getContents();
					myDivTaxRate    = mySheet.getCell(i, myAllRow+7).getContents();
					myHiTaxRate     = mySheet.getCell(i, myAllRow+8).getContents();
					myHiDivTaxRate  = mySheet.getCell(i, myAllRow+9).getContents();
					myTaxRegime     = mySheet.getCell(i, myAllRow+10).getContents();
					myLoAgeAllow    = mySheet.getCell(i, myAllRow+13).getContents();
					myHiAgeAllow    = mySheet.getCell(i, myAllRow+14).getContents();
					myAgeAllowLimit = mySheet.getCell(i, myAllRow+15).getContents();
					myCapitalAllow  = mySheet.getCell(i, myAllRow+18).getContents();
				
					/* Handle AddTaxRate which may be missing */
					myCell    	 = mySheet.getCell(i, myAllRow+11);
					myAddTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {					
						myAddTaxRate    = myCell.getContents();
					}
				
					/* Handle AddDivTaxRate which may be missing */
					myCell    		= mySheet.getCell(i, myAllRow+12);
					myAddDivTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddDivTaxRate = myCell.getContents();
					}
				
					/* Handle AddAllowLimit which may be missing */
					myCell          = mySheet.getCell(i, myAllRow+16);
					myAddAllowLimit = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddAllowLimit = myCell.getContents();
					}
			    
					/* Handle AddAllowLimit which may be missing */
					myCell        = mySheet.getCell(i, myAllRow+17);
					myAddIncBound = null;
					if (myCell.getType() != CellType.EMPTY) {
						myAddIncBound = myCell.getContents();
					}
			    
					/* Handle CapTaxRate which may be missing */
					myCell        = mySheet.getCell(i, myAllRow+19);
					myCapTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myCapTaxRate = myCell.getContents();
					}
			    
					/* Handle HiCapTaxRate which may be missing */
					myCell         = mySheet.getCell(i, myAllRow+20);
					myHiCapTaxRate = null;
					if (myCell.getType() != CellType.EMPTY) {
						myHiCapTaxRate = myCell.getContents();
					}
			    
					/* Add the Tax Year */
					myList.addItem(0,
								   myTaxRegime,
		        		           myYear.getTime(),
		    	   				   myAllowance,
		    	   				   myRentalAllow,
		    					   myLoAgeAllow,
		    					   myHiAgeAllow,
		    					   myCapitalAllow,
		    					   myAgeAllowLimit,
		    					   myAddAllowLimit,
		    					   myLoTaxBand,
		    					   myBasicTaxBand,
		    					   myAddIncBound,
		    					   myLoTaxRate,
		    					   myBasicTaxRate,
		    					   myHiTaxRate,
		    					   myIntTaxRate,
		    					   myDivTaxRate,
		    					   myHiDivTaxRate,
		    					   myAddTaxRate,
		    					   myAddDivTaxRate,
		    					   myCapTaxRate,
		    					   myHiCapTaxRate);
				
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
								"Failed to Load TaxYears",
								e);
		}
		
		/* Return to caller */
		return true;
	}
}
