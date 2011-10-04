package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

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
	protected TableAccountType(Database<FinanceData>	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?,?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getAccountTypes();
		setList(theList);
	}

	/* Load the account type */
	protected void loadTheItem(int pId, int pControlId, boolean isEnabled, byte[] pType, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pControlId, isEnabled, pType, pDesc);
	}
}
