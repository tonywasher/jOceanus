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
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

import java.util.Arrays;
import java.util.Objects;

import org.w3c.dom.Element;

import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCellStyleType;

/**
 * Hold columns as a list of values.
 */
class PrometheusOdfColumnStore {
    /**
     * Column Expansion.
     */
    private static final int COL_EXPAND = 20;

    /**
     * Underlying sheet.
     */
    private final PrometheusOdfSheetCore theSheet;

    /**
     * The Parser.
     */
    private final PrometheusOdfParser theParser;

    /**
     * Array of hiddens.
     */
    private Boolean[] theHiddens;

    /**
     * Array of styles.
     */
    private PrometheusSheetCellStyleType[] theStyles;

    /**
     * Number of Columns.
     */
    private int theNumCols;

    /**
     * ReadOnly Constructor.
     * @param pSheet the owning sheet.
     */
    PrometheusOdfColumnStore(final PrometheusOdfSheetCore pSheet) {
        /* Store details */
        theSheet = pSheet;
        theParser = theSheet.getParser();
        theHiddens = new Boolean[COL_EXPAND];
        theStyles = new PrometheusSheetCellStyleType[COL_EXPAND];
    }

    /**
     * Mutable Constructor.
     * @param pSheet the owning sheet.
     * @param pNumCols the initial # of columns
     */
    PrometheusOdfColumnStore(final PrometheusOdfSheetCore pSheet,
                             final int pNumCols) {
        /* Store details */
        theSheet = pSheet;
        theParser = theSheet.getParser();
        theNumCols = pNumCols;
        theHiddens = new Boolean[theNumCols];
        theStyles = new PrometheusSheetCellStyleType[theNumCols];
    }

    /**
     * Obtain OasisSheet.
     * @return the row.
     */
    PrometheusOdfSheetCore getSheet() {
        return theSheet;
    }

    /**
     * Obtain Column count.
     * @return the column count.
     */
    int getColumnCount() {
        return theNumCols;
    }

    /**
     * Process a column Element.
     * @param pColumn the column to process
     */
    void processColumn(final Element pColumn) {
        /* Determine the number of repeated columns */
        final String myRepeatStr = theParser.getAttribute(pColumn, PrometheusOdfTableItem.COLUMNREPEAT);
        int myRepeat = myRepeatStr == null
                       ? 1
                       : Integer.parseInt(myRepeatStr);

        /* Parse the value */
        final Boolean myValue = parseColumnElement(pColumn);

        /* Add the additional columns */
        addAdditionalCols(myRepeat);

        /* If we have a value */
        if (myValue != null) {
            /* Loop through the cells in reverse order */
            for (int iIndex = theNumCols - 1;
                 myRepeat > 0; iIndex--, myRepeat--) {
                /* Set the value */
                theHiddens[iIndex] = myValue;
            }
        }
     }

    /**
     * Add additional columns to table.
     * @param pXtraCols the number of columns to add.
     */
    private void addAdditionalCols(final int pXtraCols) {
        /* Adjust the # of columns */
        theNumCols += pXtraCols;

        /* Determine the expansion length */
        final int myLen = (((theNumCols + 1) / COL_EXPAND) + 1)  * COL_EXPAND;
        theHiddens = Arrays.copyOf(theHiddens, myLen);
        theStyles = Arrays.copyOf(theStyles, myLen);
        theSheet.addAdditionalCols(pXtraCols);
    }

    /**
     * Obtain a readOnly column by its index.
     * @param pSheet the owning sheet
     * @param pColIndex the index of the column.
     * @return the column if it exists, else null
     */
    PrometheusOdfColumn getReadOnlyColumnByIndex(final PrometheusOdfSheet pSheet,
                                                 final int pColIndex) {
        /* Handle index out of range */
        if (pColIndex < 0 || pColIndex >= theNumCols) {
            return null;
        }

        /* Just return the column */
        return new PrometheusOdfColumn(this, pSheet, pColIndex, true);
    }

    /**
     * Obtain a mutable column by its index, creating column if it does not exist.
     * @param pSheet the owning sheet
     * @param pColIndex the index of the column.
     * @return the column
     */
    PrometheusOdfColumn getMutableColumnByIndex(final PrometheusOdfSheet pSheet,
                                                final int pColIndex) {
        /* Handle negative column index */
        if (pColIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pColIndex >= theNumCols) {
            /* Determine the number of extra columns required */
            final int myXtraCols = pColIndex
                    - theNumCols
                    + 1;

            /* Add additional columns */
            addAdditionalCols(myXtraCols);
        }

        /* Return the required column */
        return new PrometheusOdfColumn(this, pSheet, pColIndex, false);
    }

    /**
     * Get hidden flag at index.
     * @param pIndex the index
     * @return the value
     */
    boolean getHiddenAtIndex(final int pIndex) {
        return theHiddens[pIndex] != null;
    }

    /**
     * Set hidden flag at index.
     * @param pIndex the index
     * @param pHidden true/false
     */
    void setHiddenAtIndex(final int pIndex,
                          final boolean pHidden) {
       theHiddens[pIndex] = pHidden;
    }

    /**
     * Set style at index.
     * @param pIndex the index
     * @param pStyle the style
     */
    void setStyleAtIndex(final int pIndex,
                         final PrometheusSheetCellStyleType pStyle) {
        theStyles[pIndex] = pStyle;
    }

    /**
     * Populate Sheet children.
     * @param pTable the sheet element
     */
    void populateSheetChildren(final Element pTable) {
        /* Loop through the columns */
        int myRepeat;
        for (int iIndex = 0; iIndex < theNumCols; iIndex += myRepeat) {
            /* Create a new column element */
            final Element myColumn = theParser.newElement(PrometheusOdfTableItem.COLUMN);
            pTable.appendChild(myColumn);

            /* Populate it */
            populateColumn(myColumn, iIndex);

            /* Determine the repeat count */
            myRepeat = getRepeatCountForIndex(iIndex);
            if (myRepeat > 1) {
                /* Set attribute and adjust index */
                theParser.setAttribute(myColumn, PrometheusOdfTableItem.COLUMNREPEAT, myRepeat);
            }
        }
    }

    /**
     * Populate Column value.
     * @param pElement the element
     * @param pIndex the column index
     */
    private void populateColumn(final Element pElement,
                                final int pIndex) {
        /* Set hidden if required */
        if (theHiddens[pIndex] != null) {
            theParser.setAttribute(pElement, PrometheusOdfTableItem.VISIBILITY, PrometheusOdfValue.COLLAPSE);
        }
        final PrometheusSheetCellStyleType myStyle = theStyles[pIndex];
        if (myStyle != null) {
            theSheet.setColumnStyle(pElement, myStyle);
            theSheet.setDefaultCellStyle(pElement, myStyle);
        }
    }

    /**
     * Obtain repeat count for index.
     * @param pIndex the index to start at
     * @return the repeat count
     */
    private int getRepeatCountForIndex(final int pIndex) {
        /* Access the current values */
        final Boolean myHidden = theHiddens[pIndex];
        final PrometheusSheetCellStyleType myStyle = theStyles[pIndex];

        /* Loop through the remaining indices */
        for (int i = pIndex + 1; i < theNumCols; i++) {
            /* Access the next object */
            final Boolean nextHidden = theHiddens[i];
            final PrometheusSheetCellStyleType nextStyle = theStyles[i];

            /* Test for equality */
            if (!Objects.equals(myHidden, nextHidden)
                || !Objects.equals(myStyle, nextStyle)) {
                return i - pIndex;
            }
        }

        /* All remaining values are equal */
        return theNumCols - pIndex;
    }

    /**
     * parse Column element.
     * @param pElement the element
     * @return the Cell
      */
    private Boolean parseColumnElement(final Element pElement) {
        /* Determine whether the column is hidden */
        final String myHidden = theParser.getAttribute(pElement, PrometheusOdfTableItem.VISIBILITY);
        return PrometheusOdfValue.COLLAPSE.getValue().equals(myHidden)
                ? Boolean.TRUE
                : null;
    }
}
