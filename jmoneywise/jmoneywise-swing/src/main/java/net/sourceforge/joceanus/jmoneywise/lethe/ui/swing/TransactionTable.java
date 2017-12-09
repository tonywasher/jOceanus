/*******************************************************************************
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldCalendarCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldDilutionCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldIntegerCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldListButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldMoneyCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldStringCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellEditor.MetisFieldUnitsCellEditor;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldCalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldDecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldIntegerCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldCellRenderer.MetisFieldStringCellRenderer;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionBuilder;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.AnalysisColumnSet;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing.TransactionPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisView;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusAction;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusActionButtons;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTable;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableColumn.PrometheusDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableModel;
import net.sourceforge.joceanus.jprometheus.lethe.ui.swing.PrometheusDataTableSelection;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusUIEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Analysis Statement.
 */
public class TransactionTable
        extends PrometheusDataTable<Transaction, MoneyWiseDataType> {
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
     * AccountUnits Column Title.
     */
    private static final String TITLE_ACCOUNTUNITS = TransactionInfoClass.ACCOUNTDELTAUNITS.toString();

    /**
     * PartnerUnits Column Title.
     */
    private static final String TITLE_PARTNERUNITS = TransactionInfoClass.PARTNERDELTAUNITS.toString();

    /**
     * PartnerAmount Column Title.
     */
    private static final String TITLE_PARTNERAMOUNT = TransactionInfoClass.PARTNERAMOUNT.toString();

    /**
     * Dilution Column Title.
     */
    private static final String TITLE_DILUTION = TransactionInfoClass.DILUTION.toString();

    /**
     * QualifyYears Column Title.
     */
    private static final String TITLE_QUALYEARS = MoneyWiseUIResource.STATEMENT_COLUMN_YEARS.getValue();

    /**
     * ReturnedAccount Column Title.
     */
    private static final String TITLE_RETURNEDACCOUNT = TransactionInfoClass.RETURNEDCASHACCOUNT.toString();

    /**
     * ReturnedCash Column Title.
     */
    private static final String TITLE_RETURNEDCASH = TransactionInfoClass.RETURNEDCASH.toString();

    /**
     * TaxCredit Column Title.
     */
    private static final String TITLE_TAXCREDIT = TransactionInfoClass.TAXCREDIT.toString();

    /**
     * NatInsurance Column Title.
     */
    private static final String TITLE_NATINS = TransactionInfoClass.EMPLOYEENATINS.toString();

    /**
     * DeemedBenefit Column Title.
     */
    private static final String TITLE_BENEFIT = TransactionInfoClass.DEEMEDBENEFIT.toString();

    /**
     * Withheld Column Title.
     */
    private static final String TITLE_WITHHELD = TransactionInfoClass.WITHHELD.toString();

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
    private final SwingView theView;

    /**
     * The updateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The field manager.
     */
    private final MetisSwingFieldManager theFieldMgr;

    /**
     * The analysis data entry.
     */
    private final MetisViewerEntry theViewerAnalysis;

    /**
     * The filter data entry.
     */
    private final MetisViewerEntry theViewerFilter;

    /**
     * Analysis View.
     */
    private final AnalysisView theAnalysisView;

    /**
     * Analysis Selection panel.
     */
    private final MoneyWiseAnalysisSelect<JComponent, Icon> theSelect;

    /**
     * The action buttons.
     */
    private final PrometheusActionButtons<JComponent, Icon> theActionButtons;

    /**
     * The error panel.
     */
    private final MetisErrorPanel<JComponent, Icon> theError;

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
    private final JPanel thePanel;

    /**
     * The new button.
     */
    private final TethysSwingButton theNewButton;

    /**
     * The Transaction dialog.
     */
    private final TransactionPanel theActiveTrans;

    /**
     * The List Selection Model.
     */
    private final PrometheusDataTableSelection<Transaction, MoneyWiseDataType> theSelectionModel;

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
     * Transactions.
     */
    private TransactionList theTransactions;

    /**
     * Statement Header.
     */
    private Transaction theHeader;

    /**
     * Constructor.
     * @param pView the data view
     */
    public TransactionTable(final SwingView pView) {
        /* initialise the underlying class */
        super(pView.getGuiFactory());

        /* Record the passed details */
        theView = pView;
        theFieldMgr = pView.getFieldManager();
        setFieldMgr(theFieldMgr);

        /* Access the GUI Factory */
        final TethysSwingGuiFactory myFactory = theView.getGuiFactory();
        final MetisViewerManager myViewer = theView.getViewerManager();

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet<>(theView, MoneyWiseDataType.class);
        setUpdateSet(theUpdateSet);
        theBuilder = new TransactionBuilder(theUpdateSet);

        /* Create the top level viewer entry for this view */
        final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.VIEW);
        final MetisViewerEntry myRegister = myViewer.newEntry(mySection, NLS_DATAENTRY);
        theViewerFilter = myViewer.newEntry(myRegister, NLS_FILTERDATAENTRY);
        theViewerAnalysis = myViewer.newEntry(myRegister, NLS_TRANSDATAENTRY);
        theViewerAnalysis.setTreeObject(theUpdateSet);

        /* Create new button */
        theNewButton = myFactory.newButton();
        PrometheusIcon.configureNewIconButton(theNewButton);

        /* Create the Analysis View */
        theAnalysisView = new AnalysisView(theView, theUpdateSet);

        /* Create the Analysis Selection */
        theSelect = new MoneyWiseAnalysisSelect<>(myFactory, theView, theAnalysisView, theNewButton);

        /* Create the action buttons */
        theActionButtons = new PrometheusActionButtons<>(myFactory, theUpdateSet);

        /* Create the error panel for this view */
        theError = theView.getToolkit().newErrorPanel(myRegister);

        /* Create the table model */
        theModel = new AnalysisTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new AnalysisColumnModel(this);
        final JTable myTable = getTable();
        myTable.setColumnModel(theColumns);

        /* Create the header panel */
        final TethysSwingBorderPaneManager myHeader = myFactory.newBorderPane();
        myHeader.setCentre(theSelect);
        myHeader.setNorth(theError);
        myHeader.setEast(theActionButtons);

        /* Create the layout for the panel */
        thePanel = new TethysSwingEnablePanel();
        thePanel.setLayout(new BorderLayout());
        thePanel.add(myHeader.getNode(), BorderLayout.PAGE_START);
        thePanel.add(super.getNode(), BorderLayout.CENTER);

        /* Create a transaction panel */
        theActiveTrans = new TransactionPanel(myFactory, theFieldMgr, theUpdateSet, theBuilder, theSelect, theError);
        thePanel.add(theActiveTrans.getNode(), BorderLayout.PAGE_END);

        /* Prevent reordering of columns and auto-resizing */
        myTable.getTableHeader().setReorderingAllowed(false);

        /* Set the number of visible rows */
        myTable.setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the selection model */
        theSelectionModel = new PrometheusDataTableSelection<>(this, theActiveTrans);

        /* Create listener */
        theUpdateSet.getEventRegistrar().addEventListener(e -> handleRewind());
        theView.getEventRegistrar().addEventListener(e -> refreshData());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theSelect.getEventRegistrar().addEventListener(e -> handleFilterSelection());
        theActiveTrans.getEventRegistrar().addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> handlePanelState());
        theActiveTrans.getEventRegistrar().addEventListener(PrometheusDataEvent.GOTOWINDOW, this::cascadeEvent);

        /* Listen to swing events */
        theNewButton.getEventRegistrar().addEventListener(e -> theModel.addNewItem());

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Initialise the filter */
        theFilter = theSelect.getFilter();
        theColumns.adjustColumns(theSelect.showColumns()
                                                         ? theSelect.getColumns()
                                                         : AnalysisColumnSet.BALANCE);
    }

    @Override
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
        theViewerAnalysis.setFocus();
    }

    /**
     * Select Statement.
     * @param pSelect the selection
     */
    protected void selectStatement(final StatementSelect<JComponent, Icon> pSelect) {
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
        final boolean hasUpdates = hasUpdates();
        final boolean isItemEditing = theActiveTrans.isEditing();

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
        final boolean myEnable = bEnable && !isItemEditing();
        super.setEnabled(myEnable);
        theSelect.setEnabled(myEnable);
        theNewButton.setEnabled(myEnable);
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
        theViewerFilter.setObject(theFilter);
        theViewerAnalysis.setTreeObject(theUpdateSet);

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
        final boolean isError = theError.hasError();

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
        final TethysDateRange myRange = theSelect.getRange();
        if (MetisDataDifference.isEqual(myRange, theRange)) {
            /* Handle a simple filter change */
            theModel.fireNewDataEvents();
            theSelectionModel.handleNewFilter();
            theViewerFilter.setObject(theFilter);
        } else {
            /* Update new lists */
            updateList();
        }
    }

    /**
     * JTable Data Model.
     */
    private final class AnalysisTableModel
            extends PrometheusDataTableModel<Transaction, MoneyWiseDataType> {
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
            final Transaction myTrans = theFilter.buildNewTransaction(theBuilder);

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
            extends PrometheusDataTableColumnModel<MoneyWiseDataType> {
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
         * AccountUnits column id.
         */
        private static final int COLUMN_ACCOUNTUNITS = 13;

        /**
         * PartnerUnits column id.
         */
        private static final int COLUMN_PARTNERUNITS = 14;

        /**
         * PartnerAmount column id.
         */
        private static final int COLUMN_PARTNERAMOUNT = 15;

        /**
         * Dilution column id.
         */
        private static final int COLUMN_DILUTION = 16;

        /**
         * QualifyYears column id.
         */
        private static final int COLUMN_QUALYEARS = 17;

        /**
         * ReturnedAccount column id.
         */
        private static final int COLUMN_RETURNEDACCOUNT = 18;

        /**
         * ReturnedCash column id.
         */
        private static final int COLUMN_RETURNEDCASH = 19;

        /**
         * TaxCredit column id.
         */
        private static final int COLUMN_TAXCREDIT = 20;

        /**
         * NatInsurance column id.
         */
        private static final int COLUMN_NATINS = 21;

        /**
         * DeemedBenefit column id.
         */
        private static final int COLUMN_BENEFIT = 22;

        /**
         * Withheld column id.
         */
        private static final int COLUMN_WITHHELD = 23;

        /**
         * Action column id.
         */
        private static final int COLUMN_ACTION = 24;

        /**
         * Comments column.
         */
        private final PrometheusDataTableColumn theDescColumn;

        /**
         * Amount column.
         */
        private final PrometheusDataTableColumn thePaidColumn;

        /**
         * Amount column.
         */
        private final PrometheusDataTableColumn theReceivedColumn;

        /**
         * Amount column.
         */
        private final PrometheusDataTableColumn theBalanceColumn;

        /**
         * Amount column.
         */
        private final PrometheusDataTableColumn theAmountColumn;

        /**
         * Reference column.
         */
        private final PrometheusDataTableColumn theReferenceColumn;

        /**
         * Tags column.
         */
        private final PrometheusDataTableColumn theTagsColumn;

        /**
         * AccountUnits column.
         */
        private final PrometheusDataTableColumn theAccountUnitsColumn;

        /**
         * PartnerUnits column.
         */
        private final PrometheusDataTableColumn thePartnerUnitsColumn;

        /**
         * PartnerAmount column.
         */
        private final PrometheusDataTableColumn thePartnerAmountColumn;

        /**
         * Dilution column.
         */
        private final PrometheusDataTableColumn theDilutionColumn;

        /**
         * QualifyYears column.
         */
        private final PrometheusDataTableColumn theQualYearsColumn;

        /**
         * ReturnedAccount column.
         */
        private final PrometheusDataTableColumn theReturnedAccountColumn;

        /**
         * ReturnedCash column.
         */
        private final PrometheusDataTableColumn theReturnedCashColumn;

        /**
         * TaxCredit column.
         */
        private final PrometheusDataTableColumn theTaxCreditColumn;

        /**
         * NatIns column.
         */
        private final PrometheusDataTableColumn theNatInsColumn;

        /**
         * Benefit column.
         */
        private final PrometheusDataTableColumn theBenefitColumn;

        /**
         * Withheld column.
         */
        private final PrometheusDataTableColumn theWithheldColumn;

        /**
         * Action column.
         */
        private final PrometheusDataTableColumn theActionColumn;

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
            final MetisFieldCalendarCellEditor myDateEditor = theFieldMgr.allocateCalendarCellEditor();
            final MetisFieldIconButtonCellEditor<AssetDirection> myDirectionIconEditor = theFieldMgr.allocateIconButtonCellEditor(AssetDirection.class);
            final MetisFieldIconButtonCellEditor<Boolean> myReconciledIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class);
            final MetisFieldIconButtonCellEditor<PrometheusAction> myActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(PrometheusAction.class);
            final MetisFieldListButtonCellEditor<TransactionTag> myTagEditor = theFieldMgr.allocateScrollListButtonCellEditor(TransactionTag.class);
            final MetisFieldStringCellEditor myStringEditor = theFieldMgr.allocateStringCellEditor();
            final MetisFieldIntegerCellEditor myIntegerEditor = theFieldMgr.allocateIntegerCellEditor();
            final MetisFieldMoneyCellEditor myMoneyEditor = theFieldMgr.allocateMoneyCellEditor();
            final MetisFieldUnitsCellEditor myUnitsEditor = theFieldMgr.allocateUnitsCellEditor();
            final MetisFieldDilutionCellEditor myDilutionEditor = theFieldMgr.allocateDilutionCellEditor();
            final MetisFieldScrollButtonCellEditor<TransactionAsset> myAccountEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionAsset.class);
            final MetisFieldScrollButtonCellEditor<TransactionAsset> myPartnerEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionAsset.class);
            final MetisFieldScrollButtonCellEditor<TransactionAsset> myReturnedEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionAsset.class);
            final MetisFieldScrollButtonCellEditor<TransactionCategory> myCategoryEditor = theFieldMgr.allocateScrollButtonCellEditor(TransactionCategory.class);
            final MetisFieldCalendarCellRenderer myDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            final MetisFieldDecimalCellRenderer myDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            final MetisFieldStringCellRenderer myStringRenderer = theFieldMgr.allocateStringCellRenderer();
            final MetisFieldIntegerCellRenderer myIntegerRenderer = theFieldMgr.allocateIntegerCellRenderer();
            final MetisFieldIconButtonCellRenderer<AssetDirection> myDirectionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(AssetDirection.class);
            final MetisFieldIconButtonCellRenderer<Boolean> myReconciledIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(Boolean.class);
            final MetisFieldIconButtonCellRenderer<PrometheusAction> myActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(PrometheusAction.class);

            /* Configure the iconButtons */
            final Map<Boolean, TethysIconMapSet<Boolean>> myRecMapSets = MoneyWiseIcon.configureReconciledIconButton();
            myReconciledIconEditor.setIconMapSet(r -> myRecMapSets.get(determineReconciledState(r)));
            myReconciledIconRenderer.setIconMapSet(r -> myRecMapSets.get(determineReconciledState(r)));
            final Map<Boolean, TethysIconMapSet<AssetDirection>> myDirMapSets = MoneyWiseIcon.configureDirectionIconButton();
            myDirectionIconEditor.setIconMapSet(r -> myDirMapSets.get(determineDirectionState(r)));
            myDirectionIconRenderer.setIconMapSet(r -> myDirMapSets.get(determineDirectionState(r)));
            final TethysIconMapSet<PrometheusAction> myActionMapSet = PrometheusIcon.configureStatusIconButton();
            myActionIconRenderer.setIconMapSet(r -> myActionMapSet);
            myActionIconEditor.setIconMapSet(r -> myActionMapSet);

            /* Create the columns */
            declareColumn(new PrometheusDataTableColumn(COLUMN_DATE, WIDTH_DATE, myDateRenderer, myDateEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_ACCOUNT, WIDTH_NAME, myStringRenderer, myAccountEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, myStringRenderer, myCategoryEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_DIRECTION, WIDTH_ICON, myDirectionIconRenderer, myDirectionIconEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_PARTNER, WIDTH_NAME, myStringRenderer, myPartnerEditor));
            declareColumn(new PrometheusDataTableColumn(COLUMN_RECONCILED, WIDTH_ICON, myReconciledIconRenderer, myReconciledIconEditor));
            theDescColumn = new PrometheusDataTableColumn(COLUMN_DESC, WIDTH_NAME, myStringRenderer, myStringEditor);
            declareColumn(theDescColumn);
            thePaidColumn = new PrometheusDataTableColumn(COLUMN_DEBITED, WIDTH_MONEY, myDecimalRenderer);
            declareColumn(thePaidColumn);
            theReceivedColumn = new PrometheusDataTableColumn(COLUMN_CREDITED, WIDTH_MONEY, myDecimalRenderer);
            declareColumn(theReceivedColumn);
            theBalanceColumn = new PrometheusDataTableColumn(COLUMN_BALANCE, WIDTH_MONEY, myDecimalRenderer);
            declareColumn(theBalanceColumn);
            theAmountColumn = new PrometheusDataTableColumn(COLUMN_AMOUNT, WIDTH_MONEY, myDecimalRenderer, myMoneyEditor);
            declareColumn(theAmountColumn);
            theReferenceColumn = new PrometheusDataTableColumn(COLUMN_REF, WIDTH_NAME, myStringRenderer, myStringEditor);
            declareColumn(theReferenceColumn);
            theTagsColumn = new PrometheusDataTableColumn(COLUMN_TAGS, WIDTH_NAME, myStringRenderer, myTagEditor);
            declareColumn(theTagsColumn);
            theAccountUnitsColumn = new PrometheusDataTableColumn(COLUMN_ACCOUNTUNITS, WIDTH_UNITS, myDecimalRenderer, myUnitsEditor);
            declareColumn(theAccountUnitsColumn);
            thePartnerUnitsColumn = new PrometheusDataTableColumn(COLUMN_PARTNERUNITS, WIDTH_UNITS, myDecimalRenderer, myUnitsEditor);
            declareColumn(thePartnerUnitsColumn);
            thePartnerAmountColumn = new PrometheusDataTableColumn(COLUMN_PARTNERAMOUNT, WIDTH_MONEY, myDecimalRenderer, myMoneyEditor);
            declareColumn(thePartnerAmountColumn);
            theDilutionColumn = new PrometheusDataTableColumn(COLUMN_DILUTION, WIDTH_DILUTION, myDecimalRenderer, myDilutionEditor);
            declareColumn(theDilutionColumn);
            theQualYearsColumn = new PrometheusDataTableColumn(COLUMN_QUALYEARS, WIDTH_INT << 1, myIntegerRenderer, myIntegerEditor);
            declareColumn(theQualYearsColumn);
            theReturnedAccountColumn = new PrometheusDataTableColumn(COLUMN_RETURNEDACCOUNT, WIDTH_NAME, myStringRenderer, myReturnedEditor);
            declareColumn(theReturnedAccountColumn);
            theReturnedCashColumn = new PrometheusDataTableColumn(COLUMN_RETURNEDCASH, WIDTH_MONEY, myDecimalRenderer, myMoneyEditor);
            declareColumn(theReturnedCashColumn);
            theTaxCreditColumn = new PrometheusDataTableColumn(COLUMN_TAXCREDIT, WIDTH_MONEY, myDecimalRenderer, myMoneyEditor);
            declareColumn(theTaxCreditColumn);
            theNatInsColumn = new PrometheusDataTableColumn(COLUMN_NATINS, WIDTH_MONEY, myDecimalRenderer, myMoneyEditor);
            declareColumn(theNatInsColumn);
            theBenefitColumn = new PrometheusDataTableColumn(COLUMN_BENEFIT, WIDTH_MONEY, myDecimalRenderer, myMoneyEditor);
            declareColumn(theBenefitColumn);
            theWithheldColumn = new PrometheusDataTableColumn(COLUMN_WITHHELD, WIDTH_MONEY, myDecimalRenderer, myMoneyEditor);
            declareColumn(theWithheldColumn);
            theActionColumn = new PrometheusDataTableColumn(COLUMN_ACTION, WIDTH_ICON << 1, myActionIconRenderer, myActionIconEditor);
            declareColumn(theActionColumn);

            /* Configure dates */
            myDateEditor.setDateConfigurator((r, c) -> handleDateEvent(c));

            /* Add menuBuilders */
            myAccountEditor.setMenuConfigurator(this::buildAccountMenu);
            myCategoryEditor.setMenuConfigurator(this::buildCategoryMenu);
            myPartnerEditor.setMenuConfigurator(this::buildPartnerMenu);
            myReturnedEditor.setMenuConfigurator(this::buildReturnedMenu);

            /* Configure the tag editor */
            myTagEditor.setSelectables(c -> theActiveTrans.buildTransactionTags());
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
         * @param pRowIndex the row index
         * @return the state
         */
        private boolean determineReconciledState(final int pRowIndex) {
            final Transaction myTrans = theModel.getItemAtIndex(pRowIndex);
            return myTrans.isLocked();
        }

        /**
         * Determine direction state.
         * @param pRowIndex the row index
         * @return the state
         */
        private boolean determineDirectionState(final int pRowIndex) {
            final Transaction myTrans = theModel.getItemAtIndex(pRowIndex);
            return !myTrans.isReconciled();
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
                case COLUMN_ACCOUNTUNITS:
                    return TITLE_ACCOUNTUNITS;
                case COLUMN_PARTNERUNITS:
                    return TITLE_PARTNERUNITS;
                case COLUMN_PARTNERAMOUNT:
                    return TITLE_PARTNERAMOUNT;
                case COLUMN_DILUTION:
                    return TITLE_DILUTION;
                case COLUMN_QUALYEARS:
                    return TITLE_QUALYEARS;
                case COLUMN_RETURNEDACCOUNT:
                    return TITLE_RETURNEDACCOUNT;
                case COLUMN_RETURNEDCASH:
                    return TITLE_RETURNEDCASH;
                case COLUMN_TAXCREDIT:
                    return TITLE_TAXCREDIT;
                case COLUMN_NATINS:
                    return TITLE_NATINS;
                case COLUMN_BENEFIT:
                    return TITLE_BENEFIT;
                case COLUMN_WITHHELD:
                    return TITLE_WITHHELD;
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
                    return pTrans.getTransactionTags();
                case COLUMN_ACCOUNTUNITS:
                    return pTrans.getAccountDeltaUnits();
                case COLUMN_PARTNERUNITS:
                    return pTrans.getPartnerDeltaUnits();
                case COLUMN_PARTNERAMOUNT:
                    return pTrans.getPartnerAmount();
                case COLUMN_DILUTION:
                    return pTrans.getDilution();
                case COLUMN_QUALYEARS:
                    return pTrans.getYears();
                case COLUMN_RETURNEDACCOUNT:
                    return pTrans.getReturnedCashAccount();
                case COLUMN_RETURNEDCASH:
                    return pTrans.getReturnedCash();
                case COLUMN_TAXCREDIT:
                    return pTrans.getTaxCredit();
                case COLUMN_NATINS:
                    return pTrans.getEmployeeNatIns();
                case COLUMN_BENEFIT:
                    return pTrans.getDeemedBenefit();
                case COLUMN_WITHHELD:
                    return pTrans.getWithheld();
                case COLUMN_DEBITED:
                    return theFilter.getDebitForTransaction(pTrans);
                case COLUMN_CREDITED:
                    return theFilter.getCreditForTransaction(pTrans);
                case COLUMN_BALANCE:
                    return theFilter.getBalanceForTransaction(pTrans);
                case COLUMN_ACTION:
                    return pTrans.isReconciled()
                                                 ? PrometheusAction.DO
                                                 : PrometheusAction.DELETE;
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
                    return PrometheusAction.DO;
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
                case COLUMN_ACCOUNTUNITS:
                    pItem.setAccountDeltaUnits((TethysUnits) pValue);
                    break;
                case COLUMN_PARTNERUNITS:
                    pItem.setPartnerDeltaUnits((TethysUnits) pValue);
                    break;
                case COLUMN_PARTNERAMOUNT:
                    pItem.setPartnerAmount((TethysMoney) pValue);
                    break;
                case COLUMN_DILUTION:
                    pItem.setDilution((TethysDilution) pValue);
                    break;
                case COLUMN_QUALYEARS:
                    pItem.setYears((Integer) pValue);
                    break;
                case COLUMN_RETURNEDACCOUNT:
                    pItem.setReturnedCashAccount((TransactionAsset) pValue);
                    theBuilder.autoCorrect(pItem);
                    break;
                case COLUMN_RETURNEDCASH:
                    pItem.setReturnedCash((TethysMoney) pValue);
                    break;
                case COLUMN_TAXCREDIT:
                    pItem.setTaxCredit((TethysMoney) pValue);
                    break;
                case COLUMN_NATINS:
                    pItem.setEmployeeNatIns((TethysMoney) pValue);
                    break;
                case COLUMN_BENEFIT:
                    pItem.setBenefit((TethysMoney) pValue);
                    break;
                case COLUMN_WITHHELD:
                    pItem.setWithheld((TethysMoney) pValue);
                    break;
                case COLUMN_RECONCILED:
                    pItem.setReconciled((Boolean) pValue);
                    break;
                case COLUMN_ACTION:
                    pItem.setDeleted(true);
                    break;
                case COLUMN_TAGS:
                    pItem.setTransactionTags((List<TransactionTag>) pValue);
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
                    return !pItem.isReconciled() && !pItem.needsNullAmount();
                case COLUMN_DESC:
                case COLUMN_REF:
                    // case COLUMN_TAGS:
                    return true;
                case COLUMN_TAXCREDIT:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.TAXCREDIT);
                case COLUMN_NATINS:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.EMPLOYEENATINS);
                case COLUMN_BENEFIT:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.DEEMEDBENEFIT);
                case COLUMN_WITHHELD:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.WITHHELD);
                case COLUMN_ACCOUNTUNITS:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.ACCOUNTDELTAUNITS);
                case COLUMN_PARTNERUNITS:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.PARTNERDELTAUNITS);
                case COLUMN_PARTNERAMOUNT:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.PARTNERAMOUNT);
                case COLUMN_DILUTION:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.DILUTION);
                case COLUMN_RETURNEDACCOUNT:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.RETURNEDCASHACCOUNT);
                case COLUMN_RETURNEDCASH:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.RETURNEDCASH);
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
                case COLUMN_ACCOUNTUNITS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.ACCOUNTDELTAUNITS);
                case COLUMN_PARTNERUNITS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERDELTAUNITS);
                case COLUMN_PARTNERAMOUNT:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.PARTNERAMOUNT);
                case COLUMN_DILUTION:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.DILUTION);
                case COLUMN_QUALYEARS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.QUALIFYYEARS);
                case COLUMN_RETURNEDACCOUNT:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASHACCOUNT);
                case COLUMN_RETURNEDCASH:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.RETURNEDCASH);
                case COLUMN_TAXCREDIT:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.TAXCREDIT);
                case COLUMN_NATINS:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.EMPLOYEENATINS);
                case COLUMN_BENEFIT:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.DEEMEDBENEFIT);
                case COLUMN_WITHHELD:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.WITHHELD);
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
            hideColumn(theWithheldColumn);
            hideColumn(theAccountUnitsColumn);
            hideColumn(thePartnerUnitsColumn);
            hideColumn(thePartnerAmountColumn);
            hideColumn(theDilutionColumn);
            hideColumn(theReturnedAccountColumn);
            hideColumn(theReturnedCashColumn);
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
                    revealColumn(thePartnerAmountColumn);
                    break;
                case SALARY:
                    revealColumn(theAmountColumn);
                    revealColumn(theTaxCreditColumn);
                    revealColumn(theNatInsColumn);
                    revealColumn(theBenefitColumn);
                    revealColumn(theWithheldColumn);
                    break;
                case INTEREST:
                    revealColumn(theAmountColumn);
                    revealColumn(theTaxCreditColumn);
                    revealColumn(theWithheldColumn);
                    break;
                case DIVIDEND:
                    revealColumn(theAmountColumn);
                    revealColumn(theTaxCreditColumn);
                    revealColumn(theAccountUnitsColumn);
                    break;
                case SECURITY:
                    revealColumn(theAmountColumn);
                    revealColumn(theAccountUnitsColumn);
                    revealColumn(thePartnerUnitsColumn);
                    revealColumn(theDilutionColumn);
                    revealColumn(theReturnedAccountColumn);
                    revealColumn(theReturnedCashColumn);
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
                    revealColumn(theWithheldColumn);
                    revealColumn(theAccountUnitsColumn);
                    revealColumn(thePartnerUnitsColumn);
                    revealColumn(thePartnerAmountColumn);
                    revealColumn(theDilutionColumn);
                    revealColumn(theReturnedAccountColumn);
                    revealColumn(theReturnedCashColumn);
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
         * Obtain the popUpMenu for Accounts.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildAccountMenu(final Integer pRowIndex,
                                      final TethysScrollMenu<TransactionAsset, Icon> pMenu) {
            /* Record active item */
            final Transaction myTrans = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveTrans.buildAccountMenu(pMenu, myTrans);
        }

        /**
         * Obtain the popUpMenu for Partner Accounts.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildPartnerMenu(final Integer pRowIndex,
                                      final TethysScrollMenu<TransactionAsset, Icon> pMenu) {
            /* Record active item */
            final Transaction myTrans = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveTrans.buildPartnerMenu(pMenu, myTrans);
        }

        /**
         * Build the popUpMenu for categories.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildCategoryMenu(final Integer pRowIndex,
                                       final TethysScrollMenu<TransactionCategory, Icon> pMenu) {
            /* Record active item */
            final Transaction myTrans = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveTrans.buildCategoryMenu(pMenu, myTrans);
        }

        /**
         * Obtain the popUpMenu for returnedCash Accounts.
         * @param pRowIndex the rowIndex for the item
         * @param pMenu the menu to build
         */
        private void buildReturnedMenu(final Integer pRowIndex,
                                       final TethysScrollMenu<TransactionAsset, Icon> pMenu) {
            /* Record active item */
            final Transaction myTrans = theModel.getItemAtIndex(pRowIndex);

            /* Build the menu */
            theActiveTrans.buildReturnedAccountMenu(pMenu, myTrans);
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
