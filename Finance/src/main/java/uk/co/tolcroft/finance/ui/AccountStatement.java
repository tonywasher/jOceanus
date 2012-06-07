/*******************************************************************************
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
package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.LayoutStyle;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDateDay.DateDayRangeSelect;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Units;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.data.TransactionType.TransTypeList;
import uk.co.tolcroft.finance.ui.controls.ComboSelect;
import uk.co.tolcroft.finance.ui.controls.StatementSelect;
import uk.co.tolcroft.finance.ui.controls.StatementSelect.StatementType;
import uk.co.tolcroft.finance.views.Statement;
import uk.co.tolcroft.finance.views.Statement.StatementLine;
import uk.co.tolcroft.finance.views.Statement.StatementLines;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.CalendarEditor;
import uk.co.tolcroft.models.ui.Editor.ComboBoxEditor;
import uk.co.tolcroft.models.ui.Editor.DilutionEditor;
import uk.co.tolcroft.models.ui.Editor.IntegerEditor;
import uk.co.tolcroft.models.ui.Editor.MoneyEditor;
import uk.co.tolcroft.models.ui.Editor.StringEditor;
import uk.co.tolcroft.models.ui.Editor.UnitsEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.ui.Renderer.IntegerRenderer;
import uk.co.tolcroft.models.ui.Renderer.StringRenderer;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class AccountStatement extends DataTable<Event> {
    /* Members */
    private static final long serialVersionUID = -9123840084764342499L;

    private View theView = null;
    private StatementModel theModel = null;
    private Account theAccount = null;
    private ListClass theViewList = null;
    private Statement theStatement = null;
    private StatementLines theLines = null;
    private AccountList theAccounts = null;
    private TransTypeList theTransTypes = null;
    private JPanel thePanel = null;
    private AccountStatement theTable = this;
    private StatementMouse theMouse = null;
    private StatementColumnModel theColumns = null;
    private AccountTab theParent = null;
    private MainTab theTopWindow = null;
    private DateDayRange theRange = null;
    private DateDayRangeSelect theSelect = null;
    private StatementSelect theStateBox = null;
    private JDataEntry theDataEntry = null;
    private ErrorPanel theError = null;
    private ComboSelect theComboList = null;
    private StatementType theStateType = null;

    /* Access methods */
    @Override
    public boolean hasHeader() {
        return true;
    }

    public JPanel getPanel() {
        return thePanel;
    }

    /* Access the debug entry */
    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    /* Table headers */
    private static final String titleDate = "Date";
    private static final String titleDesc = "Description";
    private static final String titleTrans = "TransactionType";
    private static final String titlePartner = "Partner";
    private static final String titleCredit = "Credit";
    private static final String titleDebit = "Debit";
    private static final String titleBalance = "Balance";
    private static final String titleDilution = "Dilution";
    private static final String titleTaxCredit = "TaxCredit";
    private static final String titleYears = "Years";

    /* Table columns */
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_TRANTYP = 1;
    private static final int COLUMN_DESC = 2;
    private static final int COLUMN_PARTNER = 3;
    private static final int COLUMN_CREDIT = 4;
    private static final int COLUMN_DEBIT = 5;
    private static final int COLUMN_BALANCE = 6;
    private static final int COLUMN_DILUTION = 7;
    private static final int COLUMN_TAXCREDIT = 8;
    private static final int COLUMN_YEARS = 9;

    /**
     * Constructor for Statement Window
     * @param pParent the parent window
     */
    public AccountStatement(AccountTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataManager());

        /* Declare variables */
        GroupLayout myLayout;

        /* Store passed details */
        theParent = pParent;
        theView = pParent.getView();
        theViewList = pParent.getViewSet().registerClass(StatementLine.class);
        theTopWindow = pParent.getTopWindow();

        /* Create the model and declare it to our superclass */
        theModel = new StatementModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new StatementColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns */
        getTableHeader().setReorderingAllowed(false);

        /* Add the mouse listener */
        theMouse = new StatementMouse();
        addMouseListener(theMouse);

        /* Create the sub panels */
        theSelect = new DateDayRangeSelect();
        theStateBox = new StatementSelect(this);

        /* Create the debug entry, attach to AccountDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry("Statement");
        theDataEntry.addAsChildOf(pParent.getDataEntry());

        /* Create the error panel for this view */
        theError = new ErrorPanel(this);

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                         false)
                                                    .addComponent(theError, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGroup(myLayout.createSequentialGroup()
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(theSelect,
                                                                                                      GroupLayout.Alignment.LEADING,
                                                                                                      GroupLayout.DEFAULT_SIZE,
                                                                                                      GroupLayout.DEFAULT_SIZE,
                                                                                                      Short.MAX_VALUE))
                                                                      .addContainerGap()
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(theStateBox,
                                                                                                      GroupLayout.Alignment.LEADING,
                                                                                                      GroupLayout.DEFAULT_SIZE,
                                                                                                      GroupLayout.DEFAULT_SIZE,
                                                                                                      Short.MAX_VALUE)))
                                                    .addComponent(getScrollPane(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup()
                                  .addComponent(theError)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(theSelect).addComponent(theStateBox))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(getScrollPane()).addContainerGap()));
    }

    @Override
    public void validateAfterChange() {
        /* Reset the balance */
        if (theStatement != null) {
            /* Protect against exceptions */
            try {
                /* Reset the balances and update the column */
                theStatement.resetBalances();
                theModel.fireUpdateColEvent(COLUMN_BALANCE);
            }
            /* Catch Exceptions */
            catch (JDataException e) {
                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to calculate table",
                        e);

                /* Show the error */
                theError.setError(myError);
            }
        }
    }

    /**
     * Save changes from the view into the underlying data
     */
    @Override
    public void saveData() {
        /* Just update the debug, save has already been done */
        updateDebug();
    }

    /**
     * Update Debug view
     */
    @Override
    public void updateDebug() {
        theDataEntry.setObject(theStatement);
    }

    /**
     * Lock on error
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(boolean isError) {
        /* Hide selection panel */
        theSelect.setVisible(!isError);
        theStateBox.setVisible(!isError);

        /* Lock scroll-able area */
        getScrollPane().setEnabled(!isError);
    }

    /**
     * Notify table that there has been a change in selection by an underlying control
     * @param obj the underlying control that has changed selection
     */
    @Override
    public void notifySelection(Object obj) {
        /* if this is a change from the range */
        if (obj == (Object) theSelect) {
            /* Protect against exceptions */
            try {
                /* Set the new range */
                setSelection(theSelect.getRange());

                /* Create SavePoint */
                theSelect.createSavePoint();
            }

            /* Catch Exceptions */
            catch (JDataException e) {
                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to change selection", e);

                /* Show the error */
                theError.setError(myError);

                /* Restore SavePoint */
                theSelect.restoreSavePoint();
            }
        }

        /* else if this is a change from the type */
        else if (obj == (Object) theStateBox) {
            /* Reset the account */
            try {
                setSelection(theAccount);
            } catch (Throwable e) {
            }
        }
    }

    /**
     * Refresh views/controls after a load/update of underlying data
     */
    public void refreshData() {
        FinanceData myData;

        /* Access the data */
        myData = theView.getData();

        /* Access TransTypes and Accounts */
        theTransTypes = myData.getTransTypes();
        theAccounts = myData.getAccounts();

        /* Access the combo list from parent */
        theComboList = theParent.getComboList();

        /* Update the possible date range */
        DateDayRange myRange = theView.getRange();
        theSelect.setOverallRange(myRange);

        /* Create SavePoint */
        theSelect.createSavePoint();
    }

    /**
     * Set Selection to the specified account
     * @param pAccount the Account for the extract
     * @throws JDataException on error
     */
    public void setSelection(Account pAccount) throws JDataException {
        theStatement = null;
        theLines = null;
        theRange = theSelect.getRange();
        theColumns.setDateEditorRange(theRange);
        theAccount = pAccount;
        theStateBox.setSelection(pAccount);
        theStateType = theStateBox.getStatementType();
        if (theAccount != null) {
            theStatement = new Statement(theView, pAccount, theRange);
            theLines = theStatement.getLines();
        }
        theColumns.setColumns();
        super.setList(theLines);
        theViewList.setDataList(theLines);
        theSelect.setLockDown(false);
        theStateBox.setLockDown();
    }

    /**
     * Call underlying controls to take notice of changes in view/selection
     */
    @Override
    public void notifyChanges() {
        /* Update the date range and the state box */
        theSelect.setLockDown(true);
        theStateBox.setLockDown();

        /* Find the edit state */
        if (theLines != null)
            theLines.findEditState();

        /* Update the parent panel */
        theParent.notifyChanges();
    }

    /**
     * Set Selection to the specified date range
     * @param pRange the Date range for the extract
     * @throws JDataException
     */
    public void setSelection(DateDayRange pRange) throws JDataException {
        theStatement = null;
        theLines = null;
        if (theAccount != null) {
            theStatement = new Statement(theView, theAccount, pRange);
            theLines = theStatement.getLines();
        }
        theRange = pRange;
        theColumns.setDateEditorRange(theRange);
        theStateBox.setSelection(theAccount);
        theStateType = theStateBox.getStatementType();
        theColumns.setColumns();
        super.setList(theLines);
        theViewList.setDataList(theLines);
        theSelect.setLockDown(false);
        theStateBox.setLockDown();
    }

    /**
     * Set selection to the period designated by the referenced control
     * @param pSource the source control
     */
    public void selectPeriod(DateDayRangeSelect pSource) {
        /* Adjust the period selection */
        theSelect.setSelection(pSource);
    }

    /**
     * Obtain the correct ComboBox for the given row/column
     */
    @Override
    public JComboBox getComboBox(int row,
                                 int column) {
        StatementLine myLine;

        /* Access the line */
        myLine = (StatementLine) theLines.get(row - 1);

        /* Switch on column */
        switch (column) {
            case COLUMN_TRANTYP:
                return (myLine.isCredit())
                                          ? theComboList.getCreditTranTypes(theStatement.getActType())
                                          : theComboList.getDebitTranTypes(theStatement.getActType());
            case COLUMN_PARTNER:
                return (myLine.isCredit())
                                          ? theComboList.getDebitAccounts(myLine.getTransType(),
                                                                          theStatement.getAccount())
                                          : theComboList.getCreditAccounts(myLine.getTransType(),
                                                                           theStatement.getAccount());
            default:
                return null;
        }
    }

    /* Statement table model */
    public class StatementModel extends DataTableModel {
        private static final long serialVersionUID = 269477444398236458L;

        /**
         * Constructor
         */
        private StatementModel() {
            /* call constructor */
            super(theTable);
        }

        /**
         * Get the number of display columns
         * @return the columns
         */
        @Override
        public int getColumnCount() {
            return (theColumns == null) ? 0 : theColumns.getColumnCount();
        }

        /**
         * Get the number of rows in the current table
         * @return the number of rows
         */
        @Override
        public int getRowCount() {
            return (theLines == null) ? 0 : theLines.size() + 1;
        }

        /**
         * Get the name of the column
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(int col) {
            switch (col) {
                case COLUMN_DATE:
                    return titleDate;
                case COLUMN_DESC:
                    return titleDesc;
                case COLUMN_TRANTYP:
                    return titleTrans;
                case COLUMN_PARTNER:
                    return titlePartner;
                case COLUMN_CREDIT:
                    return titleCredit;
                case COLUMN_DEBIT:
                    return titleDebit;
                case COLUMN_BALANCE:
                    return titleBalance;
                case COLUMN_DILUTION:
                    return titleDilution;
                case COLUMN_TAXCREDIT:
                    return titleTaxCredit;
                case COLUMN_YEARS:
                    return titleYears;
                default:
                    return null;
            }
        }

        /**
         * Get the object class of the column
         * @param col the column
         * @return the class of the objects associated with the column
         */
        @Override
        public Class<?> getColumnClass(int col) {
            switch (col) {
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

        /**
         * Obtain the Field id associated with the column
         * @param row the row
         * @param column the column
         */
        @Override
        public JDataField getFieldForCell(int row,
                                          int column) {
            StatementLine myLine = null;
            if (row > 0)
                myLine = (StatementLine) theLines.get(row - 1);

            /* Switch on column */
            switch (column) {
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
                    if ((myLine == null) || (myLine.isCredit()))
                        return ((theStateType == StatementType.UNITS)
                                                                     ? Event.FIELD_UNITS
                                                                     : Event.FIELD_AMOUNT);
                    else
                        return null;
                case COLUMN_DEBIT:
                    if ((myLine == null) || (!myLine.isCredit()))
                        return ((theStateType == StatementType.UNITS)
                                                                     ? Event.FIELD_UNITS
                                                                     : Event.FIELD_AMOUNT);
                    else
                        return null;
                default:
                    return null;
            }
        }

        /**
         * Is the cell at (row, col) editable
         */
        @Override
        public boolean isCellEditable(int row,
                                      int col) {
            StatementLine myLine;

            /* Locked if the account is closed */
            if (theStatement.getAccount().isClosed())
                return false;

            /* Lock the start balance */
            if (row == 0)
                return false;

            /* Access the line */
            myLine = (StatementLine) theLines.get(row - 1);

            /* Cannot edit if row is deleted or locked */
            if (myLine.isDeleted() || myLine.isLocked())
                return false;

            /* switch on column */
            switch (col) {
                case COLUMN_BALANCE:
                    return false;
                case COLUMN_DATE:
                    return true;
                case COLUMN_TRANTYP:
                    return (myLine.getDate() != null);
                case COLUMN_DESC:
                    return ((myLine.getDate() != null) && (myLine.getTransType() != null));
                default:
                    if ((myLine.getDate() == null) || (myLine.getDesc() == null)
                            || (myLine.getTransType() == null))
                        return false;

                    /* Access the transaction type */
                    TransactionType myType = myLine.getTransType();

                    /* Handle columns */
                    switch (col) {
                        case COLUMN_CREDIT:
                            return myLine.isCredit();
                        case COLUMN_DEBIT:
                            return !myLine.isCredit();
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

        /**
         * Get the value at (row, col)
         * @return the object value
         */
        @Override
        public Object getValueAt(int row,
                                 int col) {
            StatementLine myLine;
            StatementLine myNext;
            Object o = null;
            boolean bShow = true;

            /* If this is the first row */
            if (row == 0) {
                switch (col) {
                    case COLUMN_DATE:
                        return theStatement.getDateRange().getStart();
                    case COLUMN_DESC:
                        return "Starting Balance";
                    case COLUMN_BALANCE:
                        return (theStateType == StatementType.UNITS)
                                                                    ? theStatement.getStartUnits()
                                                                    : theStatement.getStartBalance();
                    default:
                        return null;
                }
            }

            /* Access the line */
            myLine = (StatementLine) theLines.get(row - 1);
            myNext = (StatementLine) theLines.peekNext(myLine);

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_DATE:
                    o = myLine.getDate();
                    break;
                case COLUMN_TRANTYP:
                    o = (myLine.getTransType() == null) ? null : myLine.getTransType().getName();
                    break;
                case COLUMN_PARTNER:
                    o = (myLine.getPartner() == null) ? null : myLine.getPartner().getName();
                    break;
                case COLUMN_BALANCE:
                    if ((myNext != null) && (Difference.isEqual(myNext.getDate(), myLine.getDate())))
                        o = null;
                    else
                        o = (theStateType == StatementType.UNITS) ? myLine.getBalanceUnits() : myLine
                                .getBalance();
                    break;
                case COLUMN_CREDIT:
                    bShow = myLine.isCredit();
                    if (bShow)
                        o = ((theStateType == StatementType.UNITS) ? myLine.getUnits() : myLine.getAmount());
                    break;
                case COLUMN_DEBIT:
                    bShow = !myLine.isCredit();
                    if (bShow)
                        o = ((theStateType == StatementType.UNITS) ? myLine.getUnits() : myLine.getAmount());
                    break;
                case COLUMN_DESC:
                    o = myLine.getDesc();
                    if ((o != null) && (((String) o).length() == 0))
                        o = null;
                    break;
                case COLUMN_DILUTION:
                    o = myLine.getDilution();
                    break;
                case COLUMN_TAXCREDIT:
                    o = myLine.getTaxCredit();
                    break;
                case COLUMN_YEARS:
                    o = myLine.getYears();
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (bShow) && (myLine.hasErrors(getFieldForCell(row, col))))
                o = Renderer.getError();

            /* Return to caller */
            return o;
        }

        /**
         * Set the value at (row, col)
         * @param obj the object value to set
         */
        @Override
        public void setValueAt(Object obj,
                               int row,
                               int col) {
            StatementLine myLine;

            /* Access the line */
            myLine = (StatementLine) theLines.get(row - 1);

            /* Push history */
            myLine.pushHistory();

            /* Determine whether the line needs a tax credit */
            boolean needsTaxCredit = Event.needsTaxCredit(myLine.getTransType(),
                                                          myLine.isCredit() ? myLine.getPartner() : myLine
                                                                  .getAccount());

            /* Protect against exceptions */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_DATE:
                        myLine.setDate((DateDay) obj);
                        break;
                    case COLUMN_DESC:
                        myLine.setDescription((String) obj);
                        break;
                    case COLUMN_TRANTYP:
                        myLine.setTransType(theTransTypes.searchFor((String) obj));
                        /* If the need for a tax credit has changed */
                        if (needsTaxCredit != Event.needsTaxCredit(myLine.getTransType(),
                                                                   myLine.isCredit()
                                                                                    ? myLine.getPartner()
                                                                                    : myLine.getAccount())) {
                            /* Determine new Tax Credit */
                            if (needsTaxCredit)
                                myLine.setTaxCredit(null);
                            else
                                myLine.setTaxCredit(myLine.calculateTaxCredit());
                        }
                        break;
                    case COLUMN_CREDIT:
                    case COLUMN_DEBIT:
                        if (theStateType == StatementType.UNITS)
                            myLine.setUnits((Units) obj);
                        else {
                            myLine.setAmount((Money) obj);
                            if (needsTaxCredit)
                                myLine.setTaxCredit(myLine.calculateTaxCredit());
                        }
                        break;
                    case COLUMN_PARTNER:
                        myLine.setPartner(theAccounts.searchFor((String) obj));
                        break;
                    case COLUMN_DILUTION:
                        myLine.setDilution((Dilution) obj);
                        break;
                    case COLUMN_TAXCREDIT:
                        myLine.setTaxCredit((Money) obj);
                        break;
                    case COLUMN_YEARS:
                        myLine.setYears((Integer) obj);
                        break;
                }
            }

            /* Handle Exceptions */
            catch (Throwable e) {
                /* Reset values */
                myLine.popHistory();
                myLine.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to update field at (" + row + "," + col + ")", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (myLine.checkForHistory()) {
                /* Note that the item has changed */
                myLine.clearErrors();
                myLine.setState(DataState.CHANGED);

                /* Validate the item and update the edit state */
                myLine.validate();
                theLines.findEditState();

                /* Switch on the updated column */
                switch (col) {
                /* redraw whole table if we have updated a sort col */
                    case COLUMN_DATE:
                    case COLUMN_DESC:
                    case COLUMN_TRANTYP:
                        /* Re-Sort the row */
                        theLines.reSort(myLine);
                        validateAfterChange();

                        /* Determine new row # */
                        int myNewRowNo = myLine.indexOf();

                        /* If the row # has changed */
                        if (myNewRowNo != row) {
                            /* Report the move of the row */
                            fireMoveRowEvents(row, myNewRowNo);
                            selectRowWithScroll(myNewRowNo);
                            break;
                        }
                        fireTableRowsUpdated(row, row);
                        break;

                    /* Recalculate balance if required */
                    case COLUMN_CREDIT:
                    case COLUMN_DEBIT:
                        validateAfterChange();

                        /* fall through */

                        /* else note that we have updated this cell */
                    default:
                        fireTableRowsUpdated(row, row);
                        break;
                }

                /* Update components to reflect changes */
                notifyChanges();
                updateDebug();
            }
        }
    }

    /**
     * Statement mouse listener
     */
    private class StatementMouse extends DataMouse<Event> {

        /* Pop-up Menu items */
        private static final String popupExtract = "View Extract";
        private static final String popupMaint = "Maintain Account";
        private static final String popupParent = "View Parent";
        private static final String popupMParent = "Maintain Parent";
        private static final String popupPartner = "View Partner";
        private static final String popupMPartner = "Maintain Partner";
        private static final String popupSetNullUnits = "Set Null Units";
        private static final String popupSetNullTax = "Set Null TaxCredit";
        private static final String popupSetNullYears = "Set Null Years";
        private static final String popupSetNullDilute = "Set Null Dilution";
        private static final String popupPattern = "Add to Pattern";
        private static final String popupCalcTax = "Calculate Tax Credit";
        private static final String popupCredit = "Set As Credit";
        private static final String popupDebit = "Set As Debit";

        /**
         * Constructor
         */
        private StatementMouse() {
            /* Call super-constructor */
            super(theTable);
        }

        /**
         * Add Null commands to menu
         * @param pMenu the menu to add to
         */
        @Override
        protected void addNullCommands(JPopupMenu pMenu) {
            JMenuItem myItem;
            StatementLine myLine;
            boolean enableNullUnits = false;
            boolean enableNullTax = false;
            boolean enableNullYears = false;
            boolean enableNullDilution = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked())
                return;

            /* Loop through the selected rows */
            for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows */
                if ((myRow == null) || (myRow.isLocked()))
                    continue;

                /* Ignore deleted rows */
                if (myRow.isDeleted())
                    continue;

                /* Access as line */
                myLine = (StatementLine) myRow;

                /* Enable null Units if we have units and are showing units */
                if ((theStateType == StatementType.UNITS) && (myLine.getUnits() != null))
                    enableNullUnits = true;

                /* Enable null Tax if we have tax and are showing tax */
                if ((theColumns.isColumnVisible(COLUMN_TAXCREDIT)) && (myLine.getTaxCredit() != null))
                    enableNullTax = true;

                /* Enable null Years if we have years and are showing years */
                if ((theColumns.isColumnVisible(COLUMN_YEARS)) && (myLine.getYears() != null))
                    enableNullYears = true;

                /* Enable null Dilution if we have dilution and are showing dilution */
                if ((theColumns.isColumnVisible(COLUMN_DILUTION)) && (myLine.getDilution() != null))
                    enableNullDilution = true;
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableNullUnits || enableNullTax || enableNullYears || enableNullDilution)
                    && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set null units */
            if (enableNullUnits) {
                /* Add the undo change choice */
                myItem = new JMenuItem(popupSetNullUnits);
                myItem.setActionCommand(popupSetNullUnits);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null tax */
            if (enableNullTax) {
                /* Add the undo change choice */
                myItem = new JMenuItem(popupSetNullTax);
                myItem.setActionCommand(popupSetNullTax);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null years */
            if (enableNullYears) {
                /* Add the undo change choice */
                myItem = new JMenuItem(popupSetNullYears);
                myItem.setActionCommand(popupSetNullYears);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null dilution */
            if (enableNullDilution) {
                /* Add the undo change choice */
                myItem = new JMenuItem(popupSetNullDilute);
                myItem.setActionCommand(popupSetNullDilute);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Add Special commands to menu
         * @param pMenu the menu to add to
         */
        @Override
        protected void addSpecialCommands(JPopupMenu pMenu) {
            JMenuItem myItem;
            StatementLine myLine;
            Money myTax;
            TransactionType myTrans;
            boolean enableCalcTax = false;
            boolean enablePattern = false;
            boolean enableCredit = false;
            boolean enableDebit = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked())
                return;

            /* Loop through the selected rows */
            for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows */
                if ((myRow == null) || (myRow.isLocked()))
                    continue;

                /* Ignore deleted rows */
                if (myRow.isDeleted())
                    continue;

                /* Enable add pattern */
                enablePattern = true;

                /* Access as line */
                myLine = (StatementLine) myRow;
                myTax = myLine.getTaxCredit();
                myTrans = myLine.getTransType();

                /* Enable Debit if we have credit */
                if (myLine.isCredit())
                    enableDebit = true;

                /* Enable Credit otherwise */
                else
                    enableCredit = true;

                /* If we have a calculable tax credit */
                if ((myTrans != null) && ((myTrans.isInterest()) || (myTrans.isDividend()))) {
                    /* Enable calculation of tax if tax is null or zero */
                    if ((myTax == null) || (!myTax.isNonZero()))
                        enableCalcTax = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableCalcTax || enablePattern || enableCredit || enableDebit)
                    && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set credit */
            if (enableCredit) {
                /* Add the credit choice */
                myItem = new JMenuItem(popupCredit);
                myItem.setActionCommand(popupCredit);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set debit */
            if (enableDebit) {
                /* Add the debit choice */
                myItem = new JMenuItem(popupDebit);
                myItem.setActionCommand(popupDebit);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can calculate tax */
            if (enableCalcTax) {
                /* Add the calculate tax choice */
                myItem = new JMenuItem(popupCalcTax);
                myItem.setActionCommand(popupCalcTax);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can add a pattern */
            if (enablePattern) {
                /* Add the add pattern choice */
                myItem = new JMenuItem(popupPattern);
                myItem.setActionCommand(popupPattern);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Add Navigation commands to menu
         * @param pMenu the menu to add to
         */
        @Override
        protected void addNavigationCommands(JPopupMenu pMenu) {
            JMenuItem myItem;
            StatementLine myLine;
            Account myAccount = null;
            Account myParent;
            int myRow;
            boolean enablePartner = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked())
                return;

            /* Access the popUp row */
            myRow = getPopupRow();

            /* If it is valid */
            if ((!isHeader()) && (myRow >= 0)) {
                /* Access the line and partner */
                myLine = (StatementLine) theTable.extractItemAt(myRow);
                myAccount = myLine.getPartner();

                /* If we have a different account then we can navigate */
                if ((!Difference.isEqual(myAccount, theAccount)) && (myAccount != null))
                    enablePartner = true;
            }

            /* If there is something to add and there are already items in the menu */
            if (pMenu.getComponentCount() > 0) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* Create the View extract choice */
            myItem = new JMenuItem(popupExtract);
            myItem.setActionCommand(popupExtract);
            myItem.addActionListener(this);
            pMenu.add(myItem);

            /* Create the Maintain account choice */
            myItem = new JMenuItem(popupMaint);
            myItem.setActionCommand(popupMaint);
            myItem.addActionListener(this);
            pMenu.add(myItem);

            /* If we are a child */
            if (theAccount.isChild()) {
                /* Access parent account */
                myParent = theAccount.getParent();

                /* Create the View account choice */
                myItem = new JMenuItem(popupParent + ": " + myParent.getName());
                myItem.setActionCommand(popupParent);
                myItem.addActionListener(this);
                pMenu.add(myItem);

                /* Create the Maintain account choice */
                myItem = new JMenuItem(popupMParent + ": " + myParent.getName());
                myItem.setActionCommand(popupMParent);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can navigate to partner */
            if (enablePartner) {
                /* Create the View account choice */
                myItem = new JMenuItem(popupPartner + ": " + myAccount.getName());
                myItem.setActionCommand(popupPartner + ":" + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);

                /* Create the Maintain account choice */
                myItem = new JMenuItem(popupMPartner + ": " + myAccount.getName());
                myItem.setActionCommand(popupMPartner + ":" + myAccount.getName());
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
        }

        /**
         * Set the specified column to credit/debit
         * @param isCredit set to Credit or else Debit
         */
        protected void setIsCredit(boolean isCredit) {
            AbstractTableModel myModel;
            StatementLine myLine;
            int row;

            /* Access the table model */
            myModel = theTable.getTableModel();

            /* Loop through the selected rows */
            for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows */
                if ((myRow == null) || (myRow.isLocked()))
                    continue;

                /* Ignore deleted rows */
                if (myRow.isDeleted())
                    continue;

                /* Determine row */
                row = myRow.indexOf() + 1;

                /* Cast to Statement Line */
                myLine = (StatementLine) myRow;

                /* Ignore rows that are already correct */
                if (myLine.isCredit() == isCredit)
                    continue;

                /* set the credit value */
                myLine.pushHistory();
                myLine.setIsCredit(isCredit);
                if (myLine.checkForHistory()) {
                    /* Note that the item has changed */
                    myLine.clearErrors();
                    myLine.setState(DataState.CHANGED);

                    /* Validate the item */
                    myLine.validate();

                    /* Notify that the row has changed */
                    myModel.fireTableRowsUpdated(row, row);
                }
            }

            /* Determine the edit state */
            theLines.findEditState();

            /* Recalculate the table */
            theTable.validateAfterChange();
        }

        /**
         * Perform actions for controls/pop-ups on this table
         * @param evt the event
         */
        @Override
        public void actionPerformed(ActionEvent evt) {
            String myCmd = evt.getActionCommand();

            /* Cancel any editing */
            theTable.cancelEditing();

            /* If this is a set null units command */
            if (myCmd.equals(popupSetNullUnits)) {
                /* Set Units column to null */
                setColumnToNull(COLUMN_DEBIT);
                setColumnToNull(COLUMN_CREDIT);
            }

            /* else if this is a set null tax command */
            else if (myCmd.equals(popupSetNullTax)) {
                /* Set Tax column to null */
                setColumnToNull(COLUMN_TAXCREDIT);
            }

            /* If this is a set null years command */
            else if (myCmd.equals(popupSetNullYears)) {
                /* Set Years column to null */
                setColumnToNull(COLUMN_YEARS);
            }

            /* If this is a set null dilute command */
            else if (myCmd.equals(popupSetNullDilute)) {
                /* Set Dilution column to null */
                setColumnToNull(COLUMN_DILUTION);
            }

            /* If this is a calculate Tax Credits command */
            else if (myCmd.equals(popupCalcTax)) {
                /* Calculate the tax credits */
                calculateTaxCredits();
            }

            /* If this is an add Pattern command */
            else if (myCmd.equals(popupPattern)) {
                /* Add the patterns */
                addPatterns();
            }

            /* If this is a navigate command */
            else if ((myCmd.equals(popupExtract)) || (myCmd.equals(popupMaint))
                    || (myCmd.equals(popupParent)) || (myCmd.equals(popupMParent))
                    || (myCmd.startsWith(popupPartner)) || (myCmd.startsWith(popupMPartner))) {
                /* perform the navigation */
                performNavigation(myCmd);
            }

            /* If this is a credit command */
            else if (myCmd.equals(popupCredit)) {
                /* Set Credit indication */
                setIsCredit(true);
            }

            /* If this is a debit command */
            else if (myCmd.equals(popupDebit)) {
                /* Set Debit indication */
                setIsCredit(false);
            }

            /* else we do not recognise the action */
            else {
                /* Pass it to the superclass */
                super.actionPerformed(evt);
                return;
            }

            /* Notify of any changes */
            notifyChanges();
            updateDebug();
        }

        /**
         * Calculate tax credits
         */
        private void calculateTaxCredits() {
            StatementLine myLine;
            TransactionType myTrans;
            Money myTax;
            int row;

            /* Loop through the selected rows */
            for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows */
                if ((myRow == null) || (myRow.isLocked()))
                    continue;

                /* Ignore deleted rows */
                if (myRow.isDeleted())
                    continue;

                /* Determine row */
                row = myRow.indexOf();
                if (theTable.hasHeader())
                    row++;

                /* Access the line */
                myLine = (StatementLine) myRow;
                myTrans = myLine.getTransType();
                myTax = myLine.getTaxCredit();

                /* Ignore rows with invalid transaction type */
                if ((myTrans == null) || ((!myTrans.isInterest()) && (!myTrans.isDividend())))
                    continue;

                /* Ignore rows with tax credit already set */
                if ((myTax != null) && (myTax.isNonZero()))
                    continue;

                /* Calculate the tax credit */
                myTax = myLine.calculateTaxCredit();

                /* set the tax credit value */
                theModel.setValueAt(myTax, row, COLUMN_TAXCREDIT);
                theModel.fireTableCellUpdated(row, COLUMN_TAXCREDIT);
            }
        }

        /**
         * Add patterns
         */
        private void addPatterns() {
            StatementLine myLine;

            /* Loop through the selected rows */
            for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked rows */
                if ((myRow == null) || (myRow.isLocked()))
                    continue;

                /* Ignore deleted rows */
                if (myRow.isDeleted())
                    continue;

                /* Access the line */
                myLine = (StatementLine) myRow;

                /* Add the pattern */
                theParent.addPattern(myLine);
            }
        }

        /**
         * Perform a navigation command
         * @param pCmd the navigation command
         */
        private void performNavigation(String pCmd) {
            String tokens[];
            String myName = null;
            Account myAccount = null;

            /* Access the action command */
            tokens = pCmd.split(":");
            String myCmd = tokens[0];
            if (tokens.length > 1)
                myName = tokens[1];

            /* Access the correct account */
            if (myName != null)
                myAccount = theView.getData().getAccounts().searchFor(myName);

            /* Handle commands */
            if (myCmd.equals(popupExtract))
                theTopWindow.selectPeriod(theSelect);
            else if (myCmd.equals(popupMaint))
                theTopWindow.selectAccountMaint(theAccount);
            else if (myCmd.equals(popupParent))
                theTopWindow.selectAccount(theAccount.getParent(), theSelect);
            else if (myCmd.equals(popupMParent))
                theTopWindow.selectAccountMaint(theAccount.getParent());
            else if (myCmd.equals(popupPartner))
                theTopWindow.selectAccount(myAccount, theSelect);
            else if (myCmd.equals(popupMPartner))
                theTopWindow.selectAccountMaint(myAccount);
        }
    }

    /**
     * Column Model class
     */
    private class StatementColumnModel extends DataColumnModel {
        private static final long serialVersionUID = -183944035127105952L;

        /* Renderers/Editors */
        private final CalendarRenderer theDateRenderer;
        private final CalendarEditor theDateEditor;
        private final DecimalRenderer theDecimalRenderer;
        private final MoneyEditor theMoneyEditor;
        private final UnitsEditor theUnitsEditor;
        private final StringRenderer theStringRenderer;
        private final StringEditor theStringEditor;
        private final DilutionEditor theDilutionEditor;
        private final IntegerRenderer theIntegerRenderer;
        private final IntegerEditor theIntegerEditor;
        private final ComboBoxEditor theComboEditor;
        private DataColumn theCreditCol = null;
        private DataColumn theDebitCol = null;
        private DataColumn theBalanceCol = null;
        private DataColumn theDiluteCol = null;
        private DataColumn theTaxCredCol = null;
        private DataColumn theYearsCol = null;

        /**
         * Constructor
         */
        private StatementColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDateEditor = new CalendarEditor();
            theDecimalRenderer = new DecimalRenderer();
            theMoneyEditor = new MoneyEditor();
            theUnitsEditor = new UnitsEditor();
            theStringRenderer = new StringRenderer();
            theStringEditor = new StringEditor();
            theDilutionEditor = new DilutionEditor();
            theIntegerRenderer = new IntegerRenderer();
            theIntegerEditor = new IntegerEditor();
            theComboEditor = new ComboBoxEditor();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_DATE, 80, theDateRenderer, theDateEditor));
            addColumn(new DataColumn(COLUMN_TRANTYP, 110, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_DESC, 150, theStringRenderer, theStringEditor));
            addColumn(new DataColumn(COLUMN_PARTNER, 130, theStringRenderer, theComboEditor));
            addColumn(theCreditCol = new DataColumn(COLUMN_CREDIT, 90, theDecimalRenderer, theMoneyEditor));
            addColumn(theDebitCol = new DataColumn(COLUMN_DEBIT, 90, theDecimalRenderer, theMoneyEditor));
            addColumn(theBalanceCol = new DataColumn(COLUMN_BALANCE, 90, theDecimalRenderer, theMoneyEditor));
            addColumn(theDiluteCol = new DataColumn(COLUMN_DILUTION, 80, theDecimalRenderer,
                    theDilutionEditor));
            addColumn(theTaxCredCol = new DataColumn(COLUMN_TAXCREDIT, 90, theDecimalRenderer, theMoneyEditor));
            addColumn(theYearsCol = new DataColumn(COLUMN_YEARS, 50, theIntegerRenderer, theIntegerEditor));
        }

        /**
         * Set the date editor range
         * @param pRange
         */
        private void setDateEditorRange(DateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }

        /**
         * Determine whether a column is visible
         * @param pCol the column id
         * @return true/false
         */
        private boolean isColumnVisible(int pCol) {
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
         * Set visible columns according to the statement type
         */
        private void setColumns() {
            AccountType myType;

            /* Hide optional columns */
            if (theBalanceCol.isMember())
                removeColumn(theBalanceCol);
            if (theDiluteCol.isMember())
                removeColumn(theDiluteCol);
            if (theTaxCredCol.isMember())
                removeColumn(theTaxCredCol);
            if (theYearsCol.isMember())
                removeColumn(theYearsCol);

            /* Switch on statement type */
            switch (theStateType) {
                case EXTRACT:
                    /* Access account type */
                    myType = theAccount.getActType();

                    /* Show Dilution column for shares */
                    if (myType.isShares())
                        addColumn(theDiluteCol);

                    /* Show the TaxCredit column as required */
                    if (myType.isMoney() && !myType.isTaxFree())
                        addColumn(theTaxCredCol);

                    /* Show the years column for LifeBonds */
                    if (myType.isLifeBond())
                        addColumn(theYearsCol);

                    /* Set money Renderers */
                    theCreditCol.setCellRenderer(theDecimalRenderer);
                    theDebitCol.setCellRenderer(theDecimalRenderer);

                    /* Set money Editors */
                    theCreditCol.setCellEditor(theMoneyEditor);
                    theDebitCol.setCellEditor(theMoneyEditor);
                    break;
                case VALUE:
                    /* Access account type */
                    myType = theAccount.getActType();

                    /* Show Balance column */
                    addColumn(theBalanceCol);

                    /* Show the TaxCredit column as required */
                    if (myType.isMoney() && !myType.isTaxFree())
                        addColumn(theTaxCredCol);

                    /* Show the years column for LifeBonds */
                    if (myType.isLifeBond())
                        addColumn(theYearsCol);

                    /* Set money Renderers */
                    theCreditCol.setCellRenderer(theDecimalRenderer);
                    theDebitCol.setCellRenderer(theDecimalRenderer);
                    theBalanceCol.setCellRenderer(theDecimalRenderer);

                    /* Set money Editors */
                    theCreditCol.setCellEditor(theMoneyEditor);
                    theDebitCol.setCellEditor(theMoneyEditor);
                    break;
                case UNITS:
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
            }
        }
    }
}
