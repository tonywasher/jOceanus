/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.ui.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Array;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.joceanus.jmetis.data.MetisDataEditState;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusTableItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusTableItem.PrometheusTableList;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusRowColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableModel.PrometheusRowTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTableSorter;

/**
 * Template class to provide a table to handle a data type.
 * @param <T> the data type.
 * @param <E> the data type enum class
 */
public abstract class PrometheusDataTable<T extends PrometheusTableItem & Comparable<? super T>, E extends Enum<E>>
        implements TethysEventProvider<PrometheusDataEvent>, TethysNode<JComponent> {
    /**
     * Panel height.
     */
    protected static final int HEIGHT_PANEL = 200;

    /**
     * Panel width.
     */
    public static final int WIDTH_PANEL = 1100;

    /**
     * Default row height.
     */
    protected static final int ROW_HEIGHT = 16;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * FieldManager.
     */
    private MetisSwingFieldManager theFieldMgr;

    /**
     * Data Table Model.
     */
    private PrometheusDataTableModel<T, E> theModel;

    /**
     * The Row Header Model.
     */
    private PrometheusRowTableModel<E> theRowHdrModel;

    /**
     * The Data List associated with the table.
     */
    private PrometheusTableList<T> theList;

    /**
     * The UpdateSet associated with the table.
     */
    private UpdateSet<E> theUpdateSet;

    /**
     * The Class associated with the table.
     */
    private Class<T> theClass;

    /**
     * The Scroll Pane.
     */
    private JScrollPane theScroll;

    /**
     * The Table.
     */
    private final JTable theTable;

    /**
     * Is Enabled?
     */
    private boolean isEnabled;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public PrometheusDataTable(final TethysSwingGuiFactory pFactory) {
        /* Create the Id */
        theId = pFactory.getNextId();

        /* Store parameters */
        theTable = new JTable();
        theRowHdrModel = new PrometheusRowTableModel<>(this);

        /* Set the selection mode */
        theTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Cascade action event.
     * @param pEvent the event to cascade
     */
    protected void cascadeEvent(final TethysEvent<PrometheusDataEvent> pEvent) {
        /* Fire the event */
        theEventManager.cascadeEvent(pEvent);
    }

    /**
     * fire a state changed event.
     */
    protected void fireStateChanged() {
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Does the table have a header row?
     * @return true/false
     */
    public boolean hasHeader() {
        return false;
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public MetisSwingFieldManager getFieldMgr() {
        return theFieldMgr;
    }

    /**
     * Does the table have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return (theUpdateSet != null) && theUpdateSet.hasUpdates();
    }

    /**
     * Does the table have the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        return hasUpdates();
    }

    /**
     * Does the table have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return (theUpdateSet != null) && theUpdateSet.hasErrors();
    }

    /**
     * Is the table active?
     * @return true/false
     */
    public boolean isActive() {
        return isEnabled;
    }

    /**
     * Get the table model.
     * @return the model
     */
    public PrometheusDataTableModel<T, E> getTableModel() {
        return theModel;
    }

    /**
     * Get the data list.
     * @return the data list
     */
    public List<T> getList() {
        return theList.getUnderlyingList();
    }

    /**
     * Get the data class.
     * @return the data class
     */
    protected Class<T> getDataClass() {
        return theClass;
    }

    @Override
    public JComponent getNode() {
        return theScroll;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theScroll.setEnabled(pEnabled);
        theTable.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theScroll.setVisible(pVisible);
    }

    /**
     * Obtain the table.
     * @return the table
     */
    protected JTable getTable() {
        return theTable;
    }

    /**
     * Set the active flag.
     * @param isActive true/false
     */
    public void setActive(final boolean isActive) {
        isEnabled = isActive;
    }

    /**
     * Set the error details.
     * @param pError the error
     */
    protected abstract void setError(OceanusException pError);

    /**
     * Increment version.
     */
    protected void incrementVersion() {
        theUpdateSet.incrementVersion();
    }

    /**
     * Notify that there have been changes to this list.
     */
    protected void notifyChanges() {
        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Get the RowTableModel.
     * @return the model
     */
    protected PrometheusRowTableModel<E> getRowTableModel() {
        return theRowHdrModel;
    }

    /**
     * Set the table model.
     * @param pModel the table model
     */
    public void setModel(final PrometheusDataTableModel<T, E> pModel) {
        /* Declare to the super class */
        theTable.setModel(pModel);

        /* Record the model */
        theModel = pModel;

        /* Create the sorter and record it */
        final TethysSwingTableSorter<T> mySorter = new TethysSwingTableSorter<>(theModel);
        mySorter.setComparator((l, r) -> l.compareTo(r));
        theModel.registerSorter(mySorter);
        theTable.setRowSorter(mySorter);

        /* Set up for zebra stripes */
        theTable.setRowMargin(0);
        theTable.setShowGrid(false);

        /* Set the row height */
        theTable.setRowHeight(ROW_HEIGHT);

        /* Create a row Header table */
        final JTable myRowHdrTable = new JTable(theRowHdrModel, new PrometheusRowColumnModel<E>(this));
        myRowHdrTable.setBackground(theTable.getTableHeader().getBackground());
        myRowHdrTable.setColumnSelectionAllowed(false);
        myRowHdrTable.setCellSelectionEnabled(false);

        /* Set the selection model */
        myRowHdrTable.setSelectionModel(theTable.getSelectionModel());

        /* Create a new Scroll Pane and add this table to it */
        theScroll = new JScrollPane();
        theScroll.setViewportView(theTable);

        /* Add as the row header */
        theScroll.setRowHeaderView(myRowHdrTable);
        theScroll.getRowHeader().setPreferredSize(new Dimension(PrometheusDataTableColumnModel.WIDTH_ROWHDR, HEIGHT_PANEL));
        theScroll.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, myRowHdrTable.getTableHeader());
    }

    /**
     * Obtain the edit state of the table.
     * @return the edit state
     */
    public MetisDataEditState getEditState() {
        return theUpdateSet == null
                                    ? MetisDataEditState.CLEAN
                                    : theUpdateSet.getEditState();
    }

    /**
     * Set the field manager for the table.
     * @param pFieldMgr the field manager
     */
    protected void setFieldMgr(final MetisSwingFieldManager pFieldMgr) {
        theFieldMgr = pFieldMgr;
    }

    /**
     * Set the update Set for the table.
     * @param pUpdateSet the update set
     */
    protected void setUpdateSet(final UpdateSet<E> pUpdateSet) {
        theUpdateSet = pUpdateSet;
    }

    /**
     * Set the list for the table.
     * @param pList the list
     */
    protected void setList(final PrometheusTableList<T> pList) {
        final int myZeroRow = hasHeader()
                                          ? 1
                                          : 0;

        /* Store list and select correct mode */
        theList = pList;
        if (pList != null) {
            theClass = pList.getBaseClass();
        }

        /* Redraw the table and row headers */
        theModel.fireNewDataEvents();

        /* If we have elements then set the selection to the first item */
        theTable.clearSelection();
        if (theModel.getRowCount() > myZeroRow) {
            selectRowWithScroll(myZeroRow);
        }
    }

    /**
     * Is the table locked?
     * @return true/false
     */
    public boolean isLocked() {
        /* Store list and select correct mode */
        return (theList == null) || theList.isLocked();
    }

    /**
     * Cancel any editing that is occurring.
     */
    public void cancelEditing() {
        /* Cancel any editing */
        if (theTable.isEditing()) {
            theTable.getCellEditor().cancelCellEditing();
        }
    }

    /**
     * Ensure that a row is visible.
     * @param row the row to view
     */
    protected void scrollRowToView(final int row) {
        /* Shift display to row */
        final Rectangle rect = theTable.getCellRect(row, 0, true);
        final JViewport viewport = (JViewport) theTable.getParent();
        final Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);
    }

    /**
     * Select a row and ensure that it is visible.
     * @param row the row to select
     */
    protected void selectRowWithScroll(final int row) {
        /* Shift display to row */
        scrollRowToView(row);

        /* clear existing selection and select the row */
        selectRow(row);
    }

    /**
     * select an explicit row.
     * @param row the row to select
     */
    protected void selectRow(final int row) {
        /* clear existing selection and select the row */
        theTable.clearSelection();
        theTable.changeSelection(row, 0, false, false);
        theTable.requestFocusInWindow();
    }

    /**
     * Get an array of the selected rows.
     * @return array of selected rows
     */
    protected T[] cacheSelectedRows() {
        /* Determine the selected rows */
        final int[] mySelected = theTable.getRowCount() == 0
                                                             ? new int[0]
                                                             : theTable.getSelectedRows();

        /* Create a row array relating to the selections */
        @SuppressWarnings("unchecked")
        final T[] myRows = (T[]) Array.newInstance(getDataClass(), mySelected.length);

        /* Loop through the selection indices */
        for (int i = 0; i < mySelected.length; i++) {
            /* Access the index and adjust for header */
            final int myIndex = theTable.convertRowIndexToModel(mySelected[i]);

            /* Store the row */
            myRows[i] = theList.get(myIndex);
        }

        /* Return the rows */
        return myRows;
    }

    /**
     * Perform additional updates after change.
     */
    protected void updateAfterChange() {
    }

    /**
     * Get showAll value.
     * @return true/false
     */
    public boolean showAll() {
        return theModel.showAll();
    }

    /**
     * Set the show deleted indication.
     * @param pShowAll the new setting
     */
    protected void setShowAll(final boolean pShowAll) {
        /* If we are changing the value */
        if (theModel.showAll() != pShowAll) {
            /* Cancel any editing */
            if (theTable.isEditing()) {
                theTable.getCellEditor().cancelCellEditing();
            }

            /* Access a cache of the selected rows */
            final T[] myRows = cacheSelectedRows();
            theTable.clearSelection();

            /* Store the new status */
            theModel.setShowAll(pShowAll);

            /* Loop through the selected rows */
            for (T myRow : myRows) {
                /* Ignore null/deleted entries */
                if ((myRow == null) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access the row # and adjust for view */
                final int myRowNo = theTable.convertRowIndexToView(theList.indexOf(myRow));

                /* Select the row */
                if (myRowNo != -1) {
                    theTable.addRowSelectionInterval(myRowNo, myRowNo);
                }
            }
        }
    }

    /**
     * Check whether showAll should be disabled.
     * @param pRow the row
     * @return disable show all
     */
    protected boolean disableShowAll(final T pRow) {
        /* Is it deleted */
        return pRow.isDeleted();
    }

    /**
     * Obtain next item in view.
     * @param pItem the original item
     * @return the next item (or null).
     */
    public T getNextItem(final T pItem) {
        /* Obtain the index of the item */
        int myIndex = theList.indexOf(pItem);

        /* Obtain the view index and return null if invalid */
        int myViewIndex = theTable.convertRowIndexToView(myIndex);
        if (myViewIndex == -1) {
            return null;
        }

        /* Increment the value and check within range */
        final int myCount = theTable.getRowCount();
        if (++myViewIndex >= myCount) {
            return null;
        }

        /* Convert back to model index and return the item */
        myIndex = theTable.convertRowIndexToModel(myViewIndex);
        return theList.get(myIndex);
    }

    /**
     * Obtain previous item in view.
     * @param pItem the original item
     * @return the previous item (or null).
     */
    public T getPrevItem(final T pItem) {
        /* Obtain the index of the item */
        int myIndex = theList.indexOf(pItem);

        /* Obtain the view index and return null if invalid or first item */
        int myViewIndex = theTable.convertRowIndexToView(myIndex);
        if (myViewIndex <= 0) {
            return null;
        }

        /* Decrement the value */
        myViewIndex--;

        /* Convert back to model index and return the item */
        myIndex = theTable.convertRowIndexToModel(myViewIndex);
        return theList.get(myIndex);
    }
}
