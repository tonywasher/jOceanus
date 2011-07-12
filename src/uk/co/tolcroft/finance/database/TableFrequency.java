package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.finance.data.Frequency;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;

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
	protected TableFrequency(Database 	pDatabase) { 
		super(pDatabase, TableName); 
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getFrequencys();
	}
	
	/* Get the List for the table for updates */
	protected Frequency.List  getUpdateList(DataSet pData) {
		return new Frequency.List(pData.getFrequencys(), ListStyle.UPDATE);
	}	

	/* Load the frequency */
	protected void loadTheItem(int pId, int pClassId, byte[] pFreq, byte[] pDesc) throws Exception {
		/* Add into the list */
		theList.addItem(pId, pClassId, pFreq, pDesc);
	}
}