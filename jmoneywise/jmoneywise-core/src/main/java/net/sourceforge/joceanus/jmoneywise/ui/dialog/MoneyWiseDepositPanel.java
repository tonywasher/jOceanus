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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseXView;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSet;
import net.sourceforge.joceanus.jprometheus.lethe.ui.fieldset.PrometheusXFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollSubMenu;

/**
 * Panel to display/edit/create a Deposit.
 */
public class MoneyWiseDepositPanel
        extends MoneyWiseItemPanel<Deposit> {
    /**
     * Rates Tab Title.
     */
    private static final String TAB_RATES = MoneyWiseUIResource.DEPOSITPANEL_TAB_RATES.getValue();

    /**
     * The fieldSet.
     */
    private final PrometheusXFieldSet<Deposit> theFieldSet;

    /**
     * DepositRate Table.
     */
    private final MoneyWiseDepositRateTable theRates;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pView the data view
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public MoneyWiseDepositPanel(final TethysUIFactory<?> pFactory,
                                 final MoneyWiseXView pView,
                                 final UpdateSet pUpdateSet,
                                 final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the account panel */
        buildAccountPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Create the DepositRates table and add to fieldSet */
        theRates = new MoneyWiseDepositRateTable(pView, getUpdateSet(), pError);
        theFieldSet.newTable(TAB_RATES, theRates);

        /* Create the listeners */
        theRates.getEventRegistrar().addEventListener(e -> {
            updateActions();
            fireStateChanged();
        });
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
        final TethysUIScrollButtonField<Payee> myParentButton = myFields.newScrollField(Payee.class);
        final TethysUIScrollButtonField<AssetCurrency> myCurrencyButton = myFields.newScrollField(AssetCurrency.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.NAME, myName, Deposit::getName);
        theFieldSet.addField(MoneyWiseAssetDataId.DESC, myDesc, Deposit::getDesc);
        theFieldSet.addField(MoneyWiseAssetDataId.CATEGORY, myCategoryButton, Deposit::getCategory);
        theFieldSet.addField(MoneyWiseAssetDataId.PARENT, myParentButton, Deposit::getParent);
        theFieldSet.addField(MoneyWiseAssetDataId.CURRENCY, myCurrencyButton, Deposit::getAssetCurrency);
        theFieldSet.addField(MoneyWiseAssetDataId.CLOSED, myClosedButton, Deposit::isClosed);

        /* Configure the menuBuilders */
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton(pFactory);
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));
    }

    /**
     * Build account subPanel.
     * @param pFactory the GUI factory
     */
    private void buildAccountPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_ACCOUNT);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIDateButtonField myMaturity = myFields.newDateField();
        final TethysUICharArrayEditField mySortCode = myFields.newCharArrayField();
        final TethysUICharArrayEditField myAccount = myFields.newCharArrayField();
        final TethysUICharArrayEditField myReference = myFields.newCharArrayField();
        final TethysUIMoneyEditField myOpening = myFields.newMoneyField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.DEPOSITMATURITY, myMaturity, Deposit::getMaturity);
        theFieldSet.addField(MoneyWiseAssetDataId.DEPOSITSORTCODE, mySortCode, Deposit::getSortCode);
        theFieldSet.addField(MoneyWiseAssetDataId.DEPOSITACCOUNT, myAccount, Deposit::getAccount);
        theFieldSet.addField(MoneyWiseAssetDataId.DEPOSITREFERENCE, myReference, Deposit::getReference);
        theFieldSet.addField(MoneyWiseAssetDataId.DEPOSITOPENINGBALANCE, myOpening, Deposit::getOpeningBalance);

        /* Configure the currency */
        myOpening.setDeemedCurrency(() -> getItem().getCurrency());
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysUIFactory<?> pFactory) {
        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayTextAreaField myNotes = myFields.newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAssetDataId.DEPOSITNOTES, myNotes, Deposit::getNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Deposit myItem = getItem();
        if (myItem != null) {
            final DepositList myDeposits = getDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
            setItem(myDeposits.findItemById(myItem.getId()));
        }

        /* Refresh the rates */
        theRates.refreshData();

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final Deposit myDeposit = getItem();
        final boolean bIsClosed = myDeposit.isClosed();
        final boolean bIsActive = myDeposit.isActive();
        final boolean bIsRelevant = myDeposit.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                ? !myDeposit.getParent().isClosed()
                : !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myDeposit.getDesc() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myDeposit.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DEPOSITSORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myDeposit.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DEPOSITACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myDeposit.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DEPOSITREFERENCE, bShowReference);
        final boolean bHasOpening = myDeposit.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DEPOSITOPENINGBALANCE, bShowOpening);
        final boolean bShowNotes = isEditable || myDeposit.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DEPOSITNOTES, bShowNotes);

        /* Maturity is only visible if the item is a bond */
        final boolean bShowMaturity = DepositCategoryClass.BOND.equals(myDeposit.getCategoryClass());
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DEPOSITMATURITY, bShowMaturity);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.DEPOSITMATURITY, isEditable && !bIsClosed);

        /* Category, Currency, and OpeningBalance cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CATEGORY, bIsChangeable);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CURRENCY, bIsChangeable && !bHasOpening);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.DEPOSITOPENINGBALANCE, bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final PrometheusXFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final Deposit myDeposit = getItem();

        /* Process updates */
        if (MoneyWiseAssetDataId.NAME.equals(myField)) {
            /* Update the Name */
            myDeposit.setName(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.DESC.equals(myField)) {
            /* Update the Description */
            myDeposit.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.CATEGORY.equals(myField)) {
            /* Update the Category */
            myDeposit.setCategory(pUpdate.getValue(DepositCategory.class));
            myDeposit.autoCorrect(getUpdateSet());
        } else if (MoneyWiseAssetDataId.PARENT.equals(myField)) {
            /* Update the Parent */
            myDeposit.setParent(pUpdate.getValue(Payee.class));
        } else if (MoneyWiseAssetDataId.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myDeposit.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (MoneyWiseAssetDataId.CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myDeposit.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAssetDataId.DEPOSITMATURITY.equals(myField)) {
            /* Update the Maturity */
            myDeposit.setMaturity(pUpdate.getValue(TethysDate.class));
        } else if (MoneyWiseAssetDataId.DEPOSITSORTCODE.equals(myField)) {
            /* Update the SortCode */
            myDeposit.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.DEPOSITACCOUNT.equals(myField)) {
            /* Update the Account */
            myDeposit.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.DEPOSITREFERENCE.equals(myField)) {
            /* Update the Reference */
            myDeposit.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.DEPOSITOPENINGBALANCE.equals(myField)) {
            /* Update the OpeningBalance */
            myDeposit.setOpeningBalance(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseAssetDataId.DEPOSITNOTES.equals(myField)) {
            /* Update the Notes */
            myDeposit.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final Deposit myItem = getItem();
        final Payee myParent = myItem.getParent();
        if (!pUpdates) {
            final DepositCategory myCategory = myItem.getCategory();
            final AssetCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCategory);
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    @Override
    public void setEditable(final boolean isEditable) {
        /* Update the rates */
        theRates.setEditable(isEditable);

        /* Pass call onwards */
        super.setEditable(isEditable);
    }

    @Override
    protected void refreshAfterUpdate() {
        /* Pass call onwards */
        super.refreshAfterUpdate();

        /* Refresh the rates */
        theRates.refreshAfterUpdate();
    }

    /**
     * Build the category type menu for an item.
     * @param pMenu the menu
     * @param pDeposit the deposit to build for
     */
    public void buildCategoryMenu(final TethysUIScrollMenu<AssetCategory> pMenu,
                                  final Deposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final DepositCategory myCurr = pDeposit.getCategory();
        TethysUIScrollItem<AssetCategory> myActive = null;

        /* Access Deposit Categories */
        final DepositCategoryList myCategories = getDataList(MoneyWiseDataType.DEPOSITCATEGORY, DepositCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<AssetCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<DepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final DepositCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            final boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(DepositCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final DepositCategory myParent = myCategory.getParentCategory();
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
     * Build the parent menu for an item.
     * @param pMenu the menu
     * @param pDeposit the deposit to build for
     */
    public void buildParentMenu(final TethysUIScrollMenu<Payee> pMenu,
                                final Deposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final DepositCategoryClass myType = pDeposit.getCategoryClass();
        final Payee myCurr = pDeposit.getParent();
        TethysUIScrollItem<Payee> myActive = null;

        /* Access Payees */
        final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getCategoryClass().canParentDeposit(myType);
            bIgnore |= myPayee.isClosed();
            if (bIgnore) {
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
     * @param pDeposit the deposit to build for
     */
    public void buildCurrencyMenu(final TethysUIScrollMenu<AssetCurrency> pMenu,
                                  final Deposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pDeposit.getAssetCurrency();
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
