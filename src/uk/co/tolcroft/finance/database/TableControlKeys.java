package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.ControlKey;
import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;

public class TableControlKeys extends DatabaseTable<ControlKey> {
	/**
	 * The name of the ControlKeys table
	 */
	protected final static String TableName		= ControlKey.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition 	theTableDef;	/* Set during load */

	/**
	 * The control key list
	 */
	private ControlKey.List	theList 		= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableControlKeys(Database	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		theTableDef = pTableDef;
		theTableDef.addStringColumn(ControlKey.FIELD_KEY, ControlKey.fieldName(ControlKey.FIELD_KEY), ControlKey.CTLLEN);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getControlKeys();
	}
		
	/* Get the List for the table for updates */
	protected ControlKey.List  getUpdateList(DataSet pData) {
		return new ControlKey.List(pData.getControlKeys(), ListStyle.UPDATE);
	}
	
	/* Load the control key */
	protected void loadItem(int pId) throws Exception {
		String			myControl;
		
		/* Get the various fields */
		myControl		= theTableDef.getStringValue(ControlKey.FIELD_KEY);
			
		/* Add into the list */
		theList.addItem(pId, myControl);
	}
	
	/* Set a field value */
	protected void setFieldValue(ControlKey	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case ControlKey.FIELD_KEY:	theTableDef.setStringValue(iField,  pItem.getSecurityKey());		break;
		}
	}
}
