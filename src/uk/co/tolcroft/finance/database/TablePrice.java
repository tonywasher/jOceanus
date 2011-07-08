package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TablePrice extends DatabaseTable<AcctPrice> {
	/**
	 * The name of the Prices table
	 */
	private final static String theTabName 		= AcctPrice.listName;
				
	/**
	 * The name of the Account column
	 */
	private final static String theActCol    	= AcctPrice.fieldName(AcctPrice.FIELD_ACCOUNT);

	/**
	 * The name of the Date column
	 */
	private final static String theDateCol 		= AcctPrice.fieldName(AcctPrice.FIELD_DATE);

	/**
	 * The name of the Price column
	 */
	private final static String thePriceCol 	= AcctPrice.fieldName(AcctPrice.FIELD_PRICE);

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
	protected AcctPrice.List  getLoadList(DataSet pData) {
		return pData.getPrices();
	}
	
	/* Get the List for the table for updates */
	protected AcctPrice.List  getUpdateList(DataSet pData) {
		return new AcctPrice.List(pData.getPrices(), ListStyle.UPDATE);
	}
	
	/* Create statement for Prices */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theActCol	+ " int NOT NULL " +
			   		"REFERENCES " + TableAccount.idReference() + ", " +
   			   theDateCol	+ " date NOT NULL, " +
			   thePriceCol	+ " varbinary(" + 2*EncryptedPair.PRICELEN + ") NOT NULL )";
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
		AcctPrice.List		myList;
		int	    		myId;
		int  			myAccountId;
		byte[] 			myPrice;
		java.util.Date  myDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getInteger();
			myAccountId = getInteger();
			myDate 		= getDate();
			myPrice     = getBinary();
	
			/* Access the list */
			myList = (AcctPrice.List)getList();
			
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
	protected void insertItem(AcctPrice	pItem) throws Exception {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setInteger(pItem.getId());
			setInteger(pItem.getAccount().getId());
			setDate(pItem.getDate());
			setBinary(pItem.getPriceBytes());
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
	protected void updateItem(AcctPrice	pItem) throws Exception {
		AcctPrice.Values 	myBase;
		
		/* Access the base */
		myBase = (AcctPrice.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Account.differs(pItem.getAccount(),
		  		  	  			myBase.getAccount()))
				updateInteger(theActCol, pItem.getAccount().getId());
			if (Date.differs(pItem.getDate(),
				  		  	 myBase.getDate()))
				updateDate(theDateCol, pItem.getDate());
			if (Utils.differs(pItem.getPriceBytes(),
						  	  myBase.getPriceBytes())) 
				updateBinary(thePriceCol, pItem.getPriceBytes());
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
