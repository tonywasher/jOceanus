package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.Frequency;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

public class TableFrequency extends TableStaticData<Frequency> {
	/**
	 * The name of the table
	 */
	protected final static String 	TableName	= Frequency.listName;
				
	/**
	 * The frequency list
	 */
	private Frequency.List	theList 			= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return Frequency.objName; }
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableFrequency(Database<?> 	pDatabase) { 
		super(pDatabase, TableName); 
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?,?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getFrequencys();
		setList(theList);
	}

	/* Load the frequency */
	protected void loadTheItem(int pId, int pControlId, boolean isEnabled, byte[] pFreq, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pControlId, isEnabled, pFreq, pDesc);
	}
}