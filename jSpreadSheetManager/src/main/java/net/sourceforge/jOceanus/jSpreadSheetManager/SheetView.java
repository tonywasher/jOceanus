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

/**
 * Represents a view of a range of cells.
 */
public class SheetView {
    /**
     * Underlying Sheet.
     */
    private final SheetSheet theSheet;

    /**
     * Top Left Cell Position.
     */
    private final CellPosition theFirstCell;

    /**
     * Bottom Right Cell Position.
     */
    private final CellPosition theLastCell;

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public SheetSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the top left position.
     * @return the top left position
     */
    public CellPosition getFirstCell() {
        return theFirstCell;
    }

    /**
     * Obtain the bottom right position.
     * @return the bottom right position
     */
    public CellPosition getLastCell() {
        return theLastCell;
    }

    /**
     * Determine number of rows in this view.
     * @return the number of rows.
     */
    public int getRowCount() {
        return theLastCell.getRowIndex()
               - theFirstCell.getRowIndex()
               + 1;
    }

    /**
     * Determine number of columns in this view.
     * @return the number of columns.
     */
    public int getColumnCount() {
        return theLastCell.getColumnIndex()
               - theFirstCell.getColumnIndex()
               + 1;
    }

    /**
     * Constructor.
     * @param pSheet the sheet containing the view
     * @param pFirstCell the first cell of the view
     * @param pLastCell the last cell of the view
     */
    protected SheetView(final SheetSheet pSheet,
                        final CellPosition pFirstCell,
                        final CellPosition pLastCell) {
        /* Store parameters */
        theSheet = pSheet;
        theFirstCell = pFirstCell;
        theLastCell = pLastCell;
    }

    /**
     * Constructor.
     * @param pFirstCell the first cell of the view
     * @param pLastCell the last cell of the view
     */
    protected SheetView(final SheetCell pFirstCell,
                        final SheetCell pLastCell) {
        /* Store parameters */
        theSheet = pFirstCell.getSheet();
        theFirstCell = pFirstCell.getPosition();
        theLastCell = pLastCell.getPosition();
    }

    /**
     * Validate Row index.
     * @param pRowIndex the index
     * @return valid true/false.
     */
    private boolean validRowIndex(final int pRowIndex) {
        /* Check that the row is within range */
        return ((pRowIndex >= 0) && (pRowIndex < getRowCount()));
    }

    /**
     * Validate Column index.
     * @param pColumnIndex the index
     * @return valid true/false.
     */
    protected boolean validColumnIndex(final int pColumnIndex) {
        /* Check that the column is within range */
        return ((pColumnIndex >= 0) && (pColumnIndex < getColumnCount()));
    }

    /**
     * Obtain the row at required index.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public SheetRow getRowByIndex(final int pRowIndex) {
        /* Handle invalid index */
        if (!validRowIndex(pRowIndex)) {
            return null;
        }

        /* Return the row */
        return theSheet.getViewRowByIndex(this, pRowIndex);
    }

    /**
     * Obtain the cell at required position.
     * @param pColumnIndex the requested column index
     * @param pRowIndex the requested row index
     * @return the requested cell.
     */
    public SheetCell getCellByPosition(final int pColumnIndex,
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
    public SheetCell getCellByPosition(final CellPosition pPosition) {
        /* Handle invalid indices */
        if ((!validRowIndex(pPosition.getRowIndex()))
            || (!validColumnIndex(pPosition.getColumnIndex()))) {
            return null;
        }

        /* Return the cell */
        SheetRow myRow = getRowByIndex(pPosition.getRowIndex());
        return myRow.getCellByIndex(pPosition.getColumnIndex());
    }
}
