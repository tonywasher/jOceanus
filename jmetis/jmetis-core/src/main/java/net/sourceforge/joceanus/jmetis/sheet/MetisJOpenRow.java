/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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

import org.jopendocument.dom.spreadsheet.Cell;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;

/**
 * JOpenDocument Row.
 */
public class MetisJOpenRow
        extends MetisDataRow {
    /**
     * The JOpen Sheet.
     */
    private final MetisJOpenSheet theJOpenSheet;

    /**
     * Is the row readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRowIndex the RowIndex
     * @param pReadOnly is the row readOnly?
     */
    protected MetisJOpenRow(final MetisJOpenSheet pSheet,
                            final int pRowIndex,
                            final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pRowIndex);
        theJOpenSheet = pSheet;
        isReadOnly = pReadOnly;
    }

    @Override
    public MetisJOpenRow getNextRow() {
        /* Determine the required index */
        final int myIndex = getRowIndex() + 1;

        /* Return the next row */
        return theJOpenSheet.getReadOnlyRowByIndex(myIndex);
    }

    @Override
    public MetisJOpenRow getPreviousRow() {
        /* Determine the required index */
        final int myIndex = getRowIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous row */
        return theJOpenSheet.getReadOnlyRowByIndex(myIndex);
    }

    @Override
    public int getCellCount() {
        return theJOpenSheet.getColumnCount();
    }

    @Override
    public MetisJOpenCell getReadOnlyCellByIndex(final int pIndex) {
        /* Handle negative index */
        if (pIndex < 0) {
            return null;
        }

        /* Access the cell */
        final Cell<SpreadSheet> myJOpenCell = theJOpenSheet.getCell(pIndex, getRowIndex());
        return myJOpenCell == null || myJOpenCell.isEmpty()
                                                            ? null
                                                            : new MetisJOpenCell(this, myJOpenCell, pIndex);
    }

    @Override
    public MetisJOpenCell getMutableCellByIndex(final int pIndex) {
        /* Handle negative index and readOnly */
        if (pIndex < 0
            || isReadOnly) {
            return null;
        }

        /* Create the cell */
        final MutableCell<SpreadSheet> myJOpenCell = theJOpenSheet.getMutableCell(pIndex, getRowIndex());
        return myJOpenCell != null
                                   ? new MetisJOpenCell(this, myJOpenCell, pIndex)
                                   : null;
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setCellStyle(final MetisJOpenCell pCell,
                                final Object pValue) {
        /* Pass through to the sheet */
        theJOpenSheet.setCellStyle(pCell, pValue);
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setAlternateCellStyle(final MetisJOpenCell pCell,
                                         final Object pValue) {
        /* Pass through to the sheet */
        theJOpenSheet.setAlternateCellStyle(pCell, pValue);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     */
    protected <T> T parseValue(final String pSource,
                               final Class<T> pClass) {
        final MetisDataFormatter myFormatter = theJOpenSheet.getDataFormatter();
        return myFormatter.parseValue(pSource, pClass);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     */
    protected <T> T parseValue(final Double pSource,
                               final Class<T> pClass) {
        final MetisDataFormatter myFormatter = theJOpenSheet.getDataFormatter();
        return myFormatter.parseValue(pSource, pClass);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pCurrCode the currency code
     * @param pClass the value type class
     * @return the formatted value
     */
    protected <T> T parseValue(final Double pSource,
                               final String pCurrCode,
                               final Class<T> pClass) {
        final MetisDataFormatter myFormatter = theJOpenSheet.getDataFormatter();
        return myFormatter.parseValue(pSource, pCurrCode, pClass);
    }
}
