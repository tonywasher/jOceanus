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
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.EventBase;
import net.sourceforge.JFinanceApp.data.EventNew;
import net.sourceforge.JFinanceApp.data.EventNew.EventNewList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JGordianKnot.EncryptedData;

/**
 * TableEncrypted extension for Event.
 * @author Tony Washer
 */
public class TableEventNew extends TableEncrypted<EventNew> {
    /**
     * The name of the Events table.
     */
    protected static final String TABLE_NAME = EventNew.LIST_NAME;

    /**
     * The event list.
     */
    private EventNewList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableEventNew(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(EventBase.FIELD_DATE);
        myTableDef.addEncryptedColumn(EventBase.FIELD_DESC, EventBase.DESCLEN);
        myTableDef.addEncryptedColumn(EventBase.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(EventBase.FIELD_DEBIT, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(EventBase.FIELD_CREDIT, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(EventBase.FIELD_TRNTYP, TableTransactionType.TABLE_NAME);

        /* Declare the sort order */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getNewEvents();
        setList(theList);
    }

    /* Load the event */
    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Date myDate = myTableDef.getDateValue(EventBase.FIELD_DATE);
        byte[] myDesc = myTableDef.getBinaryValue(EventBase.FIELD_DESC);
        byte[] myAmount = myTableDef.getBinaryValue(EventBase.FIELD_AMOUNT);
        int myDebitId = myTableDef.getIntegerValue(EventBase.FIELD_DEBIT);
        int myCreditId = myTableDef.getIntegerValue(EventBase.FIELD_CREDIT);
        int myTranType = myTableDef.getIntegerValue(EventBase.FIELD_TRNTYP);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myDate, myDesc, myAmount, myDebitId, myCreditId, myTranType);
    }

    @Override
    protected void setFieldValue(final EventNew pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventBase.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(Event.FIELD_DATE, pItem.getDate());
        } else if (EventBase.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_DESC, pItem.getDescBytes());
        } else if (EventBase.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_AMOUNT, pItem.getAmountBytes());
        } else if (EventBase.FIELD_DEBIT.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_DEBIT, pItem.getDebit().getId());
        } else if (EventBase.FIELD_CREDIT.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_CREDIT, pItem.getCredit().getId());
        } else if (EventBase.FIELD_TRNTYP.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_TRNTYP, pItem.getTransType().getId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
