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
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
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
 * Panel to display/edit/create a Security.
 */
public class SecurityPanel
        extends DataItemPanel<Security> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7711868258621672746L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Security> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * Symbol Text Field.
     */
    private final JTextField theSymbol;

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
    private final ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * SecurityPrice Table.
     */
    private final SecurityPriceTable thePrices;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public SecurityPanel(final JFieldManager pFieldMgr,
                         final UpdateSet<MoneyWiseDataType> pUpdateSet,
                         final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        theName = new JTextField(Security.NAMELEN);
        theDesc = new JTextField(Security.DESCLEN);
        theSymbol = new JTextField(Security.SYMBOLLEN);

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
        thePrices = new SecurityPriceTable(pFieldMgr, getUpdateSet(), pError);
        myTabs.add("Prices", thePrices.getPanel());

        /* Create the layout */
        setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        add(myMainPanel);
        add(myTabs);

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
        MoneyWiseIcons.buildOptionButton(theClosedState);

        /* restrict the fields */
        restrictField(theName, Security.NAMELEN);
        restrictField(theDesc, Security.NAMELEN);
        restrictField(theSymbol, Security.NAMELEN);
        restrictField(theTypeButton, Security.NAMELEN);
        restrictField(theCurrencyButton, Security.NAMELEN);
        restrictField(theParentButton, Security.NAMELEN);
        restrictField(myClosedButton, Security.NAMELEN);

        theFieldSet.addFieldElement(Security.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Security.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Security.FIELD_SYMBOL, DataType.STRING, theSymbol);
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

        /* Adjust FieldSet */
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
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Security mySecurity = getItem();

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = mySecurity.isClosed() || !mySecurity.isRelevant();
        theFieldSet.setVisibility(Security.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = mySecurity.isClosed()
                                                   ? !mySecurity.getParent().isClosed()
                                                   : !mySecurity.isRelevant();
        theClosedState.setState(bEditClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || mySecurity.getDesc() != null;
        theFieldSet.setVisibility(Security.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        boolean bShowNotes = isEditable || mySecurity.getNotes() != null;
        theFieldSet.setVisibility(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Security type and currency cannot be changed if the item is active */
        boolean bIsActive = mySecurity.isActive();
        theFieldSet.setEditable(Security.FIELD_SECTYPE, !bIsActive);
        theFieldSet.setEditable(Security.FIELD_CURRENCY, !bIsActive);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Security mySecurity = getItem();

        /* Process updates */
        if (myField.equals(Security.FIELD_NAME)) {
            /* Update the Name */
            mySecurity.setName(pUpdate.getValue(String.class));
        } else if (myField.equals(Security.FIELD_DESC)) {
            /* Update the Description */
            mySecurity.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(Security.FIELD_SYMBOL)) {
            /* Update the Symbol */
            mySecurity.setSymbol(pUpdate.getValue(String.class));
        } else if (myField.equals(Security.FIELD_SECTYPE)) {
            /* Update the Security Type */
            mySecurity.setSecurityType(pUpdate.getValue(SecurityType.class));
        } else if (myField.equals(Security.FIELD_PARENT)) {
            /* Update the Parent */
            mySecurity.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Security.FIELD_CURRENCY)) {
            /* Update the Currency */
            mySecurity.setSecurityCurrency(pUpdate.getValue(AccountCurrency.class));
        } else if (myField.equals(Security.FIELD_CLOSED)) {
            /* Update the Closed indication */
            mySecurity.setClosed(pUpdate.getValue(Boolean.class));
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
    public void setItem(final Security pItem) {
        /* Update the prices */
        thePrices.setSecurity(pItem);

        /* Pass call onwards */
        super.setItem(pItem);
    }

    @Override
    public void setEditable(final boolean isEditable) {
        /* Update the prices=-0o */
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
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theSecTypeMenuBuilder.equals(o)) {
                buildSecTypeMenu();
            } else if (theParentMenuBuilder.equals(o)) {
                buildParentMenu();
            } else if (theCurrencyMenuBuilder.equals(o)) {
                buildCurrencyMenu();
            }
        }

        /**
         * Build the securityType list for the item.
         */
        private void buildSecTypeMenu() {
            /* Clear the menu */
            theSecTypeMenuBuilder.clearMenu();

            /* Record active item */
            Security mySecurity = getItem();
            SecurityType myCurr = mySecurity.getSecurityType();
            JMenuItem myActive = null;

            /* Access SecurityTypes */
            MoneyWiseData myData = mySecurity.getDataSet();
            SecurityTypeList myTypes = myData.getSecurityTypes();

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
                JMenuItem myItem = theSecTypeMenuBuilder.addItem(myType);

                /* If this is the active secType */
                if (myType.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theSecTypeMenuBuilder.showItem(myActive);
        }

        /**
         * Build the parent list for the item.
         */
        private void buildParentMenu() {
            /* Clear the menu */
            theParentMenuBuilder.clearMenu();

            /* Record active item */
            Security mySecurity = getItem();
            Payee myCurr = mySecurity.getParent();
            JMenuItem myActive = null;

            /* Access Payees */
            PayeeList myPayees = findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

            /* Loop through the Payees */
            Iterator<Payee> myIterator = myPayees.iterator();
            while (myIterator.hasNext()) {
                Payee myPayee = myIterator.next();

                /* Ignore deleted or non-owner */
                boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentAccount();
                if (bIgnore) {
                    continue;
                }

                /* Create a new action for the payee */
                JMenuItem myItem = theParentMenuBuilder.addItem(myPayee);

                /* If this is the active parent */
                if (myPayee.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theParentMenuBuilder.showItem(myActive);
        }

        /**
         * Build the currency list for the item.
         */
        private void buildCurrencyMenu() {
            /* Clear the menu */
            theCurrencyMenuBuilder.clearMenu();

            /* Record active item */
            Security mySecurity = getItem();
            AccountCurrency myCurr = mySecurity.getSecurityCurrency();
            JMenuItem myActive = null;

            /* Access Currencies */
            MoneyWiseData myData = mySecurity.getDataSet();
            AccountCurrencyList myCurrencies = myData.getAccountCurrencies();

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
                JMenuItem myItem = theCurrencyMenuBuilder.addItem(myCurrency);

                /* If this is the active currency */
                if (myCurrency.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theCurrencyMenuBuilder.showItem(myActive);
        }
    }
}
