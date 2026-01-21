/*
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.ui.panel;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.metis.help.MetisHelpModule;
import io.github.tonywasher.joceanus.metis.help.MetisHelpWindow;
import io.github.tonywasher.joceanus.metis.toolkit.MetisToolkit;
import io.github.tonywasher.joceanus.metis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadCreateBackup;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadCreateDatabase;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadCreateTables;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadCreateXMLFile;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadId;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadLoadBackup;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadLoadDatabase;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadLoadXMLFile;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadPurgeDatabase;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadRenewSecurity;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadStoreDatabase;
import net.sourceforge.joceanus.prometheus.threads.PrometheusThreadUpdatePassword;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.ui.PrometheusMenuId;
import net.sourceforge.joceanus.prometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIAlert;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIMainPanel;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIMenuBarManager;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIMenuBarManager.TethysUIMenuSubMenu;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusManager;

/**
 * Main window for application.
 */
public abstract class PrometheusMainWindow
        implements TethysUIMainPanel {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(PrometheusMainWindow.class);

    /**
     * Discard prompt.
     */
    private static final String PROMPT_DISCARD = PrometheusUIResource.PROMPT_DISCARD.getValue();

    /**
     * Close Dialog title.
     */
    private static final String TITLE_CLOSE = PrometheusUIResource.TITLE_CLOSE.getValue();

    /**
     * The Toolkit.
     */
    private MetisToolkit theToolkit;

    /**
     * The GUI Factory.
     */
    private TethysUIFactory<?> theGuiFactory;

    /**
     * The data view.
     */
    private PrometheusDataControl theView;

    /**
     * The panel.
     */
    private TethysUIBorderPaneManager thePanel;

    /**
     * The data menu.
     */
    private TethysUIMenuBarManager theMenuBar;

    /**
     * The Thread Manager.
     */
    private TethysUIThreadManager theThreadMgr;

    /**
     * The Started data window.
     */
    private MetisViewerWindow theDataWdw;

    /**
     * The Status window.
     */
    private TethysUIThreadStatusManager theStatusBar;

    /**
     * Constructor.
     */
    protected PrometheusMainWindow() {
    }

    /**
     * Get the data view.
     *
     * @return the data view
     */
    public PrometheusDataControl getView() {
        return theView;
    }

    @Override
    public TethysUIComponent getComponent() {
        return thePanel;
    }

    @Override
    public TethysUIMenuBarManager getMenuBar() {
        return theMenuBar;
    }

    /**
     * Build the main panel.
     *
     * @return the main panel
     * @throws OceanusException on error
     */
    protected abstract TethysUIComponent buildMainPanel() throws OceanusException;

    /**
     * Obtain the Help Module.
     *
     * @return the help module
     * @throws OceanusException on error
     */
    protected abstract MetisHelpModule getHelpModule() throws OceanusException;

    /**
     * Build the main window.
     *
     * @param pView    the Data view
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public void buildMainWindow(final PrometheusDataControl pView,
                                final PrometheusToolkit pToolkit) throws OceanusException {
        /* Store the view */
        theView = pView;
        theToolkit = pToolkit.getToolkit();
        theGuiFactory = theToolkit.getGuiFactory();
        theThreadMgr = theToolkit.getThreadManager();

        /* Obtain the active profile */
        final OceanusProfile myTask = theView.getActiveTask();
        myTask.startTask("buildGUI");

        /* Create the panel */
        thePanel = theGuiFactory.paneFactory().newBorderPane();

        /* Build the Main Panel */
        final TethysUIComponent myMainPanel = buildMainPanel();

        /* Access the status bar and set to invisible */
        theStatusBar = theThreadMgr.getStatusManager();
        theStatusBar.setVisible(false);
        theThreadMgr.getEventRegistrar().addEventListener(TethysUIThreadEvent.THREADEND, e -> {
            setVisibility();
            theStatusBar.setVisible(false);
        });

        /* Create the panel */
        thePanel.setNorth(theStatusBar);
        thePanel.setCentre(myMainPanel);

        /* Build the Main Menu */
        buildMainMenu();

        /* Create the data window */
        theDataWdw = theToolkit.newViewerWindow();
        theDataWdw.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theMenuBar.setEnabled(PrometheusMenuId.DATAVIEWER, true));

        /* Complete task */
        myTask.end();
    }

    /**
     * Build Main Menu.
     */
    protected void buildMainMenu() {
        /* Create the menu bar */
        theMenuBar = theGuiFactory.menuFactory().newMenuBar();

        /* Add Data Menu Items */
        TethysUIMenuSubMenu myMenu = theMenuBar.newSubMenu(PrometheusMenuId.DATA);
        addDataMenuItems(myMenu);

        /* Add Edit Menu Items */
        myMenu = theMenuBar.newSubMenu(PrometheusMenuId.EDIT);
        addEditMenuItems(myMenu);

        /* Add Backup Menu Items */
        myMenu = theMenuBar.newSubMenu(PrometheusMenuId.BACKUP);
        addBackupMenuItems(myMenu);

        /* Add Security Menu Items */
        myMenu = theMenuBar.newSubMenu(PrometheusMenuId.SECURITY);
        addSecurityMenuItems(myMenu);

        /* Add Help Menu items */
        myMenu = theMenuBar.newSubMenu(PrometheusMenuId.HELP);
        addHelpMenuItems(myMenu);
    }

    /**
     * Add Data Menu items.
     *
     * @param pMenu the menu
     */
    protected void addDataMenuItems(final TethysUIMenuSubMenu pMenu) {
        /* Add Standard Data Menu items */
        pMenu.newMenuItem(PrometheusThreadId.LOADDB, e -> loadDatabase());
        pMenu.newMenuItem(PrometheusThreadId.STOREDB, e -> storeDatabase());
        pMenu.newMenuItem(PrometheusThreadId.CREATEDB, e -> createDatabase());
        pMenu.newMenuItem(PrometheusThreadId.CREATEDBTABLES, e -> createDatabaseTables());
        pMenu.newMenuItem(PrometheusThreadId.PURGEDB, e -> purgeDatabase());
    }

    /**
     * Add EditMenu items.
     *
     * @param pMenu the menu
     */
    protected void addEditMenuItems(final TethysUIMenuSubMenu pMenu) {
        /* Add Standard Edit Menu items */
        pMenu.newMenuItem(PrometheusMenuId.UNDO, e -> undoLastEdit());
        pMenu.newMenuItem(PrometheusMenuId.RESET, e -> resetEdit());
    }

    /**
     * Add Backup Menu items.
     *
     * @param pMenu the menu
     */
    protected void addBackupMenuItems(final TethysUIMenuSubMenu pMenu) {
        /* Add Standard Backup menu items */
        pMenu.newMenuItem(PrometheusThreadId.CREATEBACKUP, e -> writeBackup());
        pMenu.newMenuItem(PrometheusThreadId.RESTOREBACKUP, e -> restoreBackup());
        pMenu.newMenuItem(PrometheusThreadId.CREATEXML, e -> createXmlBackup());
        pMenu.newMenuItem(PrometheusThreadId.CREATEXTRACT, e -> createXmlXtract());
        pMenu.newMenuItem(PrometheusThreadId.RESTOREXML, e -> loadXmlFile());
    }

    /**
     * Add Security Menu items.
     *
     * @param pMenu the menu
     */
    protected void addSecurityMenuItems(final TethysUIMenuSubMenu pMenu) {
        /* Add Standard Security menu items */
        pMenu.newMenuItem(PrometheusThreadId.CHANGEPASS, e -> updatePassword());
        pMenu.newMenuItem(PrometheusThreadId.RENEWSECURITY, e -> reNewSecurity());
    }

    /**
     * Add Help Menu items.
     *
     * @param pMenu the menu
     */
    protected void addHelpMenuItems(final TethysUIMenuSubMenu pMenu) {
        /* Create the menu items */
        pMenu.newMenuItem(PrometheusMenuId.SHOWHELP, e -> displayHelp());
        pMenu.newMenuItem(PrometheusMenuId.DATAVIEWER, e -> displayViewerMgr());
        pMenu.newMenuItem(PrometheusMenuId.ABOUT, e -> displayAbout());
    }

    /**
     * Make the frame.
     */
    public void makeFrame() {
        /* Set visibility */
        setVisibility();
    }

    /**
     * Set visibility.
     */
    public void setVisibility() {
        /* Determine whether we have any updates */
        final boolean hasUpdates = hasUpdates();
        final boolean hasChanges = hasChanges();
        final boolean hasControl = getView().getData().getControl() != null;

        /* Note whether we have a worker thread */
        final boolean hasWorker = hasWorker();

        /* Disable menus if we have a worker thread */
        theMenuBar.setEnabled(PrometheusMenuId.DATA, !hasWorker);
        theMenuBar.setEnabled(PrometheusMenuId.BACKUP, !hasWorker);
        theMenuBar.setEnabled(PrometheusMenuId.SECURITY, !hasWorker && hasControl);

        /* If we have changes but no updates enable the undo/reset options */
        if (hasWorker || !hasControl) {
            theMenuBar.setEnabled(PrometheusMenuId.EDIT, false);
        } else {
            theMenuBar.setEnabled(PrometheusMenuId.EDIT, !hasUpdates && hasChanges);
        }

        /* If we have changes disable the create backup options */
        final boolean allowBackups = !hasChanges && !hasUpdates && hasControl;
        theMenuBar.setEnabled(PrometheusThreadId.CREATEBACKUP, allowBackups);
        theMenuBar.setEnabled(PrometheusThreadId.CREATEXML, allowBackups);
        theMenuBar.setEnabled(PrometheusThreadId.CREATEXTRACT, allowBackups);

        /* If we have changes disable the security options */
        final boolean allowSecurity = !hasChanges && !hasUpdates;
        theMenuBar.setEnabled(PrometheusThreadId.CHANGEPASS, allowSecurity);
        theMenuBar.setEnabled(PrometheusThreadId.RENEWSECURITY, allowSecurity);

        /* If we have updates disable the load backup/database option */
        theMenuBar.setEnabled(PrometheusThreadId.RESTOREBACKUP, !hasUpdates);
        theMenuBar.setEnabled(PrometheusThreadId.RESTOREXML, !hasUpdates);
        theMenuBar.setEnabled(PrometheusThreadId.LOADDB, !hasUpdates);

        /* If we have updates or no changes disable the save database */
        theMenuBar.setEnabled(PrometheusThreadId.STOREDB, !hasUpdates && hasChanges);
    }

    /**
     * Has the underlying data got changes.
     *
     * @return true/false
     */
    protected final boolean hasChanges() {
        return theView.getData().hasUpdates();
    }

    /**
     * Has the window got updates.
     *
     * @return true/false
     */
    protected abstract boolean hasUpdates();

    /**
     * Is a worker active.
     *
     * @return true/false
     */
    protected final boolean hasWorker() {
        return theThreadMgr.hasWorker();
    }

    /**
     * Start a thread.
     *
     * @param pThread the thread to start
     */
    protected void startThread(final TethysUIThread<?> pThread) {
        /* Execute the thread and record it */
        theThreadMgr.startThread(pThread);

        /* Adjust visible threads */
        theStatusBar.setVisible(true);
        setVisibility();
    }

    /**
     * Load Database.
     */
    private void loadDatabase() {
        /* Create the worker thread */
        final PrometheusThreadLoadDatabase myThread = new PrometheusThreadLoadDatabase(theView);
        startThread(myThread);
    }

    /**
     * Store Database.
     */
    private void storeDatabase() {
        /* Create the worker thread */
        final PrometheusThreadStoreDatabase myThread = new PrometheusThreadStoreDatabase(theView);
        startThread(myThread);
    }

    /**
     * Create Database.
     */
    private void createDatabase() {
        /* Create the worker thread */
        final PrometheusThreadCreateDatabase myThread = new PrometheusThreadCreateDatabase(theView);
        startThread(myThread);
    }

    /**
     * Create Database Tables.
     */
    private void createDatabaseTables() {
        /* Create the worker thread */
        final PrometheusThreadCreateTables myThread = new PrometheusThreadCreateTables(theView);
        startThread(myThread);
    }

    /**
     * Purge Database.
     */
    private void purgeDatabase() {
        /* Create the worker thread */
        final PrometheusThreadPurgeDatabase myThread = new PrometheusThreadPurgeDatabase(theView);
        startThread(myThread);
    }

    /**
     * UnDo last edit.
     */
    private void undoLastEdit() {
        /* Create a new profile */
        final OceanusProfile myTask = theView.getNewProfile("unDoLastEdit");

        /* Undo the last edit */
        theView.undoLastChange();

        /* Adjust visibility */
        setVisibility();

        /* Complete the task */
        myTask.end();
    }

    /**
     * reset Edit changes.
     */
    private void resetEdit() {
        /* Create a new profile */
        final OceanusProfile myTask = theView.getNewProfile("resetEdit");

        /* Reset the edit View */
        theView.resetChanges();

        /* Adjust visibility */
        setVisibility();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Write Backup.
     */
    private void writeBackup() {
        /* Create the worker thread */
        final PrometheusThreadCreateBackup myThread = new PrometheusThreadCreateBackup(theView);
        startThread(myThread);
    }

    /**
     * Restore Backup.
     */
    private void restoreBackup() {
        /* Create the worker thread */
        final PrometheusThreadLoadBackup myThread = new PrometheusThreadLoadBackup(theView);
        startThread(myThread);
    }

    /**
     * Create XML Backup file.
     */
    private void createXmlBackup() {
        /* Create the worker thread */
        final PrometheusThreadCreateXMLFile myThread = new PrometheusThreadCreateXMLFile(theView, true);
        startThread(myThread);
    }

    /**
     * Create XML Extract file.
     */
    private void createXmlXtract() {
        /* Create the worker thread */
        final PrometheusThreadCreateXMLFile myThread = new PrometheusThreadCreateXMLFile(theView, false);
        startThread(myThread);
    }

    /**
     * Load XML Backup file.
     */
    private void loadXmlFile() {
        /* Create the worker thread */
        final PrometheusThreadLoadXMLFile myThread = new PrometheusThreadLoadXMLFile(theView);
        startThread(myThread);
    }

    /**
     * Update password.
     */
    private void updatePassword() {
        /* Create the worker thread */
        final PrometheusThreadUpdatePassword myThread = new PrometheusThreadUpdatePassword(theView);
        startThread(myThread);
    }

    /**
     * ReNew Security.
     */
    private void reNewSecurity() {
        /* Create the worker thread */
        final PrometheusThreadRenewSecurity myThread = new PrometheusThreadRenewSecurity(theView);
        startThread(myThread);
    }

    /**
     * Display ViewerMgr.
     */
    private void displayViewerMgr() {
        /* Disable the menu item */
        theMenuBar.setEnabled(PrometheusMenuId.DATAVIEWER, false);

        /* Display it */
        theDataWdw.showDialog();
    }

    /**
     * Display Help.
     */
    private void displayHelp() {
        try {
            /* Create the help window */
            final MetisHelpWindow myHelpWdw = theToolkit.newHelpWindow();
            myHelpWdw.setModule(getHelpModule());

            /* Listen for its closure */
            myHelpWdw.getEventRegistrar().addEventListener(e -> {
                theMenuBar.setEnabled(PrometheusMenuId.SHOWHELP, true);
                myHelpWdw.hideDialog();
            });

            /* Disable the menu item */
            theMenuBar.setEnabled(PrometheusMenuId.SHOWHELP, false);

            /* Display it */
            myHelpWdw.showDialog();

        } catch (OceanusException e) {
            LOGGER.error("Failed to start Help Window", e);
        }
    }

    /**
     * Display About Box.
     */
    protected abstract void displayAbout();

    @Override
    public boolean handleAppClose() {
        /* If we have updates or changes */
        if (hasUpdates() || hasChanges()) {
            /* Ask whether to continue */
            final TethysUIAlert myAlert = theGuiFactory.dialogFactory().newAlert();
            myAlert.setMessage(PROMPT_DISCARD);
            myAlert.setTitle(TITLE_CLOSE);
            final boolean myResult = myAlert.confirmYesNo();

            /* Ignore if not confirmed */
            if (!myResult) {
                return false;
            }
        }

        /* terminate the executor */
        theThreadMgr.shutdown();
        return true;
    }
}
