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

import net.sourceforge.joceanus.jprometheus.lethe.data.DataKey;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet.CryptographyDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for DataKey.
 * @author Tony Washer
 */
public class PrometheusSheetDataKey
        extends PrometheusSheetDataItem<DataKey, CryptographyDataType> {
    /**
     * SheetName for Keys.
     */
    private static final String SHEET_NAME = DataKey.class.getSimpleName();

    /**
     * KeySetId column.
     */
    private static final int COL_KEYSETID = COL_ID + 1;

    /**
     * isHashPrime column.
     */
    private static final int COL_HASHPRIME = COL_KEYSETID + 1;

    /**
     * isSymKey column.
     */
    private static final int COL_ISSYMKEY = COL_HASHPRIME + 1;

    /**
     * KeyType column.
     */
    private static final int COL_KEYTYPE = COL_ISSYMKEY + 1;

    /**
     * KeyData column.
     */
    private static final int COL_KEYDATA = COL_KEYTYPE + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected PrometheusSheetDataKey(final PrometheusSheetReader<?> pReader) {
        /* Call super constructor */
        super(pReader, SHEET_NAME);

        /* Access the Lists */
        final DataSet<?, ?> myData = pReader.getData();
        setDataList(myData.getDataKeys());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected PrometheusSheetDataKey(final PrometheusSheetWriter<?> pWriter) {
        /* Call super constructor */
        super(pWriter, SHEET_NAME);

        /* Access the Control list */
        final DataSet<?, ?> myData = pWriter.getData();
        setDataList(myData.getDataKeys());
    }

    @Override
    protected DataValues<CryptographyDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<CryptographyDataType> myValues = getRowValues(DataKey.OBJECT_NAME);
        myValues.addValue(DataKey.FIELD_KEYSET, loadInteger(COL_KEYSETID));
        myValues.addValue(DataKey.FIELD_HASHPRIME, loadBoolean(COL_HASHPRIME));
        myValues.addValue(DataKey.FIELD_KEYTYPE, loadInteger(COL_KEYTYPE));
        myValues.addValue(DataKey.FIELD_KEYDEF, loadBytes(COL_KEYDATA));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final DataKey pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_KEYSETID, pItem.getDataKeySetId());
        writeBoolean(COL_HASHPRIME, pItem.isHashPrime());
        writeInteger(COL_KEYTYPE, pItem.getKeyTypeId());
        writeBytes(COL_KEYDATA, pItem.getSecuredKeyDef());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_KEYDATA;
    }
}
