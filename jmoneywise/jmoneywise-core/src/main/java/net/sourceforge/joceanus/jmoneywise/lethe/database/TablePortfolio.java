/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Portfolio.
 */
public class TablePortfolio
        extends PrometheusTableEncrypted<Portfolio, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Portfolio.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePortfolio(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(Portfolio.FIELD_PORTTYPE, TablePortfolioType.TABLE_NAME);
        myTableDef.addEncryptedColumn(Portfolio.FIELD_NAME, Portfolio.NAMELEN);
        myTableDef.addReferenceColumn(Portfolio.FIELD_CURRENCY, TableAssetCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(Portfolio.FIELD_PARENT, TablePayee.TABLE_NAME);
        myTableDef.addNullEncryptedColumn(Portfolio.FIELD_DESC, Portfolio.DESCLEN);
        myTableDef.addBooleanColumn(Portfolio.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getPortfolios());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Portfolio.OBJECT_NAME);
        myValues.addValue(Portfolio.FIELD_NAME, myTableDef.getBinaryValue(Portfolio.FIELD_NAME));
        myValues.addValue(Portfolio.FIELD_DESC, myTableDef.getBinaryValue(Portfolio.FIELD_DESC));
        myValues.addValue(Portfolio.FIELD_PORTTYPE, myTableDef.getIntegerValue(Portfolio.FIELD_PORTTYPE));
        myValues.addValue(Portfolio.FIELD_PARENT, myTableDef.getIntegerValue(Portfolio.FIELD_PARENT));
        myValues.addValue(Portfolio.FIELD_CURRENCY, myTableDef.getIntegerValue(Portfolio.FIELD_CURRENCY));
        myValues.addValue(Portfolio.FIELD_CLOSED, myTableDef.getBooleanValue(Portfolio.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Portfolio pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (Portfolio.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Portfolio.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Portfolio.FIELD_PORTTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getPortfolioTypeId());
        } else if (Portfolio.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (Portfolio.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetCurrencyId());
        } else if (Portfolio.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
