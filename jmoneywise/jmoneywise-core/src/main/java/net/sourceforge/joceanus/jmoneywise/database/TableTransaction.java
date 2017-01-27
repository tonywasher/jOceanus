/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.database;

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jmetis.data.MetisEncryptedData;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Transaction.
 * @author Tony Washer
 */
public class TableTransaction
        extends PrometheusTableEncrypted<Transaction, MoneyWiseDataType> {
    /**
     * The name of the Transactions table.
     */
    protected static final String TABLE_NAME = Transaction.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTransaction(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        PrometheusColumnDefinition myDateCol = myTableDef.addDateColumn(Transaction.FIELD_DATE);
        myTableDef.addIntegerColumn(Transaction.FIELD_PAIR);
        myTableDef.addIntegerColumn(Transaction.FIELD_ACCOUNT);
        myTableDef.addIntegerColumn(Transaction.FIELD_PARTNER);
        myTableDef.addNullEncryptedColumn(Transaction.FIELD_AMOUNT, MetisEncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(Transaction.FIELD_CATEGORY, TableTransCategory.TABLE_NAME);
        myTableDef.addBooleanColumn(Transaction.FIELD_RECONCILED);

        /* Declare the sort order */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getTransactions());
    }

    /* Load the event */
    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Transaction.OBJECT_NAME);
        myValues.addValue(Transaction.FIELD_DATE, myTableDef.getDateValue(Transaction.FIELD_DATE));
        myValues.addValue(Transaction.FIELD_PAIR, myTableDef.getIntegerValue(Transaction.FIELD_PAIR));
        myValues.addValue(Transaction.FIELD_CATEGORY, myTableDef.getIntegerValue(Transaction.FIELD_CATEGORY));
        myValues.addValue(Transaction.FIELD_ACCOUNT, myTableDef.getIntegerValue(Transaction.FIELD_ACCOUNT));
        myValues.addValue(Transaction.FIELD_PARTNER, myTableDef.getIntegerValue(Transaction.FIELD_PARTNER));
        myValues.addValue(Transaction.FIELD_AMOUNT, myTableDef.getBinaryValue(Transaction.FIELD_AMOUNT));
        myValues.addValue(Transaction.FIELD_RECONCILED, myTableDef.getBooleanValue(Transaction.FIELD_RECONCILED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Transaction pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        PrometheusTableDefinition myTableDef = getTableDef();
        if (Transaction.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (Transaction.FIELD_PAIR.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetPairId());
        } else if (Transaction.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAmountBytes());
        } else if (Transaction.FIELD_ACCOUNT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAccountId());
        } else if (Transaction.FIELD_PARTNER.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getPartnerId());
        } else if (Transaction.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Transaction.FIELD_RECONCILED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isReconciled());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
