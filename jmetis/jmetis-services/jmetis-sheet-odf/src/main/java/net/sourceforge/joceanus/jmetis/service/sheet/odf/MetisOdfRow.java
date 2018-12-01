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

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetRow;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Class representing a row in Oasis.
 * @author Tony Washer
 */
public class MetisOdfRow
        extends MetisSheetRow {
    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The map of rows.
     */
    private final MetisOdfRowMap theRowMap;

    /**
     * The map of cells.
     */
    private final MetisOdfCellMap theCellMap;

    /**
     * The underlying ODFDOM row.
     */
    private final Element theOasisRow;

    /**
     * Constructor.
     * @param pMap the row map
     * @param pRow the Oasis row
     * @param pIndex the index
     * @param pReadOnly is the row readOnly?
     */
    MetisOdfRow(final MetisOdfRowMap pMap,
                final Element pRow,
                final int pIndex,
                final boolean pReadOnly) {
        /* Store parameters */
        super(pMap.getSheet(), pIndex, pReadOnly);
        theParser = getSheet().getParser();
        theRowMap = pMap;
        theOasisRow = pRow;

        /* Create the cell map */
        theCellMap = new MetisOdfCellMap(this);
    }

    @Override
    public MetisOdfSheet getSheet() {
        return (MetisOdfSheet) super.getSheet();
    }

    @Override
    public MetisOdfRow getNextRow() {
        return theRowMap.getReadOnlyRowByIndex(getRowIndex() + 1);
    }

    @Override
    public MetisOdfRow getPreviousRow() {
        return theRowMap.getReadOnlyRowByIndex(getRowIndex() - 1);
    }

    /**
     * Obtain the row element.
     * @return the row element
     */
    Element getRowElement() {
        return theOasisRow;
    }

    /**
     * Obtain the row style name.
     * @return the row style name
     */
    String getRowStyle() {
        return theParser.getAttribute(theOasisRow, MetisOdfTableItem.STYLENAME);
    }

    @Override
    public boolean isHidden() {
        final String myString = theParser.getAttribute(theOasisRow, MetisOdfTableItem.VISIBILITY);
        return myString != null
                && !myString.equals(MetisOdfValue.VISIBLE.getValue());
    }

    /**
     * Set the row style.
     * @param pStyle the row style
     */
    void setRowStyle(final String pStyle) {
        /* Ignore if readOnly */
        if (!isReadOnly()) {
            /* Set the row style */
            theParser.setAttribute(theOasisRow, MetisOdfTableItem.STYLENAME, pStyle);
        }
    }

    @Override
    protected void setHiddenValue(final boolean isHidden) {
        /* Set the visibility attribute */
        theParser.setAttribute(theOasisRow, MetisOdfTableItem.VISIBILITY,
                isHidden
                      ? MetisOdfValue.COLLAPSE
                      : MetisOdfValue.VISIBLE);
    }

    @Override
    public int getCellCount() {
        return theCellMap.getCellCount();
    }

    @Override
    public MetisOdfCell getReadOnlyCellByIndex(final int pIndex) {
        return theCellMap.getReadOnlyCellByIndex(pIndex);
    }

    @Override
    protected MetisOdfCell getWriteableCellByIndex(final int pIndex) {
        return theCellMap.getMutableCellByIndex(pIndex);
    }

    /**
     * Add extra columns to row.
     * @param pNumNewCols the number of new columns to add
     */
    void addColumnsToRow(final int pNumNewCols) {
        /* Pass call to CellMap */
        theCellMap.addAdditionalCells(pNumNewCols);
    }

    /**
     * Format object value.
     * @param pValue the value
     * @return the formatted value
     */
    String formatValue(final Object pValue) {
        final TethysDataFormatter myFormatter = theRowMap.getFormatter();
        return myFormatter.formatObject(pValue);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     */
    <T> T parseValue(final String pSource,
                     final Class<T> pClass) {
        final TethysDataFormatter myFormatter = theRowMap.getFormatter();
        return myFormatter.parseValue(pSource, pClass);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pClass the value type class
     * @return the formatted value
     */
    <T> T parseValue(final Double pSource,
                     final Class<T> pClass) {
        final TethysDataFormatter myFormatter = theRowMap.getFormatter();
        return myFormatter.parseValue(pSource, pClass);
    }

    /**
     * Parse object value.
     * @param <T> the value type
     * @param pSource the source value
     * @param pCurrCode the currency code
     * @param pClass the value type class
     * @return the formatted value
     */
    <T> T parseValue(final Double pSource,
                     final String pCurrCode,
                     final Class<T> pClass) {
        final TethysDataFormatter myFormatter = theRowMap.getFormatter();
        return myFormatter.parseValue(pSource, pCurrCode, pClass);
    }

    /**
     * Ensure and determine the cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setCellStyle(final Element pCell,
                      final Object pValue) {
        /* Pass through to the sheet */
        getSheet().setCellStyle(pCell, pValue);
    }

    /**
     * Ensure and determine the alternate cell style.
     * @param pCell the cell to style
     * @param pValue the cell value
     */
    void setAlternateCellStyle(final Element pCell,
                               final Object pValue) {
        /* Pass through to the sheet */
        getSheet().setAlternateCellStyle(pCell, pValue);
    }
}
