/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.prometheus.sheets;

import net.sourceforge.joceanus.prometheus.data.PrometheusControlKey;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * SheetDataItem extension for ControlKey.
 * @author Tony Washer
 */
public class PrometheusSheetControlKey
        extends PrometheusSheetDataItem<PrometheusControlKey> {
    /**
     * SheetName for Keys.
     */
    private static final String SHEET_NAME = PrometheusControlKey.class.getSimpleName();

    /**
     * KeyData column.
     */
    private static final int COL_CREATION = COL_ID + 1;

    /**
     * KeyData column.
     */
    private static final int COL_KEYDATA = COL_CREATION + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected PrometheusSheetControlKey(final PrometheusSheetReader pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        final PrometheusDataSet myData = pReader.getData();
        setDataList(myData.getControlKeys());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the Spreadsheet writer
     */
    protected PrometheusSheetControlKey(final PrometheusSheetWriter pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        final PrometheusDataSet myData = pWriter.getData();
        setDataList(myData.getControlKeys());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(PrometheusControlKey.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.CONTROLKEY_CREATION, loadDate(COL_CREATION));
        myValues.addValue(PrometheusDataResource.CONTROLKEY_LOCKBYTES, loadBytes(COL_KEYDATA));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final PrometheusControlKey pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeDate(COL_CREATION, pItem.getCreationDate());
        writeBytes(COL_KEYDATA, pItem.getLockBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_KEYDATA;
    }
}
