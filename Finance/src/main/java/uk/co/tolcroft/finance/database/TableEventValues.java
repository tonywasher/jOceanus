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
import uk.co.tolcroft.finance.data.EventValue;
import uk.co.tolcroft.finance.data.EventValue.EventValueList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.DatabaseTable;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

public class TableEventValues extends DatabaseTable<EventValue> {
    /**
     * The name of the EventValues table
     */
    protected final static String TableName = EventValue.LIST_NAME;

    /**
     * The table definition
     */
    private TableDefinition theTableDef; /* Set during load */

    /**
     * The EventValues list
     */
    private EventValueList theList = null;

    /**
     * Constructor
     * @param pDatabase the database control
     */
    protected TableEventValues(Database<?> pDatabase) {
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
        theTableDef.addReferenceColumn(EventValue.FIELD_INFOTYPE, TableEventInfoType.TABLE_NAME);
        myEvtCol = theTableDef.addReferenceColumn(EventValue.FIELD_EVENT, TableEvent.TableName);
        theTableDef.addIntegerColumn(EventValue.FIELD_VALUE);

        /* Declare the sort order */
        myEvtCol.setSortOrder(SortOrder.ASCENDING);
    }

    /* Declare DataSet */
    @Override
    protected void declareData(DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getEventValues();
        setList(theList);
    }

    /* Load the tax year */
    @Override
    public void loadItem(int pId) throws JDataException {
        int myInfoType;
        int myEvent;
        int myValue;

        /* Get the various fields */
        myInfoType = theTableDef.getIntegerValue(EventValue.FIELD_INFOTYPE);
        myEvent = theTableDef.getIntegerValue(EventValue.FIELD_EVENT);
        myValue = theTableDef.getIntegerValue(EventValue.FIELD_VALUE);

        /* Add into the list */
        theList.addItem(pId, myInfoType, myEvent, myValue);
    }

    /* Set a field value */
    @Override
    protected void setFieldValue(EventValue pItem,
                                 JDataField iField) throws JDataException {
        /* Switch on field id */
        if (iField == EventValue.FIELD_INFOTYPE) {
            theTableDef.setIntegerValue(iField, pItem.getInfoType().getId());
        } else if (iField == EventValue.FIELD_EVENT) {
            theTableDef.setIntegerValue(iField, pItem.getEvent().getId());
        } else if (iField == EventValue.FIELD_VALUE) {
            theTableDef.setIntegerValue(iField, pItem.getValue());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

}
