/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataManager.JDataEntry;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.ui.ErrorPanel;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTable;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTableColumn;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTableModel;
import net.sourceforge.jOceanus.jDataModels.ui.JDataTableMouse;
import net.sourceforge.jOceanus.jDataModels.views.UpdateEntry;
import net.sourceforge.jOceanus.jDataModels.views.UpdateSet;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDateDay.JDateDayRangeSelect;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.ComboBoxCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.DilutionCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.IntegerCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.MoneyCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.StringCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.UnitsCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.IntegerCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldManager;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType;
import net.sourceforge.jOceanus.jMoneyWise.ui.MainTab.ActionRequest;
import net.sourceforge.jOceanus.jMoneyWise.ui.controls.ComboSelect;
import net.sourceforge.jOceanus.jMoneyWise.ui.controls.StatementSelect;
import net.sourceforge.jOceanus.jMoneyWise.ui.controls.StatementSelect.StatementType;
import net.sourceforge.jOceanus.jMoneyWise.views.Statement;
import net.sourceforge.jOceanus.jMoneyWise.views.Statement.StatementLine;
import net.sourceforge.jOceanus.jMoneyWise.views.Statement.StatementLines;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * Account Statement Table.
 * @author Tony Washer
 */
public class AccountStatement
        extends JDataTable<StatementLine> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -9123840084764342499L;

    /**
     * Date view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * Table Model.
     */
    private final StatementModel theModel;

    /**
     * Account.
     */
    private transient Account theAccount = null;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * Lines Update Entry.
     */
    private final transient UpdateEntry<StatementLine> theLinesEntry;

    /**
     * EventInfo Update Entry.
     */
    private final transient UpdateEntry<EventInfo> theInfoEntry;

    /**
     * Statement.
     */
    private transient Statement theStatement = null;

    /**
     * Statement Lines.
     */
    private transient StatementLines theLines = null;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * Self Reference.
     */
    private final AccountStatement theTable = this;

    /**
     * Column Model.
     */
    private final StatementColumnModel theColumns;

    /**
     * Selected range.
     */
    private transient JDateDayRange theRange = null;

    /**
     * Range selection panel.
     */
    private JDateDayRangeSelect theSelect = null;

    /**
     * Statement type selection panel.
     */
    private final StatementSelect theStateBox;

    /**
     * Error Panel.
     */
    private final ErrorPanel theError;

    /**
     * ComboSelect.
     */
    private final transient ComboSelect theComboList;

    /**
     * Statement Type.
     */
    private StatementType theStateType = null;

    @Override
    public boolean hasHeader() {
        return true;
    }

    @Override
    protected void setError(final JDataException pError) {
        theError.setError(pError);
    }

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountStatement.class.getName());

    /**
     * Date column header.
     */
    private static final String TITLE_DATE = Extract.TITLE_DATE;

    /**
     * Description column header.
     */
    private static final String TITLE_DESC = Extract.TITLE_DESC;

    /**
     * Transaction Type column header.
     */
    private static final String TITLE_TRANS = Extract.TITLE_TRANS;

    /**
     * Partner column header.
     */
    private static final String TITLE_PARTNER = AccountPatterns.TITLE_PARTNER;

    /**
     * Credit column header.
     */
    private static final String TITLE_CREDIT = Extract.TITLE_CREDIT;

    /**
     * Debit column header.
     */
    private static final String TITLE_DEBIT = Extract.TITLE_DEBIT;

    /**
     * Balance column header.
     */
    private static final String TITLE_BALANCE = NLS_BUNDLE.getString("TitleBalance");

    /**
     * Dilution column header.
     */
    private static final String TITLE_DILUTION = Extract.TITLE_DILUTE;

    /**
     * TaxCredit column header.
     */
    private static final String TITLE_TAXCREDIT = Extract.TITLE_TAXCRED;

    /**
     * Years column header.
     */
    private static final String TITLE_YEARS = Extract.TITLE_YEARS;

    /**
     * Pop-up View Extract.
     */
    private static final String POPUP_EXTRACT = NLS_BUNDLE.getString("PopUpExtract");

    /**
     * Pop-up Maintain account.
     */
    private static final String POPUP_MAINT = NLS_BUNDLE.getString("PopUpMaint");

    /**
     * Pop-up View Parent.
     */
    private static final String POPUP_PARENT = NLS_BUNDLE.getString("PopUpParent");

    /**
     * Pop-up Maintain parent.
     */
    private static final String POPUP_MAINT_PARENT = NLS_BUNDLE.getString("PopUpMaintParent");

    /**
     * Pop-up View Partner.
     */
    private static final String POPUP_PARTNER = NLS_BUNDLE.getString("PopUpPartner");

    /**
     * Pop-up maintain partner.
     */
    private static final String POPUP_MAINT_PARTNER = NLS_BUNDLE.getString("PopUpMaintPartner");

    /**
     * Pop-up Set null units.
     */
    private static final String POPUP_NULLUNITS = Extract.POPUP_NULLDEBUNITS;

    /**
     * Pop-up Set null Tax Credit.
     */
    private static final String POPUP_NULLTAX = Extract.POPUP_NULLTAX;

    /**
     * Pop-up Set null Years.
     */
    private static final String POPUP_NULLYEARS = Extract.POPUP_NULLYEARS;

    /**
     * Pop-up Set null dilution.
     */
    private static final String POPUP_NULLDILUTE = Extract.POPUP_NULLDILUTE;

    /**
     * Pop-up Add Pattern.
     */
    private static final String POPUP_PATTERN = NLS_BUNDLE.getString("PopUpPattern");

    /**
     * Pop-up Calculate Tax.
     */
    private static final String POPUP_CALCTAX = Extract.POPUP_CALCTAX;

    /**
     * Pop-up Set Credit.
     */
    private static final String POPUP_CREDIT = AccountPatterns.POPUP_CREDIT;

    /**
     * Pop-up Set Debit.
     */
    private static final String POPUP_DEBIT = AccountPatterns.POPUP_DEBIT;

    /**
     * Date column id.
     */
    private static final int COLUMN_DATE = 0;

    /**
     * Date column id.
     */
    private static final int COLUMN_TRANTYP = 1;

    /**
     * Description column id.
     */
    private static final int COLUMN_DESC = 2;

    /**
     * Partner column id.
     */
    private static final int COLUMN_PARTNER = 3;

    /**
     * Credit column id.
     */
    private static final int COLUMN_CREDIT = 4;

    /**
     * Debit column id.
     */
    private static final int COLUMN_DEBIT = 5;

    /**
     * Balance column id.
     */
    private static final int COLUMN_BALANCE = 6;

    /**
     * Dilution column id.
     */
    private static final int COLUMN_DILUTION = 7;

    /**
     * TaxCredit column id.
     */
    private static final int COLUMN_TAXCREDIT = 8;

    /**
     * Years column id.
     */
    private static final int COLUMN_YEARS = 9;

    /**
     * Date column width.
     */
    private static final int WIDTH_DATE = 80;

    /**
     * Date column width.
     */
    private static final int WIDTH_TRANTYP = 110;

    /**
     * Description column width.
     */
    private static final int WIDTH_DESC = 150;

    /**
     * Partner column width.
     */
    private static final int WIDTH_PARTNER = 130;

    /**
     * Credit column width.
     */
    private static final int WIDTH_CREDIT = 90;

    /**
     * Debit column width.
     */
    private static final int WIDTH_DEBIT = 90;

    /**
     * Balance column width.
     */
    private static final int WIDTH_BALANCE = 90;

    /**
     * Dilution column width.
     */
    private static final int WIDTH_DILUTION = 80;

    /**
     * TaxCredit column width.
     */
    private static final int WIDTH_TAXCREDIT = 90;

    /**
     * Years column width.
     */
    private static final int WIDTH_YEARS = 50;

    /**
     * Constructor for Statement Window.
     * @param pView the view
     * @param pUpdateSet the update set
     * @param pCombo the combo manager
     * @param pError the error panel
     */
    public AccountStatement(final View pView,
                            final UpdateSet pUpdateSet,
                            final ComboSelect pCombo,
                            final ErrorPanel pError) {
        /* Store passed details */
        theView = pView;
        theComboList = pCombo;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);
        theError = pError;
        theUpdateSet = pUpdateSet;
        theLinesEntry = theUpdateSet.registerClass(StatementLine.class);
        theInfoEntry = theUpdateSet.registerClass(EventInfo.class);
        setUpdateSet(theUpdateSet);

        /* Create the model and declare it to our superclass */
        theModel = new StatementModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StatementColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns */
        getTableHeader().setReorderingAllowed(false);

        /* Add the mouse listener */
        StatementMouse myMouse = new StatementMouse();
        addMouseListener(myMouse);

        /* Create the sub panels */
        theSelect = new JDateDayRangeSelect();
        theStateBox = new StatementSelect();

        /* Create listeners */
        StatementListener myListener = new StatementListener();
        theSelect.addPropertyChangeListener(JDateDayRangeSelect.PROPERTY_RANGE, myListener);
        theStateBox.addChangeListener(myListener);
        theUpdateSet.addActionListener(myListener);

        /* Create a small panel for selection */
        JPanel myTop = new JPanel();
        myTop.setLayout(new BoxLayout(myTop, BoxLayout.X_AXIS));
        myTop.add(theSelect);
        myTop.add(theStateBox);

        /* Create the panel */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(myTop);
        thePanel.add(getScrollPane());
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final JDataEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theLinesEntry.getName());
    }

    @Override
    public void updateAfterChange() {
        /* Reset the balance */
        if (theStatement != null) {
            /* Protect against exceptions */
            try {
                /* Reset the balances and update the column */
                theStatement.resetBalances();
                theModel.fireUpdateColEvent(COLUMN_BALANCE);

                /* Catch Exceptions */
            } catch (JDataException e) {
                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to calculate table", e);

                /* Show the error */
                theError.setError(myError);
            }
        }
    }

    /**
     * The listener class.
     */
    private final class StatementListener
            implements PropertyChangeListener, ChangeListener, ActionListener {

        @Override
        public void stateChanged(final ChangeEvent evt) {
            /* if this is a change from the statement type */
            if (theStateBox.equals(evt.getSource())) {
                /* Reset the account */
                try {
                    setSelection(theAccount);
                } catch (JDataException e) {
                    e = null;
                }
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
                } catch (JDataException e) {
                    /* Build the error */
                    JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to change selection", e);

                    /* Show the error */
                    theError.setError(myError);

                    /* Restore SavePoint */
                    theSelect.restoreSavePoint();
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireTableDataChanged();
                notifyChanges();
            }
        }
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    protected void refreshData() {
        /* Update the possible date range */
        JDateDayRange myRange = theView.getRange();
        theSelect.setOverallRange(myRange);

        /* Create SavePoint */
        theSelect.createSavePoint();
    }

    /**
     * Set Selection to the specified account.
     * @param pAccount the Account for the extract
     * @throws JDataException on error
     */
    public void setSelection(final Account pAccount) throws JDataException {
        theStatement = null;
        theLines = null;
        EventInfoList myInfo = null;
        theRange = theSelect.getRange();
        theColumns.setDateEditorRange(theRange);
        theAccount = pAccount;
        theStateBox.setSelection(pAccount);
        theStateType = theStateBox.getStatementType();
        if (theAccount != null) {
            theStatement = new Statement(theView, pAccount, theRange);
            theLines = theStatement.getLines();
            myInfo = theLines.getEventInfo();
            theStatement.setFilter(theModel.getFilter());
        }
        theColumns.setColumns();
        super.setList(theLines);
        theLinesEntry.setDataList(theLines);
        theInfoEntry.setDataList(myInfo);
        theSelect.setEnabled(!hasUpdates());
        theStateBox.setEnabled(!hasUpdates());
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Update the date range and the state box */
        theSelect.setEnabled(!hasUpdates());
        theStateBox.setEnabled(!hasUpdates());

        /* Find the edit state */
        if (theLines != null) {
            theLines.findEditState();
        }

        /* update after changes */
        updateAfterChange();

        /* Notify listeners */
        fireStateChanged();
    }

    /**
     * Set Selection to the specified date range.
     * @param pRange the Date range for the extract
     * @throws JDataException on error
     */
    public void setSelection(final JDateDayRange pRange) throws JDataException {
        theStatement = null;
        theLines = null;
        EventInfoList myInfo = null;
        if (theAccount != null) {
            theStatement = new Statement(theView, theAccount, pRange);
            theLines = theStatement.getLines();
            myInfo = theLines.getEventInfo();
        }
        theRange = pRange;
        theColumns.setDateEditorRange(theRange);
        theStateBox.setSelection(theAccount);
        theStateType = theStateBox.getStatementType();
        theColumns.setColumns();
        super.setList(theLines);
        theLinesEntry.setDataList(theLines);
        theInfoEntry.setDataList(myInfo);
        theSelect.setEnabled(!hasUpdates());
        theStateBox.setEnabled(!hasUpdates());
    }

    /**
     * Set selection to the period designated by the referenced control.
     * @param pSource the source control
     */
    public void selectPeriod(final JDateDayRangeSelect pSource) {
        /* Adjust the period selection */
        theSelect.setSelection(pSource);
    }

    /**
     * Obtain the correct ComboBox for the given row/column.
     * @param row the row
     * @param column the column
     * @return the comboBox
     */
    @Override
    public JComboBox<?> getComboBox(final int row,
                                    final int column) {
        /* Access the line */
        StatementLine myLine = theLines.get(row);

        /* Switch on column */
        switch (column) {
            case COLUMN_TRANTYP:
                return (myLine.isCredit()) ? theComboList.getCreditTranTypes(theStatement.getAccount()) : theComboList.getDebitTranTypes(theStatement
                        .getAccount());
            case COLUMN_PARTNER:
                return (myLine.isCredit()) ? theComboList.getDebitAccounts(myLine.getTransType(), theStatement.getAccount()) : theComboList.getCreditAccounts(
                        myLine.getTransType(), theStatement.getAccount());
            default:
                return null;
        }
    }

    /**
     * Statement table model.
     */
    public final class StatementModel
            extends JDataTableModel<StatementLine> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 269477444398236458L;

        /**
         * Constructor.
         */
        private StatementModel() {
            /* call constructor */
            super(theTable);
        }

        @Override
        public StatementLine getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theLines.get(pRowIndex);
        }

        /**
         * Get the number of display columns.
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null) ? 0 : theColumns.getColumnCount();
        }

        /**
         * Get the number of rows in the current table.
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theLines == null) ? 0 : theLines.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DATE:
                    return TITLE_DATE;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_TRANTYP:
                    return TITLE_TRANS;
                case COLUMN_PARTNER:
                    return TITLE_PARTNER;
                case COLUMN_CREDIT:
                    return TITLE_CREDIT;
                case COLUMN_DEBIT:
                    return TITLE_DEBIT;
                case COLUMN_BALANCE:
                    return TITLE_BALANCE;
                case COLUMN_DILUTION:
                    return TITLE_DILUTION;
                case COLUMN_TAXCREDIT:
                    return TITLE_TAXCREDIT;
                case COLUMN_YEARS:
                    return TITLE_YEARS;
                default:
                    return null;
            }
        }

        @Override
        public Class<?> getColumnClass(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DESC:
                    return String.class;
                case COLUMN_TRANTYP:
                    return String.class;
                case COLUMN_PARTNER:
                    return String.class;
                default:
                    return Object.class;
            }
        }

        @Override
        public JDataField getFieldForCell(final StatementLine pLine,
                                          final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return Event.FIELD_DATE;
                case COLUMN_DESC:
                    return Event.FIELD_DESC;
                case COLUMN_TRANTYP:
                    return Event.FIELD_TRNTYP;
                case COLUMN_PARTNER:
                    return StatementLine.FIELD_PARTNER;
                case COLUMN_DILUTION:
                    return Event.FIELD_DILUTION;
                case COLUMN_TAXCREDIT:
                    return Event.FIELD_TAXCREDIT;
                case COLUMN_YEARS:
                    return Event.FIELD_YEARS;
                case COLUMN_CREDIT:
                    if ((pLine == null)
                        || (pLine.isCredit())) {
                        return ((theStateType == StatementType.Units) ? Event.FIELD_CREDUNITS : Event.FIELD_AMOUNT);
                    } else {
                        return null;
                    }
                case COLUMN_DEBIT:
                    if ((pLine == null)
                        || (!pLine.isCredit())) {
                        return ((theStateType == StatementType.Units) ? Event.FIELD_DEBTUNITS : Event.FIELD_AMOUNT);
                    } else {
                        return null;
                    }
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(final StatementLine pLine,
                                      final int pColIndex) {
            /* Locked if the account is closed */
            if (theStatement.getAccount().isClosed()) {
                return false;
            }

            /* Cannot edit if row is header, deleted or locked */
            if (pLine.isHeader()
                || pLine.isDeleted()
                || pLine.isLocked()) {
                return false;
            }

            /* switch on column */
            switch (pColIndex) {
                case COLUMN_BALANCE:
                    return false;
                case COLUMN_DATE:
                    return true;
                case COLUMN_TRANTYP:
                    return (pLine.getDate() != null);
                case COLUMN_DESC:
                    return ((pLine.getDate() != null) && (pLine.getTransType() != null));
                default:
                    if ((pLine.getDate() == null)
                        || (pLine.getDesc() == null)
                        || (pLine.getTransType() == null)) {
                        return false;
                    }

                    /* Access the transaction type */
                    TransactionType myType = pLine.getTransType();

                    /* Handle columns */
                    switch (pColIndex) {
                        case COLUMN_CREDIT:
                            return pLine.isCredit();
                        case COLUMN_DEBIT:
                            return !pLine.isCredit();
                        case COLUMN_YEARS:
                            return myType.isTaxableGain();
                        case COLUMN_TAXCREDIT:
                            return myType.needsTaxCredit();
                        case COLUMN_DILUTION:
                            return myType.isDilutable();
                        default:
                            return true;
                    }
            }

        }

        @Override
        public Object getItemValue(final StatementLine pLine,
                                   final int pColIndex) {
            /* Access the line */
            boolean isUnits = (theStateType == StatementType.Units);

            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pLine.getDate();
                case COLUMN_TRANTYP:
                    return pLine.getTransType();
                case COLUMN_PARTNER:
                    return pLine.getPartner();
                case COLUMN_BALANCE:
                    StatementLine myNext = theTable.getNextItem(pLine);
                    if ((myNext != null)
                        && (!pLine.isHeader())
                        && (Difference.isEqual(myNext.getDate(), pLine.getDate()))) {
                        return null;
                    } else {
                        return (isUnits) ? pLine.getBalanceUnits() : pLine.getBalance();
                    }
                case COLUMN_CREDIT:
                    if (pLine.isCredit()) {
                        return (isUnits) ? pLine.getCreditUnits() : pLine.getAmount();
                    }
                    return null;
                case COLUMN_DEBIT:
                    if (!pLine.isCredit()) {

                        return (isUnits) ? pLine.getDebitUnits() : pLine.getAmount();
                    }
                    return null;
                case COLUMN_DESC:
                    return pLine.getDesc();
                case COLUMN_DILUTION:
                    return pLine.getDilution();
                case COLUMN_TAXCREDIT:
                    return pLine.getTaxCredit();
                case COLUMN_YEARS:
                    return pLine.getYears();
                default:
                    return null;
            }
        }

        @Override
        public void setItemValue(final StatementLine pLine,
                                 final int pColIndex,
                                 final Object pValue) throws JDataException {
            /* Determine whether the line needs a tax credit */
            boolean needsTaxCredit = Event.needsTaxCredit(pLine.getTransType(), pLine.isCredit() ? pLine.getPartner() : pLine.getAccount());

            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pLine.setDate((JDateDay) pValue);
                    break;
                case COLUMN_DESC:
                    pLine.setDescription((String) pValue);
                    break;
                case COLUMN_TRANTYP:
                    pLine.setTransType((TransactionType) pValue);

                    /* If the need for a tax credit has changed */
                    if (needsTaxCredit != Event.needsTaxCredit(pLine.getTransType(), pLine.isCredit() ? pLine.getPartner() : pLine.getAccount())) {
                        /* Determine new Tax Credit */
                        if (needsTaxCredit) {
                            pLine.setTaxCredit(null);
                        } else {
                            pLine.setTaxCredit(pLine.calculateTaxCredit());
                        }
                    }
                    break;
                case COLUMN_CREDIT:
                case COLUMN_DEBIT:
                    if (theStateType == StatementType.Units) {
                        if (pLine.isCredit()) {
                            pLine.setCreditUnits((JUnits) pValue);
                        } else {
                            pLine.setDebitUnits((JUnits) pValue);
                        }
                    } else {
                        pLine.setAmount((JMoney) pValue);
                        if (needsTaxCredit) {
                            pLine.setTaxCredit(pLine.calculateTaxCredit());
                        }
                    }
                    break;
                case COLUMN_PARTNER:
                    pLine.setPartner((Account) pValue);
                    break;
                case COLUMN_DILUTION:
                    pLine.setDilution((JDilution) pValue);
                    break;
                case COLUMN_TAXCREDIT:
                    pLine.setTaxCredit((JMoney) pValue);
                    break;
                case COLUMN_YEARS:
                    pLine.setYears((Integer) pValue);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Statement mouse listener.
     */
    private final class StatementMouse
            extends JDataTableMouse<StatementLine> {
        /**
         * Constructor.
         */
        private StatementMouse() {
            /* Call super-constructor */
            super(theTable);
        }

        /**
         * Add Null commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addNullCommands(final JPopupMenu pMenu) {
            JMenuItem myItem;
            boolean enableNullUnits = false;
            boolean enableNullTax = false;
            boolean enableNullYears = false;
            boolean enableNullDilution = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as line */
                StatementLine myLine = (StatementLine) myRow;

                /* Enable null Units if we have units and are showing units */
                if ((theStateType == StatementType.Units)
                    && (myLine.getCreditUnits() != null)) {
                    enableNullUnits = true;
                }

                /* Enable null Tax if we have tax and are showing tax */
                if ((theColumns.isColumnVisible(COLUMN_TAXCREDIT))
                    && (myLine.getTaxCredit() != null)) {
                    enableNullTax = true;
                }

                /* Enable null Years if we have years and are showing years */
                if ((theColumns.isColumnVisible(COLUMN_YEARS))
                    && (myLine.getYears() != null)) {
                    enableNullYears = true;
                }

                /* Enable null Dilution if we have dilution and are showing dilution */
                if ((theColumns.isColumnVisible(COLUMN_DILUTION))
                    && (myLine.getDilution() != null)) {
                    enableNullDilution = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            boolean nullItem = enableNullUnits
                               || enableNullTax;
            nullItem = nullItem
                       || enableNullYears
                       || enableNullDilution;
            if ((nullItem)
                && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set null units */
            if (enableNullUnits) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLUNITS);
                myItem.setActionCommand(POPUP_NULLUNITS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null tax */
            if (enableNullTax) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLTAX);
                myItem.setActionCommand(POPUP_NULLTAX);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null years */
            if (enableNullYears) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLYEARS);
                myItem.setActionCommand(POPUP_NULLYEARS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null dilution */
            if (enableNullDilution) {
                /* Add the undo change choice */
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
            JMenuItem myItem;
            StatementLine myLine;
            JMoney myTax;
            TransactionType myTrans;
            boolean enableCalcTax = false;
            boolean enablePattern = false;
            boolean enableCredit = false;
            boolean enableDebit = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Enable add pattern */
                enablePattern = true;

                /* Access as line */
                myLine = (StatementLine) myRow;
                myTax = myLine.getTaxCredit();
                myTrans = myLine.getTransType();

                /* Enable Debit if we have credit */
                if (myLine.isCredit()) {
                    enableDebit = true;

                    /* Enable Credit otherwise */
                } else {
                    enableCredit = true;
                }

                /* If we have a calculable tax credit that is null/zero */
                boolean isTaxable = ((myTrans != null) && ((myTrans.isInterest()) || (myTrans.isDividend())));
                if ((isTaxable)
                    && ((myTax == null) || (!myTax.isNonZero()))) {
                    enableCalcTax = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            boolean addSeparator = enableCalcTax
                                   || enablePattern;
            addSeparator = addSeparator
                           || enableCredit
                           || enableDebit;
            if ((addSeparator)
                && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set credit */
            if (enableCredit) {
                /* Add the credit choice */
                myItem = new JMenuItem(POPUP_CREDIT);
                myItem.setActionCommand(POPUP_CREDIT);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set debit */
            if (enableDebit) {
                /* Add the debit choice */
                myItem = new JMenuItem(POPUP_DEBIT);
                myItem.setActionCommand(POPUP_DEBIT);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can calculate tax */
            if (enableCalcTax) {
                /* Add the calculate tax choice */
                myItem = new JMenuItem(POPUP_CALCTAX);
                myItem.setActionCommand(POPUP_CALCTAX);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can add a pattern */
            if (enablePattern) {
                /* Add the add pattern choice */
                myItem = new JMenuItem(POPUP_PATTERN);
                myItem.setActionCommand(POPUP_PATTERN);
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
            JMenuItem myItem;
            StatementLine myLine;
            Account myAccount = null;
            Account myParent;
            int myRow;
            boolean enablePartner = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Access the popUp row */
            myRow = getPopupRow();

            /* If it is valid */
            if ((!isHeader())
                && (myRow >= 0)) {
                /* Access the line and partner */
                myLine = theModel.getItemAtIndex(myRow);
                myAccount = myLine.getPartner();

                /* If we have a different account then we can navigate */
                if ((!Difference.isEqual(myAccount, theAccount))
                    && (myAccount != null)) {
                    enablePartner = true;
                }
            }

            /* If there are already items in the menu */
            if (pMenu.getComponentCount() > 0) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* Create the View extract choice */
            myItem = new JMenuItem(POPUP_EXTRACT);
            myItem.setActionCommand(POPUP_EXTRACT);
            myItem.addActionListener(this);
            pMenu.add(myItem);

            /* Create the Maintain account choice */
            myItem = new JMenuItem(POPUP_MAINT);
            myItem.setActionCommand(POPUP_MAINT);
            myItem.addActionListener(this);
            pMenu.add(myItem);

            /* If we are a child */
            if (theAccount.isChild()) {
                /* Access parent account */
                myParent = theAccount.getParent();

                /* Create the View account choice */
                myItem = new JMenuItem(POPUP_PARENT
                                       + ": "
                                       + myParent.getName());
                myItem.setActionCommand(POPUP_PARENT);
                myItem.addActionListener(this);
                pMenu.add(myItem);

                /* Create the Maintain account choice */
                myItem = new JMenuItem(POPUP_MAINT_PARENT
                                       + ": "
                                       + myParent.getName());
                myItem.setActionCommand(POPUP_MAINT_PARENT);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can navigate to partner */
            if (enablePartner) {
                /* Create the View account choice */
                myItem = new JMenuItem(POPUP_PARTNER
                                       + ": "
                                       + myAccount.getName());
                myItem.setActionCommand(POPUP_PARTNER
                                        + ":"
                                        + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);

                /* Create the Maintain account choice */
                myItem = new JMenuItem(POPUP_MAINT_PARTNER
                                       + ": "
                                       + myAccount.getName());
                myItem.setActionCommand(POPUP_MAINT_PARTNER
                                        + ":"
                                        + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Set the specified column to credit/debit.
         * @param isCredit set to Credit or else Debit
         */
        protected void setIsCredit(final boolean isCredit) {
            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Cast to Statement Line */
                StatementLine myLine = (StatementLine) myRow;

                /* Ignore rows that are already correct */
                if (myLine.isCredit() == isCredit) {
                    continue;
                }

                /* set the credit value */
                myLine.pushHistory();
                myLine.setIsCredit(isCredit);
            }

            /* Increment version */
            theUpdateSet.incrementVersion();
        }

        /**
         * Perform actions for controls/pop-ups on this table.
         * @param evt the event
         */
        @Override
        public void actionPerformed(final ActionEvent evt) {
            String myCmd = evt.getActionCommand();

            /* Cancel any editing */
            theTable.cancelEditing();

            /* Determine whether this is a navigate command */
            boolean isNavigate = myCmd.equals(POPUP_EXTRACT)
                                 || myCmd.equals(POPUP_MAINT);
            isNavigate = isNavigate
                         || myCmd.equals(POPUP_PARENT)
                         || myCmd.equals(POPUP_MAINT_PARENT);
            isNavigate = isNavigate
                         || myCmd.startsWith(POPUP_PARTNER)
                         || myCmd.startsWith(POPUP_MAINT_PARTNER);

            /* If this is a set null units command */
            if (myCmd.equals(POPUP_NULLUNITS)) {
                /* Set Units column to null */
                setColumnToNull(COLUMN_CREDIT);

                /* else if this is a set null tax command */
            } else if (myCmd.equals(POPUP_NULLTAX)) {
                /* Set Tax column to null */
                setColumnToNull(COLUMN_TAXCREDIT);

                /* If this is a set null years command */
            } else if (myCmd.equals(POPUP_NULLYEARS)) {
                /* Set Years column to null */
                setColumnToNull(COLUMN_YEARS);

                /* If this is a set null dilute command */
            } else if (myCmd.equals(POPUP_NULLDILUTE)) {
                /* Set Dilution column to null */
                setColumnToNull(COLUMN_DILUTION);

                /* If this is a calculate Tax Credits command */
            } else if (myCmd.equals(POPUP_CALCTAX)) {
                /* Calculate the tax credits */
                calculateTaxCredits();

                /* If this is an add Pattern command */
            } else if (myCmd.equals(POPUP_PATTERN)) {
                /* Add the patterns */
                addPatterns();

                /* If this is a navigate command */
            } else if (isNavigate) {
                /* perform the navigation */
                performNavigation(myCmd);

                /* If this is a credit command */
            } else if (myCmd.equals(POPUP_CREDIT)) {
                /* Set Credit indication */
                setIsCredit(true);

                /* If this is a debit command */
            } else if (myCmd.equals(POPUP_DEBIT)) {
                /* Set Debit indication */
                setIsCredit(false);

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
            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Determine row */
                int row = myRow.indexOf();
                if (theTable.hasHeader()) {
                    row++;
                }

                /* Access the line */
                StatementLine myLine = (StatementLine) myRow;
                TransactionType myTrans = myLine.getTransType();
                JMoney myTax = myLine.getTaxCredit();

                /* Ignore rows with invalid transaction type */
                if ((myTrans == null)
                    || ((!myTrans.isInterest()) && (!myTrans.isDividend()))) {
                    continue;
                }

                /* Ignore rows with tax credit already set */
                if ((myTax != null)
                    && (myTax.isNonZero())) {
                    continue;
                }

                /* Calculate the tax credit */
                myTax = myLine.calculateTaxCredit();

                /* set the tax credit value */
                theModel.setValueAt(myTax, row, COLUMN_TAXCREDIT);
            }

            /* Increment version */
            theUpdateSet.incrementVersion();
        }

        /**
         * Add patterns.
         */
        private void addPatterns() {
            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Access the line */
                StatementLine myLine = (StatementLine) myRow;

                /* Request the action */
                fireActionEvent(MainTab.ACTION_ADDPATTERN, new ActionRequest(theAccount, myLine));
            }
        }

        /**
         * Perform a navigation command.
         * @param pCmd the navigation command
         */
        private void performNavigation(final String pCmd) {
            String[] tokens;
            String myName = null;
            Account myAccount = null;

            /* Access the action command */
            tokens = pCmd.split(":");
            String myCmd = tokens[0];
            if (tokens.length > 1) {
                myName = tokens[1];
            }

            /* Access the correct account */
            if (myName != null) {
                myAccount = theView.getData().getAccounts().findItemByName(myName);
            }

            /* Handle commands */
            if (myCmd.equals(POPUP_EXTRACT)) {
                fireActionEvent(MainTab.ACTION_VIEWEXTRACT, new ActionRequest(theSelect));
            } else if (myCmd.equals(POPUP_MAINT)) {
                fireActionEvent(MainTab.ACTION_MAINTACCOUNT, new ActionRequest(theAccount));
            } else if (myCmd.equals(POPUP_PARENT)) {
                fireActionEvent(MainTab.ACTION_VIEWACCOUNT, new ActionRequest(theAccount.getParent()));
            } else if (myCmd.equals(POPUP_MAINT_PARENT)) {
                fireActionEvent(MainTab.ACTION_MAINTACCOUNT, new ActionRequest(theAccount.getParent()));
            } else if (myCmd.equals(POPUP_PARTNER)) {
                fireActionEvent(MainTab.ACTION_VIEWACCOUNT, new ActionRequest(myAccount));
            } else if (myCmd.equals(POPUP_MAINT_PARTNER)) {
                fireActionEvent(MainTab.ACTION_MAINTACCOUNT, new ActionRequest(myAccount));
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class StatementColumnModel
            extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -183944035127105952L;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * Date Editor.
         */
        private final CalendarCellEditor theDateEditor;

        /**
         * Decimal Renderer.
         */
        private final DecimalCellRenderer theDecimalRenderer;

        /**
         * Money Editor.
         */
        private final MoneyCellEditor theMoneyEditor;

        /**
         * Units Editor.
         */
        private final UnitsCellEditor theUnitsEditor;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Dilution Editor.
         */
        private final DilutionCellEditor theDilutionEditor;

        /**
         * Integer Renderer.
         */
        private final IntegerCellRenderer theIntegerRenderer;

        /**
         * Integer Editor.
         */
        private final IntegerCellEditor theIntegerEditor;

        /**
         * ComboBox Editor.
         */
        private final ComboBoxCellEditor theComboEditor;

        /**
         * Credit Column.
         */
        private final JDataTableColumn theCreditCol;

        /**
         * Debit Column.
         */
        private final JDataTableColumn theDebitCol;

        /**
         * Balance Column.
         */
        private final JDataTableColumn theBalanceCol;

        /**
         * Dilution Column.
         */
        private final JDataTableColumn theDiluteCol;

        /**
         * Tax Credit column.
         */
        private final JDataTableColumn theTaxCredCol;

        /**
         * Years Column.
         */
        private final JDataTableColumn theYearsCol;

        /**
         * Constructor.
         */
        private StatementColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theMoneyEditor = theFieldMgr.allocateMoneyCellEditor();
            theUnitsEditor = theFieldMgr.allocateUnitsCellEditor();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theDilutionEditor = theFieldMgr.allocateDilutionCellEditor();
            theIntegerRenderer = theFieldMgr.allocateIntegerCellRenderer();
            theIntegerEditor = theFieldMgr.allocateIntegerCellEditor();
            theComboEditor = theFieldMgr.allocateComboBoxCellEditor();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            addColumn(new JDataTableColumn(COLUMN_TRANTYP, WIDTH_TRANTYP, theStringRenderer, theComboEditor));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new JDataTableColumn(COLUMN_PARTNER, WIDTH_PARTNER, theStringRenderer, theComboEditor));
            theCreditCol = new JDataTableColumn(COLUMN_CREDIT, WIDTH_CREDIT, theDecimalRenderer, theMoneyEditor);
            theDebitCol = new JDataTableColumn(COLUMN_DEBIT, WIDTH_DEBIT, theDecimalRenderer, theMoneyEditor);
            theBalanceCol = new JDataTableColumn(COLUMN_BALANCE, WIDTH_BALANCE, theDecimalRenderer, theMoneyEditor);
            theDiluteCol = new JDataTableColumn(COLUMN_DILUTION, WIDTH_DILUTION, theDecimalRenderer, theDilutionEditor);
            theTaxCredCol = new JDataTableColumn(COLUMN_TAXCREDIT, WIDTH_TAXCREDIT, theDecimalRenderer, theMoneyEditor);
            theYearsCol = new JDataTableColumn(COLUMN_YEARS, WIDTH_YEARS, theIntegerRenderer, theIntegerEditor);
            addColumn(theCreditCol);
            addColumn(theDebitCol);
            addColumn(theBalanceCol);
            addColumn(theDiluteCol);
            addColumn(theTaxCredCol);
            addColumn(theYearsCol);
        }

        /**
         * Set the date editor range.
         * @param pRange the range
         */
        private void setDateEditorRange(final JDateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }

        /**
         * Determine whether a column is visible.
         * @param pCol the column id
         * @return true/false
         */
        private boolean isColumnVisible(final int pCol) {
            switch (pCol) {
                case COLUMN_TAXCREDIT:
                    return theTaxCredCol.isMember();
                case COLUMN_YEARS:
                    return theYearsCol.isMember();
                case COLUMN_DILUTION:
                    return theDiluteCol.isMember();
                default:
                    return true;
            }
        }

        /**
         * Set visible columns according to the statement type.
         */
        private void setColumns() {
            AccountType myType;

            /* Hide optional columns */
            if (theBalanceCol.isMember()) {
                removeColumn(theBalanceCol);
            }
            if (theDiluteCol.isMember()) {
                removeColumn(theDiluteCol);
            }
            if (theTaxCredCol.isMember()) {
                removeColumn(theTaxCredCol);
            }
            if (theYearsCol.isMember()) {
                removeColumn(theYearsCol);
            }

            /* Switch on statement type */
            switch (theStateType) {
                case Extract:
                    /* Access account type */
                    myType = theAccount.getActType();

                    /* Show Dilution column for shares */
                    if (myType.isShares()) {
                        addColumn(theDiluteCol);
                    }

                    /* Show the TaxCredit column as required */
                    if (myType.isMoney()
                        && !myType.isTaxFree()) {
                        addColumn(theTaxCredCol);
                    }

                    /* Show the years column for LifeBonds */
                    if (myType.isLifeBond()) {
                        addColumn(theYearsCol);
                    }

                    /* Set money Renderers */
                    theCreditCol.setCellRenderer(theDecimalRenderer);
                    theDebitCol.setCellRenderer(theDecimalRenderer);

                    /* Set money Editors */
                    theCreditCol.setCellEditor(theMoneyEditor);
                    theDebitCol.setCellEditor(theMoneyEditor);
                    break;
                case Value:
                    /* Access account type */
                    myType = theAccount.getActType();

                    /* Show Balance column */
                    addColumn(theBalanceCol);

                    /* Show the TaxCredit column as required */
                    if (myType.isMoney()
                        && !myType.isTaxFree()) {
                        addColumn(theTaxCredCol);
                    }

                    /* Show the years column for LifeBonds */
                    if (myType.isLifeBond()) {
                        addColumn(theYearsCol);
                    }

                    /* Set money Renderers */
                    theCreditCol.setCellRenderer(theDecimalRenderer);
                    theDebitCol.setCellRenderer(theDecimalRenderer);
                    theBalanceCol.setCellRenderer(theDecimalRenderer);

                    /* Set money Editors */
                    theCreditCol.setCellEditor(theMoneyEditor);
                    theDebitCol.setCellEditor(theMoneyEditor);
                    break;
                case Units:
                    /* Show Balance and dilution columns */
                    addColumn(theBalanceCol);
                    addColumn(theDiluteCol);

                    /* Set units Renderers */
                    theCreditCol.setCellRenderer(theDecimalRenderer);
                    theDebitCol.setCellRenderer(theDecimalRenderer);
                    theBalanceCol.setCellRenderer(theDecimalRenderer);

                    /* Set units Editors */
                    theCreditCol.setCellEditor(theUnitsEditor);
                    theDebitCol.setCellEditor(theUnitsEditor);
                    break;
                default:
                    break;
            }
        }
    }
}
