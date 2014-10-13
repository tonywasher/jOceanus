/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.sheet;

/**
 * Class representing a column in Oasis.
 * @author Tony Washer
 */
public abstract class DataColumn {
    /**
     * The underlying sheet.
     */
    private final DataSheet theSheet;

    /**
     * The index of this column.
     */
    private final int theColIndex;

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public DataSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the column index.
     * @return column index
     */
    public int getColumnIndex() {
        return theColIndex;
    }

    /**
     * Constructor.
     * @param pSheet the sheet for the column
     * @param pIndex the index
     */
    protected DataColumn(final DataSheet pSheet,
                         final int pIndex) {
        /* Store parameters */
        theSheet = pSheet;
        theColIndex = pIndex;
    }

    /**
     * Get the next column.
     * @return the next column
     */
    public abstract DataColumn getNextColumn();

    /**
     * Get the previous column.
     * @return the previous column
     */
    public abstract DataColumn getPreviousColumn();

    /**
     * Set hidden status.
     * @param isHidden is the column hidden?
     */
    public abstract void setHidden(final boolean isHidden);

    /**
     * Is the column hidden?
     * @return true/false
     */
    public abstract boolean isHidden();

    /**
     * Set the default cell style.
     * @param pStyle the cell style
     */
    public abstract void setDefaultCellStyle(final CellStyleType pStyle);
}
