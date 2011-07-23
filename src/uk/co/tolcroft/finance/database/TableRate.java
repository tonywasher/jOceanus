package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;

public class TableRate extends DatabaseTable<AcctRate> {
	/**
	 * The name of the Rates table
	 */
	protected final static String TableName 	= AcctRate.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef ;	/* Set during load */

	/**
	 * The rate list
	 */
	private AcctRate.List	theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableRate(Database	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		theTableDef = pTableDef;
		theTableDef.addReferenceColumn(EncryptedItem.FIELD_CONTROL, EncryptedItem.NAME_CTLID, TableControlKeys.TableName);
		theTableDef.addReferenceColumn(AcctRate.FIELD_ACCOUNT, AcctRate.fieldName(AcctRate.FIELD_ACCOUNT), TableAccount.TableName);
		theTableDef.addEncryptedColumn(AcctRate.FIELD_RATE, AcctRate.fieldName(AcctRate.FIELD_RATE), EncryptedItem.RATELEN);
		theTableDef.addNullEncryptedColumn(AcctRate.FIELD_BONUS, AcctRate.fieldName(AcctRate.FIELD_BONUS), EncryptedItem.RATELEN);
		theTableDef.addNullDateColumn(AcctRate.FIELD_ENDDATE, AcctRate.fieldName(AcctRate.FIELD_ENDDATE));
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getRates();
	}
	
	/* Get the List for the table for updates */
	protected AcctRate.List  getUpdateList(DataSet pData) {
		return new AcctRate.List(pData.getRates(), ListStyle.UPDATE);
	}
	
	/* Load the rate */
	protected void loadItem(int pId) throws Exception {
		int	    		myControlId;
		int	  			myAccountId;
		byte[]			myRate;
		byte[] 			myBonus;
		java.util.Date  myEndDate;
		
		/* Get the various fields */
		myControlId	= theTableDef.getIntegerValue(EncryptedItem.FIELD_CONTROL);
		myAccountId = theTableDef.getIntegerValue(AcctRate.FIELD_ACCOUNT);
		myRate 		= theTableDef.getBinaryValue(AcctRate.FIELD_RATE);
		myBonus     = theTableDef.getBinaryValue(AcctRate.FIELD_BONUS);
		myEndDate  	= theTableDef.getDateValue(AcctRate.FIELD_ENDDATE);
	
		/* Add into the list */
		theList.addItem(pId,
						myControlId,
			            myAccountId, 
			            myRate,
			            myEndDate, 
			            myBonus);
		
		/* Return to caller */
		return;
	}
	
	/* Set a field value */
	protected void setFieldValue(AcctRate	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case EncryptedItem.FIELD_CONTROL: 	theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());	break;
			case AcctRate.FIELD_ACCOUNT:	theTableDef.setIntegerValue(iField, pItem.getAccount().getId());	break;
			case AcctRate.FIELD_RATE:		theTableDef.setBinaryValue(iField, pItem.getRateBytes());			break;
			case AcctRate.FIELD_BONUS:		theTableDef.setBinaryValue(iField, pItem.getBonusBytes());			break;
			case AcctRate.FIELD_ENDDATE:	theTableDef.setDateValue(iField, pItem.getEndDate());				break;
		}
	}	
}
