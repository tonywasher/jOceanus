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
package net.sourceforge.joceanus.jmetis.service.sheet.odf;

import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellRange;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Class representing an Oasis sheet within a workBook.
 */
public class MetisOdfSheet
        extends MetisSheetSheet {
    /**
     * The Oasis WorkBook.
     */
    private final MetisOdfWorkBook theOasisBook;

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The Styler.
     */
    private final MetisOdfStyler theStyler;

    /**
     * The Sheet index.
     */
    private final int theIndex;

    /**
     * The Oasis Table.
     */
    private final Element theOasisTable;

    /**
     * The Row Map.
     */
    private MetisOdfRowMap theRowMap;

    /**
     * The Column Map.
     */
    private MetisOdfColumnMap theColMap;

    /**
     * Constructor for Oasis Sheet.
     * @param pBook the WorkBook
     * @param pTable the Oasis table
     * @param pIndex the index of the sheet
     * @param pReadOnly is the sheet readOnly?
     */
    MetisOdfSheet(final MetisOdfWorkBook pBook,
                  final Element pTable,
                  final int pIndex,
                  final boolean pReadOnly) {
        /* Construct super-class */
        super(pBook, pBook.getParser().getAttribute(pTable, MetisOdfTableItem.NAME), pReadOnly);

        /* Store parameters */
        theOasisBook = pBook;
        theParser = pBook.getParser();
        theStyler = pBook.getStyler();
        theIndex = pIndex;
        theOasisTable = pTable;

        /* Create the maps */
        theColMap = new MetisOdfColumnMap(this);
        theRowMap = new MetisOdfRowMap(this, theColMap.getColumnCount());
    }

    /**
     * Obtain parser.
     * @return the parser.
     */
    MetisOdfParser getParser() {
        return theParser;
    }

    /**
     * Obtain formatter.
     * @return the formatter.
     */
    TethysDataFormatter getFormatter() {
        return theOasisBook.getFormatter();
    }

    /**
     * Obtain the underlying table element.
     * @return the element
     */
    Element getTableElement() {
        return theOasisTable;
    }

    @Override
    public int getSheetIndex() {
        return theIndex;
    }

    @Override
    public int getRowCount() {
        return theRowMap.getRowCount();
    }

    @Override
    public MetisOdfRow getReadOnlyRowByIndex(final int pRowIndex) {
        /* Obtain row from row map */
        return theRowMap.getReadOnlyRowByIndex(pRowIndex);
    }

    @Override
    public MetisOdfRow getMutableRowByIndex(final int pRowIndex) {
        /* Obtain row from row map, creating row if necessary */
        return isReadOnly()
               ? null
               : theRowMap.getMutableRowByIndex(pRowIndex);
    }

    @Override
    public MetisOdfColumn getReadOnlyColumnByIndex(final int pColIndex) {
        /* Obtain column from column map */
        return theColMap.getReadOnlyColumnByIndex(pColIndex);
    }

    @Override
    public MetisOdfColumn getMutableColumnByIndex(final int pColIndex) {
        /* Obtain column from column map, creating column if necessary */
        return isReadOnly()
               ? null
               : theColMap.getMutableColumnByIndex(pColIndex);
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(final boolean isHidden) {
        if (!isReadOnly()) {
            theParser.setAttribute(theOasisTable, MetisOdfTableItem.STYLENAME,
                          isHidden
                              ? MetisOdfStyler.STYLE_HIDDENTABLE
                              : MetisOdfStyler.STYLE_TABLE);
        }
    }

    /**
     * Set the column style for the column.
     * @param pColumn the column
     * @param pStyle the style
     */
    void setColumnStyle(final Element pColumn,
                        final MetisSheetCellStyleType pStyle) {
        theParser.setAttribute(pColumn, MetisOdfTableItem.STYLENAME, MetisOdfStyler.getColumnStyleName(pStyle));
    }

    /**
     * Set the default style for the column.
     * @param pColumn the column index
     * @param pStyle the style
     */
    void setDefaultCellStyle(final Element pColumn,
                             final MetisSheetCellStyleType pStyle) {
        final String myStyle = theStyler.getCellStyle(pStyle);
        theParser.setAttribute(pColumn, MetisOdfTableItem.DEFAULTCELLSTYLE, myStyle);
    }

    /**
     * Set cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setCellStyle(final Element pCell,
                      final Object pValue) {
        final String myStyle = theStyler.getCellStyle(pValue);
        theParser.setAttribute(pCell, MetisOdfTableItem.STYLENAME, myStyle);
    }

    /**
     * Set alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setAlternateCellStyle(final Element pCell,
                               final Object pValue) {
        final String myStyle = theStyler.getAlternateCellStyle(pValue);
        theParser.setAttribute(pCell, MetisOdfTableItem.STYLENAME, myStyle);
    }

    @Override
    public void declareRange(final String pName,
                             final MetisSheetCellPosition pFirstCell,
                             final MetisSheetCellPosition pLastCell) throws OceanusException {
        if (!isReadOnly()) {
            /* Build the range */
            final MetisSheetCellRange myRange = new MetisSheetCellRange(getName(), pFirstCell, pLastCell);

            /* Declare to workBook */
            theOasisBook.declareRange(pName, myRange);
        }
    }

    @Override
    public void applyDataValidation(final MetisSheetCellPosition pFirstCell,
                                    final MetisSheetCellPosition pLastCell,
                                    final String pName) throws OceanusException {
        if (!isReadOnly()) {
            /* Declare to workBook */
            theOasisBook.applyDataValidation(this, pFirstCell, pLastCell, pName);
        }
    }

    @Override
    public void applyDataFilter(final MetisSheetCellPosition pBaseCell,
                                final int pNumRows) throws OceanusException {
        if (!isReadOnly()) {
            /* Build the range */
            final MetisSheetCellPosition myEnd = new MetisSheetCellPosition(pBaseCell.getColumnIndex(), pNumRows - 1);
            final MetisSheetCellRange myRange = new MetisSheetCellRange(getName(), pBaseCell, myEnd);

            /* Declare to workbook */
            theOasisBook.applyDataFilter(myRange);
        }
    }

    @Override
    public void createFreezePane(final MetisSheetCellPosition pFreezeCell) {
        /* NoOp */
    }

    /**
     * Add columns to rows.
     * @param pNumNewCols number of new columns to add
     */
    void addColumnsToRows(final int pNumNewCols) {
        /* pass call to rows */
        theRowMap.addColumnsToRows(pNumNewCols);
    }

    /**
     * Create a new TableTableColumnElement.
     * @return the new element
     */
    Element newColumnElement() {
        return theParser.newElement(MetisOdfTableItem.COLUMN);
    }

    /**
     * Create a new TableTableRowElement.
     * @param pNumCols the number of columns for the row
     * @return the new element
     */
    Element newRowElement(final int pNumCols) {
        /* Allocate the row */
        final Element myRow = theParser.newElement(MetisOdfTableItem.ROW);
        theParser.setAttribute(myRow, MetisOdfTableItem.STYLENAME, MetisOdfStyler.STYLE_ROW);

        /* Allocate a cell for the row */
        final Element myCell = theParser.newElement(MetisOdfTableItem.CELL);
        myRow.appendChild(myCell);

        /* Handle repeat count */
        if (pNumCols > 1) {
            theParser.setAttribute(myCell, MetisOdfTableItem.COLUMNREPEAT, pNumCols);
        }

        /* Return the row */
        return myRow;
    }

    /**
     * Create a new TableTableCellElement.
     * @return the new element
     */
    Element newCellElement() {
        return theParser.newElement(MetisOdfTableItem.CELL);
    }

    /**
     * Create a new TextPElement.
     * @return the new element
     */
    Element newTextPElement() {
        return theParser.newElement(MetisOdfTableItem.TEXT);
    }
}
