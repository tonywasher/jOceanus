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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JDataProfile;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;

/**
 * Event Register Table.
 * @author Tony Washer
 */
public class Register
        extends JDataTable<Transaction, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5531752729052421790L;

    /**
     * Date column title.
     */
    protected static final String TITLE_DATE = Transaction.FIELD_DATE.getName();

    /**
     * Description column title.
     */
    protected static final String TITLE_DESC = TransactionInfoClass.COMMENTS.toString();

    /**
     * CategoryType column title.
     */
    protected static final String TITLE_CATEGORY = Transaction.FIELD_CATEGORY.getName();

    /**
     * Amount column title.
     */
    protected static final String TITLE_AMOUNT = Transaction.FIELD_AMOUNT.getName();

    /**
     * Debit column title.
     */
    protected static final String TITLE_DEBIT = Transaction.FIELD_DEBIT.getName();

    /**
     * Credit column title.
     */
    protected static final String TITLE_CREDIT = Transaction.FIELD_CREDIT.getName();

    /**
     * Reconciled Column Title.
     */
    private static final String TITLE_RECONCILED = ResourceMgr.getString(MoneyWiseUIResource.STATEMENT_COLUMN_RECONCILED);

    /**
     * Data View.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * Update Set.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * Transaction Update Entry.
     */
    private final transient UpdateEntry<Transaction, MoneyWiseDataType> theEventEntry;

    /**
     * TransactionInfo Update Entry.
     */
    private final transient UpdateEntry<TransactionInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * Table Model.
     */
    private final RegisterModel theModel;

    /**
     * Transactions.
     */
    private transient TransactionList theTransactions = null;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * Column Model.
     */
    private final RegisterColumnModel theColumns;

    /**
     * Selected range.
     */
    private transient JDateDayRange theRange = null;

    /**
     * Range selection panel.
     */
    private JDateDayRangeSelect theSelect = null;

    /**
     * Action Buttons.
     */
    private final ActionButtons theActionButtons;

    /**
     * Data Entry.
     */
    private final transient JDataEntry theDataRegister;

    /**
     * Error Panel.
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
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    /**
     * Panel width.
     */
    private static final int PANEL_WIDTH = 980;

    /**
     * Constructor for Register Window.
     * @param pView the data view
     */
    public Register(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView, MoneyWiseDataType.class);
        theEventEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSACTION);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSACTIONINFO);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_EDIT);
        theDataRegister = myDataMgr.new JDataEntry(Register.class.getSimpleName());
        theDataRegister.addAsChildOf(mySection);

        /* Create the model and declare it to our superclass */
        theModel = new RegisterModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new RegisterColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(PANEL_WIDTH, HEIGHT_PANEL));

        /* Create the sub panels */
        theSelect = new JDateDayRangeSelect(false);
        theActionButtons = new ActionButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataRegister);

        /* Create listener */
        RegisterListener myListener = new RegisterListener();
        theSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, myListener);
        theError.addChangeListener(myListener);
        theActionButtons.addActionListener(myListener);
        theUpdateSet.addChangeListener(myListener);
        theView.addChangeListener(myListener);

        /* Create the header panel */
        JPanel myHeader = new JPanel();
        myHeader.setLayout(new BoxLayout(myHeader, BoxLayout.X_AXIS));
        myHeader.add(theSelect);
        myHeader.add(theError);
        myHeader.add(theActionButtons);

        /* Create the panel */
        thePanel = new JEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(myHeader);
        thePanel.add(getScrollPane());

        /* Hide the save buttons initially */
        theActionButtons.setVisible(false);
    }

    /**
     * Determine focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Focus on the Data entry */
        theDataRegister.setFocus();
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    private void refreshData() {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Register");

        /* Protect against exceptions */
        try {
            /* Access range */
            JDateDayRange myRange = theView.getRange();
            theSelect.setOverallRange(myRange);
            theRange = theSelect.getRange();
            setSelection(theRange);

            /* Create SavePoint */
            theSelect.createSavePoint();

            /* Touch the updateSet */
            theDataRegister.setObject(theUpdateSet);
        } catch (JOceanusException e) {
            /* Show the error */
            theView.addError(e);

            /* Restore SavePoint */
            theSelect.restoreSavePoint();
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();

        /* Update the table buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates);
        theSelect.setEnabled(!hasUpdates);

        /* Update the top level tabs */
        fireStateChanged();
    }

    /**
     * Set Selection to the specified date range.
     * @param pRange the Date range for the extract
     * @throws JOceanusException on error
     */
    public void setSelection(final JDateDayRange pRange) throws JOceanusException {
        theRange = pRange;
        theTransactions = null;
        TransactionInfoList myInfo = null;
        if (theRange != null) {
            /* Get the Rates edit list */
            MoneyWiseData myData = theView.getData();
            TransactionList myTrans = myData.getTransactions();
            theTransactions = myTrans.deriveEditList(pRange);
            myInfo = theTransactions.getTransactionInfo();
        }
        setList(theTransactions);
        theEventEntry.setDataList(theTransactions);
        theInfoEntry.setDataList(myInfo);
        theActionButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());
        fireStateChanged();

        /* Touch the updateSet */
        theDataRegister.setObject(theUpdateSet);
    }

    /**
     * Set selection to the period designated by the referenced control.
     * @param pSource the source control
     */
    public void selectPeriod(final JDateDayRangeSelect pSource) {
        /* Protect against exceptions */
        try {
            /* Adjust the period selection (this will not call back) */
            theSelect.setSelection(pSource);

            /* Utilise the selection */
            setSelection(theSelect.getRange());

            /* Catch exceptions */
        } catch (JOceanusException e) {
            /* Build the error */
            JOceanusException myError = new JMoneyWiseDataException("Failed to select Range", e);

            /* Show the error */
            setError(myError);

            /* Restore the original selection */
            theSelect.restoreSavePoint();
        }
    }

    /**
     * Register listener class.
     */
    private final class RegisterListener
            implements ActionListener, PropertyChangeListener, ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent e) {
            Object o = e.getSource();

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

                /* If this is the data view */
            } else if (theView.equals(o)) {
                /* Refresh Data */
                refreshData();

                /* If we are performing a rewind */
            } else if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            /* If this is the range select panel */
            if (theSelect.equals(evt.getSource())) {
                /* Protect against exceptions */
                try {
                    /* Set the new range */
                    setSelection(theSelect.getRange());

                    /* Create SavePoint */
                    theSelect.createSavePoint();

                    /* Catch Exceptions */
                } catch (JOceanusException e) {
                    /* Build the error */
                    JOceanusException myError = new JMoneyWiseDataException("Failed to change selection", e);

                    /* Show the error */
                    setError(myError);

                    /* Restore SavePoint */
                    theSelect.restoreSavePoint();
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If this event relates to the action buttons */
            if (theActionButtons.equals(o)) {
                /* Cancel any editing */
                cancelEditing();

                /* Perform the command */
                theUpdateSet.processCommand(e.getActionCommand(), theError);

                /* Notify listeners of changes */
                notifyChanges();
            }
        }
    }

    /**
     * Register table model.
     */
    public final class RegisterModel
            extends JDataTableModel<Transaction, MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 7997087757206121152L;

        /**
         * Constructor.
         */
        private RegisterModel() {
            /* call constructor */
            super(Register.this);
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                       ? 0
                                       : theColumns.getDeclaredCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theTransactions == null)
                                            ? 0
                                            : theTransactions.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public Transaction getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theTransactions.get(pRowIndex);
        }

        @Override
        public JDataField getFieldForCell(final Transaction pTrans,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Transaction pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Object getItemValue(final Transaction pTrans,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pTrans, pColIndex);
        }

        @Override
        public void setItemValue(final Transaction pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }
    }

    /**
     * Column Model class.
     */
    private final class RegisterColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -7502445487118370020L;

        /**
         * Date column id.
         */
        private static final int COLUMN_DATE = 0;

        /**
         * Category column id.
         */
        private static final int COLUMN_CATEGORY = 1;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 2;

        /**
         * Amount column id.
         */
        private static final int COLUMN_AMOUNT = 3;

        /**
         * Debit column id.
         */
        private static final int COLUMN_DEBIT = 4;

        /**
         * Credit column id.
         */
        private static final int COLUMN_CREDIT = 5;

        /**
         * Reconciled column id.
         */
        private static final int COLUMN_RECONCILED = 6;

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
        private final IconButtonCellRenderer<Boolean> theIconRenderer;

        /**
         * Date editor.
         */
        private final CalendarCellEditor theDateEditor;

        /**
         * Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theIconEditor;

        /**
         * String editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Constructor.
         */
        private RegisterColumnModel() {
            /* call constructor */
            super(Register.this);

            /* Create the relevant formatters/editors */
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theIconEditor);

            /* Configure the iconButton */
            MoneyWiseIcons.buildReconciledButton(theIconEditor.getComplexState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_AMOUNT, WIDTH_MONEY, theDecimalRenderer));
            declareColumn(new JDataTableColumn(COLUMN_DEBIT, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_CREDIT, WIDTH_NAME, theStringRenderer));
            declareColumn(new JDataTableColumn(COLUMN_RECONCILED, WIDTH_ICON, theIconRenderer, theIconEditor));
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
                    return TITLE_CATEGORY;
                case COLUMN_AMOUNT:
                    return TITLE_AMOUNT;
                case COLUMN_CREDIT:
                    return TITLE_CREDIT;
                case COLUMN_DEBIT:
                    return TITLE_DEBIT;
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
                case COLUMN_AMOUNT:
                    return pTrans.getAmount();
                case COLUMN_DESC:
                    return pTrans.getComments();
                case COLUMN_RECONCILED:
                    return pTrans.isReconciled();
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
                case COLUMN_RECONCILED:
                    pItem.setReconciled((Boolean) pValue);
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
                case COLUMN_DATE:
                case COLUMN_RECONCILED:
                    return !pItem.isLocked();
                case COLUMN_DESC:
                    return true;
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
                case COLUMN_AMOUNT:
                    return Transaction.FIELD_AMOUNT;
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
}
