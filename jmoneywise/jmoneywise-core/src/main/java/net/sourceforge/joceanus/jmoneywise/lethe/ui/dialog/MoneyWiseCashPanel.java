/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseXIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSet;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollSubMenu;

/**
 * Panel to display/edit/create a Cash.
 */
public class MoneyWiseCashPanel
        extends MoneyWiseItemPanel<Cash> {
    /**
     * The fieldSet.
     */
    private final PrometheusXFieldSet<Cash> theFieldSet;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public MoneyWiseCashPanel(final TethysUIFactory<?> pFactory,
                              final UpdateSet pUpdateSet,
                              final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the details panel */
        buildDetailsPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     */
    private void buildMainPanel(final TethysUIFactory<?> pFactory) {
        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<AssetCategory> myCategoryButton = myFields.newScrollField(AssetCategory.class);
        final TethysUIScrollButtonField<AssetCurrency> myCurrencyButton = myFields.newScrollField(AssetCurrency.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.NAME, myName, Cash::getName);
        theFieldSet.addField(MoneyWiseAssetDataId.DESC, myDesc, Cash::getDesc);
        theFieldSet.addField(MoneyWiseAssetDataId.CATEGORY, myCategoryButton, Cash::getCategory);
        theFieldSet.addField(MoneyWiseAssetDataId.CURRENCY, myCurrencyButton, Cash::getAssetCurrency);
        theFieldSet.addField(MoneyWiseAssetDataId.CLOSED, myClosedButton, Cash::isClosed);

        /* Configure the menuBuilders */
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseXIcon.configureLockedIconButton(pFactory);
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));
    }

    /**
     * Build account subPanel.
     * @param pFactory the GUI factory
     */
    private void buildDetailsPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_DETAILS);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIMoneyEditField myOpening = myFields.newMoneyField();

        /* Create the buttons */
        final TethysUIScrollButtonField<TransactionCategory> myAutoExpenseButton = myFields.newScrollField(TransactionCategory.class);
        final TethysUIScrollButtonField<Payee> myAutoPayeeButton = myFields.newScrollField(Payee.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.CASHAUTOEXPENSE, myAutoExpenseButton, Cash::getAutoExpense);
        theFieldSet.addField(MoneyWiseAssetDataId.CASHAUTOPAYEE, myAutoPayeeButton, Cash::getAutoPayee);
        theFieldSet.addField(MoneyWiseAssetDataId.CASHOPENINGBALANCE, myOpening, Cash::getOpeningBalance);

        /* Configure the menuBuilders */
        myAutoExpenseButton.setMenuConfigurator(c -> buildAutoExpenseMenu(c, getItem()));
        myAutoPayeeButton.setMenuConfigurator(c -> buildAutoPayeeMenu(c, getItem()));
        myOpening.setDeemedCurrency(() -> getItem().getCurrency());
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysUIFactory<?> pFactory) {
        /* Allocate fields */
        final TethysUICharArrayTextAreaField myNotes = pFactory.fieldFactory().newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAssetDataId.CASHNOTES, myNotes, Cash::getNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Cash myItem = getItem();
        if (myItem != null) {
            final CashList myCash = getDataList(MoneyWiseDataType.CASH, CashList.class);
            setItem(myCash.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final Cash myCash = getItem();
        final boolean bIsClosed = myCash.isClosed();
        final boolean bIsActive = myCash.isActive();
        final boolean bIsRelevant = myCash.isRelevant();
        final boolean isAutoExpense = myCash.isAutoExpense();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCash.getDesc() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DESC, bShowDesc);

        /* AutoExpense/Payee is hidden unless we are autoExpense */
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CASHAUTOEXPENSE, isAutoExpense);
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CASHAUTOPAYEE, isAutoExpense);

        /* OpeningBalance is hidden if we are autoExpense */
        final boolean bHasOpening = myCash.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CASHOPENINGBALANCE, !isAutoExpense && bShowOpening);

        /* Determine whether to show notes */
        final boolean bShowNotes = isEditable || myCash.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CASHNOTES, bShowNotes);

        /* Category/Currency cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CATEGORY, bIsChangeable);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CURRENCY, bIsChangeable && !bHasOpening);

        /* AutoExpense/Payee cannot be changed for closed item */
        final boolean canEdit = isEditable && !bIsClosed;
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CASHAUTOEXPENSE, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CASHAUTOPAYEE, canEdit);
    }

    @Override
    protected void updateField(final PrometheusXFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final Cash myCash = getItem();

        /* Process updates */
        if (MoneyWiseAssetDataId.NAME.equals(myField)) {
            /* Update the Name */
            myCash.setName(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.DESC.equals(myField)) {
            /* Update the Description */
            myCash.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.CATEGORY.equals(myField)) {
            /* Update the Category */
            myCash.setCategory(pUpdate.getValue(CashCategory.class));
            myCash.autoCorrect(getUpdateSet());
        } else if (MoneyWiseAssetDataId.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myCash.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (MoneyWiseAssetDataId.CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myCash.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAssetDataId.CASHAUTOEXPENSE.equals(myField)) {
            /* Update the AutoExpense */
            myCash.setAutoExpense(pUpdate.getValue(TransactionCategory.class));
        } else if (MoneyWiseAssetDataId.CASHAUTOPAYEE.equals(myField)) {
            /* Update the AutoPayee */
            myCash.setAutoPayee(pUpdate.getValue(Payee.class));
        } else if (MoneyWiseAssetDataId.CASHOPENINGBALANCE.equals(myField)) {
            /* Update the OpeningBalance */
            myCash.setOpeningBalance(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseAssetDataId.CASHNOTES.equals(myField)) {
            /* Update the OpeningBalance */
            myCash.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final Cash myItem = getItem();
        final Payee myAutoPayee = myItem.getAutoPayee();
        if (!pUpdates) {
            final CashCategory myCategory = myItem.getCategory();
            final TransactionCategory myAutoExpense = myItem.getAutoExpense();
            final AssetCurrency myCurrency = myItem.getAssetCurrency();
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
    public void buildCategoryMenu(final TethysUIScrollMenu<AssetCategory> pMenu,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final CashCategory myCurr = pCash.getCategory();
        TethysUIScrollItem<AssetCategory> myActive = null;

        /* Access Cash Categories */
        final CashCategoryList myCategories = getDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<AssetCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<CashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final CashCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            final boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(CashCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final CashCategory myParent = myCategory.getParentCategory();
            final String myParentName = myParent.getName();
            final TethysUIScrollSubMenu<AssetCategory> myMenu = myMap.computeIfAbsent(myParentName, pMenu::addSubMenu);

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<AssetCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
    private void buildAutoExpenseMenu(final TethysUIScrollMenu<TransactionCategory> pMenu,
                                      final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final TransactionCategory myCurr = pCash.getAutoExpense();
        TethysUIScrollItem<TransactionCategory> myActive = null;

        /* Access Transaction Categories */
        final TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<TransactionCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final TransactionCategory myCategory = myIterator.next();

            /* Ignore deleted or non-expense-subTotal items */
            final TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
            boolean bIgnore = myCategory.isDeleted() || myClass.canParentCategory();
            bIgnore |= !myClass.isExpense();
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final TransactionCategory myParent = myCategory.getParentCategory();
            final String myParentName = myParent.getName();
            final TethysUIScrollSubMenu<TransactionCategory> myMenu = myMap.computeIfAbsent(myParentName, pMenu::addSubMenu);

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<TransactionCategory> myItem = myMenu.getSubMenu().addItem(myCategory);

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
    private void buildAutoPayeeMenu(final TethysUIScrollMenu<Payee> pMenu,
                                    final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final Payee myCurr = pCash.getAutoPayee();
        TethysUIScrollItem<Payee> myActive = null;

        /* Access Payees */
        final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted */
            if (myPayee.isDeleted()) {
                continue;
            }

            /* Create a new action for the payee */
            final TethysUIScrollItem<Payee> myItem = pMenu.addItem(myPayee);

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
    public void buildCurrencyMenu(final TethysUIScrollMenu<AssetCurrency> pMenu,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pCash.getAssetCurrency();
        TethysUIScrollItem<AssetCurrency> myActive = null;

        /* Access Currencies */
        final AssetCurrencyList myCurrencies = getDataList(MoneyWiseDataType.CURRENCY, AssetCurrencyList.class);

        /* Loop through the AccountCurrencies */
        final Iterator<AssetCurrency> myIterator = myCurrencies.iterator();
        while (myIterator.hasNext()) {
            final AssetCurrency myCurrency = myIterator.next();

            /* Ignore deleted or disabled */
            final boolean bIgnore = myCurrency.isDeleted() || !myCurrency.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the currency */
            final TethysUIScrollItem<AssetCurrency> myItem = pMenu.addItem(myCurrency);

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
