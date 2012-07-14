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
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import net.sourceforge.JDataManager.EventManager;
import net.sourceforge.JDataManager.EventManager.ActionDetailEvent;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.EditState;
import net.sourceforge.JDataModels.ui.RenderManager.PopulateRenderData;
import net.sourceforge.JDataModels.ui.RenderManager.RenderData;
import net.sourceforge.JDataModels.ui.Renderer.RowCell;
import net.sourceforge.JDataModels.views.UpdateSet;

/**
 * Template class to provide a table to handle a data type.
 * @author Tony Washer
 * @param <T> the data type.
 */
public abstract class DataTable<T extends DataItem & Comparable<T>> extends JTable {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1258025191244933784L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataTable.class.getName());

    /**
     * Table header.
     */
    private static final String TITLE_ROW = NLS_BUNDLE.getString("TitleRow");

    /**
     * Row Header Width.
     */
    private static final int ROWHDR_WIDTH = 30;

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
    private DataTableModel theModel = null;

    /**
     * The Row Header Model.
     */
    private RowTableModel theRowHdrModel = null;

    /**
     * The Data List associated with the table.
     */
    private DataList<?, T> theList = null;

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
     * Show deleted?
     */
    private boolean doShowDel = false;

    /**
     * Is Enabled?
     */
    private boolean isEnabled = false;

    /**
     * The Event Manager.
     */
    private final transient EventManager theManager = new EventManager(this);

    /**
     * Add change Listener to list.
     * @param pListener the listener to add
     */
    public void addChangeListener(final ChangeListener pListener) {
        /* Add the change listener */
        theManager.addChangeListener(pListener);
    }

    /**
     * Add action Listener to list.
     * @param pListener the listener to add
     */
    public void addActionListener(final ActionListener pListener) {
        /* Add the action listener */
        theManager.addActionListener(pListener);
    }

    /**
     * Remove Change Listener.
     * @param pListener the listener to remove
     */
    public void removeChangeListener(final ChangeListener pListener) {
        /* Remove the change listener */
        theManager.removeChangeListener(pListener);
    }

    /**
     * Remove Action Listener.
     * @param pListener the listener to remove
     */
    public void removeActionListener(final ActionListener pListener) {
        /* Remove the action listener */
        theManager.removeActionListener(pListener);
    }

    /**
     * Fire State Changed Event to all registered listeners.
     */
    protected void fireStateChanged() {
        /* Fire the standard event */
        theManager.fireStateChanged();
    }

    /**
     * Fire Action Performed Event to all registered listeners.
     * @param pCommand the action command
     */
    protected void fireActionPerformed(final String pCommand) {
        /* Fire standard action performed event */
        theManager.fireActionPerformed(pCommand);
    }

    /**
     * Fire Action Detail Event to all registered listeners.
     * @param pSubId the SubId of the event.
     * @param pDetails the action details
     */
    protected void fireActionEvent(final int pSubId,
                                   final Object pDetails) {
        /* Fire action detail event */
        theManager.fireActionEvent(this, pSubId, pDetails);
    }

    /**
     * Cascade action event.
     * @param pEvent the event to cascade
     */
    protected void cascadeActionEvent(final ActionDetailEvent pEvent) {
        /* Fire action detail event */
        theManager.cascadeActionEvent(this, pEvent);
    }

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
    public DataTableModel getTableModel() {
        return theModel;
    }

    /**
     * Get the data list.
     * @return the data list
     */
    public DataList<?, T> getList() {
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
     * Notify that there have been changes to this list.
     */
    protected void notifyChanges() {
        /* Notify listeners */
        fireStateChanged();
    }

    /**
     * Get the combo box for the item at row and column.
     * @param row the row
     * @param col the column
     * @return the combo box
     */
    public JComboBox getComboBox(final int row,
                                 final int col) {
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
    public DataTable() {
        /* Store parameters */
        theRowHdrModel = new RowTableModel(this);

        /* Set the selection mode */
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * Set the table model.
     * @param pModel the table model
     */
    public void setModel(final DataTableModel pModel) {
        /* Declare to the super class */
        super.setModel(pModel);

        /* Record the model */
        theModel = pModel;

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
    protected void setList(final DataList<?, T> pList) {
        int myZeroRow = hasHeader() ? 1 : 0;

        /* Store list and select correct mode */
        theList = pList;
        if (pList != null) {
            pList.setShowDeleted(doShowDel);
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
     * Extract the item at the given index.
     * @param uIndex the index
     * @return the item
     */
    public T extractItemAt(final int uIndex) {
        return ((theList == null) ? null : theList.get(uIndex));
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

    @Override
    public void valueChanged(final ListSelectionEvent evt) {
        super.valueChanged(evt);
        if (evt.getValueIsAdjusting()) {
            return;
        }
        notifyChanges();
    }

    /**
     * Select a row and ensure that it is visible.
     * @param row the row to select
     */
    protected void selectRowWithScroll(final int row) {
        Rectangle rect;
        Point pt;
        JViewport viewport;

        /* Shift display to line */
        rect = getCellRect(row, 0, true);
        viewport = (JViewport) getParent();
        pt = viewport.getViewPosition();
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
    protected DataItem[] cacheSelectedRows() {
        int[] mySelected;
        int myIndex;
        int i, j;

        /* Determine the selected rows */
        mySelected = getSelectedRows();

        /* Create a row array relating to the selections */
        DataItem[] myRows = new DataItem[mySelected.length];
        Arrays.fill(myRows, null);

        /* Loop through the selection indices */
        for (i = 0, j = 0; i < mySelected.length; i++) {
            /* Access the index and adjust for header */
            myIndex = mySelected[i];
            if (hasHeader()) {
                myIndex--;
            }
            if (myIndex < 0) {
                continue;
            }

            /* Store the row */
            myRows[j] = theList.get(myIndex);
            j++;
        }

        /* Return the rows */
        return myRows;
    }

    /**
     * Perform additional validation after change.
     */
    // protected void validateAfterChange() {
    // }

    /**
     * Set the show deleted indication.
     * @param pShowDel the new setting
     */
    protected void setShowDeleted(final boolean pShowDel) {
        DataItem[] myRows;
        int myRowNo;

        /* If we are changing the value */
        if (doShowDel != pShowDel) {
            /* Cancel any editing */
            if (isEditing()) {
                cellEditor.cancelCellEditing();
            }

            /* Access a cache of the selected rows */
            myRows = cacheSelectedRows();
            clearSelection();

            /* Store the new status */
            doShowDel = pShowDel;
            theList.setShowDeleted(doShowDel);

            /* Redraw the table */
            theModel.fireNewDataEvents();

            /* Loop through the selected rows */
            for (DataItem myRow : myRows) {
                /* Ignore null/deleted entries */
                if ((myRow == null) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access the row # and adjust for header */
                myRowNo = myRow.indexOf();
                if (hasHeader()) {
                    myRowNo++;
                }

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
        theUpdateSet.incrementVersion();

        /* Determine the row # allowing for header */
        int myRowNo = myItem.indexOf();
        if (hasHeader()) {
            myRowNo++;
        }

        /* Validate the new item */
        myItem.validate();

        /* Notify of the insertion of the row */
        theModel.fireInsertRowEvents(myRowNo);

        /* Shift display to line */
        selectRowWithScroll(myRowNo);
        theUpdateSet.incrementVersion();
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
     * Check whether we should hide deleted rows.
     * @return hide deleted rows (true/false)
     */
    protected boolean hideDeletedRows() {
        return !doShowDel;
    }

    /**
     * Check whether showDeleted should be disabled.
     * @param pRow the row
     * @return disable show deleted
     */
    protected boolean disableShowDeleted(final T pRow) {
        /* Is it deleted */
        return pRow.isDeleted();
    }

    /**
     * Delete the selected items.
     */
    protected void deleteRows() {
        /* Access the selected rows */
        DataItem[] myRows = cacheSelectedRows();
        boolean hideDeleted = hideDeletedRows();

        /* Loop through the selected rows */
        for (int i = 0; i < myRows.length; i++) {
            /* Access the row */
            T myRow = theClass.cast(myRows[i]);

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
            if (hasHeader()) {
                myRowNo++;
            }

            /* Mark the row as deleted */
            myRow.setDeleted(true);

            /* If we are showing deleted items */
            if (!hideDeleted) {
                /* Notify of the update of the row */
                theModel.fireUpdateRowEvents(myRowNo);

                /* else we are not showing deleted items */
            } else {
                /* Notify of the deletion of the row and remove from list */
                myRows[i] = null;
                theModel.fireDeleteRowEvents(myRowNo);
            }
        }

        /* Re-validate after change */
        theUpdateSet.incrementVersion();
        // validateAfterChange();
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
        DataItem[] myRows = cacheSelectedRows();

        /* Loop through the selected rows */
        for (int i = 0; i < myRows.length; i++) {
            /* Access the row */
            T myRow = theClass.cast(myRows[i]);

            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Ignore non-Duplicatable entries */
            if (!isRowDuplicatable(myRow)) {
                continue;
            }

            /* Access the row # and adjust for header */
            int myRowNo = myRow.indexOf();
            if (hasHeader()) {
                myRowNo++;
            }

            /* Create the new Item */
            T myItem = theList.addNewItem(myRow);

            /* Determine the row # allowing for header */
            myRowNo = myItem.indexOf();
            if (hasHeader()) {
                myRowNo++;
            }

            /* Validate the new item */
            myItem.validate();

            /* Notify of the insertion of the row */
            theModel.fireInsertRowEvents(myRowNo);
        }

        /* Re-validate after change */
        theUpdateSet.incrementVersion();
        // validateAfterChange();
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
        DataItem[] myRows = cacheSelectedRows();

        /* Loop through the selected rows */
        for (int i = 0; i < myRows.length; i++) {
            /* Access the row */
            T myRow = theClass.cast(myRows[i]);

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
            if (hasHeader()) {
                myRowNo++;
            }

            /* Mark the row as recovered */
            myRow.setDeleted(false);
            myRow.clearErrors();
            myRow.validate();

            /* Notify of the update of the row */
            theModel.fireUpdateRowEvents(myRowNo);
        }

        /* Re-validate after change */
        theUpdateSet.incrementVersion();
        // validateAfterChange();
    }

    /**
     * Row Table model class.
     */
    public static class RowTableModel extends AbstractTableModel implements PopulateRenderData {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7172213268168894124L;

        /**
         * The DataTable.
         */
        private DataTable<?> theTable = null;

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        protected RowTableModel(final DataTable<?> pTable) {
            /* Access rowHdrModel */
            theTable = pTable;
        }

        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public int getRowCount() {
            return theTable.getModel().getRowCount();
        }

        @Override
        public String getColumnName(final int col) {
            return TITLE_ROW;
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            return Integer.class;
        }

        @Override
        public Object getValueAt(final int row,
                                 final int col) {
            return theTable.hasHeader() ? row : row + 1;
        }

        @Override
        public void populateRenderData(final RenderData pData) {
            DataItem myRow;
            int iRow;
            int myIndex;
            JDataField[] iFields;
            DataColumnModel myColModel;

            /* If we have a header decrement the index */
            iRow = pData.getRow();
            myIndex = iRow;
            if (theTable.hasHeader()) {
                myIndex--;
            }

            /* Obtain defaults from table header */
            pData.initFromHeader(theTable.getTableHeader());

            /* If this is a data row */
            if (myIndex >= 0) {
                /* Access the row */
                myRow = theTable.getList().get(myIndex);
                myColModel = (DataColumnModel) theTable.getColumnModel();
                iFields = myColModel.getColumnFields();

                /* Has the row changed */
                pData.processRowHeader(myRow, iFields);
            }
        }
    }

    /**
     * Data Table model class.
     */
    public abstract static class DataTableModel extends AbstractTableModel implements PopulateRenderData {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 3815818983288519203L;

        /**
         * The DataTable.
         */
        private DataTable<?> theTable = null;

        /**
         * The RowHdrModel.
         */
        private RowTableModel theRowHdrModel = null;

        /**
         * Get the field associated with the cell.
         * @param row the row
         * @param col the column
         * @return the field
         */
        public abstract JDataField getFieldForCell(final int row,
                                                   final int col);

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        protected DataTableModel(final DataTable<?> pTable) {
            /* Access rowHdrModel */
            theTable = pTable;
            theRowHdrModel = pTable.getRowTableModel();
        }

        /**
         * fire events for moving of a row.
         * @param pFromRow the original row
         * @param pToRow the new row
         */
        protected void fireMoveRowEvents(final int pFromRow,
                                         final int pToRow) {
            /* Report the deletion and insertion */
            fireTableRowsDeleted(pFromRow, pFromRow);
            fireTableRowsInserted(pToRow, pToRow);

            /* If To Row is earlier */
            if (pToRow > pFromRow) {
                /* Report the change of headers in the region */
                theRowHdrModel.fireTableRowsUpdated(pFromRow, pToRow);

                /* else from row is earlier */
            } else {
                /* Report the change of headers in the region */
                theRowHdrModel.fireTableRowsUpdated(pToRow, pFromRow);
            }
        }

        /**
         * fire events for insertion of a row.
         * @param pNewRow the inserted row
         */
        protected void fireInsertRowEvents(final int pNewRow) {
            /* Note that we have an inserted row */
            fireTableRowsInserted(pNewRow, pNewRow);
            theRowHdrModel.fireTableRowsInserted(pNewRow, pNewRow);

            /* Access the row count */
            int iNumRows = getRowCount();

            /* If we have rows subsequent to the inserted row */
            if (iNumRows > pNewRow + 1) {
                /* Note that we need to rebuild subsequent rows in row header */
                theRowHdrModel.fireTableRowsUpdated(pNewRow + 1, iNumRows - 1);
            }
        }

        /**
         * fire events for deletion of a row.
         * @param pOldRow the deleted row
         */
        protected void fireDeleteRowEvents(final int pOldRow) {
            /* Note that we have an deleted row */
            fireTableRowsDeleted(pOldRow, pOldRow);
            theRowHdrModel.fireTableRowsInserted(pOldRow, pOldRow);

            /* Access the row count */
            int iNumRows = getRowCount();

            /* If we have rows subsequent to the deleted row */
            if (iNumRows > pOldRow) {
                /* Note that we need to rebuild subsequent rows in row header */
                theRowHdrModel.fireTableRowsUpdated(pOldRow, iNumRows - 1);
            }
        }

        /**
         * fire row updated events.
         * @param pRow the updated row
         */
        protected void fireUpdateRowEvents(final int pRow) {
            /* Note that the data for this row and header has changed */
            fireTableRowsUpdated(pRow, pRow);
            theRowHdrModel.fireTableRowsUpdated(pRow, pRow);
        }

        /**
         * fire column updated events.
         * @param pCol the updated column
         */
        public void fireUpdateColEvent(final int pCol) {
            /* Access the size of the table */
            int mySize = getRowCount();
            if (mySize == 0) {
                return;
            }

            /* Create the table event */
            TableModelEvent myEvent = new TableModelEvent(this, 0, mySize - 1, pCol);

            /* Note that the data for this row and header has changed */
            fireTableChanged(myEvent);
        }

        /**
         * fire events for new data view.
         */
        protected void fireNewDataEvents() {
            /* Note that the data for table and row header has changed */
            fireTableDataChanged();
            theRowHdrModel.fireTableDataChanged();
        }

        @Override
        public void populateRenderData(final RenderData pData) {
            DataItem myRow;
            int iRow;
            int myIndex;
            JDataField iField;

            /* If we have a header decrement the index */
            iRow = pData.getRow();
            myIndex = iRow;
            if (theTable.hasHeader()) {
                myIndex--;
            }

            /* If this is a data row */
            if (myIndex >= 0) {
                /* Access the row */
                myRow = theTable.getList().get(myIndex);
                iField = getFieldForCell(iRow, pData.getCol());

                /* Has the field changed */
                pData.processTableRow(myRow, iField);

                /* else set default values */
            } else {
                pData.setDefaults();
            }
        }
    }

    /**
     * TableColumn extension class.
     */
    protected static class DataColumn extends TableColumn {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6117303771805259099L;

        /**
         * Is the column currently in the model.
         */
        private boolean isMember = false;

        /**
         * The Model for the Table.
         */
        private AbstractTableModel theModel = null;

        /**
         * Is the column currently a member?
         * @return true/false
         */
        public boolean isMember() {
            return isMember;
        }

        /**
         * Set whether the column is a member.
         * @param pMember true/false
         */
        public void setMember(final boolean pMember) {
            isMember = pMember;
        }

        /**
         * Set the table model.
         * @param pModel the table model
         */
        public void setModel(final AbstractTableModel pModel) {
            theModel = pModel;
        }

        /**
         * Constructor.
         * @param modelIndex model index
         * @param width column width
         * @param cellRenderer cell renderer
         * @param cellEditor cell editor
         */
        public DataColumn(final int modelIndex,
                          final int width,
                          final TableCellRenderer cellRenderer,
                          final TableCellEditor cellEditor) {
            /* Call super-constructor */
            super(modelIndex, width, cellRenderer, cellEditor);
        }

        @Override
        public Object getHeaderValue() {
            /* Return the column name according to the model */
            return theModel.getColumnName(getModelIndex());
        }
    }

    /**
     * Column Model class.
     */
    protected static class DataColumnModel extends DefaultTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -5503203201580691221L;

        /**
         * The DataTableModel.
         */
        private final DataTableModel theModel;

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        protected DataColumnModel(final DataTable<?> pTable) {
            /* Access TableModel */
            theModel = pTable.getTableModel();
        }

        /**
         * Add a column to the end of the model.
         * @param pColumn the column
         */
        protected void addColumn(final DataColumn pColumn) {
            /* Set the range */
            super.addColumn(pColumn);
            pColumn.setMember(true);
            pColumn.setModel(theModel);
        }

        /**
         * Remove a column from the model.
         * @param pColumn the column
         */
        protected void removeColumn(final DataColumn pColumn) {
            /* Set the range */
            super.removeColumn(pColumn);
            pColumn.setMember(false);
        }

        /**
         * Access the array of displayed column indices.
         * @return the array of columns
         */
        protected JDataField[] getColumnFields() {
            /* Declare the field array */
            JDataField[] myFields = new JDataField[getColumnCount()];
            int myCol;

            /* Loop through the columns */
            for (int i = 0; i < myFields.length; i++) {
                /* Access the column index for this column */
                myCol = getColumn(i).getModelIndex();

                /* Store the field # */
                myFields[i] = theModel.getFieldForCell(-1, myCol);
            }

            /* return the fields */
            return myFields;
        }
    }

    /**
     * Row Column Model class.
     */
    private static final class RowColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -579928883936388389L;

        /**
         * Row renderer.
         */
        private RowCell theRowRenderer = null;

        /**
         * Constructor.
         * @param pTable the table with which this model is associated
         */
        private RowColumnModel(final DataTable<?> pTable) {
            /* Call super-constructor */
            super(pTable);

            /* Create the relevant formatters/editors */
            theRowRenderer = pTable.getRenderMgr().allocateRowRenderer();

            /* Create the columns */
            DataColumn myCol = new DataColumn(0, ROWHDR_WIDTH, theRowRenderer, null);
            addColumn(myCol);
            myCol.setModel(pTable.getRowTableModel());
        }
    }
}
