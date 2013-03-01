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

import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;

import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;

/**
 * Class representing a row within a sheet or a view.
 */
public class OasisRow
        extends DataRow {
    /**
     * The Oasis Sheet.
     */
    private final OasisSheet theOasisSheet;

    /**
     * The Oasis Row.
     */
    private final Row theOasisRow;

    /**
     * Is this a View row.
     */
    private final boolean isView;

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRow the Oasis Row
     * @param pRowIndex the RowIndex
     */
    protected OasisRow(final OasisSheet pSheet,
                       final Row pRow,
                       final int pRowIndex) {
        /* Store parameters */
        super(pSheet, pRowIndex);
        theOasisSheet = pSheet;
        theOasisRow = pRow;
        isView = false;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pRow the Oasis Row
     * @param pRowIndex the RowIndex
     */
    protected OasisRow(final DataView pView,
                       final Row pRow,
                       final int pRowIndex) {
        /* Store parameters */
        super(pView, pRowIndex);
        theOasisSheet = (OasisSheet) getSheet();
        theOasisRow = pRow;
        isView = true;
    }

    @Override
    public DataRow getNextRow() {
        /* Determine the required index */
        int myIndex = getRowIndex() + 1;

        /* If this is a view */
        if (isView) {
            /* Handle next row in view */
            return (getView().convertRowIndex(myIndex) < 0)
                    ? null
                    : new OasisRow(getView(), theOasisRow.getNextRow(), myIndex);
        }

        /* Handle next row in sheet */
        return (myIndex >= theOasisSheet.getRowCount())
                ? theOasisSheet.getRowByIndex(myIndex)
                : new OasisRow(theOasisSheet, theOasisRow.getNextRow(), myIndex);
    }

    @Override
    public DataRow getPreviousRow() {
        /* Determine the required index */
        int myIndex = getRowIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous row */
        return (isView)
                ? new OasisRow(getView(), theOasisRow.getPreviousRow(), myIndex)
                : new OasisRow(theOasisSheet, theOasisRow.getPreviousRow(), myIndex);
    }

    @Override
    public int getCellCount() {
        /* Return cell count */
        return (isView)
                ? getView().getColumnCount()
                : theOasisRow.getCellCount();
    }

    @Override
    public DataCell getCellByIndex(final int pIndex) {
        int myIndex = (isView)
                ? getView().convertColumnIndex(pIndex)
                : pIndex;
        if (myIndex < 0) {
            return null;
        }

        /* Return the cell */
        Cell myOasisCell = theOasisRow.getCellByIndex(myIndex);
        return (myOasisCell.getValueType() != null)
                ? new OasisCell(this, myOasisCell, pIndex)
                : null;
    }

    @Override
    public DataCell createCellByIndex(final int pIndex) {
        /* if this is a view row */
        if (isView) {
            /* Not allowed */
            return null;
        }

        /* Create the cell */
        Cell myOasisCell = theOasisRow.getCellByIndex(pIndex);
        return new OasisCell(this, myOasisCell, pIndex);
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final OasisCell pCell,
                                final CellStyleType pStyle) {
        /* Pass through to the sheet */
        theOasisSheet.setCellStyle(pCell, pStyle);
    }
}
