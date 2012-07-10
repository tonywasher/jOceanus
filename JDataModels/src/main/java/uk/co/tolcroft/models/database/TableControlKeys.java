/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.database;

import uk.co.tolcroft.models.ModelException;
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
	@Override
	protected void defineTable(TableDefinition	pTableDef) {
		/* Define Standard table */
		super.defineTable(pTableDef);
		theTableDef = pTableDef;

		/* Define the columns */
		theTableDef.addBinaryColumn(ControlKey.FIELD_PASSHASH,   ControlKey.fieldName(ControlKey.FIELD_PASSHASH), ControlKey.HASHLEN);
		theTableDef.addIntegerColumn(ControlKey.FIELD_KEYMODE,   ControlKey.fieldName(ControlKey.FIELD_KEYMODE));
		theTableDef.addBinaryColumn(ControlKey.FIELD_PUBLICKEY,  ControlKey.fieldName(ControlKey.FIELD_PUBLICKEY), ControlKey.PUBLICLEN);
		theTableDef.addBinaryColumn(ControlKey.FIELD_PRIVATEKEY, ControlKey.fieldName(ControlKey.FIELD_PRIVATEKEY), ControlKey.PRIVATELEN);
	}
	
	@Override
	protected void declareData(DataSet<?> pData) {
		theList = pData.getControlKeys();
		setList(theList);
	}

	@Override
	protected void loadItem(int pId) throws ModelException {
		byte[]	myHash;
		byte[]	myPublic;
		byte[]	myPrivate;
		int		myType;
		
		/* Get the various fields */
		myHash		= theTableDef.getBinaryValue(ControlKey.FIELD_PASSHASH);
		myType		= theTableDef.getIntegerValue(ControlKey.FIELD_KEYMODE);
		myPrivate	= theTableDef.getBinaryValue(ControlKey.FIELD_PRIVATEKEY);
		myPublic	= theTableDef.getBinaryValue(ControlKey.FIELD_PUBLICKEY);
			
		/* Add into the list */
		theList.addItem(pId, myType, myHash, myPublic, myPrivate);
	}
	
	@Override
	protected void setFieldValue(ControlKey	pItem, int iField) throws ModelException  {
		/* Switch on field id */
		switch (iField) {
			case ControlKey.FIELD_KEYMODE:		theTableDef.setIntegerValue(iField,  pItem.getKeyMode().getMode());	break;
			case ControlKey.FIELD_PASSHASH:		theTableDef.setBinaryValue(iField,  pItem.getPasswordHash());		break;
			case ControlKey.FIELD_PUBLICKEY:	theTableDef.setBinaryValue(iField,  pItem.getPublicKey());			break;
			case ControlKey.FIELD_PRIVATEKEY:	theTableDef.setBinaryValue(iField,  pItem.getPrivateKey());			break;
			default:							super.setFieldValue(pItem, iField);									break;
		}
	}
}
