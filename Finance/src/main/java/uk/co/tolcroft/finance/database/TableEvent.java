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
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

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
     * The table definition.
     */
    private TableDefinition theTableDef; /* Set during load */

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
    }

    /**
     * Define the table columns (called from within super-constructor).
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(final TableDefinition pTableDef) {
        /* Define Standard table */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define sort column variable */
        ColumnDefinition myDateCol;

        /* Define the columns */
        myDateCol = theTableDef.addDateColumn(Event.FIELD_DATE);
        theTableDef.addEncryptedColumn(Event.FIELD_DESC, Event.DESCLEN);
        theTableDef.addEncryptedColumn(Event.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        theTableDef.addReferenceColumn(Event.FIELD_DEBIT, TableAccount.TABLE_NAME);
        theTableDef.addReferenceColumn(Event.FIELD_CREDIT, TableAccount.TABLE_NAME);
        theTableDef.addNullEncryptedColumn(Event.FIELD_UNITS, EncryptedData.UNITSLEN);
        theTableDef.addReferenceColumn(Event.FIELD_TRNTYP, TableTransactionType.TABLE_NAME);
        theTableDef.addNullEncryptedColumn(Event.FIELD_TAXCREDIT, EncryptedData.MONEYLEN);
        theTableDef.addNullEncryptedColumn(Event.FIELD_DILUTION, EncryptedData.DILUTELEN);
        theTableDef.addNullIntegerColumn(Event.FIELD_YEARS);

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
    protected void loadItem(final int pId,
                            final int pControlId) throws JDataException {
        /* Get the various fields */
        Date myDate = theTableDef.getDateValue(Event.FIELD_DATE);
        byte[] myDesc = theTableDef.getBinaryValue(Event.FIELD_DESC);
        byte[] myAmount = theTableDef.getBinaryValue(Event.FIELD_AMOUNT);
        int myDebitId = theTableDef.getIntegerValue(Event.FIELD_DEBIT);
        int myCreditId = theTableDef.getIntegerValue(Event.FIELD_CREDIT);
        byte[] myUnits = theTableDef.getBinaryValue(Event.FIELD_UNITS);
        int myTranType = theTableDef.getIntegerValue(Event.FIELD_TRNTYP);
        byte[] myTaxCred = theTableDef.getBinaryValue(Event.FIELD_TAXCREDIT);
        byte[] myDilution = theTableDef.getBinaryValue(Event.FIELD_DILUTION);
        Integer myYears = theTableDef.getIntegerValue(Event.FIELD_YEARS);

        /* Add into the list */
        theList.addItem(pId, pControlId, myDate, myDesc, myAmount, myDebitId, myCreditId, myUnits,
                        myTranType, myTaxCred, myDilution, myYears);
    }

    @Override
    protected void setFieldValue(final Event pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == Event.FIELD_DATE) {
            theTableDef.setDateValue(Event.FIELD_DATE, pItem.getDate());
        } else if (iField == Event.FIELD_DESC) {
            theTableDef.setBinaryValue(Event.FIELD_DESC, pItem.getDescBytes());
        } else if (iField == Event.FIELD_AMOUNT) {
            theTableDef.setBinaryValue(Event.FIELD_AMOUNT, pItem.getAmountBytes());
        } else if (iField == Event.FIELD_DEBIT) {
            theTableDef.setIntegerValue(Event.FIELD_DEBIT, pItem.getDebit().getId());
        } else if (iField == Event.FIELD_CREDIT) {
            theTableDef.setIntegerValue(Event.FIELD_CREDIT, pItem.getCredit().getId());
        } else if (iField == Event.FIELD_UNITS) {
            theTableDef.setBinaryValue(Event.FIELD_UNITS, pItem.getUnitsBytes());
        } else if (iField == Event.FIELD_TRNTYP) {
            theTableDef.setIntegerValue(Event.FIELD_TRNTYP, pItem.getTransType().getId());
        } else if (iField == Event.FIELD_TAXCREDIT) {
            theTableDef.setBinaryValue(Event.FIELD_TAXCREDIT, pItem.getTaxCreditBytes());
        } else if (iField == Event.FIELD_DILUTION) {
            theTableDef.setBinaryValue(Event.FIELD_DILUTION, pItem.getDilutionBytes());
        } else if (iField == Event.FIELD_YEARS) {
            theTableDef.setIntegerValue(Event.FIELD_YEARS, pItem.getYears());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
