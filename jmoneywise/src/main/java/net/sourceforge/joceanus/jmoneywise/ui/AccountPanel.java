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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.JDataTable;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

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
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = NLS_BUNDLE.getString("DataEntryTitle");

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
    private final JScrollButton<PanelName> theSelectButton;

    /**
     * The card panel.
     */
    private final JPanel theCardPanel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * The filter card panel.
     */
    private final JEnablePanel theFilterCardPanel;

    /**
     * The card layout for the filter button.
     */
    private final CardLayout theFilterLayout;

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
     * The UpdateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The action buttons panel.
     */
    private final ActionButtons theActionButtons;

    /**
     * Are we refreshing?
     */
    private boolean isRefreshing = false;

    /**
     * Constructor.
     * @param pView the data view
     */
    public AccountPanel(final View pView) {
        /* Build the Update set */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(pView, MoneyWiseDataType.class);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = pView.getDataMgr();
        JDataEntry mySection = pView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry(NLS_DATAENTRY);
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the action buttons panel */
        theActionButtons = new ActionButtons(theUpdateSet);

        /* Create the table panels */
        thePayeeTable = new PayeeTable(pView, theUpdateSet, theError);
        theSecurityTable = new SecurityTable(pView, theUpdateSet, theError);
        theDepositTable = new DepositTable(pView, theUpdateSet, theError);
        theCashTable = new CashTable(pView, theUpdateSet, theError);
        theLoanTable = new LoanTable(pView, theUpdateSet, theError);
        thePortfolioTable = new PortfolioTable(pView, theUpdateSet, theError);

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_DATA);
        theSelectButton = new JScrollButton<PanelName>();
        buildSelectMenu();

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

        /* Create the new card panel */
        theFilterCardPanel = new JEnablePanel();
        theFilterLayout = new CardLayout();
        theFilterCardPanel.setLayout(theFilterLayout);

        /* Build the new card panel */
        theFilterCardPanel.add(theDepositTable.getFilterPanel(), PanelName.DEPOSITS.toString());
        theFilterCardPanel.add(theCashTable.getFilterPanel(), PanelName.CASH.toString());
        theFilterCardPanel.add(theLoanTable.getFilterPanel(), PanelName.LOANS.toString());
        theFilterCardPanel.add(thePortfolioTable.getFilterPanel(), PanelName.PORTFOLIOS.toString());
        theFilterCardPanel.add(theSecurityTable.getFilterPanel(), PanelName.SECURITIES.toString());
        theFilterCardPanel.add(thePayeeTable.getFilterPanel(), PanelName.PAYEES.toString());

        /* Create the selection panel */
        JPanel mySelect = new JPanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelect.setLayout(new BoxLayout(mySelect, BoxLayout.X_AXIS));
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(myLabel);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(theSelectButton);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(Box.createHorizontalGlue());
        mySelect.add(theFilterCardPanel);
        mySelect.add(Box.createHorizontalGlue());
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.setPreferredSize(new Dimension(JDataTable.WIDTH_PANEL, CategoryPanel.PANEL_PAD));
        mySelect.setMaximumSize(new Dimension(Integer.MAX_VALUE, CategoryPanel.PANEL_PAD));

        /* Create the header panel */
        JPanel myHeader = new JPanel();
        myHeader.setLayout(new BoxLayout(myHeader, BoxLayout.X_AXIS));
        myHeader.add(mySelect);
        myHeader.add(theError);
        myHeader.add(theActionButtons);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(myHeader);
        add(theCardPanel);

        /* Create the listener */
        AccountListener myListener = new AccountListener();
        theError.addChangeListener(myListener);
        theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, myListener);
        theDepositTable.addChangeListener(myListener);
        theDepositTable.addActionListener(myListener);
        theCashTable.addChangeListener(myListener);
        theCashTable.addActionListener(myListener);
        theLoanTable.addChangeListener(myListener);
        theLoanTable.addActionListener(myListener);
        thePortfolioTable.addChangeListener(myListener);
        thePortfolioTable.addActionListener(myListener);
        theSecurityTable.addChangeListener(myListener);
        theSecurityTable.addActionListener(myListener);
        thePayeeTable.addChangeListener(myListener);
        thePayeeTable.addActionListener(myListener);
        theActionButtons.addActionListener(myListener);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);
    }

    /**
     * Build select menu.
     */
    private void buildSelectMenu() {
        /* Create builder */
        JScrollMenuBuilder<PanelName> myBuilder = theSelectButton.getMenuBuilder();

        /* Loop through the panels */
        for (PanelName myPanel : PanelName.values()) {
            /* Create a new JMenuItem for the panel */
            myBuilder.addItem(myPanel);
        }
    }

    /**
     * Show locked accounts.
     * @param pShow true/false
     */
    public void showLocked(final boolean pShow) {
        /* Adjust lock settings */
        thePayeeTable.setShowAll(pShow);
        theSecurityTable.setShowAll(pShow);
        theDepositTable.setShowAll(pShow);
        theCashTable.setShowAll(pShow);
        theLoanTable.setShowAll(pShow);
        thePortfolioTable.setShowAll(pShow);
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Cancel editing */
        thePayeeTable.cancelEditing();
        theSecurityTable.cancelEditing();
        theDepositTable.cancelEditing();
        theCashTable.cancelEditing();
        theLoanTable.cancelEditing();
        thePortfolioTable.cancelEditing();
    }

    /**
     * Refresh data.
     * @throws JOceanusException on error
     */
    protected void refreshData() throws JOceanusException {
        /* Note that we are refreshing */
        isRefreshing = true;

        /* Must be done in dataType order to ensure that links are resolved correctly */
        thePayeeTable.refreshData();
        theSecurityTable.refreshData();
        theDepositTable.refreshData();
        theCashTable.refreshData();
        theLoanTable.refreshData();
        thePortfolioTable.refreshData();

        /* Clear refreshing flag */
        isRefreshing = false;
        setVisibility();

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Switch on active component */
        switch (theActive) {
            case DEPOSITS:
                theDepositTable.determineFocus(theDataEntry);
                break;
            case CASH:
                theCashTable.determineFocus(theDataEntry);
                break;
            case LOANS:
                theLoanTable.determineFocus(theDataEntry);
                break;
            case PORTFOLIOS:
                thePortfolioTable.determineFocus(theDataEntry);
                break;
            case SECURITIES:
                theSecurityTable.determineFocus(theDataEntry);
                break;
            case PAYEES:
                thePayeeTable.determineFocus(theDataEntry);
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
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public boolean hasSession() {
        /* Determine whether we have session focus */
        boolean hasSession = theDepositTable.hasSession();
        if (!hasSession) {
            hasSession = theCashTable.hasUpdates();
        }
        if (!hasSession) {
            hasSession = theLoanTable.hasUpdates();
        }
        if (!hasSession) {
            hasSession = thePortfolioTable.hasUpdates();
        }
        if (!hasSession) {
            hasSession = theSecurityTable.hasUpdates();
        }
        if (!hasSession) {
            hasSession = thePayeeTable.hasSession();
        }

        /* Return to caller */
        return hasSession;
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
     * Does this panel have item editing occurring?
     * @return true/false
     */
    public boolean isItemEditing() {
        /* Determine whether we have item editing */
        boolean isEditing = theDepositTable.isItemEditing();
        if (!isEditing) {
            isEditing = theCashTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theLoanTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = thePortfolioTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = theSecurityTable.isItemEditing();
        }
        if (!isEditing) {
            isEditing = thePayeeTable.isItemEditing();
        }

        /* Return to caller */
        return isEditing;
    }

    /**
     * Select account.
     * @param pAccount the account to select
     */
    protected void selectAccount(final AssetBase<?> pAccount) {
        /* Determine which panel to show */
        if (pAccount instanceof Deposit) {
            theDepositTable.selectDeposit((Deposit) pAccount);
            showPanel(PanelName.DEPOSITS);
        } else if (pAccount instanceof Cash) {
            theCashTable.selectCash((Cash) pAccount);
            showPanel(PanelName.CASH);
        } else if (pAccount instanceof Loan) {
            theLoanTable.selectLoan((Loan) pAccount);
            showPanel(PanelName.LOANS);
        } else if (pAccount instanceof Portfolio) {
            thePortfolioTable.selectPortfolio((Portfolio) pAccount);
            showPanel(PanelName.PORTFOLIOS);
        } else if (pAccount instanceof Security) {
            theSecurityTable.selectSecurity((Security) pAccount);
            showPanel(PanelName.SECURITIES);
        } else if (pAccount instanceof Payee) {
            thePayeeTable.selectPayee((Payee) pAccount);
            showPanel(PanelName.PAYEES);
        }
    }

    /**
     * Show panel.
     * @param pName the panel name
     */
    private void showPanel(final PanelName pName) {
        /* Obtain name of panel */
        String myName = pName.toString();

        /* Move correct card to front */
        theLayout.show(theCardPanel, myName);
        theFilterLayout.show(theFilterCardPanel, myName);

        /* Note the active panel */
        theActive = pName;
        theSelectButton.setText(myName);

        /* Determine the focus */
        determineFocus();
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        boolean hasUpdates = hasUpdates();
        boolean isItemEditing = isItemEditing();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);

        /* Update the selection */
        theSelectButton.setEnabled(!isItemEditing);
        theFilterCardPanel.setEnabled(!isItemEditing);

        /* Alert listeners that there has been a change */
        fireStateChanged();
    }

    /**
     * Listener.
     */
    private final class AccountListener
            implements ActionListener, ChangeListener, PropertyChangeListener {
        @Override
        public void actionPerformed(final ActionEvent pEvent) {
            Object o = pEvent.getSource();
            String myCmd = pEvent.getActionCommand();

            /* if this is the action buttons reporting */
            if (theActionButtons.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Process the command */
                theUpdateSet.processCommand(myCmd, theError);

                /* If this is an ActionDetailEvent */
            } else if (pEvent instanceof ActionDetailEvent) {
                /* Access event and obtain details */
                ActionDetailEvent evt = (ActionDetailEvent) pEvent;
                switch (evt.getSubId()) {
                /* Pass through the event */
                    case MainTab.ACTION_VIEWREGISTER:
                    case MainTab.ACTION_VIEWSTATEMENT:
                    case MainTab.ACTION_VIEWCATEGORY:
                    case MainTab.ACTION_VIEWTAG:
                    case MainTab.ACTION_VIEWTAXYEAR:
                    case MainTab.ACTION_VIEWSTATIC:
                        cascadeActionEvent(evt);
                        break;

                    /* Access subPanels */
                    case MainTab.ACTION_VIEWACCOUNT:
                        selectAccount(evt.getDetails(AssetBase.class));
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            /* Access reporting object */
            Object o = e.getSource();

            /* If this is the error panel reporting */
            if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelectButton.setVisible(!isError);

                /* Lock scroll-able area */
                theCardPanel.setEnabled(!isError);

                /* Lock Action Buttons */
                theActionButtons.setEnabled(!isError);

                /* if this is one of the sub-panels */
            } else if (!isRefreshing) {
                /* Adjust visibility */
                setVisibility();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            Object o = evt.getSource();

            /* if this event relates to the select button */
            if (theSelectButton.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Show the correct panel */
                showPanel(theSelectButton.getValue());
            }
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
