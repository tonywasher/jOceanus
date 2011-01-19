package uk.co.tolcroft.finance.database;

import java.sql.SQLException;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTaxType extends DatabaseTable<TaxType> {
	/**
	 * The name of the TaxType table
	 */
	private final static String theTabName 		= "TaxTypes";
				
	/**
	 * The name of the TaxType column
	 */
	private final static String theTaxTypCol 	= "TaxType";
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxType(Database 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected TaxType.List  getLoadList(DataSet pData) {
		return pData.getTaxTypes();
	}
	
	/* Get the List for the table for updates */
	protected TaxType.List  getUpdateList(DataSet pData) {
		return new TaxType.List(pData.getTaxTypes(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Tax Type of the newly loaded item
	 * @return the Tax Type
	 */
	private String getTaxType() throws SQLException {
		return getString();
	}
		
	/**
	 * Set the TaxType of the item to be inserted
	 * @param pTaxType the Tax Type of the item
	 */
	private void setTaxType(String pTaxType) throws SQLException {
		setString(pTaxType);
	}
	
	/**
	 * Update the Name of the item
	 * @param pValue the new name
	 */
	private void updateName(String pValue) {
		updateString(theTaxTypCol, pValue);
	}	

	/* Create statement for Tax Types */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " bigint NOT NULL PRIMARY KEY, " +
			   theTaxTypCol	+ " varchar(" + TaxType.NAMELEN + ") NOT NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Tax Types */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theTaxTypCol + 
		       " from " + getTableName();			
	}
		
	/* Load the tax type */
	protected void loadItem() throws Exception {
		TaxType.List	myList;
		long    		myId;
		String  		myType;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   = getID();
			myType = getTaxType();
			
			/* Access the list */
			myList = (TaxType.List)getList();
			
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
	
	/* Insert statement for Tax Types */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theTaxTypCol + ")" +
		       " VALUES(?,?)";
	}
		
	/* Insert a Tax Type */
	protected void insertItem( TaxType 			pItem) throws Exception  {
		
		/* Protect the load */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setTaxType(pItem.getName());
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

	/* Update the TaxType */
	protected void updateItem(TaxType	pItem) throws Exception {
		TaxType.Values	myBase;
		
		/* Access the base */
		myBase = (TaxType.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Utils.differs(pItem.getName(),
				  		  	  myBase.getName()))
				updateName(pItem.getName());
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
