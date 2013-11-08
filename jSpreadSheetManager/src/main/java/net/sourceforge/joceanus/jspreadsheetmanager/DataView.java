/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jspreadsheetmanager;

import java.util.Iterator;

/**
 * Represents a view of a range of cells.
 */
public class DataView
        implements Iterable<DataRow> {
    /**
     * Underlying Sheet.
     */
    private final DataSheet theSheet;

    /**
     * Base Cell Position.
     */
    private final CellPosition theBaseCell;

    /**
     * Number of rows in view.
     */
    private final int theNumRows;

    /**
     * Number of columns in view.
     */
    private final int theNumColumns;

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public DataSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the top left position.
     * @return the top left position
     */
    public CellPosition getBaseCell() {
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
     * Constructor.
     * @param pSheet the sheet containing the view
     * @param pFirstCell the first cell of the view
     * @param pLastCell the last cell of the view
     */
    protected DataView(final DataSheet pSheet,
                       final CellPosition pFirstCell,
                       final CellPosition pLastCell) {
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
    protected DataView(final DataCell pFirstCell,
                       final DataCell pLastCell) {
        /* Store parameters */
        this(pFirstCell.getSheet(), pFirstCell.getPosition(), pLastCell.getPosition());
    }

    /**
     * Convert Row index.
     * @param pRowIndex the view index
     * @return the sheet index or -1 if outside view
     */
    protected int convertRowIndex(final int pRowIndex) {
        /* Reject values outside range */
        if ((pRowIndex < 0)
            || (pRowIndex >= theNumRows)) {
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
        if ((pColIndex < 0)
            || (pColIndex >= theNumColumns)) {
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
    public DataRow getRowByIndex(final int pRowIndex) {
        /* Return the row */
        int myIndex = convertRowIndex(pRowIndex);
        return (myIndex < 0)
                ? null
                : theSheet.getReadOnlyRowByIndex(myIndex);
    }

    /**
     * Obtain the cell at required position.
     * @param pColumnIndex the requested column index
     * @param pRowIndex the requested row index
     * @return the requested cell.
     */
    public DataCell getCellByPosition(final int pColumnIndex,
                                      final int pRowIndex) {
        /* Return the cell */
        CellPosition myPos = new CellPosition(pColumnIndex, pRowIndex);
        return getCellByPosition(myPos);
    }

    /**
     * Obtain the cell at required position.
     * @param pPosition the requested position
     * @return the requested cell.
     */
    public DataCell getCellByPosition(final CellPosition pPosition) {
        /* Return the cell */
        DataRow myRow = getRowByIndex(pPosition.getRowIndex());
        return (myRow == null)
                ? null
                : getRowCellByIndex(myRow, pPosition.getColumnIndex());
    }

    /**
     * Obtain the cell at required index.
     * @param pRow the row to extract from
     * @param pIndex the requested index
     * @return the requested cell.
     */
    public DataCell getRowCellByIndex(final DataRow pRow,
                                      final int pIndex) {
        /* Return the cell */
        int myIndex = convertColumnIndex(pIndex);
        return (myIndex < 0)
                ? null
                : pRow.getReadOnlyCellByIndex(myIndex);
    }

    @Override
    public Iterator<DataRow> iterator() {
        return new RowIterator(this);
    }

    /**
     * Iterator class for rows.
     */
    private static class RowIterator
            implements Iterator<DataRow> {
        /**
         * The data view.
         */
        private final DataView theView;

        /**
         * The base row.
         */
        private final int theBaseRow;

        /**
         * The last row.
         */
        private DataRow theLastRow;

        /**
         * Constructor.
         * @param pView the underlying view.
         */
        protected RowIterator(final DataView pView) {
            theLastRow = null;
            theView = pView;
            theBaseRow = theView.getBaseCell().getRowIndex();
        }

        @Override
        public boolean hasNext() {
            /* Calculate the next index */
            int iIndex = (theLastRow != null)
                    ? theLastRow.getRowIndex() + 1
                    : theBaseRow;

            /* Check that the row is within the view */
            iIndex -= theBaseRow;
            return (iIndex >= 0)
                   && (iIndex < theView.getRowCount());
        }

        @Override
        public DataRow next() {
            /* If we are a new iterator */
            if (theLastRow == null) {
                /* Access the first element of the view */
                theLastRow = theView.getRowByIndex(0);
            } else {
                /* Return the next row */
                theLastRow = theLastRow.getNextRow();
            }

            /* Return the next row */
            return theLastRow;
        }

        @Override
        public void remove() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }
    }
}
