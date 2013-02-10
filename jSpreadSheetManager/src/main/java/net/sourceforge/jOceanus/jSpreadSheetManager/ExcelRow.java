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
     * Is this a View row.
     */
    private final boolean isView;

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
        isView = false;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pRow the Excel Row
     * @param pRowIndex the RowIndex
     */
    protected ExcelRow(final DataView pView,
                       final HSSFRow pRow,
                       final int pRowIndex) {
        /* Store parameters */
        super(pView, pRowIndex);
        theExcelSheet = (ExcelSheet) getSheet();
        theExcelRow = pRow;
        isView = true;
    }

    @Override
    public DataRow getNextRow() {
        /* Determine the required index */
        int myIndex = getRowIndex() + 1;

        /* Return the next row */
        return (isView) ? theExcelSheet.getRowByIndex(getView(), myIndex) : theExcelSheet.getRowByIndex(myIndex);
    }

    @Override
    public DataRow getPreviousRow() {
        /* Determine the required index */
        int myIndex = getRowIndex() - 1;
        if (myIndex < 0) {
            return null;
        }

        /* Return the previous row */
        return (isView) ? theExcelSheet.getRowByIndex(getView(), myIndex) : theExcelSheet.getRowByIndex(myIndex);
    }

    @Override
    public int getCellCount() {
        /* If this is a view */
        return (isView) ? getView().getColumnCount() : theExcelRow.getLastCellNum();
    }

    @Override
    public DataCell getCellByIndex(final int pIndex) {
        /* Record the required index */
        int myIndex = (isView) ? getView().convertColumnIndex(pIndex) : pIndex;
        if (myIndex < 0) {
            return null;
        }

        /* Access the cell */
        HSSFCell myExcelCell = theExcelRow.getCell(myIndex, Row.RETURN_BLANK_AS_NULL);
        return (myExcelCell != null) ? new ExcelCell(this, myExcelCell, pIndex) : null;
    }

    @Override
    public DataCell createCellByIndex(final int pIndex) {
        /* if this is a view row */
        if (isView) {
            /* Not allowed */
            return null;
        }

        /* Create the cell */
        HSSFCell myExcelCell = theExcelRow.createCell(pIndex);
        return (myExcelCell != null) ? new ExcelCell(this, myExcelCell, pIndex) : null;
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
