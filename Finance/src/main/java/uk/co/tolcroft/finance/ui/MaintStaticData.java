/*******************************************************************************
 * JFinanceApp: Finance Application
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
package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.data.StaticData.StaticList;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.StringEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.Renderer.IntegerRenderer;
import uk.co.tolcroft.models.ui.Renderer.StringRenderer;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.views.ViewList;
import uk.co.tolcroft.models.views.ViewList.ListClass;

/**
 * Static Data Table.
 * @author Tony Washer
 * @param <L> the list type
 * @param <T> the data type
 */
public class MaintStaticData<L extends StaticList<L, T, ?>, T extends StaticData<T, ?>> extends DataTable<T> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8747707037700378702L;

    /**
     * The Data view.
     */
    private final View theView;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * The Parent.
     */
    private final MaintStatic theParent;

    /**
     * The Data class.
     */
    private final Class<L> theClass;

    /**
     * The List.
     */
    private L theStatic = null;

    /**
     * Self reference.
     */
    private final MaintStaticData<L, T> theTable = this;

    /**
     * The Tavle Model.
     */
    private final StaticModel theModel;

    /**
     * The Columns model.
     */
    private final StaticColumnModel theColumns;

    /**
     * The view set.
     */
    private final ViewList theViewSet;

    /**
     * The List class.
     */
    private final ListClass theDataView;

    /**
     * The Data entry.
     */
    private final JDataEntry theDataEntry;

    /**
     * The Save buttons.
     */
    private final SaveButtons theTabButs;

    /**
     * The Error panel.
     */
    private final ErrorPanel theError;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    /**
     * Class column title.
     */
    private static final String TITLE_CLASS = "Class";

    /**
     * Name column title.
     */
    private static final String TITLE_NAME = "Name";

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = "Description";

    /**
     * Order column title.
     */
    private static final String TITLE_ORDER = "SortOrder";

    /**
     * Enabled column title.
     */
    private static final String TITLE_ENABLED = "Enabled";

    /**
     * Active column title.
     */
    private static final String TITLE_ACTIVE = "Active";

    /**
     * Class column id.
     */
    private static final int COLUMN_CLASS = 0;

    /**
     * Name column id.
     */
    private static final int COLUMN_NAME = 1;

    /**
     * Description column id.
     */
    private static final int COLUMN_DESC = 2;

    /**
     * Order column id.
     */
    private static final int COLUMN_ORDER = 3;

    /**
     * Enabled column id.
     */
    private static final int COLUMN_ENABLED = 4;

    /**
     * Active column id.
     */
    private static final int COLUMN_ACTIVE = 5;

    /**
     * Class column width.
     */
    private static final int WIDTH_CLASS = 90;

    /**
     * Name column width.
     */
    private static final int WIDTH_NAME = 80;

    /**
     * Description column width.
     */
    private static final int WIDTH_DESC = 200;

    /**
     * Order column width.
     */
    private static final int WIDTH_ORDER = 20;

    /**
     * Enabled column width.
     */
    private static final int WIDTH_ENABLED = 20;

    /**
     * Active column width.
     */
    private static final int WIDTH_ACTIVE = 20;

    /**
     * Panel width.
     */
    private static final int WIDTH_PANEL = 800;

    /**
     * Panel height.
     */
    private static final int HEIGHT_PANEL = 200;

    /**
     * Constructor.
     * @param pParent the parent
     * @param pClass the list class
     */
    public MaintStaticData(final MaintStatic pParent,
                           final Class<L> pClass) {
        /* Call super constructor */
        super(pParent.getDataManager());

        /* Declare variables */
        GroupLayout myLayout;

        /* Record the passed details */
        theParent = pParent;
        theClass = pClass;
        theView = pParent.getView();

        /* Build the View set and List */
        theViewSet = theParent.getViewSet();
        theDataView = theViewSet.registerClass(pClass);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry(pClass.getEnclosingClass().getSimpleName());
        theDataEntry.addAsChildOf(pParent.getDataEntry());

        /* Set the table model */
        theModel = new StaticModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StaticColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Add the mouse listener */
        StaticMouse myMouse = new StaticMouse();
        addMouseListener(myMouse);

        /* Create the sub panels */
        theTabButs = new SaveButtons(this);

        /* Create the error panel for this view */
        theError = new ErrorPanel(this);

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
                                                    .addComponent(theError, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(getScrollPane(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, WIDTH_PANEL,
                                                                  Short.MAX_VALUE)
                                                    .addComponent(theTabButs, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theError)
                                  .addComponent(getScrollPane()).addComponent(theTabButs)));
    }

    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    @Override
    public void updateDebug() {
        theDataEntry.setObject(theStatic);
    }

    @Override
    public void lockOnError(final boolean isError) {
        /* Lock scroll-able area */
        getScrollPane().setEnabled(!isError);

        /* Lock row/tab buttons area */
        theTabButs.setEnabled(!isError);
    }

    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (theStatic != null) {
            theStatic.findEditState();
        }

        /* Update the table buttons */
        theTabButs.setLockDown();

        /* Update the top level tabs */
        theParent.setVisibility();
    }

    @Override
    public void saveData() {
        if (theStatic != null) {
            super.validateAll();
            if (!hasErrors()) {
                theViewSet.applyChanges();
            }
        }
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     * @throws JDataException on error
     */
    public void refreshData() throws JDataException {
        FinanceData myData;

        /* Access data */
        myData = theView.getData();

        /* Access edit list */
        theStatic = myData.getDataList(theClass);
        theStatic = theStatic.getEditList();

        /* Update the Data View */
        setList(theStatic);
        theDataView.setDataList(theStatic);

        /* Update the table buttons */
        theTabButs.setLockDown();
    }

    /**
     * Static table model.
     */
    public final class StaticModel extends DataTableModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6428052539280821038L;

        /**
         * Constructor.
         */
        private StaticModel() {
            /* call constructor */
            super(theTable);
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null) ? 0 : theColumns.getColumnCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theStatic == null) ? 0 : theStatic.size();
        }

        /**
         * Get the name of the column.
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(final int col) {
            switch (col) {
                case COLUMN_CLASS:
                    return TITLE_CLASS;
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_ORDER:
                    return TITLE_ORDER;
                case COLUMN_ENABLED:
                    return TITLE_ENABLED;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the Field id associated with the column.
         * @param row the row
         * @param column the column
         * @return the field id.
         */
        @Override
        public JDataField getFieldForCell(final int row,
                                          final int column) {
            /* Switch on column */
            switch (column) {
                case COLUMN_CLASS:
                    return StaticData.FIELD_CLASS;
                case COLUMN_NAME:
                    return StaticData.FIELD_NAME;
                case COLUMN_DESC:
                    return StaticData.FIELD_DESC;
                case COLUMN_ENABLED:
                    return StaticData.FIELD_ENABLED;
                case COLUMN_ORDER:
                    return StaticData.FIELD_ORDER;
                default:
                    return null;
            }
        }

        /**
         * Get the object class of the column.
         * @param col the column
         * @return the class of the objects associated with the column
         */
        @Override
        public Class<?> getColumnClass(final int col) {
            switch (col) {
                case COLUMN_DESC:
                    return String.class;
                case COLUMN_CLASS:
                    return String.class;
                case COLUMN_NAME:
                    return String.class;
                case COLUMN_ORDER:
                    return Integer.class;
                case COLUMN_ENABLED:
                    return String.class;
                case COLUMN_ACTIVE:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        /**
         * Is the cell at (row, col) editable?
         * @param row the row
         * @param col the column
         * @return true/false
         */
        @Override
        public boolean isCellEditable(final int row,
                                      final int col) {
            /* Access the data */
            T myData = theStatic.get(row);

            /* Not edit-able is not enabled */
            if (!myData.getEnabled()) {
                return false;
            }

            /* switch on column */
            switch (col) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_CLASS:
                case COLUMN_ORDER:
                case COLUMN_ENABLED:
                case COLUMN_ACTIVE:
                default:
                    return false;
            }
        }

        /**
         * Get the value at (row, col).
         * @param row the row
         * @param col the column
         * @return the object value
         */
        @Override
        public Object getValueAt(final int row,
                                 final int col) {
            T myData;
            Object o;

            /* Access the data */
            myData = theStatic.get(row);

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_CLASS:
                    o = myData.getStaticClass().toString();
                    break;
                case COLUMN_NAME:
                    o = myData.getName();
                    break;
                case COLUMN_DESC:
                    o = myData.getDesc();
                    if ((o != null) && (((String) o).length() == 0)) {
                        o = null;
                    }
                    break;
                case COLUMN_ENABLED:
                    o = myData.getEnabled() ? "true" : "false";
                    break;
                case COLUMN_ORDER:
                    o = myData.getOrder();
                    break;
                case COLUMN_ACTIVE:
                    o = myData.isActive() ? "true" : "false";
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myData.hasErrors(getFieldForCell(row, col)))) {
                o = Renderer.getError();
            }

            /* Return to caller */
            return o;
        }

        /**
         * Set the value at (row, col).
         * @param obj the object value to set
         * @param row the row
         * @param col the column
         */
        @Override
        public void setValueAt(final Object obj,
                               final int row,
                               final int col) {
            /* Access the line */
            T myData = theStatic.get(row);

            /* Push history */
            myData.pushHistory();

            /* Protect against Exceptions */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_NAME:
                        myData.setName((String) obj);
                        break;
                    case COLUMN_DESC:
                        myData.setDescription((String) obj);
                        break;
                    default:
                        break;
                }

                /* Handle Exceptions */
            } catch (Exception e) {
                /* Reset values */
                myData.popHistory();
                myData.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to update field at (" + row + "," + col + ")", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (myData.checkForHistory()) {
                /* Note that the item has changed */
                myData.clearErrors();
                myData.setState(DataState.CHANGED);

                /* Validate the item and update the edit state */
                myData.validate();
                theStatic.findEditState();

                /* note that we have updated this cell */
                fireTableRowsUpdated(row, row);

                /* Note that changes have occurred */
                notifyChanges();
                updateDebug();
            }
        }
    }

    /**
     * Static mouse listener.
     */
    private final class StaticMouse extends DataMouse<T> {
        /**
         * Enabled Popup.
         */
        private static final String POPUP_ENABLED = "Set As Enabled";

        /**
         * Disabled Popup.
         */
        private static final String POPUP_DISABLED = "Set As Disabled";

        /**
         * Constructor.
         */
        private StaticMouse() {
            /* Call super-constructor */
            super(theTable);
        }

        /**
         * Disable Insert/Delete.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addInsertDelete(final JPopupMenu pMenu) {
        }

        /**
         * Add Special commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addSpecialCommands(final JPopupMenu pMenu) {
            JMenuItem myItem;
            T myData;
            Class<T> myClass = (theStatic != null) ? theStatic.getBaseClass() : null;
            boolean enableEnable = false;
            boolean enableDisable = false;
            boolean isEnabled;
            boolean isActive;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (T myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as data */
                myData = myClass.cast(myRow);

                /* Determine flags */
                isEnabled = myData.getEnabled();
                isActive = myData.isActive();

                /* Determine whether we can enable/disable */
                if (!isEnabled) {
                    enableEnable = true;
                } else if (!isActive) {
                    enableDisable = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if (((enableEnable) || (enableDisable)) && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can Enable the item */
            if (enableEnable) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_ENABLED);
                myItem.setActionCommand(POPUP_ENABLED);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can Disable the item */
            if (enableDisable) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_DISABLED);
                myItem.setActionCommand(POPUP_DISABLED);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Perform actions for controls/pop-ups on this table.
         * @param evt the event
         */
        @Override
        public void actionPerformed(final ActionEvent evt) {
            String myCmd = evt.getActionCommand();

            /* Cancel any editing */
            theTable.cancelEditing();

            /* If this is a set null units command */
            if (myCmd.equals(POPUP_ENABLED)) {
                /* Enable disabled rows */
                setEnabledRows(true);

                /* else if this is a set null tax command */
            } else if (myCmd.equals(POPUP_DISABLED)) {
                /* Disable rows */
                setEnabledRows(false);

                /* else we do not recognise the action */
            } else {
                /* Pass it to the superclass */
                super.actionPerformed(evt);
                return;
            }

            /* Notify of any changes */
            notifyChanges();
            updateDebug();
        }

        /**
         * Enable/Disable Rows.
         * @param doEnable true/false
         */
        private void setEnabledRows(final boolean doEnable) {
            T myData;
            Class<T> myClass = (theStatic != null) ? theStatic.getBaseClass() : null;
            boolean isEnabled;
            boolean isActive;
            int row;

            /* Loop through the selected rows */
            for (T myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Determine row */
                row = myRow.indexOf();

                /* Access as data */
                myData = myClass.cast(myRow);

                /* Determine flags */
                isEnabled = myData.getEnabled();
                isActive = myData.isActive();

                /* If we are enabling */
                if (doEnable) {
                    /* Ignore enabled rows */
                    if (isEnabled) {
                        continue;
                    }

                    /* Enable the row */
                    myData.setEnabled(true);

                    /* If we are disabling */
                } else {
                    /* Ignore disabled/active rows */
                    if ((!isEnabled) || (isActive)) {
                        continue;
                    }

                    /* Enable the row */
                    myData.setEnabled(false);
                }

                /* set the tax credit value */
                theModel.fireTableCellUpdated(row, COLUMN_ENABLED);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class StaticColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 676363206266447113L;

        /**
         * Integer renderer.
         */
        private final IntegerRenderer theIntegerRenderer;

        /**
         * String renderer.
         */
        private final StringRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringEditor theStringEditor;

        /**
         * Constructor.
         */
        private StaticColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theIntegerRenderer = new IntegerRenderer();
            theStringRenderer = new StringRenderer();
            theStringEditor = new StringEditor();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_CLASS, WIDTH_CLASS, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            addColumn(new DataColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new DataColumn(COLUMN_ORDER, WIDTH_ORDER, theIntegerRenderer, null));
            addColumn(new DataColumn(COLUMN_ENABLED, WIDTH_ENABLED, theStringRenderer, null));
            addColumn(new DataColumn(COLUMN_ACTIVE, WIDTH_ACTIVE, theStringRenderer, null));
        }
    }
}
