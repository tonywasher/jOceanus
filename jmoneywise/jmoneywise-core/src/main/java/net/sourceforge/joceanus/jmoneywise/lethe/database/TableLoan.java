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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for Loan.
 */
public class TableLoan
        extends PrometheusXTableEncrypted<Loan> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Loan.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableLoan(final PrometheusXDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusXColumnDefinition myCatCol = myTableDef.addReferenceColumn(Loan.FIELD_CATEGORY, TableLoanCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(Loan.FIELD_CURRENCY, TableAssetCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(Loan.FIELD_PARENT, TablePayee.TABLE_NAME);
        myTableDef.addEncryptedColumn(Loan.FIELD_NAME, Loan.NAMELEN);
        myTableDef.addNullEncryptedColumn(Loan.FIELD_DESC, Loan.DESCLEN);
        myTableDef.addBooleanColumn(Loan.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getLoans());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues myValues = getRowValues(Loan.OBJECT_NAME);
        myValues.addValue(Loan.FIELD_NAME, myTableDef.getBinaryValue(Loan.FIELD_NAME));
        myValues.addValue(Loan.FIELD_DESC, myTableDef.getBinaryValue(Loan.FIELD_DESC));
        myValues.addValue(Loan.FIELD_CATEGORY, myTableDef.getIntegerValue(Loan.FIELD_CATEGORY));
        myValues.addValue(Loan.FIELD_PARENT, myTableDef.getIntegerValue(Loan.FIELD_PARENT));
        myValues.addValue(Loan.FIELD_CURRENCY, myTableDef.getIntegerValue(Loan.FIELD_CURRENCY));
        myValues.addValue(Loan.FIELD_CLOSED, myTableDef.getBooleanValue(Loan.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Loan pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        if (Loan.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Loan.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else if (Loan.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetCurrencyId());
        } else if (Loan.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Loan.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Loan.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
