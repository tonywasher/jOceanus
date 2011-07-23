package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;

public class TableAccountType extends TableStaticData<AccountType> {
	/**
	 * The table name
	 */
	protected static final String 	TableName	= AccountType.listName;
	
	/**
	 * The account type list
	 */
	private AccountType.List	theList			= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return AccountType.objName; }
	
	/**
	 * Constructors
	 * @param pDatabase the database control
	 */
	protected TableAccountType(Database	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getAccountTypes();
	}
	
	/* Get the List for the table for updates */
	protected AccountType.List  getUpdateList(DataSet pData) {
		return new AccountType.List(pData.getAccountTypes(), ListStyle.UPDATE);
	}
	
	/* Load the account type */
	protected void loadTheItem(int pId, int pControlId, int pClassId, byte[] pType, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pControlId, pClassId, pType, pDesc);
	}
}
