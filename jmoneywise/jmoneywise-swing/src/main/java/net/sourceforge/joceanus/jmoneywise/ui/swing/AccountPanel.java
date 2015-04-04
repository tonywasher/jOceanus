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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.ActionButtons;
import net.sourceforge.joceanus.jprometheus.ui.swing.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.swing.JDataTable;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusActionEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusActionRegistration;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * Top-level panel for Accounts.
 */
public class AccountPanel
        extends JPanel
        implements JOceanusEventProvider {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 2128429177675141337L;

    /**
     * Strut width.
     */
    protected static final int STRUT_WIDTH = 5;

    /**
     * Text for DataEntry Title.
     */
    private static final String NLS_DATAENTRY = MoneyWiseUIResource.ASSET_DATAENTRY.getValue();

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = MoneyWiseUIResource.ASSET_TITLE_SELECT.getValue();

    /**
     * Text for Selection Prompt.
     */
    private static final String NLS_DATA = MoneyWiseUIResource.ASSET_PROMPT_SELECT.getValue();

    /**
     * The Event Manager.
     */
    private final transient JOceanusEventManager theEventManager;

    /**
     * The Data View.
     */
    private final transient SwingView theView;

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
     * Option Table.
     */
    private final StockOptionTable theOptionTable;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The data entry.
     */
    private final transient ViewerEntry theDataEntry;

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
    public AccountPanel(final SwingView pView) {
        /* Store details */
        theView = pView;

        /* Create the event manager */
        theEventManager = new JOceanusEventManager();

        /* Build the Update set */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(pView, MoneyWiseDataType.class);

        /* Create the top level debug entry for this view */
        ViewerManager myDataMgr = pView.getViewerManager();
        ViewerEntry mySection = pView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.newEntry(NLS_DATAENTRY);
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
        theOptionTable = new StockOptionTable(pView, theUpdateSet, theError);

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
        theCardPanel.add(theOptionTable.getPanel(), PanelName.OPTIONS.toString());
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
        theFilterCardPanel.add(theOptionTable.getFilterPanel(), PanelName.OPTIONS.toString());

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

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the listener */
        new AccountListener();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
        theOptionTable.setShowAll(pShow);
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
        theOptionTable.cancelEditing();
    }

    /**
     * Refresh data.
     * @throws JOceanusException on error
     */
    protected void refreshData() throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Accounts");

        /* Note that we are refreshing */
        isRefreshing = true;

        /* Must be done in dataType order to ensure that links are resolved correctly */
        thePayeeTable.refreshData();
        theSecurityTable.refreshData();
        theDepositTable.refreshData();
        theCashTable.refreshData();
        theLoanTable.refreshData();
        thePortfolioTable.refreshData();
        theOptionTable.refreshData();

        /* Clear refreshing flag */
        isRefreshing = false;
        setVisibility();

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);

        /* Complete the task */
        myTask.end();
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
            case OPTIONS:
                theOptionTable.determineFocus(theDataEntry);
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
        if (!hasUpdates) {
            hasUpdates = theOptionTable.hasUpdates();
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
            hasSession = theCashTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theLoanTable.hasSession();
        }
        if (!hasSession) {
            hasSession = thePortfolioTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theSecurityTable.hasSession();
        }
        if (!hasSession) {
            hasSession = thePayeeTable.hasSession();
        }
        if (!hasSession) {
            hasSession = theOptionTable.hasSession();
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
        if (!hasErrors) {
            hasErrors = theOptionTable.hasErrors();
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
        if (!isEditing) {
            isEditing = theOptionTable.isItemEditing();
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
        } else if (pAccount instanceof StockOption) {
            theOptionTable.selectOption((StockOption) pAccount);
            showPanel(PanelName.OPTIONS);
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
        theEventManager.fireStateChanged();
    }

    /**
     * Listener.
     */
    private final class AccountListener
            implements JOceanusActionEventListener, JOceanusChangeEventListener, PropertyChangeListener {
        /**
         * Error Registration.
         */
        private final JOceanusChangeRegistration theErrorReg;

        /**
         * Action Registration.
         */
        private final JOceanusActionRegistration theActionReg;

        /**
         * Constructor.
         */
        private AccountListener() {
            /* Handle interesting items */
            theSelectButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
            theErrorReg = theError.getEventRegistrar().addChangeListener(this);
            theActionReg = theActionButtons.getEventRegistrar().addActionListener(this);

            /* Handle sub-panels */
            JOceanusEventRegistrar myRegistrar = theDepositTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theCashTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theLoanTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = thePortfolioTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theSecurityTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = thePayeeTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
            myRegistrar = theOptionTable.getEventRegistrar();
            myRegistrar.addChangeListener(this);
            myRegistrar.addActionListener(this);
        }

        @Override
        public void processActionEvent(final JOceanusActionEvent pEvent) {
            /* if this is the action buttons reporting */
            if (theActionReg.isRelevant(pEvent)) {
                /* Cancel Editing */
                cancelEditing();

                /* Process the command */
                theUpdateSet.processCommand(pEvent.getActionId(), theError);

                /* else handle or cascade */
            } else {
                /* Access event and obtain details */
                switch (pEvent.getActionId()) {
                /* Pass through the event */
                    case MainTab.ACTION_VIEWSTATEMENT:
                    case MainTab.ACTION_VIEWCATEGORY:
                    case MainTab.ACTION_VIEWTAG:
                    case MainTab.ACTION_VIEWTAXYEAR:
                    case MainTab.ACTION_VIEWSTATIC:
                        theEventManager.cascadeActionEvent(pEvent);
                        break;

                    /* Access subPanels */
                    case MainTab.ACTION_VIEWACCOUNT:
                        selectAccount(pEvent.getDetails(AssetBase.class));
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the error panel reporting */
            if (theErrorReg.isRelevant(pEvent)) {
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
        DEPOSITS(MoneyWiseDataType.DEPOSIT),

        /**
         * Cash.
         */
        CASH(MoneyWiseDataType.CASH),

        /**
         * Loans.
         */
        LOANS(MoneyWiseDataType.LOAN),

        /**
         * Portfolios.
         */
        PORTFOLIOS(MoneyWiseDataType.PORTFOLIO),

        /**
         * Securities.
         */
        SECURITIES(MoneyWiseDataType.SECURITY),

        /**
         * Payees.
         */
        PAYEES(MoneyWiseDataType.PAYEE),

        /**
         * StockOptions.
         */
        OPTIONS(MoneyWiseDataType.STOCKOPTION);

        /**
         * The String name.
         */
        private String theName;

        /**
         * Constructor.
         * @param pDataType the dataType
         */
        private PanelName(final MoneyWiseDataType pDataType) {
            theName = pDataType.getListName();
        }

        @Override
        public String toString() {
            /* return the name */
            return theName;
        }
    }
}
