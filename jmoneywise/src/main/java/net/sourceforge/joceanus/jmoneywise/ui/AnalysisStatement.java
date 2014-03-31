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

import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AnalysisStatement.class.getName());

    /**
     * Date Column Title.
     */
    private static final String TITLE_DATE = Transaction.FIELD_DATE.getName();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = Transaction.FIELD_CATEGORY.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = TransactionInfoClass.COMMENTS.toString();

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
    private static final String TITLE_RECONCILED = NLS_BUNDLE.getString("TitleReconciled");

    /**
     * Debited Column Title.
     */
    private static final String TITLE_DEBITED = NLS_BUNDLE.getString("TitleDebited");

    /**
     * Credited Column Title.
     */
    private static final String TITLE_CREDITED = NLS_BUNDLE.getString("TitleCredited");

    /**
     * Balance Column Title.
     */
    private static final String TITLE_BALANCE = NLS_BUNDLE.getString("TitleBalance");

    /**
     * Opening Balance Text.
     */
    private static final String TEXT_OPENBALANCE = NLS_BUNDLE.getString("TextOpenBalance");

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
     * The save buttons.
     */
    private final SaveButtons theSaveButtons;

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
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView);
        theTransEntry = theUpdateSet.registerClass(Transaction.class);
        theInfoEntry = theUpdateSet.registerClass(TransactionInfo.class);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_EDIT);
        theDataAnalysis = myDataMgr.new JDataEntry(AnalysisStatement.class.getSimpleName());
        theDataAnalysis.addAsChildOf(mySection);
        theDataAnalysis.setObject(theUpdateSet);
        theDataFilter = myDataMgr.new JDataEntry(AnalysisFilter.class.getSimpleName());
        theDataFilter.addAsChildOf(mySection);

        /* Create the Analysis Selection */
        theSelect = new AnalysisSelect();

        /* Create the save buttons */
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataAnalysis);

        /* Create the table model */
        theModel = new AnalysisTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new AnalysisColumnModel(this);
        setColumnModel(theColumns);

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theSelect);
        thePanel.add(theError);
        thePanel.add(getScrollPane());
        thePanel.add(theSaveButtons);

        /* Create listener */
        AnalysisListener myListener = new AnalysisListener();
        theView.addChangeListener(myListener);
        theSelect.addChangeListener(myListener);
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
    public void refreshData() {
        /* Update the selection */
        theSelect.refreshData(theView);

        /* Set the filter */
        theFilter = theSelect.getFilter();

        /* Set the selection */
        theRange = null;
        setSelection(theSelect.getRange());
    }

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
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
        }
        setList(theTransactions);
        theTransEntry.setDataList(theTransactions);
        theInfoEntry.setDataList(myInfo);
        theSaveButtons.setEnabled(true);
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
        public boolean isCellEditable(final Transaction pTrans,
                                      final int pColIndex) {
            return false;
        }

        @Override
        public Transaction getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return pRowIndex == 0
                                 ? theHeader
                                 : theTransactions.get(pRowIndex - 1);
        }

        @Override
        public Object getItemValue(final Transaction pTrans,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return pTrans.isHeader()
                                    ? theColumns.getHeaderValue(pColIndex)
                                    : theColumns.getItemValue(pTrans, pColIndex);
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
            implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If this is the View */
            if (theView.equals(o)) {
                /* Refresh the data */
                refreshData();
            }

            /* If this is the Selection */
            if (theSelect.equals(o)) {
                /* Set the filter */
                theFilter = theSelect.getFilter();

                /* Set the selection */
                JDateDayRange myRange = theSelect.getRange();
                if (Difference.isEqual(myRange, theRange)) {
                    theModel.fireNewDataEvents();
                } else {
                    setSelection(myRange);
                }
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
         * Description column id.
         */
        private static final int COLUMN_DESC = 4;

        /**
         * Reconciled column id.
         */
        private static final int COLUMN_RECONCILED = 5;

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
         * Icon Renderer.
         */
        private final IconCellRenderer theIconRenderer;

        /**
         * Constructor.
         * @param pTable the table
         */
        private AnalysisColumnModel(final AnalysisStatement pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theIconRenderer = theFieldMgr.allocateIconCellRenderer();

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer));
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_DEBIT, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_CREDIT, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_RECONCILED, WIDTH_ICON, theIconRenderer));
            declareColumn(new JDataTableColumn(COLUMN_DEBITED, WIDTH_MONEY, theDecimalRenderer));
            declareColumn(new JDataTableColumn(COLUMN_CREDITED, WIDTH_MONEY, theDecimalRenderer));
            declareColumn(new JDataTableColumn(COLUMN_BALANCE, WIDTH_MONEY, theDecimalRenderer));
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
                    return pTrans.isReconciled()
                                                ? Register.ICON_RECONCILED
                                                : null;
                case COLUMN_DEBITED:
                    return theFilter.getDebitForTransaction(pTrans);
                case COLUMN_CREDITED:
                    return theFilter.getCreditForTransaction(pTrans);
                case COLUMN_BALANCE:
                    return theFilter.getBalanceForTransaction(pTrans);
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
                default:
                    return null;
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
                case COLUMN_RECONCILED:
                    return Transaction.FIELD_RECONCILED;
                default:
                    return null;
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
