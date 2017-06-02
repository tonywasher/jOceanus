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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfoSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency.AssetCurrencyList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.LoanCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.lethe.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingEnableWrapper.TethysSwingEnablePanel;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSpringUtilities;

/**
 * Panel to display/edit/create a Loan.
 */
public class LoanPanel
        extends MoneyWiseDataItemPanel<Loan> {
    /**
     * The Field Set.
     */
    private final MetisFieldSet<Loan> theFieldSet;

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
    private final JScrollButton<AssetCurrency> theCurrencyButton;

    /**
     * Closed Button Field.
     */
    private final ComplexIconButtonState<Boolean, Boolean> theClosedState;

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
    private final JScrollMenuBuilder<AssetCurrency> theCurrencyMenuBuilder;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public LoanPanel(final TethysSwingGuiFactory pFactory,
                     final MetisFieldManager pFieldMgr,
                     final UpdateSet<MoneyWiseDataType> pUpdateSet,
                     final MetisErrorPanel<JComponent, Icon> pError) {
        /* Initialise the panel */
        super(pFactory, pFieldMgr, pUpdateSet, pError);

        /* Create the buttons */
        theCategoryButton = new JScrollButton<>();
        theParentButton = new JScrollButton<>();
        theCurrencyButton = new JScrollButton<>();

        /* Set closed button */
        theClosedState = new ComplexIconButtonState<>(Boolean.FALSE);

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

        /* Create the listeners */
        theCategoryMenuBuilder = theCategoryButton.getMenuBuilder();
        theCategoryMenuBuilder.getEventRegistrar().addEventListener(e -> buildCategoryMenu(theCategoryMenuBuilder, getItem()));
        theParentMenuBuilder = theParentButton.getMenuBuilder();
        theParentMenuBuilder.getEventRegistrar().addEventListener(e -> buildParentMenu(theParentMenuBuilder, getItem()));
        theCurrencyMenuBuilder = theCurrencyButton.getMenuBuilder();
        theCurrencyMenuBuilder.getEventRegistrar().addEventListener(e -> buildCurrencyMenu(theCurrencyMenuBuilder, getItem()));
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Build the closed button state */
        JIconButton<Boolean> myClosedButton = new JIconButton<>(theClosedState);
        MoneyWiseIcons.buildLockedButton(theClosedState);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();

        /* restrict the fields */
        restrictField(myName, Loan.NAMELEN);
        restrictField(myDesc, Loan.NAMELEN);
        restrictField(theCategoryButton, Loan.NAMELEN);
        restrictField(theCurrencyButton, Loan.NAMELEN);
        restrictField(theParentButton, Loan.NAMELEN);
        restrictField(myClosedButton, Loan.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(Loan.FIELD_NAME, MetisDataType.STRING, myName);
        theFieldSet.addFieldElement(Loan.FIELD_DESC, MetisDataType.STRING, myDesc);
        theFieldSet.addFieldElement(Loan.FIELD_CATEGORY, LoanCategory.class, theCategoryButton);
        theFieldSet.addFieldElement(Loan.FIELD_PARENT, Payee.class, theParentButton);
        theFieldSet.addFieldElement(Loan.FIELD_CURRENCY, AssetCurrency.class, theCurrencyButton);
        theFieldSet.addFieldElement(Loan.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(Loan.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_CATEGORY, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_PARENT, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_CURRENCY, myPanel);
        theFieldSet.addFieldToPanel(Loan.FIELD_CLOSED, myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
        JTextField myOpening = new JTextField();

        /* Restrict the fields */
        int myWidth = Loan.NAMELEN >> 1;
        restrictField(mySortCode, myWidth);
        restrictField(myAccount, myWidth);
        restrictField(myReference, myWidth);
        restrictField(myOpening, myWidth);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), MetisDataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), MetisDataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), MetisDataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), MetisDataType.MONEY, myOpening);

        /* Create the extras panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), myPanel);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

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
        theFieldSet.addFieldElement(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), MetisDataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        TethysSwingEnablePanel myPanel = new TethysSwingEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        TethysSwingSpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        Loan myItem = getItem();
        if (myItem != null) {
            LoanList myLoans = getDataList(MoneyWiseDataType.LOAN, LoanList.class);
            setItem(myLoans.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Loan myLoan = getItem();
        boolean bIsClosed = myLoan.isClosed();
        boolean bIsActive = myLoan.isActive();
        boolean bIsRelevant = myLoan.isRelevant();
        boolean bIsChangeable = !bIsActive && isEditable;

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(Loan.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = bIsClosed
                                        ? !myLoan.getParent().isClosed()
                                        : !bIsRelevant;
        theFieldSet.setEditable(Loan.FIELD_CLOSED, isEditable && bEditClosed);
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
        boolean bHasOpening = myLoan.getOpeningBalance() != null;
        boolean bShowOpening = bIsChangeable || bHasOpening;
        MetisField myOpeningField = LoanInfoSet.getFieldForClass(AccountInfoClass.OPENINGBALANCE);
        theFieldSet.setVisibility(myOpeningField, bShowOpening);
        boolean bShowNotes = isEditable || myLoan.getNotes() != null;
        theFieldSet.setVisibility(LoanInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Category/Currency cannot be changed if the item is active */
        theFieldSet.setEditable(Loan.FIELD_CATEGORY, bIsChangeable);
        theFieldSet.setEditable(Loan.FIELD_CURRENCY, bIsChangeable && !bHasOpening);
        theFieldSet.setEditable(myOpeningField, bIsChangeable);

        /* Set editable value for parent */
        theFieldSet.setEditable(Loan.FIELD_PARENT, isEditable && !bIsClosed);

        /* Set currency for opening balance */
        theFieldSet.setAssumedCurrency(myOpeningField, myLoan.getCurrency());
    }

    @Override
    protected void updateField(final MetisFieldUpdate pUpdate) throws OceanusException {
        /* Access the field */
        MetisField myField = pUpdate.getField();
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
            myLoan.autoCorrect(getUpdateSet());
        } else if (myField.equals(Loan.FIELD_PARENT)) {
            /* Update the Parent */
            myLoan.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Loan.FIELD_CURRENCY)) {
            /* Update the Currency */
            myLoan.setAssetCurrency(pUpdate.getValue(AssetCurrency.class));
        } else if (myField.equals(Loan.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myLoan.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (LoanInfoSet.getClassForField(myField)) {
                case OPENINGBALANCE:
                    myLoan.setOpeningBalance(pUpdate.getMoney());
                    break;
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
    protected void declareGoToItems(final boolean pUpdates) {
        Loan myItem = getItem();
        Payee myParent = myItem.getParent();
        if (!pUpdates) {
            LoanCategory myCategory = myItem.getCategory();
            AssetCurrency myCurrency = myItem.getAssetCurrency();
            declareGoToItem(myCategory);
            declareGoToItem(myCurrency);
        }
        declareGoToItem(myParent);
    }

    /**
     * Build the category type list for an item.
     * @param pMenuBuilder the menu builder
     * @param pLoan the loan to build for
     */
    public void buildCategoryMenu(final JScrollMenuBuilder<LoanCategory> pMenuBuilder,
                                  final Loan pLoan) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        LoanCategory myCurr = pLoan.getCategory();
        JMenuItem myActive = null;

        /* Access Loan Categories */
        LoanCategoryList myCategories = getDataList(MoneyWiseDataType.LOANCATEGORY, LoanCategoryList.class);

        /* Create a simple map for top-level categories */
        Map<String, JScrollMenu> myMap = new HashMap<>();

        /* Loop through the available category values */
        Iterator<LoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            LoanCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            boolean bIgnore = myCategory.isDeleted() || myCategory.isCategoryClass(LoanCategoryClass.PARENT);
            if (bIgnore) {
                continue;
            }

            /* Determine menu to add to */
            LoanCategory myParent = myCategory.getParentCategory();
            String myParentName = myParent.getName();
            JScrollMenu myMenu = myMap.get(myParentName);

            /* If this is a new menu */
            if (myMenu == null) {
                /* Create a new JMenu and add it to the popUp */
                myMenu = pMenuBuilder.addSubMenu(myParentName);
                myMap.put(myParentName, myMenu);
            }

            /* Create a new JMenuItem and add it to the popUp */
            JMenuItem myItem = pMenuBuilder.addItem(myMenu, myCategory, myCategory.getSubCategory());

            /* Note active category */
            if (myCategory.equals(myCurr)) {
                myActive = myMenu;
                myMenu.showItem(myItem);
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    /**
     * Build the parent list for an item.
     * @param pMenuBuilder the menu builder
     * @param pLoan the loan to build for
     */
    public void buildParentMenu(final JScrollMenuBuilder<Payee> pMenuBuilder,
                                final Loan pLoan) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        LoanCategoryClass myType = pLoan.getCategoryClass();
        Payee myCurr = pLoan.getParent();
        JMenuItem myActive = null;

        /* Access Payees */
        PayeeList myPayees = getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* Loop through the Payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted or non-owner */
            boolean bIgnore = myPayee.isDeleted() || !myPayee.getPayeeTypeClass().canParentLoan(myType);
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
     * @param pLoan the loan to build for
     */
    public void buildCurrencyMenu(final JScrollMenuBuilder<AssetCurrency> pMenuBuilder,
                                  final Loan pLoan) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        AssetCurrency myCurr = pLoan.getAssetCurrency();
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
}
