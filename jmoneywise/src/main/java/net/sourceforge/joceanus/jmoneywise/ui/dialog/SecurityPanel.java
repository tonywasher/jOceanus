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

import java.util.Iterator;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import net.sourceforge.joceanus.jmetis.field.JFieldComponent.JFieldButtonAction;
import net.sourceforge.joceanus.jmetis.field.JFieldComponent.JFieldButtonPopUp;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityType.SecurityTypeList;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Security.
 */
public class SecurityPanel
        extends DataItemPanel<Security>
        implements JFieldButtonPopUp {
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
    private final JButton theTypeButton;

    /**
     * Security Parent Button Field.
     */
    private final JButton theParentButton;

    /**
     * Currency Button Field.
     */
    private final JButton theCurrencyButton;

    /**
     * Closed Button Field.
     */
    private final JButton theClosedButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public SecurityPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the labels */
        JLabel myNameLabel = new JLabel(Security.FIELD_NAME.getName() + ":", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel(Security.FIELD_DESC.getName() + ":", SwingConstants.TRAILING);
        JLabel mySymLabel = new JLabel(Security.FIELD_SYMBOL.getName() + ":", SwingConstants.TRAILING);
        JLabel myTypeLabel = new JLabel(Security.FIELD_SECTYPE.getName() + ":", SwingConstants.TRAILING);
        JLabel myParLabel = new JLabel(Security.FIELD_PARENT.getName() + ":", SwingConstants.TRAILING);
        JLabel myCurrLabel = new JLabel(Security.FIELD_CURRENCY.getName() + ":", SwingConstants.TRAILING);
        JLabel myClosedLabel = new JLabel(Security.FIELD_CLOSED.getName() + ":", SwingConstants.TRAILING);

        /* Create the text fields */
        theName = new JTextField(Security.NAMELEN);
        theDesc = new JTextField(Security.DESCLEN);
        theSymbol = new JTextField(Security.SYMBOLLEN);

        /* Create the buttons */
        theTypeButton = new JButton();
        theParentButton = new JButton();
        theCurrencyButton = new JButton();
        theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Security.FIELD_NAME, DataType.STRING, myNameLabel, theName);
        theFieldSet.addFieldElement(Security.FIELD_DESC, DataType.STRING, myDescLabel, theDesc);
        theFieldSet.addFieldElement(Security.FIELD_SYMBOL, DataType.STRING, mySymLabel, theSymbol);
        theFieldSet.addFieldElement(Security.FIELD_SECTYPE, this, SecurityType.class, myTypeLabel, theTypeButton);
        theFieldSet.addFieldElement(Security.FIELD_PARENT, this, Payee.class, myParLabel, theParentButton);
        theFieldSet.addFieldElement(Security.FIELD_CURRENCY, this, AccountCurrency.class, myCurrLabel, theCurrencyButton);
        theFieldSet.addFieldElement(Security.FIELD_CLOSED, this, Boolean.class, myClosedLabel, theClosedButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        add(myNameLabel);
        add(theName);
        add(myDescLabel);
        add(theDesc);
        add(myTypeLabel);
        add(theTypeButton);
        add(myParLabel);
        add(theParentButton);
        add(mySymLabel);
        add(theSymbol);
        add(myCurrLabel);
        add(theCurrencyButton);
        add(myClosedLabel);
        add(theClosedButton);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Security.FIELD_CLOSED, false);
    }

    @Override
    public JPopupMenu getPopUpMenu(final JFieldButtonAction pActionSrc,
                                   final JDataField pField) {
        /* Switch on field */
        if (pField.equals(Security.FIELD_SECTYPE)) {
            /* Build the category type menu */
            return getSecTypePopUpMenu(pActionSrc);
        } else if (pField.equals(Security.FIELD_CURRENCY)) {
            /* Build the currency menu */
            return getCurrencyPopUpMenu(pActionSrc);
        } else if (pField.equals(Security.FIELD_PARENT)) {
            /* Build the parent menu */
            return getParentPopUpMenu(pActionSrc);
        }

        /* return no menu */
        return null;
    }

    /**
     * Build the security type menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getSecTypePopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Determine the type of the security */
        Security mySecurity = getItem();
        SecurityType myCurr = mySecurity.getSecurityType();
        JMenuItem myActive = null;

        /* Access Security types */
        MoneyWiseData myData = mySecurity.getDataSet();
        SecurityTypeList mySecTypes = myData.getSecurityTypes();

        /* Loop through the SecurityTypes */
        Iterator<SecurityType> myIterator = mySecTypes.iterator();
        while (myIterator.hasNext()) {
            SecurityType myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            Action myAction = pActionSrc.getNewAction(myType);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);

            /* If this is the active type */
            if (myType.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        myMenu.showItem(myActive);

        /* Return the menu */
        return myMenu;
    }

    /**
     * Build the currency menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getCurrencyPopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Determine the currency of the security */
        Security mySecurity = getItem();
        AccountCurrency myCurr = mySecurity.getSecurityCurrency();
        JMenuItem myActive = null;

        /* Access AccountCurrencies */
        MoneyWiseData myData = mySecurity.getDataSet();
        AccountCurrencyList myCurrencies = myData.getAccountCurrencies();

        /* Loop through the Currencies */
        Iterator<AccountCurrency> myIterator = myCurrencies.iterator();
        while (myIterator.hasNext()) {
            AccountCurrency myType = myIterator.next();

            /* Ignore deleted or disabled */
            boolean bIgnore = myType.isDeleted() || !myType.getEnabled();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the currency */
            Action myAction = pActionSrc.getNewAction(myType);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);

            /* If this is the active currency */
            if (myType.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        myMenu.showItem(myActive);

        /* Return the menu */
        return myMenu;
    }

    /**
     * Build the parent menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getParentPopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Determine the parent of the security */
        Security mySecurity = getItem();
        Payee myCurr = mySecurity.getParent();
        JMenuItem myActive = null;

        /* Access Payees */
        MoneyWiseData myData = mySecurity.getDataSet();
        PayeeList myPayees = myData.getPayees();

        /* Loop through the Payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentAccount();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the type */
            Action myAction = pActionSrc.getNewAction(myPayee);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);

            /* If this is the active payee */
            if (myPayee.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        myMenu.showItem(myActive);

        /* Return the menu */
        return myMenu;
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
        }
    }
}
