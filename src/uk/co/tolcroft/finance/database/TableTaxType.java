package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTaxType extends TableStaticData<TaxType> {
	/**
	 * The name of the TaxType table
	 */
	private final static String 	theTabName 		= TaxType.listName;
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxType(Database 	pDatabase) {
		super(pDatabase, theTabName, TaxType.objName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected TaxType.List  getLoadList(DataSet pData) {
		return pData.getTaxTypes();
	}
	
	/* Get the List for the table for updates */
	protected TaxType.List  getUpdateList(DataSet pData) {
		return new TaxType.List(pData.getTaxTypes(), ListStyle.UPDATE);
	}
	
	/* Load the tax type */
	protected void loadTheItem(int pId, int pClassId, byte[] pType, byte[] pDesc) throws Exception {
		TaxType.List	myList;
		
		/* Protect the access */
		try {			
			/* Access the list */
			myList = (TaxType.List)getList();
			
			/* Add into the list */
			myList.addItem(pId, pClassId, pType, pDesc);
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
