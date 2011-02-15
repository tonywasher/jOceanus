package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableRate extends DatabaseTable<AcctRate> {
	/**
	 * The name of the Rates table
	 */
	private final static String theTabName 		= "Rates";
				
	/**
	 * The name of the Account column
	 */
	private final static String theActCol    	= "Account";

	/**
	 * The name of the Rate column
	 */
	private final static String theRateCol 		= "Rate";

	/**
	 * The name of the Bonus column
	 */
	private final static String theBonusCol   	= "Bonus";

	/**
	 * The name of the EndDate column
	 */
	private final static String theEndDatCol 	= "EndDate";

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableRate(Database	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected AcctRate.List  getLoadList(DataSet pData) {
		return pData.getRates();
	}
	
	/* Get the List for the table for updates */
	protected AcctRate.List  getUpdateList(DataSet pData) {
		return new AcctRate.List(pData.getRates(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Account of the newly loaded item
	 * @return the Account
	 */
	private int getAccount() throws SQLException {
		return getInteger();
	}

	/**
	 * Determine the Rate of the newly loaded item
	 * @return the Rate
	 */
	private String getRate() throws SQLException {
		return getString();
	}

	/**
	 * Determine the Bonus of the newly loaded item
	 * @return the Bonus
	 */
	private String getBonus() throws SQLException {
		return getString();
	}

	/**
	 * Determine the EndDate of the newly loaded item
	 * @return the EndDate
	 */
	private java.util.Date getEndDate() throws SQLException {
		return getDate();
	}

	/**
	 * Set the Account of the item to be inserted
	 * @param pAccount the account of the item
	 */
	private void setAccount(int pAccount) throws SQLException {
		setInteger(pAccount);
	}

	/**
	 * Set the Rate of the item to be inserted
	 * @param pRate the Rate of the item
	 */
	private void setRate(Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the Bonus of the item to be inserted
	 * @param pBonus the bonus of the item
	 */
	private void setBonus(Rate pBonus) throws SQLException {
		setString((pBonus == null) ? null 
                				   : pBonus.format(false));
	}

	/**
	 * Set the EndDate of the item to be inserted
	 * @param pEndDate the endDate of the item
	 */
	private void setEndDate(Date pEndDate) throws SQLException {
		setDate(pEndDate);
	}

	/**
	 * Update the Account of the item
	 * @param pValue the new account
	 */
	private void updateAccount(int pValue) {
		updateInteger(theActCol, pValue);
	}		

	/**
	 * Update the Rate of the item
	 * @param pValue the new rate
	 */
	private void updateRate(Rate pValue) {
		updateString(theRateCol, pValue.format(false));
	}		

	/**
	 * Update the Bonus of the item
	 * @param pValue the new bonus
	 */
	private void updateBonus(Rate pValue) {
		updateString(theBonusCol, (pValue == null) ? null
                								   : pValue.format(false));
	}	

	/**
	 * Update the EndDate of the item
	 * @param pValue the new endDate
	 */
	private void updateEndDate(Date pValue) {
		updateDate(theEndDatCol, pValue);
	}

	/* Create statement for Rates */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theActCol	+ " int NOT NULL " +
			   		"REFERENCES " + TableAccount.idReference() + ", " +
   			   theRateCol	+ " decimal(4,2) NOT NULL, " +
			   theBonusCol	+ " decimal(4,2) NULL, " +
			   theEndDatCol	+ " date NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Rates */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theActCol + "," + 
		                 theRateCol + "," + theBonusCol + "," +
		                 theEndDatCol +
		                 " from " + getTableName();			
	}
	
	/* Load the rate */
	protected void loadItem() throws Exception {
		AcctRate.List		myList;
		int	    		myId;
		int	  			myAccountId;
		String			myRate;
		String 			myBonus;
		java.util.Date  myEndDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getID();
			myAccountId = getAccount();
			myRate 		= getRate();
			myBonus     = getBonus();
			myEndDate  	= getEndDate();
	
			/* Access the list */
			myList = (AcctRate.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, 
				           myAccountId, 
				           myRate,
				           myEndDate, 
				           myBonus);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Rates */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theActCol + "," +
		              theRateCol + "," + theBonusCol + "," +
		              theEndDatCol + ")" + 
		       " VALUES(?,?,?,?,?)";
	}
	
	/* Insert the rate */
	protected void insertItem(AcctRate	pItem) throws Exception {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setAccount(pItem.getAccount().getId());
			setRate(pItem.getRate());
			setBonus(pItem.getBonus());
			setEndDate(pItem.getEndDate());
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
	
	/* Update the rate */
	protected void updateItem(AcctRate	pItem) throws Exception {
		AcctRate.Values myBase;
		
		/* Access the base */
		myBase = (AcctRate.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Account.differs(pItem.getAccount(),
		  	  		  		  	myBase.getAccount()))
				updateAccount(pItem.getAccount().getId());
			if (Rate.differs(pItem.getRate(),
				  		  	 myBase.getRate()))
				updateRate(pItem.getRate());
			if (Rate.differs(pItem.getBonus(),
					  		 myBase.getBonus())) 
				updateBonus(pItem.getBonus());
			if (Date.differs(pItem.getEndDate(),
				             myBase.getEndDate())) 
				updateEndDate(pItem.getEndDate());
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
