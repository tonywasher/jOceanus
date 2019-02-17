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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import org.w3c.dom.Element;

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCell;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCellPosition;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetRow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Row store.
 */
class MetisOdfRowStore {
    /**
     * Row Expansion.
     */
    private static final int ROW_EXPAND = 100;

    /**
     * Underlying sheet.
     */
    private final MetisOdfSheetCore theSheet;

    /**
     * The Parser.
     */
    private final MetisOdfParser theParser;

    /**
     * The Formatter.
     */
    private final TethysDataFormatter theFormatter;

    /**
     * Array of hiddens.
     */
    private Boolean[] theHiddens;

    /**
     * Array of cellStores.
     */
    private MetisOdfCellStore[] theRows;

    /**
     * Number of Rows.
     */
    private int theNumRows;

    /**
     * Number of Columns.
     */
    private int theNumCols;

    /**
     * ReadOnly Constructor.
     * @param pSheet the owning sheet.
     * @throws OceanusException on error
     */
    MetisOdfRowStore(final MetisOdfSheetCore pSheet) throws OceanusException {
        /* Store details */
        theSheet = pSheet;
        theParser = theSheet.getParser();
        theFormatter = theSheet.getFormatter();
        theHiddens = new Boolean[ROW_EXPAND];
        theRows = new MetisOdfCellStore[ROW_EXPAND];
    }

    /**
     * Mutable Constructor.
     * @param pSheet the owning sheet.
     * @param pNumRows the initial number of rows
     * @param pNumCols the initial number of columns
      */
    MetisOdfRowStore(final MetisOdfSheetCore pSheet,
                     final int pNumRows,
                     final int pNumCols) {
        /* Store details */
        theSheet = pSheet;
        theParser = theSheet.getParser();
        theFormatter = theSheet.getFormatter();
        theNumRows = pNumRows;
        theNumCols = pNumCols;
        theHiddens = new Boolean[theNumRows];
        theRows = new MetisOdfCellStore[theNumRows];
    }

    /**
     * Obtain OasisSheet.
     * @return the row.
     */
    MetisOdfSheetCore getSheet() {
        return theSheet;
    }

    /**
     * Obtain Row count.
     * @return the row count.
     */
    int getRowCount() {
        return theNumRows;
    }

    /**
     * Obtain Cell count.
     * @return the cell count.
     */
    int getCellCount() {
        return theNumCols;
    }

    /**
     * Process a row Element.
     * @param pRow the row to process
     * @throws OceanusException on error
     */
    void processRow(final Element pRow) throws OceanusException {
        /* Determine the number of repeated columns */
        final String myRepeatStr = theParser.getAttribute(pRow, MetisOdfTableItem.ROWREPEAT);
        int myRepeat = myRepeatStr == null
                       ? 1
                       : Integer.parseInt(myRepeatStr);

        /* Parse the values */
        final Boolean myHidden = parseRowHidden(pRow);
        final MetisOdfCellStore myCells = parseRowCells(pRow, theNumRows);

        /* Add the additional rows */
        addAdditionalRows(myRepeat);

        /* Loop through the cells in reverse order */
        for (int iIndex = theNumRows - 1;
             myRepeat > 0; iIndex--, myRepeat--) {
            /* Set the values */
            theHiddens[iIndex] = myHidden;
            theRows[iIndex] = myCells;
        }
     }

    /**
     * Add additional rows to table.
     * @param pXtraRows the number of rows to add.
     */
    private void addAdditionalRows(final int pXtraRows) {
        /* Adjust the # of rows */
        theNumRows += pXtraRows;

        /* Determine the expansion length */
        final int myLen = (((theNumRows + 1) / ROW_EXPAND) + 1)  * ROW_EXPAND;
        theHiddens = Arrays.copyOf(theHiddens, myLen);
        theRows = Arrays.copyOf(theRows, myLen);
    }

    /**
     * Add additional columns to table.
     * @param pXtraCols the number of columns to add.
     */
    void addAdditionalCols(final int pXtraCols) {
        theNumCols += pXtraCols;
        for (MetisOdfCellStore myCells : theRows) {
            if (myCells != null) {
                myCells.addAdditionalCells(pXtraCols);
            }
        }
    }

    /**
     * Obtain a readOnly row by its index.
     * @param pSheet the owning sheet
     * @param pRowIndex the index of the row.
     * @return the row
     */
    MetisOdfRow getReadOnlyRowByIndex(final MetisOdfSheet pSheet,
                                      final int pRowIndex) {
        /* Handle index out of range */
        if (pRowIndex < 0 || pRowIndex >= theNumRows) {
            return null;
        }

        /* If we have no cells, just return null */
        final MetisOdfCellStore myCells = theRows[pRowIndex];
        if (myCells == null) {
            return null;
        }

        /* Just return the row */
        return new MetisOdfRow(this, pSheet, pRowIndex, true);
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
        /* Determine upper bound for search */
        final int myBound = Math.min(pLastRow, theNumRows);

        /* Create a list of cells */
        final List<MetisSheetRow> myList = new ArrayList<>();
        for (int iIndex = pFirstRow; iIndex <= myBound; iIndex++) {
            /* Only return a row if a value is present */
            final MetisOdfCellStore myCells = theRows[iIndex];
            if (myCells != null) {
                myList.add(new MetisOdfRow(this, pSheet, iIndex, true));
            }
        }

        /* Return the iterator */
        return myList.listIterator();
    }

    /**
     * Obtain an iterator of non-null cells for the range.
     * @param pRow the row for the cell
     * @param pFirstIndex the index of the first cell.
     * @param pLastIndex the index of the last cell.
     * @return the iterator
     */
    ListIterator<MetisSheetCell> iteratorForRange(final MetisOdfRow pRow,
                                                  final int pFirstIndex,
                                                  final int pLastIndex) {
        /* Access cells */
        final MetisOdfCellStore myCells = theRows[pRow.getRowIndex()];
        return myCells.iteratorForRange(pRow, pFirstIndex, pLastIndex);
    }

    /**
     * Obtain a mutable row by its index, creating row if it does not exist.
     * @param pSheet the owning sheet
     * @param pRowIndex the index of the row.
     * @return the row
     */
    MetisOdfRow getMutableRowByIndex(final MetisOdfSheet pSheet,
                                     final int pRowIndex) {
        /* Handle negative row index */
        if (pRowIndex < 0) {
            return null;
        }

        /* If we need to extend the table */
        if (pRowIndex >= theNumRows) {
            /* Determine the number of extra rows required */
            final int myXtraRows = pRowIndex
                    - theNumRows
                    + 1;

            /* Add additional rows */
            addAdditionalRows(myXtraRows);
        }

        /* Ensure that cells are created for the row */
        final MetisOdfCellStore myCells = theRows[pRowIndex];
        if (myCells == null) {
            theRows[pRowIndex] = new MetisOdfCellStore(this, pRowIndex, theNumCols);
        }

        /* Return the required row */
        return new MetisOdfRow(this, pSheet, pRowIndex, false);
    }

    /**
     * Obtain a readOnly cell by its index.
     * @param pRow the row containing the cell.
     * @param pCellIndex the index of the cell.
     * @return the row
     */
    MetisOdfCell getReadOnlyCellByIndex(final MetisOdfRow pRow,
                                        final int pCellIndex) {
        /* Handle index out of range */
        final MetisOdfCellStore myCells = theRows[pRow.getRowIndex()];

        /* Just return the cell */
        return myCells.getReadOnlyCellByIndex(pRow, pCellIndex);
    }

    /**
     * Obtain a mutable cell by its index.
     * @param pRow the row containing the cell.
     * @param pCellIndex the index of the cell.
     * @return the row
     */
    MetisOdfCell getMutableCellByIndex(final MetisOdfRow pRow,
                                       final int pCellIndex) {
        /* Access cells */
        final MetisOdfCellStore myCells = theRows[pRow.getRowIndex()];

        /* Just return the cell */
        return myCells.getMutableCellByIndex(pRow, pCellIndex);
    }

    /**
     * Obtain the index of the max valued cell.
     * @param pRow the row containing the cell.
     * @return the index
     */
    int getMaxValuedCellForRow(final MetisOdfRow pRow) {
        /* Access cells */
        final MetisOdfCellStore myCells = theRows[pRow.getRowIndex()];

        /* Just return the cell */
        return myCells.getMaxValuedIndex();
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
        /* Access the details */
        final int iFirstRow = pFirstCell.getRowIndex();
        final int iLastRow = pLastCell.getRowIndex();
        final int iFirstCol = pFirstCell.getColumnIndex();
        final int iLastCol = pLastCell.getColumnIndex();

        /* Loop through the cells in reverse order */
        for (int iIndex = iLastRow;
             iIndex >= iFirstRow; iIndex--) {
            /* Ensure that cells are created for the row */
            MetisOdfCellStore myCells = theRows[iIndex];
            if (myCells == null) {
                myCells = new MetisOdfCellStore(this, iIndex, theNumCols);
                theRows[iIndex] = myCells;
            }

            /* Set the validation */
            myCells.applyValidation(pValidation, iFirstCol, iLastCol);
        }
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
     * Format object value.
     * @param pValue the value
     * @return the formatted value
     */
    String formatValue(final Object pValue) {
         return theFormatter.formatObject(pValue);
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
        return theFormatter.parseValue(pSource, pClass);
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
         return theFormatter.parseValue(pSource, pCurrCode, pClass);
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

    /**
     * Process Sheet children.
     * @param pTable the table element
     */
    void populateSheetChildren(final Element pTable) {
        /* Loop through the rows */
        int myRepeat;
        for (int iIndex = 0; iIndex < theNumRows; iIndex += myRepeat) {
            /* Create a new row element */
            final Element myRow = theParser.newElement(MetisOdfTableItem.ROW);
            pTable.appendChild(myRow);

            /* Populate it */
            populateRow(myRow, iIndex);

            /* Determine the repeat count */
            myRepeat = getRepeatCountForIndex(iIndex);
            if (myRepeat > 1) {
                /* Set attribute and adjust index */
                theParser.setAttribute(myRow, MetisOdfTableItem.ROWREPEAT, myRepeat);
            }
        }
    }

    /**
     * Populate Row value.
     * @param pElement the element
     * @param pIndex the column index
     */
    private void populateRow(final Element pElement,
                             final int pIndex) {
        /* Set attributes */
        theParser.setAttribute(pElement, MetisOdfTableItem.STYLENAME, MetisOdfStyler.STYLE_ROW);
        if (theHiddens[pIndex] != null) {
            theParser.setAttribute(pElement, MetisOdfTableItem.VISIBILITY, MetisOdfValue.COLLAPSE);
        }

        /* Populate cells */
        final MetisOdfCellStore myCells = theRows[pIndex];
        if (myCells != null) {
            myCells.populateRowChildren(pElement);
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
        final MetisOdfCellStore myCells = theRows[pIndex];

        /* Loop through the remaining indices */
        for (int i = pIndex + 1; i < theNumRows; i++) {
            /* Access the next object */
            final Boolean nextHidden = theHiddens[i];
            final MetisOdfCellStore nextCells = theRows[i];

            /* Test for equality */
            if (!Objects.equals(myHidden, nextHidden)
                    || !Objects.equals(myCells, nextCells)) {
                return i - pIndex;
            }
        }

        /* All remaining values are equal */
        return theNumRows - pIndex;
    }

    /**
     * parse Row hidden value.
     * @param pElement the element
     * @return the hidden indication
     */
    private Boolean parseRowHidden(final Element pElement) {
        /* Determine whether the column is hidden */
        final String myHidden = theParser.getAttribute(pElement, MetisOdfTableItem.VISIBILITY);
        return MetisOdfValue.COLLAPSE.getValue().equals(myHidden)
               ? Boolean.TRUE
               : null;
    }

    /**
     * parse Row cells.
     * @param pElement the element
     * @param pIndex the index
     * @return the Cells
     * @throws OceanusException on error
     */
    private MetisOdfCellStore parseRowCells(final Element pElement,
                                            final int pIndex) throws OceanusException {
        return new MetisOdfCellStore(this, pIndex, pElement);
    }
}
