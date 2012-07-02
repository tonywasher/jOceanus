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

/**
 * TabelEncrypted extension for Pattern.
 * @author Tony Washer
 */
public class TablePattern extends TableEncrypted<Event> {
    /**
     * The name of the Patterns table.
     */
    protected static final String TABLE_NAME = Pattern.LIST_NAME;

    /**
     * The pattern list.
     */
    private PatternList theList = null;

    /**
     * The accounts list.
     */
    private Account.AccountList theAccounts = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePattern(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myActCol = myTableDef.addReferenceColumn(Pattern.FIELD_ACCOUNT,
                                                                  TableAccount.TABLE_NAME);
        ColumnDefinition myDateCol = myTableDef.addDateColumn(Event.FIELD_DATE);
        myTableDef.addEncryptedColumn(Event.FIELD_DESC, Event.DESCLEN);
        myTableDef.addEncryptedColumn(Event.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(Pattern.FIELD_PARTNER, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(Event.FIELD_TRNTYP, TableTransactionType.TABLE_NAME);
        myTableDef.addBooleanColumn(Pattern.FIELD_ISCREDIT);
        myTableDef.addReferenceColumn(Pattern.FIELD_FREQ, TableFrequency.TABLE_NAME);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getPatterns();
        theAccounts = myData.getAccounts();
        setList(theList);
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        theAccounts.validateLoadedAccounts();
    }

    @Override
    protected void loadItem(final int pId,
                            final int pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        int myAccountId = myTableDef.getIntegerValue(Pattern.FIELD_ACCOUNT);
        Date myDate = myTableDef.getDateValue(Event.FIELD_DATE);
        byte[] myDesc = myTableDef.getBinaryValue(Event.FIELD_DESC);
        byte[] myAmount = myTableDef.getBinaryValue(Event.FIELD_AMOUNT);
        int myPartnerId = myTableDef.getIntegerValue(Pattern.FIELD_PARTNER);
        int myTranType = myTableDef.getIntegerValue(Event.FIELD_TRNTYP);
        boolean isCredit = myTableDef.getBooleanValue(Pattern.FIELD_ISCREDIT);
        int myFreq = myTableDef.getIntegerValue(Pattern.FIELD_FREQ);

        /* Add into the list */
        theList.addItem(pId, pControlId, myDate, myDesc, myAmount, myAccountId, myPartnerId, myTranType,
                        myFreq, isCredit);
    }

    @Override
    protected void setFieldValue(final Event pItem,
                                 final JDataField iField) throws JDataException {
        /* Can only handle a pattern */
        if (!(pItem instanceof Pattern)) {
            return;
        }

        Pattern myItem = (Pattern) pItem;

        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (iField == Pattern.FIELD_ACCOUNT) {
            myTableDef.setIntegerValue(Pattern.FIELD_ACCOUNT, myItem.getAccount().getId());
        } else if (iField == Event.FIELD_DATE) {
            myTableDef.setDateValue(Event.FIELD_DATE, pItem.getDate());
        } else if (iField == Event.FIELD_DESC) {
            myTableDef.setBinaryValue(Event.FIELD_DESC, pItem.getDescBytes());
        } else if (iField == Event.FIELD_AMOUNT) {
            myTableDef.setBinaryValue(Event.FIELD_AMOUNT, pItem.getAmountBytes());
        } else if (iField == Pattern.FIELD_PARTNER) {
            myTableDef.setIntegerValue(Pattern.FIELD_PARTNER, myItem.getPartner().getId());
        } else if (iField == Event.FIELD_TRNTYP) {
            myTableDef.setIntegerValue(Event.FIELD_TRNTYP, pItem.getTransType().getId());
        } else if (iField == Pattern.FIELD_ISCREDIT) {
            myTableDef.setBooleanValue(Pattern.FIELD_ISCREDIT, myItem.isCredit());
        } else if (iField == Pattern.FIELD_FREQ) {
            myTableDef.setIntegerValue(Pattern.FIELD_FREQ, myItem.getFrequency().getId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
