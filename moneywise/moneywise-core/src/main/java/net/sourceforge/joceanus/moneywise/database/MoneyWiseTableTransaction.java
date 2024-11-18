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

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableDefinition.PrometheusSortOrder;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * TableEncrypted extension for Transaction.
 * @author Tony Washer
 */
public class MoneyWiseTableTransaction
        extends PrometheusTableEncrypted<MoneyWiseTransaction> {
    /**
     * The name of the Transactions table.
     */
    protected static final String TABLE_NAME = MoneyWiseTransaction.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected MoneyWiseTableTransaction(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        final PrometheusColumnDefinition myDateCol = myTableDef.addDateColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        myTableDef.addBooleanColumn(MoneyWiseBasicResource.TRANSACTION_DIRECTION);
        myTableDef.addLongColumn(MoneyWiseBasicResource.TRANSACTION_ACCOUNT);
        myTableDef.addLongColumn(MoneyWiseBasicResource.TRANSACTION_PARTNER);
        myTableDef.addNullEncryptedColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT, TethysMoney.BYTE_LEN);
        myTableDef.addReferenceColumn(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTableTransCategory.TABLE_NAME);
        myTableDef.addBooleanColumn(MoneyWiseBasicResource.TRANSACTION_RECONCILED);

        /* Declare the sort order */
        myDateCol.setSortOrder(PrometheusSortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getTransactions());
    }

    /* Load the event */
    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseTransaction.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE, myTableDef.getDateValue(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE));
        myValues.addValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION, myTableDef.getBooleanValue(MoneyWiseBasicResource.TRANSACTION_DIRECTION));
        myValues.addValue(MoneyWiseBasicDataType.TRANSCATEGORY, myTableDef.getIntegerValue(MoneyWiseBasicDataType.TRANSCATEGORY));
        myValues.addValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, myTableDef.getLongValue(MoneyWiseBasicResource.TRANSACTION_ACCOUNT));
        myValues.addValue(MoneyWiseBasicResource.TRANSACTION_PARTNER, myTableDef.getLongValue(MoneyWiseBasicResource.TRANSACTION_PARTNER));
        myValues.addValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT, myTableDef.getBinaryValue(MoneyWiseBasicResource.TRANSACTION_AMOUNT));
        myValues.addValue(MoneyWiseBasicResource.TRANSACTION_RECONCILED, myTableDef.getBooleanValue(MoneyWiseBasicResource.TRANSACTION_RECONCILED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseTransaction pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (MoneyWiseBasicResource.TRANSACTION_DIRECTION.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.getDirection().isFrom());
        } else if (MoneyWiseBasicResource.TRANSACTION_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAmountBytes());
        } else if (MoneyWiseBasicResource.TRANSACTION_ACCOUNT.equals(iField)) {
            myTableDef.setLongValue(iField, pItem.getAccountId());
        } else if (MoneyWiseBasicResource.TRANSACTION_PARTNER.equals(iField)) {
            myTableDef.setLongValue(iField, pItem.getPartnerId());
        } else if (MoneyWiseBasicDataType.TRANSCATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (MoneyWiseBasicResource.TRANSACTION_RECONCILED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isReconciled());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
