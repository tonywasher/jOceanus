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
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Rate;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountRate;
import uk.co.tolcroft.finance.data.AccountRate.AccountRateList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.CalendarEditor;
import uk.co.tolcroft.models.ui.Editor.RateEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class AccountRates extends DataTable<AccountRate> {
    /* Members */
    private static final long serialVersionUID = 36193763696660335L;

    private View theView = null;
    private RatesModel theModel = null;
    private AccountRateList theRates = null;
    private JPanel thePanel = null;
    private AccountRates theTable = this;
    private RatesMouse theMouse = null;
    private RatesColumnModel theColumns = null;
    private AccountTab theParent = null;
    private DateDayRange theRange = null;
    private Account theAccount = null;
    private ListClass theViewList = null;
    private JDataEntry theDataEntry = null;
    private ErrorPanel theError = null;

    /* Access methods */
    public JPanel getPanel() {
        return thePanel;
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    /* Access the debug entry */
    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    /* Table headers */
    private static final String titleRate = "Rate";
    private static final String titleBonus = "Bonus";
    private static final String titleDate = "EndDate";

    /* Table columns */
    private static final int COLUMN_RATE = 0;
    private static final int COLUMN_BONUS = 1;
    private static final int COLUMN_DATE = 2;

    /**
     * Constructor for Rates Window
     * @param pParent the parent window
     */
    public AccountRates(AccountTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataManager());

        /* Declare variables */
        GroupLayout myLayout;

        /* Store details about the parent */
        theParent = pParent;
        theView = pParent.getView();
        theViewList = pParent.getViewSet().registerClass(AccountRate.class);

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
        theMouse = new RatesMouse();
        addMouseListener(theMouse);

        /* Create the debug entry, attach to AccountDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry("Rates");
        theDataEntry.addAsChildOf(pParent.getDataEntry());
        theDataEntry.hideEntry();

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
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theError)
                                  .addComponent(getScrollPane()).addContainerGap()));
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
     * Refresh views/controls after a load/update of underlying data
     */
    public void refreshData() {
        theRange = theView.getRange();
        theRange = new DateDayRange(theRange.getStart(), null);
        theColumns.setDateEditorRange(theRange);
    }

    /**
     * Update Debug view
     */
    @Override
    public void updateDebug() {
        theDataEntry.setObject(theRates);
    }

    /**
     * Call underlying controls to take notice of changes in view/selection
     */
    @Override
    public void notifyChanges() {
        /* Find the edit state */
        if (theRates != null)
            theRates.findEditState();

        /* Update the parent panel */
        theParent.notifyChanges();
    }

    /**
     * Set Selection to the specified account.
     * @param pAccount the Account for the extract
     * @throws JDataException on error
     */
    public void setSelection(Account pAccount) throws JDataException {
        /* Record the account */
        theAccount = pAccount;
        theRates = null;

        /* If we have an account */
        if (theAccount != null) {
            /* Get the Rates edit list */
            FinanceData myData = theView.getData();
            AccountRate.AccountRateList myRates = myData.getRates();
            theRates = myRates.getEditList(pAccount);
        }

        /* Declare the list to the underlying table and ViewList */
        setList(theRates);
        theViewList.setDataList(theRates);
    }

    /**
     * Perform additional validation after change
     */
    @Override
    protected void validateAfterChange() {
        DataListIterator<AccountRate> myIterator;
        AccountRate myCurr;
        int myIndex = -1;

        /* Access the list iterator */
        myIterator = theRates.listIterator();

        /* Loop through the Rates in reverse order */
        while ((myCurr = myIterator.previous()) != null) {
            /* Break loop if we have a date */
            DateDay myDate = myCurr.getDate();
            if (myDate != null)
                break;

            /* Validate rate */
            myCurr.clearErrors();
            myCurr.validate();

            /* Fire row update */
            myIndex = myCurr.indexOf();
            theModel.fireTableRowsUpdated(myIndex, myIndex);
        }

        /* Calculate Edit state */
        theRates.findEditState();
    }

    /* Rates table model */
    public class RatesModel extends DataTableModel {
        private static final long serialVersionUID = 296797947278000196L;

        /**
         * Constructor
         */
        private RatesModel() {
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
            return (theRates == null) ? 0 : theRates.size();
        }

        /**
         * Get the name of the column
         * @param col the column
         * @return the name of the column
         */
        @Override
        public String getColumnName(int col) {
            switch (col) {
                case COLUMN_RATE:
                    return titleRate;
                case COLUMN_BONUS:
                    return titleBonus;
                case COLUMN_DATE:
                    return titleDate;
                default:
                    return null;
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
         * Is the cell at (row, col) editable
         */
        @Override
        public boolean isCellEditable(int row,
                                      int col) {
            /* Locked if the account is closed */
            if (theAccount.isClosed())
                return false;

            /* Otherwise edit-able */
            return true;
        }

        /**
         * Get the value at (row, col)
         * @return the object value
         */
        @Override
        public Object getValueAt(int row,
                                 int col) {
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
            if ((o == null) && (myRate.hasErrors(getFieldForCell(row, col))))
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
                }
            }

            /* Handle Exceptions */
            catch (Throwable e) {
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
                /* Set changed status */
                myRate.clearErrors();
                myRate.setState(DataState.CHANGED);

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

                /* Validate the item and update the edit state */
                myRate.validate();
                validateAfterChange();

                /* Update components to reflect changes */
                notifyChanges();
                updateDebug();
            }
        }
    }

    /**
     * Rates mouse listener
     */
    private class RatesMouse extends DataMouse<AccountRate> {

        /* Pop-up Menu items */
        private static final String popupSetNullDate = "Set Null Date";
        private static final String popupSetNullBonus = "Set Null Bonus";

        /**
         * Constructor
         */
        private RatesMouse() {
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
            AccountRate myRate;
            boolean enableNullDate = false;
            boolean enableNullBonus = false;

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

                /* Access as rate */
                myRate = (AccountRate) myRow;

                /* Enable null Date if we have date */
                if (myRate.getDate() != null)
                    enableNullDate = true;

                /* Enable null Tax if we have tax */
                if (myRate.getBonus() != null)
                    enableNullBonus = true;
            }

            /* If there is something to add and there are already items in the menu */
            if ((enableNullDate || enableNullBonus) && (pMenu.getComponentCount() > 0)) {
                /* Add a separator */
                pMenu.addSeparator();
            }

            /* If we can set null date */
            if (enableNullDate) {
                /* Add the undo change choice */
                myItem = new JMenuItem(popupSetNullDate);
                myItem.setActionCommand(popupSetNullDate);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }

            /* If we can set null bonus */
            if (enableNullBonus) {
                /* Add the undo change choice */
                myItem = new JMenuItem(popupSetNullBonus);
                myItem.setActionCommand(popupSetNullBonus);
                myItem.addActionListener(this);
                pMenu.add(myItem);
            }
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

            /* If this is a set null date command */
            if (myCmd.equals(popupSetNullDate)) {
                /* Set Date column to null */
                setColumnToNull(COLUMN_DATE);
            }

            /* else if this is a set null bonus command */
            else if (myCmd.equals(popupSetNullBonus)) {
                /* Set Bonus column to null */
                setColumnToNull(COLUMN_BONUS);
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
    private class RatesColumnModel extends DataColumnModel {
        private static final long serialVersionUID = -3431873508431574944L;

        /* Renderers/Editors */
        private CalendarRenderer theDateRenderer = null;
        private CalendarEditor theDateEditor = null;
        private DecimalRenderer theRateRenderer = null;
        private RateEditor theRateEditor = null;

        /**
         * Constructor
         */
        private RatesColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDateEditor = new CalendarEditor();
            theRateRenderer = new DecimalRenderer();
            theRateEditor = new RateEditor();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_RATE, 90, theRateRenderer, theRateEditor));
            addColumn(new DataColumn(COLUMN_BONUS, 90, theRateRenderer, theRateEditor));
            addColumn(new DataColumn(COLUMN_DATE, 100, theDateRenderer, theDateEditor));
        }

        /**
         * Set the date editor range
         * @param pRange
         */
        private void setDateEditorRange(DateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }
    }
}
