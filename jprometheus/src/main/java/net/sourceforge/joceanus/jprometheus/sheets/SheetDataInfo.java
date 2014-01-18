/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jdatamodels.sheets;

import net.sourceforge.joceanus.jdatamodels.data.DataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to a data info type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class SheetDataInfo<T extends DataInfo<T, ?, ?, ?>>
        extends SheetDataItem<T> {
    /**
     * InfoType column.
     */
    private static final int COL_INFOTYPE = COL_CONTROLID + 1;

    /**
     * Owner column.
     */
    private static final int COL_OWNER = COL_INFOTYPE + 1;

    /**
     * Value column.
     */
    private static final int COL_VALUE = COL_OWNER + 1;

    /**
     * Load the Static Data from backup.
     * @param pId the data id
     * @param pControlId the control Id
     * @param pInfoTypeId the info type id
     * @param pOwnerId the owner id
     * @param pValue the value
     * @throws JOceanusException on error
     */
    protected abstract void loadEncryptedItem(final Integer pId,
                                              final Integer pControlId,
                                              final Integer pInfoTypeId,
                                              final Integer pOwnerId,
                                              final byte[] pValue) throws JOceanusException;

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
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myInfoTypeId = loadInteger(COL_INFOTYPE);
        Integer myOwnerId = loadInteger(COL_OWNER);

        /* Access the value bytes */
        byte[] myValueBytes = loadBytes(COL_VALUE);

        /* Load the item */
        loadEncryptedItem(pId, myControlId, myInfoTypeId, myOwnerId, myValueBytes);
    }

    @Override
    protected void insertSecureItem(final T pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeInteger(COL_INFOTYPE, pItem.getInfoTypeId());
        writeInteger(COL_OWNER, pItem.getOwnerId());
        writeBytes(COL_VALUE, pItem.getValueBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_VALUE;
    }
}
