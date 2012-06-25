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
package uk.co.tolcroft.finance.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDateDay.DateDayRangeSelect;
import net.sourceforge.JHelpManager.HelpException;
import net.sourceforge.JHelpManager.HelpModule;
import uk.co.tolcroft.finance.core.LoadArchive;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.help.FinanceHelp;
import uk.co.tolcroft.finance.ui.controls.ComboSelect;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.MainWindow;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.subversion.threads.SubversionBackup;

/**
 * Main Window for JFinanceApp.
 * @author Tony Washer
 */
public class MainTab extends MainWindow<FinanceData> implements ChangeListener {
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
    private ComboSelect theComboList = null;

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
     * Extract tab title.
     */
    private static final String TITLE_EXTRACT = "Extract";

    /**
     * Account tab title.
     */
    private static final String TITLE_ACCOUNT = "Account";

    /**
     * Report tab title.
     */
    private static final String TITLE_REPORT = "Report";

    /**
     * SpotPrices tab title.
     */
    private static final String TITLE_SPOTVIEW = "SpotPrices";

    /**
     * Maintenance tab title.
     */
    private static final String TITLE_MAINT = "Maintenance";

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
        theView = new View(this);

        /* Build the main window */
        buildMainWindow(theView);

        /* Initialise the data */
        refreshData();
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
        theExtract = new Extract(this);
        theTabs.addTab(TITLE_EXTRACT, theExtract.getPanel());

        /* Create the accounts control and add to tabbed pane */
        theAccountCtl = new AccountTab(theView);
        theTabs.addTab(TITLE_ACCOUNT, theAccountCtl);

        /* Create the Report Tab */
        theReportTab = new ReportTab(theView);
        theTabs.addTab(TITLE_REPORT, theReportTab);

        /* Create the SpotView Tab */
        theSpotView = new PricePoint(this);
        theTabs.addTab(TITLE_SPOTVIEW, theSpotView.getPanel());

        /* Create the Maintenance Tab */
        theMaint = new MaintenanceTab(this);
        theTabs.addTab(TITLE_MAINT, theMaint);

        /* Add change listener */
        theTabs.addChangeListener(this);
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
        theLoadSheet = new JMenuItem("Load Spreadsheet");
        theLoadSheet.addActionListener(this);
        pMenu.add(theLoadSheet);

        /* Create the file menu items */
        theSVBackup = new JMenuItem("Backup SubVersion");
        theSVBackup.addActionListener(this);
        pMenu.add(theSVBackup);

        /* Pass call on */
        super.addDataMenuItems(pMenu);
    }

    @Override
    public boolean hasUpdates() {
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
        SubversionBackup<FinanceData> myThread = new SubversionBackup<FinanceData>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Select an explicit account and period.
     * @param pAccount the account
     * @param pSource the range
     */
    public void selectAccount(final Account pAccount,
                              final DateDayRangeSelect pSource) {
        /* Pass through to the Account control */
        theAccountCtl.selectAccount(pAccount, pSource);

        /* Goto the Accounts tab */
        gotoNamedTab(TITLE_ACCOUNT);
    }

    /**
     * Select an explicit extract period.
     * @param pSource the range
     */
    public void selectPeriod(final DateDayRangeSelect pSource) {
        /* Pass through to the Extract */
        theExtract.selectPeriod(pSource);

        /* Goto the Extract tab */
        gotoNamedTab(TITLE_EXTRACT);
    }

    /**
     * Select an explicit account for maintenance.
     * @param pAccount the account
     */
    public void selectAccountMaint(final Account pAccount) {
        /* Pass through to the Account control */
        theMaint.selectAccount(pAccount);

        /* Goto the Accounts tab */
        gotoNamedTab(TITLE_MAINT);
    }

    /**
     * Goto the specific tab.
     * @param pTabName the tab name
     */
    public void gotoNamedTab(final String pTabName) {
        /* Access the Named index */
        int iIndex = theTabs.indexOfTab(pTabName);

        /* Select the required tab */
        theTabs.setSelectedIndex(iIndex);
    }

    /**
     * refresh data.
     * @throws JDataException on error
     */
    public void refreshData() throws JDataException {
        /* Create the combo list */
        theComboList = new ComboSelect(theView);

        /* Refresh the windows */
        theExtract.refreshData();
        theAccountCtl.refreshData(theComboList);
        theReportTab.refreshData();
        theSpotView.refreshData();
        theMaint.refreshData();

        /* Sort out visible tabs */
        setVisibility();
    }

    @Override
    public void setVisibility() {
        int iIndex;
        boolean hasUpdates;
        boolean hasWorker;
        boolean showTab;

        /* Sort out underlying visibility */
        super.setVisibility();

        /* Determine whether we have any updates */
        hasUpdates = hasUpdates();

        /* Note whether we have a worker thread */
        hasWorker = hasWorker();

        /* Access the Extract panel and determine its status */
        iIndex = theTabs.indexOfTab(TITLE_EXTRACT);
        showTab = (!hasWorker && (!hasUpdates || theExtract.hasUpdates()));

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
     * Change listener.
     * @param e the event
     */
    @Override
    public void stateChanged(final ChangeEvent e) {
        /* Ignore if it is not the tabs */
        if (!theTabs.equals(e.getSource())) {
            return;
        }

        /* Determine the focus */
        determineFocus();
    }

    /**
     * Determine focus.
     */
    private void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is extract */
        if (myComponent.equals(theExtract.getPanel())) {
            /* Set the debug focus */
            // theExtract.getDataEntry().setFocus();
            theExtract.requestFocusInWindow();

            /* If the selected component is account */
        } else if (myComponent.equals(theAccountCtl)) {
            /* Determine focus of accounts */
            theAccountCtl.determineFocus();

            /* If the selected component is SpotView */
        } else if (myComponent.equals(theSpotView.getPanel())) {
            /* Set the debug focus */
            // theSpotView.getDataEntry().setFocus();
            theSpotView.requestFocusInWindow();

            /* If the selected component is Maintenance */
        } else if (myComponent.equals(theMaint)) {
            /* Determine focus of maintenance */
            theMaint.determineFocus();
        }
    }
}
