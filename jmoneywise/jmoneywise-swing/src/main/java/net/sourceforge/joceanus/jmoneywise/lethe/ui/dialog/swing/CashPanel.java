/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldSet;
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
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTextArea;

/**
 * Panel to display/edit/create a Cash.
 */
public class CashPanel
        extends MoneyWiseItemPanel<Cash> {
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
                     final MetisSwingFieldManager pFieldMgr,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Build the main panel */
        final MoneyWiseDataPanel myPanel = buildMainPanel(pFactory);

        /* Build the detail panel */
        buildXtrasPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Define the panel */
        defineMainPanel(myPanel);
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private MoneyWiseDataPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(Cash.NAMELEN);

        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<CashCategory> myCategoryButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        final TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* Assign the fields to the panel */
        myPanel.addField(Cash.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(Cash.FIELD_DESC, MetisDataType.STRING, myDesc);
        myPanel.addField(Cash.FIELD_CATEGORY, CashCategory.class, myCategoryButton);
        myPanel.addField(Cash.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        myPanel.addField(Cash.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Layout the panel */
        myPanel.compactPanel();

        /* Configure the menuBuilders */
        myCategoryButton.setMenuConfigurator(c -> buildCategoryMenu(c, getItem()));
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
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_DETAILS, Cash.NAMELEN >> 1);

        /* Allocate fields */
        final TethysSwingStringTextField myOpening = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<TransactionCategory> myAutoExpenseButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<Payee> myAutoPayeeButton = pFactory.newScrollButton();

        /* Assign the fields to the panel */
        myTab.addField(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), TransactionCategory.class, myAutoExpenseButton);
        myTab.addField(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), Payee.class, myAutoPayeeButton);
        myTab.addField(CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), MetisDataType.MONEY, myOpening);

        /* Layout the panel */
        myTab.compactPanel();

        /* Configure the menuBuilders */
        myAutoExpenseButton.setMenuConfigurator(c -> buildAutoExpenseMenu(c, getItem()));
        myAutoPayeeButton.setMenuConfigurator(c -> buildAutoPayeeMenu(c, getItem()));
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(AccountInfoClass.NOTES.toString(), Cash.NAMELEN);

        /* Allocate fields */
        final TethysSwingTextArea myNotes = pFactory.newTextArea();
        final TethysSwingScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Assign the fields to the panel */
        myTab.addField(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Layout the panel */
        myTab.compactPanel();
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
        /* Access the fieldSet */
        final MetisSwingFieldSet<Cash> myFieldSet = getFieldSet();

        /* Access the item */
        final Cash myCash = getItem();
        final boolean bIsClosed = myCash.isClosed();
        final boolean bIsActive = myCash.isActive();
        final boolean bIsRelevant = myCash.isRelevant();
        final boolean isAutoExpense = myCash.isAutoExpense();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        myFieldSet.setVisibility(Cash.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        myFieldSet.setEditable(Cash.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myCash.getDesc() != null;
        myFieldSet.setVisibility(Cash.FIELD_DESC, bShowDesc);

        /* AutoExpense/Payee is hidden unless we are autoExpense */
        final MetisField myAutoExpenseField = CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE);
        final MetisField myAutoPayeeField = CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE);
        myFieldSet.setVisibility(myAutoExpenseField, isAutoExpense);
        myFieldSet.setVisibility(myAutoPayeeField, isAutoExpense);

        /* OpeningBalance is hidden if we are autoExpense */
        final MetisField myOpeningField = CashInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
        final boolean bHasOpening = myCash.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        myFieldSet.setVisibility(myOpeningField, !isAutoExpense && bShowOpening);

        /* Determine whether to show notes */
        final boolean bShowNotes = isEditable || myCash.getNotes() != null;
        myFieldSet.setVisibility(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Category/Currency cannot be changed if the item is active */
        myFieldSet.setEditable(Cash.FIELD_CATEGORY, bIsChangeable);
        myFieldSet.setEditable(Cash.FIELD_CURRENCY, bIsChangeable && !bHasOpening);

        /* AutoExpense/Payee cannot be changed for closed item */
        final boolean canEdit = isEditable && !bIsClosed;
        myFieldSet.setEditable(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), canEdit);
        myFieldSet.setEditable(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), canEdit);

        /* Set currency for opening balance */
        if (!isAutoExpense) {
            myFieldSet.setAssumedCurrency(myOpeningField, myCash.getCurrency());
        }
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisField myField = pUpdate.getField();
        final Cash myCash = getItem();

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
    public void buildCategoryMenu(final TethysScrollMenu<CashCategory, Icon> pMenu,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final CashCategory myCurr = pCash.getCategory();
        TethysScrollMenuItem<CashCategory> myActive = null;

        /* Access Cash Categories */
        final CashCategoryList myCategories = getDataList(MoneyWiseDataType.CASHCATEGORY, CashCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysScrollSubMenu<CashCategory, Icon>> myMap = new HashMap<>();

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
            TethysScrollSubMenu<CashCategory, Icon> myMenu = myMap.get(myParentName);

            /* If this is a new subMenu */
            if (myMenu == null) {
                /* Create a new subMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<CashCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
        final TransactionCategory myCurr = pCash.getAutoExpense();
        TethysScrollMenuItem<TransactionCategory> myActive = null;

        /* Access Transaction Categories */
        final TransactionCategoryList myCategories = getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysScrollSubMenu<TransactionCategory, Icon>> myMap = new HashMap<>();

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
            TethysScrollSubMenu<TransactionCategory, Icon> myMenu = myMap.get(myParentName);

            /* If this is a new subMenu */
            if (myMenu == null) {
                /* Create a new subMMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<TransactionCategory> myItem = myMenu.getSubMenu().addItem(myCategory);

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
        final Payee myCurr = pCash.getAutoPayee();
        TethysScrollMenuItem<Payee> myActive = null;

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
     * @param pCash the cash to build for
     */
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency, Icon> pMenu,
                                  final Cash pCash) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pCash.getAssetCurrency();
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
