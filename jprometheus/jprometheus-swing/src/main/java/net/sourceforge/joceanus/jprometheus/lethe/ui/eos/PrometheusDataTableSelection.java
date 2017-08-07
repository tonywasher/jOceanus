/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.ui.eos;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;

/**
 * Data Table selection class.
 * @param <T> the data type
 * @param <E> the data type enum class
 */
public class PrometheusDataTableSelection<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>> {
    /**
     * The data table.
     */
    private final PrometheusDataTable<T, E> theDataTable;

    /**
     * The table.
     */
    private final JTable theTable;

    /**
     * The table model.
     */
    private final PrometheusDataTableModel<T, E> theTableModel;

    /**
     * The List Selection Model.
     */
    private final ListSelectionModel theSelectionModel;

    /**
     * The Item Panel.
     */
    private final PrometheusDataItemPanel<T, ?, E> theItemPanel;

    /**
     * Constructor.
     * @param pTable the table
     * @param pPanel the item panel
     */
    public PrometheusDataTableSelection(final PrometheusDataTable<T, E> pTable,
                                        final PrometheusDataItemPanel<T, ?, E> pPanel) {
        /* Store parameters */
        theDataTable = pTable;
        theTable = pTable.getTable();
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

        /* Recover the selected item */
        recoverSelectedItem();

        /* Set default selection */
        theDataTable.selectRowWithScroll(defaultSelectIndex());
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
            /* Recover the selected item */
            recoverSelectedItem();
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
            /* Recover the selected item */
            recoverSelectedItem();
        }
    }

    /**
     * recover selected item.
     */
    private void recoverSelectedItem() {
        /* Obtain the model index of the selected item */
        DataList<T, E> myList = theDataTable.getList();
        T myCurr = theItemPanel.getSelectedItem();
        int iIndex = myCurr == null
                                    ? -1
                                    : myList.indexOf(myCurr);

        /* If we have an active item */
        if (iIndex != -1) {
            /* Determine whether we have a header */
            T myFirst = theTableModel.getItemAtIndex(0);
            if (myFirst.isHeader()) {
                iIndex++;
            }

            /* Convert index to view */
            iIndex = theTable.convertRowIndexToView(iIndex);
        }

        /* Reset no selection to default selection */
        if (iIndex == -1) {
            iIndex = defaultSelectIndex();
        }

        /* Set selection */
        theDataTable.selectRowWithScroll(iIndex);
    }

    /**
     * obtain the default selection index.
     * @return the default selection
     */
    private int defaultSelectIndex() {
        /* Access the number of viewable rows */
        int iNumRows = theTableModel.getViewRowCount();

        /* If we do not have any rows, say so */
        if (iNumRows == 0) {
            return -1;
        }

        /* Access first row */
        int iIndex = theTable.convertRowIndexToModel(0);
        T myFirst = theTableModel.getItemAtIndex(iIndex);
        return myFirst.isHeader()
                                  ? iNumRows > 1
                                                 ? 1
                                                 : -1
                                  : 0;
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
            /* Access the item */
            T myItem = theTableModel.getItemAtIndex(iIndex);

            /* If this is a header record */
            if (myItem.isHeader()) {
                /* Not allowed to select header */
                myItem = null;
            }

            /* Declare item to the panel */
            theItemPanel.setItem(myItem);

            /* Check whether this item is EndOfList */
            if (iIndex == 0 || iIndex == iRowCount - 1) {
                theItemPanel.setEdgeOfList();
            }

            /* else clear the item panel */
        } else {
            theItemPanel.setEditable(false);
            theItemPanel.setItem(null);
            theDataTable.notifyChanges();
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
