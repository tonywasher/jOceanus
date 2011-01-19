package uk.co.tolcroft.finance.database;

import java.sql.SQLException;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTaxRegime extends DatabaseTable<TaxRegime> {
	/**
	 * The name of the TaxRegime table
	 */
	private final static String theTabName 		= "TaxRegime";
				
	/**
	 * The name of the TaxRegime column
	 */
	private final static String theTaxRegCol 	= "TaxRegime";
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxRegime(Database 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected TaxRegime.List  getLoadList(DataSet pData) {
		return pData.getTaxRegimes();
	}
	
	/* Get the List for the table for updates */
	protected TaxRegime.List  getUpdateList(DataSet pData) {
		return new TaxRegime.List(pData.getTaxRegimes(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Tax Regime of the newly loaded item
	 * @return the Tax Regime
	 */
	private String getTaxRegime() throws SQLException {
		return getString();
	}
		
	/**
	 * Set the Tax Regime of the item to be inserted
	 * @param pTaxReg the Tax Regime of the item
	 */
	private void setTaxRegime(String pTaxReg) throws SQLException {
		setString(pTaxReg);
	}
	
	/**
	 * Update the Name of the item
	 * @param pValue the new name
	 */
	private void updateName(String pValue) {
		updateString(theTaxRegCol, pValue);
	}	

	/* Create statement for Tax Regimes */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " bigint NOT NULL PRIMARY KEY, " +
			   theTaxRegCol	+ " varchar(" + TaxRegime.NAMELEN + ") NOT NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Tax Regimes */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theTaxRegCol + 
		       " from " + getTableName();			
	}
		
	/* Load the tax regime */
	protected void loadItem() throws Exception {
		TaxRegime.List	myList;
		long    		myId;
		String  		myType;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   = getID();
			myType = getTaxRegime();
			
			/* Access the list */
			myList = (TaxRegime.List)getList();
			
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
	
	/* Insert statement for Tax Regimes */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theTaxRegCol + ")" +
		       " VALUES(?,?)";
	}
		
	/* Insert a Tax Regime */
	protected void insertItem(TaxRegime 		pItem) throws Exception  {
		
		/* Protect the load */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setTaxRegime(pItem.getName());
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

	/* Update the TaxRegime */
	protected void updateItem(TaxRegime	pItem) throws Exception {
		TaxRegime.Values	myBase;
		
		/* Access the base */
		myBase = (TaxRegime.Values)pItem.getBaseObj();
			
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
