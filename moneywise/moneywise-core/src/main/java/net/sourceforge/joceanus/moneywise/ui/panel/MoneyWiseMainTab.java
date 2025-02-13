/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.ui.panel;

import net.sourceforge.joceanus.metis.help.MetisHelpModule;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseIOException;
import net.sourceforge.joceanus.moneywise.lethe.ui.panel.MoneyWiseReportTab;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.moneywise.threads.MoneyWiseThreadLoadArchive;
import net.sourceforge.joceanus.moneywise.threads.MoneyWiseThreadWriteQIF;
import net.sourceforge.joceanus.moneywise.lethe.ui.controls.MoneyWiseAnalysisSelect.MoneyWiseStatementSelect;
import net.sourceforge.joceanus.moneywise.lethe.ui.panel.MoneyWiseTransactionTable.MoneyWiseStatementPanel;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.moneywise.help.MoneyWiseHelp;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseGoToId;
import net.sourceforge.joceanus.moneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.moneywise.threads.MoneyWiseThreadId;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.prometheus.ui.panel.PrometheusMainWindow;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIAboutBox;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.factory.TethysUILogTextArea;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIMainPanel;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIMenuBarManager;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIMenuBarManager.TethysUIMenuSubMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUITabPaneManager;
import net.sourceforge.joceanus.tethys.api.pane.TethysUITabPaneManager.TethysUITabItem;

/**
 * Main Window for MoneyWise.
 */
public class MoneyWiseMainTab
        extends PrometheusMainWindow
        implements TethysUIMainPanel {
    /**
     * Report tab title.
     */
    private static final String TITLE_REPORT = MoneyWiseUIResource.MAIN_REPORT.getValue();

    /**
     * Register tab title.
     */
    private static final String TITLE_REGISTER = MoneyWiseUIResource.MAIN_REGISTER.getValue();

    /**
     * Market tab title.
     */
    private static final String TITLE_MARKET = MoneyWiseUIResource.MAIN_MARKET.getValue();

    /**
     * Maintenance tab title.
     */
    private static final String TITLE_MAINT = MoneyWiseUIResource.MAIN_MAINTENANCE.getValue();

    /**
     * The data view.
     */
    private final MoneyWiseView theView;

    /**
     * The tabs.
     */
    private TethysUITabPaneManager theTabs;

    /**
     * The register panel.
     */
    private MoneyWiseStatementPanel theRegister;

    /**
     * The MarketPanel.
     */
    private MoneyWiseMarketTabs theMarket;

    /**
     * The maintenance panel.
     */
    private MoneyWiseMaintenance theMaint;

    /**
     * The aboutBox.
     */
    private TethysUIAboutBox theAboutBox;

    /**
     * Constructor.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    public MoneyWiseMainTab(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Create prometheus toolkit */
        final PrometheusToolkit myToolkit = new PrometheusToolkit(pFactory);

        /* create the view */
        theView = new MoneyWiseView(myToolkit, new MoneyWiseUKTaxYearCache());

        /* Build the main window */
        buildMainWindow(theView, myToolkit);

        /* Initialise visibility */
        setVisibility();
    }

    @Override
    public MoneyWiseView getView() {
        return theView;
    }

    @Override
    protected MetisHelpModule getHelpModule() throws OceanusException {
        try {
            return new MoneyWiseHelp();
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Unable to load help", e);
        }
    }

    @Override
    protected TethysUIComponent buildMainPanel() throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("buildMain");

        /* Create the Tabbed Pane */
        final TethysUIFactory<?> myFactory = theView.getGuiFactory();
        theTabs = myFactory.paneFactory().newTabPane();

        /* Create the Report Tab */
        myTask.startTask(TITLE_REPORT);
        final MoneyWiseReportTab myReports = new MoneyWiseReportTab(theView);
        theTabs.addTabItem(TITLE_REPORT, myReports);

        /* Create the Register Tab */
        myTask.startTask(TITLE_REGISTER);
        theRegister = new MoneyWiseStatementPanel(theView);
        theTabs.addTabItem(TITLE_REGISTER, theRegister);

        /* Create the Market Tab */
        myTask.startTask(TITLE_MARKET);
        theMarket = new MoneyWiseMarketTabs(theView);
        theTabs.addTabItem(TITLE_MARKET, theMarket);

        /* Create the Maintenance Tab */
        myTask.startTask(TITLE_MAINT);
        theMaint = new MoneyWiseMaintenance(theView);
        theTabs.addTabItem(TITLE_MAINT, theMaint);

        /* Create the log tab */
        final TethysUILogTextArea myLog = myFactory.getLogSink();
        final TethysUITabItem myLogTab = theTabs.addTabItem(MoneyWiseUIResource.MAIN_LOG.getValue(), myLog);
        myLog.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> myLogTab.setVisible(true));
        myLog.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> myLogTab.setVisible(false));
        myLogTab.setVisible(myLog.isActive());

        /* Create listeners */
        theTabs.getEventRegistrar().addEventListener(e -> determineFocus());
        theView.getEventRegistrar().addEventListener(e -> setVisibility());
        myReports.getEventRegistrar().addEventListener(this::handleGoToEvent);
        theMarket.getEventRegistrar().addEventListener(e -> setVisibility());
        setChildListeners(theRegister.getEventRegistrar());
        setChildListeners(theMaint.getEventRegistrar());

        /* Create listener and initialise focus */
        determineFocus();

        /* Complete task */
        myTask.end();

        /* Return the panel */
        return theTabs;
    }

    /**
     * setChildListeners.
     * @param pRegistrar the registrar
     */
    private void setChildListeners(final OceanusEventRegistrar<PrometheusDataEvent> pRegistrar) {
        pRegistrar.addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> setVisibility());
        pRegistrar.addEventListener(PrometheusDataEvent.GOTOWINDOW, this::handleGoToEvent);
    }

    /**
     * Add Data Menu items.
     * @param pMenu the menu
     */
    @Override
    protected void addDataMenuItems(final TethysUIMenuSubMenu pMenu) {
        /* Create the data menu items */
        pMenu.newMenuItem(MoneyWiseThreadId.LOADARCHIVE, e -> loadSpreadsheet());
        pMenu.newMenuItem(MoneyWiseThreadId.CREATEQIF, e -> createQIF());

        /* Pass call on */
        super.addDataMenuItems(pMenu);
    }

    @Override
    public final boolean hasUpdates() {
        /* Determine whether we have edit session updates */
        boolean hasUpdates = theRegister.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theMarket.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theMaint.hasUpdates();
        }
        return hasUpdates;
    }

    /**
     * Has this set of panels got the session focus?
     * @return true/false
     */
    public final boolean hasSession() {
        /* Determine whether we have edit session updates */
        boolean hasSession = theRegister.hasSession();
        if (!hasSession) {
            hasSession = theMarket.hasSession();
        }
        if (!hasSession) {
            hasSession = theMaint.hasSession();
        }
        return hasSession;
    }

    /**
     * Load Spreadsheet.
     */
    public void loadSpreadsheet() {
        /* Create the worker thread */
        final MoneyWiseThreadLoadArchive myThread = new MoneyWiseThreadLoadArchive(theView);
        startThread(myThread);
    }

    /**
     * Create QIF file.
     */
    public void createQIF() {
        /* Create the worker thread */
        final MoneyWiseThreadWriteQIF myThread = new MoneyWiseThreadWriteQIF(theView);
        startThread(myThread);
    }

    /**
     * Select a Statement.
     * @param pSelect the statement request
     */
    private void selectStatement(final MoneyWiseStatementSelect pSelect) {
        /* Pass through to the Register view */
        theRegister.selectStatement(pSelect);

        /* Goto the Register tab */
        gotoNamedTab(TITLE_REGISTER);
    }

    /**
     * Select maintenance.
     * @param pEvent the action request
     */
    private void selectMaintenance(final PrometheusGoToEvent<MoneyWiseGoToId> pEvent) {
        /* Pass through to the Maintenance view */
        theMaint.selectMaintenance(pEvent);

        /* Goto the Maintenance tab */
        gotoNamedTab(TITLE_MAINT);
    }

    /**
     * Goto the specific tab.
     * @param pTabName the tab name
     */
    private void gotoNamedTab(final String pTabName) {
        /* Look up item and select it */
        final TethysUITabItem myItem = theTabs.findItemByName(pTabName);
        if (myItem != null) {
            myItem.selectItem();
        }
    }

    @Override
    public final void setVisibility() {
        /* Sort out underlying visibility */
        super.setVisibility();

        /* Determine whether we have any session focus */
        final boolean hasSession = hasSession();

        /* Note whether we have a worker thread */
        final boolean hasWorker = hasWorker();

        /* Note whether we have data */
        final boolean hasControl = theView.getData().getControl() != null;

        /* Obtain the menuBar */
        final TethysUIMenuBarManager myMenuBar = getMenuBar();

        /* Disable menus if we have no data */
        myMenuBar.setEnabled(MoneyWiseThreadId.CREATEQIF, !hasWorker && hasControl);

        /* Enable/Disable the reports tab */
        boolean doEnabled = !hasWorker && !hasSession;
        theTabs.enableItemByName(TITLE_REPORT, doEnabled);

        /* Enable/Disable the register tab */
        doEnabled = !hasWorker && (!hasSession || theRegister.hasSession());
        theTabs.enableItemByName(TITLE_REGISTER, doEnabled);

        /* Enable/Disable/Hide the spotPrices tab */
        doEnabled = !hasWorker && (!hasSession || theMarket.hasSession());
        final TethysUITabItem myItem = theTabs.findItemByName(TITLE_MARKET);
        myItem.setEnabled(doEnabled);
        myItem.setVisible(theView.hasActiveSecurities() || theView.hasMultipleCurrencies());

        /* Enable/Disable the maintenance tab */
        doEnabled = !hasWorker && (!hasSession || theMaint.hasSession());
        theTabs.enableItemByName(TITLE_MAINT, doEnabled);

        /* Enable/Disable the tabs */
        theTabs.setEnabled(!hasWorker);

        /* If we have updates disable the load backup/database option */
        myMenuBar.setEnabled(MoneyWiseThreadId.LOADARCHIVE, !hasSession);
    }

    /**
     * Determine focus.
     */
    private void determineFocus() {
        /* Access the selected component */
        final TethysUITabItem myItem = theTabs.getSelectedTab();
        final Integer myId = myItem.getId();

        /* If the selected component is Register */
        if (myId.equals(theRegister.getId())) {
            /* Determine focus of Register */
            theRegister.determineFocus();

            /* If the selected component is Market */
        } else if (myId.equals(theMarket.getId())) {
            /* Determine focus of Market */
            theMarket.determineFocus();

            /* If the selected component is Maintenance */
        } else if (myId.equals(theMaint.getId())) {
            /* Determine focus of maintenance */
            theMaint.determineFocus();
        }
    }

    @Override
    protected void displayAbout() {
        /* Create about box if it does not exist */
        if (theAboutBox == null) {
            theAboutBox = theView.getGuiFactory().dialogFactory().newAboutBox();
        }
        theAboutBox.showDialog();
    }

    /**
     * handle GoTo Event.
     * @param pEvent the event
     */
    private void handleGoToEvent(final OceanusEvent<PrometheusDataEvent> pEvent) {
        /* Access details */
        @SuppressWarnings("unchecked")
        final PrometheusGoToEvent<MoneyWiseGoToId> myEvent = pEvent.getDetails(PrometheusGoToEvent.class);

        /* Access event and obtain details */
        switch (myEvent.getId()) {
            /* View the requested statement */
            case STATEMENT:
                final MoneyWiseStatementSelect mySelect = myEvent.getDetails(MoneyWiseStatementSelect.class);
                selectStatement(mySelect);
                break;

            /* Access maintenance */
            case ACCOUNT:
            case CATEGORY:
            case REGION:
            case TAG:
            case STATIC:
                selectMaintenance(myEvent);
                break;
            default:
                break;
        }
    }
}
