/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.service.sheet.odf;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellPosition;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellRange;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ListIterator;

/**
 * Sheet definition.
 */
class PrometheusOdfSheetCore {
    /**
     * The name of the sheet.
     */
    private final String theName;

    /**
     * The WorkBook.
     */
    private final PrometheusOdfWorkBook theBook;

    /**
     * The Table Store.
     */
    private final PrometheusOdfTableStore theStore;

    /**
     * The Parser.
     */
    private final PrometheusOdfParser theParser;

    /**
     * The Styler.
     */
    private final PrometheusOdfStyler theStyler;

    /**
     * The formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * The Columns for the sheet.
     */
    private final PrometheusOdfColumnStore theColumns;

    /**
     * The Rows for the sheet.
     */
    private final PrometheusOdfRowStore theRows;

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
     *
     * @param pStore   the table store
     * @param pIndex   the index
     * @param pElement the Sheet element.
     * @throws OceanusException on error
     */
    PrometheusOdfSheetCore(final PrometheusOdfTableStore pStore,
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
        theName = theParser.getAttribute(pElement, PrometheusOdfTableItem.NAME);

        /* Create the rows and the columns */
        theColumns = new PrometheusOdfColumnStore(this);
        theRows = new PrometheusOdfRowStore(this);

        /* Process the Sheet Node */
        processSheetNode(pElement);
    }

    /**
     * Mutable Constructor.
     *
     * @param pStore   the table store
     * @param pIndex   the index
     * @param pNumRows the initial number of rows
     * @param pNumCols the initial number of columns
     * @param pElement the Sheet element.
     */
    PrometheusOdfSheetCore(final PrometheusOdfTableStore pStore,
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
        theName = theParser.getAttribute(theElement, PrometheusOdfTableItem.NAME);

        /* Create the column and row stores */
        theColumns = new PrometheusOdfColumnStore(this, pNumCols);
        theRows = new PrometheusOdfRowStore(this, pNumRows, pNumCols);
    }

    /**
     * Obtain the name.
     *
     * @return the name
     */
    String getName() {
        return theName;
    }

    /**
     * Obtain the parser.
     *
     * @return the parser
     */
    PrometheusOdfParser getParser() {
        return theParser;
    }

    /**
     * Obtain the formatter.
     *
     * @return the formatter
     */
    OceanusDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the formatter.
     *
     * @return the formatter
     */
    int getRowCount() {
        return theRows.getRowCount();
    }

    /**
     * Process Sheet Node.
     *
     * @param pNode the node
     * @throws OceanusException on error
     */
    private void processSheetNode(final Node pNode) throws OceanusException {
        /* Loop through the children of the node */
        for (Node myNode = pNode.getFirstChild(); myNode != null; myNode = myNode.getNextSibling()) {
            /* If this is a row element */
            if (theParser.isElementOfType(myNode, PrometheusOdfTableItem.ROW)) {
                /* Add row to list */
                theRows.processRow((Element) myNode);

                /* else if this is a column element */
            } else if (theParser.isElementOfType(myNode, PrometheusOdfTableItem.COLUMN)) {
                /* Add column to list */
                theColumns.processColumn((Element) myNode);

                /* If this is a node that contains groups */
            } else if (theParser.isElementOfType(myNode, PrometheusOdfTableItem.ROWGROUP, PrometheusOdfTableItem.COLUMNGROUP,
                    PrometheusOdfTableItem.HDRROWS, PrometheusOdfTableItem.HDRCOLUMNS,
                    PrometheusOdfTableItem.ROWS, PrometheusOdfTableItem.COLUMNS)) {
                /* Process node */
                processSheetNode(myNode);
            }
        }
    }

    /**
     * Is the sheet hidden?
     *
     * @return true/false
     */
    boolean isHidden() {
        return isHidden;
    }

    /**
     * Set the sheet hidden status.
     *
     * @param pHidden true/false
     */
    void setHidden(final boolean pHidden) {
        isHidden = pHidden;
    }

    /**
     * Add additional columns to rows.
     *
     * @param pXtraCols the number of columns to add.
     */
    void addAdditionalCols(final int pXtraCols) {
        if (theRows != null) {
            theRows.addAdditionalCols(pXtraCols);
        }
    }

    /**
     * Obtain a readOnly sheet.
     *
     * @return the sheet
     */
    PrometheusOdfSheet getReadOnlySheet() {
        return new PrometheusOdfSheet(theBook, this, theIndex, true);
    }

    /**
     * Obtain a mutable sheet.
     *
     * @return the sheet
     */
    PrometheusOdfSheet getMutableSheet() {
        return new PrometheusOdfSheet(theBook, this, theIndex, false);
    }

    /**
     * Obtain a readOnly row by its index.
     *
     * @param pSheet    the owning sheet
     * @param pRowIndex the index of the row.
     * @return the row
     */
    PrometheusOdfRow getReadOnlyRowByIndex(final PrometheusOdfSheet pSheet,
                                           final int pRowIndex) {
        return theRows.getReadOnlyRowByIndex(pSheet, pRowIndex);
    }

    /**
     * Obtain an iterator of non-null rows for the range.
     *
     * @param pSheet    the sheet for the rows
     * @param pFirstRow the index of the first row.
     * @param pLastRow  the index of the last row.
     * @return the iterator
     */
    ListIterator<PrometheusSheetRow> iteratorForRange(final PrometheusOdfSheet pSheet,
                                                      final int pFirstRow,
                                                      final int pLastRow) {
        return theRows.iteratorForRange(pSheet, pFirstRow, pLastRow);
    }

    /**
     * Obtain a mutable row by its index, creating row if it does not exist.
     *
     * @param pSheet    the owning sheet
     * @param pRowIndex the index of the row.
     * @return the row
     */
    PrometheusOdfRow getMutableRowByIndex(final PrometheusOdfSheet pSheet,
                                          final int pRowIndex) {
        return theRows.getMutableRowByIndex(pSheet, pRowIndex);
    }

    /**
     * Obtain a readOnly column by its index.
     *
     * @param pSheet    the owning sheet
     * @param pColIndex the index of the row.
     * @return the column
     */
    PrometheusOdfColumn getReadOnlyColumnByIndex(final PrometheusOdfSheet pSheet,
                                                 final int pColIndex) {
        return theColumns.getReadOnlyColumnByIndex(pSheet, pColIndex);
    }

    /**
     * Obtain a mutable column by its index, creating column if it does not exist.
     *
     * @param pSheet    the owning sheet
     * @param pColIndex the index of the column.
     * @return the column
     */
    PrometheusOdfColumn getMutableColumnByIndex(final PrometheusOdfSheet pSheet,
                                                final int pColIndex) {
        return theColumns.getMutableColumnByIndex(pSheet, pColIndex);
    }

    /**
     * Set the column style for the column.
     *
     * @param pColumn the column
     * @param pStyle  the style
     */
    void setColumnStyle(final Element pColumn,
                        final PrometheusSheetCellStyleType pStyle) {
        theParser.setAttribute(pColumn, PrometheusOdfTableItem.STYLENAME, PrometheusOdfStyler.getColumnStyleName(pStyle));
    }

    /**
     * Set the default style for the column.
     *
     * @param pColumn the column index
     * @param pStyle  the style
     */
    void setDefaultCellStyle(final Element pColumn,
                             final PrometheusSheetCellStyleType pStyle) {
        final String myStyle = theStyler.getCellStyle(pStyle);
        theParser.setAttribute(pColumn, PrometheusOdfTableItem.DEFAULTCELLSTYLE, myStyle);
    }

    /**
     * Set cell style.
     *
     * @param pCell  the cell to style
     * @param pValue the cell value
     */
    void setCellStyle(final Element pCell,
                      final Object pValue) {
        final String myStyle = theStyler.getCellStyle(pValue);
        theParser.setAttribute(pCell, PrometheusOdfTableItem.STYLENAME, myStyle);
    }

    /**
     * Set alternate cell style.
     *
     * @param pCell  the cell to style
     * @param pValue the cell value
     */
    void setAlternateCellStyle(final Element pCell,
                               final Object pValue) {
        final String myStyle = theStyler.getAlternateCellStyle(pValue);
        theParser.setAttribute(pCell, PrometheusOdfTableItem.STYLENAME, myStyle);
    }

    /**
     * Declare the named range.
     *
     * @param pName  the name of the range
     * @param pRange the range to declare
     * @throws OceanusException on error
     */
    void declareRange(final String pName,
                      final PrometheusSheetCellRange pRange) throws OceanusException {
        theStore.declareRange(pName, pRange);
    }

    /**
     * Apply Data Validation.
     *
     * @param pFirstCell  the the first cell in the range
     * @param pLastCell   the last cell in the range
     * @param pValidRange the name of the validation range
     */
    void applyDataValidation(final PrometheusSheetCellPosition pFirstCell,
                             final PrometheusSheetCellPosition pLastCell,
                             final String pValidRange) {
        theStore.applyDataValidation(this, pFirstCell, pLastCell, pValidRange);
    }

    /**
     * Apply validation.
     *
     * @param pValidation the validation name
     * @param pFirstCell  the first cell
     * @param pLastCell   the last cell
     */
    void applyValidation(final String pValidation,
                         final PrometheusSheetCellPosition pFirstCell,
                         final PrometheusSheetCellPosition pLastCell) {
        theRows.applyValidation(pValidation, pFirstCell, pLastCell);
    }

    /**
     * Apply Data Filter.
     *
     * @param pRange the range
     */
    void applyDataFilter(final PrometheusSheetCellRange pRange) {
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
        theParser.setAttribute(theElement, PrometheusOdfTableItem.STYLENAME,
                isHidden
                        ? PrometheusOdfStyler.STYLE_HIDDENTABLE
                        : PrometheusOdfStyler.STYLE_TABLE);
    }
}
