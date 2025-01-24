/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.dialog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan.MoneyWiseLoanList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory.MoneyWiseLoanCategoryList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseLoanCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollSubMenu;

/**
 * Panel to display/edit/create a Loan.
 */
public class MoneyWiseLoanPanel
        extends MoneyWiseAssetPanel<MoneyWiseLoan> {
    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<MoneyWiseLoan> theFieldSet;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pEditSet the edit set
     * @param pOwner the owning table
     */
    public MoneyWiseLoanPanel(final TethysUIFactory<?> pFactory,
                              final PrometheusEditSet pEditSet,
                              final MoneyWiseAssetTable<MoneyWiseLoan> pOwner) {
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
        final TethysUIScrollButtonField<MoneyWisePayee> myParentButton = myFields.newScrollField(MoneyWisePayee.class);
        final TethysUIScrollButtonField<MoneyWiseCurrency> myCurrencyButton = myFields.newScrollField(MoneyWiseCurrency.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseLoan::getName);
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseLoan::getDesc);
        theFieldSet.addField(MoneyWiseBasicResource.CATEGORY_NAME, myCategoryButton, MoneyWiseLoan::getCategory);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_PARENT, myParentButton, MoneyWiseLoan::getParent);
        theFieldSet.addField(MoneyWiseStaticDataType.CURRENCY, myCurrencyButton, MoneyWiseLoan::getAssetCurrency);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_CLOSED, myClosedButton, MoneyWiseLoan::isClosed);

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
     * @param pFactory the GUI factory
     */
    private void buildAccountPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_ACCOUNT);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayEditField mySortCode = myFields.newCharArrayField();
        final TethysUICharArrayEditField myAccount = myFields.newCharArrayField();
        final TethysUICharArrayEditField myReference = myFields.newCharArrayField();
        final TethysUIMoneyEditField myOpening = myFields.newMoneyField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAccountInfoClass.SORTCODE, mySortCode, MoneyWiseLoan::getSortCode);
        theFieldSet.addField(MoneyWiseAccountInfoClass.ACCOUNT, myAccount, MoneyWiseLoan::getAccount);
        theFieldSet.addField(MoneyWiseAccountInfoClass.REFERENCE, myReference, MoneyWiseLoan::getReference);
        theFieldSet.addField(MoneyWiseAccountInfoClass.OPENINGBALANCE, myOpening, MoneyWiseLoan::getOpeningBalance);

        /* Configure the currency */
        myOpening.setDeemedCurrency(() -> getItem().getCurrency());

        /* Configure validation checks */
        mySortCode.setValidator(this::isValidSortCode);
        myAccount.setValidator(this::isValidAccount);
        myReference.setValidator(this::isValidReference);
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
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAccountInfoClass.NOTES, myNotes, MoneyWiseLoan::getNotes);

        /* Configure validation checks */
        myNotes.setValidator(this::isValidNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWiseLoan myItem = getItem();
        if (myItem != null) {
            final MoneyWiseLoanList myLoans = getDataList(MoneyWiseBasicDataType.LOAN, MoneyWiseLoanList.class);
            setItem(myLoans.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final MoneyWiseLoan myLoan = getItem();
        final boolean bIsClosed = myLoan.isClosed();
        final boolean bIsActive = myLoan.isActive();
        final boolean bIsRelevant = myLoan.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.ASSET_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                ? !myLoan.getParent().isClosed()
                : !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myLoan.getDesc() != null;
        theFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myLoan.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.SORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myLoan.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.ACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myLoan.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.REFERENCE, bShowReference);
        final boolean bHasOpening = myLoan.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.OPENINGBALANCE, bShowOpening);
        final boolean bShowNotes = isEditable || myLoan.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.NOTES, bShowNotes);

        /* Category/Currency cannot be changed if the item is active */
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
        final MoneyWiseLoan myLoan = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the Name */
            myLoan.setName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myLoan.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_NAME.equals(myField)) {
            /* Update the Category */
            myLoan.setCategory(pUpdate.getValue(MoneyWiseLoanCategory.class));
            myLoan.autoCorrect(getEditSet());
        } else if (MoneyWiseBasicResource.ASSET_PARENT.equals(myField)) {
            /* Update the Parent */
            myLoan.setParent(pUpdate.getValue(MoneyWisePayee.class));
        } else if (MoneyWiseStaticDataType.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myLoan.setAssetCurrency(pUpdate.getValue(MoneyWiseCurrency.class));
        } else if (MoneyWiseBasicResource.ASSET_CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myLoan.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAccountInfoClass.SORTCODE.equals(myField)) {
            /* Update the SortCode */
            myLoan.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.ACCOUNT.equals(myField)) {
            /* Update the Account */
            myLoan.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.REFERENCE.equals(myField)) {
            /* Update the Reference */
            myLoan.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.OPENINGBALANCE.equals(myField)) {
            /* Update the OpeningBalance */
            myLoan.setOpeningBalance(pUpdate.getValue(OceanusMoney.class));
        } else if (MoneyWiseAccountInfoClass.NOTES.equals(myField)) {
            /* Update the Notes */
            myLoan.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseLoan myItem = getItem();
        final MoneyWisePayee myParent = myItem.getParent();
        if (!pUpdates) {
            final MoneyWiseLoanCategory myCategory = myItem.getCategory();
            final MoneyWiseCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCategory);
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category menu for an item.
     * @param pMenu the menu
     * @param pLoan the loan to build for
     */
    public void buildCategoryMenu(final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu,
                                  final MoneyWiseLoan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseLoanCategory myCurr = pLoan.getCategory();
        TethysUIScrollItem<MoneyWiseAssetCategory> myActive = null;

        /* Access Loan Categories */
        final MoneyWiseLoanCategoryList myCategories = getDataList(MoneyWiseBasicDataType.LOANCATEGORY, MoneyWiseLoanCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<MoneyWiseAssetCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<MoneyWiseLoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseLoanCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            final boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(MoneyWiseLoanCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final MoneyWiseLoanCategory myParent = myCategory.getParentCategory();
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
     * @param pMenu the menu
     * @param pLoan the loan to build for
     */
    public void buildParentMenu(final TethysUIScrollMenu<MoneyWisePayee> pMenu,
                                final MoneyWiseLoan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseLoanCategoryClass myType = pLoan.getCategoryClass();
        final MoneyWisePayee myCurr = pLoan.getParent();
        TethysUIScrollItem<MoneyWisePayee> myActive = null;

        /* Access Payees */
        final MoneyWisePayeeList myPayees = getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);

        /* Loop through the Payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getCategoryClass().canParentLoan(myType);
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
     * @param pMenu the menu
     * @param pLoan the loan to build for
     */
    public void buildCurrencyMenu(final TethysUIScrollMenu<MoneyWiseCurrency> pMenu,
                                  final MoneyWiseLoan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCurrency myCurr = pLoan.getAssetCurrency();
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
