package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;

public class TableTransactionType extends TableStaticData<TransactionType> {
	/**
	 * The name of the TransType table
	 */
	protected final static String 	TableName	= TransactionType.listName;
				
	/**
	 * The transaction type list
	 */
	private TransactionType.List	theList 	= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return TransactionType.objName; }
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTransactionType(Database 	pDatabase) { 
		super(pDatabase, TableName); 
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getTransTypes();
	}
	
	/* Get the List for the table for updates */
	protected TransactionType.List  getUpdateList(DataSet pData) {
		return new TransactionType.List(pData.getTransTypes(), ListStyle.UPDATE);
	}
	
	/* Load the transaction type */
	protected void loadTheItem(int pId, int pClassId, byte[] pTrans, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pClassId, pTrans, pDesc);
	}
}
