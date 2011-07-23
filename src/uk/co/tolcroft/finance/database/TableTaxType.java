package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;

public class TableTaxType extends TableStaticData<TaxType> {
	/**
	 * The name of the TaxType table
	 */
	private final static String 	theTabName 	= TaxType.listName;
				
	/**
	 * The tax type list
	 */
	private TaxType.List	theList 			= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return TaxType.objName; }
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxType(Database 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getTaxTypes();
	}
		
	/* Get the List for the table for updates */
	protected TaxType.List  getUpdateList(DataSet pData) {
		return new TaxType.List(pData.getTaxTypes(), ListStyle.UPDATE);
	}
	
	/* Load the tax type */
	protected void loadTheItem(int pId, int pControlId, int pClassId, byte[] pType, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pControlId, pClassId, pType, pDesc);
	}
}
