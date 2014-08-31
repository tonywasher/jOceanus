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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.JTableHeader;

import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.slf4j.Logger;

/**
 * Template class to provide mouse support for a table.
 * @author Tony Washer
 * @param <T> the data type.
 * @param <E> the data type enum class
 */
public abstract class JDataTableMouse<T extends DataItem<E> & Comparable<? super T>, E extends Enum<E>>
        extends MouseAdapter
        implements ActionListener {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(JDataTableMouse.class.getName());

    /**
     * Insert menu item.
     */
    private static final String POPUP_INSERT = NLS_BUNDLE.getString("PopUpInsert");

    /**
     * Delete menu item.
     */
    private static final String POPUP_DELETE = NLS_BUNDLE.getString("PopUpDelete");

    /**
     * Duplicate menu item.
     */
    private static final String POPUP_DUPLICATE = NLS_BUNDLE.getString("PopUpDuplicate");

    /**
     * Recover menu item.
     */
    private static final String POPUP_RECOVER = NLS_BUNDLE.getString("PopUpRecover");

    /**
     * ShowDeleted menu item.
     */
    private static final String POPUP_SHOWALL = NLS_BUNDLE.getString("PopUpShowAll");

    /**
     * The underlying data table.
     */
    private final JDataTable<T, E> theTable;

    /**
     * Are we showing all items?
     */
    private boolean doShowAll = false;

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
     * Constructor.
     * @param pTable the table
     */
    public JDataTableMouse(final JDataTable<T, E> pTable) {
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
        /* If we can trigger a PopUp menu */
        if ((e.isPopupTrigger()) && (theTable.isEnabled())) {
            /* Note if this is a header PopUp */
            Object o = e.getComponent();
            isHeader = (o instanceof JTableHeader);

            /* Access the point that the mouse was clicked at */
            Point p = new Point(e.getX(), e.getY());

            /* If we are in the table */
            if (!isHeader) {
                /* Access column and row */
                theRow = theTable.rowAtPoint(p);
                theCol = theTable.columnAtPoint(p);
                int myRow = theRow;

                /* Adjust column for view differences */
                if (theCol != -1) {
                    theCol = theTable.convertColumnIndexToModel(theCol);
                }
                if (theRow != -1) {
                    theRow = theTable.convertRowIndexToModel(theRow);
                }

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
            JPopupMenu myMenu = new JPopupMenu();

            /* Add special commands to menu */
            addSpecialCommands(myMenu);

            /* Add navigation commands to menu */
            addNavigationCommands(myMenu);

            /* Add insert/delete commands to menu */
            addInsertDelete(myMenu);

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
        Class<T> myClass = theTable.getDataClass();

        /* Nothing to do if the table is locked */
        if (theTable.isLocked()) {
            return;
        }

        /* Determine whether insert is allowed */
        boolean enableIns = theTable.insertAllowed();
        boolean enableRecov = false;
        boolean enableDel = false;
        boolean enableShow = true;
        boolean enableDupl = false;

        /* Loop through the selected rows */
        for (DataItem<E> myRow : theTable.cacheSelectedRows()) {
            /* Ignore locked rows */
            if ((myRow == null) || (myRow.isLocked())) {
                continue;
            }

            /* Access data correctly */
            T myData = myClass.cast(myRow);

            /* Determine actions for row */
            enableDel |= theTable.isRowDeletable(myData);
            enableDupl |= theTable.isRowDuplicatable(myData);
            enableShow &= !theTable.disableShowAll(myData);
            enableRecov |= theTable.isRowRecoverable(myData);
        }

        /* If there is something to add and there are already items in the menu */
        boolean haveItems = enableIns || enableDel || enableDupl;
        haveItems |= enableShow || enableRecov;
        if ((haveItems) && (pMenu.getComponentCount() > 0)) {
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
            myCheckBox = new JCheckBoxMenuItem(POPUP_SHOWALL);
            myCheckBox.setSelected(doShowAll);
            myCheckBox.setActionCommand(POPUP_SHOWALL);
            myCheckBox.addActionListener(this);
            pMenu.add(myCheckBox);
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
        /* Access the table model */
        JDataTableModel<T, E> myModel = theTable.getTableModel();
        Class<T> myClass = theTable.getDataClass();

        try {
            /* Loop through the selected rows */
            for (DataItem<E> myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Determine row */
                int row = myRow.indexOf();
                if (theTable.hasHeader()) {
                    row++;
                }

                /* Ignore null rows */
                if (myModel.getValueAt(row, col) == null) {
                    continue;
                }

                /* Access data correctly */
                T myData = myClass.cast(myRow);

                /* set the null value */
                myModel.setItemValue(myData, col, null);
            }
        } catch (JOceanusException e) {
            Logger myLogger = theTable.getLogger();
            myLogger.error("Failed to NULL column", e);
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

            /* if this is a show all command */
        } else if (myCmd.equals(POPUP_SHOWALL)) {
            /* Note the new criteria */
            doShowAll = ((JCheckBoxMenuItem) mySrc).isSelected();

            /* Notify the table */
            theTable.setShowAll(doShowAll);
        }

        /* Notify of any changes */
        theTable.notifyChanges();
    }
}
