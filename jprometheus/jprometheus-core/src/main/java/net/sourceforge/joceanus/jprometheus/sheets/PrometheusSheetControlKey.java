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
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for ControlKey.
 * @author Tony Washer
 */
public class PrometheusSheetControlKey
        extends PrometheusSheetDataItem<ControlKey, CryptographyDataType> {
    /**
     * SheetName for Keys.
     */
    private static final String SHEET_NAME = ControlKey.class.getSimpleName();

    /**
     * KeyData column.
     */
    private static final int COL_HASHPRIME = COL_ID + 1;

    /**
     * KeyData column.
     */
    private static final int COL_PRIMEKEYDATA = COL_HASHPRIME + 1;

    /**
     * KeyData column.
     */
    private static final int COL_ALTKEYDATA = COL_PRIMEKEYDATA + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected PrometheusSheetControlKey(final PrometheusSheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        DataSet<?, ?> myData = pReader.getData();
        setDataList(myData.getControlKeys());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the Spreadsheet writer
     */
    protected PrometheusSheetControlKey(final PrometheusSheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        DataSet<?, ?> myData = pWriter.getData();
        setDataList(myData.getControlKeys());
    }

    @Override
    protected DataValues<CryptographyDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        DataValues<CryptographyDataType> myValues = getRowValues(ControlKey.OBJECT_NAME);
        myValues.addValue(ControlKey.FIELD_HASHPRIME, loadBoolean(COL_HASHPRIME));
        myValues.addValue(ControlKey.FIELD_PRIMEBYTES, loadBytes(COL_PRIMEKEYDATA));
        myValues.addValue(ControlKey.FIELD_ALTBYTES, loadBytes(COL_ALTKEYDATA));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final ControlKey pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeBoolean(COL_HASHPRIME, pItem.isHashPrime());
        writeBytes(COL_PRIMEKEYDATA, pItem.getPrimeHashBytes());
        writeBytes(COL_ALTKEYDATA, pItem.getAltHashBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_ALTKEYDATA;
    }
}
