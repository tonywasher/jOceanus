/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis.TaxBasisList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableStaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableStaticData extension for TaxBucket.
 * @author Tony Washer
 */
public class TableTaxBasis
        extends TableStaticData<TaxBasis, MoneyWiseDataType> {
    /**
     * The name of the TaxBasis table.
     */
    private static final String TABLE_NAME = TaxBasis.LIST_NAME;

    /**
     * The tax basis list.
     */
    private TaxBasisList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTaxBasis(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getTaxBases();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Build data values */
        return getRowValues(TaxBasis.OBJECT_NAME);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Sort the data */
        theList.reSort();

        /* Validate the data */
        theList.validateOnLoad();
    }
}
