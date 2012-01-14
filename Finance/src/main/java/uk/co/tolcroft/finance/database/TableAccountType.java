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
package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

public class TableAccountType extends TableStaticData<AccountType> {
	/**
	 * The table name
	 */
	protected static final String 	TableName	= AccountType.listName;
	
	/**
	 * The account type list
	 */
	private AccountType.List	theList			= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return AccountType.objName; }
	
	/**
	 * Constructors
	 * @param pDatabase the database control
	 */
	protected TableAccountType(Database<FinanceData>	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getAccountTypes();
		setList(theList);
	}

	/* Load the account type */
	protected void loadTheItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pType, byte[] pDesc) throws ModelException {
		/* Add into the list */
		theList.addItem(pId, pControlId, isEnabled, iOrder, pType, pDesc);
	}
}
