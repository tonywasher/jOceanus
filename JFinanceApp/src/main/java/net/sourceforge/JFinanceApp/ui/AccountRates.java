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
package net.sourceforge.JFinanceApp.ui;

import java.awt.event.ActionEvent;
import java.util.ListIterator;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.ui.DataMouse;
import net.sourceforge.JDataModels.ui.DataTable;
import net.sourceforge.JDataModels.ui.Editor.CalendarEditor;
import net.sourceforge.JDataModels.ui.Editor.RateEditor;
import net.sourceforge.JDataModels.ui.ErrorPanel;
import net.sourceforge.JDataModels.ui.RenderManager;
import net.sourceforge.JDataModels.ui.Renderer.CalendarRenderer;
import net.sourceforge.JDataModels.ui.Renderer.DecimalRenderer;
import net.sourceforge.JDataModels.ui.Renderer.RendererFieldValue;
import net.sourceforge.JDataModels.views.UpdateSet;
import net.sourceforge.JDataModels.views.UpdateSet.UpdateEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.AccountRate;
import net.sourceforge.JFinanceApp.data.AccountRate.AccountRateList;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.views.View;

/**
 * Account Rates Table.
 * @author Tony Washer
 */
public class AccountRates extends DataTable<AccountRate> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 36193763696660335L;

    /**
     * The Data View.
     */
    private final transient View theView;

    /**
     * The render manager.
     */
    private final transient RenderManager theRenderMgr;

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
    private final transient UpdateEntry theUpdateEntry;

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
    public boolean hasHeader() {
        return false;
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
        theRenderMgr = theView.getRenderMgr();
        setRenderMgr(theRenderMgr);
        theError = pError;
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerClass(AccountRate.class);
        setUpdateSet(theUpdateSet);

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
    public void refreshData() {
        DateDayRange myRange = theView.getRange();
        myRange = new DateDayRange(myRange.getStart(), null);
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
            AccountRate.AccountRateList myRates = myData.getRates();
            theRates = myRates.deriveEditList(pAccount);
        }

        /* Declare the list to the underlying table and ViewList */
        setList(theRates);
        theUpdateEntry.setDataList(theRates);
    }

    /**
     * Perform additional validation after change.
     */
    // @Override
    protected void validateAfterChange() {
        /* Access the list iterator */
        ListIterator<AccountRate> myIterator = theRates.listIterator();

        /* Loop through the Rates in reverse order */
        while (myIterator.hasPrevious()) {
            AccountRate myCurr = myIterator.previous();

            /* Break loop if we have a date */
            DateDay myDate = myCurr.getDate();
            if (myDate != null) {
                break;
            }

            /* Validate rate */
            myCurr.clearErrors();
            myCurr.validate();

            /* Fire row update */
            int myIndex = myCurr.indexOf();
            theModel.fireTableRowsUpdated(myIndex, myIndex);
        }

        /* Calculate Edit state */
        theRates.findEditState();
    }

    /**
     * Rates table model.
     */
    public final class RatesModel extends DataTableModel {
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

        /**
         * Get the name of the column.
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(final int col) {
            switch (col) {
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

        /**
         * Is the cell editable?
         * @param row the row
         * @param col the column
         * @return true/false
         */
        @Override
        public boolean isCellEditable(final int row,
                                      final int col) {
            /* Locked if the account is closed */
            return !theAccount.isClosed();
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
            AccountRate myRate;
            Object o;

            /* Access the rate */
            myRate = theRates.get(row);

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_RATE:
                    o = myRate.getRate();
                    break;
                case COLUMN_BONUS:
                    o = myRate.getBonus();
                    break;
                case COLUMN_DATE:
                    o = myRate.getEndDate();
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myRate.hasErrors(getFieldForCell(row, col)))) {
                o = RendererFieldValue.Error;
            }

            /* Return to caller */
            return o;
        }

        /**
         * Set the value at (row, col).
         * @param obj the object value to set
         * @param row the row
         * @param col the column
         */
        @Override
        public void setValueAt(final Object obj,
                               final int row,
                               final int col) {
            AccountRate myRate;

            /* Access the rate */
            myRate = theRates.get(row);

            /* Push history */
            myRate.pushHistory();

            /* Protect against Exceptions */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_RATE:
                        myRate.setRate((Rate) obj);
                        break;
                    case COLUMN_BONUS:
                        myRate.setBonus((Rate) obj);
                        break;
                    case COLUMN_DATE:
                        myRate.setEndDate((DateDay) obj);
                        break;
                    default:
                        break;
                }

                /* Handle Exceptions */
            } catch (JDataException e) {
                /* Reset values */
                myRate.popHistory();
                myRate.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to update field at (" + row + "," + col + ")", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* reset history if no change */
            if (myRate.checkForHistory()) {
                /* Switch on the updated column */
                switch (col) {
                /* if we have updated a sort column */
                    case COLUMN_DATE:
                        /* Re-Sort the row */
                        theRates.reSort(myRate);

                        /* Determine new row # */
                        int myNewRowNo = myRate.indexOf();

                        /* If the row # has changed */
                        if (myNewRowNo != row) {
                            /* Report the move of the row */
                            selectRowWithScroll(myNewRowNo);
                            break;
                        }
                    default:
                        break;
                }

                /* Increment data version */
                theUpdateSet.incrementVersion();

                /* Update components to reflect changes */
                fireTableDataChanged();
                notifyChanges();
            }
        }
    }

    /**
     * Rates mouse listener.
     */
    private final class RatesMouse extends DataMouse<AccountRate> {
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
                if ((myRow == null) || (myRow.isLocked()) || (myRow.isDeleted())) {
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
            if ((enableNullDate || enableNullBonus) && (pMenu.getComponentCount() > 0)) {
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

        @Override
        protected void setNullValue(final AccountRate pItem,
                                    final int col) {
            /* Switch on the column */
            switch (col) {
                case COLUMN_DATE:
                    pItem.setNullValue(AccountRate.FIELD_ENDDATE);
                    break;
                case COLUMN_BONUS:
                    pItem.setNullValue(AccountRate.FIELD_BONUS);
                    break;
                default:
                    break;
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
    private final class RatesColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -3431873508431574944L;

        /**
         * DateRenderer.
         */
        private final CalendarRenderer theDateRenderer;

        /**
         * DateEditor.
         */
        private final CalendarEditor theDateEditor;

        /**
         * RateRenderer.
         */
        private final DecimalRenderer theRateRenderer;

        /**
         * RateEditor.
         */
        private final RateEditor theRateEditor;

        /**
         * Constructor.
         */
        private RatesColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = theRenderMgr.allocateCalendarRenderer();
            theDateEditor = new CalendarEditor();
            theRateRenderer = theRenderMgr.allocateDecimalRenderer();
            theRateEditor = new RateEditor();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_RATE, WIDTH_RATE, theRateRenderer, theRateEditor));
            addColumn(new DataColumn(COLUMN_BONUS, WIDTH_BONUS, theRateRenderer, theRateEditor));
            addColumn(new DataColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
        }

        /**
         * Set the date editor range.
         * @param pRange the range
         */
        private void setDateEditorRange(final DateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }
    }
}
