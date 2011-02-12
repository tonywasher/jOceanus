package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTaxYear extends DatabaseTable<TaxYear> {
	/**
	 * The name of the TaxYears table
	 */
	private final static String theTabName 		= "TaxYears";
				
	/**
	 * The name of the Year column
	 */
	private final static String theYearCol   	= "Year";

	/**
	 * The name of the Allowance column
	 */
	private final static String theAllwCol   	= "PersonalAllowance";

	/**
	 * The name of the Rental Allowance column
	 */
	private final static String theRentCol   	= "RentalAllowance";

	/**
	 * The name of the LoTaxBand column
	 */
	private final static String theLoBdCol   	= "LoTaxBand";

	/**
	 * The name of the BasicTaxBand column
	 */
	private final static String theBsBdCol   	= "BasicTaxBand";

	/**
	 * The name of the LoAgeAllow column
	 */
	private final static String theLoAACol   	= "LoAgeAllowance";

	/**
	 * The name of the HiAgeAllow column
	 */
	private final static String theHiAACol   	= "HiAgeAllowance";

	/**
	 * The name of the HiAgeAllow column
	 */
	private final static String theCpAlCol   	= "CapitalAllowance";

	/**
	 * The name of the AgeAllowLimit column
	 */
	private final static String theAgLmCol   	= "AgeAllowanceLimit";

	/**
	 * The name of the AddAllowLimit column
	 */
	private final static String theAdLmCol   	= "AddAllowanceLimit";

	/**
	 * The name of the AddIncBoundary column
	 */
	private final static String theAdBdCol   	= "AddIncomeBoundary";

	/**
	 * The name of the LoTaxRate column
	 */
	private final static String theLoTxCol   	= "LoTaxRate";

	/**
	 * The name of the BasicTaxRate column
	 */
	private final static String theBsTxCol   	= "BasicTaxRate";

	/**
	 * The name of the HiTaxRate column
	 */
	private final static String theHiTxCol   	= "HiTaxRate";

	/**
	 * The name of the IntTaxRate column
	 */
	private final static String theInTxCol   	= "IntTaxRate";

	/**
	 * The name of the DivTaxRate column
	 */
	private final static String theDvTxCol   	= "DivTaxRate";

	/**
	 * The name of the HiDivTaxRate column
	 */
	private final static String theHDTxCol   	= "HiDivTaxRate";

	/**
	 * The name of the AddTaxRate column
	 */
	private final static String theAdTxCol   	= "AddTaxRate";

	/**
	 * The name of the AddDivTaxRate column
	 */
	private final static String theADTxCol   	= "AddDivTaxRate";

	/**
	 * The name of the CapTaxRate column
	 */
	private final static String theCpTxCol   	= "CapTaxRate";

	/**
	 * The name of the AddDivTaxRate column
	 */
	private final static String theHCTxCol   	= "HiCapTaxRate";

	/**
	 * The name of the TaxRegime column
	 */
	private final static String theTxRgCol   	= "TaxRegime";

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
	
	/**
	 * Determine the Year of the newly loaded item
	 * @return the Year
	 */
	private java.util.Date getYear() throws SQLException {
		return getDate();
	}		

	/**
	 * Determine the TaxRegime of the newly loaded item
	 * @return the TaxRegime
	 */
	private int getTaxRegime() throws SQLException {
		return getInteger();
	}

	/**
	 * Determine the Allowance of the newly loaded item
	 * @return the Allowance
	 */
	private String getAllowance() throws SQLException {
		return getString();
	}

	/**
	 * Determine the RentalAllowance of the newly loaded item
	 * @return the Rental Allowance
	 */
	private String getRentalAllow() throws SQLException {
		return getString();
	}

	/**
	 * Determine the LoTaxBand of the newly loaded item
	 * @return the LoTaxBand
	 */
	private String getLoTaxBand() throws SQLException {
		return getString();
	}	

	/**
	 * Determine the BasicTaxBand of the newly loaded item
	 * @return the BasicTaxBand
	 */
	private String getBasicTaxBand() throws SQLException {
		return getString();
	}

	/**
	 * Determine the LoAgeAllow of the newly loaded item
	 * @return the LoAgeAllow
	 */
	private String getLoAgeAllow() throws SQLException {
		return getString();
	}

	/**
	 * Determine the HiAgeAllow of the newly loaded item
	 * @return the HiAgeAllow
	 */
	private String getHiAgeAllow() throws SQLException {
		return getString();
	}

	/**
	 * Determine the AgeAllowLimit of the newly loaded item
	 * @return the AgeAllowLimit
	 */
	private String getAgeAllowLimit() throws SQLException {
		return getString();
	}

	/**
	 * Determine the AddAllowLimit of the newly loaded item
	 * @return the AddAllowLimit
	 */
	private String getAddAllowLimit() throws SQLException {
		return getString();
	}

	/**
	 * Determine the LoTaxRate of the newly loaded item
	 * @return the LoTaxRate
	 */
	private String getLoTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the BasicTaxRate of the newly loaded item
	 * @return the BasicTaxRate
	 */
	private String getBasicTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the HiTaxRate of the newly loaded item
	 * @return the HiLoTaxRate
	 */
	private String getHiTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the IntTaxRate of the newly loaded item
	 * @return the IntTaxRate
	 */
	private String getIntTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the DivTaxRate of the newly loaded item
	 * @return the DivTaxRate
	 */
	private String getDivTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the HiDivTaxRate of the newly loaded item
	 * @return the HiDivTaxRate
	 */
	private String getHiDivTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the AddTaxRate of the newly loaded item
	 * @return the AddTaxRate
	 */
	private String getAddTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the AddDivTaxRate of the newly loaded item
	 * @return the AddDivTaxRate
	 */
	private String getAddDivTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the AddIncBoundary of the newly loaded item
	 * @return the AddIncBoundary
	 */
	private String getAddIncBoundary() throws SQLException {
		return getString();
	}

	/**
	 * Determine the CapitalAllowance of the newly loaded item
	 * @return the CapAllowance
	 */
	private String getCapitalAllow() throws SQLException {
		return getString();
	}

	/**
	 * Determine the CapitalTAxRate of the newly loaded item
	 * @return the CapitalTaxRate
	 */
	private String getCapTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the HiCapitalTaxRate of the newly loaded item
	 * @return the HiCapTaxRate
	 */
	private String getHiCapTaxRate() throws SQLException {
		return getString();
	}

	/**
	 * Set the Year of the item to be inserted
	 * @param pYear the Year of the item
	 */
	private void setYear(uk.co.tolcroft.models.Date pYear) throws SQLException {
		setDate(pYear);
	}

	/**
	 * Set the TaxRegime of the item to be inserted
	 * @param id the id of the TaxRegime for the item
	 */
	private void setTaxRegime(int id) throws SQLException {
		setInteger(id);
	}
	
	/**
	 * Set the Allowance of the item to be inserted
	 * @param pAllow the Allowance of the item
	 */
	private void setAllowance(Number.Money pAllow) throws SQLException {
		setString(pAllow.format(false));
	}

	/**
	 * Set the Rental Allowance of the item to be inserted
	 * @param pAllow the RentalAllowance of the item
	 */
	private void setRentalAllow(Number.Money pAllow) throws SQLException {
		setString(pAllow.format(false));
	}

	/**
	 * Set the LoTaxBand of the item to be inserted
	 * @param pBand the LoTaxBand of the item
	 */
	private void setLoTaxBand(Number.Money pBand) throws SQLException {
		setString(pBand.format(false));
	}

	/**
	 * Set the BasicTaxBand of the item to be inserted
	 * @param pBand the BasicTaxBand of the item
	 */
	private void setBasicTaxBand(Number.Money pBand) throws SQLException {
		setString(pBand.format(false));
	}

	/**
	 * Set the LoAgeAllow of the item to be inserted
	 * @param pBand the loAgeAllow of the item
	 */
	private void setLoAgeAllow(Number.Money pBand) throws SQLException {
		setString(pBand.format(false));
	}

	/**
	 * Set the HiAgeAllow of the item to be inserted
	 * @param pBand the hiAgeAllow of the item
	 */
	private void setHiAgeAllow(Number.Money pBand) throws SQLException {
		setString(pBand.format(false));
	}

	/**
	 * Set the AgeAllowLimit of the item to be inserted
	 * @param pBand the ageAllowLimit of the item
	 */
	private void setAgeAllowLimit(Number.Money pBand) throws SQLException {
		setString(pBand.format(false));
	}

	/**
	 * Set the AddAllowLimit of the item to be inserted
	 * @param pBand the addAllowLimit of the item
	 */
	private void setAddAllowLimit(Number.Money pBand) throws SQLException {
		setString((pBand == null) ? null : pBand.format(false));
	}

	/**
	 * Set the LoTaxRate of the item to be inserted
	 * @param pRate the LoTaxRate of the item
	 */
	private void setLoTaxRate(Number.Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the BasicTaxRate of the item to be inserted
	 * @param pRate the BasicTaxRate of the item
	 */
	private void setBasicTaxRate(Number.Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the HiTaxRate of the item to be inserted
	 * @param pRate the HiTaxRate of the item
	 */
	private void setHiTaxRate(Number.Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the IntTaxRate of the item to be inserted
	 * @param pRate the IntTaxRate of the item
	 */
	private void setIntTaxRate(Number.Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the DivTaxRate of the item to be inserted
	 * @param pRate the DivTaxRate of the item
	 */
	private void setDivTaxRate(Number.Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the HiDivTaxRate of the item to be inserted
	 * @param pRate the HiDivTaxRate of the item
	 */
	private void setHiDivTaxRate(Number.Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the AddTaxRate of the item to be inserted
	 * @param pRate the AddTaxRate of the item
=	 */
	private void setAddTaxRate(Number.Rate pRate) throws SQLException {
		setString((pRate == null) ? null : pRate.format(false));
	}

	/**
	 * Set the AddDivTaxRate of the item to be inserted
	 * @param pRate the AddDivTaxRate of the item
	 */
	private void setAddDivTaxRate(Number.Rate pRate) throws SQLException {
		setString((pRate == null) ? null : pRate.format(false));
	}

	/**
	 * Set the AddIncBoundary of the item to be inserted
	 * @param pBand the AddIncBoundary of the item
	 */
	private void setAddIncBoundary(Number.Money pBand) throws SQLException {
		setString((pBand == null) ? null : pBand.format(false));
	}

	/**
	 * Set the CapitalAllowance of the item to be inserted
	 * @param pBand the CapitalAllowance of the item
	 */
	private void setCapitalAllow(Number.Money pBand) throws SQLException {
		setString(pBand.format(false));
	}

	/**
	 * Set the CapitalTaxRate of the item to be inserted
	 * @param pRate the CapTaxRate of the item
	 */
	private void setCapTaxRate(Number.Rate pRate) throws SQLException {
		setString((pRate == null) ? null : pRate.format(false));
	}

	/**
	 * Set the HiCapitalTaxRate of the item to be inserted
	 * @param pRate the HiCapTaxRate of the item
	 */
	private void setHiCapTaxRate(Number.Rate pRate) throws SQLException {
		setString((pRate == null) ? null : pRate.format(false));
	}

	/**
	 * Update the Year of the item
	 * @param pValue the new date
	 */
	private void updateYear(Date pValue) {
		updateDate(theYearCol, pValue);
	}		

	/**
	 * Update the TaxRegime of the item
	 * @param uId the id of the tax regime
	 */
	private void updateTaxRegime(int uId) {
		updateInteger(theTxRgCol, uId);
	}
	
	/**
	 * Update the allowance of the item
	 * @param pValue the new allowance
	 */
	private void updateAllowance(Number.Money pValue) {
		updateString(theAllwCol, pValue.format(false));
	}

	/**
	 * Update the rental allowance of the item
	 * @param pValue the new rental allowance
	 */
	private void updateRentalAllow(Number.Money pValue) {
		updateString(theRentCol, pValue.format(false));
	}

	/**
	 * Update the LoTaxBand of the item
	 * @param pValue the new Lo tax band
	 */
	private void updateLoTaxBand(Number.Money pValue) {
		updateString(theLoBdCol, pValue.format(false));
	}	

	/**
	 * Update the BasicTaxBand of the item
	 * @param pValue the new Basic tax band
	 */
	private void updateBasicTaxBand(Number.Money pValue) {
		updateString(theBsBdCol, pValue.format(false));
	}

	/**
	 * Update the LoAgeAllow of the item
	 * @param pValue the new Lo Age allowance
	 */
	private void updateLoAgeAllow(Number.Money pValue) {
		updateString(theLoAACol, pValue.format(false));
	}

	/**
	 * Update the HiAgeAllow of the item
	 * @param pValue the new Hi Age allowance
	 */
	private void updateHiAgeAllow(Number.Money pValue) {
		updateString(theHiAACol, pValue.format(false));
	}

	/**
	 * Update the AgeAllowLimit of the item
	 * @param pValue the new age allowance limit
	 */
	private void updateAgeAllowLimit(Number.Money pValue) {
		updateString(theAgLmCol, pValue.format(false));
	}

	/**
	 * Update the AddAllowLimit of the item
	 * @param pValue the new additional allowance limit
	 */
	private void updateAddAllowLimit(Number.Money pValue) {
		updateString(theAdLmCol, (pValue == null) ? null : pValue.format(false));
	}

	/**
	 * Update the LoTaxRate of the item
	 * @param pValue the new lo tax rate
	 */
	private void updateLoTaxRate(Number.Rate pValue) {
		updateString(theLoTxCol, pValue.format(false));
	}

	/**
	 * Update the BasicTaxRate of the item
	 * @param pValue the new Basic tax rate
	 */
	private void updateBasicTaxRate(Number.Rate pValue) {
		updateString(theBsTxCol, pValue.format(false));
	}

	/**
	 * Update the HiTaxRate of the item
	 * @param pValue the new high tax rate
	 */
	private void updateHiTaxRate(Number.Rate pValue) {
		updateString(theHiTxCol, pValue.format(false));
	}

	/**
	 * Update the IntTaxRate of the item
	 * @param pValue the new IntTaxRate
	 */
	private void updateIntTaxRate(Number.Rate pValue) {
		updateString(theInTxCol, pValue.format(false));
	}

	/**
	 * Update the DivTaxRate of the item
	 * @param pValue the new DivTaxRate
	 */
	private void updateDivTaxRate(Number.Rate pValue) {
		updateString(theDvTxCol, pValue.format(false));
	}

	/**
	 * Update the HiDivTaxRate of the item
	 * @param pValue the new HiDivTaxRate
	 */
	private void updateHiDivTaxRate(Number.Rate pValue) {
		updateString(theHDTxCol, pValue.format(false));
	}

	/**
	 * Update the AddTaxRate of the item
	 * @param pValue the new additional tax rate
	 */
	private void updateAddTaxRate(Number.Rate pValue) {
		updateString(theAdTxCol, (pValue == null) ? null : pValue.format(false));
	}

	/**
	 * Update the AddDivTaxRate of the item
	 * @param pValue the new additional dividend rate
	 */
	private void updateAddDivTaxRate(Number.Rate pValue) {
		updateString(theADTxCol, (pValue == null) ? null : pValue.format(false));
	}

	/**
	 * Update the AddIncBoundary of the item
	 * @param pValue the new additional income boundary
	 */
	private void updateAddIncBoundary(Number.Money pValue) {
		updateString(theADTxCol, (pValue == null) ? null : pValue.format(false));
	}

	/**
	 * Update the CapitalAllowance of the item
	 * @param pValue the new CapitalAllowance
	 */
	private void updateCapitalAllow(Number.Money pValue) {
		updateString(theCpAlCol, pValue.format(false));
	}

	/**
	 * Update the CapTaxRate of the item
	 * @param pValue the new CapTaxRate
	 */
	private void updateCapTaxRate(Number.Rate pValue) {
		updateString(theCpTxCol, (pValue == null) ? null : pValue.format(false));
	}

	/**
	 * Update the HiCapTaxRate of the item
	 * @param pValue the new HiCapTaxRate
	 */
	private void updateHiCapTaxRate(Number.Rate pValue) {
		updateString(theHCTxCol, (pValue == null) ? null : pValue.format(false));
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
			myId        	= getID();
			myYear          = getYear();
			myRegime        = getTaxRegime();
			myAllowance     = getAllowance();
			myRentalAllow   = getRentalAllow();
			myLoBand        = getLoTaxBand();
			myBasicBand     = getBasicTaxBand();
			myLoAgeAllow    = getLoAgeAllow();
			myHiAgeAllow    = getHiAgeAllow();
			myAgeAllowLimit = getAgeAllowLimit();
			myAddAllowLimit = getAddAllowLimit();
			myLoTaxRate     = getLoTaxRate();
			myBasicTaxRate  = getBasicTaxRate();
			myHiTaxRate     = getHiTaxRate();
			myIntTaxRate    = getIntTaxRate();
			myDivTaxRate    = getDivTaxRate();
			myHiDivTaxRate  = getHiDivTaxRate();
			myAddTaxRate    = getAddTaxRate();
			myAddDivTaxRate = getAddDivTaxRate();
			myAddIncBound 	= getAddIncBoundary();
			myCapitalAllow  = getCapitalAllow();
			myCapTaxRate    = getCapTaxRate();
			myHiCapTaxRate  = getHiCapTaxRate();
	
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
			setID(pItem.getId());
			setYear(pItem.getDate());
			setTaxRegime(pItem.getTaxRegime().getId());
			setAllowance(pItem.getAllowance());
			setRentalAllow(pItem.getRentalAllowance());
			setLoTaxBand(pItem.getLoBand());
			setBasicTaxBand(pItem.getBasicBand());
			setLoAgeAllow(pItem.getLoAgeAllow());
			setHiAgeAllow(pItem.getHiAgeAllow());
			setAgeAllowLimit(pItem.getAgeAllowLimit());
			setAddAllowLimit(pItem.getAddAllowLimit());
			setLoTaxRate(pItem.getLoTaxRate());
			setBasicTaxRate(pItem.getBasicTaxRate());
			setHiTaxRate(pItem.getHiTaxRate());
			setIntTaxRate(pItem.getIntTaxRate());
			setDivTaxRate(pItem.getDivTaxRate());
			setHiDivTaxRate(pItem.getHiDivTaxRate());
			setAddTaxRate(pItem.getAddTaxRate());
			setAddDivTaxRate(pItem.getAddDivTaxRate());
			setAddIncBoundary(pItem.getAddIncBound());
			setCapitalAllow(pItem.getCapitalAllow());
			setCapTaxRate(pItem.getCapTaxRate());
			setHiCapTaxRate(pItem.getHiCapTaxRate());
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
			if (Utils.differs(pItem.getDate(), 
							  myBase.getYear()))
				updateYear(pItem.getDate());
			if (Utils.differs(pItem.getTaxRegime(),
					  		  myBase.getTaxRegime()))
				updateTaxRegime(pItem.getTaxRegime().getId());
			if (Utils.differs(pItem.getAllowance(),
		  		  			  myBase.getAllowance()))
				updateAllowance(pItem.getAllowance());
			if (Utils.differs(pItem.getRentalAllowance(),
							  myBase.getRentalAllow()))
				updateRentalAllow(pItem.getRentalAllowance());
			if (Utils.differs(pItem.getLoBand(),
		  		  			  myBase.getLoBand()))
				updateLoTaxBand(pItem.getLoBand());
			if (Utils.differs(pItem.getBasicBand(),
		  		  			  myBase.getBasicBand()))
				updateBasicTaxBand(pItem.getBasicBand());
			if (Utils.differs(pItem.getLoAgeAllow(),
		  		 			  myBase.getLoAgeAllow()))
				updateLoAgeAllow(pItem.getLoAgeAllow());
			if (Utils.differs(pItem.getHiAgeAllow(),
		  		  			  myBase.getHiAgeAllow()))
				updateHiAgeAllow(pItem.getHiAgeAllow());
			if (Utils.differs(pItem.getCapitalAllow(),
							  myBase.getCapitalAllow()))
				updateCapitalAllow(pItem.getHiAgeAllow());
			if (Utils.differs(pItem.getAgeAllowLimit(),
		  		  			  myBase.getAgeAllowLimit()))
				updateAgeAllowLimit(pItem.getAgeAllowLimit());
			if (Utils.differs(pItem.getAddAllowLimit(),
		  		  			  myBase.getAddAllowLimit()))
				updateAddAllowLimit(pItem.getAddAllowLimit());
			if (Utils.differs(pItem.getAddIncBound(),
		  		  		      myBase.getAddIncBound()))
				updateAddIncBoundary(pItem.getAddIncBound());
			if (Utils.differs(pItem.getLoTaxRate(),
		  		  			  myBase.getLoTaxRate()))
				updateLoTaxRate(pItem.getLoTaxRate());
			if (Utils.differs(pItem.getBasicTaxRate(),
		  		  			  myBase.getBasicTaxRate()))
				updateBasicTaxRate(pItem.getBasicTaxRate());
			if (Utils.differs(pItem.getHiTaxRate(),
		  		  			  myBase.getHiTaxRate()))
				updateHiTaxRate(pItem.getHiTaxRate());
			if (Utils.differs(pItem.getIntTaxRate(),
		  		  			  myBase.getIntTaxRate()))
				updateIntTaxRate(pItem.getIntTaxRate());
			if (Utils.differs(pItem.getDivTaxRate(),
		  		  			  myBase.getDivTaxRate()))
				updateDivTaxRate(pItem.getDivTaxRate());
			if (Utils.differs(pItem.getHiDivTaxRate(),
		  		  			  myBase.getHiDivTaxRate()))
				updateHiDivTaxRate(pItem.getHiDivTaxRate());
			if (Utils.differs(pItem.getAddTaxRate(),
		  		  			  myBase.getAddTaxRate()))
				updateAddTaxRate(pItem.getAddTaxRate());
			if (Utils.differs(pItem.getAddDivTaxRate(),
		  		  			  myBase.getAddDivTaxRate()))
				updateAddDivTaxRate(pItem.getAddDivTaxRate());
			if (Utils.differs(pItem.getCapTaxRate(),
		  		  			  myBase.getCapTaxRate()))
				updateCapTaxRate(pItem.getCapTaxRate());
			if (Utils.differs(pItem.getHiCapTaxRate(),
		  		  			  myBase.getHiCapTaxRate()))
				updateHiCapTaxRate(pItem.getHiCapTaxRate());
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
