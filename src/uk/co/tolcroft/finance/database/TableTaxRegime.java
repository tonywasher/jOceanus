package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.*;

public class TableTaxRegime extends TableStaticData<TaxRegime> {
	/**
	 * The name of the TaxRegime table
	 */
	protected final static String 	TableName 	= TaxRegime.listName;
				
	/**
	 * The frequency list
	 */
	private TaxRegime.List	theList 			= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return TaxRegime.objName; }
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxRegime(Database 	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getTaxRegimes();
	}
	
	/* Get the List for the table for updates */
	protected TaxRegime.List  getUpdateList(DataSet pData) {
		return new TaxRegime.List(pData.getTaxRegimes(), ListStyle.UPDATE);
	}
	
	/* Load the tax regime */
	protected void loadTheItem(int pId, int pClassId, byte[] pRegime, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pClassId, pRegime, pDesc);
	}	
}
