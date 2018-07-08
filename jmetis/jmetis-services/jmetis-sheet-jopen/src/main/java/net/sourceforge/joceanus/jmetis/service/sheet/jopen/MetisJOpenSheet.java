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
package net.sourceforge.joceanus.jmetis.service.sheet.jopen;

import org.jopendocument.dom.spreadsheet.Cell;
import org.jopendocument.dom.spreadsheet.MutableCell;
import org.jopendocument.dom.spreadsheet.Sheet;
import org.jopendocument.dom.spreadsheet.SpreadSheet;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * JOpenDocument Sheet.
 */
public class MetisJOpenSheet
        extends MetisSheetSheet {
    /**
     * The Sheet index.
     */
    private final int theIndex;

    /**
     * The JOpen WorkBook.
     */
    private final MetisJOpenWorkBook theJOpenBook;

    /**
     * The JOpen Sheet.
     */
    private final Sheet theJOpenSheet;

    /**
     * Constructor.
     * @param pBook the workBook
     * @param pSheet the underlying sheet
     * @param pIndex the index of the sheet
     */
    protected MetisJOpenSheet(final MetisJOpenWorkBook pBook,
                              final Sheet pSheet,
                              final int pIndex) {
        /* Construct super-class */
        super(pBook, pSheet.getName());

        /* Store parameters */
        theJOpenBook = pBook;
        theJOpenSheet = pSheet;
        theIndex = pIndex;
    }

    /**
     * Obtain the data formatter.
     * @return the formatter
     */
    protected TethysDataFormatter getDataFormatter() {
        return theJOpenBook.getDataFormatter();
    }

    @Override
    public int getSheetIndex() {
        return theIndex;
    }

    @Override
    public int getRowCount() {
        return theJOpenSheet.getRowCount();
    }

    /**
     * Obtain the column count.
     * @return the column count
     */
    protected int getColumnCount() {
        return theJOpenSheet.getColumnCount();
    }

    @Override
    public MetisJOpenRow getReadOnlyRowByIndex(final int pRowIndex) {
        /* Reject rows which do not exist */
        if (pRowIndex < 0 || pRowIndex >= getRowCount()) {
            return null;
        }

        /* Return the new row */
        return new MetisJOpenRow(this, pRowIndex, true);
    }

    @Override
    public MetisJOpenRow getMutableRowByIndex(final int pRowIndex) {
        /* Reject rows which do not exist */
        if (pRowIndex < 0) {
            return null;
        }

        /* Make sure that we have enough rows */
        if (pRowIndex >= getRowCount()) {
            theJOpenSheet.ensureRowCount(pRowIndex + 1);
        }

        /* Return the new row */
        return new MetisJOpenRow(this, pRowIndex, false);
    }

    @Override
    public MetisJOpenColumn getReadOnlyColumnByIndex(final int pColIndex) {
        /* Reject rows which do not exist */
        if (pColIndex < 0 || pColIndex >= getColumnCount()) {
            return null;
        }

        /* Return the new column */
        return new MetisJOpenColumn(this, pColIndex, true);
    }

    @Override
    public MetisJOpenColumn getMutableColumnByIndex(final int pColIndex) {
        /* Reject columns which do not exist */
        if (pColIndex < 0) {
            return null;
        }

        /* Make sure that we have enough columns */
        if (pColIndex >= getColumnCount()) {
            theJOpenSheet.ensureColumnCount(pColIndex + 1);
        }

        /* Return the new column */
        return new MetisJOpenColumn(this, pColIndex, false);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(final boolean isHidden) {
        /* NoOp */
    }

    /**
     * Obtain Cell at position.
     * @param pColumn the column index
     * @param pRow the row index
     * @return the cell
     */
    protected Cell<SpreadSheet> getCell(final int pColumn,
                                        final int pRow) {
        /* Check indexes */
        if (pColumn < 0 || pColumn >= getColumnCount()) {
            return null;
        }
        if (pRow < 0 || pRow >= getRowCount()) {
            return null;
        }

        /* Return the cell */
        return theJOpenSheet.getImmutableCellAt(pColumn, pRow);
    }

    /**
     * Obtain MutableCell at position.
     * @param pColumn the column index
     * @param pRow the row index
     * @return the cell
     */
    protected MutableCell<SpreadSheet> getMutableCell(final int pColumn,
                                                      final int pRow) {
        /* Check indexes */
        if (pRow < 0 || pRow >= getRowCount()) {
            return null;
        }
        if (pColumn < 0) {
            return null;
        }

        /* Make sure that we have enough columns */
        if (pColumn >= getColumnCount()) {
            theJOpenSheet.ensureColumnCount(pColumn + 1);
        }

        /* Return the cell */
        return theJOpenSheet.getCellAt(pColumn, pRow);
    }

    /**
     * Set the default style for the column.
     * @param pColIndex the column index
     * @param pStyle the style
     */
    protected void setDefaultCellStyle(final int pColIndex,
                                       final MetisSheetCellStyleType pStyle) {
        // final OdfStyle myStyle = theOasisBook.getCellStyle(pStyle);
        // pColumn.setTableDefaultCellStyleNameAttribute(myStyle.getStyleNameAttribute());
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setCellStyle(final MetisJOpenCell pCell,
                                final Object pValue) {
        // pCell.setCellStyle(theExcelBook.getCellStyle(pValue));
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    protected void setAlternateCellStyle(final MetisJOpenCell pCell,
                                         final Object pValue) {
        // pCell.setCellStyle(theExcelBook.getAlternateCellStyle(pValue));
    }

    @Override
    public void declareRange(final String pName,
                             final MetisSheetCellPosition pFirstCell,
                             final MetisSheetCellPosition pLastCell) throws OceanusException {
        /* TODO */
    }

    @Override
    public void applyDataValidation(final MetisSheetCellPosition pFirstCell,
                                    final MetisSheetCellPosition pLastCell,
                                    final String pName) throws OceanusException {
        /* NoOp */
    }

    @Override
    public void applyDataFilter(final MetisSheetCellPosition pBaseCell,
                                final int pNumRows) throws OceanusException {
        /* NoOp */
    }

    @Override
    public void createFreezePane(final MetisSheetCellPosition pFreezeCell) {
        /* NoOp */
    }
}
