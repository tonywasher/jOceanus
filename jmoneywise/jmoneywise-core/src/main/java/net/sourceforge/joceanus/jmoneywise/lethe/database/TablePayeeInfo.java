/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.database;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PayeeInfo;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDataInfo;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableDataInfo extension for PayeeInfo.
 * @author Tony Washer
 */
public class TablePayeeInfo
        extends PrometheusTableDataInfo<PayeeInfo, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = PayeeInfo.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePayeeInfo(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME, TableAccountInfoType.TABLE_NAME, TablePayee.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getPayeeInfo());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Build data values */
        return getRowValues(PayeeInfo.OBJECT_NAME);
    }
}
