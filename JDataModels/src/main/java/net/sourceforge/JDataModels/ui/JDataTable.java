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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.JDataManager.EditState;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.ui.JDataTableColumn.RowColumnModel;
import net.sourceforge.JDataModels.ui.JDataTableModel.RowTableModel;
import net.sourceforge.JDataModels.views.UpdateSet;
import net.sourceforge.JEventManager.JEventTable;
import net.sourceforge.JFieldSet.Editor.ComboBoxSelector;
import net.sourceforge.JFieldSet.RenderManager;
import net.sourceforge.JTableFilter.TableFilter;

/**
 * Template class to provide a table to handle a data type.
 * @author Tony Washer
 * @param <T> the data type.
 */
public abstract class JDataTable<T extends DataItem & Comparable<? super T>> extends JEventTable implements
        ComboBoxSelector {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1258025191244933784L;

    /**
     * Row Header Width.
     */
    protected static final int ROWHDR_WIDTH = 30;

    /**
     * Row Header Height.
     */
    private static final int ROWHDR_HEIGHT = 200;

    /**
     * The Row Header Table.
     */
    private JTable theRowHdrTable = null;

    /**
     * RenderManager.
     */
    private RenderManager theRenderMgr = null;

    /**
     * Data Table Model.
     */
    private JDataTableModel<T> theModel = null;

    /**
     * The Row Header Model.
     */
    private RowTableModel theRowHdrModel = null;

    /**
     * The Data List associated with the table.
     */
    private DataList<T> theList = null;

    /**
     * The UpdateSet associated with the table.
     */
    private UpdateSet theUpdateSet = null;

    /**
     * The Class associated with the table.
     */
    private Class<T> theClass = null;

    /**
     * The Scroll Pane.
     */
    private JScrollPane theScroll = null;

    /**
     * Is Enabled?
     */
    private boolean isEnabled = false;

    /**
     * Does the table have a header row?
     * @return true/false
     */
    public boolean hasHeader() {
        return false;
    }

    /**
     * Obtain the render manager.
     * @return the render manager
     */
    public RenderManager getRenderMgr() {
        return theRenderMgr;
    }

    /**
     * Does the table have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return (theUpdateSet != null) && theUpdateSet.hasUpdates();
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
    public JDataTableModel<T> getTableModel() {
        return theModel;
    }

    /**
     * Get the data list.
     * @return the data list
     */
    public DataList<T> getList() {
        return theList;
    }

    /**
     * Get the data class.
     * @return the data class
     */
    protected Class<T> getDataClass() {
        return theClass;
    }

    /**
     * Get the scroll pane.
     * @return the scroll pane
     */
    public JScrollPane getScrollPane() {
        return theScroll;
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
    protected abstract void setError(final JDataException pError);

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
        fireStateChanged();
    }

    @Override
    public JComboBox getComboBox(final int pRowIndex,
                                 final int pColIndex) {
        return null;
    }

    /**
     * Get the RowTableModel.
     * @return the model
     */
    protected RowTableModel getRowTableModel() {
        return theRowHdrModel;
    }

    /**
     * Constructor.
     */
    public JDataTable() {
        /* Store parameters */
        theRowHdrModel = new RowTableModel(this);

        /* Set the selection mode */
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * Set the table model.
     * @param pModel the table model
     */
    public void setModel(final JDataTableModel<T> pModel) {
        /* Declare to the super class */
        super.setModel(pModel);

        /* Record the model */
        theModel = pModel;

        /* Create the filter and record it */
        TableFilter<T> myFilter = new TableFilter<T>(theModel, true);
        theModel.registerFilter(myFilter);
        setRowSorter(myFilter);

        /* Create a row Header table */
        theRowHdrTable = new JTable(theRowHdrModel, new RowColumnModel(this));
        theRowHdrTable.setBackground(getTableHeader().getBackground());
        theRowHdrTable.setColumnSelectionAllowed(false);
        theRowHdrTable.setCellSelectionEnabled(false);

        /* Set the selection model */
        theRowHdrTable.setSelectionModel(getSelectionModel());

        /* Create a new Scroll Pane and add this table to it */
        theScroll = new JScrollPane();
        theScroll.setViewportView(this);

        /* Add as the row header */
        theScroll.setRowHeaderView(theRowHdrTable);
        theScroll.getRowHeader().setPreferredSize(new Dimension(ROWHDR_WIDTH, ROWHDR_HEIGHT));
        theScroll.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, theRowHdrTable.getTableHeader());
    }

    @Override
    public void addMouseListener(final MouseListener pListener) {
        /* Pass call on */
        super.addMouseListener(pListener);

        /* Listen for the row header table as well */
        if (theRowHdrTable != null) {
            theRowHdrTable.addMouseListener(pListener);
        }
    }

    /**
     * Obtain the edit state of the table.
     * @return the edit state
     */
    public EditState getEditState() {
        return (theUpdateSet == null) ? EditState.CLEAN : theUpdateSet.getEditState();
    }

    /**
     * Set the render manager for the table.
     * @param pRenderMgr the render manager
     */
    protected void setRenderMgr(final RenderManager pRenderMgr) {
        theRenderMgr = pRenderMgr;
    }

    /**
     * Set the update Set for the table.
     * @param pUpdateSet the update set
     */
    protected void setUpdateSet(final UpdateSet pUpdateSet) {
        theUpdateSet = pUpdateSet;
    }

    /**
     * Set the list for the table.
     * @param pList the list
     */
    protected void setList(final DataList<T> pList) {
        int myZeroRow = hasHeader() ? 1 : 0;

        /* Store list and select correct mode */
        theList = pList;
        if (pList != null) {
            theClass = pList.getBaseClass();
        }

        /* Redraw the table and row headers */
        theModel.fireNewDataEvents();

        /* If we have elements then set the selection to the first item */
        clearSelection();
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
        return ((theList == null) || theList.isLocked());
    }

    /**
     * Cancel any editing that is occurring.
     */
    public void cancelEditing() {
        /* Cancel any editing */
        if (isEditing()) {
            cellEditor.cancelCellEditing();
        }
    }

    /**
     * Select a row and ensure that it is visible.
     * @param row the row to select
     */
    protected void selectRowWithScroll(final int row) {
        /* Shift display to line */
        Rectangle rect = getCellRect(row, 0, true);
        JViewport viewport = (JViewport) getParent();
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);

        /* clear existing selection and select the row */
        selectRow(row);
    }

    /**
     * select an explicit row.
     * @param row the row to select
     */
    protected void selectRow(final int row) {
        /* clear existing selection and select the row */
        clearSelection();
        changeSelection(row, 0, false, false);
        requestFocusInWindow();
    }

    /**
     * Get an array of the selected rows.
     * @return array of selected rows
     */
    protected T[] cacheSelectedRows() {
        /* Determine the selected rows */
        int[] mySelected = getSelectedRows();

        /* Create a row array relating to the selections */
        @SuppressWarnings("unchecked")
        T[] myRows = (T[]) Array.newInstance(getDataClass(), mySelected.length);

        /* Loop through the selection indices */
        for (int i = 0, j = 0; i < mySelected.length; i++) {
            /* Access the index and adjust for header */
            int myIndex = convertRowIndexToModel(mySelected[i]);

            /* Store the row */
            myRows[j] = theList.get(myIndex);
            j++;
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
            if (isEditing()) {
                cellEditor.cancelCellEditing();
            }

            /* Access a cache of the selected rows */
            T[] myRows = cacheSelectedRows();
            clearSelection();

            /* Store the new status */
            theModel.setShowAll(pShowAll);

            /* Loop through the selected rows */
            for (T myRow : myRows) {
                /* Ignore null/deleted entries */
                if ((myRow == null) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access the row # and adjust for view */
                int myRowNo = convertRowIndexToView(myRow.indexOf());

                /* Select the row */
                addRowSelectionInterval(myRowNo, myRowNo);
            }
        }
    }

    /**
     * Check whether insert is allowed for this table.
     * @return insert allowed (true/false)
     */
    protected boolean insertAllowed() {
        return true;
    }

    /**
     * Insert an item.
     */
    protected void insertRow() {
        /* Create the new Item */
        T myItem = theList.addNewItem();
        myItem.setNewVersion();

        /* Determine the row # allowing for header */
        int myRowNo = myItem.indexOf();

        /* Validate the new item */
        myItem.validate();

        /* Notify of the insertion of the row */
        theModel.fireInsertRowEvents(myRowNo);

        /* Shift display to line */
        selectRowWithScroll(convertRowIndexToView(myRowNo));
        incrementVersion();
    }

    /**
     * Check whether a row is deletable.
     * @param pRow the row
     * @return is the row deletable
     */
    protected boolean isRowDeletable(final T pRow) {
        /* Not deletable if already deleted */
        return (!pRow.isDeleted());
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
     * Delete the selected items.
     */
    protected void deleteRows() {
        /* Access the selected rows */
        T[] myRows = cacheSelectedRows();

        /* Loop through the selected rows */
        for (int i = 0; i < myRows.length; i++) {
            /* Access the row */
            T myRow = myRows[i];

            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Ignore non-Deletable entries */
            if (!isRowDeletable(myRow)) {
                continue;
            }

            /* Access the row # and adjust for header */
            int myRowNo = myRow.indexOf();

            /* Mark the row as deleted */
            myRow.setDeleted(true);

            /* Notify of the update of the row */
            theModel.fireUpdateRowEvents(myRowNo);
        }

        /* Allow adjustments and increment version */
        updateAfterChange();
        incrementVersion();
    }

    /**
     * Check whether a row is duplicatable.
     * @param pRow the row
     * @return is the row duplicatable
     */
    protected boolean isRowDuplicatable(final T pRow) {
        /* Not duplicatable if already deleted */
        return (!pRow.isDeleted());
    }

    /**
     * Duplicate the selected items.
     */
    protected void duplicateRows() {
        /* Access the selected rows */
        T[] myRows = cacheSelectedRows();

        /* Loop through the selected rows */
        for (int i = 0; i < myRows.length; i++) {
            /* Access the row */
            T myRow = myRows[i];

            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Ignore non-Duplicatable entries */
            if (!isRowDuplicatable(myRow)) {
                continue;
            }

            /* Create the new Item */
            T myItem = theList.addNewItem(myRow);

            /* Determine the row # */
            int myRowNo = myItem.indexOf();

            /* Validate the new item */
            myItem.validate();

            /* Notify of the insertion of the row */
            theModel.fireInsertRowEvents(myRowNo);
        }

        /* Allow adjustments and increment version */
        updateAfterChange();
        incrementVersion();
    }

    /**
     * Check whether a row is recoverable.
     * @param pRow the row
     * @return is the row recoverable
     */
    protected boolean isRowRecoverable(final T pRow) {
        /* Must be deleted to be recoverable */
        return pRow.isDeleted();
    }

    /**
     * Recover the selected items.
     */
    protected void recoverRows() {
        /* Access the selected rows */
        T[] myRows = cacheSelectedRows();

        /* Loop through the selected rows */
        for (int i = 0; i < myRows.length; i++) {
            /* Access the row */
            T myRow = myRows[i];

            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Ignore non-Recoverable entries */
            if (!isRowRecoverable(myRow)) {
                continue;
            }

            /* Access the row # and adjust for header */
            int myRowNo = myRow.indexOf();

            /* Mark the row as recovered */
            myRow.setDeleted(false);
            myRow.clearErrors();
            myRow.validate();

            /* Notify of the update of the row */
            theModel.fireUpdateRowEvents(myRowNo);
        }

        /* Allow adjustments and increment version */
        updateAfterChange();
        incrementVersion();
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
        int myViewIndex = convertRowIndexToView(myIndex);
        if (myViewIndex == -1) {
            return null;
        }

        /* Increment the value and check within range */
        int myCount = getRowCount();
        if (++myViewIndex >= myCount) {
            return null;
        }

        /* Convert back to model index and return the item */
        myIndex = convertRowIndexToModel(myViewIndex);
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
        int myViewIndex = convertRowIndexToView(myIndex);
        if (myViewIndex <= 0) {
            return null;
        }

        /* Decrement the value */
        myViewIndex--;

        /* Convert back to model index and return the item */
        myIndex = convertRowIndexToModel(myViewIndex);
        return theList.get(myIndex);
    }
}
