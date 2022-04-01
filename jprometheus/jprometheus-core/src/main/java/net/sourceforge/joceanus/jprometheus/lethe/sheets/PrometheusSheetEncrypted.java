/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to an encrypted data type.
 * @author Tony Washer
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public abstract class PrometheusSheetEncrypted<T extends EncryptedItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends PrometheusSheetDataItem<T, E> {
    /**
     * KeySetId column.
     */
    protected static final int COL_KEYSETID = COL_ID + 1;

    /**
     * Constructor for a load operation.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected PrometheusSheetEncrypted(final PrometheusSheetReader<?> pReader,
                                       final String pRange) {
        /* Pass call on */
        super(pReader, pRange);
    }

    /**
     * Constructor for a write operation.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected PrometheusSheetEncrypted(final PrometheusSheetWriter<?> pWriter,
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
    protected DataValues<E> getRowValues(final String pName) throws OceanusException {
        /* Allocate the values */
        final DataValues<E> myValues = super.getRowValues(pName);

        /* Add the control id and return the new values */
        myValues.addValue(EncryptedItem.FIELD_KEYSET, loadInteger(COL_KEYSETID));
        return myValues;
    }
}
