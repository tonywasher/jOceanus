/*******************************************************************************
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

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.CalendarCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.DilutionCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.IntegerCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.MoneyCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.ScrollListButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellEditor.UnitsCellEditor;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.IntegerCellRenderer;
import net.sourceforge.joceanus.jmetis.field.swing.MetisSwingFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.TransactionBuilder;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.AnalysisColumnSet;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.AnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.swing.TransactionPanel;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisView;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusSwingActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusSwingErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollListButton.JScrollListMenuBuilder;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButton.TethysSwingScrollButtonManager;

/**
 * Analysis Statement.
 */
public class TransactionTable
        extends JDataTable<Transaction, MoneyWiseDataType> {
    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.REGISTER_DATAENTRY.getValue();

    /**
     * Text for Filter DataEntry Title.
     */
    private static final String NLS_FILTERDATAENTRY = MoneyWiseUIResource.FILTER_DATAENTRY.getValue();

    /**
     * Text for Transactions DataEntry Title.
     */
    private static final String NLS_TRANSDATAENTRY = MoneyWiseUIResource.TRANSACTION_DATAENTRY.getValue();

    /**
     * Date Column Title.
     */
    private static final String TITLE_DATE = Transaction.FIELD_DATE.getName();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = Transaction.FIELD_CATEGORY.getName();

    /**
     * Account Column Title.
     */
    private static final String TITLE_ACCOUNT = Transaction.FIELD_ACCOUNT.getName();

    /**
     * Direction Column Title.
     */
    private static final String TITLE_DIRECTION = MoneyWiseUIResource.STATEMENT_COLUMN_DIRECTION.getValue();

    /**
     * Partner Column Title.
     */
    private static final String TITLE_PARTNER = Transaction.FIELD_PARTNER.getName();

    /**
     * Reconciled Column Title.
     */
    private static final String TITLE_RECONCILED = MoneyWiseUIResource.STATEMENT_COLUMN_RECONCILED.getValue();

    /**
     * Debited Column Title.
     */
    private static final String TITLE_DEBITED = MoneyWiseUIResource.STATEMENT_COLUMN_DEBIT.getValue();

    /**
     * Credited Column Title.
     */
    private static final String TITLE_CREDITED = MoneyWiseUIResource.STATEMENT_COLUMN_CREDIT.getValue();

    /**
     * Balance Column Title.
     */
    private static final String TITLE_BALANCE = MoneyWiseUIResource.STATEMENT_COLUMN_BALANCE.getValue();

    /**
     * Amount column title.
     */
    private static final String TITLE_AMOUNT = Transaction.FIELD_AMOUNT.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = TransactionInfoClass.COMMENTS.toString();

    /**
     * Tags column title.
     */
    private static final String TITLE_TAGS = TransactionInfoClass.TRANSTAG.toString();

    /**
     * Reference column title.
     */
    private static final String TITLE_REF = TransactionInfoClass.REFERENCE.toString();

    /**
     * CreditUnits Column Title.
     */
    private static final String TITLE_CREDUNITS = TransactionInfoClass.CREDITUNITS.toString();

    /**
     * DebitUnits Column Title.
     */
    private static final String TITLE_DEBUNITS = TransactionInfoClass.DEBITUNITS.toString();

    /**
     * Dilution Column Title.
     */
    private static final String TITLE_DILUTION = TransactionInfoClass.DILUTION.toString();

    /**
     * QualifyYears Column Title.
     */
    private static final String TITLE_QUALYEARS = MoneyWiseUIResource.STATEMENT_COLUMN_YEARS.getValue();

    /**
     * ThirdParty Column Title.
     */
    private static final String TITLE_3RDPARTY = TransactionInfoClass.THIRDPARTY.toString();

    /**
     * TaxCredit Column Title.
     */
    private static final String TITLE_TAXCREDIT = TransactionInfoClass.TAXCREDIT.toString();

    /**
     * NatInsurance Column Title.
     */
    private static final String TITLE_NATINS = TransactionInfoClass.NATINSURANCE.toString();

    /**
     * DeemedBenefit Column Title.
     */
    private static final String TITLE_BENEFIT = TransactionInfoClass.DEEMEDBENEFIT.toString();

    /**
     * CharityDonation Column Title.
     */
    private static final String TITLE_DONATION = TransactionInfoClass.CHARITYDONATION.toString();

    /**
     * Action Column Title.
     */
    private static final String TITLE_ACTION = MoneyWiseUIResource.COLUMN_ACTION.getValue();

    /**
     * Opening Balance Text.
     */
    private static final String TEXT_OPENBALANCE = MoneyWiseUIResource.STATEMENT_OPENINGBALANCE.getValue();

    /**
     * The data view.
     */
    private final View theView;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The field manager.
     */
    private final MetisFieldManager theFieldMgr;

    /**
     * The analysis data entry.
     */
    private final MetisViewerEntry theDataAnalysis;

    /**
     * The filter data entry.
     */
    private final MetisViewerEntry theDataFilter;

    /**
     * Analysis View.
     */
    private final AnalysisView theAnalysisView;

    /**
     * Analysis Selection panel.
     */
    private final AnalysisSelect theSelect;

    /**
     * The action buttons.
     */
    private final PrometheusSwingActionButtons theActionButtons;

    /**
     * The error panel.
     */
    private final PrometheusSwingErrorPanel theError;

    /**
     * The table model.
     */
    private final AnalysisTableModel theModel;

    /**
     * The Column Model.
     */
    private final AnalysisColumnModel theColumns;

    /**
     * The panel.
     */
    private final TethysSwingEnablePanel thePanel;

    /**
     * The date range.
     */
    private TethysDateRange theRange;

    /**
     * The analysis filter.
     */
    private AnalysisFilter<?, ?> theFilter;

    /**
     * Transactions.
     */
    private TransactionList theTransactions;

    /**
     * Statement Header.
     */
    private Transaction theHeader;

    /**
     * The new button.
     */
    private final TethysSwingScrollButtonManager<String> theNewButton;

    /**
     * The Transaction dialog.
     */
    private final TransactionPanel theActiveTrans;

    /**
     * The List Selection Model.
     */
    private final JDataTableSelection<Transaction, MoneyWiseDataType> theSelectionModel;

    /**
     * TransactionBuilder.
     */
    private final TransactionBuilder theBuilder;

    /**
     * Constructor.
     * @param pView the data view
     */
    public TransactionTable(final SwingView pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = pView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet<>(theView, MoneyWiseDataType.class);
        setUpdateSet(theUpdateSet);
        theBuilder = new TransactionBuilder(theUpdateSet);

        /* Create the top level debug entry for this view */
        MetisViewerManager myDataMgr = theView.getViewerManager();
        MetisViewerEntry mySection = theView.getDataEntry(DataControl.DATA_VIEWS);
        MetisViewerEntry myDataRegister = myDataMgr.newEntry(NLS_DATAENTRY);
        myDataRegister.addAsChildOf(mySection);
        theDataFilter = myDataMgr.newEntry(NLS_FILTERDATAENTRY);
        theDataFilter.addAsChildOf(myDataRegister);
        theDataAnalysis = myDataMgr.newEntry(NLS_TRANSDATAENTRY);
        theDataAnalysis.addAsChildOf(myDataRegister);
        theDataAnalysis.setObject(theUpdateSet);

        /* Create new button */
        theNewButton = new TethysSwingScrollButtonManager<>();
        PrometheusIcon.configureNewScrollButton(theNewButton);

        /* Create the Analysis View */
        theAnalysisView = new AnalysisView(theView, theUpdateSet);

        /* Create the Analysis Selection */
        theSelect = new AnalysisSelect(theView, theAnalysisView, theNewButton);

        /* Create the action buttons */
        theActionButtons = new PrometheusSwingActionButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new PrometheusSwingErrorPanel(myDataMgr, myDataRegister);

        /* Create the table model */
        theModel = new AnalysisTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new AnalysisColumnModel(this);
        JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Create the header panel */
        JPanel myHeader = new TethysSwingEnablePanel();
        myHeader.setLayout(new BorderLayout());
        myHeader.add(theSelect.getNode(), BorderLayout.CENTER);
        myHeader.add(theError.getNode(), BorderLayout.PAGE_START);
        myHeader.add(theActionButtons.getNode(), BorderLayout.LINE_END);

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(myHeader, BorderLayout.PAGE_START);
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Create a transaction panel */
        theActiveTrans = new TransactionPanel(theFieldMgr, theUpdateSet, theBuilder, theSelect, theError);
        thePanel.add(theActiveTrans.getNode(), BorderLayout.PAGE_END);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<>(this, theActiveTrans);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theView.getEventRegistrar().addEventListener(e -> refreshData());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theSelect.getEventRegistrar().addEventListener(e -> handleFilterSelection());
        theActiveTrans.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveTrans.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);

        /* Listen to swing events */
        theNewButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theModel.addNewItem());

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Initialise the filter */
        theFilter = theSelect.getFilter();
        theColumns.adjustColumns(theSelect.showColumns()
                                                         ? theSelect.getColumns()
                                                         : AnalysisColumnSet.BALANCE);
    }

    /**
     * Obtain the node.
     * @return the node
     */
    public JComponent getNode() {
        return thePanel;
    }

    /**
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveTrans.isEditing();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        getTable().requestFocusInWindow();

        /* Set the required focus */
        theDataAnalysis.setFocus();
    }

    /**
     * Select Statement.
     * @param pSelect the selection
     */
    protected void selectStatement(final StatementSelect pSelect) {
        /* Update selection */
        theSelect.selectStatement(pSelect);

        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Ensure that columns are correct */
        theColumns.adjustColumns(theSelect.showColumns()
                                                         ? theSelect.getColumns()
                                                         : AnalysisColumnSet.BALANCE);

        /* Update the lists */
        updateList();
        theSelectionModel.handleNewFilter();
    }

    /**
     * Refresh data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Statement");

        /* Update the selection */
        theSelect.refreshData();

        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Update the list */
        updateList();

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void setError(final OceanusException pError) {
        theError.addError(pError);
    }

    @Override
    public void notifyChanges() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();
        boolean isItemEditing = theActiveTrans.isEditing();

        /* Update the table buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);
        theSelect.setEnabled(!hasUpdates && !isItemEditing);
        theNewButton.setEnabled(!isItemEditing);

        /* Adjust enable of the table */
        setEnabled(!isItemEditing);

        /* Pass call on */
        super.notifyChanges();
    }

    @Override
    public void setEnabled(final boolean bEnable) {
        /* Ensure that we are disabled whilst editing */
        super.setEnabled(bEnable && !isItemEditing());
        theNewButton.setEnabled(bEnable && !isItemEditing());
    }

    /**
     * Update lists.
     */
    private void updateList() {
        /* Access the transactions */
        theTransactions = theAnalysisView.getTransactions();
        theRange = theAnalysisView.getRange();
        theHeader = null;

        if (theTransactions != null) {
            /* Create the header */
            theHeader = new AnalysisHeader(theTransactions);

            /* Update the column editors */
            theColumns.refreshData();

            /* Notify panel of refresh */
            theActiveTrans.refreshData();
            theActiveTrans.updateEditors(theRange);

            /* Notify the builder */
            theBuilder.setParameters(theTransactions, theRange);
        }

        /* Update lists */
        setList(theTransactions);
        theActionButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());
        fireStateChanged();

        /* Touch the filter and updateSet */
        theDataFilter.setObject(theFilter);
        theDataAnalysis.setObject(theUpdateSet);

        /* Adjust the filter */
        theSelectionModel.handleNewFilter();
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
        theActiveTrans.setEditable(false);
    }

    /**
     * Handle updateSet rewind.
     */
    private void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveTrans.isEditing()) {
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
        if (!theActiveTrans.isEditing()) {
            /* handle the edit transition */
            theSelectionModel.handleEditTransition();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelect.setVisible(!isError);

        /* Lock scroll area */
        super.getNode().setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    /**
     * handle Action Buttons.
     * @param pEvent the event
     */
    private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
        /* Cancel editing */
        cancelEditing();

        /* Perform the command */
        theUpdateSet.processCommand(pEvent.getEventId(), theError);

        /* Adjust for changes */
        notifyChanges();
    }

    /**
     * Handle filter selection.
     */
    private void handleFilterSelection() {
        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Ensure that columns are correct */
        theColumns.adjustColumns(theSelect.showColumns()
                                                         ? theSelect.getColumns()
                                                         : AnalysisColumnSet.BALANCE);

        /* Set the selection */
        TethysDateRange myRange = theSelect.getRange();
        if (MetisDifference.isEqual(myRange, theRange)) {
            /* Handle a simple filter change */
            theModel.fireNewDataEvents();
            theSelectionModel.handleNewFilter();
            theDataFilter.setObject(theFilter);
        } else {
            /* Update new lists */
            updateList();
        }
    }

    /**
     * JTable Data Model.
     */
    private final class AnalysisTableModel
            extends JDataTableModel<Transaction, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -7384250393275180461L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private AnalysisTableModel(final TransactionTable pTable) {
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
            return (theTransactions == null)
                                             ? 0
                                             : 1 + theTransactions.size();
        }

        @Override
        public MetisField getFieldForCell(final Transaction pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Transaction pItem,
                                      final int pColIndex) {
            return pItem.isHeader()
                                    ? false
                                    : theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Transaction getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return pRowIndex == 0
                                  ? theHeader
                                  : theTransactions.get(pRowIndex - 1);
        }

        @Override
        public Object getItemValue(final Transaction pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return pItem.isHeader()
                                    ? theColumns.getHeaderValue(pColIndex)
                                    : theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final Transaction pItem,
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
        public boolean includeRow(final Transaction pRow) {
            /* Handle no filter */
            if (theFilter == null) {
                return false;
            }

            /* Return visibility of row */
            return !pRow.isDeleted() && !theFilter.filterTransaction(pRow);
        }

        /**
         * New item.
         */
        private void addNewItem() {
            /* Create the new transaction */
            Transaction myTrans = theFilter.buildNewTransaction(theBuilder);

            /* If we have one available */
            if (myTrans != null) {
                /* Add the new item */
                myTrans.setNewVersion();
                theTransactions.append(myTrans);

                /* Validate the new item and notify of the changes */
                myTrans.validate();
                incrementVersion();

                /* Lock the table */
                setEnabled(false);
                theActiveTrans.setNewItem(myTrans);
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class AnalysisColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7376205781228806385L;

        /**
         * Date column id.
         */
        private static final int COLUMN_DATE = 0;

        /**
         * Account column id.
         */
        private static final int COLUMN_ACCOUNT = 1;

        /**
         * Category column id.
         */
        private static final int COLUMN_CATEGORY = 2;

        /**
         * Direction column id.
         */
        private static final int COLUMN_DIRECTION = 3;

        /**
         * Partner column id.
         */
        private static final int COLUMN_PARTNER = 4;

        /**
         * Reconciled column id.
         */
        private static final int COLUMN_RECONCILED = 5;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 6;

        /**
         * Debited column id.
         */
        private static final int COLUMN_DEBITED = 7;

        /**
         * Credited column id.
         */
        private static final int COLUMN_CREDITED = 8;

        /**
         * Balance column id.
         */
        private static final int COLUMN_BALANCE = 9;

        /**
         * Amount column id.
         */
        private static final int COLUMN_AMOUNT = 10;

        /**
         * Tags column id.
         */
        private static final int COLUMN_TAGS = 11;

        /**
         * Reference column id.
         */
        private static final int COLUMN_REF = 12;

        /**
         * CreditUnits column id.
         */
        private static final int COLUMN_CREDUNITS = 13;

        /**
         * DebitUnits column id.
         */
        private static final int COLUMN_DEBUNITS = 14;

        /**
         * Dilution column id.
         */
        private static final int COLUMN_DILUTION = 15;

        /**
         * QualifyYears column id.
         */
        private static final int COLUMN_QUALYEARS = 16;

        /**
         * ThirdParty column id.
         */
        private static final int COLUMN_3RDPARTY = 17;

        /**
         * TaxCredit column id.
         */
        private static final int COLUMN_TAXCREDIT = 18;

        /**
         * NatInsurance column id.
         */
        private static final int COLUMN_NATINS = 19;

        /**
         * DeemedBenefit column id.
         */
        private static final int COLUMN_BENEFIT = 20;

        /**
         * CharityDonation column id.
         */
        private static final int COLUMN_DONATION = 21;

        /**
         * Action column id.
         */
        private static final int COLUMN_ACTION = 22;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Integer Renderer.
         */
        private final IntegerCellRenderer theIntegerRenderer;

        /**
         * Icon Renderer.
         */
        private final IconButtonCellRenderer<AssetDirection> theDirectionIconRenderer;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<AssetDirection> theDirectionIconEditor;

        /**
         * Icon Renderer.
         */
        private final IconButtonCellRenderer<Boolean> theReconciledIconRenderer;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theReconciledIconEditor;

        /**
         * Date editor.
         */
        private final CalendarCellEditor theDateEditor;

        /**
         * String editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Integer editor.
         */
        private final IntegerCellEditor theIntegerEditor;

        /**
         * Money editor.
         */
        private final MoneyCellEditor theMoneyEditor;

        /**
         * Units editor.
         */
        private final UnitsCellEditor theUnitsEditor;

        /**
         * Dilution editor.
         */
        private final DilutionCellEditor theDilutionEditor;

        /**
         * Action Icon Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theActionIconRenderer;

        /**
         * Action Icon editor.
         */
        private final IconButtonCellEditor<ActionType> theActionIconEditor;

        /**
         * Account ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<TransactionAsset> theAccountEditor;

        /**
         * Category ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<TransactionCategory> theCategoryEditor;

        /**
         * Deposit ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<Deposit> theDepositEditor;

        /**
         * ScrollListButton Menu Editor.
         */
        private final ScrollListButtonCellEditor<TransactionTag> theTagEditor;

        /**
         * Comments column.
         */
        private final JDataTableColumn theDescColumn;

        /**
         * Amount column.
         */
        private final JDataTableColumn thePaidColumn;

        /**
         * Amount column.
         */
        private final JDataTableColumn theReceivedColumn;

        /**
         * Amount column.
         */
        private final JDataTableColumn theBalanceColumn;

        /**
         * Amount column.
         */
        private final JDataTableColumn theAmountColumn;

        /**
         * Reference column.
         */
        private final JDataTableColumn theReferenceColumn;

        /**
         * Tags column.
         */
        private final JDataTableColumn theTagsColumn;

        /**
         * CreditUnits column.
         */
        private final JDataTableColumn theCredUnitsColumn;

        /**
         * DebitUnits column.
         */
        private final JDataTableColumn theDebUnitsColumn;

        /**
         * Dilution column.
         */
        private final JDataTableColumn theDilutionColumn;

        /**
         * QualifyYears column.
         */
        private final JDataTableColumn theQualYearsColumn;

        /**
         * ThirdParty column.
         */
        private final JDataTableColumn the3rdPartyColumn;

        /**
         * TaxCredit column.
         */
        private final JDataTableColumn theTaxCreditColumn;

        /**
         * NatIns column.
         */
        private final JDataTableColumn theNatInsColumn;

        /**
         * Benefit column.
         */
        private final JDataTableColumn theBenefitColumn;

        /**
         * Donation column.
         */
        private final JDataTableColumn theDonationColumn;

        /**
         * Action column.
         */
        private final JDataTableColumn theActionColumn;

        /**
         * ColumnSet.
         */
        private AnalysisColumnSet theColumnSet;

        /**
         * Constructor.
         * @param pTable the table
         */
        private AnalysisColumnModel(final TransactionTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theDirectionIconEditor = theFieldMgr.allocateIconButtonCellEditor(AssetDirection.class, true);
            theReconciledIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theTagEditor = theFieldMgr.allocateScrollListButtonCellEditor();
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theIntegerEditor = theFieldMgr.allocateIntegerCellEditor();
            theMoneyEditor = theFieldMgr.allocateMoneyCellEditor();
            theUnitsEditor = theFieldMgr.allocateUnitsCellEditor();
            theDilutionEditor = theFieldMgr.allocateDilutionCellEditor();
            theAccountEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionAsset.class);
            theCategoryEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionCategory.class);
            theDepositEditor = theFieldMgr.allocateScrollButtonCellEditor(Deposit.class);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theIntegerRenderer = theFieldMgr.allocateIntegerCellRenderer();
            theDirectionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theDirectionIconEditor);
            theReconciledIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theReconciledIconEditor);
            theActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theActionIconEditor);

            /* Configure the iconButtons */
            MoneyWiseIcons.buildDirectionButton(theDirectionIconEditor.getComplexState());
            MoneyWiseIcons.buildReconciledButton(theReconciledIconEditor.getComplexState());
            MoneyWiseIcons.buildStatusButton(theActionIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            declareColumn(new JDataTableColumn(COLUMN_ACCOUNT, WIDTH_NAME, theStringRenderer, theAccountEditor));
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer, theCategoryEditor));
            declareColumn(new JDataTableColumn(COLUMN_DIRECTION, WIDTH_ICON, theDirectionIconRenderer, theDirectionIconEditor));
            declareColumn(new JDataTableColumn(COLUMN_PARTNER, WIDTH_NAME, theStringRenderer, theAccountEditor));
            declareColumn(new JDataTableColumn(COLUMN_RECONCILED, WIDTH_ICON, theReconciledIconRenderer, theReconciledIconEditor));
            theDescColumn = new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor);
            declareColumn(theDescColumn);
            thePaidColumn = new JDataTableColumn(COLUMN_DEBITED, WIDTH_MONEY, theDecimalRenderer);
            declareColumn(thePaidColumn);
            theReceivedColumn = new JDataTableColumn(COLUMN_CREDITED, WIDTH_MONEY, theDecimalRenderer);
            declareColumn(theReceivedColumn);
            theBalanceColumn = new JDataTableColumn(COLUMN_BALANCE, WIDTH_MONEY, theDecimalRenderer);
            declareColumn(theBalanceColumn);
            theAmountColumn = new JDataTableColumn(COLUMN_AMOUNT, WIDTH_MONEY, theDecimalRenderer, theMoneyEditor);
            declareColumn(theAmountColumn);
            theReferenceColumn = new JDataTableColumn(COLUMN_REF, WIDTH_NAME, theStringRenderer, theStringEditor);
            declareColumn(theReferenceColumn);
            theTagsColumn = new JDataTableColumn(COLUMN_TAGS, WIDTH_NAME, theStringRenderer, theTagEditor);
            declareColumn(theTagsColumn);
            theCredUnitsColumn = new JDataTableColumn(COLUMN_CREDUNITS, WIDTH_UNITS, theDecimalRenderer, theUnitsEditor);
            declareColumn(theCredUnitsColumn);
            theDebUnitsColumn = new JDataTableColumn(COLUMN_DEBUNITS, WIDTH_UNITS, theDecimalRenderer, theUnitsEditor);
            declareColumn(theDebUnitsColumn);
            theDilutionColumn = new JDataTableColumn(COLUMN_DILUTION, WIDTH_DILUTION, theDecimalRenderer, theDilutionEditor);
            declareColumn(theDilutionColumn);
            theQualYearsColumn = new JDataTableColumn(COLUMN_QUALYEARS, WIDTH_INT << 1, theIntegerRenderer, theIntegerEditor);
            declareColumn(theQualYearsColumn);
            the3rdPartyColumn = new JDataTableColumn(COLUMN_3RDPARTY, WIDTH_NAME, theStringRenderer, theDepositEditor);
            declareColumn(the3rdPartyColumn);
            theTaxCreditColumn = new JDataTableColumn(COLUMN_TAXCREDIT, WIDTH_MONEY, theDecimalRenderer, theMoneyEditor);
            declareColumn(theTaxCreditColumn);
            theNatInsColumn = new JDataTableColumn(COLUMN_NATINS, WIDTH_MONEY, theDecimalRenderer, theMoneyEditor);
            declareColumn(theNatInsColumn);
            theBenefitColumn = new JDataTableColumn(COLUMN_BENEFIT, WIDTH_MONEY, theDecimalRenderer, theMoneyEditor);
            declareColumn(theBenefitColumn);
            theDonationColumn = new JDataTableColumn(COLUMN_DONATION, WIDTH_MONEY, theDecimalRenderer, theMoneyEditor);
            declareColumn(theDonationColumn);
            theActionColumn = new JDataTableColumn(COLUMN_ACTION, WIDTH_ICON << 1, theActionIconRenderer, theActionIconEditor);
            declareColumn(theActionColumn);

            /* Add listeners */
            theCategoryEditor.getEventRegistrar().addEventListener(e -> buildCategoryMenu());
            theAccountEditor.getEventRegistrar().addEventListener(e -> buildAccountMenu());
            theDepositEditor.getEventRegistrar().addEventListener(e -> buildThirdPartyMenu());
            theTagEditor.getEventRegistrar().addEventListener(e -> buildTagMenu());
        }

        /**
         * RefreshData.
         */
        private void refreshData() {
            /* Update the range for the date editor */
            theDateEditor.setRange(theRange);

            /* Update details for the tag menu */
            theActiveTrans.updateTagMenuBuilder(theTagEditor.getMenuBuilder());
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DATE:
                    return TITLE_DATE;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_CATEGORY:
                    return TITLE_CAT;
                case COLUMN_ACCOUNT:
                    return TITLE_ACCOUNT;
                case COLUMN_DIRECTION:
                    return TITLE_DIRECTION;
                case COLUMN_PARTNER:
                    return TITLE_PARTNER;
                case COLUMN_DEBITED:
                    return TITLE_DEBITED;
                case COLUMN_CREDITED:
                    return TITLE_CREDITED;
                case COLUMN_BALANCE:
                    return TITLE_BALANCE;
                case COLUMN_RECONCILED:
                    return TITLE_RECONCILED;
                case COLUMN_AMOUNT:
                    return TITLE_AMOUNT;
                case COLUMN_TAGS:
                    return TITLE_TAGS;
                case COLUMN_REF:
                    return TITLE_REF;
                case COLUMN_CREDUNITS:
                    return TITLE_CREDUNITS;
                case COLUMN_DEBUNITS:
                    return TITLE_DEBUNITS;
                case COLUMN_DILUTION:
                    return TITLE_DILUTION;
                case COLUMN_QUALYEARS:
                    return TITLE_QUALYEARS;
                case COLUMN_3RDPARTY:
                    return TITLE_3RDPARTY;
                case COLUMN_TAXCREDIT:
                    return TITLE_TAXCREDIT;
                case COLUMN_NATINS:
                    return TITLE_NATINS;
                case COLUMN_BENEFIT:
                    return TITLE_BENEFIT;
                case COLUMN_DONATION:
                    return TITLE_DONATION;
                case COLUMN_ACTION:
                    return TITLE_ACTION;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the column.
         * @param pTrans transaction
         * @param pColIndex column index
         * @return the value
         */
        private Object getItemValue(final Transaction pTrans,
                                    final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pTrans.getDate();
                case COLUMN_CATEGORY:
                    return pTrans.getCategory();
                case COLUMN_ACCOUNT:
                    return pTrans.getAccount();
                case COLUMN_DIRECTION:
                    return pTrans.getDirection();
                case COLUMN_PARTNER:
                    return pTrans.getPartner();
                case COLUMN_DESC:
                    return pTrans.getComments();
                case COLUMN_RECONCILED:
                    return pTrans.isReconciled();
                case COLUMN_AMOUNT:
                    return pTrans.getAmount();
                case COLUMN_REF:
                    return pTrans.getReference();
                case COLUMN_TAGS:
                    return pTrans.getTagNameList();
                case COLUMN_CREDUNITS:
                    return pTrans.getCreditUnits();
                case COLUMN_DEBUNITS:
                    return pTrans.getDebitUnits();
                case COLUMN_DILUTION:
                    return pTrans.getDilution();
                case COLUMN_QUALYEARS:
                    return pTrans.getYears();
                case COLUMN_3RDPARTY:
                    return pTrans.getThirdParty();
                case COLUMN_TAXCREDIT:
                    return pTrans.getTaxCredit();
                case COLUMN_NATINS:
                    return pTrans.getNatInsurance();
                case COLUMN_BENEFIT:
                    return pTrans.getDeemedBenefit();
                case COLUMN_DONATION:
                    return pTrans.getCharityDonation();
                case COLUMN_DEBITED:
                    return theFilter.getDebitForTransaction(pTrans);
                case COLUMN_CREDITED:
                    return theFilter.getCreditForTransaction(pTrans);
                case COLUMN_BALANCE:
                    return theFilter.getBalanceForTransaction(pTrans);
                case COLUMN_ACTION:
                    return pTrans.isReconciled()
                                                 ? ActionType.DO
                                                 : ActionType.DELETE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the header value for the event column.
         * @param pColIndex column index
         * @return the value
         */
        private Object getHeaderValue(final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return theRange.getStart();
                case COLUMN_DESC:
                    return TEXT_OPENBALANCE;
                case COLUMN_BALANCE:
                    return theFilter.getStartingBalance();
                case COLUMN_ACTION:
                    return ActionType.DO;
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
        @SuppressWarnings("unchecked")
        private void setItemValue(final Transaction pItem,
                                  final int pColIndex,
                                  final Object pValue) throws OceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pItem.setDate((TethysDate) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pItem.setCategory((TransactionCategory) pValue);
                    theBuilder.autoCorrect(pItem);
                    break;
                case COLUMN_ACCOUNT:
                    pItem.setAccount(TransactionPanel.resolveAsset((TransactionAsset) pValue));
                    theBuilder.autoCorrect(pItem);
                    break;
                case COLUMN_DIRECTION:
                    pItem.switchDirection();
                    theBuilder.autoCorrect(pItem);
                    break;
                case COLUMN_PARTNER:
                    pItem.setPartner(TransactionPanel.resolveAsset((TransactionAsset) pValue));
                    theBuilder.autoCorrect(pItem);
                    break;
                case COLUMN_DESC:
                    pItem.setComments((String) pValue);
                    break;
                case COLUMN_AMOUNT:
                    pItem.setAmount((TethysMoney) pValue);
                    theBuilder.autoCorrect(pItem);
                    break;
                case COLUMN_REF:
                    pItem.setReference((String) pValue);
                    break;
                case COLUMN_CREDUNITS:
                    pItem.setCreditUnits((TethysUnits) pValue);
                    break;
                case COLUMN_DEBUNITS:
                    pItem.setDebitUnits((TethysUnits) pValue);
                    break;
                case COLUMN_DILUTION:
                    pItem.setDilution((TethysDilution) pValue);
                    break;
                case COLUMN_QUALYEARS:
                    pItem.setYears((Integer) pValue);
                    break;
                case COLUMN_3RDPARTY:
                    pItem.setThirdParty((Deposit) pValue);
                    break;
                case COLUMN_TAXCREDIT:
                    pItem.setTaxCredit((TethysMoney) pValue);
                    break;
                case COLUMN_NATINS:
                    pItem.setNatInsurance((TethysMoney) pValue);
                    break;
                case COLUMN_BENEFIT:
                    pItem.setBenefit((TethysMoney) pValue);
                    break;
                case COLUMN_DONATION:
                    pItem.setDonation((TethysMoney) pValue);
                    break;
                case COLUMN_RECONCILED:
                    pItem.setReconciled((Boolean) pValue);
                    break;
                case COLUMN_ACTION:
                    pItem.setDeleted(true);
                    break;
                case COLUMN_TAGS:
                    theActiveTrans.updateTag(pItem, (TethysEvent<TethysUIEvent>) pValue);
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
        private boolean isCellEditable(final Transaction pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_RECONCILED:
                    return !pItem.isLocked();
                case COLUMN_DATE:
                case COLUMN_ACCOUNT:
                case COLUMN_PARTNER:
                case COLUMN_CATEGORY:
                case COLUMN_ACTION:
                    return !pItem.isReconciled();
                case COLUMN_DIRECTION:
                    return !pItem.isReconciled() && pItem.canSwitchDirection();
                case COLUMN_AMOUNT:
                    return !pItem.isReconciled() && !pItem.needsZeroAmount();
                case COLUMN_DESC:
                case COLUMN_REF:
                case COLUMN_TAGS:
                    return true;
                case COLUMN_TAXCREDIT:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.TAXCREDIT);
                case COLUMN_NATINS:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.NATINSURANCE);
                case COLUMN_BENEFIT:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.DEEMEDBENEFIT);
                case COLUMN_DONATION:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.CHARITYDONATION);
                case COLUMN_CREDUNITS:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.CREDITUNITS);
                case COLUMN_DEBUNITS:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.DEBITUNITS);
                case COLUMN_DILUTION:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.DILUTION);
                case COLUMN_3RDPARTY:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.THIRDPARTY);
                case COLUMN_QUALYEARS:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.QUALIFYYEARS);
                default:
                    return false;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        private MetisField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return Transaction.FIELD_DATE;
                case COLUMN_DESC:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS);
                case COLUMN_CATEGORY:
                    return Transaction.FIELD_CATEGORY;
                case COLUMN_ACCOUNT:
                    return Transaction.FIELD_ACCOUNT;
                case COLUMN_DIRECTION:
                    return Transaction.FIELD_DIRECTION;
                case COLUMN_PARTNER:
                    return Transaction.FIELD_PARTNER;
                case COLUMN_AMOUNT:
                    return Transaction.FIELD_AMOUNT;
                case COLUMN_REF:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE);
                case COLUMN_TAGS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG);
                case COLUMN_CREDUNITS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.CREDITUNITS);
                case COLUMN_DEBUNITS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEBITUNITS);
                case COLUMN_DILUTION:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION);
                case COLUMN_QUALYEARS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS);
                case COLUMN_3RDPARTY:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.THIRDPARTY);
                case COLUMN_TAXCREDIT:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT);
                case COLUMN_NATINS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.NATINSURANCE);
                case COLUMN_BENEFIT:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT);
                case COLUMN_DONATION:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.CHARITYDONATION);
                case COLUMN_RECONCILED:
                    return Transaction.FIELD_RECONCILED;
                default:
                    return null;
            }
        }

        /**
         * Hide all columns.
         */
        private void hideAllColumns() {
            hideColumn(thePaidColumn);
            hideColumn(theReceivedColumn);
            hideColumn(theBalanceColumn);
            hideColumn(theDescColumn);
            hideColumn(theAmountColumn);
            hideColumn(theTagsColumn);
            hideColumn(theReferenceColumn);
            hideColumn(theTaxCreditColumn);
            hideColumn(theNatInsColumn);
            hideColumn(theBenefitColumn);
            hideColumn(theDonationColumn);
            hideColumn(theCredUnitsColumn);
            hideColumn(theDebUnitsColumn);
            hideColumn(theDilutionColumn);
            hideColumn(the3rdPartyColumn);
            hideColumn(theQualYearsColumn);
        }

        /**
         * Adjust columns.
         * @param pSet the set to display.
         */
        private void adjustColumns(final AnalysisColumnSet pSet) {
            /* Ignore if we are already the right set */
            if (pSet.equals(theColumnSet)) {
                return;
            }

            /* Hide all columns */
            hideAllColumns();

            /* Switch on column set */
            boolean reSize = true;
            switch (pSet) {
                case BALANCE:
                    revealColumn(theDescColumn);
                    revealColumn(thePaidColumn);
                    revealColumn(theReceivedColumn);
                    revealColumn(theBalanceColumn);
                    break;
                case STANDARD:
                    revealColumn(theDescColumn);
                    revealColumn(theAmountColumn);
                    revealColumn(theTagsColumn);
                    revealColumn(theReferenceColumn);
                    break;
                case SALARY:
                    revealColumn(theAmountColumn);
                    revealColumn(theTaxCreditColumn);
                    revealColumn(theNatInsColumn);
                    revealColumn(theBenefitColumn);
                    break;
                case INTEREST:
                    revealColumn(theAmountColumn);
                    revealColumn(theTaxCreditColumn);
                    revealColumn(theDonationColumn);
                    break;
                case DIVIDEND:
                    revealColumn(theAmountColumn);
                    revealColumn(theTaxCreditColumn);
                    revealColumn(theCredUnitsColumn);
                    break;
                case SECURITY:
                    revealColumn(theAmountColumn);
                    revealColumn(theCredUnitsColumn);
                    revealColumn(theDebUnitsColumn);
                    revealColumn(theDilutionColumn);
                    revealColumn(the3rdPartyColumn);
                    revealColumn(theQualYearsColumn);
                    reSize = false;
                    break;
                case ALL:
                default:
                    revealColumn(theDescColumn);
                    revealColumn(theAmountColumn);
                    revealColumn(theTagsColumn);
                    revealColumn(theReferenceColumn);
                    revealColumn(theTaxCreditColumn);
                    revealColumn(theNatInsColumn);
                    revealColumn(theBenefitColumn);
                    revealColumn(theDonationColumn);
                    revealColumn(theCredUnitsColumn);
                    revealColumn(theDebUnitsColumn);
                    revealColumn(theDilutionColumn);
                    revealColumn(the3rdPartyColumn);
                    revealColumn(theQualYearsColumn);
                    reSize = false;
                    break;
            }

            /* Set reSize mode */
            getTable().setAutoResizeMode(reSize
                                                ? JTable.AUTO_RESIZE_ALL_COLUMNS
                                                : JTable.AUTO_RESIZE_OFF);

            /* Store the column set */
            theColumnSet = pSet;
        }

        /**
         * Build the popUpMenu for accounts.
         */
        private void buildAccountMenu() {
            /* Access details */
            JScrollMenuBuilder<TransactionAsset> myBuilder = theAccountEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theAccountEditor.getPoint();
            Transaction myTrans = theModel.getItemAtIndex(myCell.y);

            /* Build the menu */
            switch (myCell.x) {
                case COLUMN_ACCOUNT:
                    theActiveTrans.buildAccountMenu(myBuilder, myTrans);
                    break;
                default:
                    theActiveTrans.buildPartnerMenu(myBuilder, myTrans);
                    break;
            }
        }

        /**
         * Build the popUpMenu for categories.
         */
        private void buildCategoryMenu() {
            /* Access details */
            JScrollMenuBuilder<TransactionCategory> myBuilder = theCategoryEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theCategoryEditor.getPoint();
            Transaction myTrans = theModel.getItemAtIndex(myCell.y);

            /* Build the menu */
            theActiveTrans.buildCategoryMenu(myBuilder, myTrans);
        }

        /**
         * Obtain the popUpMenu for thirdParty deposits.
         */
        private void buildThirdPartyMenu() {
            /* Access details */
            JScrollMenuBuilder<Deposit> myBuilder = theDepositEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theDepositEditor.getPoint();
            Transaction myTrans = theModel.getItemAtIndex(myCell.y);

            /* Build the menu */
            theActiveTrans.buildThirdPartyMenu(myBuilder, myTrans);
        }

        /**
         * Build the popUpMenu for tags.
         */
        private void buildTagMenu() {
            /* Access details */
            JScrollListMenuBuilder<TransactionTag> myBuilder = theTagEditor.getMenuBuilder();

            /* Record active item */
            Point myCell = theTagEditor.getPoint();
            Transaction myTrans = theModel.getItemAtIndex(myCell.y);

            /* Build the menu */
            theActiveTrans.buildTagMenu(myBuilder, myTrans);
        }
    }

    /**
     * Analysis Header class.
     */
    private static class AnalysisHeader
            extends Transaction {
        /**
         * Constructor.
         * @param pList the Transaction list
         */
        protected AnalysisHeader(final TransactionList pList) {
            super(pList);
            setHeader(true);
        }
    }
}
