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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
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
 * Panel to display/edit/create a Loan.
 */
public class LoanPanel
        extends MoneyWiseItemPanel<Loan> {
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
    public LoanPanel(final TethysSwingGuiFactory pFactory,
                     final MetisFieldManager pFieldMgr,
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
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(Loan.NAMELEN);

        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<LoanCategory> myCategoryButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<Payee> myParentButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        final TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* Assign the fields to the panel */
        myPanel.addField(Loan.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(Loan.FIELD_DESC, MetisDataType.STRING, myDesc);
        myPanel.addField(Loan.FIELD_CATEGORY, LoanCategory.class, myCategoryButton);
        myPanel.addField(Loan.FIELD_PARENT, Payee.class, myParentButton);
        myPanel.addField(Loan.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        myPanel.addField(Loan.FIELD_CLOSED, Boolean.class, myClosedButton);

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
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_DETAILS, Loan.NAMELEN >> 1);

        /* Allocate fields */
        final TethysSwingStringTextField mySortCode = pFactory.newStringField();
        final TethysSwingStringTextField myAccount = pFactory.newStringField();
        final TethysSwingStringTextField myReference = pFactory.newStringField();
        final TethysSwingStringTextField myOpening = pFactory.newStringField();

        /* Assign the fields to the panel */
        myTab.addField(LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), MetisDataType.CHARARRAY, mySortCode);
        myTab.addField(LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), MetisDataType.CHARARRAY, myAccount);
        myTab.addField(LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), MetisDataType.CHARARRAY, myReference);
        myTab.addField(LoanInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), MetisDataType.MONEY, myOpening);

        /* Layout the panel */
        myTab.compactPanel();
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysSwingGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(AccountInfoClass.NOTES.toString(), Loan.NAMELEN);

        /* Allocate fields */
        final TethysSwingTextArea myNotes = pFactory.newTextArea();
        final TethysSwingScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Assign the fields to the panel */
        myTab.addField(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Layout the panel */
        myTab.compactPanel();
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
        /* Access the fieldSet */
        final MetisFieldSet<Loan> myFieldSet = getFieldSet();

        /* Access the item */
        final Loan myLoan = getItem();
        final boolean bIsClosed = myLoan.isClosed();
        final boolean bIsActive = myLoan.isActive();
        final boolean bIsRelevant = myLoan.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        myFieldSet.setVisibility(Loan.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                                              ? !myLoan.getParent().isClosed()
                                              : !bIsRelevant;
        myFieldSet.setEditable(Loan.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myLoan.getDesc() != null;
        myFieldSet.setVisibility(Loan.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myLoan.getSortCode() != null;
        myFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        final boolean bShowAccount = isEditable || myLoan.getAccount() != null;
        myFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        final boolean bShowReference = isEditable || myLoan.getReference() != null;
        myFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        final boolean bHasOpening = myLoan.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        final MetisField myOpeningField = LoanInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
        myFieldSet.setVisibility(myOpeningField, bShowOpening);
        final boolean bShowNotes = isEditable || myLoan.getNotes() != null;
        myFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Category/Currency cannot be changed if the item is active */
        myFieldSet.setEditable(Loan.FIELD_CATEGORY, bIsChangeable);
        myFieldSet.setEditable(Loan.FIELD_CURRENCY, bIsChangeable && !bHasOpening);
        myFieldSet.setEditable(myOpeningField, bIsChangeable);

        /* Set editable value for parent */
        myFieldSet.setEditable(Loan.FIELD_PARENT, isEditable && !bIsClosed);

        /* Set currency for opening balance */
        myFieldSet.setAssumedCurrency(myOpeningField, myLoan.getCurrency());
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisField myField = pUpdate.getField();
        final Loan myLoan = getItem();

        /* Process updates */
        if (myField.equals(Loan.FIELD_NAME)) {
            /* Update the Name */
            myLoan.setName(pUpdate.getString());
        } else if (myField.equals(Loan.FIELD_DESC)) {
            /* Update the Description */
            myLoan.setDescription(pUpdate.getString());
        } else if (myField.equals(Loan.FIELD_CATEGORY)) {
            /* Update the Category */
            myLoan.setLoanCategory(pUpdate.getValue(LoanCategory.class));
            myLoan.autoCorrect(getUpdateSet());
        } else if (myField.equals(Loan.FIELD_PARENT)) {
            /* Update the Parent */
            myLoan.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Loan.FIELD_CURRENCY)) {
            /* Update the Currency */
            myLoan.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(Loan.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myLoan.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (LoanInfoSet.getClassForField(myField)) {
                case OPENINGBALANCE:
                    myLoan.setOpeningBalance(pUpdate.getMoney());
                    break;
                case SORTCODE:
                    myLoan.setSortCode(pUpdate.getCharArray());
                    break;
                case ACCOUNT:
                    myLoan.setAccount(pUpdate.getCharArray());
                    break;
                case REFERENCE:
                    myLoan.setReference(pUpdate.getCharArray());
                    break;
                case NOTES:
                    myLoan.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
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
    public void buildCategoryMenu(final TethysScrollMenu<LoanCategory, Icon> pMenu,
                                  final Loan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final LoanCategory myCurr = pLoan.getCategory();
        TethysScrollMenuItem<LoanCategory> myActive = null;

        /* Access Loan Categories */
        final LoanCategoryList myCategories = getDataList(MoneyWiseDataType.LOANCATEGORY, LoanCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysScrollSubMenu<LoanCategory, Icon>> myMap = new HashMap<>();

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
            TethysScrollSubMenu<LoanCategory, Icon> myMenu = myMap.get(myParentName);

            /* If this is a new subMenu */
            if (myMenu == null) {
                /* Create a new subMenu and add it to the popUp */
                myMenu = pMenu.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new MenuItem and add it to the popUp */
            final TethysScrollMenuItem<LoanCategory> myItem = myMenu.getSubMenu().addItem(myCategory, myCategory.getSubCategory());

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
    public void buildParentMenu(final TethysScrollMenu<Payee, Icon> pMenu,
                                final Loan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final LoanCategoryClass myType = pLoan.getCategoryClass();
        final Payee myCurr = pLoan.getParent();
        TethysScrollMenuItem<Payee> myActive = null;

        /* Access Payees */
        final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentLoan(myType);
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
     * @param pLoan the loan to build for
     */
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency, Icon> pMenu,
                                  final Loan pLoan) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pLoan.getAssetCurrency();
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
