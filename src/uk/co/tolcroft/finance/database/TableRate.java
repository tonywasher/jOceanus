package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableRate extends DatabaseTable<AcctRate> {
	/**
	 * The name of the Rates table
	 */
	private final static String theTabName 		= AcctRate.listName;
				
	/**
	 * The name of the Account column
	 */
	private final static String theActCol    	= AcctRate.fieldName(AcctRate.FIELD_ACCOUNT);

	/**
	 * The name of the Rate column
	 */
	private final static String theRateCol 		= AcctRate.fieldName(AcctRate.FIELD_RATE);

	/**
	 * The name of the Bonus column
	 */
	private final static String theBonusCol   	= AcctRate.fieldName(AcctRate.FIELD_BONUS);

	/**
	 * The name of the EndDate column
	 */
	private final static String theEndDatCol 	= AcctRate.fieldName(AcctRate.FIELD_ENDDATE);

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
	
	/* Create statement for Rates */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theActCol	+ " int NOT NULL " +
			   		"REFERENCES " + TableAccount.idReference() + ", " +
   			   theRateCol	+ " varbinary(" + 2*EncryptedPair.RATELEN + ") NOT NULL, " +
			   theBonusCol	+ " varbinary(" + 2*EncryptedPair.RATELEN + ") NULL, " +
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
		byte[]			myRate;
		byte[] 			myBonus;
		java.util.Date  myEndDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getInteger();
			myAccountId = getInteger();
			myRate 		= getBinary();
			myBonus     = getBinary();
			myEndDate  	= getDate();
	
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
			setInteger(pItem.getId());
			setInteger(pItem.getAccount().getId());
			setBinary(pItem.getRateBytes());
			setBinary(pItem.getBonusBytes());
			setDate(pItem.getEndDate());
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
				updateInteger(theActCol, pItem.getAccount().getId());
			if (Utils.differs(pItem.getRateBytes(),
				  		  	  myBase.getRateBytes()))
				updateBinary(theRateCol, pItem.getRateBytes());
			if (Utils.differs(pItem.getBonusBytes(),
					  		  myBase.getBonusBytes())) 
				updateBinary(theBonusCol, pItem.getBonusBytes());
			if (Date.differs(pItem.getEndDate(),
				             myBase.getEndDate())) 
				updateDate(theEndDatCol, pItem.getEndDate());
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
