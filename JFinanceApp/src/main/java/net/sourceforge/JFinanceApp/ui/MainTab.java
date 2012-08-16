/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.MainWindow;
import net.sourceforge.JDataModels.threads.ThreadStatus;
import net.sourceforge.JDateDay.JDateDayRangeSelect;
import net.sourceforge.JEventManager.ActionDetailEvent;
import net.sourceforge.JFinanceApp.core.LoadArchive;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.help.FinanceHelp;
import net.sourceforge.JFinanceApp.ui.controls.ComboSelect;
import net.sourceforge.JFinanceApp.views.View;
import net.sourceforge.JHelpManager.HelpException;
import net.sourceforge.JHelpManager.HelpModule;
import net.sourceforge.JSvnManager.threads.SubversionBackup;

/**
 * Main Window for JFinanceApp.
 * @author Tony Washer
 */
public class MainTab extends MainWindow<FinanceData> {
    /**
     * Add pattern action event.
     */
    protected static final int ACTION_ADDPATTERN = ActionEvent.ACTION_PERFORMED + 1;

    /**
     * View Extract.
     */
    protected static final int ACTION_VIEWEXTRACT = ACTION_ADDPATTERN + 1;

    /**
     * View Account.
     */
    protected static final int ACTION_VIEWACCOUNT = ACTION_VIEWEXTRACT + 1;

    /**
     * Maintain Account.
     */
    protected static final int ACTION_MAINTACCOUNT = ACTION_VIEWACCOUNT + 1;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MainTab.class.getName());

    /**
     * Extract tab title.
     */
    private static final String TITLE_EXTRACT = NLS_BUNDLE.getString("TabExtract");

    /**
     * Account tab title.
     */
    private static final String TITLE_ACCOUNT = NLS_BUNDLE.getString("TabAccount");

    /**
     * Report tab title.
     */
    private static final String TITLE_REPORT = NLS_BUNDLE.getString("TabReport");

    /**
     * SpotPrices tab title.
     */
    private static final String TITLE_SPOTVIEW = NLS_BUNDLE.getString("TabSpotPrices");

    /**
     * Maintenance tab title.
     */
    private static final String TITLE_MAINT = NLS_BUNDLE.getString("TabMaint");

    /**
     * Maintenance tab title.
     */
    private static final String MENU_ARCHIVE = NLS_BUNDLE.getString("MenuArchive");

    /**
     * Maintenance tab title.
     */
    private static final String MENU_SUBVERSION = NLS_BUNDLE.getString("MenuSubVersion");

    /**
     * The data view.
     */
    private final View theView;

    /**
     * The tabs.
     */
    private JTabbedPane theTabs = null;

    /**
     * The Events panel.
     */
    private Extract theExtract = null;

    /**
     * The Account panel.
     */
    private AccountTab theAccountCtl = null;

    /**
     * The report panel.
     */
    private ReportTab theReportTab = null;

    /**
     * The SpotPricesPanel.
     */
    private PricePoint theSpotView = null;

    /**
     * The maintenance panel.
     */
    private MaintenanceTab theMaint = null;

    /**
     * The comboList.
     */
    private final ComboSelect theComboList;

    /**
     * The Load Sheet menus.
     */
    private JMenuItem theLoadSheet = null;

    /**
     * The SubversionBackup menu.
     */
    private JMenuItem theSVBackup = null;

    @Override
    public View getView() {
        return theView;
    }

    /**
     * Obtain the comboList.
     * @return the comboList
     */
    protected ComboSelect getComboList() {
        return theComboList;
    }

    /**
     * Obtain the frame name.
     * @return the frame name
     */
    @Override
    protected String getFrameName() {
        return "Finance";
    }

    @Override
    protected HelpModule getHelpModule() throws JDataException {
        try {
            return new FinanceHelp();
        } catch (HelpException e) {
            throw new JDataException(ExceptionClass.DATA, "Unable to load help", e);
        }
    }

    /**
     * Constructor.
     * @throws JDataException on error
     */
    public MainTab() throws JDataException {
        /* Create the view */
        theView = new View();

        /* Create the combo list */
        theComboList = new ComboSelect(theView);

        /* Build the main window */
        buildMainWindow(theView);
    }

    /**
     * Build the main panel.
     * @return the main panel
     */
    @Override
    protected JComponent buildMainPanel() {
        /* Create the Tabbed Pane */
        theTabs = new JTabbedPane();

        /* Create the extract table and add to tabbed pane */
        theExtract = new Extract(theView, theComboList);
        theTabs.addTab(TITLE_EXTRACT, theExtract.getPanel());

        /* Create the accounts control and add to tabbed pane */
        theAccountCtl = new AccountTab(theView, theComboList);
        theTabs.addTab(TITLE_ACCOUNT, theAccountCtl);

        /* Create the Report Tab */
        theReportTab = new ReportTab(theView);
        theTabs.addTab(TITLE_REPORT, theReportTab);

        /* Create the SpotView Tab */
        theSpotView = new PricePoint(theView);
        theTabs.addTab(TITLE_SPOTVIEW, theSpotView.getPanel());

        /* Create the Maintenance Tab */
        theMaint = new MaintenanceTab(this);
        theTabs.addTab(TITLE_MAINT, theMaint);

        /* Add listeners */
        MainListener myListener = new MainListener();
        theView.addChangeListener(myListener);
        theTabs.addChangeListener(myListener);
        theExtract.addChangeListener(myListener);
        theAccountCtl.addChangeListener(myListener);
        theSpotView.addChangeListener(myListener);
        theMaint.addChangeListener(myListener);
        theExtract.addActionListener(myListener);
        theAccountCtl.addActionListener(myListener);
        theMaint.addActionListener(myListener);
        determineFocus();

        /* Return the panel */
        return theTabs;
    }

    /**
     * Add Data Menu items.
     * @param pMenu the menu
     */
    @Override
    protected void addDataMenuItems(final JMenu pMenu) {
        /* Create the file menu items */
        theLoadSheet = new JMenuItem(MENU_ARCHIVE);
        theLoadSheet.addActionListener(this);
        pMenu.add(theLoadSheet);

        /* Create the file menu items */
        theSVBackup = new JMenuItem(MENU_SUBVERSION);
        theSVBackup.addActionListener(this);
        pMenu.add(theSVBackup);

        /* Pass call on */
        super.addDataMenuItems(pMenu);
    }

    @Override
    public final boolean hasUpdates() {
        /* Determine whether we have edit session updates */
        return theExtract.hasUpdates() || theAccountCtl.hasUpdates() || theSpotView.hasUpdates()
                || theMaint.hasUpdates();
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the Load spreadsheet item */
        if (theLoadSheet.equals(o)) {
            /* Start a write backup operation */
            loadSpreadsheet();

            /* If this event relates to the Load spreadsheet item */
        } else if (theSVBackup.equals(o)) {
            /* Start a write backup operation */
            backupSubversion();

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
        ThreadStatus<FinanceData> myStatus = new ThreadStatus<FinanceData>(theView, getStatusBar());

        /* Create the worker thread */
        LoadArchive myThread = new LoadArchive(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Backup subversion.
     */
    public void backupSubversion() {
        /* Allocate the status */
        ThreadStatus<FinanceData> myStatus = new ThreadStatus<FinanceData>(theView, getStatusBar());

        /* Create the worker thread */
        SubversionBackup<FinanceData> myThread = new SubversionBackup<FinanceData>(myStatus,
                theView.getPreferenceMgr());
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Select an explicit account and period.
     * @param pAccount the account
     * @param pSource the range
     */
    private void selectAccount(final Account pAccount,
                               final JDateDayRangeSelect pSource) {
        /* Pass through to the Account control */
        theAccountCtl.selectAccount(pAccount, pSource);

        /* Goto the Accounts tab */
        gotoNamedTab(TITLE_ACCOUNT);
    }

    /**
     * Select an explicit extract period.
     * @param pSource the range
     */
    private void selectExtract(final JDateDayRangeSelect pSource) {
        /* Pass through to the Extract */
        theExtract.selectPeriod(pSource);

        /* Goto the Extract tab */
        gotoNamedTab(TITLE_EXTRACT);
    }

    /**
     * Select an explicit account for maintenance.
     * @param pAccount the account
     */
    private void selectAccountMaint(final Account pAccount) {
        /* Pass through to the Account control */
        theMaint.selectAccount(pAccount);

        /* Goto the Accounts tab */
        gotoNamedTab(TITLE_MAINT);
    }

    /**
     * Add a pattern from an event.
     * @param pEvent the base event
     */
    private void addPattern(final Event pEvent) {
        /* Add the pattern */
        theAccountCtl.addPattern(pEvent);

        /* Change focus to the account */
        gotoNamedTab(TITLE_ACCOUNT);
    }

    /**
     * Goto the specific tab.
     * @param pTabName the tab name
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

        /* Determine whether we have any updates */
        boolean hasUpdates = hasUpdates();

        /* Note whether we have a worker thread */
        boolean hasWorker = hasWorker();

        /* Access the Extract panel and determine its status */
        int iIndex = theTabs.indexOfTab(TITLE_EXTRACT);
        boolean showTab = (!hasWorker && (!hasUpdates || theExtract.hasUpdates()));

        /* Enable/Disable the extract tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Access the AccountCtl panel and determine its status */
        iIndex = theTabs.indexOfTab(TITLE_ACCOUNT);
        showTab = (!hasWorker && (!hasUpdates || theAccountCtl.hasUpdates()));

        /* Enable/Disable the account control tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Access the Report panel */
        iIndex = theTabs.indexOfTab(TITLE_REPORT);
        showTab = (!hasWorker && !hasUpdates);

        /* Enable/Disable the reports tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Access the SpotView panel and determine its status */
        iIndex = theTabs.indexOfTab(TITLE_SPOTVIEW);
        showTab = (!hasWorker && (!hasUpdates || theSpotView.hasUpdates()));

        /* Enable/Disable the spotView tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Access the Maintenance panel */
        iIndex = theTabs.indexOfTab(TITLE_MAINT);
        showTab = (!hasWorker && (!hasUpdates || theMaint.hasUpdates()));

        /* Enable/Disable the maintenance tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* If we have updates disable the load backup/database option */
        theLoadSheet.setEnabled(!hasUpdates);
    }

    /**
     * Determine focus.
     */
    private void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is extract */
        if (myComponent.equals(theExtract.getPanel())) {
            /* Determine focus of extract */
            theExtract.determineFocus();

            /* If the selected component is account */
        } else if (myComponent.equals(theAccountCtl)) {
            /* Determine focus of accounts */
            theAccountCtl.determineFocus();

            /* If the selected component is SpotView */
        } else if (myComponent.equals(theSpotView.getPanel())) {
            /* Determine focus of SpotView */
            theSpotView.determineFocus();

            /* If the selected component is Maintenance */
        } else if (myComponent.equals(theMaint)) {
            /* Determine focus of maintenance */
            theMaint.determineFocus();
        }
    }

    /**
     * The listener class.
     */
    private final class MainListener implements ActionListener, ChangeListener {
        @Override
        public void stateChanged(final ChangeEvent e) {
            Object o = e.getSource();

            /* If this is the tabs */
            if (theTabs.equals(o)) {

                /* Determine the focus */
                determineFocus();

                /* If this is the data view */
            } else if (theView.equals(o)) {
                /* Set Visibility and refresh the combo list */
                setVisibility();
                theComboList.refreshData();

                /* else if it is one of the sub-panels */
            } else if (theExtract.equals(o) || theAccountCtl.equals(o) || theMaint.equals(o)
                    || theSpotView.equals(o)) {
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
                Object o = evt.getDetails();
                if (o instanceof ActionRequest) {
                    ActionRequest myReq = (ActionRequest) o;
                    switch (evt.getSubId()) {
                    /* Add the requested pattern */
                        case ACTION_ADDPATTERN:
                            selectAccount(myReq.getAccount(), myReq.getRange());
                            addPattern(myReq.getEvent());
                            break;

                        /* View the requested extract */
                        case ACTION_VIEWEXTRACT:
                            selectExtract(myReq.getRange());
                            break;

                        /* View the requested account */
                        case ACTION_VIEWACCOUNT:
                            selectAccount(myReq.getAccount(), myReq.getRange());
                            break;

                        /* Maintain the requested account */
                        case ACTION_MAINTACCOUNT:
                            selectAccountMaint(myReq.getAccount());
                            break;
                        default:
                            break;
                    }
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
        private final Account theAccount;

        /**
         * The selected range.
         */
        private final JDateDayRangeSelect theRange;

        /**
         * The base Event.
         */
        private final Event theEvent;

        /**
         * Obtain the selected account.
         * @return the account
         */
        protected Account getAccount() {
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
         * Obtain the selected event.
         * @return the event
         */
        protected Event getEvent() {
            return theEvent;
        }

        /**
         * Constructor.
         * @param pAccount the requested account
         */
        protected ActionRequest(final Account pAccount) {
            theAccount = pAccount;
            theRange = null;
            theEvent = null;
        }

        /**
         * Constructor.
         * @param pRange the requested range
         */
        protected ActionRequest(final JDateDayRangeSelect pRange) {
            theAccount = null;
            theRange = pRange;
            theEvent = null;
        }

        /**
         * Constructor.
         * @param pAccount the requested account
         * @param pRange the requested range
         */
        protected ActionRequest(final Account pAccount,
                                final JDateDayRangeSelect pRange) {
            theAccount = pAccount;
            theRange = pRange;
            theEvent = null;
        }

        /**
         * Constructor.
         * @param pAccount the requested account
         * @param pEvent the base event
         */
        protected ActionRequest(final Account pAccount,
                                final Event pEvent) {
            theAccount = pAccount;
            theRange = null;
            theEvent = pEvent;
        }
    }
}
