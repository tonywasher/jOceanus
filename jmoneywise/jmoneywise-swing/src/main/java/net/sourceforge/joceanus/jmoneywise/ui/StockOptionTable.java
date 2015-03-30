/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest.StockOptionVestList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.StockOptionPanel;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.event.swing.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Options Table.
 */
public class StockOptionTable
        extends JDataTable<StockOption, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -984057825141989844L;

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = StockOption.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = StockOption.FIELD_DESC.getName();

    /**
     * StockHolding Column Title.
     */
    private static final String TITLE_STOCKHOLDING = StockOption.FIELD_STOCKHOLDING.getName();

    /**
     * Closed Column Title.
     */
    private static final String TITLE_CLOSED = StockOption.FIELD_CLOSED.getName();

    /**
     * ShowClosed prompt.
     */
    private static final String PROMPT_CLOSED = MoneyWiseUIControlResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = PrometheusUIResource.STATIC_TITLE_ACTIVE.getValue();

    /**
     * LastTransaction Column Title.
     */
    private static final String TITLE_LASTTRAN = MoneyWiseUIResource.ASSET_COLUMN_LATEST.getValue();

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final transient UpdateEntry<StockOption, MoneyWiseDataType> theOptionEntry;

    /**
     * OptionVest Update Entry.
     */
    private final transient UpdateEntry<StockOptionVest, MoneyWiseDataType> theVestEntry;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The Table Model.
     */
    private final StockOptionTableModel theModel;

    /**
     * The Column Model.
     */
    private final StockOptionColumnModel theColumns;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The filter panel.
     */
    private final JEnablePanel theFilterPanel;

    /**
     * The locked check box.
     */
    private final JCheckBox theLockedCheckBox;

    /**
     * The new button.
     */
    private final JButton theNewButton;

    /**
     * The Option dialog.
     */
    private final StockOptionPanel theActiveAccount;

    /**
     * The List Selection Model.
     */
    private final transient JDataTableSelection<StockOption, MoneyWiseDataType> theSelectionModel;

    /**
     * Options.
     */
    private transient StockOptionList theOptions = null;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public StockOptionTable(final View pView,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final ErrorPanel pError) {
        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theOptionEntry = theUpdateSet.registerType(MoneyWiseDataType.STOCKOPTION);
        theVestEntry = theUpdateSet.registerType(MoneyWiseDataType.STOCKOPTIONVEST);
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new StockOptionTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StockOptionColumnModel(this);
        setColumnModel(theColumns);
        theColumns.setColumns();

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the CheckBox */
        theLockedCheckBox = new JCheckBox(PROMPT_CLOSED);

        /* Create new button */
        theNewButton = MoneyWiseIcons.getNewButton();

        /* Create the filter panel */
        theFilterPanel = new JEnablePanel();
        theFilterPanel.setLayout(new BoxLayout(theFilterPanel, BoxLayout.X_AXIS));
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(theLockedCheckBox);
        theFilterPanel.add(Box.createHorizontalGlue());
        theFilterPanel.add(theNewButton);
        theFilterPanel.add(Box.createRigidArea(new Dimension(AccountPanel.STRUT_WIDTH, 0)));

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(getScrollPane());

        /* Create an account panel */
        theActiveAccount = new StockOptionPanel(theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveAccount);

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<StockOption, MoneyWiseDataType>(this, theActiveAccount);

        /* Create listener */
        new StockOptionListener();
    }

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected JPanel getFilterPanel() {
        return theFilterPanel;
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveAccount.isEditing();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final JDataEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theOptionEntry.getName());
    }

    /**
     * Refresh data.
     * @throws JOceanusException on error
     */
    protected void refreshData() throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Options");

        /* Get the Portfolios edit list */
        MoneyWiseData myData = theView.getData();
        StockOptionList myOptions = myData.getStockOptions();
        theOptions = myOptions.deriveEditList(theUpdateSet);
        theOptionEntry.setDataList(theOptions);

        /* Get the StockOption Vest list */
        StockOptionVestList myVests = myData.getStockOptionVests();
        myVests = myVests.deriveEditList(theUpdateSet);
        theVestEntry.setDataList(myVests);

        /* Notify panel of refresh */
        theActiveAccount.refreshData();

        /* Notify of the change */
        setList(theOptions);

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void setShowAll(final boolean pShow) {
        super.setShowAll(pShow);
        theColumns.setColumns();
    }

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    @Override
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    @Override
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
    }

    @Override
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    @Override
    public void cancelEditing() {
        /* Cancel editing on table */
        super.cancelEditing();

        /* Stop editing any item */
        theActiveAccount.setEditable(false);
    }

    /**
     * Select StockOption.
     * @param pOption the option to select
     */
    protected void selectOption(final StockOption pOption) {
        /* Find the item in the list */
        int myIndex = theOptions.indexOf(pOption);
        myIndex = convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveAccount.isEditing());

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * JTable Data Model.
     */
    private final class StockOptionTableModel
            extends JDataTableModel<StockOption, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 2054182612545665424L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private StockOptionTableModel(final StockOptionTable pTable) {
            /* call constructor */
            super(pTable);
        }

        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                       ? 0
                                       : theColumns.getDeclaredCount();
        }

        @Override
        public int getRowCount() {
            return (theOptions == null)
                                       ? 0
                                       : theOptions.size();
        }

        @Override
        public JDataField getFieldForCell(final StockOption pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final StockOption pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public StockOption getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theOptions.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final StockOption pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final StockOption pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final StockOption pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return showAll() || !pRow.isDisabled();
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Protect against Exceptions */
            try {
                /* Create the new option */
                StockOption myOption = new StockOption(theOptions);
                myOption.setDefaults(theUpdateSet);

                /* Add the new item */
                myOption.setNewVersion();
                theOptions.append(myOption);

                /* Validate the new item and notify of the changes */
                myOption.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveAccount.setNewItem(myOption);

                /* Handle Exceptions */
            } catch (JOceanusException e) {
                /* Build the error */
                JOceanusException myError = new JMoneyWiseDataException("Failed to create new account", e);

                /* Show the error */
                setError(myError);
            }
        }
    }

    /**
     * Listener class.
     */
    private final class StockOptionListener
            implements ActionListener, ItemListener, ChangeListener, JOceanusChangeEventListener {
        /**
         * UpdateSet Registration.
         */
        private final JOceanusChangeRegistration theUpdateSetReg;

        /**
         * Constructor.
         */
        private StockOptionListener() {
            /* Register listeners */
            theUpdateSetReg = theUpdateSet.getEventRegistrar().addChangeListener(this);

            /* Listen to swing events */
            theNewButton.addActionListener(this);
            theLockedCheckBox.addItemListener(this);
            theActiveAccount.addChangeListener(this);
            theActiveAccount.addActionListener(this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If we are performing a rewind */
            if (theUpdateSetReg.isRelevant(pEvent)) {
                /* Only action if we are not editing */
                if (!theActiveAccount.isEditing()) {
                    /* Handle the reWind */
                    theSelectionModel.handleReWind();
                }

                /* Adjust for changes */
                notifyChanges();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If we are noting change of edit state */
            if (theActiveAccount.equals(o)) {
                /* Only action if we are not editing */
                if (!theActiveAccount.isEditing()) {
                    /* handle the edit transition */
                    theSelectionModel.handleEditTransition();
                }

                /* Note changes */
                notifyChanges();
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent pEvent) {
            /* Access reporting object and command */
            Object o = pEvent.getSource();

            /* if this is the locked check box reporting */
            if (theLockedCheckBox.equals(o)) {
                /* Adjust the showAll settings */
                setShowAll(theLockedCheckBox.isSelected());
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* Handle actions */
            if ((theActiveAccount.equals(o))
                && (pEvent instanceof ActionDetailEvent)) {
                cascadeActionEvent((ActionDetailEvent) pEvent);
            } else if (theNewButton.equals(o)) {
                theModel.addNewItem();
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class StockOptionColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -1924047563738775420L;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 0;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 1;

        /**
         * StockHolding column id.
         */
        private static final int COLUMN_STOCKHOLDING = 2;

        /**
         * Closed column id.
         */
        private static final int COLUMN_CLOSED = 3;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 4;

        /**
         * LastTran column id.
         */
        private static final int COLUMN_LASTTRAN = 5;

        /**
         * Closed Icon Renderer.
         */
        private final IconButtonCellRenderer<Boolean> theClosedIconRenderer;

        /**
         * Status Icon Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theStatusIconRenderer;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Closed Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theClosedIconEditor;

        /**
         * Status Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theStatusIconEditor;

        /**
         * Holding ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<SecurityHolding> theHoldingEditor;

        /**
         * Closed column.
         */
        private final JDataTableColumn theClosedColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private StockOptionColumnModel(final StockOptionTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theClosedIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theHoldingEditor = theFieldMgr.allocateScrollButtonCellEditor(SecurityHolding.class);
            theClosedIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theClosedIconEditor);
            theStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theStatusIconEditor);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButtons */
            MoneyWiseIcons.buildLockedButton(theClosedIconEditor.getComplexState());
            MoneyWiseIcons.buildStatusButton(theStatusIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_STOCKHOLDING, WIDTH_NAME, theStringRenderer, theHoldingEditor));
            theClosedColumn = new JDataTableColumn(COLUMN_CLOSED, WIDTH_ICON, theClosedIconRenderer, theClosedIconEditor);
            declareColumn(theClosedColumn);
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theStatusIconRenderer, theStatusIconEditor));
            declareColumn(new JDataTableColumn(COLUMN_LASTTRAN, WIDTH_DATE, theDateRenderer));

            /* Initialise the columns */
            setColumns();

            /* Add listeners */
            new EditorListener();
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on mode */
            if (showAll()) {
                revealColumn(theClosedColumn);
            } else {
                hideColumn(theClosedColumn);
            }
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_STOCKHOLDING:
                    return TITLE_STOCKHOLDING;
                case COLUMN_CLOSED:
                    return TITLE_CLOSED;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                case COLUMN_LASTTRAN:
                    return TITLE_LASTTRAN;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the Option column.
         * @param pOption StockOption
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final StockOption pOption,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pOption.getName();
                case COLUMN_STOCKHOLDING:
                    return pOption.getStockHolding();
                case COLUMN_DESC:
                    return pOption.getDesc();
                case COLUMN_CLOSED:
                    return pOption.isClosed();
                case COLUMN_ACTIVE:
                    return pOption.isActive()
                                             ? ActionType.ACTIVE
                                             : ActionType.DELETE;
                case COLUMN_LASTTRAN:
                    Transaction myTran = pOption.getLatest();
                    return (myTran == null)
                                           ? null
                                           : myTran.getDate();
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws JOceanusException on error
         */
        private void setItemValue(final StockOption pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_STOCKHOLDING:
                    pItem.setStockHolding(theOptions.declareStockHolding((SecurityHolding) pValue));
                    break;
                case COLUMN_CLOSED:
                    pItem.setClosed((Boolean) pValue);
                    break;
                case COLUMN_ACTIVE:
                    pItem.setDeleted(true);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final StockOption pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_STOCKHOLDING:
                case COLUMN_ACTIVE:
                    return !pItem.isActive();
                case COLUMN_CLOSED:
                    return pItem.isClosed() || !pItem.isRelevant();
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        protected JDataField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return StockOption.FIELD_NAME;
                case COLUMN_DESC:
                    return StockOption.FIELD_DESC;
                case COLUMN_STOCKHOLDING:
                    return StockOption.FIELD_STOCKHOLDING;
                case COLUMN_CLOSED:
                    return StockOption.FIELD_CLOSED;
                case COLUMN_ACTIVE:
                    return StockOption.FIELD_TOUCH;
                default:
                    return null;
            }
        }

        /**
         * EditorListener.
         */
        private final class EditorListener
                implements ChangeListener {
            /**
             * Constructor.
             */
            private EditorListener() {
                theHoldingEditor.addChangeListener(this);
            }

            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                Object o = pEvent.getSource();

                if (theHoldingEditor.equals(o)) {
                    buildHoldingMenu();
                }
            }

            /**
             * Build the popUpMenu for security holding.
             */
            private void buildHoldingMenu() {
                /* Access details */
                JScrollMenuBuilder<SecurityHolding> myBuilder = theHoldingEditor.getMenuBuilder();

                /* Record active item */
                Point myCell = theHoldingEditor.getPoint();
                StockOption myOption = theOptions.get(myCell.y);

                /* Build the menu */
                theActiveAccount.buildHoldingMenu(myBuilder, myOption);
            }
        }
    }
}
