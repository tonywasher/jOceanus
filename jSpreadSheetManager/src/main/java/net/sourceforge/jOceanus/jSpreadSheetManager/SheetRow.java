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

import net.sourceforge.jOceanus.jSpreadSheetManager.SheetWorkBook.CellStyleType;
import net.sourceforge.jOceanus.jSpreadSheetManager.SheetWorkBook.WorkBookType;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;

/**
 * Class representing a row within a sheet or a view.
 */
public class SheetRow {
    /**
     * Sheet type.
     */
    private final WorkBookType theBookType;

    /**
     * Is row readOnly?
     */
    private final boolean isReadOnly;

    /**
     * The underlying sheet.
     */
    private final SheetSheet theSheet;

    /**
     * The underlying view.
     */
    private final SheetView theView;

    /**
     * The index of this row.
     */
    private final int theRowIndex;

    /**
     * The Excel Row.
     */
    private final HSSFRow theExcelRow;

    /**
     * DataFormatter.
     */
    private final DataFormatter theFormatter;

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
    public SheetSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the underlying view.
     * @return the underlying view
     */
    public SheetView getView() {
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
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pExcelRow the Excel Row
     * @param pFormatter the data formatter
     */
    protected SheetRow(final SheetSheet pSheet,
                       final HSSFRow pExcelRow,
                       final DataFormatter pFormatter) {
        /* Store parameters */
        theSheet = pSheet;
        theExcelRow = pExcelRow;
        theOasisRow = null;
        theBookType = WorkBookType.EXCELXLS;
        theView = null;
        theRowIndex = pExcelRow.getRowNum();
        isReadOnly = false;
        theFormatter = pFormatter;
    }

    /**
     * Constructor.
     * @param pSheet the sheet for the row
     * @param pOasisRow the Oasis Row
     */
    protected SheetRow(final SheetSheet pSheet,
                       final OdfTableRow pOasisRow) {
        /* Store parameters */
        theSheet = pSheet;
        theExcelRow = null;
        theOasisRow = pOasisRow;
        theBookType = WorkBookType.OASISODS;
        theView = null;
        theRowIndex = pOasisRow.getRowIndex();
        isReadOnly = false;
        theFormatter = null;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pExcelRow the Excel Row
     */
    protected SheetRow(final SheetView pView,
                       final HSSFRow pExcelRow,
                       final DataFormatter pFormatter) {
        /* Store parameters */
        theSheet = pView.getSheet();
        theExcelRow = pExcelRow;
        theOasisRow = null;
        theBookType = WorkBookType.EXCELXLS;
        theView = pView;
        theRowIndex = pExcelRow.getRowNum()
                      - pView.getFirstCell().getRowIndex();
        isReadOnly = true;
        theFormatter = pFormatter;
    }

    /**
     * Constructor.
     * @param pView the view for the row
     * @param pOasisRow the Excel Row
     * @param pFormatter the data formatter
     */
    protected SheetRow(final SheetView pView,
                       final OdfTableRow pOasisRow) {
        /* Store parameters */
        theSheet = pView.getSheet();
        theExcelRow = null;
        theOasisRow = pOasisRow;
        theBookType = WorkBookType.OASISODS;
        theView = pView;
        theRowIndex = pOasisRow.getRowIndex()
                      - pView.getFirstCell().getRowIndex();
        isReadOnly = true;
        theFormatter = null;
    }

    /**
     * Get the next row
     * @return the next row
     */
    public SheetRow getNextRow() {
        /* Determine the required index */
        int myIndex = theRowIndex + 1;

        /* Return the next row */
        if (theView != null) {
            return theView.getRowByIndex(myIndex);
        } else {
            return theSheet.getRowByIndex(myIndex);
        }
    }

    /**
     * Get the previous row
     * @return the previous row
     */
    public SheetRow getPreviousRow() {
        /* Determine the required index */
        int myIndex = theRowIndex - 1;

        /* Return the next row */
        if (theView != null) {
            return theView.getRowByIndex(myIndex);
        } else {
            return theSheet.getRowByIndex(myIndex);
        }
    }

    /**
     * Determine number of columns in this row.
     * @return the number of columns.
     */
    public int getColumnCount() {
        /* If this is a view */
        if (theView != null) {
            /* Number of columns is the number of columns in the view */
            return theView.getColumnCount();
        }

        /* Switch on book type */
        switch (theBookType) {
            case EXCELXLS:
                return theExcelRow.getLastCellNum();
            case OASISODS:
                return theOasisRow.getCellCount();
            default:
                return 0;
        }
    }

    /**
     * Get the cell at the required index
     * @param pIndex the column index.
     * @return the cell
     */
    public SheetCell getCellByIndex(final int pIndex) {
        /* Record the required index */
        int myIndex = pIndex;

        /* if this is a view row */
        if (theView != null) {
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
            case EXCELXLS:
                HSSFCell myExcelCell = theExcelRow.getCell(myIndex);
                if (myExcelCell != null) {
                    return new SheetCell(this, myExcelCell, theFormatter);
                } else {
                    return null;
                }
            case OASISODS:
                OdfTableCell myOasisCell = theOasisRow.getCellByIndex(myIndex);
                if ((!isReadOnly)
                    || (myOasisCell.getValueType() != null)) {
                    return new SheetCell(this, myOasisCell);
                } else {
                    return null;
                }
            default:
                return null;
        }
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final SheetCell pCell,
                                final CellStyleType pStyle) {
        /* Pass through to the sheet */
        theSheet.setCellStyle(pCell, pStyle);
    }
}
