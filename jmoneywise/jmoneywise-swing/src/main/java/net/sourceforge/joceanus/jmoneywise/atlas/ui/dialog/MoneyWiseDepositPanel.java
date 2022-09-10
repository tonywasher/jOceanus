/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseNewItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCharArrayEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysMoneyEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStringEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;

/**
 * Panel to display/edit/create a Deposit.
 */
public class MoneyWiseDepositPanel
        extends MoneyWiseNewItemPanel<Deposit> {
    /**
     * Rates Tab Title.
     */
    private static final String TAB_RATES = MoneyWiseUIResource.DEPOSITPANEL_TAB_RATES.getValue();

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<Deposit> theFieldSet;

    /**
     * DepositRate Table.
     */
    private final MoneyWiseDepositRateTable theRates;

    /**
     * Table tab item.
     */
    //private final MoneyWiseDataTabTable theRatesTab;

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
    public MoneyWiseDepositPanel(final TethysGuiFactory pFactory,
                                 final MoneyWiseView pView,
                                 final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                 final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the detail panel */
        buildXtrasPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Create the DepositRates table */
        theRates = new MoneyWiseDepositRateTable(pView, getUpdateSet(), pError);
        //theRatesTab = new MoneyWiseDataTabTable(TAB_RATES, theRates);

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
    private void buildMainPanel(final TethysGuiFactory pFactory) {
        /* Create the text fields */
        final TethysStringEditField myName = pFactory.newStringField();
        final TethysStringEditField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysScrollButtonField<DepositCategory> myCategoryButton = pFactory.newScrollField(DepositCategory.class);
        final TethysScrollButtonField<Payee> myParentButton = pFactory.newScrollField(Payee.class);
        final TethysScrollButtonField<AssetCurrency> myCurrencyButton = pFactory.newScrollField(AssetCurrency.class);
        final TethysIconButtonField<Boolean> myClosedButton = pFactory.newIconField(Boolean.class);

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
        final Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));
    }

    /**
     * Build extras subPanel.
     * @param pFactory the GUI factory
     */
    private void buildXtrasPanel(final TethysGuiFactory pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_DETAILS);

        /* Allocate fields */
        final TethysDateButtonField myMaturity = pFactory.newDateField();
        final TethysCharArrayEditField mySortCode = pFactory.newCharArrayField();
        final TethysCharArrayEditField myAccount = pFactory.newCharArrayField();
        final TethysCharArrayEditField myReference = pFactory.newCharArrayField();
        final TethysMoneyEditField myOpening = pFactory.newMoneyField();

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
    private void buildNotesPanel(final TethysGuiFactory pFactory) {
        /* Allocate fields */
        final TethysCharArrayTextAreaField myNotes = pFactory.newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(AccountInfoClass.NOTES.toString(), MoneyWiseAssetDataId.DEPOSITNOTES, myNotes, Deposit::getNotes);
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

        /* Set the table visibility */
        //theRatesTab.setRequireVisible(isEditable || !theRates.isViewEmpty());
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
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
    public void setItem(final Deposit pItem) {
        /* Update the rates */
        theRates.setDeposit(pItem);

        /* Pass call onwards */
        super.setItem(pItem);
    }

    @Override
    public void setNewItem(final Deposit pItem) {
        /* Update the rates */
        theRates.setDeposit(pItem);

        /* Pass call onwards */
        super.setNewItem(pItem);
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
    public void buildCategoryMenu(final TethysScrollMenu<DepositCategory> pMenu,
                                  final Deposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final DepositCategory myCurr = pDeposit.getCategory();
        TethysScrollMenuItem<DepositCategory> myActive = null;

        /* Access Deposit Categories */
        final DepositCategoryList myCategories = getDataList(MoneyWiseDataType.DEPOSITCATEGORY, DepositCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysScrollSubMenu<DepositCategory>> myMap = new HashMap<>();

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
            TethysScrollSubMenu<DepositCategory> myMenu = myMap.get(myParentName);

            /* If this is a new subMenu */
            if (myMenu == null) {
                /* Create a new subMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<DepositCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
    public void buildParentMenu(final TethysScrollMenu<Payee> pMenu,
                                final Deposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final DepositCategoryClass myType = pDeposit.getCategoryClass();
        final Payee myCurr = pDeposit.getParent();
        TethysScrollMenuItem<Payee> myActive = null;

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
            final TethysScrollMenuItem<Payee> myItem = pMenu.addItem(myPayee);

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
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency> pMenu,
                                  final Deposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pDeposit.getAssetCurrency();
        TethysScrollMenuItem<AssetCurrency> myActive = null;

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
            final TethysScrollMenuItem<AssetCurrency> myItem = pMenu.addItem(myCurrency);

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
