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

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
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
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;
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
     * The Field Set.
     */
    private final MetisFieldSet<Deposit> theFieldSet;

    /**
     * DepositRate Table.
     */
    private final DepositRateTable theRates;

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
    public DepositPanel(final TethysSwingGuiFactory pFactory,
                        final MetisFieldManager pFieldMgr,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet,
                        final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        final JPanel myMainPanel = buildMainPanel(pFactory);

        /* Create a tabbedPane */
        final JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel(pFactory);
        myTabs.add(TAB_DETAILS, myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel(pFactory);
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Create the DepositRates table */
        theRates = new DepositRateTable(pFactory, pFieldMgr, getUpdateSet(), pError);
        myTabs.add(TAB_RATES, theRates.getNode());

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

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
    private JPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<DepositCategory> myCategoryButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<Payee> myParentButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        final TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* restrict the fields */
        restrictField(myName, Deposit.NAMELEN);
        restrictField(myDesc, Deposit.NAMELEN);
        restrictField(myCategoryButton, Deposit.NAMELEN);
        restrictField(myCurrencyButton, Deposit.NAMELEN);
        restrictField(myParentButton, Deposit.NAMELEN);
        restrictField(myClosedButton, Deposit.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Deposit.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(Deposit.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(Deposit.FIELD_CATEGORY, DepositCategory.class, myCategoryButton);
        theFieldSet.addFieldElement(Deposit.FIELD_PARENT, Payee.class, myParentButton);
        theFieldSet.addFieldElement(Deposit.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        theFieldSet.addFieldElement(Deposit.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Deposit.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CLOSED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
     * @return the panel
     */
    private JPanel buildXtrasPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        final TethysSwingDateButtonManager myMaturity = pFactory.newDateButton();
        final TethysSwingStringTextField mySortCode = pFactory.newStringField();
        final TethysSwingStringTextField myAccount = pFactory.newStringField();
        final TethysSwingStringTextField myReference = pFactory.newStringField();
        final TethysSwingStringTextField myOpening = pFactory.newStringField();

        /* Restrict the fields */
        final int myWidth = Deposit.NAMELEN >> 1;
        restrictField(myMaturity, myWidth);
        restrictField(mySortCode, myWidth);
        restrictField(myAccount, myWidth);
        restrictField(myReference, myWidth);
        restrictField(myOpening, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY), myMaturity);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), MetisDataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), MetisDataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), MetisDataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), MetisDataType.MONEY, myOpening);

        /* Create the extras panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the extras panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildNotesPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        final TethysSwingTextArea myNotes = pFactory.newTextArea();
        final TethysSwingScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the notes panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
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
        theFieldSet.setVisibility(Deposit.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                                              ? !myDeposit.getParent().isClosed()
                                              : !bIsRelevant;
        theFieldSet.setEditable(Deposit.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myDeposit.getDesc() != null;
        theFieldSet.setVisibility(Deposit.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myDeposit.getSortCode() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        final boolean bShowAccount = isEditable || myDeposit.getAccount() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        final boolean bShowReference = isEditable || myDeposit.getReference() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        final boolean bHasOpening = myDeposit.getOpeningBalance() != null;
        final boolean bShowOpening = bIsChangeable || bHasOpening;
        final MetisField myOpeningField = DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
        theFieldSet.setVisibility(myOpeningField, bShowOpening);
        final boolean bShowNotes = isEditable || myDeposit.getNotes() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Maturity is only visible if the item is a bond */
        final boolean bShowMaturity = DepositCategoryClass.BOND.equals(myDeposit.getCategoryClass());
        final MetisField myMaturityField = DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY);
        theFieldSet.setVisibility(myMaturityField, bShowMaturity);
        theFieldSet.setEditable(myMaturityField, isEditable && !bIsClosed);

        /* Category, Currency, and OpeningBalance cannot be changed if the item is active */
        theFieldSet.setEditable(Deposit.FIELD_CATEGORY, bIsChangeable);
        theFieldSet.setEditable(Deposit.FIELD_CURRENCY, bIsChangeable && !bHasOpening);
        theFieldSet.setEditable(myOpeningField, bIsChangeable);

        /* Set currency for opening balance */
        theFieldSet.setAssumedCurrency(myOpeningField, myDeposit.getCurrency());

        /* Set editable value for parent */
        theFieldSet.setEditable(Deposit.FIELD_PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisField myField = pUpdate.getField();
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
            myDeposit.setDepositCategory(pUpdate.getValue(DepositCategory.class));
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
    public void buildCategoryMenu(final TethysScrollMenu<DepositCategory, Icon> pMenu,
                                  final Deposit pDeposit) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final DepositCategory myCurr = pDeposit.getCategory();
        TethysScrollMenuItem<DepositCategory> myActive = null;

        /* Access Deposit Categories */
        final DepositCategoryList myCategories = getDataList(MoneyWiseDataType.DEPOSITCATEGORY, DepositCategoryList.class);

        /* Create a simple map for top-level categories */
        final Map<String, TethysScrollSubMenu<DepositCategory, Icon>> myMap = new HashMap<>();

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
            TethysScrollSubMenu<DepositCategory, Icon> myMenu = myMap.get(myParentName);

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
    public void buildParentMenu(final TethysScrollMenu<Payee, Icon> pMenu,
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
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentDeposit(myType);
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
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency, Icon> pMenu,
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
