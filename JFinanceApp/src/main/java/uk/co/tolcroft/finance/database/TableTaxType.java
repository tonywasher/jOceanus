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

public class TableTaxType extends TableStaticData<TaxType> {
	/**
	 * The name of the TaxType table
	 */
	private final static String 	theTabName 	= TaxType.listName;
				
	/**
	 * The tax type list
	 */
	private TaxType.List	theList 			= null;

	/**
	 * Obtain the data column name
	 * @return the data column name
	 */
	protected String getDataName()  { return TaxType.objName; }
	
	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableTaxType(Database<?> 	pDatabase) {
		super(pDatabase, theTabName);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getTaxTypes();
		setList(theList);
	}

	/* Load the tax type */
	protected void loadTheItem(int pId, int pControlId, boolean isEnabled, int iOrder, byte[] pType, byte[] pDesc) throws ModelException {
		/* Add into the list */
		theList.addItem(pId, pControlId, isEnabled, iOrder, pType, pDesc);
	}
}
