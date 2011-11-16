package uk.co.tolcroft.models.database;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.ControlData;
import uk.co.tolcroft.models.data.DataSet;

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
	protected TableControl(Database<?>	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		/* Define Standard table */
		super.defineTable(pTableDef);
		theTableDef = pTableDef;

		/* Define the columns */
		theTableDef.addIntegerColumn(ControlData.FIELD_VERS, ControlData.fieldName(ControlData.FIELD_VERS));
		theTableDef.addReferenceColumn(ControlData.FIELD_CONTROL, ControlData.fieldName(ControlData.FIELD_CONTROL), TableControlKeys.TableName);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		theList = pData.getControlData();
		setList(theList);
	}
		
	/* Load the control data */
	protected void loadItem(int pId) throws Exception {
		int	  			myVers;
		int				myControl;
		
		/* Get the various fields */
		myVers 			= theTableDef.getIntegerValue(ControlData.FIELD_VERS);
		myControl		= theTableDef.getIntegerValue(ControlData.FIELD_CONTROL);
			
		/* Add into the list */
		theList.addItem(pId, myVers, myControl);
	}
	
	/* Set a field value */
	protected void setFieldValue(ControlData	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case ControlData.FIELD_VERS:	theTableDef.setIntegerValue(iField, pItem.getDataVersion());			break;
			case ControlData.FIELD_CONTROL:	theTableDef.setIntegerValue(iField,  pItem.getControlKey().getId());	break;
			default:						super.setFieldValue(pItem, iField);										break;
		}
	}
}
