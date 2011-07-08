package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableControl extends DatabaseTable<ControlData> {
	/**
	 * The name of the Static table
	 */
	private final static String theTabName 		= ControlData.listName;
				
	/**
	 * The name of the DataVersion column
	 */
	private final static String theVersCol 		= ControlData.fieldName(ControlData.FIELD_VERS);
				
	/**
	 * The name of the ControlKey column
	 */
	private final static String theControlCol 	= ControlData.fieldName(ControlData.FIELD_CONTROL);
				
	/**
	 * The name of the SecurityKey column
	 */
	private final static String theKeyCol 		= ControlData.fieldName(ControlData.FIELD_KEY);
				
	/**
	 * The name of the InitVector column
	 */
	private final static String theIVCol 		= ControlData.fieldName(ControlData.FIELD_IV);
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableControl(Database	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected ControlData.List  getLoadList(DataSet pData) {
		return pData.getControl();
	}
	
	/* Get the List for the table for updates */
	protected ControlData.List  getUpdateList(DataSet pData) {
		return new ControlData.List(pData.getControl(), ListStyle.UPDATE);
	}
	
	/* Create statement for Static */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 		+ " int NOT NULL PRIMARY KEY, " +
			   theVersCol		+ " int NOT NULL, " +
			   theControlCol	+ " varchar(" + ControlData.CTLLEN + ") NOT NULL," +
			   theKeyCol		+ " varbinary(" + ControlData.KEYLEN + ") NOT NULL," +
		   	   theIVCol			+ " binary(" + ControlData.INITVLEN + ") NOT NULL )";
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
		ControlData.List		myList;
		int 	   		myId;
		int	  			myVers;
		String			myControl;
		byte[]			myKey;
		byte[]			myVector;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   			= getInteger();
			myVers 			= getInteger();
			myControl		= getString();
			myKey  			= getBinary();
			myVector		= getBinary();
			
			/* Access the list */
			myList = (ControlData.List)getList();
			
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
	protected void insertItem(ControlData	pItem) throws Exception  {
		
		/* Protect the load */
		try {			
			/* Set the fields */
			setInteger(pItem.getId());
			setInteger(pItem.getDataVersion());
			setString(pItem.getControlKey());
			setBinary(pItem.getSecurityKey());
			setBinary(pItem.getInitVector());
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
	protected void updateItem(ControlData	pItem) throws Exception {
		ControlData.Values	myBase;
		
		/* Access the base */
		myBase = (ControlData.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (pItem.getDataVersion() != myBase.getDataVersion())
				updateInteger(theVersCol, pItem.getDataVersion());
			if (Utils.differs(pItem.getControlKey(),
							  myBase.getControlKey()))
				updateString(theControlCol, pItem.getControlKey());
			if (Utils.differs(pItem.getSecurityKey(),
					  		  myBase.getSecurityKey()))
				updateBinary(theKeyCol, pItem.getSecurityKey());
			if (Utils.differs(pItem.getInitVector(),
					  		  myBase.getInitVector()))
				updateBinary(theIVCol, pItem.getInitVector());
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
