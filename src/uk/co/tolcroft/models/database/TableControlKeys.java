package uk.co.tolcroft.models.database;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.ControlKey;
import uk.co.tolcroft.models.data.DataSet;

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
	protected TableControlKeys(Database<?>	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		super.defineTable(pTableDef);
		theTableDef = pTableDef;
		theTableDef.addBinaryColumn(ControlKey.FIELD_PASSHASH,   ControlKey.fieldName(ControlKey.FIELD_PASSHASH), ControlKey.HASHLEN);
		theTableDef.addIntegerColumn(ControlKey.FIELD_KEYTYPE,   ControlKey.fieldName(ControlKey.FIELD_KEYTYPE));
		theTableDef.addIntegerColumn(ControlKey.FIELD_NUMSTEPS,  ControlKey.fieldName(ControlKey.FIELD_NUMSTEPS));
		theTableDef.addBinaryColumn(ControlKey.FIELD_PUBLICKEY,  ControlKey.fieldName(ControlKey.FIELD_PUBLICKEY), ControlKey.PUBLICLEN);
		theTableDef.addBinaryColumn(ControlKey.FIELD_PRIVATEKEY, ControlKey.fieldName(ControlKey.FIELD_PRIVATEKEY), ControlKey.PRIVATELEN);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		theList = pData.getControlKeys();
		setList(theList);
	}

	/* Load the control key */
	protected void loadItem(int pId) throws Exception {
		byte[]	myHash;
		byte[]	myPublic;
		byte[]	myPrivate;
		int		myType;
		int		mySteps;
		
		/* Get the various fields */
		myHash		= theTableDef.getBinaryValue(ControlKey.FIELD_PASSHASH);
		myType		= theTableDef.getIntegerValue(ControlKey.FIELD_KEYTYPE);
		mySteps		= theTableDef.getIntegerValue(ControlKey.FIELD_NUMSTEPS);
		myPrivate	= theTableDef.getBinaryValue(ControlKey.FIELD_PRIVATEKEY);
		myPublic	= theTableDef.getBinaryValue(ControlKey.FIELD_PUBLICKEY);
			
		/* Add into the list */
		theList.addItem(pId, myType, mySteps, myHash, myPublic, myPrivate);
	}
	
	/* Set a field value */
	protected void setFieldValue(ControlKey	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case ControlKey.FIELD_KEYTYPE:		theTableDef.setIntegerValue(iField,  pItem.getKeyType().getId());	break;
			case ControlKey.FIELD_NUMSTEPS:		theTableDef.setIntegerValue(iField,  pItem.getNumSteps());			break;
			case ControlKey.FIELD_PASSHASH:		theTableDef.setBinaryValue(iField,  pItem.getPasswordHash());		break;
			case ControlKey.FIELD_PUBLICKEY:	theTableDef.setBinaryValue(iField,  pItem.getPublicKey());			break;
			case ControlKey.FIELD_PRIVATEKEY:	theTableDef.setBinaryValue(iField,  pItem.getPrivateKey());			break;
			default:							super.setFieldValue(pItem, iField);									break;
		}
	}
}
