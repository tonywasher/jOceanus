/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.database;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseLoanInfo;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusTableDataInfo;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableDataInfo extension for LoanInfo.
 * @author Tony Washer
 */
public class MoneyWiseTableLoanInfo
        extends PrometheusTableDataInfo<MoneyWiseLoanInfo> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = MoneyWiseLoanInfo.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected MoneyWiseTableLoanInfo(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME, MoneyWiseTableAccountInfoType.TABLE_NAME, MoneyWiseTableLoan.TABLE_NAME);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getLoanInfo());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseLoanInfo.OBJECT_NAME);
    }
}
