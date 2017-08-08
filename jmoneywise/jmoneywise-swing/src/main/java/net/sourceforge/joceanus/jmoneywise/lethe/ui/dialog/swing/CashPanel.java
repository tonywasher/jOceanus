/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisEosFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.eos.MetisEosFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTextArea;

/**
 * Panel to display/edit/create a Cash.
 */
public class CashPanel
        extends MoneyWiseEosItemPanel<Cash> {
    /**
     * The Field Set.
     */
    private final MetisEosFieldSet<Cash> theFieldSet;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public CashPanel(final TethysSwingGuiFactory pFactory,
                     final MetisEosFieldManager pFieldMgr,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel(pFactory);

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel(pFactory);
        myTabs.add(TAB_DETAILS, myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel(pFactory);
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create the text fields */
        TethysSwingStringTextField myName = pFactory.newStringField();
        TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        TethysSwingScrollButtonManager<CashCategory> myCategoryButton = pFactory.newScrollButton();
        TethysSwingScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* restrict the fields */
        restrictField(myName, Cash.NAMELEN);
        restrictField(myDesc, Cash.NAMELEN);
        restrictField(myCategoryButton, Cash.NAMELEN);
        restrictField(myCurrencyButton, Cash.NAMELEN);
        restrictField(myClosedButton, Cash.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Cash.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(Cash.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(Cash.FIELD_CATEGORY, CashCategory.class, myCategoryButton);
        theFieldSet.addFieldElement(Cash.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        theFieldSet.addFieldElement(Cash.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Cash.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Cash.FIELD_CLOSED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Configure the menuBuilders */
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extras subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildXtrasPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        TethysSwingStringTextField myOpening = pFactory.newStringField();

        /* Create the buttons */
        TethysSwingScrollButtonManager<TransactionCategory> myAutoExpenseButton = pFactory.newScrollButton();
        TethysSwingScrollButtonManager<Payee> myAutoPayeeButton = pFactory.newScrollButton();

        /* restrict the fields */
        int myWidth = Cash.NAMELEN >> 1;
        restrictField(myAutoExpenseButton, myWidth);
        restrictField(myAutoPayeeButton, myWidth);
        restrictField(myOpening, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), TransactionCategory.class, myAutoExpenseButton);
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), Payee.class, myAutoPayeeButton);
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), MetisDataType.MONEY, myOpening);

        /* Create the extras panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), myPanel);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), myPanel);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Configure the menuBuilders */
        myAutoExpenseButton.setMenuConfigurator(c -> buildAutoExpenseMenu(c, getItem()));
        myAutoPayeeButton.setMenuConfigurator(c -> buildAutoPayeeMenu(c, getItem()));

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildNotesPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        TethysSwingTextArea myNotes = pFactory.newTextArea();
        TethysSwingScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myCash.getDesc() != null;
        theFieldSet.setVisibility(Cash.FIELD_DESC, bShowDesc);

        /* AutoExpense/Payee is hidden unless we are autoExpense */
        MetisField myAutoExpenseField = CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE);
        MetisField myAutoPayeeField = CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE);
        theFieldSet.setVisibility(myAutoExpenseField, isAutoExpense);
        theFieldSet.setVisibility(myAutoPayeeField, isAutoExpense);

        /* OpeningBalance is hidden if we are autoExpense */
        MetisField myOpeningField = CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
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
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
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
     * Build the category menu for an item.
     * @param pMenu the menu
     * @param pCash the cash to build for
     */
    public void buildCategoryMenu(final TethysScrollMenu<CashCategory, Icon> pMenu,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        CashCategory myCurr = pCash.getCategory();
        TethysScrollMenuItem<CashCategory> myActive = null;

        /* Access Cash Categories */
        CashCategoryList myCategories = getDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);

        /* Create a simple map for top-level categories */
        Map<String, TethysScrollSubMenu<CashCategory, Icon>> myMap = new HashMap<>();

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
            TethysScrollSubMenu<CashCategory, Icon> myMenu = myMap.get(myParentName);

            /* If this is a new subMenu */
            if (myMenu == null) {
                /* Create a new subMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<CashCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

            /* Note active category */
            if (myCategory.equals(myCurr)) {
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Build the autoExpense menu for an item.
     * @param pMenu the menu
     * @param pCash the cash to build for
     */
    private void buildAutoExpenseMenu(final TethysScrollMenu<TransactionCategory, Icon> pMenu,
                                      final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        TransactionCategory myCurr = pCash.getAutoExpense();
        TethysScrollMenuItem<TransactionCategory> myActive = null;

        /* Access Transaction Categories */
        TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Create a simple map for top-level categories */
        Map<String, TethysScrollSubMenu<TransactionCategory, Icon>> myMap = new HashMap<>();

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
            TethysScrollSubMenu<TransactionCategory, Icon> myMenu = myMap.get(myParentName);

            /* If this is a new subMenu */
            if (myMenu == null) {
                /* Create a new subMMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<TransactionCategory> myItem = myMenu.getSubMenu().addItem(myCategory);

            /* Note active category */
            if (myCategory.equals(myCurr)) {
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Build the autoPayee menu for an item.
     * @param pMenu the menu
     * @param pCash the cash to build for
     */
    private void buildAutoPayeeMenu(final TethysScrollMenu<Payee, Icon> pMenu,
                                    final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        Payee myCurr = pCash.getAutoPayee();
        TethysScrollMenuItem<Payee> myActive = null;

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
            TethysScrollMenuItem<Payee> myItem = pMenu.addItem(myPayee);

            /* If this is the active parent */
            if (myPayee.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Build the currency menu for an item.
     * @param pMenu the menu
     * @param pCash the cash to build for
     */
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency, Icon> pMenu,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        AssetCurrency myCurr = pCash.getAssetCurrency();
        TethysScrollMenuItem<AssetCurrency> myActive = null;

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
            TethysScrollMenuItem<AssetCurrency> myItem = pMenu.addItem(myCurrency);

            /* If this is the active currency */
            if (myCurrency.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }
}
