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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.JDataProfile;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.AssetBase;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.help.MoneyWiseHelp;
import net.sourceforge.joceanus.jmoneywise.threads.LoadArchive;
import net.sourceforge.joceanus.jmoneywise.threads.MoneyWiseStatus;
import net.sourceforge.joceanus.jmoneywise.threads.WriteQIF;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.MainWindow;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnableTabbed;
import net.sourceforge.joceanus.jtethys.help.HelpException;
import net.sourceforge.joceanus.jtethys.help.HelpModule;

import org.slf4j.Logger;

/**
 * Main Window for jMoneyWise.
 * @author Tony Washer
 */
public class MainTab
        extends MainWindow<MoneyWiseData, MoneyWiseDataType> {
    /**
     * View Statement.
     */
    public static final int ACTION_VIEWSTATEMENT = ActionEvent.ACTION_PERFORMED + 1;

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
    private final View theView;

    /**
     * The logger.
     */
    private final Logger theLogger;

    /**
     * The tabs.
     */
    private JEnableTabbed theTabs = null;

    /**
     * The register panel.
     */
    private TransactionTable theRegister = null;

    /**
     * The SpotPricesPanel.
     */
    private SpotPricesTable theSpotPrices = null;

    /**
     * The SpotRatesPanel.
     */
    private SpotRatesTable theSpotRates = null;

    /**
     * The report panel.
     */
    private ReportTab theReports = null;

    /**
     * The maintenance panel.
     */
    private MaintenanceTab theMaint = null;

    /**
     * The Load Sheet menus.
     */
    private JMenuItem theLoadSheet = null;

    /**
     * The CreateQIF menu.
     */
    private JMenuItem theCreateQIF = null;

    @Override
    public View getView() {
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
    protected HelpModule getHelpModule() throws JOceanusException {
        try {
            return new MoneyWiseHelp(theLogger);
        } catch (HelpException e) {
            throw new JMoneyWiseIOException("Unable to load help", e);
        }
    }

    /**
     * Constructor.
     * @param pProfile
     * the startup profile
     * @param pLogger
     * the logger
     * @throws JOceanusException
     * on error
     */
    public MainTab(final JDataProfile pProfile,
                   final Logger pLogger) throws JOceanusException {
        /* Store the logger */
        theLogger = pLogger;

        /* Create the view */
        theView = new View(pProfile, theLogger);

        /* Build the main window */
        buildMainWindow(theView);
    }

    @Override
    protected JComponent buildMainPanel() throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = theView.getActiveTask();
        myTask = myTask.startTask("buildMain");

        /* Create the Tabbed Pane */
        theTabs = new JEnableTabbed();

        /* Create the Report Tab */
        myTask.startTask("Report");
        theReports = new ReportTab(theView);
        theTabs.addTab(TITLE_REPORT, theReports);

        /* Create the Register Tab */
        myTask.startTask("Register");
        theRegister = new TransactionTable(theView);
        theTabs.addTab(TITLE_REGISTER, theRegister.getPanel());

        /* Create the SpotPrices Tab */
        myTask.startTask("SpotPrices");
        theSpotPrices = new SpotPricesTable(theView);
        theTabs.addTab(TITLE_SPOTPRICES, theSpotPrices.getPanel());

        /* Create the SpotRates Tab */
        myTask.startTask("SpotRates");
        theSpotRates = new SpotRatesTable(theView);
        theTabs.addTab(TITLE_SPOTRATES, theSpotRates.getPanel());

        /* Create the Maintenance Tab */
        myTask.startTask("Maintenance");
        theMaint = new MaintenanceTab(this);
        theTabs.addTab(TITLE_MAINT, theMaint);

        /* Create listener and initialise focus */
        new MainListener();
        determineFocus();

        /* Set the icon */
        getFrame().setIconImages(MoneyWiseIcons.getProgramImages());

        /* Complete task */
        myTask.end();

        /* Return the panel */
        return theTabs;
    }

    /**
     * Add Data Menu items.
     * @param pMenu
     * the menu
     */
    @Override
    protected void addDataMenuItems(final JMenu pMenu) {
        /* Create the file menu items */
        theLoadSheet = new JMenuItem(MENU_ARCHIVE);
        theLoadSheet.addActionListener(this);
        pMenu.add(theLoadSheet);

        /* Create the file menu items */
        theCreateQIF = new JMenuItem(MENU_CREATEQIF);
        theCreateQIF.addActionListener(this);
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

    @Override
    public void actionPerformed(final ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the Load spreadsheet item */
        if (theLoadSheet.equals(o)) {
            /* Start a write backup operation */
            loadSpreadsheet();

            /* If this event relates to the Create QIF item */
        } else if (theCreateQIF.equals(o)) {
            /* Start a createQIF operation */
            createQIF();

            /* else pass the event on */
        } else {
            super.actionPerformed(evt);
        }
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
     * @param pSelect
     * the statement request
     */
    private void selectStatement(final StatementSelect pSelect) {
        /* Pass through to the Register view */
        theRegister.selectStatement(pSelect);

        /* Goto the Register tab */
        gotoNamedTab(TITLE_REGISTER);
    }

    /**
     * Select maintenance.
     * @param pEvent
     * the action request
     */
    private void selectMaintenance(final ActionDetailEvent pEvent) {
        /* Pass through to the Maintenance view */
        theMaint.selectMaintenance(pEvent);

        /* Goto the Maintenance tab */
        gotoNamedTab(TITLE_MAINT);
    }

    /**
     * Goto the specific tab.
     * @param pTabName
     * the tab name
     */
    private void gotoNamedTab(final String pTabName) {
        /* Access the Named index */
        int iIndex = theTabs.indexOfTab(pTabName);

        /* Select the required tab */
        theTabs.setSelectedIndex(iIndex);
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

        /* Enable/Disable the register tab */
        int iIndex = theTabs.indexOfTab(TITLE_REGISTER);
        if (iIndex != -1) {
            boolean doEnabled = !hasWorker && (!hasSession || theRegister.hasSession());
            if (doEnabled != theTabs.isEnabledAt(iIndex)) {
                theTabs.setEnabledAt(iIndex, doEnabled);
            }
        }

        /* Enable/Disable the reports tab */
        iIndex = theTabs.indexOfTab(TITLE_REPORT);
        if (iIndex != -1) {
            boolean doEnabled = !hasWorker && !hasSession;
            if (doEnabled != theTabs.isEnabledAt(iIndex)) {
                theTabs.setEnabledAt(iIndex, doEnabled);
            }
        }

        /* Enable/Disable the spotPrices tab */
        iIndex = theTabs.indexOfTab(TITLE_SPOTPRICES);
        if (iIndex != -1) {
            boolean doEnabled = !hasWorker && (!hasSession || theSpotPrices.hasSession());
            if (doEnabled != theTabs.isEnabledAt(iIndex)) {
                theTabs.setEnabledAt(iIndex, doEnabled);
            }
        }

        /* Enable/Disable the spotRates tab */
        iIndex = theTabs.indexOfTab(TITLE_SPOTRATES);
        if (iIndex != -1) {
            boolean doEnabled = !hasWorker && (!hasSession || theSpotRates.hasSession());
            if (doEnabled != theTabs.isEnabledAt(iIndex)) {
                theTabs.setEnabledAt(iIndex, doEnabled);
            }
        }

        /* Enable/Disable the maintenance tab */
        iIndex = theTabs.indexOfTab(TITLE_MAINT);
        if (iIndex != -1) {
            boolean doEnabled = !hasWorker && (!hasSession || theMaint.hasSession());
            if (doEnabled != theTabs.isEnabledAt(iIndex)) {
                theTabs.setEnabledAt(iIndex, doEnabled);
            }
        }

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
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is Register */
        if (myComponent.equals(theRegister)) {
            /* Determine focus of Register */
            theRegister.determineFocus();

            /* If the selected component is SpotPrices */
        } else if (myComponent.equals(theSpotPrices.getPanel())) {
            /* Determine focus of SpotPrices */
            theSpotPrices.determineFocus();

            /* If the selected component is SpotRates */
        } else if (myComponent.equals(theSpotRates.getPanel())) {
            /* Determine focus of SpotRates */
            theSpotRates.determineFocus();

            /* If the selected component is Maintenance */
        } else if (myComponent.equals(theMaint)) {
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
     * The listener class.
     */
    private final class MainListener
            implements ActionListener, ChangeListener {
        /**
         * Constructor.
         */
        private MainListener() {
            theView.addChangeListener(this);
            theTabs.addChangeListener(this);
            theRegister.addChangeListener(this);
            theSpotPrices.addChangeListener(this);
            theSpotRates.addChangeListener(this);
            theMaint.addChangeListener(this);
            theRegister.addActionListener(this);
            theReports.addActionListener(this);
            theMaint.addActionListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent e) {
            Object o = e.getSource();

            /* If this is the tabs */
            if (theTabs.equals(o)) {

                /* Determine the focus */
                determineFocus();

                /* If this is the data view */
            } else if (theView.equals(o)) {
                /* Set Visibility */
                setVisibility();

                /* else if it is one of the sub-panels */
            } else if (theRegister.equals(o)
                       || theMaint.equals(o)
                       || theSpotPrices.equals(o)
                       || theSpotRates.equals(o)) {
                /* Set the visibility */
                setVisibility();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If this is an ActionDetailEvent */
            if (e instanceof ActionDetailEvent) {
                /* Access event and obtain details */
                ActionDetailEvent evt = (ActionDetailEvent) e;
                switch (evt.getSubId()) {
                /* View the requested statement */
                    case ACTION_VIEWSTATEMENT:
                        StatementSelect mySelect = evt.getDetails(StatementSelect.class);
                        selectStatement(mySelect);
                        break;

                    /* Access maintenance */
                    case ACTION_VIEWACCOUNT:
                    case ACTION_VIEWTAXYEAR:
                    case ACTION_VIEWCATEGORY:
                    case ACTION_VIEWTAG:
                    case ACTION_VIEWSTATIC:
                        selectMaintenance(evt);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Account and period request.
     */
    protected static final class ActionRequest {
        /**
         * The account.
         */
        private final AssetBase<?> theAccount;

        /**
         * The selected range.
         */
        private final JDateDayRangeSelect theRange;

        /**
         * The base Event.
         */
        private final Transaction theTransaction;

        /**
         * Obtain the selected account.
         * @return the account
         */
        protected AssetBase<?> getAccount() {
            return theAccount;
        }

        /**
         * Obtain the selected range.
         * @return the range
         */
        protected JDateDayRangeSelect getRange() {
            return theRange;
        }

        /**
         * Obtain the selected transaction.
         * @return the event
         */
        protected Transaction getTransaction() {
            return theTransaction;
        }

        /**
         * Constructor.
         * @param pAccount
         * the requested account
         */
        protected ActionRequest(final AssetBase<?> pAccount) {
            theAccount = pAccount;
            theRange = null;
            theTransaction = null;
        }

        /**
         * Constructor.
         * @param pRange
         * the requested range
         */
        protected ActionRequest(final JDateDayRangeSelect pRange) {
            theAccount = null;
            theRange = pRange;
            theTransaction = null;
        }

        /**
         * Constructor.
         * @param pAccount
         * the requested account
         * @param pRange
         * the requested range
         */
        protected ActionRequest(final AssetBase<?> pAccount,
                                final JDateDayRangeSelect pRange) {
            theAccount = pAccount;
            theRange = pRange;
            theTransaction = null;
        }

        /**
         * Constructor.
         * @param pAccount
         * the requested account
         * @param pTrans
         * the base transaction
         */
        protected ActionRequest(final AssetBase<?> pAccount,
                                final Transaction pTrans) {
            theAccount = pAccount;
            theRange = null;
            theTransaction = pTrans;
        }
    }
}
