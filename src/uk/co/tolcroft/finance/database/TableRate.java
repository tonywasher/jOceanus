package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableRate extends DatabaseTable<Rate> {
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
	protected Rate.List  getLoadList(DataSet pData) {
		return pData.getRates();
	}
	
	/* Get the List for the table for updates */
	protected Rate.List  getUpdateList(DataSet pData) {
		return new Rate.List(pData.getRates(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Account of the newly loaded item
	 * @return the Account
	 */
	private long getAccount() throws SQLException {
		return getLong();
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
	private void setAccount(long pAccount) throws SQLException {
		setLong(pAccount);
	}

	/**
	 * Set the Rate of the item to be inserted
	 * @param pRate the Rate of the item
	 */
	private void setRate(Number.Rate pRate) throws SQLException {
		setString(pRate.format(false));
	}

	/**
	 * Set the Bonus of the item to be inserted
	 * @param pBonus the bonus of the item
	 */
	private void setBonus(Number.Rate pBonus) throws SQLException {
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
	private void updateAccount(long pValue) {
		updateLong(theActCol, pValue);
	}		

	/**
	 * Update the Rate of the item
	 * @param pValue the new rate
	 */
	private void updateRate(Number.Rate pValue) {
		updateString(theRateCol, pValue.format(false));
	}		

	/**
	 * Update the Bonus of the item
	 * @param pValue the new bonus
	 */
	private void updateBonus(Number.Rate pValue) {
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
			   theIdCol 	+ " bigint NOT NULL PRIMARY KEY, " +
			   theActCol	+ " bigint NOT NULL " +
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
		Rate.List		myList;
		long    		myId;
		long  			myAccountId;
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
			myList = (Rate.List)getList();
			
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
	protected void insertItem(Rate	pItem) throws Exception {
		
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
	protected void updateItem(Rate	pItem) throws Exception {
		Rate.Values myBase;
		
		/* Access the base */
		myBase = (Rate.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Utils.differs(pItem.getAccount(),
		  	  		  		  myBase.getAccount()))
				updateAccount(pItem.getAccount().getId());
			if (Utils.differs(pItem.getRate(),
				  		  	  myBase.getRate()))
				updateRate(pItem.getRate());
			if (Utils.differs(pItem.getBonus(),
						  	  myBase.getBonus())) 
				updateBonus(pItem.getBonus());
			if (Utils.differs(pItem.getEndDate(),
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
