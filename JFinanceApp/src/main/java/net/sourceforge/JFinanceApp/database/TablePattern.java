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
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.Pattern;
import net.sourceforge.JFinanceApp.data.Pattern.PatternList;
import net.sourceforge.JGordianKnot.EncryptedData;

/**
 * TabelEncrypted extension for Pattern.
 * @author Tony Washer
 */
public class TablePattern extends TableEncrypted<Pattern> {
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
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
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
        theList.addSecureItem(pId, pControlId, myDate, myDesc, myAmount, myAccountId, myPartnerId,
                              myTranType, myFreq, isCredit);
    }

    @Override
    protected void setFieldValue(final Pattern pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Pattern.FIELD_ACCOUNT.equals(iField)) {
            myTableDef.setIntegerValue(Pattern.FIELD_ACCOUNT, pItem.getAccount().getId());
        } else if (Event.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(Event.FIELD_DATE, pItem.getDate());
        } else if (Event.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_DESC, pItem.getDescBytes());
        } else if (Event.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_AMOUNT, pItem.getAmountBytes());
        } else if (Pattern.FIELD_PARTNER.equals(iField)) {
            myTableDef.setIntegerValue(Pattern.FIELD_PARTNER, pItem.getPartner().getId());
        } else if (Event.FIELD_TRNTYP.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_TRNTYP, pItem.getTransType().getId());
        } else if (Pattern.FIELD_ISCREDIT.equals(iField)) {
            myTableDef.setBooleanValue(Pattern.FIELD_ISCREDIT, pItem.isCredit());
        } else if (Pattern.FIELD_FREQ.equals(iField)) {
            myTableDef.setIntegerValue(Pattern.FIELD_FREQ, pItem.getFrequency().getId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
