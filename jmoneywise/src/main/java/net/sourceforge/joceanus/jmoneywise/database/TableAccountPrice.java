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

import net.sourceforge.joceanus.jmetis.viewer.EncryptedData;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice.AccountPriceList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for AccountPrice.
 * @author Tony Washer
 */
public class TableAccountPrice
        extends TableEncrypted<AccountPrice, MoneyWiseDataType> {
    /**
     * The name of the Prices table.
     */
    protected static final String TABLE_NAME = AccountPrice.LIST_NAME;

    /**
     * The price list.
     */
    private AccountPriceList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableAccountPrice(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myActCol = myTableDef.addReferenceColumn(AccountPrice.FIELD_SECURITY, TableAccount.TABLE_NAME);
        ColumnDefinition myDateCol = myTableDef.addDateColumn(AccountPrice.FIELD_DATE);
        myTableDef.addEncryptedColumn(AccountPrice.FIELD_PRICE, EncryptedData.PRICELEN);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getAccountPrices();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(AccountPrice.OBJECT_NAME);
        myValues.addValue(AccountPrice.FIELD_SECURITY, myTableDef.getIntegerValue(AccountPrice.FIELD_SECURITY));
        myValues.addValue(AccountPrice.FIELD_DATE, myTableDef.getDateValue(AccountPrice.FIELD_DATE));
        myValues.addValue(AccountPrice.FIELD_PRICE, myTableDef.getBinaryValue(AccountPrice.FIELD_PRICE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final AccountPrice pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (AccountPrice.FIELD_SECURITY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getSecurityId());
        } else if (AccountPrice.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (AccountPrice.FIELD_PRICE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPriceBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Validate the security prices */
        theList.validateOnLoad();
    }
}
