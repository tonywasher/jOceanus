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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;

/**
 * Class representing a row within a sheet or a view.
 */
public class MetisExcelRow
        extends MetisDataRow {
    /**
     * The Excel Sheet.
     */
    private final MetisExcelSheet theExcelSheet;

    /**
     * The Excel Row.
     */
    private final HSSFRow theExcelRow;

    /**
     * Is the row readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRow the Excel Row
     * @param pRowIndex the RowIndex
     * @param pReadOnly is the row readOnly?
     */
    protected MetisExcelRow(final MetisExcelSheet pSheet,
                            final HSSFRow pRow,
                            final int pRowIndex,
                            final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pRowIndex);
        theExcelSheet = pSheet;
        theExcelRow = pRow;
        isReadOnly = pReadOnly;
    }

    /**
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    protected CellValue evaluateFormula(final HSSFCell pCell) {
        return theExcelSheet.evaluateFormula(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    protected String formatCellValue(final HSSFCell pCell) {
        return theExcelSheet.formatCellValue(pCell);
    }

    @Override
    public MetisExcelRow getNextRow() {
        /* Determine the required index */
        final int myIndex = getRowIndex() + 1;

        /* Return the next row */
        return theExcelSheet.getReadOnlyRowByIndex(myIndex);
    }

    @Override
    public MetisExcelRow getPreviousRow() {
        /* Determine the required index */
        final int myIndex = getRowIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous row */
        return theExcelSheet.getReadOnlyRowByIndex(myIndex);
    }

    @Override
    public int getCellCount() {
        /* return the cell count */
        return theExcelRow.getLastCellNum();
    }

    @Override
    public MetisExcelCell getReadOnlyCellByIndex(final int pIndex) {
        /* Handle negative index */
        if (pIndex < 0) {
            return null;
        }

        /* Access the cell */
        final HSSFCell myExcelCell = theExcelRow.getCell(pIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return (myExcelCell != null)
                                     ? new MetisExcelCell(this, myExcelCell, pIndex, true)
                                     : null;
    }

    @Override
    public MetisExcelCell getMutableCellByIndex(final int pIndex) {
        /* Handle negative index and readOnly */
        if ((pIndex < 0)
            || (isReadOnly)) {
            return null;
        }

        /* Create the cell */
        final HSSFCell myExcelCell = theExcelRow.createCell(pIndex);
        return (myExcelCell != null)
                                     ? new MetisExcelCell(this, myExcelCell, pIndex, false)
                                     : null;
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setCellStyle(final MetisExcelCell pCell,
                                final Object pValue) {
        /* Pass through to the sheet */
        theExcelSheet.setCellStyle(pCell, pValue);
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setAlternateCellStyle(final MetisExcelCell pCell,
                                         final Object pValue) {
        /* Pass through to the sheet */
        theExcelSheet.setAlternateCellStyle(pCell, pValue);
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
        final MetisDataFormatter myFormatter = theExcelSheet.getDataFormatter();
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
        final MetisDataFormatter myFormatter = theExcelSheet.getDataFormatter();
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
        final MetisDataFormatter myFormatter = theExcelSheet.getDataFormatter();
        return myFormatter.parseValue(pSource, pCurrCode, pClass);
    }
}
