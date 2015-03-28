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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.data.DataType;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.CashInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Cash.
 */
public class CashPanel
        extends MoneyWiseDataItemPanel<Cash> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1242762723020329985L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Cash> theFieldSet;

    /**
     * CashCategory Button Field.
     */
    private final JScrollButton<CashCategory> theCategoryButton;

    /**
     * Currency Button Field.
     */
    private final JScrollButton<AssetCurrency> theCurrencyButton;

    /**
     * AutoExpense Button Field.
     */
    private final JScrollButton<TransactionCategory> theAutoExpenseButton;

    /**
     * AutoPayee Button Field.
     */
    private final JScrollButton<Payee> theAutoPayeeButton;

    /**
     * Closed Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public CashPanel(final JFieldManager pFieldMgr,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the buttons */
        theCategoryButton = new JScrollButton<CashCategory>();
        theCurrencyButton = new JScrollButton<AssetCurrency>();
        theAutoExpenseButton = new JScrollButton<TransactionCategory>();
        theAutoPayeeButton = new JScrollButton<Payee>();

        /* Set closed button */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel();
        myTabs.add(TAB_DETAILS, myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel();
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new CashListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Set states */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildLockedButton(theClosedState);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();

        /* restrict the fields */
        restrictField(myName, Cash.NAMELEN);
        restrictField(myDesc, Cash.NAMELEN);
        restrictField(theCategoryButton, Cash.NAMELEN);
        restrictField(theCurrencyButton, Cash.NAMELEN);
        restrictField(myClosedButton, Cash.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Cash.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(Cash.FIELD_DESC, DataType.STRING, myDesc);
        theFieldSet.addFieldElement(Cash.FIELD_CATEGORY, CashCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Cash.FIELD_CURRENCY, AssetCurrency.class, theCurrencyButton);
        theFieldSet.addFieldElement(Cash.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Cash.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_CLOSED, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extras subPanel.
     * @return the panel
     */
    private JPanel buildXtrasPanel() {
        /* Allocate fields */
        JTextField myOpening = new JTextField();

        /* restrict the fields */
        int myWidth = Cash.NAMELEN >> 1;
        restrictField(theAutoExpenseButton, myWidth);
        restrictField(theAutoPayeeButton, myWidth);
        restrictField(myOpening, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), TransactionCategory.class, theAutoExpenseButton);
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), Payee.class, theAutoPayeeButton);
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), DataType.MONEY, myOpening);

        /* Create the extras panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), myPanel);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), myPanel);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Notes subPanel.
     * @return the panel
     */
    private JPanel buildNotesPanel() {
        /* Allocate fields */
        JTextArea myNotes = new JTextArea();
        JScrollPane myScroll = new JScrollPane(myNotes);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Cash myItem = getItem();
        if (myItem != null) {
            CashList myCash = getDataList(MoneyWiseDataType.CASH, CashList.class);
            setItem(myCash.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Cash myCash = getItem();
        boolean bIsClosed = myCash.isClosed();
        boolean bIsActive = myCash.isActive();
        boolean bIsRelevant = myCash.isRelevant();
        boolean isAutoExpense = myCash.isAutoExpense();
        boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Cash.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setEditable(Cash.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState.setState(bEditClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myCash.getDesc() != null;
        theFieldSet.setVisibility(Cash.FIELD_DESC, bShowDesc);

        /* Currency is hidden if we are autoExpense */
        theFieldSet.setVisibility(Cash.FIELD_CURRENCY, !isAutoExpense);

        /* AutoExpense/Payee is hidden unless we are autoExpense */
        JDataField myAutoExpenseField = CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE);
        JDataField myAutoPayeeField = CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE);
        theFieldSet.setVisibility(myAutoExpenseField, isAutoExpense);
        theFieldSet.setVisibility(myAutoPayeeField, isAutoExpense);

        /* OpeningBalance is hidden if we are autoExpense */
        JDataField myOpeningField = CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
        boolean bHasOpening = myCash.getOpeningBalance() != null;
        boolean bShowOpening = bIsChangeable || bHasOpening;
        theFieldSet.setVisibility(myOpeningField, !isAutoExpense && bShowOpening);

        /* Category/Currency cannot be changed if the item is active */
        theFieldSet.setEditable(Cash.FIELD_CATEGORY, bIsChangeable);
        theFieldSet.setEditable(Cash.FIELD_CURRENCY, bIsChangeable && !bHasOpening);

        /* AutoExpense/Payee cannot be changed for closed item */
        boolean canEdit = isEditable && !bIsClosed;
        theFieldSet.setEditable(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), canEdit);
        theFieldSet.setEditable(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), canEdit);

        /* Set currency for opening balance */
        if (!isAutoExpense) {
            theFieldSet.setAssumedCurrency(myOpeningField, myCash.getCurrency());
        }
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Cash myCash = getItem();

        /* Process updates */
        if (myField.equals(Cash.FIELD_NAME)) {
            /* Update the Name */
            myCash.setName(pUpdate.getString());
        } else if (myField.equals(Cash.FIELD_DESC)) {
            /* Update the Description */
            myCash.setDescription(pUpdate.getString());
        } else if (myField.equals(Cash.FIELD_CATEGORY)) {
            /* Update the Category */
            myCash.setCashCategory(pUpdate.getValue(CashCategory.class));
            myCash.autoCorrect(getUpdateSet());
        } else if (myField.equals(Cash.FIELD_CURRENCY)) {
            /* Update the Currency */
            myCash.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(Cash.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myCash.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (CashInfoSet.getClassForField(myField)) {
                case AUTOEXPENSE:
                    myCash.setAutoExpense(pUpdate.getValue(TransactionCategory.class));
                    break;
                case AUTOPAYEE:
                    myCash.setAutoPayee(pUpdate.getValue(Payee.class));
                    break;
                case OPENINGBALANCE:
                    myCash.setOpeningBalance(pUpdate.getMoney());
                    break;
                case NOTES:
                    myCash.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        Cash myItem = getItem();
        Payee myAutoPayee = myItem.getAutoPayee();
        if (!pUpdates) {
            CashCategory myCategory = myItem.getCategory();
            TransactionCategory myAutoExpense = myItem.getAutoExpense();
            AssetCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCategory);
            declareGoToItem(myCurrency);
            declareGoToItem(myAutoExpense);
        }
        declareGoToItem(myAutoPayee);
    }

    /**
     * Build the category list for an item.
     * @param pMenuBuilder the menu builder
     * @param pCash the cash to build for
     */
    public void buildCategoryMenu(final JScrollMenuBuilder<CashCategory> pMenuBuilder,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        CashCategory myCurr = pCash.getCategory();
        JMenuItem myActive = null;

        /* Access Cash Categories */
        CashCategoryList myCategories = getDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);

        /* Create a simple map for top-level categories */
        Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

        /* Loop through the available category values */
        Iterator<CashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            CashCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(CashCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            CashCategory myParent = myCategory.getParentCategory();
            String myParentName = myParent.getName();
            JScrollMenu myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenuBuilder.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            JMenuItem myItem = pMenuBuilder.addItem(myMenu, myCategory, myCategory.getSubCategory());

            /* Note active category */
            if (myCategory.equals(myCurr)) {
                myActive = myMenu;
                myMenu.showItem(myItem);
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the autoExpense list for an item.
     * @param pMenuBuilder the menu builder
     * @param pCash the cash to build for
     */
    private void buildAutoExpenseMenu(final JScrollMenuBuilder<TransactionCategory> pMenuBuilder,
                                      final Cash pCash) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        TransactionCategory myCurr = pCash.getAutoExpense();
        JMenuItem myActive = null;

        /* Access Transaction Categories */
        TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Create a simple map for top-level categories */
        Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

        /* Loop through the available category values */
        Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategory myCategory = myIterator.next();

            /* Ignore deleted or non-expense-subTotal items */
            TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
            boolean bIgnore = myCategory.isDeleted() || myClass.canParentCategory();
            bIgnore |= !myClass.isExpense();
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            TransactionCategory myParent = myCategory.getParentCategory();
            String myParentName = myParent.getName();
            JScrollMenu myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenuBuilder.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            JMenuItem myItem = pMenuBuilder.addItem(myMenu, myCategory, myCategory.getSubCategory());

            /* Note active category */
            if (myCategory.equals(myCurr)) {
                myActive = myMenu;
                myMenu.showItem(myItem);
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the payee list for an item.
     * @param pMenuBuilder the menu builder
     * @param pCash the cash to build for
     */
    private void buildAutoPayeeMenu(final JScrollMenuBuilder<Payee> pMenuBuilder,
                                    final Cash pCash) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        Payee myCurr = pCash.getAutoPayee();
        JMenuItem myActive = null;

        /* Access Payees */
        PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted */
            if (myPayee.isDeleted()) {
                continue;
            }

            /* Create a new action for the payee */
            JMenuItem myItem = pMenuBuilder.addItem(myPayee);

            /* If this is the active parent */
            if (myPayee.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the currency list for an item.
     * @param pMenuBuilder the menu builder
     * @param pCash the cash to build for
     */
    public void buildCurrencyMenu(final JScrollMenuBuilder<AssetCurrency> pMenuBuilder,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        AssetCurrency myCurr = pCash.getAssetCurrency();
        JMenuItem myActive = null;

        /* Access Currencies */
        AssetCurrencyList myCurrencies = getDataList(MoneyWiseDataType.CURRENCY, AssetCurrencyList.class);

        /* Loop through the AccountCurrencies */
        Iterator<AssetCurrency> myIterator = myCurrencies.iterator();
        while (myIterator.hasNext()) {
            AssetCurrency myCurrency = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myCurrency.isDeleted() || !myCurrency.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the currency */
            JMenuItem myItem = pMenuBuilder.addItem(myCurrency);

            /* If this is the active currency */
            if (myCurrency.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Cash Listener.
     */
    private final class CashListener
            implements ChangeListener {
        /**
         * The Category Menu Builder.
         */
        private final JScrollMenuBuilder<CashCategory> theCategoryMenuBuilder;

        /**
         * The Currency Menu Builder.
         */
        private final JScrollMenuBuilder<AssetCurrency> theCurrencyMenuBuilder;

        /**
         * The AutoExpense Menu Builder.
         */
        private final JScrollMenuBuilder<TransactionCategory> theAutoExpenseMenuBuilder;

        /**
         * The AutoPayee Menu Builder.
         */
        private final JScrollMenuBuilder<Payee> theAutoPayeeMenuBuilder;

        /**
         * Constructor.
         */
        private CashListener() {
            /* Access the MenuBuilders */
            theCategoryMenuBuilder = theCategoryButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
            theCurrencyMenuBuilder.addChangeListener(this);
            theAutoExpenseMenuBuilder = theAutoExpenseButton.getMenuBuilder();
            theAutoExpenseMenuBuilder.addChangeListener(this);
            theAutoPayeeMenuBuilder = theAutoPayeeButton.getMenuBuilder();
            theAutoPayeeMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu(theCategoryMenuBuilder, getItem());
            } else if (theCurrencyMenuBuilder.equals(o)) {
                buildCurrencyMenu(theCurrencyMenuBuilder, getItem());
            } else if (theAutoExpenseMenuBuilder.equals(o)) {
                buildAutoExpenseMenu(theAutoExpenseMenuBuilder, getItem());
            } else if (theAutoPayeeMenuBuilder.equals(o)) {
                buildAutoPayeeMenu(theAutoPayeeMenuBuilder, getItem());
            }
        }
    }
}
