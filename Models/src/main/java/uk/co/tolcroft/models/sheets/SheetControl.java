/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.ControlData;
import uk.co.tolcroft.models.data.ControlData.ControlDataList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

/**
 * SheetDataItem extension for ControlData.
 * @author Tony Washer
 */
public class SheetControl extends SheetDataItem<ControlData> {
    /**
     * SheetName for ControlData.
     */
    private static final String SHEET_NAME = ControlData.class.getSimpleName();

    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 3;

    /**
     * Version column.
     */
    private static final int COL_VERSION = 1;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROL = 2;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one.
     */
    private boolean isBackup = false;

    /**
     * ControlData data list.
     */
    private ControlDataList theList = null;

    /**
     * DataSet.
     */
    private DataSet<?> theData = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetControl(final SheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        theData = pReader.getData();
        theList = theData.getControlData();
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetControl(final SheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);

        /* Access the Control list */
        theList = pWriter.getData().getControlData();
        setDataList(theList);
    }

    @Override
    protected void loadItem() throws Exception {
        /* If this is a backup */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            int myVersion = loadInteger(COL_VERSION);

            /* Access the Control Key */
            int myControl = loadInteger(COL_CONTROL);

            /* Add the Control */
            theList.addItem(myID, myVersion, myControl);

            /* else this is plain text */
        } else {
            /* Access the Version */
            int myID = loadInteger(COL_ID);
            int myVersion = loadInteger(COL_VERSION);

            /* Add the Control */
            theList.addItem(myID, myVersion);
        }
    }

    @Override
    protected void insertItem(final ControlData pItem) throws Exception {
        /* If this is a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_VERSION, pItem.getDataVersion());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());

            /* else just write the data version */
        } else {
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_VERSION, pItem.getDataVersion());
        }
    }

    @Override
    protected void preProcessOnWrite() throws Exception {
        /* Ignore if this is a backup */
        if (isBackup) {
            return;
        }

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(COL_ID, DataItem.FIELD_ID.getName());
        writeHeader(COL_VERSION, ControlData.FIELD_VERSION.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws Exception {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the three columns as the range */
            nameRange(NUM_COLS);

            /* else */
        } else {
            /* Set the two columns as the range */
            nameRange(NUM_COLS - 1);

            /* Set the Id column as hidden */
            setHiddenColumn(COL_ID);

            /* Set default column types */
            setIntegerColumn(COL_ID);
            setIntegerColumn(COL_VERSION);
        }
    }
}
