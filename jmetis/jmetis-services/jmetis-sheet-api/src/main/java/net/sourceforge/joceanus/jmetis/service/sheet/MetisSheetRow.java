/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet;

/**
 * Class representing a row within a sheet or a view.
 */
public abstract class MetisSheetRow {
    /**
     * The underlying sheet.
     */
    private final MetisSheetSheet theSheet;

    /**
     * The underlying view.
     */
    private final MetisSheetView theView;

    /**
     * The index of this row.
     */
    private final int theRowIndex;

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRowIndex the Row index
     */
    protected MetisSheetRow(final MetisSheetSheet pSheet,
                            final int pRowIndex) {
        /* Store parameters */
        theSheet = pSheet;
        theView = null;
        theRowIndex = pRowIndex;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pRowIndex the Row index
     */
    protected MetisSheetRow(final MetisSheetView pView,
                            final int pRowIndex) {
        /* Store parameters */
        theSheet = pView.getSheet();
        theView = pView;
        theRowIndex = pRowIndex;
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public MetisSheetSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public MetisSheetView getView() {
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
     * Get the next row.
     * @return the next row
     */
    public abstract MetisSheetRow getNextRow();

    /**
     * Get the previous row.
     * @return the previous row
     */
    public abstract MetisSheetRow getPreviousRow();

    /**
     * Determine number of cells in this row.
     * @return the number of cells.
     */
    public abstract int getCellCount();

    /**
     * Get the cell at the required index.
     * @param pIndex the column index.
     * @return the cell
     */
    public abstract MetisSheetCell getReadOnlyCellByIndex(int pIndex);

    /**
     * Get the cell at the required index.
     * @param pIndex the column index.
     * @return the cell
     */
    public abstract MetisSheetCell getMutableCellByIndex(int pIndex);
}
