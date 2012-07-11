/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.sheets;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.StaticData;
import net.sourceforge.JDataModels.sheets.SpreadSheet.SheetType;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to a static data type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class SheetStaticData<T extends StaticData<T, ?>> extends SheetDataItem<T> {
    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 6;

    /**
     * ControlId column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * Enabled column.
     */
    private static final int COL_ENABLED = 2;

    /**
     * Order column.
     */
    private static final int COL_ORDER = 3;

    /**
     * Name column.
     */
    private static final int COL_NAME = 4;

    /**
     * Description column.
     */
    private static final int COL_DESC = 5;

    /**
     * Load the Static Data from backup.
     * @param pId the data id
     * @param pControlId the control Id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JDataException on error
     */
    protected abstract void loadEncryptedItem(final int pId,
                                              final int pControlId,
                                              final boolean isEnabled,
                                              final int iOrder,
                                              final byte[] pName,
                                              final byte[] pDesc) throws JDataException;

    /**
     * Load the static data from extract.
     * @param pId the Id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JDataException on error
     */
    protected abstract void loadClearTextItem(final int pId,
                                              final boolean isEnabled,
                                              final int iOrder,
                                              final String pName,
                                              final String pDesc) throws JDataException;

    /**
     * Is the spreadsheet a backup spreadsheet or an edit-able one.
     */
    private boolean isBackup = false;

    /**
     * The name of the items.
     */
    private String theNames = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected SheetStaticData(final SheetReader<?> pReader,
                              final String pRange) {
        /* Call super constructor */
        super(pReader, pRange);

        /* Note whether this is a backup */
        isBackup = (pReader.getType() == SheetType.BACKUP);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     * @param pNames the name range to create
     */
    protected SheetStaticData(final SheetWriter<?> pWriter,
                              final String pRange,
                              final String pNames) {
        /* Call super constructor */
        super(pWriter, pRange);

        /* Record the names */
        theNames = pNames;

        /* Note whether this is a backup */
        isBackup = (pWriter.getType() == SheetType.BACKUP);
    }

    @Override
    protected void loadItem() throws JDataException {
        /* If this is a backup load */
        if (isBackup) {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            int myControlId = loadInteger(COL_CONTROL);
            boolean myEnabled = loadBoolean(COL_ENABLED);
            int myOrder = loadInteger(COL_ORDER);

            /* Access the name and description bytes */
            byte[] myNameBytes = loadBytes(COL_NAME);
            byte[] myDescBytes = loadBytes(COL_DESC);

            /* Load the item */
            loadEncryptedItem(myID, myControlId, myEnabled, myOrder, myNameBytes, myDescBytes);

            /* else this is a load from an edit-able spreadsheet */
        } else {
            /* Access the IDs */
            int myID = loadInteger(COL_ID);
            boolean myEnabled = loadBoolean(COL_ENABLED - 1);
            int myOrder = loadInteger(COL_ORDER - 1);

            /* Access the name and description bytes */
            String myName = loadString(COL_NAME - 1);
            String myDesc = loadString(COL_DESC - 1);

            /* Load the item */
            loadClearTextItem(myID, myEnabled, myOrder, myName, myDesc);
        }
    }

    @Override
    protected void insertItem(final T pItem) throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeInteger(COL_CONTROL, pItem.getControlKey().getId());
            writeBoolean(COL_ENABLED, pItem.getEnabled());
            writeInteger(COL_ORDER, pItem.getOrder());
            writeBytes(COL_NAME, pItem.getNameBytes());
            writeBytes(COL_DESC, pItem.getDescBytes());

            /* else we are creating an edit-able spreadsheet */
        } else {
            /* Set the fields */
            writeInteger(COL_ID, pItem.getId());
            writeBoolean(COL_ENABLED - 1, pItem.getEnabled());
            writeInteger(COL_ORDER - 1, pItem.getOrder());
            writeString(COL_NAME, pItem.getName());
            writeString(COL_DESC, pItem.getDesc());
        }
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
        /* Ignore if this is a backup */
        if (isBackup) {
            return;
        }

        /* Create a new row */
        newRow();

        /* Write titles */
        writeHeader(COL_ID, DataItem.FIELD_ID.getName());
        writeHeader(COL_ORDER - 1, StaticData.FIELD_ORDER.getName());
        writeHeader(COL_ENABLED - 1, StaticData.FIELD_ENABLED.getName());
        writeHeader(COL_NAME - 1, StaticData.FIELD_NAME.getName());
        writeHeader(COL_DESC - 1, StaticData.FIELD_DESC.getName());

        /* Adjust for Header */
        adjustForHeader();
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* If we are creating a backup */
        if (isBackup) {
            /* Set the six columns as the range */
            nameRange(NUM_COLS);

            /* else this is an edit-able spreadsheet */
        } else {
            /* Set the five columns as the range */
            nameRange(NUM_COLS - 1);

            /* Set the Id column as hidden */
            setHiddenColumn(COL_ID);

            /* Set default column types */
            setIntegerColumn(COL_ID);
            setBooleanColumn(COL_ENABLED - 1);
            setIntegerColumn(COL_ORDER - 1);

            /* Set the name column width and range */
            nameColumnRange(COL_NAME - 1, theNames);
            setColumnWidth(COL_NAME - 1, StaticData.NAMELEN);

            /* Set description column width */
            setColumnWidth(COL_DESC - 1, StaticData.DESCLEN);
        }
    }
}
