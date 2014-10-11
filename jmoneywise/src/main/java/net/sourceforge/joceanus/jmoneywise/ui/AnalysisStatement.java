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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.DilutionCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IntegerCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.MoneyCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.ScrollListButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.UnitsCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IntegerCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JDataProfile;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.TransactionTag;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisColumnSet;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.TransactionPanel;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableSelection;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollListButton.JScrollListMenuBuilder;

/**
 * Analysis Statement.
 */
public class AnalysisStatement
        extends JDataTable<Transaction, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8054491530459145911L;

    /**
     * Date Column Title.
     */
    private static final String TITLE_DATE = Transaction.FIELD_DATE.getName();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = Transaction.FIELD_CATEGORY.getName();

    /**
     * Debit Column Title.
     */
    private static final String TITLE_DEBIT = Transaction.FIELD_DEBIT.getName();

    /**
     * Credit Column Title.
     */
    private static final String TITLE_CREDIT = Transaction.FIELD_CREDIT.getName();

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
     * Portfolio Column Title.
     */
    private static final String TITLE_PORTFOLIO = TransactionInfoClass.PORTFOLIO.toString();

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
    private static final String TITLE_ACTION = MoneyWiseUIControlResource.COLUMN_ACTION.getValue();

    /**
     * Opening Balance Text.
     */
    private static final String TEXT_OPENBALANCE = MoneyWiseUIResource.STATEMENT_OPENINGBALANCE.getValue();

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The updateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The event entry.
     */
    private final transient UpdateEntry<Transaction, MoneyWiseDataType> theTransEntry;

    /**
     * The info entry.
     */
    private final transient UpdateEntry<TransactionInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The analysis data entry.
     */
    private final transient JDataEntry theDataAnalysis;

    /**
     * The filter data entry.
     */
    private final transient JDataEntry theDataFilter;

    /**
     * Analysis Selection panel.
     */
    private final AnalysisSelect theSelect;

    /**
     * The action buttons.
     */
    private final ActionButtons theActionButtons;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

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
    private final JEnablePanel thePanel;

    /**
     * The date range.
     */
    private transient JDateDayRange theRange;

    /**
     * The analysis filter.
     */
    private transient AnalysisFilter<?> theFilter;

    /**
     * Transactions.
     */
    private transient TransactionList theTransactions = null;

    /**
     * Statement Header.
     */
    private transient Transaction theHeader;

    /**
     * The new button.
     */
    private final JButton theNewButton;

    /**
     * The Transaction dialog.
     */
    private final TransactionPanel theActiveTrans;

    /**
     * The List Selection Model.
     */
    private final transient JDataTableSelection<Transaction, MoneyWiseDataType> theSelectionModel;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public AnalysisStatement(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView, MoneyWiseDataType.class);
        theTransEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSACTION);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSACTIONINFO);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_EDIT);
        theDataAnalysis = myDataMgr.new JDataEntry(AnalysisStatement.class.getSimpleName());
        theDataAnalysis.addAsChildOf(mySection);
        theDataAnalysis.setObject(theUpdateSet);
        theDataFilter = myDataMgr.new JDataEntry(AnalysisFilter.class.getSimpleName());
        theDataFilter.addAsChildOf(mySection);

        /* Create new button */
        theNewButton = MoneyWiseIcons.getNewButton();

        /* Create the Analysis Selection */
        theSelect = new AnalysisSelect(theNewButton);

        /* Create the action buttons */
        theActionButtons = new ActionButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataAnalysis);

        /* Create the table model */
        theModel = new AnalysisTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new AnalysisColumnModel(this);
        setColumnModel(theColumns);

        /* Create the header panel */
        JPanel myHeader = new JPanel();
        myHeader.setLayout(new BoxLayout(myHeader, BoxLayout.X_AXIS));
        myHeader.add(theSelect);
        myHeader.add(theError);
        myHeader.add(theActionButtons);

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(myHeader);
        thePanel.add(getScrollPane());

        /* Create a transaction panel */
        theActiveTrans = new TransactionPanel(theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveTrans);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the selection model */
        theSelectionModel = new JDataTableSelection<Transaction, MoneyWiseDataType>(this, theActiveTrans);

        /* Create listener */
        new AnalysisListener();

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

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

        /* Set the selection */
        theRange = null;
        setSelection(theSelect.getRange());
    }

    /**
     * Refresh data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Statement");

        /* Update the selection */
        theSelect.refreshData(theView);

        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Set the selection */
        theRange = null;
        setSelection(theSelect.getRange());

        /* Complete the task */
        myTask.end();
    }

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();
        boolean isItemEditing = theActiveTrans.isEditing();

        /* Update the table buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);
        theSelect.setEnabled(!hasUpdates && !isItemEditing);

        /* Adjust enable of the table */
        setEnabled(!isItemEditing);

        /* Update the top level tabs */
        fireStateChanged();
    }

    /**
     * Set Selection to the specified date range.
     * @param pRange the Date range for the extract
     */
    public void setSelection(final JDateDayRange pRange) {
        theRange = pRange;
        theTransactions = null;
        theHeader = null;
        TransactionInfoList myInfo = null;
        if (theRange != null) {
            /* Get the Events edit list */
            MoneyWiseData myData = theView.getData();
            TransactionList myTransactions = myData.getTransactions();
            theTransactions = myTransactions.deriveEditList(pRange);
            theHeader = new AnalysisHeader(theTransactions);
            myInfo = theTransactions.getTransactionInfo();

            /* Update the column editors */
            theColumns.refreshData();

            /* Notify panel of refresh */
            theActiveTrans.refreshData();
            theActiveTrans.updateEditors(theRange);
        }

        /* Update lists */
        setList(theTransactions);
        theTransEntry.setDataList(theTransactions);
        theInfoEntry.setDataList(myInfo);
        theActionButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());
        fireStateChanged();

        /* Touch the filter and updateSet */
        theDataFilter.setObject(theFilter);
        theDataAnalysis.setObject(theUpdateSet);
    }

    /**
     * Does this panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    /**
     * Do we have errors?
     * @return true/false
     */
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
        private AnalysisTableModel(final AnalysisStatement pTable) {
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
        public JDataField getFieldForCell(final Transaction pItem,
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
        public boolean includeRow(final Transaction pRow) {
            /* Handle no filter */
            if (theFilter == null) {
                return false;
            }

            /* Return visibility of row */
            return !pRow.isDeleted() && !theFilter.filterTransaction(pRow);
        }
    }

    /**
     * Listener class.
     */
    private final class AnalysisListener
            implements ChangeListener, ActionListener {
        /**
         * Constructor.
         */
        private AnalysisListener() {
            theError.addChangeListener(this);
            theView.addChangeListener(this);
            theSelect.addChangeListener(this);
            theActionButtons.addActionListener(this);
            theUpdateSet.addChangeListener(this);
            theActiveTrans.addChangeListener(this);
            theActiveTrans.addActionListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If this is the error panel */
            if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelect.setVisible(!isError);

                /* Lock scroll area */
                getScrollPane().setEnabled(!isError);

                /* Lock Action Buttons */
                theActionButtons.setEnabled(!isError);

                /* If this is the View */
            } else if (theView.equals(o)) {
                /* Refresh the data */
                refreshData();

                /* If we are performing a rewind */
            } else if (theUpdateSet.equals(o)) {
                /* Only action if we are not editing */
                if (!theActiveTrans.isEditing()) {
                    /* Handle the reWind */
                    theSelectionModel.handleReWind();
                }

                /* Adjust for changes */
                notifyChanges();

                /* If we are noting change of edit state */
            } else if (theActiveTrans.equals(o)) {
                /* Only action if we are not editing */
                if (!theActiveTrans.isEditing()) {
                    /* handle the edit transition */
                    theSelectionModel.handleEditTransition();
                }

                /* Note changes */
                notifyChanges();

                /* If this is the Selection */
            } else if (theSelect.equals(o)) {
                /* Set the filter */
                theFilter = theSelect.getFilter();

                /* Ensure that columns are correct */
                theColumns.adjustColumns(theSelect.showColumns()
                                                                ? theSelect.getColumns()
                                                                : AnalysisColumnSet.BALANCE);

                /* Set the selection */
                JDateDayRange myRange = theSelect.getRange();
                if (Difference.isEqual(myRange, theRange)) {
                    theModel.fireNewDataEvents();
                } else {
                    setSelection(myRange);
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();

            /* If this event relates to the action buttons */
            if (theActionButtons.equals(o)) {
                /* Cancel any editing */
                cancelEditing();

                /* Perform the command */
                theUpdateSet.processCommand(pEvent.getActionCommand(), theError);

                /* Notify listeners of changes */
                notifyChanges();
            } else if ((theActiveTrans.equals(o))
                       && (pEvent instanceof ActionDetailEvent)) {
                cascadeActionEvent((ActionDetailEvent) pEvent);
            } else if (theNewButton.equals(o)) {
                // theModel.addNewItem();
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
         * Category column id.
         */
        private static final int COLUMN_CATEGORY = 1;

        /**
         * Debit column id.
         */
        private static final int COLUMN_DEBIT = 2;

        /**
         * Credit column id.
         */
        private static final int COLUMN_CREDIT = 3;

        /**
         * Reconciled column id.
         */
        private static final int COLUMN_RECONCILED = 4;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 5;

        /**
         * Debited column id.
         */
        private static final int COLUMN_DEBITED = 6;

        /**
         * Credited column id.
         */
        private static final int COLUMN_CREDITED = 7;

        /**
         * Balance column id.
         */
        private static final int COLUMN_BALANCE = 8;

        /**
         * Amount column id.
         */
        private static final int COLUMN_AMOUNT = 9;

        /**
         * Tags column id.
         */
        private static final int COLUMN_TAGS = 10;

        /**
         * Reference column id.
         */
        private static final int COLUMN_REF = 11;

        /**
         * CreditUnits column id.
         */
        private static final int COLUMN_CREDUNITS = 12;

        /**
         * DebitUnits column id.
         */
        private static final int COLUMN_DEBUNITS = 13;

        /**
         * Dilution column id.
         */
        private static final int COLUMN_DILUTION = 14;

        /**
         * Portfolio column id.
         */
        private static final int COLUMN_PORTFOLIO = 15;

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
         * ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<Portfolio> thePortfolioEditor;

        /**
         * ScrollButton Menu Editor.
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
         * Portfolio column.
         */
        private final JDataTableColumn thePortfolioColumn;

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
        private AnalysisColumnModel(final AnalysisStatement pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theReconciledIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theActionIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theTagEditor = theFieldMgr.allocateScrollListButtonCellEditor();
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theIntegerEditor = theFieldMgr.allocateIntegerCellEditor();
            theMoneyEditor = theFieldMgr.allocateMoneyCellEditor();
            theUnitsEditor = theFieldMgr.allocateUnitsCellEditor();
            theDilutionEditor = theFieldMgr.allocateDilutionCellEditor();
            thePortfolioEditor = theFieldMgr.allocateScrollButtonCellEditor(Portfolio.class);
            theDepositEditor = theFieldMgr.allocateScrollButtonCellEditor(Deposit.class);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theIntegerRenderer = theFieldMgr.allocateIntegerCellRenderer();
            theReconciledIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theReconciledIconEditor);
            theActionIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theActionIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildReconciledButton(theReconciledIconEditor.getComplexState());
            MoneyWiseIcons.buildStatusButton(theActionIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_DEBIT, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_CREDIT, WIDTH_NAME, theStringRenderer));
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
            thePortfolioColumn = new JDataTableColumn(COLUMN_PORTFOLIO, WIDTH_NAME, theStringRenderer, thePortfolioEditor);
            declareColumn(thePortfolioColumn);
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

            /* Set the balance column set */
            adjustColumns(AnalysisColumnSet.BALANCE);

            /* Add listeners */
            new EditorListener();
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
                case COLUMN_DEBIT:
                    return TITLE_DEBIT;
                case COLUMN_CREDIT:
                    return TITLE_CREDIT;
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
                case COLUMN_PORTFOLIO:
                    return TITLE_PORTFOLIO;
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
                case COLUMN_CREDIT:
                    return pTrans.getCredit();
                case COLUMN_DEBIT:
                    return pTrans.getDebit();
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
                case COLUMN_PORTFOLIO:
                    return pTrans.getPortfolio();
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
         * @throws JOceanusException on error
         */
        private void setItemValue(final Transaction pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pItem.setDate((JDateDay) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pItem.setCategory((TransactionCategory) pValue);
                    break;
                case COLUMN_DEBIT:
                    pItem.setDebit((AssetBase<?>) pValue);
                    break;
                case COLUMN_CREDIT:
                    pItem.setCredit((AssetBase<?>) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setComments((String) pValue);
                    break;
                case COLUMN_AMOUNT:
                    pItem.setAmount((JMoney) pValue);
                    break;
                case COLUMN_REF:
                    pItem.setReference((String) pValue);
                    break;
                case COLUMN_CREDUNITS:
                    pItem.setCreditUnits((JUnits) pValue);
                    break;
                case COLUMN_DEBUNITS:
                    pItem.setDebitUnits((JUnits) pValue);
                    break;
                case COLUMN_DILUTION:
                    pItem.setDilution((JDilution) pValue);
                    break;
                case COLUMN_PORTFOLIO:
                    pItem.setPortfolio((Portfolio) pValue);
                    break;
                case COLUMN_QUALYEARS:
                    pItem.setYears((Integer) pValue);
                    break;
                case COLUMN_3RDPARTY:
                    pItem.setThirdParty((Deposit) pValue);
                    break;
                case COLUMN_TAXCREDIT:
                    pItem.setTaxCredit((JMoney) pValue);
                    break;
                case COLUMN_NATINS:
                    pItem.setNatInsurance((JMoney) pValue);
                    break;
                case COLUMN_BENEFIT:
                    pItem.setBenefit((JMoney) pValue);
                    break;
                case COLUMN_DONATION:
                    pItem.setDonation((JMoney) pValue);
                    break;
                case COLUMN_RECONCILED:
                    pItem.setReconciled((Boolean) pValue);
                    break;
                case COLUMN_ACTION:
                    pItem.setDeleted(true);
                    break;
                case COLUMN_TAGS:
                    theActiveTrans.updateTag(pItem, (ItemEvent) pValue);
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
                case COLUMN_CREDIT:
                case COLUMN_DEBIT:
                case COLUMN_CATEGORY:
                case COLUMN_AMOUNT:
                case COLUMN_ACTION:
                    return !pItem.isReconciled();
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
                case COLUMN_PORTFOLIO:
                    return TransactionPanel.isEditableField(pItem, TransactionInfoClass.PORTFOLIO);
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
        private JDataField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return Transaction.FIELD_DATE;
                case COLUMN_DESC:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.COMMENTS);
                case COLUMN_CATEGORY:
                    return Transaction.FIELD_CATEGORY;
                case COLUMN_CREDIT:
                    return Transaction.FIELD_CREDIT;
                case COLUMN_DEBIT:
                    return Transaction.FIELD_DEBIT;
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
                case COLUMN_PORTFOLIO:
                    return TransactionInfoSet.getFieldForClass(TransactionInfoClass.PORTFOLIO);
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
            hideColumn(thePortfolioColumn);
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
                    revealColumn(thePortfolioColumn);
                    break;
                case SECURITY:
                    revealColumn(theAmountColumn);
                    revealColumn(theCredUnitsColumn);
                    revealColumn(theDebUnitsColumn);
                    revealColumn(thePortfolioColumn);
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
                    revealColumn(thePortfolioColumn);
                    revealColumn(theDilutionColumn);
                    revealColumn(the3rdPartyColumn);
                    revealColumn(theQualYearsColumn);
                    reSize = false;
                    break;
            }

            /* Set reSize mode */
            setAutoResizeMode(reSize
                                    ? AUTO_RESIZE_ALL_COLUMNS
                                    : AUTO_RESIZE_OFF);

            /* Store the column set */
            theColumnSet = pSet;
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
                thePortfolioEditor.addChangeListener(this);
                theDepositEditor.addChangeListener(this);
                theTagEditor.addChangeListener(this);
            }

            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                Object o = pEvent.getSource();

                if (thePortfolioEditor.equals(o)) {
                    buildPortfolioMenu();
                } else if (theDepositEditor.equals(o)) {
                    buildThirdPartyMenu();
                } else if (theTagEditor.equals(o)) {
                    buildTagMenu();
                }
            }

            /**
             * Build the popUpMenu for portfolios.
             */
            private void buildPortfolioMenu() {
                /* Access details */
                JScrollMenuBuilder<Portfolio> myBuilder = thePortfolioEditor.getMenuBuilder();

                /* Record active item */
                Point myCell = thePortfolioEditor.getPoint();
                Transaction myTrans = theModel.getItemAtIndex(myCell.y);

                /* Build the menu */
                theActiveTrans.buildPortfolioMenu(myBuilder, myTrans);
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
            setSplit(Boolean.FALSE);
        }
    }
}
