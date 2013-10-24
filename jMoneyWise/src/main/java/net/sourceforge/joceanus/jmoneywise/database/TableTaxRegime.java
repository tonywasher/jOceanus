/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.database;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.database.Database;
import net.sourceforge.joceanus.jdatamodels.database.TableStaticData;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;

/**
 * TableStaticData extension for TaxRegime.
 * @author Tony Washer
 */
public class TableTaxRegime
        extends TableStaticData<TaxRegime> {
    /**
     * The name of the TaxRegime table.
     */
    protected static final String TABLE_NAME = TaxRegime.LIST_NAME;

    /**
     * The tax regime list.
     */
    private TaxRegimeList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTaxRegime(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getTaxRegimes();
        setList(theList);
    }

    @Override
    protected void loadTheItem(final Integer pId,
                               final Integer pControlId,
                               final Boolean isEnabled,
                               final Integer iOrder,
                               final byte[] pRegime,
                               final byte[] pDesc) throws JDataException {
        /* Add into the list */
        theList.addSecureItem(pId, pControlId, isEnabled, iOrder, pRegime, pDesc);
    }
}
