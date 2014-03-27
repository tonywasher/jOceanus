/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.BooleanCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;

/**
 * Cash Table.
 */
public class CashTable
        extends JDataTable<Cash, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5070528756857524143L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(CashTable.class.getName());

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = Cash.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = Cash.FIELD_DESC.getName();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = Cash.FIELD_CATEGORY.getName();

    /**
     * Currency Column Title.
     */
    private static final String TITLE_CURRENCY = Cash.FIELD_CURRENCY.getName();

    /**
     * Closed Column Title.
     */
    private static final String TITLE_CLOSED = Cash.FIELD_CLOSED.getName();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = NLS_BUNDLE.getString("TitleActive");

    /**
     * LastTransaction Column Title.
     */
    private static final String TITLE_LASTTRAN = NLS_BUNDLE.getString("TitleLastTran");

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The updateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final transient UpdateEntry<Cash, MoneyWiseDataType> theCashEntry;

    /**
     * The analysis data entry.
     */
    private final transient JDataEntry theDataCashs;

    /**
     * The save buttons.
     */
    private final SaveButtons theSaveButtons;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The Column Model.
     */
    private final CashColumnModel theColumns;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * Cash.
     */
    private transient CashList theCash = null;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public CashTable(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView);
        theCashEntry = theUpdateSet.registerClass(Cash.class);
        setUpdateSet(theUpdateSet);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_MAINT);
        theDataCashs = myDataMgr.new JDataEntry(CashTable.class.getSimpleName());
        theDataCashs.addAsChildOf(mySection);
        theDataCashs.setObject(theUpdateSet);

        /* Create the save buttons */
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataCashs);

        /* Create the table model */
        CashTableModel myModel = new CashTableModel(this);
        setModel(myModel);

        /* Create the data column model and declare it */
        theColumns = new CashColumnModel(this);
        setColumnModel(theColumns);

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theError);
        thePanel.add(getScrollPane());
        thePanel.add(theSaveButtons);

        /* Create listener */
        CashListener myListener = new CashListener();
        theView.addChangeListener(myListener);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataCashs.setFocus();
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Get the Events edit list */
        MoneyWiseData myData = theView.getData();
        CashList myCash = myData.getCash();
        theCash = myCash.deriveEditList();
        setList(theCash);
        theCashEntry.setDataList(theCash);
        theSaveButtons.setEnabled(true);
        fireStateChanged();

        /* Touch the updateSet */
        theDataCashs.setObject(theUpdateSet);
    }

    @Override
    protected void setError(final JOceanusException pError) {
        theError.addError(pError);
    }

    @Override
    public boolean hasUpdates() {
        return theUpdateSet.hasUpdates();
    }

    @Override
    public boolean hasErrors() {
        return theUpdateSet.hasErrors();
    }

    /**
     * JTable Data Model.
     */
    private final class CashTableModel
            extends JDataTableModel<Cash, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -4222092905548422982L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CashTableModel(final CashTable pTable) {
            /* call constructor */
            super(pTable);
        }

        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                       ? 0
                                       : theColumns.getColumnCount();
        }

        @Override
        public int getRowCount() {
            return (theCash == null)
                                    ? 0
                                    : theCash.size();
        }

        @Override
        public JDataField getFieldForCell(final Cash pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Cash pItem,
                                      final int pColIndex) {
            return false;
        }

        @Override
        public Cash getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theCash.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final Cash pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final Cash pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return true;
        }
    }

    /**
     * Listener class.
     */
    private final class CashListener
            implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If this is the View */
            if (theView.equals(o)) {
                /* Refresh the data */
                refreshData();
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class CashColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -8625206224148826400L;

        /**
         * Name column id.
         */
        private static final int COLUMN_NAME = 0;

        /**
         * Category column id.
         */
        private static final int COLUMN_CATEGORY = 1;

        /**
         * Description column id.
         */
        private static final int COLUMN_DESC = 2;

        /**
         * Currency column id.
         */
        private static final int COLUMN_CURR = 3;

        /**
         * Closed column id.
         */
        private static final int COLUMN_CLOSED = 4;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 5;

        /**
         * LastTran column id.
         */
        private static final int COLUMN_LASTTRAN = 6;

        /**
         * Boolean Renderer.
         */
        private final BooleanCellRenderer theBooleanRenderer;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * Constructor.
         * @param pTable the table
         */
        private CashColumnModel(final CashTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theBooleanRenderer = theFieldMgr.allocateBooleanCellRenderer();
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Create the columns */
            addColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_CURR, WIDTH_NAME, theStringRenderer));
            addColumn(new JDataTableColumn(COLUMN_CLOSED, WIDTH_BOOL, theBooleanRenderer));
            addColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_BOOL, theBooleanRenderer));
            addColumn(new JDataTableColumn(COLUMN_LASTTRAN, WIDTH_DATE, theDateRenderer));
        }

        /**
         * Obtain column name.
         * @param pColIndex the column index
         * @return the column name
         */
        private String getColumnName(final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                    return TITLE_NAME;
                case COLUMN_DESC:
                    return TITLE_DESC;
                case COLUMN_CATEGORY:
                    return TITLE_CAT;
                case COLUMN_CURR:
                    return TITLE_CURRENCY;
                case COLUMN_CLOSED:
                    return TITLE_CLOSED;
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                case COLUMN_LASTTRAN:
                    return TITLE_LASTTRAN;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the Cash column.
         * @param pCash Cash
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final Cash pCash,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pCash.getName();
                case COLUMN_CATEGORY:
                    return pCash.getCategoryName();
                case COLUMN_DESC:
                    return pCash.getDesc();
                case COLUMN_CURR:
                    return pCash.getCashCurrencyName();
                case COLUMN_CLOSED:
                    return pCash.isClosed();
                case COLUMN_ACTIVE:
                    return pCash.isActive();
                case COLUMN_LASTTRAN:
                    Transaction myTran = pCash.getLatest();
                    return (myTran == null)
                                           ? null
                                           : myTran.getDate();
                default:
                    return null;
            }
        }

        /**
         * Obtain the field for the column index.
         * @param pColIndex column index
         * @return the field
         */
        protected JDataField getFieldForCell(final int pColIndex) {
            /* Switch on column */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return Cash.FIELD_NAME;
                case COLUMN_DESC:
                    return Cash.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return Cash.FIELD_CATEGORY;
                case COLUMN_CURR:
                    return Cash.FIELD_CURRENCY;
                case COLUMN_CLOSED:
                    return Cash.FIELD_CLOSED;
                case COLUMN_ACTIVE:
                    return Cash.FIELD_TOUCH;
                default:
                    return null;
            }
        }
    }
}
