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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JGordianKnot.EncryptedData;
import uk.co.tolcroft.finance.data.EventData;
import uk.co.tolcroft.finance.data.EventData.EventDataList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

public class TableEventData extends TableEncrypted<EventData> {
    /**
     * The name of the EventData table
     */
    protected final static String TableName = EventData.LIST_NAME;

    /**
     * The table definition
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The event data list
     */
    private EventDataList theList = null;

    /**
     * Constructor
     * @param pDatabase the database control
     */
    protected TableEventData(Database<?> pDatabase) {
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

        /* Define sort column variable */
        ColumnDefinition myEvtCol;

        /* Declare the columns */
        theTableDef.addReferenceColumn(EventData.FIELD_INFOTYPE, TableEventInfoType.TABLE_NAME);
        myEvtCol = theTableDef.addReferenceColumn(EventData.FIELD_EVENT, TableEvent.TableName);
        theTableDef.addEncryptedColumn(EventData.FIELD_VALUE, EncryptedData.MONEYLEN);

        /* Declare the sort order */
        myEvtCol.setSortOrder(SortOrder.ASCENDING);
    }

    /* Declare DataSet */
    @Override
    protected void declareData(DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getEventData();
        setList(theList);
    }

    /* Load the event */
    @Override
    protected void loadItem(int pId,
                            int pControlId) throws JDataException {
        int myInfoTypId;
        int myEventId;
        byte[] myValue;

        /* Get the various fields */
        myInfoTypId = theTableDef.getIntegerValue(EventData.FIELD_INFOTYPE);
        myEventId = theTableDef.getIntegerValue(EventData.FIELD_EVENT);
        myValue = theTableDef.getBinaryValue(EventData.FIELD_VALUE);

        /* Add into the list */
        theList.addItem(pId, pControlId, myInfoTypId, myEventId, myValue);
    }

    /* Set a field value */
    @Override
    protected void setFieldValue(EventData pItem,
                                 JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == EventData.FIELD_INFOTYPE) {
            theTableDef.setIntegerValue(EventData.FIELD_INFOTYPE, pItem.getInfoType().getId());
        } else if (iField == EventData.FIELD_EVENT) {
            theTableDef.setIntegerValue(EventData.FIELD_EVENT, pItem.getEvent().getId());
        } else if (iField == EventData.FIELD_VALUE) {
            theTableDef.setBinaryValue(EventData.FIELD_VALUE, pItem.getValueBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
