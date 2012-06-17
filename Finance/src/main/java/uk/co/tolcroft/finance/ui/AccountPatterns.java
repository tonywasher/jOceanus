/*******************************************************************************
 * JFinanceApp: Finance Application
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
import java.util.Iterator;

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
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.Frequency;
import uk.co.tolcroft.finance.data.Frequency.FrequencyList;
import uk.co.tolcroft.finance.data.Pattern;
import uk.co.tolcroft.finance.data.Pattern.PatternList;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.ui.controls.ComboSelect;
import uk.co.tolcroft.finance.views.Statement.StatementLine;
import uk.co.tolcroft.finance.views.View;
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

/**
 * Account Patterns Table.
 * @author Tony Washer
 */
public class AccountPatterns extends DataTable<Event> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1968946370981616222L;

    /**
     * Date view.
     */
    private final View theView;

    /**
     * Table Model.
     */
    private final PatternsModel theModel;

    /**
     * Patterns list.
     */
    private PatternList thePatterns = null;

    /**
     * Frequency list.
     */
    private FrequencyList theFreqs = null;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * Frequency Box.
     */
    private final JComboBox theFreqBox;

    /**
     * Self Reference.
     */
    private final AccountPatterns theTable = this;

    /**
     * Column Model.
     */
    private final PatternColumnModel theColumns;

    /**
     * The parent.
     */
    private final AccountTab theParent;

    /**
     * The account.
     */
    private Account theAccount = null;

    /**
     * List Class.
     */
    private final ListClass theViewList;

    /**
     * ComboList.
     */
    private ComboSelect theComboList = null;

    /**
     * Data Entry.
     */
    private final JDataEntry theDataEntry;

    /**
     * Error Panel.
     */
    private final ErrorPanel theError;

    /**
     * Obtain panel.
     * @return the panel
     */
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

    /**
     * Date column title.
     */
    private static final String TITLE_DATE = "Date";

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = "Description";

    /**
     * Transaction type column title.
     */
    private static final String TITLE_TRANS = "TransactionType";

    /**
     * Partner column title.
     */
    private static final String TITLE_PARTNER = "Partner";

    /**
     * Credit column title.
     */
    private static final String TITLE_CREDIT = "Credit";

    /**
     * Debit column title.
     */
    private static final String TITLE_DEBIT = "Debit";

    /**
     * Frequency column title.
     */
    private static final String TITLE_FREQ = "Frequency";

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
     * Frequency column id.
     */
    private static final int COLUMN_FREQ = 6;

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
     * Frequency column width.
     */
    private static final int WIDTH_FREQ = 110;

    /**
     * Panel width.
     */
    private static final int WIDTH_PANEL = 900;

    /**
     * Constructor for Patterns Window.
     * @param pParent the parent window
     */
    public AccountPatterns(final AccountTab pParent) {
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
        PatternMouse myMouse = new PatternMouse();
        addMouseListener(myMouse);

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
                                                                  GroupLayout.DEFAULT_SIZE, WIDTH_PANEL,
                                                                  Short.MAX_VALUE)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theError)
                                  .addComponent(getScrollPane())));
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    public void refreshData() {
        /* Access the data */
        FinanceData myData = theView.getData();

        /* Access Frequencies, TransTypes and Accounts */
        theFreqs = myData.getFrequencys();

        /* Access the combo list from parent */
        theComboList = theParent.getComboList();

        /* If we have frequencies already populated */
        if (theFreqBox.getItemCount() > 0) {
            /* Remove the frequencies */
            theFreqBox.removeAllItems();
        }

        /* Access the frequency iterator */
        Iterator<Frequency> myIterator = theFreqs.listIterator();

        /* Add the Frequency values to the frequencies box */
        while (myIterator.hasNext()) {
            Frequency myFreq = myIterator.next();

            /* Ignore the frequency if it is not enabled */
            if (!myFreq.getEnabled()) {
                continue;
            }

            /* Add the item to the list */
            theFreqBox.addItem(myFreq);
        }
    }

    /**
     * Update Debug view.
     */
    @Override
    public void updateDebug() {
        theDataEntry.setObject(thePatterns);
    }

    /**
     * Save changes from the view into the underlying data.
     */
    @Override
    public void saveData() {
        /* Just update the debug, save has already been done */
        updateDebug();
    }

    /**
     * Lock on error.
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(final boolean isError) {
        /* Lock scroll-able area */
        getScrollPane().setEnabled(!isError);
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (thePatterns != null) {
            thePatterns.findEditState();
        }

        /* Update the parent panel */
        theParent.notifyChanges();
    }

    /**
     * Set Selection to the specified account.
     * @param pAccount the Account for the extract
     * @throws JDataException on error
     */
    public void setSelection(final Account pAccount) throws JDataException {
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
     * Obtain the correct ComboBox for the given row/column.
     * @param row the row
     * @param column the column
     * @return the comboBox
     */
    @Override
    public JComboBox getComboBox(final int row,
                                 final int column) {
        /* Access the pattern */
        Pattern myPattern = (Pattern) thePatterns.get(row);

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
     * Add a pattern based on a statement line.
     * @param pLine the statement line
     */
    public void addPattern(final StatementLine pLine) {
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
        theModel.fireInsertRows(myRow);
    }

    /**
     * Patterns table model.
     */
    public final class PatternsModel extends DataTableModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -8445100544184045930L;

        /**
         * Constructor.
         */
        private PatternsModel() {
            /* call constructor */
            super(theTable);
        }

        /**
         * Invoke Insert row events.
         * @param pRow row for insert.
         */
        protected void fireInsertRows(final int pRow) {
            fireInsertRowEvents(pRow);
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
            return (thePatterns == null) ? 0 : thePatterns.size();
        }

        /**
         * Get the name of the column.
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(final int col) {
            switch (col) {
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
                case COLUMN_FREQ:
                    return TITLE_FREQ;
                default:
                    return null;
            }
        }

        /**
         * Get the object class of the column.
         * @param col the column
         * @return the class of the objects associated with the column
         */
        @Override
        public Class<?> getColumnClass(final int col) {
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
         * Obtain the Field id associated with the column.
         * @param row the row
         * @param column the column
         * @return the field id
         */
        @Override
        public JDataField getFieldForCell(final int row,
                                          final int column) {
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
         * Is the cell at (row, col) editable?
         * @param row the row
         * @param col the column
         * @return true/false
         */
        @Override
        public boolean isCellEditable(final int row,
                                      final int col) {
            /* If the account is not editable */
            if (theAccount.isLocked()) {
                return false;
            }

            /* Access the pattern */
            Pattern myPattern = (Pattern) thePatterns.get(row);

            /* Cannot edit if row is deleted or locked */
            if (myPattern.isDeleted() || myPattern.isLocked()) {
                return false;
            }

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
         * Get the value at (row, col).
         * @param row the row
         * @param col the column
         * @return the object value
         */
        @Override
        public Object getValueAt(final int row,
                                 final int col) {
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
                    if ((o != null) && (((String) o).length() == 0)) {
                        o = null;
                    }
                    break;
                case COLUMN_TRANTYP:
                    o = myPattern.getTransType();
                    break;
                case COLUMN_PARTNER:
                    o = myPattern.getPartner();
                    break;
                case COLUMN_CREDIT:
                    o = (myPattern.isCredit()) ? myPattern.getAmount() : null;
                    break;
                case COLUMN_DEBIT:
                    o = (!myPattern.isCredit()) ? myPattern.getAmount() : null;
                    break;
                case COLUMN_FREQ:
                    o = myPattern.getFrequency();
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myPattern.hasErrors(getFieldForCell(row, col)))) {
                o = Renderer.getError();
            }

            /* Return to caller */
            return o;
        }

        /**
         * Set the value at (row, col).
         * @param row the row
         * @param col the column
         * @param obj the object value to set
         */
        @Override
        public void setValueAt(final Object obj,
                               final int row,
                               final int col) {
            /* Access the pattern */
            Pattern myPattern = (Pattern) thePatterns.get(row);

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
                        myPattern.setTransType((TransactionType) obj);
                        break;
                    case COLUMN_CREDIT:
                    case COLUMN_DEBIT:
                        myPattern.setAmount((Money) obj);
                        break;
                    case COLUMN_PARTNER:
                        myPattern.setPartner((Account) obj);
                        break;
                    case COLUMN_FREQ:
                    default:
                        myPattern.setFrequency((Frequency) obj);
                        break;
                }

                /* Handle Exceptions */
            } catch (Exception e) {
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
     * Pattern mouse listener.
     */
    private final class PatternMouse extends DataMouse<Event> {
        /**
         * Credit menu item.
         */
        private static final String POPUP_CREDIT = "Set As Credit";

        /**
         * Debit menu item.
         */
        private static final String POPUP_DEBIT = "Set As Debit";

        /**
         * Constructor.
         */
        private PatternMouse() {
            /* Call super-constructor */
            super(theTable);
        }

        /**
         * Add Special commands to menu.
         * @param pMenu the menu to add to
         */
        @Override
        protected void addSpecialCommands(final JPopupMenu pMenu) {
            JMenuItem myItem;
            Pattern myLine;
            boolean enableCredit = false;
            boolean enableDebit = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (Event myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as line */
                myLine = (Pattern) myRow;

                /* Enable Debit if we have credit */
                if (myLine.isCredit()) {
                    enableDebit = true;

                    /* Enable Credit otherwise */
                } else {
                    enableCredit = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableCredit || enableDebit) && (pMenu.getComponentCount() > 0)) {
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
        }

        /**
         * Set the specified column to credit/debit.
         * @param isCredit set to Credit or else Debit
         */
        protected void setIsCredit(final boolean isCredit) {
            AbstractTableModel myModel;
            Pattern myPattern;
            int row;

            /* Access the table model */
            myModel = theTable.getTableModel();

            /* Loop through the selected rows */
            for (Event myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
                    continue;
                }

                /* Determine row */
                row = myRow.indexOf();
                myPattern = (Pattern) myRow;

                /* Ignore rows that are already correct */
                if (myPattern.isCredit() == isCredit) {
                    continue;
                }

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
         * Perform actions for controls/pop-ups on this table.
         * @param evt the event
         */
        @Override
        public void actionPerformed(final ActionEvent evt) {
            String myCmd = evt.getActionCommand();

            /* Cancel any editing */
            theTable.cancelEditing();

            /* If this is a credit command */
            if (myCmd.equals(POPUP_CREDIT)) {
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
            notifyChanges();
            updateDebug();
        }
    }

    /**
     * Column Model class.
     */
    private final class PatternColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 520785956133901998L;

        /**
         * Date Renderer.
         */
        private final CalendarRenderer theDateRenderer;

        /**
         * Date Editor.
         */
        private final CalendarEditor theDateEditor;

        /**
         * Decimal Renderer.
         */
        private final DecimalRenderer theDecimalRenderer;

        /**
         * Money Editor.
         */
        private final MoneyEditor theMoneyEditor;

        /**
         * String Renderer.
         */
        private final StringRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringEditor theStringEditor;

        /**
         * Combo Editor.
         */
        private final ComboBoxEditor theComboEditor;

        /**
         * Constructor.
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
            theDateEditor.setRange(Pattern.RANGE_PATTERN);

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            addColumn(new DataColumn(COLUMN_TRANTYP, WIDTH_TRANTYP, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new DataColumn(COLUMN_PARTNER, WIDTH_PARTNER, theStringRenderer, theComboEditor));
            addColumn(new DataColumn(COLUMN_CREDIT, WIDTH_CREDIT, theDecimalRenderer, theMoneyEditor));
            addColumn(new DataColumn(COLUMN_DEBIT, WIDTH_DEBIT, theDecimalRenderer, theMoneyEditor));
            addColumn(new DataColumn(COLUMN_FREQ, WIDTH_FREQ, theStringRenderer, theComboEditor));
        }
    }
}
