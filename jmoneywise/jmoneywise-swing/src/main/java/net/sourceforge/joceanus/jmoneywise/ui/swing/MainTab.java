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

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.help.MoneyWiseHelp;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.threads.swing.LoadArchive;
import net.sourceforge.joceanus.jmoneywise.threads.swing.MoneyWiseStatus;
import net.sourceforge.joceanus.jmoneywise.threads.swing.WriteQIF;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseAnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.swing.MoneyWiseIcons;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.ui.swing.MainWindow;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.help.TethysHelpModule;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabPaneManager.TethysSwingTabItem;

/**
 * Main Window for jMoneyWise.
 * @author Tony Washer
 */
public class MainTab
        extends MainWindow<MoneyWiseData, MoneyWiseDataType> {
    /**
     * View Statement.
     */
    public static final int ACTION_VIEWSTATEMENT = 1;

    /**
     * View Account.
     */
    public static final int ACTION_VIEWACCOUNT = ACTION_VIEWSTATEMENT + 1;

    /**
     * View Category.
     */
    public static final int ACTION_VIEWCATEGORY = ACTION_VIEWACCOUNT + 1;

    /**
     * View Tag.
     */
    public static final int ACTION_VIEWTAG = ACTION_VIEWCATEGORY + 1;

    /**
     * View TaxYear.
     */
    public static final int ACTION_VIEWTAXYEAR = ACTION_VIEWTAG + 1;

    /**
     * View Static.
     */
    public static final int ACTION_VIEWSTATIC = ACTION_VIEWTAXYEAR + 1;

    /**
     * Report tab title.
     */
    private static final String TITLE_REPORT = MoneyWiseUIResource.MAIN_REPORT.getValue();

    /**
     * Register tab title.
     */
    private static final String TITLE_REGISTER = MoneyWiseUIResource.MAIN_REGISTER.getValue();

    /**
     * SpotPrices tab title.
     */
    private static final String TITLE_SPOTPRICES = MoneyWiseUIResource.MAIN_SPOTPRICES.getValue();

    /**
     * SpotRates tab title.
     */
    private static final String TITLE_SPOTRATES = MoneyWiseUIResource.MAIN_SPOTRATES.getValue();

    /**
     * Maintenance tab title.
     */
    private static final String TITLE_MAINT = MoneyWiseUIResource.MAIN_MAINTENANCE.getValue();

    /**
     * QIF menu title.
     */
    private static final String MENU_CREATEQIF = MoneyWiseUIResource.MAIN_MENU_CREATEQIF.getValue();

    /**
     * Archive menu title.
     */
    private static final String MENU_ARCHIVE = MoneyWiseUIResource.MAIN_MENU_LOADARCHIVE.getValue();

    /**
     * Program name.
     */
    private static final String PROGRAM_NAME = ProgramResource.PROGRAM_NAME.getValue();

    /**
     * The data view.
     */
    private final SwingView theView;

    /**
     * The tabs.
     */
    private TethysSwingTabPaneManager theTabs;

    /**
     * The register panel.
     */
    private TransactionTable theRegister;

    /**
     * The SpotPricesPanel.
     */
    private SpotPricesTable theSpotPrices;

    /**
     * The SpotRatesPanel.
     */
    private SpotRatesTable theSpotRates;

    /**
     * The report panel.
     */
    private ReportTab theReports;

    /**
     * The maintenance panel.
     */
    private MaintenanceTab theMaint;

    /**
     * The Load Sheet menus.
     */
    private JMenuItem theLoadSheet;

    /**
     * The CreateQIF menu.
     */
    private JMenuItem theCreateQIF;

    /**
     * Constructor.
     * @param pProfile the startup profile
     * @throws OceanusException on error
     */
    public MainTab(final MetisProfile pProfile) throws OceanusException {
        /* Create the view */
        theView = new SwingView(pProfile);

        /* Build the main window */
        buildMainWindow(theView, theView.getUtilitySet());
    }

    @Override
    public SwingView getView() {
        return theView;
    }

    /**
     * Obtain the frame name.
     * @return the frame name
     */
    @Override
    protected String getFrameName() {
        return PROGRAM_NAME;
    }

    @Override
    protected TethysHelpModule getHelpModule() throws OceanusException {
        try {
            return new MoneyWiseHelp();
        } catch (OceanusException e) {
            throw new JMoneyWiseIOException("Unable to load help", e);
        }
    }

    @Override
    protected JComponent buildMainPanel() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("buildMain");

        /* Create the Tabbed Pane */
        theTabs = theView.getUtilitySet().getGuiFactory().newTabPane();

        /* Create the Report Tab */
        myTask.startTask("Report");
        theReports = new ReportTab(theView);
        theTabs.addTabItem(TITLE_REPORT, theReports);

        /* Create the Register Tab */
        myTask.startTask("Register");
        theRegister = new TransactionTable(theView);
        theTabs.addTabItem(TITLE_REGISTER, theRegister);

        /* Create the SpotPrices Tab */
        myTask.startTask("SpotPrices");
        theSpotPrices = new SpotPricesTable(theView);
        theTabs.addTabItem(TITLE_SPOTPRICES, theSpotPrices);

        /* Create the SpotRates Tab */
        myTask.startTask("SpotRates");
        theSpotRates = new SpotRatesTable(theView);
        theTabs.addTabItem(TITLE_SPOTRATES, theSpotRates);

        /* Create the Maintenance Tab */
        myTask.startTask("Maintenance");
        theMaint = new MaintenanceTab(this);
        theTabs.addTabItem(TITLE_MAINT, theMaint);

        /* Create listeners */
        theTabs.getEventRegistrar().addEventListener(e -> determineFocus());
        theView.getEventRegistrar().addEventListener(e -> setVisibility());
        theReports.getEventRegistrar().addEventListener(this::handleGoToEvent);
        theSpotPrices.getEventRegistrar().addEventListener(e -> setVisibility());
        theSpotRates.getEventRegistrar().addEventListener(e -> setVisibility());
        setChildListeners(theRegister.getEventRegistrar());
        setChildListeners(theMaint.getEventRegistrar());

        /* Create listener and initialise focus */
        determineFocus();

        /* Set the icon */
        getFrame().setIconImages(MoneyWiseIcons.getProgramImages());

        /* Complete task */
        myTask.end();

        /* Return the panel */
        return theTabs.getNode();
    }

    /**
     * setChildListeners.
     * @param pRegistrar the registrar
     */
    private void setChildListeners(final TethysEventRegistrar<PrometheusDataEvent> pRegistrar) {
        pRegistrar.addEventListener(PrometheusDataEvent.ADJUSTVISIBILITY, e -> setVisibility());
        pRegistrar.addEventListener(PrometheusDataEvent.GOTOWINDOW, this::handleGoToEvent);
    }

    /**
     * Add Data Menu items.
     * @param pMenu the menu
     */
    @Override
    protected void addDataMenuItems(final JMenu pMenu) {
        /* Create the file menu items */
        theLoadSheet = new JMenuItem(MENU_ARCHIVE);
        theLoadSheet.addActionListener(e -> loadSpreadsheet());
        pMenu.add(theLoadSheet);

        /* Create the file menu items */
        theCreateQIF = new JMenuItem(MENU_CREATEQIF);
        theCreateQIF.addActionListener(e -> createQIF());
        pMenu.add(theCreateQIF);

        /* Pass call on */
        super.addDataMenuItems(pMenu);
    }

    @Override
    public final boolean hasUpdates() {
        /* Determine whether we have edit session updates */
        boolean hasUpdates = theRegister.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theSpotPrices.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theSpotRates.hasUpdates();
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
            hasSession = theSpotPrices.hasSession();
        }
        if (!hasSession) {
            hasSession = theSpotRates.hasSession();
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
        /* Allocate the status */
        MoneyWiseStatus myStatus = new MoneyWiseStatus(theView, getStatusBar());

        /* Create the worker thread */
        LoadArchive myThread = new LoadArchive(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Create QIF file.
     */
    public void createQIF() {
        /* Allocate the status */
        MoneyWiseStatus myStatus = new MoneyWiseStatus(theView, getStatusBar());

        /* Create the worker thread */
        WriteQIF myThread = new WriteQIF(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Select a Statement.
     * @param pSelect the statement request
     */
    private void selectStatement(final StatementSelect<JComponent, Icon> pSelect) {
        /* Pass through to the Register view */
        theRegister.selectStatement(pSelect);

        /* Goto the Register tab */
        gotoNamedTab(TITLE_REGISTER);
    }

    /**
     * Select maintenance.
     * @param pEvent the action request
     */
    private void selectMaintenance(final PrometheusGoToEvent pEvent) {
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
        TethysTabItem<?, ?> myItem = theTabs.findItemByName(pTabName);
        if (myItem != null) {
            myItem.selectItem();
        }
    }

    @Override
    public final void setVisibility() {
        /* Sort out underlying visibility */
        super.setVisibility();

        /* Determine whether we have any session focus */
        boolean hasSession = hasSession();

        /* Note whether we have a worker thread */
        boolean hasWorker = hasWorker();

        /* Note whether we have data */
        boolean hasControl = theView.getData().getControl() != null;

        /* Disable menus if we have no data */
        theCreateQIF.setEnabled(!hasWorker && hasControl);

        /* Enable/Disable the reports tab */
        boolean doEnabled = !hasWorker && !hasSession;
        theTabs.enableItemByName(TITLE_REPORT, doEnabled);

        /* Enable/Disable the register tab */
        doEnabled = !hasWorker && (!hasSession || theRegister.hasSession());
        theTabs.enableItemByName(TITLE_REGISTER, doEnabled);

        /* Enable/Disable/Hide the spotPrices tab */
        doEnabled = !hasWorker && (!hasSession || theSpotPrices.hasSession());
        TethysSwingTabItem myItem = theTabs.findItemByName(TITLE_SPOTPRICES);
        myItem.setEnabled(doEnabled);
        myItem.setVisible(theView.hasActiveSecurities());

        /* Enable/Disable/Hide the spotRates tab */
        doEnabled = !hasWorker && (!hasSession || theSpotRates.hasSession());
        myItem = theTabs.findItemByName(TITLE_SPOTRATES);
        myItem.setEnabled(doEnabled);
        myItem.setVisible(theView.hasMultipleCurrencies());

        /* Enable/Disable the maintenance tab */
        doEnabled = !hasWorker && (!hasSession || theMaint.hasSession());
        theTabs.enableItemByName(TITLE_MAINT, doEnabled);

        /* Enable/Disable the tabs */
        theTabs.setEnabled(!hasWorker);

        /* If we have updates disable the load backup/database option */
        theLoadSheet.setEnabled(!hasSession);
    }

    /**
     * Determine focus.
     */
    private void determineFocus() {
        /* Access the selected component */
        TethysSwingTabItem myItem = theTabs.getSelectedTab();
        JComponent myComponent = myItem.getNode();

        /* If the selected component is Register */
        if (myComponent.equals(theRegister.getNode())) {
            /* Determine focus of Register */
            theRegister.determineFocus();

            /* If the selected component is SpotPrices */
        } else if (myComponent.equals(theSpotPrices.getNode())) {
            /* Determine focus of SpotPrices */
            theSpotPrices.determineFocus();

            /* If the selected component is SpotRates */
        } else if (myComponent.equals(theSpotRates.getNode())) {
            /* Determine focus of SpotRates */
            theSpotRates.determineFocus();

            /* If the selected component is Maintenance */
        } else if (myComponent.equals(theMaint.getNode())) {
            /* Determine focus of maintenance */
            theMaint.determineFocus();
        }
    }

    @Override
    protected void displayAbout() {
        /* Create a new AboutBox */
        AboutBox myAbout = new AboutBox(getFrame(), getFrameName());

        /* Show the box */
        myAbout.showDialog();
    }

    /**
     * handle GoTo Event.
     * @param pEvent the event
     */
    private void handleGoToEvent(final TethysEvent<PrometheusDataEvent> pEvent) {
        /* Access details */
        PrometheusGoToEvent myEvent = pEvent.getDetails(PrometheusGoToEvent.class);

        /* Access event and obtain details */
        switch (myEvent.getId()) {
            /* View the requested statement */
            case ACTION_VIEWSTATEMENT:
                @SuppressWarnings("unchecked")
                StatementSelect<JComponent, Icon> mySelect = myEvent.getDetails(StatementSelect.class);
                selectStatement(mySelect);
                break;

            /* Access maintenance */
            case ACTION_VIEWACCOUNT:
            case ACTION_VIEWTAXYEAR:
            case ACTION_VIEWCATEGORY:
            case ACTION_VIEWTAG:
            case ACTION_VIEWSTATIC:
                selectMaintenance(myEvent);
                break;
            default:
                break;
        }
    }
}
