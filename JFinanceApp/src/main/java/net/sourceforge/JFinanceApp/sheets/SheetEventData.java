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
package net.sourceforge.JFinanceApp.sheets;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.sheets.SheetDataItem;
import net.sourceforge.JDataModels.sheets.SpreadSheet.SheetType;
import net.sourceforge.JFinanceApp.data.EventData;
import net.sourceforge.JFinanceApp.data.EventData.EventDataList;
import net.sourceforge.JFinanceApp.data.FinanceData;

/**
 * SheetDataItem extension for EventData.
 * @author Tony Washer
 */
public class SheetEventData extends SheetDataItem<EventData> {
    /**
     * NamedArea for Events.
     */
    private static final String AREA_EVENTDATA = EventData.LIST_NAME;

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 5;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * Event column.
     */
    private static final int COL_EVENT = 2;

    /**
     * InfoType column.
     */
    private static final int COL_INFOTYPE = 3;

    /**
     * Data column.
     */
    private static final int COL_DATA = 4;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one?
     */
    private boolean isBackup = false;

    /**
     * Events data list.
     */
    private EventDataList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventData(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTDATA);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getEventData();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventData(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTDATA);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Events list */
        theList = pWriter.getData().getEventData();
        setDataList(theList);
    }

    /**
     * Load an item from the spreadsheet.
     * @throws JDataException on error
     */
    @Override
    protected void loadItem() throws JDataException {

        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            int myControlId = loadInteger(COL_CONTROL);
            int myEventId = loadInteger(COL_EVENT);
            int myInfoId = loadInteger(COL_INFOTYPE);

            /* Access the binary values */
            byte[] myValue = loadBytes(COL_DATA);

            /* Load the item */
            theList.addSecureItem(myID, myControlId, myInfoId, myEventId, myValue);

            /* else this is a load from an edit-able spreadsheet */
            // } else {
            /* Access the Account */
            // int myID = loadInteger(0);

            /* Load the item */
            // theList.addItem(myID, myDate, myDesc, myAmount, myDebit, myCredit, myUnits, myTransType,
            // myTaxCredit, myDilution, myYears);
        }
    }

    @Override
    protected void insertItem(final EventData pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());
            writeInteger(COL_EVENT, pItem.getEvent().getId());
            writeInteger(COL_INFOTYPE, pItem.getInfoType().getId());
            writeBytes(COL_DATA, pItem.getValueBytes());

            /* else we are creating an edit-able spreadsheet */
            // } else {
            /* Set the fields */
            // writeInteger(0, pItem.getId());
        }
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
        /* Ignore if we are creating a backup */
        if (isBackup) {
            return;
        }

        /* Write titles */
        // writeString(0, Event.fieldName(Event.FIELD_ID));
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the five columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
            // } else {
            /* Set the four columns as the range */
            // nameRange(4);

            /* Hide the ID column */
            // setHiddenColumn(0);
        }
    }
}
