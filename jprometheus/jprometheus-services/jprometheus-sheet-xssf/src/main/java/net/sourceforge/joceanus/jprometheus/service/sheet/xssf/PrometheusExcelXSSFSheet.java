/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2021 Tony Washer
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

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCellPosition;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCellStyleType;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetFormats;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Class representing an Excel sheet within a workBook.
 */
public class PrometheusExcelXSSFSheet
        extends PrometheusSheetSheet {
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
    private final PrometheusExcelXSSFWorkBook theExcelBook;

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
    PrometheusExcelXSSFSheet(final PrometheusExcelXSSFWorkBook pBook,
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
    public PrometheusExcelXSSFRow getReadOnlyRowByIndex(final int pRowIndex) {
        final XSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        return myExcelRow == null
                                  ? null
                                  : new PrometheusExcelXSSFRow(this, myExcelRow, pRowIndex, true);
    }

    @Override
    protected ListIterator<PrometheusSheetRow> iteratorForRange(final int pFirstIndex,
                                                                final int pLastIndex) {
        /* Determine bounds for search */
        final int myLower = Math.max(pFirstIndex, theExcelSheet.getFirstRowNum());
        final int myUpper = Math.min(pLastIndex, theExcelSheet.getLastRowNum());

        /* Create a list of cells */
        final List<PrometheusSheetRow> myList = new ArrayList<>();
        for (int iIndex = myLower; iIndex <= myUpper; iIndex++) {
            /* Only return a row if a value is present */
            final XSSFRow myExcelRow = theExcelSheet.getRow(iIndex);
            if (myExcelRow != null) {
                myList.add(new PrometheusExcelXSSFRow(this, myExcelRow, iIndex, true));
            }
        }

        /* Return the iterator */
        return myList.listIterator();
    }

    @Override
    public PrometheusExcelXSSFRow getMutableRowByIndex(final int pRowIndex) {
        if (isReadOnly()) {
            return null;
        }
        XSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        if (myExcelRow == null) {
            /* Create the row if it does not exist */
            myExcelRow = theExcelSheet.createRow(pRowIndex);
        }
        return new PrometheusExcelXSSFRow(this, myExcelRow, pRowIndex, false);
    }

    @Override
    public PrometheusExcelXSSFColumn getReadOnlyColumnByIndex(final int pColIndex) {
        return new PrometheusExcelXSSFColumn(this, pColIndex, true);
    }

    @Override
    public PrometheusExcelXSSFColumn getMutableColumnByIndex(final int pColIndex) {
        return isReadOnly()
                          ? null
                          : new PrometheusExcelXSSFColumn(this, pColIndex, false);
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
                             final PrometheusSheetCellPosition pFirstCell,
                             final PrometheusSheetCellPosition pLastCell) throws OceanusException {
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
    public void applyDataValidation(final PrometheusSheetCellPosition pFirstCell,
                                    final PrometheusSheetCellPosition pLastCell,
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
    public void applyDataFilter(final PrometheusSheetCellPosition pBaseCell,
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
    public void createFreezePane(final PrometheusSheetCellPosition pFreezeCell) {
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
                             final PrometheusSheetCellStyleType pStyle) {
        theExcelSheet.setDefaultColumnStyle(pColIndex, theExcelBook.getCellStyle(pStyle));
        theExcelSheet.setColumnWidth(pColIndex, getColumnWidth(pStyle));
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setCellStyle(final PrometheusExcelXSSFCell pCell,
                      final Object pValue) {
        pCell.setCellStyle(theExcelBook.getCellStyle(pValue));
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setAlternateCellStyle(final PrometheusExcelXSSFCell pCell,
                               final Object pValue) {
        pCell.setCellStyle(theExcelBook.getAlternateCellStyle(pValue));
    }

    /**
     * Obtain column cell width.
     * @param pStyle the style type
     * @return the name of the style
     */
    private static int getColumnWidth(final PrometheusSheetCellStyleType pStyle) {
        switch (pStyle) {
            case INTEGER:
                return PrometheusSheetFormats.WIDTH_INT
                       * WIDTH_CHAR;
            case BOOLEAN:
                return PrometheusSheetFormats.WIDTH_BOOL
                       * WIDTH_CHAR;
            case DATE:
                return PrometheusSheetFormats.WIDTH_DATE
                       * WIDTH_CHAR;
            case MONEY:
                return PrometheusSheetFormats.WIDTH_MONEY
                       * WIDTH_CHAR;
            case PRICE:
                return PrometheusSheetFormats.WIDTH_PRICE
                       * WIDTH_CHAR;
            case UNITS:
                return PrometheusSheetFormats.WIDTH_UNITS
                       * WIDTH_CHAR;
            case RATE:
                return PrometheusSheetFormats.WIDTH_RATE
                       * WIDTH_CHAR;
            case DILUTION:
                return PrometheusSheetFormats.WIDTH_DILUTION
                       * WIDTH_CHAR;
            case RATIO:
                return PrometheusSheetFormats.WIDTH_RATIO
                       * WIDTH_CHAR;
            case STRING:
            default:
                return PrometheusSheetFormats.WIDTH_STRING
                       * WIDTH_CHAR;
        }
    }
}
