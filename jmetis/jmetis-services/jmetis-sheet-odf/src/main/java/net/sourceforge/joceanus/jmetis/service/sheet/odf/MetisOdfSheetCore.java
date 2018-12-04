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

import java.util.ListIterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellRange;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellStyleType;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetRow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Sheet definition.
 */
class MetisOdfSheetCore {
    /**
     * The name of the sheet.
     */
    private final String theName;

    /**
     * The WorkBook.
     */
    private final MetisOdfWorkBook theBook;

    /**
     * The Table Store.
     */
    private final MetisOdfTableStore theStore;

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The Styler.
     */
    private final MetisOdfStyler theStyler;

    /**
     * The formatter.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * The Columns for the sheet.
     */
    private final MetisOdfColumnStore theColumns;

    /**
     * The Rows for the sheet.
     */
    private final MetisOdfRowStore theRows;

    /**
     * The sheet index.
     */
    private final int theIndex;

    /**
     * Is the sheet element.
     */
    private final Element theElement;

    /**
     * Is the sheet hidden?
     */
    private boolean isHidden;

    /**
     * ReadOnly Constructor.
     * @param pStore the table store
     * @param pIndex the index
     * @param pElement the Sheet element.
     * @throws OceanusException on error
     */
    MetisOdfSheetCore(final MetisOdfTableStore pStore,
                      final int pIndex,
                      final Element pElement) throws OceanusException {
        /* Store parameters */
        theStore = pStore;
        theBook = pStore.getWorkBook();
        theIndex = pIndex;
        theElement = pElement;

        /* Access the formatter, styler and parser */
        theParser = theBook.getParser();
        theStyler = theBook.getStyler();
        theFormatter = theBook.getFormatter();

        /* Access the name of the sheet */
        theName = theParser.getAttribute(pElement, MetisOdfTableItem.NAME);

        /* Create the rows and the columns */
        theColumns = new MetisOdfColumnStore(this);
        theRows = new MetisOdfRowStore(this);

        /* Process the Sheet Node */
        processSheetNode(pElement);
    }

    /**
     * Mutable Constructor.
     * @param pStore the table store
     * @param pIndex the index
     * @param pNumRows the initial number of rows
     * @param pNumCols the initial number of columns
     * @param pElement the Sheet element.
     */
    MetisOdfSheetCore(final MetisOdfTableStore pStore,
                      final int pIndex,
                      final int pNumRows,
                      final int pNumCols,
                      final Element pElement) {
        /* Store parameters */
        theStore = pStore;
        theBook = pStore.getWorkBook();
        theIndex = pIndex;
        theElement = pElement;

        /* Access the formatter, styler and parser */
        theParser = theBook.getParser();
        theStyler = theBook.getStyler();
        theFormatter = theBook.getFormatter();

        /* Access the name of the sheet */
        theName = theParser.getAttribute(theElement, MetisOdfTableItem.NAME);

        /* Create the column and row stores */
        theColumns = new MetisOdfColumnStore(this, pNumCols);
        theRows = new MetisOdfRowStore(this, pNumRows, pNumCols);
    }

    /**
     * Obtain the name.
     * @return the name
     */
    String getName() {
        return theName;
    }

    /**
     * Obtain the parser.
     * @return the parser
     */
    MetisOdfParser getParser() {
        return theParser;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    TethysDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    int getRowCount() {
        return theRows.getRowCount();
    }

    /**
     * Process Sheet Node.
     * @param pNode the node
     * @throws OceanusException on error
     */
    private void processSheetNode(final Node pNode) throws OceanusException {
        /* Loop through the children of the node */
        for (Node myNode = pNode.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a row element */
            if (theParser.isElementOfType(myNode, MetisOdfTableItem.ROW)) {
                /* Add row to list */
                theRows.processRow((Element) myNode);

                /* else if this is a column element */
            } else if (theParser.isElementOfType(myNode, MetisOdfTableItem.COLUMN)) {
               /* Add column to list */
               theColumns.processColumn((Element) myNode);

               /* If this is a node that contains groups */
            } else if (theParser.isElementOfType(myNode, MetisOdfTableItem.ROWGROUP, MetisOdfTableItem.COLUMNGROUP,
                    MetisOdfTableItem.HDRROWS, MetisOdfTableItem.HDRCOLUMNS,
                    MetisOdfTableItem.ROWS, MetisOdfTableItem.COLUMNS)) {
                /* Process node */
                processSheetNode(myNode);
            }
        }
    }

    /**
     * Is the sheet hidden?
     * @return true/false
     */
    boolean isHidden() {
        return isHidden;
    }

    /**
     * Set the sheet hidden status.
     * @param pHidden true/false
     */
    void setHidden(final boolean pHidden) {
        isHidden = pHidden;
    }

    /**
     * Add additional columns to rows.
     * @param pXtraCols the number of columns to add.
     */
    void addAdditionalCols(final int pXtraCols) {
        if (theRows != null) {
            theRows.addAdditionalCols(pXtraCols);
        }
    }

    /**
     * Obtain a readOnly sheet.
     * @return the sheet
     */
    MetisOdfSheet getReadOnlySheet() {
        return new MetisOdfSheet(theBook, this, theIndex, true);
    }

    /**
     * Obtain a mutable sheet.
     * @return the sheet
     */
    MetisOdfSheet getMutableSheet() {
        return new MetisOdfSheet(theBook, this, theIndex, false);
    }

    /**
     * Obtain a readOnly row by its index.
     * @param pSheet the owning sheet
     * @param pRowIndex the index of the row.
     * @return the row
     */
    MetisOdfRow getReadOnlyRowByIndex(final MetisOdfSheet pSheet,
                                      final int pRowIndex) {
        return theRows.getReadOnlyRowByIndex(pSheet, pRowIndex);
    }

    /**
     * Obtain an iterator of non-null rows for the range.
     * @param pSheet the sheet for the rows
     * @param pFirstRow the index of the first row.
     * @param pLastRow the index of the last row.
     * @return the iterator
     */
    ListIterator<MetisSheetRow> iteratorForRange(final MetisOdfSheet pSheet,
                                                 final int pFirstRow,
                                                 final int pLastRow) {
        return theRows.iteratorForRange(pSheet, pFirstRow, pLastRow);
    }

    /**
     * Obtain a mutable row by its index, creating row if it does not exist.
     * @param pSheet the owning sheet
     * @param pRowIndex the index of the row.
     * @return the row
     */
    MetisOdfRow getMutableRowByIndex(final MetisOdfSheet pSheet,
                                     final int pRowIndex) {
        return theRows.getMutableRowByIndex(pSheet, pRowIndex);
    }

    /**
     * Obtain a readOnly column by its index.
     * @param pSheet the owning sheet
     * @param pColIndex the index of the row.
     * @return the column
     */
    MetisOdfColumn getReadOnlyColumnByIndex(final MetisOdfSheet pSheet,
                                            final int pColIndex) {
        return theColumns.getReadOnlyColumnByIndex(pSheet, pColIndex);
    }

    /**
     * Obtain a mutable column by its index, creating column if it does not exist.
     * @param pSheet the owning sheet
     * @param pColIndex the index of the column.
     * @return the column
     */
    MetisOdfColumn getMutableColumnByIndex(final MetisOdfSheet pSheet,
                                           final int pColIndex) {
        return theColumns.getMutableColumnByIndex(pSheet, pColIndex);
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

    /**
     * Declare the named range.
     * @param pName the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    void declareRange(final String pName,
                      final MetisSheetCellRange pRange) throws OceanusException {
        theStore.declareRange(pName, pRange);
    }

    /**
     * Apply Data Validation.
     * @param pFirstCell the the first cell in the range
     * @param pLastCell the last cell in the range
     * @param pValidRange the name of the validation range
     */
    void applyDataValidation(final MetisSheetCellPosition pFirstCell,
                             final MetisSheetCellPosition pLastCell,
                             final String pValidRange) {
        theStore.applyDataValidation(this, pFirstCell, pLastCell, pValidRange);
    }

    /**
     * Apply validation.
     * @param pValidation the validation name
     * @param pFirstCell the first cell
     * @param pLastCell the last cell
     */
    void applyValidation(final String pValidation,
                         final MetisSheetCellPosition pFirstCell,
                         final MetisSheetCellPosition pLastCell) {
        theRows.applyValidation(pValidation, pFirstCell, pLastCell);
    }

    /**
     * Apply Data Filter.
     * @param pRange the range
     */
    void applyDataFilter(final MetisSheetCellRange pRange) {
        theStore.applyDataFilter(pRange);
    }

    /**
     * Process Sheet children.
     */
    void populateSheet() {
        /* Populate the columns and rows  */
        theColumns.populateSheetChildren(theElement);
        theRows.populateSheetChildren(theElement);

        /* Set the style */
        theParser.setAttribute(theElement, MetisOdfTableItem.STYLENAME,
                isHidden
                    ? MetisOdfStyler.STYLE_HIDDENTABLE
                    : MetisOdfStyler.STYLE_TABLE);
    }
}
