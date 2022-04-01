/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
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
 * TableEncrypted extension for Cash.
 */
public class TableCash
        extends PrometheusTableEncrypted<Cash, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Cash.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableCash(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(Cash.FIELD_CATEGORY, TableCashCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(Cash.FIELD_CURRENCY, TableAssetCurrency.TABLE_NAME);
        myTableDef.addEncryptedColumn(Cash.FIELD_NAME, Cash.NAMELEN);
        myTableDef.addNullEncryptedColumn(Cash.FIELD_DESC, Cash.DESCLEN);
        myTableDef.addBooleanColumn(Cash.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getCash());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Cash.OBJECT_NAME);
        myValues.addValue(Cash.FIELD_NAME, myTableDef.getBinaryValue(Cash.FIELD_NAME));
        myValues.addValue(Cash.FIELD_DESC, myTableDef.getBinaryValue(Cash.FIELD_DESC));
        myValues.addValue(Cash.FIELD_CATEGORY, myTableDef.getIntegerValue(Cash.FIELD_CATEGORY));
        myValues.addValue(Cash.FIELD_CURRENCY, myTableDef.getIntegerValue(Cash.FIELD_CURRENCY));
        myValues.addValue(Cash.FIELD_CLOSED, myTableDef.getBooleanValue(Cash.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Cash pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (Cash.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Cash.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetCurrencyId());
        } else if (Cash.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Cash.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Cash.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
