/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDataItem;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition.SortOrder;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for ExchangeRate.
 * @author Tony Washer
 */
public class TableExchangeRate
        extends PrometheusTableDataItem<ExchangeRate, MoneyWiseDataType> {
    /**
     * The name of the ExchangeRate table.
     */
    protected static final String TABLE_NAME = ExchangeRate.LIST_NAME;

    /**
     * The formatter.
     */
    private MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableExchangeRate(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myDateCol = myTableDef.addDateColumn(ExchangeRate.FIELD_DATE);
        final PrometheusColumnDefinition myFromCol = myTableDef.addReferenceColumn(ExchangeRate.FIELD_FROM, TableAssetCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(ExchangeRate.FIELD_TO, TableAssetCurrency.TABLE_NAME);
        myTableDef.addRatioColumn(ExchangeRate.FIELD_RATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.DESCENDING);
        myFromCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getExchangeRates());
        theFormatter = myData.getDataFormatter();
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(ExchangeRate.OBJECT_NAME);
        myValues.addValue(ExchangeRate.FIELD_DATE, myTableDef.getDateValue(ExchangeRate.FIELD_DATE));
        myValues.addValue(ExchangeRate.FIELD_FROM, myTableDef.getIntegerValue(ExchangeRate.FIELD_FROM));
        myValues.addValue(ExchangeRate.FIELD_TO, myTableDef.getIntegerValue(ExchangeRate.FIELD_TO));
        myValues.addValue(ExchangeRate.FIELD_RATE, myTableDef.getRatioValue(ExchangeRate.FIELD_RATE, theFormatter));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final ExchangeRate pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (ExchangeRate.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (ExchangeRate.FIELD_FROM.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getFromCurrencyId());
        } else if (ExchangeRate.FIELD_TO.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getToCurrencyId());
        } else if (ExchangeRate.FIELD_RATE.equals(iField)) {
            myTableDef.setRatioValue(iField, pItem.getExchangeRate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
