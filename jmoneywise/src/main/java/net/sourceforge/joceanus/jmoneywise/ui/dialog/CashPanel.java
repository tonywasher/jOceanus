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
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.data.CashCategory.CashCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.CashInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.CashCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
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
     * AutoExpense Button Field.
     */
    private final JScrollButton<TransactionCategory> theAutoExpenseButton;

    /**
     * AutoPayee Button Field.
     */
    private final JScrollButton<Payee> theAutoPayeeButton;

    /**
     * Closed Button Field.
     */
    private final ComplexIconButtonState<Boolean, Boolean> theClosedState;

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
        theAutoExpenseButton = new JScrollButton<TransactionCategory>();
        theAutoPayeeButton = new JScrollButton<Payee>();

        /* Set closed button */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();
        add(myTabs);

        /* Build the main panel */
        JPanel myPanel = buildMainPanel();
        myTabs.add("Main", myPanel);

        /* Build the detail panel */
        myPanel = buildXtrasPanel();
        myTabs.add("Details", myPanel);

        /* Build the notes panel */
        myPanel = buildNotesPanel();
        myTabs.add("Notes", myPanel);

        /* Create the listener */
        new AccountListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Set states */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildOptionButton(theClosedState);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Cash.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Cash.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Cash.FIELD_CATEGORY, CashCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Cash.FIELD_CURRENCY, AccountCurrency.class, theCurrencyButton);
        theFieldSet.addFieldElement(Cash.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(Cash.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(Cash.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(Cash.FIELD_CATEGORY, this);
        theFieldSet.addFieldToPanel(Cash.FIELD_CURRENCY, this);
        theFieldSet.addFieldToPanel(Cash.FIELD_CLOSED, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extras subPanel.
     * @return the panel
     */
    private JPanel buildXtrasPanel() {
        /* Adjust FieldSet */
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), TransactionCategory.class, theAutoExpenseButton);
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), Payee.class, theAutoPayeeButton);

        /* Create the extras panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE), myPanel);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.AUTOPAYEE), myPanel);
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
        theFieldSet.addFieldElement(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(CashInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
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
        } else if (myField.equals(Cash.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myCash.setClosed(pUpdate.getValue(Boolean.class));
        } else {
            /* Switch on the field */
            switch (CashInfoSet.getClassForField(myField)) {
                case AUTOEXPENSE:
                    myCash.setAutoExpense(pUpdate.getValue(TransactionCategory.class));
                    break;
                case AUTOPAYEE:
                    myCash.setAutoPayee(pUpdate.getValue(Payee.class));
                    break;
                case NOTES:
                    myCash.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
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
         * The AutoExpense Menu Builder.
         */
        private final JScrollMenuBuilder<TransactionCategory> theAutoExpenseMenuBuilder;

        /**
         * The AutoPayee Menu Builder.
         */
        private final JScrollMenuBuilder<Payee> theAutoPayeeMenuBuilder;

        /**
         * Constructor.
         */
        private AccountListener() {
            /* Access the MenuBuilders */
            theCategoryMenuBuilder = theCategoryButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
            theCurrencyMenuBuilder.addChangeListener(this);
            theAutoExpenseMenuBuilder = theAutoExpenseButton.getMenuBuilder();
            theAutoExpenseMenuBuilder.addChangeListener(this);
            theAutoPayeeMenuBuilder = theAutoPayeeButton.getMenuBuilder();
            theAutoPayeeMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theCategoryMenuBuilder.equals(o)) {
                buildCategoryMenu();
            } else if (theCurrencyMenuBuilder.equals(o)) {
                buildCurrencyMenu();
            } else if (theAutoExpenseMenuBuilder.equals(o)) {
                buildAutoExpenseMenu();
            } else if (theAutoPayeeMenuBuilder.equals(o)) {
                buildAutoPayeeMenu();
            }
        }

        /**
         * Build the category list for the item.
         */
        private void buildCategoryMenu() {
            /* Clear the menu */
            theCategoryMenuBuilder.clearMenu();

            /* Record active item */
            Cash myCash = getItem();
            CashCategory myCurr = myCash.getCategory();
            JMenuItem myActive = null;

            /* Access Cash Categories */
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
         * Build the autoExpense list for the item.
         */
        private void buildAutoExpenseMenu() {
            /* Clear the menu */
            theAutoExpenseMenuBuilder.clearMenu();

            /* Record active item */
            Cash myCash = getItem();
            TransactionCategory myCurr = myCash.getAutoExpense();
            JMenuItem myActive = null;

            /* Access Transaction Categories */
            MoneyWiseData myData = myCash.getDataSet();
            TransactionCategoryList myCategories = myData.getTransCategories();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

            /* Loop through the available category values */
            Iterator<TransactionCategory> myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                TransactionCategory myCategory = myIterator.next();

                /* Ignore deleted or non-expense-subTotal items */
                TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
                boolean bIgnore = myCategory.isDeleted() || !myClass.isSubTotal();
                bIgnore |= !myClass.isExpense();
                if (bIgnore) {
                    continue;
                }

                /* Create a new JMenu and add it to the popUp */
                String myName = myCategory.getName();
                JScrollMenu myMenu = theAutoExpenseMenuBuilder.addSubMenu(myName);
                myMap.put(myName, myMenu);
            }

            /* Re-Loop through the available category values */
            myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                TransactionCategory myCategory = myIterator.next();

                /* Ignore deleted or non-expense-subTotal items */
                TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();
                boolean bIgnore = myCategory.isDeleted() || myClass.isSubTotal();
                bIgnore |= !myClass.isExpense();
                if (bIgnore) {
                    continue;
                }

                /* Determine menu to add to */
                TransactionCategory myParent = myCategory.getParentCategory();
                JScrollMenu myMenu = myMap.get(myParent.getName());

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theAutoExpenseMenuBuilder.addItem(myMenu, myCategory);

                /* Note active category */
                if (myCategory.equals(myCurr)) {
                    myActive = myMenu;
                    myMenu.showItem(myItem);
                }
            }

            /* Ensure active item is visible */
            theAutoExpenseMenuBuilder.showItem(myActive);
        }

        /**
         * Build the parent list for the item.
         */
        private void buildAutoPayeeMenu() {
            /* Clear the menu */
            theAutoPayeeMenuBuilder.clearMenu();

            /* Record active item */
            Cash myCash = getItem();
            Payee myCurr = myCash.getAutoPayee();
            JMenuItem myActive = null;

            /* Access Payees */
            PayeeList myPayees = PayeeList.class.cast(findBaseList(Payee.class));

            /* Loop through the Payees */
            Iterator<Payee> myIterator = myPayees.iterator();
            while (myIterator.hasNext()) {
                Payee myPayee = myIterator.next();

                /* Ignore deleted */
                if (myPayee.isDeleted()) {
                    continue;
                }

                /* Create a new action for the payee */
                JMenuItem myItem = theAutoPayeeMenuBuilder.addItem(myPayee);

                /* If this is the active parent */
                if (myPayee.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theAutoPayeeMenuBuilder.showItem(myActive);
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
