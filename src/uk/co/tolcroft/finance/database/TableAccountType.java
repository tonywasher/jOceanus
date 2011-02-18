package uk.co.tolcroft.finance.database;

import java.sql.SQLException;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableAccountType extends DatabaseTable<AccountType> {
	/**
	 * The name of the AccountType table
	 */
	private final static String theTabName 		= "AccountTypes";
				
	/**
	 * The name of the AccountType column
	 */
	private final static String theActTypCol 	= "AccountType";
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableAccountType(Database	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected AccountType.List  getLoadList(DataSet pData) {
		return pData.getAccountTypes();
	}
	
	/* Get the List for the table for updates */
	protected AccountType.List  getUpdateList(DataSet pData) {
		return new AccountType.List(pData.getAccountTypes(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Account Type of the newly loaded item
	 * @return the Account Type
	 */
	private byte[] getAccountType() throws SQLException {
		return getBinary();
	}
		
	/**
	 * Set the AccountType of the item to be inserted
	 * @param pActType the Account Type of the item
	 */
	private void setAccountType(byte[] pActType) throws SQLException {
		setBinary(pActType);
	}
	
	/**
	 * Update the Name of the item
	 * @param pValue the new name
	 */
	private void updateName(byte[] pValue) {
		updateBinary(theActTypCol, pValue);
	}	

	/* Create statement for Account Types */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theActTypCol	+ " varbinary(" + AccountType.NAMELEN + ") NOT NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Account Types */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theActTypCol + 
		       " from " + getTableName();			
	}
		
	/* Load the account type */
	protected void loadItem() throws Exception {
		AccountType.List	myList;
		int	    			myId;
		byte[]  			myType;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   = getID();
			myType = getAccountType();
			
			/* Access the list */
			myList = (AccountType.List)getList();
			
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
	
	/* Insert statement for Account Types */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theActTypCol + ")" +
		       " VALUES(?,?)";
	}
		
	/* Insert an Account Type */
	protected void insertItem(AccountType 		pItem) throws Exception  {
		
		/* Protect the load */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setAccountType(pItem.getNameBytes());
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

	/* Update the Account Type */
	protected void updateItem(AccountType	pItem) throws Exception {
		AccountType.Values	myBase;
		
		/* Access the base */
		myBase = (AccountType.Values)pItem.getBaseObj();
			
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
