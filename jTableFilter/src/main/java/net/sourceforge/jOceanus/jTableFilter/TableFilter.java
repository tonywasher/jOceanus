/*******************************************************************************
 * jTableFilter: JTable RowFilter/Sorter
 * Copyright 2012 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jTableFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.table.TableModel;

import net.sourceforge.jOceanus.jTableFilter.TableFilter.TableFilterModel;

/**
 * RowSorter to provide filtering capabilities with natural sorting.
 * @param <T> row data type
 */
public class TableFilter<T extends Comparable<? super T>>
        extends RowSorter<TableFilterModel<T>> {
    /**
     * Interface for Model.
     * @param <T> the row type
     */
    public interface TableFilterModel<T extends Comparable<? super T>>
            extends TableModel {
        /**
         * Obtain the item at the model index.
         * @param pRowIndex the index
         * @return the item
         */
        T getItemAtIndex(final int pRowIndex);

        /**
         * Do we include row in view.
         * @param pRow the row
         * @return true/false
         */
        boolean includeRow(final T pRow);
    }

    /**
     * Filtered index indicator.
     */
    private static final int ROW_FILTERED = -1;

    /**
     * Row class for data structures.
     * @param <X> the row type
     */
    private static final class RowEntry<X extends Comparable<? super X>> {
        /**
         * The actual Row.
         */
        private X theRow;

        /**
         * The reference index.
         */
        private int theReference;

        /**
         * Constructor.
         * @param pRow the row
         */
        private RowEntry(final X pRow) {
            /* Store parameters */
            theRow = pRow;
            theReference = ROW_FILTERED;
        }

        /**
         * Constructor.
         * @param pEntry the entry
         */
        private RowEntry(final RowEntry<X> pEntry) {
            /* Store parameters */
            theRow = pEntry.theRow;
            theReference = pEntry.theReference;
        }

        /**
         * Constructor.
         * @param pRow the row
         * @param pReference the reference
         */
        private RowEntry(final X pRow,
                         final int pReference) {
            /* Store parameters */
            theRow = pRow;
            theReference = pReference;
        }

        /**
         * Compare row using natural order.
         * @param pThat the row to compare to
         * @return -1,0,1 as per the order
         */
        public int compareRow(final RowEntry<X> pThat) {
            return theRow.compareTo(pThat.theRow);
        }

        /**
         * Compare reference.
         * @param pThat the row to compare to
         * @return negative,0,positive as per the order
         */
        public int compareReference(final RowEntry<X> pThat) {
            return theReference
                   - pThat.theReference;
        }
    }

    /**
     * The model for the Filter.
     */
    private final TableFilterModel<T> theModel;

    /**
     * Are we sorting?
     */
    private boolean doSort = false;

    /**
     * Mapping from View to Model.
     */
    private RowEntry<T>[] theViewToModel;

    /**
     * Mapping from Model to View.
     */
    private RowEntry<T>[] theModelToView;

    /**
     * Index mapping for ViewToModel.
     */
    private int[] theIndexViewToModel = null;

    /**
     * Empty SortKey list.
     */
    private List<SortKey> theSortList;

    /**
     * Constructor.
     * @param pModel the model
     */
    public TableFilter(final TableFilterModel<T> pModel) {
        this(pModel, false);
    }

    /**
     * Constructor.
     * @param pModel the model
     * @param sortEntries do we sort entries? true/false
     */
    public TableFilter(final TableFilterModel<T> pModel,
                       final boolean sortEntries) {
        theModel = pModel;
        doSort = sortEntries;
        allRowsChanged();
        theSortList = new ArrayList<SortKey>();
    }

    /**
     * Obtain reference array from mapping array.
     * @param <X> the rowType
     * @param pSource the mapping array
     * @return the reference array
     */
    private static <X extends Comparable<? super X>> int[] getReferenceArray(final RowEntry<X>[] pSource) {
        /* Allocate new array */
        int iNumRows = pSource.length;
        int[] myArray = new int[iNumRows];

        /* Loop through the rows copying reference */
        for (int i = 0; i < iNumRows; i++) {
            myArray[i] = pSource[i].theReference;
        }

        /* Return the new array */
        return myArray;
    }

    /**
     * Deep clone the array.
     * @param <X> the rowType
     * @param pSource the mapping array
     * @param pNewLen the new length of the array
     * @return the reference array
     */
    private static <X extends Comparable<? super X>> RowEntry<X>[] cloneArray(final RowEntry<X>[] pSource,
                                                                              final int pNewLen) {
        /* Allocate new array */
        @SuppressWarnings("unchecked")
        RowEntry<X>[] myArray = (RowEntry<X>[]) new RowEntry[pNewLen];
        int iSrcLen = pSource.length;

        /* Loop through the rows copying reference */
        for (int i = 0; i < iSrcLen; i++) {
            RowEntry<X> myEntry = pSource[i];
            if (myEntry != null) {
                myArray[i] = new RowEntry<X>(myEntry);
            }
        }

        /* Return the new array */
        return myArray;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void allRowsChanged() {
        /* Allocate the two arrays */
        int iNumRows = theModel.getRowCount();
        int iView = 0;
        theViewToModel = (RowEntry<T>[]) new RowEntry[iNumRows];
        theModelToView = (RowEntry<T>[]) new RowEntry[iNumRows];

        /* Loop through the model elements */
        for (int i = 0; i < iNumRows; i++) {
            /* Access the row */
            T myRow = theModel.getItemAtIndex(i);
            RowEntry<T> myEntry = new RowEntry<T>(myRow);

            /* If the row is visible */
            if (theModel.includeRow(myRow)) {
                /* Update view and link arrays */
                theViewToModel[iView] = new RowEntry<T>(myRow, i);
                myEntry.theReference = iView++;

                /* else not visible */
            } else {
                myEntry.theReference = ROW_FILTERED;
            }

            /* Record entry */
            theModelToView[i] = myEntry;
        }

        /* If we have hidden rows */
        if (iView < iNumRows) {
            /* Truncate the viewToModel array */
            theViewToModel = Arrays.copyOf(theViewToModel, iView);
        }

        /* If we are sorting */
        if (doSort) {
            /* Sort the view mapping */
            sortViewToModel(theViewToModel);
        }
    }

    /**
     * Set Sort mode.
     * @param sortEntries do we sort entries? true/false
     */
    public void setSortMode(final boolean sortEntries) {
        /* If the mode is changing */
        if (doSort != sortEntries) {
            /* Record the mode */
            doSort = sortEntries;

            /* Record the old reference mapping */
            theIndexViewToModel = getReferenceArray(theViewToModel);

            /* Rebuild the mappings */
            allRowsChanged();

            /* Report the mapping change */
            reportMappingChanged();
        }
    }

    @Override
    public int convertRowIndexToModel(final int pIndex) {
        /* Handle out of range */
        if ((pIndex < 0)
            || (pIndex >= getViewRowCount())) {
            throw new IndexOutOfBoundsException("Invalid Index");
        }

        /* Return mapping */
        return theViewToModel[pIndex].theReference;
    }

    @Override
    public int convertRowIndexToView(final int pIndex) {
        /* Handle out of range */
        if ((pIndex < 0)
            || (pIndex >= getModelRowCount())) {
            throw new IndexOutOfBoundsException("Invalid Index");
        }

        /* Return mapping */
        return theModelToView[pIndex].theReference;
    }

    @Override
    public TableFilterModel<T> getModel() {
        return theModel;
    }

    @Override
    public int getModelRowCount() {
        return theModelToView.length;
    }

    @Override
    public List<? extends SortKey> getSortKeys() {
        return theSortList;
    }

    @Override
    public int getViewRowCount() {
        return theViewToModel.length;
    }

    @Override
    public void modelStructureChanged() {
        /* Rebuild maps */
        allRowsChanged();
    }

    @Override
    public void rowsDeleted(final int pFirstRow,
                            final int pEndRow) {
        /* Determine model row count */
        int iNumRows = theModelToView.length;
        int iIndex;

        /* If the range is invalid */
        if ((pFirstRow < 0)
            || (pEndRow >= iNumRows)
            || (pFirstRow > pEndRow)) {
            throw new IndexOutOfBoundsException("Invalid Range");
        }

        /* Make a deep copy of the ViewToModel */
        RowEntry<T>[] newViewToModel = cloneArray(theViewToModel, theViewToModel.length);
        boolean viewDeleted = false;

        /* Loop through the rows to be deleted */
        for (int i = pFirstRow; i <= pEndRow; i++) {
            /* Access view reference */
            iIndex = theModelToView[i].theReference;

            /* If the row is currently visible */
            if (iIndex != ROW_FILTERED) {
                /* Remove link from view to Model */
                newViewToModel[iIndex].theReference = ROW_FILTERED;
                viewDeleted = true;
            }
        }

        /* Compress the ModelToView array */
        int iNumTrailing = iNumRows
                           - pEndRow
                           - 1;
        int iNewLen = pFirstRow
                      + iNumTrailing;
        if (iNumTrailing > 0) {
            /* Shift entries in the map down */
            System.arraycopy(theModelToView, pEndRow + 1, theModelToView, pFirstRow, iNumTrailing);

            /* Adjust View references for moved model indices */
            for (int i = pFirstRow; i < iNewLen; i++) {
                /* If the entry is visible */
                iIndex = theModelToView[i].theReference;
                if (iIndex != ROW_FILTERED) {
                    /* Repair the reference */
                    newViewToModel[iIndex].theReference = i;
                }
            }
        }

        /* Truncate the modelToView array */
        theModelToView = Arrays.copyOf(theModelToView, iNewLen);

        /* If we should compress the viewToModel array */
        if (viewDeleted) {
            /* Compress the viewToModel array */
            newViewToModel = compressViewToModel(newViewToModel, newViewToModel.length);
        }

        /* Record the old reference mapping and swap in the new */
        theIndexViewToModel = getReferenceArray(theViewToModel);
        theViewToModel = newViewToModel;
    }

    @Override
    public void rowsInserted(final int pFirstRow,
                             final int pEndRow) {
        /* Determine model row count */
        int iNumRows = getModelRowCount();

        /* If the range is invalid */
        if ((pFirstRow < 0)
            || (pFirstRow > iNumRows)
            || (pFirstRow > pEndRow)) {
            throw new IndexOutOfBoundsException("Invalid Range");
        }

        /* Determine the number of rows that we are inserting plus trailing entries */
        int iXtraLen = (pEndRow - pFirstRow) + 1;
        int iNumTrailing = iNumRows
                           - pFirstRow;
        int iNewLen = iNumRows
                      + iXtraLen;
        int iIndex;

        /* Adjust arrays to have space for new entries */
        theModelToView = Arrays.copyOf(theModelToView, iNewLen);
        RowEntry<T>[] newViewToModel = cloneArray(theViewToModel, theViewToModel.length
                                                                  + iXtraLen);

        /* If we have trailing entries */
        if (iNumTrailing > 0) {
            /* Shift the model entries up */
            System.arraycopy(theModelToView, pFirstRow, theModelToView, pEndRow + 1, iNumTrailing);
            for (int i = pEndRow + 1; i < iNewLen; i++) {
                /* If the entry is visible */
                iIndex = theModelToView[i].theReference;
                if (iIndex != ROW_FILTERED) {
                    /* Adjust view to model reference */
                    newViewToModel[iIndex].theReference = i;
                }
            }
        }

        /* Initialise insert point */
        int iView = theViewToModel.length;

        /* Loop through the new model elements */
        for (int i = pFirstRow; i <= pEndRow; i++) {
            /* Access the row */
            T myRow = theModel.getItemAtIndex(i);
            RowEntry<T> myEntry = new RowEntry<T>(myRow);

            /* If the row is visible */
            if (theModel.includeRow(myRow)) {
                /* Update view and link arrays */
                newViewToModel[iView] = new RowEntry<T>(myRow, i);
                myEntry.theReference = iView++;

                /* else not visible */
            } else {
                myEntry.theReference = ROW_FILTERED;
            }

            /* Record entry */
            theModelToView[i] = myEntry;
        }

        /* If we have hidden rows */
        if (iView < theViewToModel.length
                    + iXtraLen) {
            /* Truncate the new mapping */
            newViewToModel = Arrays.copyOf(newViewToModel, iView);
        }

        /* Sort the view mapping */
        sortViewToModel(newViewToModel);

        /* Save the old mapping and swap in the new */
        theIndexViewToModel = getReferenceArray(theViewToModel);
        theViewToModel = newViewToModel;
    }

    @Override
    public void rowsUpdated(final int pFirstRow,
                            final int pEndRow) {
        /* Determine model row count */
        int iNumRows = theModelToView.length;

        /* If the range is invalid */
        if ((pFirstRow < 0)
            || (pEndRow >= iNumRows)
            || (pFirstRow > pEndRow)) {
            throw new IndexOutOfBoundsException("Invalid Range");
        }

        /* Make a copy of the ViewToModel, expanded to full amount */
        theIndexViewToModel = null;
        RowEntry<T>[] newViewToModel = cloneArray(theViewToModel, iNumRows);
        int iView = theViewToModel.length;
        boolean viewChanged = false;
        boolean viewDeleted = false;

        /* Loop through the updated model elements */
        for (int i = pFirstRow; i <= pEndRow; i++) {
            /* Determine whether we are currently visible */
            boolean isCurrVisible = (theModelToView[i].theReference != ROW_FILTERED);

            /* Access the row */
            T myRow = theModelToView[i].theRow;

            /* Determine whether we are now visible */
            boolean isNowVisible = theModel.includeRow(myRow);

            /* Skip if there is no change */
            if (isCurrVisible == isNowVisible) {
                continue;
            }

            /* Note that there is a change */
            viewChanged = true;

            /* If the row is visible */
            if (isNowVisible) {
                /* Set indices */
                newViewToModel[iView] = new RowEntry<T>(myRow, i);
                theModelToView[i].theReference = iView++;

                /* else not visible */
            } else {
                /* Access view reference */
                int iIndex = theModelToView[i].theReference;

                /* Remove links between view and Model */
                newViewToModel[iIndex].theReference = ROW_FILTERED;
                theModelToView[i].theReference = ROW_FILTERED;
                viewDeleted = true;
            }
        }

        /* If we should compress the viewToModel array */
        if (viewDeleted) {
            /* Compress the viewToModel array */
            newViewToModel = compressViewToModel(newViewToModel, iView);

            /* else if we have hidden rows */
        } else if (iView < iNumRows) {
            /* Truncate the new mapping */
            newViewToModel = Arrays.copyOf(newViewToModel, iView);
        }

        /* Sort the array */
        viewChanged = sortViewToModel(newViewToModel)
                      || viewChanged;

        /* If we have changed */
        if (viewChanged) {
            /* Save the old mapping and swap in the new */
            theIndexViewToModel = getReferenceArray(theViewToModel);
            theViewToModel = newViewToModel;
        }
    }

    /**
     * Update sort mapping if required.
     * <p>
     * This should really be done within the rowsUpdated() method, but unfortunately fireXXX methods are ignored from within these method (undocumented), and
     * hence additional calls must be made to honour the interface.
     * <p>
     * Note also that the parameter to the fireMethod is ViewToModel as per the documented name of the parameter and not the description.
     */
    public void reportMappingChanged() {
        /* If we have a saved mapping change */
        if (theIndexViewToModel != null) {
            /* Fire the event */
            fireRowSorterChanged(theIndexViewToModel);

            /* Reset reference model */
            theIndexViewToModel = null;
        }
    }

    @Override
    public void rowsUpdated(final int pFirstRow,
                            final int pEndRow,
                            final int pColumn) {
        /* Check valid column */
        if ((pColumn < 0)
            || (pColumn >= theModel.getColumnCount())) {
            throw new IndexOutOfBoundsException("Invalid Column");
        }

        /* Ignore column */
        rowsUpdated(pFirstRow, pEndRow);
    }

    /**
     * Compress viewToModel array.
     * @param pArray the array to compress
     * @param pLength the length of the array
     * @return the compressed array
     */
    private RowEntry<T>[] compressViewToModel(final RowEntry<T>[] pArray,
                                              final int pLength) {
        /* Compress the ViewToModel array */
        int iView = 0;
        for (int i = 0; i < pLength; i++) {
            /* If the entry is visible */
            int iIndex = pArray[i].theReference;
            if (iIndex != ROW_FILTERED) {
                /* If the index differs */
                if (i != iView) {
                    /* Move entry down and fix model reference */
                    pArray[iView] = pArray[i];
                    theModelToView[iIndex].theReference = iView;
                }

                /* Increment view count */
                iView++;
            }
        }

        /* Return the new shrunken array */
        return Arrays.copyOf(pArray, iView);
    }

    /**
     * Check whether the first row is correctly before the second row.
     * @param pFirst the first row to test
     * @param pSecond the second row to test
     * @return true/false
     */
    private boolean isCorrectOrder(final RowEntry<T> pFirst,
                                   final RowEntry<T> pSecond) {
        int iResult = (doSort) ? pFirst.compareRow(pSecond) : pFirst.compareReference(pSecond);
        return iResult <= 0;
    }

    /**
     * Sort the viewToModel map.
     * <p>
     * Uses insertion sort since the list will generally be almost sorted. We could used Arrays.sort() but this does not return an indication of whether the
     * array was changed by the sort.
     * @param pArray the array to sort
     * @return was sort order changed? true/false
     */
    private boolean sortViewToModel(final RowEntry<T>[] pArray) {
        /* Default is no sort occurred */
        boolean isChanged = false;

        /* Loop through the array */
        int iLen = pArray.length;
        for (int i = 1; i < iLen; i++) {
            /* Access the item and note the hole */
            RowEntry<T> myItem = pArray[i];
            int iHole = i;

            /* Loop while we have a hole */
            while (iHole > 0) {
                /* If we are in the correct place, break loop */
                RowEntry<T> myTest = pArray[iHole - 1];
                if (isCorrectOrder(myTest, myItem)) {
                    break;
                }

                /* Shift tested item and note the change */
                pArray[iHole--] = myTest;
                isChanged = true;
            }

            /* Store item into hole */
            pArray[iHole] = myItem;
        }

        /* If a change occurred */
        if (isChanged) {
            /* Loop through the array */
            for (int i = 0; i < iLen; i++) {
                /* Access view reference */
                int iIndex = pArray[i].theReference;

                /* Correct the link from the model */
                theModelToView[iIndex].theReference = i;
            }
        }

        /* Return whether a sort was effected */
        return isChanged;
    }

    @Override
    public void setSortKeys(final List<? extends SortKey> keys) {
        /* No sort allowed */
    }

    @Override
    public void toggleSortOrder(final int column) {
        /* No sort allowed */
    }

    /**
     * Obtain an iterator over Model rows. Note that this iterator is for a self-contained snapshot of the table mapping. It will not be affected or invalidated
     * by subsequent changes.
     * @return the iterator
     */
    public Iterator<T> modelIterator() {
        /* Allocate iterator */
        return new TableIterator(theModelToView);
    }

    /**
     * Obtain an iterator over View rows. Note that this iterator is for a self-contained snapshot of the table mapping. It will not be affected or invalidated
     * by subsequent changes.
     * @return the iterator
     */
    public Iterator<T> viewIterator() {
        /* Allocate iterator */
        return new TableIterator(theViewToModel);
    }

    /**
     * Iterator for rows. Note that this iterator is for a self-contained snapshot of the table mapping. It will not be affected or invalidated by subsequent
     * changes.
     */
    private final class TableIterator
            implements Iterator<T> {
        /**
         * List to iterate over.
         */
        private final RowEntry<T>[] theArray;

        /**
         * Size of index.
         */
        private int theSize;

        /**
         * Next index.
         */
        private int theIndex;

        /**
         * Constructor.
         * @param pArray the array to iterate over
         */
        private TableIterator(final RowEntry<T>[] pArray) {
            /* Store the array */
            theArray = pArray;
            theSize = theArray.length;
            theIndex = 0;
        }

        @Override
        public boolean hasNext() {
            /* Determine whether we have further elements */
            return (theSize > theIndex);
        }

        @Override
        public T next() {
            /* Return the element */
            return theArray[theIndex++].theRow;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
