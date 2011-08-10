package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.DataKey;
import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.DataList.ListStyle;

public class TableDataKeys extends DatabaseTable<DataKey> {
	/**
	 * The name of the Static table
	 */
	protected final static String TableName		= DataKey.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition 	theTableDef;	/* Set during load */

	/**
	 * The DataKey data list
	 */
	private DataKey.List	theList 		= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableDataKeys(Database	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		super.defineTable(pTableDef);
		theTableDef = pTableDef;
		theTableDef.addReferenceColumn(DataKey.FIELD_CONTROL, DataKey.fieldName(DataKey.FIELD_CONTROL), TableControlKeys.TableName);
		theTableDef.addIntegerColumn(DataKey.FIELD_KEYTYPE, DataKey.fieldName(DataKey.FIELD_KEYTYPE));
		theTableDef.addBinaryColumn(DataKey.FIELD_KEY, DataKey.fieldName(DataKey.FIELD_KEY), DataKey.KEYLEN);
	}
	
	/* PreProcess on Load */
	protected void preProcessOnLoad(DataSet pData) {
		theList = pData.getDataKeys();
	}
		
	/* Get the List for the table for updates */
	protected DataKey.List  getUpdateList(DataSet pData) {
		return new DataKey.List(pData.getDataKeys(), ListStyle.UPDATE);
	}
	
	/* Load the data key */
	protected void loadItem(int pId) throws Exception {
		int				myControl;
		int	  			myKeyType;
		byte[]			myKey;
		
		/* Get the various fields */
		myControl		= theTableDef.getIntegerValue(DataKey.FIELD_CONTROL);
		myKeyType		= theTableDef.getIntegerValue(DataKey.FIELD_KEYTYPE);
		myKey  			= theTableDef.getBinaryValue(DataKey.FIELD_KEY);
			
		/* Add into the list */
		theList.addItem(pId, myControl, myKeyType, myKey);
	}
	
	/* Set a field value */
	protected void setFieldValue(DataKey	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case DataKey.FIELD_CONTROL:	theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());	break;
			case DataKey.FIELD_KEYTYPE:	theTableDef.setIntegerValue(iField, pItem.getKeyType().getId());	break;
			case DataKey.FIELD_KEY:		theTableDef.setBinaryValue(iField,  pItem.getSecuredKeyDef());		break;
			default:					super.setFieldValue(pItem, iField);									break;
		}
	}
}
