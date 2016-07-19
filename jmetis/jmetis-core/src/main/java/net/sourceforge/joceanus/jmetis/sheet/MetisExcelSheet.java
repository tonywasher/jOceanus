/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.sheet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.util.CellReference;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Class representing an Excel sheet within a workBook.
 */
public class MetisExcelSheet
        extends MetisDataSheet {
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
    private final MetisExcelWorkBook theExcelBook;

    /**
     * The Excel Sheet.
     */
    private final HSSFSheet theExcelSheet;

    /**
     * Is the sheet readOnly.
     */
    private final boolean isReadOnly;

    /**
     * Constructor for Excel Sheet.
     * @param pBook the WorkBook
     * @param pSheet the Excel sheet
     * @param pIndex the index of the sheet
     * @param pReadOnly is the sheet readOnly?
     */
    protected MetisExcelSheet(final MetisExcelWorkBook pBook,
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
    protected MetisDataFormatter getDataFormatter() {
        return theExcelBook.getDataFormatter();
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
    public MetisExcelRow getReadOnlyRowByIndex(final int pRowIndex) {
        HSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        return (myExcelRow == null)
                                    ? null
                                    : new MetisExcelRow(this, myExcelRow, pRowIndex, true);
    }

    @Override
    public MetisExcelRow getMutableRowByIndex(final int pRowIndex) {
        if (isReadOnly) {
            return null;
        }
        HSSFRow myExcelRow = theExcelSheet.getRow(pRowIndex);
        if (myExcelRow == null) {
            /* Create the row if it does not exist */
            myExcelRow = theExcelSheet.createRow(pRowIndex);
        }
        return new MetisExcelRow(this, myExcelRow, pRowIndex, false);
    }

    @Override
    public MetisExcelColumn getReadOnlyColumnByIndex(final int pColIndex) {
        return new MetisExcelColumn(this, pColIndex, true);
    }

    @Override
    public MetisExcelColumn getMutableColumnByIndex(final int pColIndex) {
        return isReadOnly
                          ? null
                          : new MetisExcelColumn(this, pColIndex, false);
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
                             final MetisCellPosition pFirstCell,
                             final MetisCellPosition pLastCell) throws OceanusException {
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
    public void applyDataValidation(final MetisCellPosition pFirstCell,
                                    final MetisCellPosition pLastCell,
                                    final String pName) throws OceanusException {
        if (!isReadOnly) {
            /* Create the CellAddressList */
            CellRangeAddressList myCells = new CellRangeAddressList(pFirstCell.getRowIndex(), pLastCell.getRowIndex(), pFirstCell.getColumnIndex(),
                    pLastCell.getColumnIndex());

            /* Declare to workBook */
            theExcelBook.applyDataValidation(theExcelSheet, myCells, pName);
        }
    }

    @Override
    public void applyDataFilter(final MetisCellPosition pBaseCell,
                                final int pNumRows) throws OceanusException {
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
    public void createFreezePane(final MetisCellPosition pFreezeCell) {
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
                                       final MetisCellStyleType pStyle) {
        theExcelSheet.setDefaultColumnStyle(pColIndex, theExcelBook.getCellStyle(pStyle));
        theExcelSheet.setColumnWidth(pColIndex, getColumnWidth(pStyle));
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setCellStyle(final MetisExcelCell pCell,
                                final Object pValue) {
        pCell.setCellStyle(theExcelBook.getCellStyle(pValue));
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setAlternateCellStyle(final MetisExcelCell pCell,
                                         final Object pValue) {
        pCell.setCellStyle(theExcelBook.getAlternateCellStyle(pValue));
    }

    /**
     * Obtain column cell width.
     * @param pStyle the style type
     * @return the name of the style
     */
    private static int getColumnWidth(final MetisCellStyleType pStyle) {
        switch (pStyle) {
            case INTEGER:
                return MetisDataWorkBook.WIDTH_INT
                       * WIDTH_CHAR;
            case BOOLEAN:
                return MetisDataWorkBook.WIDTH_BOOL
                       * WIDTH_CHAR;
            case DATE:
                return MetisDataWorkBook.WIDTH_DATE
                       * WIDTH_CHAR;
            case MONEY:
                return MetisDataWorkBook.WIDTH_MONEY
                       * WIDTH_CHAR;
            case PRICE:
                return MetisDataWorkBook.WIDTH_PRICE
                       * WIDTH_CHAR;
            case UNITS:
                return MetisDataWorkBook.WIDTH_UNITS
                       * WIDTH_CHAR;
            case RATE:
                return MetisDataWorkBook.WIDTH_RATE
                       * WIDTH_CHAR;
            case DILUTION:
                return MetisDataWorkBook.WIDTH_DILUTION
                       * WIDTH_CHAR;
            case RATIO:
                return MetisDataWorkBook.WIDTH_RATIO
                       * WIDTH_CHAR;
            case STRING:
            default:
                return MetisDataWorkBook.WIDTH_STRING
                       * WIDTH_CHAR;
        }
    }
}
