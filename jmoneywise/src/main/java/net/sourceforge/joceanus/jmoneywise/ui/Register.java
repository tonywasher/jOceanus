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
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
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
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableMouse;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Register.class.getName());

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
     * PopUp viewAccount.
     */
    private static final String POPUP_VIEW = NLS_BUNDLE.getString("PopUpViewAccount");

    /**
     * PopUp maintAccount.
     */
    private static final String POPUP_MAINT = NLS_BUNDLE.getString("PopUpMaintAccount");

    /**
     * PopUp nullDebitUnits.
     */
    protected static final String POPUP_NULLDEBUNITS = NLS_BUNDLE.getString("PopUpNullDebitUnits");

    /**
     * PopUp nullCreditUnits.
     */
    protected static final String POPUP_NULLCREDUNITS = NLS_BUNDLE.getString("PopUpNullCreditUnits");

    /**
     * PopUp nullTaxCredit.
     */
    protected static final String POPUP_NULLTAX = NLS_BUNDLE.getString("PopUpNullTax");

    /**
     * PopUp nullYears.
     */
    protected static final String POPUP_NULLYEARS = NLS_BUNDLE.getString("PopUpNullYears");

    /**
     * PopUp nullDilution.
     */
    protected static final String POPUP_NULLDILUTE = NLS_BUNDLE.getString("PopUpNullDilute");

    /**
     * PopUp calcTax.
     */
    protected static final String POPUP_CALCTAX = NLS_BUNDLE.getString("PopUpCalcTax");

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
     * Save Buttons.
     */
    private final SaveButtons theSaveButtons;

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
     * Panel width.
     */
    private static final int PANEL_WIDTH = 980;

    /**
     * Panel height.
     */
    private static final int PANEL_HEIGHT = 200;

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
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView);
        theEventEntry = theUpdateSet.registerClass(Transaction.class);
        theInfoEntry = theUpdateSet.registerClass(TransactionInfo.class);
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
        setPreferredScrollableViewportSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        /* Add the mouse listener */
        RegisterMouse myMouse = new RegisterMouse();
        addMouseListener(myMouse);

        /* Create the sub panels */
        theSelect = new JDateDayRangeSelect(false);
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataRegister);

        /* Create listener */
        RegisterListener myListener = new RegisterListener();
        theSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, myListener);
        theError.addChangeListener(myListener);
        theSaveButtons.addActionListener(myListener);
        theUpdateSet.addActionListener(myListener);
        theView.addChangeListener(myListener);

        /* Create the panel */
        thePanel = new JEnablePanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theError);
        thePanel.add(theSelect);
        thePanel.add(getScrollPane());
        thePanel.add(theSaveButtons);
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
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (theTransactions != null) {
            theTransactions.findEditState();
        }

        /* Update the table buttons */
        theSaveButtons.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

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
        theSaveButtons.setEnabled(true);
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

                /* Lock Save Buttons */
                theSaveButtons.setEnabled(!isError);

                /* If this is the data view */
            } else if (theView.equals(o)) {
                /* Refresh Data */
                refreshData();
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

            /* If this event relates to the save buttons */
            if (theSaveButtons.equals(o)) {
                /* Cancel any editing */
                cancelEditing();

                /* Perform the command */
                theUpdateSet.processCommand(e.getActionCommand(), theError);

                /* Notify listeners of changes */
                notifyChanges();

                /* If we are performing a rewind */
            } else if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
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
                                       : theColumns.getColumnCount();
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
        public boolean isCellEditable(final Transaction pTrans,
                                      final int pColIndex) {
            return false;
        }

        @Override
        public Object getItemValue(final Transaction pTrans,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pTrans, pColIndex);
        }
    }

    /**
     * Register mouse listener.
     */
    private final class RegisterMouse
            extends JDataTableMouse<Transaction, MoneyWiseDataType> {
        /**
         * Constructor.
         */
        private RegisterMouse() {
            /* Call super-constructor */
            super(Register.this);
        }

        /**
         * Add Null commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addNullCommands(final JPopupMenu pMenu) {
            JMenuItem myItem;
            boolean enableNullDebUnits = false;
            boolean enableNullCredUnits = false;
            boolean enableNullTax = false;
            boolean enableNullYears = false;
            boolean enableNullDilution = false;
            Register mySelf = Register.this;

            /* Nothing to do if the table is locked */
            if (mySelf.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem<?> myRow : mySelf.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as transaction */
                Transaction myTrans = (Transaction) myRow;

                /* Enable null Units if we have units */
                if (myTrans.getDebitUnits() != null) {
                    enableNullDebUnits = true;
                }

                /* Enable null CreditUnits if we have units */
                if (myTrans.getCreditUnits() != null) {
                    enableNullCredUnits = true;
                }

                /* Enable null Tax if we have tax */
                if (myTrans.getTaxCredit() != null) {
                    enableNullTax = true;
                }

                /* Enable null Years if we have years */
                if (myTrans.getYears() != null) {
                    enableNullYears = true;
                }

                /* Enable null Dilution if we have dilution */
                if (myTrans.getDilution() != null) {
                    enableNullDilution = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            boolean nullItem = enableNullDebUnits || enableNullCredUnits || enableNullTax;
            nullItem = nullItem || enableNullYears || enableNullDilution;
            if ((nullItem) && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set null debit units */
            if (enableNullDebUnits) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLDEBUNITS);
                myItem.setActionCommand(POPUP_NULLDEBUNITS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null credit units */
            if (enableNullCredUnits) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLCREDUNITS);
                myItem.setActionCommand(POPUP_NULLCREDUNITS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null tax */
            if (enableNullTax) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLTAX);
                myItem.setActionCommand(POPUP_NULLTAX);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null years */
            if (enableNullYears) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLYEARS);
                myItem.setActionCommand(POPUP_NULLYEARS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null dilution */
            if (enableNullDilution) {
                /* Add the null choice */
                myItem = new JMenuItem(POPUP_NULLDILUTE);
                myItem.setActionCommand(POPUP_NULLDILUTE);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Add Special commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addSpecialCommands(final JPopupMenu pMenu) {
            boolean enableCalcTax = false;
            Register mySelf = Register.this;

            /* Nothing to do if the table is locked */
            if (mySelf.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem<?> myRow : mySelf.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as event */
                Transaction myTrans = (Transaction) myRow;
                JMoney myTax = myTrans.getTaxCredit();
                TransactionCategory myCat = myTrans.getCategory();

                /* If we have a calculable tax credit that is null/zero */
                boolean isTaxable = (myCat != null) && (myTrans.isInterest() || myTrans.isDividend());
                if ((isTaxable) && ((myTax == null) || (!myTax.isNonZero()))) {
                    enableCalcTax = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableCalcTax) && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can calculate tax */
            if (enableCalcTax) {
                /* Add the undo change choice */
                JMenuItem myItem = new JMenuItem(POPUP_CALCTAX);
                myItem.setActionCommand(POPUP_CALCTAX);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Add Navigation commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addNavigationCommands(final JPopupMenu pMenu) {
            /* Nothing to do if the table is locked */
            if (Register.this.isLocked()) {
                return;
            }

            /* Access the popUp row/column and ignore if not valid */
            int myRow = getPopupRow();
            int myCol = getPopupCol();
            if (myRow < 0) {
                return;
            }

            /* Access the transaction */
            Transaction myTrans = theModel.getItemAtIndex(myRow);

            /* If the column is Credit */
            AssetBase<?> myAccount;
            if (myCol == COLUMN_CREDIT) {
                myAccount = myTrans.getCredit();
            } else if (myCol == COLUMN_DEBIT) {
                myAccount = myTrans.getDebit();
            } else {
                myAccount = null;
            }

            /* If we have an account we can navigate */
            boolean enableNavigate = myAccount != null;

            /* If there is something to add and there are already items in the menu */
            if ((enableNavigate) && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can navigate */
            if (enableNavigate) {
                /* Create the View account choice */
                JMenuItem myItem = new JMenuItem(POPUP_VIEW + ": " + myAccount.getName());

                /* Set the command and add to menu */
                myItem.setActionCommand(POPUP_VIEW + ":" + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);

                /* Create the Maintain account choice */
                myItem = new JMenuItem(POPUP_MAINT + ": " + myAccount.getName());

                /* Set the command and add to menu */
                myItem.setActionCommand(POPUP_MAINT + ":" + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            String myCmd = evt.getActionCommand();

            /* Cancel any editing */
            Register.this.cancelEditing();

            /* If this is a calculate Tax Credits command */
            if (myCmd.equals(POPUP_CALCTAX)) {
                /* Calculate the tax credits */
                calculateTaxCredits();

                /* If this is a navigate command */
            } else if ((myCmd.startsWith(POPUP_VIEW)) || (myCmd.startsWith(POPUP_MAINT))) {
                /* perform the navigation */
                performNavigation(myCmd);

                /* else we do not recognise the action */
            } else {
                /* Pass it to the superclass */
                super.actionPerformed(evt);
                return;
            }

            /* Notify of any changes */
            theModel.fireTableDataChanged();
            notifyChanges();
        }

        /**
         * Calculate tax credits.
         */
        private void calculateTaxCredits() {
            Register mySelf = Register.this;

            /* Loop through the selected rows */
            for (DataItem<?> myRow : mySelf.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access the event */
                Transaction myTrans = (Transaction) myRow;
                TransactionCategory myCat = myTrans.getCategory();
                JMoney myTax = myTrans.getTaxCredit();

                /* Ignore rows with invalid category type */
                if ((myCat == null) || ((!myTrans.isInterest()) && (!myTrans.isDividend()))) {
                    continue;
                }

                /* Ignore rows with tax credit already set */
                if ((myTax != null) && (myTax.isNonZero())) {
                    continue;
                }

                /* Calculate the tax credit */
                myTax = myTrans.calculateTaxCredit();
            }

            /* Increment version */
            theUpdateSet.incrementVersion();
        }

        /**
         * Perform a navigation command.
         * @param pCmd the navigation command
         */
        private void performNavigation(final String pCmd) {
            /* Access the action command */
            // String[] tokens = pCmd.split(":");
            // String myCmd = tokens[0];
            // String myName = null;
            // if (tokens.length > 1) {
            // myName = tokens[1];
            // }

            /* Access the correct account */
            // Account myAccount = theView.getData().getAccounts().findItemByName(myName);

            /* If this is an account view request */
            // if (myCmd.compareTo(POPUP_VIEW) == 0) {
            /* Switch view */
            // fireActionEvent(MainTab.ACTION_VIEWACCOUNT, new ActionRequest(myAccount, theSelect));

            /* If this is an account maintenance request */
            // } else if (myCmd.compareTo(POPUP_MAINT) == 0) {
            /* Switch view */
            // fireActionEvent(MainTab.ACTION_MAINTACCOUNT, new ActionRequest(myAccount));
            // }
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
         * Constructor.
         */
        private RegisterColumnModel() {
            /* call constructor */
            super(Register.this);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer));
            addColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_AMOUNT, WIDTH_MONEY, theDecimalRenderer));
            addColumn(new JDataTableColumn(COLUMN_DEBIT, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_CREDIT, WIDTH_NAME, theStringRenderer));
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
                case COLUMN_AMOUNT:
                    return Transaction.FIELD_AMOUNT;
                case COLUMN_CATEGORY:
                    return Transaction.FIELD_CATEGORY;
                case COLUMN_CREDIT:
                    return Transaction.FIELD_CREDIT;
                case COLUMN_DEBIT:
                    return Transaction.FIELD_DEBIT;
                default:
                    return null;
            }
        }
    }
}
