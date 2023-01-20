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

import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to a static data type.
 * @author Tony Washer
 * @param <T> the data type
 */
public abstract class PrometheusSheetStaticData<T extends StaticDataItem>
        extends PrometheusSheetEncrypted<T> {
    /**
     * Enabled column.
     */
    private static final int COL_ENABLED = COL_KEYSETID + 1;

    /**
     * Order column.
     */
    private static final int COL_ORDER = COL_ENABLED + 1;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_ORDER + 1;

    /**
     * Description column.
     */
    protected static final int COL_DESC = COL_NAME + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     * @param pRange the range to load
     */
    protected PrometheusSheetStaticData(final PrometheusSheetReader<?> pReader,
                                        final String pRange) {
        /* Call super constructor */
        super(pReader, pRange);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected PrometheusSheetStaticData(final PrometheusSheetWriter<?> pWriter,
                                        final String pRange) {
        /* Call super constructor */
        super(pWriter, pRange);
    }

    @Override
    protected void insertSecureItem(final T pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeBoolean(COL_ENABLED, pItem.getEnabled());
        writeInteger(COL_ORDER, pItem.getOrder());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_DESC;
    }

    @Override
    protected DataValues getRowValues(final String pName) throws OceanusException {
        /* Obtain the values */
        final DataValues myValues = super.getRowValues(pName);

        /* Add the info and return the new values */
        myValues.addValue(StaticDataItem.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(StaticDataItem.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(StaticDataItem.FIELD_ORDER, loadInteger(COL_ORDER));
        myValues.addValue(StaticDataItem.FIELD_ENABLED, loadBoolean(COL_ENABLED));
        return myValues;
    }
}
