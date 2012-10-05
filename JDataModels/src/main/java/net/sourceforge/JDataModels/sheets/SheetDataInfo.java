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
import net.sourceforge.JDataModels.data.DataInfo;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to a data info type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class SheetDataInfo<T extends DataInfo<T, ?, ?>> extends SheetDataItem<T> {
    /**
     * Number of columns.
     */
    private static final int NUM_COLS = 6;

    /**
     * ControlId column.
     */
    private static final int COL_CONTROL = 1;

    /**
     * InfoType column.
     */
    private static final int COL_INFOTYPE = 2;

    /**
     * Owner column.
     */
    private static final int COL_OWNER = 3;

    /**
     * Value column.
     */
    private static final int COL_VALUE = 4;

    /**
     * Load the Static Data from backup.
     * @param pId the data id
     * @param pControlId the control Id
     * @param pInfoTypeId the info type id
     * @param pOwnerId the owner id
     * @param pValue the value
     * @throws JDataException on error
     */
    protected abstract void loadEncryptedItem(final int pId,
                                              final int pControlId,
                                              final int pInfoTypeId,
                                              final int pOwnerId,
                                              final byte[] pValue) throws JDataException;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected SheetDataInfo(final SheetReader<?> pReader,
                            final String pRange) {
        /* Call super constructor */
        super(pReader, pRange);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected SheetDataInfo(final SheetWriter<?> pWriter,
                            final String pRange) {
        /* Call super constructor */
        super(pWriter, pRange);
    }

    @Override
    protected void loadItem() throws JDataException {
        /* Access the IDs */
        int myID = loadInteger(COL_ID);
        int myControlId = loadInteger(COL_CONTROL);
        int myInfoTypeId = loadInteger(COL_INFOTYPE);
        int myOwnerId = loadInteger(COL_OWNER);

        /* Access the value bytes */
        byte[] myValueBytes = loadBytes(COL_VALUE);

        /* Load the item */
        loadEncryptedItem(myID, myControlId, myInfoTypeId, myOwnerId, myValueBytes);
    }

    @Override
    protected void insertItem(final T pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROL, pItem.getControlKey().getId());
        writeInteger(COL_INFOTYPE, pItem.getInfoTypeId());
        writeInteger(COL_OWNER, pItem.getOwnerId());
        writeBytes(COL_VALUE, pItem.getValueBytes());
    }

    @Override
    protected void preProcessOnWrite() throws JDataException {
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the five columns as the range */
        nameRange(NUM_COLS);
    }
}
