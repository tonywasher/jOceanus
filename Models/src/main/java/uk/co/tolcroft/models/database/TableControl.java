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
	@Override
	protected void defineTable(TableDefinition	pTableDef) {
		/* Define Standard table */
		super.defineTable(pTableDef);
		theTableDef = pTableDef;

		/* Define the columns */
		theTableDef.addIntegerColumn(ControlData.FIELD_VERS, ControlData.fieldName(ControlData.FIELD_VERS));
		theTableDef.addReferenceColumn(ControlData.FIELD_CONTROL, ControlData.fieldName(ControlData.FIELD_CONTROL), TableControlKeys.TableName);
	}
	
	@Override
	protected void declareData(DataSet<?> pData) {
		theList = pData.getControlData();
		setList(theList);
	}
		
	@Override
	protected void loadItem(int pId) throws ModelException {
		int	  			myVers;
		int				myControl;
		
		/* Get the various fields */
		myVers 			= theTableDef.getIntegerValue(ControlData.FIELD_VERS);
		myControl		= theTableDef.getIntegerValue(ControlData.FIELD_CONTROL);
			
		/* Add into the list */
		theList.addItem(pId, myVers, myControl);
	}
	
	@Override
	protected void setFieldValue(ControlData	pItem, int iField) throws ModelException  {
		/* Switch on field id */
		switch (iField) {
			case ControlData.FIELD_VERS:	theTableDef.setIntegerValue(iField, pItem.getDataVersion());			break;
			case ControlData.FIELD_CONTROL:	theTableDef.setIntegerValue(iField,  pItem.getControlKey().getId());	break;
			default:						super.setFieldValue(pItem, iField);										break;
		}
	}
}
