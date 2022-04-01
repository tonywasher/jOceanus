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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.dialog.swing;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldSetBase.MetisLetheFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog.MoneyWiseDepositRateTable;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTextArea;

/**
 * Panel to display/edit/create a Deposit.
 */
public class DepositPanel
        extends MoneyWiseItemPanel<Deposit> {
    /**
     * Rates Tab Title.
     */
    private static final String TAB_RATES = MoneyWiseUIResource.DEPOSITPANEL_TAB_RATES.getValue();

    /**
     * DepositRate Table.
     */
    private final MoneyWiseDepositRateTable theRates;

    /**
     * Table tab item.
     */
    private final MoneyWiseDataTabTable theRatesTab;

    /**
     * The Closed State.
     */
    private Boolean theClosedState = Boolean.FALSE;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pView the data view
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public DepositPanel(final TethysSwingGuiFactory pFactory,
                        final MoneyWiseView pView,
                        final MetisSwingFieldManager pFieldMgr,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet,
                        final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Build the main panel */
        final MoneyWiseDataPanel myPanel = buildMainPanel(pFactory);

        /* Build the detail panel */
        buildXtrasPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Create the DepositRates table */
        theRates = new MoneyWiseDepositRateTable(pView, getUpdateSet(), pError);
        theRatesTab = new MoneyWiseDataTabTable(TAB_RATES, theRates);

        /* Define the panel */
        defineMainPanel(myPanel);

        /* Create the listeners */
        theRates.getEventRegistrar().addEventListener(e -> {
            updateActions();
            fireStateChanged();
        });
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private MoneyWiseDataPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(Deposit.NAMELEN);

        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<DepositCategory> myCategoryButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<Payee> myParentButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        final TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* Assign the fields to the panel */
        myPanel.addField(Deposit.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(Deposit.FIELD_DESC, MetisDataType.STRING, myDesc);
        myPanel.addField(Deposit.FIELD_CATEGORY, DepositCategory.class, myCategoryButton);
        myPanel.addField(Deposit.FIELD_PARENT, Payee.class, myParentButton);
        myPanel.addField(Deposit.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        myPanel.addField(Deposit.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Layout the panel */
        myPanel.compactPanel();

        /* Configure the menuBuilders */
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extras subPanel.
     * @param pFactory the GUI factory
     */
    private void buildXtrasPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_DETAILS, Deposit.NAMELEN >> 1);

        /* Allocate fields */
        final TethysSwingDateButtonManager myMaturity = pFactory.newDateButton();
        final TethysSwingStringTextField mySortCode = pFactory.newStringField();
        final TethysSwingStringTextField myAccount = pFactory.newStringField();
        final TethysSwingStringTextField myReference = pFactory.newStringField();
        final TethysSwingStringTextField myOpening = pFactory.newStringField();

        /* Assign the fields to the panel */
        myTab.addField(DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY), myMaturity);
        myTab.addField(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), MetisDataType.CHARARRAY, mySortCode);
        myTab.addField(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), MetisDataType.CHARARRAY, myAccount);
        myTab.addField(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), MetisDataType.CHARARRAY, myReference);
        myTab.addField(DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), MetisDataType.MONEY, myOpening);

        /* Layout the panel */
        myTab.compactPanel();
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(AccountInfoClass.NOTES.toString(), Deposit.NAMELEN);

        /* Allocate fields */
        final TethysSwingTextArea myNotes = pFactory.newTextArea();
        final TethysSwingScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Assign the fields to the panel */
        myTab.addField(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Layout the panel */
        myTab.compactPanel();
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
        /* Access the fieldSet */
        final MetisSwingFieldSet<Deposit> myFieldSet = getFieldSet();

        /* Access the item */
        final Deposit myDeposit = getItem();
        final boolean bIsClosed = myDeposit.isClosed();
        final boolean bIsActive = myDeposit.isActive();
        final boolean bIsRelevant = myDeposit.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        myFieldSet.setVisibility(Deposit.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                                              ? !myDeposit.getParent().isClosed()
                                              : !bIsRelevant;
        myFieldSet.setEditable(Deposit.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myDeposit.getDesc() != null;
        myFieldSet.setVisibility(Deposit.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myDeposit.getSortCode() != null;
        myFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        final boolean bShowAccount = isEditable || myDeposit.getAccount() != null;
        myFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        final boolean bShowReference = isEditable || myDeposit.getReference() != null;
        myFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        final boolean bHasOpening = myDeposit.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        final MetisLetheField myOpeningField = DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
        myFieldSet.setVisibility(myOpeningField, bShowOpening);
        final boolean bShowNotes = isEditable || myDeposit.getNotes() != null;
        myFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Maturity is only visible if the item is a bond */
        final boolean bShowMaturity = DepositCategoryClass.BOND.equals(myDeposit.getCategoryClass());
        final MetisLetheField myMaturityField = DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY);
        myFieldSet.setVisibility(myMaturityField, bShowMaturity);
        myFieldSet.setEditable(myMaturityField, isEditable && !bIsClosed);

        /* Category, Currency, and OpeningBalance cannot be changed if the item is active */
        myFieldSet.setEditable(Deposit.FIELD_CATEGORY, bIsChangeable);
        myFieldSet.setEditable(Deposit.FIELD_CURRENCY, bIsChangeable && !bHasOpening);
        myFieldSet.setEditable(myOpeningField, bIsChangeable);

        /* Set currency for opening balance */
        myFieldSet.setAssumedCurrency(myOpeningField, myDeposit.getCurrency());

        /* Set editable value for parent */
        myFieldSet.setEditable(Deposit.FIELD_PARENT, isEditable && !bIsClosed);

        /* Set the table visibility */
        theRatesTab.setRequireVisible(isEditable || !theRates.isViewEmpty());
    }

    @Override
    protected void updateField(final MetisLetheFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisLetheField myField = pUpdate.getField();
        final Deposit myDeposit = getItem();

        /* Process updates */
        if (myField.equals(Deposit.FIELD_NAME)) {
            /* Update the Name */
            myDeposit.setName(pUpdate.getString());
        } else if (myField.equals(Deposit.FIELD_DESC)) {
            /* Update the Description */
            myDeposit.setDescription(pUpdate.getString());
        } else if (myField.equals(Deposit.FIELD_CATEGORY)) {
            /* Update the Category */
            myDeposit.setCategory(pUpdate.getValue(DepositCategory.class));
            myDeposit.autoCorrect(getUpdateSet());
        } else if (myField.equals(Deposit.FIELD_PARENT)) {
            /* Update the Parent */
            myDeposit.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Deposit.FIELD_CURRENCY)) {
            /* Update the Currency */
            myDeposit.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(Deposit.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myDeposit.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (DepositInfoSet.getClassForField(myField)) {
                case MATURITY:
                    myDeposit.setMaturity(pUpdate.getDate());
                    break;
                case OPENINGBALANCE:
                    myDeposit.setOpeningBalance(pUpdate.getMoney());
                    break;
                case SORTCODE:
                    myDeposit.setSortCode(pUpdate.getCharArray());
                    break;
                case ACCOUNT:
                    myDeposit.setAccount(pUpdate.getCharArray());
                    break;
                case REFERENCE:
                    myDeposit.setReference(pUpdate.getCharArray());
                    break;
                case NOTES:
                    myDeposit.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
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
