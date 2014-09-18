/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.ui;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;

/**
 * Data Table selection class.
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public class JDataTableSelection<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>> {
    /**
     * The table.
     */
    private final JDataTable<T, E> theTable;

    /**
     * The table model.
     */
    private final JDataTableModel<T, E> theTableModel;

    /**
     * The List Selection Model.
     */
    private final ListSelectionModel theSelectionModel;

    /**
     * The Item Panel.
     */
    private final DataItemPanel<T, E> theItemPanel;

    /**
     * Constructor.
     * @param pTable the table
     * @param pPanel the item panel
     */
    public JDataTableSelection(final JDataTable<T, E> pTable,
                               final DataItemPanel<T, E> pPanel) {
        /* Store parameters */
        theTable = pTable;
        theItemPanel = pPanel;

        /* Derive further parameters */
        theTableModel = pTable.getTableModel();
        theSelectionModel = theTable.getSelectionModel();

        /* Add selection listener */
        theSelectionModel.addListSelectionListener(new SelectionListener());
    }

    /**
     * Handle filter change.
     */
    public void handleNewFilter() {
        /* Notify new data */
        theTableModel.fireNewDataEvents();

        /* Set default selection */
        theTable.selectRowWithScroll(0);
    }

    /**
     * Handle rewind.
     */
    public void handleReWind() {
        /* Notify new data */
        theTableModel.fireNewDataEvents();

        /* If we have no data, but we have data to view */
        if ((theTable.getSelectedRow() == -1)
            && (theTableModel.getViewRowCount() > 0)) {
            /* Set default selection */
            theTable.selectRowWithScroll(0);
        }
    }

    /**
     * Handle edit session transition.
     */
    public void handleEditTransition() {
        /* Notify new data */
        theTableModel.fireNewDataEvents();

        /* If we have no data, but we have data to view */
        if ((theTable.getSelectedRow() == -1)
            && (theTableModel.getViewRowCount() > 0)) {
            /* Obtain the model index of the selected item */
            DataList<T, E> myList = theTable.getList();
            T myCurr = theItemPanel.getSelectedItem();
            int iIndex = myCurr == null
                                       ? -1
                                       : myList.indexOf(myCurr);

            /* Convert model index to view */
            if (iIndex != -1) {
                iIndex = theTable.convertRowIndexToView(iIndex);
            }

            /* Reset no selection to default selection */
            if (iIndex == -1) {
                iIndex = 0;
            }

            /* Set selection */
            theTable.selectRowWithScroll(iIndex);
        }
    }

    /**
     * Handle selection change.
     */
    public void handleSelectionChange() {
        /* Access selection model */
        int iIndex = -1;
        int iRowCount = theTableModel.getViewRowCount();
        if (!theSelectionModel.isSelectionEmpty()) {
            /* Determine the selected item */
            iIndex = theSelectionModel.getMinSelectionIndex();

            /* perform a health check */
            if (iRowCount == 0) {
                iIndex = -1;
            }

            /* Convert to the model */
            if (iIndex != -1) {
                iIndex = theTable.convertRowIndexToModel(iIndex);
            }
        }

        /* If we have a selected row */
        if (iIndex != -1) {
            /* Select the correct item */
            DataList<T, E> myList = theTable.getList();
            T myCategory = myList.get(iIndex);
            theItemPanel.setItem(myCategory);

            /* Check whether this item is EndOfList */
            if (iIndex == 0 || iIndex == iRowCount - 1) {
                theItemPanel.setEdgeOfList();
            }

            /* else clear the item panel */
        } else {
            theItemPanel.setEditable(false);
            theItemPanel.setItem(null);
            theTable.notifyChanges();
        }
    }

    /**
     * Listener class.
     */
    private final class SelectionListener
            implements ListSelectionListener {
        @Override
        public void valueChanged(final ListSelectionEvent pEvent) {
            /* If we have finished selecting */
            if (!pEvent.getValueIsAdjusting()) {
                /* Handle the selection change */
                handleSelectionChange();
            }
        }
    }
}
