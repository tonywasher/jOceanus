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
package io.github.tonywasher.joceanus.prometheus.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import io.github.tonywasher.joceanus.metis.data.MetisDataState;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataList;

/**
 * Batch control class. This controls updating data lists after the commit of the batch.
 */
public class PrometheusBatchControl {
    /**
     * Capacity of Batch Control (0=Unlimited).
     */
    private final int theCapacity;

    /**
     * Number of items in this batch.
     */
    private int theItems;

    /**
     * The List of tables associated with this batch.
     */
    private List<PrometheusBatchTable> theList;

    /**
     * The Currently active Database table.
     */
    private PrometheusTableDataItem<?> theCurrTable;

    /**
     * The Currently active Mode.
     */
    private MetisDataState theCurrMode;

    /**
     * Is the current table in use.
     */
    private boolean isTableActive;

    /**
     * Constructor.
     *
     * @param pBatchSize the batch size
     */
    protected PrometheusBatchControl(final Integer pBatchSize) {
        /* Create the batch table list */
        theList = new ArrayList<>();

        /* Store capacity and capacity */
        theCapacity = pBatchSize;
    }

    /**
     * Is the batch full.
     *
     * @return true/false is the batch full
     */
    protected boolean isFull() {
        return theCapacity != 0
                && theItems >= theCapacity;
    }

    /**
     * Is the batch active.
     *
     * @return true/false is the batch active
     */
    protected boolean isActive() {
        return theItems >= 0;
    }

    /**
     * Set the currently active state.
     *
     * @param pTable the Table being operated on
     * @param pMode  the Mode that is in operation
     */
    protected void setCurrentTable(final PrometheusTableDataItem<?> pTable,
                                   final MetisDataState pMode) {
        /* Store details */
        theCurrTable = pTable;
        theCurrMode = pMode;
        isTableActive = false;
    }

    /**
     * Add item to the batch.
     */
    protected void addBatchItem() {
        /* Increment batch count */
        theItems++;

        /* If the current table is not active */
        if (!isTableActive) {
            /* Create the batch entry */
            final PrometheusBatchTable myTable = new PrometheusBatchTable();

            /* Add to the batch list */
            theList.add(myTable);
            isTableActive = true;
        }
    }

    /**
     * Commit the batch.
     */
    protected void commitItems() {
        /* Access iterator for the list */
        final Iterator<PrometheusBatchTable> myIterator = theList.iterator();

        /* Loop through the items */
        while (myIterator.hasNext()) {
            /* Access the next entry */
            final PrometheusBatchTable myTable = myIterator.next();

            /* Commit batch items in the table */
            switch (myTable.theState) {
                case DELETED:
                    myTable.commitDeleteBatch();
                    break;
                default:
                    myTable.commitBatch();
                    break;
            }
        }

        /* Clear the list */
        theList.clear();
        isTableActive = false;
        theItems = 0;
    }

    /**
     * Table step.
     */
    private final class PrometheusBatchTable {
        /**
         * The table that is being controlled.
         */
        private final PrometheusTableDataItem<?> theTable;

        /**
         * The State of the table.
         */
        private final MetisDataState theState;

        /**
         * Constructor.
         */
        private PrometheusBatchTable() {
            /* Store the details */
            theTable = theCurrTable;
            theState = theCurrMode;
        }

        /**
         * Mark updates in the table as committed for insert/change.
         */
        private void commitBatch() {
            /* Access the iterator */
            final Iterator<?> myIterator = theTable.getList().iterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                final PrometheusDataItem myCurr = (PrometheusDataItem) myIterator.next();

                /* Ignore items that are not this type */
                if (myCurr.getState() != theState) {
                    continue;
                }

                /* Commit the item and break loop if required */
                if (commitItem(myCurr)) {
                    break;
                }
            }
        }

        /**
         * Mark updates in the table as committed for delete.
         */
        private void commitDeleteBatch() {
            /* Access the iterator */
            final PrometheusDataList<?> myList = theTable.getList();
            final ListIterator<?> myIterator = myList.listIterator(myList.size());

            /* Loop through the list */
            while (myIterator.hasPrevious()) {
                final PrometheusDataItem myCurr = (PrometheusDataItem) myIterator.previous();

                /* Ignore items that are not this type */
                final MetisDataState myState = myCurr.getState();
                if ((myState != MetisDataState.DELETED)
                        && (myState != MetisDataState.DELNEW)) {
                    continue;
                }

                /* Commit the item and break loop if required */
                if (commitItem(myCurr)) {
                    break;
                }
            }
        }

        /**
         * Mark an item in the table as committed.
         *
         * @param pItem the item to commit
         * @return have we reached the end of the batch?
         */
        private boolean commitItem(final PrometheusDataItem pItem) {
            /* Access the underlying element */
            final PrometheusDataItem myBase = pItem.getBase();

            /* If we are handling deletions */
            if (theState == MetisDataState.DELETED) {
                /* Unlink the underlying item */
                myBase.unLink();

                /* Remove any registration */
                myBase.deRegister();

                /* else we are handling new/changed items */
            } else {
                /* Clear the history */
                myBase.clearHistory();
            }

            /* Mark this item as clean */
            pItem.clearHistory();

            /* If we have to worry about batch space */
            /* Adjust batch and note if we are finished */
            return theCapacity > 0
                    && --theItems == 0;
        }
    }
}
