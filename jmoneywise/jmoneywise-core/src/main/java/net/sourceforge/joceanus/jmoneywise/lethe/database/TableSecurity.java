/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Security.
 */
public class TableSecurity
        extends PrometheusXTableEncrypted<Security> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Security.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableSecurity(final PrometheusXDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusXColumnDefinition myCatCol = myTableDef.addReferenceColumn(Security.FIELD_CATEGORY, TableSecurityType.TABLE_NAME);
        myTableDef.addReferenceColumn(Security.FIELD_CURRENCY, TableAssetCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(Security.FIELD_PARENT, TablePayee.TABLE_NAME);
        myTableDef.addEncryptedColumn(Security.FIELD_NAME, Security.NAMELEN);
        myTableDef.addNullEncryptedColumn(Security.FIELD_DESC, Security.DESCLEN);
        myTableDef.addBooleanColumn(Security.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getSecurities());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues myValues = getRowValues(Security.OBJECT_NAME);
        myValues.addValue(Security.FIELD_NAME, myTableDef.getBinaryValue(Security.FIELD_NAME));
        myValues.addValue(Security.FIELD_DESC, myTableDef.getBinaryValue(Security.FIELD_DESC));
        myValues.addValue(Security.FIELD_CATEGORY, myTableDef.getIntegerValue(Security.FIELD_CATEGORY));
        myValues.addValue(Security.FIELD_PARENT, myTableDef.getIntegerValue(Security.FIELD_PARENT));
        myValues.addValue(Security.FIELD_CURRENCY, myTableDef.getIntegerValue(Security.FIELD_CURRENCY));
        myValues.addValue(Security.FIELD_CLOSED, myTableDef.getBooleanValue(Security.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Security pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        if (Security.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Security.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (Security.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetCurrencyId());
        } else if (Security.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Security.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Security.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
