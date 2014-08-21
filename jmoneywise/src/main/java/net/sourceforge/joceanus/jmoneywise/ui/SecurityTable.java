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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
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
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.IconButtonCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldCellRenderer.StringCellRenderer;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfo;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.dialog.SecurityPanel;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jmoneywise.views.ViewSecurityPrice;
import net.sourceforge.joceanus.jmoneywise.views.ViewSecurityPrice.ViewSecurityPriceList;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableColumn.JDataTableColumnModel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTableModel;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons.ActionType;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Security Table.
 */
public class SecurityTable
        extends JDataTable<Security, MoneyWiseDataType> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -9040660595889670213L;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(SecurityTable.class.getName());

    /**
     * Name Column Title.
     */
    private static final String TITLE_NAME = Security.FIELD_NAME.getName();

    /**
     * Description Column Title.
     */
    private static final String TITLE_DESC = Security.FIELD_DESC.getName();

    /**
     * Category Column Title.
     */
    private static final String TITLE_CAT = Security.FIELD_SECTYPE.getName();

    /**
     * Parent Column Title.
     */
    private static final String TITLE_PARENT = Security.FIELD_PARENT.getName();

    /**
     * Symbol Column Title.
     */
    private static final String TITLE_SYMBOL = Security.FIELD_SYMBOL.getName();

    /**
     * Currency Column Title.
     */
    private static final String TITLE_CURRENCY = Security.FIELD_CURRENCY.getName();

    /**
     * Closed Column Title.
     */
    private static final String TITLE_CLOSED = Security.FIELD_CLOSED.getName();

    /**
     * Active Column Title.
     */
    private static final String TITLE_ACTIVE = NLS_BUNDLE.getString("TitleActive");

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
    private final transient UpdateEntry<Security, MoneyWiseDataType> theSecurityEntry;

    /**
     * SecurityInfo Update Entry.
     */
    private final transient UpdateEntry<SecurityInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * SecurityPrice Update Entry.
     */
    private final transient UpdateEntry<ViewSecurityPrice, MoneyWiseDataType> thePriceEntry;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The Table Model.
     */
    private final SecurityTableModel theModel;

    /**
     * The Column Model.
     */
    private final SecurityColumnModel theColumns;

    /**
     * The panel.
     */
    private final JEnablePanel thePanel;

    /**
     * The Security dialog.
     */
    private final SecurityPanel theActiveAccount;

    /**
     * Securities.
     */
    private transient SecurityList theSecurities = null;

    /**
     * SecurityPrices.
     */
    private transient ViewSecurityPriceList thePrices = null;

    /**
     * Security types.
     */
    private transient SecurityTypeList theSecTypes = null;

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
    public SecurityTable(final View pView,
                         final UpdateSet<MoneyWiseDataType> pUpdateSet,
                         final ErrorPanel pError) {
        /* Record the passed details */
        theView = pView;
        theError = pError;
        theFieldMgr = theView.getFieldMgr();
        setFieldMgr(theFieldMgr);

        /* Build the Update set and entries */
        theUpdateSet = pUpdateSet;
        theSecurityEntry = theUpdateSet.registerType(MoneyWiseDataType.SECURITY);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.SECURITYINFO);
        thePriceEntry = theUpdateSet.registerType(MoneyWiseDataType.SECURITYPRICE);
        setUpdateSet(theUpdateSet);

        /* Create the table model */
        theModel = new SecurityTableModel(this);
        setModel(theModel);

        /* Create the data column model and declare it */
        theColumns = new SecurityColumnModel(this);
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
        theActiveAccount = new SecurityPanel(theView, theFieldMgr, theUpdateSet, theError);
        thePanel.add(theActiveAccount);

        /* Create listener */
        new SecurityListener();
    }

    /**
     * Determine Focus.
     * @param pEntry the master data entry
     */
    protected void determineFocus(final JDataEntry pEntry) {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        pEntry.setFocus(theSecurityEntry.getName());
    }

    /**
     * Refresh data.
     * @throws JOceanusException on error
     */
    public void refreshData() throws JOceanusException {
        /* Access the various lists */
        MoneyWiseData myData = theView.getData();
        theCurrencies = myData.getAccountCurrencies();
        theSecTypes = myData.getSecurityTypes();

        /* Obtain the security edit list */
        SecurityList mySecurities = myData.getSecurities();
        theSecurities = mySecurities.deriveEditList();
        theSecurities.resolveUpdateSetLinks(theUpdateSet);
        theSecurityEntry.setDataList(theSecurities);
        SecurityInfoList myInfo = theSecurities.getSecurityInfo();
        theInfoEntry.setDataList(myInfo);

        /* Get the Security prices list */
        thePrices = new ViewSecurityPriceList(theView);
        thePrices.resolveUpdateSetLinks(theUpdateSet);
        thePriceEntry.setDataList(thePrices);

        /* Notify panel of refresh */
        theActiveAccount.refreshData();

        /* Notify of the change */
        setList(theSecurities);
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
    public boolean hasSession() {
        return hasUpdates() || isItemEditing();
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
     * Select security.
     * @param pSecurity the security to select
     */
    protected void selectSecurity(final Security pSecurity) {
        /* Find the item in the list */
        int myIndex = theSecurities.indexOf(pSecurity);
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
    private final class SecurityTableModel
            extends JDataTableModel<Security, MoneyWiseDataType> {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = 5589152198291951338L;

        /**
         * Constructor.
         * @param pTable the table
         */
        private SecurityTableModel(final SecurityTable pTable) {
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
            return (theSecurities == null)
                                          ? 0
                                          : theSecurities.size();
        }

        @Override
        public JDataField getFieldForCell(final Security pItem,
                                          final int pColIndex) {
            return theColumns.getFieldForCell(pColIndex);
        }

        @Override
        public boolean isCellEditable(final Security pItem,
                                      final int pColIndex) {
            return theColumns.isCellEditable(pItem, pColIndex);
        }

        @Override
        public Security getItemAtIndex(final int pRowIndex) {
            /* Extract item from index */
            return theSecurities.get(pRowIndex);
        }

        @Override
        public Object getItemValue(final Security pItem,
                                   final int pColIndex) {
            /* Return the appropriate value */
            return theColumns.getItemValue(pItem, pColIndex);
        }

        @Override
        public void setItemValue(final Security pItem,
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
        public boolean includeRow(final Security pRow) {
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
    private final class SecurityListener
            implements ActionListener, ChangeListener, ListSelectionListener {
        /**
         * Constructor.
         */
        private SecurityListener() {
            /* Listen to correct events */
            theUpdateSet.addChangeListener(this);
            theActiveAccount.addChangeListener(this);
            theActiveAccount.addActionListener(this);

            /* Add selection listener */
            getSelectionModel().addListSelectionListener(this);
        }

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
        public void actionPerformed(final ActionEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* Handle actions */
            if ((theActiveAccount.equals(o))
                && (pEvent instanceof ActionDetailEvent)) {
                cascadeActionEvent((ActionDetailEvent) pEvent);
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
                    Security myAccount = theSecurities.get(iIndex);
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
    private final class SecurityColumnModel
            extends JDataTableColumnModel<MoneyWiseDataType> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -1820683409156751701L;

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
         * Symbol column id.
         */
        private static final int COLUMN_SYMBOL = 4;

        /**
         * Currency column id.
         */
        private static final int COLUMN_CURR = 5;

        /**
         * Closed column id.
         */
        private static final int COLUMN_CLOSED = 6;

        /**
         * Active column id.
         */
        private static final int COLUMN_ACTIVE = 7;

        /**
         * Closed Icon Renderer.
         */
        private final IconButtonCellRenderer<Boolean> theClosedIconRenderer;

        /**
         * Status Icon Renderer.
         */
        private final IconButtonCellRenderer<ActionType> theStatusIconRenderer;

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
        private final IconButtonCellEditor<ActionType> theStatusIconEditor;

        /**
         * SecurityType ScrollButton Menu Editor.
         */
        private final ScrollButtonCellEditor<SecurityType> theTypeEditor;

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
        private SecurityColumnModel(final SecurityTable pTable) {
            /* call constructor */
            super(pTable);

            /* Create the relevant formatters */
            theClosedIconEditor = theFieldMgr.allocateIconButtonCellEditor(Boolean.class, true);
            theStatusIconEditor = theFieldMgr.allocateIconButtonCellEditor(ActionType.class, false);
            theStringEditor = theFieldMgr.allocateStringCellEditor();
            theTypeEditor = theFieldMgr.allocateScrollButtonCellEditor(SecurityType.class);
            theParentEditor = theFieldMgr.allocateScrollButtonCellEditor(Payee.class);
            theCurrencyEditor = theFieldMgr.allocateScrollButtonCellEditor(AccountCurrency.class);
            theClosedIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theClosedIconEditor);
            theStatusIconRenderer = theFieldMgr.allocateIconButtonCellRenderer(theStatusIconEditor);
            theStringRenderer = theFieldMgr.allocateStringCellRenderer();

            /* Configure the iconButtons */
            MoneyWiseIcons.buildLockedButton(theClosedIconEditor.getComplexState());
            MoneyWiseIcons.buildStatusButton(theStatusIconEditor.getState());

            /* Create the columns */
            declareColumn(new JDataTableColumn(COLUMN_NAME, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_CATEGORY, WIDTH_NAME, theStringRenderer, theTypeEditor));
            declareColumn(new JDataTableColumn(COLUMN_DESC, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_PARENT, WIDTH_NAME, theStringRenderer, theParentEditor));
            declareColumn(new JDataTableColumn(COLUMN_SYMBOL, WIDTH_NAME, theStringRenderer, theStringEditor));
            declareColumn(new JDataTableColumn(COLUMN_CURR, WIDTH_CURR, theStringRenderer, theCurrencyEditor));
            theClosedColumn = new JDataTableColumn(COLUMN_CLOSED, WIDTH_ICON, theClosedIconRenderer, theClosedIconEditor);
            declareColumn(theClosedColumn);
            declareColumn(new JDataTableColumn(COLUMN_ACTIVE, WIDTH_ICON, theStatusIconRenderer, theStatusIconEditor));

            /* Initialise the columns */
            setColumns();

            /* Add listeners */
            ScrollEditorListener myListener = new ScrollEditorListener();
            theTypeEditor.addChangeListener(myListener);
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
                case COLUMN_ACTIVE:
                    return TITLE_ACTIVE;
                case COLUMN_PARENT:
                    return TITLE_PARENT;
                case COLUMN_SYMBOL:
                    return TITLE_SYMBOL;
                case COLUMN_CURR:
                    return TITLE_CURRENCY;
                case COLUMN_CLOSED:
                    return TITLE_CLOSED;
                default:
                    return null;
            }
        }

        /**
         * Obtain the value for the security column.
         * @param pSecurity security
         * @param pColIndex column index
         * @return the value
         */
        protected Object getItemValue(final Security pSecurity,
                                      final int pColIndex) {
            /* Return the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    return pSecurity.getName();
                case COLUMN_CATEGORY:
                    return pSecurity.getSecurityType();
                case COLUMN_DESC:
                    return pSecurity.getDesc();
                case COLUMN_CLOSED:
                    return pSecurity.isClosed();
                case COLUMN_ACTIVE:
                    return pSecurity.isActive()
                                               ? ActionType.ACTIVE
                                               : ActionType.DELETE;
                case COLUMN_SYMBOL:
                    return pSecurity.getSymbol();
                case COLUMN_PARENT:
                    return pSecurity.getParent();
                case COLUMN_CURR:
                    return pSecurity.getSecurityCurrency();
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
        private void setItemValue(final Security pItem,
                                  final int pColIndex,
                                  final Object pValue) throws JOceanusException {
            /* Set the appropriate value */
            switch (pColIndex) {
                case COLUMN_NAME:
                    pItem.setName((String) pValue);
                    break;
                case COLUMN_CATEGORY:
                    pItem.setSecurityType((SecurityType) pValue);
                    break;
                case COLUMN_PARENT:
                    pItem.setParent((Payee) pValue);
                    break;
                case COLUMN_DESC:
                    pItem.setDescription((String) pValue);
                    break;
                case COLUMN_SYMBOL:
                    pItem.setSymbol((String) pValue);
                    break;
                case COLUMN_CURR:
                    pItem.setSecurityCurrency((AccountCurrency) pValue);
                    break;
                case COLUMN_CLOSED:
                    pItem.setClosed((Boolean) pValue);
                    break;
                case COLUMN_ACTIVE:
                    pItem.setDeleted(true);
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
        private boolean isCellEditable(final Security pItem,
                                       final int pColIndex) {
            switch (pColIndex) {
                case COLUMN_NAME:
                case COLUMN_DESC:
                    return true;
                case COLUMN_SYMBOL:
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
                    return Security.FIELD_NAME;
                case COLUMN_DESC:
                    return Security.FIELD_DESC;
                case COLUMN_CATEGORY:
                    return Security.FIELD_SECTYPE;
                case COLUMN_CLOSED:
                    return Security.FIELD_CLOSED;
                case COLUMN_ACTIVE:
                    return Security.FIELD_TOUCH;
                case COLUMN_SYMBOL:
                    return Security.FIELD_SYMBOL;
                case COLUMN_PARENT:
                    return Security.FIELD_PARENT;
                case COLUMN_CURR:
                    return Security.FIELD_CURRENCY;
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

                if (theTypeEditor.equals(o)) {
                    buildSecurityTypeMenu();
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
                Security mySecurity = theSecurities.get(myCell.y);
                Payee myCurr = mySecurity.getParent();
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
             * Build the popUpMenu for securityType.
             */
            private void buildSecurityTypeMenu() {
                /* Access details */
                JScrollMenuBuilder<SecurityType> myBuilder = theTypeEditor.getMenuBuilder();
                Point myCell = theTypeEditor.getPoint();
                myBuilder.clearMenu();

                /* Record active item */
                Security mySecurity = theSecurities.get(myCell.y);
                SecurityType myCurr = mySecurity.getSecurityType();
                JMenuItem myActive = null;

                /* Loop through the SecurityTypes */
                Iterator<SecurityType> myIterator = theSecTypes.iterator();
                while (myIterator.hasNext()) {
                    SecurityType myType = myIterator.next();

                    /* Ignore deleted or disabled */
                    boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
                    if (bIgnore) {
                        continue;
                    }

                    /* Create a new action for the securityType */
                    JMenuItem myItem = myBuilder.addItem(myType);

                    /* If this is the active type */
                    if (myType.equals(myCurr)) {
                        /* Record it */
                        myActive = myItem;
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
                Security mySecurity = theSecurities.get(myCell.y);
                AccountCurrency myCurr = mySecurity.getSecurityCurrency();
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
