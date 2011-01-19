package uk.co.tolcroft.finance.database;

import java.sql.SQLException;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TablePattern extends DatabaseTable<Pattern> {
	/**
	 * The name of the Patterns table
	 */
	private final static String theTabName 		= "Patterns";
				
	/**
	 * The name of the Account column
	 */
	private final static String theActCol    	= "Account";

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
	 * The name of the Partner Account column
	 */
	private final static String thePartCol   	= "Partner";

	/**
	 * The name of the TransType column
	 */
	private final static String theTrnTypCol 	= "TransactionType";

	/**
	 * The name of the isCredit flag column
	 */
	private final static String theIsCrtCol  	= "isCredit";

	/**
	 * The name of the Frequency column
	 */
	private final static String theFreqCol   	= "Frequency";
	
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
	
	/**
	 * Determine the Account of the newly loaded item
	 * @return the Account
	 */
	private long getAccount() throws SQLException {
		return getLong();
	}

	/**
	 * Determine the Date of the newly loaded item
	 * @return the Rate
	 */
	protected java.util.Date getDate() throws SQLException {
		return super.getDate();
	}

	/**
	 * Determine the Description of the newly loaded item
	 * @return the Description
	 */
	private String getDesc() throws SQLException {
		return getString();
	}

	/**
	 * Determine the Amount of the newly loaded item
	 * @return the Amount
	 */
	private String getAmount() throws SQLException {
		return getString();
	}

	/**
	 * Determine the Partner Account of the newly loaded item
	 * @return the Partner account
	 */
	private long getPartner() throws SQLException {
		return getLong();
	}

	/**
	 * Determine the TransType of the newly loaded item
	 * @return the TransType
	 */
	private long getTransType() throws SQLException {
		return getLong();
	}

	/**
	 * Determine the isCredit flag of the newly loaded item
	 * @return the isCredit Flag
	 */
	private boolean getIsCredit() throws SQLException {
		return getBoolean();
	}

	/**
	 * Determine the Frequency of the newly loaded item
	 * @return the Frequency
	 */
	private long getFrequency() throws SQLException {
		return getLong();
	}		
	
	/**
	 * Set the Account of the item to be inserted
	 * @param pAccount the account of the item
	 */
	private void setAccount(long pAccount) throws SQLException {
		setLong(pAccount);
	}

	/**
	 * Set the Date of the item to be inserted
	 * @param pDate the Date of the item
	 */
	protected void setDate(Date pDate) throws SQLException {
		super.setDate(pDate);
	}

	/**
	 * Set the Description of the item to be inserted/updated
	 * @param pDesc the description of the item
	 */
	private void setDesc(String pDesc) throws SQLException {
		setString(pDesc);
	}

	/**
	 * Set the Amount of the item to be inserted/updated
	 * @param pAmount the amount of the item
	 */
	private void setAmount(Number.Money pAmount) throws SQLException {
		setString(pAmount.format(false));
	}

	/**
	 * Set the Partner Account of the item to be inserted/updated
	 * @param pPartner the partner account of the item
	 */
	private void setPartner(long pPartner) throws SQLException {
		setLong(pPartner);
	}

	/**
	 * Set the TransType of the item to be inserted/updated
	 * @param pTransType the transtype of the item
	 */
	private void setTransType(long pTransType) throws SQLException {
		setLong(pTransType);
	}

	/**
	 * Set the IsCredit flag of the item to be inserted/updated
	 * @param isCredit the isCredit flag of the item
	 */
	private void setIsCredit(boolean isCredit) throws SQLException {
		setBoolean(isCredit);
	}

	/**
	 * Set the Frequency of the item to be inserted/updated
	 * @param pFrequency the frequency of the item
	 */
	private void setFrequency(long pFrequency) throws SQLException {
		setLong(pFrequency);
	}
	
	/**
	 * Update the Account of the item
	 * @param pValue the new account
	 */
	private void updateAccount(long pValue) {
		updateLong(theActCol, pValue);
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
	private void updateDescription(String pValue) {
		updateString(theDescCol, pValue);
	}

	/**
	 * Update the Amount of the item
	 * @param pValue the new amount
	 */
	private void updateAmount(Number.Money pValue) {
		updateString(theAmntCol, pValue.format(false));
	}	

	/**
	 * Update the Partner Account of the item
	 * @param pValue the new partner account
	 */
	private void updatePartner(long pValue) {
		updateLong(thePartCol, pValue);
	}

	/**
	 * Update the isCredit flag
	 * @param pValue the new flag
	 */
	private void updateIsCredit(boolean isCredit) {
		updateBoolean(theIsCrtCol, isCredit);
	}

	/**
	/**
	 * Update the TransType of the item
	 * @param pValue the new transtype
	 */
	private void updateTransType(long pValue) {
		updateLong(theTrnTypCol, pValue);
	}

	/**
	 * Update the Frequency of the item
	 * @param pValue the new frequency
	 */
	private void updateFrequency(long pValue) {
		updateLong(theFreqCol, pValue);
	}
	
	/* Create statement for Patterns */
	protected String createStatement() {
		return "create table " + theTabName + " ( " +
			   theIdCol 	+ " bigint NOT NULL PRIMARY KEY, " +
			   theActCol	+ " bigint NOT NULL " +
			   		"REFERENCES " + TableAccount.idReference() + ", " +
   			   theDateCol	+ " date NOT NULL, " +
  			   theDescCol	+ " varchar(" + Pattern.DESCLEN + ") NOT NULL, " +
  			   theAmntCol	+ " money NOT NULL, " +
  			   thePartCol	+ " bigint NOT NULL " +
		   			"REFERENCES " + TableAccount.idReference() + ", " +
		   	   theTrnTypCol	+ " bigint NOT NULL " +
	   				"REFERENCES " + TableTransactionType.idReference() + ", " +
 			   theIsCrtCol	+ " bit NOT NULL, " +
			   theFreqCol	+ " bigint NOT NULL " + 
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
		long    		myId;
		long  			myAccountId;
		long  			myPartnerId;
		long  			myTranType;
		long  			myFreq;
		boolean			isCredit;
		String 			myDesc;
		String 			myAmount;
		java.util.Date  myDate;
		
		/* Protect the access */
		try {			
			/* Get the various fields */
			myId        = getID();
			myAccountId = getAccount();
			myDate 		= getDate();
			myDesc    	= getDesc();
			myAmount    = getAmount();
			myPartnerId = getPartner();
			myTranType  = getTransType();
			isCredit    = getIsCredit();
			myFreq  	= getFrequency();
	
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
			setID(pItem.getId());
			setAccount(pItem.getAccount().getId());
			setDate(pItem.getDate());
			setDesc(pItem.getDesc());
			setAmount(pItem.getAmount());
			setPartner(pItem.getPartner().getId());
			setTransType(pItem.getTransType().getId());
			setIsCredit(pItem.isCredit());
			setFrequency(pItem.getFrequency().getId());
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
			if (Utils.differs(pItem.getAccount(),
				  		  	  myBase.getAccount()))
				updateAccount(pItem.getAccount().getId());
			if (Utils.differs(pItem.getDate(),
		  		  	  		  myBase.getDate()))
				updateDate(pItem.getDate());
			if (Utils.differs(pItem.getDesc(),
						  	  myBase.getDesc())) 
				updateDescription(pItem.getDesc());
			if (Utils.differs(pItem.getAmount(),
		  		  	  		  myBase.getAmount()))
				updateAmount(pItem.getAmount());
			if (Utils.differs(pItem.getPartner(),
				  	  		  myBase.getPartner())) 
				updatePartner(pItem.getPartner().getId());
			if (Utils.differs(pItem.getTransType(),
		  		  	  		  myBase.getTransType()))
				updateTransType(pItem.getTransType().getId());
			if (pItem.isCredit() != myBase.isCredit()) 
				updateIsCredit(pItem.isCredit());
			if (Utils.differs(pItem.getFrequency(),
		  	  		  		  myBase.getFrequency())) 
				updateFrequency(pItem.getFrequency().getId());
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
