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
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimal;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition.PrometheusSortOrder;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableEncrypted;

/**
 * TableEncrypted extension for DepositRate.
 *
 * @author Tony Washer
 */
public class MoneyWiseTableDepositRate
        extends PrometheusTableEncrypted<MoneyWiseDepositRate> {
    /**
     * The name of the Rates table.
     */
    protected static final String TABLE_NAME = MoneyWiseDepositRate.LIST_NAME;

    /**
     * Constructor.
     *
     * @param pDatabase the database control
     */
    protected MoneyWiseTableDepositRate(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myActCol = myTableDef.addReferenceColumn(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseTableDeposit.TABLE_NAME);
        myTableDef.addEncryptedColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, OceanusDecimal.BYTE_LEN);
        myTableDef.addNullEncryptedColumn(MoneyWiseBasicResource.DEPOSITRATE_BONUS, OceanusDecimal.BYTE_LEN);
        final PrometheusColumnDefinition myDateCol = myTableDef.addNullDateColumn(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(PrometheusSortOrder.DESCENDING);
        myActCol.setSortOrder(PrometheusSortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getDepositRates());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseDepositRate.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicDataType.DEPOSIT, myTableDef.getIntegerValue(MoneyWiseBasicDataType.DEPOSIT));
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE, myTableDef.getBinaryValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE));
        myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS, myTableDef.getBinaryValue(MoneyWiseBasicResource.DEPOSITRATE_BONUS));
        myValues.addValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE, myTableDef.getDateValue(MoneyWiseBasicResource.DEPOSITRATE_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseDepositRate pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (MoneyWiseBasicDataType.DEPOSIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDepositId());
        } else if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_RATE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getRateBytes());
        } else if (MoneyWiseBasicResource.DEPOSITRATE_BONUS.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getBonusBytes());
        } else if (MoneyWiseBasicResource.DEPOSITRATE_ENDDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getEndDate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
