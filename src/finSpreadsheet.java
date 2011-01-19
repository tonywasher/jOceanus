package finance;

import java.io.*;
import java.util.Calendar;
import java.util.Date;

import jxl.*;
import jxl.write.*;
import finance.finObject.ExceptionClass;
import finance.finBuilder;
import finance.finThread.statusCtl;
import finance.finZipFile.zipInputFile;
import finance.finZipFile.zipOutputFile;
import finance.finZipFile.zipOutputFile.zipMode;

public class finSpreadsheet {
	/* Static class names */
	private static String Static	   = "Static";
	private static String DataVersion  = "DataVersion";
	private static String AccountTypes = "AccountTypes";
	private static String TransTypes   = "TransactionTypes";
	private static String TransTypes1  = "TransType";
	private static String TaxClasses   = "TaxClasses";
	private static String TaxRegimes   = "TaxRegimes";
	private static String Frequencies  = "Frequencies";
	private static String TaxYears 	   = "TaxParameters";
	private static String Accounts 	   = "Accounts";
	private static String Rates 	   = "Rates";
	private static String Prices 	   = "Prices";
	private static String Prices1 	   = "SpotPricesData";
	private static String Patterns 	   = "Patterns";
	private static String Events 	   = "Events";
	
	/* Excel Parser class */
	public static class ExcelParser {
		/* Members */
		private String          theName     = null;
		private finBuilder      theBuilder  = null;
		private Workbook        theWorkbook = null;
	    private int             theMaxYear  = 2010;
	    private int             theMinYear  = 1982;
	    private statusCtl		theThread   = null;

		/* Access methods */
		public finData getData()    { return theBuilder.getData(); }
		
		/* Constructor */
		public ExcelParser(String    pName,
						   statusCtl pThread) {
			/* Store passed parameters */
			theName    = pName;
			theThread  = pThread;
			
			/* Create the data builder */
			theBuilder = new finBuilder();			
		}
		
		/* Open the Workbook */
		private void accessWorkbook() throws finObject.Exception {
			try {
				theWorkbook = Workbook.getWorkbook(new File(theName));
			}
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
											  "Failed to load workbook <" + theName + ">",
											  e);
			}
		}
		
		/* Close the Workbook */
		private void closeWorkbook() {
			theWorkbook.close();		
		}
		
		/* Access the Parameters */
		private boolean loadParameters() {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myCell;
			int     myCol;
			int     myRow;
			int     myStages;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName("YearRange");
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				
				myTop    = myRange[0].getTopLeft();
				myRow    = myTop.getRow();
				myCol    = myTop.getColumn();
			
				/* Access the Year Range */
				myCell = mySheet.getCell(myCol, myRow+1);
				theMinYear = Integer.parseInt(myCell.getContents());
				myCell = mySheet.getCell(myCol+1, myRow+1);
				theMaxYear = Integer.parseInt(myCell.getContents());
			}

			/* Calculate the number of stages */
			myStages = 14 + theMaxYear - theMinYear;
			
			/* Declare the number of stages */
			return theThread.setNumStages(myStages);
		}
		
		/* Access the Account Types */
		private boolean loadAccountTypes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(AccountTypes);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(AccountTypes)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of account types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
				     i <= myBottom.getRow();
				     i++) {
					/* Access the cell by reference */
					myCell = mySheet.getCell(myCol, i);
					
					/* Add the value into the finance tables */
					theBuilder.addAccountType(0, myCell.getContents());
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Transaction Types */
		private boolean loadTransTypes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(TransTypes1);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(TransTypes)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of transaction types */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
				     i <= myBottom.getRow();
				     i++) {
					/* Access the cell by reference */
					myCell = mySheet.getCell(myCol, i);
					
					/* If the event is a Tax Credit */
					if ((myCell.getContents().compareTo("TaxPaid")    == 0) ||
						(myCell.getContents().compareTo("TaxPaidInt") == 0) ||
						(myCell.getContents().compareTo("TaxPaidDiv") == 0) ||
						(myCell.getContents().compareTo("TaxPaidUnit") == 0)) {
						/* Ignore it */
						continue;						
					}
					
					/* Add the value into the finance tables */
					theBuilder.addTransType(0, myCell.getContents());
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Tax Types */
		private boolean loadTaxTypes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(TaxClasses);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(TaxClasses)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of tax classes */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
				     i <= myBottom.getRow();
				     i++) {
					/* Access the cell by reference */
					myCell = mySheet.getCell(myCol, i);
					
					/* Add the value into the finance tables */
					theBuilder.addTaxType(0, myCell.getContents());
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Tax Regimes */
		private boolean loadTaxRegimes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(TaxRegimes);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(TaxRegimes)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of tax classes */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
				     i <= myBottom.getRow();
				     i++) {
					/* Access the cell by reference */
					myCell = mySheet.getCell(myCol, i);
					
					/* Add the value into the finance tables */
					theBuilder.addTaxRegime(0, myCell.getContents());
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Frequencies */
		private boolean loadFrequencies() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(Frequencies);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(Frequencies)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of frequencies */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the single column range */
				for (int i = myTop.getRow();
				     i <= myBottom.getRow();
				     i++) {
					/* Access the cell by reference */
					myCell = mySheet.getCell(myCol, i);
					
					/* Add the value into the finance tables */
					theBuilder.addFrequency(0, myCell.getContents());
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Accounts */
		private boolean loadAccounts() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			int       myCol;
			String    myAccount;
			String    myAcType; 
			String    myParent;
			Date      myMaturity;
			Date      myClosed;
			DateCell  myDateCell;
			Cell      myCell;
			int       myTotal;
			int       myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(Accounts);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(Accounts)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of accounts */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the table in reverse order */
				for (int i = myBottom.getRow();
				     i >= myTop.getRow();
				     i--) {
					
					/* Access account and account type */
					myAccount = mySheet.getCell(myCol, i).getContents();
					myAcType  = mySheet.getCell(myCol+1, i).getContents();
					
					/* Handle maturity which may be missing */
					myCell     = mySheet.getCell(myCol+2, i);
					myMaturity = null;
					if (myCell.getType() != CellType.EMPTY) {
					    myDateCell = (DateCell)myCell;
					    myMaturity = myDateCell.getDate();
					}
				
					/* Handle parent which may be missing */
					myCell     = mySheet.getCell(myCol+3, i);
					myParent = null;
					if (myCell.getType() != CellType.EMPTY) {
					    myParent = myCell.getContents();
					}
				
					/* Handle closed which may be missing */
					myCell     = mySheet.getCell(myCol+4, i);
					myClosed = null;
					if (myCell.getType() != CellType.EMPTY) {
					    myDateCell = (DateCell)myCell;
					    myClosed = myDateCell.getDate();
					}
					
					/* Add the value into the finance tables */
					theBuilder.addAccount(0,
							              myAccount,
							              myAcType,
							              null,
							              myMaturity,
							              myClosed,
							              myParent);
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Rates */
		private boolean loadRates() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			int       myCol;
			String    myAccount;
			String    myRate;
			String    myBonus;
			Date      myExpiry;
			DateCell  myDateCell;
			Cell      myCell;
			int       myTotal;
			int       myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(Rates);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(Rates)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of rates */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
				     i <= myBottom.getRow();
				     i++) {
					
					/* Access account */
					myAccount = mySheet.getCell(myCol, i).getContents();
					
					/* Handle Rate which may be missing */
					myCell = mySheet.getCell(myCol+1, i);
					myRate = null;
					if (myCell.getType() != CellType.EMPTY) {
					    myRate = myCell.getContents();
					}
					
					/* Handle bonus which may be missing */
					myCell  = mySheet.getCell(myCol+2, i);
					myBonus = null;
					if (myCell.getType() != CellType.EMPTY) {
					    myBonus = myCell.getContents();
					}
					
					/* Handle expiration which may be missing */
					myCell     = mySheet.getCell(myCol+3, i);
					myExpiry = null;
					if (myCell.getType() != CellType.EMPTY) {
					    myDateCell = (DateCell)myCell;
					    myExpiry = myDateCell.getDate();
					}
					
					/* Add the value into the finance tables */
					if (myRate != null)
						theBuilder.addRate(0,
							               myAccount,
							               myRate,
							               myExpiry,
							               myBonus);
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Prices */
		private boolean loadPrices() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			int       myActRow;
			int       myDateCol;
			String    myAccount;
			String    myPrice; 
			Date      myDate;
			DateCell  myDateCell;
			Cell      myCell;
			int       myTotal;
			int       myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(Prices1);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(Prices)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet   = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myActRow  = myTop.getRow();
				myDateCol = myTop.getColumn();
			
				/* Count the number of tax classes */
				myTotal  = (myBottom.getRow() - myTop.getRow() + 1);
				myTotal *= (myBottom.getColumn() - myTop.getColumn() - 1);
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the table */
				for (int i = myTop.getRow() + 1;
				     i <= myBottom.getRow();
				     i++) {
					
					/* Access date */
					myDateCell = (DateCell)mySheet.getCell(myDateCol, i);
				    myDate     = myDateCell.getDate();
				    
					/* Loop through the columns of the table */
					for (int j = myTop.getColumn() + 2;
					     j <= myBottom.getColumn();
					     j++) {
						
						/* Access account */
						myAccount = mySheet.getCell(j, myActRow).getContents();
					
	  				    /* Handle price which may be missing */
					    myCell    = mySheet.getCell(j, i);
					    myPrice   = null;
					    if (myCell.getType() != CellType.EMPTY) {
							double myDouble = ((NumberCell)myCell).getValue();
							myPrice = Double.toString(myDouble);
					
						    /* Add any non-zero prices into the finance tables */
						    if (!myPrice.equals("0.0"))
						        theBuilder.addPrice(0,
						        		            myDate,
						    	   				    myAccount,
						    					    myPrice);
					    }
						
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Patterns */
		private boolean loadPatterns() throws finObject.Exception {
			/* Local variables */
			Range[]   	myRange;
			Sheet     	mySheet;
			Cell      	myTop;
			Cell      	myBottom;
			int       	myCol;
			Date      	myDate;
			String    	myAccount;
			String    	myDesc;
			String    	myPartner;
			String    	myTransType;
			String    	myAmount;
			String    	myFrequency;
			boolean   	isCredit;
			DateCell  	myDateCell;
			BooleanCell myBoolCell;
			int       	myTotal;
			int       	myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(Patterns);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(Patterns)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
			
				/* Count the number of patterns */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
				     i <= myBottom.getRow();
				     i++) {
					
					/* Access strings */
					myAccount 	= mySheet.getCell(myCol, i).getContents();
					myDesc    	= mySheet.getCell(myCol+2, i).getContents();
					myAmount  	= mySheet.getCell(myCol+3, i).getContents();
					myPartner 	= mySheet.getCell(myCol+4, i).getContents();
					myTransType = mySheet.getCell(myCol+5, i).getContents();
					myFrequency = mySheet.getCell(myCol+7, i).getContents();
					
					/* Handle Date */
				    myDateCell = (DateCell)mySheet.getCell(myCol+1, i);
				    myDate     = myDateCell.getDate();
					
					/* Handle isCredit */
				    myBoolCell 	= (BooleanCell)mySheet.getCell(myCol+6, i);
				    isCredit 	= myBoolCell.getValue();
					
					/* Add the value into the finance tables */
					theBuilder.addPattern(0,
						                  myDate,
						                  myDesc,
						                  myAmount,
						                  myAccount,
						                  myPartner,
						                  myTransType,
						                  myFrequency,
						                  isCredit);
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Tax Years */
		private boolean loadTaxYears() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			String    myAllowance;
			String    myRentalAllow;
			String    myCapitalAllow;
			String    myLoTaxBand;
			String    myBasicTaxBand;
			String    myLoAgeAllow;
			String    myHiAgeAllow;
			String    myAgeAllowLimit;
			String    myAddAllowLimit;
			String    myAddIncBound;
			String    myLoTaxRate; 
			String    myBasicTaxRate;
			String    myHiTaxRate;
			String    myIntTaxRate;
			String    myDivTaxRate;
			String    myHiDivTaxRate;
			String    myAddTaxRate;
			String    myAddDivTaxRate;
			String 	  myCapTaxRate;
			String    myHiCapTaxRate;
			String    myTaxRegime;
			Calendar  myYear;
			Cell      myCell;
			int       myAllRow;
			int       myTotal;
			int       myCount = 0;
			
			/* Find the range of cells */
			myRange = theWorkbook.findByName(TaxYears);
			
			/* Declare the new stage */
			if (!theThread.setNewStage(TaxYears)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet   = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myAllRow  = myTop.getRow();
				
				/* Count the number of TaxYears */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Create the calendar instance */
				myYear    = Calendar.getInstance();
				myYear.set(theMaxYear, Calendar.APRIL, 5, 0, 0, 0);
			
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
				    
			        theBuilder.addTaxYear(0,
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
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Events for a Year */
		private boolean loadYearEvents(Integer uYear) throws finObject.Exception {
			/* Local variables */
			String    myName;
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			String    myDesc;
			String    myAmount;
			String    myDebit;
			String    myCredit; 
			String    myUnits;
			String    myTranType;
			String	  myTaxCredit;
			int		  myYears;
			Cell      myCell;
			DateCell  myDateCell;
			Date      myDate;
			int       myCol;
			int       myTotal;
			int       myCount = 0;
			
			/* Find the range of cells */
			myName  = uYear.toString();
			myName  = "Finance" + myName.substring(2);
			myRange = theWorkbook.findByName(myName);
			
			/* Declare the new stage */
			if (!theThread.setNewStage("Events from " + uYear)) return false;
			
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
				
				/* Access the relevant sheet and Cell references */
				mySheet   = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop     = myRange[0].getTopLeft();
				myBottom  = myRange[0].getBottomRight();
				myCol     = myTop.getColumn();
				
				/* Count the number of TaxYears */
				myTotal  = myBottom.getRow() - myTop.getRow();
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Loop through the rows of the table */
				for (int i = myTop.getRow()+1;
				     i <= myBottom.getRow();
				     i++) {
					
					/* Access date */
					myDateCell = (DateCell)mySheet.getCell(myCol, i);
				    myDate     = myDateCell.getDate();
				    
					/* Access the values */
					myDesc         = mySheet.getCell(myCol+1, i).getContents();
					myAmount       = mySheet.getCell(myCol+2, i).getContents();
					myDebit        = mySheet.getCell(myCol+3, i).getContents();
					myCredit       = mySheet.getCell(myCol+4, i).getContents();
					myTranType     = mySheet.getCell(myCol+7, i).getContents();
				    
					/* Handle Units which may be missing */
					myCell    = mySheet.getCell(myCol+6, i);
					myUnits   = null;
					if (myCell.getType() != CellType.EMPTY) {
						double myDouble = ((NumberCell)myCell).getValue();
						myUnits = Double.toString(myDouble);
					}

					/* Handle Tax Credit which may be missing */
					myCell      = mySheet.getCell(myCol+8, i);
					myTaxCredit = null;
					if (myCell.getType() != CellType.EMPTY) {
						myTaxCredit = myCell.getContents();
					}

					/* Handle Years which may be missing */
					myCell    = mySheet.getCell(myCol+9, i);
					myYears   = 0;
					if (myCell.getType() != CellType.EMPTY) {
						myYears = Integer.parseInt(myCell.getContents());
					}

					/* If the event is a salary payment */
					if (myTranType.compareTo("TaxedIncome") == 0) {
						boolean bOK = true;
						/* Look for the TaxPaidInt in the next line */
						myCell = mySheet.getCell(myCol+7, i+1);
						if (myCell.getContents().compareTo("TaxPaid") != 0)
							bOK = false;
						
						/* Look for identical date */
						myDateCell = (DateCell)mySheet.getCell(myCol, i+1);
						if (myDateCell.getDate().compareTo(myDate) != 0)
							bOK = false;
						
						/* Look for identical description */
						myCell = mySheet.getCell(myCol+1, i+1);
						if ((myCell.getContents().compareTo(myDesc) != 0) &&
							(myCell.getContents().compareTo("PAYE") != 0))
							bOK = false;
						
						/* Look for identical debit */
						myCell = mySheet.getCell(myCol+3, i+1);
						if (myCell.getContents().compareTo(myDebit) != 0)
							bOK = false;
						
						/* If we are OK */
						if (bOK) {
							/* Access the amount as tax credit for this one */
							myTaxCredit = mySheet.getCell(myCol+2, i+1).getContents();
							
							/* Skip the next line */
							i++;
						}
						else {
							/* Throw an exception */
							throw new finObject.Exception(ExceptionClass.DATA,
														  "Salary event without matching TaxCredit");
						}
					}
					
					/* If the event is an interest payment */
					if (myTranType.compareTo("Interest") == 0) {
						boolean bOK = true;
						/* Look for the TaxPaidInt in the next line */
						myCell = mySheet.getCell(myCol+7, i+1);
						if (myCell.getContents().compareTo("TaxPaidInt") != 0)
							bOK = false;
						
						/* Look for identical date */
						myDateCell = (DateCell)mySheet.getCell(myCol, i+1);
						if (myDateCell.getDate().compareTo(myDate) != 0)
							bOK = false;
						
						/* Look for identical description */
						myCell = mySheet.getCell(myCol+1, i+1);
						if (myCell.getContents().compareTo(myDesc) != 0)
							bOK = false;
						
						/* Look for identical debit */
						myCell = mySheet.getCell(myCol+3, i+1);
						if (myCell.getContents().compareTo(myDebit) != 0)
							bOK = false;
						
						/* If we are OK */
						if (bOK) {
							/* Access the amount as tax credit for this one */
							myTaxCredit = mySheet.getCell(myCol+2, i+1).getContents();
							
							/* Skip the next line */
							i++;
						}
						else {
							/* Throw an exception */
							throw new finObject.Exception(ExceptionClass.DATA,
														  "Interest event without matching TaxCredit");
						}
					}
					
					/* If the event is a dividend payment */
					if (myTranType.compareTo("Dividend") == 0) {
						boolean bOK = true;
						/* Look for the TaxPaidDiv in the next line */
						myCell = mySheet.getCell(myCol+7, i+1);
						if (myCell.getContents().compareTo("TaxPaidDiv") != 0)
							bOK = false;
						
						/* Look for identical date */
						myDateCell = (DateCell)mySheet.getCell(myCol, i+1);
						if (myDateCell.getDate().compareTo(myDate) != 0)
							bOK = false;
						
						/* Look for identical description */
						myCell = mySheet.getCell(myCol+1, i+1);
						if (myCell.getContents().compareTo(myDesc) != 0)
							bOK = false;
						
						/* Look for identical debit */
						myCell = mySheet.getCell(myCol+3, i+1);
						if (myCell.getContents().compareTo(myDebit) != 0)
							bOK = false;
						
						/* If we are OK */
						if (bOK) {
							/* Access the amount as tax credit for this one */
							myTaxCredit = mySheet.getCell(myCol+2, i+1).getContents();
							
							/* Skip the next line */
							i++;
						}
						else {
							/* Throw an exception */
							throw new finObject.Exception(ExceptionClass.DATA,
														  "Dividend event without matching TaxCredit");
						}
					}
					
					/* If the event is a Unit Trust Dividend payment */
					if (myTranType.compareTo("UnitTrustDiv") == 0) {
						boolean bOK = true;
						/* Look for the TaxPaidUnit in the next line */
						myCell = mySheet.getCell(myCol+7, i+1);
						if (myCell.getContents().compareTo("TaxPaidUnit") != 0)
							bOK = false;
						
						/* Look for identical date */
						myDateCell = (DateCell)mySheet.getCell(myCol, i+1);
						if (myDateCell.getDate().compareTo(myDate) != 0)
							bOK = false;
						
						/* Look for identical description */
						myCell = mySheet.getCell(myCol+1, i+1);
						if (myCell.getContents().compareTo(myDesc) != 0)
							bOK = false;
						
						/* Look for identical debit */
						myCell = mySheet.getCell(myCol+3, i+1);
						if (myCell.getContents().compareTo(myDebit) != 0)
							bOK = false;
						
						/* If we are OK */
						if (bOK) {
							/* Access the amount as tax credit for this one */
							myTaxCredit = mySheet.getCell(myCol+2, i+1).getContents();
							
							/* Skip the next line */
							i++;
						}
						else {
							/* Throw an exception */
							throw new finObject.Exception(ExceptionClass.DATA,
														  "UT Dividend event without matching TaxCredit");
						}
					}
					
					/* If the event is a Tax Credit */
					if ((myTranType.compareTo("TaxPaid")    == 0) ||
						(myTranType.compareTo("TaxPaidInt") == 0) ||
						(myTranType.compareTo("TaxPaidDiv") == 0) ||
						(myTranType.compareTo("TaxPaidUnit") == 0)) {
						/* Throw an exception */
						throw new finObject.Exception(ExceptionClass.DATA,
													  "Unmatched TaxCredit event");						
					}
					
					/* Add the event */
					theBuilder.addEvent(0,
							            myDate,
							            myDesc,
					  		            myAmount,
							            myDebit,
							            myCredit,
							            myUnits,
							            myTranType,
							            myTaxCredit,
							            myYears);
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Events */
		private boolean loadEvents() throws finObject.Exception {
			boolean   bContinue = true;

			/* Loop through the columns of the table */
			for (int i = theMinYear;
			     i <= theMaxYear;
			     i++) {
					
				/* Access the values */
				bContinue = loadYearEvents(i);
				if (!bContinue) break;
			}
			
			/* Return to caller */
			return bContinue;
		}
		
		/* parse the Workbook */
		public finData loadWorkbook() throws finObject.Exception {
			boolean bContinue;
			
			/* Access workbook */
			accessWorkbook();
			
			/* Protect the workbook retrieval */
			try {
				/* Load tables */
				bContinue = loadParameters();
				if (bContinue) bContinue = loadAccountTypes();
				if (bContinue) bContinue = loadTransTypes();
				if (bContinue) bContinue = loadTaxTypes();
				if (bContinue) bContinue = loadTaxRegimes();
				if (bContinue) bContinue = loadFrequencies();
				if (bContinue) bContinue = loadTaxYears();
				if (bContinue) getData().calculateDateRange();
				if (bContinue) bContinue = loadAccounts();
				if (bContinue) bContinue = loadRates();
				if (bContinue) bContinue = loadPrices();
				if (bContinue) bContinue = loadPatterns();
				if (bContinue) theBuilder.validateAccounts();
				if (bContinue) bContinue = loadEvents();
			
				/* Declare the new stage */
				if (!theThread.setNewStage("Analysing data")) bContinue = false;
				
				/* Close the work book */
				closeWorkbook();
			
				/* Analyse the data */
				if (bContinue) getData().analyseData();
				
				/* Declare the new stage */
				if (!theThread.setNewStage("Refreshing data")) bContinue = false;
				
				/* Check for cancellation */
				if (!bContinue) 
					throw new finObject.Exception(ExceptionClass.EXCEL,
												  "Operation Cancelled");
			}
			
			/* Catch exceptions */
			catch (finObject.Exception e) {
				/* Cascade the error */
				throw e;
			}
			
			catch (Exception e) {
				/* Report the error */
				throw new finObject.Exception(ExceptionClass.EXCEL, 
						  					  "Failed to load Workbook",
						  					  e);				
			}
			
			/* Return the new data */
			return (bContinue) ? getData() : null;
		}	
	}
	
	public static class backupWriter {
		/* properties */
		private finData			 theData	 = null;
		private WritableWorkbook theWorkbook = null;
	    private statusCtl  		 theThread   = null;
		private boolean      	 isEncrypted = false;

		/* Constructor */
		public backupWriter(finData pData, statusCtl pThread) {
			/* Store the data */
			theData   = pData;
			theThread = pThread;
		}
		
		/* Set Encryption flag */
		public void setEncryption() { isEncrypted = true; }
		
		/* Create the backup */
		public void createBackup(File pFile) throws finObject.Exception {
			boolean             bContinue;
			OutputStream 		myStream  = null;
			FileOutputStream    myOutFile = null;
			zipOutputFile		myZipFile = null;
					
			/* Protect the workbook creation */
			try {
				/* Declare the number of stages */
				bContinue = theThread.setNumStages(12);

				/* If we are to continue */
				if (bContinue) {
					/* If we are not encrypted */
					if (!isEncrypted) {
						/* Create an output stream to the file */
						myOutFile = new FileOutputStream(pFile);
						myStream  = new BufferedOutputStream(myOutFile);
					}
				
					/* else we are encrypted */
					else {
						/* Create the new output Zip file */
						myZipFile = new zipOutputFile(pFile);
						myStream = myZipFile.getOutputStream(pFile, zipMode.COMPRESS_AND_ENCRYPT);
					}

					/* Create the work book attached to the output stream */
					theWorkbook = Workbook.createWorkbook(myStream);									
				}
				
				/* build the static types */
				if (bContinue) bContinue = buildAccountTypes();
				if (bContinue) bContinue = buildTransTypes();
				if (bContinue) bContinue = buildTaxClasses();
				if (bContinue) bContinue = buildTaxRegimes();
				if (bContinue) bContinue = buildFrequencies();
				if (bContinue) bContinue = buildTaxYears();
				if (bContinue) bContinue = buildAccounts();
				if (bContinue) bContinue = buildRates();
				if (bContinue) bContinue = buildPrices();
				if (bContinue) bContinue = buildPatterns();
				if (bContinue) bContinue = buildEvents();
				if (bContinue) bContinue = theThread.setNewStage("Writing");
				
				/* If we have created the workbook OK */
				if (bContinue) {
					/* Write it out to disk */
					theWorkbook.write();
					theWorkbook.close();
				}
				
				/* Close the buffers */
				myStream.close();
				myStream  = null;
				if (myOutFile != null) myOutFile.close();
				myOutFile = null;
				if (myZipFile != null) myZipFile.close();
				myZipFile = null;
				
				/* Check for cancellation */
				if (!bContinue) 
					throw new finObject.Exception(ExceptionClass.EXCEL,
												  "Operation Cancelled");
			} 
			
			/* Catch exceptions */
			catch (finObject.Exception e) {
				/* Delete the file on error */
				pFile.delete();

				/* Cascade the error */
				throw e;
			}
			
			catch (Exception e) {
				/* Delete the file on error */
				pFile.delete();
				
				/* Report the error */
				throw new finObject.Exception(ExceptionClass.EXCEL, 
						  					  "Failed to create Workbook",
						  					  e);				
			}
		}
		
		/* Build AccountType details */
		public boolean buildAccountTypes() throws finObject.Exception {
			WritableSheet 			mySheet;
			finStatic.ActTypeList 	myAcTypes;
			finStatic.AccountType	myCurr;
			int						myRow;
			int						myCount;
			int						myTotal;
			jxl.write.Label			myCell;
			int						myOffset = 1;
		
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(AccountTypes)) return false;
				
				/* Create the sheet */
				mySheet   = theWorkbook.createSheet(Static, 0);
			
				/* Store the DataVersion */
				myCell = new jxl.write.Label(0, 0, Long.toString(theData.getDataVersion()));
				mySheet.addCell(myCell);
				theWorkbook.addNameArea(DataVersion, mySheet, 0, 0, 0, 0);

				/* Access the account Types */
				myAcTypes = theData.getActTypes();
			
				/* Count the number of account types */
				myTotal   = myAcTypes.countItems();
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
				
				/* Loop through the account types */
				for (myCurr  = myAcTypes.getFirst();
					 myCurr != null;
					 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier and Name cells */
					myCell = new jxl.write.Label(myOffset, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					
					/* Add the name to the list */
					myCell = new jxl.write.Label(myOffset+1, myRow, myCurr.getName());
					mySheet.addCell(myCell);
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(AccountTypes, mySheet, myOffset, 0, myOffset+1, myRow-1);				
			}
		
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
											  "Failed to create AccountTypes",
											  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Build TransType details */
		public boolean buildTransTypes() throws finObject.Exception {
			WritableSheet 			mySheet;
			finStatic.TransTypeList	myTranTypes;
			finStatic.TransType		myCurr;
			int						myRow;
			int						myCount;
			int						myTotal;
			jxl.write.Label			myCell;
			int						myOffset = 3;
		
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(TransTypes)) return false;
				
				/* Access the sheet */
				mySheet   = theWorkbook.getSheet(Static);
			
				/* Access the transaction Types */
				myTranTypes = theData.getTransTypes();
			
				/* Count the number of transaction types */
				myTotal   = myTranTypes.countItems();
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
				
				/* Loop through the transaction types */
				for (myCurr  = myTranTypes.getFirst();
					 myCurr != null;
					 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier and Name cells */
					myCell = new jxl.write.Label(myOffset, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					
					/* Add the name to the list */
					myCell = new jxl.write.Label(myOffset+1, myRow, myCurr.getName());
					mySheet.addCell(myCell);
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(TransTypes, mySheet, myOffset, 0, myOffset+1, myRow-1);
			}
		
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
											  "Failed to create TransTypes",
											  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Build TaxClass details */
		public boolean buildTaxClasses() throws finObject.Exception {
			WritableSheet 			mySheet;
			finStatic.TaxTypeList 	myTaxTypes;
			finStatic.TaxType		myCurr;
			int						myRow;
			int						myCount;
			int						myTotal;
			jxl.write.Label			myCell;
			int						myOffset = 5;
		
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(TaxClasses)) return false;
				
				/* Create the sheet */
				mySheet   = theWorkbook.getSheet(Static);
			
				/* Access the tax classes */
				myTaxTypes = theData.getTaxTypes();
			
				/* Count the number of tax classes */
				myTotal   = myTaxTypes.countItems();
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
				
				/* Loop through the tax classes */
				for (myCurr  = myTaxTypes.getFirst();
					 myCurr != null;
					 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier and Name cells */
					myCell = new jxl.write.Label(myOffset, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					
					/* Add the name to the list */
					myCell = new jxl.write.Label(myOffset+1, myRow, myCurr.getName());
					mySheet.addCell(myCell);

					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(TaxClasses, mySheet, myOffset, 0, myOffset+1, myRow-1);
			}
		
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
											  "Failed to create TaxClasses",
											  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Build TaxRegime details */
		public boolean buildTaxRegimes() throws finObject.Exception {
			WritableSheet 			mySheet;
			finStatic.TaxRegimeList	myTaxRegimes;
			finStatic.TaxRegime		myCurr;
			int						myRow;
			int						myCount;
			int						myTotal;
			jxl.write.Label			myCell;
			int						myOffset = 7;
		
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(TaxRegimes)) return false;
				
				/* Create the sheet */
				mySheet   = theWorkbook.getSheet(Static);
			
				/* Access the tax regimes */
				myTaxRegimes = theData.getTaxRegimes();
			
				/* Count the number of tax regimes */
				myTotal   = myTaxRegimes.countItems();
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
				
				/* Loop through the tax regimes */
				for (myCurr  = myTaxRegimes.getFirst();
					 myCurr != null;
					 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier and Name cells */
					myCell = new jxl.write.Label(myOffset, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					
					/* Add the name to the list */
					myCell = new jxl.write.Label(myOffset+1, myRow, myCurr.getName());
					mySheet.addCell(myCell);

					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(TaxRegimes, mySheet, myOffset, 0, myOffset+1, myRow-1);
			}
		
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
											  "Failed to create TaxRegimes",
											  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Build Frequency details */
		public boolean buildFrequencies() throws finObject.Exception {
			WritableSheet 			mySheet;
			finStatic.FreqList 		myFreqs;
			finStatic.Frequency		myCurr;
			int						myRow;
			int						myCount;
			int						myTotal;
			jxl.write.Label			myCell;
			int						myOffset = 9;
		
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(Frequencies)) return false;
				
				/* Create the sheet */
				mySheet   = theWorkbook.getSheet(Static);
			
				/* Access the frequencies */
				myFreqs	  = theData.getFrequencys();
			
				/* Count the number of frequencies */
				myTotal   = myFreqs.countItems();
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
				
				/* Loop through the frequencies */
				for (myCurr  = myFreqs.getFirst();
					 myCurr != null;
					 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier and Name cells */
					myCell = new jxl.write.Label(myOffset, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					
					/* Add the name to the list */
					myCell = new jxl.write.Label(myOffset+1, myRow, myCurr.getName());
					mySheet.addCell(myCell);
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(Frequencies, mySheet, myOffset, 0, myOffset+1, myRow-1);
			}
		
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
											  "Failed to create Frequencies",
											  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Build TaxYear details */
		public boolean buildTaxYears() throws finObject.Exception {
			WritableSheet 			mySheet;
			finData.TaxParmList		myYears;
			finData.TaxParms		myCurr;
			int						myRow;
			int						myCount;
			int						myTotal;
			jxl.write.Label			myCell;
			jxl.write.DateTime		myDate;
			WritableCellFormat		myFormat;
		
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(TaxYears)) return false;
				
				/* Create the formats */
				myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
			
				/* Create the sheet */
				mySheet   = theWorkbook.createSheet(TaxYears, 0);
			
				/* Access the tax years */
				myYears	  = theData.getTaxYears();
			
				/* Count the number of years */
				myTotal   = myYears.countItems();
				
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
				
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
				
				/* Loop through the tax years */
				for (myCurr  = myYears.getFirst();
					 myCurr != null;
					 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier cell */
					myCell = new jxl.write.Label(0, myRow, 
							 					 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(1, myRow, 
		 					 					 Long.toString(myCurr.getTaxRegime().getId()));
					mySheet.addCell(myCell);
					
					/* Create the Date cells */
					myDate = new jxl.write.DateTime(2, myRow, 
							   						myCurr.getDate().getDate(),
							   						myFormat);
					mySheet.addCell(myDate);
					
					/* Create the Money cells */
					myCell = new jxl.write.Label(3, myRow, 
										   		 myCurr.getAllowance().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(4, myRow, 
							 					 myCurr.getLoAgeAllow().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(5, myRow, 
							 					 myCurr.getHiAgeAllow().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(6, myRow, 
							 					 myCurr.getCapitalAllow().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(7, myRow, 
					         					 myCurr.getRentalAllowance().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(8, myRow, 
							 					 myCurr.getAgeAllowLimit().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(9, myRow, 
							   			         myCurr.getLoBand().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(10, myRow, 
										         myCurr.getBasicBand().format(false));
					mySheet.addCell(myCell);
					
					/* Create the Rate cells */
					myCell = new jxl.write.Label(11, myRow, 
										         myCurr.getLoTaxRate().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(12, myRow, 
							   			         myCurr.getBasicTaxRate().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(13, myRow, 
							   			         myCurr.getHiTaxRate().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(14, myRow, 
							   			         myCurr.getIntTaxRate().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(15, myRow, 
									             myCurr.getDivTaxRate().format(false));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(16, myRow, 
										         myCurr.getHiDivTaxRate().format(false));
					mySheet.addCell(myCell);
					if (myCurr.getAddAllowLimit() != null) {
						myCell = new jxl.write.Label(17, myRow, 
													 myCurr.getAddAllowLimit().format(false));
						mySheet.addCell(myCell);
					}
					if (myCurr.getAddIncBound() != null) {
						myCell = new jxl.write.Label(18, myRow, 
													 myCurr.getAddIncBound().format(false));
						mySheet.addCell(myCell);
					}
					if (myCurr.getAddTaxRate() != null) {
						myCell = new jxl.write.Label(19, myRow, 
													 myCurr.getAddTaxRate().format(false));
						mySheet.addCell(myCell);
					}
					if (myCurr.getAddDivTaxRate() != null) {
						myCell = new jxl.write.Label(20, myRow, 
													 myCurr.getAddDivTaxRate().format(false));
						mySheet.addCell(myCell);
					}
					if (myCurr.getCapTaxRate() != null) {
						myCell = new jxl.write.Label(21, myRow, 
													 myCurr.getCapTaxRate().format(false));
						mySheet.addCell(myCell);
					}
					if (myCurr.getHiCapTaxRate() != null) {
						myCell = new jxl.write.Label(22, myRow, 
													 myCurr.getHiCapTaxRate().format(false));
						mySheet.addCell(myCell);
					}
					
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
			
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(TaxYears, mySheet, 0, 0, 22, myRow-1);
			}
		
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
											  "Failed to create TaxYears",
											  e);
			}
			
			/* Return to caller */
			return true;
		}
	
		/* Build Accounts details */
		public boolean buildAccounts() throws finObject.Exception {
			WritableSheet 			mySheet;
			finData.AccountList		myAccounts;
			finData.Account			myCurr;
			int						myRow;
			int						myCount;
			int						myTotal;
			jxl.write.Label			myCell;
			jxl.write.DateTime		myDate;
			WritableCellFormat		myFormat;
	
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(Accounts)) return false;
			
				/* Create the formats */
				myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
		
				/* Create the sheet */
				mySheet    = theWorkbook.createSheet(Accounts, 0);
		
				/* Access the accounts */
				myAccounts = theData.getAccounts();
		
				/* Count the number of accounts */
				myTotal   = myAccounts.countItems();
			
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
			
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
			
				/* Loop through the accounts */
				for (myCurr  = myAccounts.getFirst();
				 	 myCurr != null;
				 	 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier cell */
					myCell   = new jxl.write.Label(0, myRow, 
												   Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					myCell   = new jxl.write.Label(2, myRow,
												   Long.toString(myCurr.getActType().getId()));
					mySheet.addCell(myCell);
					if (myCurr.getParent() != null) {
						myCell   = new jxl.write.Label(4, myRow, 
											           Long.toString(myCurr.getParent().getId()));
						mySheet.addCell(myCell);
					}
				
					/* Create the String cells */
					myCell = new jxl.write.Label(1, myRow, myCurr.getName());
					mySheet.addCell(myCell);
					if (myCurr.getDesc() != null) {
						myCell = new jxl.write.Label(3, myRow, myCurr.getDesc());
						mySheet.addCell(myCell);
					}
					
					/* Create the Date cells */
					if (myCurr.getMaturity() != null) {
						myDate = new jxl.write.DateTime(5, myRow, 
						   						    	myCurr.getMaturity().getDate(),
						   						    	myFormat);
						mySheet.addCell(myDate);
					}
					if (myCurr.getClose() != null) {
						myDate = new jxl.write.DateTime(6, myRow, 
						   						    	myCurr.getClose().getDate(),
						   						    	myFormat);
						mySheet.addCell(myDate);
					}
				
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
		
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(Accounts, mySheet, 0, 0, 6, myRow-1);
			}
	
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to create Accounts",
										  	  e);
			}
		
			/* Return to caller */
			return true;
		}
		
		/* Build Rates details */
		public boolean buildRates() throws finObject.Exception {
			WritableSheet 		mySheet;
			finData.RateList	myRates;
			finData.Rate		myCurr;
			int					myRow;
			int					myCount;
			int					myTotal;
			jxl.write.Label		myCell;
			jxl.write.DateTime	myDate;
			WritableCellFormat	myFormat;
	
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(Rates)) return false;
			
				/* Create the formats */
				myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
		
				/* Create the sheet */
				mySheet    = theWorkbook.createSheet(Rates, 0);
		
				/* Access the Rates */
				myRates = theData.getRates();
		
				/* Count the number of rates */
				myTotal   = myRates.countItems();
			
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
			
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
			
				/* Loop through the Rates */
				for (myCurr  = myRates.getFirst();
				 	 myCurr != null;
				 	 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier cell */
					myCell = new jxl.write.Label(0, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(1, myRow,
												 Long.toString(myCurr.getAccount().getId()));
					mySheet.addCell(myCell);
				
					/* Create the Rate cells */
					myCell = new jxl.write.Label(2, myRow, 
												 myCurr.getRate().format(false));
					mySheet.addCell(myCell);
					if (myCurr.getBonus() != null) {
						myCell = new jxl.write.Label(3, myRow, 
													 myCurr.getBonus().format(false));
						mySheet.addCell(myCell);
					}
					
					/* Create the Date cells */
					if (myCurr.getEndDate() != null) {
						myDate = new jxl.write.DateTime(4, myRow, 
														myCurr.getEndDate().getDate(),
														myFormat);
						mySheet.addCell(myDate);
					}
				
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
		
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(Rates, mySheet, 0, 0, 4, myRow-1);
			}
	
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to create Rates",
										  	  e);
			}
		
			/* Return to caller */
			return true;
		}
		
		/* Build Prices details */
		public boolean buildPrices() throws finObject.Exception {
			WritableSheet 		mySheet;
			finData.PriceList	myPrices;
			finData.Price		myCurr;
			int					myRow;
			int					myCount;
			int					myTotal;
			jxl.write.Label		myCell;
			jxl.write.DateTime	myDate;
			WritableCellFormat	myFormat;
	
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(Prices)) return false;
			
				/* Create the formats */
				myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
		
				/* Create the sheet */
				mySheet    = theWorkbook.createSheet(Prices, 0);
		
				/* Access the prices */
				myPrices  = theData.getPrices();
				
				/* Count the number of prices */
				myTotal   = myPrices.countItems();
			
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
			
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
			
				/* Loop through the Prices */
				for (myCurr  = myPrices.getFirst();
				 	 myCurr != null;
				 	 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier cell */
					myCell = new jxl.write.Label(0, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(1, myRow, 
												 Long.toString(myCurr.getAccount().getId()));
					mySheet.addCell(myCell);
				
					/* Create the Price cells */
					myCell = new jxl.write.Label(3, myRow, 
							 					 myCurr.getPrice().format(false));
					mySheet.addCell(myCell);
					
					/* Create the Date cells */
					myDate = new jxl.write.DateTime(2, myRow, 
													myCurr.getDate().getDate(),
													myFormat);
					mySheet.addCell(myDate);
				
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
		
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(Prices, mySheet, 0, 0, 3, myRow-1);
			}
	
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to create Prices",
										  	  e);
			}
		
			/* Return to caller */
			return true;
		}
		
		/* Build Patterns details */
		public boolean buildPatterns() throws finObject.Exception {
			WritableSheet 		mySheet;
			finData.PatternList	myPatterns;
			finData.Pattern		myCurr;
			int					myRow;
			int					myCount;
			int					myTotal;
			jxl.write.Label		myCell;
			jxl.write.Boolean	myCredit;
			jxl.write.DateTime	myDate;
			WritableCellFormat	myFormat;
	
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(Patterns)) return false;
			
				/* Create the formats */
				myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
		
				/* Create the sheet */
				mySheet    = theWorkbook.createSheet(Patterns, 0);
		
				/* Access the patterns */
				myPatterns = theData.getPatterns();
		
				/* Count the number of patterns */
				myTotal   = myPatterns.countItems();
			
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
			
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
			
				/* Loop through the Patterns */
				for (myCurr  = myPatterns.getFirst();
				 	 myCurr != null;
				 	 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier cells */
					myCell = new jxl.write.Label(0, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(1, myRow, 
												 Long.toString(myCurr.getAccount().getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(2, myRow, 
												 Long.toString(myCurr.getPartner().getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(5, myRow, 
												 Long.toString(myCurr.getTransType().getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(6, myRow,
												 Long.toString(myCurr.getFrequency().getId()));
					mySheet.addCell(myCell);
				
					/* Create the Amount cells */
					myCell = new jxl.write.Label(8, myRow, 
												 myCurr.getAmount().format(false));
					mySheet.addCell(myCell);
				
					/* Create the IsCredit cells */
					myCredit = new jxl.write.Boolean(4, myRow, myCurr.isCredit(), myFormat);
					mySheet.addCell(myCredit);
					
					/* Create the Desc cells */
					myCell = new jxl.write.Label(7, myRow, myCurr.getDesc());
					mySheet.addCell(myCell);
					
					/* Create the Date cells */
					myDate = new jxl.write.DateTime(3, myRow, 
													myCurr.getDate().getDate(),
													myFormat);
					mySheet.addCell(myDate);
				
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
		
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(Patterns, mySheet, 0, 0, 8, myRow-1);
			}
	
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to create Patterns",
										  	  e);
			}
		
			/* Return to caller */
			return true;
		}
		
		/* Build Event details */
		public boolean buildEvents() throws finObject.Exception {
			WritableSheet 				mySheet;
			finData.EventList			myEvents;
			finData.Event				myCurr;
			int							myRow;
			int							myCount;
			int							myTotal;
			jxl.write.Label				myCell;
			jxl.write.DateTime			myDate;
			WritableCellFormat			myFormat;
	
			/* Protect against exceptions */
			try { 
				/* Declare the new stage */
				if (!theThread.setNewStage(Events)) return false;
			
				/* Create the formats */
				myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
		
				/* Create the sheet */
				mySheet    = theWorkbook.createSheet(Events, 0);
		
				/* Access the events */
				myEvents = theData.getEvents();
		
				/* Count the number of events */
				myTotal   = myEvents.countItems();
			
				/* Declare the number of steps */
				if (!theThread.setNumSteps(myTotal)) return false;
			
				/* Initialise counts */
				myRow   = 0;
				myCount = 0;
			
				/* Loop through the Events */
				for (myCurr  = myEvents.getFirst();
				 	 myCurr != null;
				 	 myCurr  = myCurr.getNext(), myRow++) {
					/* Create the Identifier cell */
					myCell = new jxl.write.Label(0, myRow, 
												 Long.toString(myCurr.getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(3, myRow, 
												 Long.toString(myCurr.getDebit().getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(4, myRow, 
												 Long.toString(myCurr.getCredit().getId()));
					mySheet.addCell(myCell);
					myCell = new jxl.write.Label(6, myRow, 
												 Long.toString(myCurr.getTransType().getId()));
					mySheet.addCell(myCell);
				
					/* Create the Amount cells */
					myCell = new jxl.write.Label(5, myRow,
												 myCurr.getAmount().format(false));
					mySheet.addCell(myCell);
					if (myCurr.getUnits() != null) {
						myCell = new jxl.write.Label(7, myRow, 
													 myCurr.getUnits().format(false));
						mySheet.addCell(myCell);
					}
					if (myCurr.getTaxCredit() != null) {
						myCell = new jxl.write.Label(8, myRow, 
													 myCurr.getTaxCredit().format(false));
						mySheet.addCell(myCell);
					}
					if (myCurr.getYears() != null) {
						myCell = new jxl.write.Label(9, myRow, 
													 myCurr.getYears().toString());
						mySheet.addCell(myCell);
					}
					
					/* Create the Desc cells */
					myCell = new jxl.write.Label(2, myRow, myCurr.getDesc());
					mySheet.addCell(myCell);
					
					/* Create the Date cells */
					myDate = new jxl.write.DateTime(1, myRow, 
													myCurr.getDate().getDate(),
													myFormat);
					mySheet.addCell(myDate);
				
					/* Report the progress */
					myCount++;
					if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
				}
		
				/* Add the Range name */
				if (myRow > 0)
					theWorkbook.addNameArea(Events, mySheet, 0, 0, 9, myRow-1);
			}
	
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to create Events",
										  	  e);
			}
		
			/* Return to caller */
			return true;
		}
	}
	
	/* backupReader class */
	public static class backupReader {
		/* Members */
		private finBuilder      theBuilder  = null;
		private Workbook        theWorkbook = null;
	    private statusCtl		theThread   = null;
		private boolean      	isEncrypted = false;

		/* Access methods */
		public finData getData()    { return theBuilder.getData(); }
		
		/* Constructor */
		public backupReader(statusCtl pThread) {
			/* Store passed parameters */
			theThread  = pThread;
			
			/* Create the data builder */
			theBuilder = new finBuilder();			
		}
		
		/* Set Encryption flag */
		public void setEncryption() { isEncrypted = true; }
				
		/* parse the Workbook */
		public finData loadBackup(File pFile) throws finObject.Exception {
			boolean             bContinue;
			InputStream 		myStream  = null;
			FileInputStream     myInFile  = null;
			zipInputFile 		myFile;
			
			/* Protect the workbook retrieval */
			try {
				/* Declare the number of stages */
				bContinue = theThread.setNumStages(14);

				/* Note the stage */
				if (bContinue) bContinue = theThread.setNewStage("Loading");
				
				/* If we are OK */
				if (bContinue) {
					/* If we are not encrypted */
					if (!isEncrypted) {
						/* Create an input stream to the file */
						myInFile = new FileInputStream(pFile);
						myStream = new BufferedInputStream(myInFile);
					}
									
					/* else we are encrypted */
					else {
						/* Access the zip file and get an input stream from the first entry */
						myFile = new zipInputFile(pFile);
						myStream = myFile.getInputStream(myFile.getFiles());
					}
					
					/* Access workbook */
					theWorkbook = Workbook.getWorkbook(myStream);
					
					/* Close the Stream to force out errors */
					myStream.close();
				}
			
				/* Load tables */
				if (bContinue) bContinue = loadAccountTypes();
				if (bContinue) bContinue = loadTransTypes();
				if (bContinue) bContinue = loadTaxTypes();
				if (bContinue) bContinue = loadTaxRegimes();
				if (bContinue) bContinue = loadFrequencies();
				if (bContinue) bContinue = loadTaxYears();
				if (bContinue) getData().calculateDateRange();
				if (bContinue) bContinue = loadAccounts();
				if (bContinue) bContinue = loadRates();
				if (bContinue) bContinue = loadPrices();
				if (bContinue) bContinue = loadPatterns();
				if (bContinue) theBuilder.validateAccounts();
				if (bContinue) bContinue = loadEvents();
			
				/* Declare the new stage */
				if (!theThread.setNewStage("Analysing data")) bContinue = false;
				
				/* Close the work book */
				theWorkbook.close();		
			
				/* Analyse the data */
				if (bContinue) getData().analyseData();
				if (!theThread.setNewStage("Refreshing data")) bContinue = false;
								
				/* Check for cancellation */
				if (!bContinue) 
					throw new finObject.Exception(ExceptionClass.EXCEL,
												  "Operation Cancelled");
			}
			
			/* Catch exceptions */
			catch (finObject.Exception e) {
				/* Show we are cancelled */
				bContinue = false;
				
				/* Cascade the error */
				throw e;
			}
			
			catch (Exception e) {
				/* Show we are cancelled */
				bContinue = false;
				
				/* Report the error */
				throw new finObject.Exception(ExceptionClass.EXCEL, 
						  					  "Failed to load Workbook",
						  					  e);				
			}
						
			/* Return the new data */
			return (bContinue) ? getData() : null;
		}
		
		/* Access the Account Types */
		private boolean loadAccountTypes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			long    myID;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Protect against exceptions */
			try { 
				/* Find the DataVersion */
				myRange = theWorkbook.findByName(DataVersion);
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
				
					/* Access the cells by reference */
					myCell = mySheet.getCell(myTop.getColumn(), myTop.getRow());
					theBuilder.setDataVersion(Long.parseLong(myCell.getContents()));
				}
					
				/* Find the range of cells */
				myRange = theWorkbook.findByName(AccountTypes);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(AccountTypes)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
					myBottom = myRange[0].getBottomRight();
					myCol    = myTop.getColumn();
			
					/* Count the number of account types */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the single column range */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
						/* Access the cells by reference */
						myCell = mySheet.getCell(myCol, i);
						myID   = Long.parseLong(myCell.getContents());
						myCell = mySheet.getCell(myCol+1, i);
					
						/* Add the value into the finance tables */
						theBuilder.addAccountType(myID, myCell.getContents());
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
						if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to Load Account Types",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Transaction Types */
		private boolean loadTransTypes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			long	myID;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Protect against exceptions */
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(TransTypes);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(TransTypes)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
					myBottom = myRange[0].getBottomRight();
					myCol    = myTop.getColumn();
			
					/* Count the number of transaction types */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the single column range */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
						/* Access the cell by reference */
						myCell = mySheet.getCell(myCol, i);
						myID   = Long.parseLong(myCell.getContents());
						myCell = mySheet.getCell(myCol+1, i);
					
						/* Add the value into the finance tables */
						theBuilder.addTransType(myID, myCell.getContents());
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to loas Transaction Types",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Tax Types */
		private boolean loadTaxTypes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			long	myID;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Protect against exceptions */
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(TaxClasses);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(TaxClasses)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
					myBottom = myRange[0].getBottomRight();
					myCol    = myTop.getColumn();
			
					/* Count the number of tax classes */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the single column range */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
						/* Access the cell by reference */
						myCell = mySheet.getCell(myCol, i);
						myID   = Long.parseLong(myCell.getContents());
						myCell = mySheet.getCell(myCol+1, i);
					
						/* Add the value into the finance tables */
						theBuilder.addTaxType(myID, myCell.getContents());
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Tax Classes",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Tax Regimes */
		private boolean loadTaxRegimes() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			long	myID;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Protect against exceptions */
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(TaxRegimes);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(TaxRegimes)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
					myBottom = myRange[0].getBottomRight();
					myCol    = myTop.getColumn();
			
					/* Count the number of tax classes */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the single column range */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
						/* Access the cell by reference */
						myCell = mySheet.getCell(myCol, i);
						myID   = Long.parseLong(myCell.getContents());
						myCell = mySheet.getCell(myCol+1, i);
					
						/* Add the value into the finance tables */
						theBuilder.addTaxRegime(myID, myCell.getContents());
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Tax Regimes",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Frequencies */
		private boolean loadFrequencies() throws finObject.Exception {
			/* Local variables */
			Range[] myRange;
			Sheet   mySheet;
			Cell    myTop;
			Cell    myBottom;
			Cell    myCell;
			long    myID;
			int     myCol;
			int     myTotal;
			int     myCount = 0;
			
			/* Protect against exceptions */
			try { 
				/* Find the range of cells */
				myRange = theWorkbook.findByName(Frequencies);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(Frequencies)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
					myBottom = myRange[0].getBottomRight();
					myCol    = myTop.getColumn();
			
					/* Count the number of frequencies */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the single column range */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
						/* Access the cell by reference */
						myCell = mySheet.getCell(myCol, i);
						myID   = Long.parseLong(myCell.getContents());
						myCell = mySheet.getCell(myCol+1, i);
					
						/* Add the value into the finance tables */
						theBuilder.addFrequency(myID, myCell.getContents());
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Frequencies",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Accounts */
		private boolean loadAccounts() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			int       myCol;
			long	  myID;
			String    myAccount;
			String	  myDesc;
			long      myAcTypeId;
			long      myParentId;
			Date      myMaturity;
			Date      myClosed;
			DateCell  myDateCell;
			Cell      myCell;
			int       myTotal;
			int       myCount = 0;
			
			/* Protect against exceptions */
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(Accounts);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(Accounts)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
					myBottom = myRange[0].getBottomRight();
					myCol    = myTop.getColumn();
			
					/* Count the number of accounts */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the table */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
					
						/* Access account and account type */
						myCell    	= mySheet.getCell(myCol, i);
						myID      	= Long.parseLong(myCell.getContents());
						myAccount 	= mySheet.getCell(myCol+1, i).getContents();
						myCell    	= mySheet.getCell(myCol+2, i);
						myAcTypeId	= Long.parseLong(myCell.getContents());
					
						/* Handle description which may be missing */
						myCell     = mySheet.getCell(myCol+3, i);
						myDesc = null;
						if (myCell.getType() != CellType.EMPTY) {
							myDesc = myCell.getContents();
						}
				
						/* Handle maturity which may be missing */
						myCell     = mySheet.getCell(myCol+5, i);
						myMaturity = null;
						if (myCell.getType() != CellType.EMPTY) {
							myDateCell = (DateCell)myCell;
							myMaturity = myDateCell.getDate();
						}
				
						/* Handle closed which may be missing */
						myCell     = mySheet.getCell(myCol+6, i);
						myClosed = null;
						if (myCell.getType() != CellType.EMPTY) {
							myDateCell = (DateCell)myCell;
							myClosed = myDateCell.getDate();
						}
					
						/* Handle parentId which may be missing */
						myCell     = mySheet.getCell(myCol+4, i);
						myParentId = -1;
						if (myCell.getType() != CellType.EMPTY) {
							myParentId	= Long.parseLong(myCell.getContents());
						}
					
						/* Add the value into the finance tables */
						theBuilder.addAccount(myID,
							              	  myAccount,
							              	  myAcTypeId,
							              	  myDesc,
							              	  myMaturity,
							              	  myClosed,
							              	  myParentId);
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Accounts",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Rates */
		private boolean loadRates() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			int       myCol;
			String    myRate;
			String    myBonus;
			Date      myEndDate;
			long      myID;
			long      myAccountID;
			DateCell  myDateCell;
			Cell      myCell;
			int       myTotal;
			int       myCount = 0;
			
			/* Protect against exceptions */
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(Rates);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(Rates)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet  = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop    = myRange[0].getTopLeft();
					myBottom = myRange[0].getBottomRight();
					myCol    = myTop.getColumn();
			
					/* Count the number of rates */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the table */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
					
						/* Access id and account id */
						myCell    	= mySheet.getCell(myCol, i);
						myID      	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+1, i);
						myAccountID	= Long.parseLong(myCell.getContents());
					
						/* Handle Rate */
						myCell     	= mySheet.getCell(myCol+2, i);
						myRate 		= myCell.getContents();
					
						/* Handle bonus which may be missing */
						myCell     = mySheet.getCell(myCol+3, i);
						myBonus	   = null;
						if (myCell.getType() != CellType.EMPTY) {
							myBonus = myCell.getContents();
						}
					
						/* Handle endDate which may be missing */
						myCell     = mySheet.getCell(myCol+4, i);
						myEndDate  = null;
						if (myCell.getType() != CellType.EMPTY) {
							myDateCell = (DateCell)myCell;
							myEndDate  = myDateCell.getDate();
						}
					
						/* Add the value into the finance tables */
						theBuilder.addRate(myID,
							               myAccountID,
							               myRate,
							               myEndDate,
							               myBonus);
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Rates",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Prices */
		private boolean loadPrices() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			int       myCol;
			long      myID;
			long      myAccountID;
			String    myPrice; 
			Date      myDate;
			DateCell  myDateCell;
			Cell      myCell;
			int       myTotal;
			int       myCount = 0;
			
			/* Protect against exceptions */
			try { 
				/* Find the range of cells */
				myRange = theWorkbook.findByName(Prices);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(Prices)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet   = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop     = myRange[0].getTopLeft();
					myBottom  = myRange[0].getBottomRight();
					myCol     = myTop.getColumn();
			
					/* Count the number of prices */
					myTotal  = (myBottom.getRow() - myTop.getRow() + 1);
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the table */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
										
						/* Access id and account id */
						myCell    	= mySheet.getCell(myCol, i);
						myID      	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+1, i);
						myAccountID	= Long.parseLong(myCell.getContents());
					
						/* Handle Price */
						myCell     	= mySheet.getCell(myCol+3, i);
						myPrice		= myCell.getContents();
					
						/* Handle Date */
						myCell     = mySheet.getCell(myCol+2, i);
						myDateCell = (DateCell)myCell;
						myDate     = myDateCell.getDate();
					
						/* Add the price into the finance tables */
						theBuilder.addPrice(myID,
			        		                myDate,
			        		                myAccountID,
			        		                myPrice);
						
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Prices",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Patterns */
		private boolean loadPatterns() throws finObject.Exception {
			/* Local variables */
			Range[]     myRange;
			Sheet       mySheet;
			Cell        myTop;
			Cell        myBottom;
			String      myDesc;
			String      myAmount;
			long	    myAccount;
			long        myPartner; 
			long        myFrequency;
			boolean     isCredit;
			long        myTranType;
			long        myID;
			Cell        myCell;
			DateCell    myDateCell;
			BooleanCell myBoolCell;
			Date        myDate;
			int         myCol;
			int         myTotal;
			int         myCount = 0;
			
			/* Protect against exceptions*/
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(Patterns);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(Patterns)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet   = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop     = myRange[0].getTopLeft();
					myBottom  = myRange[0].getBottomRight();
					myCol     = myTop.getColumn();
				
					/* Count the number of Patterns */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the table */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
					
						/* Access ids */
						myCell    	= mySheet.getCell(myCol, i);
						myID      	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+1, i);
						myAccount  	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+2, i);
						myPartner  	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+5, i);
						myTranType 	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+6, i);
						myFrequency	= Long.parseLong(myCell.getContents());
					
						/* Access date */
						myDateCell = (DateCell)mySheet.getCell(myCol+3, i);
						myDate     = myDateCell.getDate();
				    
						/* Access Flag */
						myBoolCell = (BooleanCell)mySheet.getCell(myCol+4, i);
						isCredit   = myBoolCell.getValue();
				    
						/* Access the values */
						myDesc         = mySheet.getCell(myCol+7, i).getContents();
						myAmount       = mySheet.getCell(myCol+8, i).getContents();
				    
						/* Add the pattern */
						theBuilder.addPattern(myID,
							                  myDate,
							                  myDesc,
					  		                  myAmount,
							                  myAccount,
							                  myPartner,
							                  myTranType,
							                  myFrequency,
							                  isCredit);
					
						/* Report the progress */
					    myCount++;
					    if ((myCount % theThread.getReportingSteps()) == 0) 
					    	if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Patterns",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Tax Years */
		private boolean loadTaxYears() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			String    myAllowance;
			String    myRentalAllow;
			String    myLoTaxBand;
			String    myBasicTaxBand;
			String    myLoAgeAllow;
			String    myHiAgeAllow;
			String	  myCapitalAllow;
			String    myAgeAllowLimit;
			String    myAddAllowLimit;
			String    myAddIncBound;
			String    myLoTaxRate; 
			String    myBasicTaxRate;
			String    myHiTaxRate;
			String    myIntTaxRate;
			String    myDivTaxRate;
			String    myHiDivTaxRate;
			String    myAddTaxRate;
			String    myAddDivTaxRate;
			String	  myCapTaxRate;
			String	  myHiCapTaxRate;
			Date      myYear;
			Cell	  myCell;
			DateCell  myDateCell;
			long      myID;
			long      myRegimeID;
			int		  myCol;
			int       myTotal;
			int       myCount = 0;
			
			/* Protect against exceptions */
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(TaxYears);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(TaxYears)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet   = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop     = myRange[0].getTopLeft();
					myBottom  = myRange[0].getBottomRight();
					myCol  	  = myTop.getColumn();
				
					/* Count the number of TaxYears */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the table */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
					
						/* Access IDs */
						myCell    	= mySheet.getCell(myCol, i);
						myID      	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+1, i);
						myRegimeID 	= Long.parseLong(myCell.getContents());
					
						/* Handle Date */
						myCell     = mySheet.getCell(myCol+2, i);
						myDateCell = (DateCell)myCell;
						myYear     = myDateCell.getDate();
					
						/* Access the values */
						myAllowance     = mySheet.getCell(myCol+3, i).getContents();
						myLoAgeAllow    = mySheet.getCell(myCol+4, i).getContents();
						myHiAgeAllow    = mySheet.getCell(myCol+5, i).getContents();
						myCapitalAllow  = mySheet.getCell(myCol+6, i).getContents();
						myRentalAllow   = mySheet.getCell(myCol+7, i).getContents();
						myAgeAllowLimit = mySheet.getCell(myCol+8, i).getContents();
						myLoTaxBand     = mySheet.getCell(myCol+9, i).getContents();
						myBasicTaxBand  = mySheet.getCell(myCol+10, i).getContents();
						myLoTaxRate     = mySheet.getCell(myCol+11, i).getContents();
						myBasicTaxRate  = mySheet.getCell(myCol+12, i).getContents();
						myHiTaxRate     = mySheet.getCell(myCol+13, i).getContents();
						myIntTaxRate    = mySheet.getCell(myCol+14, i).getContents();
						myDivTaxRate    = mySheet.getCell(myCol+15, i).getContents();
						myHiDivTaxRate  = mySheet.getCell(myCol+16, i).getContents();

						/* Handle AddAllowLimit which may be missing */
						myCell 		 	= mySheet.getCell(myCol+17, i);
						myAddAllowLimit = null;
						if (myCell.getType() != CellType.EMPTY) {
							myAddAllowLimit = myCell.getContents();
						}
						
						/* Handle AddIncBound which may be missing */
						myCell 		  = mySheet.getCell(myCol+18, i);
						myAddIncBound = null;
						if (myCell.getType() != CellType.EMPTY) {
							myAddIncBound = myCell.getContents();
						}
						
						/* Handle AddTaxRate which may be missing */
						myCell 		 = mySheet.getCell(myCol+19, i);
						myAddTaxRate = null;
						if (myCell.getType() != CellType.EMPTY) {
							myAddTaxRate    = myCell.getContents();
						}
						
						/* Handle AddDivTaxRate which may be missing */
						myCell    		= mySheet.getCell(myCol+20, i);
						myAddDivTaxRate = null;
						if (myCell.getType() != CellType.EMPTY) {
							myAddDivTaxRate = myCell.getContents();
						}
						
						/* Handle CapTaxRate which may be missing */
						myCell 		 = mySheet.getCell(myCol+21, i);
						myCapTaxRate = null;
						if (myCell.getType() != CellType.EMPTY) {
							myCapTaxRate    = myCell.getContents();
						}
						
						/* Handle HiCapTaxRate which may be missing */
						myCell   	   = mySheet.getCell(myCol+22, i);
						myHiCapTaxRate = null;
						if (myCell.getType() != CellType.EMPTY) {
							myHiCapTaxRate = myCell.getContents();
						}
						
						theBuilder.addTaxYear(myID,
											  myRegimeID,
			        		              	  myYear,
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
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load TaxYears",
										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
		
		/* Access the Events */
		private boolean loadEvents() throws finObject.Exception {
			/* Local variables */
			Range[]   myRange;
			Sheet     mySheet;
			Cell      myTop;
			Cell      myBottom;
			String    myDesc;
			String    myAmount;
			long	  myDebit;
			long      myCredit; 
			String    myUnits;
			String    myTaxCredit;
			long      myTranType;
			long      myID;
			Cell      myCell;
			DateCell  myDateCell;
			Date      myDate;
			int		  myYears;
			int       myCol;
			int       myTotal;
			int       myCount = 0;
			
			/* Protect against exceptions */
			try {
				/* Find the range of cells */
				myRange = theWorkbook.findByName(Events);
			
				/* Declare the new stage */
				if (!theThread.setNewStage(Events)) return false;
			
				/* If we found the range OK */
				if ((myRange != null) && (myRange.length == 1)) {
				
					/* Access the relevant sheet and Cell references */
					mySheet   = theWorkbook.getSheet(myRange[0].getFirstSheetIndex());
					myTop     = myRange[0].getTopLeft();
					myBottom  = myRange[0].getBottomRight();
					myCol     = myTop.getColumn();
				
					/* Count the number of Events */
					myTotal  = myBottom.getRow() - myTop.getRow() + 1;
				
					/* Declare the number of steps */
					if (!theThread.setNumSteps(myTotal)) return false;
				
					/* Loop through the rows of the table */
					for (int i = myTop.getRow();
						 i <= myBottom.getRow();
						 i++) {
					
						/* Access IDs */
						myCell    	= mySheet.getCell(myCol, i);
						myID      	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+3, i);
						myDebit    	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+4, i);
						myCredit   	= Long.parseLong(myCell.getContents());
						myCell    	= mySheet.getCell(myCol+6, i);
						myTranType 	= Long.parseLong(myCell.getContents());
					
						/* Access date */
						myDateCell = (DateCell)mySheet.getCell(myCol+1, i);
						myDate     = myDateCell.getDate();
				    
						/* Access the values */
						myDesc         = mySheet.getCell(myCol+2, i).getContents();
						myAmount       = mySheet.getCell(myCol+5, i).getContents();
				    
						/* Handle Units which may be missing */
						myCell    = mySheet.getCell(myCol+7, i);
						myUnits   = null;
						if (myCell.getType() != CellType.EMPTY) {
							myUnits = myCell.getContents();
						}

						/* Handle TaxCredit which may be missing */
						myCell      = mySheet.getCell(myCol+8, i);
						myTaxCredit = null;
						if (myCell.getType() != CellType.EMPTY) {
							myTaxCredit = myCell.getContents();
						}

						/* Handle Years which may be missing */
						myCell    = mySheet.getCell(myCol+9, i);
						myYears   = 0;
						if (myCell.getType() != CellType.EMPTY) {
							myYears = Integer.parseInt(myCell.getContents());
						}

						/* Add the event */
						theBuilder.addEvent(myID,
							            	myDate,
							            	myDesc,
							            	myAmount,
							            	myDebit,
							            	myCredit,
							            	myUnits,
							            	myTranType,
							            	myTaxCredit,
							            	myYears);
					
						/* Report the progress */
						myCount++;
						if ((myCount % theThread.getReportingSteps()) == 0) 
							if (!theThread.setStepsDone(myCount)) return false;
					}
				}
			}
			
			catch (Exception e) {
				throw new finObject.Exception(ExceptionClass.EXCEL, 
										  	  "Failed to load Events",										  	  e);
			}
			
			/* Return to caller */
			return true;
		}
	}	
}
