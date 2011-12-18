package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.EventInfoType;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

public class TableEventInfoType extends TableStaticData<EventInfoType> {
	/**
	 * The name of the table
	 */
	protected final static String 	TableName	= EventInfoType.listName;
				
	/**
	 * The InfoType list
	 */
	private EventInfoType.List	theList 			= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return EventInfoType.objName; }
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableEventInfoType(Database<?> 	pDatabase) { 
		super(pDatabase, TableName); 
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getInfoTypes();
		setList(theList);
	}

	/* Load the infoType */
	protected void loadTheItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pFreq, byte[] pDesc) throws ModelException {
		/* Add into the list */
		theList.addItem(pId, pControlId, isEnabled, iOrder, pFreq, pDesc);
	}
}
