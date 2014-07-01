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
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a Portfolio.
 */
public class PortfolioPanel
        extends DataItemPanel<Portfolio>
        implements JFieldButtonPopUp {
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
    private final JButton theHoldButton;

    /**
     * Closed Button Field.
     */
    private final JButton theClosedButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     */
    public PortfolioPanel(final JFieldManager pFieldMgr) {
        /* Initialise the panel */
        super(pFieldMgr);

        /* Create the labels */
        JLabel myNameLabel = new JLabel(Portfolio.FIELD_NAME.getName() + ":", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel(Portfolio.FIELD_DESC.getName() + ":", SwingConstants.TRAILING);
        JLabel myHoldLabel = new JLabel(Portfolio.FIELD_HOLDING.getName() + ":", SwingConstants.TRAILING);
        JLabel myClosedLabel = new JLabel(Portfolio.FIELD_CLOSED.getName() + ":", SwingConstants.TRAILING);

        /* Create the text fields */
        theName = new JTextField(Portfolio.NAMELEN);
        theDesc = new JTextField(Portfolio.DESCLEN);

        /* Create the buttons */
        theHoldButton = new JButton();
        theClosedButton = new JButton();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();
        theFieldSet.addFieldElement(Portfolio.FIELD_NAME, DataType.STRING, myNameLabel, theName);
        theFieldSet.addFieldElement(Portfolio.FIELD_DESC, DataType.STRING, myDescLabel, theDesc);
        theFieldSet.addFieldElement(Portfolio.FIELD_HOLDING, this, Deposit.class, myHoldLabel, theHoldButton);
        theFieldSet.addFieldElement(Portfolio.FIELD_CLOSED, this, Boolean.class, myClosedLabel, theClosedButton);

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        setLayout(mySpring);
        add(myNameLabel);
        add(theName);
        add(myDescLabel);
        add(theDesc);
        add(myHoldLabel);
        add(theHoldButton);
        add(myClosedLabel);
        add(theClosedButton);
        SpringUtilities.makeCompactGrid(this, mySpring, getComponentCount() >> 1, 2, PADDING_SIZE);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Set visibility */
        theFieldSet.setVisibility(Portfolio.FIELD_CLOSED, false);
    }

    @Override
    public JPopupMenu getPopUpMenu(final JFieldButtonAction pActionSrc,
                                   final JDataField pField) {
        /* Switch on field */
        if (pField.equals(Portfolio.FIELD_HOLDING)) {
            /* Build the holding menu */
            return getHoldingPopUpMenu(pActionSrc);
        }

        /* return no menu */
        return null;
    }

    /**
     * Build the holding menu.
     * @param pActionSrc the action source
     * @return the menu
     */
    private JPopupMenu getHoldingPopUpMenu(final JFieldButtonAction pActionSrc) {
        /* Create the menu */
        JScrollPopupMenu myMenu = new JScrollPopupMenu();

        /* Determine the holding of the portfolio */
        Portfolio myPortfolio = getItem();
        Deposit myCurr = myPortfolio.getHolding();
        JMenuItem myActive = null;

        /* Access Portfolios */
        MoneyWiseData myData = myPortfolio.getDataSet();
        DepositList myDeposits = myData.getDeposits();

        /* Loop through the Deposits */
        Iterator<Deposit> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            Deposit myDeposit = myIterator.next();

            /* Ignore deleted/closed */
            boolean bIgnore = myDeposit.isDeleted() || myDeposit.isClosed();
            if (bIgnore) {
                continue;
            }

            /* Create a new action for the deposit */
            Action myAction = pActionSrc.getNewAction(myDeposit);
            JMenuItem myItem = new JMenuItem(myAction);
            myMenu.addMenuItem(myItem);

            /* If this is the active category */
            if (myDeposit.equals(myCurr)) {
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
}
