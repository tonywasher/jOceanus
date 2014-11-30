/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Portfolio.
 */
public class PortfolioPanel
        extends MoneyWiseDataItemPanel<Portfolio> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 8504264018922234415L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Portfolio> theFieldSet;

    /**
     * Parent Button Field.
     */
    private final JScrollButton<Payee> theParentButton;

    /**
     * Currency Button Field.
     */
    private final JScrollButton<AssetCurrency> theCurrencyButton;

    /**
     * Closed Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * TaxFree Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theTaxFreeState;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public PortfolioPanel(final JFieldManager pFieldMgr,
                          final UpdateSet<MoneyWiseDataType> pUpdateSet,
                          final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the buttons */
        theParentButton = new JScrollButton<Payee>();
        theCurrencyButton = new JScrollButton<AssetCurrency>();

        /* Set button states */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);
        theTaxFreeState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel();
        myTabs.add(TAB_DETAILS, myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel();
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new PortfolioListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Build the button states */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildLockedButton(theClosedState);
        JIconButton<Boolean> myTaxFreeButton = new JIconButton<Boolean>(theTaxFreeState);
        MoneyWiseIcons.buildOptionButton(theTaxFreeState);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();

        /* restrict the fields */
        restrictField(myName, Portfolio.NAMELEN);
        restrictField(myDesc, Portfolio.NAMELEN);
        restrictField(theParentButton, Portfolio.NAMELEN);
        restrictField(theCurrencyButton, Portfolio.NAMELEN);
        restrictField(myClosedButton, Portfolio.NAMELEN);
        restrictField(myTaxFreeButton, Portfolio.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Portfolio.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(Portfolio.FIELD_DESC, DataType.STRING, myDesc);
        theFieldSet.addFieldElement(Portfolio.FIELD_PARENT, Payee.class, theParentButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_CURRENCY, AssetCurrency.class, theCurrencyButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_CLOSED, Boolean.class, myClosedButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_TAXFREE, Boolean.class, myTaxFreeButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_CLOSED, myPanel);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_TAXFREE, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extras subPanel.
     * @return the panel
     */
    private JPanel buildXtrasPanel() {
        /* Allocate fields */
        JTextField mySortCode = new JTextField();
        JTextField myAccount = new JTextField();
        JTextField myReference = new JTextField();
        JTextField myWebSite = new JTextField();
        JTextField myCustNo = new JTextField();
        JTextField myUserId = new JTextField();
        JTextField myPassWord = new JTextField();

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
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), DataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), DataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), DataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), DataType.CHARARRAY, myWebSite);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), DataType.CHARARRAY, myCustNo);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), DataType.CHARARRAY, myUserId);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), DataType.CHARARRAY, myPassWord);

        /* Create the extras panel */
        JEnablePanel myPanel = new JEnablePanel();

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
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Notes subPanel.
     * @return the panel
     */
    private JPanel buildNotesPanel() {
        /* Allocate fields */
        JTextArea myNotes = new JTextArea();
        JScrollPane myScroll = new JScrollPane(myNotes);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
        theClosedState.setState(bEditClosed);

        /* Determine whether the taxFree button should be visible */
        boolean bShowTaxFree = myPortfolio.isTaxFree() || bIsChangeable;
        theFieldSet.setVisibility(Portfolio.FIELD_TAXFREE, bShowTaxFree);

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

        /* Parent, Currency and TaxFree status cannot be changed if the item is active */
        theFieldSet.setEditable(Portfolio.FIELD_PARENT, bIsChangeable);
        theFieldSet.setEditable(Portfolio.FIELD_CURRENCY, bIsChangeable);
        theFieldSet.setEditable(Portfolio.FIELD_TAXFREE, bIsChangeable);
        theTaxFreeState.setState(bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setEditable(Portfolio.FIELD_PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Portfolio myPortfolio = getItem();

        /* Process updates */
        if (myField.equals(Portfolio.FIELD_NAME)) {
            /* Update the Name */
            myPortfolio.setName(pUpdate.getString());
        } else if (myField.equals(Portfolio.FIELD_DESC)) {
            /* Update the Description */
            myPortfolio.setDescription(pUpdate.getString());
        } else if (myField.equals(Portfolio.FIELD_PARENT)) {
            /* Update the Parent */
            myPortfolio.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Portfolio.FIELD_CURRENCY)) {
            /* Update the Currency */
            myPortfolio.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(Portfolio.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myPortfolio.setClosed(pUpdate.getBoolean());
        } else if (myField.equals(Portfolio.FIELD_TAXFREE)) {
            /* Update the taxFree indication */
            myPortfolio.setTaxFree(pUpdate.getBoolean());
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
            AssetCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the parent list for an item.
     * @param pMenuBuilder the menu builder
     * @param pPortfolio the portfolio to build for
     */
    public void buildParentMenu(final JScrollMenuBuilder<Payee> pMenuBuilder,
                                final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        Payee myCurr = pPortfolio.getParent();
        JMenuItem myActive = null;

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
            JMenuItem myItem = pMenuBuilder.addItem(myPayee);

            /* If this is the active parent */
            if (myPayee.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the currency list for an item.
     * @param pMenuBuilder the menu builder
     * @param pPortfolio the portfolio to build for
     */
    public void buildCurrencyMenu(final JScrollMenuBuilder<AssetCurrency> pMenuBuilder,
                                  final Portfolio pPortfolio) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        AssetCurrency myCurr = pPortfolio.getAssetCurrency();
        JMenuItem myActive = null;

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
            JMenuItem myItem = pMenuBuilder.addItem(myCurrency);

            /* If this is the active currency */
            if (myCurrency.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Portfolio Listener.
     */
    private final class PortfolioListener
            implements ChangeListener {
        /**
         * The Parent Menu Builder.
         */
        private final JScrollMenuBuilder<Payee> theParentMenuBuilder;

        /**
         * The Currency Menu Builder.
         */
        private final JScrollMenuBuilder<AssetCurrency> theCurrencyMenuBuilder;

        /**
         * Constructor.
         */
        private PortfolioListener() {
            /* Access the MenuBuilders */
            theParentMenuBuilder = theParentButton.getMenuBuilder();
            theParentMenuBuilder.addChangeListener(this);
            theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
            theCurrencyMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theParentMenuBuilder.equals(o)) {
                buildParentMenu(theParentMenuBuilder, getItem());
            } else if (theCurrencyMenuBuilder.equals(o)) {
                buildCurrencyMenu(theCurrencyMenuBuilder, getItem());
            }
        }
    }
}
