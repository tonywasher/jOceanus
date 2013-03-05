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

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;

/**
 * Class representing a row within a sheet or a view.
 */
public class ExcelRow
        extends DataRow {
    /**
     * The Excel Sheet.
     */
    private final ExcelSheet theExcelSheet;

    /**
     * The Excel Row.
     */
    private final HSSFRow theExcelRow;

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

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pRow the Excel Row
     * @param pRowIndex the RowIndex
     */
    protected ExcelRow(final ExcelSheet pSheet,
                       final HSSFRow pRow,
                       final int pRowIndex) {
        /* Store parameters */
        super(pSheet, pRowIndex);
        theExcelSheet = pSheet;
        theExcelRow = pRow;
    }

    @Override
    public ExcelRow getNextRow() {
        /* Determine the required index */
        int myIndex = getRowIndex() + 1;

        /* Return the next row */
        return theExcelSheet.createRowByIndex(myIndex);
    }

    @Override
    public ExcelRow getPreviousRow() {
        /* Determine the required index */
        int myIndex = getRowIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous row */
        return theExcelSheet.getRowByIndex(myIndex);
    }

    @Override
    public int getCellCount() {
        /* return the cell count */
        return theExcelRow.getLastCellNum();
    }

    @Override
    public ExcelCell getCellByIndex(final int pIndex) {
        /* Record the required index */
        // int myIndex = (isView)
        // ? getView().convertColumnIndex(pIndex)
        // : pIndex;
        if (pIndex < 0) {
            return null;
        }

        /* Access the cell */
        HSSFCell myExcelCell = theExcelRow.getCell(pIndex, Row.RETURN_BLANK_AS_NULL);
        return (myExcelCell != null)
                ? new ExcelCell(this, myExcelCell, pIndex)
                : null;
    }

    @Override
    public ExcelCell createCellByIndex(final int pIndex) {
        /* Create the cell */
        HSSFCell myExcelCell = theExcelRow.createCell(pIndex);
        return (myExcelCell != null)
                ? new ExcelCell(this, myExcelCell, pIndex)
                : null;
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final ExcelCell pCell,
                                final CellStyleType pStyle) {
        /* Pass through to the sheet */
        theExcelSheet.setCellStyle(pCell, pStyle);
    }
}
