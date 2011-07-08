package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTaxYear extends DatabaseTable<TaxYear> {
	/**
	 * The name of the TaxYears table
	 */
	private final static String theTabName 		= TaxYear.listName;
				
	/**
	 * The name of the Year column
	 */
	private final static String theYearCol   	= TaxYear.fieldName(TaxYear.FIELD_YEAR);

	/**
	 * The name of the Allowance column
	 */
	private final static String theAllwCol   	= TaxYear.fieldName(TaxYear.FIELD_ALLOW);

	/**
	 * The name of the Rental Allowance column
	 */
	private final static String theRentCol   	= TaxYear.fieldName(TaxYear.FIELD_RENTAL);

	/**
	 * The name of the LoTaxBand column
	 */
	private final static String theLoBdCol   	= TaxYear.fieldName(TaxYear.FIELD_LOBAND);

	/**
	 * The name of the BasicTaxBand column
	 */
	private final static String theBsBdCol   	= TaxYear.fieldName(TaxYear.FIELD_BSBAND);

	/**
	 * The name of the LoAgeAllow column
	 */
	private final static String theLoAACol   	= TaxYear.fieldName(TaxYear.FIELD_LOAGAL);

	/**
	 * The name of the HiAgeAllow column
	 */
	private final static String theHiAACol   	= TaxYear.fieldName(TaxYear.FIELD_HIAGAL);

	/**
	 * The name of the HiAgeAllow column
	 */
	private final static String theCpAlCol   	= TaxYear.fieldName(TaxYear.FIELD_CAPALW);

	/**
	 * The name of the AgeAllowLimit column
	 */
	private final static String theAgLmCol   	= TaxYear.fieldName(TaxYear.FIELD_AGELMT);

	/**
	 * The name of the AddAllowLimit column
	 */
	private final static String theAdLmCol   	= TaxYear.fieldName(TaxYear.FIELD_ADDLMT);

	/**
	 * The name of the AddIncBoundary column
	 */
	private final static String theAdBdCol   	= TaxYear.fieldName(TaxYear.FIELD_ADDBDY);

	/**
	 * The name of the LoTaxRate column
	 */
	private final static String theLoTxCol   	= TaxYear.fieldName(TaxYear.FIELD_LOTAX);

	/**
	 * The name of the BasicTaxRate column
	 */
	private final static String theBsTxCol   	= TaxYear.fieldName(TaxYear.FIELD_BASTAX);

	/**
	 * The name of the HiTaxRate column
	 */
	private final static String theHiTxCol   	= TaxYear.fieldName(TaxYear.FIELD_HITAX);

	/**
	 * The name of the IntTaxRate column
	 */
	private final static String theInTxCol   	= TaxYear.fieldName(TaxYear.FIELD_INTTAX);

	/**
	 * The name of the DivTaxRate column
	 */
	private final static String theDvTxCol   	= TaxYear.fieldName(TaxYear.FIELD_DIVTAX);

	/**
	 * The name of the HiDivTaxRate column
	 */
	private final static String theHDTxCol   	= TaxYear.fieldName(TaxYear.FIELD_HDVTAX);

	/**
	 * The name of the AddTaxRate column
	 */
	private final static String theAdTxCol   	= TaxYear.fieldName(TaxYear.FIELD_ADDTAX);

	/**
	 * The name of the AddDivTaxRate column
	 */
	private final static String theADTxCol   	= TaxYear.fieldName(TaxYear.FIELD_ADVTAX);

	/**
	 * The name of the CapTaxRate column
	 */
	private final static String theCpTxCol   	= TaxYear.fieldName(TaxYear.FIELD_CAPTAX);

	/**
	 * The name of the AddDivTaxRate column
	 */
	private final static String theHCTxCol   	= TaxYear.fieldName(TaxYear.FIELD_HCPTAX);

	/**
	 * The name of the TaxRegime column
	 */
	private final static String theTxRgCol   	= TaxYear.fieldName(TaxYear.FIELD_REGIME);

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxYear(Database	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected TaxYear.List  getLoadList(DataSet pData) {
		return pData.getTaxYears();
	}
	
	/* Get the List for the table for updates */
	protected TaxYear.List  getUpdateList(DataSet pData) {
		return new TaxYear.List(pData.getTaxYears(), ListStyle.UPDATE);
	}
	/* Create statement for TaxYears */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theYearCol	+ " date NOT NULL, " +
			   theTxRgCol	+ " int NOT NULL " + 
		   			"REFERENCES " + TableTaxRegime.idReference() + ", " +
		   	   theAllwCol	+ " money NOT NULL, " +
			   theRentCol	+ " money NOT NULL, " +
			   theLoBdCol	+ " money NOT NULL, " +
			   theBsBdCol	+ " money NOT NULL, " +
			   theLoAACol	+ " money NOT NULL, " +
			   theHiAACol	+ " money NOT NULL, " +
			   theAgLmCol	+ " money NOT NULL, " +
			   theAdLmCol	+ " money NULL, " +
			   theLoTxCol	+ " decimal(4,2) NOT NULL," +
			   theBsTxCol	+ " decimal(4,2) NOT NULL," +
			   theHiTxCol	+ " decimal(4,2) NOT NULL," +
			   theInTxCol	+ " decimal(4,2) NOT NULL," +
			   theDvTxCol	+ " decimal(4,2) NOT NULL," +
			   theHDTxCol	+ " decimal(4,2) NOT NULL," +
			   theAdTxCol	+ " decimal(4,2) NULL," +
			   theADTxCol	+ " decimal(4,2) NULL," +
			   theAdBdCol	+ " money NULL," +
			   theCpAlCol	+ " money NOT NULL," +
			   theCpTxCol	+ " decimal(4,2) NULL," +
			   theHCTxCol	+ " decimal(4,2) NULL)";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for TaxYears */
	protected String loadStatement() {
		return "select " +  theIdCol   + "," + theYearCol + "," + 
							theTxRgCol + "," +
		 					theAllwCol + "," + theRentCol + "," +
		 					theLoBdCol + "," + theBsBdCol + "," +
		 					theLoAACol + "," + theHiAACol + "," + 
		 					theAgLmCol + "," + theAdLmCol + "," + 
		 					theLoTxCol + "," + theBsTxCol + "," + 
		 					theHiTxCol + "," + theInTxCol + "," +
		 					theDvTxCol + "," + theHDTxCol + "," +
		 					theAdTxCol + "," + theADTxCol + "," +
		 					theAdBdCol + "," + theCpAlCol + "," +
		 					theCpTxCol + "," + theHCTxCol + 
		 					" from " + getTableName() +			
		 					" order by " + theYearCol;
	}
	
	/* Load the tax year */
	public void loadItem() throws Exception {
		TaxYear.List	myList;
		int	    		myId;
		java.util.Date  myYear;
		String  		myAllowance;
		String  		myRentalAllow;
		String  		myLoBand;
		String  		myBasicBand;
		String  		myLoAgeAllow;
		String  		myHiAgeAllow;
		String  		myCapitalAllow;
		String  		myAgeAllowLimit;
		String  		myAddAllowLimit;
		String  		myAddIncBound;
		String  		myLoTaxRate;
		String  		myBasicTaxRate;
		String  		myHiTaxRate;
		String  		myIntTaxRate;
		String  		myDivTaxRate;
		String  		myHiDivTaxRate;
		String  		myAddTaxRate;
		String  		myAddDivTaxRate;
		String  		myCapTaxRate;
		String  		myHiCapTaxRate;
		int		  		myRegime;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        	= getInteger();
			myYear          = getDate();
			myRegime        = getInteger();
			myAllowance     = getString();
			myRentalAllow   = getString();
			myLoBand        = getString();
			myBasicBand     = getString();
			myLoAgeAllow    = getString();
			myHiAgeAllow    = getString();
			myAgeAllowLimit = getString();
			myAddAllowLimit = getString();
			myLoTaxRate     = getString();
			myBasicTaxRate  = getString();
			myHiTaxRate     = getString();
			myIntTaxRate    = getString();
			myDivTaxRate    = getString();
			myHiDivTaxRate  = getString();
			myAddTaxRate    = getString();
			myAddDivTaxRate = getString();
			myAddIncBound 	= getString();
			myCapitalAllow  = getString();
			myCapTaxRate    = getString();
			myHiCapTaxRate  = getString();
	
			/* Access the list */
			myList = (TaxYear.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, myRegime, myYear, 
		              	   myAllowance, myRentalAllow,
		              	   myLoAgeAllow, myHiAgeAllow,
		              	   myCapitalAllow,
		              	   myAgeAllowLimit, myAddAllowLimit,
		              	   myLoBand, myBasicBand,
		              	   myAddIncBound,
		              	   myLoTaxRate, myBasicTaxRate, 
		              	   myHiTaxRate, myIntTaxRate,
		              	   myDivTaxRate, myHiDivTaxRate,
		              	   myAddTaxRate, myAddDivTaxRate,
		              	   myCapTaxRate, myHiCapTaxRate);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for TaxYears */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
        	   " (" + theIdCol   + "," + theYearCol + "," +
 	   		  		  theTxRgCol + "," +
        	   		  theAllwCol + "," + theRentCol + "," +
        	   		  theLoBdCol + "," + theBsBdCol + "," +
        	   		  theLoAACol + "," + theHiAACol + "," + 
        	   		  theAgLmCol + "," + theAdLmCol + "," + 
        	   		  theLoTxCol + "," + theBsTxCol + "," + 
        	   		  theHiTxCol + "," + theInTxCol + "," +
        	   		  theDvTxCol + "," + theHDTxCol + "," +
        	   		  theAdTxCol + "," + theADTxCol + "," +
        	   		  theAdBdCol + "," + theCpAlCol + "," +
        	   		  theCpTxCol + "," + theHCTxCol + ")" +
        	   " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	}
	
	/* Insert the tax year */
	protected void insertItem(TaxYear			pItem) throws Exception {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setInteger(pItem.getId());
			setDate(pItem.getDate());
			setInteger(pItem.getTaxRegime().getId());
			setNumber(pItem.getAllowance());
			setNumber(pItem.getRentalAllowance());
			setNumber(pItem.getLoBand());
			setNumber(pItem.getBasicBand());
			setNumber(pItem.getLoAgeAllow());
			setNumber(pItem.getHiAgeAllow());
			setNumber(pItem.getAgeAllowLimit());
			setNumber(pItem.getAddAllowLimit());
			setNumber(pItem.getLoTaxRate());
			setNumber(pItem.getBasicTaxRate());
			setNumber(pItem.getHiTaxRate());
			setNumber(pItem.getIntTaxRate());
			setNumber(pItem.getDivTaxRate());
			setNumber(pItem.getHiDivTaxRate());
			setNumber(pItem.getAddTaxRate());
			setNumber(pItem.getAddDivTaxRate());
			setNumber(pItem.getAddIncBound());
			setNumber(pItem.getCapitalAllow());
			setNumber(pItem.getCapTaxRate());
			setNumber(pItem.getHiCapTaxRate());
		}
				
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to insert item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Update the tax year */
	protected void updateItem(TaxYear			pItem) throws Exception {
		TaxYear.Values 	myBase;
		
		/* Access the base */
		myBase = (TaxYear.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Date.differs(pItem.getDate(), 
							 myBase.getYear()))
				updateDate(theYearCol, pItem.getDate());
			if (TaxRegime.differs(pItem.getTaxRegime(),
					  			  myBase.getTaxRegime()))
				updateInteger(theTxRgCol, pItem.getTaxRegime().getId());
			if (Money.differs(pItem.getAllowance(),
		  		  			  myBase.getAllowance()))
				updateNumber(theAllwCol, pItem.getAllowance());
			if (Money.differs(pItem.getRentalAllowance(),
							  myBase.getRentalAllow()))
				updateNumber(theRentCol, pItem.getRentalAllowance());
			if (Money.differs(pItem.getLoBand(),
		  		  			  myBase.getLoBand()))
				updateNumber(theLoBdCol, pItem.getLoBand());
			if (Money.differs(pItem.getBasicBand(),
		  		  			  myBase.getBasicBand()))
				updateNumber(theBsBdCol, pItem.getBasicBand());
			if (Money.differs(pItem.getLoAgeAllow(),
		  		 			  myBase.getLoAgeAllow()))
				updateNumber(theLoAACol, pItem.getLoAgeAllow());
			if (Money.differs(pItem.getHiAgeAllow(),
		  		  			  myBase.getHiAgeAllow()))
				updateNumber(theHiAACol, pItem.getHiAgeAllow());
			if (Money.differs(pItem.getCapitalAllow(),
							  myBase.getCapitalAllow()))
				updateNumber(theCpAlCol, pItem.getCapitalAllow());
			if (Money.differs(pItem.getAgeAllowLimit(),
		  		  			  myBase.getAgeAllowLimit()))
				updateNumber(theAgLmCol, pItem.getAgeAllowLimit());
			if (Money.differs(pItem.getAddAllowLimit(),
		  		  			  myBase.getAddAllowLimit()))
				updateNumber(theAdLmCol, pItem.getAddAllowLimit());
			if (Money.differs(pItem.getAddIncBound(),
		  		  		      myBase.getAddIncBound()))
				updateNumber(theAdBdCol, pItem.getAddIncBound());
			if (Rate.differs(pItem.getLoTaxRate(),
		  		  			 myBase.getLoTaxRate()))
				updateNumber(theLoTxCol, pItem.getLoTaxRate());
			if (Rate.differs(pItem.getBasicTaxRate(),
		  		  			 myBase.getBasicTaxRate()))
				updateNumber(theBsTxCol, pItem.getBasicTaxRate());
			if (Rate.differs(pItem.getHiTaxRate(),
		  		  			 myBase.getHiTaxRate()))
				updateNumber(theHiTxCol, pItem.getHiTaxRate());
			if (Rate.differs(pItem.getIntTaxRate(),
		  		  			 myBase.getIntTaxRate()))
				updateNumber(theInTxCol, pItem.getIntTaxRate());
			if (Rate.differs(pItem.getDivTaxRate(),
		  		  			 myBase.getDivTaxRate()))
				updateNumber(theDvTxCol, pItem.getDivTaxRate());
			if (Rate.differs(pItem.getHiDivTaxRate(),
		  		  			 myBase.getHiDivTaxRate()))
				updateNumber(theHDTxCol, pItem.getHiDivTaxRate());
			if (Rate.differs(pItem.getAddTaxRate(),
		  		  			 myBase.getAddTaxRate()))
				updateNumber(theAdTxCol, pItem.getAddTaxRate());
			if (Rate.differs(pItem.getAddDivTaxRate(),
		  		  			 myBase.getAddDivTaxRate()))
				updateNumber(theADTxCol, pItem.getAddDivTaxRate());
			if (Rate.differs(pItem.getCapTaxRate(),
		  		  			 myBase.getCapTaxRate()))
				updateNumber(theCpTxCol, pItem.getCapTaxRate());
			if (Rate.differs(pItem.getHiCapTaxRate(),
		  		  			 myBase.getHiCapTaxRate()))
				updateNumber(theHCTxCol, pItem.getHiCapTaxRate());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to update item",
					            e);
		}
			
		/* Return to caller */
		return;
	}
}
