package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;

public class TableControl extends DatabaseTable<ControlData> {
	/**
	 * The name of the Static table
	 */
	protected final static String TableName		= ControlData.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition 	theTableDef;	/* Set during load */

	/**
	 * The control data list
	 */
	private ControlData.List	theList 		= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableControl(Database	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		theTableDef = pTableDef;
		theTableDef.addIntegerColumn(ControlData.FIELD_VERS, ControlData.fieldName(ControlData.FIELD_VERS));
		theTableDef.addStringColumn(ControlData.FIELD_CONTROL, ControlData.fieldName(ControlData.FIELD_CONTROL), ControlData.CTLLEN);
		theTableDef.addBinaryColumn(ControlData.FIELD_KEY, ControlData.fieldName(ControlData.FIELD_KEY), ControlData.KEYLEN);
		theTableDef.addBinaryColumn(ControlData.FIELD_IV, ControlData.fieldName(ControlData.FIELD_IV), ControlData.INITVLEN);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getControl();
	}
		
	/* Get the List for the table for updates */
	protected ControlData.List  getUpdateList(DataSet pData) {
		return new ControlData.List(pData.getControl(), ListStyle.UPDATE);
	}
	
	/* Load the control data */
	protected void loadItem(int pId) throws Exception {
		int	  			myVers;
		String			myControl;
		byte[]			myKey;
		byte[]			myVector;
		
		/* Get the various fields */
		myVers 			= theTableDef.getIntegerValue(ControlData.FIELD_VERS);
		myControl		= theTableDef.getStringValue(ControlData.FIELD_CONTROL);
		myKey  			= theTableDef.getBinaryValue(ControlData.FIELD_KEY);
		myVector		= theTableDef.getBinaryValue(ControlData.FIELD_IV);
			
		/* Add into the list */
		theList.addItem(pId, myVers, myControl, myKey, myVector);
	}
	
	/* Set a field value */
	protected void setFieldValue(ControlData	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case ControlData.FIELD_VERS:	theTableDef.setIntegerValue(iField, pItem.getDataVersion());	break;
			case ControlData.FIELD_CONTROL:	theTableDef.setStringValue(iField,  pItem.getControlKey());		break;
			case ControlData.FIELD_KEY:		theTableDef.setBinaryValue(iField,  pItem.getSecurityKey());	break;
			case ControlData.FIELD_IV:		theTableDef.setBinaryValue(iField,  pItem.getInitVector());		break;
		}
	}
}
