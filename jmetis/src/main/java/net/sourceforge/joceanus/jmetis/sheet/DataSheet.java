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

import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class representing a sheet within a workBook.
 */
public abstract class DataSheet {
    /**
     * Name of sheet.
     */
    private final String theSheetName;

    /**
     * Obtain the name of the sheet.
     * @return the name
     */
    public String getName() {
        return theSheetName;
    }

    /**
     * Constructor for Excel Sheet.
     * @param pName the sheet name
     */
    protected DataSheet(final String pName) {
        /* Store parameters */
        theSheetName = pName;
    }

    /**
     * Get sheet index.
     * @return the index of the sheet
     */
    public abstract int getSheetIndex();

    /**
     * Is the sheet hidden?
     * @return true/false
     */
    public abstract boolean isHidden();

    /**
     * Set sheet hidden status.
     * @param isHidden true/false
     */
    public abstract void setHidden(final boolean isHidden);

    /**
     * Get row count.
     * @return the count of rows
     */
    public abstract int getRowCount();

    /**
     * Obtain the row at required index within the sheet, if it exists.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public abstract DataRow getReadOnlyRowByIndex(final int pRowIndex);

    /**
     * Obtain the row at required index within the sheet, create it if it does not exist.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public abstract DataRow getMutableRowByIndex(final int pRowIndex);

    /**
     * Obtain the column by index.
     * @param pColIndex the column index
     * @return the column
     */
    public abstract DataColumn getReadOnlyColumnByIndex(final int pColIndex);

    /**
     * Obtain the column by index, creating column if it does not exist.
     * @param pColIndex the column index
     * @return the column
     */
    public abstract DataColumn getMutableColumnByIndex(final int pColIndex);

    /**
     * Name a range.
     * @param pName the name of the range
     * @param pFirstCell the first cell in the range
     * @param pLastCell the last cell in the range
     * @throws JOceanusException on error
     */
    public abstract void declareRange(final String pName,
                                      final CellPosition pFirstCell,
                                      final CellPosition pLastCell) throws JOceanusException;

    /**
     * Name a single cell as a range.
     * @param pName the name of the range
     * @param pSingleCell the cell to name
     * @throws JOceanusException on error
     */
    public void declareRange(final String pName,
                             final CellPosition pSingleCell) throws JOceanusException {
        /* declare the range */
        declareRange(pName, pSingleCell, pSingleCell);
    }

    /**
     * Apply data validation to a range of cells.
     * @param pFirstCell the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pName the name of the validation range list
     * @throws JOceanusException on error
     */
    public abstract void applyDataValidation(final CellPosition pFirstCell,
                                             final CellPosition pLastCell,
                                             final String pName) throws JOceanusException;

    /**
     * Apply data validation to a range of cells.
     * @param pBaseCell the first cell in the range
     * @param pNumRows the number of rows in the filter
     * @throws JOceanusException on error
     */
    public abstract void applyDataFilter(final CellPosition pBaseCell,
                                         final int pNumRows) throws JOceanusException;

    /**
     * Create freeze panes.
     * @param pFreezeCell the cell to freeze at
     */
    public abstract void createFreezePane(final CellPosition pFreezeCell);
}
