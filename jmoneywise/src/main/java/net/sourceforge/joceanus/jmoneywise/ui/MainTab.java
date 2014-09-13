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
import net.sourceforge.joceanus.jtethys.resource.ResourceMgr;
import net.sourceforge.joceanus.jthemis.svn.threads.SubversionBackup;
import net.sourceforge.joceanus.jthemis.svn.threads.SubversionRestore;

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
     * View Register.
     */
    public static final int ACTION_VIEWREGISTER = ACTION_VIEWSTATEMENT + 1;

    /**
     * View Account.
     */
    public static final int ACTION_VIEWACCOUNT = ACTION_VIEWREGISTER + 1;

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
    private static final String TITLE_REPORT = ResourceMgr.getString(MoneyWiseUIResource.MAIN_REPORT);

    /**
     * Statement tab title.
     */
    private static final String TITLE_STATEMENT = ResourceMgr.getString(MoneyWiseUIResource.MAIN_STATEMENT);

    /**
     * Register tab title.
     */
    private static final String TITLE_REGISTER = ResourceMgr.getString(MoneyWiseUIResource.MAIN_REGISTER);

    /**
     * SpotPrices tab title.
     */
    private static final String TITLE_SPOTVIEW = ResourceMgr.getString(MoneyWiseUIResource.MAIN_SPOTPRICE);

    /**
     * Maintenance tab title.
     */
    private static final String TITLE_MAINT = ResourceMgr.getString(MoneyWiseUIResource.MAIN_MAINTENANCE);

    /**
     * QIF menu title.
     */
    private static final String MENU_CREATEQIF = ResourceMgr.getString(MoneyWiseUIResource.MAIN_MENU_CREATEQIF);

    /**
     * Archive menu title.
     */
    private static final String MENU_ARCHIVE = ResourceMgr.getString(MoneyWiseUIResource.MAIN_MENU_LOADARCHIVE);

    /**
     * SubVersion menu title.
     */
    private static final String MENU_BACKUPSVN = ResourceMgr.getString(MoneyWiseUIResource.MAIN_MENU_BACKUPSVN);

    /**
     * SubVersion menu title.
     */
    private static final String MENU_RESTORESVN = ResourceMgr.getString(MoneyWiseUIResource.MAIN_MENU_RESTORESVN);

    /**
     * Program name.
     */
    private static final String PROGRAM_NAME = ResourceMgr.getString(ProgramResource.PROGRAM_NAME);

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
     * @param pProfile the startup profile
     * @param pLogger the logger
     * @throws JOceanusException on error
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
        ReportTab myReportTab = new ReportTab(theView);
        theTabs.addTab(TITLE_REPORT, myReportTab);

        /* Create the Analysis Tab */
        myTask.startTask("Analysis");
        theStatement = new AnalysisStatement(theView);
        theTabs.addTab(TITLE_STATEMENT, theStatement.getPanel());

        /* Create the Register Tab */
        myTask.startTask("Register");
        theRegister = new Register(theView);
        theTabs.addTab(TITLE_REGISTER, theRegister.getPanel());

        /* Create the SpotView Tab */
        myTask.startTask("SpotPrices");
        theSpotView = new PricePoint(theView);
        theTabs.addTab(TITLE_SPOTVIEW, theSpotView.getPanel());

        /* Create the Maintenance Tab */
        myTask.startTask("Maintenance");
        theMaint = new MaintenanceTab(this);
        theTabs.addTab(TITLE_MAINT, theMaint);

        /* Add listeners */
        MainListener myListener = new MainListener();
        theView.addChangeListener(myListener);
        theTabs.addChangeListener(myListener);
        theRegister.addChangeListener(myListener);
        theStatement.addChangeListener(myListener);
        theSpotView.addChangeListener(myListener);
        theMaint.addChangeListener(myListener);
        theRegister.addActionListener(myListener);
        theStatement.addActionListener(myListener);
        myReportTab.addActionListener(myListener);
        theMaint.addActionListener(myListener);
        determineFocus();

        /* Set the icon */
        getFrame().setIconImage(MoneyWiseIcons.getProgramImage());

        /* Complete task */
        myTask.end();

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
        boolean hasUpdates = theRegister.hasUpdates();
        if (!hasUpdates) {
            hasUpdates = theStatement.hasUpdates();
        }
        if (!hasUpdates) {
            hasUpdates = theSpotView.hasUpdates();
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
            hasSession = theStatement.hasSession();
        }
        if (!hasSession) {
            hasSession = theSpotView.hasSession();
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

            /* If this event relates to the Subversion backup item */
        } else if (theSVNBackup.equals(o)) {
            /* Start a write backup operation */
            backupSubversion();

            /* If this event relates to the Subversion restore item */
        } else if (theSVNRestore.equals(o)) {
            /* Start a restore backup operation */
            restoreSubversion();

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
     * Backup subversion.
     */
    public void backupSubversion() {
        /* Allocate the status */
        MoneyWiseStatus myStatus = new MoneyWiseStatus(theView, getStatusBar());

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
        MoneyWiseStatus myStatus = new MoneyWiseStatus(theView, getStatusBar());

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
    private void selectStatement(final StatementSelect pSelect) {
        /* Pass through to the Statement view */
        theStatement.selectStatement(pSelect);

        /* Goto the Statement tab */
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
     * Select maintenance.
     * @param pEvent the action request
     */
    private void selectMaintenance(final ActionDetailEvent pEvent) {
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

        /* Enable/Disable the statement tab */
        iIndex = theTabs.indexOfTab(TITLE_STATEMENT);
        if (iIndex != -1) {
            boolean doEnabled = !hasWorker && (!hasSession || theStatement.hasSession());
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

        /* Enable/Disable the spotView tab */
        iIndex = theTabs.indexOfTab(TITLE_SPOTVIEW);
        if (iIndex != -1) {
            boolean doEnabled = !hasWorker && (!hasSession || theSpotView.hasSession());
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
        AboutBox myAbout = new AboutBox(getFrame(), getFrameName());

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
                /* Set Visibility */
                setVisibility();

                /* else if it is one of the sub-panels */
            } else if (theRegister.equals(o)
                       || theStatement.equals(o)
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
         * @param pAccount the requested account
         */
        protected ActionRequest(final AssetBase<?> pAccount) {
            theAccount = pAccount;
            theRange = null;
            theTransaction = null;
        }

        /**
         * Constructor.
         * @param pRange the requested range
         */
        protected ActionRequest(final JDateDayRangeSelect pRange) {
            theAccount = null;
            theRange = pRange;
            theTransaction = null;
        }

        /**
         * Constructor.
         * @param pAccount the requested account
         * @param pRange the requested range
         */
        protected ActionRequest(final AssetBase<?> pAccount,
                                final JDateDayRangeSelect pRange) {
            theAccount = pAccount;
            theRange = pRange;
            theTransaction = null;
        }

        /**
         * Constructor.
         * @param pAccount the requested account
         * @param pTrans the base transaction
         */
        protected ActionRequest(final AssetBase<?> pAccount,
                                final Transaction pTrans) {
            theAccount = pAccount;
            theRange = null;
            theTransaction = pTrans;
        }
    }
}
