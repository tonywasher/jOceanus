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
package uk.co.tolcroft.models.sheets;

import uk.co.tolcroft.models.data.ControlData;
import uk.co.tolcroft.models.data.ControlData.ControlDataList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public class SheetControl extends SheetDataItem<ControlData> {
    /**
     * SheetName for ControlData
     */
    private static final String Control = ControlData.class.getSimpleName();

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one
     */
    private boolean isBackup = false;

    /**
     * ControlData data list
     */
    private ControlDataList theList = null;

    /**
     * DataSet
     */
    private DataSet<?> theData = null;

    /**
     * Constructor for loading a spreadsheet
     * @param pReader the spreadsheet reader
     */
    protected SheetControl(SheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, Control);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);

        /* Access the Lists */
        theData = pReader.getData();
        theList = theData.getControlData();
    }

    /**
     * Constructor for creating a spreadsheet
     * @param pWriter the spreadsheet writer
     */
    protected SheetControl(SheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, Control);

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
            int myID = loadInteger(0);
            int myVersion = loadInteger(1);

            /* Access the Control Key */
            int myControl = loadInteger(2);

            /* Add the Control */
            theList.addItem(myID, myVersion, myControl);
        }

        /* else this is plain text */
        else {
            /* Access the Version */
            int myID = loadInteger(0);
            int myVersion = loadInteger(1);

            /* Add the Control */
            theList.addItem(myID, myVersion);
        }
    }

    @Override
    protected void insertItem(ControlData pItem) throws Exception {
        /* If this is a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(0, pItem.getId());
            writeInteger(1, pItem.getDataVersion());
            writeInteger(2, pItem.getControlKey().getId());
        }

        /* else just write the data version */
        else {
            writeInteger(0, pItem.getId());
            writeInteger(1, pItem.getDataVersion());
        }
    }

    @Override
    protected void preProcessOnWrite() throws Exception {
        /* Ignore if this is a backup */
        if (isBackup)
            return;

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(0, DataItem.FIELD_ID.getName());
        writeHeader(1, ControlData.FIELD_VERSION.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws Exception {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the three columns as the range */
            nameRange(3);
        }

        /* else */
        else {
            /* Set the two columns as the range */
            nameRange(2);

            /* Set the Id column as hidden */
            setHiddenColumn(0);

            /* Set default column types */
            setIntegerColumn(0);
            setIntegerColumn(1);
        }
    }
}
