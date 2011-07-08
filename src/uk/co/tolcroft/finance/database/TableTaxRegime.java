package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableTaxRegime extends TableStaticData<TaxRegime> {
	/**
	 * The name of the TaxRegime table
	 */
	private final static String 	theTabName 		= TaxRegime.listName;
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxRegime(Database 	pDatabase) {
		super(pDatabase, theTabName, TaxRegime.objName);
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected TaxRegime.List  getLoadList(DataSet pData) {
		return pData.getTaxRegimes();
	}
	
	/* Get the List for the table for updates */
	protected TaxRegime.List  getUpdateList(DataSet pData) {
		return new TaxRegime.List(pData.getTaxRegimes(), ListStyle.UPDATE);
	}
	
	/* Load the tax regime */
	protected void loadTheItem(int pId, int pClassId, byte[] pRegime, byte[] pDesc) throws Exception {
		TaxRegime.List	myList;
		
		/* Protect the access */
		try {			
			/* Access the list */
			myList = (TaxRegime.List)getList();
			
			/* Add into the list */
			myList.addItem(pId, pClassId, pRegime, pDesc);
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
