package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableAccountType extends TableStaticData<AccountType> {
	/**
	 * The table name
	 */
	private static final String 	theTabName	= AccountType.listName;
	
	/**
	 * Constructors
	 * @param pDatabase the database control
	 */
	protected TableAccountType(Database	pDatabase) {
		super(pDatabase, theTabName, AccountType.objName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected AccountType.List  getLoadList(DataSet pData) {
		return pData.getAccountTypes();
	}
	
	/* Get the List for the table for updates */
	protected AccountType.List  getUpdateList(DataSet pData) {
		return new AccountType.List(pData.getAccountTypes(), ListStyle.UPDATE);
	}
	
	/* Load the account type */
	protected void loadTheItem(int pId, int pClassId, byte[] pType, byte[] pDesc) throws Exception {
		AccountType.List	myList;
		
		/* Protect the access */
		try {			
			/* Access the list */
			myList = (AccountType.List)getList();
			
			/* Add into the list */
			myList.addItem(pId, pClassId, pType, pDesc);
		}
								
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + getTableName() + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
}
