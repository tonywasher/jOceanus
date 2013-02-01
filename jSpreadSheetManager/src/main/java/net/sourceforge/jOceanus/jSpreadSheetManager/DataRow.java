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
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.WorkBookType;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.CellValue;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

/**
 * Class representing a row within a sheet or a view.
 */
public class DataRow {
    /**
     * Sheet type.
     */
    private final WorkBookType theBookType;

    /**
     * Is row readOnly?
     */
    private final boolean isReadOnly;

    /**
     * Is this derived from a view.
     */
    private final boolean isView;

    /**
     * The underlying sheet.
     */
    private final DataSheet theSheet;

    /**
     * The underlying view.
     */
    private final DataView theView;

    /**
     * The index of this row.
     */
    private final int theRowIndex;

    /**
     * The Excel Row.
     */
    private final HSSFRow theExcelRow;

    /**
     * The Oasis Row.
     */
    private final OdfTableRow theOasisRow;

    /**
     * Is the row readOnly.
     * @return true/false
     */
    protected boolean isReadOnly() {
        return isReadOnly;
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public DataSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public DataView getView() {
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
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    protected CellValue evaluateFormula(final HSSFCell pCell) {
        return theSheet.evaluateFormula(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    protected String formatCellValue(final HSSFCell pCell) {
        return theSheet.formatCellValue(pCell);
    }

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pExcelRow the Excel Row
     */
    protected DataRow(final DataSheet pSheet,
                      final HSSFRow pExcelRow) {
        /* Store parameters */
        theSheet = pSheet;
        theExcelRow = pExcelRow;
        theOasisRow = null;
        theBookType = WorkBookType.ExcelXLS;
        theView = null;
        theRowIndex = pExcelRow.getRowNum();
        isReadOnly = false;
        isView = false;
    }

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pOasisRow the Oasis Row
     */
    protected DataRow(final DataSheet pSheet,
                      final OdfTableRow pOasisRow) {
        /* Store parameters */
        theSheet = pSheet;
        theExcelRow = null;
        theOasisRow = pOasisRow;
        theBookType = WorkBookType.OasisODS;
        theView = null;
        theRowIndex = pOasisRow.getRowIndex();
        isReadOnly = false;
        isView = false;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pExcelRow the Excel Row
     */
    protected DataRow(final DataView pView,
                      final HSSFRow pExcelRow) {
        /* Store parameters */
        theSheet = pView.getSheet();
        theExcelRow = pExcelRow;
        theOasisRow = null;
        theBookType = WorkBookType.ExcelXLS;
        theView = pView;
        theRowIndex = pExcelRow.getRowNum()
                      - pView.getFirstCell().getRowIndex();
        isReadOnly = true;
        isView = true;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pOasisRow the Excel Row
     */
    protected DataRow(final DataView pView,
                      final OdfTableRow pOasisRow) {
        /* Store parameters */
        theSheet = pView.getSheet();
        theExcelRow = null;
        theOasisRow = pOasisRow;
        theBookType = WorkBookType.OasisODS;
        theView = pView;
        theRowIndex = pOasisRow.getRowIndex()
                      - pView.getFirstCell().getRowIndex();
        isReadOnly = true;
        isView = true;
    }

    /**
     * Get the next row.
     * @return the next row
     */
    public DataRow getNextRow() {
        /* Determine the required index */
        int myIndex = theRowIndex + 1;

        /* Return the next row */
        if (isView) {
            if (!theView.validRowIndex(myIndex)) {
                return null;
            }
            switch (theBookType) {
                case ExcelXLS:
                    return theSheet.getViewRowByIndex(theView, myIndex);
                case OasisODS:
                    return new DataRow(theView, theOasisRow.getNextRow());
                default:
                    return null;
            }
        } else {
            switch (theBookType) {
                case ExcelXLS:
                    return theSheet.getRowByIndex(myIndex);
                case OasisODS:
                    return new DataRow(theSheet, theOasisRow.getNextRow());
                default:
                    return null;
            }
        }
    }

    /**
     * Get the previous row.
     * @return the previous row
     */
    public DataRow getPreviousRow() {
        /* Determine the required index */
        int myIndex = theRowIndex - 1;

        if (myIndex < 0) {
            return null;
        }

        /* Return the next row */
        if (isView) {
            if (!theView.validRowIndex(myIndex)) {
                return null;
            }
            switch (theBookType) {
                case ExcelXLS:
                    return theSheet.getViewRowByIndex(theView, myIndex);
                case OasisODS:
                    return new DataRow(theView, theOasisRow.getPreviousRow());
                default:
                    return null;
            }
        } else {
            switch (theBookType) {
                case ExcelXLS:
                    return theSheet.getRowByIndex(myIndex);
                case OasisODS:
                    return new DataRow(theSheet, theOasisRow.getPreviousRow());
                default:
                    return null;
            }
        }
    }

    /**
     * Determine number of columns in this row.
     * @return the number of columns.
     */
    public int getColumnCount() {
        /* If this is a view */
        if (isView) {
            /* Number of columns is the number of columns in the view */
            return theView.getColumnCount();
        }

        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                return theExcelRow.getLastCellNum();
            case OasisODS:
                return theOasisRow.getCellCount();
            default:
                return 0;
        }
    }

    /**
     * Get the cell at the required index.
     * @param pIndex the column index.
     * @return the cell
     */
    public DataCell getCellByIndex(final int pIndex) {
        /* Record the required index */
        int myIndex = pIndex;

        /* if this is a view row */
        if (isView) {
            /* Handle invalid index */
            if (!theView.validColumnIndex(pIndex)) {
                return null;
            }

            /* Determine the actual index of the column */
            myIndex = theView.getFirstCell().getColumnIndex()
                      + pIndex;
        }

        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                HSSFCell myExcelCell = theExcelRow.getCell(myIndex);
                if (myExcelCell != null) {
                    return new DataCell(this, myExcelCell);
                } else {
                    return null;
                }
            case OasisODS:
                OdfTableCell myOasisCell = theOasisRow.getCellByIndex(myIndex);
                if ((!isReadOnly)
                    || (myOasisCell.getValueType() != null)) {
                    return new DataCell(this, myOasisCell);
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * Get the cell at the required index.
     * @param pIndex the column index.
     * @return the cell
     */
    public DataCell createCellByIndex(final int pIndex) {
        /* Record the required index */
        int myIndex = pIndex;

        /* if this is a view row */
        if (isView) {
            /* Not allowed */
            return null;
        }

        /* Switch on book type */
        switch (theBookType) {
            case ExcelXLS:
                HSSFCell myExcelCell = theExcelRow.createCell(myIndex);
                if (myExcelCell != null) {
                    return new DataCell(this, myExcelCell);
                } else {
                    return null;
                }
            case OasisODS:
                OdfTableCell myOasisCell = theOasisRow.getCellByIndex(myIndex);
                return new DataCell(this, myOasisCell);
            default:
                return null;
        }
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final DataCell pCell,
                                final CellStyleType pStyle) {
        /* Pass through to the sheet */
        theSheet.setCellStyle(pCell, pStyle);
    }
}
