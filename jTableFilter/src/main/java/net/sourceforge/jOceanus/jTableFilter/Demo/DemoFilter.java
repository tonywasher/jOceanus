/*******************************************************************************
 * jTableFilter: JTable RowFilter/Sorter
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
package net.sourceforge.jOceanus.jTableFilter.Demo;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JApplet;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.jOceanus.jTableFilter.TableFilter;
import net.sourceforge.jOceanus.jTableFilter.TableFilter.TableFilterModel;

/**
 * Demo application for JTableFilter.
 * @author Tony Washer
 */
public class DemoFilter
        extends JApplet {
    /**
     * The Serial Id.
     */
    private static final long serialVersionUID = 1701124585110023740L;

    /**
     * Logger.
     */
    private static Logger theLogger = Logger.getAnonymousLogger();

    @Override
    public void init() {
        // Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    /* Create the Test Table */
                    TestTable myTable = new TestTable();

                    /* Access the panel */
                    JScrollPane myPanel = myTable.getScrollPane();
                    setContentPane(myPanel);
                }
            });
        } catch (InvocationTargetException e) {
            theLogger.log(Level.SEVERE, "Failed to invoke thread", e);
        } catch (InterruptedException e) {
            theLogger.log(Level.SEVERE, "Thread was interrupted", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("DataFilter Test");

            /* Create the Test Table */
            TestTable myTable = new TestTable();

            /* Access the panel */
            JScrollPane myPanel = myTable.getScrollPane();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            theLogger.log(Level.SEVERE, "createGUI didn't complete successfully", e);
        }
    }

    /**
     * Table class.
     */
    public static class TestTable
            extends JTable {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -3910208219313418696L;

        /**
         * The Panel Width.
         */
        private static final int PANEL_WIDTH = 600;

        /**
         * The Panel Height.
         */
        private static final int PANEL_HEIGHT = 300;

        /**
         * ScrollPane.
         */
        private final JScrollPane theScroll;

        /**
         * The model.
         */
        private final TestTableModel theModel;

        /**
         * Get the scroll pane.
         * @return the scroll pane
         */
        public JScrollPane getScrollPane() {
            return theScroll;
        }

        @Override
        public TestTableModel getModel() {
            return theModel;
        }

        /**
         * Constructor.
         */
        protected TestTable() {
            /* Create the mode for this table */
            theModel = new TestTableModel();
            setModel(theModel);

            /* Create the filter and record it */
            TableFilter<RowData> myFilter = new TableFilter<RowData>(theModel);
            theModel.registerFilter(myFilter);
            setRowSorter(myFilter);

            /* Add some items to the list */
            theModel.addNewRowAtEnd("Michael");
            theModel.addNewRowAtEnd("Timothy");
            theModel.addNewRowAtEnd("Adrian");
            theModel.addNewRowAtEnd("David");

            /* Set the number of visible rows */
            setPreferredScrollableViewportSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

            /* Create a new Scroll Pane for this table */
            theScroll = new JScrollPane(this);

            /* Add the mouse listener */
            addMouseListener(new TestMouse(this));
        }
    }

    /**
     * Row class.
     */
    public static class RowData
            implements Comparable<RowData> {
        /**
         * Name of item.
         */
        private String theName = null;

        /**
         * Visible.
         */
        private boolean isVisible = true;

        /**
         * Constructor.
         * @param pName the name
         */
        public RowData(final String pName) {
            theName = pName;
        }

        /**
         * Obtain the name.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Is the item visible?
         * @return true/false
         */
        public boolean isVisible() {
            return isVisible;
        }

        /**
         * Set name of item.
         * @param pName the name
         */
        public void setName(final String pName) {
            theName = pName;
        }

        /**
         * Set visibility of item.
         * @param setVisible true/false
         */
        public void setVisibility(final boolean setVisible) {
            isVisible = setVisible;
        }

        @Override
        public int compareTo(final RowData pThat) {
            /* Compare names */
            return theName.compareTo(pThat.getName());
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Compare names */
            return (pThat instanceof RowData)
                   && theName.equals(((RowData) pThat).getName());
        }

        @Override
        public int hashCode() {
            return theName.hashCode();
        }
    }

    /**
     * DataModel.
     */
    protected static class TestTableModel
            extends AbstractTableModel
            implements TableFilterModel<RowData> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 7780259663236675737L;

        /**
         * Name column.
         */
        private static final int COL_NAME = 0;

        /**
         * Visibility column.
         */
        private static final int COL_VISIBLE = 1;

        /**
         * Number of columns.
         */
        private static final int NUM_COLS = COL_VISIBLE + 1;

        /**
         * List of items.
         */
        private final transient List<RowData> theList;

        /**
         * Should we show all items.
         */
        private boolean showAll = false;

        /**
         * Should we sort items.
         */
        private boolean sortItems = false;

        /**
         * The table filter.
         */
        private transient TableFilter<RowData> theFilter = null;

        /**
         * Get showAll value.
         * @return true/false
         */
        public boolean showAll() {
            return showAll;
        }

        /**
         * Get sortItems value.
         * @return true/false
         */
        public boolean sortItems() {
            return sortItems;
        }

        /**
         * Register the data filter.
         * @param pFilter the filter
         */
        public void registerFilter(final TableFilter<RowData> pFilter) {
            theFilter = pFilter;
        }

        /**
         * Constructor.
         */
        public TestTableModel() {
            /* Allocate the list */
            theList = new ArrayList<RowData>();
        }

        /**
         * Add row at end.
         * @param pName the name
         */
        public void addNewRowAtEnd(final String pName) {
            /* Determine index of new item */
            int iIndex = getRowCount();

            /* Create the new row and add it to the list */
            RowData myRow = new RowData(pName);
            theList.add(myRow);

            /* Say that we have added the row */
            fireTableRowsInserted(iIndex, iIndex);
            theFilter.reportMappingChanged();
        }

        /**
         * Add row at index.
         * @param pIndex the index
         * @param pName the name
         */
        public void addNewRowAtIndex(final int pIndex,
                                     final String pName) {
            /* Create the new row and add it to the list */
            RowData myRow = new RowData(pName);
            theList.add(pIndex, myRow);

            /* Say that we have added the row */
            fireTableRowsInserted(pIndex, pIndex);
        }

        /**
         * Delete row at index.
         * @param pRowIndex the row to delete
         */
        public void deleteRow(final int pRowIndex) {
            /* Remove the item at the index */
            theList.remove(pRowIndex);

            /* Say that we have deleted the row */
            fireTableRowsDeleted(pRowIndex, pRowIndex);
        }

        /**
         * Set sort items.
         * @param doSort true/false
         */
        public void setSortItems(final boolean doSort) {
            /* Set the sort details */
            theFilter.setSortMode(doSort);
            sortItems = doSort;
        }

        /**
         * Set showAll value.
         * @param doShowAll true/false
         */
        public void setShowAll(final boolean doShowAll) {
            /* Ignore if no change */
            if (doShowAll == showAll) {
                return;
            }

            /* Record the value and alert to the change */
            showAll = doShowAll;
            fireTableDataChanged();
        }

        @Override
        public RowData getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theList.get(pRowIndex);
        }

        @Override
        public boolean includeRow(final RowData pRow) {
            /* Return visibility */
            return showAll
                   || pRow.isVisible();
        }

        @Override
        public int getColumnCount() {
            return NUM_COLS;
        }

        @Override
        public int getRowCount() {
            return theList.size();
        }

        @Override
        public String getColumnName(final int col) {
            switch (col) {
                case COL_NAME:
                    return "Name";
                case COL_VISIBLE:
                    return "isVisible";
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(final int col) {
            switch (col) {
                case COL_NAME:
                    return String.class;
                case COL_VISIBLE:
                    return Boolean.class;
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(final int row,
                                      final int col) {
            return true;
        }

        @Override
        public Object getValueAt(final int pRowIndex,
                                 final int pColIndex) {
            /* Access the row */
            RowData myItem = getItemAtIndex(pRowIndex);

            /* Switch on column */
            switch (pColIndex) {
                case COL_NAME:
                    return myItem.getName();
                case COL_VISIBLE:
                    return myItem.isVisible();
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(final Object pValue,
                               final int pRowIndex,
                               final int pColIndex) {
            /* Access the row */
            RowData myItem = getItemAtIndex(pRowIndex);

            /* Switch on column */
            switch (pColIndex) {
                case COL_NAME:
                    myItem.setName((String) pValue);
                    break;
                case COL_VISIBLE:
                    myItem.setVisibility((Boolean) pValue);
                    break;
                default:
                    break;
            }

            /* Say that we have changed the row */
            fireTableCellUpdated(pRowIndex, pColIndex);
        }

        @Override
        public void fireTableCellUpdated(final int pRowIndex,
                                         final int pColIndex) {
            super.fireTableCellUpdated(pRowIndex, pColIndex);
            theFilter.reportMappingChanged();
        }
    }

    /**
     * Mouse adapter.
     */
    public static class TestMouse
            extends MouseAdapter
            implements ActionListener {
        /**
         * Insert new name.
         */
        private static final String POPUP_INSERTEND = "Insert At End";

        /**
         * Insert at position.
         */
        private static final String POPUP_INSERTHERE = "Insert Here";

        /**
         * Insert menu command.
         */
        private static final String CMD_INSERT = POPUP_INSERTHERE
                                                 + ":";

        /**
         * Delete menu item.
         */
        private static final String POPUP_DELETE = "Delete Item";

        /**
         * Delete menu command.
         */
        private static final String CMD_DELETE = POPUP_DELETE
                                                 + ":";

        /**
         * ShowDeleted menu item.
         */
        private static final String POPUP_SHOWALL = "Show All Items";

        /**
         * SetSorted menu item.
         */
        private static final String POPUP_SORT = "Sort Items";

        /**
         * The Table.
         */
        private final TestTable theTable;

        /**
         * The Model.
         */
        private final TestTableModel theModel;

        /**
         * Constructor.
         * @param pTable the table
         */
        public TestMouse(final TestTable pTable) {
            /* Store parameters */
            theTable = pTable;
            theModel = theTable.getModel();

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
            if (e.isPopupTrigger()) {
                /* Access the point that the mouse was clicked at */
                Point p = new Point(e.getX(), e.getY());

                /* Access row and adjust for view differences */
                int myRow = theTable.rowAtPoint(p);
                if (myRow != -1) {
                    myRow = theTable.convertRowIndexToModel(myRow);
                }

                /* Create the pop-up menu */
                JPopupMenu myMenu = new JPopupMenu();

                /* Add the delete item choice */
                JMenuItem myItem = new JMenuItem(POPUP_INSERTEND);
                myItem.setActionCommand(POPUP_INSERTEND);
                myItem.addActionListener(this);
                myMenu.add(myItem);

                /* If the row is in bounds */
                if ((myRow >= 0)
                    && (myRow < theModel.getRowCount())) {
                    /* Add the delete item choice */
                    myItem = new JMenuItem(POPUP_INSERTHERE);
                    myItem.setActionCommand(CMD_INSERT
                                            + myRow);
                    myItem.addActionListener(this);
                    myMenu.add(myItem);

                    /* Add the delete item choice */
                    myItem = new JMenuItem(POPUP_DELETE);
                    myItem.setActionCommand(CMD_DELETE
                                            + myRow);
                    myItem.addActionListener(this);
                    myMenu.add(myItem);
                }

                /* Add the Visibility CheckBox */
                JCheckBoxMenuItem myCheckBox = new JCheckBoxMenuItem(POPUP_SHOWALL);
                myCheckBox.setSelected(theModel.showAll());
                myCheckBox.setActionCommand(POPUP_SHOWALL);
                myCheckBox.addActionListener(this);
                myMenu.add(myCheckBox);

                /* Add the Sort CheckBox */
                myCheckBox = new JCheckBoxMenuItem(POPUP_SORT);
                myCheckBox.setSelected(theModel.sortItems());
                myCheckBox.setActionCommand(POPUP_SORT);
                myCheckBox.addActionListener(this);
                myMenu.add(myCheckBox);

                /* If we have items in the menu */
                if (myMenu.getComponentCount() > 0) {
                    /* Show the pop-up menu */
                    myMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access event details */
            Object mySrc = evt.getSource();
            String myCmd = evt.getActionCommand();

            /* if this is an insert command */
            if (myCmd.equals(POPUP_INSERTEND)) {
                /* Notify the table */
                theModel.addNewRowAtEnd("New Name");

                /* if this is a delete command */
            } else if (myCmd.startsWith(CMD_INSERT)) {
                /* Strip the prefix */
                myCmd = myCmd.substring(CMD_INSERT.length());
                int myRow = Integer.parseInt(myCmd);

                /* Notify the table */
                theModel.addNewRowAtIndex(myRow, "New Name");

                /* if this is a delete command */
            } else if (myCmd.startsWith(CMD_DELETE)) {
                /* Strip the prefix */
                myCmd = myCmd.substring(CMD_DELETE.length());
                int myRow = Integer.parseInt(myCmd);

                /* Notify the table */
                theModel.deleteRow(myRow);

                /* if this is a show all command */
            } else if (myCmd.equals(POPUP_SHOWALL)) {
                /* Note the new criteria */
                boolean doShowAll = ((JCheckBoxMenuItem) mySrc).isSelected();

                /* Notify the table */
                theModel.setShowAll(doShowAll);

                /* if this is a sort command */
            } else if (myCmd.equals(POPUP_SORT)) {
                /* Note the new criteria */
                boolean doSort = ((JCheckBoxMenuItem) mySrc).isSelected();

                /* Notify the table */
                theModel.setSortItems(doSort);
            }
        }
    }
}
