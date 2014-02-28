/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Cash.
 */
public class TableCash
        extends TableEncrypted<Cash, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Cash.LIST_NAME;

    /**
     * The cash list.
     */
    private CashList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableCash(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myCatCol = myTableDef.addReferenceColumn(Cash.FIELD_CATEGORY, TableCashCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(Cash.FIELD_CURRENCY, TableAccountCurrency.TABLE_NAME);
        myTableDef.addEncryptedColumn(Cash.FIELD_NAME, Cash.NAMELEN);
        myTableDef.addNullEncryptedColumn(Cash.FIELD_DESC, Cash.DESCLEN);
        myTableDef.addBooleanColumn(Cash.FIELD_CLOSED);

        /* Declare Sort Columns */
        myCatCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getCash();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Cash.OBJECT_NAME);
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
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Cash.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Cash.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCashCurrencyId());
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

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();
    }
}
