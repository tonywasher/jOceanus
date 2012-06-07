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

import net.sourceforge.JDataManager.JDataException;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TaxRegime;
import uk.co.tolcroft.finance.data.TaxRegime.TaxRegimeList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableStaticData;

public class TableTaxRegime extends TableStaticData<TaxRegime> {
    /**
     * The name of the TaxRegime table
     */
    protected final static String TableName = TaxRegime.LIST_NAME;

    /**
     * The frequency list
     */
    private TaxRegimeList theList = null;

    /**
     * Constructor
     * @param pDatabase the database control
     */
    protected TableTaxRegime(Database<?> pDatabase) {
        super(pDatabase, TableName);
    }

    /* Declare DataSet */
    @Override
    protected void declareData(DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getTaxRegimes();
        setList(theList);
    }

    /* Load the tax regime */
    @Override
    protected void loadTheItem(int pId,
                               int pControlId,
                               boolean isEnabled,
                               int iOrder,
                               byte[] pRegime,
                               byte[] pDesc) throws JDataException {
        /* Add into the list */
        theList.addItem(pId, pControlId, isEnabled, iOrder, pRegime, pDesc);
    }
}
