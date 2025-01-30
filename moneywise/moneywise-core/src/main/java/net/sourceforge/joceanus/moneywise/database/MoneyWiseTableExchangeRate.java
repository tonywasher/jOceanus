/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.database;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDataItem;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition.PrometheusSortOrder;

/**
 * TableEncrypted extension for ExchangeRate.
 * @author Tony Washer
 */
public class MoneyWiseTableExchangeRate
        extends PrometheusTableDataItem<MoneyWiseExchangeRate> {
    /**
     * The name of the ExchangeRate table.
     */
    protected static final String TABLE_NAME = MoneyWiseExchangeRate.LIST_NAME;

    /**
     * The formatter.
     */
    private OceanusDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected MoneyWiseTableExchangeRate(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myDateCol = myTableDef.addDateColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        final PrometheusColumnDefinition myFromCol = myTableDef.addReferenceColumn(MoneyWiseBasicResource.XCHGRATE_FROM, MoneyWiseTableCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(MoneyWiseBasicResource.XCHGRATE_TO, MoneyWiseTableCurrency.TABLE_NAME);
        myTableDef.addRatioColumn(MoneyWiseBasicResource.XCHGRATE_RATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(PrometheusSortOrder.DESCENDING);
        myFromCol.setSortOrder(PrometheusSortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getExchangeRates());
        theFormatter = myData.getDataFormatter();
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseExchangeRate.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, myTableDef.getDateValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE));
        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_FROM, myTableDef.getIntegerValue(MoneyWiseBasicResource.XCHGRATE_FROM));
        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_TO, myTableDef.getIntegerValue(MoneyWiseBasicResource.XCHGRATE_TO));
        myValues.addValue(MoneyWiseBasicResource.XCHGRATE_RATE, myTableDef.getRatioValue(MoneyWiseBasicResource.XCHGRATE_RATE, theFormatter));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseExchangeRate pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (MoneyWiseBasicResource.XCHGRATE_FROM.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getFromCurrencyId());
        } else if (MoneyWiseBasicResource.XCHGRATE_TO.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getToCurrencyId());
        } else if (MoneyWiseBasicResource.XCHGRATE_RATE.equals(iField)) {
            myTableDef.setRatioValue(iField, pItem.getExchangeRate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
