/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.database;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCashCategoryType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusDataStore;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusTableStaticData;

/**
 * TableStaticData extension for CashCategoryType.
 *
 * @author Tony Washer
 */
public class MoneyWiseTableCashCategoryType
        extends PrometheusTableStaticData<MoneyWiseCashCategoryType> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = MoneyWiseCashCategoryType.LIST_NAME;

    /**
     * Constructors.
     *
     * @param pDatabase the database control
     */
    protected MoneyWiseTableCashCategoryType(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getCashCategoryTypes());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseCashCategoryType.OBJECT_NAME);
    }
}
