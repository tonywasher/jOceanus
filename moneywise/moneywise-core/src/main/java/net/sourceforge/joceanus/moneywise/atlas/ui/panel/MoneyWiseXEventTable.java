/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.ui.panel;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.ui.MetisAction;
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.ui.MetisIcon;
import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisDataType;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalysisManager;
import net.sourceforge.joceanus.moneywise.atlas.ui.controls.MoneyWiseXAnalysisSelect;
import net.sourceforge.joceanus.moneywise.atlas.ui.controls.MoneyWiseXAnalysisSelect.MoneyWiseXStatementSelect;
import net.sourceforge.joceanus.moneywise.atlas.ui.dialog.MoneyWiseXTransactionDialog;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransDefaults;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseAnalysisColumnSet;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseBaseTable;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateConfig;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.prometheus.views.PrometheusUIEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.tethys.api.table.TethysUITableManager;

import java.util.Map;

/**
 * MoneyWise Event Table.
 */
public class MoneyWiseXEventTable
        extends MoneyWiseBaseTable<MoneyWiseXAnalysisEvent> {
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
    private final MoneyWiseXTransactionDialog theActiveTran;

    /**
     * The new button.
     */
    private final TethysUIButton theNewButton;

    /**
     * Analysis View.
     */
    private final MoneyWiseXAnalysisManager theAnalysisMgr;

    /**
     * Analysis Selection panel.
     */
    private final MoneyWiseXAnalysisSelect theSelect;

    /**
     * The action buttons.
     */
    private final PrometheusActionButtons theActionButtons;

    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransDefaults theBuilder;

    /**
     * The UpdateSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The date range.
     */
    private OceanusDateRange theRange;

    /**
     * The analysis filter.
     */
    private MoneyWiseXAnalysisFilter<?, ?> theFilter;

    /**
     * The edit list.
     */
    private MoneyWiseTransactionList theTransactions;

    /**
     * ColumnSet.
     */
    private MoneyWiseAnalysisColumnSet theColumnSet;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the editSet
     * @param pAnalysisMgr the analysisManager
     * @param pError the error panel
     * @param pFilter the filter viewer entry
     * @param pAnalysis the analysis viewer entry
     */
    MoneyWiseXEventTable(final MoneyWiseView pView,
                         final PrometheusEditSet pEditSet,
                         final MoneyWiseXAnalysisManager pAnalysisMgr,
                         final MetisErrorPanel pError,
                         final MetisViewerEntry pFilter,
                         final MetisViewerEntry pAnalysis) {
        /* Store parameters */
        super(pView, pEditSet, pError, MoneyWiseXAnalysisDataType.EVENT);

        /* store parameters */
        theEditSet = pEditSet;
        theAnalysisMgr = pAnalysisMgr;
        theError = pError;

        /* Store viewer entries */
        theViewerAnalysis = pAnalysis;
        theViewerFilter = pFilter;

        /* Access gui factory */
        final TethysUIFactory<?> myGuiFactory = pView.getGuiFactory();
        final TethysUITableManager<MetisDataFieldId, MoneyWiseXAnalysisEvent> myTable = getTable();

        /* Create new button */
        theNewButton = myGuiFactory.buttonFactory().newButton();
        MetisIcon.configureNewIconButton(theNewButton);

        /* Create the Analysis Selection */
        theSelect = new MoneyWiseXAnalysisSelect(myGuiFactory, pView, theAnalysisMgr, theNewButton);

        /* Create the action buttons */
        theActionButtons = new PrometheusActionButtons(myGuiFactory, getEditSet());

        /* Create the builder */
        theBuilder = new MoneyWiseTransDefaults(getEditSet());

        /* Create a transaction panel */
        theActiveTran = new MoneyWiseXTransactionDialog(myGuiFactory, pEditSet, theBuilder, theSelect, this);
        declareItemPanel(theActiveTran);

        /* Set table configuration */
        myTable.setDisabled(MoneyWiseXAnalysisEvent::isDisabled)
                .setComparator(MoneyWiseXAnalysisEvent::compareTo);

        /* Create the date column */
        myTable.declareDateColumn(MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE)
                .setDateConfigurator((r, c) -> handleDateEvent(c))
                .setCellValueFactory(this::getFilteredDate)
                .setEditable(true)
                .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
                .setColumnWidth(WIDTH_DATE)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setDate, r, v));

        /* Create the account column */
        myTable.declareScrollColumn(MoneyWiseBasicResource.TRANSACTION_ACCOUNT, MoneyWiseTransAsset.class)
                .setMenuConfigurator(this::buildAccountMenu)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getAccount)
                .setEditable(true)
                .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setAccount, r, v));

        /* Create the category column */
        myTable.declareScrollColumn(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategory.class)
                .setMenuConfigurator(this::buildCategoryMenu)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getCategory)
                .setEditable(true)
                .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setCategory, r, v));

        /* Create the direction column */
        final Map<Boolean, TethysUIIconMapSet<MoneyWiseAssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton(myGuiFactory);
        myTable.declareIconColumn(MoneyWiseBasicResource.TRANSACTION_DIRECTION, MoneyWiseAssetDirection.class)
                .setIconMapSet(r -> myDirMapSets.get(determineDirectionState(r)))
                .setCellValueFactory(MoneyWiseXEventTable::getFilteredDirection)
                .setEditable(true)
                .setCellEditable(r -> !r.isHeader() && !r.isReconciled() && r.canSwitchDirection())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(this::setDirection, r, v));

        /* Create the partner column */
        myTable.declareScrollColumn(MoneyWiseBasicResource.TRANSACTION_PARTNER, MoneyWiseTransAsset.class)
                .setMenuConfigurator(this::buildPartnerMenu)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getPartner)
                .setEditable(true)
                .setCellEditable(r -> !r.isHeader() && !r.isReconciled())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setPartner, r, v));

        /* Create the reconciled column */
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton(myGuiFactory);
        myTable.declareIconColumn(MoneyWiseBasicResource.TRANSACTION_RECONCILED, Boolean.class)
                .setIconMapSet(r -> myRecMapSets.get(determineReconciledState(r)))
                .setCellValueFactory(MoneyWiseXAnalysisEvent::isReconciled)
                .setEditable(true)
                .setCellEditable(r -> !r.isHeader() && !r.isLocked())
                .setColumnWidth(WIDTH_ICON)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setReconciled, r, v));

        /* Create the comments column */
        myTable.declareStringColumn(MoneyWiseTransInfoClass.COMMENTS)
                .setCellValueFactory(MoneyWiseXEventTable::getFilteredComments)
                .setEditable(true)
                .setCellEditable(r -> !r.isHeader())
                .setColumnWidth(WIDTH_DESC)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setComments, r, v));

        /* Create the amount column */
        myTable.declareMoneyColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getAmount)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setAmount, r, v));

        /* Create the tag column */
        myTable.declareListColumn(MoneyWiseTransInfoClass.TRANSTAG, MoneyWiseTransTag.class)
                .setSelectables(c -> theActiveTran.buildTransactionTags())
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getTransactionTags)
                .setEditable(true)
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setTransactionTags, r, v));

        /* Create the reference column */
        myTable.declareStringColumn(MoneyWiseTransInfoClass.REFERENCE)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getReference)
                .setEditable(true)
                .setColumnWidth(WIDTH_DESC)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setReference, r, v));

        /* Create the taxCredit column */
        myTable.declareMoneyColumn(MoneyWiseTransInfoClass.TAXCREDIT)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getTaxCredit)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setTaxCredit, r, v));

        /* Create the EeNatIns column */
        myTable.declareMoneyColumn(MoneyWiseTransInfoClass.EMPLOYEENATINS)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getEmployeeNatIns)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setEmployeeNatIns, r, v));

        /* Create the ErNatIns column */
        myTable.declareMoneyColumn(MoneyWiseTransInfoClass.EMPLOYERNATINS)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getEmployerNatIns)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setEmployerNatIns, r, v));

        /* Create the Benefit column */
        myTable.declareMoneyColumn(MoneyWiseTransInfoClass.DEEMEDBENEFIT)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getDeemedBenefit)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setDeemedBenefit, r, v));

        /* Create the Withheld column */
        myTable.declareMoneyColumn(MoneyWiseTransInfoClass.WITHHELD)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getWithheld)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setWithheld, r, v));

        /* Create the AccountUnits column */
        myTable.declareUnitsColumn(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getAccountDeltaUnits)
                .setEditable(true)
                .setColumnWidth(WIDTH_UNITS)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setAccountDeltaUnits, r, v));

        /* Create the PartnerUnits column */
        myTable.declareUnitsColumn(MoneyWiseTransInfoClass.PARTNERDELTAUNITS)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getPartnerDeltaUnits)
                .setEditable(true)
                .setColumnWidth(WIDTH_UNITS)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setPartnerDeltaUnits, r, v));

        /* Create the Dilution column */
        myTable.declareRatioColumn(MoneyWiseTransInfoClass.DILUTION)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getDilution)
                .setEditable(true)
                .setColumnWidth(WIDTH_UNITS)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setDilution, r, v));

        /* Create the returned cash account column */
        myTable.declareScrollColumn(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT, MoneyWiseTransAsset.class)
                .setMenuConfigurator(this::buildReturnedMenu)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getReturnedCashAccount)
                .setEditable(true)
                .setCellEditable(r -> !r.isReconciled())
                .setColumnWidth(WIDTH_NAME)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setReturnedCashAccount, r, v));

        /* Create the returned cash column */
        myTable.declareMoneyColumn(MoneyWiseTransInfoClass.RETURNEDCASH)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getReturnedCash)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setReturnedCash, r, v));

        /* Create the partner amount column */
        myTable.declareMoneyColumn(MoneyWiseTransInfoClass.PARTNERAMOUNT)
                .setCellValueFactory(MoneyWiseXAnalysisEvent::getPartnerAmount)
                .setEditable(true)
                .setColumnWidth(WIDTH_MONEY)
                .setOnCommit((r, v) -> updateField(MoneyWiseXAnalysisEvent::setPartnerAmount, r, v));

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
        myTable.declareIconColumn(PrometheusDataResource.DATAITEM_TOUCH, MetisAction.class)
                .setIconMapSet(r -> myActionMapSet)
                .setCellValueFactory(MoneyWiseXEventTable::getFilteredAction)
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
                : MoneyWiseAnalysisColumnSet.BALANCE);
    }

    /**
     * Obtain the selection panel.
     * @return the select panel
     */
    MoneyWiseXAnalysisSelect getSelect() {
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
    protected void setDirection(final MoneyWiseXAnalysisEvent pRow,
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
    private void handleDateEvent(final OceanusDateConfig pConfig) {
        pConfig.setEarliestDate(theRange == null
                ? null
                : theRange.getStart());
        pConfig.setLatestDate(theRange == null
                ? null
                : theRange.getEnd());
    }

    @Override
    public boolean isFieldChanged(final MetisDataFieldId pField,
                                  final MoneyWiseXAnalysisEvent pItem) {
        if (pField.equals(MoneyWiseTransDataId.DEBIT)
                || pField.equals(MoneyWiseTransDataId.CREDIT)
                || pField.equals(MoneyWiseTransDataId.BALANCE)) {
            return false;
        }
        return super.isFieldChanged(pField, pItem);
    }

    /**
     * Determine reconciled state.
     * @param pEvent the transaction
     * @return the state
     */
    private static boolean determineReconciledState(final MoneyWiseXAnalysisEvent pEvent) {
        return pEvent.isLocked();
    }

    /**
     * Determine direction state.
     * @param pEvent the transaction
     * @return the state
     */
    private static boolean determineDirectionState(final MoneyWiseXAnalysisEvent pEvent) {
        return pEvent.isReconciled();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        getTable().requestFocus();
    }

    /**
     * Obtain filtered debit for event.
     * @param pEvent the event
     * @return the debit
     */
    private OceanusDecimal getFilteredDebit(final MoneyWiseXAnalysisEvent pEvent) {
        return theFilter.getDebitForEvent(pEvent);
    }

    /**
     * Obtain filtered debit for event.
     * @param pEvent the event
     * @return the debit
     */
    private OceanusDecimal getFilteredCredit(final MoneyWiseXAnalysisEvent pEvent) {
        return theFilter.getCreditForEvent(pEvent);
    }

    /**
     * Obtain filtered balance for event.
     * @param pEvent the event
     * @return the balance
     */
    private OceanusDecimal getFilteredBalance(final MoneyWiseXAnalysisEvent pEvent) {
        return pEvent.isHeader() ? theFilter.getStartingBalance() : theFilter.getBalanceForEvent(pEvent);
    }

    /**
     * Obtain filtered date for event.
     * @param pEvent the event
     * @return the date value
     */
    private OceanusDate getFilteredDate(final MoneyWiseXAnalysisEvent pEvent) {
        return pEvent.isHeader() ? theRange.getStart() : pEvent.getDate();
    }

    /**
     * Obtain filtered comment for event.
     * @param pEvent the event
     * @return the comments
     */
    private static String getFilteredComments(final MoneyWiseXAnalysisEvent pEvent) {
        return pEvent.isHeader() ? MoneyWiseUIResource.STATEMENT_OPENINGBALANCE.getValue() : pEvent.getComments();
    }

    /**
     * Obtain filtered direction for event.
     * @param pEvent the event
     * @return the direction value
     */
    private static MoneyWiseAssetDirection getFilteredDirection(final MoneyWiseXAnalysisEvent pEvent) {
        return pEvent.isHeader() ? null : pEvent.getDirection();
    }

    /**
     * Obtain filtered action for event.
     * @param pEvent the event
     * @return the action value
     */
    private static MetisAction getFilteredAction(final MoneyWiseXAnalysisEvent pEvent) {
        return (pEvent.isHeader() || pEvent.isReconciled()) ? MetisAction.DO : MetisAction.DELETE;
    }

    @Override
    protected void selectItem(final MoneyWiseXAnalysisEvent pEvent) {
        final MoneyWiseXAnalysisEvent myEvent = pEvent != null && !pEvent.isHeader() ? pEvent : null;
        theActiveTran.setItem(myEvent);
    }

    /**
     * Select Statement.
     * @param pSelect the selection
     */
    void selectStatement(final MoneyWiseXStatementSelect pSelect) {
        /* Update selection */
        theSelect.selectStatement(pSelect);

        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Ensure that columns are correct */
        adjustColumns(theSelect.showColumns()
                ? theSelect.getColumns()
                : MoneyWiseAnalysisColumnSet.BALANCE);

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
        OceanusProfile myTask = getView().getActiveTask();
        myTask = myTask.startTask("refreshData");

        /* TODO Update the selection */
        //theSelect.refreshData();

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
                : MoneyWiseAnalysisColumnSet.BALANCE);

        /* Set the selection */
        final OceanusDateRange myRange = theSelect.getRange();
        if (MetisDataDifference.isEqual(myRange, theRange)) {
            /* Handle a simple filter change */
            theViewerFilter.setObject(theFilter);
            updateTableData();
        } else {
            /* Update new lists */
            updateList();
        }
    }


    @Override
    protected boolean isFiltered(final MoneyWiseXAnalysisEvent pRow) {
        /* Handle no filter */
        if (theFilter == null) {
            return false;
        }

        /* Handle header visibility */
        if (pRow.isHeader()) {
            return MoneyWiseAnalysisColumnSet.BALANCE.equals(theColumnSet);
        }

        /* Return visibility of row */
        return super.isFiltered(pRow) && !theFilter.filterEvent(pRow);
    }

    @Override
    public void notifyChanges() {
        /* Determine whether we have updates */
        final boolean hasUpdates = hasUpdates();
        final boolean isItemEditing = theActiveTran.isEditing();

        /* Update the table buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);
        theSelect.setEnabled(!isItemEditing);
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
        /* Access the transactions TODO Sort out range */
        theTransactions = theEditSet.getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class);
        //theRange = theAnalysisView.getRange();

        /* If we have data */
        if (theTransactions != null) {
            MoneyWiseTransaction myHeader = theTransactions.findItemById(AnalysisHeader.ID_VALUE);
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
        getTable().setItems(theAnalysisMgr.getAnalysis().getEvents().getUnderlyingList());
        theActionButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Touch the filter and updateSet */
        theViewerFilter.setObject(theFilter);
        theViewerAnalysis.setTreeObject(getEditSet());
        restoreSelected();
    }

    @Override
    public void cancelEditing() {
        super.cancelEditing();
        theActiveTran.setEditable(false);
    }

    /**
     * Select event.
     * @param pEvent the event to select
     */
    void selectEvent(final MoneyWiseXAnalysisEvent pEvent) {
        /* Select the row */
        getTable().selectRow(pEvent);
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
    private void handleActionButtons(final OceanusEvent<PrometheusUIEvent> pEvent) {
        /* Cancel editing */
        cancelEditing();

        /* Perform the command */
        theEditSet.processCommand(pEvent.getEventId(), theError);

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
            final MoneyWiseXAnalysisEvent myEvent = theActiveTran.getSelectedItem();
            updateTableData();
            if (myEvent != null) {
                getTable().selectRow(myEvent);
            } else {
                restoreSelected();
            }
        } else {
            getTable().cancelEditing();
        }

        /* Note changes */
        notifyChanges();
    }

    /**
     * Obtain the popUpMenu for Accounts.
     * @param pEvent the event
     * @param pMenu the menu to build
     */
    private void buildAccountMenu(final MoneyWiseXAnalysisEvent pEvent,
                                  final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildAccountMenu(pMenu, pEvent);
    }

    /**
     * Obtain the popUpMenu for Partner Accounts.
     * @param pEvent the event
     * @param pMenu the menu to build
     */
    private void buildPartnerMenu(final MoneyWiseXAnalysisEvent pEvent,
                                  final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildPartnerMenu(pMenu, pEvent);
    }

    /**
     * Build the popUpMenu for categories.
     * @param pEvent the event
     * @param pMenu the menu to build
     */
    private void buildCategoryMenu(final MoneyWiseXAnalysisEvent pEvent,
                                   final TethysUIScrollMenu<MoneyWiseTransCategory> pMenu) {
        /* Build the menu */
        theActiveTran.buildCategoryMenu(pMenu, pEvent);
    }

    /**
     * Build the popUpMenu for returned.
     * @param pEvent the event
     * @param pMenu the menu to build
     */
    private void buildReturnedMenu(final MoneyWiseXAnalysisEvent pEvent,
                                   final TethysUIScrollMenu<MoneyWiseTransAsset> pMenu) {
        /* Build the menu */
        theActiveTran.buildReturnedAccountMenu(pMenu, pEvent);
    }

    /**
     * New item.
     */
    private void addNewItem() {
        /* Make sure that we have finished editing */
        cancelEditing();

        /* Create a new profile */
        final OceanusProfile myTask = getView().getNewProfile("addNewItem");

        /* Create the new transaction */
        myTask.startTask("buildItem");
        final MoneyWiseTransaction myTrans = theFilter.buildNewTransaction(theBuilder);

        /* If we have one available */
        if (myTrans != null) {
            /* Add the new item */
            myTask.startTask("addToList");
            theTransactions.add(myTrans);
            myTrans.setNewVersion();

            /* Validate the new item and notify of the changes */
            myTask.startTask("incrementVersion");
            getEditSet().incrementVersion();

            /* validate the item */
            myTask.startTask("validate");
            myTrans.validate();

            /* Lock the table */
            myTask.startTask("setItem");
            theActiveTran.setNewItem(theAnalysisMgr.getAnalysis().getEvents().newTransaction(myTrans));
            setTableEnabled(false);
        }

        /* End the task */
        myTask.end();
    }

    /**
     * Adjust columns.
     * @param pSet the set to display.
     */
    private void adjustColumns(final MoneyWiseAnalysisColumnSet pSet) {
        /* Ignore if we are already the right set */
        if (pSet.equals(theColumnSet)) {
            return;
        }

        /* Hide all columns */
        final TethysUITableManager<MetisDataFieldId, MoneyWiseXAnalysisEvent> myTable = getTable();
        hideAllColumns();

        /* Switch on column set */
        switch (pSet) {
            case BALANCE:
                myTable.getColumn(MoneyWiseTransInfoClass.COMMENTS).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.DEBIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.CREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransDataId.BALANCE).setVisible(true);
                break;
            case STANDARD:
                myTable.getColumn(MoneyWiseTransInfoClass.COMMENTS).setVisible(true);
                myTable.getColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.TRANSTAG).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.REFERENCE).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.PARTNERAMOUNT).setVisible(true);
                break;
            case SALARY:
                myTable.getColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.EMPLOYEENATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.EMPLOYERNATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.DEEMEDBENEFIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.WITHHELD).setVisible(true);
                break;
            case INTEREST:
                myTable.getColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.WITHHELD).setVisible(true);
                break;
            case DIVIDEND:
                myTable.getColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS).setVisible(true);
                break;
            case SECURITY:
                myTable.getColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.PARTNERDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.DILUTION).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.RETURNEDCASH).setVisible(true);
                break;
            case ALL:
            default:
                myTable.getColumn(MoneyWiseTransInfoClass.COMMENTS).setVisible(true);
                myTable.getColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.TRANSTAG).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.REFERENCE).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.TAXCREDIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.EMPLOYERNATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.EMPLOYEENATINS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.DEEMEDBENEFIT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.WITHHELD).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.PARTNERDELTAUNITS).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.PARTNERAMOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.DILUTION).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT).setVisible(true);
                myTable.getColumn(MoneyWiseTransInfoClass.RETURNEDCASH).setVisible(true);
                break;
        }

        /* Store the column set */
        theColumnSet = pSet;
    }

    /**
     * Hide all columns.
     */
    private void hideAllColumns() {
        final TethysUITableManager<MetisDataFieldId, MoneyWiseXAnalysisEvent> myTable = getTable();
        myTable.getColumn(MoneyWiseTransDataId.DEBIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.CREDIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransDataId.BALANCE).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.COMMENTS).setVisible(false);
        myTable.getColumn(MoneyWiseBasicResource.TRANSACTION_AMOUNT).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.TRANSTAG).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.REFERENCE).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.TAXCREDIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.EMPLOYERNATINS).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.EMPLOYEENATINS).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.DEEMEDBENEFIT).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.WITHHELD).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.PARTNERDELTAUNITS).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.PARTNERAMOUNT).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.DILUTION).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT).setVisible(false);
        myTable.getColumn(MoneyWiseTransInfoClass.RETURNEDCASH).setVisible(false);
     }


    /**
     * Analysis Header class.
     */
    private static class AnalysisHeader
            extends MoneyWiseTransaction {
        /**
         * Analysis Header Id.
         */
        static final int ID_VALUE = 1;

        /**
         * Constructor.
         * @param pList the Transaction list
         */
        protected AnalysisHeader(final MoneyWiseTransactionList pList) {
            super(pList);
            setHeader(true);
            setIndexedId(ID_VALUE);
        }
    }

    /**
     * Transaction DataIds.
     */
    private enum MoneyWiseTransDataId
            implements MetisDataFieldId {
        /**
         * Debit.
         */
        DEBIT(MoneyWiseUIResource.STATEMENT_COLUMN_DEBIT),

        /**
         * Credit.
         */
        CREDIT(MoneyWiseUIResource.STATEMENT_COLUMN_CREDIT),

        /**
         * Balance.
         */
        BALANCE(MoneyWiseUIResource.STATEMENT_COLUMN_BALANCE);

        /**
         * The Value.
         */
        private final String theValue;

        /**
         * Constructor.
         * @param pKeyName the key name
         */
        MoneyWiseTransDataId(final MetisDataFieldId pKeyName) {
            theValue = pKeyName.getId();
        }

        @Override
        public String getId() {
            return theValue;
        }

        @Override
        public String toString() {
            return getId();
        }
    }

    /**
     * Transaction Panel.
     */
    public static class MoneyWiseXStatementPanel
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
        private final OceanusEventManager<PrometheusDataEvent> theEventManager;

        /**
         * The updateSet.
         */
        private final PrometheusEditSet theEditSet;

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
        private final MoneyWiseXEventTable theTable;

        /**
         * The panel.
         */
        private final TethysUIBorderPaneManager thePanel;

        /**
         * Constructor.
         *
         * @param pView the data view
         * @param pAnalysisMgr the analysisManager
         */
        public MoneyWiseXStatementPanel(final MoneyWiseView pView,
                                        final MoneyWiseXAnalysisManager pAnalysisMgr) {
            /* Build the Update set and entry */
            theEditSet = new PrometheusEditSet(pView);

            /* Create the event manager */
            theEventManager = new OceanusEventManager<>();

            /* Create the top level viewer entry for this view */
            final MetisViewerManager myViewer = pView.getViewerManager();
            final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
            final MetisViewerEntry myRegister = myViewer.newEntry(mySection, NLS_DATAENTRY);
            final MetisViewerEntry myViewerFilter = myViewer.newEntry(myRegister, NLS_FILTERDATAENTRY);
            theViewerAnalysis = myViewer.newEntry(myRegister, NLS_TRANSDATAENTRY);
            theViewerAnalysis.setTreeObject(theEditSet);

            /* Create the error panel for this view */
            theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerAnalysis);

            /* Create the table */
            theTable = new MoneyWiseXEventTable(pView, theEditSet, pAnalysisMgr, theError, myViewerFilter, theViewerAnalysis);

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
        public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Select Statement.
         * @param pSelect the selection
         */
        public void selectStatement(final MoneyWiseXStatementSelect pSelect) {
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
        private void handleActionButtons(final OceanusEvent<PrometheusUIEvent> pEvent) {
            /* Cancel editing */
            theTable.cancelEditing();

            /* Perform the command */
            theEditSet.processCommand(pEvent.getEventId(), theError);

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
