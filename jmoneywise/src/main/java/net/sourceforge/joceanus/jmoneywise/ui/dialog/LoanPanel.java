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
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.data.LoanCategory.LoanCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency.AccountCurrencyList;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Loan.
 */
public class LoanPanel
        extends DataItemPanel<Loan>
        implements JFieldButtonPopUp {
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
    private final JButton theCatButton;

    /**
     * Loan Parent Button Field.
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
    public LoanPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the labels */
        JLabel myNameLabel = new JLabel(Loan.FIELD_NAME.getName() + ":", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel(Loan.FIELD_DESC.getName() + ":", SwingConstants.TRAILING);
        JLabel myCatLabel = new JLabel(Loan.FIELD_CATEGORY.getName() + ":", SwingConstants.TRAILING);
        JLabel myParLabel = new JLabel(Loan.FIELD_PARENT.getName() + ":", SwingConstants.TRAILING);
        JLabel myCurrLabel = new JLabel(Loan.FIELD_CURRENCY.getName() + ":", SwingConstants.TRAILING);
        JLabel myClosedLabel = new JLabel(Loan.FIELD_CLOSED.getName() + ":", SwingConstants.TRAILING);

        /* Create the text fields */
        theName = new JTextField(Loan.NAMELEN);
        theDesc = new JTextField(Loan.DESCLEN);

        /* Create the buttons */
        theCatButton = new JButton();
        theParentButton = new JButton();
        theCurrencyButton = new JButton();
        theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Loan.FIELD_NAME, DataType.STRING, myNameLabel, theName);
        theFieldSet.addFieldElement(Loan.FIELD_DESC, DataType.STRING, myDescLabel, theDesc);
        theFieldSet.addFieldElement(Loan.FIELD_CATEGORY, this, LoanCategory.class, myCatLabel, theCatButton);
        theFieldSet.addFieldElement(Loan.FIELD_PARENT, this, Payee.class, myParLabel, theParentButton);
        theFieldSet.addFieldElement(Loan.FIELD_CURRENCY, this, AccountCurrency.class, myCurrLabel, theCurrencyButton);
        theFieldSet.addFieldElement(Loan.FIELD_CLOSED, this, Boolean.class, myClosedLabel, theClosedButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        add(myNameLabel);
        add(theName);
        add(myDescLabel);
        add(theDesc);
        add(myCatLabel);
        add(theCatButton);
        add(myParLabel);
        add(theParentButton);
        add(myCurrLabel);
        add(theCurrencyButton);
        add(myClosedLabel);
        add(theClosedButton);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Loan.FIELD_CLOSED, false);
    }

    @Override
    public JPopupMenu getPopUpMenu(final JFieldButtonAction pActionSrc,
                                   final JDataField pField) {
        /* Switch on field */
        if (pField.equals(Loan.FIELD_CATEGORY)) {
            /* Build the category menu */
            return getCategoryPopUpMenu(pActionSrc);
        } else if (pField.equals(Loan.FIELD_CURRENCY)) {
            /* Build the currency menu */
            return getCurrencyPopUpMenu(pActionSrc);
        } else if (pField.equals(Loan.FIELD_PARENT)) {
            /* Build the parent menu */
            return getParentPopUpMenu(pActionSrc);
        }

        /* return no menu */
        return null;
    }

    /**
     * Build the category menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getCategoryPopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Determine the category of the loan */
        Loan myLoan = getItem();
        LoanCategory myCurr = myLoan.getCategory();
        JMenuItem myActive = null;

        /* Access Categories */
        MoneyWiseData myData = myLoan.getDataSet();
        LoanCategoryList myCategories = myData.getLoanCategories();

        /* Loop through the Categories */
        Iterator<LoanCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            LoanCategory myCategory = myIterator.next();

            /* Ignore deleted or parent */
            boolean bIgnore = myCategory.isDeleted() || myCategory.getCategoryTypeClass().isTotals();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the category */
            Action myAction = pActionSrc.getNewAction(myCategory);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);

            /* If this is the active category */
            if (myCategory.equals(myCurr)) {
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

        /* Determine the currency of the loan */
        Loan myLoan = getItem();
        AccountCurrency myCurr = myLoan.getLoanCurrency();
        JMenuItem myActive = null;

        /* Access AccountCurrencies */
        MoneyWiseData myData = myLoan.getDataSet();
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

        /* Determine the parent of the loan */
        Loan myLoan = getItem();
        Payee myCurr = myLoan.getParent();
        JMenuItem myActive = null;

        /* Access Payees */
        MoneyWiseData myData = myLoan.getDataSet();
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
        Loan myLoan = getItem();

        /* Process updates */
        if (myField.equals(Loan.FIELD_NAME)) {
            /* Update the Name */
            myLoan.setName(pUpdate.getValue(String.class));
        } else if (myField.equals(Loan.FIELD_DESC)) {
            /* Update the Description */
            myLoan.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(Loan.FIELD_CATEGORY)) {
            /* Update the Category */
            myLoan.setLoanCategory(pUpdate.getValue(LoanCategory.class));
        } else if (myField.equals(Loan.FIELD_PARENT)) {
            /* Update the Parent */
            myLoan.setParent(pUpdate.getValue(Payee.class));
        } else if (myField.equals(Loan.FIELD_CURRENCY)) {
            /* Update the Currency */
            myLoan.setLoanCurrency(pUpdate.getValue(AccountCurrency.class));
        }
    }
}
