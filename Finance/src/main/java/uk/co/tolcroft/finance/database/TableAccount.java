/*******************************************************************************
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

public class TableAccount extends TableEncrypted<Account> {
    /**
     * The name of the Account table
     */
    protected final static String TableName = Account.LIST_NAME;

    /**
     * The table definition
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The account list
     */
    private AccountList theList = null;

    /**
     * Constructor
     * @param pDatabase the database control
     */
    protected TableAccount(Database<FinanceData> pDatabase) {
        super(pDatabase, TableName);
    }

    /**
     * Define the table columns (called from within super-constructor)
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(TableDefinition pTableDef) {
        /* Define Standard table */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define sort column variable */
        ColumnDefinition mySortCol;

        /* Define the columns */
        theTableDef.addEncryptedColumn(Account.FIELD_NAME, Account.NAMELEN);
        mySortCol = theTableDef.addReferenceColumn(Account.FIELD_TYPE, TableAccountType.TABLE_NAME);
        theTableDef.addNullEncryptedColumn(Account.FIELD_DESC, Account.DESCLEN);
        theTableDef.addNullDateColumn(Account.FIELD_MATURITY);
        theTableDef.addNullDateColumn(Account.FIELD_CLOSE);
        theTableDef.addNullReferenceColumn(Account.FIELD_PARENT, TableName);
        theTableDef.addNullReferenceColumn(Account.FIELD_ALIAS, TableName);
        theTableDef.addNullEncryptedColumn(Account.FIELD_WEBSITE, Account.WSITELEN);
        theTableDef.addNullEncryptedColumn(Account.FIELD_CUSTNO, Account.CUSTLEN);
        theTableDef.addNullEncryptedColumn(Account.FIELD_USERID, Account.UIDLEN);
        theTableDef.addNullEncryptedColumn(Account.FIELD_PASSWORD, Account.PWDLEN);
        theTableDef.addNullEncryptedColumn(Account.FIELD_ACCOUNT, Account.ACTLEN);
        theTableDef.addNullEncryptedColumn(Account.FIELD_NOTES, Account.NOTELEN);

        /* Declare the sort order */
        mySortCol.setSortOrder(SortOrder.ASCENDING);
    }

    /* Declare DataSet */
    @Override
    protected void declareData(DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getAccounts();
        setList(theList);
    }

    /* Load the account */
    @Override
    protected void loadItem(int pId,
                            int pControlId) throws JDataException {
        byte[] myName;
        int myActTypeId;
        Integer myParentId;
        Integer myAliasId;
        byte[] myDesc;
        Date myMaturity;
        Date myClosed;
        byte[] myWebSite;
        byte[] myCustNo;
        byte[] myUserId;
        byte[] myPassword;
        byte[] myAccount;
        byte[] myNotes;

        /* Get the various fields */
        myName = theTableDef.getBinaryValue(Account.FIELD_NAME);
        myActTypeId = theTableDef.getIntegerValue(Account.FIELD_TYPE);
        myDesc = theTableDef.getBinaryValue(Account.FIELD_DESC);
        myMaturity = theTableDef.getDateValue(Account.FIELD_MATURITY);
        myClosed = theTableDef.getDateValue(Account.FIELD_CLOSE);
        myParentId = theTableDef.getIntegerValue(Account.FIELD_PARENT);
        myAliasId = theTableDef.getIntegerValue(Account.FIELD_ALIAS);
        myWebSite = theTableDef.getBinaryValue(Account.FIELD_WEBSITE);
        myCustNo = theTableDef.getBinaryValue(Account.FIELD_CUSTNO);
        myUserId = theTableDef.getBinaryValue(Account.FIELD_USERID);
        myPassword = theTableDef.getBinaryValue(Account.FIELD_PASSWORD);
        myAccount = theTableDef.getBinaryValue(Account.FIELD_ACCOUNT);
        myNotes = theTableDef.getBinaryValue(Account.FIELD_NOTES);

        /* Add into the list */
        theList.addItem(pId, pControlId, myName, myActTypeId, myDesc, myMaturity, myClosed, myParentId,
                        myAliasId, myWebSite, myCustNo, myUserId, myPassword, myAccount, myNotes);
    }

    /* Set a field value */
    @Override
    protected void setFieldValue(Account pItem,
                                 JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == Account.FIELD_NAME) {
            theTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (iField == Account.FIELD_TYPE) {
            theTableDef.setIntegerValue(iField, pItem.getActType().getId());
        } else if (iField == Account.FIELD_DESC) {
            theTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (iField == Account.FIELD_MATURITY) {
            theTableDef.setDateValue(iField, pItem.getMaturity());
        } else if (iField == Account.FIELD_CLOSE) {
            theTableDef.setDateValue(iField, pItem.getClose());
        } else if (iField == Account.FIELD_PARENT) {
            theTableDef.setIntegerValue(iField, (pItem.getParent() != null)
                                                                           ? pItem.getParent().getId()
                                                                           : null);
        } else if (iField == Account.FIELD_ALIAS) {
            theTableDef.setIntegerValue(iField, (pItem.getAlias() != null) ? pItem.getAlias().getId() : null);
        } else if (iField == Account.FIELD_WEBSITE) {
            theTableDef.setBinaryValue(iField, pItem.getWebSiteBytes());
        } else if (iField == Account.FIELD_CUSTNO) {
            theTableDef.setBinaryValue(iField, pItem.getCustNoBytes());
        } else if (iField == Account.FIELD_USERID) {
            theTableDef.setBinaryValue(iField, pItem.getUserIdBytes());
        } else if (iField == Account.FIELD_PASSWORD) {
            theTableDef.setBinaryValue(iField, pItem.getPasswordBytes());
        } else if (iField == Account.FIELD_ACCOUNT) {
            theTableDef.setBinaryValue(iField, pItem.getAccountBytes());
        } else if (iField == Account.FIELD_NOTES) {
            theTableDef.setBinaryValue(iField, pItem.getNotesBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
