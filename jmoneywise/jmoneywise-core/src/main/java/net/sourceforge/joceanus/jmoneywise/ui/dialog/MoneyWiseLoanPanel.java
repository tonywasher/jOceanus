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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.ids.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
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
 * Panel to display/edit/create a Loan.
 */
public class MoneyWiseLoanPanel
        extends MoneyWiseItemPanel<Loan> {
    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<Loan> theFieldSet;

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
    public MoneyWiseLoanPanel(final TethysUIFactory<?> pFactory,
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
        theFieldSet.addField(MoneyWiseAssetDataId.NAME, myName, Loan::getName);
        theFieldSet.addField(MoneyWiseAssetDataId.DESC, myDesc, Loan::getDesc);
        theFieldSet.addField(MoneyWiseAssetDataId.CATEGORY, myCategoryButton, Loan::getCategory);
        theFieldSet.addField(MoneyWiseAssetDataId.PARENT, myParentButton, Loan::getParent);
        theFieldSet.addField(MoneyWiseAssetDataId.CURRENCY, myCurrencyButton, Loan::getAssetCurrency);
        theFieldSet.addField(MoneyWiseAssetDataId.CLOSED, myClosedButton, Loan::isClosed);

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
        final TethysUICharArrayEditField mySortCode = myFields.newCharArrayField();
        final TethysUICharArrayEditField myAccount = myFields.newCharArrayField();
        final TethysUICharArrayEditField myReference = myFields.newCharArrayField();
        final TethysUIMoneyEditField myOpening = myFields.newMoneyField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.LOANSORTCODE, mySortCode, Loan::getSortCode);
        theFieldSet.addField(MoneyWiseAssetDataId.LOANACCOUNT, myAccount, Loan::getAccount);
        theFieldSet.addField(MoneyWiseAssetDataId.LOANREFERENCE, myReference, Loan::getReference);
        theFieldSet.addField(MoneyWiseAssetDataId.LOANOPENINGBALANCE, myOpening, Loan::getOpeningBalance);

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
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAssetDataId.LOANNOTES, myNotes, Loan::getNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Loan myItem = getItem();
        if (myItem != null) {
            final LoanList myLoans = getDataList(MoneyWiseDataType.LOAN, LoanList.class);
            setItem(myLoans.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final Loan myLoan = getItem();
        final boolean bIsClosed = myLoan.isClosed();
        final boolean bIsActive = myLoan.isActive();
        final boolean bIsRelevant = myLoan.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                ? !myLoan.getParent().isClosed()
                : !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myLoan.getDesc() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myLoan.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.LOANSORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myLoan.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.LOANACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myLoan.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.LOANREFERENCE, bShowReference);
        final boolean bHasOpening = myLoan.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.LOANOPENINGBALANCE, bShowOpening);
        final boolean bShowNotes = isEditable || myLoan.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.LOANNOTES, bShowNotes);

        /* Category/Currency cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CATEGORY, bIsChangeable);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CURRENCY, bIsChangeable && !bHasOpening);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.LOANOPENINGBALANCE, bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final Loan myLoan = getItem();

        /* Process updates */
        if (MoneyWiseAssetDataId.NAME.equals(myField)) {
            /* Update the Name */
            myLoan.setName(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.DESC.equals(myField)) {
            /* Update the Description */
            myLoan.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.CATEGORY.equals(myField)) {
            /* Update the Category */
            myLoan.setCategory(pUpdate.getValue(LoanCategory.class));
            myLoan.autoCorrect(getUpdateSet());
        } else if (MoneyWiseAssetDataId.PARENT.equals(myField)) {
            /* Update the Parent */
            myLoan.setParent(pUpdate.getValue(Payee.class));
        } else if (MoneyWiseAssetDataId.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myLoan.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (MoneyWiseAssetDataId.CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myLoan.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAssetDataId.LOANSORTCODE.equals(myField)) {
            /* Update the SortCode */
            myLoan.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.LOANACCOUNT.equals(myField)) {
            /* Update the Account */
            myLoan.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.LOANREFERENCE.equals(myField)) {
            /* Update the Reference */
            myLoan.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.LOANOPENINGBALANCE.equals(myField)) {
            /* Update the OpeningBalance */
            myLoan.setOpeningBalance(pUpdate.getValue(TethysMoney.class));
        } else if (MoneyWiseAssetDataId.LOANNOTES.equals(myField)) {
            /* Update the Notes */
            myLoan.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final Loan myItem = getItem();
        final Payee myParent = myItem.getParent();
        if (!pUpdates) {
            final LoanCategory myCategory = myItem.getCategory();
            final AssetCurrency myCurrency = myItem.getAssetCurrency();
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
    public void buildCategoryMenu(final TethysUIScrollMenu<AssetCategory> pMenu,
                                  final Loan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final LoanCategory myCurr = pLoan.getCategory();
        TethysUIScrollItem<AssetCategory> myActive = null;

        /* Access Loan Categories */
        final LoanCategoryList myCategories = getDataList(MoneyWiseDataType.LOANCATEGORY, LoanCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysUIScrollSubMenu<AssetCategory>> myMap = new HashMap<>();

        /* Loop through the available category values */
        final Iterator<LoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final LoanCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            final boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(LoanCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            final LoanCategory myParent = myCategory.getParentCategory();
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
     * @param pLoan the loan to build for
     */
    public void buildParentMenu(final TethysUIScrollMenu<Payee> pMenu,
                                final Loan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final LoanCategoryClass myType = pLoan.getCategoryClass();
        final Payee myCurr = pLoan.getParent();
        TethysUIScrollItem<Payee> myActive = null;

        /* Access Payees */
        final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getCategoryClass().canParentLoan(myType);
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
     * @param pLoan the loan to build for
     */
    public void buildCurrencyMenu(final TethysUIScrollMenu<AssetCurrency> pMenu,
                                  final Loan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pLoan.getAssetCurrency();
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
