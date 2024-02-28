/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.sheets;

import net.sourceforge.joceanus.jprometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusEncryptedDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to an encrypted data type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class PrometheusSheetEncrypted<T extends PrometheusEncryptedDataItem>
        extends PrometheusSheetDataItem<T> {
    /**
     * KeySetId column.
     */
    protected static final int COL_KEYSETID = COL_ID + 1;

    /**
     * Constructor for a load operation.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected PrometheusSheetEncrypted(final PrometheusSheetReader pReader,
                                       final String pRange) {
        /* Pass call on */
        super(pReader, pRange);
    }

    /**
     * Constructor for a write operation.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected PrometheusSheetEncrypted(final PrometheusSheetWriter pWriter,
                                       final String pRange) {
        /* Pass call on */
        super(pWriter, pRange);
    }

    @Override
    protected void insertSecureItem(final T pItem) throws OceanusException {
        super.insertSecureItem(pItem);
        writeInteger(COL_KEYSETID, pItem.getDataKeySetId());
    }

    @Override
    protected PrometheusDataValues getRowValues(final String pName) throws OceanusException {
        /* Allocate the values */
        final PrometheusDataValues myValues = super.getRowValues(pName);

        /* Add the control id and return the new values */
        myValues.addValue(PrometheusCryptographyDataType.DATAKEYSET, loadInteger(COL_KEYSETID));
        return myValues;
    }
}
