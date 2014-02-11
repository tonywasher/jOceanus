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

import net.sourceforge.joceanus.jprometheus.data.ControlKey;
import net.sourceforge.joceanus.jprometheus.data.ControlKey.ControlKeyList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for ControlKey.
 * @author Tony Washer
 */
public class SheetControlKey
        extends SheetDataItem<ControlKey, CryptographyDataType> {
    /**
     * SheetName for Keys.
     */
    private static final String SHEET_NAME = ControlKey.class.getSimpleName();

    /**
     * KeyData column.
     */
    private static final int COL_KEYDATA = COL_ID + 1;

    /**
     * ControlKey data list.
     */
    private ControlKeyList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetControlKey(final SheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        DataSet<?, ?> myData = pReader.getData();
        theList = myData.getControlKeys();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the Spreadsheet writer
     */
    protected SheetControlKey(final SheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        theList = pWriter.getData().getControlKeys();
        setDataList(theList);
    }

    @Override
    protected DataValues<CryptographyDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<CryptographyDataType> myValues = getRowValues(ControlKey.OBJECT_NAME);
        myValues.addValue(ControlKey.FIELD_PASSHASH, loadBytes(COL_KEYDATA));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final ControlKey pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeBytes(COL_KEYDATA, pItem.getHashBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_KEYDATA;
    }
}
