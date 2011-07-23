package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;

public class TablePattern extends DatabaseTable<Pattern> {
	/**
	 * The name of the Patterns table
	 */
	protected final static String TableName		= Pattern.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The pattern list
	 */
	private Pattern.List	theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TablePattern(Database	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		theTableDef = pTableDef;
		theTableDef.addReferenceColumn(EncryptedItem.FIELD_CONTROL, EncryptedItem.NAME_CTLID, TableControlKeys.TableName);
		theTableDef.addReferenceColumn(Pattern.FIELD_ACCOUNT, Pattern.fieldName(Pattern.FIELD_ACCOUNT), TableAccount.TableName);
		theTableDef.addDateColumn(Pattern.FIELD_DATE, Pattern.fieldName(Pattern.FIELD_DATE));
		theTableDef.addEncryptedColumn(Pattern.FIELD_DESC, Pattern.fieldName(Pattern.FIELD_DESC), Pattern.DESCLEN);
		theTableDef.addEncryptedColumn(Pattern.FIELD_AMOUNT, Pattern.fieldName(Pattern.FIELD_AMOUNT), EncryptedItem.MONEYLEN);
		theTableDef.addReferenceColumn(Pattern.FIELD_PARTNER, Pattern.fieldName(Pattern.FIELD_PARTNER), TableAccount.TableName);
		theTableDef.addReferenceColumn(Pattern.FIELD_TRNTYP, Pattern.fieldName(Pattern.FIELD_TRNTYP), TableTransactionType.TableName);
		theTableDef.addBooleanColumn(Pattern.FIELD_CREDIT, Pattern.fieldName(Pattern.FIELD_CREDIT));
		theTableDef.addReferenceColumn(Pattern.FIELD_FREQ, Pattern.fieldName(Pattern.FIELD_FREQ), TableFrequency.TableName);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getPatterns();
	}
	
	/* Get the List for the table for updates */
	protected Pattern.List  getUpdateList(DataSet pData) {
		return new Pattern.List(pData.getPatterns(), ListStyle.UPDATE);
	}
	
	/**
	 * postProcess on Load
	 */
	protected void postProcessOnLoad(DataSet pData) throws Exception {
		Account.List myAccounts = pData.getAccounts();
		myAccounts.validateLoadedAccounts();
	}
	
	/* Load the pattern */
	protected void loadItem(int pId) throws Exception {
		int	    		myControlId;
		int				myAccountId;
		int  			myPartnerId;
		int  			myTranType;
		int  			myFreq;
		boolean			isCredit;
		byte[] 			myDesc;
		byte[] 			myAmount;
		java.util.Date  myDate;
		
		/* Get the various fields */
		myControlId	= theTableDef.getIntegerValue(EncryptedItem.FIELD_CONTROL);
		myAccountId = theTableDef.getIntegerValue(Pattern.FIELD_ACCOUNT);
		myDate 		= theTableDef.getDateValue(Pattern.FIELD_DATE);
		myDesc    	= theTableDef.getBinaryValue(Pattern.FIELD_DESC);
		myAmount    = theTableDef.getBinaryValue(Pattern.FIELD_AMOUNT);
		myPartnerId = theTableDef.getIntegerValue(Pattern.FIELD_PARTNER);
		myTranType  = theTableDef.getIntegerValue(Pattern.FIELD_TRNTYP);
		isCredit    = theTableDef.getBooleanValue(Pattern.FIELD_CREDIT);
		myFreq  	= theTableDef.getIntegerValue(Pattern.FIELD_FREQ);
	
		/* Add into the list */
		theList.addItem(pId,
						myControlId,
		           	    myDate,
			            myDesc,
			            myAmount,
			            myAccountId, 
			            myPartnerId,
			            myTranType,
			            myFreq,
			            isCredit);
		
		/* Return to caller */
		return;
	}
	
	/* Set a field value */
	protected void setFieldValue(Pattern	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case EncryptedItem.FIELD_CONTROL: 	theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());	break;
			case Pattern.FIELD_ACCOUNT:	theTableDef.setIntegerValue(Pattern.FIELD_ACCOUNT, pItem.getAccount().getId());		break;
			case Pattern.FIELD_DATE:	theTableDef.setDateValue(Pattern.FIELD_DATE, pItem.getDate());						break;
			case Pattern.FIELD_DESC:	theTableDef.setBinaryValue(Pattern.FIELD_DESC, pItem.getDescBytes());				break;
			case Pattern.FIELD_AMOUNT:	theTableDef.setBinaryValue(Pattern.FIELD_AMOUNT, pItem.getAmountBytes());			break;
			case Pattern.FIELD_PARTNER:	theTableDef.setIntegerValue(Pattern.FIELD_PARTNER, pItem.getPartner().getId());		break;
			case Pattern.FIELD_TRNTYP:	theTableDef.setIntegerValue(Pattern.FIELD_TRNTYP, pItem.getTransType().getId());	break;
			case Pattern.FIELD_CREDIT:	theTableDef.setBooleanValue(Pattern.FIELD_CREDIT, pItem.isCredit());				break;
			case Pattern.FIELD_FREQ:	theTableDef.setIntegerValue(Pattern.FIELD_FREQ, pItem.getFrequency().getId());		break;
		}
	}
}
