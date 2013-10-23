/*******************************************************************************
 * jSpreadSheetManager: SpreadSheet management
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

/**
 * Class representing an Excel sheet within a workBook.
 */
public class ExcelSheet
        extends DataSheet {
    /**
     * Character width.
     */
    private static final int WIDTH_CHAR = 256;

    /**
     * The Sheet index.
     */
    private final int theIndex;

    /**
     * The Excel WorkBook.
     */
    private final ExcelWorkBook theExcelBook;

    /**
     * The Excel Sheet.
     */
    private final HSSFSheet theExcelSheet;

    /**
     * Is the sheet readOnly.
     */
    private final boolean isReadOnly;

    /**
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    protected CellValue evaluateFormula(final HSSFCell pCell) {
        return theExcelBook.evaluateFormula(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    protected String formatCellValue(final HSSFCell pCell) {
        return theExcelBook.formatCellValue(pCell);
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    protected JDataFormatter getDataFormatter() {
        return theExcelBook.getDataFormatter();
    }

    /**
     * Constructor for Excel Sheet.
     * @param pBook the WorkBook
     * @param pSheet the Excel sheet
     * @param pIndex the index of the sheet
     * @param pReadOnly is the sheet readOnly?
     */
    protected ExcelSheet(final ExcelWorkBook pBook,
                         final HSSFSheet pSheet,
                         final int pIndex,
                         final boolean pReadOnly) {
        /* Construct super-class */
        super(pSheet.getSheetName());

        /* Store parameters */
        theExcelBook = pBook;
        theExcelSheet = pSheet;
        theIndex = pIndex;
        isReadOnly = pReadOnly;
    }

    @Override
    public int getSheetIndex() {
        return theIndex;
    }

    @Override
    public int getRowCount() {
        int iLastRowNum = theExcelSheet.getLastRowNum();
        return (iLastRowNum == 0)
                ? theExcelSheet.getPhysicalNumberOfRows()
                : iLastRowNum + 1;
    }

    @Override
    public ExcelRow getReadOnlyRowByIndex(final int pRowIndex) {
        HSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        return (myExcelRow == null)
                ? null
                : new ExcelRow(this, myExcelRow, pRowIndex, true);
    }

    @Override
    public ExcelRow getMutableRowByIndex(final int pRowIndex) {
        if (isReadOnly) {
            return null;
        }
        HSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        if (myExcelRow == null) {
            /* Create the row if it does not exist */
            myExcelRow = theExcelSheet.createRow(pRowIndex);
        }
        return new ExcelRow(this, myExcelRow, pRowIndex, false);
    }

    @Override
    public ExcelColumn getReadOnlyColumnByIndex(final int pColIndex) {
        return new ExcelColumn(this, pColIndex, true);
    }

    @Override
    public ExcelColumn getMutableColumnByIndex(final int pColIndex) {
        return (isReadOnly)
                ? null
                : new ExcelColumn(this, pColIndex, false);
    }

    @Override
    public boolean isHidden() {
        return theExcelBook.isSheetHidden(theIndex);
    }

    @Override
    public void setHidden(final boolean isHidden) {
        if (!isReadOnly) {
            theExcelBook.setSheetHidden(theIndex, isHidden);
        }
    }

    @Override
    public void declareRange(final String pName,
                             final CellPosition pFirstCell,
                             final CellPosition pLastCell) throws JDataException {
        if (!isReadOnly) {
            /* Build the area reference */
            String myName = getName();
            CellReference myFirst = new CellReference(myName, pFirstCell.getRowIndex(), pFirstCell.getColumnIndex(), true, true);
            CellReference myLast = new CellReference(myName, pLastCell.getRowIndex(), pLastCell.getColumnIndex(), true, true);
            AreaReference myArea = new AreaReference(myFirst, myLast);

            /* Declare to workBook */
            theExcelBook.declareRange(pName, myArea);
        }
    }

    @Override
    public void applyDataValidation(final CellPosition pFirstCell,
                                    final CellPosition pLastCell,
                                    final String pName) throws JDataException {
        if (!isReadOnly) {
            /* Create the CellAddressList */
            CellRangeAddressList myCells = new CellRangeAddressList(pFirstCell.getRowIndex(), pLastCell.getRowIndex(), pFirstCell.getColumnIndex(),
                    pLastCell.getColumnIndex());

            /* Declare to workBook */
            theExcelBook.applyDataValidation(theExcelSheet, myCells, pName);
        }
    }

    @Override
    public void applyDataFilter(final CellPosition pBaseCell,
                                final int pNumRows) throws JDataException {
        if (!isReadOnly) {
            /* Create the CellAddressList */
            int myRow = pBaseCell.getRowIndex();
            int myCol = pBaseCell.getColumnIndex();
            CellRangeAddressList myCells = new CellRangeAddressList(myRow, myRow
                                                                           + pNumRows
                                                                           - 1, myCol, myCol);

            /* Declare to workBook */
            theExcelBook.applyDataFilter(theExcelSheet, myCells);
        }
    }

    @Override
    public void createFreezePane(final CellPosition pFreezeCell) {
        if (!isReadOnly) {
            theExcelSheet.createFreezePane(pFreezeCell.getColumnIndex(), pFreezeCell.getRowIndex());
        }
    }

    /**
     * Set the column's hidden status.
     * @param pColIndex the column index
     * @param isHidden true/false
     */
    protected void setColumnHidden(final int pColIndex,
                                   final boolean isHidden) {
        theExcelSheet.setColumnHidden(pColIndex, isHidden);
    }

    /**
     * Is the column hidden?
     * @param pColIndex the column
     * @return true/false
     */
    protected boolean isColumnHidden(final int pColIndex) {
        return theExcelSheet.isColumnHidden(pColIndex);
    }

    /**
     * Set the default style for the column.
     * @param pColIndex the column index
     * @param pStyle the style
     */
    protected void setDefaultCellStyle(final int pColIndex,
                                       final CellStyleType pStyle) {
        theExcelSheet.setDefaultColumnStyle(pColIndex, theExcelBook.getCellStyle(pStyle));
        theExcelSheet.setColumnWidth(pColIndex, getColumnWidth(pStyle));
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setCellStyle(final ExcelCell pCell,
                                final Object pValue) {
        pCell.setCellStyle(theExcelBook.getCellStyle(pValue));
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setAlternateCellStyle(final ExcelCell pCell,
                                         final Object pValue) {
        pCell.setCellStyle(theExcelBook.getAlternateCellStyle(pValue));
    }

    /**
     * Obtain column cell width.
     * @param pStyle the style type
     * @return the name of the style
     */
    private static int getColumnWidth(final CellStyleType pStyle) {
        switch (pStyle) {
            case Integer:
                return DataWorkBook.WIDTH_INT
                       * WIDTH_CHAR;
            case Boolean:
                return DataWorkBook.WIDTH_BOOL
                       * WIDTH_CHAR;
            case Date:
                return DataWorkBook.WIDTH_DATE
                       * WIDTH_CHAR;
            case Money:
                return DataWorkBook.WIDTH_MONEY
                       * WIDTH_CHAR;
            case Price:
                return DataWorkBook.WIDTH_PRICE
                       * WIDTH_CHAR;
            case Units:
                return DataWorkBook.WIDTH_UNITS
                       * WIDTH_CHAR;
            case Rate:
                return DataWorkBook.WIDTH_RATE
                       * WIDTH_CHAR;
            case Dilution:
                return DataWorkBook.WIDTH_DILUTION
                       * WIDTH_CHAR;
            case Ratio:
                return DataWorkBook.WIDTH_RATIO
                       * WIDTH_CHAR;
            case String:
            default:
                return DataWorkBook.WIDTH_STRING
                       * WIDTH_CHAR;
        }
    }
}