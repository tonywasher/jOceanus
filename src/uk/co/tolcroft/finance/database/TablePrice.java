package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TablePrice extends DatabaseTable<Price> {
	/**
	 * The name of the Prices table
	 */
	private final static String theTabName 		= "Prices";
				
	/**
	 * The name of the Account column
	 */
	private final static String theActCol    	= "Account";

	/**
	 * The name of the Date column
	 */
	private final static String theDateCol 		= "Date";

	/**
	 * The name of the Price column
	 */
	private final static String thePriceCol 	= "Price";

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TablePrice(Database 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected Price.List  getLoadList(DataSet pData) {
		return pData.getPrices();
	}
	
	/* Get the List for the table for updates */
	protected Price.List  getUpdateList(DataSet pData) {
		return new Price.List(pData.getPrices(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Account of the newly loaded item
	 * @return the Account
	 */
	private int getAccount() throws SQLException {
		return getInteger();
	}

	/**
	 * Determine the Date of the newly loaded item
	 * @return the Rate
	 */
	protected java.util.Date getDate() throws SQLException {
		return super.getDate();
	}

	/**
	 * Determine the Price of the newly loaded item
	 * @return the Bonus
	 */
	private String getPrice() throws SQLException {
		return getString();
	}

	/**
	 * Set the Account of the item to be inserted
	 * @param pAccount the account of the item
	 */
	private void setAccount(int pAccount) throws SQLException {
		setInteger(pAccount);
	}

	/**
	 * Set the Date of the item to be inserted
	 * @param pDate the Date of the item
	 */
	protected void setDate(Date pDate) throws SQLException {
		super.setDate(pDate);
	}

	/**
	 * Set the Price of the item to be inserted
	 * @param pPrice the price of the item
	 */
	private void setPrice(Number.Price pPrice) throws SQLException {
		setString(pPrice.format(false));
	}

	/**
	 * Update the Account of the item
	 * @param pValue the new account
	 */
	private void updateAccount(int pValue) {
		updateInteger(theActCol, pValue);
	}		

	/**
	 * Update the Date of the item
	 * @param pValue the new date
	 */
	private void updateDate(Date pValue) {
		updateDate(theDateCol, pValue);
	}		

	/**
	 * Update the Price of the item
	 * @param pPrice the new price
	 */
	private void updatePrice(Number.Price pValue) {
		updateString(thePriceCol, (pValue == null) ? null
                								   : pValue.format(false));
	}	

	/* Create statement for Prices */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theActCol	+ " int NOT NULL " +
			   		"REFERENCES " + TableAccount.idReference() + ", " +
   			   theDateCol	+ " date NOT NULL, " +
			   thePriceCol	+ " money NOT NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Prices */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theActCol + "," + 
		                 theDateCol + "," + thePriceCol + 
		                 " from " + getTableName();			
	}
	
	/* Load the price */
	protected void loadItem() throws Exception {
		Price.List		myList;
		int	    		myId;
		int  			myAccountId;
		String 			myPrice;
		java.util.Date  myDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getID();
			myAccountId = getAccount();
			myDate 		= getDate();
			myPrice     = getPrice();
	
			/* Access the list */
			myList = (Price.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, 
			           	   myDate,
				           myAccountId, 
				           myPrice);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Prices */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theActCol + "," +
		              theDateCol + "," + thePriceCol + ")" + 
		       " VALUES(?,?,?,?)";
	}
	
	/* Insert the price */
	protected void insertItem(Price	pItem) throws Exception {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setAccount(pItem.getAccount().getId());
			setDate(pItem.getDate());
			setPrice(pItem.getPrice());
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
	
	/* Update the price */
	protected void updateItem(Price	pItem) throws Exception {
		Price.Values 	myBase;
		
		/* Access the base */
		myBase = (Price.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Utils.differs(pItem.getAccount(),
		  		  	  		  myBase.getAccount()))
				updateAccount(pItem.getAccount().getId());
			if (Utils.differs(pItem.getDate(),
				  		  	  myBase.getDate()))
				updateDate(pItem.getDate());
			if (Utils.differs(pItem.getPrice(),
						  	  myBase.getPrice())) 
				updatePrice(pItem.getPrice());
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
