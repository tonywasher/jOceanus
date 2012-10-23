/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jArgo.jMoneyWise.database;

import java.util.Date;

import javax.swing.SortOrder;

import net.sourceforge.jArgo.jDataManager.JDataException;
import net.sourceforge.jArgo.jDataManager.JDataFields.JDataField;
import net.sourceforge.jArgo.jDataModels.data.DataSet;
import net.sourceforge.jArgo.jDataModels.database.ColumnDefinition;
import net.sourceforge.jArgo.jDataModels.database.Database;
import net.sourceforge.jArgo.jDataModels.database.TableDefinition;
import net.sourceforge.jArgo.jDataModels.database.TableEncrypted;
import net.sourceforge.jArgo.jMoneyWise.data.Account;
import net.sourceforge.jArgo.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jArgo.jMoneyWise.data.FinanceData;

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
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        byte[] myName = myTableDef.getBinaryValue(Account.FIELD_NAME);
        Integer myActTypeId = myTableDef.getIntegerValue(Account.FIELD_TYPE);
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
        theList.addSecureItem(pId, pControlId, myName, myActTypeId, myDesc, myMaturity, myClosed, myParentId,
                              myAliasId, myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
    }

    @Override
    protected void setFieldValue(final Account pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Account.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (Account.FIELD_TYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getActType().getId());
        } else if (Account.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (Account.FIELD_MATURITY.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getMaturity());
        } else if (Account.FIELD_CLOSE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getClose());
        } else if (Account.FIELD_PARENT.equals(iField)) {
            myTableDef
                    .setIntegerValue(iField, (pItem.getParent() != null) ? pItem.getParent().getId() : null);
        } else if (Account.FIELD_ALIAS.equals(iField)) {
            myTableDef.setIntegerValue(iField, (pItem.getAlias() != null) ? pItem.getAlias().getId() : null);
        } else if (Account.FIELD_WEBSITE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getWebSiteBytes());
        } else if (Account.FIELD_CUSTNO.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getCustNoBytes());
        } else if (Account.FIELD_USERID.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getUserIdBytes());
        } else if (Account.FIELD_PASSWORD.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPasswordBytes());
        } else if (Account.FIELD_ACCOUNT.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAccountBytes());
        } else if (Account.FIELD_NOTES.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNotesBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
