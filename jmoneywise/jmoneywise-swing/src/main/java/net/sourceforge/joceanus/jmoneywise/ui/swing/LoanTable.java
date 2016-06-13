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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseErrorPanel;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.swing.LoanPanel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingCheckBox;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Loan Table.
 */
public class LoanTable
        extends JDataTable<Loan, MoneyWiseDataType> {
    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = Loan.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = Loan.FIELD_DESC.getName();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = Loan.FIELD_CATEGORY.getName();

    /**
     * Parent Column Title.
     */
    private static final String TITLE_PARENT = Loan.FIELD_PARENT.getName();

    /**
     * Currency Column Title.
     */
    private static final String TITLE_CURRENCY = Loan.FIELD_CURRENCY.getName();

    /**
     * Closed Column Title.
     */
    private static final String TITLE_CLOSED = Loan.FIELD_CLOSED.getName();

    /**
     * ShowClosed prompt.
     */
    private static final String PROMPT_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

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
    private final SwingView theView;

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final UpdateEntry<Loan, MoneyWiseDataType> theLoanEntry;

    /**
     * LoanInfo Update Entry.
     */
    private final UpdateEntry<LoanInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The error panel.
     */
    private final MoneyWiseErrorPanel<JComponent, Icon> theError;

    /**
     * The Table Model.
     */
    private final LoanTableModel theModel;

    /**
     * The Column Model.
     */
    private final LoanColumnModel theColumns;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The filter panel.
     */
    private final TethysSwingBoxPaneManager theFilterPanel;

    /**
     * The locked check box.
     */
    private final TethysSwingCheckBox theLockedCheckBox;

    /**
     * The new button.
     */
    private final TethysSwingButton theNewButton;

    /**
     * The Loan dialog.
     */
    private final LoanPanel theActiveAccount;

    /**
     * The List Selection Model.
     */
    private final JDataTableSelection<Loan, MoneyWiseDataType> theSelectionModel;

    /**
     * Loans.
     */
    private LoanList theLoans;

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public LoanTable(final SwingView pView,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final MoneyWiseErrorPanel<JComponent, Icon> pError) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Access the GUI Factory */
        TethysSwingGuiFactory myFactory = pView.getGuiFactory();

        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theLoanEntry = theUpdateSet.registerType(MoneyWiseDataType.LOAN);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.LOANINFO);
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new LoanTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new LoanColumnModel(this);
        JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the CheckBox */
        theLockedCheckBox = myFactory.newCheckBox(PROMPT_CLOSED);

        /* Create new button */
        theNewButton = myFactory.newButton();
        PrometheusIcon.configureNewIconButton(theNewButton);

        /* Create the filter panel */
        theFilterPanel = myFactory.newHBoxPane();
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theLockedCheckBox);
        theFilterPanel.addSpacer();
        theFilterPanel.addNode(theNewButton);

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Create an account panel */
        theActiveAccount = new LoanPanel(myFactory, theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveAccount.getNode(), BorderLayout.PAGE_END);

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<>(this, theActiveAccount);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theActiveAccount.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveAccount.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);

        /* Listen to swing events */
        theNewButton.getEventRegistrar().addEventListener(e -> theModel.addNewItem());
        theLockedCheckBox.getEventRegistrar().addEventListener(e -> setShowAll(theLockedCheckBox.isSelected()));
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * Obtain the filter panel.
     * @return the filter panel
     */
    protected TethysSwingBoxPaneManager getFilterPanel() {
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
    protected void determineFocus(final MetisViewerEntry pEntry) {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theLoanEntry.getName());
    }

    /**
     * Refresh data.
     * @throws OceanusException on error
     */
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Loans");

        /* Access the various lists */
        MoneyWiseData myData = theView.getData();

        /* Obtain the loan edit list */
        LoanList myLoans = myData.getLoans();
        theLoans = myLoans.deriveEditList(theUpdateSet);
        theLoanEntry.setDataList(theLoans);
        LoanInfoList myInfo = theLoans.getLoanInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActiveAccount.refreshData();

        /* Notify of the change */
        setList(theLoans);

        /* Complete the task */
        myTask.end();
    }

    @Override
    public void setShowAll(final boolean pShow) {
        super.setShowAll(pShow);
        theColumns.setColumns();
    }

    @Override
    protected void setError(final OceanusException pError) {
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
     * Select loan.
     * @param pLoan the loan to select
     */
    protected void selectLoan(final Loan pLoan) {
        /* Find the item in the list */
        int myIndex = theLoans.indexOf(pLoan);
        myIndex = getTable().convertRowIndexToView(myIndex);
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
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveAccount.isEditing()) {
            /* Handle the reWind */
            theSelectionModel.handleReWind();
        }

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveAccount.isEditing()) {
            /* handle the edit transition */
            theSelectionModel.handleEditTransition();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * JTable Data Model.
     */
    private final class LoanTableModel
            extends JDataTableModel<Loan, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -3418228782485601783L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private LoanTableModel(final LoanTable pTable) {
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
            return (theLoans == null)
                                      ? 0
                                      : theLoans.size();
        }

        @Override
        public MetisField getFieldForCell(final Loan pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Loan pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Loan getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theLoans.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final Loan pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final Loan pItem,
                                 final int pColIndex,
                                 final Object pValue) throws OceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final Loan pRow) {
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
                /* Create the new loan */
                Loan myLoan = new Loan(theLoans);
                myLoan.setDefaults(theUpdateSet);

                /* Add the new item */
                myLoan.setNewVersion();
                theLoans.append(myLoan);

                /* Validate the new item and notify of the changes */
                myLoan.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveAccount.setNewItem(myLoan);

                /* Handle Exceptions */
            } catch (OceanusException e) {
                /* Build the error */
                OceanusException myError = new MoneyWiseDataException("Failed to create new account", e);

                /* Show the error */
                setError(myError);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class LoanColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4570479051836834291L;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 0;

        /**
         * Category column id.
         */
        private static final int COLUMN_CATEGORY = 1;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 2;

        /**
         * Parent column id.
         */
        private static final int COLUMN_PARENT = 3;

        /**
         * Currency column id.
         */
        private static final int COLUMN_CURR = 4;

        /**
         * Closed column id.
         */
        private static final int COLUMN_CLOSED = 5;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 6;

        /**
         * LastTran column id.
         */
        private static final int COLUMN_LASTTRAN = 7;

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
         * Category ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<LoanCategory> theCategoryEditor;

        /**
         * Parent ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<Payee> theParentEditor;

        /**
         * Currency ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<AssetCurrency> theCurrencyEditor;

        /**
         * Closed column.
         */
        private final JDataTableColumn theClosedColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private LoanColumnModel(final LoanTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theClosedIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theCategoryEditor = theFieldMgr.allocateScrollButtonCellEditor(LoanCategory.class);
            theParentEditor = theFieldMgr.allocateScrollButtonCellEditor(Payee.class);
            theCurrencyEditor = theFieldMgr.allocateScrollButtonCellEditor(AssetCurrency.class);
            theClosedIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theClosedIconEditor);
            theStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theStatusIconEditor);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButtons */
            MoneyWiseIcons.buildLockedButton(theClosedIconEditor.getComplexState());
            MoneyWiseIcons.buildStatusButton(theStatusIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer, theCategoryEditor));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_PARENT, WIDTH_NAME, theStringRenderer, theParentEditor));
            declareColumn(new JDataTableColumn(COLUMN_CURR, WIDTH_CURR, theStringRenderer, theCurrencyEditor));
            theClosedColumn = new JDataTableColumn(COLUMN_CLOSED, WIDTH_ICON, theClosedIconRenderer, theClosedIconEditor);
            declareColumn(theClosedColumn);
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theStatusIconRenderer, theStatusIconEditor));
            declareColumn(new JDataTableColumn(COLUMN_LASTTRAN, WIDTH_DATE, theDateRenderer));

            /* Initialise the columns */
            setColumns();

            /* Add listeners */
            theCategoryEditor.getEventRegistrar().addEventListener(e -> buildCategoryMenu());
            theCurrencyEditor.getEventRegistrar().addEventListener(e -> buildCurrencyMenu());
            theParentEditor.getEventRegistrar().addEventListener(e -> buildParentMenu());
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
                case COLUMN_CATEGORY:
                    return TITLE_CAT;
                case COLUMN_PARENT:
                    return TITLE_PARENT;
                case COLUMN_CURR:
                    return TITLE_CURRENCY;
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
         * Obtain the value for the Loan column.
         * @param pLoan Loan
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final Loan pLoan,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pLoan.getName();
                case COLUMN_CATEGORY:
                    return pLoan.getCategory();
                case COLUMN_DESC:
                    return pLoan.getDesc();
                case COLUMN_PARENT:
                    return pLoan.getParent();
                case COLUMN_CURR:
                    return pLoan.getAssetCurrency();
                case COLUMN_CLOSED:
                    return pLoan.isClosed();
                case COLUMN_ACTIVE:
                    return pLoan.isActive()
                                            ? ActionType.ACTIVE
                                            : ActionType.DELETE;
                case COLUMN_LASTTRAN:
                    Transaction myTran = pLoan.getLatest();
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
         * @throws OceanusException on error
         */
        private void setItemValue(final Loan pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pItem.setLoanCategory((LoanCategory) pValue);
                    pItem.autoCorrect(theUpdateSet);
                    break;
                case COLUMN_PARENT:
                    pItem.setParent((Payee) pValue);
                    break;
                case COLUMN_CURR:
                    pItem.setAssetCurrency((AssetCurrency) pValue);
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
        private boolean isCellEditable(final Loan pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_PARENT:
                    return !pItem.isClosed();
                case COLUMN_CATEGORY:
                case COLUMN_CURR:
                case COLUMN_ACTIVE:
                    return !pItem.isActive();
                case COLUMN_CLOSED:
                    return pItem.isClosed()
                                            ? !pItem.getParent().isClosed()
                                            : !pItem.isRelevant();
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        protected MetisField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return Loan.FIELD_NAME;
                case COLUMN_DESC:
                    return Loan.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return Loan.FIELD_CATEGORY;
                case COLUMN_PARENT:
                    return Loan.FIELD_PARENT;
                case COLUMN_CURR:
                    return Loan.FIELD_CURRENCY;
                case COLUMN_CLOSED:
                    return Loan.FIELD_CLOSED;
                case COLUMN_ACTIVE:
                    return Loan.FIELD_TOUCH;
                default:
                    return null;
            }
        }

        /**
         * Build the popUpMenu for parents.
         */
        private void buildParentMenu() {
            /* Access details */
            JScrollMenuBuilder<Payee> myBuilder = theParentEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theParentEditor.getPoint();
            Loan myLoan = theLoans.get(myCell.y);

            /* Build the menu */
            theActiveAccount.buildParentMenu(myBuilder, myLoan);
        }

        /**
         * Obtain the popUpMenu for categories.
         */
        private void buildCategoryMenu() {
            /* Access details */
            JScrollMenuBuilder<LoanCategory> myBuilder = theCategoryEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theCategoryEditor.getPoint();
            Loan myLoan = theLoans.get(myCell.y);

            /* Build the menu */
            theActiveAccount.buildCategoryMenu(myBuilder, myLoan);
        }

        /**
         * Build the popUpMenu for currencies.
         */
        private void buildCurrencyMenu() {
            /* Access details */
            JScrollMenuBuilder<AssetCurrency> myBuilder = theCurrencyEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theCurrencyEditor.getPoint();
            Loan myLoan = theLoans.get(myCell.y);

            /* Build the menu */
            theActiveAccount.buildCurrencyMenu(myBuilder, myLoan);
        }
    }
}
