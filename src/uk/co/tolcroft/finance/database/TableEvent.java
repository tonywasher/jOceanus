package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableEvent extends DatabaseTable<Event> {
	/**
	 * The name of the Events table
	 */
	private final static String theTabName 		= Event.listName;
				
	/**
	 * The name of the Date column
	 */
	private final static String theDateCol 		= Event.fieldName(Event.FIELD_DATE);

	/**
	 * The name of the Description column
	 */
	private final static String theDescCol   	= Event.fieldName(Event.FIELD_DESC);

	/**
	 * The name of the Amount column
	 */
	private final static String theAmntCol   	= Event.fieldName(Event.FIELD_AMOUNT);

	/**
	 * The name of the Debit Account column
	 */
	private final static String theDebCol   	= Event.fieldName(Event.FIELD_DEBIT);

	/**
	 * The name of the Credit Account column
	 */
	private final static String theCredCol   	= Event.fieldName(Event.FIELD_CREDIT);

	/**
	 * The name of the Units column
	 */
	private final static String theUnitCol   	= Event.fieldName(Event.FIELD_UNITS);

	/**
	 * The name of the TransType column
	 */
	private final static String theTrnTypCol 	= Event.fieldName(Event.FIELD_TRNTYP);

	/**
	 * The name of the TaxCredit flag column
	 */
	private final static String theTaxCrtCol  	= Event.fieldName(Event.FIELD_TAXCREDIT);

	/**
	 * The name of the Dilution column
	 */
	private final static String theDiluteCol   	= Event.fieldName(Event.FIELD_DILUTION);
	
	/**
	 * The name of the Years column
	 */
	private final static String theYearsCol   	= Event.fieldName(Event.FIELD_YEARS);
	
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
	
	/* Create statement for Events */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
   			   theDateCol	+ " date NOT NULL, " +
  			   theDescCol	+ " varbinary(" + 2*Event.DESCLEN + ") NOT NULL, " +
  			   theAmntCol	+ " varbinary(" + 2*EncryptedPair.MONEYLEN + ") NOT NULL, " +
			   theDebCol	+ " int NOT NULL " +
		   			"REFERENCES " + TableAccount.idReference() + ", " +
  			   theCredCol	+ " int NOT NULL " +
		   			"REFERENCES " + TableAccount.idReference() + ", " +
 			   theUnitCol	+ " varbinary(" + 2*EncryptedPair.UNITSLEN + ") NULL, " +
		   	   theTrnTypCol	+ " int NOT NULL " +
	   				"REFERENCES " + TableTransactionType.idReference() + ", " +
 			   theTaxCrtCol	+ " varbinary(" + 2*EncryptedPair.MONEYLEN + ") NULL, " +
 			   theDiluteCol	+ " varbinary(" + 2*EncryptedPair.DILUTELEN + ") NULL, " +
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
		byte[]			myDilution;
		Integer			myYears;
		java.util.Date  myDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getInteger();
			myDate 		= getDate();
			myDesc    	= getBinary();
			myAmount    = getBinary();
			myDebitId 	= getInteger();
			myCreditId 	= getInteger();
			myUnits 	= getBinary();
			myTranType  = getInteger();
			myTaxCred   = getBinary();
			myDilution  = getBinary();
			myYears  	= getInteger();
	
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
			setInteger(pItem.getId());
			setDate(pItem.getDate());
			setBinary(pItem.getDescBytes());
			setBinary(pItem.getAmountBytes());
			setInteger(pItem.getDebit().getId());
			setInteger(pItem.getCredit().getId());
			setBinary(pItem.getUnitsBytes());
			setInteger(pItem.getTransType().getId());
			setBinary(pItem.getTaxCredBytes());
			setBinary(pItem.getDilutionBytes());
			setInteger(pItem.getYears());
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
				updateDate(theDateCol, pItem.getDate());
			if (Utils.differs(pItem.getDescBytes(),
						  	  myBase.getDescBytes())) 
				updateBinary(theDescCol, pItem.getDescBytes());
			if (Utils.differs(pItem.getAmountBytes(),
		  		  	  		  myBase.getAmountBytes()))
				updateBinary(theAmntCol, pItem.getAmountBytes());
			if (Account.differs(pItem.getDebit(),
				  	  			myBase.getDebit())) 
				updateInteger(theDebCol, pItem.getDebit().getId());
			if (Account.differs(pItem.getCredit(),
		  	  		  			myBase.getCredit())) 
				updateInteger(theCredCol, pItem.getCredit().getId());
			if (Utils.differs(pItem.getUnitsBytes(),
  		  	  		  		  myBase.getUnitsBytes()))
				updateBinary(theUnitCol, pItem.getUnitsBytes());
			if (TransactionType.differs(pItem.getTransType(),
		  		  	  		  			myBase.getTransType()))
				updateInteger(theTrnTypCol, pItem.getTransType().getId());
			if (Utils.differs(pItem.getTaxCredBytes(),
				  	  		  myBase.getTaxCredBytes())) 
				updateBinary(theTaxCrtCol, pItem.getTaxCredBytes());
			if (Utils.differs(pItem.getDilutionBytes(),
		  	  		  		  myBase.getDilutionBytes())) 
				updateBinary(theDiluteCol, pItem.getDilutionBytes());
			if (Utils.differs(pItem.getYears(),
		  	  		  		  myBase.getYears())) 
				updateInteger(theYearsCol, pItem.getYears());
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
