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
package net.sourceforge.joceanus.jprometheus.lethe.sheets;

import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to a data info type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class PrometheusSheetXDataInfo<T extends DataInfoItem>
        extends PrometheusSheetXEncrypted<T> {
    /**
     * InfoType column.
     */
    private static final int COL_INFOTYPE = COL_KEYSETID + 1;

    /**
     * Owner column.
     */
    private static final int COL_OWNER = COL_INFOTYPE + 1;

    /**
     * Value column.
     */
    private static final int COL_VALUE = COL_OWNER + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected PrometheusSheetXDataInfo(final PrometheusSheetXReader pReader,
                                       final String pRange) {
        /* Call super constructor */
        super(pReader, pRange);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected PrometheusSheetXDataInfo(final PrometheusSheetXWriter pWriter,
                                       final String pRange) {
        /* Call super constructor */
        super(pWriter, pRange);
    }

    @Override
    protected void insertSecureItem(final T pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_INFOTYPE, pItem.getInfoTypeId());
        writeInteger(COL_OWNER, pItem.getOwnerId());
        writeBytes(COL_VALUE, pItem.getValueBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_VALUE;
    }

    @Override
    protected DataValues getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final DataValues myValues = super.getRowValues(pName);

        /* Add the info and return the new values */
        myValues.addValue(DataInfoItem.FIELD_INFOTYPE, loadInteger(COL_INFOTYPE));
        myValues.addValue(DataInfoItem.FIELD_OWNER, loadInteger(COL_OWNER));
        myValues.addValue(DataInfoItem.FIELD_VALUE, loadBytes(COL_VALUE));
        return myValues;
    }
}