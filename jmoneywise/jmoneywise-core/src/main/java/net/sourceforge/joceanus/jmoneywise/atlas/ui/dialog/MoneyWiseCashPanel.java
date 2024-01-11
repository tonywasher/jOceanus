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
package net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashCategory.MoneyWiseCashCategoryList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditSet;
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
        extends MoneyWiseItemPanel<MoneyWiseCash> {
    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<MoneyWiseCash> theFieldSet;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pError the error panel
     */
    public MoneyWiseCashPanel(final TethysUIFactory<?> pFactory,
                              final PrometheusEditSet pEditSet,
                              final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pError);

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
        final TethysUIScrollButtonField<MoneyWiseAssetCategory> myCategoryButton = myFields.newScrollField(MoneyWiseAssetCategory.class);
        final TethysUIScrollButtonField<MoneyWiseCurrency> myCurrencyButton = myFields.newScrollField(MoneyWiseCurrency.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseCash::getName);
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseCash::getDesc);
        theFieldSet.addField(MoneyWiseBasicResource.CATEGORY_NAME, myCategoryButton, MoneyWiseCash::getCategory);
        theFieldSet.addField(MoneyWiseStaticDataType.CURRENCY, myCurrencyButton, MoneyWiseCash::getAssetCurrency);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_CLOSED, myClosedButton, MoneyWiseCash::isClosed);

        /* Configure the menuBuilders */
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton(pFactory);
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
        final TethysUIScrollButtonField<MoneyWiseTransCategory> myAutoExpenseButton = myFields.newScrollField(MoneyWiseTransCategory.class);
        final TethysUIScrollButtonField<MoneyWisePayee> myAutoPayeeButton = myFields.newScrollField(MoneyWisePayee.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAccountInfoClass.AUTOEXPENSE, myAutoExpenseButton, MoneyWiseCash::getAutoExpense);
        theFieldSet.addField(MoneyWiseAccountInfoClass.AUTOPAYEE, myAutoPayeeButton, MoneyWiseCash::getAutoPayee);
        theFieldSet.addField(MoneyWiseAccountInfoClass.OPENINGBALANCE, myOpening, MoneyWiseCash::getOpeningBalance);

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
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAccountInfoClass.NOTES, myNotes, MoneyWiseCash::getNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWiseCash myItem = getItem();
        if (myItem != null) {
            final MoneyWiseCashList myCash = getDataList(MoneyWiseBasicDataType.CASH, MoneyWiseCashList.class);
            setItem(myCash.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final MoneyWiseCash myCash = getItem();
        final boolean bIsClosed = myCash.isClosed();
        final boolean bIsActive = myCash.isActive();
        final boolean bIsRelevant = myCash.isRelevant();
        final boolean isAutoExpense = myCash.isAutoExpense();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.ASSET_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCash.getDesc() != null;
        theFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* AutoExpense/Payee is hidden unless we are autoExpense */
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.AUTOEXPENSE, isAutoExpense);
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.AUTOPAYEE, isAutoExpense);

        /* OpeningBalance is hidden if we are autoExpense */
        final boolean bHasOpening = myCash.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.OPENINGBALANCE, !isAutoExpense && bShowOpening);

        /* Determine whether to show notes */
        final boolean bShowNotes = isEditable || myCash.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.NOTES, bShowNotes);

        /* Category/Currency cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.CATEGORY_NAME, bIsChangeable);
        theFieldSet.setFieldEditable(MoneyWiseStaticDataType.CURRENCY, bIsChangeable && !bHasOpening);

        /* AutoExpense/Payee cannot be changed for closed item */
        final boolean canEdit = isEditable && !bIsClosed;
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.AUTOEXPENSE, canEdit);
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.AUTOPAYEE, canEdit);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseCash myCash = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the Name */
            myCash.setName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myCash.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_NAME.equals(myField)) {
            /* Update the Category */
            myCash.setCategory(pUpdate.getValue(MoneyWiseCashCategory.class));
            myCash.autoCorrect(getEditSet());
        } else if (MoneyWiseStaticDataType.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myCash.setAssetCurrency(pUpdate.getValue(MoneyWiseCurrency.class));
        } else if (MoneyWiseBasicResource.ASSET_CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myCash.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAccountInfoClass.AUTOEXPENSE.equals(myField)) {
            /* Update the AutoExpense */
            myCash.setAutoExpense(pUpdate.getValue(MoneyWiseTransCategory.class));
        } else if (MoneyWiseAccountInfoClass.AUTOPAYEE.equals(myField)) {
            /* Update the AutoPayee */
            myCash.setAutoPayee(pUpdate.getValue(MoneyWisePayee.class));
        } else if (MoneyWiseAccountInfoClass.OPENINGBALANCE.equals(myField)) {
            /* Update the OpeningBalance */
            myCash.setOpeningBalance(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseAccountInfoClass.NOTES.equals(myField)) {
            /* Update the OpeningBalance */
            myCash.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseCash myItem = getItem();
        final MoneyWisePayee myAutoPayee = myItem.getAutoPayee();
        if (!pUpdates) {
            final MoneyWiseCashCategory myCategory = myItem.getCategory();
            final MoneyWiseTransCategory myAutoExpense = myItem.getAutoExpense();
            final MoneyWiseCurrency myCurrency = myItem.getAssetCurrency();
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
    public void buildCategoryMenu(final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu,
                                  final MoneyWiseCash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCashCategory myCurr = pCash.getCategory();
        TethysUIScrollItem<MoneyWiseAssetCategory> myActive = null;

        /* Access Cash Categories */
        final MoneyWiseCashCategoryList myCategories = getDataList(MoneyWiseBasicDataType.CASHCATEGORY, MoneyWiseCashCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<MoneyWiseAssetCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<MoneyWiseCashCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseCashCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            final boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(MoneyWiseCashCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseCashCategory myParent = myCategory.getParentCategory();
            final String myParentName = myParent.getName();
            final TethysUIScrollSubMenu<MoneyWiseAssetCategory> myMenu = myMap.computeIfAbsent(myParentName, pMenu::addSubMenu);

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseAssetCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
    private void buildAutoExpenseMenu(final TethysUIScrollMenu<MoneyWiseTransCategory> pMenu,
                                      final MoneyWiseCash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseTransCategory myCurr = pCash.getAutoExpense();
        TethysUIScrollItem<MoneyWiseTransCategory> myActive = null;

        /* Access Transaction Categories */
        final MoneyWiseTransCategoryList myCategories = getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<MoneyWiseTransCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<MoneyWiseTransCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategory myCategory = myIterator.next();

            /* Ignore deleted or non-expense-subTotal items */
            final MoneyWiseTransCategoryClass myClass = myCategory.getCategoryTypeClass();
            boolean bIgnore = myCategory.isDeleted() || myClass.canParentCategory();
            bIgnore |= !myClass.isExpense();
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseTransCategory myParent = myCategory.getParentCategory();
            final String myParentName = myParent.getName();
            final TethysUIScrollSubMenu<MoneyWiseTransCategory> myMenu = myMap.computeIfAbsent(myParentName, pMenu::addSubMenu);

            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<MoneyWiseTransCategory> myItem = myMenu.getSubMenu().addItem(myCategory);

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
    private void buildAutoPayeeMenu(final TethysUIScrollMenu<MoneyWisePayee> pMenu,
                                    final MoneyWiseCash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWisePayee myCurr = pCash.getAutoPayee();
        TethysUIScrollItem<MoneyWisePayee> myActive = null;

        /* Access Payees */
        final MoneyWisePayeeList myPayees = getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);

        /* Loop through the Payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted */
            if (myPayee.isDeleted()) {
                continue;
            }

            /* Create a new action for the payee */
            final TethysUIScrollItem<MoneyWisePayee> myItem = pMenu.addItem(myPayee);

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
    public void buildCurrencyMenu(final TethysUIScrollMenu<MoneyWiseCurrency> pMenu,
                                  final MoneyWiseCash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCurrency myCurr = pCash.getAssetCurrency();
        TethysUIScrollItem<MoneyWiseCurrency> myActive = null;

        /* Access Currencies */
        final MoneyWiseCurrencyList myCurrencies = getDataList(MoneyWiseStaticDataType.CURRENCY, MoneyWiseCurrencyList.class);

        /* Loop through the AccountCurrencies */
        final Iterator<MoneyWiseCurrency> myIterator = myCurrencies.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseCurrency myCurrency = myIterator.next();

            /* Ignore deleted or disabled */
            final boolean bIgnore = myCurrency.isDeleted() || !myCurrency.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the currency */
            final TethysUIScrollItem<MoneyWiseCurrency> myItem = pMenu.addItem(myCurrency);

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
