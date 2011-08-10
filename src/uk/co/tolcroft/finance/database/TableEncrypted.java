package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.EncryptedItem;
import uk.co.tolcroft.models.Exception;

public abstract class TableEncrypted<T extends EncryptedItem<T>> extends DatabaseTable<T> {
	/**
	 * The table definition
	 */
	private TableDefinition 		theTableDef;	/* Set during load */

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableEncrypted(Database 	pDatabase, 
							 String 	pTabName) {
		super(pDatabase, pTabName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		super.defineTable(pTableDef);
		theTableDef = pTableDef;
		theTableDef.addReferenceColumn(EncryptedItem.FIELD_CONTROL, EncryptedItem.NAME_CTLID, TableControlKeys.TableName);
	}
	
	/**
	 * Load an individual item from the result set
	 * @param pId the Id of the item
	 * @param pControlId the ControlKey id of the item 
	 */
	protected abstract void   	loadItem(int pId, int pControlId)	throws Exception;
	
	/* Load the static data */
	protected void loadItem(int pId) throws Exception {
		int	    						myControlId;
		
		/* Get the various fields */
		myControlId	= theTableDef.getIntegerValue(EncryptedItem.FIELD_CONTROL);
			
		/* Add into the list */
		loadItem(pId, myControlId);
	}
	
	/* Set a field value */
	protected void setFieldValue(T	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case EncryptedItem.FIELD_CONTROL: 	theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());	break;
			default:							super.setFieldValue(pItem, iField);									break;
		}
	}
}
