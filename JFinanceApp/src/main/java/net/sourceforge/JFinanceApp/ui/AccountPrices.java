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

import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataModels.ui.Editor.CalendarEditor;
import net.sourceforge.JDataModels.ui.Editor.PriceEditor;
import net.sourceforge.JDataModels.ui.ErrorPanel;
import net.sourceforge.JDataModels.ui.JDataTable;
import net.sourceforge.JDataModels.ui.JDataTableColumn;
import net.sourceforge.JDataModels.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.JDataModels.ui.JDataTableModel;
import net.sourceforge.JDataModels.ui.JDataTableMouse;
import net.sourceforge.JDataModels.ui.RenderManager;
import net.sourceforge.JDataModels.ui.Renderer.CalendarRenderer;
import net.sourceforge.JDataModels.ui.Renderer.DecimalRenderer;
import net.sourceforge.JDataModels.views.UpdateSet;
import net.sourceforge.JDataModels.views.UpdateSet.UpdateEntry;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.AccountPrice;
import net.sourceforge.JFinanceApp.views.View;
import net.sourceforge.JFinanceApp.views.ViewPrice;
import net.sourceforge.JFinanceApp.views.ViewPrice.ViewPriceList;

/**
 * Account Prices Table.
 * @author Tony Washer
 */
public class AccountPrices extends JDataTable<ViewPrice> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 1035380774297559650L;

    /**
     * Date View.
     */
    private final transient View theView;

    /**
     * The render manager.
     */
    private final transient RenderManager theRenderMgr;

    /**
     * Price List.
     */
    private transient ViewPriceList thePrices = null;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * DataSet range.
     */
    private transient DateDayRange theRange = null;

    /**
     * Account.
     */
    private transient Account theAccount = null;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * UpdateEntry Class.
     */
    private final transient UpdateEntry theUpdateEntry;

    /**
     * Self Reference.
     */
    private final AccountPrices theTable = this;

    /**
     * Column Model.
     */
    private final PricesColumnModel theColumns;

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
    protected void setError(final JDataException pError) {
        theError.setError(pError);
    }

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountPrices.class.getName());

    /**
     * Date column title.
     */
    private static final String TITLE_DATE = NLS_BUNDLE.getString("TitleDate");

    /**
     * Price column title.
     */
    private static final String TITLE_PRICE = NLS_BUNDLE.getString("TitlePrice");

    /**
     * Dilution column title.
     */
    private static final String TITLE_DILUTION = NLS_BUNDLE.getString("TitleDilution");

    /**
     * Diluted price column title.
     */
    private static final String TITLE_DILUTEDPRICE = NLS_BUNDLE.getString("TitleDilutedPrice");

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
     * @param pView the view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public AccountPrices(final View pView,
                         final UpdateSet pUpdateSet,
                         final ErrorPanel pError) {
        /* Store details */
        theView = pView;
        theRenderMgr = theView.getRenderMgr();
        setRenderMgr(theRenderMgr);
        theError = pError;
        theUpdateSet = pUpdateSet;
        theUpdateEntry = theUpdateSet.registerClass(ViewPrice.class);
        setUpdateSet(theUpdateSet);

        /* Create the model and declare it to our superclass */
        PricesModel myModel = new PricesModel();
        setModel(myModel);

        /* Create the data column model and declare it */
        theColumns = new PricesColumnModel();
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_OFF);

        /* Add the mouse listener */
        PricesMouse myMouse = new PricesMouse(this);
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
        theRange = theView.getRange();
        theColumns.setDateEditorRange(theRange);
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
        thePrices = null;

        /* If we have an account */
        if (theAccount != null) {
            /* Obtain the Prices extract */
            thePrices = new ViewPriceList(theView, pAccount);
        }

        /* Declare the list */
        theColumns.setColumnSelection();
        super.setList(thePrices);
        theUpdateEntry.setDataList(thePrices);
    }

    /**
     * Check whether a row is deletable.
     * @param pRow the row
     * @return is the row deletable
     */
    @Override
    protected boolean isRowDeletable(final ViewPrice pRow) {
        /* If the row is not deleted, we can delete if the list size is greater than one */
        return ((!pRow.isDeleted()) && (thePrices.size() > 1));
    }

    /**
     * Check whether we duplicate a row.
     * @param pRow the row
     * @return false
     */
    @Override
    protected boolean isRowDuplicatable(final ViewPrice pRow) {
        return false;
    }

    /**
     * Prices table model.
     */
    public final class PricesModel extends JDataTableModel<ViewPrice> {
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

        @Override
        public ViewPrice getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return thePrices.get(pRowIndex);
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

        @Override
        public String getColumnName(final int pColIndex) {
            switch (pColIndex) {
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

        @Override
        public JDataField getFieldForCell(final ViewPrice pPrice,
                                          final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return AccountPrice.FIELD_DATE;
                case COLUMN_PRICE:
                    return AccountPrice.FIELD_PRICE;
                default:
                    return null;
            }
        }

        @Override
        public boolean isCellEditable(final ViewPrice pPrice,
                                      final int pColIndex) {
            /* Locked if the account is closed */
            if (theAccount.isClosed()) {
                return false;
            }

            switch (pColIndex) {
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

        @Override
        public Object getItemValue(final ViewPrice pPrice,
                                   final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    return pPrice.getDate();
                case COLUMN_PRICE:
                    return pPrice.getPrice();
                case COLUMN_DILUTION:
                    return pPrice.getDilution();
                case COLUMN_DILUTEDPRICE:
                    return pPrice.getDilutedPrice();
                default:
                    return null;
            }
        }

        @Override
        public void setItemValue(final ViewPrice pPrice,
                                 final int pColIndex,
                                 final Object pValue) throws JDataException {
            /* Store the appropriate value */
            switch (pColIndex) {
                case COLUMN_DATE:
                    pPrice.setDate((DateDay) pValue);
                    break;
                case COLUMN_PRICE:
                    pPrice.setPrice((Price) pValue);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Prices mouse listener.
     */
    private static final class PricesMouse extends JDataTableMouse<ViewPrice> {
        /**
         * Constructor.
         * @param pTable the table
         */
        private PricesMouse(final AccountPrices pTable) {
            /* Call super-constructor */
            super(pTable);
        }
    }

    /**
     * Column Model class.
     */
    private final class PricesColumnModel extends JDataTableColumnModel {
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
        private final JDataTableColumn theDiluteCol;

        /**
         * Diluted Price column.
         */
        private final JDataTableColumn theDilPriceCol;

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
            theDateRenderer = theRenderMgr.allocateCalendarRenderer();
            theDateEditor = new CalendarEditor();
            theDecimalRenderer = theRenderMgr.allocateDecimalRenderer();
            thePriceEditor = new PriceEditor();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_DATE, WIDTH_DATE, theDateRenderer, theDateEditor));
            addColumn(new JDataTableColumn(COLUMN_PRICE, WIDTH_PRICE, theDecimalRenderer, thePriceEditor));
            theDiluteCol = new JDataTableColumn(COLUMN_DILUTION, WIDTH_DILUTION, theDecimalRenderer, null);
            theDilPriceCol = new JDataTableColumn(COLUMN_DILUTEDPRICE, WIDTH_DILUTEDPRICE,
                    theDecimalRenderer, null);
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
