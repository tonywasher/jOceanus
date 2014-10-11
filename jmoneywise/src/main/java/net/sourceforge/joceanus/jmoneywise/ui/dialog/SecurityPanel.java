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
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jmoneywise.views.View;
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
 * Panel to display/edit/create a Security.
 */
public class SecurityPanel
        extends MoneyWiseDataItemPanel<Security> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7711868258621672746L;

    /**
     * Prices Tab Title.
     */
    private static final String TAB_PRICES = MoneyWiseUIControlResource.SECURITYPANEL_TAB_PRICES.getValue();

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Security> theFieldSet;

    /**
     * Security Type Button Field.
     */
    private final JScrollButton<SecurityType> theTypeButton;

    /**
     * Security Parent Button Field.
     */
    private final JScrollButton<Payee> theParentButton;

    /**
     * Currency Button Field.
     */
    private final JScrollButton<AccountCurrency> theCurrencyButton;

    /**
     * Closed Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * SecurityPrice Table.
     */
    private final SecurityPriceTable thePrices;

    /**
     * Constructor.
     * @param pView the data view
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public SecurityPanel(final View pView,
                         final JFieldManager pFieldMgr,
                         final UpdateSet<MoneyWiseDataType> pUpdateSet,
                         final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the buttons */
        theTypeButton = new JScrollButton<SecurityType>();
        theParentButton = new JScrollButton<Payee>();
        theCurrencyButton = new JScrollButton<AccountCurrency>();

        /* Set closed button */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the notes panel */
        JPanel myPanel = buildNotesPanel();
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Create the SecurityPrices table */
        thePrices = new SecurityPriceTable(pView, pFieldMgr, getUpdateSet(), pError);
        myTabs.add(TAB_PRICES, thePrices.getPanel());

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new SecurityListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Build the closed button state */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildLockedButton(theClosedState);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();
        JTextField mySymbol = new JTextField();

        /* restrict the fields */
        restrictField(myName, Security.NAMELEN);
        restrictField(myDesc, Security.NAMELEN);
        restrictField(mySymbol, Security.NAMELEN);
        restrictField(theTypeButton, Security.NAMELEN);
        restrictField(theCurrencyButton, Security.NAMELEN);
        restrictField(theParentButton, Security.NAMELEN);
        restrictField(myClosedButton, Security.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Security.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(Security.FIELD_DESC, DataType.STRING, myDesc);
        theFieldSet.addFieldElement(Security.FIELD_SYMBOL, DataType.STRING, mySymbol);
        theFieldSet.addFieldElement(Security.FIELD_SECTYPE, SecurityType.class, theTypeButton);
        theFieldSet.addFieldElement(Security.FIELD_PARENT, Payee.class, theParentButton);
        theFieldSet.addFieldElement(Security.FIELD_CURRENCY, AccountCurrency.class, theCurrencyButton);
        theFieldSet.addFieldElement(Security.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Security.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_SYMBOL, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_SECTYPE, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Security.FIELD_CLOSED, myPanel);
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
        theFieldSet.addFieldElement(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Security myItem = getItem();
        if (myItem != null) {
            SecurityList mySecurities = findDataList(MoneyWiseDataType.SECURITY, SecurityList.class);
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
        Security mySecurity = getItem();
        boolean bIsClosed = mySecurity.isClosed();
        boolean bIsActive = mySecurity.isActive();
        boolean bIsRelevant = mySecurity.isRelevant();

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Security.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = bIsClosed
                                       ? !mySecurity.getParent().isClosed()
                                       : !bIsRelevant;
        theFieldSet.setEditable(Security.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState.setState(bEditClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || mySecurity.getDesc() != null;
        theFieldSet.setVisibility(Security.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        boolean bShowNotes = isEditable || mySecurity.getNotes() != null;
        theFieldSet.setVisibility(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Security type and currency cannot be changed if the item is active */
        theFieldSet.setEditable(Security.FIELD_SECTYPE, isEditable && !bIsActive);
        theFieldSet.setEditable(Security.FIELD_CURRENCY, isEditable && !bIsActive);

        /* Set editable value for parent */
        theFieldSet.setEditable(Security.FIELD_PARENT, isEditable && !bIsClosed);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Security mySecurity = getItem();

        /* Process updates */
        if (myField.equals(Security.FIELD_NAME)) {
            /* Update the Name */
            mySecurity.setName(pUpdate.getString());
        } else if (myField.equals(Security.FIELD_DESC)) {
            /* Update the Description */
            mySecurity.setDescription(pUpdate.getString());
        } else if (myField.equals(Security.FIELD_SYMBOL)) {
            /* Update the Symbol */
            mySecurity.setSymbol(pUpdate.getString());
        } else if (myField.equals(Security.FIELD_SECTYPE)) {
            /* Update the Security Type */
            mySecurity.setSecurityType(pUpdate.getValue(SecurityType.class));
            mySecurity.adjustForCategory(getUpdateSet());
        } else if (myField.equals(Security.FIELD_PARENT)) {
            /* Update the Parent */
            mySecurity.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Security.FIELD_CURRENCY)) {
            /* Update the Currency */
            mySecurity.setSecurityCurrency(pUpdate.getValue(AccountCurrency.class));
        } else if (myField.equals(Security.FIELD_CLOSED)) {
            /* Update the Closed indication */
            mySecurity.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (SecurityInfoSet.getClassForField(myField)) {
                case NOTES:
                    mySecurity.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void buildGoToMenu() {
        Security myItem = getItem();
        SecurityType myType = myItem.getSecurityType();
        Payee myParent = myItem.getParent();
        AccountCurrency myCurrency = myItem.getSecurityCurrency();
        if (!getUpdateSet().hasUpdates()) {
            buildGoToEvent(myType);
            buildGoToEvent(myCurrency);
        }
        buildGoToEvent(myParent);
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
     * @throws JOceanusException on error
     */
    public void addNewPrice(final Security pSecurity) throws JOceanusException {
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
     * Build the securityType list for an item.
     * @param pMenuBuilder the menu builder
     * @param pSecurity the security to build for
     */
    public void buildSecTypeMenu(final JScrollMenuBuilder<SecurityType> pMenuBuilder,
                                 final Security pSecurity) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        SecurityType myCurr = pSecurity.getSecurityType();
        JMenuItem myActive = null;

        /* Access SecurityTypes */
        SecurityTypeList myTypes = findDataList(MoneyWiseDataType.SECURITYTYPE, SecurityTypeList.class);

        /* Loop through the SecurityTypes */
        Iterator<SecurityType> myIterator = myTypes.iterator();
        while (myIterator.hasNext()) {
            SecurityType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the secType */
            JMenuItem myItem = pMenuBuilder.addItem(myType);

            /* If this is the active secType */
            if (myType.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the parent list for an item.
     * @param pMenuBuilder the menu builder
     * @param pSecurity the security to build for
     */
    public void buildParentMenu(final JScrollMenuBuilder<Payee> pMenuBuilder,
                                final Security pSecurity) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        SecurityTypeClass myType = pSecurity.getSecurityTypeClass();
        Payee myCurr = pSecurity.getParent();
        JMenuItem myActive = null;

        /* Access Payees */
        PayeeList myPayees = findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentSecurity(myType);
            bIgnore |= myPayee.isClosed();
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
     * @param pSecurity the security to build for
     */
    public void buildCurrencyMenu(final JScrollMenuBuilder<AccountCurrency> pMenuBuilder,
                                  final Security pSecurity) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        AccountCurrency myCurr = pSecurity.getSecurityCurrency();
        JMenuItem myActive = null;

        /* Access Currencies */
        AccountCurrencyList myCurrencies = findDataList(MoneyWiseDataType.CURRENCY, AccountCurrencyList.class);

        /* Loop through the AccountCurrencies */
        Iterator<AccountCurrency> myIterator = myCurrencies.iterator();
        while (myIterator.hasNext()) {
            AccountCurrency myCurrency = myIterator.next();

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
     * Security Listener.
     */
    private final class SecurityListener
            implements ChangeListener {
        /**
         * The SecurityType Menu Builder.
         */
        private final JScrollMenuBuilder<SecurityType> theSecTypeMenuBuilder;

        /**
         * The Parent Menu Builder.
         */
        private final JScrollMenuBuilder<Payee> theParentMenuBuilder;

        /**
         * The Currency Menu Builder.
         */
        private final JScrollMenuBuilder<AccountCurrency> theCurrencyMenuBuilder;

        /**
         * Constructor.
         */
        private SecurityListener() {
            /* Access the MenuBuilders */
            theSecTypeMenuBuilder = theTypeButton.getMenuBuilder();
            theSecTypeMenuBuilder.addChangeListener(this);
            theParentMenuBuilder = theParentButton.getMenuBuilder();
            theParentMenuBuilder.addChangeListener(this);
            theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
            theCurrencyMenuBuilder.addChangeListener(this);
            thePrices.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theSecTypeMenuBuilder.equals(o)) {
                buildSecTypeMenu(theSecTypeMenuBuilder, getItem());
            } else if (theParentMenuBuilder.equals(o)) {
                buildParentMenu(theParentMenuBuilder, getItem());
            } else if (theCurrencyMenuBuilder.equals(o)) {
                buildCurrencyMenu(theCurrencyMenuBuilder, getItem());
            } else if (thePrices.equals(o)) {
                updateActions();
                fireStateChanged();
            }
        }
    }
}
