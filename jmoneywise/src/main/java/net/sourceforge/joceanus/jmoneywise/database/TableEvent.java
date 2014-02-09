/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.database;

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jmetis.viewer.EncryptedData;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableEncrypted extension for Event.
 * @author Tony Washer
 */
public class TableEvent extends TableEncrypted<Event, MoneyWiseDataType> {
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
        ColumnDefinition myDateCol = myTableDef.addDateColumn(EventBase.FIELD_DATE);
        myTableDef.addReferenceColumn(EventBase.FIELD_DEBIT, TableAccount.TABLE_NAME);
        myTableDef.addReferenceColumn(EventBase.FIELD_CREDIT, TableAccount.TABLE_NAME);
        myTableDef.addEncryptedColumn(EventBase.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(EventBase.FIELD_CATEGORY, TableEventCategory.TABLE_NAME);
        myTableDef.addBooleanColumn(EventBase.FIELD_RECONCILED);
        myTableDef.addBooleanColumn(EventBase.FIELD_SPLIT);
        myTableDef.addNullReferenceColumn(EventBase.FIELD_PARENT, TABLE_NAME);

        /* Declare the sort order */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theList = myData.getEvents();
        setList(theList);
    }

    /* Load the event */
    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Event.OBJECT_NAME);
        myValues.addValue(Event.FIELD_DATE, myTableDef.getDateValue(Event.FIELD_DATE));
        myValues.addValue(Event.FIELD_CATEGORY, myTableDef.getIntegerValue(Event.FIELD_CATEGORY));
        myValues.addValue(Event.FIELD_DEBIT, myTableDef.getIntegerValue(Event.FIELD_DEBIT));
        myValues.addValue(Event.FIELD_CREDIT, myTableDef.getIntegerValue(Event.FIELD_CREDIT));
        myValues.addValue(Event.FIELD_AMOUNT, myTableDef.getBinaryValue(Event.FIELD_AMOUNT));
        myValues.addValue(Event.FIELD_RECONCILED, myTableDef.getBooleanValue(Event.FIELD_RECONCILED));
        myValues.addValue(Event.FIELD_SPLIT, myTableDef.getBooleanValue(Event.FIELD_SPLIT));
        myValues.addValue(Event.FIELD_PARENT, myTableDef.getIntegerValue(Event.FIELD_PARENT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Event pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventBase.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (EventBase.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAmountBytes());
        } else if (EventBase.FIELD_DEBIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDebitId());
        } else if (EventBase.FIELD_CREDIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCreditId());
        } else if (EventBase.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (EventBase.FIELD_RECONCILED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isReconciled());
        } else if (EventBase.FIELD_SPLIT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isSplit());
        } else if (EventBase.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and sort the data */
        theList.resolveDataSetLinks();
        theList.reSort();
    }
}
