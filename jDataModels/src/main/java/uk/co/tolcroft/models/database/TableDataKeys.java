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
import uk.co.tolcroft.models.data.DataKey;
import uk.co.tolcroft.models.data.DataSet;

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
	protected TableDataKeys(Database<?>	pDatabase) {
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
		theTableDef.addReferenceColumn(DataKey.FIELD_CONTROL, DataKey.fieldName(DataKey.FIELD_CONTROL), TableControlKeys.TableName);
		theTableDef.addIntegerColumn(DataKey.FIELD_KEYTYPE, DataKey.fieldName(DataKey.FIELD_KEYTYPE));
		theTableDef.addBinaryColumn(DataKey.FIELD_KEY, DataKey.fieldName(DataKey.FIELD_KEY), DataKey.KEYLEN);
	}
	
	@Override
	protected void declareData(DataSet<?> pData) {
		theList = pData.getDataKeys();
		setList(theList);
	}
	
	@Override
	protected void loadItem(int pId) throws ModelException {
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
	
	@Override
	protected void setFieldValue(DataKey	pItem, int iField) throws ModelException  {
		/* Switch on field id */
		switch (iField) {
			case DataKey.FIELD_CONTROL:	theTableDef.setIntegerValue(iField, pItem.getControlKey().getId());	break;
			case DataKey.FIELD_KEYTYPE:	theTableDef.setIntegerValue(iField, pItem.getKeyType().getId());	break;
			case DataKey.FIELD_KEY:		theTableDef.setBinaryValue(iField,  pItem.getSecuredKeyDef());		break;
			default:					super.setFieldValue(pItem, iField);									break;
		}
	}
}
