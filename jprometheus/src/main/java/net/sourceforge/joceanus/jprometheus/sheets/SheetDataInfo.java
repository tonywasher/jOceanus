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

import net.sourceforge.joceanus.jprometheus.data.DataInfo;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Extension of SheetDataItem class for accessing a sheet that is related to a data info type.
 * @author Tony Washer
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public abstract class SheetDataInfo<T extends DataInfo<T, ?, ?, ?, E>, E extends Enum<E>>
        extends SheetEncrypted<T, E> {
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
    protected SheetDataInfo(final SheetReader<?> pReader,
                            final String pRange) {
        /* Call super constructor */
        super(pReader, pRange);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     * @param pRange the range to create
     */
    protected SheetDataInfo(final SheetWriter<?> pWriter,
                            final String pRange) {
        /* Call super constructor */
        super(pWriter, pRange);
    }

    @Override
    protected void insertSecureItem(final T pItem) throws JOceanusException {
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
    protected DataValues<E> getRowValues(final String pName) throws JOceanusException {
        /* Obtain the values */
        DataValues<E> myValues = super.getRowValues(pName);

        /* Add the info and return the new values */
        myValues.addValue(DataInfo.FIELD_INFOTYPE, loadInteger(COL_INFOTYPE));
        myValues.addValue(DataInfo.FIELD_OWNER, loadInteger(COL_OWNER));
        myValues.addValue(DataInfo.FIELD_VALUE, loadBytes(COL_VALUE));
        return myValues;
    }
}
