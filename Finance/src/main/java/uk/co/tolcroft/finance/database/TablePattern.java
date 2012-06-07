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
import net.sourceforge.JGordianKnot.EncryptedData;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.Pattern;
import uk.co.tolcroft.finance.data.Pattern.PatternList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

public class TablePattern extends TableEncrypted<Event> {
    /**
     * The name of the Patterns table
     */
    protected final static String TableName = Pattern.LIST_NAME;

    /**
     * The table definition
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The pattern list
     */
    private PatternList theList = null;

    /**
     * The accounts list
     */
    private Account.AccountList theAccounts = null;

    /**
     * Constructor
     * @param pDatabase the database control
     */
    protected TablePattern(Database<?> pDatabase) {
        super(pDatabase, TableName);
    }

    /**
     * Define the table columns (called from within super-constructor)
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(TableDefinition pTableDef) {
        /* Define sort column variable */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define Sort Column variables */
        ColumnDefinition myDateCol;
        ColumnDefinition myActCol;

        /* Declare the columns */
        myActCol = theTableDef.addReferenceColumn(Pattern.FIELD_ACCOUNT, TableAccount.TableName);
        myDateCol = theTableDef.addDateColumn(Event.FIELD_DATE);
        theTableDef.addEncryptedColumn(Event.FIELD_DESC, Event.DESCLEN);
        theTableDef.addEncryptedColumn(Event.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        theTableDef.addReferenceColumn(Pattern.FIELD_PARTNER, TableAccount.TableName);
        theTableDef.addReferenceColumn(Event.FIELD_TRNTYP, TableTransactionType.TABLE_NAME);
        theTableDef.addBooleanColumn(Pattern.FIELD_ISCREDIT);
        theTableDef.addReferenceColumn(Pattern.FIELD_FREQ, TableFrequency.TABLE_NAME);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    /* Declare DataSet */
    @Override
    protected void declareData(DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getPatterns();
        theAccounts = myData.getAccounts();
        setList(theList);
    }

    /**
     * postProcess on Load
     */
    @Override
    protected void postProcessOnLoad() throws JDataException {
        theAccounts.validateLoadedAccounts();
    }

    /* Load the pattern */
    @Override
    protected void loadItem(int pId,
                            int pControlId) throws JDataException {
        int myAccountId;
        int myPartnerId;
        int myTranType;
        int myFreq;
        boolean isCredit;
        byte[] myDesc;
        byte[] myAmount;
        Date myDate;

        /* Get the various fields */
        myAccountId = theTableDef.getIntegerValue(Pattern.FIELD_ACCOUNT);
        myDate = theTableDef.getDateValue(Event.FIELD_DATE);
        myDesc = theTableDef.getBinaryValue(Event.FIELD_DESC);
        myAmount = theTableDef.getBinaryValue(Event.FIELD_AMOUNT);
        myPartnerId = theTableDef.getIntegerValue(Pattern.FIELD_PARTNER);
        myTranType = theTableDef.getIntegerValue(Event.FIELD_TRNTYP);
        isCredit = theTableDef.getBooleanValue(Pattern.FIELD_ISCREDIT);
        myFreq = theTableDef.getIntegerValue(Pattern.FIELD_FREQ);

        /* Add into the list */
        theList.addItem(pId, pControlId, myDate, myDesc, myAmount, myAccountId, myPartnerId, myTranType,
                        myFreq, isCredit);

        /* Return to caller */
        return;
    }

    /* Set a field value */
    @Override
    protected void setFieldValue(Event pItem,
                                 JDataField iField) throws JDataException {
        Pattern myItem = (Pattern) pItem;

        /* Switch on field id */
        if (iField == Pattern.FIELD_ACCOUNT) {
            theTableDef.setIntegerValue(Pattern.FIELD_ACCOUNT, myItem.getAccount().getId());
        } else if (iField == Event.FIELD_DATE) {
            theTableDef.setDateValue(Event.FIELD_DATE, pItem.getDate());
        } else if (iField == Event.FIELD_DESC) {
            theTableDef.setBinaryValue(Event.FIELD_DESC, pItem.getDescBytes());
        } else if (iField == Event.FIELD_AMOUNT) {
            theTableDef.setBinaryValue(Event.FIELD_AMOUNT, pItem.getAmountBytes());
        } else if (iField == Pattern.FIELD_PARTNER) {
            theTableDef.setIntegerValue(Pattern.FIELD_PARTNER, myItem.getPartner().getId());
        } else if (iField == Event.FIELD_TRNTYP) {
            theTableDef.setIntegerValue(Event.FIELD_TRNTYP, pItem.getTransType().getId());
        } else if (iField == Pattern.FIELD_ISCREDIT) {
            theTableDef.setBooleanValue(Pattern.FIELD_ISCREDIT, myItem.isCredit());
        } else if (iField == Pattern.FIELD_FREQ) {
            theTableDef.setIntegerValue(Pattern.FIELD_FREQ, myItem.getFrequency().getId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
