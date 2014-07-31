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

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.IconButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.ScrollButtonCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellEditor.StringCellEditor;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.CalendarCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;

/**
 * Loan Table.
 */
public class LoanTable
        extends JDataTable<Loan, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7033868298272052342L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(LoanTable.class.getName());

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = Loan.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = Loan.FIELD_DESC.getName();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = Loan.FIELD_CATEGORY.getName();

    /**
     * Parent Column Title.
     */
    private static final String TITLE_PARENT = Loan.FIELD_PARENT.getName();

    /**
     * Currency Column Title.
     */
    private static final String TITLE_CURRENCY = Loan.FIELD_CURRENCY.getName();

    /**
     * Closed Column Title.
     */
    private static final String TITLE_CLOSED = Loan.FIELD_CLOSED.getName();

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
    private final transient UpdateEntry<Loan, MoneyWiseDataType> theLoanEntry;

    /**
     * LoanInfo Update Entry.
     */
    private final transient UpdateEntry<LoanInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The Table Model.
     */
    private final LoanTableModel theModel;

    /**
     * The Column Model.
     */
    private final LoanColumnModel theColumns;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * Loans.
     */
    private transient LoanList theLoans = null;

    /**
     * Categories.
     */
    private transient LoanCategoryList theCategories = null;

    /**
     * Currencies.
     */
    private transient AccountCurrencyList theCurrencies = null;

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
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public LoanTable(final View pView,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final ErrorPanel pError) {
        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Create listener */
        LoanListener myListener = new LoanListener();

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theLoanEntry = theUpdateSet.registerType(MoneyWiseDataType.LOAN);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.LOANINFO);
        setUpdateSet(theUpdateSet);
        theUpdateSet.addChangeListener(myListener);

        /* Create the table model */
        theModel = new LoanTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new LoanColumnModel(this);
        setColumnModel(theColumns);

        /* Prevent reordering of columns and auto-resizing */
        getTableHeader().setReorderingAllowed(false);
        setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

        /* Set the number of visible rows */
        setPreferredScrollableViewportSize(new Dimension(WIDTH_PANEL, HEIGHT_PANEL));

        /* Create the layout for the panel */
        thePanel = new JEnablePanel();
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
        pEntry.setFocus(theLoanEntry.getName());
    }

    /**
     * Refresh data.
     * @throws JOceanusException on error
     */
    public void refreshData() throws JOceanusException {
        /* Access the various lists */
        MoneyWiseData myData = theView.getData();
        theCurrencies = myData.getAccountCurrencies();
        theCategories = myData.getLoanCategories();

        /* Obtain the loan edit list */
        LoanList myLoans = myData.getLoans();
        theLoans = myLoans.deriveEditList();
        theLoans.resolveUpdateSetLinks(theUpdateSet);
        theLoanEntry.setDataList(theLoans);
        LoanInfoList myInfo = theLoans.getLoanInfo();
        theInfoEntry.setDataList(myInfo);
        setList(theLoans);
        fireStateChanged();
    }

    @Override
    public void setShowAll(final boolean pShow) {
        super.setShowAll(pShow);
        theColumns.setColumns();
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
     * Select loan.
     * @param pLoan the loan to select
     */
    protected void selectLoan(final Loan pLoan) {
        /* Find the item in the list */
        int myIndex = theLoans.indexOf(pLoan);
        myIndex = convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    /**
     * JTable Data Model.
     */
    private final class LoanTableModel
            extends JDataTableModel<Loan, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -3418228782485601783L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private LoanTableModel(final LoanTable pTable) {
            /* call constructor */
            super(pTable);
        }

        @Override
        public int getColumnCount() {
            return (theColumns == null)
                                       ? 0
                                       : theColumns.getDeclaredCount();
        }

        @Override
        public int getRowCount() {
            return (theLoans == null)
                                     ? 0
                                     : theLoans.size();
        }

        @Override
        public JDataField getFieldForCell(final Loan pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Loan pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Loan getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theLoans.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final Loan pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final Loan pItem,
                                 final int pColIndex,
                                 final Object pValue) throws JOceanusException {
            /* Set the item value for the column */
            theColumns.setItemValue(pItem, pColIndex, pValue);
        }

        @Override
        public String getColumnName(final int pColIndex) {
            /* Obtain the column name */
            return theColumns.getColumnName(pColIndex);
        }

        @Override
        public boolean includeRow(final Loan pRow) {
            /* Ignore deleted rows */
            if (pRow.isDeleted()) {
                return false;
            }

            /* Handle filter */
            return showAll() || !pRow.isDisabled();
        }
    }

    /**
     * Listener class.
     */
    private final class LoanListener
            implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Refresh the model */
                theModel.fireNewDataEvents();
            }
        }
    }

    /**
     * Column Model class.
     */
    private final class LoanColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -4570479051836834291L;

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
         * Parent column id.
         */
        private static final int COLUMN_PARENT = 3;

        /**
         * Currency column id.
         */
        private static final int COLUMN_CURR = 4;

        /**
         * Closed column id.
         */
        private static final int COLUMN_CLOSED = 5;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 6;

        /**
         * LastTran column id.
         */
        private static final int COLUMN_LASTTRAN = 7;

        /**
         * Closed Icon Renderer.
         */
        private final IconButtonCellRenderer<Boolean> theClosedIconRenderer;

        /**
         * Status Icon Renderer.
         */
        private final IconButtonCellRenderer<Boolean> theStatusIconRenderer;

        /**
         * Date Renderer.
         */
        private final CalendarCellRenderer theDateRenderer;

        /**
         * String Renderer.
         */
        private final StringCellRenderer theStringRenderer;

        /**
         * String editor.
         */
        private final StringCellEditor theStringEditor;

        /**
         * Closed Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theClosedIconEditor;

        /**
         * Status Icon editor.
         */
        private final IconButtonCellEditor<Boolean> theStatusIconEditor;

        /**
         * Category ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<LoanCategory> theCategoryEditor;

        /**
         * Parent ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<Payee> theParentEditor;

        /**
         * Currency ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<AccountCurrency> theCurrencyEditor;

        /**
         * Closed column.
         */
        private final JDataTableColumn theClosedColumn;

        /**
         * Constructor.
         * @param pTable the table
         */
        private LoanColumnModel(final LoanTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theClosedIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theCategoryEditor = theFieldMgr.allocateScrollButtonCellEditor(LoanCategory.class);
            theParentEditor = theFieldMgr.allocateScrollButtonCellEditor(Payee.class);
            theCurrencyEditor = theFieldMgr.allocateScrollButtonCellEditor(AccountCurrency.class);
            theClosedIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theClosedIconEditor);
            theStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theStatusIconEditor);
            theDateRenderer = theFieldMgr.allocateCalendarCellRenderer();
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButtons */
            MoneyWiseIcons.buildLockedButton(theClosedIconEditor.getComplexState());
            MoneyWiseIcons.buildStatusButton(theStatusIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer, theCategoryEditor));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_PARENT, WIDTH_NAME, theStringRenderer, theParentEditor));
            declareColumn(new JDataTableColumn(COLUMN_CURR, WIDTH_CURR, theStringRenderer, theCurrencyEditor));
            theClosedColumn = new JDataTableColumn(COLUMN_CLOSED, WIDTH_ICON, theClosedIconRenderer, theClosedIconEditor);
            declareColumn(theClosedColumn);
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theStatusIconRenderer, theStatusIconEditor));
            declareColumn(new JDataTableColumn(COLUMN_LASTTRAN, WIDTH_DATE, theDateRenderer));

            /* Initialise the columns */
            setColumns();

            /* Add listeners */
            ScrollEditorListener myListener = new ScrollEditorListener();
            theCategoryEditor.addChangeListener(myListener);
            theParentEditor.addChangeListener(myListener);
            theCurrencyEditor.addChangeListener(myListener);
        }

        /**
         * Set visible columns according to the mode.
         */
        private void setColumns() {
            /* Switch on mode */
            if (showAll()) {
                revealColumn(theClosedColumn);
            } else {
                hideColumn(theClosedColumn);
            }
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
                case COLUMN_PARENT:
                    return TITLE_PARENT;
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
         * Obtain the value for the Loan column.
         * @param pLoan Loan
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final Loan pLoan,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pLoan.getName();
                case COLUMN_CATEGORY:
                    return pLoan.getCategory();
                case COLUMN_DESC:
                    return pLoan.getDesc();
                case COLUMN_PARENT:
                    return pLoan.getParent();
                case COLUMN_CURR:
                    return pLoan.getLoanCurrency();
                case COLUMN_CLOSED:
                    return pLoan.isClosed();
                case COLUMN_ACTIVE:
                    return pLoan.isActive();
                case COLUMN_LASTTRAN:
                    Transaction myTran = pLoan.getLatest();
                    return (myTran == null)
                                           ? null
                                           : myTran.getDate();
                default:
                    return null;
            }
        }

        /**
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws JOceanusException on error
         */
        private void setItemValue(final Loan pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pItem.setLoanCategory((LoanCategory) pValue);
                    break;
                case COLUMN_PARENT:
                    pItem.setParent((Payee) pValue);
                    break;
                case COLUMN_CURR:
                    pItem.setLoanCurrency((AccountCurrency) pValue);
                    break;
                case COLUMN_CLOSED:
                    pItem.setClosed((Boolean) pValue);
                    break;
                case COLUMN_ACTIVE:
                    deleteRow(pItem);
                    break;
                default:
                    break;
            }
        }

        /**
         * Is the cell editable?
         * @param pItem the item
         * @param pColIndex the column index
         * @return true/false
         */
        private boolean isCellEditable(final Loan pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_PARENT:
                    return !pItem.isClosed();
                case COLUMN_CATEGORY:
                case COLUMN_CURR:
                case COLUMN_ACTIVE:
                    return !pItem.isActive();
                case COLUMN_CLOSED:
                    return pItem.isClosed()
                                           ? !pItem.getParent().isClosed()
                                           : !pItem.isRelevant();
                default:
                    return false;
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
                    return Loan.FIELD_NAME;
                case COLUMN_DESC:
                    return Loan.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return Loan.FIELD_CATEGORY;
                case COLUMN_PARENT:
                    return Loan.FIELD_PARENT;
                case COLUMN_CURR:
                    return Loan.FIELD_CURRENCY;
                case COLUMN_CLOSED:
                    return Loan.FIELD_CLOSED;
                case COLUMN_ACTIVE:
                    return Loan.FIELD_TOUCH;
                default:
                    return null;
            }
        }

        /**
         * ScrollEditorListener.
         */
        private class ScrollEditorListener
                implements ChangeListener {
            @Override
            public void stateChanged(final ChangeEvent pEvent) {
                Object o = pEvent.getSource();

                if (theCategoryEditor.equals(o)) {
                    buildCategoryMenu();
                } else if (theParentEditor.equals(o)) {
                    buildParentMenu();
                } else if (theCurrencyEditor.equals(o)) {
                    buildCurrencyMenu();
                }
            }

            /**
             * Build the popUpMenu for parents.
             */
            private void buildParentMenu() {
                /* Access details */
                JScrollMenuBuilder<Payee> myBuilder = theParentEditor.getMenuBuilder();
                Point myCell = theParentEditor.getPoint();
                myBuilder.clearMenu();

                /* Record active item */
                Loan myLoan = theLoans.get(myCell.y);
                Payee myCurr = myLoan.getParent();
                JMenuItem myActive = null;

                /* We should use the update payee list */
                PayeeList myPayees = theUpdateSet.findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

                /* Loop through the Payees */
                Iterator<Payee> myIterator = myPayees.iterator();
                while (myIterator.hasNext()) {
                    Payee myPayee = myIterator.next();

                    /* Ignore deleted/non-parent/closed */
                    boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentAccount();
                    bIgnore |= myPayee.isClosed();
                    if (bIgnore) {
                        continue;
                    }

                    /* Create a new action for the type */
                    JMenuItem myItem = myBuilder.addItem(myPayee);

                    /* If this is the active parent */
                    if (myPayee.equals(myCurr)) {
                        /* Record it */
                        myActive = myItem;
                    }
                }

                /* Ensure active item is visible */
                myBuilder.showItem(myActive);
            }

            /**
             * Obtain the popUpMenu for categories.
             */
            private void buildCategoryMenu() {
                /* Access details */
                JScrollMenuBuilder<LoanCategory> myBuilder = theCategoryEditor.getMenuBuilder();
                Point myCell = theCategoryEditor.getPoint();
                myBuilder.clearMenu();

                /* Access active category */
                JMenuItem myActive = null;
                Loan myLoan = theLoans.get(myCell.y);
                LoanCategory myActiveCat = (myLoan == null)
                                                           ? null
                                                           : myLoan.getCategory();

                /* Create a simple map for top-level categories */
                Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

                /* Loop through the available category values */
                Iterator<LoanCategory> myIterator = theCategories.iterator();
                while (myIterator.hasNext()) {
                    LoanCategory myCategory = myIterator.next();

                    /* Only process parent items */
                    if (!myCategory.isCategoryClass(LoanCategoryClass.PARENT)) {
                        continue;
                    }

                    /* Create a new JMenu and add it to the popUp */
                    String myName = myCategory.getName();
                    JScrollMenu myMenu = myBuilder.addSubMenu(myName);
                    myMap.put(myName, myMenu);
                }

                /* Re-Loop through the available category values */
                myIterator = theCategories.iterator();
                while (myIterator.hasNext()) {
                    LoanCategory myCategory = myIterator.next();

                    /* Only process low-level items */
                    if (myCategory.isCategoryClass(LoanCategoryClass.PARENT)) {
                        continue;
                    }

                    /* Determine menu to add to */
                    LoanCategory myParent = myCategory.getParentCategory();
                    JScrollMenu myMenu = myMap.get(myParent.getName());

                    /* Create a new JMenuItem and add it to the popUp */
                    JMenuItem myItem = myBuilder.addItem(myMenu, myCategory);

                    /* Note active category */
                    if (myCategory.equals(myActiveCat)) {
                        myActive = myMenu;
                        myMenu.showItem(myItem);
                    }
                }

                /* Ensure active item is visible */
                myBuilder.showItem(myActive);
            }

            /**
             * Build the popUpMenu for currencies.
             */
            private void buildCurrencyMenu() {
                /* Access details */
                JScrollMenuBuilder<AccountCurrency> myBuilder = theCurrencyEditor.getMenuBuilder();
                Point myCell = theCurrencyEditor.getPoint();
                myBuilder.clearMenu();

                /* Record active item */
                Loan myLoan = theLoans.get(myCell.y);
                AccountCurrency myCurr = myLoan.getLoanCurrency();
                JMenuItem myActive = null;

                /* Loop through the Currencies */
                Iterator<AccountCurrency> myIterator = theCurrencies.iterator();
                while (myIterator.hasNext()) {
                    AccountCurrency myCurrency = myIterator.next();

                    /* Ignore deleted or disabled */
                    boolean bIgnore = myCurrency.isDeleted() || !myCurrency.getEnabled();
                    if (bIgnore) {
                        continue;
                    }

                    /* Create a new action for the currency */
                    JMenuItem myItem = myBuilder.addItem(myCurrency);

                    /* If this is the active currency */
                    if (myCurrency.equals(myCurr)) {
                        /* Record it */
                        myActive = myItem;
                    }
                }

                /* Ensure active item is visible */
                myBuilder.showItem(myActive);
            }
        }
    }
}
