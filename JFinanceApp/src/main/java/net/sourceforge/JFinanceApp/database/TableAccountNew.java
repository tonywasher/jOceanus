/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.database;

import java.util.Date;

import javax.swing.SortOrder;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.database.ColumnDefinition;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JDataModels.database.TableDefinition;
import net.sourceforge.JDataModels.database.TableEncrypted;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.AccountBase;
import net.sourceforge.JFinanceApp.data.AccountNew;
import net.sourceforge.JFinanceApp.data.AccountNew.AccountNewList;
import net.sourceforge.JFinanceApp.data.FinanceData;

/**
 * TableEncrypted extension for Account.
 * @author Tony Washer
 */
public class TableAccountNew extends TableEncrypted<AccountNew> {
    /**
     * The name of the Account table.
     */
    protected static final String TABLE_NAME = AccountNew.LIST_NAME;

    /**
     * The account list.
     */
    private AccountNewList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableAccountNew(final Database<FinanceData> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addEncryptedColumn(AccountBase.FIELD_NAME, Account.NAMELEN);
        ColumnDefinition mySortCol = myTableDef.addReferenceColumn(AccountBase.FIELD_TYPE,
                                                                   TableAccountType.TABLE_NAME);
        myTableDef.addNullEncryptedColumn(AccountBase.FIELD_DESC, Account.DESCLEN);
        myTableDef.addNullDateColumn(AccountBase.FIELD_CLOSE);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getNewAccounts();
        setList(theList);
    }

    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myName = myTableDef.getBinaryValue(Account.FIELD_NAME);
        int myActTypeId = myTableDef.getIntegerValue(Account.FIELD_TYPE);
        byte[] myDesc = myTableDef.getBinaryValue(Account.FIELD_DESC);
        Date myClosed = myTableDef.getDateValue(Account.FIELD_CLOSE);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myName, myActTypeId, myDesc, myClosed);
    }

    @Override
    protected void setFieldValue(final AccountNew pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (AccountBase.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (AccountBase.FIELD_TYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getActType().getId());
        } else if (AccountBase.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Account.FIELD_CLOSE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getClose());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
