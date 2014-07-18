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
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Cash.
 */
public class CashPanel
        extends DataItemPanel<Cash> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1242762723020329985L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Cash> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * CashCategory Button Field.
     */
    private final JScrollButton<CashCategory> theCategoryButton;

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
    public CashPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theName = new JTextField(Cash.NAMELEN);
        theDesc = new JTextField(Cash.DESCLEN);

        /* Create the buttons */
        theCategoryButton = new JScrollButton<CashCategory>();
        theCurrencyButton = new JScrollButton<AccountCurrency>();
        // theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Cash.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Cash.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Cash.FIELD_CATEGORY, CashCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Cash.FIELD_CURRENCY, AccountCurrency.class, theCurrencyButton);
        // theFieldSet.addFieldElement(Cash.FIELD_CLOSED, this, Boolean.class, myClosedLabel, theClosedButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(Cash.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(Cash.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(Cash.FIELD_CATEGORY, this);
        theFieldSet.addFieldToPanel(Cash.FIELD_CURRENCY, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Create the listener */
        new AccountListener();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Cash.FIELD_CLOSED, false);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Cash myCash = getItem();

        /* Process updates */
        if (myField.equals(Cash.FIELD_NAME)) {
            /* Update the Name */
            myCash.setName(pUpdate.getValue(String.class));
        } else if (myField.equals(Cash.FIELD_DESC)) {
            /* Update the Description */
            myCash.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(Cash.FIELD_CATEGORY)) {
            /* Update the Category */
            myCash.setCashCategory(pUpdate.getValue(CashCategory.class));
        } else if (myField.equals(Cash.FIELD_CURRENCY)) {
            /* Update the Currency */
            myCash.setCashCurrency(pUpdate.getValue(AccountCurrency.class));
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
        private final JScrollMenuBuilder<CashCategory> theCategoryMenuBuilder;

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
            theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
            theCurrencyMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu();
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
            Cash myCash = getItem();
            CashCategory myCurr = myCash.getCategory();
            JMenuItem myActive = null;

            /* Access Deposit Categories */
            MoneyWiseData myData = myCash.getDataSet();
            CashCategoryList myCategories = myData.getCashCategories();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

            /* Loop through the available category values */
            Iterator<CashCategory> myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                CashCategory myCategory = myIterator.next();

                /* Only process parent items */
                if (!myCategory.isCategoryClass(CashCategoryClass.PARENT)) {
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
                CashCategory myCategory = myIterator.next();

                /* Only process low-level items */
                if (myCategory.isCategoryClass(CashCategoryClass.PARENT)) {
                    continue;
                }

                /* Determine menu to add to */
                CashCategory myParent = myCategory.getParentCategory();
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
         * Build the currency list for the item.
         */
        private void buildCurrencyMenu() {
            /* Clear the menu */
            theCurrencyMenuBuilder.clearMenu();

            /* Record active item */
            Cash myCash = getItem();
            AccountCurrency myCurr = myCash.getCashCurrency();
            JMenuItem myActive = null;

            /* Access Currencies */
            MoneyWiseData myData = myCash.getDataSet();
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
