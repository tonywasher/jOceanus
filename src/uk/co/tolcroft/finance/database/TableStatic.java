package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableStatic extends DatabaseTable<Static> {
	/**
	 * The name of the Static table
	 */
	private final static String theTabName 		= "Static";
				
	/**
	 * The name of the DataVersion column
	 */
	private final static String theVersCol 		= "DataVersion";
				
	/**
	 * The name of the ControlKey column
	 */
	private final static String theControlCol 	= "ControlKey";
				
	/**
	 * The name of the SecurityKey column
	 */
	private final static String theKeyCol 		= "SecurityKey";
				
	/**
	 * The name of the InitVector column
	 */
	private final static String theIVCol 		= "InitVector";
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableStatic(Database	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected Static.List  getLoadList(DataSet pData) {
		return pData.getStatic();
	}
	
	/* Get the List for the table for updates */
	protected Static.List  getUpdateList(DataSet pData) {
		return new Static.List(pData.getStatic(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Data Version of the newly loaded item
	 * @return the Data Version
	 */
	private int getDataVersion() throws SQLException {
		return getInteger();
	}
		
	/**
	 * Determine the ControlKey of the newly loaded item
	 * @return the Control Key
	 */
	private String getControlKey() throws SQLException {
		return getString();
	}
		
	/**
	 * Determine the SecurityKey of the newly loaded item
	 * @return the Security Key
	 */
	private byte[] getSecurityKey() throws SQLException {
		return getBinary();
	}
		
	/**
	 * Determine the InitVector of the newly loaded item
	 * @return the InitVector
	 */
	private byte[] getInitVector() throws SQLException {
		return getBinary();
	}
		
	/**
	 * Set the DataVersion of the item to be inserted
	 * @param pVersion the version of the item
	 */
	private void setDataVersion(int pVersion) throws SQLException {
		setInteger(pVersion);
	}
	
	/**
	 * Set the ControlKey of the item to be inserted
	 * @param pKey the SecurityKey of the item
	 */
	private void setControlKey(String pKey) throws SQLException {
		setString(pKey);
	}
	
	/**
	 * Set the SecurityKey of the item to be inserted
	 * @param pKey the SecurityKey of the item
	 */
	private void setSecurityKey(byte[] pKey) throws SQLException {
		setBinary(pKey);
	}
	
	/**
	 * Set the InitVector of the item to be inserted
	 * @param pInitVector the InitVector of the item
	 */
	private void setInitVector(byte[] pInitVector) throws SQLException {
		setBinary(pInitVector);
	}
	
	/**
	 * Update the version of the item
	 * @param pValue the new version
	 */
	private void updateDataVersion(int pValue) {
		updateInteger(theVersCol, pValue);
	}	

	/**
	 * Update the ControlKey of the item
	 * @param pValue the new key
	 */
	private void updateControlKey(String pValue) {
		updateString(theControlCol, pValue);
	}	

	/**
	 * Update the SecurityKey of the item
	 * @param pValue the new key
	 */
	private void updateSecurityKey(byte[] pValue) {
		updateBinary(theKeyCol, pValue);
	}	

	/**
	 * Update the InitVector of the item
	 * @param pValue the new initVector
	 */
	private void updateInitVector(byte[] pValue) {
		updateBinary(theIVCol, pValue);
	}	

	/* Create statement for Static */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 		+ " int NOT NULL PRIMARY KEY, " +
			   theVersCol		+ " int NOT NULL, " +
			   theControlCol	+ " varchar(" + Static.CTLLEN + ") NOT NULL," +
			   theKeyCol		+ " varbinary(" + Static.KEYLEN + ") NOT NULL," +
		   	   theIVCol			+ " binary(" + Static.INITVLEN + ") NOT NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Static */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theVersCol + 
		 			 "," + theControlCol + 
		 			 "," + theKeyCol + "," + theIVCol +
		       " from " + getTableName();			
	}
		
	/* Load the static */
	protected void loadItem() throws Exception {
		Static.List		myList;
		int 	   		myId;
		int	  			myVers;
		String			myControl;
		byte[]			myKey;
		byte[]			myVector;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   			= getID();
			myVers 			= getDataVersion();
			myControl		= getControlKey();
			myKey  			= getSecurityKey();
			myVector		= getInitVector();
			
			/* Access the list */
			myList = (Static.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, myVers, myControl, myKey, myVector);
		}
								
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + theTabName + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Static */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theVersCol + 
 			   "," + theControlCol + 
		       "," + theKeyCol + "," + theIVCol + ")" +
		       " VALUES(?,?,?,?,?)";
	}
		
	/* Insert a Static */
	protected void insertItem(Static	pItem) throws Exception  {
		
		/* Protect the load */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setDataVersion(pItem.getDataVersion());
			setControlKey(pItem.getControlKey());
			setSecurityKey(pItem.getSecurityKey());
			setInitVector(pItem.getInitVector());
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

	/* Update the Static */
	protected void updateItem(Static	pItem) throws Exception {
		Static.Values	myBase;
		
		/* Access the base */
		myBase = (Static.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (pItem.getDataVersion() != myBase.getDataVersion())
				updateDataVersion(pItem.getDataVersion());
			if (Utils.differs(pItem.getControlKey(),
							  myBase.getControlKey()))
				updateControlKey(pItem.getControlKey());
			if (Utils.differs(pItem.getSecurityKey(),
					  		  myBase.getSecurityKey()))
				updateSecurityKey(pItem.getSecurityKey());
			if (Utils.differs(pItem.getInitVector(),
					  		  myBase.getInitVector()))
				updateInitVector(pItem.getInitVector());
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
