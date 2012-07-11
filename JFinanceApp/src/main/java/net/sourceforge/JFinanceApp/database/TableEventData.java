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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.database.ColumnDefinition;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JDataModels.database.TableDefinition;
import net.sourceforge.JDataModels.database.TableDefinition.SortOrder;
import net.sourceforge.JDataModels.database.TableEncrypted;
import net.sourceforge.JFinanceApp.data.EventData;
import net.sourceforge.JFinanceApp.data.EventData.EventDataList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JGordianKnot.EncryptedData;

/**
 * TableEncrypted extension for EventData.
 * @author Tony Washer
 */
public class TableEventData extends TableEncrypted<EventData> {
    /**
     * The name of the EventData table.
     */
    protected static final String TABLE_NAME = EventData.LIST_NAME;

    /**
     * The event data list.
     */
    private EventDataList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableEventData(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addReferenceColumn(EventData.FIELD_INFOTYPE, TableEventInfoType.TABLE_NAME);
        ColumnDefinition myEvtCol = myTableDef.addReferenceColumn(EventData.FIELD_EVENT,
                                                                  TableEvent.TABLE_NAME);
        myTableDef.addEncryptedColumn(EventData.FIELD_VALUE, EncryptedData.MONEYLEN);

        /* Declare the sort order */
        myEvtCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getEventData();
        setList(theList);
    }

    @Override
    protected void loadItem(final int pId,
                            final int pControlId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        int myInfoTypId = myTableDef.getIntegerValue(EventData.FIELD_INFOTYPE);
        int myEventId = myTableDef.getIntegerValue(EventData.FIELD_EVENT);
        byte[] myValue = myTableDef.getBinaryValue(EventData.FIELD_VALUE);

        /* Add into the list */
        theList.addItem(pId, pControlId, myInfoTypId, myEventId, myValue);
    }

    @Override
    protected void setFieldValue(final EventData pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventData.FIELD_INFOTYPE.equals(iField)) {
            myTableDef.setIntegerValue(EventData.FIELD_INFOTYPE, pItem.getInfoType().getId());
        } else if (EventData.FIELD_EVENT.equals(iField)) {
            myTableDef.setIntegerValue(EventData.FIELD_EVENT, pItem.getEvent().getId());
        } else if (EventData.FIELD_VALUE.equals(iField)) {
            myTableDef.setBinaryValue(EventData.FIELD_VALUE, pItem.getValueBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
