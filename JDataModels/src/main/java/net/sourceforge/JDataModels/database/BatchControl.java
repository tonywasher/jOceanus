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
package net.sourceforge.JDataModels.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataState;

/**
 * Batch control class. This controls updating data lists after the commit of the batch.
 */
public class BatchControl {
    /**
     * Default batch size for updates.
     */
    protected static final int DEF_BATCH_SIZE = 50;

    /**
     * Batch size for updates.
     */
    private int theBatchSize = DEF_BATCH_SIZE;

    /**
     * Capacity of Batch Control (0=Unlimited).
     */
    private int theCapacity = theBatchSize;

    /**
     * Number of items in this batch.
     */
    private int theItems = 0;

    /**
     * The List of tables associated with this batch.
     */
    private List<BatchTable> theList = null;

    /**
     * The Currently active Database table.
     */
    private DatabaseTable<?> theCurrTable = null;

    /**
     * The Currently active Mode.
     */
    private DataState theCurrMode = null;

    /**
     * Is the current table in use.
     */
    private boolean isTableActive = false;

    /**
     * Constructor.
     * @param pBatchSize the batch size
     */
    protected BatchControl(final Integer pBatchSize) {
        /* Create the batch table list */
        theList = new ArrayList<BatchTable>();

        /* Store batchSize and capacity */
        theBatchSize = pBatchSize;
        theCapacity = pBatchSize;
    }

    /**
     * Is the batch full.
     * @return true/false is the batch full
     */
    protected boolean isFull() {
        return (theCapacity != 0) && (theItems >= theCapacity);
    }

    /**
     * Is the batch active.
     * @return true/false is the batch active
     */
    protected boolean isActive() {
        return (theItems >= 0);
    }

    /**
     * Set the currently active state.
     * @param pTable the Table being operated on
     * @param pMode the Mode that is in operation
     */
    protected void setCurrentTable(final DatabaseTable<?> pTable,
                                   final DataState pMode) {
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
            BatchTable myTable = new BatchTable();

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
        Iterator<BatchTable> myIterator = theList.iterator();

        /* Loop through the items */
        while (myIterator.hasNext()) {
            /* Access the next entry */
            BatchTable myTable = myIterator.next();

            /* Commit batch items in the table */
            myTable.commitBatch();
        }

        /* Clear the list */
        theList.clear();
        isTableActive = false;
        theItems = 0;
    }

    /**
     * Table step.
     */
    private final class BatchTable {
        /**
         * The table that is being controlled.
         */
        private final DatabaseTable<?> theTable;

        /**
         * The State of the table.
         */
        private final DataState theState;

        /**
         * Constructor.
         */
        private BatchTable() {
            /* Store the details */
            theTable = theCurrTable;
            theState = theCurrMode;
        }

        /**
         * Mark a update in the table as committed.
         */
        private void commitBatch() {
            /* Access the iterator */
            Iterator<?> myIterator = theTable.getList().iterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                DataItem myCurr = (DataItem) myIterator.next();

                /* Ignore items that are not this type */
                if (myCurr.getState() != theState) {
                    continue;
                }

                /* Access the underlying element */
                DataItem myBase = myCurr.getBase();

                /* If we are handling deletions */
                if (theState == DataState.DELETED) {
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
                myCurr.clearHistory();

                /* If we have to worry about batch space */
                /* Adjust batch and break if we are finished */
                if ((theCapacity > 0) && (--theItems == 0)) {
                    break;
                }
            }
        }
    }
}
