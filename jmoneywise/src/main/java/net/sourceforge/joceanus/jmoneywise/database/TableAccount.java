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
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.AccountBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Account.
 * @author Tony Washer
 */
public class TableAccount extends TableEncrypted<Account, MoneyWiseDataType> {
    /**
     * The name of the Account table.
     */
    protected static final String TABLE_NAME = Account.LIST_NAME;

    /**
     * The account list.
     */
    private AccountList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableAccount(final Database<MoneyWiseData> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addEncryptedColumn(AccountBase.FIELD_NAME, AccountBase.NAMELEN);
        ColumnDefinition mySortCol = myTableDef.addReferenceColumn(AccountBase.FIELD_CATEGORY, TableAccountCategory.TABLE_NAME);
        myTableDef.addBooleanColumn(AccountBase.FIELD_CLOSED);
        myTableDef.addBooleanColumn(AccountBase.FIELD_TAXFREE);
        myTableDef.addBooleanColumn(AccountBase.FIELD_GROSS);
        myTableDef.addNullReferenceColumn(AccountBase.FIELD_CURRENCY, TableAccountCurrency.TABLE_NAME);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getAccounts();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Account.OBJECT_NAME);
        myValues.addValue(Account.FIELD_NAME, myTableDef.getBinaryValue(Account.FIELD_NAME));
        myValues.addValue(Account.FIELD_CATEGORY, myTableDef.getIntegerValue(Account.FIELD_CATEGORY));
        myValues.addValue(Account.FIELD_CURRENCY, myTableDef.getIntegerValue(Account.FIELD_CURRENCY));
        myValues.addValue(Account.FIELD_GROSS, myTableDef.getBooleanValue(Account.FIELD_GROSS));
        myValues.addValue(Account.FIELD_TAXFREE, myTableDef.getBooleanValue(Account.FIELD_TAXFREE));
        myValues.addValue(Account.FIELD_CLOSED, myTableDef.getBooleanValue(Account.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Account pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (AccountBase.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (AccountBase.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAccountCategoryId());
        } else if (AccountBase.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else if (AccountBase.FIELD_TAXFREE.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isTaxFree());
        } else if (AccountBase.FIELD_GROSS.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isGrossInterest());
        } else if (AccountBase.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAccountCurrencyId());
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
