/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.prometheus.service.sheet;

import java.util.ListIterator;

/**
 * Class representing a row within a sheet or a view.
 */
public abstract class PrometheusSheetRow {
    /**
     * The underlying sheet.
     */
    private final PrometheusSheetSheet theSheet;

    /**
     * The underlying view.
     */
    private final PrometheusSheetView theView;

    /**
     * The index of this row.
     */
    private final int theRowIndex;

    /**
     * Is the row readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRowIndex the Row index
     * @param pReadOnly is the row readOnly?
     */
    protected PrometheusSheetRow(final PrometheusSheetSheet pSheet,
                                 final int pRowIndex,
                                 final boolean pReadOnly) {
        /* Store parameters */
        theSheet = pSheet;
        theView = null;
        theRowIndex = pRowIndex;
        isReadOnly = pReadOnly;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pRowIndex the Row index
     */
    protected PrometheusSheetRow(final PrometheusSheetView pView,
                                 final int pRowIndex) {
        /* Store parameters */
        theSheet = pView.getSheet();
        theView = pView;
        theRowIndex = pRowIndex;
        isReadOnly = true;
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public PrometheusSheetSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public PrometheusSheetView getView() {
        return theView;
    }

    /**
     * Obtain the row index.
     * @return row index
     */
    public int getRowIndex() {
        return theRowIndex;
    }

    /**
     * Is the row readOnly?
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Get the next row.
     * @return the next row
     */
    public abstract PrometheusSheetRow getNextRow();

    /**
     * Get the previous row.
     * @return the previous row
     */
    public abstract PrometheusSheetRow getPreviousRow();

    /**
     * Determine number of cells in this row.
     * @return the number of cells.
     */
    public abstract int getCellCount();

    /**
     * Determine index of the max valued cell.
     * @return the index.
     */
    public abstract int getMaxValuedCellIndex();

    /**
     * Set hidden status.
     * @param isHidden is the column hidden?
     */
    public void setHidden(final boolean isHidden) {
        if (!isReadOnly) {
            setHiddenValue(isHidden);
        }
    }

    /**
     * Set hidden status.
     * @param isHidden is the column hidden?
     */
    protected abstract void setHiddenValue(boolean isHidden);

    /**
     * Is the column hidden?
     * @return true/false
     */
    public abstract boolean isHidden();

    /**
     * Get the cell at the required index.
     * @param pIndex the column index.
     * @return the cell
     */
    public abstract PrometheusSheetCell getReadOnlyCellByIndex(int pIndex);

    /**
     * Get the cell at the required index.
     * @param pIndex the column index.
     * @return the cell
     */
    public PrometheusSheetCell getMutableCellByIndex(final int pIndex) {
        return !isReadOnly && pIndex >= 0
                ? getWriteableCellByIndex(pIndex)
                : null;
    }

    /**
     * Get the cell at the required index.
     * @param pIndex the column index.
     * @return the cell
     */
    protected abstract PrometheusSheetCell getWriteableCellByIndex(int pIndex);

    /**
     * Obtain an iterator of non-null cells for the row in the view.
     * @param pFirstIndex the first cell in the view
     * @param pLastIndex the last cell in the view
     * @return the iterator
     */
    protected abstract ListIterator<PrometheusSheetCell> iteratorForRange(int pFirstIndex,
                                                                          int pLastIndex);
}
