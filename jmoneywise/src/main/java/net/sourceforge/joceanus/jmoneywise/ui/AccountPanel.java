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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;

/**
 * Top-level panel for Accounts.
 */
public class AccountPanel
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2128429177675141337L;

    /**
     * Strut width.
     */
    protected static final int STRUT_WIDTH = 5;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountPanel.class.getName());

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = NLS_BUNDLE.getString("SelectionTitle");

    /**
     * Text for Selection Prompt.
     */
    private static final String NLS_DATA = NLS_BUNDLE.getString("SelectionPrompt");

    /**
     * The select button.
     */
    private final JButton theSelectButton;

    /**
     * The card panel.
     */
    private final JPanel theCardPanel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * The active panel.
     */
    private PanelName theActive;

    /**
     * Deposit Table.
     */
    private final DepositTable theDepositTable;

    /**
     * Cash Table.
     */
    private final CashTable theCashTable;

    /**
     * Loan Table.
     */
    private final LoanTable theLoanTable;

    /**
     * Portfolio Table.
     */
    private final PortfolioTable thePortfolioTable;

    /**
     * Security Table.
     */
    private final SecurityTable theSecurityTable;

    /**
     * Payee Table.
     */
    private final PayeeTable thePayeeTable;

    /**
     * Constructor.
     * @param pView the data view
     */
    public AccountPanel(final View pView) {
        /* Create the table panels */
        theDepositTable = new DepositTable(pView);
        theCashTable = new CashTable(pView);
        theLoanTable = new LoanTable(pView);
        thePortfolioTable = new PortfolioTable(pView);
        theSecurityTable = new SecurityTable(pView);
        thePayeeTable = new PayeeTable(pView);

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_DATA);
        theSelectButton = new JButton(ArrowIcon.DOWN);
        theSelectButton.setVerticalTextPosition(AbstractButton.CENTER);
        theSelectButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Create the card panel */
        theCardPanel = new JEnablePanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Add to the card panels */
        theCardPanel.add(theDepositTable.getPanel(), PanelName.DEPOSITS.toString());
        theCardPanel.add(theCashTable.getPanel(), PanelName.CASH.toString());
        theCardPanel.add(theLoanTable.getPanel(), PanelName.LOANS.toString());
        theCardPanel.add(thePortfolioTable.getPanel(), PanelName.PORTFOLIOS.toString());
        theCardPanel.add(theSecurityTable.getPanel(), PanelName.SECURITIES.toString());
        theCardPanel.add(thePayeeTable.getPanel(), PanelName.PAYEES.toString());
        theActive = PanelName.DEPOSITS;
        theSelectButton.setText(theActive.toString());

        /* Create the selection panel */
        JPanel mySelect = new JPanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelect.setLayout(new BoxLayout(mySelect, BoxLayout.X_AXIS));
        mySelect.add(myLabel);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(theSelectButton);
        mySelect.add(Box.createHorizontalGlue());

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mySelect);
        add(theCardPanel);

        /* Create the listener */
        AccountListener myListener = new AccountListener();
        theSelectButton.addActionListener(myListener);
        theDepositTable.addChangeListener(myListener);
        theCashTable.addChangeListener(myListener);
        theLoanTable.addChangeListener(myListener);
        thePortfolioTable.addChangeListener(myListener);
        theSecurityTable.addChangeListener(myListener);
        thePayeeTable.addChangeListener(myListener);
    }

    /**
     * Refresh data.
     */
    protected void refreshData() {
        theDepositTable.refreshData();
        theCashTable.refreshData();
        theLoanTable.refreshData();
        thePortfolioTable.refreshData();
        theSecurityTable.refreshData();
        thePayeeTable.refreshData();
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Switch on active component */
        switch (theActive) {
            case DEPOSITS:
                theDepositTable.determineFocus();
                break;
            case CASH:
                theCashTable.determineFocus();
                break;
            case LOANS:
                theLoanTable.determineFocus();
                break;
            case PORTFOLIOS:
                thePortfolioTable.determineFocus();
                break;
            case SECURITIES:
                theSecurityTable.determineFocus();
                break;
            case PAYEES:
                thePayeeTable.determineFocus();
                break;
            default:
                break;
        }
    }

    /**
     * Does this panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Determine whether we have updates */
        boolean hasUpdates = theDepositTable.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theCashTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theLoanTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = thePortfolioTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theSecurityTable.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = thePayeeTable.hasUpdates();
        }

        /* Return to caller */
        return hasUpdates;
    }

    /**
     * Does this panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        /* Determine whether we have errors */
        boolean hasErrors = theDepositTable.hasErrors();
        if (!hasErrors) {
            hasErrors = theCashTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theLoanTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = thePortfolioTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = theSecurityTable.hasErrors();
        }
        if (!hasErrors) {
            hasErrors = thePayeeTable.hasErrors();
        }

        /* Return to caller */
        return hasErrors;
    }

    /**
     * Listener.
     */
    private final class AccountListener
            implements ActionListener, ChangeListener {
        /**
         * Show Selection menu.
         */
        private void showSelectMenu() {
            /* Create a new popUp menu */
            JPopupMenu myPopUp = new JPopupMenu();

            /* Loop through the panel names */
            for (PanelName myName : PanelName.values()) {
                /* Add reference */
                AccountAction myAction = new AccountAction(myName);
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.add(myItem);
            }

            /* Show the AnalysisType menu in the correct place */
            Rectangle myLoc = theSelectButton.getBounds();
            myPopUp.show(theSelectButton, 0, myLoc.height);
        }

        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();

            /* If this event relates to the SelectButton */
            if (theSelectButton.equals(o)) {
                /* Show the selection menu */
                showSelectMenu();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            /* Pass on notification */
            fireStateChanged();
        }
    }

    /**
     * Category action class.
     */
    private final class AccountAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -1763554175717417130L;

        /**
         * Category name.
         */
        private final PanelName theName;

        /**
         * Constructor.
         * @param pName the panel name
         */
        private AccountAction(final PanelName pName) {
            super(pName.toString());
            theName = pName;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* Move correct card to front */
            theLayout.show(theCardPanel, theName.toString());

            /* Note the active panel */
            theActive = theName;
            theSelectButton.setText(theName.toString());
        }
    }

    /**
     * Panel names.
     */
    private enum PanelName {
        /**
         * Deposits.
         */
        DEPOSITS,

        /**
         * Cash.
         */
        CASH,

        /**
         * Loans.
         */
        LOANS,

        /**
         * Portfolios.
         */
        PORTFOLIOS,

        /**
         * Securities.
         */
        SECURITIES,

        /**
         * Payees.
         */
        PAYEES;

        /**
         * The String name.
         */
        private String theName;

        @Override
        public String toString() {
            /* If we have not yet loaded the name */
            if (theName == null) {
                /* Load the name */
                theName = NLS_BUNDLE.getString(name());
            }

            /* return the name */
            return theName;
        }
    }
}
