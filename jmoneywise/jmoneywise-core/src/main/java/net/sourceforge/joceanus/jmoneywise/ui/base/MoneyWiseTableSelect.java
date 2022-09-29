/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.base;

import java.util.Iterator;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * Table Selection control.
 * @param <T> the item type
 */
public class MoneyWiseTableSelect<T extends DataItem<MoneyWiseDataType> & Comparable<? super T>> {
    /**
     * The table.
     */
    private final TethysUITableManager<?, T> theTable;

    /**
     * The filter.
     */
    private final Predicate<T> theFilter;

    /**
     * The most recent selection.
     */
    private T theSelected;

    /**
     * Constructor.
     * @param pTable the table
     * @param pFilter the filter
     */
    MoneyWiseTableSelect(final TethysUITableManager<?, T> pTable,
                         final Predicate<T> pFilter) {
        theTable = pTable;
        theFilter = pFilter;
    }

    /**
     * Record the selected item.
     * @param pItem the selected item
     */
    public void recordSelection(final T pItem) {
        /* Ignore null selection */
        if (pItem != null) {
            /* Record the selection */
            theSelected = pItem;

            /* Scroll it into view */
            theTable.scrollSelectedToView();
        }
    }

    /**
     * Restore the selected item.
     */
    public void restoreSelected() {
        /* Determine the item to select */
        T myItem = theSelected;

        /* If we have a selected item */
        if (myItem != null) {
            /* If it is no longer visible */
            if (theFilter != null && !theFilter.test(myItem)) {
                /* locate the nearest item */
                myItem = findNearest();
            }
        } else {
            /* Find the first item */
            myItem = findFirst();
        }

        /* Select the item */
        theTable.selectRow(myItem);
    }

    /**
     * Find the first visible item.
     * @return the first item
     */
    private T findFirst() {
        /* Access the view iterator */
        final Iterator<T> myIterator = theTable.viewIterator();
        while (myIterator.hasNext()) {
            final T myItem = myIterator.next();

            /* Return first non-header */
            if (!myItem.isHeader()) {
                return myItem;
            }
        }

        /* None found */
        return null;
    }

    /**
     * Find the nearest visible item.
     * @return the nearest item
     */
    private T findNearest() {
        /* The most recent item */
        T myBest = null;

        /* Access the view iterator */
        final Iterator<T> myIterator = theTable.viewIterator();
        while (myIterator.hasNext()) {
            final T myItem = myIterator.next();

            /* Ignore header */
            if (myItem.isHeader()) {
                continue;
            }

            /* If we have zoomed past the selected item */
            if (myItem.compareTo(theSelected) > 0) {
                return myBest == null ? myItem : myBest;
            }

            /* Note the nearest item */
            myBest = myItem;
        }

        /* Return the best item */
        return myBest;
    }
}
