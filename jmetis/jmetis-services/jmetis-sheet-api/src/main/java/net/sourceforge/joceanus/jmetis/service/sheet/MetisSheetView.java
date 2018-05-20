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
package net.sourceforge.joceanus.jmetis.service.sheet;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Represents a view of a range of cells.
 */
public class MetisSheetView
        implements Iterable<MetisSheetRow> {
    /**
     * Underlying Sheet.
     */
    private final MetisSheetSheet theSheet;

    /**
     * Base Cell Position.
     */
    private final MetisSheetCellPosition theBaseCell;

    /**
     * Number of rows in view.
     */
    private final int theNumRows;

    /**
     * Number of columns in view.
     */
    private final int theNumColumns;

    /**
     * Constructor.
     * @param pSheet the sheet containing the view
     * @param pFirstCell the first cell of the view
     * @param pLastCell the last cell of the view
     */
    public MetisSheetView(final MetisSheetSheet pSheet,
                          final MetisSheetCellPosition pFirstCell,
                          final MetisSheetCellPosition pLastCell) {
        /* Store parameters */
        theSheet = pSheet;
        theBaseCell = pFirstCell;
        theNumRows = pLastCell.getRowIndex()
                     - pFirstCell.getRowIndex()
                     + 1;
        theNumColumns = pLastCell.getColumnIndex()
                        - pFirstCell.getColumnIndex()
                        + 1;
    }

    /**
     * Constructor.
     * @param pFirstCell the first cell of the view
     * @param pLastCell the last cell of the view
     */
    protected MetisSheetView(final MetisSheetCell pFirstCell,
                             final MetisSheetCell pLastCell) {
        /* Store parameters */
        this(pFirstCell.getSheet(), pFirstCell.getPosition(), pLastCell.getPosition());
    }

    /**
     * Obtain the underlying sheet.
     * @return the underlying sheet
     */
    public MetisSheetSheet getSheet() {
        return theSheet;
    }

    /**
     * Obtain the top left position.
     * @return the top left position
     */
    public MetisSheetCellPosition getBaseCell() {
        return theBaseCell;
    }

    /**
     * Determine number of rows in this view.
     * @return the number of rows.
     */
    public int getRowCount() {
        return theNumRows;
    }

    /**
     * Determine number of columns in this view.
     * @return the number of columns.
     */
    public int getColumnCount() {
        return theNumColumns;
    }

    /**
     * Convert Row index.
     * @param pRowIndex the view index
     * @return the sheet index or -1 if outside view
     */
    protected int convertRowIndex(final int pRowIndex) {
        /* Reject values outside range */
        if (pRowIndex < 0
            || pRowIndex >= theNumRows) {
            return -1;
        }

        /* Return adjusted index */
        return pRowIndex
               + theBaseCell.getRowIndex();
    }

    /**
     * Convert Column index.
     * @param pColIndex the view index
     * @return the sheet index or -1 if outside view
     */
    private int convertColumnIndex(final int pColIndex) {
        /* Reject values outside range */
        if (pColIndex < 0
            || pColIndex >= theNumColumns) {
            return -1;
        }

        /* Return adjusted index */
        return pColIndex
               + theBaseCell.getColumnIndex();
    }

    /**
     * Obtain the row at required index.
     * @param pRowIndex the requested row index
     * @return the requested row.
     */
    public MetisSheetRow getRowByIndex(final int pRowIndex) {
        /* Return the row */
        final int myIndex = convertRowIndex(pRowIndex);
        return myIndex < 0
                           ? null
                           : theSheet.getReadOnlyRowByIndex(myIndex);
    }

    /**
     * Obtain the cell at required position.
     * @param pColumnIndex the requested column index
     * @param pRowIndex the requested row index
     * @return the requested cell.
     */
    public MetisSheetCell getCellByPosition(final int pColumnIndex,
                                            final int pRowIndex) {
        /* Return the cell */
        final MetisSheetCellPosition myPos = new MetisSheetCellPosition(pColumnIndex, pRowIndex);
        return getCellByPosition(myPos);
    }

    /**
     * Obtain the cell at required position.
     * @param pPosition the requested position
     * @return the requested cell.
     */
    public MetisSheetCell getCellByPosition(final MetisSheetCellPosition pPosition) {
        /* Return the cell */
        final MetisSheetRow myRow = getRowByIndex(pPosition.getRowIndex());
        return myRow == null
                             ? null
                             : getRowCellByIndex(myRow, pPosition.getColumnIndex());
    }

    /**
     * Obtain the cell at required index.
     * @param pRow the row to extract from
     * @param pIndex the requested index
     * @return the requested cell.
     */
    public MetisSheetCell getRowCellByIndex(final MetisSheetRow pRow,
                                            final int pIndex) {
        /* Return the cell */
        final int myIndex = convertColumnIndex(pIndex);
        return myIndex < 0
                           ? null
                           : pRow.getReadOnlyCellByIndex(myIndex);
    }

    @Override
    public Iterator<MetisSheetRow> iterator() {
        return new RowIterator(this);
    }

    @Override
    public void forEach(final Consumer<? super MetisSheetRow> pAction) {
        final Iterator<MetisSheetRow> myIterator = iterator();
        while (myIterator.hasNext()) {
            pAction.accept(myIterator.next());
        }
    }

    @Override
    public Spliterator<MetisSheetRow> spliterator() {
        return new RowSpliterator(this);
    }

    /**
     * Iterator class for rows.
     */
    private static class RowIterator
            implements Iterator<MetisSheetRow> {
        /**
         * The data view.
         */
        private final MetisSheetView theView;

        /**
         * The base row.
         */
        private final int theBaseRow;

        /**
         * The last row.
         */
        private MetisSheetRow theLastRow;

        /**
         * Constructor.
         * @param pView the underlying view.
         */
        protected RowIterator(final MetisSheetView pView) {
            theLastRow = null;
            theView = pView;
            theBaseRow = theView.getBaseCell().getRowIndex();
        }

        @Override
        public boolean hasNext() {
            /* Calculate the next index */
            int iIndex = theLastRow != null
                                            ? theLastRow.getRowIndex() + 1
                                            : theBaseRow;

            /* Check that the row is within the view */
            iIndex -= theBaseRow;
            return iIndex >= 0
                   && iIndex < theView.getRowCount();
        }

        @Override
        public MetisSheetRow next() {
            /* Check validity */
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            /* If we are a new iterator */
            if (theLastRow == null) {
                /* Access the first element of the view */
                theLastRow = theView.getRowByIndex(0);
            } else {
                /* Return the next row */
                theLastRow = theLastRow.getNextRow();
            }

            /* Return the next row */
            return theLastRow;
        }

        @Override
        public void remove() {
            /* Throw exception */
            throw new UnsupportedOperationException();
        }

        @Override
        public void forEachRemaining(final Consumer<? super MetisSheetRow> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }
    }

    /**
     * Spliterator class for rows.
     */
    private static class RowSpliterator
            implements Spliterator<MetisSheetRow> {
        /**
         * The minimum split size.
         */
        private static final int MIN_SPLIT = 8;

        /**
         * The data view.
         */
        private final MetisSheetView theView;

        /**
         * The last row.
         */
        private MetisSheetRow theLastRow;

        /**
         * The current index.
         */
        private int theCurrIndex;

        /**
         * The number of rows.
         */
        private int theLastIndex;

        /**
         * Constructor.
         * @param pView the underlying view.
         */
        protected RowSpliterator(final MetisSheetView pView) {
            theLastRow = null;
            theView = pView;
            theCurrIndex = 0;
            theLastIndex = theView.getRowCount();
        }

        /**
         * Constructor.
         * @param pBase the base spliterator.
         * @param pSplit the split point
         */
        private RowSpliterator(final RowSpliterator pBase,
                               final int pSplit) {
            theView = pBase.theView;
            theCurrIndex = pBase.theCurrIndex + pSplit;
            theLastIndex = pBase.theLastIndex;
        }

        /**
         * Is there a next row in this spliterator?
         * @return true/false
         */
        private boolean hasNext() {
            /* Check that the row is within the view */
            return theCurrIndex < theLastIndex;
        }

        /**
         * Obtain the next element in this spliterator.
         * @return the next element
         */
        private MetisSheetRow next() {
            /* If we are a new iterator */
            if (theLastRow == null) {
                /* Access the first element of the view */
                theLastRow = theView.getRowByIndex(theCurrIndex);
            } else {
                /* Return the next row */
                theLastRow = theLastRow.getNextRow();
            }

            /* Increment the index */
            theCurrIndex++;

            /* Return the next row */
            return theLastRow;
        }

        @Override
        public Spliterator<MetisSheetRow> trySplit() {
            /* Check the size and don't split if too small */
            final long myRemaining = estimateSize();
            if (myRemaining < MIN_SPLIT) {
                return null;
            }

            /* Determine the split point */
            final int mySplit = (int) (myRemaining >> 1);

            /* Create the spliterator */
            final RowSpliterator mySpliterator = new RowSpliterator(this, mySplit);

            /* Adjust self */
            theLastIndex = theCurrIndex + mySplit;

            /* Return */
            return mySpliterator;
        }

        @Override
        public boolean tryAdvance(final Consumer<? super MetisSheetRow> pAction) {
            if (hasNext()) {
                pAction.accept(next());
                return true;
            }
            return false;
        }

        @Override
        public void forEachRemaining(final Consumer<? super MetisSheetRow> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }

        @Override
        public long estimateSize() {
            return (long) theLastIndex - theCurrIndex;
        }

        @Override
        public long getExactSizeIfKnown() {
            return estimateSize();
        }

        @Override
        public int characteristics() {
            return Spliterator.NONNULL + Spliterator.SIZED + Spliterator.SUBSIZED;
        }

        @Override
        public boolean hasCharacteristics(final int pCharacteristics) {
            return (pCharacteristics & characteristics()) == pCharacteristics;
        }

        @Override
        public Comparator<? super MetisSheetRow> getComparator() {
            throw new IllegalStateException();
        }
    }
}