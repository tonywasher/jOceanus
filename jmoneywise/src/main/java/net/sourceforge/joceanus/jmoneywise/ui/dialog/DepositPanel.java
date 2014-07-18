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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Deposit.
 */
public class DepositPanel
        extends DataItemPanel<Deposit> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5458693709039462001L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Deposit> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * DepositCategory Button Field.
     */
    private final JScrollButton<DepositCategory> theCategoryButton;

    /**
     * Deposit Parent Button Field.
     */
    private final JScrollButton<Payee> theParentButton;

    /**
     * Currency Button Field.
     */
    private final JScrollButton<AccountCurrency> theCurrencyButton;

    /**
     * Closed Button Field.
     */
    // private final JButton theClosedButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public DepositPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theName = new JTextField(Deposit.NAMELEN);
        theDesc = new JTextField(Deposit.DESCLEN);

        /* Create the buttons */
        theCategoryButton = new JScrollButton<DepositCategory>();
        theParentButton = new JScrollButton<Payee>();
        theCurrencyButton = new JScrollButton<AccountCurrency>();
        // theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Deposit.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Deposit.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Deposit.FIELD_CATEGORY, DepositCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Deposit.FIELD_PARENT, Payee.class, theParentButton);
        theFieldSet.addFieldElement(Deposit.FIELD_CURRENCY, AccountCurrency.class, theCurrencyButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(Deposit.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(Deposit.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CATEGORY, this);
        theFieldSet.addFieldToPanel(Deposit.FIELD_PARENT, this);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CURRENCY, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Create the listener */
        new AccountListener();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Deposit.FIELD_CLOSED, false);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Deposit myDeposit = getItem();

        /* Process updates */
        if (myField.equals(Deposit.FIELD_NAME)) {
            /* Update the Name */
            myDeposit.setName(pUpdate.getValue(String.class));
        } else if (myField.equals(Deposit.FIELD_DESC)) {
            /* Update the Description */
            myDeposit.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(Deposit.FIELD_CATEGORY)) {
            /* Update the Category */
            myDeposit.setDepositCategory(pUpdate.getValue(DepositCategory.class));
        } else if (myField.equals(Deposit.FIELD_PARENT)) {
            /* Update the Parent */
            myDeposit.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Deposit.FIELD_CURRENCY)) {
            /* Update the Currency */
            myDeposit.setDepositCurrency(pUpdate.getValue(AccountCurrency.class));
        }
    }

    /**
     * Account Listener.
     */
    private final class AccountListener
            implements ChangeListener {
        /**
         * The Category Menu Builder.
         */
        private final JScrollMenuBuilder<DepositCategory> theCategoryMenuBuilder;

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
        private AccountListener() {
            /* Access the MenuBuilders */
            theCategoryMenuBuilder = theCategoryButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theParentMenuBuilder = theParentButton.getMenuBuilder();
            theParentMenuBuilder.addChangeListener(this);
            theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
            theCurrencyMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu();
            } else if (theParentMenuBuilder.equals(o)) {
                buildParentMenu();
            } else if (theCurrencyMenuBuilder.equals(o)) {
                buildCurrencyMenu();
            }
        }

        /**
         * Build the category type list for the item.
         */
        private void buildCategoryMenu() {
            /* Clear the menu */
            theCategoryMenuBuilder.clearMenu();

            /* Record active item */
            Deposit myDeposit = getItem();
            DepositCategory myCurr = myDeposit.getCategory();
            JMenuItem myActive = null;

            /* Access Deposit Categories */
            MoneyWiseData myData = myDeposit.getDataSet();
            DepositCategoryList myCategories = myData.getDepositCategories();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

            /* Loop through the available category values */
            Iterator<DepositCategory> myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                DepositCategory myCategory = myIterator.next();

                /* Only process parent items */
                if (!myCategory.isCategoryClass(DepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Create a new JMenu and add it to the popUp */
                String myName = myCategory.getName();
                JScrollMenu myMenu = theCategoryMenuBuilder.addSubMenu(myName);
                myMap.put(myName, myMenu);
            }

            /* Re-Loop through the available category values */
            myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                DepositCategory myCategory = myIterator.next();

                /* Only process low-level items */
                if (myCategory.isCategoryClass(DepositCategoryClass.PARENT)) {
                    continue;
                }

                /* Determine menu to add to */
                DepositCategory myParent = myCategory.getParentCategory();
                JScrollMenu myMenu = myMap.get(myParent.getName());

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theCategoryMenuBuilder.addItem(myMenu, myCategory);

                /* Note active category */
                if (myCategory.equals(myCurr)) {
                    myActive = myMenu;
                    myMenu.showItem(myItem);
                }
            }

            /* Ensure active item is visible */
            theCategoryMenuBuilder.showItem(myActive);
        }

        /**
         * Build the parent list for the item.
         */
        private void buildParentMenu() {
            /* Clear the menu */
            theParentMenuBuilder.clearMenu();

            /* Record active item */
            Deposit myDeposit = getItem();
            Payee myCurr = myDeposit.getParent();
            JMenuItem myActive = null;

            /* Access Payees */
            PayeeList myPayees = PayeeList.class.cast(findBaseList(Payee.class));

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
            Deposit myDeposit = getItem();
            AccountCurrency myCurr = myDeposit.getDepositCurrency();
            JMenuItem myActive = null;

            /* Access Currencies */
            MoneyWiseData myData = myDeposit.getDataSet();
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
