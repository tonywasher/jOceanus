/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.database;

import javax.swing.SortOrder;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.ColumnDefinition;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.database.TableDefinition;
import net.sourceforge.jOceanus.jDataModels.database.TableEncrypted;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountBase;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * TableEncrypted extension for Account.
 * @author Tony Washer
 */
public class TableAccount
        extends TableEncrypted<Account> {
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
    protected TableAccount(final Database<FinanceData> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addEncryptedColumn(AccountBase.FIELD_NAME, AccountBase.NAMELEN);
        ColumnDefinition mySortCol = myTableDef.addReferenceColumn(AccountBase.FIELD_CATEGORY, TableAccountCategory.TABLE_NAME);
        myTableDef.addBooleanColumn(AccountBase.FIELD_CLOSED);
        myTableDef.addBooleanColumn(AccountBase.FIELD_TAXFREE);
        myTableDef.addNullReferenceColumn(AccountBase.FIELD_CURRENCY, TableAccountCurrency.TABLE_NAME);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getAccounts();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myName = myTableDef.getBinaryValue(AccountBase.FIELD_NAME);
        Integer myCategoryId = myTableDef.getIntegerValue(AccountBase.FIELD_CATEGORY);
        Boolean isClosed = myTableDef.getBooleanValue(AccountBase.FIELD_CLOSED);
        Boolean isTaxFree = myTableDef.getBooleanValue(AccountBase.FIELD_TAXFREE);
        Integer myCurrencyId = myTableDef.getIntegerValue(AccountBase.FIELD_CURRENCY);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myName, myCategoryId, isClosed, isTaxFree, myCurrencyId);
    }

    @Override
    protected void setFieldValue(final Account pItem,
                                 final JDataField iField) throws JDataException {
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
        } else if (AccountBase.FIELD_CURRENCY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAccountCurrencyId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();
    }
}
