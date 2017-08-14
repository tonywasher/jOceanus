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
import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region.RegionList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDataTextField.TethysSwingStringTextField;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTextArea;

/**
 * Panel to display/edit/create a Security.
 */
public class SecurityPanel
        extends MoneyWiseItemPanel<Security> {
    /**
     * Info Tab Title.
     */
    private static final String TAB_INFO = MoneyWiseUIResource.TRANSPANEL_TAB_INFO.getValue();

    /**
     * Prices Tab Title.
     */
    private static final String TAB_PRICES = MoneyWiseUIResource.SECURITYPANEL_TAB_PRICES.getValue();

    /**
     * The Field Set.
     */
    private final MetisFieldSet<Security> theFieldSet;

    /**
     * SecurityPrice Table.
     */
    private final SecurityPriceTable thePrices;

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
    public SecurityPanel(final TethysSwingGuiFactory pFactory,
                         final SwingView pView,
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

        /* Build the info panel */
        JPanel myPanel = buildInfoPanel(pFactory);
        myTabs.add(TAB_INFO, myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel(pFactory);
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Create the SecurityPrices table */
        thePrices = new SecurityPriceTable(pView, pFieldMgr, getUpdateSet(), pError);
        myTabs.add(TAB_PRICES, thePrices.getNode());

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

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
    private JPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create the text fields */
        final TethysSwingStringTextField myName = pFactory.newStringField();
        final TethysSwingStringTextField myDesc = pFactory.newStringField();
        final TethysSwingStringTextField mySymbol = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<SecurityType> myTypeButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<Payee> myParentButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        final TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* restrict the fields */
        restrictField(myName, Security.NAMELEN);
        restrictField(myDesc, Security.NAMELEN);
        restrictField(mySymbol, Security.NAMELEN);
        restrictField(myTypeButton, Security.NAMELEN);
        restrictField(myCurrencyButton, Security.NAMELEN);
        restrictField(myParentButton, Security.NAMELEN);
        restrictField(myClosedButton, Security.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Security.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(Security.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(Security.FIELD_SECTYPE, SecurityType.class, myTypeButton);
        theFieldSet.addFieldElement(Security.FIELD_PARENT, Payee.class, myParentButton);
        theFieldSet.addFieldElement(Security.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        theFieldSet.addFieldElement(Security.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Security.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_SECTYPE, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_CLOSED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
     * @return the panel
     */
    private JPanel buildInfoPanel(final TethysSwingGuiFactory pFactory) {
        /* Allocate fields */
        final TethysSwingStringTextField mySymbol = pFactory.newStringField();
        final TethysSwingStringTextField myPrice = pFactory.newStringField();

        /* Create the buttons */
        final TethysSwingScrollButtonManager<Region> myRegionButton = pFactory.newScrollButton();
        final TethysSwingScrollButtonManager<Security> myStockButton = pFactory.newScrollButton();

        /* Restrict the fields */
        final int myWidth = Transaction.DESCLEN >> 1;
        restrictField(mySymbol, myWidth);
        restrictField(myPrice, myWidth);
        restrictField(myRegionButton, myWidth);
        restrictField(myStockButton, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL), MetisDataType.STRING, mySymbol);
        theFieldSet.addFieldElement(SecurityInfoSet.getFieldForClass(AccountInfoClass.REGION), Region.class, myRegionButton);
        theFieldSet.addFieldElement(SecurityInfoSet.getFieldForClass(AccountInfoClass.UNDERLYINGSTOCK), Security.class, myStockButton);
        theFieldSet.addFieldElement(SecurityInfoSet.getFieldForClass(AccountInfoClass.OPTIONPRICE), MetisDataType.PRICE, myPrice);

        /* Create the Info panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the info panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL), myPanel);
        theFieldSet.addFieldToPanel(SecurityInfoSet.getFieldForClass(AccountInfoClass.REGION), myPanel);
        theFieldSet.addFieldToPanel(SecurityInfoSet.getFieldForClass(AccountInfoClass.UNDERLYINGSTOCK), myPanel);
        theFieldSet.addFieldToPanel(SecurityInfoSet.getFieldForClass(AccountInfoClass.OPTIONPRICE), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Configure the menuBuilders */
        myRegionButton.setMenuConfigurator(c -> buildRegionMenu(c, getItem()));
        myStockButton.setMenuConfigurator(c -> buildStockMenu(c, getItem()));

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
        theFieldSet.addFieldElement(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        final TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the notes panel */
        final SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
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
        final Currency myCurrency = mySecurity.getCurrency();

        /* Determine whether the closed button should be visible */
        final boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Security.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        final boolean bEditClosed = bIsClosed
                                              ? !mySecurity.getParent().isClosed()
                                              : !bIsRelevant;
        theFieldSet.setEditable(Security.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        final boolean bShowDesc = isEditable || mySecurity.getDesc() != null;
        theFieldSet.setVisibility(Security.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        final boolean bShowNotes = isEditable || mySecurity.getNotes() != null;
        theFieldSet.setVisibility(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Determine whether the symbol field should be visible */
        MetisField myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.SYMBOL);
        boolean bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.SYMBOL);
        boolean bShowField = bEditField || mySecurity.getSymbol() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the region field should be visible */
        myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.REGION);
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.REGION);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the stock field should be visible */
        myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.UNDERLYINGSTOCK);
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.UNDERLYINGSTOCK);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);

        /* Determine whether the price field should be visible */
        myField = SecurityInfoSet.getFieldForClass(AccountInfoClass.OPTIONPRICE);
        bEditField = isEditable && isEditableField(mySecurity, AccountInfoClass.OPTIONPRICE);
        bShowField = bEditField || mySecurity.getRegion() != null;
        theFieldSet.setVisibility(myField, bShowField);
        theFieldSet.setEditable(myField, bEditField);
        theFieldSet.setAssumedCurrency(myField, myCurrency);

        /* Security type and currency cannot be changed if the item is active */
        theFieldSet.setEditable(Security.FIELD_SECTYPE, isEditable && !bIsActive);
        theFieldSet.setEditable(Security.FIELD_CURRENCY, isEditable && !bIsActive);

        /* Set editable value for parent */
        theFieldSet.setEditable(Security.FIELD_PARENT, isEditable && !bIsClosed);
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
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        final MetisField myField = pUpdate.getField();
        final Security mySecurity = getItem();

        /* Process updates */
        if (myField.equals(Security.FIELD_NAME)) {
            /* Update the Name */
            mySecurity.setName(pUpdate.getString());
        } else if (myField.equals(Security.FIELD_DESC)) {
            /* Update the Description */
            mySecurity.setDescription(pUpdate.getString());
        } else if (myField.equals(Security.FIELD_SECTYPE)) {
            /* Update the Security Type */
            mySecurity.setSecurityType(pUpdate.getValue(SecurityType.class));
            mySecurity.autoCorrect(getUpdateSet());
        } else if (myField.equals(Security.FIELD_PARENT)) {
            /* Update the Parent */
            mySecurity.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Security.FIELD_CURRENCY)) {
            /* Update the Currency */
            mySecurity.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(Security.FIELD_CLOSED)) {
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
            final SecurityType myType = myItem.getSecurityType();
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
    public void buildSecTypeMenu(final TethysScrollMenu<SecurityType, Icon> pMenu,
                                 final Security pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final SecurityType myCurr = pSecurity.getSecurityType();
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
    public void buildParentMenu(final TethysScrollMenu<Payee, Icon> pMenu,
                                final Security pSecurity) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        final SecurityTypeClass myType = pSecurity.getSecurityTypeClass();
        final Payee myCurr = pSecurity.getParent();
        TethysScrollMenuItem<Payee> myActive = null;

        /* Access Payees */
        final PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        final Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentSecurity(myType);
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
    public void buildRegionMenu(final TethysScrollMenu<Region, Icon> pMenu,
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
    public void buildStockMenu(final TethysScrollMenu<Security, Icon> pMenu,
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
            bIgnore |= !mySecurity.getSecurityTypeClass().isShares();
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
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency, Icon> pMenu,
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
