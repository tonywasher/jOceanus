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
import net.sourceforge.JFinanceApp.data.Event.EventList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JGordianKnot.EncryptedData;

/**
 * TableEncrypted extension for Event.
 * @author Tony Washer
 */
public class TableEvent extends TableEncrypted<Event> {
    /**
     * The name of the Events table.
     */
    protected static final String TABLE_NAME = Event.LIST_NAME;

    /**
     * The event list.
     */
    private EventList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableEvent(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Define the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(Event.FIELD_DATE);
        myTableDef.addEncryptedColumn(Event.FIELD_DESC, Event.DESCLEN);
        myTableDef.addEncryptedColumn(Event.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(Event.FIELD_DEBIT, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(Event.FIELD_CREDIT, TableAccount.TABLE_NAME);
        myTableDef.addNullEncryptedColumn(Event.FIELD_UNITS, EncryptedData.UNITSLEN);
        myTableDef.addReferenceColumn(Event.FIELD_TRNTYP, TableTransactionType.TABLE_NAME);
        myTableDef.addNullEncryptedColumn(Event.FIELD_TAXCREDIT, EncryptedData.MONEYLEN);
        myTableDef.addNullEncryptedColumn(Event.FIELD_DILUTION, EncryptedData.DILUTELEN);
        myTableDef.addNullIntegerColumn(Event.FIELD_YEARS);

        /* Declare the sort order */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getEvents();
        setList(theList);
    }

    /* Load the event */
    @Override
    protected void loadItem(final Integer pId,
                            final Integer pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Date myDate = myTableDef.getDateValue(Event.FIELD_DATE);
        byte[] myDesc = myTableDef.getBinaryValue(Event.FIELD_DESC);
        byte[] myAmount = myTableDef.getBinaryValue(Event.FIELD_AMOUNT);
        int myDebitId = myTableDef.getIntegerValue(Event.FIELD_DEBIT);
        int myCreditId = myTableDef.getIntegerValue(Event.FIELD_CREDIT);
        byte[] myUnits = myTableDef.getBinaryValue(Event.FIELD_UNITS);
        int myTranType = myTableDef.getIntegerValue(Event.FIELD_TRNTYP);
        byte[] myTaxCred = myTableDef.getBinaryValue(Event.FIELD_TAXCREDIT);
        byte[] myDilution = myTableDef.getBinaryValue(Event.FIELD_DILUTION);
        Integer myYears = myTableDef.getIntegerValue(Event.FIELD_YEARS);

        /* Add into the list */
        theList.addSecureItem(pId, pControlId, myDate, myDesc, myAmount, myDebitId, myCreditId, myUnits,
                              myTranType, myTaxCred, myDilution, myYears);
    }

    @Override
    protected void setFieldValue(final Event pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Event.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(Event.FIELD_DATE, pItem.getDate());
        } else if (Event.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_DESC, pItem.getDescBytes());
        } else if (Event.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_AMOUNT, pItem.getAmountBytes());
        } else if (Event.FIELD_DEBIT.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_DEBIT, pItem.getDebit().getId());
        } else if (Event.FIELD_CREDIT.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_CREDIT, pItem.getCredit().getId());
        } else if (Event.FIELD_UNITS.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_UNITS, pItem.getUnitsBytes());
        } else if (Event.FIELD_TRNTYP.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_TRNTYP, pItem.getTransType().getId());
        } else if (Event.FIELD_TAXCREDIT.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_TAXCREDIT, pItem.getTaxCreditBytes());
        } else if (Event.FIELD_DILUTION.equals(iField)) {
            myTableDef.setBinaryValue(Event.FIELD_DILUTION, pItem.getDilutionBytes());
        } else if (Event.FIELD_YEARS.equals(iField)) {
            myTableDef.setIntegerValue(Event.FIELD_YEARS, pItem.getYears());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
