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

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.CellStyleType;

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
     * Constructor for Excel Sheet.
     * @param pBook the WorkBook
     * @param pSheet the Excel sheet
     * @param pIndex the index of the sheet
     */
    protected ExcelSheet(final ExcelWorkBook pBook,
                         final HSSFSheet pSheet,
                         final int pIndex) {
        /* Construct super-class */
        super(pSheet.getSheetName());

        /* Store parameters */
        theExcelBook = pBook;
        theExcelSheet = pSheet;
        theIndex = pIndex;
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
    public DataRow getRowByIndex(final int pRowIndex) {
        HSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        if (myExcelRow == null) {
            /* Create the row if it does not exist */
            myExcelRow = theExcelSheet.createRow(pRowIndex);
        }
        return new ExcelRow(this, myExcelRow, pRowIndex);
    }

    @Override
    protected DataRow getRowByIndex(final DataView pView,
                                    final int pRowIndex) {
        /* Determine the actual index of the row */
        int myIndex = pView.convertRowIndex(pRowIndex);
        if (myIndex < 0) {
            return null;
        }

        HSSFRow myExcelRow = theExcelSheet.getRow(myIndex);
        return new ExcelRow(pView, myExcelRow, pRowIndex);
    }

    @Override
    public void declareRange(final String pName,
                             final CellPosition pFirstCell,
                             final CellPosition pLastCell) throws JDataException {
        /* Build the area reference */
        String myName = getName();
        CellReference myFirst = new CellReference(myName, pFirstCell.getRowIndex(), pFirstCell.getColumnIndex(), true, true);
        CellReference myLast = new CellReference(myName, pLastCell.getRowIndex(), pLastCell.getColumnIndex(), true, true);
        AreaReference myArea = new AreaReference(myFirst, myLast);

        /* Declare to workBook */
        theExcelBook.declareRange(pName, myArea);
    }

    @Override
    public void applyDataValidation(final CellPosition pFirstCell,
                                    final CellPosition pLastCell,
                                    final String pName) throws JDataException {
        /* Create the CellAddressList */
        CellRangeAddressList myCells = new CellRangeAddressList(pFirstCell.getRowIndex(), pLastCell.getRowIndex(), pFirstCell.getColumnIndex(),
                pLastCell.getColumnIndex());

        /* Declare to workBook */
        theExcelBook.applyDataValidation(theExcelSheet, myCells, pName);
    }

    @Override
    public void applyDataFilter(final CellPosition pBaseCell,
                                final int pNumRows) throws JDataException {
        /* Create the CellAddressList */
        int myRow = pBaseCell.getRowIndex();
        int myCol = pBaseCell.getColumnIndex();
        CellRangeAddressList myCells = new CellRangeAddressList(myRow, myRow
                                                                       + pNumRows
                                                                       - 1, myCol, myCol);

        /* Declare to workBook */
        theExcelBook.applyDataFilter(theExcelSheet, myCells);
    }

    @Override
    public void createFreezePane(final CellPosition pFreezeCell) {
        theExcelSheet.createFreezePane(pFreezeCell.getColumnIndex(), pFreezeCell.getRowIndex());
    }

    @Override
    public void setColumnHidden(final int pColIndex,
                                final boolean isHidden) {
        theExcelSheet.setColumnHidden(pColIndex, isHidden);
    }

    @Override
    public void setColumnWidth(final int pColIndex,
                               final int pWidth) {
        /* Set the column width */
        theExcelSheet.setColumnWidth(pColIndex, WIDTH_CHAR
                                                * pWidth);
    }

    @Override
    public void setDefaultColumnStyle(final int pColIndex,
                                      final CellStyleType pStyle) {
        theExcelSheet.setDefaultColumnStyle(pColIndex, theExcelBook.getCellStyle(pStyle));
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pStyle the style type to use
     */
    protected void setCellStyle(final ExcelCell pCell,
                                final CellStyleType pStyle) {
        pCell.setCellStyle(theExcelBook.getCellStyle(pStyle));
    }
}
