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
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

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
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.CashInfo;
import net.sourceforge.joceanus.jmoneywise.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.CashPanel;
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
     * CashInfo Update Entry.
     */
    private final transient UpdateEntry<CashInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The Table Model.
     */
    private final CashTableModel theModel;

    /**
     * The Column Model.
     */
    private final CashColumnModel theColumns;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The Cash dialog.
     */
    private final CashPanel theActiveAccount;

    /**
     * Cash.
     */
    private transient CashList theCash = null;

    /**
     * Categories.
     */
    private transient CashCategoryList theCategories = null;

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
     * Are we in the middle of an item edit?
     * @return true/false
     */
    protected boolean isItemEditing() {
        return theActiveAccount.isEditing();
    }

    /**
     * Constructor.
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public CashTable(final View pView,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final ErrorPanel pError) {
        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Create listener */
        CashListener myListener = new CashListener();

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theCashEntry = theUpdateSet.registerType(MoneyWiseDataType.CASH);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.CASHINFO);
        setUpdateSet(theUpdateSet);
        theUpdateSet.addChangeListener(myListener);

        /* Create the table model */
        theModel = new CashTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new CashColumnModel(this);
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

        /* Create an account panel */
        theActiveAccount = new CashPanel(theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveAccount);
        theActiveAccount.addChangeListener(myListener);

        /* Add selection listener */
        getSelectionModel().addListSelectionListener(myListener);
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final JDataEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theCashEntry.getName());
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Access the various lists */
        MoneyWiseData myData = theView.getData();
        theCurrencies = myData.getAccountCurrencies();
        theCategories = myData.getCashCategories();

        /* Get the Cash edit list */
        CashList myCash = myData.getCash();
        theCash = myCash.deriveEditList();
        theCashEntry.setDataList(theCash);
        CashInfoList myInfo = theCash.getCashInfo();
        theInfoEntry.setDataList(myInfo);

        /* Notify panel of refresh */
        theActiveAccount.refreshData();

        /* Notify of the change */
        setList(theCash);
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

    @Override
    public void cancelEditing() {
        /* Cancel editing on table */
        super.cancelEditing();

        /* Stop editing any item */
        theActiveAccount.setEditable(false);
    }

    /**
     * Select cash.
     * @param pCash the cash to select
     */
    protected void selectCash(final Cash pCash) {
        /* Find the item in the list */
        int myIndex = theCash.indexOf(pCash);
        myIndex = convertRowIndexToView(myIndex);
        if (myIndex != -1) {
            /* Select the row and ensure that it is visible */
            selectRowWithScroll(myIndex);
        }
    }

    @Override
    protected void notifyChanges() {
        /* Adjust enable of the table */
        setEnabled(!theActiveAccount.isEditing());

        /* Pass call on */
        super.notifyChanges();
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
                                       : theColumns.getDeclaredCount();
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
            return theColumns.isCellEditable(pItem, pColIndex);
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
        public void setItemValue(final Cash pItem,
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
        public boolean includeRow(final Cash pRow) {
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
    private final class CashListener
            implements ChangeListener, ListSelectionListener {

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If we are performing a rewind */
            if (theUpdateSet.equals(o)) {
                /* Only action if we are not editing */
                if (!theActiveAccount.isEditing()) {
                    /* Refresh the model */
                    theModel.fireNewDataEvents();
                }

                /* Adjust for changes */
                notifyChanges();
            }

            /* If we are noting change of edit state */
            if (theActiveAccount.equals(o)) {
                /* If the account is now deleted */
                if (theActiveAccount.isItemDeleted()) {
                    /* Refresh the model */
                    theModel.fireNewDataEvents();
                }

                /* Note changes */
                notifyChanges();
            }
        }

        @Override
        public void valueChanged(final ListSelectionEvent pEvent) {
            /* If we have finished selecting */
            if (!pEvent.getValueIsAdjusting()) {
                /* Access selection model */
                ListSelectionModel myModel = getSelectionModel();
                if (!myModel.isSelectionEmpty()) {
                    /* Loop through the indices */
                    int iIndex = myModel.getMinSelectionIndex();
                    iIndex = convertRowIndexToModel(iIndex);
                    Cash myAccount = theCash.get(iIndex);
                    theActiveAccount.setItem(myAccount);
                } else {
                    theActiveAccount.setEditable(false);
                    theActiveAccount.setItem(null);
                    notifyChanges();
                }
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
        private final ScrollButtonCellEditor<CashCategory> theCategoryEditor;

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
        private CashColumnModel(final CashTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theClosedIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theCategoryEditor = theFieldMgr.allocateScrollButtonCellEditor(CashCategory.class);
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
                    return pCash.getCategory();
                case COLUMN_DESC:
                    return pCash.getDesc();
                case COLUMN_CURR:
                    return pCash.getCashCurrency();
                case COLUMN_CLOSED:
                    return pCash.isClosed();
                case COLUMN_ACTIVE:
                    pCash.isActive();
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
         * Set the value for the item column.
         * @param pItem the item
         * @param pColIndex column index
         * @param pValue the value to set
         * @throws JOceanusException on error
         */
        private void setItemValue(final Cash pItem,
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
                    pItem.setCashCategory((CashCategory) pValue);
                    break;
                case COLUMN_CURR:
                    pItem.setCashCurrency((AccountCurrency) pValue);
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
        private boolean isCellEditable(final Cash pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_CATEGORY:
                case COLUMN_CURR:
                case COLUMN_ACTIVE:
                    return !pItem.isActive();
                case COLUMN_CLOSED:
                    return pItem.isClosed() || !pItem.isRelevant();
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
                } else if (theCurrencyEditor.equals(o)) {
                    buildCurrencyMenu();
                }
            }

            /**
             * Obtain the popUpMenu for categories.
             */
            private void buildCategoryMenu() {
                /* Access details */
                JScrollMenuBuilder<CashCategory> myBuilder = theCategoryEditor.getMenuBuilder();
                Point myCell = theCategoryEditor.getPoint();
                myBuilder.clearMenu();

                /* Access active category */
                JMenuItem myActive = null;
                Cash myCash = theCash.get(myCell.y);
                CashCategory myActiveCat = (myCash == null)
                                                           ? null
                                                           : myCash.getCategory();

                /* Create a simple map for top-level categories */
                Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

                /* Loop through the available category values */
                Iterator<CashCategory> myIterator = theCategories.iterator();
                while (myIterator.hasNext()) {
                    CashCategory myCategory = myIterator.next();

                    /* Only process parent items */
                    if (!myCategory.isCategoryClass(CashCategoryClass.PARENT)) {
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
                    CashCategory myCategory = myIterator.next();

                    /* Only process low-level items */
                    if (myCategory.isCategoryClass(CashCategoryClass.PARENT)) {
                        continue;
                    }

                    /* Determine menu to add to */
                    CashCategory myParent = myCategory.getParentCategory();
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
                Cash myCash = theCash.get(myCell.y);
                AccountCurrency myCurr = myCash.getCashCurrency();
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
