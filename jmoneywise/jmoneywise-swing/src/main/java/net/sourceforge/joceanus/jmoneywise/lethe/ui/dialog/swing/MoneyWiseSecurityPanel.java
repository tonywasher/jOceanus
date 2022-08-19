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

import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisLetheFieldSetBase.MetisLetheFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.base.MoneyWiseItemPanel;
import net.sourceforge.joceanus.jmoneywise.atlas.ui.dialog.MoneyWiseSecurityPriceTable;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetBase;
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
 * Panel to display/edit/create a Security.
 */
public class MoneyWiseSecurityPanel
        extends MoneyWiseItemPanel<Security> {
    /**
     * Prices Tab Title.
     */
    private static final String TAB_PRICES = MoneyWiseUIResource.SECURITYPANEL_TAB_PRICES.getValue();

    /**
     * SecurityPrice Table.
     */
    private final MoneyWiseSecurityPriceTable thePrices;

    /**
     * Table tab item.
     */
    private final MoneyWiseDataTabTable thePricesTab;

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
    public MoneyWiseSecurityPanel(final TethysGuiFactory pFactory,
                                  final MoneyWiseView pView,
                                  final MetisSwingFieldManager pFieldMgr,
                                  final UpdateSet<MoneyWiseDataType> pUpdateSet,
                                  final MetisErrorPanel pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Build the main panel */
        final MoneyWiseDataPanel myPanel = buildMainPanel(pFactory);

        /* Build the info panel */
        buildInfoPanel(pFactory);

        /* Build the notes panel */
        buildNotesPanel(pFactory);

        /* Create the SecurityPrices table */
        thePrices = new MoneyWiseSecurityPriceTable(pView, getUpdateSet(), pError);
        thePricesTab = new MoneyWiseDataTabTable(TAB_PRICES, thePrices);

        /* Define the panel */
        defineMainPanel(myPanel);

        /* Create the listener */
        thePrices.getEventRegistrar().addEventListener(e -> {
            updateActions();
            fireStateChanged();
        });
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private MoneyWiseDataPanel buildMainPanel(final TethysGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataPanel myPanel = new MoneyWiseDataPanel(Security.NAMELEN);

        /* Create the text fields */
        final TethysStringEditField myName = pFactory.newStringField();
        final TethysStringEditField myDesc = pFactory.newStringField();

        /* Create the buttons */
        final TethysScrollButtonManager<SecurityType> myTypeButton = pFactory.newScrollButton();
        final TethysScrollButtonManager<Payee> myParentButton = pFactory.newScrollButton();
        final TethysScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        final TethysIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* Assign the fields to the panel */
        myPanel.addField(AssetBase.FIELD_NAME, MetisDataType.STRING, myName);
        myPanel.addField(AssetBase.FIELD_DESC, MetisDataType.STRING, myDesc);
        myPanel.addField(AssetBase.FIELD_CATEGORY, SecurityType.class, myTypeButton);
        myPanel.addField(AssetBase.FIELD_PARENT, Payee.class, myParentButton);
        myPanel.addField(AssetBase.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        myPanel.addField(AssetBase.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Layout the panel */
        myPanel.compactPanel();

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildSecTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        final Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
        myClosedButton.setIconMapSet(() -> myMapSets.get(theClosedState));

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build info subPanel.
     * @param pFactory the GUI factory
     */
    private void buildInfoPanel(final TethysGuiFactory pFactory) {
        /* Create a new panel */
        final MoneyWiseDataTabItem myTab = new MoneyWiseDataTabItem(TAB_DETAILS, DataItem.NAMELEN >> 1);

        /* Allocate fields */
        final TethysStringEditField mySymbol = pFactory.newStringField();
        final TethysStringEditField myPrice = pFactory.newStringField();

        /* Create the buttons */
        final TethysScrollButtonManager<Region> myRegionButton = pFactory.newScrollButton();
        final TethysScrollButtonManager<Security> myStockButton = pFactory.newScrollButton();

        /* Assign the fields to the panel */
        myTab.addField(SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL), MetisDataType.STRING, mySymbol);
        myTab.addField(SecurityInfoSet.getFieldForClass(AccountInfoClass.REGION), Region.class, myRegionButton);
        myTab.addField(SecurityInfoSet.getFieldForClass(AccountInfoClass.UNDERLYINGSTOCK), Security.class, myStockButton);
        myTab.addField(SecurityInfoSet.getFieldForClass(AccountInfoClass.OPTIONPRICE), MetisDataType.PRICE, myPrice);

        /* Layout the panel */
        myTab.compactPanel();

        /* Configure the menuBuilders */
        myRegionButton.setMenuConfigurator(c -> buildRegionMenu(c, getItem()));
        myStockButton.setMenuConfigurator(c -> buildStockMenu(c, getItem()));
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
        myTab.addField(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Layout the panel */
        myTab.compactPanel();
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
        /* Access the fieldSet */
        final MetisSwingFieldSet<Security> myFieldSet = getFieldSet();

        /* Access the item */
        final Security mySecurity = getItem();
        final boolean bIsClosed = mySecurity.isClosed();
        final boolean bIsActive = mySecurity.isActive();
        final boolean bIsRelevant = mySecurity.isRelevant();
        final Currency myCurrency = mySecurity.getCurrency();

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        myFieldSet.setVisibility(AssetBase.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                                              ? !mySecurity.getParent().isClosed()
                                              : !bIsRelevant;
        myFieldSet.setEditable(AssetBase.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || mySecurity.getDesc() != null;
        myFieldSet.setVisibility(AssetBase.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowNotes = isEditable || mySecurity.getNotes() != null;
        myFieldSet.setVisibility(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Determine whether the symbol field should be visible */
        MetisLetheField myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL);
        boolean bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.SYMBOL);
        boolean bShowField = bEditField || mySecurity.getSymbol() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the region field should be visible */
        myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.REGION);
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.REGION);
        bShowField = bEditField || mySecurity.getRegion() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the stock field should be visible */
        myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.UNDERLYINGSTOCK);
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.UNDERLYINGSTOCK);
        bShowField = bEditField || mySecurity.getRegion() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);

        /* Determine whether the price field should be visible */
        myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.OPTIONPRICE);
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.OPTIONPRICE);
        bShowField = bEditField || mySecurity.getRegion() != null;
        myFieldSet.setVisibility(myField, bShowField);
        myFieldSet.setEditable(myField, bEditField);
        myFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Security type and currency cannot be changed if the item is active */
        myFieldSet.setEditable(AssetBase.FIELD_CATEGORY, isEditable && !bIsActive);
        myFieldSet.setEditable(AssetBase.FIELD_CURRENCY, isEditable && !bIsActive);

        /* Set editable value for parent */
        myFieldSet.setEditable(AssetBase.FIELD_PARENT, isEditable && !bIsClosed);

        /* Set the table visibility */
        boolean bShowPrices = !mySecurity.isSecurityClass(SecurityTypeClass.STOCKOPTION);
        bShowPrices &= isEditable || !thePrices.isViewEmpty();
        thePricesTab.setRequireVisible(bShowPrices);
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
    protected void updateField(final MetisLetheFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisLetheField myField = pUpdate.getField();
        final Security mySecurity = getItem();

        /* Process updates */
        if (myField.equals(AssetBase.FIELD_NAME)) {
            /* Update the Name */
            mySecurity.setName(pUpdate.getString());
        } else if (myField.equals(AssetBase.FIELD_DESC)) {
            /* Update the Description */
            mySecurity.setDescription(pUpdate.getString());
        } else if (myField.equals(AssetBase.FIELD_CATEGORY)) {
            /* Update the Security Type */
            mySecurity.setCategory(pUpdate.getValue(SecurityType.class));
            mySecurity.autoCorrect(getUpdateSet());
        } else if (myField.equals(AssetBase.FIELD_PARENT)) {
            /* Update the Parent */
            mySecurity.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(AssetBase.FIELD_CURRENCY)) {
            /* Update the Currency */
            mySecurity.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(AssetBase.FIELD_CLOSED)) {
            /* Update the Closed indication */
            mySecurity.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (SecurityInfoSet.getClassForField(myField)) {
                case NOTES:
                    mySecurity.setNotes(pUpdate.getCharArray());
                    break;
                case SYMBOL:
                    mySecurity.setSymbol(pUpdate.getString());
                    break;
                case REGION:
                    mySecurity.setRegion(pUpdate.getValue(Region.class));
                    break;
                case UNDERLYINGSTOCK:
                    mySecurity.setUnderlyingStock(pUpdate.getValue(Security.class));
                    break;
                case OPTIONPRICE:
                    mySecurity.setOptionPrice(pUpdate.getPrice());
                    break;
                default:
                    break;
            }
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

    @Override
    public void setItem(final Security pItem) {
        /* Update the prices */
        thePrices.setSecurity(pItem);

        /* Pass call onwards */
        super.setItem(pItem);
    }

    @Override
    public void setNewItem(final Security pItem) {
        /* Update the prices */
        thePrices.setSecurity(pItem);

        /* Pass call onwards */
        super.setNewItem(pItem);
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
