/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePortfolioType.MoneyWisePortfolioTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.jmoneywise.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * Panel to display/edit/create a Portfolio.
 */
public class MoneyWisePortfolioPanel
        extends MoneyWiseItemPanel<MoneyWisePortfolio> {
    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<MoneyWisePortfolio> theFieldSet;

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
    public MoneyWisePortfolioPanel(final TethysUIFactory<?> pFactory,
                                   final PrometheusEditSet pEditSet,
                                   final MoneyWiseAssetTable<MoneyWisePortfolio> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        buildMainPanel(pFactory, pOwner);

        /* Build the account panel */
        buildAccountPanel(pFactory);

        /* Build the web panel */
        buildWebPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @param pOwner the owning table
     */
    private void buildMainPanel(final TethysUIFactory<?> pFactory,
                                final MoneyWiseAssetTable<MoneyWisePortfolio> pOwner) {
        /* Create the text fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField myName = myFields.newStringField();
        final TethysUIStringEditField myDesc = myFields.newStringField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseAssetCategory> myTypeButton = myFields.newScrollField(MoneyWiseAssetCategory.class);
        final TethysUIScrollButtonField<MoneyWisePayee> myParentButton = myFields.newScrollField(MoneyWisePayee.class);
        final TethysUIScrollButtonField<MoneyWiseCurrency> myCurrencyButton = myFields.newScrollField(MoneyWiseCurrency.class);
        final TethysUIIconButtonField<Boolean> myClosedButton = myFields.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWisePortfolio::getName);
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWisePortfolio::getDesc);
        theFieldSet.addField(MoneyWiseBasicResource.CATEGORY_NAME, myTypeButton, MoneyWisePortfolio::getCategory);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_PARENT, myParentButton, MoneyWisePortfolio::getParent);
        theFieldSet.addField(MoneyWiseStaticDataType.CURRENCY, myCurrencyButton, MoneyWisePortfolio::getAssetCurrency);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_CLOSED, myClosedButton, MoneyWisePortfolio::isClosed);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton(pFactory);
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Configure name checks */
        myName.setValidator(this::isValidName);
        myName.setReporter(pOwner::showValidateError);

        /* Configure description checks */
        myDesc.setValidator(this::isValidDesc);
        myDesc.setReporter(pOwner::showValidateError);
    }

    /**
     * Build extras subPanel.
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

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAccountInfoClass.SORTCODE, mySortCode, MoneyWisePortfolio::getSortCode);
        theFieldSet.addField(MoneyWiseAccountInfoClass.ACCOUNT, myAccount, MoneyWisePortfolio::getAccount);
        theFieldSet.addField(MoneyWiseAccountInfoClass.REFERENCE, myReference, MoneyWisePortfolio::getReference);
    }

    /**
     * Build extras subPanel.
     * @param pFactory the GUI factory
     */
    private void buildWebPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_WEB);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUICharArrayEditField myWebSite = myFields.newCharArrayField();
        final TethysUICharArrayEditField myCustNo = myFields.newCharArrayField();
        final TethysUICharArrayEditField myUserId = myFields.newCharArrayField();
        final TethysUICharArrayEditField myPassWord = myFields.newCharArrayField();

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAccountInfoClass.WEBSITE, myWebSite, MoneyWisePortfolio::getWebSite);
        theFieldSet.addField(MoneyWiseAccountInfoClass.CUSTOMERNO, myCustNo, MoneyWisePortfolio::getCustNo);
        theFieldSet.addField(MoneyWiseAccountInfoClass.USERID, myUserId, MoneyWisePortfolio::getUserId);
        theFieldSet.addField(MoneyWiseAccountInfoClass.PASSWORD, myPassWord, MoneyWisePortfolio::getPassword);
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
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAccountInfoClass.NOTES, myNotes, MoneyWisePortfolio::getNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWisePortfolio myItem = getItem();
        if (myItem != null) {
            final MoneyWisePortfolioList myPortfolios = getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
            setItem(myPortfolios.findItemById(myItem.getIndexedId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final MoneyWisePortfolio myPortfolio = getItem();
        final boolean bIsClosed = myPortfolio.isClosed();
        final boolean bIsActive = myPortfolio.isActive();
        final boolean bIsRelevant = myPortfolio.isRelevant();
        final boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.ASSET_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || myPortfolio.getDesc() != null;
        theFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowSortCode = isEditable || myPortfolio.getSortCode() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.SORTCODE, bShowSortCode);
        final boolean bShowAccount = isEditable || myPortfolio.getAccount() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.ACCOUNT, bShowAccount);
        final boolean bShowReference = isEditable || myPortfolio.getReference() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.REFERENCE, bShowReference);
        final boolean bShowWebSite = isEditable || myPortfolio.getWebSite() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.WEBSITE, bShowWebSite);
        final boolean bShowCustNo = isEditable || myPortfolio.getCustNo() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.CUSTOMERNO, bShowCustNo);
        final boolean bShowUserId = isEditable || myPortfolio.getUserId() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.USERID, bShowUserId);
        final boolean bShowPasswd = isEditable || myPortfolio.getPassword() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.PASSWORD, bShowPasswd);
        final boolean bShowNotes = isEditable || myPortfolio.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.NOTES, bShowNotes);

        /* Type, Parent and Currency status cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.CATEGORY_NAME, bIsChangeable);
        theFieldSet.setFieldEditable(MoneyWiseStaticDataType.CURRENCY, bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_PARENT, bIsChangeable && !bIsClosed);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWisePortfolio myPortfolio = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the Name */
            myPortfolio.setName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            myPortfolio.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_NAME.equals(myField)) {
            /* Update the portfolioType */
            myPortfolio.setCategory(pUpdate.getValue(MoneyWisePortfolioType.class));
        } else if (MoneyWiseBasicResource.ASSET_PARENT.equals(myField)) {
            /* Update the Parent */
            myPortfolio.setParent(pUpdate.getValue(MoneyWisePayee.class));
        } else if (MoneyWiseStaticDataType.CURRENCY.equals(myField)) {
            /* Update the Currency */
            myPortfolio.setAssetCurrency(pUpdate.getValue(MoneyWiseCurrency.class));
        } else if (MoneyWiseBasicResource.ASSET_CLOSED.equals(myField)) {
            /* Update the Closed indication */
            myPortfolio.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAccountInfoClass.SORTCODE.equals(myField)) {
            /* Update the SortCode */
            myPortfolio.setSortCode(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.ACCOUNT.equals(myField)) {
            /* Update the Account */
            myPortfolio.setAccount(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.REFERENCE.equals(myField)) {
            /* Update the Reference */
            myPortfolio.setReference(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.WEBSITE.equals(myField)) {
            /* Update the WebSite */
            myPortfolio.setWebSite(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.CUSTOMERNO.equals(myField)) {
            /* Update the Customer# */
            myPortfolio.setCustNo(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.USERID.equals(myField)) {
            /* Update the UserId */
            myPortfolio.setUserId(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.PASSWORD.equals(myField)) {
            /* Update the Password */
            myPortfolio.setPassword(pUpdate.getValue(char[].class));
        } else if (MoneyWiseAccountInfoClass.NOTES.equals(myField)) {
            /* Update the Notes */
            myPortfolio.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWisePortfolio myItem = getItem();
        final MoneyWisePayee myParent = myItem.getParent();
        if (!pUpdates) {
            final MoneyWisePortfolioType myType = myItem.getCategory();
            declareGoToItem(myType);
            final MoneyWiseCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the portfolioType menu for an item.
     * @param pMenu the menu
     * @param pPortfolio the portfolio to build for
     */
    public void buildTypeMenu(final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu,
                              final MoneyWisePortfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWisePortfolioType myCurr = pPortfolio.getCategory();
        TethysUIScrollItem<MoneyWiseAssetCategory> myActive = null;

        /* Access PortfolioTypes */
        final MoneyWisePortfolioTypeList myTypes = getDataList(MoneyWiseStaticDataType.PORTFOLIOTYPE, MoneyWisePortfolioTypeList.class);

        /* Loop through the Types */
        final Iterator<MoneyWisePortfolioType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePortfolioType myType = myIterator.next();

            /* Create a new action for the type */
            final TethysUIScrollItem<MoneyWiseAssetCategory> myItem = pMenu.addItem(myType);

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
    public void buildParentMenu(final TethysUIScrollMenu<MoneyWisePayee> pMenu,
                                final MoneyWisePortfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWisePayee myCurr = pPortfolio.getParent();
        TethysUIScrollItem<MoneyWisePayee> myActive = null;

        /* Access Payees */
        final MoneyWisePayeeList myPayees = getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);

        /* Loop through the Payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted/closed and ones that cannot own this portfolio */
            boolean bIgnore = myPayee.isDeleted() || myPayee.isClosed();
            bIgnore |= !myPayee.getCategoryClass().canParentPortfolio();
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
     * @param pPortfolio the portfolio to build for
     */
    public void buildCurrencyMenu(final TethysUIScrollMenu<MoneyWiseCurrency> pMenu,
                                  final MoneyWisePortfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCurrency myCurr = pPortfolio.getAssetCurrency();
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
