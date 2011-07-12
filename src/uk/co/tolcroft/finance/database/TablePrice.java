package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;

public class TablePrice extends DatabaseTable<AcctPrice> {
	/**
	 * The name of the Prices table
	 */
	protected final static String TableName		= AcctPrice.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The price list
	 */
	private AcctPrice.List	theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TablePrice(Database 	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		theTableDef = pTableDef;
		theTableDef.addReferenceColumn(AcctPrice.FIELD_ACCOUNT, AcctPrice.fieldName(AcctPrice.FIELD_ACCOUNT), TableAccount.TableName);
		theTableDef.addDateColumn(AcctPrice.FIELD_DATE, AcctPrice.fieldName(AcctPrice.FIELD_DATE));
		theTableDef.addEncryptedColumn(AcctPrice.FIELD_PRICE, AcctPrice.fieldName(AcctPrice.FIELD_PRICE), EncryptedPair.PRICELEN);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getPrices();
	}
	
	/* Get the List for the table for updates */
	protected AcctPrice.List  getUpdateList(DataSet pData) {
		return new AcctPrice.List(pData.getPrices(), ListStyle.UPDATE);
	}
	
	/* Load the price */
	protected void loadItem(int pId) throws Exception {
		int  			myAccountId;
		byte[] 			myPrice;
		java.util.Date  myDate;
		
		/* Get the various fields */
		myAccountId = theTableDef.getIntegerValue(AcctPrice.FIELD_ACCOUNT);
		myDate 		= theTableDef.getDateValue(AcctPrice.FIELD_DATE);
		myPrice     = theTableDef.getBinaryValue(AcctPrice.FIELD_PRICE);
	
		/* Add into the list */
		theList.addItem(pId, 
		           	    myDate,
			            myAccountId, 
			            myPrice);
		
		/* Return to caller */
		return;
	}
	
	/* Set a field value */
	protected void setFieldValue(AcctPrice	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case AcctPrice.FIELD_ACCOUNT:	theTableDef.setIntegerValue(AcctPrice.FIELD_ACCOUNT, pItem.getAccount().getId());	break;
			case AcctPrice.FIELD_DATE:		theTableDef.setDateValue(AcctPrice.FIELD_DATE, pItem.getDate());					break;
			case AcctPrice.FIELD_PRICE:		theTableDef.setBinaryValue(AcctPrice.FIELD_PRICE, pItem.getPriceBytes());			break;
		}
	}
}
