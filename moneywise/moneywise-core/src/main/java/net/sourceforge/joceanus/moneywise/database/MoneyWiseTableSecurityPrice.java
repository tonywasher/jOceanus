/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition.PrometheusSortOrder;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * TableEncrypted extension for SecurityPrice.
 * @author Tony Washer
 */
public class MoneyWiseTableSecurityPrice
        extends PrometheusTableEncrypted<MoneyWiseSecurityPrice> {
    /**
     * The name of the Prices table.
     */
    protected static final String TABLE_NAME = MoneyWiseSecurityPrice.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected MoneyWiseTableSecurityPrice(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myActCol = myTableDef.addReferenceColumn(MoneyWiseBasicDataType.SECURITY, MoneyWiseTableSecurity.TABLE_NAME);
        final PrometheusColumnDefinition myDateCol = myTableDef.addDateColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        myTableDef.addEncryptedColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, OceanusMoney.BYTE_LEN);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(PrometheusSortOrder.DESCENDING);
        myActCol.setSortOrder(PrometheusSortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getSecurityPrices());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseSecurityPrice.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicDataType.SECURITY, myTableDef.getIntegerValue(MoneyWiseBasicDataType.SECURITY));
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, myTableDef.getDateValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE));
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE, myTableDef.getBinaryValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseSecurityPrice pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (MoneyWiseBasicDataType.SECURITY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityId());
        } else if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_PRICE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPriceBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
