/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Deposit.
 */
public class TableDeposit
        extends PrometheusTableEncrypted<Deposit, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Deposit.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableDeposit(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(Deposit.FIELD_CATEGORY, TableDepositCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(Deposit.FIELD_CURRENCY, TableAssetCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(Deposit.FIELD_PARENT, TablePayee.TABLE_NAME);
        myTableDef.addEncryptedColumn(Deposit.FIELD_NAME, Deposit.NAMELEN);
        myTableDef.addNullEncryptedColumn(Deposit.FIELD_DESC, Deposit.DESCLEN);
        myTableDef.addBooleanColumn(Deposit.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getDeposits());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Deposit.OBJECT_NAME);
        myValues.addValue(Deposit.FIELD_NAME, myTableDef.getBinaryValue(Deposit.FIELD_NAME));
        myValues.addValue(Deposit.FIELD_DESC, myTableDef.getBinaryValue(Deposit.FIELD_DESC));
        myValues.addValue(Deposit.FIELD_CATEGORY, myTableDef.getIntegerValue(Deposit.FIELD_CATEGORY));
        myValues.addValue(Deposit.FIELD_PARENT, myTableDef.getIntegerValue(Deposit.FIELD_PARENT));
        myValues.addValue(Deposit.FIELD_CURRENCY, myTableDef.getIntegerValue(Deposit.FIELD_CURRENCY));
        myValues.addValue(Deposit.FIELD_CLOSED, myTableDef.getBooleanValue(Deposit.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Deposit pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (Deposit.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Deposit.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (Deposit.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetCurrencyId());
        } else if (Deposit.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Deposit.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Deposit.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
