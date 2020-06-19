/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for SecurityPrice.
 * @author Tony Washer
 */
public class TableSecurityPrice
        extends PrometheusTableEncrypted<SecurityPrice, MoneyWiseDataType> {
    /**
     * The name of the Prices table.
     */
    protected static final String TABLE_NAME = SecurityPrice.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableSecurityPrice(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myActCol = myTableDef.addReferenceColumn(SecurityPrice.FIELD_SECURITY, TableSecurity.TABLE_NAME);
        final PrometheusColumnDefinition myDateCol = myTableDef.addDateColumn(SecurityPrice.FIELD_DATE);
        myTableDef.addEncryptedColumn(SecurityPrice.FIELD_PRICE, MetisEncryptedData.PRICELEN);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.DESCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getSecurityPrices());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(SecurityPrice.OBJECT_NAME);
        myValues.addValue(SecurityPrice.FIELD_SECURITY, myTableDef.getIntegerValue(SecurityPrice.FIELD_SECURITY));
        myValues.addValue(SecurityPrice.FIELD_DATE, myTableDef.getDateValue(SecurityPrice.FIELD_DATE));
        myValues.addValue(SecurityPrice.FIELD_PRICE, myTableDef.getBinaryValue(SecurityPrice.FIELD_PRICE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final SecurityPrice pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (SecurityPrice.FIELD_SECURITY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityId());
        } else if (SecurityPrice.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (SecurityPrice.FIELD_PRICE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPriceBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
