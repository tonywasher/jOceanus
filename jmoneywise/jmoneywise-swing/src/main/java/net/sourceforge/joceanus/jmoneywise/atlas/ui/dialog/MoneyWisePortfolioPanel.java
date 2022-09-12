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

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType.PortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCharArrayEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStringEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * Panel to display/edit/create a Portfolio.
 */
public class MoneyWisePortfolioPanel
        extends MoneyWiseItemPanel<Portfolio> {
    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<Portfolio> theFieldSet;

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
    public MoneyWisePortfolioPanel(final TethysGuiFactory pFactory,
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
        final TethysScrollButtonField<PortfolioType> myTypeButton = pFactory.newScrollField(PortfolioType.class);
        final TethysScrollButtonField<Payee> myParentButton = pFactory.newScrollField(Payee.class);
        final TethysScrollButtonField<AssetCurrency> myCurrencyButton = pFactory.newScrollField(AssetCurrency.class);
        final TethysIconButtonField<Boolean> myClosedButton = pFactory.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.NAME, myName, Portfolio::getName);
        theFieldSet.addField(MoneyWiseAssetDataId.DESC, myDesc, Portfolio::getDesc);
        theFieldSet.addField(MoneyWiseAssetDataId.CATEGORY, myTypeButton, Portfolio::getCategory);
        theFieldSet.addField(MoneyWiseAssetDataId.PARENT, myParentButton, Portfolio::getParent);
        theFieldSet.addField(MoneyWiseAssetDataId.CURRENCY, myCurrencyButton, Portfolio::getAssetCurrency);
        theFieldSet.addField(MoneyWiseAssetDataId.CLOSED, myClosedButton, Portfolio::isClosed);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildTypeMenu(c, getItem()));
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
        final TethysCharArrayEditField mySortCode = pFactory.newCharArrayField();
        final TethysCharArrayEditField myAccount = pFactory.newCharArrayField();
        final TethysCharArrayEditField myReference = pFactory.newCharArrayField();
        final TethysCharArrayEditField myWebSite = pFactory.newCharArrayField();
        final TethysCharArrayEditField myCustNo = pFactory.newCharArrayField();
        final TethysCharArrayEditField myUserId = pFactory.newCharArrayField();
        final TethysCharArrayEditField myPassWord = pFactory.newCharArrayField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.PORTFOLIOSORTCODE, mySortCode, Portfolio::getSortCode);
        theFieldSet.addField(MoneyWiseAssetDataId.PORTFOLIOACCOUNT, myAccount, Portfolio::getAccount);
        theFieldSet.addField(MoneyWiseAssetDataId.PORTFOLIOREFERENCE, myReference, Portfolio::getReference);
        theFieldSet.addField(MoneyWiseAssetDataId.PORTFOLIOWEBSITE, myWebSite, Portfolio::getWebSite);
        theFieldSet.addField(MoneyWiseAssetDataId.PORTFOLIOCUSTNO, myCustNo, Portfolio::getCustNo);
        theFieldSet.addField(MoneyWiseAssetDataId.PORTFOLIOUSERID, myUserId, Portfolio::getUserId);
        theFieldSet.addField(MoneyWiseAssetDataId.PORTFOLIOPASSWORD, myPassWord, Portfolio::getPassword);
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysGuiFactory pFactory) {
        /* Allocate fields */
        final TethysCharArrayTextAreaField myNotes = pFactory.newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(AccountInfoClass.NOTES.toString(), MoneyWiseAssetDataId.PORTFOLIONOTES, myNotes, Portfolio::getNotes);
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
        /* Access the item */
        final Portfolio myPortfolio = getItem();
        final boolean bIsClosed = myPortfolio.isClosed();
        final boolean bIsActive = myPortfolio.isActive();
        final boolean bIsRelevant = myPortfolio.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myPortfolio.getDesc() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myPortfolio.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIOSORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myPortfolio.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIOACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myPortfolio.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIOREFERENCE, bShowReference);
        final boolean bShowWebSite = isEditable || myPortfolio.getWebSite() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIOWEBSITE, bShowWebSite);
        final boolean bShowCustNo = isEditable || myPortfolio.getCustNo() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIOCUSTNO, bShowCustNo);
        final boolean bShowUserId = isEditable || myPortfolio.getUserId() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIOUSERID, bShowUserId);
        final boolean bShowPasswd = isEditable || myPortfolio.getPassword() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIOPASSWORD, bShowPasswd);
        final boolean bShowNotes = isEditable || myPortfolio.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.PORTFOLIONOTES, bShowNotes);

        /* Type, Parent and Currency status cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CATEGORY, bIsChangeable);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CURRENCY, bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.PARENT, bIsChangeable && !bIsClosed);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final Portfolio myPortfolio = getItem();

        /* Process updates */
        if (MoneyWiseAssetDataId.NAME.equals(myField)) {
            /* Update the Name */
            myPortfolio.setName(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.DESC.equals(myField)) {
            /* Update the Description */
            myPortfolio.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.CATEGORY.equals(myField)) {
            /* Update the portfolioType */
            myPortfolio.setCategory(pUpdate.getValue(PortfolioType.class));
        } else if (MoneyWiseAssetDataId.PARENT.equals(myField)) {
            /* Update the Parent */
            myPortfolio.setParent(pUpdate.getValue(Payee.class));
        } else if (MoneyWiseAssetDataId.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myPortfolio.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (MoneyWiseAssetDataId.CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myPortfolio.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAssetDataId.PORTFOLIOSORTCODE.equals(myField)) {
            /* Update the SortCode */
            myPortfolio.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PORTFOLIOACCOUNT.equals(myField)) {
            /* Update the Account */
            myPortfolio.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PORTFOLIOREFERENCE.equals(myField)) {
            /* Update the Reference */
            myPortfolio.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PORTFOLIOWEBSITE.equals(myField)) {
            /* Update the WebSite */
            myPortfolio.setWebSite(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PORTFOLIOCUSTNO.equals(myField)) {
            /* Update the Customer# */
            myPortfolio.setCustNo(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PORTFOLIOUSERID.equals(myField)) {
            /* Update the UserId */
            myPortfolio.setUserId(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PORTFOLIOPASSWORD.equals(myField)) {
            /* Update the Password */
            myPortfolio.setPassword(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAssetDataId.PORTFOLIONOTES.equals(myField)) {
            /* Update the Notes */
            myPortfolio.setNotes(pUpdate.getValue(char[].class));
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
