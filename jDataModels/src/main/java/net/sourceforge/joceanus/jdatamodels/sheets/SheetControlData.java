/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamodels.data.ControlData;
import net.sourceforge.joceanus.jdatamodels.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;

/**
 * SheetDataItem extension for ControlData.
 * @author Tony Washer
 */
public class SheetControlData
        extends SheetDataItem<ControlData> {
    /**
     * SheetName for ControlData.
     */
    private static final String SHEET_NAME = ControlData.class.getSimpleName();

    /**
     * Version column.
     */
    private static final int COL_VERSION = COL_ID + 1;

    /**
     * ControlKey column.
     */
    private static final int COL_CONTROLID = COL_VERSION + 1;

    /**
     * ControlData data list.
     */
    private ControlDataList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetControlData(final SheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        DataSet<?, ?> myData = pReader.getData();
        theList = myData.getControlData();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetControlData(final SheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        theList = pWriter.getData().getControlData();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myVersion = loadInteger(COL_VERSION);

        /* Access the Control Key */
        Integer myControl = loadInteger(COL_CONTROLID);

        /* Add the Control */
        theList.addSecureItem(pId, myVersion, myControl);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the specific values */
        Integer myVersion = loadInteger(COL_VERSION);

        /* Add the Control */
        theList.addOpenItem(pId, myVersion);
    }

    @Override
    protected void insertSecureItem(final ControlData pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_VERSION, pItem.getDataVersion());
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
    }

    @Override
    protected void insertOpenItem(final ControlData pItem) throws JDataException {
        writeInteger(COL_VERSION, pItem.getDataVersion());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_VERSION, ControlData.FIELD_VERSION.getName());
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set default column types */
        setIntegerColumn(COL_VERSION);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return isBackup()
                ? COL_CONTROLID
                : COL_VERSION;
    }
}
