package uk.co.tolcroft.finance.database;

import java.sql.SQLException;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTransactionType extends DatabaseTable<TransactionType> {
	/**
	 * The name of the TransType table
	 */
	private final static String 	theTabName		= "TransactionTypes";
				
	/**
	 * The name of the TransType column
	 */
	private final static String 	theTrnTypCol	= "TransactionType";
			
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTransactionType(Database 	pDatabase) { 
		super(pDatabase, theTabName); 
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected TransactionType.List  getLoadList(DataSet pData) {
		return pData.getTransTypes();
	}
	
	/* Get the List for the table for updates */
	protected TransactionType.List  getUpdateList(DataSet pData) {
		return new TransactionType.List(pData.getTransTypes(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Transaction Type of the newly loaded item
	 * @return the Transaction Type
	 */
	private byte[] getTransType() throws SQLException {
		return getBinary();
	}
	
	/**
	 * Set the TransactionType of the item to be inserted/updated
	 * @param pTransType the Transaction Type of the item
	 */
	private void setTransType(byte[] pTransType) throws SQLException {
		setBinary(pTransType);
	}

	/**
	 * Update the Name of the item
	 * @param pValue the new name
	 */
	private void updateName(byte[] pValue) {
		updateBinary(theTrnTypCol, pValue);
	}	

	/* Create statement for Transaction Types */
	public String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theTrnTypCol	+ " varbinary(" + TransactionType.NAMELEN + ") NOT NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Transaction Types */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theTrnTypCol + 
		               " from " + getTableName();			
	}
	
	/* Load the transaction type */
	protected void loadItem() throws Exception {
		TransactionType.List	myList;
		int	    				myId;
		byte[]  				myType;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   = getID();
			myType = getTransType();
			
			/* Access the list */
			myList = (TransactionType.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, myType);
		}
								
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + theTabName + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Transaction Types */
	public String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theTrnTypCol + ")" +
		       " VALUES(?,?)";
	}
	
	/* Insert a transaction Type */
	protected void insertItem(TransactionType 	pItem) throws Exception  {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setTransType(pItem.getNameBytes());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to insert " + theTabName + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}

	/* Update the TransactionType */
	protected void updateItem(TransactionType	pItem) throws Exception {
		TransactionType.Values	myBase;
		
		/* Access the base */
		myBase = (TransactionType.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Utils.differs(pItem.getNameBytes(),
				  		  	  myBase.getNameBytes()))
				updateName(pItem.getNameBytes());
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
								pItem,
					            "Failed to update " + theTabName + " item",
					            e);
		}
			
		/* Return to caller */
		return;
	}	
}
