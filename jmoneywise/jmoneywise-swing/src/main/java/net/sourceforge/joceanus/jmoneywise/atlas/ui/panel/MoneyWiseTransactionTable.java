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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.panel;

import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.ui.MetisAction;
import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.TransactionPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisView;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.swing.PrometheusSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTableManager;

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
    private final TransactionPanel theActiveTran;

    /**
     * The new button.
     */
    private final TethysButton theNewButton;

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

        /* Store viewer entries */
        theViewerAnalysis = pAnalysis;
        theViewerFilter = pFilter;

        /* Access field manager and gui factory */
        final MetisSwingFieldManager myFieldMgr = ((PrometheusSwingToolkit) pView.getToolkit()).getFieldManager();
        final TethysGuiFactory myGuiFactory = pView.getGuiFactory();
        final TethysTableManager<MetisLetheField, Transaction> myTable = getTable();

        /* Create new button */
        theNewButton = myGuiFactory.newButton();
        MetisIcon.configureNewIconButton(theNewButton);

        /* Create the Analysis View */
        theAnalysisView = new AnalysisView(pView, getUpdateSet());

        /* Create the Analysis Selection */
        theSelect = new MoneyWiseAnalysisSelect(myGuiFactory, pView, theAnalysisView, theNewButton);
        theFilter = theSelect.getFilter();

        /* Create the action buttons */
        theActionButtons = new PrometheusActionButtons(myGuiFactory, getUpdateSet());

        /* Create the builder */
        theBuilder = new TransactionBuilder(getUpdateSet());

        /* Create a tag panel */
        theActiveTran = new TransactionPanel(myGuiFactory, myFieldMgr, pUpdateSet, theBuilder, theSelect, pError);
        declareItemPanel(theActiveTran);

        /* Set table configuration */
        myTable.setDisabled(Transaction::isDisabled)
               .setComparator(Transaction::compareTo)
               .setOnSelect(theActiveTran::setItem);

        /* Create the date column */
        myTable.declareDateColumn(Transaction.FIELD_DATE)
               .setDateConfigurator((r, c) -> handleDateEvent(c))
               .setCellValueFactory(Transaction::getDate)
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled())
               .setColumnWidth(WIDTH_DATE)
               .setOnCommit((r, v) -> updateField(Transaction::setDate, r, v));

        /* Create the account column */
        myTable.declareScrollColumn(TransactionBase.FIELD_ACCOUNT, TransactionAsset.class)
               .setMenuConfigurator(this::buildAccountMenu)
               .setCellValueFactory(Transaction::getAccount)
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setAccount, r, v));

        /* Create the category column */
        myTable.declareScrollColumn(TransactionBase.FIELD_CATEGORY, TransactionCategory.class)
               .setMenuConfigurator(this::buildCategoryMenu)
               .setCellValueFactory(Transaction::getCategory)
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setCategory, r, v));

        /* Create the direction column */
        final Map<Boolean, TethysIconMapSet<AssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton();
        myTable.declareIconColumn(TransactionBase.FIELD_DIRECTION, AssetDirection.class)
               .setIconMapSet(r -> myDirMapSets.get(determineDirectionState(r)))
               .setCellValueFactory(Transaction::getDirection)
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled() && r.canSwitchDirection())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(this::setDirection, r, v));

        /* Create the partner column */
        myTable.declareScrollColumn(TransactionBase.FIELD_PARTNER, TransactionAsset.class)
               .setMenuConfigurator(this::buildPartnerMenu)
               .setCellValueFactory(Transaction::getPartner)
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setPartner, r, v));

        /* Create the reconciled column */
        final Map<Boolean, TethysIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton();
        myTable.declareIconColumn(TransactionBase.FIELD_RECONCILED, Boolean.class)
               .setIconMapSet(r -> myRecMapSets.get(determineReconciledState(r)))
               .setCellValueFactory(Transaction::isReconciled)
               .setEditable(true)
               .setCellEditable(r -> !r.isLocked())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(Transaction::setReconciled, r, v));

        /* Create the comments column */
        myTable.declareStringColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS))
               .setCellValueFactory(Transaction::getComments)
               .setEditable(true)
               .setColumnWidth(WIDTH_DESC)
               .setOnCommit((r, v) -> updateField(Transaction::setComments, r, v));

        /* Create the amount column */
        myTable.declareMoneyColumn(TransactionBase.FIELD_AMOUNT)
               .setCellValueFactory(Transaction::getAmount)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setAmount, r, v));

        /* Create the reference column */
        myTable.declareStringColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.REFERENCE))
               .setCellValueFactory(Transaction::getReference)
               .setEditable(true)
               .setColumnWidth(WIDTH_DESC)
               .setOnCommit((r, v) -> updateField(Transaction::setReference, r, v));

        /* Create the taxCredit column */
        myTable.declareMoneyColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT))
               .setCellValueFactory(Transaction::getTaxCredit)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setTaxCredit, r, v));

        /* Create the EeNatIns column */
        myTable.declareMoneyColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYEENATINS))
               .setCellValueFactory(Transaction::getEmployeeNatIns)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setEmployeeNatIns, r, v));

        /* Create the ErNatIns column */
        myTable.declareMoneyColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYERNATINS))
               .setCellValueFactory(Transaction::getEmployerNatIns)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setEmployerNatIns, r, v));

        /* Create the Benefit column */
        myTable.declareMoneyColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT))
               .setCellValueFactory(Transaction::getDeemedBenefit)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setBenefit, r, v));

        /* Create the Withheld column */
        myTable.declareMoneyColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD))
               .setCellValueFactory(Transaction::getWithheld)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setWithheld, r, v));

        /* Create the AccountUnits column */
        myTable.declareUnitsColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS))
               .setCellValueFactory(Transaction::getAccountDeltaUnits)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setAccountDeltaUnits, r, v));

        /* Create the PartnerUnits column */
        myTable.declareUnitsColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS))
               .setCellValueFactory(Transaction::getAccountDeltaUnits)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setPartnerDeltaUnits, r, v));

        /* Create the Dilution column */
        myTable.declareDilutionColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION))
               .setCellValueFactory(Transaction::getDilution)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setDilution, r, v));

        /* Create the QualifyYears column */
        myTable.declareIntegerColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS))
               .setCellValueFactory(Transaction::getYears)
               .setEditable(true)
               .setColumnWidth(WIDTH_UNITS)
               .setOnCommit((r, v) -> updateField(Transaction::setYears, r, v));

        /* Create the returned cash account column */
        myTable.declareScrollColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT), TransactionAsset.class)
               .setMenuConfigurator(this::buildReturnedMenu)
               .setCellValueFactory(Transaction::getReturnedCashAccount)
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled())
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setReturnedCashAccount, r, v));

        /* Create the returned cash column */
        myTable.declareMoneyColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH))
               .setCellValueFactory(Transaction::getReturnedCash)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setReturnedCash, r, v));

        /* Create the partner amount column */
        myTable.declareMoneyColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT))
               .setCellValueFactory(Transaction::getPartnerAmount)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setPartnerAmount, r, v));

        /* Create the exchangeRate column */
        myTable.declareRatioColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.XCHANGERATE))
               .setCellValueFactory(Transaction::getExchangeRate)
               .setEditable(true)
               .setColumnWidth(WIDTH_MONEY)
               .setOnCommit((r, v) -> updateField(Transaction::setExchangeRate, r, v));

        /* Create the tag column */
        myTable.declareListColumn(TransactionInfoSet.getFieldForClass(TransactionInfoClass.TRANSTAG), TransactionTag.class)
               .setSelectables(c -> theActiveTran.buildTransactionTags())
               .setCellValueFactory(Transaction::getTransactionTags)
               .setEditable(true)
               .setColumnWidth(WIDTH_NAME)
               .setOnCommit((r, v) -> updateField(Transaction::setTransactionTags, r, v));

        /* Create the debit column */
        myTable.declareRawDecimalColumn(Transaction.FIELD_TOUCH)
               .setCellValueFactory(theFilter::getDebitForTransaction)
               .setEditable(false)
               .setColumnWidth(WIDTH_MONEY);

        /* Create the credit column */
        myTable.declareRawDecimalColumn(Transaction.FIELD_TOUCH)
               .setCellValueFactory(theFilter::getCreditForTransaction)
               .setEditable(false)
               .setColumnWidth(WIDTH_MONEY);

        /* Create the balance column */
        myTable.declareRawDecimalColumn(Transaction.FIELD_TOUCH)
               .setCellValueFactory(theFilter::getBalanceForTransaction)
               .setEditable(false)
               .setColumnWidth(WIDTH_MONEY);

        /* Create the Active column */
        final TethysIconMapSet<MetisAction> myActionMapSet = MetisIcon.configureStatusIconButton();
        myTable.declareIconColumn(Transaction.FIELD_TOUCH, MetisAction.class)
               .setIconMapSet(r -> myActionMapSet)
               .setCellValueFactory(r -> r.isReconciled() ? MetisAction.DO : MetisAction.DELETE)
               .setName(MoneyWiseUIResource.STATICDATA_ACTIVE.getValue())
               .setEditable(true)
               .setCellEditable(r -> !r.isReconciled())
               .setColumnWidth(WIDTH_ICON)
               .setOnCommit((r, v) -> updateField(this::deleteRow, r, v));

        /* Add listeners */
        theNewButton.getEventRegistrar().addEventListener(e -> addNewItem());
        theActiveTran.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
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
     * Select Statement.
     * @param pSelect the selection
     */
    protected void selectStatement(final StatementSelect pSelect) {
        /* Update selection */
        theSelect.selectStatement(pSelect);

        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Update the lists */
        updateList();
    }

    @Override
    protected void refreshData() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = getView().getActiveTask();
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
        setEnabled(!isItemEditing);

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

        if (theTransactions != null) {
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
        /* Select the row and ensure that it is visible */
        getTable().selectRowWithScroll(pTran);
    }

    @Override
    protected void handleRewind() {
        /* Only action if we are not editing */
        if (!theActiveTran.isEditing()) {
            /* Handle the reWind */
            setEnabled(true);
            getTable().fireTableDataChanged();
            selectTran(theActiveTran.getSelectedItem());
        }

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
            selectTran(theActiveTran.getSelectedItem());
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
                                  final TethysScrollMenu<TransactionAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildAccountMenu(pMenu, pTrans);
    }

    /**
     * Obtain the popUpMenu for Partner Accounts.
     * @param pTrans the transaction
     * @param pMenu the menu to build
     */
    private void buildPartnerMenu(final Transaction pTrans,
                                  final TethysScrollMenu<TransactionAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildPartnerMenu(pMenu, pTrans);
    }

    /**
     * Build the popUpMenu for categories.
     * @param pTrans the transaction
     * @param pMenu the menu to build
     */
    private void buildCategoryMenu(final Transaction pTrans,
                                   final TethysScrollMenu<TransactionCategory> pMenu) {
        /* Build the menu */
        theActiveTran.buildCategoryMenu(pMenu, pTrans);
    }

    /**
     * Build the popUpMenu for categories.
     * @param pTrans the transaction
     * @param pMenu the menu to build
     */
    private void buildReturnedMenu(final Transaction pTrans,
                                   final TethysScrollMenu<TransactionAsset> pMenu) {
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
            setEnabled(false);
            theActiveTran.setNewItem(myTrans);
        }
    }

    /**
     * SpotRates Panel.
     */
    public static class MoneyWiseTransactionPanel
            implements TethysComponent, TethysEventProvider<PrometheusDataEvent> {
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
        private final TethysBorderPaneManager thePanel;

        /**
         * Constructor.
         *
         * @param pView the data view
         */
        public MoneyWiseTransactionPanel(final MoneyWiseView pView) {
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
            final TethysGuiFactory myGuiFactory = pView.getGuiFactory();

            /* Create the header panel */
            final TethysBorderPaneManager myHeader = myGuiFactory.newBorderPane();
            myHeader.setCentre(theTable.getSelect());
            myHeader.setNorth(theError);
            myHeader.setEast(theTable.getActionButtons());

            /* Create the panel */
            thePanel = myGuiFactory.newBorderPane();
            thePanel.setNorth(myHeader);
            thePanel.setCentre(theTable);

            /* Add listeners */
            theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
            theTable.getActionButtons().getEventRegistrar().addEventListener(this::handleActionButtons);
            theTable.getEventRegistrar().addEventListener(e -> notifyChanges());
        }

        @Override
        public Integer getId() {
            return thePanel.getId();
        }

        @Override
        public TethysNode getNode() {
            return thePanel.getNode();
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
