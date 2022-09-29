/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.ui.panel;

import java.util.Map;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseTransDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.ui.AnalysisColumnSet;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseAnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.MoneyWiseTransactionPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisView;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataId;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.table.TethysUITableManager;

/**
 * MoneyWise Transaction Table.
 */
public class MoneyWiseTransactionTable
        extends MoneyWiseBaseTable<Transaction> {
    /**
     * The analysis data entry.
     */
    private final MetisViewerEntry theViewerAnalysis;

    /**
     * The filter data entry.
     */
    private final MetisViewerEntry theViewerFilter;

    /**
     * The transaction dialog.
     */
    private final MoneyWiseTransactionPanel theActiveTran;

    /**
     * The new button.
     */
    private final TethysUIButton theNewButton;

    /**
     * Analysis View.
     */
    private final AnalysisView theAnalysisView;

    /**
     * Analysis Selection panel.
     */
    private final MoneyWiseAnalysisSelect theSelect;

    /**
     * The action buttons.
     */
    private final PrometheusActionButtons theActionButtons;

    /**
     * TransactionBuilder.
     */
    private final TransactionBuilder theBuilder;

    /**
     * The UpdateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The date range.
     */
    private TethysDateRange theRange;

    /**
     * The analysis filter.
     */
    private AnalysisFilter<?, ?> theFilter;

    /**
     * The edit list.
     */
    private TransactionList theTransactions;

    /**
     * ColumnSet.
     */
    private AnalysisColumnSet theColumnSet;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the updateSet
     * @param pError the error panel
     * @param pFilter the filter viewer entry
     * @param pAnalysis the analysis viewer entry
     */
    MoneyWiseTransactionTable(final MoneyWiseView pView,
                              final UpdateSet<MoneyWiseDataType> pUpdateSet,
                              final MetisErrorPanel pError,
                              final MetisViewerEntry pFilter,
                              final MetisViewerEntry pAnalysis) {
        /* Store parameters */
        super(pView, pUpdateSet, pError, MoneyWiseDataType.TRANSACTION);

        /* store parameters */
        theUpdateSet = pUpdateSet;
        theError = pError;

        /* Store viewer entries */
        theViewerAnalysis = pAnalysis;
        theViewerFilter = pFilter;

        /* Access gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<PrometheusDataFieldId, Transaction> myTable = getTable();

        /* Create new button */
        theNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(theNewButton);

        /* Create the Analysis View */
        theAnalysisView = new AnalysisView(pView, getUpdateSet());

        /* Create the Analysis Selection */
        theSelect = new MoneyWiseAnalysisSelect(myGuiFactory, pView, theAnalysisView, theNewButton);

        /* Create the action buttons */
        theActionButtons = new PrometheusActionButtons(myGuiFactory, getUpdateSet());

        /* Create the builder */
        theBuilder = new TransactionBuilder(getUpdateSet());

        /* Create a transaction panel */
        theActiveTran = new MoneyWiseTransactionPanel(myGuiFactory, pUpdateSet, theBuilder, theSelect, pError);
        declareItemPanel(theActiveTran);

        /* Set table configuration */
        myTable.setDisabled(Transaction::isDisabled)
               .setComparator(Transaction::compareTo);

        /* Create the date column */
        myTable.declareDateColumn(MoneyWiseTransDataId.DATE)
               .setDateConfigurator((r, c) -> handleDateEvent(c))
               .setCellValueFactory(this::getFilteredDate)
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
               .setColumnWidth(WIDTH_DATE)
               .setOnCommit((r, v) -> updateField(Transaction::setDate, r, v));

        /* Create the account column */
        myTable.declareScrollColumn(MoneyWiseTransDataId.ACCOUNT, TransactionAsset.class)
               .setMenuConfigurator(this::buildAccountMenu)
               .setCellValueFactory(Transaction::getAccount)
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setAccount, r, v));

        /* Create the category column */
        myTable.declareScrollColumn(MoneyWiseTransDataId.CATEGORY, TransactionCategory.class)
               .setMenuConfigurator(this::buildCategoryMenu)
               .setCellValueFactory(Transaction::getCategory)
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setCategory, r, v));

        /* Create the direction column */
        final Map<Boolean, TethysUIIconMapSet<AssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton(myGuiFactory);
        myTable.declareIconColumn(MoneyWiseTransDataId.DIRECTION, AssetDirection.class)
               .setIconMapSet(r -> myDirMapSets.get(determineDirectionState(r)))
               .setCellValueFactory(this::getFilteredDirection)
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader() && !r.isReconciled() && r.canSwitchDirection())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(this::setDirection, r, v));

        /* Create the partner column */
        myTable.declareScrollColumn(MoneyWiseTransDataId.PARTNER, TransactionAsset.class)
               .setMenuConfigurator(this::buildPartnerMenu)
               .setCellValueFactory(Transaction::getPartner)
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setPartner, r, v));

        /* Create the reconciled column */
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton(myGuiFactory);
        myTable.declareIconColumn(MoneyWiseTransDataId.RECONCILED, Boolean.class)
               .setIconMapSet(r -> myRecMapSets.get(determineReconciledState(r)))
               .setCellValueFactory(Transaction::isReconciled)
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader() && !r.isLocked())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(Transaction::setReconciled, r, v));

        /* Create the comments column */
        myTable.declareStringColumn(MoneyWiseTransDataId.COMMENTS)
               .setCellValueFactory(this::getFilteredComments)
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader())
               .setColumnWidth(WIDTH_DESC)
               .setOnCommit((r, v) -> updateField(Transaction::setComments, r, v));

        /* Create the amount column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.AMOUNT)
               .setCellValueFactory(Transaction::getAmount)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setAmount, r, v));

        /* Create the tag column */
        myTable.declareListColumn(MoneyWiseTransDataId.TAG, TransactionTag.class)
                .setSelectables(c -> theActiveTran.buildTransactionTags())
                .setCellValueFactory(Transaction::getTransactionTags)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(Transaction::setTransactionTags, r, v));

        /* Create the reference column */
        myTable.declareStringColumn(MoneyWiseTransDataId.REFERENCE)
               .setCellValueFactory(Transaction::getReference)
               .setEditable(true)
               .setColumnWidth(WIDTH_DESC)
               .setOnCommit((r, v) -> updateField(Transaction::setReference, r, v));

        /* Create the taxCredit column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.TAXCREDIT)
               .setCellValueFactory(Transaction::getTaxCredit)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setTaxCredit, r, v));

        /* Create the EeNatIns column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.EMPLOYEENATINS)
               .setCellValueFactory(Transaction::getEmployeeNatIns)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setEmployeeNatIns, r, v));

        /* Create the ErNatIns column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.EMPLOYERNATINS)
               .setCellValueFactory(Transaction::getEmployerNatIns)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setEmployerNatIns, r, v));

        /* Create the Benefit column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.DEEMEDBENEFIT)
               .setCellValueFactory(Transaction::getDeemedBenefit)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setBenefit, r, v));

        /* Create the Withheld column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.WITHHELD)
               .setCellValueFactory(Transaction::getWithheld)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setWithheld, r, v));

        /* Create the AccountUnits column */
        myTable.declareUnitsColumn(MoneyWiseTransDataId.ACCOUNTDELTAUNITS)
               .setCellValueFactory(Transaction::getAccountDeltaUnits)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setAccountDeltaUnits, r, v));

        /* Create the PartnerUnits column */
        myTable.declareUnitsColumn(MoneyWiseTransDataId.PARTNERDELTAUNITS)
               .setCellValueFactory(Transaction::getAccountDeltaUnits)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setPartnerDeltaUnits, r, v));

        /* Create the Dilution column */
        myTable.declareDilutionColumn(MoneyWiseTransDataId.DILUTION)
               .setCellValueFactory(Transaction::getDilution)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setDilution, r, v));

        /* Create the QualifyYears column */
        myTable.declareIntegerColumn(MoneyWiseTransDataId.QUALIFYYEARS)
               .setCellValueFactory(Transaction::getYears)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setYears, r, v));

        /* Create the returned cash account column */
        myTable.declareScrollColumn(MoneyWiseTransDataId.RETURNEDCASHACCOUNT, TransactionAsset.class)
               .setMenuConfigurator(this::buildReturnedMenu)
               .setCellValueFactory(Transaction::getReturnedCashAccount)
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setReturnedCashAccount, r, v));

        /* Create the returned cash column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.RETURNEDCASH)
               .setCellValueFactory(Transaction::getReturnedCash)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setReturnedCash, r, v));

        /* Create the partner amount column */
        myTable.declareMoneyColumn(MoneyWiseTransDataId.PARTNERAMOUNT)
               .setCellValueFactory(Transaction::getPartnerAmount)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setPartnerAmount, r, v));

        /* Create the debit column */
        myTable.declareRawDecimalColumn(MoneyWiseTransDataId.DEBIT)
               .setCellValueFactory(this::getFilteredDebit)
               .setEditable(false)
               .setColumnWidth(WIDTH_MONEY);

        /* Create the credit column */
        myTable.declareRawDecimalColumn(MoneyWiseTransDataId.CREDIT)
               .setCellValueFactory(this::getFilteredCredit)
               .setEditable(false)
               .setColumnWidth(WIDTH_MONEY);

        /* Create the balance column */
        myTable.declareRawDecimalColumn(MoneyWiseTransDataId.BALANCE)
               .setCellValueFactory(this::getFilteredBalance)
               .setEditable(false)
               .setColumnWidth(WIDTH_MONEY);

        /* Create the Active column */
        final TethysUIIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton(myGuiFactory);
        myTable.declareIconColumn(PrometheusDataId.TOUCH, MetisAction.class)
               .setIconMapSet(r -> myActionMapSet)
               .setCellValueFactory(this::getFilteredAction)
               .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
               .setEditable(true)
               .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        pView.getEventRegistrar().addEventListener(e -> refreshData());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.SELECTIONCHANGED, e -> handleFilterSelection());
        theSelect.getEventRegistrar().addEventListener(PrometheusDataEvent.SAVETOFILE, e -> writeCSVToFile(pView.getGuiFactory()));
        theActiveTran.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);
        theFilter = theSelect.getFilter();

        /* Initialise the columns */
        adjustColumns(theSelect.showColumns()
                ? theSelect.getColumns()
                : AnalysisColumnSet.BALANCE);
    }

    /**
     * Obtain the selection panel.
     * @return the select panel
     */
    MoneyWiseAnalysisSelect getSelect() {
        return theSelect;
    }

    /**
     * Obtain the action buttons.
     * @return the action buttons
     */
    PrometheusActionButtons getActionButtons() {
        return theActionButtons;
    }

    /**
     * Delete row.
     * @param pRow the row
     * @param pValue the value (ignored)
     * @throws OceanusException on error
     */
    protected void setDirection(final Transaction pRow,
                                final Object pValue) throws OceanusException {
        pRow.switchDirection();
    }

    @Override
    protected boolean isItemEditing() {
        return theActiveTran.isEditing();
    }

    /**
     * handle Date event.
     * @param pConfig the dateConfig
     */
    private void handleDateEvent(final TethysDateConfig pConfig) {
        pConfig.setEarliestDate(theRange == null
                ? null
                : theRange.getStart());
        pConfig.setLatestDate(theRange == null
                ? null
                : theRange.getEnd());
    }

    /**
     * Determine reconciled state.
     * @param pTrans the transaction
     * @return the state
     */
    private boolean determineReconciledState(final Transaction pTrans) {
        return pTrans.isLocked();
    }

    /**
     * Determine direction state.
     * @param pTrans the transaction
     * @return the state
     */
    private boolean determineDirectionState(final Transaction pTrans) {
         return pTrans.isReconciled();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        getTable().requestFocus();
    }

    /**
     * Obtain filtered debit for transaction.
     * @param pTrans the transaction
     * @return the debit
     */
    private TethysDecimal getFilteredDebit(final Transaction pTrans) {
        return theFilter.getDebitForTransaction(pTrans);
    }

    /**
     * Obtain filtered debit for transaction.
     * @param pTrans the transaction
     * @return the debit
     */
    private TethysDecimal getFilteredCredit(final Transaction pTrans) {
        return theFilter.getCreditForTransaction(pTrans);
    }

    /**
     * Obtain filtered debit for transaction.
     * @param pTrans the transaction
     * @return the debit
     */
    private TethysDecimal getFilteredBalance(final Transaction pTrans) {
        return pTrans.isHeader() ? theFilter.getStartingBalance() : theFilter.getBalanceForTransaction(pTrans);
    }

    /**
     * Obtain date value.
     * @param pTrans the transaction
     * @return the date value
     */
    private TethysDate getFilteredDate(final Transaction pTrans) {
        return pTrans.isHeader() ? theRange.getStart() : pTrans.getDate();
    }

    /**
     * Obtain date value.
     * @param pTrans the transaction
     * @return the date value
     */
    private String getFilteredComments(final Transaction pTrans) {
        return pTrans.isHeader() ? MoneyWiseUIResource.STATEMENT_OPENINGBALANCE.getValue() : pTrans.getComments();
    }

    /**
     * Obtain direction value.
     * @param pTrans the transaction
     * @return the direction value
     */
    private AssetDirection getFilteredDirection(final Transaction pTrans) {
        return pTrans.isHeader() ? null : pTrans.getDirection();
    }

    /**
     * Obtain date value.
     * @param pTrans the transaction
     * @return the date value
     */
    private MetisAction getFilteredAction(final Transaction pTrans) {
        return (pTrans.isHeader() || pTrans.isReconciled()) ? MetisAction.DO : MetisAction.DELETE;
    }

    @Override
    protected void selectItem(final Transaction pTrans) {
        final Transaction myTrans = pTrans != null && !pTrans.isHeader() ? pTrans : null;
        theActiveTran.setItem(myTrans);
    }

    /**
     * Select Statement.
     * @param pSelect the selection
     */
    void selectStatement(final StatementSelect pSelect) {
        /* Update selection */
        theSelect.selectStatement(pSelect);

        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Ensure that columns are correct */
        adjustColumns(theSelect.showColumns()
                ? theSelect.getColumns()
                : AnalysisColumnSet.BALANCE);

        /* Update the lists */
        updateList();
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        final boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelect.setVisible(!isError);

        /* Lock scroll area */
        getTable().setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    @Override
    protected void refreshData() {
        /* Obtain the active profile */
        TethysProfile myTask = getView().getActiveTask();
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

    /**
     * Handle filter selection.
     */
    private void handleFilterSelection() {
        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Ensure that columns are correct */
        adjustColumns(theSelect.showColumns()
                ? theSelect.getColumns()
                : AnalysisColumnSet.BALANCE);

        /* Set the selection */
        final TethysDateRange myRange = theSelect.getRange();
        if (MetisDataDifference.isEqual(myRange, theRange)) {
            /* Handle a simple filter change */
            theViewerFilter.setObject(theFilter);
            getTable().fireTableDataChanged();
        } else {
            /* Update new lists */
            updateList();
        }
    }


    @Override
    protected boolean isFiltered(final Transaction pRow) {
        /* Handle no filter */
        if (theFilter == null) {
            return false;
        }

        /* Handle header visibility */
        if (pRow.isHeader()) {
            return AnalysisColumnSet.BALANCE.equals(theColumnSet);
        }

        /* Return visibility of row */
        return super.isFiltered(pRow) && !theFilter.filterTransaction(pRow);
    }

    @Override
    public void notifyChanges() {
        /* Determine whether we have updates */
        final boolean hasUpdates = hasUpdates();
        final boolean isItemEditing = theActiveTran.isEditing();

        /* Update the table buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);
        theSelect.setEnabled(!hasUpdates && !isItemEditing);
        theNewButton.setEnabled(!isItemEditing);

        /* Adjust enable of the table */
        if (!isItemEditing) {
            setEnabled(true);
        } else {
            setTableEnabled(false);
        }

        /* Pass call on */
        super.notifyChanges();
    }

    /**
     * Update lists.
     */
    private void updateList() {
        /* Access the transactions */
        theTransactions = theAnalysisView.getTransactions();
        theRange = theAnalysisView.getRange();

        /* If we have data */
        if (theTransactions != null) {
            Transaction myHeader = theTransactions.findItemById(AnalysisHeader.ID_VALUE);
            if (myHeader == null) {
                /* Create the header */
                myHeader = new AnalysisHeader(theTransactions);
                theTransactions.add(myHeader);
            }

            /* Notify panel of refresh */
            theActiveTran.refreshData();
            theActiveTran.updateEditors(theRange);

            /* Notify the builder */
            theBuilder.setParameters(theTransactions, theRange);
        }

        /* Update lists */
        getTable().setItems(theTransactions.getUnderlyingList());
        theActionButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Touch the filter and updateSet */
        theViewerFilter.setObject(theFilter);
        theViewerAnalysis.setTreeObject(getUpdateSet());
        restoreSelected();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveTran.setEditable(false);
    }

    /**
     * Select transaction.
     * @param pTran the transaction to select
     */
    void selectTran(final Transaction pTran) {
        /* Select the row */
        getTable().selectRow(pTran);
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveTran.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            super.handleRewind();
        }

        /* Adjust for changes */
        notifyChanges();
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
     * Handle panel state.
     */
    private void handlePanelState() {
        /* Only action if we are not editing */
        if (!theActiveTran.isEditing()) {
            /* handle the edit transition */
            setEnabled(true);
            getTable().fireTableDataChanged();
            final Transaction myTrans = theActiveTran.getSelectedItem();
            if (myTrans != null) {
                selectTran(myTrans);
            } else {
                restoreSelected();
            }
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * Obtain the popUpMenu for Accounts.
     * @param pTrans the transaction
     * @param pMenu the menu to build
     */
    private void buildAccountMenu(final Transaction pTrans,
                                  final TethysUIScrollMenu<TransactionAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildAccountMenu(pMenu, pTrans);
    }

    /**
     * Obtain the popUpMenu for Partner Accounts.
     * @param pTrans the transaction
     * @param pMenu the menu to build
     */
    private void buildPartnerMenu(final Transaction pTrans,
                                  final TethysUIScrollMenu<TransactionAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildPartnerMenu(pMenu, pTrans);
    }

    /**
     * Build the popUpMenu for categories.
     * @param pTrans the transaction
     * @param pMenu the menu to build
     */
    private void buildCategoryMenu(final Transaction pTrans,
                                   final TethysUIScrollMenu<TransactionCategory> pMenu) {
        /* Build the menu */
        theActiveTran.buildCategoryMenu(pMenu, pTrans);
    }

    /**
     * Build the popUpMenu for categories.
     * @param pTrans the transaction
     * @param pMenu the menu to build
     */
    private void buildReturnedMenu(final Transaction pTrans,
                                   final TethysUIScrollMenu<TransactionAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildReturnedAccountMenu(pMenu, pTrans);
    }

    /**
     * New item.
     */
    private void addNewItem() {
        /* Make sure that we have finished editing */
        cancelEditing();

        /* Create the new transaction */
        final Transaction myTrans = theFilter.buildNewTransaction(theBuilder);

        /* If we have one available */
        if (myTrans != null) {
            /* Add the new item */
            myTrans.setNewVersion();
            theTransactions.add(myTrans);

            /* Validate the new item and notify of the changes */
            myTrans.validate();
            getUpdateSet().incrementVersion();

            /* Lock the table */
            setTableEnabled(false);
            theActiveTran.setNewItem(myTrans);
        }
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
        final TethysUITableManager<PrometheusDataFieldId, Transaction> myTable = getTable();
        hideAllColumns();

        /* Switch on column set */
         switch (pSet) {
            case BALANCE:
                myTable.getColumn(MoneyWiseTransDataId.COMMENTS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.DEBIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.CREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.BALANCE).setVisible(true);
                break;
            case STANDARD:
                myTable.getColumn(MoneyWiseTransDataId.COMMENTS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.TAG).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.REFERENCE).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.PARTNERAMOUNT).setVisible(true);
                break;
            case SALARY:
                myTable.getColumn(MoneyWiseTransDataId.AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.EMPLOYEENATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.EMPLOYERNATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.DEEMEDBENEFIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.WITHHELD).setVisible(true);
                break;
            case INTEREST:
                myTable.getColumn(MoneyWiseTransDataId.AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.WITHHELD).setVisible(true);
                break;
            case DIVIDEND:
                myTable.getColumn(MoneyWiseTransDataId.AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.ACCOUNTDELTAUNITS).setVisible(true);
                break;
            case SECURITY:
                myTable.getColumn(MoneyWiseTransDataId.AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.ACCOUNTDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.PARTNERDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.DILUTION).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.RETURNEDCASHACCOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.RETURNEDCASH).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.QUALIFYYEARS).setVisible(true);
                break;
            case ALL:
            default:
                myTable.getColumn(MoneyWiseTransDataId.COMMENTS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.TAG).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.REFERENCE).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.EMPLOYERNATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.EMPLOYEENATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.DEEMEDBENEFIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.WITHHELD).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.ACCOUNTDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.PARTNERDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.PARTNERAMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.DILUTION).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.RETURNEDCASHACCOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.RETURNEDCASH).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.QUALIFYYEARS).setVisible(true);
                break;
        }

        /* Store the column set */
        theColumnSet = pSet;
    }

    /**
     * Hide all columns.
     */
    private void hideAllColumns() {
        final TethysUITableManager<PrometheusDataFieldId, Transaction> myTable = getTable();
        myTable.getColumn(MoneyWiseTransDataId.DEBIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.CREDIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.BALANCE).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.COMMENTS).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.AMOUNT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.TAG).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.REFERENCE).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.TAXCREDIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.EMPLOYERNATINS).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.EMPLOYEENATINS).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.DEEMEDBENEFIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.WITHHELD).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.ACCOUNTDELTAUNITS).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.PARTNERDELTAUNITS).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.PARTNERAMOUNT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.DILUTION).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.RETURNEDCASHACCOUNT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.RETURNEDCASH).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.QUALIFYYEARS).setVisible(false);
    }


    /**
     * Analysis Header class.
     */
    private static class AnalysisHeader
            extends Transaction {
        /**
         * Analysis Header Id.
         */
        static final int ID_VALUE = 1;

        /**
         * Constructor.
         * @param pList the Transaction list
         */
        protected AnalysisHeader(final TransactionList pList) {
            super(pList);
            setHeader(true);
            setId(ID_VALUE);
        }
    }

    /**
     * Transaction Panel.
     */
    public static class MoneyWiseStatementPanel
            implements TethysUIComponent, TethysEventProvider<PrometheusDataEvent> {
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
         * The Event Manager.
         */
        private final TethysEventManager<PrometheusDataEvent> theEventManager;

        /**
         * The updateSet.
         */
        private final UpdateSet<MoneyWiseDataType> theUpdateSet;

        /**
         * The analysis data entry.
         */
        private final MetisViewerEntry theViewerAnalysis;

        /**
         * The error panel.
         */
        private final MetisErrorPanel theError;

        /**
         * The table.
         */
        private final MoneyWiseTransactionTable theTable;

        /**
         * The panel.
         */
        private final TethysUIBorderPaneManager thePanel;

        /**
         * Constructor.
         *
         * @param pView the data view
         */
        MoneyWiseStatementPanel(final MoneyWiseView pView) {
            /* Build the Update set and entry */
            theUpdateSet = new UpdateSet<>(pView, MoneyWiseDataType.class);

            /* Create the event manager */
            theEventManager = new TethysEventManager<>();

            /* Create the top level viewer entry for this view */
            final MetisViewerManager myViewer = pView.getViewerManager();
            final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
            final MetisViewerEntry myRegister = myViewer.newEntry(mySection, NLS_DATAENTRY);
            final MetisViewerEntry myViewerFilter = myViewer.newEntry(myRegister, NLS_FILTERDATAENTRY);
            theViewerAnalysis = myViewer.newEntry(myRegister, NLS_TRANSDATAENTRY);
            theViewerAnalysis.setTreeObject(theUpdateSet);

            /* Create the error panel for this view */
            theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerAnalysis);

            /* Create the table */
            theTable = new MoneyWiseTransactionTable(pView, theUpdateSet, theError, myViewerFilter, theViewerAnalysis);

            /* Create the action buttons */
            final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();

            /* Create the header panel */
            final TethysUIPaneFactory myPanes = myGuiFactory.paneFactory();
            final TethysUIBorderPaneManager myHeader = myPanes.newBorderPane();
            myHeader.setCentre(theTable.getSelect());
            myHeader.setNorth(theError);
            myHeader.setEast(theTable.getActionButtons());

            /* Create the panel */
            thePanel = myPanes.newBorderPane();
            thePanel.setNorth(myHeader);
            thePanel.setCentre(theTable);

            /* Add listeners */
            theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
            theTable.getActionButtons().getEventRegistrar().addEventListener(this::handleActionButtons);
            theTable.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> notifyChanges());
            theTable.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, theEventManager::cascadeEvent);
        }

        @Override
        public TethysUIComponent getUnderlying() {
            return thePanel;
        }

        @Override
        public void setEnabled(final boolean pEnabled) {
            thePanel.setEnabled(pEnabled);
        }

        @Override
        public void setVisible(final boolean pVisible) {
            thePanel.setVisible(pVisible);
        }

        @Override
        public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Select Statement.
         * @param pSelect the selection
         */
        public void selectStatement(final StatementSelect pSelect) {
            theTable.selectStatement(pSelect);
        }

        /**
         * handleErrorPane.
         */
        private void handleErrorPane() {
            /* Determine whether we have an error */
            final boolean isError = theError.hasError();

            /* Hide selection panel on error */
            theTable.getSelect().setVisible(!isError);

            /* Lock scroll area */
            theTable.setEnabled(!isError);

            /* Lock Action Buttons */
            theTable.getActionButtons().setEnabled(!isError);
        }

        /**
         * handle Action Buttons.
         * @param pEvent the event
         */
        private void handleActionButtons(final TethysEvent<PrometheusUIEvent> pEvent) {
            /* Cancel editing */
            theTable.cancelEditing();

            /* Perform the command */
            theUpdateSet.processCommand(pEvent.getEventId(), theError);

            /* Adjust for changes */
            theTable.notifyChanges();
        }

        /**
         * Determine Focus.
         */
        public void determineFocus() {
            /* Request the focus */
            theTable.determineFocus();

            /* Focus on the Data entry */
            theViewerAnalysis.setFocus();
        }

        /**
         * Call underlying controls to take notice of changes in view/selection.
         */
        private void notifyChanges() {
            /* Notify listeners */
            theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
        }

        /**
         * Does the panel have updates?
         * @return true/false
         */
        public boolean hasUpdates() {
            return theTable.hasUpdates();
        }

        /**
         * Does the panel have a session?
         * @return true/false
         */
        public boolean hasSession() {
            return theTable.hasUpdates();
        }

        /**
         * Does the panel have errors?
         * @return true/false
         */
        public boolean hasErrors() {
            return theTable.hasErrors();
        }
    }
}
