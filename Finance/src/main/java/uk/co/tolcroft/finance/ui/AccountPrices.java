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

/**
 * Account Prices Table.
 * @author Tony Washer
 */
public class AccountPrices extends DataTable<AccountPrice> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1035380774297559650L;

    /**
     * Date View.
     */
    private final View theView;

    /**
     * Price List.
     */
    private ViewPriceList thePrices = null;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * Parent.
     */
    private final AccountTab theParent;

    /**
     * DataSet range.
     */
    private DateDayRange theRange = null;

    /**
     * Account.
     */
    private Account theAccount = null;

    /**
     * List Class.
     */
    private final ListClass theViewList;

    /**
     * Self Reference.
     */
    private final AccountPrices theTable = this;

    /**
     * Column Model.
     */
    private final PricesColumnModel theColumns;

    /**
     * Data Entry.
     */
    private final JDataEntry theDataEntry;

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
    public boolean hasHeader() {
        return false;
    }

    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    /**
     * Do we need members?
     * @return true/false
     */
    public boolean needsMembers() {
        return true;
    }

    /**
     * Date column title.
     */
    private static final String TITLE_DATE = "Date";

    /**
     * Price column title.
     */
    private static final String TITLE_PRICE = "Price";

    /**
     * Dilution column title.
     */
    private static final String TITLE_DILUTION = "Dilution";

    /**
     * Diluted price column title.
     */
    private static final String TITLE_DILUTEDPRICE = "DilutedPrice";

    /**
     * Date column id.
     */
    private static final int COLUMN_DATE = 0;

    /**
     * Price column title.
     */
    private static final int COLUMN_PRICE = 1;

    /**
     * Dilution column title.
     */
    private static final int COLUMN_DILUTION = 2;

    /**
     * Diluted Price column title.
     */
    private static final int COLUMN_DILUTEDPRICE = 3;

    /**
     * Date column width.
     */
    private static final int WIDTH_DATE = 80;

    /**
     * Price column width.
     */
    private static final int WIDTH_PRICE = 90;

    /**
     * Dilution column width.
     */
    private static final int WIDTH_DILUTION = 90;

    /**
     * Diluted Price column width.
     */
    private static final int WIDTH_DILUTEDPRICE = 100;

    /**
     * Constructor for Prices Window.
     * @param pParent the parent window
     */
    public AccountPrices(final AccountTab pParent) {
        /* Initialise superclass */
        super(pParent.getDataManager());

        /* Declare variables */
        GroupLayout myLayout;

        /* Store details about the parent */
        theParent = pParent;
        theView = pParent.getView();
        theViewList = pParent.getViewSet().registerClass(ViewPrice.class);

        /* Create the model and declare it to our superclass */
        PricesModel myModel = new PricesModel();
        setModel(myModel);

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
        PricesMouse myMouse = new PricesMouse();
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
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(theError)
                                  .addComponent(getScrollPane()).addContainerGap()));
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    public void refreshData() {
        theRange = theView.getRange();
        theColumns.setDateEditorRange(theRange);
    }

    /**
     * Update Debug view.
     */
    @Override
    public void updateDebug() {
        theDataEntry.setObject(thePrices);
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
        if (thePrices != null) {
            thePrices.findEditState();
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
     * Check whether a row is deletable.
     * @param pRow the row
     * @return is the row deletable
     */
    protected boolean isRowDeletable(final ViewPrice pRow) {
        /* If the row is not deleted, we can delete if the list size is greater than one */
        return ((!pRow.isDeleted()) && (thePrices.size() > 1));
    }

    /**
     * Check whether we duplicate a row.
     * @param pRow the row
     * @return false
     */
    protected boolean isRowDuplicatable(final ViewPrice pRow) {
        return false;
    }

    /**
     * Prices table model.
     */
    public final class PricesModel extends DataTableModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -2613779599240142148L;

        /**
         * Constructor.
         */
        private PricesModel() {
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
            return (thePrices == null) ? 0 : thePrices.size();
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
                case COLUMN_PRICE:
                    return TITLE_PRICE;
                case COLUMN_DILUTION:
                    return TITLE_DILUTION;
                case COLUMN_DILUTEDPRICE:
                    return TITLE_DILUTEDPRICE;
                default:
                    return null;
            }
        }

        /**
         * Obtain the Field id associated with the column.
         * @param row the row
         * @param column the column
         * @return the fieldId
         */
        @Override
        public JDataField getFieldForCell(final int row,
                                          final int column) {
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
         * Is the cell at (row, col) editable?
         * @param row the row
         * @param col the column
         * @return true/false
         */
        @Override
        public boolean isCellEditable(final int row,
                                      final int col) {
            /* Locked if the account is closed */
            if (theAccount.isClosed()) {
                return false;
            }

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
         * Get the value at (row, col).
         * @param row the row
         * @param col the column
         * @return the object value
         */
        @Override
        public Object getValueAt(final int row,
                                 final int col) {
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
            if ((o == null) && (myPrice.hasErrors(getFieldForCell(row, col)))) {
                o = Renderer.getError();
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
                    default:
                        break;
                }

                /* Handle Exceptions */
            } catch (Exception e) {
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
     * Prices mouse listener.
     */
    private final class PricesMouse extends DataMouse<AccountPrice> {
        /**
         * Constructor.
         */
        private PricesMouse() {
            /* Call super-constructor */
            super(theTable);
        }
    }

    /**
     * Column Model class.
     */
    private final class PricesColumnModel extends DataColumnModel {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -851990835577845594L;

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
         * Price Editor.
         */
        private final PriceEditor thePriceEditor;

        /**
         * Dilution column.
         */
        private final DataColumn theDiluteCol;

        /**
         * Diluted Price column.
         */
        private final DataColumn theDilPriceCol;

        /**
         * Do we have dilutions?
         */
        private boolean hasDilutions = true;

        /**
         * Constructor.
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
            addColumn(new DataColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            addColumn(new DataColumn(COLUMN_PRICE, WIDTH_PRICE, theDecimalRenderer, thePriceEditor));
            theDiluteCol = new DataColumn(COLUMN_DILUTION, WIDTH_DILUTION, theDecimalRenderer, null);
            theDilPriceCol = new DataColumn(COLUMN_DILUTEDPRICE, WIDTH_DILUTEDPRICE, theDecimalRenderer, null);
            addColumn(theDiluteCol);
            addColumn(theDilPriceCol);
        }

        /**
         * Set the date editor range.
         * @param pRange the range
         */
        private void setDateEditorRange(final DateDayRange pRange) {
            /* Set the range */
            theDateEditor.setRange(pRange);
        }

        /**
         * Set column selection for this view.
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

                /* else If we are showing dilutions */
            } else if (hasDilutions) {
                /* Remove the dilutions columns and record the fact */
                removeColumn(theDiluteCol);
                removeColumn(theDilPriceCol);
                hasDilutions = false;
            }
        }
    }
}
