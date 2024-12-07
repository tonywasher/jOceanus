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
package net.sourceforge.joceanus.prometheus.service.sheet.hssf;

import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class representing a row within a sheet or a view.
 */
public class PrometheusExcelHSSFRow
        extends PrometheusSheetRow {
    /**
     * The Excel Sheet.
     */
    private final PrometheusExcelHSSFSheet theExcelSheet;

    /**
     * The Excel Row.
     */
    private final HSSFRow theExcelRow;

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRow the Excel Row
     * @param pRowIndex the RowIndex
     * @param pReadOnly is the row readOnly?
     */
    PrometheusExcelHSSFRow(final PrometheusExcelHSSFSheet pSheet,
                           final HSSFRow pRow,
                           final int pRowIndex,
                           final boolean pReadOnly) {
        /* Store parameters */
        super(pSheet, pRowIndex, pReadOnly);
        theExcelSheet = pSheet;
        theExcelRow = pRow;
    }

    /**
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    CellValue evaluateFormula(final HSSFCell pCell) {
        return theExcelSheet.evaluateFormula(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    String formatCellValue(final HSSFCell pCell) {
        return theExcelSheet.formatCellValue(pCell);
    }

    @Override
    public PrometheusExcelHSSFRow getNextRow() {
        /* Determine the required index */
        final int myIndex = getRowIndex() + 1;

        /* Return the next row */
        return theExcelSheet.getReadOnlyRowByIndex(myIndex);
    }

    @Override
    public PrometheusExcelHSSFRow getPreviousRow() {
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
    public int getMaxValuedCellIndex() {
        return theExcelRow.getLastCellNum() - 1;
    }

    @Override
    public PrometheusExcelHSSFCell getReadOnlyCellByIndex(final int pIndex) {
        /* Handle negative index */
        if (pIndex < 0) {
            return null;
        }

        /* Access the cell */
        final HSSFCell myExcelCell = theExcelRow.getCell(pIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return myExcelCell != null
                                   ? new PrometheusExcelHSSFCell(this, myExcelCell, pIndex, true)
                                   : null;
    }

    @Override
    protected ListIterator<PrometheusSheetCell> iteratorForRange(final int pFirstIndex,
                                                                 final int pLastIndex) {
        /* Determine bounds for search */
        final int myLower = Math.max(pFirstIndex, theExcelRow.getFirstCellNum());
        final int myUpper = Math.min(pLastIndex, theExcelRow.getLastCellNum());

        /* Create a list of cells */
        final List<PrometheusSheetCell> myList = new ArrayList<>();
        for (int iIndex = myLower; iIndex <= myUpper; iIndex++) {
            /* Only return a cell if a value is present */
            final HSSFCell myExcelCell = theExcelRow.getCell(iIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (myExcelCell != null) {
                myList.add(new PrometheusExcelHSSFCell(this, myExcelCell, iIndex, true));
            }
        }

        /* Return the iterator */
        return myList.listIterator();
    }

    @Override
    public PrometheusExcelHSSFCell getWriteableCellByIndex(final int pIndex) {
        /* Create the cell */
        final HSSFCell myExcelCell = theExcelRow.createCell(pIndex);
        return new PrometheusExcelHSSFCell(this, myExcelCell, pIndex, false);
    }

    @Override
    protected void setHiddenValue(final boolean pHidden) {
        theExcelRow.setZeroHeight(pHidden);
    }

    @Override
    public boolean isHidden() {
        return theExcelRow.getZeroHeight();
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setCellStyle(final PrometheusExcelHSSFCell pCell,
                      final Object pValue) {
        /* Pass through to the sheet */
        theExcelSheet.setCellStyle(pCell, pValue);
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setAlternateCellStyle(final PrometheusExcelHSSFCell pCell,
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
        final OceanusDataFormatter myFormatter = theExcelSheet.getDataFormatter();
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
        final OceanusDataFormatter myFormatter = theExcelSheet.getDataFormatter();
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
        final OceanusDataFormatter myFormatter = theExcelSheet.getDataFormatter();
        return myFormatter.parseValue(pSource, pCurrCode, pClass);
    }
}
