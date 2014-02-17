/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.sheets;

import net.sourceforge.joceanus.jprometheus.data.ControlData;
import net.sourceforge.joceanus.jprometheus.data.ControlData.ControlDataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for ControlData.
 * @author Tony Washer
 */
public class SheetControlData
        extends SheetDataItem<ControlData, CryptographyDataType> {
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
    protected DataValues<CryptographyDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<CryptographyDataType> myValues = getRowValues(ControlData.OBJECT_NAME);
        myValues.addValue(ControlData.FIELD_DATAVERSION, loadInteger(COL_VERSION));
        myValues.addValue(ControlData.FIELD_CONTROLKEY, loadInteger(COL_CONTROLID));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final ControlData pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_VERSION, pItem.getDataVersion());
        writeInteger(COL_CONTROLID, pItem.getControlKeyId());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CONTROLID;
    }
}
