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

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Price;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.finance.views.ViewPrice;
import uk.co.tolcroft.finance.views.ViewPrice.ViewPriceList;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.ui.DataMouse;
import uk.co.tolcroft.models.ui.DataTable;
import uk.co.tolcroft.models.ui.Editor.CalendarEditor;
import uk.co.tolcroft.models.ui.Editor.PriceEditor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.Renderer.CalendarRenderer;
import uk.co.tolcroft.models.ui.Renderer.DecimalRenderer;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class AccountPrices extends DataTable<AccountPrice> {
    /* Members */
    private static final long serialVersionUID = 1035380774297559650L;

    private View theView = null;
    private PricesModel theModel = null;
    private ViewPriceList thePrices = null;
    private JPanel thePanel = null;
    private AccountTab theParent = null;
    private DateDayRange theRange = null;
    private Account theAccount = null;
    private ListClass theViewList = null;
    private AccountPrices theTable = this;
    private PricesMouse theMouse = null;
    private PricesColumnModel theColumns = null;
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

    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    /* Hooks */
    public boolean needsMembers() {
        return true;
    }

    /* Table headers */
    private static final String titleDate = "Date";
    private static final String titlePrice = "Price";
    private static final String titleDilution = "Dilution";
    private static final String titleDilPrice = "DilutedPrice";

    /* Table columns */
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_PRICE = 1;
    private static final int COLUMN_DILUTION = 2;
    private static final int COLUMN_DILUTEDPRICE = 3;

    /**
     * Constructor for Prices Window
     * @param pParent the parent window
     */
    public AccountPrices(AccountTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataManager());

        /* Declare variables */
        GroupLayout myLayout;

        /* Store details about the parent */
        theParent = pParent;
        theView = pParent.getView();
        theViewList = pParent.getViewSet().registerClass(ViewPrice.class);

        /* Create the model and declare it to our superclass */
        theModel = new PricesModel();
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new PricesColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        /* Create the debug entry, attach to AccountDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry("Prices");
        theDataEntry.addAsChildOf(pParent.getDataEntry());
        theDataEntry.hideEntry();

        /* Add the mouse listener */
        theMouse = new PricesMouse();
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
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theError)
                                  .addComponent(getScrollPane()).addContainerGap()));
    }

    /**
     * Refresh views/controls after a load/update of underlying data
     */
    public void refreshData() {
        theRange = theView.getRange();
        theColumns.setDateEditorRange(theRange);
    }

    /**
     * Update Debug view
     */
    @Override
    public void updateDebug() {
        theDataEntry.setObject(thePrices);
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
        if (thePrices != null)
            thePrices.findEditState();

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
        thePrices = null;

        /* If we have an account */
        if (theAccount != null) {
            /* Obtain the Prices extract */
            thePrices = new ViewPriceList(theView, pAccount);
        }

        /* Declare the list */
        theColumns.setColumnSelection();
        super.setList(thePrices);
        theViewList.setDataList(thePrices);
    }

    /**
     * Check whether a row is deletable
     * @param pRow the row
     * @return is the row deletable
     */
    protected boolean isRowDeletable(ViewPrice pRow) {
        /* If the row is not deleted */
        if (!pRow.isDeleted()) {
            /* we can delete if the list size is greater than one */
            if (thePrices.sizeNormal() > 1)
                return true;
        }

        /* Not Deletable */
        return false;
    }

    /**
     * Check whether we duplicate a row
     * @param pRow the row
     * @return false
     */
    protected boolean isRowDuplicatable(ViewPrice pRow) {
        return false;
    }

    /* Prices table model */
    public class PricesModel extends DataTableModel {
        private static final long serialVersionUID = -2613779599240142148L;

        /**
         * Constructor
         */
        private PricesModel() {
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
            return (thePrices == null) ? 0 : thePrices.size();
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
                case COLUMN_PRICE:
                    return titlePrice;
                case COLUMN_DILUTION:
                    return titleDilution;
                case COLUMN_DILUTEDPRICE:
                    return titleDilPrice;
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
                case COLUMN_DATE:
                    return AccountPrice.FIELD_DATE;
                case COLUMN_PRICE:
                    return AccountPrice.FIELD_PRICE;
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

            switch (col) {
                case COLUMN_DATE:
                    return (theRange != null);
                case COLUMN_PRICE:
                    return true;
                case COLUMN_DILUTION:
                    return false;
                case COLUMN_DILUTEDPRICE:
                    return false;
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
            ViewPrice myPrice;
            Object o;

            /* Access the price */
            myPrice = (ViewPrice) thePrices.get(row);

            /* Return the appropriate value */
            switch (col) {
                case COLUMN_DATE:
                    o = myPrice.getDate();
                    break;
                case COLUMN_PRICE:
                    o = myPrice.getPrice();
                    break;
                case COLUMN_DILUTION:
                    o = myPrice.getDilution();
                    break;
                case COLUMN_DILUTEDPRICE:
                    o = myPrice.getDilutedPrice();
                    break;
                default:
                    o = null;
                    break;
            }

            /* If we have a null value for an error field, set error description */
            if ((o == null) && (myPrice.hasErrors(getFieldForCell(row, col))))
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
            ViewPrice myPrice;

            /* Access the price */
            myPrice = (ViewPrice) thePrices.get(row);

            /* Push history */
            myPrice.pushHistory();

            /* Protect against Exceptions */
            try {
                /* Store the appropriate value */
                switch (col) {
                    case COLUMN_DATE:
                        myPrice.setDate((DateDay) obj);
                        break;
                    case COLUMN_PRICE:
                        myPrice.setPrice((Price) obj);
                        break;
                }
            }

            /* Handle Exceptions */
            catch (Throwable e) {
                /* Reset values */
                myPrice.popHistory();
                myPrice.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA,
                        "Failed to update field at (" + row + "," + col + ")", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* If we have changes */
            if (myPrice.checkForHistory()) {
                /* Set new state */
                myPrice.clearErrors();
                myPrice.setState(DataState.CHANGED);

                /* Validate the item and update the edit state */
                myPrice.validate();
                thePrices.findEditState();

                /* Switch on the updated column */
                switch (col) {
                    case COLUMN_DATE:
                        /* Re-Sort the row */
                        thePrices.reSort(myPrice);

                        /* Determine new row # */
                        int myNewRowNo = myPrice.indexOf();

                        /* If the row # has changed */
                        if (myNewRowNo != row) {
                            /* Report the move of the row */
                            fireMoveRowEvents(row, myNewRowNo);
                            selectRowWithScroll(myNewRowNo);
                            break;
                        }

                        /* else fall through */

                        /* else note that we have updated this row */
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
     * Prices mouse listener
     */
    private class PricesMouse extends DataMouse<AccountPrice> {
        /**
         * Constructor
         */
        private PricesMouse() {
            /* Call super-constructor */
            super(theTable);
        }
    }

    /**
     * Column Model class
     */
    private class PricesColumnModel extends DataColumnModel {
        private static final long serialVersionUID = -851990835577845594L;

        /* Renderers/Editors */
        private CalendarRenderer theDateRenderer = null;
        private CalendarEditor theDateEditor = null;
        private DecimalRenderer theDecimalRenderer = null;
        private PriceEditor thePriceEditor = null;
        private DataColumn theDiluteCol = null;
        private DataColumn theDilPriceCol = null;
        private boolean hasDilutions = true;

        /**
         * Constructor
         */
        private PricesColumnModel() {
            /* call constructor */
            super(theTable);

            /* Create the relevant formatters/editors */
            theDateRenderer = new CalendarRenderer();
            theDateEditor = new CalendarEditor();
            theDecimalRenderer = new DecimalRenderer();
            thePriceEditor = new PriceEditor();

            /* Create the columns */
            addColumn(new DataColumn(COLUMN_DATE, 80, theDateRenderer, theDateEditor));
            addColumn(new DataColumn(COLUMN_PRICE, 90, theDecimalRenderer, thePriceEditor));
            addColumn(theDiluteCol = new DataColumn(COLUMN_DILUTION, 90, theDecimalRenderer, null));
            addColumn(theDilPriceCol = new DataColumn(COLUMN_DILUTEDPRICE, 100, theDecimalRenderer, null));
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
         * Set column selection for this view
         */
        private void setColumnSelection() {
            /* If we should show dilutions */
            if ((thePrices != null) && (thePrices.hasDilutions())) {
                /* If we are not showing dilutions */
                if (!hasDilutions) {
                    /* Add the dilutions columns and record the fact */
                    addColumn(theDiluteCol);
                    addColumn(theDilPriceCol);
                    hasDilutions = true;
                }
            }

            /* else If we are showing dilutions */
            else if (hasDilutions) {
                /* Remove the dilutions columns and record the fact */
                removeColumn(theDiluteCol);
                removeColumn(theDilPriceCol);
                hasDilutions = false;
            }
        }
    }
}
