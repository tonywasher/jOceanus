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

import net.sourceforge.joceanus.jdatamodels.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to a static data type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class SheetStaticData<T extends StaticData<T, ?>>
        extends SheetDataItem<T> {
    /**
     * Enabled column.
     */
    private static final int COL_ENABLED = COL_CONTROLID + 1;

    /**
     * Order column.
     */
    private static final int COL_ORDER = COL_ENABLED + 1;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_ORDER + 1;

    /**
     * Description column.
     */
    protected static final int COL_DESC = COL_NAME + 1;

    /**
     * Load the Static Data from backup.
     * @param pId the data id
     * @param pControlId the control Id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JOceanusException on error
     */
    protected abstract void loadEncryptedItem(final Integer pId,
                                              final Integer pControlId,
                                              final Boolean isEnabled,
                                              final Integer iOrder,
                                              final byte[] pName,
                                              final byte[] pDesc) throws JOceanusException;

    /**
     * Load the static data from extract.
     * @param pId the Id
     * @param isEnabled isEnabled
     * @param iOrder the sort order
     * @param pName the name
     * @param pDesc the description
     * @throws JOceanusException on error
     */
    protected abstract void loadClearTextItem(final Integer pId,
                                              final Boolean isEnabled,
                                              final Integer iOrder,
                                              final String pName,
                                              final String pDesc) throws JOceanusException;

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
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Integer myControlId = loadInteger(COL_CONTROLID);
        Boolean myEnabled = loadBoolean(COL_ENABLED);
        Integer myOrder = loadInteger(COL_ORDER);

        /* Access the name and description bytes */
        byte[] myNameBytes = loadBytes(COL_NAME);
        byte[] myDescBytes = loadBytes(COL_DESC);

        /* Load the item */
        loadEncryptedItem(pId, myControlId, myEnabled, myOrder, myNameBytes, myDescBytes);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JOceanusException {
        /* Access the IDs */
        Boolean myEnabled = loadBoolean(COL_ENABLED);
        Integer myOrder = loadInteger(COL_ORDER);

        /* Access the name and description bytes */
        String myName = loadString(COL_NAME);
        String myDesc = loadString(COL_DESC);

        /* Load the item */
        loadClearTextItem(pId, myEnabled, myOrder, myName, myDesc);
    }

    @Override
    protected void insertSecureItem(final T pItem) throws JOceanusException {
        /* Set the fields */
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
        writeBoolean(COL_ENABLED, pItem.getEnabled());
        writeInteger(COL_ORDER, pItem.getOrder());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected void insertOpenItem(final T pItem) throws JOceanusException {
        /* Set the fields */
        writeBoolean(COL_ENABLED, pItem.getEnabled());
        writeInteger(COL_ORDER, pItem.getOrder());
        writeString(COL_NAME, pItem.getName());
        writeString(COL_DESC, pItem.getDesc());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_ORDER, StaticData.FIELD_ORDER.getName());
        writeHeader(COL_ENABLED, StaticData.FIELD_ENABLED.getName());
        writeHeader(COL_NAME, StaticData.FIELD_NAME.getName());
        writeHeader(COL_DESC, StaticData.FIELD_DESC.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
        /* Set default column types */
        setBooleanColumn(COL_ENABLED);
        setIntegerColumn(COL_ORDER);
        setStringColumn(COL_NAME);
        setStringColumn(COL_DESC);

        /* Set the name column range */
        nameColumnRange(COL_NAME, theNames);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }
}
