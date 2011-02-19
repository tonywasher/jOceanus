package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableEvent extends DatabaseTable<Event> {
	/**
	 * The name of the Events table
	 */
	private final static String theTabName 		= "Events";
				
	/**
	 * The name of the Date column
	 */
	private final static String theDateCol 		= "Date";

	/**
	 * The name of the Description column
	 */
	private final static String theDescCol   	= "Description";

	/**
	 * The name of the Amount column
	 */
	private final static String theAmntCol   	= "Amount";

	/**
	 * The name of the Debit Account column
	 */
	private final static String theDebCol   	= "Debit";

	/**
	 * The name of the Credit Account column
	 */
	private final static String theCredCol   	= "Credit";

	/**
	 * The name of the Units column
	 */
	private final static String theUnitCol   	= "Units";

	/**
	 * The name of the TransType column
	 */
	private final static String theTrnTypCol 	= "TransactionType";

	/**
	 * The name of the TaxCredit flag column
	 */
	private final static String theTaxCrtCol  	= "TaxCredit";

	/**
	 * The name of the Dilution column
	 */
	private final static String theDiluteCol   	= "Dilution";
	
	/**
	 * The name of the Years column
	 */
	private final static String theYearsCol   	= "Years";
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableEvent(Database 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected Event.List  getLoadList(DataSet pData) {
		return pData.getEvents();
	}
	
	/* Get the List for the table for updates */
	protected Event.List  getUpdateList(DataSet pData) {
		return new Event.List(pData.getEvents(), ListStyle.UPDATE);
	}
	
	/**
	 * Determine the Date of the newly loaded item
	 * @return the Date
	 */
	protected java.util.Date getDate() throws SQLException {
		return super.getDate();
	}

	/**
	 * Determine the Description of the newly loaded item
	 * @return the Description
	 */
	private byte[] getDesc() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the Amount of the newly loaded item
	 * @return the Amount
	 */
	private byte[] getAmount() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the Debit Account of the newly loaded item
	 * @return the Debit account
	 */
	private int getDebit() throws SQLException {
		return getInteger();
	}

	/**
	 * Determine the Credit Account of the newly loaded item
	 * @return the Credit account
	 */
	private int getCredit() throws SQLException {
		return getInteger();
	}

	/**
	 * Determine the Units of the newly loaded item
	 * @return the Units
	 */
	private byte[] getUnits() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the TransType of the newly loaded item
	 * @return the TransType
	 */
	private int getTransType() throws SQLException {
		return getInteger();
	}

	/**
	 * Determine the TaxCredit of the newly loaded item
	 * @return the TaxCredit 
	 */
	private byte[] getTaxCredit() throws SQLException {
		return getBinary();
	}

	/**
	 * Determine the Dilution of the newly loaded item
	 * @return the Dilution 
	 */
	private String getDilution() throws SQLException {
		return getString();
	}

	/**
	 * Determine the Years of the newly loaded item
	 * @return the Years
	 */
	private Integer getYears() throws SQLException {
		return getInteger();
	}		
	
	/**
	 * Set the Date of the item to be inserted
	 * @param pDate the Date of the item
	 */
	protected void setDate(Date pDate) throws SQLException {
		super.setDate(pDate);
	}

	/**
	 * Set the Description of the item to be inserted
	 * @param pDesc the description of the item
	 */
	private void setDesc(byte[] pDesc) throws SQLException {
		setBinary(pDesc);
	}

	/**
	 * Set the Amount of the item to be inserted
	 * @param pAmount the amount of the item
	 */
	private void setAmount(byte[] pAmount) throws SQLException {
		setBinary(pAmount);
	}

	/**
	 * Set the Debit Account of the item to be inserted
	 * @param pDebit the partner account of the item
	 */
	private void setDebit(int pDebit) throws SQLException {
		setInteger(pDebit);
	}

	/**
	 * Set the Credit Account of the item to be inserted
	 * @param pCredit the partner account of the item
	 */
	private void setCredit(int pCredit) throws SQLException {
		setInteger(pCredit);
	}

	/**
	 * Set the Units of the item to be inserted
	 * @param pUnits the units of the item
	 */
	private void setUnits(byte[] pUnits) throws SQLException {
		setBinary(pUnits);
	}

	/**
	 * Set the TransType of the item to be inserted
	 * @param pTransType the transtype of the item
	 */
	private void setTransType(int pTransType) throws SQLException {
		setInteger(pTransType);
	}

	/**
	 * Set the TaxCredit of the item to be inserted
	 * @param pCredit the TaxCredit of the item
	 */
	private void setTaxCredit(byte[] pCredit) throws SQLException {
		setBinary(pCredit);
	}

	/**
	 * Set the Dilution of the item to be inserted
	 * @param pDilution the Dilution of the item
	 */
	private void setDilution(Dilution pDilution) throws SQLException {
		setString((pDilution == null) ? null : pDilution.format(false));
	}

	/**
	 * Set the Years of the item to be inserted
	 * @param pYears the years of the item
	 */
	private void setYears(Integer pYears) throws SQLException {
		setInteger(pYears);
	}
	
	/**
	 * Update the Date of the item
	 * @param pValue the new date
	 */
	private void updateDate(Date pValue) {
		updateDate(theDateCol, pValue);
	}		

	/**
	 * Update the Description of the item
	 * @param pValue the new description
	 */
	private void updateDescription(byte[] pValue) {
		updateBinary(theDescCol, pValue);
	}

	/**
	 * Update the Amount of the item
	 * @param pValue the new amount
	 */
	private void updateAmount(byte[] pValue) {
		updateBinary(theAmntCol, pValue);
	}	

	/**
	 * Update the Debit Account of the item
	 * @param pValue the new debit account
	 */
	private void updateDebit(int pValue) {
		updateInteger(theDebCol, pValue);
	}

	/**
	 * Update the Credit Account of the item
	 * @param pValue the new credit account
	 */
	private void updateCredit(int pValue) {
		updateInteger(theCredCol, pValue);
	}

	/**
	 * Update the Units of the item
	 * @param pValue the new units
	 */
	private void updateUnits(byte[] pValue) {
		updateBinary(theUnitCol, pValue);
	}

	/**
	 * Update the TransType of the item
	 * @param pValue the new transtype
	 */
	private void updateTransType(int pValue) {
		updateInteger(theTrnTypCol, pValue);
	}

	/**
	 * Update the TaxCredit of the item
	 * @param pValue the new tax credit
	 */
	private void updateTaxCredit(byte[] pValue) {
		updateBinary(theTaxCrtCol, pValue);
	}
	
	/**
	 * Update the Dilution of the item
	 * @param pValue the new dilution
	 */
	private void updateDilution(Dilution pValue) {
		updateString(theDiluteCol, (pValue == null) ? null : pValue.format(false));
	}
	
	/**
	 * Update the Frequency of the item
	 * @param pValue the new frequency
	 */
	private void updateYears(Integer pValue) {
		updateInteger(theYearsCol, pValue);
	}
	
	/* Create statement for Events */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
   			   theDateCol	+ " date NOT NULL, " +
  			   theDescCol	+ " varbinary(" + Event.DESCLEN + ") NOT NULL, " +
  			   theAmntCol	+ " varbinary(" + EncryptedPair.MONEYLEN + ") NOT NULL, " +
			   theDebCol	+ " int NOT NULL " +
		   			"REFERENCES " + TableAccount.idReference() + ", " +
  			   theCredCol	+ " int NOT NULL " +
		   			"REFERENCES " + TableAccount.idReference() + ", " +
 			   theUnitCol	+ " varbinary(" + EncryptedPair.UNITSLEN + ") NULL, " +
		   	   theTrnTypCol	+ " int NOT NULL " +
	   				"REFERENCES " + TableTransactionType.idReference() + ", " +
 			   theTaxCrtCol	+ " varbinary(" + EncryptedPair.MONEYLEN + ") NULL, " +
 			   theDiluteCol	+ " decimal(18,6) NULL, " +
			   theYearsCol	+ " int NULL )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Events */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theDateCol + "," + 
        				theDescCol + "," + theAmntCol + "," + 
        				theDebCol + "," + theCredCol +  "," +
        				theUnitCol + "," + theTrnTypCol + "," +
        				theTaxCrtCol + "," + theDiluteCol + "," +
        				theYearsCol + " " +
        				" from " + getTableName() +			
        				" order by " + theDateCol;			
	}
	
	/* Load the event */
	protected void loadItem() throws Exception {
		Event.List		myList;
		int	    		myId;
		int  			myDebitId;
		int  			myCreditId;
		int  			myTranType;
		byte[] 			myDesc;
		byte[] 			myAmount;
		byte[] 			myUnits;
		byte[] 			myTaxCred;
		String			myDilution;
		Integer			myYears;
		java.util.Date  myDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getID();
			myDate 		= getDate();
			myDesc    	= getDesc();
			myAmount    = getAmount();
			myDebitId 	= getDebit();
			myCreditId 	= getCredit();
			myUnits 	= getUnits();
			myTranType  = getTransType();
			myTaxCred   = getTaxCredit();
			myDilution  = getDilution();
			myYears  	= getYears();
	
			/* Access the list */
			myList = (Event.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, 
			           	   myDate,
				           myDesc,
				           myAmount,
				           myDebitId, 
				           myCreditId,
				           myUnits,
				           myTranType,
				           myTaxCred,
				           myDilution,
				           myYears);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Events */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
        			" (" + theIdCol + "," + theDateCol + "," +
        			theDescCol + "," + theAmntCol + "," + 
        			theDebCol + "," + theCredCol +  "," +
        			theUnitCol + "," + theTrnTypCol + "," +
        			theTaxCrtCol + "," + theDiluteCol + "," + 
        			theYearsCol + ") " + 
        			"VALUES(?,?,?,?,?,?,?,?,?,?,?)";
	}
	
	/* Insert the event */
	protected void insertItem(Event			pItem) throws Exception {

		/* Protect the access */
		try {			
			/* Set the fields */
			setID(pItem.getId());
			setDate(pItem.getDate());
			setDesc(pItem.getDescBytes());
			setAmount(pItem.getAmountBytes());
			setDebit(pItem.getDebit().getId());
			setCredit(pItem.getCredit().getId());
			setUnits(pItem.getUnitsBytes());
			setTransType(pItem.getTransType().getId());
			setTaxCredit(pItem.getTaxCredBytes());
			setDilution(pItem.getDilution());
			setYears(pItem.getYears());
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
	
	/* Update the event */
	protected void updateItem(Event			pItem) throws Exception {
		Event.Values 	myBase;
		
		/* Access the base */
		myBase = (Event.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Date.differs(pItem.getDate(),
				  		  	 myBase.getDate()))
				updateDate(pItem.getDate());
			if (Utils.differs(pItem.getDescBytes(),
						  	  myBase.getDescBytes())) 
				updateDescription(pItem.getDescBytes());
			if (Utils.differs(pItem.getAmountBytes(),
		  		  	  		  myBase.getAmountBytes()))
				updateAmount(pItem.getAmountBytes());
			if (Account.differs(pItem.getDebit(),
				  	  			myBase.getDebit())) 
				updateDebit(pItem.getDebit().getId());
			if (Account.differs(pItem.getCredit(),
		  	  		  			myBase.getCredit())) 
				updateCredit(pItem.getCredit().getId());
			if (Utils.differs(pItem.getUnitsBytes(),
  		  	  		  		  myBase.getUnitsBytes()))
				updateUnits(pItem.getUnitsBytes());
			if (TransactionType.differs(pItem.getTransType(),
		  		  	  		  			myBase.getTransType()))
				updateTransType(pItem.getTransType().getId());
			if (Utils.differs(pItem.getTaxCredBytes(),
				  	  		  myBase.getTaxCredBytes())) 
				updateTaxCredit(pItem.getTaxCredBytes());
			if (Dilution.differs(pItem.getDilution(),
		  	  		  		  	 myBase.getDilution())) 
				updateDilution(pItem.getDilution());
			if (Utils.differs(pItem.getYears(),
		  	  		  		  myBase.getYears())) 
				updateYears(pItem.getYears());
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
