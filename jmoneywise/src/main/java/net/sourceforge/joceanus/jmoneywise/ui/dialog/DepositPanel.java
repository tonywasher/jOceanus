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
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.data.DepositCategory.DepositCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.DepositInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.DepositCategoryClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Deposit.
 */
public class DepositPanel
        extends MoneyWiseDataItemPanel<Deposit> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -5458693709039462001L;

    /**
     * Rates Tab Title.
     */
    private static final String TAB_RATES = MoneyWiseUIControlResource.DEPOSITPANEL_TAB_RATES.getValue();

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
    private final transient ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * TaxFree Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theTaxFreeState;

    /**
     * Gross Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theGrossState;

    /**
     * DepositRate Table.
     */
    private final DepositRateTable theRates;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public DepositPanel(final JFieldManager pFieldMgr,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet,
                        final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        theName = new JTextField();
        theDesc = new JTextField();

        /* Create the buttons */
        theCategoryButton = new JScrollButton<DepositCategory>();
        theParentButton = new JScrollButton<Payee>();
        theCurrencyButton = new JScrollButton<AccountCurrency>();

        /* Create icon button states */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);
        theTaxFreeState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);
        theGrossState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

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

        /* Create the DepositRates table */
        theRates = new DepositRateTable(pFieldMgr, getUpdateSet(), pError);
        myTabs.add(TAB_RATES, theRates.getPanel());

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new DepositListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Set states */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildOptionButton(theClosedState);
        JIconButton<Boolean> myTaxFreeButton = new JIconButton<Boolean>(theTaxFreeState);
        MoneyWiseIcons.buildOptionButton(theTaxFreeState);
        JIconButton<Boolean> myGrossButton = new JIconButton<Boolean>(theGrossState);
        MoneyWiseIcons.buildOptionButton(theGrossState);

        /* restrict the fields */
        restrictField(theName, Deposit.NAMELEN);
        restrictField(theDesc, Deposit.NAMELEN);
        restrictField(theCategoryButton, Deposit.NAMELEN);
        restrictField(theCurrencyButton, Deposit.NAMELEN);
        restrictField(theParentButton, Deposit.NAMELEN);
        restrictField(myClosedButton, Deposit.NAMELEN);
        restrictField(myTaxFreeButton, Deposit.NAMELEN);
        restrictField(myGrossButton, Deposit.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Deposit.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Deposit.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Deposit.FIELD_CATEGORY, DepositCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Deposit.FIELD_PARENT, Payee.class, theParentButton);
        theFieldSet.addFieldElement(Deposit.FIELD_CURRENCY, AccountCurrency.class, theCurrencyButton);
        theFieldSet.addFieldElement(Deposit.FIELD_CLOSED, Boolean.class, myClosedButton);
        theFieldSet.addFieldElement(Deposit.FIELD_TAXFREE, Boolean.class, myTaxFreeButton);
        theFieldSet.addFieldElement(Deposit.FIELD_GROSS, Boolean.class, myGrossButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Deposit.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_CLOSED, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_TAXFREE, myPanel);
        theFieldSet.addFieldToPanel(Deposit.FIELD_GROSS, myPanel);
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
        JDateDayButton myMaturity = new JDateDayButton();
        JTextField mySortCode = new JTextField();
        JTextField myAccount = new JTextField();
        JTextField myReference = new JTextField();
        JTextField myOpening = new JTextField();

        /* Restrict the fields */
        int myWidth = Deposit.NAMELEN >> 1;
        restrictField(myMaturity, myWidth);
        restrictField(mySortCode, myWidth);
        restrictField(myAccount, myWidth);
        restrictField(myReference, myWidth);
        restrictField(myOpening, myWidth);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY), DataType.DATEDAY, myMaturity);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), DataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), DataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), DataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), DataType.MONEY, myOpening);

        /* Create the extras panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), myPanel);
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
        theFieldSet.addFieldElement(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Deposit myItem = getItem();
        if (myItem != null) {
            DepositList myDeposits = findDataList(MoneyWiseDataType.DEPOSIT, DepositList.class);
            setItem(myDeposits.findItemById(myItem.getId()));
        }

        /* Refresh the rates */
        theRates.refreshData();

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Deposit myDeposit = getItem();
        DepositCategoryClass myClass = myDeposit.getCategoryClass();
        boolean bIsClosed = myDeposit.isClosed();
        boolean bIsActive = myDeposit.isActive();
        boolean bIsGross = myDeposit.isGross();
        boolean bIsTaxFree = myDeposit.isTaxFree();
        boolean bIsRelevant = myDeposit.isRelevant();
        boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Deposit.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = bIsClosed
                                       ? !myDeposit.getParent().isClosed()
                                       : !bIsRelevant;
        theFieldSet.setEditable(Deposit.FIELD_CLOSED, bIsChangeable);
        theClosedState.setState(bEditClosed);

        /* Determine whether the taxFree/Gross buttons should be visible */
        boolean bShowTaxFree = bIsTaxFree || (bIsChangeable && !bIsGross);
        theFieldSet.setVisibility(Deposit.FIELD_TAXFREE, bShowTaxFree);
        theTaxFreeState.setState(bEditClosed);
        boolean bShowGross = bIsGross || (bIsChangeable && !bIsTaxFree);
        theFieldSet.setVisibility(Deposit.FIELD_GROSS, bShowGross);
        theGrossState.setState(bEditClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myDeposit.getDesc() != null;
        theFieldSet.setVisibility(Deposit.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        boolean bShowSortCode = isEditable || myDeposit.getSortCode() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), bShowSortCode);
        boolean bShowAccount = isEditable || myDeposit.getAccount() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), bShowAccount);
        boolean bShowReference = isEditable || myDeposit.getReference() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), bShowReference);
        boolean bShowOpening = bIsChangeable || myDeposit.getOpeningBalance() != null;
        JDataField myOpeningField = DepositInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
        theFieldSet.setVisibility(myOpeningField, bShowOpening);
        boolean bShowNotes = isEditable || myDeposit.getNotes() != null;
        theFieldSet.setVisibility(DepositInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Maturity is only visible if the item is a bond */
        boolean bShowMaturity = DepositCategoryClass.BOND.equals(myDeposit.getCategoryClass());
        JDataField myMaturityField = DepositInfoSet.getFieldForClass(AccountInfoClass.MATURITY);
        theFieldSet.setVisibility(myMaturityField, bShowMaturity);
        theFieldSet.setEditable(myMaturityField, !bIsClosed);

        /* Category, Currency, Gross and TaxFree status cannot be changed if the item is active */
        boolean canTaxFree = myClass.canTaxFree();
        boolean isHolding = myDeposit.getTouchStatus().touchedBy(MoneyWiseDataType.PORTFOLIO);
        boolean canTaxChange = canTaxFree && !isHolding && bIsChangeable;
        theFieldSet.setEditable(Deposit.FIELD_CATEGORY, bIsChangeable);
        theFieldSet.setEditable(Deposit.FIELD_CURRENCY, bIsChangeable);
        theFieldSet.setEditable(Deposit.FIELD_GROSS, canTaxChange && !bIsTaxFree);
        theFieldSet.setEditable(Deposit.FIELD_TAXFREE, canTaxChange && !bIsGross);
        theFieldSet.setEditable(myOpeningField, bIsChangeable);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Deposit myDeposit = getItem();

        /* Process updates */
        if (myField.equals(Deposit.FIELD_NAME)) {
            /* Update the Name */
            myDeposit.setName(pUpdate.getString());
        } else if (myField.equals(Deposit.FIELD_DESC)) {
            /* Update the Description */
            myDeposit.setDescription(pUpdate.getString());
        } else if (myField.equals(Deposit.FIELD_CATEGORY)) {
            /* Update the Category */
            myDeposit.setDepositCategory(pUpdate.getValue(DepositCategory.class));
            myDeposit.adjustForCategory(getUpdateSet());
        } else if (myField.equals(Deposit.FIELD_PARENT)) {
            /* Update the Parent */
            myDeposit.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Deposit.FIELD_CURRENCY)) {
            /* Update the Currency */
            myDeposit.setDepositCurrency(pUpdate.getValue(AccountCurrency.class));
        } else if (myField.equals(Deposit.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myDeposit.setClosed(pUpdate.getBoolean());
        } else if (myField.equals(Deposit.FIELD_TAXFREE)) {
            /* Update the taxFree indication */
            myDeposit.setTaxFree(pUpdate.getBoolean());
        } else if (myField.equals(Deposit.FIELD_GROSS)) {
            /* Update the Gross indication */
            myDeposit.setGross(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (DepositInfoSet.getClassForField(myField)) {
                case MATURITY:
                    myDeposit.setMaturity(pUpdate.getDateDay());
                    break;
                case OPENINGBALANCE:
                    myDeposit.setOpeningBalance(pUpdate.getMoney());
                    break;
                case SORTCODE:
                    myDeposit.setSortCode(pUpdate.getCharArray());
                    break;
                case ACCOUNT:
                    myDeposit.setAccount(pUpdate.getCharArray());
                    break;
                case REFERENCE:
                    myDeposit.setReference(pUpdate.getCharArray());
                    break;
                case NOTES:
                    myDeposit.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void buildGoToMenu() {
        Deposit myItem = getItem();
        DepositCategory myCategory = myItem.getCategory();
        Payee myParent = myItem.getParent();
        AccountCurrency myCurrency = myItem.getDepositCurrency();
        buildGoToEvent(myCategory);
        buildGoToEvent(myParent);
        buildGoToEvent(myCurrency);
    }

    @Override
    public void setItem(final Deposit pItem) {
        /* Update the rates */
        theRates.setDeposit(pItem);

        /* Pass call onwards */
        super.setItem(pItem);
    }

    @Override
    public void setNewItem(final Deposit pItem) {
        /* Update the rates */
        theRates.setDeposit(pItem);

        /* Pass call onwards */
        super.setNewItem(pItem);
    }

    @Override
    public void setEditable(final boolean isEditable) {
        /* Update the rates */
        theRates.setEditable(isEditable);

        /* Pass call onwards */
        super.setEditable(isEditable);
    }

    @Override
    protected void refreshAfterUpdate() {
        /* Pass call onwards */
        super.refreshAfterUpdate();

        /* Refresh the rates */
        theRates.refreshAfterUpdate();
    }

    /**
     * Deposit Listener.
     */
    private final class DepositListener
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
        private DepositListener() {
            /* Access the MenuBuilders */
            theCategoryMenuBuilder = theCategoryButton.getMenuBuilder();
            theCategoryMenuBuilder.addChangeListener(this);
            theParentMenuBuilder = theParentButton.getMenuBuilder();
            theParentMenuBuilder.addChangeListener(this);
            theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
            theCurrencyMenuBuilder.addChangeListener(this);
            theRates.addChangeListener(this);
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
            } else if (theRates.equals(o)) {
                updateActions();
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

                /* Ignore deleted or non-parent */
                boolean bIgnore = myCategory.isDeleted() || !myCategory.isCategoryClass(DepositCategoryClass.PARENT);
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
                DepositCategory myCategory = myIterator.next();

                /* Ignore deleted or parent */
                boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(DepositCategoryClass.PARENT);
                if (bIgnore) {
                    continue;
                }

                /* Determine menu to add to */
                DepositCategory myParent = myCategory.getParentCategory();
                JScrollMenu myMenu = myMap.get(myParent.getName());

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theCategoryMenuBuilder.addItem(myMenu, myCategory, myCategory.getSubCategory());

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
            DepositCategoryClass myType = myDeposit.getCategoryClass();
            Payee myCurr = myDeposit.getParent();
            JMenuItem myActive = null;

            /* Access Payees */
            PayeeList myPayees = findDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

            /* Loop through the Payees */
            Iterator<Payee> myIterator = myPayees.iterator();
            while (myIterator.hasNext()) {
                Payee myPayee = myIterator.next();

                /* Ignore deleted or non-owner */
                boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentDeposit(myType);
                bIgnore |= myPayee.isClosed();
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
