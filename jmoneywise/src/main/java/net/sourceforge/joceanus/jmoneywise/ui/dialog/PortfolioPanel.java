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
import net.sourceforge.joceanus.jtethys.JOceanusException;
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
    // private final JButton theClosedButton;

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
        // theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Portfolio.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(Portfolio.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(Portfolio.FIELD_HOLDING, Deposit.class, theHoldingButton);
        // theFieldSet.addFieldElement(Portfolio.FIELD_CLOSED, this, Boolean.class, myClosedLabel, theClosedButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_NAME, this);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_DESC, this);
        theFieldSet.addFieldToPanel(Portfolio.FIELD_HOLDING, this);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Create the listener */
        new AccountListener();
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Portfolio.FIELD_CLOSED, false);
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
        }
    }

    /**
     * Account Listener.
     */
    private final class AccountListener
            implements ChangeListener {
        /**
         * The Holding Menu Builder.
         */
        private final JScrollMenuBuilder<Deposit> theHoldingMenuBuilder;

        /**
         * Constructor.
         */
        private AccountListener() {
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
