package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;

public class TableAccount extends TableEncrypted<Account> {
	/**
	 * The name of the Account table
	 */
	protected final static String TableName		= Account.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The account list
	 */
	private Account.List	theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableAccount(Database 	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		super.defineTable(pTableDef);
		theTableDef = pTableDef;
		theTableDef.addEncryptedColumn(Account.FIELD_NAME, Account.fieldName(Account.FIELD_NAME), Account.NAMELEN);
		theTableDef.addReferenceColumn(Account.FIELD_TYPE, Account.fieldName(Account.FIELD_TYPE), TableAccountType.TableName);
		theTableDef.addNullEncryptedColumn(Account.FIELD_DESC, Account.fieldName(Account.FIELD_DESC), Account.DESCLEN);
		theTableDef.addNullDateColumn(Account.FIELD_MATURITY, Account.fieldName(Account.FIELD_MATURITY));
		theTableDef.addNullDateColumn(Account.FIELD_CLOSE, Account.fieldName(Account.FIELD_CLOSE));
		theTableDef.addNullReferenceColumn(Account.FIELD_PARENT, Account.fieldName(Account.FIELD_PARENT), TableName);
		theTableDef.addNullReferenceColumn(Account.FIELD_ALIAS, Account.fieldName(Account.FIELD_ALIAS), TableName);
		theTableDef.addNullEncryptedColumn(Account.FIELD_WEBSITE, Account.fieldName(Account.FIELD_WEBSITE), Account.WSITELEN);
		theTableDef.addNullEncryptedColumn(Account.FIELD_CUSTNO, Account.fieldName(Account.FIELD_CUSTNO), Account.CUSTLEN);
		theTableDef.addNullEncryptedColumn(Account.FIELD_USERID, Account.fieldName(Account.FIELD_USERID), Account.UIDLEN);
		theTableDef.addNullEncryptedColumn(Account.FIELD_PASSWORD, Account.fieldName(Account.FIELD_PASSWORD), Account.PWDLEN);
		theTableDef.addNullEncryptedColumn(Account.FIELD_ACCOUNT, Account.fieldName(Account.FIELD_ACCOUNT), Account.ACTLEN);
		theTableDef.addNullEncryptedColumn(Account.FIELD_NOTES, Account.fieldName(Account.FIELD_NOTES), Account.NOTELEN);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getAccounts();
	}
	
	/* Get the List for the table for updates */
	protected Account.List  getUpdateList(DataSet pData) {
		return new Account.List(pData.getAccounts(), ListStyle.UPDATE);
	}
	
	/* Load the account */
	protected void loadItem(int pId, int pControlId) throws Exception {
		byte[]  		myName;
		int    			myActTypeId;
		Integer	   		myParentId;
		Integer    		myAliasId;
		byte[]  		myDesc;
		java.util.Date  myMaturity;
		java.util.Date  myClosed;
		byte[]     		myWebSite;
		byte[]     		myCustNo;
		byte[]     		myUserId;
		byte[]     		myPassword;
		byte[]     		myAccount;
		byte[]     		myNotes;
		
		/* Get the various fields */
		myName   		= theTableDef.getBinaryValue(Account.FIELD_NAME);
		myActTypeId 	= theTableDef.getIntegerValue(Account.FIELD_TYPE);
		myDesc      	= theTableDef.getBinaryValue(Account.FIELD_DESC);
		myMaturity  	= theTableDef.getDateValue(Account.FIELD_MATURITY);
		myClosed    	= theTableDef.getDateValue(Account.FIELD_CLOSE);
		myParentId		= theTableDef.getIntegerValue(Account.FIELD_PARENT);
		myAliasId		= theTableDef.getIntegerValue(Account.FIELD_ALIAS);
		myWebSite		= theTableDef.getBinaryValue(Account.FIELD_WEBSITE);
		myCustNo		= theTableDef.getBinaryValue(Account.FIELD_CUSTNO);
		myUserId		= theTableDef.getBinaryValue(Account.FIELD_USERID);
		myPassword		= theTableDef.getBinaryValue(Account.FIELD_PASSWORD);
		myAccount		= theTableDef.getBinaryValue(Account.FIELD_ACCOUNT);
		myNotes			= theTableDef.getBinaryValue(Account.FIELD_NOTES);
	
		/* Add into the list */
		theList.addItem(pId,
						pControlId,
			            myName, 
				        myActTypeId,
				        myDesc, 
				        myMaturity,
				        myClosed,
				        myParentId,
				        myAliasId,
					    myWebSite,
					    myCustNo,
					    myUserId,
					    myPassword,
					    myAccount,
					    myNotes);
	}
	
	/* Set a field value */
	protected void setFieldValue(Account	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case Account.FIELD_NAME:		theTableDef.setBinaryValue(iField, pItem.getNameBytes());			break;
			case Account.FIELD_TYPE:		theTableDef.setIntegerValue(iField, pItem.getActType().getId());	break;
			case Account.FIELD_DESC:		theTableDef.setBinaryValue(iField, pItem.getDescBytes());			break;
			case Account.FIELD_MATURITY:	theTableDef.setDateValue(iField, pItem.getMaturity());				break;
			case Account.FIELD_CLOSE:		theTableDef.setDateValue(iField, pItem.getClose());					break;
			case Account.FIELD_PARENT:		theTableDef.setIntegerValue(iField, (pItem.getParent() != null)
															? pItem.getParent().getId() : null);				break;
			case Account.FIELD_ALIAS:		theTableDef.setIntegerValue(iField, (pItem.getAlias() != null)
															? pItem.getAlias().getId() : null);					break;
			case Account.FIELD_WEBSITE:		theTableDef.setBinaryValue(iField, pItem.getWebSiteBytes());		break;
			case Account.FIELD_CUSTNO:		theTableDef.setBinaryValue(iField, pItem.getCustNoBytes());			break;
			case Account.FIELD_USERID:		theTableDef.setBinaryValue(iField, pItem.getUserIdBytes());			break;
			case Account.FIELD_PASSWORD:	theTableDef.setBinaryValue(iField, pItem.getPasswordBytes());		break;
			case Account.FIELD_ACCOUNT:		theTableDef.setBinaryValue(iField, pItem.getAccountBytes());		break;
			case Account.FIELD_NOTES:		theTableDef.setBinaryValue(iField, pItem.getNotesBytes());			break;
			default:						super.setFieldValue(pItem, iField);									break;
		}
	}	
}
