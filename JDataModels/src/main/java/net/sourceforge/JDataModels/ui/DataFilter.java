/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.RowSorter;
import javax.swing.table.TableModel;

import net.sourceforge.JDataModels.ui.DataFilter.DataFilterModel;

/**
 * RowSorter to provide filtering capabilities without sort.
 * @param <T> row data type
 */
public class DataFilter<T extends Comparable<T>> extends RowSorter<DataFilterModel<T>> {
    /**
     * Interface for Model.
     * @param <T> the row type
     */
    public interface DataFilterModel<T extends Comparable<T>> extends TableModel {
        /**
         * Obtain the item at the model index.
         * @param pRowIndex the index
         * @return the item
         */
        T getItemAtIndex(final int pRowIndex);

        /**
         * Include item at index in view.
         * @param pRowIndex the index
         * @return true/false
         */
        boolean includeIndex(final int pRowIndex);

        /**
         * Register filter.
         * @param pFilter the filter
         */
        void registerFilter(final DataFilter<T> pFilter);
    }

    /**
     * The model for the Filter.
     */
    private final DataFilterModel<T> theModel;

    /**
     * Mapping from View to Model.
     */
    private int[] theViewToModel;

    /**
     * Mapping from Model to View. If an element is not present in the view the value is set to -1.
     */
    private int[] theModelToView;

    /**
     * Saved mapping for ViewToModel.
     */
    private int[] theOldViewToModel = null;

    /**
     * Empty SortKey list.
     */
    private List<SortKey> theSortList;

    /**
     * Constructor.
     * @param pModel the model
     */
    public DataFilter(final DataFilterModel<T> pModel) {
        theModel = pModel;
        pModel.registerFilter(this);
        allRowsChanged();
        theSortList = new ArrayList<SortKey>();
    }

    @Override
    public final void allRowsChanged() {
        /* Allocate the two arrays */
        int iNumRows = theModel.getRowCount();
        int iView = 0;
        theViewToModel = new int[iNumRows];
        theModelToView = new int[iNumRows];

        /* Loop through the model elements */
        for (int i = 0; i < iNumRows; i++) {
            /* If the row is visible */
            if (theModel.includeIndex(i)) {
                /* Set indices */
                theViewToModel[iView] = i;
                theModelToView[i] = iView++;

                /* else not visible */
            } else {
                theModelToView[i] = -1;
            }
        }

        /* If we have hidden rows */
        if (iView < iNumRows) {
            /* Truncate the viewToModel array */
            theViewToModel = Arrays.copyOf(theViewToModel, iView);
        }
    }

    @Override
    public int convertRowIndexToModel(final int pIndex) {
        /* Handle out of range */
        if ((pIndex < 0) || (pIndex >= getViewRowCount())) {
            throw new IndexOutOfBoundsException("Invalid Index");
        }

        /* If we have a mapping */
        return theViewToModel[pIndex];
    }

    @Override
    public int convertRowIndexToView(final int pIndex) {
        /* Handle out of range */
        if ((pIndex < 0) || (pIndex >= getModelRowCount())) {
            throw new IndexOutOfBoundsException("Invalid Index");
        }

        /* If we have a mapping */
        return theModelToView[pIndex];
    }

    @Override
    public DataFilterModel<T> getModel() {
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

        /* If the range is invalid */
        if ((pFirstRow < 0) || (pEndRow >= iNumRows) || (pFirstRow > pEndRow)) {
            throw new IndexOutOfBoundsException("Invalid Range");
        }

        /* Make a copy of the ViewToModel, expanded to full amount */
        int[] newViewToModel = Arrays.copyOf(theViewToModel, iNumRows);
        int iView = 0;

        /* Loop through the existing model elements */
        for (int i = 0; i < iNumRows; i++) {
            /* Determine whether we are currently visible */
            boolean isCurrVisible = (theModelToView[i] != -1);

            /* If we have yet to reach the first row */
            if (i < pFirstRow) {
                /* If we are currently visible */
                if (isCurrVisible) {
                    /* Increment view */
                    iView++;
                }

                /* Re-loop */
                continue;
            }

            /* Determine whether we are now visible */
            boolean isNowVisible = (i > pEndRow) ? isCurrVisible : false;

            /* If the row is visible */
            if (isNowVisible) {
                /* Set indices */
                newViewToModel[iView] = i;
                theModelToView[i] = iView++;

                /* else not visible */
            } else {
                theModelToView[i] = -1;
            }
        }

        /* Calculate number of trailing entries and new row count */
        int iNumTrailing = iNumRows - pEndRow - 1;
        int iNewLen = pFirstRow + iNumTrailing;
        if (iNumTrailing > 0) {
            /* Shift entries in the map down */
            System.arraycopy(theModelToView, pEndRow + 1, theModelToView, pFirstRow, iNumTrailing);

            /* Adjust View indices */
            for (int i = pFirstRow; i < iNewLen; i++) {
                /* If the entry is visible */
                int index = theModelToView[i];
                if (index != -1) {
                    /* Repair the index */
                    newViewToModel[index] = i;
                }
            }
        }

        /* Truncate the arrays */
        theModelToView = Arrays.copyOf(theModelToView, iNewLen);
        newViewToModel = Arrays.copyOf(newViewToModel, iView);

        /* Save the old mapping and swap in the new */
        theOldViewToModel = theViewToModel;
        theViewToModel = newViewToModel;
    }

    @Override
    public void rowsInserted(final int pFirstRow,
                             final int pEndRow) {
        /* Determine model row count */
        int iNumRows = getModelRowCount();

        /* If the range is invalid */
        if ((pFirstRow < 0) || (pFirstRow > iNumRows) || (pFirstRow > pEndRow)) {
            throw new IndexOutOfBoundsException("Invalid Range");
        }

        /* Determine the number of rows that we are inserting plus trailing entries */
        int iXtraLen = (pEndRow - pFirstRow) + 1;
        int iNumTrailing = iNumRows - pFirstRow;

        /* Adjust Model to view to have space for new entries */
        theModelToView = Arrays.copyOf(theModelToView, iNumRows + iXtraLen);
        if (iNumTrailing > 0) {
            System.arraycopy(theModelToView, pFirstRow, theModelToView, pEndRow + 1, iNumTrailing);
        }
        iNumRows += iXtraLen;

        /* Make a copy of the ViewToModel, expanded to full amount */
        int[] newViewToModel = Arrays.copyOf(theViewToModel, iNumRows);
        int iView = 0;

        /* Loop through the model elements */
        for (int i = 0; i < iNumRows; i++) {
            /* Determine whether we are currently visible */
            boolean isCurrVisible = (theModelToView[i] != -1);

            /* If we have yet to reach the first row */
            if (i < pFirstRow) {
                /* If we are currently visible */
                if (isCurrVisible) {
                    /* Increment view */
                    iView++;
                }

                /* Re-loop */
                continue;
            }

            /* Determine whether we are now visible */
            boolean isNowVisible = (i > pEndRow) ? isCurrVisible : theModel.includeIndex(i);

            /* If the row is visible */
            if (isNowVisible) {
                /* Set indices */
                newViewToModel[iView] = i;
                theModelToView[i] = iView++;

                /* else not visible */
            } else {
                theModelToView[i] = -1;
            }
        }

        /* If we have hidden rows */
        if (iView < iNumRows) {
            /* Truncate the new mapping */
            newViewToModel = Arrays.copyOf(newViewToModel, iView);
        }

        /* Save the old mapping and swap in the new */
        theOldViewToModel = theViewToModel;
        theViewToModel = newViewToModel;
    }

    @Override
    public void rowsUpdated(final int pFirstRow,
                            final int pEndRow) {
        /* Determine model row count */
        int iNumRows = theModelToView.length;

        /* If the range is invalid */
        if ((pFirstRow < 0) || (pEndRow >= iNumRows) || (pFirstRow > pEndRow)) {
            throw new IndexOutOfBoundsException("Invalid Range");
        }

        /* Make a copy of the ViewToModel, expanded to full amount */
        theOldViewToModel = null;
        int[] newViewToModel = Arrays.copyOf(theViewToModel, iNumRows);
        int iView = 0;
        boolean isChanged = false;

        /* Loop through the existing model elements */
        for (int i = 0; i < iNumRows; i++) {
            /* Determine whether we are currently visible */
            boolean isCurrVisible = (theModelToView[i] != -1);

            /* If we have yet to reach the first row */
            if (i < pFirstRow) {
                /* If we are currently visible */
                if (isCurrVisible) {
                    /* Increment view */
                    iView++;
                }

                /* Re-loop */
                continue;
            }

            /* Determine whether we are now visible */
            boolean isNowVisible = (i > pEndRow) ? isCurrVisible : theModel.includeIndex(i);

            /* Note if we have made a change */
            if (isCurrVisible != isNowVisible) {
                isChanged = true;
            }

            /* If the row is visible */
            if (isNowVisible) {
                /* Set indices */
                newViewToModel[iView] = i;
                theModelToView[i] = iView++;

                /* else not visible */
            } else {
                theModelToView[i] = -1;
            }
        }

        /* if there has been a change */
        if (isChanged) {
            /* If we have hidden rows */
            if (iView < iNumRows) {
                /* Truncate the new mapping */
                newViewToModel = Arrays.copyOf(newViewToModel, iView);
            }

            /* Save the old mapping and swap in the new */
            theOldViewToModel = theViewToModel;
            theViewToModel = newViewToModel;
        }
    }

    /**
     * Update sort mapping if required.
     * <p>
     * This should really be done within the rowsUpdated() etc. methods, but unfortunately fireXXX methods are
     * ignored from within these methods (undocumented), and hence additional calls must be made to honour the
     * interface.
     * <p>
     * Note also that the parameter to the fireMethod is ViewToModel as per the documented name of the
     * parameter and not the description.
     */
    public void reportMappingChanged() {
        /* If we have a saved mapping change */
        if (theOldViewToModel != null) {
            /* Fire the event */
            fireRowSorterChanged(theOldViewToModel);

            /* Reset old view to model */
            theOldViewToModel = null;
        }
    }

    @Override
    public void rowsUpdated(final int pFirstRow,
                            final int pEndRow,
                            final int pColumn) {
        /* Check valid column */
        if ((pColumn < 0) || (pColumn >= theModel.getColumnCount())) {
            throw new IndexOutOfBoundsException("Invalid Column");
        }

        /* Ignore column */
        rowsUpdated(pFirstRow, pEndRow);
    }

    @Override
    public void setSortKeys(final List<? extends SortKey> keys) {
        /* No sort allowed */
    }

    @Override
    public void toggleSortOrder(final int column) {
        /* No sort allowed */
    }
}
