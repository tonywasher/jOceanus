package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.finance.data.Frequency;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Utils;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableFrequency extends DatabaseTable<Frequency> {
	/**
	 * The name of the table
	 */
	private final static String 	theTabName		= "Frequencies";
				
	/**
	 * The name of the Frequency column
	 */
	private final static String 	theFreqCol		= "Frequency";
			
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableFrequency(Database 	pDatabase) { 
		super(pDatabase, theTabName); 
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected Frequency.List  getLoadList(DataSet pData) {
		return pData.getFrequencys();
	}
	
	/* Get the List for the table for updates */
	protected Frequency.List  getUpdateList(DataSet pData) {
		return new Frequency.List(pData.getFrequencys(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Frequency of the newly loaded item
	 * @return the Frequency
	 */
	private String getFrequency() throws SQLException {
		return getString();
	}
	
	/**
	 * Set the Frequency of the item to be inserted
	 * @param pTransType the Transaction Type of the item
	 */
	private void setFrequency(String pFrequency) throws SQLException {
		setString(pFrequency);
	}

	/**
	 * Update the Name of the item
	 * @param pValue the new name
	 */
	private void updateName(String pValue) {
		updateString(theFreqCol, pValue);
	}	

	/* Create statement for Frequencies */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " bigint NOT NULL PRIMARY KEY, " +
			   theFreqCol	+ " varchar(" + Frequency.NAMELEN + ") NOT NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Frequencies */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theFreqCol + 
		               " from " + getTableName();			
	}
	
	/* Load the frequency */
	protected void loadItem() throws Exception {
		Frequency.List	myList;
		long    		myId;
		String  		myFreq;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId   = getID();
			myFreq = getFrequency();
			
			/* Access the list */
			myList = (Frequency.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, myFreq);
		}
								
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + theTabName + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Frequency */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
		       " (" + theIdCol + "," + theFreqCol + ")" +
		       " VALUES(?,?)";
	}
	
	/* Insert a frequency */
	protected void insertItem(Frequency 		pItem) throws Exception  {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setFrequency(pItem.getName());
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

	/* Update the Frequency */
	protected void updateItem(Frequency	pItem) throws Exception {
		Frequency.Values	myBase;
		
		/* Access the base */
		myBase = (Frequency.Values)pItem.getBaseObj();
			
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
