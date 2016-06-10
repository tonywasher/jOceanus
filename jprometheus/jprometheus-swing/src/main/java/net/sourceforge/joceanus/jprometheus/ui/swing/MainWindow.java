/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadEvent;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingThreadManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisSwingViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisViewerWindow;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jprometheus.threads.PrometheusThreadId;
import net.sourceforge.joceanus.jprometheus.threads.swing.CreateBackup;
import net.sourceforge.joceanus.jprometheus.threads.swing.CreateDatabase;
import net.sourceforge.joceanus.jprometheus.threads.swing.CreateXmlFile;
import net.sourceforge.joceanus.jprometheus.threads.swing.LoadBackup;
import net.sourceforge.joceanus.jprometheus.threads.swing.LoadDatabase;
import net.sourceforge.joceanus.jprometheus.threads.swing.LoadXmlFile;
import net.sourceforge.joceanus.jprometheus.threads.swing.PurgeDatabase;
import net.sourceforge.joceanus.jprometheus.threads.swing.RenewSecurity;
import net.sourceforge.joceanus.jprometheus.threads.swing.StoreDatabase;
import net.sourceforge.joceanus.jprometheus.threads.swing.UpdatePassword;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusMenuId;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.TethysHelpModule;
import net.sourceforge.joceanus.jtethys.help.swing.TethysSwingHelpWindow;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager.TethysMenuSubMenu;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingMenuBarManager;

/**
 * Main window for application.
 * @author Tony Washer
 * @param <T> the data set type
 * @param <E> the data list enum class
 */
public abstract class MainWindow<T extends DataSet<T, E>, E extends Enum<E>> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);

    /**
     * Discard prompt.
     */
    private static final String PROMPT_DISCARD = PrometheusUIResource.PROMPT_DISCARD.getValue();

    /**
     * Close Dialog title.
     */
    private static final String TITLE_CLOSE = PrometheusUIResource.TITLE_CLOSE.getValue();

    /**
     * The GUI Factory.
     */
    private TethysSwingGuiFactory theGuiFactory;

    /**
     * The data view.
     */
    private DataControl<T, E, JComponent, Icon> theView;

    /**
     * The frame.
     */
    private JFrame theFrame;

    /**
     * The panel.
     */
    private JPanel thePanel;

    /**
     * The data menu.
     */
    private TethysSwingMenuBarManager theMenuBar;

    /**
     * The Thread Manager.
     */
    private MetisSwingThreadManager theThreadMgr;

    /**
     * The Started Help window.
     */
    private TethysSwingHelpWindow theHelpWdw;

    /**
     * The Viewer Manager.
     */
    private MetisSwingViewerManager theViewerMgr;

    /**
     * The Started data window.
     */
    private MetisViewerWindow theDataWdw;

    /**
     * The Status window.
     */
    private JComponent theStatusBar;

    /**
     * The listener.
     */
    private final MainListener theListener;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    protected MainWindow() throws OceanusException {
        /* create listener */
        theListener = new MainListener();
    }

    /**
     * Get the data view.
     * @return the data view
     */
    public DataControl<T, E, JComponent, Icon> getView() {
        return theView;
    }

    /**
     * Get the frame.
     * @return the frame
     */
    public JFrame getFrame() {
        return theFrame;
    }

    /**
     * Get the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Get the menu bar.
     * @return the menu bar
     */
    protected TethysMenuBarManager getMenuBar() {
        return theMenuBar;
    }

    /**
     * Build the main panel.
     * @return the main panel
     * @throws OceanusException on error
     */
    protected abstract JComponent buildMainPanel() throws OceanusException;

    /**
     * Obtain the frame name.
     * @return the frame name
     */
    protected abstract String getFrameName();

    /**
     * Obtain the Help Module.
     * @return the help module
     * @throws OceanusException on error
     */
    protected abstract TethysHelpModule getHelpModule() throws OceanusException;

    /**
     * Build the main window.
     * @param pView the Data view
     * @param pUtilitySet the utility set
     * @throws OceanusException on error
     */
    public void buildMainWindow(final DataControl<T, E, JComponent, Icon> pView,
                                final JOceanusSwingUtilitySet pUtilitySet) throws OceanusException {
        /* Store the view */
        theView = pView;
        theViewerMgr = pUtilitySet.getViewerManager();
        theGuiFactory = pUtilitySet.getGuiFactory();
        theThreadMgr = pUtilitySet.getThreadManager();

        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask.startTask("buildGUI");

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the frame */
        theFrame = new JFrame(getFrameName());
        pUtilitySet.getGuiFactory().setFrame(theFrame);

        /* Build the Main Panel */
        JComponent myMainPanel = buildMainPanel();

        /* Access the status bar and set to invisible */
        theStatusBar = theThreadMgr.getStatusManager().getNode();
        theStatusBar.setVisible(false);
        theThreadMgr.getEventRegistrar().addEventListener(MetisThreadEvent.THREADEND, e -> {
            setVisibility();
            theStatusBar.setVisible(false);
        });

        /* Create the layout */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theThreadMgr.getStatusManager().getNode());
        thePanel.add(myMainPanel);

        /* Attach the panel to the frame */
        thePanel.setOpaque(true);
        theFrame.setContentPane(thePanel);

        /* Build the Main Menu */
        buildMainMenu();

        /* Complete task */
        myTask.end();
    }

    /**
     * Build Main Menu.
     */
    protected void buildMainMenu() {
        /* Create the menu bar */
        theMenuBar = theGuiFactory.newMenuBar();

        /* Add Data Menu Items */
        TethysMenuSubMenu<?> myMenu = theMenuBar.newSubMenu(PrometheusMenuId.DATA);
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

        /* Add the Menu bar */
        theFrame.setJMenuBar(theMenuBar.getNode());
    }

    /**
     * Add Data Menu items.
     * @param pMenu the menu
     */
    protected void addDataMenuItems(final TethysMenuSubMenu<?> pMenu) {
        /* Add Standard Data Menu items */
        pMenu.newMenuItem(PrometheusThreadId.LOADDB, e -> loadDatabase());
        pMenu.newMenuItem(PrometheusThreadId.STOREDB, e -> storeDatabase());
        pMenu.newMenuItem(PrometheusThreadId.CREATEDB, e -> createDatabase());
        pMenu.newMenuItem(PrometheusThreadId.PURGEDB, e -> purgeDatabase());
    }

    /**
     * Add EditMenu items.
     * @param pMenu the menu
     */
    protected void addEditMenuItems(final TethysMenuSubMenu<?> pMenu) {
        /* Add Standard Edit Menu items */
        pMenu.newMenuItem(PrometheusMenuId.UNDO, e -> undoLastEdit());
        pMenu.newMenuItem(PrometheusMenuId.RESET, e -> resetEdit());
    }

    /**
     * Add Backup Menu items.
     * @param pMenu the menu
     */
    protected void addBackupMenuItems(final TethysMenuSubMenu<?> pMenu) {
        /* Add Standard Backup menu items */
        pMenu.newMenuItem(PrometheusThreadId.CREATEBACKUP, e -> writeBackup());
        pMenu.newMenuItem(PrometheusThreadId.RESTOREBACKUP, e -> restoreBackup());
        pMenu.newMenuItem(PrometheusThreadId.CREATEXML, e -> createXmlBackup());
        pMenu.newMenuItem(PrometheusThreadId.CREATEXTRACT, e -> createXmlXtract());
        pMenu.newMenuItem(PrometheusThreadId.RESTOREXML, e -> loadXmlFile());
    }

    /**
     * Add Security Menu items.
     * @param pMenu the menu
     */
    protected void addSecurityMenuItems(final TethysMenuSubMenu<?> pMenu) {
        /* Add Standard Security menu items */
        pMenu.newMenuItem(PrometheusThreadId.CHANGEPASS, e -> updatePassword());
        pMenu.newMenuItem(PrometheusThreadId.RENEWSECURITY, e -> reNewSecurity());
    }

    /**
     * Add Help Menu items.
     * @param pMenu the menu
     */
    protected void addHelpMenuItems(final TethysMenuSubMenu<?> pMenu) {
        /* Create the menu items */
        pMenu.newMenuItem(PrometheusMenuId.SHOWHELP, e -> displayHelp());
        pMenu.newMenuItem(PrometheusMenuId.DATAVIEWER, e -> displayViewerMgr());
        pMenu.newMenuItem(PrometheusMenuId.ABOUT, e -> displayAbout());
    }

    /**
     * Make the frame.
     * @throws OceanusException on error
     */
    public void makeFrame() throws OceanusException {
        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Add a window listener */
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(theListener);
        theView.setFrame(theFrame);

        /* Set visibility */
        setVisibility();
    }

    /**
     * Set visibility.
     */
    public void setVisibility() {
        /* Determine whether we have any updates */
        boolean hasUpdates = hasUpdates();
        boolean hasChanges = hasChanges();
        boolean hasControl = getView().getData().getControl() != null;

        /* Note whether we have a worker thread */
        boolean hasWorker = hasWorker();

        /* Disable menus if we have a worker thread */
        theMenuBar.setEnabled(PrometheusMenuId.DATA, !hasWorker);
        theMenuBar.setEnabled(PrometheusMenuId.BACKUP, !hasWorker);
        theMenuBar.setEnabled(PrometheusMenuId.SECURITY, !hasWorker && hasControl);

        /* If we have changes but no updates enable the undo/reset options */
        if ((hasWorker) || (!hasControl)) {
            theMenuBar.setEnabled(PrometheusMenuId.EDIT, false);
        } else {
            theMenuBar.setEnabled(PrometheusMenuId.EDIT, !hasUpdates && hasChanges);
        }

        /* If we have changes disable the create backup options */
        boolean allowBackups = !hasChanges && !hasUpdates && hasControl;
        theMenuBar.setEnabled(PrometheusThreadId.CREATEBACKUP, allowBackups);
        theMenuBar.setEnabled(PrometheusThreadId.CREATEXML, allowBackups);
        theMenuBar.setEnabled(PrometheusThreadId.CREATEXTRACT, allowBackups);

        /* If we have changes disable the security options */
        boolean allowSecurity = !hasChanges && !hasUpdates;
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
     * @return true/false
     */
    protected final boolean hasChanges() {
        return theView.getData().hasUpdates();
    }

    /**
     * Has the window got updates.
     * @return true/false
     */
    protected abstract boolean hasUpdates();

    /**
     * Is a worker active.
     * @return true/false
     */
    protected final boolean hasWorker() {
        return theThreadMgr.hasWorker();
    }

    /**
     * Start a thread.
     * @param pThread the thread to start
     */
    protected void startThread(final MetisThread<?, JComponent, Icon> pThread) {
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
        LoadDatabase<T, E, JComponent, Icon> myThread = new LoadDatabase<>(theView);
        startThread(myThread);
    }

    /**
     * Store Database.
     */
    private void storeDatabase() {
        /* Create the worker thread */
        StoreDatabase<T, E, JComponent, Icon> myThread = new StoreDatabase<>(theView);
        startThread(myThread);
    }

    /**
     * Create Database.
     */
    private void createDatabase() {
        /* Create the worker thread */
        CreateDatabase<T, E, JComponent, Icon> myThread = new CreateDatabase<>(theView);
        startThread(myThread);
    }

    /**
     * Purge Database.
     */
    private void purgeDatabase() {
        /* Create the worker thread */
        PurgeDatabase<T, E, JComponent, Icon> myThread = new PurgeDatabase<>(theView);
        startThread(myThread);
    }

    /**
     * UnDo last edit.
     */
    private void undoLastEdit() {
        /* Create a new profile */
        MetisProfile myTask = theView.getNewProfile("unDoLastEdit");

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
        MetisProfile myTask = theView.getNewProfile("resetEdit");

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
        CreateBackup<T, E, JComponent, Icon> myThread = new CreateBackup<>(theView);
        startThread(myThread);
    }

    /**
     * Restore Backup.
     */
    private void restoreBackup() {
        /* Create the worker thread */
        LoadBackup<T, E, JComponent, Icon> myThread = new LoadBackup<>(theView);
        startThread(myThread);
    }

    /**
     * Create XML Backup file.
     */
    private void createXmlBackup() {
        /* Create the worker thread */
        CreateXmlFile<T, E, JComponent, Icon> myThread = new CreateXmlFile<>(theView, true);
        startThread(myThread);
    }

    /**
     * Create XML Extract file.
     */
    private void createXmlXtract() {
        /* Create the worker thread */
        CreateXmlFile<T, E, JComponent, Icon> myThread = new CreateXmlFile<>(theView, false);
        startThread(myThread);
    }

    /**
     * Load XML Backup file.
     */
    private void loadXmlFile() {
        /* Create the worker thread */
        LoadXmlFile<T, E, JComponent, Icon> myThread = new LoadXmlFile<>(theView);
        startThread(myThread);
    }

    /**
     * Update password.
     */
    private void updatePassword() {
        /* Create the worker thread */
        UpdatePassword<T, E, JComponent, Icon> myThread = new UpdatePassword<>(theView);
        startThread(myThread);
    }

    /**
     * ReNew Security.
     */
    private void reNewSecurity() {
        /* Create the worker thread */
        RenewSecurity<T, E, JComponent, Icon> myThread = new RenewSecurity<>(theView);
        startThread(myThread);
    }

    /**
     * Display ViewerMgr.
     */
    private void displayViewerMgr() {
        /* Create the viewer window */
        theDataWdw = new MetisViewerWindow(theFrame, theViewerMgr);

        /* Listen for its closure */
        theDataWdw.addWindowListener(theListener);

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
            theHelpWdw = new TethysSwingHelpWindow((TethysSwingGuiFactory) theView.getUtilitySet().getGuiFactory());
            theHelpWdw.setModule(getHelpModule());

            /* Listen for its closure */
            theHelpWdw.getEventRegistrar().addEventListener(e -> {
                theMenuBar.setEnabled(PrometheusMenuId.SHOWHELP, true);
                theHelpWdw.hideDialog();
                theHelpWdw = null;
            });

            /* Disable the menu item */
            theMenuBar.setEnabled(PrometheusMenuId.SHOWHELP, false);

            /* Display it */
            theHelpWdw.showDialog();
        } catch (OceanusException e) {
            LOGGER.error("Failed to start Help Window", e);
            theHelpWdw = null;
        }
    }

    /**
     * Display About Box.
     */
    protected abstract void displayAbout();

    /**
     * Listener class.
     */
    private class MainListener
            extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent evt) {
            Object o = evt.getSource();

            /* If this is the frame that is closing down */
            if (theFrame.equals(o)) {
                /* If we have updates or changes */
                if ((hasUpdates()) || (hasChanges())) {
                    /* Ask whether to continue */
                    int myOption = JOptionPane.showConfirmDialog(theFrame, PROMPT_DISCARD, TITLE_CLOSE, JOptionPane.YES_NO_OPTION);

                    /* Ignore if no was responded */
                    if (myOption != JOptionPane.YES_OPTION) {
                        return;
                    }
                }

                /* terminate the executor */
                theThreadMgr.shutdown();

                /* Dispose of the data/help Windows if they exist */
                if (theDataWdw != null) {
                    theDataWdw.dispose();
                }
                if (theHelpWdw != null) {
                    theHelpWdw.hideDialog();
                }

                /* Dispose of the frame */
                theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                theFrame.dispose();

                /* else if this is the Data Window shutting down */
            } else if (o.equals(theDataWdw)) {
                /* Re-enable the viewer menu item */
                theMenuBar.setEnabled(PrometheusMenuId.DATAVIEWER, true);
                theDataWdw.dispose();
                theDataWdw = null;

                /* Notify viewer manager */
                theViewerMgr.declareWindow(null);

                /* else if this is the Help Window shutting down */
            } else if (o.equals(theHelpWdw)) {
                /* Re-enable the help menu item */
                theMenuBar.setEnabled(PrometheusMenuId.SHOWHELP, true);
                theHelpWdw.hideDialog();
                theHelpWdw = null;
            }
        }
    }
}
