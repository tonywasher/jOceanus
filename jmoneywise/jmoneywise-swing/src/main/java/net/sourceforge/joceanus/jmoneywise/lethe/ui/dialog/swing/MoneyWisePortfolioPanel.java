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

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldSetBase.MetisLetheFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.PortfolioInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType.PortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStringEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTextArea;

/**
 * Panel to display/edit/create a Portfolio.
 */
public class MoneyWisePortfolioPanel
        extends MoneyWiseItemPanel<Portfolio> {
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
    public MoneyWisePortfolioPanel(final TethysGuiFactory pFactory,
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

        /* Define the panel */
        defineMainPanel(myPanel);
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private MoneyWiseDataPanel buildMainPanel(final TethysGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(Portfolio.NAMELEN);

        /* Create the text fields */
        final TethysStringEditField myName = pFactory.newStringField();
        final TethysStringEditField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysScrollButtonManager<PortfolioType> myTypeButton = pFactory.newScrollButton();
        final TethysScrollButtonManager<Payee> myParentButton = pFactory.newScrollButton();
        final TethysScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        final TethysIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* Assign the fields to the panel */
        myPanel.addField(AssetBase.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(AssetBase.FIELD_DESC, MetisDataType.STRING, myDesc);
        myPanel.addField(AssetBase.FIELD_CATEGORY, PortfolioType.class, myTypeButton);
        myPanel.addField(AssetBase.FIELD_PARENT, Payee.class, myParentButton);
        myPanel.addField(AssetBase.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        myPanel.addField(AssetBase.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Layout the panel */
        myPanel.compactPanel();

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildTypeMenu(c, getItem()));
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
    private void buildXtrasPanel(final TethysGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_DETAILS, DataItem.NAMELEN >> 1);

        /* Allocate fields */
        final TethysStringEditField mySortCode = pFactory.newStringField();
        final TethysStringEditField myAccount = pFactory.newStringField();
        final TethysStringEditField myReference = pFactory.newStringField();
        final TethysStringEditField myWebSite = pFactory.newStringField();
        final TethysStringEditField myCustNo = pFactory.newStringField();
        final TethysStringEditField myUserId = pFactory.newStringField();
        final TethysStringEditField myPassWord = pFactory.newStringField();

        /* Assign the fields to the panel */
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), MetisDataType.CHARARRAY, mySortCode);
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), MetisDataType.CHARARRAY, myAccount);
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), MetisDataType.CHARARRAY, myReference);
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), MetisDataType.CHARARRAY, myWebSite);
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), MetisDataType.CHARARRAY, myCustNo);
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), MetisDataType.CHARARRAY, myUserId);
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), MetisDataType.CHARARRAY, myPassWord);

        /* Layout the panel */
        myTab.compactPanel();
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(AccountInfoClass.NOTES.toString(), DataItem.NAMELEN);

        /* Allocate fields */
        final TethysTextArea myNotes = pFactory.newTextArea();
        final TethysScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Assign the fields to the panel */
        myTab.addField(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Layout the panel */
        myTab.compactPanel();
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Portfolio myItem = getItem();
        if (myItem != null) {
            final PortfolioList myPortfolios = getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
            setItem(myPortfolios.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the fieldSet */
        final MetisSwingFieldSet<Portfolio> myFieldSet = getFieldSet();

        /* Access the item */
        final Portfolio myPortfolio = getItem();
        final boolean bIsClosed = myPortfolio.isClosed();
        final boolean bIsActive = myPortfolio.isActive();
        final boolean bIsRelevant = myPortfolio.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        myFieldSet.setVisibility(AssetBase.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        myFieldSet.setEditable(AssetBase.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myPortfolio.getDesc() != null;
        myFieldSet.setVisibility(AssetBase.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myPortfolio.getSortCode() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        final boolean bShowAccount = isEditable || myPortfolio.getAccount() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        final boolean bShowReference = isEditable || myPortfolio.getReference() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        final boolean bShowWebSite = isEditable || myPortfolio.getWebSite() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), bShowWebSite);
        final boolean bShowCustNo = isEditable || myPortfolio.getCustNo() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), bShowCustNo);
        final boolean bShowUserId = isEditable || myPortfolio.getUserId() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), bShowUserId);
        final boolean bShowPasswd = isEditable || myPortfolio.getPassword() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), bShowPasswd);
        final boolean bShowNotes = isEditable || myPortfolio.getNotes() != null;
        myFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Type, Parent and Currency status cannot be changed if the item is active */
        myFieldSet.setEditable(AssetBase.FIELD_CATEGORY, bIsChangeable);
        myFieldSet.setEditable(AssetBase.FIELD_PARENT, bIsChangeable);
        myFieldSet.setEditable(AssetBase.FIELD_CURRENCY, bIsChangeable);

        /* Set editable value for parent */
        myFieldSet.setEditable(AssetBase.FIELD_PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final MetisLetheFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisLetheField myField = pUpdate.getField();
        final Portfolio myPortfolio = getItem();

        /* Process updates */
        if (myField.equals(AssetBase.FIELD_NAME)) {
            /* Update the Name */
            myPortfolio.setName(pUpdate.getString());
        } else if (myField.equals(AssetBase.FIELD_DESC)) {
            /* Update the Description */
            myPortfolio.setDescription(pUpdate.getString());
        } else if (myField.equals(AssetBase.FIELD_CATEGORY)) {
            /* Update the portfolioType */
            myPortfolio.setCategory(pUpdate.getValue(PortfolioType.class));
        } else if (myField.equals(AssetBase.FIELD_PARENT)) {
            /* Update the Parent */
            myPortfolio.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(AssetBase.FIELD_CURRENCY)) {
            /* Update the Currency */
            myPortfolio.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(AssetBase.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myPortfolio.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (PortfolioInfoSet.getClassForField(myField)) {
                case SORTCODE:
                    myPortfolio.setSortCode(pUpdate.getCharArray());
                    break;
                case ACCOUNT:
                    myPortfolio.setAccount(pUpdate.getCharArray());
                    break;
                case REFERENCE:
                    myPortfolio.setReference(pUpdate.getCharArray());
                    break;
                case WEBSITE:
                    myPortfolio.setWebSite(pUpdate.getCharArray());
                    break;
                case CUSTOMERNO:
                    myPortfolio.setCustNo(pUpdate.getCharArray());
                    break;
                case USERID:
                    myPortfolio.setUserId(pUpdate.getCharArray());
                    break;
                case PASSWORD:
                    myPortfolio.setPassword(pUpdate.getCharArray());
                    break;
                case NOTES:
                    myPortfolio.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final Portfolio myItem = getItem();
        final Payee myParent = myItem.getParent();
        if (!pUpdates) {
            final PortfolioType myType = myItem.getCategory();
            declareGoToItem(myType);
            final AssetCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the portfolioType menu for an item.
     * @param pMenu the menu
     * @param pPortfolio the portfolio to build for
     */
    public void buildTypeMenu(final TethysScrollMenu<PortfolioType> pMenu,
                              final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final PortfolioType myCurr = pPortfolio.getCategory();
        TethysScrollMenuItem<PortfolioType> myActive = null;

        /* Access PortfolioTypes */
        final PortfolioTypeList myTypes = getDataList(MoneyWiseDataType.PORTFOLIOTYPE, PortfolioTypeList.class);

        /* Loop through the Types */
        final Iterator<PortfolioType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final PortfolioType myType = myIterator.next();

            /* Create a new action for the type */
            final TethysScrollMenuItem<PortfolioType> myItem = pMenu.addItem(myType);

            /* If this is the active type */
            if (myType.equals(myCurr)) {
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
     * Build the parent menu for an item.
     * @param pMenu the menu
     * @param pPortfolio the portfolio to build for
     */
    public void buildParentMenu(final TethysScrollMenu<Payee> pMenu,
                                final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final Payee myCurr = pPortfolio.getParent();
        TethysScrollMenuItem<Payee> myActive = null;

        /* Access Payees */
        final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted/closed and ones that cannot own this portfolio */
            boolean bIgnore = myPayee.isDeleted() || myPayee.isClosed();
            bIgnore |= !myPayee.getCategoryClass().canParentPortfolio();
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
     * @param pPortfolio the portfolio to build for
     */
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency> pMenu,
                                  final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pPortfolio.getAssetCurrency();
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
