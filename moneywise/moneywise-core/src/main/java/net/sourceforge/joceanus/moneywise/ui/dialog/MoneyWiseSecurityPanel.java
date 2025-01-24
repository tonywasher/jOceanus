/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.ui.dialog;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion.MoneyWiseRegionList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfoSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAssetCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency.MoneyWiseCurrencyList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityType.MoneyWiseSecurityTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.moneywise.ui.base.MoneyWiseAssetTable;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSet;
import net.sourceforge.joceanus.prometheus.ui.fieldset.PrometheusFieldSetEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUICharArrayTextAreaField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIPriceEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.tethys.api.field.TethysUIFieldFactory;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollItem;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

/**
 * Panel to display/edit/create a Security.
 */
public class MoneyWiseSecurityPanel
        extends MoneyWiseAssetPanel<MoneyWiseSecurity> {
    /**
     * Prices Tab Title.
     */
    private static final String TAB_PRICES = MoneyWiseUIResource.SECURITYPANEL_TAB_PRICES.getValue();

    /**
     * The fieldSet.
     */
    private final PrometheusFieldSet<MoneyWiseSecurity> theFieldSet;

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
     * @param pEditSet the edit set
     * @param pOwner the owning table
     */
    public MoneyWiseSecurityPanel(final TethysUIFactory<?> pFactory,
                                  final MoneyWiseView pView,
                                  final PrometheusEditSet pEditSet,
                                  final MoneyWiseAssetTable<MoneyWiseSecurity> pOwner) {
        /* Initialise the panel */
        super(pFactory, pEditSet, pOwner);

        /* Access the fieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.setReporter(pOwner::showValidateError);

        /* Build the main panel */
        buildMainPanel(pFactory);

        /* Build the details panel */
        buildDetailsPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Create the SecurityPrices table */
        thePrices = new MoneyWiseSecurityPriceTable(pView, getEditSet(), pOwner.getErrorPanel());
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
    private void buildMainPanel(final TethysUIFactory<?> pFactory) {
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
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_NAME, myName, MoneyWiseSecurity::getName);
        theFieldSet.addField(PrometheusDataResource.DATAITEM_FIELD_DESC, myDesc, MoneyWiseSecurity::getDesc);
        theFieldSet.addField(MoneyWiseBasicResource.CATEGORY_NAME, myTypeButton, MoneyWiseSecurity::getCategory);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_PARENT, myParentButton, MoneyWiseSecurity::getParent);
        theFieldSet.addField(MoneyWiseStaticDataType.CURRENCY, myCurrencyButton, MoneyWiseSecurity::getAssetCurrency);
        theFieldSet.addField(MoneyWiseBasicResource.ASSET_CLOSED, myClosedButton, MoneyWiseSecurity::isClosed);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildSecTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton(pFactory);
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Configure validation checks */
        myName.setValidator(this::isValidName);
        myDesc.setValidator(this::isValidDesc);
    }

    /**
     * Build details subPanel.
     * @param pFactory the GUI factory
     */
    private void buildDetailsPanel(final TethysUIFactory<?> pFactory) {
        /* Create a new panel */
        theFieldSet.newPanel(TAB_DETAILS);

        /* Allocate fields */
        final TethysUIFieldFactory myFields = pFactory.fieldFactory();
        final TethysUIStringEditField mySymbol = myFields.newStringField();
        final TethysUIPriceEditField myPrice = myFields.newPriceField();

        /* Create the buttons */
        final TethysUIScrollButtonField<MoneyWiseRegion> myRegionButton = myFields.newScrollField(MoneyWiseRegion.class);
        final TethysUIScrollButtonField<MoneyWiseSecurity> myStockButton = myFields.newScrollField(MoneyWiseSecurity.class);

        /* Assign the fields to the panel */
        theFieldSet.addField(MoneyWiseAccountInfoClass.SYMBOL, mySymbol, MoneyWiseSecurity::getSymbol);
        theFieldSet.addField(MoneyWiseAccountInfoClass.REGION, myRegionButton, MoneyWiseSecurity::getRegion);
        theFieldSet.addField(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK, myStockButton, MoneyWiseSecurity::getUnderlyingStock);
        theFieldSet.addField(MoneyWiseAccountInfoClass.OPTIONPRICE, myPrice, MoneyWiseSecurity::getOptionPrice);

        /* Configure the menuBuilders */
        myRegionButton.setMenuConfigurator(c -> buildRegionMenu(c, getItem()));
        myStockButton.setMenuConfigurator(c -> buildStockMenu(c, getItem()));
        myPrice.setDeemedCurrency(() -> getItem().getCurrency());

        /* Configure validation checks */
        mySymbol.setValidator(this::isValidSymbol);
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
        theFieldSet.newTextArea(TAB_NOTES, MoneyWiseAccountInfoClass.NOTES, myNotes, MoneyWiseSecurity::getNotes);

        /* Configure validation checks */
        myNotes.setValidator(this::isValidNotes);
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        final MoneyWiseSecurity myItem = getItem();
        if (myItem != null) {
            final MoneyWiseSecurityList mySecurities = getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class);
            setItem(mySecurities.findItemById(myItem.getIndexedId()));
        }

        /* Refresh the prices */
        thePrices.refreshData();

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        final MoneyWiseSecurity mySecurity = getItem();
        final boolean bIsClosed = mySecurity.isClosed();
        final boolean bIsActive = mySecurity.isActive();
        final boolean bIsRelevant = mySecurity.isRelevant();

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setFieldVisible(MoneyWiseBasicResource.ASSET_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                ? !mySecurity.getParent().isClosed()
                : !bIsRelevant;
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || mySecurity.getDesc() != null;
        theFieldSet.setFieldVisible(PrometheusDataResource.DATAITEM_FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowNotes = isEditable || mySecurity.getNotes() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.NOTES, bShowNotes);

        /* Determine whether the symbol field should be visible */
        boolean bEditField = isEditable && isEditableField(mySecurity, MoneyWiseAccountInfoClass.SYMBOL);
        boolean bShowField = bEditField || mySecurity.getSymbol() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.SYMBOL, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.SYMBOL, bEditField);

        /* Determine whether the region field should be visible */
        bEditField = isEditable && isEditableField(mySecurity, MoneyWiseAccountInfoClass.REGION);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.REGION, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.REGION, bEditField);

        /* Determine whether the stock field should be visible */
        bEditField = isEditable && isEditableField(mySecurity, MoneyWiseAccountInfoClass.UNDERLYINGSTOCK);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK, bEditField);

        /* Determine whether the price field should be visible */
        bEditField = isEditable && isEditableField(mySecurity, MoneyWiseAccountInfoClass.OPTIONPRICE);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setFieldVisible(MoneyWiseAccountInfoClass.OPTIONPRICE, bShowField);
        theFieldSet.setFieldEditable(MoneyWiseAccountInfoClass.OPTIONPRICE, bEditField);

        /* Security type and currency cannot be changed if the item is active */
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.CATEGORY_NAME, isEditable && !bIsActive);
        theFieldSet.setFieldEditable(MoneyWiseStaticDataType.CURRENCY, isEditable && !bIsActive);

        /* Set editable value for parent */
        theFieldSet.setFieldEditable(MoneyWiseBasicResource.ASSET_PARENT, isEditable && !bIsClosed);
    }

    /**
     * Is the field editable?
     * @param pSecurity the security
     * @param pField the field class
     * @return true/false
     */
    public static boolean isEditableField(final MoneyWiseSecurity pSecurity,
                                          final MoneyWiseAccountInfoClass pField) {
        /* Access the infoSet */
        final MoneyWiseSecurityInfoSet myInfoSet = pSecurity.getInfoSet();

        /* Check whether the field is available */
        final MetisFieldRequired isRequired = myInfoSet.isClassRequired(pField);
        return !isRequired.equals(MetisFieldRequired.NOTALLOWED);
    }

    @Override
    protected void updateField(final PrometheusFieldSetEvent pUpdate) throws OceanusException {
        /* Access the field */
        final MetisDataFieldId myField = pUpdate.getFieldId();
        final MoneyWiseSecurity mySecurity = getItem();

        /* Process updates */
        if (PrometheusDataResource.DATAITEM_FIELD_NAME.equals(myField)) {
            /* Update the Name */
            mySecurity.setName(pUpdate.getValue(String.class));
        } else if (PrometheusDataResource.DATAITEM_FIELD_DESC.equals(myField)) {
            /* Update the Description */
            mySecurity.setDescription(pUpdate.getValue(String.class));
        } else if (MoneyWiseBasicResource.CATEGORY_NAME.equals(myField)) {
            /* Update the Security Type */
            mySecurity.setCategory(pUpdate.getValue(MoneyWiseSecurityType.class));
            mySecurity.autoCorrect(getEditSet());
        } else if (MoneyWiseBasicResource.ASSET_PARENT.equals(myField)) {
            /* Update the Parent */
            mySecurity.setParent(pUpdate.getValue(MoneyWisePayee.class));
        } else if (MoneyWiseStaticDataType.CURRENCY.equals(myField)) {
            /* Update the Currency */
            mySecurity.setAssetCurrency(pUpdate.getValue(MoneyWiseCurrency.class));
        } else if (MoneyWiseBasicResource.ASSET_CLOSED.equals(myField)) {
            /* Update the Closed indication */
            mySecurity.setClosed(pUpdate.getValue(Boolean.class));
        } else if (MoneyWiseAccountInfoClass.SYMBOL.equals(myField)) {
            /* Update the Symbol */
            mySecurity.setSymbol(pUpdate.getValue(String.class));
        } else if (MoneyWiseAccountInfoClass.REGION.equals(myField)) {
            /* Update the Region */
            mySecurity.setRegion(pUpdate.getValue(MoneyWiseRegion.class));
        } else if (MoneyWiseAccountInfoClass.UNDERLYINGSTOCK.equals(myField)) {
            /* Update the Underlying Stock */
            mySecurity.setUnderlyingStock(pUpdate.getValue(MoneyWiseSecurity.class));
        } else if (MoneyWiseAccountInfoClass.OPTIONPRICE.equals(myField)) {
            /* Update the OptionPrice */
            mySecurity.setOptionPrice(pUpdate.getValue(OceanusPrice.class));
        } else if (MoneyWiseAccountInfoClass.NOTES.equals(myField)) {
            /* Update the Notes */
            mySecurity.setNotes(pUpdate.getValue(char[].class));
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        final MoneyWiseSecurity myItem = getItem();
        final MoneyWisePayee myParent = myItem.getParent();
        if (!pUpdates) {
            final MoneyWiseSecurityType myType = myItem.getCategory();
            final MoneyWiseCurrency myCurrency = myItem.getAssetCurrency();
            final MoneyWiseRegion myRegion = myItem.getRegion();
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
    public void addNewPrice(final MoneyWiseSecurity pSecurity) throws OceanusException {
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
    public void buildSecTypeMenu(final TethysUIScrollMenu<MoneyWiseAssetCategory> pMenu,
                                 final MoneyWiseSecurity pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseSecurityType myCurr = pSecurity.getCategory();
        TethysUIScrollItem<MoneyWiseAssetCategory> myActive = null;

        /* Access SecurityTypes */
        final MoneyWiseSecurityTypeList myTypes = getDataList(MoneyWiseStaticDataType.SECURITYTYPE, MoneyWiseSecurityTypeList.class);

        /* Loop through the SecurityTypes */
        final Iterator<MoneyWiseSecurityType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseSecurityType myType = myIterator.next();

            /* Ignore deleted or disabled */
            final boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the secType */
            final TethysUIScrollItem<MoneyWiseAssetCategory> myItem = pMenu.addItem(myType);

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
    public void buildParentMenu(final TethysUIScrollMenu<MoneyWisePayee> pMenu,
                                final MoneyWiseSecurity pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseSecurityClass myType = pSecurity.getCategoryClass();
        final MoneyWisePayee myCurr = pSecurity.getParent();
        TethysUIScrollItem<MoneyWisePayee> myActive = null;

        /* Access Payees */
        final MoneyWisePayeeList myPayees = getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);

        /* Loop through the Payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getCategoryClass().canParentSecurity(myType);
            bIgnore |= myPayee.isClosed();
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
     * Build the region menu for an item.
     * @param pMenu the menu
     * @param pSecurity the security to build for
     */
    public void buildRegionMenu(final TethysUIScrollMenu<MoneyWiseRegion> pMenu,
                                final MoneyWiseSecurity pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseRegion myCurr = pSecurity.getRegion();
        TethysUIScrollItem<MoneyWiseRegion> myActive = null;

        /* Access regions */
        final MoneyWiseRegionList myRegions = getDataList(MoneyWiseBasicDataType.REGION, MoneyWiseRegionList.class);

        /* Loop through the Regions */
        final Iterator<MoneyWiseRegion> myIterator = myRegions.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseRegion myRegion = myIterator.next();

            /* Ignore deleted */
            final boolean bIgnore = myRegion.isDeleted();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the region */
            final TethysUIScrollItem<MoneyWiseRegion> myItem = pMenu.addItem(myRegion);

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
    public void buildStockMenu(final TethysUIScrollMenu<MoneyWiseSecurity> pMenu,
                               final MoneyWiseSecurity pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseSecurity myCurr = pSecurity.getUnderlyingStock();
        TethysUIScrollItem<MoneyWiseSecurity> myActive = null;

        /* Access securities */
        final MoneyWiseSecurityList mySecurities = getDataList(MoneyWiseBasicDataType.SECURITY, MoneyWiseSecurityList.class);

        /* Loop through the Securities */
        final Iterator<MoneyWiseSecurity> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseSecurity mySecurity = myIterator.next();

            /* Ignore deleted and non share */
            boolean bIgnore = mySecurity.isDeleted();
            bIgnore |= !mySecurity.getCategoryClass().isShares();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the region */
            final TethysUIScrollItem<MoneyWiseSecurity> myItem = pMenu.addItem(mySecurity);

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
    public void buildCurrencyMenu(final TethysUIScrollMenu<MoneyWiseCurrency> pMenu,
                                  final MoneyWiseSecurity pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final MoneyWiseCurrency myCurr = pSecurity.getAssetCurrency();
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
