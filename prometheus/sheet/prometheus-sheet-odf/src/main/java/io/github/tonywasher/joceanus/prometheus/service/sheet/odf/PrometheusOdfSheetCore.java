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
package io.github.tonywasher.joceanus.prometheus.service.sheet.odf;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellPosition;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellRange;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetCellStyleType;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import io.github.tonywasher.joceanus.prometheus.service.sheet.odf.PrometheusOdf.PrometheusOdfSheetCoreCtl;
import io.github.tonywasher.joceanus.prometheus.service.sheet.odf.PrometheusOdf.PrometheusOdfSheetCtl;
import io.github.tonywasher.joceanus.prometheus.service.sheet.odf.PrometheusOdf.PrometheusOdfTableStoreCtl;
import io.github.tonywasher.joceanus.prometheus.service.sheet.odf.PrometheusOdf.PrometheusOdfWorkBookCtl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ListIterator;

/**
 * Sheet definition.
 */
public class PrometheusOdfSheetCore
        implements PrometheusOdfSheetCoreCtl {
    /**
     * The name of the sheet.
     */
    private final String theName;

    /**
     * The WorkBook.
     */
    private final PrometheusOdfWorkBookCtl theBook;

    /**
     * The Table Store.
     */
    private final PrometheusOdfTableStoreCtl theStore;

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
    PrometheusOdfSheetCore(final PrometheusOdfTableStoreCtl pStore,
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
    PrometheusOdfSheetCore(final PrometheusOdfTableStoreCtl pStore,
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

    @Override
    public String getName() {
        return theName;
    }

    @Override
    public PrometheusOdfParser getParser() {
        return theParser;
    }

    @Override
    public OceanusDataFormatter getFormatter() {
        return theFormatter;
    }

    @Override
    public int getRowCount() {
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

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public void setHidden(final boolean pHidden) {
        isHidden = pHidden;
    }

    @Override
    public void addAdditionalCols(final int pXtraCols) {
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

    @Override
    public ListIterator<PrometheusSheetRow> iteratorForRange(final PrometheusOdfSheetCtl pSheet,
                                                             final int pFirstRow,
                                                             final int pLastRow) {
        return theRows.iteratorForRange(pSheet, pFirstRow, pLastRow);
    }

    @Override
    public PrometheusOdfRow getReadOnlyRowByIndex(final PrometheusOdfSheetCtl pSheet,
                                                  final int pRowIndex) {
        return theRows.getReadOnlyRowByIndex(pSheet, pRowIndex);
    }

    @Override
    public PrometheusOdfRow getMutableRowByIndex(final PrometheusOdfSheetCtl pSheet,
                                                 final int pRowIndex) {
        return theRows.getMutableRowByIndex(pSheet, pRowIndex);
    }

    @Override
    public PrometheusOdfColumn getReadOnlyColumnByIndex(final PrometheusOdfSheetCtl pSheet,
                                                        final int pColIndex) {
        return theColumns.getReadOnlyColumnByIndex(pSheet, pColIndex);
    }

    @Override
    public PrometheusOdfColumn getMutableColumnByIndex(final PrometheusOdfSheetCtl pSheet,
                                                       final int pColIndex) {
        return theColumns.getMutableColumnByIndex(pSheet, pColIndex);
    }

    @Override
    public void setColumnStyle(final Element pColumn,
                               final PrometheusSheetCellStyleType pStyle) {
        theParser.setAttribute(pColumn, PrometheusOdfTableItem.STYLENAME, PrometheusOdfStyler.getColumnStyleName(pStyle));
    }

    @Override
    public void setDefaultCellStyle(final Element pColumn,
                                    final PrometheusSheetCellStyleType pStyle) {
        final String myStyle = theStyler.getCellStyle(pStyle);
        theParser.setAttribute(pColumn, PrometheusOdfTableItem.DEFAULTCELLSTYLE, myStyle);
    }

    @Override
    public void setCellStyle(final Element pCell,
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
    public void setAlternateCellStyle(final Element pCell,
                                      final Object pValue) {
        final String myStyle = theStyler.getAlternateCellStyle(pValue);
        theParser.setAttribute(pCell, PrometheusOdfTableItem.STYLENAME, myStyle);
    }

    @Override
    public void declareRange(final String pName,
                             final PrometheusSheetCellRange pRange) throws OceanusException {
        theStore.declareRange(pName, pRange);
    }

    @Override
    public void applyDataValidation(final PrometheusSheetCellPosition pFirstCell,
                                    final PrometheusSheetCellPosition pLastCell,
                                    final String pValidRange) {
        theStore.applyDataValidation(this, pFirstCell, pLastCell, pValidRange);
    }

    @Override
    public void applyValidation(final String pValidation,
                                final PrometheusSheetCellPosition pFirstCell,
                                final PrometheusSheetCellPosition pLastCell) {
        theRows.applyValidation(pValidation, pFirstCell, pLastCell);
    }

    @Override
    public void applyDataFilter(final PrometheusSheetCellRange pRange) {
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
