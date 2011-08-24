package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

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
	protected TableTaxRegime(Database<?> 	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getTaxRegimes();
		setList(theList);
	}

	
	/* Load the tax regime */
	protected void loadTheItem(int pId, int pControlId, int pClassId, byte[] pRegime, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pControlId, pClassId, pRegime, pDesc);
	}	
}
