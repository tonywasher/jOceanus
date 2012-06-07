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
import javax.swing.table.AbstractTableModel;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.Frequency;
import uk.co.tolcroft.finance.data.Frequency.FrequencyList;
import uk.co.tolcroft.finance.data.Pattern;
import uk.co.tolcroft.finance.data.Pattern.PatternList;
import uk.co.tolcroft.finance.data.TransactionType.TransTypeList;
import uk.co.tolcroft.finance.ui.controls.ComboSelect;
import uk.co.tolcroft.finance.views.Statement;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.CalendarEditor;
import uk.co.tolcroft.models.ui.Editor.ComboBoxEditor;
import uk.co.tolcroft.models.ui.Editor.MoneyEditor;
import uk.co.tolcroft.models.ui.Editor.StringEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.ui.Renderer.StringRenderer;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class AccountPatterns extends DataTable<Event> {
    /* Members */
    private static final long serialVersionUID = 1968946370981616222L;

    private View theView = null;
    private PatternsModel theModel = null;
    private PatternList thePatterns = null;
    private AccountList theAccounts = null;
    private FrequencyList theFreqs = null;
    private TransTypeList theTransTypes = null;
    private JPanel thePanel = null;
    private JComboBox theFreqBox = null;
    private AccountPatterns theTable = this;
    private PatternMouse theMouse = null;
    private PatternColumnModel theColumns = null;
    private AccountTab theParent = null;
    private Account theAccount = null;
    private ListClass theViewList = null;
    private ComboSelect theComboList = null;
    private JDataEntry theDataEntry = null;
    private ErrorPanel theError = null;
    private boolean freqsPopulated = false;

    /* Access methods */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

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
    private static final String titleFreq = "Frequency";

    /* Table columns */
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_TRANTYP = 1;
    private static final int COLUMN_DESC = 2;
    private static final int COLUMN_PARTNER = 3;
    private static final int COLUMN_CREDIT = 4;
    private static final int COLUMN_DEBIT = 5;
    private static final int COLUMN_FREQ = 6;

    /**
     * Constructor for Patterns Window
     * @param pParent the parent window
     */
    public AccountPatterns(AccountTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataManager());

        /* Declare variables */
        GroupLayout myLayout;

        /* Store details about the parent */
        theParent = pParent;
        theView = pParent.getView();
        theViewList = pParent.getViewSet().registerClass(Pattern.class);

        /* Create the model and declare it to our superclass */
        theModel = new PatternsModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new PatternColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns */
        getTableHeader().setReorderingAllowed(false);

        /* Build the combo box */
        theFreqBox = new JComboBox();

        /* Create the debug entry, attach to AccountDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry("Patterns");
        theDataEntry.addAsChildOf(pParent.getDataEntry());
        theDataEntry.hideEntry();

        /* Add the mouse listener */
        theMouse = new PatternMouse();
        addMouseListener(theMouse);

        /* Create the error panel for this view */
        theError = new ErrorPanel(this);

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                         false)
                                                    .addComponent(theError, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(getScrollPane(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theError)
                                  .addComponent(getScrollPane())));
    }

    /**
     * Refresh views/controls after a load/update of underlying data
     */
    public void refreshData() {
        FinanceData myData;
        Frequency myFreq;

        DataListIterator<Frequency> myIterator;

        /* Access the data */
        myData = theView.getData();

        /* Access Frequencies, TransTypes and Accounts */
        theFreqs = myData.getFrequencys();
        theTransTypes = myData.getTransTypes();
        theAccounts = myData.getAccounts();

        /* Access the combo list from parent */
        theComboList = theParent.getComboList();

        /* If we have frequencies already populated */
        if (freqsPopulated) {
            /* Remove the frequencies */
            theFreqBox.removeAllItems();
            freqsPopulated = false;
        }

        /* Access the frequency iterator */
        myIterator = theFreqs.listIterator();

        /* Add the Frequency values to the frequencies box */
        while ((myFreq = myIterator.next()) != null) {
            /* Ignore the frequency if it is not enabled */
            if (!myFreq.getEnabled())
                continue;

            /* Add the item to the list */
            theFreqBox.addItem(myFreq.getName());
            freqsPopulated = true;
        }
    }

    /**
     * Update Debug view
     */
    @Override
    public void updateDebug() {
        theDataEntry.setObject(thePatterns);
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
     * Lock on error
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(boolean isError) {
        /* Lock scroll-able area */
        getScrollPane().setEnabled(!isError);
    }

    /**
     * Call underlying controls to take notice of changes in view/selection
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (thePatterns != null)
            thePatterns.findEditState();

        /* Update the parent panel */
        theParent.notifyChanges();
    }

    /**
     * Set Selection to the specified account
     * @param pAccount the Account for the extract
     * @throws JDataException
     */
    public void setSelection(Account pAccount) throws JDataException {
        /* Record the account */
        theAccount = pAccount;
        thePatterns = null;

        /* If we have an account */
        if (theAccount != null) {
            /* Get the Patterns edit list */
            FinanceData myData = theView.getData();
            PatternList myPatterns = myData.getPatterns();
            thePatterns = myPatterns.getEditList(pAccount);
        }

        /* Declare the list to the underlying table and view list */
        super.setList(thePatterns);
        theViewList.setDataList(thePatterns);
    }

    /**
     * Obtain the correct ComboBox for the given row/column
     */
    @Override
    public JComboBox getComboBox(int row,
                                 int column) {
        Pattern myPattern;

        /* Access the pattern */
        myPattern = (Pattern) thePatterns.get(row);

        /* Switch on column */
        switch (column) {
            case COLUMN_FREQ:
                return theFreqBox;
            case COLUMN_TRANTYP:
                return (myPattern.isCredit())
                                             ? theComboList.getCreditTranTypes(theAccount.getActType())
                                             : theComboList.getDebitTranTypes(theAccount.getActType());
            case COLUMN_PARTNER:
                return (myPattern.isCredit()) ? theComboList.getDebitAccounts(myPattern.getTransType(),
                                                                              theAccount) : theComboList
                        .getCreditAccounts(myPattern.getTransType(), theAccount);
            default:
                return null;
        }
    }

    /**
     * Add a pattern based on a statement line
     * @param pLine the statement line
     */
    public void addPattern(Statement.StatementLine pLine) {
        Pattern myPattern;
        int myRow;

        /* Create the new Item */
        myPattern = new Pattern(thePatterns, pLine);
        thePatterns.add(myPattern);
        myPattern.validate();

        /* Note the changes */
        notifyChanges();

        /* Access the row # */
        myRow = myPattern.indexOf();

        /* Notify of the insertion of the row */
        theModel.fireInsertRowEvents(myRow);
    }

    /* Patterns table model */
    public class PatternsModel extends DataTableModel {
        private static final long serialVersionUID = -8445100544184045930L;

        /**
         * Constructor
         */
        private PatternsModel() {
            /* call constructor */
            super(theTable);
        }

        @Override
        protected void fireInsertRowEvents(int pRow) {
            super.fireInsertRowEvents(pRow);
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
            return (thePatterns == null) ? 0 : thePatterns.size();
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
                case COLUMN_FREQ:
                    return titleFreq;
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
                case COLUMN_CREDIT:
                    return String.class;
                case COLUMN_DEBIT:
                    return String.class;
                case COLUMN_FREQ:
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
            /* Switch on column */
            switch (column) {
                case COLUMN_DATE:
                    return Event.FIELD_DATE;
                case COLUMN_DESC:
                    return Event.FIELD_DESC;
                case COLUMN_TRANTYP:
                    return Event.FIELD_TRNTYP;
                case COLUMN_CREDIT:
                    return Event.FIELD_AMOUNT;
                case COLUMN_DEBIT:
                    return Event.FIELD_AMOUNT;
                case COLUMN_PARTNER:
                    return Pattern.FIELD_PARTNER;
                case COLUMN_FREQ:
                    return Pattern.FIELD_FREQ;
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
            Pattern myPattern;

            /* If the account is not editable */
            if (theAccount.isLocked())
                return false;

            /* Access the pattern */
            myPattern = (Pattern) thePatterns.get(row);

            /* Cannot edit if row is deleted or locked */
            if (myPattern.isDeleted() || myPattern.isLocked())
                return false;

            switch (col) {
                case COLUMN_CREDIT:
                    return myPattern.isCredit();
                case COLUMN_DEBIT:
                    return !myPattern.isCredit();
                default:
                    return true;
            }
        }

        /**
         * Get the value at (row, col)
         * @return the object value
         */
        @Override
        public Object getValueAt(int row,
                                 int col) {
            Pattern myPattern;
            Object o;

            /* Access the pattern */
            myPattern = (Pattern) thePatterns.get(row);

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_DATE:
                    o = myPattern.getDate();
                    break;
                case COLUMN_DESC:
                    o = myPattern.getDesc();
                    if ((o != null) && (((String) o).length() == 0))
                        o = null;
                    break;
                case COLUMN_TRANTYP:
                    o = (myPattern.getTransType() == null) ? null : myPattern.getTransType().getName();
                    break;
                case COLUMN_PARTNER:
                    o = (myPattern.getPartner() == null) ? null : myPattern.getPartner().getName();
                    break;
                case COLUMN_CREDIT:
                    o = (myPattern.isCredit()) ? myPattern.getAmount() : null;
                    break;
                case COLUMN_DEBIT:
                    o = (!myPattern.isCredit()) ? myPattern.getAmount() : null;
                    break;
                case COLUMN_FREQ:
                    o = (myPattern.getFrequency() == null) ? null : myPattern.getFrequency().getName();
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myPattern.hasErrors(getFieldForCell(row, col))))
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
            Pattern myPattern;

            /* Access the pattern */
            myPattern = (Pattern) thePatterns.get(row);

            /* Push history */
            myPattern.pushHistory();

            /* Process errors caught here */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_DATE:
                        myPattern.setDate((DateDay) obj);
                        break;
                    case COLUMN_DESC:
                        myPattern.setDescription((String) obj);
                        break;
                    case COLUMN_TRANTYP:
                        myPattern.setTransType(theTransTypes.searchFor((String) obj));
                        break;
                    case COLUMN_CREDIT:
                    case COLUMN_DEBIT:
                        myPattern.setAmount((Money) obj);
                        break;
                    case COLUMN_PARTNER:
                        myPattern.setPartner(theAccounts.searchFor((String) obj));
                        break;
                    case COLUMN_FREQ:
                    default:
                        myPattern.setFrequency(theFreqs.searchFor((String) obj));
                        break;
                }
            }

            /* Handle Exceptions */
            catch (Exception e) {
                /* Reset values */
                myPattern.popHistory();
                myPattern.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to update field at (" + row + "," + col + ")", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (myPattern.checkForHistory()) {
                /* Note that the item has changed */
                myPattern.setState(DataState.CHANGED);

                /* Validate the item and update the edit state */
                myPattern.clearErrors();
                myPattern.validate();
                thePatterns.findEditState();

                /* Switch on the updated column */
                switch (col) {
                /* if we have updated a sort col */
                    case COLUMN_DATE:
                    case COLUMN_DESC:
                    case COLUMN_TRANTYP:
                        /* Re-Sort the row */
                        thePatterns.reSort(myPattern);

                        /* Determine new row # */
                        int myNewRowNo = myPattern.indexOf();

                        /* If the row # has changed */
                        if (myNewRowNo != row) {
                            /* Report the move of the row */
                            fireMoveRowEvents(row, myNewRowNo);
                            selectRowWithScroll(myNewRowNo);
                            break;
                        }

                        /* else fall through */

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
     * Pattern mouse listener
     */
    private class PatternMouse extends DataMouse<Event> {

        /* Pop-up Menu items */
        private static final String popupCredit = "Set As Credit";
        private static final String popupDebit = "Set As Debit";

        /**
         * Constructor
         */
        private PatternMouse() {
            /* Call super-constructor */
            super(theTable);
        }

        /**
         * Add Special commands to menu
         * @param pMenu the menu to add to
         */
        @Override
        protected void addSpecialCommands(JPopupMenu pMenu) {
            JMenuItem myItem;
            Pattern myLine;
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

                /* Access as line */
                myLine = (Pattern) myRow;

                /* Enable Debit if we have credit */
                if (myLine.isCredit())
                    enableDebit = true;

                /* Enable Credit otherwise */
                else
                    enableCredit = true;
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableCredit || enableDebit) && (pMenu.getComponentCount() > 0)) {
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
        }

        /**
         * Set the specified column to credit/debit
         * @param isCredit set to Credit or else Debit
         */
        protected void setIsCredit(boolean isCredit) {
            AbstractTableModel myModel;
            Pattern myPattern;
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
                row = myRow.indexOf();
                myPattern = (Pattern) myRow;

                /* Ignore rows that are already correct */
                if (myPattern.isCredit() == isCredit)
                    continue;

                /* set the credit value */
                myPattern.pushHistory();
                myPattern.setIsCredit(isCredit);
                if (myPattern.checkForHistory()) {
                    /* Note that the item has changed */
                    myPattern.clearErrors();
                    myPattern.setState(DataState.CHANGED);

                    /* Validate the item */
                    myPattern.validate();

                    /* Notify that the row has changed */
                    myModel.fireTableRowsUpdated(row, row);
                }
            }

            /* Determine the edit state */
            thePatterns.findEditState();
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

            /* If this is a credit command */
            if (myCmd.equals(popupCredit)) {
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
    }

    /**
     * Column Model class
     */
    private class PatternColumnModel extends DataColumnModel {
        private static final long serialVersionUID = 520785956133901998L;

        /* Renderers/Editors */
        private CalendarRenderer theDateRenderer = null;
        private CalendarEditor theDateEditor = null;
        private DecimalRenderer theDecimalRenderer = null;
        private MoneyEditor theMoneyEditor = null;
        private StringRenderer theStringRenderer = null;
        private StringEditor theStringEditor = null;
        private ComboBoxEditor theComboEditor = null;

        /**
         * Constructor
         */
        private PatternColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDateEditor = new CalendarEditor();
            theDecimalRenderer = new DecimalRenderer();
            theMoneyEditor = new MoneyEditor();
            theStringRenderer = new StringRenderer();
            theStringEditor = new StringEditor();
            theComboEditor = new ComboBoxEditor();

            /* Restrict the date editor to pattern range */
            theDateEditor.setRange(Pattern.thePatternRange);

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_DATE, 80, theDateRenderer, theDateEditor));
            addColumn(new DataColumn(COLUMN_TRANTYP, 110, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_DESC, 150, theStringRenderer, theStringEditor));
            addColumn(new DataColumn(COLUMN_PARTNER, 130, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_CREDIT, 90, theDecimalRenderer, theMoneyEditor));
            addColumn(new DataColumn(COLUMN_DEBIT, 90, theDecimalRenderer, theMoneyEditor));
            addColumn(new DataColumn(COLUMN_FREQ, 110, theStringRenderer, theComboEditor));
        }
    }
}
