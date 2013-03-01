/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2013 Tony Washer
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
package net.sourceforge.jOceanus.jSpreadSheetManager;

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;

/**
 * Class representing a sheet within a workBook.
 */
public abstract class DataSheet
        implements Iterable<DataRow> {
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
     * Get row count.
     * @return the count of rows
     */
    public abstract int getRowCount();

    /**
     * Obtain the row at required index within the sheet.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public abstract DataRow getRowByIndex(final int pRowIndex);

    /**
     * Obtain the row at required index within the view.
     * @param pView the requested row index
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    protected abstract DataRow getRowByIndex(final DataView pView,
                                             final int pRowIndex);

    /**
     * Name a range.
     * @param pName the name of the range
     * @param pFirstCell the first cell in the range
     * @param pLastCell the last cell in the range
     * @throws JDataException on error
     */
    public abstract void declareRange(final String pName,
                                      final CellPosition pFirstCell,
                                      final CellPosition pLastCell) throws JDataException;

    /**
     * Name a single cell as a range.
     * @param pName the name of the range
     * @param pSingleCell the cell to name
     * @throws JDataException on error
     */
    public void declareRange(final String pName,
                             final CellPosition pSingleCell) throws JDataException {
        /* declare the range */
        declareRange(pName, pSingleCell, pSingleCell);
    }

    /**
     * Apply data validation to a range of cells.
     * @param pFirstCell the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pName the name of the validation range list
     * @throws JDataException on error
     */
    public abstract void applyDataValidation(final CellPosition pFirstCell,
                                             final CellPosition pLastCell,
                                             final String pName) throws JDataException;

    /**
     * Apply data validation to a range of cells.
     * @param pBaseCell the first cell in the range
     * @param pNumRows the number of rows in the filter
     * @throws JDataException on error
     */
    public abstract void applyDataFilter(final CellPosition pBaseCell,
                                         final int pNumRows) throws JDataException;

    /**
     * Create freeze panes.
     * @param pFreezeCell the cell to freeze at
     */
    public abstract void createFreezePane(final CellPosition pFreezeCell);

    /**
     * Set Column hidden status.
     * @param pColIndex the column to show/hide
     * @param isHidden is the column hidden?
     */
    public abstract void setColumnHidden(final int pColIndex,
                                         final boolean isHidden);

    /**
     * Set Column width.
     * @param pColIndex the column to set width for
     * @param pWidth the width in characters
     */
    public abstract void setColumnWidth(final int pColIndex,
                                        final int pWidth);

    /**
     * Set Column width.
     * @param pColIndex the column to set style for
     * @param pStyle the default style type
     */
    public abstract void setDefaultColumnStyle(final int pColIndex,
                                               final CellStyleType pStyle);

    @Override
    public Iterator<DataRow> iterator() {
        return new DataRowIterator(this);
    }
}
