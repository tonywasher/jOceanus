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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Loan.
 */
public class LoanPanel
        extends DataItemPanel<Loan> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 3298368283270989964L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Loan> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * LoanCategory Button Field.
     */
    private final JScrollButton<LoanCategory> theCategoryButton;

    /**
     * Loan Parent Button Field.
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
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public LoanPanel(final JFieldManager pFieldMgr,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        theName = new JTextField();
        theDesc = new JTextField();

        /* Create the buttons */
        theCategoryButton = new JScrollButton<LoanCategory>();
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

        /* Build the detail panel */
        JPanel myPanel = buildXtrasPanel();
        myTabs.add("Details", myPanel);

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
        new LoanListener();
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
        restrictField(theName, Loan.NAMELEN);
        restrictField(theDesc, Loan.NAMELEN);
        restrictField(theCategoryButton, Loan.NAMELEN);
        restrictField(theCurrencyButton, Loan.NAMELEN);
        restrictField(theParentButton, Loan.NAMELEN);
        restrictField(myClosedButton, Loan.NAMELEN);

        theFieldSet.addFieldElement(Loan.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Loan.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Loan.FIELD_CATEGORY, LoanCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Loan.FIELD_PARENT, Payee.class, theParentButton);
        theFieldSet.addFieldElement(Loan.FIELD_CURRENCY, AccountCurrency.class, theCurrencyButton);
        theFieldSet.addFieldElement(Loan.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Loan.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_CLOSED, myPanel);
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

        /* Restrict the fields */
        int myWidth = Loan.NAMELEN >> 1;
        restrictField(mySortCode, myWidth);
        restrictField(myAccount, myWidth);
        restrictField(myReference, myWidth);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), DataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), DataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), DataType.CHARARRAY, myReference);

        /* Create the extras panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), myPanel);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), myPanel);
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
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Loan myItem = getItem();
        if (myItem != null) {
            LoanList myLoans = findDataList(MoneyWiseDataType.LOAN, LoanList.class);
            setItem(myLoans.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Loan myLoan = getItem();

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = myLoan.isClosed() || !myLoan.isRelevant();
        theFieldSet.setVisibility(Loan.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = myLoan.isClosed()
                                               ? !myLoan.getParent().isClosed()
                                               : !myLoan.isRelevant();
        theClosedState.setState(bEditClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myLoan.getDesc() != null;
        theFieldSet.setVisibility(Loan.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        boolean bShowSortCode = isEditable || myLoan.getSortCode() != null;
        theFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        boolean bShowAccount = isEditable || myLoan.getAccount() != null;
        theFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        boolean bShowReference = isEditable || myLoan.getReference() != null;
        theFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        boolean bShowNotes = isEditable || myLoan.getNotes() != null;
        theFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Currency cannot be changed if the item is active */
        boolean bIsActive = myLoan.isActive();
        theFieldSet.setEditable(Loan.FIELD_CURRENCY, isEditable && !bIsActive);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Loan myLoan = getItem();

        /* Process updates */
        if (myField.equals(Loan.FIELD_NAME)) {
            /* Update the Name */
            myLoan.setName(pUpdate.getString());
        } else if (myField.equals(Loan.FIELD_DESC)) {
            /* Update the Description */
            myLoan.setDescription(pUpdate.getString());
        } else if (myField.equals(Loan.FIELD_CATEGORY)) {
            /* Update the Category */
            myLoan.setLoanCategory(pUpdate.getValue(LoanCategory.class));
        } else if (myField.equals(Loan.FIELD_PARENT)) {
            /* Update the Parent */
            myLoan.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Loan.FIELD_CURRENCY)) {
            /* Update the Currency */
            myLoan.setLoanCurrency(pUpdate.getValue(AccountCurrency.class));
        } else if (myField.equals(Loan.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myLoan.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (LoanInfoSet.getClassForField(myField)) {
                case SORTCODE:
                    myLoan.setSortCode(pUpdate.getCharArray());
                    break;
                case ACCOUNT:
                    myLoan.setAccount(pUpdate.getCharArray());
                    break;
                case REFERENCE:
                    myLoan.setReference(pUpdate.getCharArray());
                    break;
                case NOTES:
                    myLoan.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void buildGoToMenu() {
        Loan myItem = getItem();
        LoanCategory myCategory = myItem.getCategory();
        Payee myParent = myItem.getParent();
        AccountCurrency myCurrency = myItem.getLoanCurrency();
        buildGoToEvent(myCategory);
        buildGoToEvent(myParent);
        buildGoToEvent(myCurrency);
    }

    /**
     * Loan Listener.
     */
    private final class LoanListener
            implements ChangeListener {
        /**
         * The Category Menu Builder.
         */
        private final JScrollMenuBuilder<LoanCategory> theCategoryMenuBuilder;

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
        private LoanListener() {
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
            Loan myLoan = getItem();
            LoanCategory myCurr = myLoan.getCategory();
            JMenuItem myActive = null;

            /* Access Loan Categories */
            MoneyWiseData myData = myLoan.getDataSet();
            LoanCategoryList myCategories = myData.getLoanCategories();

            /* Create a simple map for top-level categories */
            Map<String, JScrollMenu> myMap = new HashMap<String, JScrollMenu>();

            /* Loop through the available category values */
            Iterator<LoanCategory> myIterator = myCategories.iterator();
            while (myIterator.hasNext()) {
                LoanCategory myCategory = myIterator.next();

                /* Ignore deleted or non-parent */
                boolean bIgnore = myCategory.isDeleted() || !myCategory.isCategoryClass(LoanCategoryClass.PARENT);
                if (bIgnore) {
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
                LoanCategory myCategory = myIterator.next();

                /* Ignore deleted or parent */
                boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(LoanCategoryClass.PARENT);
                if (bIgnore) {
                    continue;
                }

                /* Determine menu to add to */
                LoanCategory myParent = myCategory.getParentCategory();
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
            Loan myLoan = getItem();
            Payee myCurr = myLoan.getParent();
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
            Loan myLoan = getItem();
            AccountCurrency myCurr = myLoan.getLoanCurrency();
            JMenuItem myActive = null;

            /* Access Currencies */
            MoneyWiseData myData = myLoan.getDataSet();
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
