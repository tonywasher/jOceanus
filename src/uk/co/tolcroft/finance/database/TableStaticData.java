package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.EncryptedItem;
import uk.co.tolcroft.finance.data.StaticClass;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;

public abstract class TableStaticData<T extends StaticData<T,?>> extends DatabaseTable<T> {
	/**
	 * The table definition
	 */
	private TableDefinition 		theTableDef;	/* Set during load */

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableStaticData(Database 	pDatabase, 
							  String 	pTabName) {
		super(pDatabase, pTabName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		theTableDef = pTableDef;
		theTableDef.addReferenceColumn(EncryptedItem.FIELD_CONTROL, EncryptedItem.NAME_CTLID, TableControlKeys.TableName);
		theTableDef.addIntegerColumn(StaticData.FIELD_CLASSID, StaticData.fieldName(StaticData.FIELD_CLASSID));
		theTableDef.addEncryptedColumn(StaticData.FIELD_NAME, getDataName(), StaticClass.NAMELEN);
		theTableDef.addNullEncryptedColumn(StaticData.FIELD_DESC, StaticData.fieldName(StaticData.FIELD_DESC), StaticClass.DESCLEN);
	}
	
	/* Load the Static Data */
	protected  abstract void loadTheItem(int pId, int pControlId, int pClassId, byte[] pName, byte[] pDesc) throws Exception;
	
	/* Name the data column */
	protected  abstract String getDataName();
	
	/* Load the static data */
	protected void loadItem(int pId) throws Exception {
		int	    						myControlId;
		int	    						myClassId;
		byte[]  						myType;
		byte[]  						myDesc;
		
		/* Get the various fields */
		myControlId	= theTableDef.getIntegerValue(EncryptedItem.FIELD_CONTROL);
		myClassId	= theTableDef.getIntegerValue(StaticData.FIELD_CLASSID);
		myType 		= theTableDef.getBinaryValue(StaticData.FIELD_NAME);
		myDesc 		= theTableDef.getBinaryValue(StaticData.FIELD_DESC);
			
		/* Add into the list */
		loadTheItem(pId, myControlId, myClassId, myType, myDesc);
		
		/* Return to caller */
		return;
	}
	
	/* Set a field value */
	protected void setFieldValue(T	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case EncryptedItem.FIELD_CONTROL: 	theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());	break;
			case StaticData.FIELD_CLASSID: 		theTableDef.setIntegerValue(iField, pItem.getStaticClassId());		break;
			case StaticData.FIELD_NAME: 		theTableDef.setBinaryValue(iField,  pItem.getNameBytes());			break;
			case StaticData.FIELD_DESC: 		theTableDef.setBinaryValue(iField, pItem.getDescBytes());			break;
		}
	}
}
