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
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Portfolio.
 */
public class PortfolioPanel
        extends DataItemPanel<Portfolio> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 8504264018922234415L;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Portfolio> theFieldSet;

    /**
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * HoldingDeposit Button Field.
     */
    private final JScrollButton<Deposit> theHoldingButton;

    /**
     * Closed Button Field.
     */
    private final ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * TaxFree Button Field.
     */
    private final ComplexIconButtonState<Boolean, Boolean> theTaxFreeState;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public PortfolioPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the text fields */
        theName = new JTextField(Portfolio.NAMELEN);
        theDesc = new JTextField(Portfolio.DESCLEN);

        /* Create the buttons */
        theHoldingButton = new JScrollButton<Deposit>();

        /* Set button states */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);
        theTaxFreeState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

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
        new PortfolioListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Build the button states */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildOptionButton(theClosedState);
        JIconButton<Boolean> myTaxFreeButton = new JIconButton<Boolean>(theTaxFreeState);
        MoneyWiseIcons.buildOptionButton(theTaxFreeState);

        theFieldSet.addFieldElement(Portfolio.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Portfolio.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Portfolio.FIELD_HOLDING, Deposit.class, theHoldingButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_CLOSED, Boolean.class, myClosedButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_TAXFREE, Boolean.class, myTaxFreeButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_HOLDING, this);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_CLOSED, this);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_TAXFREE, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

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
        JTextField myWebSite = new JTextField();
        JTextField myCustNo = new JTextField();
        JTextField myUserId = new JTextField();
        JTextField myPassWord = new JTextField();

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), DataType.CHARARRAY, mySortCode);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), DataType.CHARARRAY, myAccount);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), DataType.CHARARRAY, myReference);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), DataType.CHARARRAY, myWebSite);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), DataType.CHARARRAY, myCustNo);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), DataType.CHARARRAY, myUserId);
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), DataType.CHARARRAY, myPassWord);

        /* Create the extras panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the extras panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.ACCOUNT), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.SORTCODE), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.REFERENCE), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.WEBSITE), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.CUSTOMERNO), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.USERID), myPanel);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.PASSWORD), myPanel);
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
        theFieldSet.addFieldElement(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(PortfolioInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        Portfolio myPortfolio = getItem();

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = myPortfolio.isClosed() || !myPortfolio.isRelevant();
        theFieldSet.setVisibility(Security.FIELD_CLOSED, bShowClosed);
        theClosedState.setState(bShowClosed);

        /* Holding and TaxFree status cannot be changed if the item is active */
        boolean bIsActive = myPortfolio.isActive();
        theFieldSet.setEditable(Portfolio.FIELD_HOLDING, !bIsActive);
        theFieldSet.setEditable(Portfolio.FIELD_TAXFREE, !bIsActive);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        Portfolio myPortfolio = getItem();

        /* Process updates */
        if (myField.equals(Portfolio.FIELD_NAME)) {
            /* Update the Name */
            myPortfolio.setName(pUpdate.getValue(String.class));
        } else if (myField.equals(Portfolio.FIELD_DESC)) {
            /* Update the Description */
            myPortfolio.setDescription(pUpdate.getValue(String.class));
        } else if (myField.equals(Portfolio.FIELD_HOLDING)) {
            /* Update the Holding */
            myPortfolio.setHolding(pUpdate.getValue(Deposit.class));
        } else if (myField.equals(Portfolio.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myPortfolio.setClosed(pUpdate.getValue(Boolean.class));
        } else if (myField.equals(Portfolio.FIELD_TAXFREE)) {
            /* Update the taxFree indication */
            myPortfolio.setTaxFree(pUpdate.getValue(Boolean.class));
        } else {
            /* Switch on the field */
            switch (PortfolioInfoSet.getClassForField(myField)) {
                case SORTCODE:
                    myPortfolio.setSortCode(pUpdate.getCharArray());
                    break;
                case ACCOUNT:
                    myPortfolio.setAccount(pUpdate.getCharArray());
                    break;
                case REFERENCE:
                    myPortfolio.setReference(pUpdate.getCharArray());
                    break;
                case WEBSITE:
                    myPortfolio.setWebSite(pUpdate.getCharArray());
                    break;
                case CUSTOMERNO:
                    myPortfolio.setCustNo(pUpdate.getCharArray());
                    break;
                case USERID:
                    myPortfolio.setUserId(pUpdate.getCharArray());
                    break;
                case PASSWORD:
                    myPortfolio.setPassword(pUpdate.getCharArray());
                    break;
                case NOTES:
                    myPortfolio.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Portfolio Listener.
     */
    private final class PortfolioListener
            implements ChangeListener {
        /**
         * The Holding Menu Builder.
         */
        private final JScrollMenuBuilder<Deposit> theHoldingMenuBuilder;

        /**
         * Constructor.
         */
        private PortfolioListener() {
            /* Access the MenuBuilders */
            theHoldingMenuBuilder = theHoldingButton.getMenuBuilder();
            theHoldingMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theHoldingMenuBuilder.equals(o)) {
                buildHoldingMenu();
            }
        }

        /**
         * Build the holding list for the item.
         */
        private void buildHoldingMenu() {
            /* Clear the menu */
            theHoldingMenuBuilder.clearMenu();

            /* Record active item */
            Portfolio myPortfolio = getItem();
            Deposit myCurr = myPortfolio.getHolding();
            JMenuItem myActive = null;

            /* Access Payees */
            DepositList myDeposits = DepositList.class.cast(findBaseList(Deposit.class));

            /* Loop through the Deposits */
            Iterator<Deposit> myIterator = myDeposits.iterator();
            while (myIterator.hasNext()) {
                Deposit myDeposit = myIterator.next();

                /* Ignore deleted/closed */
                boolean bIgnore = myDeposit.isDeleted() || myDeposit.isClosed();
                if (bIgnore) {
                    continue;
                }

                /* Create a new action for the payee */
                JMenuItem myItem = theHoldingMenuBuilder.addItem(myDeposit);

                /* If this is the active holding */
                if (myDeposit.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theHoldingMenuBuilder.showItem(myActive);
        }
    }
}
