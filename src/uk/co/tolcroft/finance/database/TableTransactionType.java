package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

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
	protected TableTransactionType(Database<?> 	pDatabase) { 
		super(pDatabase, TableName); 
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getTransTypes();
		setList(theList);
	}

	/* Load the transaction type */
	protected void loadTheItem(int pId, int pControlId, int pClassId, byte[] pTrans, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pControlId, pClassId, pTrans, pDesc);
	}
}
