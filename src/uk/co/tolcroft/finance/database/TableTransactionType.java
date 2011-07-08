package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTransactionType extends TableStaticData<TransactionType> {
	/**
	 * The name of the TransType table
	 */
	private final static String 	theTabName		= TransactionType.listName;
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTransactionType(Database 	pDatabase) { 
		super(pDatabase, theTabName, TransactionType.objName); 
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected TransactionType.List  getLoadList(DataSet pData) {
		return pData.getTransTypes();
	}
	
	/* Get the List for the table for updates */
	protected TransactionType.List  getUpdateList(DataSet pData) {
		return new TransactionType.List(pData.getTransTypes(), ListStyle.UPDATE);
	}
	
	/* Load the transaction type */
	protected void loadTheItem(int pId, int pClassId, byte[] pTrans, byte[] pDesc) throws Exception {
		TransactionType.List	myList;
		
		/* Protect the access */
		try {			
			/* Access the list */
			myList = (TransactionType.List)getList();
			
			/* Add into the list */
			myList.addItem(pId, pClassId, pTrans, pDesc);
		}
								
		catch (Throwable e) {
			throw new Exception(ExceptionClass.SQLSERVER,
					            "Failed to load " + theTabName + " item",
					            e);
		}
		
		/* Return to caller */
		return;
	}
}
