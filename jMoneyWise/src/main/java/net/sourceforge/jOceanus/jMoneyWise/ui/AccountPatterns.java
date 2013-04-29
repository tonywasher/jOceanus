/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sourceforge.jOceanus.jDataManager.JDataException;
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
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.ComboBoxCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.MoneyCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.StringCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldManager;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern.PatternList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.Frequency;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.Frequency.FrequencyList;
import net.sourceforge.jOceanus.jMoneyWise.ui.controls.ComboSelect;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * Account Patterns Table.
 * @author Tony Washer
 */
public class AccountPatterns
        extends JDataTable<Pattern> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1968946370981616222L;

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
    private final PatternsModel theModel;

    /**
     * Patterns list.
     */
    private transient PatternList thePatterns = null;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * Frequency Box.
     */
    private final JComboBox<Frequency> theFreqBox;

    /**
     * Self Reference.
     */
    private final AccountPatterns theTable = this;

    /**
     * Column Model.
     */
    private final PatternColumnModel theColumns;

    /**
     * The account.
     */
    private transient Account theAccount = null;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * Update Entry.
     */
    private final transient UpdateEntry<Pattern> theUpdateEntry;

    /**
     * ComboList.
     */
    private final transient ComboSelect theComboList;

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
    protected void setError(final JDataException pError) {
        theError.setError(pError);
    }

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountPatterns.class.getName());

    /**
     * Date column title.
     */
    private static final String TITLE_DATE = Extract.TITLE_DATE;

    /**
     * Description column title.
     */
    private static final String TITLE_DESC = Extract.TITLE_DESC;

    /**
     * Category type column title.
     */
    private static final String TITLE_CATEGORY = Extract.TITLE_CATEGORY;

    /**
     * Partner column title.
     */
    protected static final String TITLE_PARTNER = NLS_BUNDLE.getString("TitlePartner");

    /**
     * Credit column title.
     */
    private static final String TITLE_CREDIT = Extract.TITLE_CREDIT;

    /**
     * Debit column title.
     */
    private static final String TITLE_DEBIT = Extract.TITLE_DEBIT;

    /**
     * Frequency column title.
     */
    private static final String TITLE_FREQ = NLS_BUNDLE.getString("TitleFrequency");

    /**
     * Credit menu item.
     */
    protected static final String POPUP_CREDIT = NLS_BUNDLE.getString("PopUpCredit");

    /**
     * Debit menu item.
     */
    protected static final String POPUP_DEBIT = NLS_BUNDLE.getString("PopUpDebit");

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
     * Category column width.
     */
    private static final int WIDTH_CATEGORY = 110;

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
     * Constructor for Patterns Window.
     * @param pView the view
     * @param pUpdateSet the update set
     * @param pCombo the combo manager
     * @param pError the error panel
     */
    public AccountPatterns(final View pView,
                           final UpdateSet pUpdateSet,
                           final ComboSelect pCombo,
                           final ErrorPanel pError) {
        /* Store details */
        theView = pView;
        theComboList = pCombo;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);
        theError = pError;
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerClass(Pattern.class);
        setUpdateSet(theUpdateSet);
        theUpdateSet.addActionListener(new PatternsListener());

        /* Create the model and declare it to our superclass */
        theModel = new PatternsModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new PatternColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns */
        getTableHeader().setReorderingAllowed(false);

        /* Build the combo box */
        theFreqBox = new JComboBox<Frequency>();

        /* Add the mouse listener */
        PatternMouse myMouse = new PatternMouse();
        addMouseListener(myMouse);

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
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
        pEntry.setFocus(theUpdateEntry.getName());
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    protected void refreshData() {
        /* Access the data */
        FinanceData myData = theView.getData();

        /* Access Frequencies, TransTypes and Accounts */
        FrequencyList myFreqs = myData.getFrequencys();

        /* If we have frequencies already populated */
        if (theFreqBox.getItemCount() > 0) {
            /* Remove the frequencies */
            theFreqBox.removeAllItems();
        }

        /* Access the frequency iterator */
        Iterator<Frequency> myIterator = myFreqs.listIterator();

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
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (thePatterns != null) {
            thePatterns.findEditState();
        }

        /* Notify listeners */
        fireStateChanged();
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
            thePatterns = myPatterns.deriveEditList(pAccount);
        }

        /* Declare the list to the underlying table and view list */
        super.setList(thePatterns);
        theUpdateEntry.setDataList(thePatterns);
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
        /* Access the pattern */
        Pattern myPattern = thePatterns.get(row);

        /* Switch on column */
        switch (column) {
            case COLUMN_FREQ:
                return theFreqBox;
            case COLUMN_CATEGORY:
                return (myPattern.isCredit())
                        ? theComboList.getCreditCategories(theAccount)
                        : theComboList.getDebitCategories(theAccount);
            case COLUMN_PARTNER:
                return (myPattern.isCredit())
                        ? theComboList.getDebitAccounts(myPattern.getCategory(), theAccount)
                        : theComboList.getCreditAccounts(myPattern.getCategory(), theAccount);
            default:
                return null;
        }
    }

    /**
     * Add a pattern based on an event.
     * @param pEvent the base event
     */
    protected void addPattern(final Event pEvent) {
        /* Create the new Item */
        Pattern myPattern = new Pattern(thePatterns, pEvent);
        thePatterns.add(myPattern);
        myPattern.validate();

        /* Note the changes */
        notifyChanges();

        /* Access the row # */
        int myRow = myPattern.indexOf();

        /* Notify of the insertion of the row */
        theModel.fireInsertRows(myRow);
    }

    /**
     * The listener class.
     */
    private final class PatternsListener
            implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }
        }
    }

    /**
     * Patterns table model.
     */
    public final class PatternsModel
            extends JDataTableModel<Pattern> {
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

        @Override
        public Pattern getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return thePatterns.get(pRowIndex);
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
            return (thePatterns == null)
                    ? 0
                    : thePatterns.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DATE:
                    return TITLE_DATE;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_CATEGORY:
                    return TITLE_CATEGORY;
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

        @Override
        public Class<?> getColumnClass(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_DESC:
                    return String.class;
                case COLUMN_CATEGORY:
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

        @Override
        public JDataField getFieldForCell(final Pattern pPattern,
                                          final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return Event.FIELD_DATE;
                case COLUMN_DESC:
                    return Event.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return Event.FIELD_CATEGORY;
                case COLUMN_CREDIT:
                    return Event.FIELD_AMOUNT;
                case COLUMN_DEBIT:
                    return Event.FIELD_AMOUNT;
                case COLUMN_PARTNER:
                    return ((pPattern == null) || pPattern.isCredit())
                            ? Event.FIELD_CREDIT
                            : Event.FIELD_DEBIT;
                case COLUMN_FREQ:
                    return Pattern.FIELD_FREQ;
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(final Pattern pPattern,
                                      final int pColIndex) {
            /* If the account is not editable */
            if (theAccount.isLocked()) {
                return false;
            }

            /* Cannot edit if row is deleted or locked */
            if (pPattern.isDeleted()
                || pPattern.isLocked()) {
                return false;
            }

            switch (pColIndex) {
                case COLUMN_CREDIT:
                    return pPattern.isCredit();
                case COLUMN_DEBIT:
                    return !pPattern.isCredit();
                default:
                    return true;
            }
        }

        @Override
        public Object getItemValue(final Pattern pPattern,
                                   final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pPattern.getDate();
                case COLUMN_DESC:
                    return pPattern.getDesc();
                case COLUMN_CATEGORY:
                    return pPattern.getCategory();
                case COLUMN_PARTNER:
                    return pPattern.getPartner();
                case COLUMN_CREDIT:
                    return (pPattern.isCredit())
                            ? pPattern.getAmount()
                            : null;
                case COLUMN_DEBIT:
                    return (!pPattern.isCredit())
                            ? pPattern.getAmount()
                            : null;
                case COLUMN_FREQ:
                    return pPattern.getFrequency();
                default:
                    return null;
            }
        }

        @Override
        public void setItemValue(final Pattern pPattern,
                                 final int pColIndex,
                                 final Object pValue) throws JDataException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pPattern.setDate((JDateDay) pValue);
                    break;
                case COLUMN_DESC:
                    pPattern.setDescription((String) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pPattern.setCategory((EventCategory) pValue);
                    break;
                case COLUMN_CREDIT:
                case COLUMN_DEBIT:
                    pPattern.setAmount((JMoney) pValue);
                    break;
                case COLUMN_PARTNER:
                    pPattern.setPartner((Account) pValue);
                    break;
                case COLUMN_FREQ:
                default:
                    pPattern.setFrequency((Frequency) pValue);
                    break;
            }
        }
    }

    /**
     * Pattern mouse listener.
     */
    private final class PatternMouse
            extends JDataTableMouse<Pattern> {
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
            boolean enableCredit = false;
            boolean enableDebit = false;

            /* Nothing to do if the table is locked */
            if (theTable.isLocked()) {
                return;
            }

            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Access as line */
                Pattern myLine = (Pattern) myRow;

                /* Enable Debit if we have credit */
                if (myLine.isCredit()) {
                    enableDebit = true;

                    /* Enable Credit otherwise */
                } else {
                    enableCredit = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableCredit || enableDebit)
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
        }

        /**
         * Set the specified column to credit/debit.
         * @param isCredit set to Credit or else Debit
         */
        protected void setIsCredit(final boolean isCredit) {
            /* Loop through the selected rows */
            for (DataItem myRow : theTable.cacheSelectedRows()) {
                /* Ignore locked/deleted rows */
                if ((myRow == null)
                    || (myRow.isLocked())
                    || (myRow.isDeleted())) {
                    continue;
                }

                /* Determine row */
                Pattern myPattern = Pattern.class.cast(myRow);

                /* Ignore rows that are already correct */
                if (myPattern.isCredit() == isCredit) {
                    continue;
                }

                /* set the credit value */
                myPattern.pushHistory();
                // myPattern.setIsCredit(isCredit);
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
            theModel.fireTableDataChanged();
            notifyChanges();
        }
    }

    /**
     * Column Model class.
     */
    private final class PatternColumnModel
            extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 520785956133901998L;

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
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String Editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Combo Editor.
         */
        private final ComboBoxCellEditor theComboEditor;

        /**
         * Constructor.
         */
        private PatternColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theDecimalRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theMoneyEditor = theFieldMgr.allocateMoneyCellEditor();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theComboEditor = theFieldMgr.allocateComboBoxCellEditor();

            /* Restrict the date editor to pattern range */
            theDateEditor.setRange(Pattern.RANGE_PATTERN);

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            addColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_CATEGORY, theStringRenderer, theComboEditor));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_DESC, theStringRenderer, theStringEditor));
            addColumn(new JDataTableColumn(COLUMN_PARTNER, WIDTH_PARTNER, theStringRenderer, theComboEditor));
            addColumn(new JDataTableColumn(COLUMN_CREDIT, WIDTH_CREDIT, theDecimalRenderer, theMoneyEditor));
            addColumn(new JDataTableColumn(COLUMN_DEBIT, WIDTH_DEBIT, theDecimalRenderer, theMoneyEditor));
            addColumn(new JDataTableColumn(COLUMN_FREQ, WIDTH_FREQ, theStringRenderer, theComboEditor));
        }
    }
}
