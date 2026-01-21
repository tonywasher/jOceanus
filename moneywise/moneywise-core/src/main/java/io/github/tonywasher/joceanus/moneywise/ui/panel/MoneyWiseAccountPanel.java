/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.moneywise.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.ui.MetisErrorPanel;
import io.github.tonywasher.joceanus.metis.viewer.MetisViewerEntry;
import io.github.tonywasher.joceanus.metis.viewer.MetisViewerManager;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseLoan;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import io.github.tonywasher.joceanus.moneywise.ui.MoneyWiseGoToId;
import io.github.tonywasher.joceanus.moneywise.ui.MoneyWiseUIResource;
import io.github.tonywasher.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.prometheus.ui.PrometheusActionButtons;
import io.github.tonywasher.joceanus.prometheus.ui.PrometheusGoToEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusUIEvent;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusViewerEntryId;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButtonFactory;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUILabel;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUICardPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;

/**
 * Top-level panel for Accounts.
 */
public class MoneyWiseAccountPanel
        implements OceanusEventProvider<PrometheusDataEvent>, TethysUIComponent {
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
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The Data View.
     */
    private final MoneyWiseView theView;

    /**
     * The Panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The select button.
     */
    private final TethysUIScrollButtonManager<PanelName> theSelectButton;

    /**
     * The card panel.
     */
    private final TethysUICardPaneManager<TethysUIComponent> theCardPanel;

    /**
     * The select panel.
     */
    private final TethysUIBorderPaneManager theSelectPanel;

    /**
     * The filter card panel.
     */
    private final TethysUICardPaneManager<TethysUIComponent> theFilterCardPanel;

    /**
     * Deposit Table.
     */
    private final MoneyWiseDepositTable theDepositTable;

    /**
     * Cash Table.
     */
    private final MoneyWiseCashTable theCashTable;

    /**
     * Loan Table.
     */
    private final MoneyWiseLoanTable theLoanTable;

    /**
     * Portfolio Table.
     */
    private final MoneyWisePortfolioTable thePortfolioTable;

    /**
     * Security Table.
     */
    private final MoneyWiseSecurityTable theSecurityTable;

    /**
     * Payee Table.
     */
    private final MoneyWisePayeeTable thePayeeTable;

    /**
     * The UpdateSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The data entry.
     */
    private final MetisViewerEntry theViewerEntry;

    /**
     * The error panel.
     */
    private final MetisErrorPanel theError;

    /**
     * The action buttons panel.
     */
    private final PrometheusActionButtons theActionButtons;

    /**
     * Are we refreshing?
     */
    private boolean isRefreshing;

    /**
     * The active panel.
     */
    private PanelName theActive;

    /**
     * Constructor.
     *
     * @param pView the data view
     */
    MoneyWiseAccountPanel(final MoneyWiseView pView) {
        /* Store details */
        theView = pView;

        /* Access GUI Factory */
        final TethysUIFactory<?> myFactory = pView.getGuiFactory();
        final MetisViewerManager myViewer = pView.getViewerManager();

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Build the Update set */
        theEditSet = new PrometheusEditSet(pView);

        /* Create the Panel */
        final TethysUIPaneFactory myPanes = myFactory.paneFactory();
        thePanel = myPanes.newBorderPane();

        /* Create the top level viewer entry for this view */
        final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.MAINTENANCE);
        theViewerEntry = myViewer.newEntry(mySection, NLS_DATAENTRY);
        theViewerEntry.setTreeObject(theEditSet);

        /* Create the error panel */
        theError = pView.getToolkit().getToolkit().newErrorPanel(theViewerEntry);

        /* Create the action buttons panel */
        theActionButtons = new PrometheusActionButtons(myFactory, theEditSet);

        /* Create the table panels */
        thePayeeTable = new MoneyWisePayeeTable(pView, theEditSet, theError);
        theSecurityTable = new MoneyWiseSecurityTable(pView, theEditSet, theError);
        theDepositTable = new MoneyWiseDepositTable(pView, theEditSet, theError);
        theCashTable = new MoneyWiseCashTable(pView, theEditSet, theError);
        theLoanTable = new MoneyWiseLoanTable(pView, theEditSet, theError);
        thePortfolioTable = new MoneyWisePortfolioTable(pView, theEditSet, theError);

        /* Create selection button and label */
        final TethysUIControlFactory myControls = myFactory.controlFactory();
        final TethysUIButtonFactory<?> myButtons = myFactory.buttonFactory();
        final TethysUILabel myLabel = myControls.newLabel(NLS_DATA);
        theSelectButton = myButtons.newScrollButton(PanelName.class);
        buildSelectMenu();

        /* Create the card panel */
        theCardPanel = myPanes.newCardPane();

        /* Add to the card panels */
        theCardPanel.addCard(PanelName.DEPOSITS.toString(), theDepositTable);
        theCardPanel.addCard(PanelName.CASH.toString(), theCashTable);
        theCardPanel.addCard(PanelName.LOANS.toString(), theLoanTable);
        theCardPanel.addCard(PanelName.PORTFOLIOS.toString(), thePortfolioTable);
        theCardPanel.addCard(PanelName.SECURITIES.toString(), theSecurityTable);
        theCardPanel.addCard(PanelName.PAYEES.toString(), thePayeeTable);
        theActive = PanelName.DEPOSITS;
        theSelectButton.setFixedText(theActive.toString());

        /* Create the new card panel */
        theFilterCardPanel = myPanes.newCardPane();

        /* Build the new card panel */
        theFilterCardPanel.addCard(PanelName.DEPOSITS.toString(), theDepositTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.CASH.toString(), theCashTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.LOANS.toString(), theLoanTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.PORTFOLIOS.toString(), thePortfolioTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.SECURITIES.toString(), theSecurityTable.getFilterPanel());
        theFilterCardPanel.addCard(PanelName.PAYEES.toString(), thePayeeTable.getFilterPanel());

        /* Create the select prompt */
        final TethysUIBoxPaneManager mySelect = myPanes.newHBoxPane();
        mySelect.addNode(myLabel);
        mySelect.addNode(theSelectButton);

        /* Create the selection panel */
        theSelectPanel = myPanes.newBorderPane();
        theSelectPanel.setBorderTitle(NLS_SELECT);
        theSelectPanel.setWest(mySelect);
        theSelectPanel.setCentre(theFilterCardPanel);

        /* Create the header panel */
        final TethysUIBorderPaneManager myHeader = myPanes.newBorderPane();
        myHeader.setCentre(theSelectPanel);
        myHeader.setNorth(theError);
        myHeader.setEast(theActionButtons);

        /* Now define the panel */
        thePanel.setNorth(myHeader);
        thePanel.setCentre(theCardPanel);

        /* Hide the action buttons initially */
        theActionButtons.setVisible(false);

        /* Create the listeners */
        theSelectButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleSelection());
        theError.getEventRegistrar().addEventListener(e -> handleErrorPane());
        theActionButtons.getEventRegistrar().addEventListener(this::handleActionButtons);
        setChildListeners(theDepositTable.getEventRegistrar());
        setChildListeners(theCashTable.getEventRegistrar());
        setChildListeners(theLoanTable.getEventRegistrar());
        setChildListeners(thePayeeTable.getEventRegistrar());
        setChildListeners(thePortfolioTable.getEventRegistrar());
        setChildListeners(theSecurityTable.getEventRegistrar());
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * setChildListeners.
     *
     * @param pRegistrar the registrar
     */
    private void setChildListeners(final OceanusEventRegistrar<PrometheusDataEvent> pRegistrar) {
        pRegistrar.addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> {
            if (!isRefreshing) {
                setVisibility();
            }
        });
        pRegistrar.addEventListener(PrometheusDataEvent.GOTOWINDOW, this::handleGoToEvent);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSelectButton.setEnabled(pEnabled);
        theCardPanel.setEnabled(pEnabled);
        theFilterCardPanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    /**
     * Build select menu.
     */
    private void buildSelectMenu() {
        /* Create builder */
        final TethysUIScrollMenu<PanelName> myMenu = theSelectButton.getMenu();

        /* Loop through the panels */
        for (PanelName myPanel : PanelName.values()) {
            /* Create a new JMenuItem for the panel */
            myMenu.addItem(myPanel);
        }
    }

    /**
     * Show locked accounts.
     *
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
     *
     * @throws OceanusException on error
     */
    public void refreshData() throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("Accounts");

        /* Note that we are refreshing */
        isRefreshing = true;
        theEditSet.setDataSet(theView.getData());

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
        theViewerEntry.setTreeObject(theEditSet);

        /* Complete the task */
        myTask.end();
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Switch on active component */
        switch (theActive) {
            case DEPOSITS:
                theDepositTable.determineFocus(theViewerEntry);
                break;
            case CASH:
                theCashTable.determineFocus(theViewerEntry);
                break;
            case LOANS:
                theLoanTable.determineFocus(theViewerEntry);
                break;
            case PORTFOLIOS:
                thePortfolioTable.determineFocus(theViewerEntry);
                break;
            case SECURITIES:
                theSecurityTable.determineFocus(theViewerEntry);
                break;
            case PAYEES:
                thePayeeTable.determineFocus(theViewerEntry);
                break;
            default:
                break;
        }
    }

    /**
     * Does this panel have updates?
     *
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
     *
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

        /* Return to caller */
        return hasSession;
    }

    /**
     * Does this panel have errors?
     *
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
     *
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
     *
     * @param pAccount the account to select
     */
    public void selectAccount(final MoneyWiseAssetBase pAccount) {
        /* Determine which panel to show */
        if (pAccount instanceof MoneyWiseDeposit myDeposit) {
            theDepositTable.selectDeposit(myDeposit);
            showPanel(PanelName.DEPOSITS);
        } else if (pAccount instanceof MoneyWiseCash myCash) {
            theCashTable.selectCash(myCash);
            showPanel(PanelName.CASH);
        } else if (pAccount instanceof MoneyWiseLoan myLoan) {
            theLoanTable.selectLoan(myLoan);
            showPanel(PanelName.LOANS);
        } else if (pAccount instanceof MoneyWisePortfolio myPortfolio) {
            thePortfolioTable.selectPortfolio(myPortfolio);
            showPanel(PanelName.PORTFOLIOS);
        } else if (pAccount instanceof MoneyWiseSecurity mySecurity) {
            theSecurityTable.selectSecurity(mySecurity);
            showPanel(PanelName.SECURITIES);
        } else if (pAccount instanceof MoneyWisePayee myPayee) {
            thePayeeTable.selectPayee(myPayee);
            showPanel(PanelName.PAYEES);
        }
    }

    /**
     * Show panel.
     *
     * @param pName the panel name
     */
    private void showPanel(final PanelName pName) {
        /* Obtain name of panel */
        final String myName = pName.toString();

        /* Move correct card to front */
        theCardPanel.selectCard(myName);
        theFilterCardPanel.selectCard(myName);

        /* Note the active panel */
        theActive = pName;
        theSelectButton.setFixedText(myName);

        /* Determine the focus */
        determineFocus();
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Determine whether we have updates */
        final boolean hasUpdates = hasUpdates();
        final boolean isItemEditing = isItemEditing();

        /* Update the action buttons */
        theActionButtons.setEnabled(true);
        theActionButtons.setVisible(hasUpdates && !isItemEditing);

        /* Update the selection */
        theSelectButton.setEnabled(!isItemEditing);
        theFilterCardPanel.setEnabled(!isItemEditing);

        /* Alert listeners that there has been a change */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * handleErrorPane.
     */
    private void handleErrorPane() {
        /* Determine whether we have an error */
        final boolean isError = theError.hasError();

        /* Hide selection panel on error */
        theSelectPanel.setVisible(!isError);

        /* Lock card panel */
        theCardPanel.setEnabled(!isError);

        /* Lock Action Buttons */
        theActionButtons.setEnabled(!isError);
    }

    /**
     * handleSelection.
     */
    private void handleSelection() {
        /* Cancel any editing */
        cancelEditing();

        /* Show selected panel */
        showPanel(theSelectButton.getValue());
    }

    /**
     * handle Action Buttons.
     *
     * @param pEvent the event
     */
    private void handleActionButtons(final OceanusEvent<PrometheusUIEvent> pEvent) {
        /* Cancel editing */
        cancelEditing();

        /* Perform the command */
        theEditSet.processCommand(pEvent.getEventId(), theError);
    }

    /**
     * handle GoTo Event.
     *
     * @param pEvent the event
     */
    private void handleGoToEvent(final OceanusEvent<PrometheusDataEvent> pEvent) {
        /* Access details */
        @SuppressWarnings("unchecked") final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = pEvent.getDetails(PrometheusGoToEvent.class);

        /* Access event and obtain details */
        switch (myEvent.getId()) {
            /* Pass through the event */
            case STATEMENT:
            case CATEGORY:
            case REGION:
            case TAG:
            case STATIC:
                theEventManager.cascadeEvent(pEvent);
                break;

            /* Access subPanels */
            case ACCOUNT:
                selectAccount(myEvent.getDetails(MoneyWiseAssetBase.class));
                break;
            default:
                break;
        }
    }

    /**
     * Panel names.
     */
    private enum PanelName {
        /**
         * Deposits.
         */
        DEPOSITS(MoneyWiseBasicDataType.DEPOSIT),

        /**
         * Cash.
         */
        CASH(MoneyWiseBasicDataType.CASH),

        /**
         * Loans.
         */
        LOANS(MoneyWiseBasicDataType.LOAN),

        /**
         * Portfolios.
         */
        PORTFOLIOS(MoneyWiseBasicDataType.PORTFOLIO),

        /**
         * Securities.
         */
        SECURITIES(MoneyWiseBasicDataType.SECURITY),

        /**
         * Payees.
         */
        PAYEES(MoneyWiseBasicDataType.PAYEE);

        /**
         * The String name.
         */
        private String theName;

        /**
         * Constructor.
         *
         * @param pDataType the dataType
         */
        PanelName(final MoneyWiseBasicDataType pDataType) {
            theName = pDataType.getListName();
        }

        @Override
        public String toString() {
            /* return the name */
            return theName;
        }
    }
}

