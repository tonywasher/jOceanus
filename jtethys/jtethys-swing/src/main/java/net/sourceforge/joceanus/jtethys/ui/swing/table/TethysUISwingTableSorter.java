/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.swing.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.swing.RowSorter;
import javax.swing.table.TableModel;

import net.sourceforge.joceanus.jtethys.ui.swing.table.TethysUISwingTableSorter.TethysUISwingTableSorterModel;

/**
 * RowSorter to provide filtering capabilities with natural sorting.
 * @param <T> row data type
 */
public class TethysUISwingTableSorter<T>
        extends RowSorter<TethysUISwingTableSorterModel<T>> {
    /**
     * Invalid Range error text.
     */
    private static final String ERROR_RANGE = "Invalid Range";

    /**
     * Invalid Index error text.
     */
    private static final String ERROR_INDEX = "Invalid Index";

    /**
     * Filtered index indicator.
     */
    static final int ROW_FILTERED = -1;

    /**
     * The model for the Filter.
     */
    private final TethysUISwingTableSorterModel<T> theModel;

    /**
     * Empty SortKey list.
     */
    private final List<SortKey> theSortList;

    /**
     * The comparator.
     */
    private Comparator<T> theComparator;

    /**
     * The filter.
     */
    private Predicate<T> theFilter;

    /**
     * Mapping from View to Model.
     */
    private int[] theViewMap;

    /**
     * The old mapping from View to Model.
     */
    private int[] theOldViewMap;

    /**
     * Mapping from Sorted to Model.
     */
    private int[] theSortedMap;

    /**
     * DataControl.
     */
    private TableRowEntry<T>[] theDataControl;

    /**
     * Interface for Model.
     * @param <T> the row type
     */
    public interface TethysUISwingTableSorterModel<T>
            extends TableModel {
        /**
         * Obtain the item at the model index.
         * @param pRowIndex the index
         * @return the item
         */
        T getItemAtIndex(int pRowIndex);
    }

    /**
     * Constructor.
     * @param pModel the model
     */
    public TethysUISwingTableSorter(final TethysUISwingTableSorterModel<T> pModel) {
        theModel = pModel;
        allRowsChanged();
        theSortList = new ArrayList<>();
    }

    /**
     * Set Filter.
     * @param pFilter the filter
     */
    public void setFilter(final Predicate<T> pFilter) {
        /* Record the filter */
        theFilter = pFilter;
        newCriteria();
    }

    /**
     * Set Comparator.
     * @param pComparator the comparator
     */
    public void setComparator(final Comparator<T> pComparator) {
        /* Record the comparator */
        theComparator = pComparator;
        newCriteria();
    }

    /**
     * Report mapping changed.
     * <p>
     * This should be called immediately after
     * {@link javax.swing.table.AbstractTableModel#fireTableCellUpdated}, to complete the call.
     * Ideally this would be called as part of fireTableRowsUpdated, but the call to
     * {@link RowSorter#fireRowSorterChanged} is ignored when called from
     * {@link #rowsUpdated(int, int, int)}, probably due to the fact that the column is not
     * contained in the sort keys
     */
    public void reportMappingChanged() {
        /* If we have an old viewMap */
        if (theOldViewMap != null) {
            /* Report the mapping change */
            fireRowSorterChanged(theOldViewMap);
            theOldViewMap = null;
        }
    }

    /**
     * Adjust for new criteria.
     */
    private void newCriteria() {
        /* Obtain a copy of the old view map */
        final int[] myOldViewMap = Arrays.copyOf(theViewMap, theViewMap.length);

        /* Rebuild the mappings */
        allRowsChanged();

        /* Report the mapping change */
        fireRowSorterChanged(myOldViewMap);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void allRowsChanged() {
        /* Initialise counters */
        final int iNumRows = theModel.getRowCount();
        int iView = 0;

        /* Allocate the data maps */
        theViewMap = new int[iNumRows];
        theSortedMap = new int[iNumRows];
        theDataControl = new TableRowEntry[iNumRows];

        /* Loop through the model elements */
        for (int i = 0; i < iNumRows; i++) {
            /* Access the row */
            final T myRow = theModel.getItemAtIndex(i);
            final TableRowEntry<T> myEntry = new TableRowEntry<>(myRow);

            /* Link sorted map */
            theSortedMap[i] = i;
            myEntry.setSorted(i);

            /* Note whether the row is visible */
            myEntry.setView(includeRow(myRow)
                    ? iView++
                    : ROW_FILTERED);

            /* Record entry */
            theDataControl[i] = myEntry;
        }

        /* If we have hidden rows */
        if (iView < iNumRows) {
            /* Truncate the viewToModel array */
            theViewMap = Arrays.copyOf(theViewMap, iView);
        }
        theOldViewMap = null;

        /* Apply the sort */
        applySort(true);
    }

    @Override
    public void rowsDeleted(final int pFirstRow,
                            final int pEndRow) {
        /* Determine model row count */
        final int iNumRows = theDataControl.length;

        /* If the range is invalid */
        if (pFirstRow < 0
                || pEndRow >= iNumRows
                || pFirstRow > pEndRow) {
            throw new IndexOutOfBoundsException(ERROR_RANGE);
        }

        /* Create copies of the sorted and view maps */
        boolean viewDeleted = false;
        int iIndex;

        /* Loop through the rows to be deleted */
        for (int i = pFirstRow; i <= pEndRow; i++) {
            /* Access entry */
            final TableRowEntry<T> myEntry = theDataControl[i];

            /* Mark the sorted entry for deletion */
            iIndex = myEntry.getSorted();
            theSortedMap[iIndex] = ROW_FILTERED;

            /* If the row is currently visible */
            iIndex = myEntry.getView();
            if (iIndex != ROW_FILTERED) {
                /* Mark the view entry for deletion */
                theViewMap[iIndex] = ROW_FILTERED;
                viewDeleted = true;
            }
        }

        /* Compress the ModelToView array */
        final int iNumTrailing = iNumRows - pEndRow - 1;
        final int iNewLen = pFirstRow + iNumTrailing;
        if (iNumTrailing > 0) {
            /* Shift entries in the map up */
            System.arraycopy(theDataControl, pEndRow + 1, theDataControl, pFirstRow, iNumTrailing);

            /* Adjust View references for moved model indices */
            for (int i = pFirstRow; i < iNewLen; i++) {
                /* Access entry */
                final TableRowEntry<T> myEntry = theDataControl[i];

                /* Adjust sorted array */
                iIndex = myEntry.getSorted();
                theSortedMap[iIndex] = i;

                /* If the entry is visible */
                iIndex = myEntry.getView();
                if (iIndex != ROW_FILTERED) {
                    /* Repair the view reference */
                    theViewMap[iIndex] = i;
                }
            }
        }

        /* Compress and store the maps which are still in sort order */
        theDataControl = Arrays.copyOf(theDataControl, iNewLen);
        compressSortMap();
        if (viewDeleted) {
            theViewMap = compressViewMap(theViewMap);
        }
        theOldViewMap = null;
    }

    /**
     * Compress sortMap.
     */
    private void compressSortMap() {
        int iSort = 0;
        final int myLen = theSortedMap.length;

        /* Loop through the sort rows */
        for (int i = 0; i < myLen; i++) {
            /* If the entry is visible */
            final int iIndex = theSortedMap[i];
            if (iIndex != ROW_FILTERED) {
                /* If the index differs */
                if (i != iSort) {
                    /* Move entry up and fix modelToSort */
                    theSortedMap[iSort] = theSortedMap[i];
                    theDataControl[iIndex].setSorted(iSort);
                }

                /* Increment sort count */
                iSort++;
            }
        }

        /* Record the new shrunken map */
        theSortedMap = Arrays.copyOf(theSortedMap, iSort);
    }

    /**
     * Compress viewMap.
     * @param pMap the map to compress
     * @return the compressed map
     */
    private int[] compressViewMap(final int[] pMap) {
        int iView = 0;
        final int myLen = pMap.length;

        /* Loop through the view rows */
        for (int i = 0; i < myLen; i++) {
            /* If the entry is visible */
            final int iIndex = pMap[i];
            if (iIndex != ROW_FILTERED) {
                /* If the index differs */
                if (i != iView) {
                    /* Move entry up and fix modelToView */
                    pMap[iView] = pMap[i];
                    theDataControl[iIndex].setView(iView);
                }

                /* Increment view count */
                iView++;
            }
        }

        /* Return the new shrunken map */
        return Arrays.copyOf(pMap, iView);
    }

    @Override
    public void rowsInserted(final int pFirstRow,
                             final int pEndRow) {
        /* Determine model row count */
        final int iNumRows = theDataControl.length;

        /* If the range is invalid */
        if (pFirstRow < 0
                || pFirstRow > iNumRows
                || pFirstRow > pEndRow) {
            throw new IndexOutOfBoundsException(ERROR_RANGE);
        }

        /* Determine the number of rows that we are inserting plus trailing entries */
        final int iXtraLen = (pEndRow - pFirstRow) + 1;
        final int iNumTrailing = iNumRows - pFirstRow;
        final int iNewLen = iNumRows + iXtraLen;
        int iIndex;

        /* Adjust arrays to have space for new entries */
        theDataControl = Arrays.copyOf(theDataControl, iNewLen);
        theSortedMap = Arrays.copyOf(theSortedMap, iNewLen);

        /* If we have trailing entries */
        if (iNumTrailing > 0) {
            /* Shift the model entries up */
            System.arraycopy(theDataControl, pFirstRow, theDataControl, pEndRow + 1, iNumTrailing);
            for (int i = pEndRow + 1; i < iNewLen; i++) {
                /* Access entry */
                final TableRowEntry<T> myEntry = theDataControl[i];

                /* Adjust sorted array */
                iIndex = myEntry.getSorted();
                theSortedMap[iIndex] = i;
            }
        }

        /* Initialise insert points */
        int iView = theViewMap.length;
        int iSort = iNumRows;

        /* Loop through the new model elements */
        for (int i = pFirstRow; i <= pEndRow; i++) {
            /* Access the row */
            final T myRow = theModel.getItemAtIndex(i);
            final TableRowEntry<T> myEntry = new TableRowEntry<>(myRow);

            /* Link sorted array */
            theSortedMap[iSort] = i;
            myEntry.setSorted(iSort++);

            /* Note whether the row is visible */
            myEntry.setView(includeRow(myRow)
                    ? iView++
                    : ROW_FILTERED);

            /* Record entry */
            theDataControl[i] = myEntry;
        }

        /* If we need to expand the viewMap */
        if (iView > theViewMap.length) {
            /* Expand the viewMap */
            theViewMap = Arrays.copyOf(theViewMap, iView);
        }
        theOldViewMap = null;

        /* Apply the sort */
        applySort(true);
    }

    @Override
    public void rowsUpdated(final int pFirstRow,
                            final int pEndRow) {
        /* Determine model row count */
        final int iNumRows = theDataControl.length;

        /* If the range is invalid */
        if (pFirstRow < 0
                || pEndRow >= iNumRows
                || pFirstRow > pEndRow) {
            throw new IndexOutOfBoundsException(ERROR_RANGE);
        }

        /* take a copy of the old view map */
        theOldViewMap = Arrays.copyOf(theViewMap, theViewMap.length);
        boolean isViewChanged = false;

        /* Initialise counters */
        int iDelta = 0;
        int iView = theViewMap.length;

        /* Loop through the updated model elements */
        for (int i = pFirstRow; i <= pEndRow; i++) {
            /* Access entry */
            final TableRowEntry<T> myEntry = theDataControl[i];

            /* Determine whether we are currently visible */
            final boolean isCurrVisible = myEntry.getView() != ROW_FILTERED;

            /* Access the row */
            final T myRow = myEntry.getRow();

            /* Determine whether we are now visible */
            final boolean isNowVisible = includeRow(myRow);

            /* Skip if there is no change */
            if (isCurrVisible == isNowVisible) {
                continue;
            }

            /* Note the change of view */
            isViewChanged = true;

            /* If the row is visible */
            if (isNowVisible) {
                /* Note newly visible row */
                iDelta++;
                myEntry.setView(iView++);

                /* else not visible */
            } else {
                /* Note newly invisible row */
                iDelta--;
                myEntry.setView(ROW_FILTERED);
            }
        }

        /* If we should adjust the viewMap */
        if (iDelta != 0) {
            /* Resize the ViewMap */
            theViewMap = Arrays.copyOf(theViewMap, theViewMap.length + iDelta);
        }

        /* Sort the array */
        isViewChanged = applySort(isViewChanged);
        if (!isViewChanged) {
            theOldViewMap = null;
        }
    }

    @Override
    public void rowsUpdated(final int pFirstRow,
                            final int pEndRow,
                            final int pColumn) {
        /* Check valid column */
        if (pColumn < 0
                || pColumn >= theModel.getColumnCount()) {
            throw new IndexOutOfBoundsException("Invalid Column");
        }

        /* Ignore column */
        rowsUpdated(pFirstRow, pEndRow);
    }

    /**
     * Do we include row in view.
     * @param pRow the row
     * @return true/false
     */
    private boolean includeRow(final T pRow) {
        return theFilter == null
                || theFilter.test(pRow);
    }

    /**
     * Check whether the first row is correctly before the second row.
     * @param pFirst the first model index to test
     * @param pSecond the second model index to test
     * @return true/false
     */
    private boolean isCorrectOrder(final int pFirst,
                                   final int pSecond) {
        final int myResult = theComparator != null
                ? theComparator.compare(theDataControl[pFirst].getRow(), theDataControl[pSecond].getRow())
                : pFirst - pSecond;
        return myResult <= 0;
    }

    @Override
    public int convertRowIndexToModel(final int pIndex) {
        /* Handle out of range */
        if (pIndex < 0 || pIndex >= getViewRowCount()) {
            throw new IndexOutOfBoundsException(ERROR_INDEX);
        }

        /* Return mapping */
        return theViewMap[pIndex];
    }

    @Override
    public int convertRowIndexToView(final int pIndex) {
        /* Handle out of range */
        if (pIndex < 0 || pIndex >= getModelRowCount()) {
            throw new IndexOutOfBoundsException(ERROR_INDEX);
        }

        /* Return mapping */
        return theDataControl[pIndex].getView();
    }

    @Override
    public List<? extends SortKey> getSortKeys() {
        return theSortList;
    }

    @Override
    public void setSortKeys(final List<? extends SortKey> keys) {
        /* No sort allowed */
    }

    @Override
    public void toggleSortOrder(final int column) {
        /* No sort allowed */
    }

    @Override
    public TethysUISwingTableSorterModel<T> getModel() {
        return theModel;
    }

    @Override
    public int getModelRowCount() {
        return theDataControl.length;
    }

    @Override
    public int getViewRowCount() {
        return theViewMap.length;
    }

    @Override
    public void modelStructureChanged() {
        /* Model changes do not affect sort/filter */
    }

    /**
     * Apply the sort to the list.
     * <p>
     * Uses insertion sort since the list will generally be almost sorted. The ViewMap is expected
     * to be the correct length, but its contents are ignored and it is reBuilt.
     * @param pViewChanged has the view been changed?
     * @return whether the view has changed
     */
    private boolean applySort(final boolean pViewChanged) {
        /* Loop through the sorted map */
        boolean isSorted = false;
        final int iLen = theSortedMap.length;
        for (int i = 1; i < iLen; i++) {
            /* Access the item and note the hole */
            final int myIndex = theSortedMap[i];
            int iHole = i;

            /* Loop while we have a hole */
            while (iHole > 0) {
                /* If we are in the correct place, break loop */
                final int myTest = theSortedMap[iHole - 1];
                if (isCorrectOrder(myTest, myIndex)) {
                    break;
                }

                /* Shift tested item */
                isSorted = true;
                theSortedMap[iHole--] = myTest;
            }

            /* Store item into hole */
            theSortedMap[iHole] = myIndex;
        }

        /* If we sorted or the view has changed */
        final boolean isViewChanged = isSorted || pViewChanged;

        /* If the view Changed */
        if (isViewChanged) {
            /* Loop through the array */
            int iView = 0;
            for (int i = 0; i < iLen; i++) {
                /* Access model index */
                final int iIndex = theSortedMap[i];

                /* Correct the link from the model to the sorted array */
                final TableRowEntry<T> myEntry = theDataControl[iIndex];
                myEntry.setSorted(i);

                /* If the row is visible */
                if (myEntry.getView() != ROW_FILTERED) {
                    /* Correct the link from the model to the view */
                    theViewMap[iView] = iIndex;
                    myEntry.setView(iView++);
                }
            }
        }

        /* Return details as to whether the view changed */
        return isViewChanged;
    }

    /**
     * Obtain an iterator over Sort rows. Note that this iterator is for a self-contained snapshot
     * of the table mapping. It will not be affected or invalidated by subsequent changes.
     * @return the iterator
     */
    public ListIterator<T> sortIterator() {
        /* Allocate iterator */
        return new TableIterator(theSortedMap, theDataControl);
    }

    /**
     * Obtain an iterator over View rows. Note that this iterator is for a self-contained snapshot
     * of the table mapping. It will not be affected or invalidated by subsequent changes.
     * @return the iterator
     */
    public ListIterator<T> viewIterator() {
        /* Allocate iterator */
        return new TableIterator(theViewMap, theDataControl);
    }

    /**
     * Row class for data structures.
     * @param <T> row data type
     */
    private static final class TableRowEntry<T> {
        /**
         * The actual Row.
         */
        private final T theRow;

        /**
         * The sorted index.
         */
        private int theSorted;

        /**
         * The view index.
         */
        private int theView;

        /**
         * Constructor.
         * @param pRow the row
         */
        TableRowEntry(final T pRow) {
            theRow = pRow;
            theSorted = ROW_FILTERED;
            theView = ROW_FILTERED;
        }

        /**
         * Obtain the row.
         * @return the row
         */
        T getRow() {
            return theRow;
        }

        /**
         * Obtain the sorted index.
         * @return the index
         */
        int getSorted() {
            return theSorted;
        }

        /**
         * Obtain the view index.
         * @return the index
         */
        int getView() {
            return theView;
        }

        /**
         * Set the sorted index.
         * @param pIndex the index
         */
        void setSorted(final int pIndex) {
            theSorted = pIndex;
        }

        /**
         * Set the view index.
         * @param pIndex the index
         */
        void setView(final int pIndex) {
            theView = pIndex;
        }
    }

    /**
     * Iterator for rows. Note that this iterator is for a self-contained snapshot of the table
     * mapping. It will not be affected or invalidated by subsequent changes.
     */
    private final class TableIterator
            implements ListIterator<T> {
        /**
         * List to iterate over.
         */
        private final int[] theList;

        /**
         * DataControl.
         */
        private final TableRowEntry<T>[] theDataControl;

        /**
         * Size of index.
         */
        private final int theSize;

        /**
         * index before.
         */
        private int theBeforeIndex;

        /**
         * index after.
         */
        private int theAfterIndex;

        /**
         * Constructor.
         * @param pList the list to iterate over
         * @param pDataControl the dataControl
         */
        TableIterator(final int[] pList,
                      final TableRowEntry<T>[] pDataControl) {
            /* Store the array */
            theSize = pList.length;
            theList = Arrays.copyOf(pList, theSize);
            theDataControl = Arrays.copyOf(pDataControl, pDataControl.length);
            theBeforeIndex = -1;
            theAfterIndex = 0;
        }

        @Override
        public boolean hasNext() {
            return theSize > theAfterIndex;
        }

        @Override
        public int nextIndex() {
            return theAfterIndex;
        }

        @Override
        public T next() {
            /* Check correctness */
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            /* Return the element */
            final int myIndex = theList[theAfterIndex++];
            theBeforeIndex++;
            return theDataControl[myIndex].getRow();
        }

        @Override
        public boolean hasPrevious() {
            /* Determine whether we have further elements */
            return theBeforeIndex >= 0;
        }

        @Override
        public int previousIndex() {
            return theBeforeIndex;
        }

        @Override
        public T previous() {
            /* Check correctness */
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }

            /* Return the element */
            final int myIndex = theList[theBeforeIndex--];
            theAfterIndex--;
            return theDataControl[myIndex].getRow();
        }

        @Override
        public void forEachRemaining(final Consumer<? super T> pAction) {
            while (hasNext()) {
                pAction.accept(next());
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(final T pItem) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(final T pItem) {
            throw new UnsupportedOperationException();
        }
    }
}
