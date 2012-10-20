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

import javax.swing.SortOrder;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.database.ColumnDefinition;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JDataModels.database.DatabaseTable;
import net.sourceforge.JDataModels.database.TableDefinition;
import net.sourceforge.JFinanceApp.data.EventValue;
import net.sourceforge.JFinanceApp.data.EventValue.EventValueList;
import net.sourceforge.JFinanceApp.data.FinanceData;

/**
 * DatabaseTable extension for EventValue.
 * @author Tony Washer
 */
public class TableEventValues extends DatabaseTable<EventValue> {
    /**
     * The name of the EventValues table.
     */
    protected static final String TABLE_NAME = EventValue.LIST_NAME;

    /**
     * The EventValues list.
     */
    private EventValueList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableEventValues(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addReferenceColumn(EventValue.FIELD_INFOTYPE, TableEventInfoType.TABLE_NAME);
        ColumnDefinition myEvtCol = myTableDef.addReferenceColumn(EventValue.FIELD_EVENT,
                                                                  TableEvent.TABLE_NAME);
        myTableDef.addIntegerColumn(EventValue.FIELD_VALUE);

        /* Declare the sort order */
        myEvtCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getEventValues();
        setList(theList);
    }

    @Override
    public void loadItem(final Integer pId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        int myInfoType = myTableDef.getIntegerValue(EventValue.FIELD_INFOTYPE);
        int myEvent = myTableDef.getIntegerValue(EventValue.FIELD_EVENT);
        int myValue = myTableDef.getIntegerValue(EventValue.FIELD_VALUE);

        /* Add into the list */
        theList.addOpenItem(pId, myInfoType, myEvent, myValue);
    }

    @Override
    protected void setFieldValue(final EventValue pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (EventValue.FIELD_INFOTYPE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getInfoType().getId());
        } else if (EventValue.FIELD_EVENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getEvent().getId());
        } else if (EventValue.FIELD_VALUE.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getValue());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }

}
