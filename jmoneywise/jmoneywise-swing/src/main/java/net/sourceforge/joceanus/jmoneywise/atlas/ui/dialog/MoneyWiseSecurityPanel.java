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
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.ids.MoneyWiseAssetDataId;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region.RegionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataFieldId;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.jprometheus.atlas.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCharArrayTextAreaField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysPriceEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStringEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * Panel to display/edit/create a Security.
 */
public class MoneyWiseSecurityPanel
        extends MoneyWiseItemPanel<Security> {
    /**
     * Prices Tab Title.
     */
    private static final String TAB_PRICES = MoneyWiseUIResource.SECURITYPANEL_TAB_PRICES.getValue();

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<Security> theFieldSet;

    /**
     * SecurityPrice Table.
     */
    private final MoneyWiseSecurityPriceTable thePrices;

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
    public MoneyWiseSecurityPanel(final TethysGuiFactory pFactory,
                                  final MoneyWiseView pView,
                                  final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                  final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pUpdateSet, pError);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the info panel */
        buildInfoPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Create the SecurityPrices table */
        thePrices = new MoneyWiseSecurityPriceTable(pView, getUpdateSet(), pError);
        theFieldSet.newTable(TAB_PRICES, thePrices);

        /* Create the listener */
        thePrices.getEventRegistrar().addEventListener(e -> {
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
        final TethysScrollButtonField<SecurityType> myTypeButton = pFactory.newScrollField(SecurityType.class);
        final TethysScrollButtonField<Payee> myParentButton = pFactory.newScrollField(Payee.class);
        final TethysScrollButtonField<AssetCurrency> myCurrencyButton = pFactory.newScrollField(AssetCurrency.class);
        final TethysIconButtonField<Boolean> myClosedButton = pFactory.newIconField(Boolean.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.NAME, myName, Security::getName);
        theFieldSet.addField(MoneyWiseAssetDataId.DESC, myDesc, Security::getDesc);
        theFieldSet.addField(MoneyWiseAssetDataId.CATEGORY, myTypeButton, Security::getCategory);
        theFieldSet.addField(MoneyWiseAssetDataId.PARENT, myParentButton, Security::getParent);
        theFieldSet.addField(MoneyWiseAssetDataId.CURRENCY, myCurrencyButton, Security::getAssetCurrency);
        theFieldSet.addField(MoneyWiseAssetDataId.CLOSED, myClosedButton, Security::isClosed);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildSecTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));
    }

    /**
     * Build info subPanel.
     * @param pFactory the GUI factory
     */
    private void buildInfoPanel(final TethysGuiFactory pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_DETAILS);

        /* Allocate fields */
        final TethysStringEditField mySymbol = pFactory.newStringField();
        final TethysPriceEditField myPrice = pFactory.newPriceField();

        /* Create the buttons */
        final TethysScrollButtonField<Region> myRegionButton = pFactory.newScrollField(Region.class);
        final TethysScrollButtonField<Security> myStockButton = pFactory.newScrollField(Security.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAssetDataId.SECURITYSYMBOL, mySymbol, Security::getSymbol);
        theFieldSet.addField(MoneyWiseAssetDataId.SECURITYREGION, myRegionButton, Security::getRegion);
        theFieldSet.addField(MoneyWiseAssetDataId.SECURITYUNDERLYING, myStockButton, Security::getUnderlyingStock);
        theFieldSet.addField(MoneyWiseAssetDataId.SECURITYOPTIONPRICE, myPrice, Security::getOptionPrice);

        /* Configure the menuBuilders */
        myRegionButton.setMenuConfigurator(c -> buildRegionMenu(c, getItem()));
        myStockButton.setMenuConfigurator(c -> buildStockMenu(c, getItem()));
        myPrice.setDeemedCurrency(() -> getItem().getCurrency());
    }

    /**
     * Build Notes subPanel.
     * @param pFactory the GUI factory
     */
    private void buildNotesPanel(final TethysGuiFactory pFactory) {
        /* Allocate fields */
        final TethysCharArrayTextAreaField myNotes = pFactory.newCharArrayAreaField();

        /* Assign the fields to the panel */
        theFieldSet.newTextArea(AccountInfoClass.NOTES.toString(), MoneyWiseAssetDataId.SECURITYNOTES, myNotes, Security::getNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final Security myItem = getItem();
        if (myItem != null) {
            final SecurityList mySecurities = getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
            setItem(mySecurities.findItemById(myItem.getId()));
        }

        /* Refresh the prices */
        thePrices.refreshData();

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final Security mySecurity = getItem();
        final boolean bIsClosed = mySecurity.isClosed();
        final boolean bIsActive = mySecurity.isActive();
        final boolean bIsRelevant = mySecurity.isRelevant();

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                ? !mySecurity.getParent().isClosed()
                : !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || mySecurity.getDesc() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowNotes = isEditable || mySecurity.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.SECURITYNOTES, bShowNotes);

        /* Determine whether the symbol field should be visible */
        boolean bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.SYMBOL);
        boolean bShowField = bEditField || mySecurity.getSymbol() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.SECURITYSYMBOL, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.SECURITYSYMBOL, bEditField);

        /* Determine whether the region field should be visible */
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.REGION);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.SECURITYREGION, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.SECURITYREGION, bEditField);

        /* Determine whether the stock field should be visible */
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.UNDERLYINGSTOCK);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.SECURITYUNDERLYING, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.SECURITYUNDERLYING, bEditField);

        /* Determine whether the price field should be visible */
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.OPTIONPRICE);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setFieldVisible(MoneyWiseAssetDataId.SECURITYOPTIONPRICE, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.SECURITYOPTIONPRICE, bEditField);

        /* Security type and currency cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CATEGORY, isEditable && !bIsActive);
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.CURRENCY, isEditable && !bIsActive);

        /* Set editable value for parent */
        theFieldSet.setFieldEditable(MoneyWiseAssetDataId.PARENT, isEditable && !bIsClosed);
    }

    /**
     * Is the field editable?
     * @param pSecurity the security
     * @param pField the field class
     * @return true/false
     */
    public static boolean isEditableField(final Security pSecurity,
                                          final AccountInfoClass pField) {
        /* Access the infoSet */
        final SecurityInfoSet myInfoSet = pSecurity.getInfoSet();

        /* Check whether the field is available */
        final MetisFieldRequired isRequired = myInfoSet.isClassRequired(pField);
        return !isRequired.equals(MetisFieldRequired.NOTALLOWED);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final PrometheusDataFieldId myField = pUpdate.getFieldId();
        final Security mySecurity = getItem();

        /* Process updates */
        if (MoneyWiseAssetDataId.NAME.equals(myField)) {
            /* Update the Name */
            mySecurity.setName(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.DESC.equals(myField)) {
            /* Update the Description */
            mySecurity.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.CATEGORY.equals(myField)) {
            /* Update the Security Type */
            mySecurity.setCategory(pUpdate.getValue(SecurityType.class));
            mySecurity.autoCorrect(getUpdateSet());
        } else if (MoneyWiseAssetDataId.PARENT.equals(myField)) {
            /* Update the Parent */
            mySecurity.setParent(pUpdate.getValue(Payee.class));
        } else if (MoneyWiseAssetDataId.CURRENCY.equals(myField)) {
            /* Update the Currency */
            mySecurity.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (MoneyWiseAssetDataId.CLOSED.equals(myField)) {
            /* Update the Closed indication */
            mySecurity.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAssetDataId.SECURITYSYMBOL.equals(myField)) {
            /* Update the Symbol */
            mySecurity.setSymbol(pUpdate.getValue(String.class));
        } else if (MoneyWiseAssetDataId.SECURITYREGION.equals(myField)) {
            /* Update the Region */
            mySecurity.setRegion(pUpdate.getValue(Region.class));
        } else if (MoneyWiseAssetDataId.SECURITYUNDERLYING.equals(myField)) {
            /* Update the Underlying Stock */
            mySecurity.setUnderlyingStock(pUpdate.getValue(Security.class));
        } else if (MoneyWiseAssetDataId.SECURITYOPTIONPRICE.equals(myField)) {
            /* Update the OptionPrice */
            mySecurity.setOptionPrice(pUpdate.getValue(TethysPrice.class));
        } else if (MoneyWiseAssetDataId.SECURITYNOTES.equals(myField)) {
            /* Update the Notes */
            mySecurity.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final Security myItem = getItem();
        final Payee myParent = myItem.getParent();
        if (!pUpdates) {
            final SecurityType myType = myItem.getCategory();
            final AssetCurrency myCurrency = myItem.getAssetCurrency();
            final Region myRegion = myItem.getRegion();
            declareGoToItem(myType);
            declareGoToItem(myRegion);
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    /**
     * Add a new price for a new security.
     * @param pSecurity the security
     * @throws OceanusException on error
     */
    public void addNewPrice(final Security pSecurity) throws OceanusException {
        /* Create the new price */
        thePrices.addNewPrice(pSecurity);
    }

    @Override
    public void setEditable(final boolean isEditable) {
        /* Update the prices */
        thePrices.setEditable(isEditable);

        /* Pass call onwards */
        super.setEditable(isEditable);
    }

    @Override
    protected void refreshAfterUpdate() {
        /* Pass call onwards */
        super.refreshAfterUpdate();

        /* Refresh the prices */
        thePrices.refreshAfterUpdate();
    }

    /**
     * Build the securityType menu for an item.
     * @param pMenu the menu
     * @param pSecurity the security to build for
     */
    public void buildSecTypeMenu(final TethysScrollMenu<SecurityType> pMenu,
                                 final Security pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final SecurityType myCurr = pSecurity.getCategory();
        TethysScrollMenuItem<SecurityType> myActive = null;

        /* Access SecurityTypes */
        final SecurityTypeList myTypes = getDataList(MoneyWiseDataType.SECURITYTYPE, SecurityTypeList.class);

        /* Loop through the SecurityTypes */
        final Iterator<SecurityType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final SecurityType myType = myIterator.next();

            /* Ignore deleted or disabled */
            final boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the secType */
            final TethysScrollMenuItem<SecurityType> myItem = pMenu.addItem(myType);

            /* If this is the active secType */
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
     * @param pSecurity the security to build for
     */
    public void buildParentMenu(final TethysScrollMenu<Payee> pMenu,
                                final Security pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final SecurityTypeClass myType = pSecurity.getCategoryClass();
        final Payee myCurr = pSecurity.getParent();
        TethysScrollMenuItem<Payee> myActive = null;

        /* Access Payees */
        final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getCategoryClass().canParentSecurity(myType);
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
     * Build the region menu for an item.
     * @param pMenu the menu
     * @param pSecurity the security to build for
     */
    public void buildRegionMenu(final TethysScrollMenu<Region> pMenu,
                                final Security pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final Region myCurr = pSecurity.getRegion();
        TethysScrollMenuItem<Region> myActive = null;

        /* Access regions */
        final RegionList myRegions = getDataList(MoneyWiseDataType.REGION, RegionList.class);

        /* Loop through the Regions */
        final Iterator<Region> myIterator = myRegions.iterator();
        while (myIterator.hasNext()) {
            final Region myRegion = myIterator.next();

            /* Ignore deleted */
            final boolean bIgnore = myRegion.isDeleted();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the region */
            final TethysScrollMenuItem<Region> myItem = pMenu.addItem(myRegion);

            /* If this is the active region */
            if (myRegion.equals(myCurr)) {
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
     * Build the stock menu for an item.
     * @param pMenu the menu
     * @param pSecurity the security to build for
     */
    public void buildStockMenu(final TethysScrollMenu<Security> pMenu,
                               final Security pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final Security myCurr = pSecurity.getUnderlyingStock();
        TethysScrollMenuItem<Security> myActive = null;

        /* Access securities */
        final SecurityList mySecurities = getDataList(MoneyWiseDataType.SECURITY, SecurityList.class);

        /* Loop through the Securities */
        final Iterator<Security> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final Security mySecurity = myIterator.next();

            /* Ignore deleted and non share */
            boolean bIgnore = mySecurity.isDeleted();
            bIgnore |= !mySecurity.getCategoryClass().isShares();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the region */
            final TethysScrollMenuItem<Security> myItem = pMenu.addItem(mySecurity);

            /* If this is the active stock */
            if (mySecurity.equals(myCurr)) {
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
     * @param pSecurity the security to build for
     */
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency> pMenu,
                                  final Security pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final AssetCurrency myCurr = pSecurity.getAssetCurrency();
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
