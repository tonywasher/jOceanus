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

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableEncrypted;
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
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        PrometheusColumnDefinition myCatCol = myTableDef.addReferenceColumn(Deposit.FIELD_CATEGORY, TableDepositCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(Deposit.FIELD_CURRENCY, TableAssetCurrency.TABLE_NAME);
        myTableDef.addReferenceColumn(Deposit.FIELD_PARENT, TablePayee.TABLE_NAME);
        myTableDef.addEncryptedColumn(Deposit.FIELD_NAME, Deposit.NAMELEN);
        myTableDef.addNullEncryptedColumn(Deposit.FIELD_DESC, Deposit.DESCLEN);
        myTableDef.addBooleanColumn(Deposit.FIELD_GROSS);
        myTableDef.addBooleanColumn(Deposit.FIELD_TAXFREE);
        myTableDef.addBooleanColumn(Deposit.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getDeposits());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Deposit.OBJECT_NAME);
        myValues.addValue(Deposit.FIELD_NAME, myTableDef.getBinaryValue(Deposit.FIELD_NAME));
        myValues.addValue(Deposit.FIELD_DESC, myTableDef.getBinaryValue(Deposit.FIELD_DESC));
        myValues.addValue(Deposit.FIELD_CATEGORY, myTableDef.getIntegerValue(Deposit.FIELD_CATEGORY));
        myValues.addValue(Deposit.FIELD_PARENT, myTableDef.getIntegerValue(Deposit.FIELD_PARENT));
        myValues.addValue(Deposit.FIELD_CURRENCY, myTableDef.getIntegerValue(Deposit.FIELD_CURRENCY));
        myValues.addValue(Deposit.FIELD_GROSS, myTableDef.getBooleanValue(Deposit.FIELD_GROSS));
        myValues.addValue(Deposit.FIELD_TAXFREE, myTableDef.getBooleanValue(Deposit.FIELD_TAXFREE));
        myValues.addValue(Deposit.FIELD_CLOSED, myTableDef.getBooleanValue(Deposit.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Deposit pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        PrometheusTableDefinition myTableDef = getTableDef();
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
        } else if (Deposit.FIELD_GROSS.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isGross());
        } else if (Deposit.FIELD_TAXFREE.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isTaxFree());
        } else if (Deposit.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
