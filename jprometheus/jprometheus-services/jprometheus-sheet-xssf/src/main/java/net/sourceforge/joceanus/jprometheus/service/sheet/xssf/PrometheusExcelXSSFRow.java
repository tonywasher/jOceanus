/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.service.sheet.xssf;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Class representing a row within a sheet or a view.
 */
public class PrometheusExcelXSSFRow
        extends PrometheusSheetRow {
    /**
     * The Excel Sheet.
     */
    private final PrometheusExcelXSSFSheet theExcelSheet;

    /**
     * The Excel Row.
     */
    private final XSSFRow theExcelRow;

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRow the Excel Row
     * @param pRowIndex the RowIndex
     * @param pReadOnly is the row readOnly?
     */
    PrometheusExcelXSSFRow(final PrometheusExcelXSSFSheet pSheet,
                           final XSSFRow pRow,
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
    CellValue evaluateFormula(final XSSFCell pCell) {
        return theExcelSheet.evaluateFormula(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    String formatCellValue(final XSSFCell pCell) {
        return theExcelSheet.formatCellValue(pCell);
    }

    @Override
    public PrometheusExcelXSSFRow getNextRow() {
        /* Determine the required index */
        final int myIndex = getRowIndex() + 1;

        /* Return the next row */
        return theExcelSheet.getReadOnlyRowByIndex(myIndex);
    }

    @Override
    public PrometheusExcelXSSFRow getPreviousRow() {
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
    public PrometheusExcelXSSFCell getReadOnlyCellByIndex(final int pIndex) {
        /* Handle negative index */
        if (pIndex < 0) {
            return null;
        }

        /* Access the cell */
        final XSSFCell myExcelCell = theExcelRow.getCell(pIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return myExcelCell != null
                                   ? new PrometheusExcelXSSFCell(this, myExcelCell, pIndex, true)
                                   : null;
    }

    /**
     * Obtain an iterator of non-null cells for the range.
     * @param pFirstCell the index of the first cell.
     * @param pLastCell the index of the last cell.
     * @return the column
     */
    protected ListIterator<PrometheusSheetCell> iteratorForRange(final int pFirstCell,
                                                                 final int pLastCell) {
        /* Determine bounds for search */
        final int myLower = Math.max(pFirstCell, theExcelRow.getFirstCellNum());
        final int myUpper = Math.min(pLastCell, theExcelRow.getLastCellNum());

        /* Create a list of cells */
        final List<PrometheusSheetCell> myList = new ArrayList<>();
        for (int iIndex = myLower; iIndex <= myUpper; iIndex++) {
            /* Only return a cell if a value is present */
            final XSSFCell myExcelCell = theExcelRow.getCell(iIndex, MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (myExcelCell != null) {
                myList.add(new PrometheusExcelXSSFCell(this, myExcelCell, iIndex, true));
            }
        }

        /* Return the iterator */
        return myList.listIterator();
    }

    @Override
    protected PrometheusExcelXSSFCell getWriteableCellByIndex(final int pIndex) {
        /* Create the cell */
        final XSSFCell myExcelCell = theExcelRow.createCell(pIndex);
        return myExcelCell != null
                                   ? new PrometheusExcelXSSFCell(this, myExcelCell, pIndex, false)
                                   : null;
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
    void setCellStyle(final PrometheusExcelXSSFCell pCell,
                      final Object pValue) {
        /* Pass through to the sheet */
        theExcelSheet.setCellStyle(pCell, pValue);
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setAlternateCellStyle(final PrometheusExcelXSSFCell pCell,
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
        final TethysDataFormatter myFormatter = theExcelSheet.getDataFormatter();
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
        final TethysDataFormatter myFormatter = theExcelSheet.getDataFormatter();
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
        final TethysDataFormatter myFormatter = theExcelSheet.getDataFormatter();
        return myFormatter.parseValue(pSource, pCurrCode, pClass);
    }
}