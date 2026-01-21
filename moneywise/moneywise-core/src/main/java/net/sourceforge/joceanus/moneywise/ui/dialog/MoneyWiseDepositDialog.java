/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.ui.dialog;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory.MoneyWiseDepositCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Panel to display/edit/create a Deposit.
 */
public class MoneyWiseDepositDialog
        extends MoneyWiseAssetDialog<MoneyWiseDeposit> {
    /**
     * Rates Tab Title.
     */
    private static final String TAB_RATES = MoneyWiseUIResource.DEPOSITPANEL_TAB_RATES.getValue();

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<MoneyWiseDeposit> theFieldSet;

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
     *
     * @param pFactory the GUI factory
     * @param pView    the data view
     * @param pEditSet the edit set
     * @param pOwner   the owning table
     */
    public MoneyWiseDepositDialog(final TethysUIFactory<?> pFactory,
                                  final MoneyWiseView pView,
                                  final PrometheusEditSet pEditSet,
                                  final MoneyWiseAssetTable<MoneyWiseDeposit> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.setReporter(pOwner::showValidateError);

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the account panel */
        buildAccountPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Create the DepositRates table and add to fieldSet */
        theRates = new MoneyWiseDepositRateTable(pView, getEditSet(), pOwner.getErrorPanel());
        theFieldSet.newTable(TAB_RATES, theRates);

        /* Create the listeners */
        theRates.getEventRegistrar().addEventListener(e -> {
            updateActions();
            fireStateChanged();
        });
    }

    /**
     * Build Main subPanel.
     *
     * @param pFactory the GUI factory
     */
    private void buildMainPanel(final TethysUIFactory<?> pFactory) {
        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseAssetCategory> myCategoryButton = myFields.newScrollField(MoneyWiseAssetCategory.class);
        final TethysUIScrollButtonField<MoneyWisePayee> myParentButton = myFields.newScrollField(MoneyWisePayee.class);
        final TethysUIScrollButtonField<MoneyWiseCurrency> myCurrencyButton = myFields.newScrollField(MoneyWiseCurrency.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseDeposit::getName);
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseDeposit::getDesc);
        theFieldSet.addField(MoneyWiseBasicResource.CATEGORY_NAME, myCategoryButton, MoneyWiseDeposit::getCategory);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_PARENT, myParentButton, MoneyWiseDeposit::getParent);
        theFieldSet.addField(MoneyWiseStaticDataType.CURRENCY, myCurrencyButton, MoneyWiseDeposit::getAssetCurrency);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_CLOSED, myClosedButton, MoneyWiseDeposit::isClosed);

        /* Configure the menuBuilders */
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton(pFactory);
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Configure validation checks */
        myName.setValidator(this::isValidName);
        myDesc.setValidator(this::isValidDesc);
    }

    /**
     * Build account subPanel.
     *
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
        theFieldSet.addField(MoneyWiseAccountInfoClass.MATURITY, myMaturity, MoneyWiseDeposit::getMaturity);
        theFieldSet.addField(MoneyWiseAccountInfoClass.SORTCODE, mySortCode, MoneyWiseDeposit::getSortCode);
        theFieldSet.addField(MoneyWiseAccountInfoClass.ACCOUNT, myAccount, MoneyWiseDeposit::getAccount);
        theFieldSet.addField(MoneyWiseAccountInfoClass.REFERENCE, myReference, MoneyWiseDeposit::getReference);
        theFieldSet.addField(MoneyWiseAccountInfoClass.OPENINGBALANCE, myOpening, MoneyWiseDeposit::getOpeningBalance);

        /* Configure the currency */
        myOpening.setDeemedCurrency(() -> getItem().getCurrency());

        /* Configure validation checks */
        mySortCode.setValidator(this::isValidSortCode);
        myAccount.setValidator(this::isValidAccount);
        myReference.setValidator(this::isValidReference);
    }

    /**
     * Build Notes subPanel.
     *
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysUIFactory<?> pFactory) {
        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayTextAreaField myNotes = myFields.newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAccountInfoClass.NOTES, myNotes, MoneyWiseDeposit::getNotes);

        /* Configure validation checks */
        myNotes.setValidator(this::isValidNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWiseDeposit myItem = getItem();
        if (myItem != null) {
            final MoneyWiseDepositList myDeposits = getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class);
            setItem(myDeposits.findItemById(myItem.getIndexedId()));
        }

        /* Refresh the rates */
        theRates.refreshData();

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final MoneyWiseDeposit myDeposit = getItem();
        final boolean bIsClosed = myDeposit.isClosed();
        final boolean bIsActive = myDeposit.isActive();
        final boolean bIsRelevant = myDeposit.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.ASSET_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                ? !myDeposit.getParent().isClosed()
                : !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myDeposit.getDesc() != null;
        theFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myDeposit.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.SORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myDeposit.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.ACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myDeposit.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.REFERENCE, bShowReference);
        final boolean bHasOpening = myDeposit.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.OPENINGBALANCE, bShowOpening);
        final boolean bShowNotes = isEditable || myDeposit.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.NOTES, bShowNotes);

        /* Maturity is only visible if the item is a bond */
        final boolean bShowMaturity = MoneyWiseDepositCategoryClass.BOND.equals(myDeposit.getCategoryClass());
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.MATURITY, bShowMaturity);
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.MATURITY, isEditable && !bIsClosed);

        /* Category, Currency, and OpeningBalance cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.CATEGORY_NAME, bIsChangeable);
        theFieldSet.setFieldEditable(MoneyWiseStaticDataType.CURRENCY, bIsChangeable && !bHasOpening);
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.OPENINGBALANCE, bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseDeposit myDeposit = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the Name */
            myDeposit.setName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myDeposit.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_NAME.equals(myField)) {
            /* Update the Category */
            myDeposit.setCategory(pUpdate.getValue(MoneyWiseDepositCategory.class));
            myDeposit.autoCorrect();
        } else if (MoneyWiseBasicResource.ASSET_PARENT.equals(myField)) {
            /* Update the Parent */
            myDeposit.setParent(pUpdate.getValue(MoneyWisePayee.class));
        } else if (MoneyWiseStaticDataType.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myDeposit.setAssetCurrency(pUpdate.getValue(MoneyWiseCurrency.class));
        } else if (MoneyWiseBasicResource.ASSET_CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myDeposit.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAccountInfoClass.MATURITY.equals(myField)) {
            /* Update the Maturity */
            myDeposit.setMaturity(pUpdate.getValue(OceanusDate.class));
        } else if (MoneyWiseAccountInfoClass.SORTCODE.equals(myField)) {
            /* Update the SortCode */
            myDeposit.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.ACCOUNT.equals(myField)) {
            /* Update the Account */
            myDeposit.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.REFERENCE.equals(myField)) {
            /* Update the Reference */
            myDeposit.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.OPENINGBALANCE.equals(myField)) {
            /* Update the OpeningBalance */
            myDeposit.setOpeningBalance(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseAccountInfoClass.NOTES.equals(myField)) {
            /* Update the Notes */
            myDeposit.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseDeposit myItem = getItem();
        final MoneyWisePayee myParent = myItem.getParent();
        if (!pUpdates) {
            final MoneyWiseDepositCategory myCategory = myItem.getCategory();
            final MoneyWiseCurrency myCurrency = myItem.getAssetCurrency();
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
     *
     * @param pMenu    the menu
     * @param pDeposit the deposit to build for
     */
    public void buildCategoryMenu(final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu,
                                  final MoneyWiseDeposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseDepositCategory myCurr = pDeposit.getCategory();
        TethysUIScrollItem<MoneyWiseAssetCategory> myActive = null;

        /* Access Deposit Categories */
        final MoneyWiseDepositCategoryList myCategories = getDataList(MoneyWiseBasicDataType.DEPOSITCATEGORY, MoneyWiseDepositCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<MoneyWiseAssetCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<MoneyWiseDepositCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDepositCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            final boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(MoneyWiseDepositCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseDepositCategory myParent = myCategory.getParentCategory();
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
     * Build the parent menu for an item.
     *
     * @param pMenu    the menu
     * @param pDeposit the deposit to build for
     */
    public void buildParentMenu(final TethysUIScrollMenu<MoneyWisePayee> pMenu,
                                final MoneyWiseDeposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseDepositCategoryClass myType = pDeposit.getCategoryClass();
        final MoneyWisePayee myCurr = pDeposit.getParent();
        TethysUIScrollItem<MoneyWisePayee> myActive = null;

        /* Access Payees */
        final MoneyWisePayeeList myPayees = getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);

        /* Loop through the Payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getCategoryClass().canParentDeposit(myType);
            bIgnore |= myPayee.isClosed();
            if (bIgnore) {
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
     *
     * @param pMenu    the menu
     * @param pDeposit the deposit to build for
     */
    public void buildCurrencyMenu(final TethysUIScrollMenu<MoneyWiseCurrency> pMenu,
                                  final MoneyWiseDeposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCurrency myCurr = pDeposit.getAssetCurrency();
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
