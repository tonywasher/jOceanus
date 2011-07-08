package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.finance.data.Frequency;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class TableFrequency extends TableStaticData<Frequency> {
	/**
	 * The name of the table
	 */
	private final static String 	theTabName		= Frequency.listName;
				
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableFrequency(Database 	pDatabase) { 
		super(pDatabase, theTabName, Frequency.objName); 
	}
	
	/* The Id for reference */
	protected static String idReference() {
		return theTabName +  "(" + theIdCol + ")";
	}
	
	/* Get the List for the table for loading */
	protected Frequency.List  getLoadList(DataSet pData) {
		return pData.getFrequencys();
	}
	
	/* Get the List for the table for updates */
	protected Frequency.List  getUpdateList(DataSet pData) {
		return new Frequency.List(pData.getFrequencys(), ListStyle.UPDATE);
	}	

	/* Load the frequency */
	protected void loadTheItem(int pId, int pClassId, byte[] pFreq, byte[] pDesc) throws Exception {
		Frequency.List	myList;
		
		/* Protect the access */
		try {			
			/* Access the list */
			myList = (Frequency.List)getList();
			
			/* Add into the list */
			myList.addItem(pId, pClassId, pFreq, pDesc);
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
