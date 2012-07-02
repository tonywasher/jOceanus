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
package uk.co.tolcroft.finance.database;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

/**
 * TableEncrypted extension for Account.
 * @author Tony Washer
 */
public class TableAccount extends TableEncrypted<Account> {
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
        myTableDef.addEncryptedColumn(Account.FIELD_NAME, Account.NAMELEN);
        ColumnDefinition mySortCol = myTableDef.addReferenceColumn(Account.FIELD_TYPE,
                                                                   TableAccountType.TABLE_NAME);
        myTableDef.addNullEncryptedColumn(Account.FIELD_DESC, Account.DESCLEN);
        myTableDef.addNullDateColumn(Account.FIELD_MATURITY);
        myTableDef.addNullDateColumn(Account.FIELD_CLOSE);
        myTableDef.addNullReferenceColumn(Account.FIELD_PARENT, TABLE_NAME);
        myTableDef.addNullReferenceColumn(Account.FIELD_ALIAS, TABLE_NAME);
        myTableDef.addNullEncryptedColumn(Account.FIELD_WEBSITE, Account.WSITELEN);
        myTableDef.addNullEncryptedColumn(Account.FIELD_CUSTNO, Account.CUSTLEN);
        myTableDef.addNullEncryptedColumn(Account.FIELD_USERID, Account.UIDLEN);
        myTableDef.addNullEncryptedColumn(Account.FIELD_PASSWORD, Account.PWDLEN);
        myTableDef.addNullEncryptedColumn(Account.FIELD_ACCOUNT, Account.ACTLEN);
        myTableDef.addNullEncryptedColumn(Account.FIELD_NOTES, Account.NOTELEN);

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
    protected void loadItem(final int pId,
                            final int pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myName = myTableDef.getBinaryValue(Account.FIELD_NAME);
        int myActTypeId = myTableDef.getIntegerValue(Account.FIELD_TYPE);
        byte[] myDesc = myTableDef.getBinaryValue(Account.FIELD_DESC);
        Date myMaturity = myTableDef.getDateValue(Account.FIELD_MATURITY);
        Date myClosed = myTableDef.getDateValue(Account.FIELD_CLOSE);
        Integer myParentId = myTableDef.getIntegerValue(Account.FIELD_PARENT);
        Integer myAliasId = myTableDef.getIntegerValue(Account.FIELD_ALIAS);
        byte[] myWebSite = myTableDef.getBinaryValue(Account.FIELD_WEBSITE);
        byte[] myCustNo = myTableDef.getBinaryValue(Account.FIELD_CUSTNO);
        byte[] myUserId = myTableDef.getBinaryValue(Account.FIELD_USERID);
        byte[] myPassword = myTableDef.getBinaryValue(Account.FIELD_PASSWORD);
        byte[] myAccount = myTableDef.getBinaryValue(Account.FIELD_ACCOUNT);
        byte[] myNotes = myTableDef.getBinaryValue(Account.FIELD_NOTES);

        /* Add into the list */
        theList.addItem(pId, pControlId, myName, myActTypeId, myDesc, myMaturity, myClosed, myParentId,
                        myAliasId, myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
    }

    @Override
    protected void setFieldValue(final Account pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (iField == Account.FIELD_NAME) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (iField == Account.FIELD_TYPE) {
            myTableDef.setIntegerValue(iField, pItem.getActType().getId());
        } else if (iField == Account.FIELD_DESC) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (iField == Account.FIELD_MATURITY) {
            myTableDef.setDateValue(iField, pItem.getMaturity());
        } else if (iField == Account.FIELD_CLOSE) {
            myTableDef.setDateValue(iField, pItem.getClose());
        } else if (iField == Account.FIELD_PARENT) {
            myTableDef
                    .setIntegerValue(iField, (pItem.getParent() != null) ? pItem.getParent().getId() : null);
        } else if (iField == Account.FIELD_ALIAS) {
            myTableDef.setIntegerValue(iField, (pItem.getAlias() != null) ? pItem.getAlias().getId() : null);
        } else if (iField == Account.FIELD_WEBSITE) {
            myTableDef.setBinaryValue(iField, pItem.getWebSiteBytes());
        } else if (iField == Account.FIELD_CUSTNO) {
            myTableDef.setBinaryValue(iField, pItem.getCustNoBytes());
        } else if (iField == Account.FIELD_USERID) {
            myTableDef.setBinaryValue(iField, pItem.getUserIdBytes());
        } else if (iField == Account.FIELD_PASSWORD) {
            myTableDef.setBinaryValue(iField, pItem.getPasswordBytes());
        } else if (iField == Account.FIELD_ACCOUNT) {
            myTableDef.setBinaryValue(iField, pItem.getAccountBytes());
        } else if (iField == Account.FIELD_NOTES) {
            myTableDef.setBinaryValue(iField, pItem.getNotesBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
