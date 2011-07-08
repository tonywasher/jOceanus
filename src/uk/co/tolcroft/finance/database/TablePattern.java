package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TablePattern extends DatabaseTable<Pattern> {
	/**
	 * The name of the Patterns table
	 */
	private final static String theTabName 		= Pattern.listName;
				
	/**
	 * The name of the Account column
	 */
	private final static String theActCol    	= Pattern.fieldName(Pattern.FIELD_ACCOUNT);

	/**
	 * The name of the Date column
	 */
	private final static String theDateCol 		= Pattern.fieldName(Pattern.FIELD_DATE);

	/**
	 * The name of the Description column
	 */
	private final static String theDescCol   	= Pattern.fieldName(Pattern.FIELD_DESC);

	/**
	 * The name of the Amount column
	 */
	private final static String theAmntCol   	= Pattern.fieldName(Pattern.FIELD_AMOUNT);

	/**
	 * The name of the Partner Account column
	 */
	private final static String thePartCol   	= Pattern.fieldName(Pattern.FIELD_PARTNER);

	/**
	 * The name of the TransType column
	 */
	private final static String theTrnTypCol 	= Pattern.fieldName(Pattern.FIELD_TRNTYP);

	/**
	 * The name of the isCredit flag column
	 */
	private final static String theIsCrtCol  	= Pattern.fieldName(Pattern.FIELD_CREDIT);

	/**
	 * The name of the Frequency column
	 */
	private final static String theFreqCol   	= Pattern.fieldName(Pattern.FIELD_FREQ);
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TablePattern(Database	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected Pattern.List  getLoadList(DataSet pData) {
		return pData.getPatterns();
	}
	
	/* Get the List for the table for updates */
	protected Pattern.List  getUpdateList(DataSet pData) {
		return new Pattern.List(pData.getPatterns(), ListStyle.UPDATE);
	}
	
	/* Create statement for Patterns */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " int NOT NULL PRIMARY KEY, " +
			   theActCol	+ " int NOT NULL " +
			   		"REFERENCES " + TableAccount.idReference() + ", " +
   			   theDateCol	+ " date NOT NULL, " +
  			   theDescCol	+ " varbinary(" + 2*Pattern.DESCLEN + ") NOT NULL, " +
  			   theAmntCol	+ " varbinary(" + 2*EncryptedPair.MONEYLEN + ") NOT NULL, " +
  			   thePartCol	+ " int NOT NULL " +
		   			"REFERENCES " + TableAccount.idReference() + ", " +
		   	   theTrnTypCol	+ " int NOT NULL " +
	   				"REFERENCES " + TableTransactionType.idReference() + ", " +
 			   theIsCrtCol	+ " bit NOT NULL, " +
			   theFreqCol	+ " int NOT NULL " + 
			   		"REFERENCES " + TableFrequency.idReference() + " )";
	}
	
	/* Determine the item name */
	protected String getItemsName() { return theTabName; }

	/* Load statement for Patterns */
	protected String loadStatement() {
		return "select " + theIdCol + "," + theActCol + "," + 
        				theDateCol + "," + theDescCol + "," + 
        				theAmntCol + "," + thePartCol +  "," +
        				theTrnTypCol + "," + theIsCrtCol + "," +
        				theFreqCol + " from " + getTableName() +			
        				" order by " + theActCol + "," + theDateCol;			
	}
	
	/* Load the price */
	protected void loadItem() throws Exception {
		Pattern.List	myList;
		int	    		myId;
		int				myAccountId;
		int  			myPartnerId;
		int  			myTranType;
		int  			myFreq;
		boolean			isCredit;
		byte[] 			myDesc;
		byte[] 			myAmount;
		java.util.Date  myDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getInteger();
			myAccountId = getInteger();
			myDate 		= getDate();
			myDesc    	= getBinary();
			myAmount    = getBinary();
			myPartnerId = getInteger();
			myTranType  = getInteger();
			isCredit    = getBoolean();
			myFreq  	= getInteger();
	
			/* Access the list */
			myList = (Pattern.List)getList();
			
			/* Add into the list */
			myList.addItem(myId, 
			           	   myDate,
				           myDesc,
				           myAmount,
				           myAccountId, 
				           myPartnerId,
				           myTranType,
				           myFreq,
				           isCredit);
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
	
	/* Insert statement for Patterns */
	protected String insertStatement() {
		return "insert into " + getTableName() + 
        			" (" + theIdCol + "," + theActCol + "," +
        			theDateCol + "," + theDescCol + "," + 
        			theAmntCol + "," + thePartCol +  "," +
        			theTrnTypCol + "," + theIsCrtCol + "," +
        			theFreqCol + ") VALUES(?,?,?,?,?,?,?,?,?)";
	}
	
	/* Insert the pattern */
	protected void insertItem(Pattern	pItem) throws Exception {
		
		/* Protect the access */
		try {			
			/* Set the fields */
			setInteger(pItem.getId());
			setInteger(pItem.getAccount().getId());
			setDate(pItem.getDate());
			setBinary(pItem.getDescBytes());
			setBinary(pItem.getAmountBytes());
			setInteger(pItem.getPartner().getId());
			setInteger(pItem.getTransType().getId());
			setBoolean(pItem.isCredit());
			setInteger(pItem.getFrequency().getId());
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
	
	/* Update the pattern */
	protected void updateItem(Pattern	pItem) throws Exception {
		Pattern.Values 	myBase;
		
		/* Access the base */
		myBase = (Pattern.Values)pItem.getBaseObj();
			
		/* Protect the update */
		try {			
			/* Update the fields */
			if (Account.differs(pItem.getAccount(),
				  		  	    myBase.getAccount()))
				updateInteger(theActCol, pItem.getAccount().getId());
			if (Date.differs(pItem.getDate(),
		  		  	  		 myBase.getDate()))
				updateDate(theDateCol, pItem.getDate());
			if (Utils.differs(pItem.getDescBytes(),
						  	  myBase.getDescBytes())) 
				updateBinary(theDescCol, pItem.getDescBytes());
			if (Utils.differs(pItem.getAmountBytes(),
		  		  	  		  myBase.getAmountBytes()))
				updateBinary(theAmntCol, pItem.getAmountBytes());
			if (Account.differs(pItem.getPartner(),
				  	  			myBase.getPartner())) 
				updateInteger(thePartCol, pItem.getPartner().getId());
			if (TransactionType.differs(pItem.getTransType(),
		  		  	  					myBase.getTransType()))
				updateInteger(theTrnTypCol, pItem.getTransType().getId());
			if (pItem.isCredit() != myBase.isCredit()) 
				updateBoolean(theIsCrtCol, pItem.isCredit());
			if (Frequency.differs(pItem.getFrequency(),
		  	  		  			  myBase.getFrequency())) 
				updateInteger(theFreqCol, pItem.getFrequency().getId());
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
