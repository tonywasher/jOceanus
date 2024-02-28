/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.service.sheet;

import java.util.Collections;
import java.util.ListIterator;

/**
 * Represents a view of a range of cells.
 */
public class PrometheusSheetView {
    /**
     * Underlying Sheet.
     */
    private final PrometheusSheetSheet theSheet;

    /**
     * Base Cell Position.
     */
    private final PrometheusSheetCellPosition theBaseCell;

    /**
     * Number of rows in view.
     */
    private final int theNumRows;

    /**
     * Number of columns in view.
     */
    private final int theNumColumns;

    /**
     * Constructor.
     * @param pSheet the sheet containing the view
     * @param pFirstCell the first cell of the view
     * @param pLastCell the last cell of the view
     */
    public PrometheusSheetView(final PrometheusSheetSheet pSheet,
                               final PrometheusSheetCellPosition pFirstCell,
                               final PrometheusSheetCellPosition pLastCell) {
        /* Store parameters */
        theSheet = pSheet;
        theBaseCell = pFirstCell;
        theNumRows = pLastCell.getRowIndex()
                     - pFirstCell.getRowIndex()
                     + 1;
        theNumColumns = pLastCell.getColumnIndex()
                        - pFirstCell.getColumnIndex()
                        + 1;
    }

    /**
     * Constructor.
     * @param pFirstCell the first cell of the view
     * @param pLastCell the last cell of the view
     */
    protected PrometheusSheetView(final PrometheusSheetCell pFirstCell,
                                  final PrometheusSheetCell pLastCell) {
        /* Store parameters */
        this(pFirstCell.getSheet(), pFirstCell.getPosition(), pLastCell.getPosition());
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public PrometheusSheetSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the top left position.
     * @return the top left position
     */
    public PrometheusSheetCellPosition getBaseCell() {
        return theBaseCell;
    }

    /**
     * Determine number of rows in this view.
     * @return the number of rows.
     */
    public int getRowCount() {
        return theNumRows;
    }

    /**
     * Determine number of columns in this view.
     * @return the number of columns.
     */
    public int getColumnCount() {
        return theNumColumns;
    }

    /**
     * Convert Row index.
     * @param pRowIndex the view index
     * @return the sheet index or -1 if outside view
     */
    protected int convertRowIndex(final int pRowIndex) {
        /* Reject values outside range */
        if (pRowIndex < 0
            || pRowIndex >= theNumRows) {
            return -1;
        }

        /* Return adjusted index */
        return pRowIndex
               + theBaseCell.getRowIndex();
    }

    /**
     * Convert Column index.
     * @param pColIndex the view index
     * @return the sheet index or -1 if outside view
     */
    private int convertColumnIndex(final int pColIndex) {
        /* Reject values outside range */
        if (pColIndex < 0
            || pColIndex >= theNumColumns) {
            return -1;
        }

        /* Return adjusted index */
        return pColIndex
               + theBaseCell.getColumnIndex();
    }

    /**
     * Obtain the row at required index.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public PrometheusSheetRow getRowByIndex(final int pRowIndex) {
        /* Return the row */
        final int myIndex = convertRowIndex(pRowIndex);
        return myIndex < 0
                           ? null
                           : theSheet.getReadOnlyRowByIndex(myIndex);
    }

    /**
     * Obtain the cell at required position.
     * @param pColumnIndex the requested column index
     * @param pRowIndex the requested row index
     * @return the requested cell.
     */
    public PrometheusSheetCell getCellByPosition(final int pColumnIndex,
                                                 final int pRowIndex) {
        /* Return the cell */
        final PrometheusSheetCellPosition myPos = new PrometheusSheetCellPosition(pColumnIndex, pRowIndex);
        return getCellByPosition(myPos);
    }

    /**
     * Obtain the cell at required position.
     * @param pPosition the requested position
     * @return the requested cell.
     */
    public PrometheusSheetCell getCellByPosition(final PrometheusSheetCellPosition pPosition) {
        /* Return the cell */
        final PrometheusSheetRow myRow = getRowByIndex(pPosition.getRowIndex());
        return myRow == null
                             ? null
                             : getRowCellByIndex(myRow, pPosition.getColumnIndex());
    }

    /**
     * Obtain the cell at required index.
     * @param pRow the row to extract from
     * @param pIndex the requested index
     * @return the requested cell.
     */
    public PrometheusSheetCell getRowCellByIndex(final PrometheusSheetRow pRow,
                                                 final int pIndex) {
        /* Return the cell */
        final int myIndex = convertColumnIndex(pIndex);
        return myIndex < 0
                           ? null
                           : pRow.getReadOnlyCellByIndex(myIndex);
    }

    /**
     * Obtain a cell iterator for non-empty cells in the view and in the row.
     * @param pRow the row
     * @return the iterator
     */
    public ListIterator<PrometheusSheetCell> cellIterator(final PrometheusSheetRow pRow) {
        /* Check that the row is in the view */
        final int myIndex = pRow.getRowIndex();
        int myFirstIndex = theBaseCell.getRowIndex();
        int myLastIndex = myFirstIndex + theNumRows - 1;

        /* Return null iterator for row not in view */
        if (myIndex < myFirstIndex
                || myIndex > myLastIndex
                || !theSheet.getName().equals(pRow.getSheet().getName())) {
            return Collections.emptyListIterator();
        }

        /* return the iterator */
        myFirstIndex = theBaseCell.getColumnIndex();
        myLastIndex = myFirstIndex + theNumColumns - 1;
        return pRow.iteratorForRange(myFirstIndex, myLastIndex);
    }

    /**
     * Obtain a row iterator for non-empty rows in this view.
     * @return the iterator
     */
    public ListIterator<PrometheusSheetRow> rowIterator() {
        /* Obtain the iterator */
        final int myFirstIndex = theBaseCell.getRowIndex();
        final int myLastIndex = myFirstIndex + theNumRows - 1;
        return theSheet.iteratorForRange(myFirstIndex, myLastIndex);
    }
}
