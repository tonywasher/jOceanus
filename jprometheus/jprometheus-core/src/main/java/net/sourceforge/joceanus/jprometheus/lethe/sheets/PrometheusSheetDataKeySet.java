/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.sheets;

import net.sourceforge.joceanus.jprometheus.lethe.data.DataKeySet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for DataKeySet.
 * @author Tony Washer
 */
public class PrometheusSheetDataKeySet
        extends PrometheusSheetDataItem<DataKeySet, CryptographyDataType> {
    /**
     * SheetName for KeySets.
     */
    private static final String SHEET_NAME = DataKeySet.class.getSimpleName();

    /**
     * ControlId column.
     */
    private static final int COL_CONTROL = COL_ID + 1;

    /**
     * CreationDate column.
     */
    private static final int COL_CREATEDATE = COL_CONTROL + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected PrometheusSheetDataKeySet(final PrometheusSheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        final DataSet<?, ?> myData = pReader.getData();
        setDataList(myData.getDataKeySets());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the Spreadsheet writer
     */
    protected PrometheusSheetDataKeySet(final PrometheusSheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        final DataSet<?, ?> myData = pWriter.getData();
        setDataList(myData.getDataKeySets());
    }

    @Override
    protected DataValues<CryptographyDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<CryptographyDataType> myValues = getRowValues(DataKeySet.OBJECT_NAME);
        myValues.addValue(DataKeySet.FIELD_CONTROLKEY, loadInteger(COL_CONTROL));
        myValues.addValue(DataKeySet.FIELD_CREATEDATE, loadDate(COL_CREATEDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final DataKeySet pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_CONTROL, pItem.getControlKeyId());
        writeDate(COL_CREATEDATE, pItem.getCreationDate());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CREATEDATE;
    }
}
