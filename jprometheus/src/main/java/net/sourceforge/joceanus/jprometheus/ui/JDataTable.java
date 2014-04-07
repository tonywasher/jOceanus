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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.ComboBoxSelector;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.RowColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel.RowTableModel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnableScroll;
import net.sourceforge.joceanus.jtethys.event.JEventTable;
import net.sourceforge.joceanus.jtethys.swing.TableFilter;

/**
 * Template class to provide a table to handle a data type.
 * @author Tony Washer
 * @param <T> the data type.
 * @param <E> the data type enum class
 */
public abstract class JDataTable<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends JEventTable
        implements ComboBoxSelector {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1258025191244933784L;

    /**
     * The active icon.
     */
    protected static final Icon ICON_ACTIVE = resizeImage(new ImageIcon(JDataTable.class.getResource("Active.png")));

    /**
     * The delete icon.
     */
    protected static final Icon ICON_DELETE = resizeImage(new ImageIcon(JDataTable.class.getResource("Delete.png")));

    /**
     * Panel height.
     */
    protected static final int HEIGHT_PANEL = 200;

    /**
     * Panel width.
     */
    public static final int WIDTH_PANEL = 900;

    /**
     * Default row height.
     */
    private static final int ROW_HEIGHT = 16;

    /**
     * The Row Header Table.
     */
    private JTable theRowHdrTable = null;

    /**
     * FieldManager.
     */
    private JFieldManager theFieldMgr = null;

    /**
     * Data Table Model.
     */
    private JDataTableModel<T, E> theModel = null;

    /**
     * The Row Header Model.
     */
    private RowTableModel<E> theRowHdrModel = null;

    /**
     * The Data List associated with the table.
     */
    private DataList<T, E> theList = null;

    /**
     * The UpdateSet associated with the table.
     */
    private UpdateSet<E> theUpdateSet = null;

    /**
     * The Class associated with the table.
     */
    private Class<T> theClass = null;

    /**
     * The Scroll Pane.
     */
    private JEnableScroll theScroll = null;

    /**
     * The Logger.
     */
    private Logger theLogger = null;

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
     * Obtain the field manager.
     * @return the field manager
     */
    public JFieldManager getFieldMgr() {
        return theFieldMgr;
    }

    /**
     * Obtain the logger.
     * @return the logger
     */
    public Logger getLogger() {
        return theLogger;
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
    public JDataTableModel<T, E> getTableModel() {
        return theModel;
    }

    /**
     * Get the data list.
     * @return the data list
     */
    public DataList<T, E> getList() {
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
    protected abstract void setError(final JOceanusException pError);

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
    public JComboBox<?> getComboBox(final int pRowIndex,
                                    final int pColIndex) {
        return null;
    }

    /**
     * Get the RowTableModel.
     * @return the model
     */
    protected RowTableModel<E> getRowTableModel() {
        return theRowHdrModel;
    }

    /**
     * Constructor.
     */
    public JDataTable() {
        /* Store parameters */
        theRowHdrModel = new RowTableModel<E>(this);

        /* Set the selection mode */
        setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    }

    /**
     * Set the table model.
     * @param pModel the table model
     */
    public void setModel(final JDataTableModel<T, E> pModel) {
        /* Declare to the super class */
        super.setModel(pModel);

        /* Record the model */
        theModel = pModel;

        /* Create the filter and record it */
        TableFilter<T> myFilter = new TableFilter<T>(theModel, true);
        theModel.registerFilter(myFilter);
        setRowSorter(myFilter);

        /* Set up for zebra stripes */
        setRowMargin(0);
        setShowGrid(false);

        /* Set the row height */
        setRowHeight(ROW_HEIGHT);

        /* Create a row Header table */
        theRowHdrTable = new JTable(theRowHdrModel, new RowColumnModel<E>(this));
        theRowHdrTable.setBackground(getTableHeader().getBackground());
        theRowHdrTable.setColumnSelectionAllowed(false);
        theRowHdrTable.setCellSelectionEnabled(false);

        /* Set the selection model */
        theRowHdrTable.setSelectionModel(getSelectionModel());

        /* Create a new Scroll Pane and add this table to it */
        theScroll = new JEnableScroll();
        theScroll.setViewportView(this);

        /* Add as the row header */
        theScroll.setRowHeaderView(theRowHdrTable);
        theScroll.getRowHeader().setPreferredSize(new Dimension(JDataTableColumnModel.WIDTH_ROWHDR, HEIGHT_PANEL));
        theScroll.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, theRowHdrTable.getTableHeader());
    }

    /**
     * Resize an icon to the row height
     * @param pSource the source icon
     * @return the resized icon
     */
    protected static Icon resizeImage(final ImageIcon pSource) {
        Image myImage = pSource.getImage();
        Image myNewImage = myImage.getScaledInstance(ROW_HEIGHT, ROW_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(myNewImage);
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
        return (theUpdateSet == null)
                                     ? EditState.CLEAN
                                     : theUpdateSet.getEditState();
    }

    /**
     * Set the field manager for the table.
     * @param pFieldMgr the field manager
     */
    protected void setFieldMgr(final JFieldManager pFieldMgr) {
        theFieldMgr = pFieldMgr;
    }

    /**
     * Set the logger for the table.
     * @param pLogger the logger
     */
    protected void setLogger(final Logger pLogger) {
        theLogger = pLogger;
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
    protected void setList(final DataList<T, E> pList) {
        int myZeroRow = hasHeader()
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
        return (theList == null) || theList.isLocked();
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
        T[] myRows = (T[]) Array.newInstance(getDataClass(), mySelected.length);

        /* Loop through the selection indices */
        for (int i = 0; i < mySelected.length; i++) {
            /* Access the index and adjust for header */
            int myIndex = convertRowIndexToModel(mySelected[i]);

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
        return !pRow.isDeleted();
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
    protected void deleteRow(final T pRow) {
        /* Access the row # and adjust for header */
        int myRowNo = pRow.indexOf();

        /* Mark the row as deleted */
        pRow.setDeleted(true);

        /* Notify of the update of the row */
        theModel.fireUpdateRowEvents(myRowNo);

        /* Increment version */
        incrementVersion();
        notifyChanges();
    }

    /**
     * Delete the row.
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
        return !pRow.isDeleted();
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
            T myItem = theList.addCopyItem(myRow);

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
