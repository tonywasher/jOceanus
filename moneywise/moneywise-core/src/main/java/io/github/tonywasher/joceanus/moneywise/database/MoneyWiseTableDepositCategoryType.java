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
package io.github.tonywasher.joceanus.moneywise.database;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryType;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusDataStore;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusTableStaticData;

/**
 * TableStaticData extension for DepositCategoryType.
 *
 * @author Tony Washer
 */
public class MoneyWiseTableDepositCategoryType
        extends PrometheusTableStaticData<MoneyWiseDepositCategoryType> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = MoneyWiseDepositCategoryType.LIST_NAME;

    /**
     * Constructors.
     *
     * @param pDatabase the database control
     */
    protected MoneyWiseTableDepositCategoryType(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getDepositCategoryTypes());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWiseDepositCategoryType.OBJECT_NAME);
    }
}
