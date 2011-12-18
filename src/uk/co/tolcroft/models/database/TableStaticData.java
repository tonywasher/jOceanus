package uk.co.tolcroft.models.database;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

public abstract class TableStaticData<T extends StaticData<T,?>> extends TableEncrypted<T> {
	/**
	 * The table definition
	 */
	private TableDefinition 		theTableDef;	/* Set during load */

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableStaticData(Database<?> 	pDatabase, 
							  String 		pTabName) {
		super(pDatabase, pTabName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		/* Define Standard table */
		super.defineTable(pTableDef);
		theTableDef = pTableDef;

		/* Define sort column variable */
		ColumnDefinition mySortCol;
		
		/* Define the columns */
		theTableDef.addBooleanColumn(StaticData.FIELD_ENABLED, StaticData.fieldName(StaticData.FIELD_ENABLED));
		mySortCol = theTableDef.addIntegerColumn(StaticData.FIELD_ORDER, StaticData.fieldName(StaticData.FIELD_ORDER));
		theTableDef.addEncryptedColumn(StaticData.FIELD_NAME, getDataName(), StaticData.NAMELEN);
		theTableDef.addNullEncryptedColumn(StaticData.FIELD_DESC, StaticData.fieldName(StaticData.FIELD_DESC), StaticData.DESCLEN);
		
		/* Declare the sort order */
		mySortCol.setSortOrder(SortOrder.ASCENDING);
	}
	
	/* Load the Static Data */
	protected  abstract void loadTheItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pName, byte[] pDesc) throws ModelException;
	
	/* Name the data column */
	protected  abstract String getDataName();
	
	/* Load the static data */
	protected void loadItem(int pId, int pControlId) throws ModelException {
		int		myOrder;
		boolean	myEnabled;
		byte[] 	myType;
		byte[] 	myDesc;
		
		/* Get the various fields */
		myEnabled	= theTableDef.getBooleanValue(StaticData.FIELD_ENABLED);
		myOrder		= theTableDef.getIntegerValue(StaticData.FIELD_ORDER);
		myType 		= theTableDef.getBinaryValue(StaticData.FIELD_NAME);
		myDesc 		= theTableDef.getBinaryValue(StaticData.FIELD_DESC);
			
		/* Add into the list */
		loadTheItem(pId, pControlId, myEnabled, myOrder, myType, myDesc);
		
		/* Return to caller */
		return;
	}
	
	/* Set a field value */
	protected void setFieldValue(T	pItem, int iField) throws ModelException  {
		/* Switch on field id */
		switch (iField) {
			case StaticData.FIELD_ENABLED: 	theTableDef.setBooleanValue(iField, pItem.getEnabled());	break;
			case StaticData.FIELD_ORDER: 	theTableDef.setIntegerValue(iField, pItem.getOrder());		break;
			case StaticData.FIELD_NAME: 	theTableDef.setBinaryValue(iField,  pItem.getNameBytes());	break;
			case StaticData.FIELD_DESC: 	theTableDef.setBinaryValue(iField, pItem.getDescBytes());	break;
			default:						super.setFieldValue(pItem, iField);							break;
		}
	}
}
