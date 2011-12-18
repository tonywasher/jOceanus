package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

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
	protected TableTaxType(Database<?> 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getTaxTypes();
		setList(theList);
	}

	/* Load the tax type */
	protected void loadTheItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pType, byte[] pDesc) throws ModelException {
		/* Add into the list */
		theList.addItem(pId, pControlId, isEnabled, iOrder, pType, pDesc);
	}
}
