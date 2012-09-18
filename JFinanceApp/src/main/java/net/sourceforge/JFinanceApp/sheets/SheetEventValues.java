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
import net.sourceforge.JFinanceApp.data.EventValue;
import net.sourceforge.JFinanceApp.data.EventValue.EventValueList;
import net.sourceforge.JFinanceApp.data.FinanceData;

/**
 * SheetDataItem extension for Event Values.
 * @author Tony Washer
 */
public class SheetEventValues extends SheetDataItem<EventValue> {
    /**
     * NamedArea for EventValues.
     */
    private static final String AREA_EVENTVALUES = EventValue.LIST_NAME;

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 4;

    /**
     * Event column.
     */
    private static final int COL_EVENT = 1;

    /**
     * InfoType column.
     */
    private static final int COL_INFOTYPE = 2;

    /**
     * Value column.
     */
    private static final int COL_VALUE = 3;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one?
     */
    private boolean isBackup = false;

    /**
     * EventValue data list.
     */
    private EventValueList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventValues(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTVALUES);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getEventValues();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventValues(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTVALUES);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the TaxYears list */
        theList = pWriter.getData().getEventValues();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws JDataException {

        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            int myEventId = loadInteger(COL_EVENT);
            int myInfoId = loadInteger(COL_INFOTYPE);
            int myValue = loadInteger(COL_VALUE);

            /* Add the Value */
            theList.addOpenItem(myID, myInfoId, myEventId, myValue);

            /* else this is a load from an edit-able spreadsheet */
            // } else {
            /* Access the ID */
            // int myID = loadInteger(0);
            // int myEventId = loadInteger(2);

            /* Access the Strings */
            // String myInfoType = loadString(1);

            /* Access the value */
            // int myValue = loadInteger(3);

            /* Add the Tax Year */
            // theList.addItem(myID,
        }
    }

    @Override
    protected void insertItem(final EventValue pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_INFOTYPE, pItem.getInfoType().getId());
            writeInteger(COL_EVENT, pItem.getEvent().getId());
            writeInteger(COL_VALUE, pItem.getValue());

            /* else we are creating an edit-able spreadsheet */
            // } else {
            /* Set the fields */
            /* writeInteger(COL_ID, pItem.getId()); */
        }
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
        /* Ignore if we are creating a backup */
        if (isBackup) {
            return;
        }

        /* Write titles */
        // writeString(0, TaxYear.fieldName(TaxYear.FIELD_ID));
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the four columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
            // }else {
            /* Set the four columns as the range */
            // nameRange(4);

            /* Set the Id column as hidden */
            // setHiddenColumn(0);

            /* Set the String column width */
            // setColumnWidth(2, StaticData.NAMELEN);
        }
    }
}
