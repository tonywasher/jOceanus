/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.service.sheet;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

import java.util.ListIterator;

/**
 * Class representing a sheet within a workBook.
 */
public abstract class PrometheusSheetSheet {
    /**
     * The WorkBook.
     */
    private final PrometheusSheetWorkBook theWorkBook;

    /**
     * Name of sheet.
     */
    private final String theSheetName;

    /**
     * Is the sheet readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor for Excel Sheet.
     *
     * @param pWorkBook the workBook
     * @param pName     the sheet name
     * @param pReadOnly is the sheet readOnly?
     */
    protected PrometheusSheetSheet(final PrometheusSheetWorkBook pWorkBook,
                                   final String pName,
                                   final boolean pReadOnly) {
        /* Store parameters */
        theWorkBook = pWorkBook;
        theSheetName = pName;
        isReadOnly = pReadOnly;
    }

    /**
     * Obtain the workBook.
     *
     * @return the workBook
     */
    public PrometheusSheetWorkBook getWorkBook() {
        return theWorkBook;
    }

    /**
     * Obtain the name of the sheet.
     *
     * @return the name
     */
    public String getName() {
        return theSheetName;
    }

    /**
     * Get sheet index.
     *
     * @return the index of the sheet
     */
    public abstract int getSheetIndex();

    /**
     * Is the sheet readOnly?
     *
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Is the sheet hidden?
     *
     * @return true/false
     */
    public abstract boolean isHidden();

    /**
     * Set sheet hidden status.
     *
     * @param isHidden true/false
     */
    public abstract void setHidden(boolean isHidden);

    /**
     * Get row count.
     *
     * @return the count of rows
     */
    public abstract int getRowCount();

    /**
     * Obtain the row at required index within the sheet, if it exists.
     *
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public abstract PrometheusSheetRow getReadOnlyRowByIndex(int pRowIndex);

    /**
     * Obtain the row at required index within the sheet, create it if it does not exist.
     *
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public abstract PrometheusSheetRow getMutableRowByIndex(int pRowIndex);

    /**
     * Obtain the column by index.
     *
     * @param pColIndex the column index
     * @return the column
     */
    public abstract PrometheusSheetColumn getReadOnlyColumnByIndex(int pColIndex);

    /**
     * Obtain the column by index, creating column if it does not exist.
     *
     * @param pColIndex the column index
     * @return the column
     */
    public abstract PrometheusSheetColumn getMutableColumnByIndex(int pColIndex);

    /**
     * Name a range.
     *
     * @param pName      the name of the range
     * @param pFirstCell the first cell in the range
     * @param pLastCell  the last cell in the range
     * @throws OceanusException on error
     */
    public abstract void declareRange(String pName,
                                      PrometheusSheetCellPosition pFirstCell,
                                      PrometheusSheetCellPosition pLastCell) throws OceanusException;

    /**
     * Name a single cell as a range.
     *
     * @param pName       the name of the range
     * @param pSingleCell the cell to name
     * @throws OceanusException on error
     */
    public void declareRange(final String pName,
                             final PrometheusSheetCellPosition pSingleCell) throws OceanusException {
        /* declare the range */
        declareRange(pName, pSingleCell, pSingleCell);
    }

    /**
     * Apply data validation to a range of cells.
     *
     * @param pFirstCell the first cell in the range
     * @param pLastCell  the last cell in the range
     * @param pName      the name of the validation range list
     * @throws OceanusException on error
     */
    public abstract void applyDataValidation(PrometheusSheetCellPosition pFirstCell,
                                             PrometheusSheetCellPosition pLastCell,
                                             String pName) throws OceanusException;

    /**
     * Apply data validation to a range of cells.
     *
     * @param pBaseCell the first cell in the range
     * @param pNumRows  the number of rows in the filter
     * @throws OceanusException on error
     */
    public abstract void applyDataFilter(PrometheusSheetCellPosition pBaseCell,
                                         int pNumRows) throws OceanusException;

    /**
     * Create freeze panes.
     *
     * @param pFreezeCell the cell to freeze at
     */
    public abstract void createFreezePane(PrometheusSheetCellPosition pFreezeCell);

    /**
     * Obtain an iterator of non-null rows for the view.
     *
     * @param pFirstIndex the first row in the view
     * @param pLastIndex  the last row in the view
     * @return the iterator
     */
    protected abstract ListIterator<PrometheusSheetRow> iteratorForRange(int pFirstIndex,
                                                                         int pLastIndex);
}
