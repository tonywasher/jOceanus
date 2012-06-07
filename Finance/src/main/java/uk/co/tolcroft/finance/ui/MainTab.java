/*******************************************************************************
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

public class MainTab extends MainWindow<FinanceData> implements ChangeListener {
    private View theView = null;
    private JTabbedPane theTabs = null;
    private Extract theExtract = null;
    private AccountTab theAccountCtl = null;
    private ReportTab theReportTab = null;
    private PricePoint theSpotView = null;
    private MaintenanceTab theMaint = null;
    private ComboSelect theComboList = null;
    private JMenuItem theLoadSheet = null;
    private JMenuItem theSVBackup = null;

    @Override
    public View getView() {
        return theView;
    }

    protected ComboSelect getComboList() {
        return theComboList;
    }

    /* Tab headers */
    private static final String titleExtract = "Extract";
    private static final String titleAccount = "Account";
    private static final String titleReport = "Report";
    private static final String titleSpotView = "SpotPrices";
    private static final String titleMaint = "Maintenance";

    /**
     * Obtain the frame name
     * @return the frame name
     */
    @Override
    protected String getFrameName() {
        return "Finance";
    }

    /**
     * Obtain the Help Module
     * @return the help module
     */
    @Override
    protected HelpModule getHelpModule() throws JDataException {
        try {
            return new FinanceHelp();
        } catch (HelpException e) {
            throw new JDataException(ExceptionClass.DATA, "Unable to load help", e);
        }
    }

    /* Constructor */
    public MainTab() throws JDataException {
        /* Create the view */
        theView = new View(this);

        /* Build the main window */
        buildMainWindow(theView);

        /* Initialise the data */
        refreshData();
    }

    /**
     * Build the main panel
     * @return the main panel
     */
    @Override
    protected JComponent buildMainPanel() {
        /* Create the Tabbed Pane */
        theTabs = new JTabbedPane();

        /* Create the extract table and add to tabbed pane */
        theExtract = new Extract(this);
        theTabs.addTab(titleExtract, theExtract.getPanel());

        /* Create the accounts control and add to tabbed pane */
        theAccountCtl = new AccountTab(this);
        theTabs.addTab(titleAccount, theAccountCtl.getPanel());

        /* Create the Report Tab */
        theReportTab = new ReportTab(this);
        theTabs.addTab(titleReport, theReportTab.getPanel());

        /* Create the SpotView Tab */
        theSpotView = new PricePoint(this);
        theTabs.addTab(titleSpotView, theSpotView.getPanel());

        /* Create the Maintenance Tab */
        theMaint = new MaintenanceTab(this);
        theTabs.addTab(titleMaint, theMaint.getPanel());

        /* Add change listener */
        theTabs.addChangeListener(this);
        determineFocus();

        /* Return the panel */
        return theTabs;
    }

    /**
     * Add Data Menu items
     * @param pMenu the menu
     */
    @Override
    protected void addDataMenuItems(JMenu pMenu) {
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
    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the Load spreadsheet item */
        if (o == theLoadSheet) {
            /* Start a write backup operation */
            loadSpreadsheet();
        }

        /* If this event relates to the Load spreadsheet item */
        else if (o == theSVBackup) {
            /* Start a write backup operation */
            backupSubversion();
        }

        /* else pass the event on */
        else
            super.actionPerformed(evt);
    }

    /* Load Spreadsheet */
    public void loadSpreadsheet() {
        /* Allocate the status */
        ThreadStatus<FinanceData> myStatus = new ThreadStatus<FinanceData>(theView, getStatusBar());

        /* Create the worker thread */
        LoadArchive myThread = new LoadArchive(myStatus);
        startThread(myThread);
    }

    /* Backup subversion */
    public void backupSubversion() {
        /* Allocate the status */
        ThreadStatus<FinanceData> myStatus = new ThreadStatus<FinanceData>(theView, getStatusBar());

        /* Create the worker thread */
        SubversionBackup<FinanceData> myThread = new SubversionBackup<FinanceData>(myStatus);
        startThread(myThread);
    }

    /* Select an explicit account and period */
    public void selectAccount(Account pAccount,
                              DateDayRangeSelect pSource) {
        /* Pass through to the Account control */
        theAccountCtl.selectAccount(pAccount, pSource);

        /* Goto the Accounts tab */
        gotoNamedTab(titleAccount);
    }

    /* Select an explicit extract period */
    public void selectPeriod(DateDayRangeSelect pSource) {
        /* Pass through to the Extract */
        theExtract.selectPeriod(pSource);

        /* Goto the Extract tab */
        gotoNamedTab(titleExtract);
    }

    /* Select an explicit account for maintenance */
    public void selectAccountMaint(Account pAccount) {
        /* Pass through to the Account control */
        theMaint.selectAccount(pAccount);

        /* Goto the Accounts tab */
        gotoNamedTab(titleMaint);
    }

    /* Goto the specific tab */
    public void gotoNamedTab(String pTabName) {
        /* Access the Named index */
        int iIndex = theTabs.indexOfTab(pTabName);

        /* Select the required tab */
        theTabs.setSelectedIndex(iIndex);
    }

    /* refresh data */
    public void refreshData() throws JDataException {
        /* Create the combo list */
        theComboList = new ComboSelect(theView);

        /* Refresh the windows */
        theExtract.refreshData();
        theAccountCtl.refreshData();
        theReportTab.refreshData();
        theSpotView.refreshData();
        theMaint.refreshData();

        /* Sort out visible tabs */
        setVisibility();
    }

    /* Set visibility */
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
        iIndex = theTabs.indexOfTab(titleExtract);
        showTab = (!hasWorker && (!hasUpdates || theExtract.hasUpdates()));

        /* Enable/Disable the extract tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, showTab);

        /* Access the AccountCtl panel and determine its status */
        iIndex = theTabs.indexOfTab(titleAccount);
        showTab = (!hasWorker && (!hasUpdates || theAccountCtl.hasUpdates()));

        /* Enable/Disable the account control tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, showTab);

        /* Access the Report panel */
        iIndex = theTabs.indexOfTab(titleReport);
        showTab = (!hasWorker && !hasUpdates);

        /* Enable/Disable the reports tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, showTab);

        /* Access the SpotView panel and determine its status */
        iIndex = theTabs.indexOfTab(titleSpotView);
        showTab = (!hasWorker && (!hasUpdates || theSpotView.hasUpdates()));

        /* Enable/Disable the spotView tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, showTab);

        /* Access the Maintenance panel */
        iIndex = theTabs.indexOfTab(titleMaint);
        showTab = (!hasWorker && (!hasUpdates || theMaint.hasUpdates()));

        /* Enable/Disable the maintenance tab */
        if (iIndex != -1)
            theTabs.setEnabledAt(iIndex, showTab);

        /* If we have updates disable the load backup/database option */
        theLoadSheet.setEnabled(!hasUpdates);
    }

    /* Change listener */
    @Override
    public void stateChanged(ChangeEvent e) {
        /* Ignore if it is not the tabs */
        if (e.getSource() != theTabs)
            return;

        /* Determine the focus */
        determineFocus();
    }

    /* Change listener */
    private void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is extract */
        if (myComponent == (Component) theExtract.getPanel()) {
            /* Set the debug focus */
            theExtract.getDataEntry().setFocus();
            theExtract.requestFocusInWindow();
        }

        /* If the selected component is account */
        else if (myComponent == (Component) theAccountCtl.getPanel()) {
            /* Determine focus of accounts */
            theAccountCtl.determineFocus();
        }

        /* If the selected component is SpotView */
        else if (myComponent == (Component) theSpotView.getPanel()) {
            /* Set the debug focus */
            theSpotView.getDataEntry().setFocus();
            theSpotView.requestFocusInWindow();
        }

        /* If the selected component is Maintenance */
        else if (myComponent == (Component) theMaint.getPanel()) {
            /* Determine focus of maintenance */
            theMaint.determineFocus();
        }
    }
}
