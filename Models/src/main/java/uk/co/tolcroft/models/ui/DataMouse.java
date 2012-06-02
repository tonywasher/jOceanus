/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.ui;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.AbstractTableModel;

import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.EditState;

/**
 * Template class to provide mouse support for a table.
 * @author Tony Washer
 * @param <T> the data type.
 */
public abstract class DataMouse<T extends DataItem<T>> extends MouseAdapter implements ActionListener {
    /**
     * The underlying data table.
     */
    private final DataTable<T> theTable;

    /**
     * Are we showing deleted items?
     */
    private boolean doShowDeleted = false;

    /**
     * The row that the mouse is on.
     */
    private int theRow = -1;

    /**
     * The column that the mouse is on.
     */
    private int theCol = -1;

    /**
     * Are we on the header?
     */
    private boolean isHeader = false;

    /**
     * Get the row that we are on.
     * @return the row
     */
    protected int getPopupRow() {
        return theRow;
    }

    /**
     * Get the column that we are on.
     * @return the column
     */
    protected int getPopupCol() {
        return theCol;
    }

    /**
     * Are we on the header?
     * @return true/false
     */
    protected boolean isHeader() {
        return isHeader;
    }

    /**
     * Insert menu item.
     */
    private static final String POPUP_INSERT = "Insert Item";

    /**
     * Delete menu item.
     */
    private static final String POPUP_DELETE = "Delete Item(s)";

    /**
     * Duplicate menu item.
     */
    private static final String POPUP_DUPLICATE = "Duplicate Item(s)";

    /**
     * Recover menu item.
     */
    private static final String POPUP_RECOVER = "Recover Item(s)";

    /**
     * ShowDeleted menu item.
     */
    private static final String POPUP_SHOWDEL = "Show Deleted";

    /**
     * Undo menu item.
     */
    private static final String POPUP_UNDO = "Undo";

    /**
     * Validate menu item.
     */
    private static final String POPUP_VALIDATE = "Validate Item(s)";

    /**
     * Reset menu item.
     */
    private static final String POPUP_RESET = "Reset Item(s)";

    /**
     * Constructor.
     * @param pTable the table
     */
    public DataMouse(final DataTable<T> pTable) {
        /* Store parameters */
        theTable = pTable;

        /* Add as listener to the header */
        theTable.getTableHeader().addMouseListener(this);
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        maybeShowPopup(e);
    }

    /**
     * Maybe show the PopUp.
     * @param e the event
     */
    public void maybeShowPopup(final MouseEvent e) {
        JPopupMenu myMenu;
        int myRow;

        /* If we can trigger a PopUp menu */
        if ((e.isPopupTrigger()) && (theTable.isEnabled())) {
            /* Note if this is a header PopUp */
            isHeader = (e.getComponent() == theTable.getTableHeader());

            /* Access the point that the mouse was clicked at */
            Point p = new Point(e.getX(), e.getY());

            /* If we are in the table */
            if (!isHeader) {
                /* Access column and row */
                theRow = theTable.rowAtPoint(p);
                theCol = theTable.columnAtPoint(p);
                myRow = theRow;

                /* Adjust column for view differences */
                theCol = theTable.convertColumnIndexToModel(theCol);

                /* If the table has a header */
                if (theTable.hasHeader()) {
                    /* Row zero is the same as header */
                    if (theRow == 0) {
                        isHeader = true;

                        /* else adjust row for header */
                    } else {
                        theRow--;
                    }
                }

                /* If we are on a valid row, ensure that this row is selected */
                if ((!isHeader) && (theRow >= 0) && (!theTable.isRowSelected(myRow))) {
                    theTable.setRowSelectionInterval(myRow, myRow);
                }
            }

            /* Create the pop-up menu */
            myMenu = new JPopupMenu();

            /* Add special commands to menu */
            addSpecialCommands(myMenu);

            /* Add navigation commands to menu */
            addNavigationCommands(myMenu);

            /* Add insert/delete commands to menu */
            addInsertDelete(myMenu);

            /* Add edit commands to menu */
            addEditCommands(myMenu);

            /* Add null commands to menu */
            addNullCommands(myMenu);

            /* If we have items in the menu */
            if (myMenu.getComponentCount() > 0) {
                /* Show the pop-up menu */
                myMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    /**
     * Add Insert/Delete commands to menu. Should be overridden if insert/delete is not required.
     * @param pMenu the menu to add to
     */
    protected void addInsertDelete(final JPopupMenu pMenu) {
        JMenuItem myItem;
        JCheckBoxMenuItem myCheckBox;
        boolean enableIns = false;
        boolean enableRecov = false;
        boolean enableDel = false;
        boolean enableShow = true;
        boolean enableDupl = false;

        /* Nothing to do if the table is locked */
        if (theTable.isLocked()) {
            return;
        }

        /* Determine whether insert is allowed */
        enableIns = theTable.insertAllowed();

        /* Loop through the selected rows */
        for (T myRow : theTable.cacheSelectedRows()) {
            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Determine actions for row */
            enableDel |= theTable.isRowDeletable(myRow);
            enableDupl |= theTable.isRowDuplicatable(myRow);
            enableShow &= !theTable.disableShowDeleted(myRow);
            enableRecov |= theTable.isRowRecoverable(myRow);
        }

        /* If there is something to add and there are already items in the menu */
        if ((enableIns || enableDel || enableDupl || enableShow || enableRecov)
                && (pMenu.getComponentCount() > 0)) {
            /* Add a separator */
            pMenu.addSeparator();
        }

        /* If we can insert a row */
        if (enableIns) {
            /* Add the insert item choice */
            myItem = new JMenuItem(POPUP_INSERT);
            myItem.setActionCommand(POPUP_INSERT);
            myItem.addActionListener(this);
            pMenu.add(myItem);
        }

        /* If we can duplicate a row */
        if (enableDupl) {
            /* Add the duplicate items choice */
            myItem = new JMenuItem(POPUP_DUPLICATE);
            myItem.setActionCommand(POPUP_DUPLICATE);
            myItem.addActionListener(this);
            pMenu.add(myItem);
        }

        /* If we can delete a row */
        if (enableDel) {
            /* Add the delete items choice */
            myItem = new JMenuItem(POPUP_DELETE);
            myItem.setActionCommand(POPUP_DELETE);
            myItem.addActionListener(this);
            pMenu.add(myItem);
        }

        /* If we can recover a row */
        if (enableRecov) {
            /* Add the delete items choice */
            myItem = new JMenuItem(POPUP_RECOVER);
            myItem.setActionCommand(POPUP_RECOVER);
            myItem.addActionListener(this);
            pMenu.add(myItem);
        }

        /* If we can change the show deleted indication */
        if (enableShow) {
            /* Add the CheckBox items choice */
            myCheckBox = new JCheckBoxMenuItem(POPUP_SHOWDEL);
            myCheckBox.setSelected(doShowDeleted);
            myCheckBox.setActionCommand(POPUP_SHOWDEL);
            myCheckBox.addActionListener(this);
            pMenu.add(myCheckBox);
        }
    }

    /**
     * Add Edit commands to menu. Should be overridden if edit is not required.
     * @param pMenu the menu to add to
     */
    protected void addEditCommands(final JPopupMenu pMenu) {
        JMenuItem myItem;
        boolean rowSelected = false;
        boolean enableUndo = false;
        boolean enableReset = false;
        boolean enableValid = false;

        /* Nothing to do if the table is locked */
        if (theTable.isLocked()) {
            return;
        }

        /* Loop through the selected rows */
        for (T myRow : theTable.cacheSelectedRows()) {
            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Ignore deleted rows */
            if (myRow.isDeleted()) {
                continue;
            }

            /* If the row has Changes */
            if (myRow.hasHistory()) {
                /* Note that we can reset */
                enableReset = true;

                /* Enable validate if required */
                if (myRow.getEditState() != EditState.VALID) {
                    enableValid = true;
                }
            }

            /* If this is a second (or later selection) */
            if (rowSelected) {
                /* Disable Undo */
                enableUndo = false;

                /* else this is the first selection */
            } else {
                /* Determine whether we can undo */
                if (myRow.hasHistory()) {
                    enableUndo = true;
                }

                /* Note that we have selected a row */
                rowSelected = true;
            }
        }

        /* If there is something to add and there are already items in the menu */
        if ((enableUndo || enableReset || enableValid) && (pMenu.getComponentCount() > 0)) {
            /* Add a separator */
            pMenu.addSeparator();
        }

        /* If we can undo changes */
        if (enableUndo) {
            /* Add the undo change choice */
            myItem = new JMenuItem(POPUP_UNDO);
            myItem.setActionCommand(POPUP_UNDO);
            myItem.addActionListener(this);
            pMenu.add(myItem);
        }

        /* If we can reset changes */
        if (enableReset) {
            /* Add the reset items choice */
            myItem = new JMenuItem(POPUP_RESET);
            myItem.setActionCommand(POPUP_RESET);
            myItem.addActionListener(this);
            pMenu.add(myItem);
        }

        /* If we can validate changes */
        if (enableValid) {
            /* Add the reset items choice */
            myItem = new JMenuItem(POPUP_VALIDATE);
            myItem.setActionCommand(POPUP_VALIDATE);
            myItem.addActionListener(this);
            pMenu.add(myItem);
        }
    }

    /**
     * Add Null commands to menu. Should be overridden if null values are required.
     * @param pMenu the menu to add to
     */
    protected void addNullCommands(final JPopupMenu pMenu) {
    }

    /**
     * Add Special commands to menu. Should be overridden if special commands are required.
     * @param pMenu the menu to add to
     */
    protected void addSpecialCommands(final JPopupMenu pMenu) {
    }

    /**
     * Add Navigation commands to menu. Should be overridden if navigation commands are required.
     * @param pMenu the menu to add to
     */
    protected void addNavigationCommands(final JPopupMenu pMenu) {
    }

    /**
     * Set the specified column to null if non-null for selected rows.
     * @param col the column
     */
    protected void setColumnToNull(final int col) {
        AbstractTableModel myModel;
        int row;

        /* Access the table model */
        myModel = theTable.getTableModel();

        /* Loop through the selected rows */
        for (T myRow : theTable.cacheSelectedRows()) {
            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Ignore deleted rows */
            if (myRow.isDeleted()) {
                continue;
            }

            /* Determine row */
            row = myRow.indexOf();
            if (theTable.hasHeader()) {
                row++;
            }

            /* Ignore null rows */
            if (myModel.getValueAt(row, col) == null) {
                continue;
            }

            /* set the null value */
            myModel.setValueAt(null, row, col);
            myModel.fireTableCellUpdated(row, col);
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        String myCmd = evt.getActionCommand();
        Object mySrc = evt.getSource();

        /* Cancel any editing */
        theTable.cancelEditing();

        /* If this is a generic insert item command */
        if (myCmd.equals(POPUP_INSERT)) {
            /* Insert a row into the table */
            theTable.insertRow();

            /* if this is a duplicate command */
        } else if (myCmd.equals(POPUP_DUPLICATE)) {
            /* Duplicate selected items */
            theTable.duplicateRows();

            /* if this is a delete items command */
        } else if (myCmd.equals(POPUP_DELETE)) {
            /* Delete selected rows */
            theTable.deleteRows();

            /* if this is a recover items command */
        } else if (myCmd.equals(POPUP_RECOVER)) {
            /* Recover selected rows */
            theTable.recoverRows();

            /* if this is a show deleted command */
        } else if (myCmd.equals(POPUP_SHOWDEL)) {
            /* Note the new criteria */
            doShowDeleted = ((JCheckBoxMenuItem) mySrc).isSelected();

            /* Notify the table */
            theTable.setShowDeleted(doShowDeleted);

            /* if this is a reset changes command */
        } else if (myCmd.equals(POPUP_RESET)) {
            /* Reset selected rows */
            theTable.resetRows();

            /* if this is a validate items command */
        } else if (myCmd.equals(POPUP_VALIDATE)) {
            /* Validate selected rows */
            theTable.validateRows();

            /* if this is an undo change command */
        } else if (myCmd.equals(POPUP_UNDO)) {
            /* Undo selected rows */
            theTable.unDoRows();
        }

        /* Notify of any changes */
        theTable.notifyChanges();
        theTable.updateDebug();
    }
}
