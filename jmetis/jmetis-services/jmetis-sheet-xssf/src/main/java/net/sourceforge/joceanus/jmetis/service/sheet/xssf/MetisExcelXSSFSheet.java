/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.service.sheet.xssf;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetFormats;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Class representing an Excel sheet within a workBook.
 */
public class MetisExcelXSSFSheet
        extends MetisSheetSheet {
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
    private final MetisExcelXSSFWorkBook theExcelBook;

    /**
     * The Excel Sheet.
     */
    private final XSSFSheet theExcelSheet;

    /**
     * Constructor for Excel Sheet.
     * @param pBook the WorkBook
     * @param pSheet the Excel sheet
     * @param pIndex the index of the sheet
     * @param pReadOnly is the sheet readOnly?
     */
    MetisExcelXSSFSheet(final MetisExcelXSSFWorkBook pBook,
                        final XSSFSheet pSheet,
                        final int pIndex,
                        final boolean pReadOnly) {
        /* Construct super-class */
        super(pBook, pSheet.getSheetName(), pReadOnly);

        /* Store parameters */
        theExcelBook = pBook;
        theExcelSheet = pSheet;
        theIndex = pIndex;
    }

    /**
     * evaluate the formula for a cell.
     * @param pCell the cell to evaluate
     * @return the calculated value
     */
    CellValue evaluateFormula(final XSSFCell pCell) {
        return theExcelBook.evaluateFormula(pCell);
    }

    /**
     * Format the cell value.
     * @param pCell the cell to evaluate
     * @return the formatted value
     */
    String formatCellValue(final XSSFCell pCell) {
        return theExcelBook.formatCellValue(pCell);
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    protected TethysDataFormatter getDataFormatter() {
        return theExcelBook.getDataFormatter();
    }

    @Override
    public int getSheetIndex() {
        return theIndex;
    }

    @Override
    public int getRowCount() {
        final int iLastRowNum = theExcelSheet.getLastRowNum();
        return iLastRowNum == 0
                                ? theExcelSheet.getPhysicalNumberOfRows()
                                : iLastRowNum + 1;
    }

    @Override
    public MetisExcelXSSFRow getReadOnlyRowByIndex(final int pRowIndex) {
        final XSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        return myExcelRow == null
                                  ? null
                                  : new MetisExcelXSSFRow(this, myExcelRow, pRowIndex, true);
    }

    @Override
    public MetisExcelXSSFRow getMutableRowByIndex(final int pRowIndex) {
        if (isReadOnly()) {
            return null;
        }
        XSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        if (myExcelRow == null) {
            /* Create the row if it does not exist */
            myExcelRow = theExcelSheet.createRow(pRowIndex);
        }
        return new MetisExcelXSSFRow(this, myExcelRow, pRowIndex, false);
    }

    @Override
    public MetisExcelXSSFColumn getReadOnlyColumnByIndex(final int pColIndex) {
        return new MetisExcelXSSFColumn(this, pColIndex, true);
    }

    @Override
    public MetisExcelXSSFColumn getMutableColumnByIndex(final int pColIndex) {
        return isReadOnly()
                          ? null
                          : new MetisExcelXSSFColumn(this, pColIndex, false);
    }

    @Override
    public boolean isHidden() {
        return theExcelBook.isSheetHidden(theIndex);
    }

    @Override
    public void setHidden(final boolean isHidden) {
        if (!isReadOnly()) {
            theExcelBook.setSheetHidden(theIndex, isHidden);
        }
    }

    @Override
    public void declareRange(final String pName,
                             final MetisSheetCellPosition pFirstCell,
                             final MetisSheetCellPosition pLastCell) throws OceanusException {
        if (!isReadOnly()) {
            /* Build the area reference */
            final String myName = getName();
            final CellReference myFirst = new CellReference(myName, pFirstCell.getRowIndex(), pFirstCell.getColumnIndex(), true, true);
            final CellReference myLast = new CellReference(myName, pLastCell.getRowIndex(), pLastCell.getColumnIndex(), true, true);
            final AreaReference myArea = new AreaReference(myFirst, myLast, SpreadsheetVersion.EXCEL97);

            /* Declare to workBook */
            theExcelBook.declareRange(pName, myArea);
        }
    }

    @Override
    public void applyDataValidation(final MetisSheetCellPosition pFirstCell,
                                    final MetisSheetCellPosition pLastCell,
                                    final String pName) {
        if (!isReadOnly()) {
            /* Create the CellAddressList */
            final CellRangeAddressList myCells = new CellRangeAddressList(pFirstCell.getRowIndex(), pLastCell.getRowIndex(), pFirstCell.getColumnIndex(),
                    pLastCell.getColumnIndex());

            /* Declare to workBook */
            theExcelBook.applyDataValidation(theExcelSheet, myCells, pName);
        }
    }

    @Override
    public void applyDataFilter(final MetisSheetCellPosition pBaseCell,
                                final int pNumRows) {
        if (!isReadOnly()) {
            /* Create the CellAddressList */
            final int myRow = pBaseCell.getRowIndex();
            final int myCol = pBaseCell.getColumnIndex();
            final CellRangeAddressList myCells = new CellRangeAddressList(myRow, myRow
                                                                                 + pNumRows
                                                                                 - 1, myCol, myCol);

            /* Declare to workBook */
            theExcelBook.applyDataFilter(theExcelSheet, myCells);
        }
    }

    @Override
    public void createFreezePane(final MetisSheetCellPosition pFreezeCell) {
        if (!isReadOnly()) {
            theExcelSheet.createFreezePane(pFreezeCell.getColumnIndex(), pFreezeCell.getRowIndex());
        }
    }

    /**
     * Set the column's hidden status.
     * @param pColIndex the column index
     * @param isHidden true/false
     */
    void setColumnHidden(final int pColIndex,
                         final boolean isHidden) {
        theExcelSheet.setColumnHidden(pColIndex, isHidden);
    }

    /**
     * Is the column hidden?
     * @param pColIndex the column
     * @return true/false
     */
    boolean isColumnHidden(final int pColIndex) {
        return theExcelSheet.isColumnHidden(pColIndex);
    }

    /**
     * Set the default style for the column.
     * @param pColIndex the column index
     * @param pStyle the style
     */
    void setDefaultCellStyle(final int pColIndex,
                             final MetisSheetCellStyleType pStyle) {
        theExcelSheet.setDefaultColumnStyle(pColIndex, theExcelBook.getCellStyle(pStyle));
        theExcelSheet.setColumnWidth(pColIndex, getColumnWidth(pStyle));
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setCellStyle(final MetisExcelXSSFCell pCell,
                      final Object pValue) {
        pCell.setCellStyle(theExcelBook.getCellStyle(pValue));
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setAlternateCellStyle(final MetisExcelXSSFCell pCell,
                               final Object pValue) {
        pCell.setCellStyle(theExcelBook.getAlternateCellStyle(pValue));
    }

    /**
     * Obtain column cell width.
     * @param pStyle the style type
     * @return the name of the style
     */
    private static int getColumnWidth(final MetisSheetCellStyleType pStyle) {
        switch (pStyle) {
            case INTEGER:
                return MetisSheetFormats.WIDTH_INT
                       * WIDTH_CHAR;
            case BOOLEAN:
                return MetisSheetFormats.WIDTH_BOOL
                       * WIDTH_CHAR;
            case DATE:
                return MetisSheetFormats.WIDTH_DATE
                       * WIDTH_CHAR;
            case MONEY:
                return MetisSheetFormats.WIDTH_MONEY
                       * WIDTH_CHAR;
            case PRICE:
                return MetisSheetFormats.WIDTH_PRICE
                       * WIDTH_CHAR;
            case UNITS:
                return MetisSheetFormats.WIDTH_UNITS
                       * WIDTH_CHAR;
            case RATE:
                return MetisSheetFormats.WIDTH_RATE
                       * WIDTH_CHAR;
            case DILUTION:
                return MetisSheetFormats.WIDTH_DILUTION
                       * WIDTH_CHAR;
            case RATIO:
                return MetisSheetFormats.WIDTH_RATIO
                       * WIDTH_CHAR;
            case STRING:
            default:
                return MetisSheetFormats.WIDTH_STRING
                       * WIDTH_CHAR;
        }
    }
}
