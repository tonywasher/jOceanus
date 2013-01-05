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
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
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
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.CalendarCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellEditor.RateCellEditor;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldCellRenderer.DecimalCellRenderer;
import net.sourceforge.jOceanus.jFieldSet.JFieldManager;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountRate.AccountRateList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * Account Rates Table.
 * @author Tony Washer
 */
public class AccountRates
        extends JDataTable<AccountRate> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 36193763696660335L;

    /**
     * The Data View.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The Table Model.
     */
    private final RatesModel theModel;

    /**
     * The rates List.
     */
    private transient AccountRateList theRates = null;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * Self Reference.
     */
    private final AccountRates theTable = this;

    /**
     * The Columns Model.
     */
    private final RatesColumnModel theColumns;

    /**
     * The Account.
     */
    private transient Account theAccount = null;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The Update Entry.
     */
    private final transient UpdateEntry<AccountRate> theUpdateEntry;

    /**
     * The Error Panel.
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
    protected void setError(final JDataException pError) {
        theError.setError(pError);
    }

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountRates.class.getName());

    /**
     * Rate Table column name.
     */
    private static final String TITLE_RATE = NLS_BUNDLE.getString("TitleRate");

    /**
     * Rate Table column name.
     */
    private static final String TITLE_BONUS = NLS_BUNDLE.getString("TitleBonus");

    /**
     * Rate Table column name.
     */
    private static final String TITLE_DATE = NLS_BUNDLE.getString("TitleEndDate");

    /**
     * Pop-up Null Date.
     */
    private static final String POPUP_NULLDATE = NLS_BUNDLE.getString("PopUpNullDate");

    /**
     * Pop-up Null Bonus.
     */
    private static final String POPUP_NULLBONUS = NLS_BUNDLE.getString("PopUpNullBonus");

    /**
     * Rate Table column id.
     */
    private static final int COLUMN_RATE = 0;

    /**
     * Bonus Table column id.
     */
    private static final int COLUMN_BONUS = 1;

    /**
     * Date Table column id.
     */
    private static final int COLUMN_DATE = 2;

    /**
     * Rate Table column width.
     */
    private static final int WIDTH_RATE = 90;

    /**
     * Bonus Table column width.
     */
    private static final int WIDTH_BONUS = 90;

    /**
     * Date Table column width.
     */
    private static final int WIDTH_DATE = 100;

    /**
     * Constructor for Rates Window.
     * @param pView the view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public AccountRates(final View pView,
                        final UpdateSet pUpdateSet,
                        final ErrorPanel pError) {
        /* Store details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);
        theError = pError;
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerClass(AccountRate.class);
        setUpdateSet(theUpdateSet);
        theUpdateSet.addActionListener(new RatesListener());

        /* Create the table model and declare it to our superclass */
        theModel = new RatesModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new RatesColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        /* Create the mouse listener (automatically added) */
        RatesMouse myMouse = new RatesMouse();
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
        JDateDayRange myRange = theView.getRange();
        myRange = new JDateDayRange(myRange.getStart(), null);
        theColumns.setDateEditorRange(myRange);
    }

    /**
     * Call underlying controls to take notice of changes in view/selection.
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (theRates != null) {
            theRates.findEditState();
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
        theRates = null;

        /* If we have an account */
        if (theAccount != null) {
            /* Get the Rates edit list */
            FinanceData myData = theView.getData();
            AccountRateList myRates = myData.getRates();
            theRates = myRates.deriveEditList(pAccount);
        }

        /* Declare the list to the underlying table and ViewList */
        setList(theRates);
        theUpdateEntry.setDataList(theRates);
    }

    /**
     * The listener class.
     */
    private final class RatesListener
            implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireTableDataChanged();
            }
        }
    }

    /**
     * Rates table model.
     */
    public final class RatesModel
            extends JDataTableModel<AccountRate> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 296797947278000196L;

        /**
         * Constructor.
         */
        private RatesModel() {
            /* call constructor */
            super(theTable);
        }

        @Override
        public AccountRate getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theRates.get(pRowIndex);
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
            return (theRates == null) ? 0 : theRates.size();
        }

        @Override
        public String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_RATE:
                    return TITLE_RATE;
                case COLUMN_BONUS:
                    return TITLE_BONUS;
                case COLUMN_DATE:
                    return TITLE_DATE;
                default:
                    return null;
            }
        }

        @Override
        public JDataField getFieldForCell(final AccountRate pRate,
                                          final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_RATE:
                    return AccountRate.FIELD_RATE;
                case COLUMN_BONUS:
                    return AccountRate.FIELD_BONUS;
                case COLUMN_DATE:
                    return AccountRate.FIELD_ENDDATE;
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(final AccountRate pPrice,
                                      final int pColIndex) {
            /* Locked if the account is closed */
            return !theAccount.isClosed();
        }

        @Override
        public Object getItemValue(final AccountRate pRate,
                                   final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_RATE:
                    return pRate.getRate();
                case COLUMN_BONUS:
                    return pRate.getBonus();
                case COLUMN_DATE:
                    return pRate.getEndDate();
                default:
                    return null;
            }
        }

        @Override
        public void setItemValue(final AccountRate pRate,
                                 final int pColIndex,
                                 final Object pValue) throws JDataException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_RATE:
                    pRate.setRate((JRate) pValue);
                    break;
                case COLUMN_BONUS:
                    pRate.setBonus((JRate) pValue);
                    break;
                case COLUMN_DATE:
                    pRate.setEndDate((JDateDay) pValue);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Rates mouse listener.
     */
    private final class RatesMouse
            extends JDataTableMouse<AccountRate> {
        /**
         * Constructor.
         */
        private RatesMouse() {
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
            AccountRate myRate;
            boolean enableNullDate = false;
            boolean enableNullBonus = false;

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

                /* Access as rate */
                myRate = (AccountRate) myRow;

                /* Enable null Date if we have date */
                if (myRate.getDate() != null) {
                    enableNullDate = true;
                }

                /* Enable null Tax if we have tax */
                if (myRate.getBonus() != null) {
                    enableNullBonus = true;
                }
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableNullDate || enableNullBonus)
                && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set null date */
            if (enableNullDate) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLDATE);
                myItem.setActionCommand(POPUP_NULLDATE);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null bonus */
            if (enableNullBonus) {
                /* Add the undo change choice */
                myItem = new JMenuItem(POPUP_NULLBONUS);
                myItem.setActionCommand(POPUP_NULLBONUS);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
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

            /* If this is a set null date command */
            if (myCmd.equals(POPUP_NULLDATE)) {
                /* Set Date column to null */
                setColumnToNull(COLUMN_DATE);

                /* else if this is a set null bonus command */
            } else if (myCmd.equals(POPUP_NULLBONUS)) {
                /* Set Bonus column to null */
                setColumnToNull(COLUMN_BONUS);

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
    private final class RatesColumnModel
            extends JDataTableColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -3431873508431574944L;

        /**
         * DateRenderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * DateEditor.
         */
        private final CalendarCellEditor theDateEditor;

        /**
         * RateRenderer.
         */
        private final DecimalCellRenderer theRateRenderer;

        /**
         * RateEditor.
         */
        private final RateCellEditor theRateEditor;

        /**
         * Constructor.
         */
        private RatesColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theDateEditor = theFieldMgr.allocateCalendarCellEditor();
            theRateRenderer = theFieldMgr.allocateDecimalCellRenderer();
            theRateEditor = theFieldMgr.allocateRateCellEditor();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_RATE, WIDTH_RATE, theRateRenderer, theRateEditor));
            addColumn(new JDataTableColumn(COLUMN_BONUS, WIDTH_BONUS, theRateRenderer, theRateEditor));
            addColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
        }

        /**
         * Set the date editor range.
         * @param pRange the range
         */
        private void setDateEditorRange(final JDateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }
    }
}
