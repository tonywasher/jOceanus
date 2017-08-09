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
 * Panel to display/edit/create a Portfolio.
 */
public class PortfolioPanel
        extends MoneyWiseItemPanel<Portfolio> {
    /**
     * The Field Set.
     */
    private final MetisFieldSet<Portfolio> theFieldSet;

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
    public PortfolioPanel(final TethysSwingGuiFactory pFactory,
                          final MetisFieldManager pFieldMgr,
                          final UpdateSet<MoneyWiseDataType> pUpdateSet,
                          final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel(pFactory);

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel(pFactory);
        myTabs.add(TAB_DETAILS, myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel(pFactory);
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();
    }

    /**
     * Build Main subPanel.
     * @param pFactory the GUI factory
     * @return the panel
     */
    private JPanel buildMainPanel(final TethysSwingGuiFactory pFactory) {
        /* Create the text fields */
        TethysSwingStringTextField myName = pFactory.newStringField();
        TethysSwingStringTextField myDesc = pFactory.newStringField();

        /* Create the buttons */
        TethysSwingScrollButtonManager<PortfolioType> myTypeButton = pFactory.newScrollButton();
        TethysSwingScrollButtonManager<Payee> myParentButton = pFactory.newScrollButton();
        TethysSwingScrollButtonManager<AssetCurrency> myCurrencyButton = pFactory.newScrollButton();
        TethysSwingIconButtonManager<Boolean> myClosedButton = pFactory.newIconButton();

        /* restrict the fields */
        restrictField(myName, Portfolio.NAMELEN);
        restrictField(myDesc, Portfolio.NAMELEN);
        restrictField(myTypeButton, Portfolio.NAMELEN);
        restrictField(myParentButton, Portfolio.NAMELEN);
        restrictField(myCurrencyButton, Portfolio.NAMELEN);
        restrictField(myClosedButton, Portfolio.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Portfolio.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(Portfolio.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(Portfolio.FIELD_PORTTYPE, PortfolioType.class, myTypeButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_PARENT, Payee.class, myParentButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_CURRENCY, AssetCurrency.class, myCurrencyButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_PORTTYPE, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_CLOSED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Configure the menuBuilders */
        myTypeButton.setMenuConfigurator(c -> buildTypeMenu(c, getItem()));
        myParentButton.setMenuConfigurator(c -> buildParentMenu(c, getItem()));
        myCurrencyButton.setMenuConfigurator(c -> buildCurrencyMenu(c, getItem()));
        Map<Boolean, TethysIconMapSet<Boolean>> myMapSets = MoneyWiseIcon.configureLockedIconButton();
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
        TethysSwingStringTextField mySortCode = pFactory.newStringField();
        TethysSwingStringTextField myAccount = pFactory.newStringField();
        TethysSwingStringTextField myReference = pFactory.newStringField();
        TethysSwingStringTextField myWebSite = pFactory.newStringField();
        TethysSwingStringTextField myCustNo = pFactory.newStringField();
        TethysSwingStringTextField myUserId = pFactory.newStringField();
        TethysSwingStringTextField myPassWord = pFactory.newStringField();

        /* Restrict the fields */
        int myWidth = Portfolio.NAMELEN >> 1;
        restrictField(mySortCode, myWidth);
        restrictField(myAccount, myWidth);
        restrictField(myReference, myWidth);
        restrictField(myWebSite, myWidth);
        restrictField(myCustNo, myWidth);
        restrictField(myUserId, myWidth);
        restrictField(myPassWord, myWidth);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), MetisDataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), MetisDataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), MetisDataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), MetisDataType.CHARARRAY, myWebSite);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), MetisDataType.CHARARRAY, myCustNo);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), MetisDataType.CHARARRAY, myUserId);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), MetisDataType.CHARARRAY, myPassWord);

        /* Create the extras panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), myPanel);
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
        TethysSwingTextArea myNotes = pFactory.newTextArea();
        TethysSwingScrollPaneManager myScroll = pFactory.newScrollPane();
        myScroll.setContent(myNotes);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Portfolio myItem = getItem();
        if (myItem != null) {
            PortfolioList myPortfolios = getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
            setItem(myPortfolios.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Portfolio myPortfolio = getItem();
        boolean bIsClosed = myPortfolio.isClosed();
        boolean bIsActive = myPortfolio.isActive();
        boolean bIsRelevant = myPortfolio.isRelevant();
        boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Portfolio.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = bIsClosed || !bIsRelevant;
        theFieldSet.setEditable(Portfolio.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState = bEditClosed;

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myPortfolio.getDesc() != null;
        theFieldSet.setVisibility(Portfolio.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        boolean bShowSortCode = isEditable || myPortfolio.getSortCode() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        boolean bShowAccount = isEditable || myPortfolio.getAccount() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        boolean bShowReference = isEditable || myPortfolio.getReference() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        boolean bShowWebSite = isEditable || myPortfolio.getWebSite() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), bShowWebSite);
        boolean bShowCustNo = isEditable || myPortfolio.getCustNo() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), bShowCustNo);
        boolean bShowUserId = isEditable || myPortfolio.getUserId() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), bShowUserId);
        boolean bShowPasswd = isEditable || myPortfolio.getPassword() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), bShowPasswd);
        boolean bShowNotes = isEditable || myPortfolio.getNotes() != null;
        theFieldSet.setVisibility(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Type, Parent and Currency status cannot be changed if the item is active */
        theFieldSet.setEditable(Portfolio.FIELD_PORTTYPE, bIsChangeable);
        theFieldSet.setEditable(Portfolio.FIELD_PARENT, bIsChangeable);
        theFieldSet.setEditable(Portfolio.FIELD_CURRENCY, bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setEditable(Portfolio.FIELD_PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
        Portfolio myPortfolio = getItem();

        /* Process updates */
        if (myField.equals(Portfolio.FIELD_NAME)) {
            /* Update the Name */
            myPortfolio.setName(pUpdate.getString());
        } else if (myField.equals(Portfolio.FIELD_DESC)) {
            /* Update the Description */
            myPortfolio.setDescription(pUpdate.getString());
        } else if (myField.equals(Portfolio.FIELD_PORTTYPE)) {
            /* Update the portfolioType */
            myPortfolio.setPortfolioType(pUpdate.getValue(PortfolioType.class));
        } else if (myField.equals(Portfolio.FIELD_PARENT)) {
            /* Update the Parent */
            myPortfolio.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Portfolio.FIELD_CURRENCY)) {
            /* Update the Currency */
            myPortfolio.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(Portfolio.FIELD_CLOSED)) {
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
        Portfolio myItem = getItem();
        Payee myParent = myItem.getParent();
        if (!pUpdates) {
            PortfolioType myType = myItem.getPortfolioType();
            declareGoToItem(myType);
            AssetCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the portfolioType menu for an item.
     * @param pMenu the menu
     * @param pPortfolio the portfolio to build for
     */
    public void buildTypeMenu(final TethysScrollMenu<PortfolioType, Icon> pMenu,
                              final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        PortfolioType myCurr = pPortfolio.getPortfolioType();
        TethysScrollMenuItem<PortfolioType> myActive = null;

        /* Access PortfolioTypes */
        PortfolioTypeList myTypes = getDataList(MoneyWiseDataType.PORTFOLIOTYPE, PortfolioTypeList.class);

        /* Loop through the Types */
        Iterator<PortfolioType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            PortfolioType myType = myIterator.next();

            /* Create a new action for the type */
            TethysScrollMenuItem<PortfolioType> myItem = pMenu.addItem(myType);

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
    public void buildParentMenu(final TethysScrollMenu<Payee, Icon> pMenu,
                                final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        Payee myCurr = pPortfolio.getParent();
        TethysScrollMenuItem<Payee> myActive = null;

        /* Access Payees */
        PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted/closed and ones that cannot own this portfolio */
            boolean bIgnore = myPayee.isDeleted() || myPayee.isClosed();
            bIgnore |= !myPayee.getPayeeTypeClass().canParentPortfolio();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the payee */
            TethysScrollMenuItem<Payee> myItem = pMenu.addItem(myPayee);

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
    public void buildCurrencyMenu(final TethysScrollMenu<AssetCurrency, Icon> pMenu,
                                  final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenu.removeAllItems();

        /* Record active item */
        AssetCurrency myCurr = pPortfolio.getAssetCurrency();
        TethysScrollMenuItem<AssetCurrency> myActive = null;

        /* Access Currencies */
        AssetCurrencyList myCurrencies = getDataList(MoneyWiseDataType.CURRENCY, AssetCurrencyList.class);

        /* Loop through the AccountCurrencies */
        Iterator<AssetCurrency> myIterator = myCurrencies.iterator();
        while (myIterator.hasNext()) {
            AssetCurrency myCurrency = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myCurrency.isDeleted() || !myCurrency.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the currency */
            TethysScrollMenuItem<AssetCurrency> myItem = pMenu.addItem(myCurrency);

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
