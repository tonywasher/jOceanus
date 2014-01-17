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
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jdatamodels.MainWindow;
import net.sourceforge.joceanus.jdateday.JDateDayRangeSelect;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.help.FinanceHelp;
import net.sourceforge.joceanus.jmoneywise.threads.FinanceStatus;
import net.sourceforge.joceanus.jmoneywise.threads.LoadArchive;
import net.sourceforge.joceanus.jmoneywise.threads.WriteQIF;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect.StatementSelect;
import net.sourceforge.joceanus.jmoneywise.ui.controls.ComboSelect;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnableTabbed;
import net.sourceforge.joceanus.jtethys.help.HelpException;
import net.sourceforge.joceanus.jtethys.help.HelpModule;
import net.sourceforge.joceanus.jthemis.svn.threads.SubversionBackup;
import net.sourceforge.joceanus.jthemis.svn.threads.SubversionRestore;

/**
 * Main Window for jMoneyWise.
 * @author Tony Washer
 */
public class MainTab
        extends MainWindow<MoneyWiseData> {
    /**
     * View Statement.
     */
    protected static final int ACTION_VIEWSTATEMENT = ActionEvent.ACTION_PERFORMED + 1;

    /**
     * View Register.
     */
    protected static final int ACTION_VIEWREGISTER = ACTION_VIEWSTATEMENT + 1;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MainTab.class.getName());

    /**
     * Report tab title.
     */
    private static final String TITLE_REPORT = NLS_BUNDLE.getString("TabReport");

    /**
     * Statement tab title.
     */
    private static final String TITLE_STATEMENT = NLS_BUNDLE.getString("TabStatement");

    /**
     * Register tab title.
     */
    private static final String TITLE_REGISTER = NLS_BUNDLE.getString("TabRegister");

    /**
     * SpotPrices tab title.
     */
    private static final String TITLE_SPOTVIEW = NLS_BUNDLE.getString("TabSpotPrices");

    /**
     * Maintenance tab title.
     */
    private static final String TITLE_MAINT = NLS_BUNDLE.getString("TabMaint");

    /**
     * QIF menu title.
     */
    private static final String MENU_CREATEQIF = NLS_BUNDLE.getString("MenuCreateQIF");

    /**
     * Archive menu title.
     */
    private static final String MENU_ARCHIVE = NLS_BUNDLE.getString("MenuArchive");

    /**
     * SubVersion menu title.
     */
    private static final String MENU_BACKUPSVN = NLS_BUNDLE.getString("MenuBackupSVN");

    /**
     * SubVersion menu title.
     */
    private static final String MENU_RESTORESVN = NLS_BUNDLE.getString("MenuRestoreSVN");

    /**
     * Program name.
     */
    private static final String PROGRAM_NAME = NLS_BUNDLE.getString("ProgramName");

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
     * The Register panel.
     */
    private Register theRegister = null;

    /**
     * The report panel.
     */
    private ReportTab theReportTab = null;

    /**
     * The analysis panel.
     */
    private AnalysisStatement theStatement = null;

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
    private JMenuItem theSVNBackup = null;

    /**
     * The SubversionRestore menu.
     */
    private JMenuItem theSVNRestore = null;

    /**
     * The CreateQIF menu.
     */
    private JMenuItem theCreateQIF = null;

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
        return PROGRAM_NAME;
    }

    @Override
    protected HelpModule getHelpModule() throws JOceanusException {
        try {
            return new FinanceHelp(theLogger);
        } catch (HelpException e) {
            throw new JOceanusException("Unable to load help", e);
        }
    }

    /**
     * Constructor.
     * @param pLogger the logger
     * @throws JOceanusException on error
     */
    public MainTab(final Logger pLogger) throws JOceanusException {
        /* Store the logger */
        theLogger = pLogger;

        /* Create the view */
        theView = new View(theLogger);

        /* Create the combo list */
        theComboList = new ComboSelect(theView);

        /* Build the main window */
        buildMainWindow(theView);
    }

    @Override
    protected JComponent buildMainPanel() throws JOceanusException {
        /* Create the Tabbed Pane */
        theTabs = new JEnableTabbed();

        /* Create the Report Tab */
        theReportTab = new ReportTab(theView);
        theTabs.addTab(TITLE_REPORT, theReportTab);

        /* Create the Analysis Tab */
        theStatement = new AnalysisStatement(theView);
        theTabs.addTab(TITLE_STATEMENT, theStatement.getPanel());

        /* Create the register table and add to tabbed pane */
        theRegister = new Register(theView, theComboList);
        theTabs.addTab(TITLE_REGISTER, theRegister.getPanel());

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
        theRegister.addChangeListener(myListener);
        theSpotView.addChangeListener(myListener);
        theMaint.addChangeListener(myListener);
        theRegister.addActionListener(myListener);
        theReportTab.addActionListener(myListener);
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
        theSVNBackup = new JMenuItem(MENU_BACKUPSVN);
        theSVNBackup.addActionListener(this);
        pMenu.add(theSVNBackup);

        /* Create the file menu items */
        theSVNRestore = new JMenuItem(MENU_RESTORESVN);
        theSVNRestore.addActionListener(this);
        pMenu.add(theSVNRestore);

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
        return theRegister.hasUpdates()
               || theSpotView.hasUpdates()
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
        } else if (theSVNBackup.equals(o)) {
            /* Start a write backup operation */
            backupSubversion();

            /* If this event relates to the Load spreadsheet item */
        } else if (theSVNRestore.equals(o)) {
            /* Start a restore backup operation */
            restoreSubversion();

            /* If this event relates to the Load spreadsheet item */
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
        FinanceStatus myStatus = new FinanceStatus(theView, getStatusBar());

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
        FinanceStatus myStatus = new FinanceStatus(theView, getStatusBar());

        /* Create the worker thread */
        SubversionBackup<MoneyWiseData> myThread = new SubversionBackup<MoneyWiseData>(myStatus, theView.getPreferenceMgr());
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Restore subversion.
     */
    public void restoreSubversion() {
        /* Allocate the status */
        FinanceStatus myStatus = new FinanceStatus(theView, getStatusBar());

        /* Create the worker thread */
        SubversionRestore<MoneyWiseData> myThread = new SubversionRestore<MoneyWiseData>(myStatus, theView.getPreferenceMgr());
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Create QIF file.
     */
    public void createQIF() {
        /* Allocate the status */
        FinanceStatus myStatus = new FinanceStatus(theView, getStatusBar());

        /* Create the worker thread */
        WriteQIF myThread = new WriteQIF(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Select a Statement.
     * @param pSelect the statement request
     */
    private void selectStatement(final StatementSelect pSelect) {
        /* Pass through to the Statement view */
        theStatement.selectStatement(pSelect);

        /* Goto the Accounts tab */
        gotoNamedTab(TITLE_STATEMENT);
    }

    /**
     * Select an explicit register period.
     * @param pSource the range
     */
    private void selectRegister(final JDateDayRangeSelect pSource) {
        /* Pass through to the Register */
        theRegister.selectPeriod(pSource);

        /* Goto the Register tab */
        gotoNamedTab(TITLE_REGISTER);
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

        /* Note whether we have data */
        boolean hasControl = theView.getData().getControl() != null;

        /* Disable menus if we have no data */
        theCreateQIF.setEnabled(!hasWorker
                                && hasControl);

        /* Access the Register panel and determine its status */
        int iIndex = theTabs.indexOfTab(TITLE_REGISTER);
        boolean showTab = !hasWorker
                          && (!hasUpdates || theRegister.hasUpdates());

        /* Enable/Disable the extract tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Access the Report panel */
        iIndex = theTabs.indexOfTab(TITLE_REPORT);
        showTab = !hasWorker
                  && !hasUpdates;

        /* Enable/Disable the reports tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Access the SpotView panel and determine its status */
        iIndex = theTabs.indexOfTab(TITLE_SPOTVIEW);
        showTab = !hasWorker
                  && (!hasUpdates || theSpotView.hasUpdates());

        /* Enable/Disable the spotView tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Access the Maintenance panel */
        iIndex = theTabs.indexOfTab(TITLE_MAINT);
        showTab = !hasWorker
                  && (!hasUpdates || theMaint.hasUpdates());

        /* Enable/Disable the maintenance tab */
        if (iIndex != -1) {
            theTabs.setEnabledAt(iIndex, showTab);
        }

        /* Enable/Disable the tabs */
        theTabs.setEnabled(!hasWorker);

        /* If we have updates disable the load backup/database option */
        theLoadSheet.setEnabled(!hasUpdates);
    }

    /**
     * Determine focus.
     */
    private void determineFocus() {
        /* Access the selected component */
        Component myComponent = theTabs.getSelectedComponent();

        /* If the selected component is register */
        if (myComponent.equals(theRegister.getPanel())) {
            /* Determine focus of register */
            theRegister.determineFocus();

            /* If the selected component is Statement */
        } else if (myComponent.equals(theStatement)) {
            /* Determine focus of Statement */
            theStatement.determineFocus();

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

    @Override
    protected void displayAbout() {
        /* Create a new AboutBox */
        AboutBox myAbout = new AboutBox(getFrame(), getFrameName(), theLogger);

        /* Show the box */
        myAbout.showDialog();
    }

    /**
     * The listener class.
     */
    private final class MainListener
            implements ActionListener, ChangeListener {
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
            } else if (theRegister.equals(o)
                       || theMaint.equals(o)
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
                switch (evt.getSubId()) {
                /* View the requested register */
                    case ACTION_VIEWREGISTER:
                        ActionRequest myReq = evt.getDetails(ActionRequest.class);
                        selectRegister(myReq.getRange());
                        break;

                    /* View the requested statement */
                    case ACTION_VIEWSTATEMENT:
                        StatementSelect mySelect = evt.getDetails(StatementSelect.class);
                        selectStatement(mySelect);
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
