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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableDefinition.PrometheusSortOrder;
import net.sourceforge.joceanus.prometheus.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * TableEncrypted extension for Deposit.
 */
public class MoneyWiseTableDeposit
        extends PrometheusTableEncrypted<MoneyWiseDeposit> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = MoneyWiseDeposit.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected MoneyWiseTableDeposit(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWiseTableDepositCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(MoneyWiseStaticDataType.CURRENCY, MoneyWiseTableCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(MoneyWiseBasicResource.ASSET_PARENT, MoneyWiseTablePayee.TABLE_NAME);
        myTableDef.addEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_NAME, PrometheusDataItem.NAMELEN);
        myTableDef.addNullEncryptedColumn(PrometheusDataResource.DATAITEM_FIELD_DESC, PrometheusDataItem.DESCLEN);
        myTableDef.addBooleanColumn(MoneyWiseBasicResource.ASSET_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(PrometheusSortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getDeposits());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseDeposit.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, myTableDef.getBinaryValue(PrometheusDataResource.DATAITEM_FIELD_DESC));
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myTableDef.getIntegerValue(MoneyWiseBasicResource.CATEGORY_NAME));
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, myTableDef.getIntegerValue(MoneyWiseBasicResource.ASSET_PARENT));
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myTableDef.getIntegerValue(MoneyWiseStaticDataType.CURRENCY));
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, myTableDef.getBooleanValue(MoneyWiseBasicResource.ASSET_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseDeposit pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (MoneyWiseBasicResource.CATEGORY_NAME.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (MoneyWiseBasicResource.ASSET_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (MoneyWiseStaticDataType.CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetCurrencyId());
        } else if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (MoneyWiseBasicResource.ASSET_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
