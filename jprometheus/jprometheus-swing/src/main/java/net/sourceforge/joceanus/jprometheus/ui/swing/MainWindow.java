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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisSwingViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisViewerWindow;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.swing.JOceanusSwingUtilitySet;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
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
import net.sourceforge.joceanus.jprometheus.threads.swing.WorkerThread;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.TethysHelpModule;
import net.sourceforge.joceanus.jtethys.help.swing.TethysSwingHelpWindow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main window for application.
 * @author Tony Washer
 * @param <T> the data set type
 * @param <E> the data list enum class
 */
public abstract class MainWindow<T extends DataSet<T, E>, E extends Enum<E>>
        implements ThreadControl {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);

    /**
     * Data menu title.
     */
    private static final String MENU_DATA = PrometheusUIResource.MENU_DATA.getValue();

    /**
     * Edit menu title.
     */
    private static final String MENU_EDIT = PrometheusUIResource.MENU_EDIT.getValue();

    /**
     * Backup menu title.
     */
    private static final String MENU_BACKUP = PrometheusUIResource.MENU_BACKUP.getValue();

    /**
     * Security menu title.
     */
    private static final String MENU_SECURITY = PrometheusUIResource.MENU_SECURITY.getValue();

    /**
     * Help menu title.
     */
    private static final String MENU_HELP = PrometheusUIResource.MENU_HELP.getValue();

    /**
     * Load Database menu item.
     */
    private static final String ITEM_LOADDB = PrometheusUIResource.MENUITEM_LOADDB.getValue();

    /**
     * Store Database menu item.
     */
    private static final String ITEM_STOREDB = PrometheusUIResource.MENUITEM_STOREDB.getValue();

    /**
     * Create Database menu item.
     */
    private static final String ITEM_CREATEDB = PrometheusUIResource.MENUITEM_CREATEDB.getValue();

    /**
     * Purge Database menu item.
     */
    private static final String ITEM_PURGEDB = PrometheusUIResource.MENUITEM_PURGEDB.getValue();

    /**
     * Undo Edit menu item.
     */
    private static final String ITEM_UNDO = PrometheusUIResource.MENUITEM_UNDO.getValue();

    /**
     * Reset Edit menu item.
     */
    private static final String ITEM_RESET = PrometheusUIResource.MENUITEM_RESET.getValue();

    /**
     * Create Backup menu item.
     */
    private static final String ITEM_MAKEBACKUP = PrometheusUIResource.MENUITEM_BACKUPCREATE.getValue();

    /**
     * Restore Backup menu item.
     */
    private static final String ITEM_RESTOREBACK = PrometheusUIResource.MENUITEM_BACKUPRESTORE.getValue();

    /**
     * Create Xml menu item.
     */
    private static final String ITEM_CREATEXML = PrometheusUIResource.MENUITEM_XMLCREATE.getValue();

    /**
     * Create Xml Xtract menu item.
     */
    private static final String ITEM_CREATEXTRACT = PrometheusUIResource.MENUITEM_XTRACTCREATE.getValue();

    /**
     * Load Xml menu item.
     */
    private static final String ITEM_LOADXML = PrometheusUIResource.MENUITEM_XMLLOAD.getValue();

    /**
     * Renew Security.
     */
    private static final String ITEM_RENEWSEC = PrometheusUIResource.MENUITEM_SECURERENEW.getValue();

    /**
     * Change Password menu item.
     */
    private static final String ITEM_CHGPASS = PrometheusUIResource.MENUITEM_CHANGEPASS.getValue();

    /**
     * Help menu item.
     */
    private static final String ITEM_HELP = PrometheusUIResource.MENUITEM_HELP.getValue();

    /**
     * Data Manager menu item.
     */
    private static final String ITEM_DATAMGR = PrometheusUIResource.MENUITEM_DATAMGR.getValue();

    /**
     * About menu item.
     */
    private static final String ITEM_ABOUT = PrometheusUIResource.MENUITEM_ABOUT.getValue();

    /**
     * Discard prompt.
     */
    private static final String PROMPT_DISCARD = PrometheusUIResource.PROMPT_DISCARD.getValue();

    /**
     * Close Dialog title.
     */
    private static final String TITLE_CLOSE = PrometheusUIResource.TITLE_CLOSE.getValue();

    /**
     * The data view.
     */
    private DataControl<T, E> theView = null;

    /**
     * The frame.
     */
    private JFrame theFrame = null;

    /**
     * The panel.
     */
    private JPanel thePanel = null;

    /**
     * The status bar.
     */
    private StatusBar theStatusBar = null;

    /**
     * The data menu.
     */
    private JMenu theDataMenu = null;

    /**
     * The edit menu.
     */
    private JMenu theEditMenu = null;

    /**
     * The backup menu.
     */
    private JMenu theBackupMenu = null;

    /**
     * The security menu.
     */
    private JMenu theSecureMenu = null;

    /**
     * The Create Database menu item.
     */
    private JMenuItem theCreateDBase = null;

    /**
     * The Purge Database menu item.
     */
    private JMenuItem thePurgeDBase = null;

    /**
     * The Load Database menu item.
     */
    private JMenuItem theLoadDBase = null;

    /**
     * The Save Database menu item.
     */
    private JMenuItem theSaveDBase = null;

    /**
     * The Undo Edit menu item.
     */
    private JMenuItem theUndoEdit = null;

    /**
     * The Reset Edit menu item.
     */
    private JMenuItem theResetEdit = null;

    /**
     * The Write Backup menu item.
     */
    private JMenuItem theWriteBackup = null;

    /**
     * The Load Backup menu item.
     */
    private JMenuItem theLoadBackup = null;

    /**
     * The Create XML Extract menu item.
     */
    private JMenuItem theCreateXtract = null;

    /**
     * The Create XML Backup menu item.
     */
    private JMenuItem theCreateXml = null;

    /**
     * The Load XML menu item.
     */
    private JMenuItem theLoadXml = null;

    /**
     * The Update password menu item.
     */
    private JMenuItem theUpdatePass = null;

    /**
     * The Renew security menu item.
     */
    private JMenuItem theRenewSec = null;

    /**
     * The Show dataMgr menu item.
     */
    private JMenuItem theShowDataMgr = null;

    /**
     * The Show about menu item.
     */
    private JMenuItem theShowAbout = null;

    /**
     * The Show help menu item.
     */
    private JMenuItem theHelpMgr = null;

    /**
     * The Active thread.
     */
    private WorkerThread<?> theThread = null;

    /**
     * The Thread executor.
     */
    private final ExecutorService theExecutor;

    /**
     * The Started Help window.
     */
    private TethysSwingHelpWindow theHelpWdw = null;

    /**
     * The Viewer Manager.
     */
    private MetisSwingViewerManager theViewerMgr = null;

    /**
     * The Started data window.
     */
    private MetisViewerWindow theDataWdw = null;

    /**
     * The listener.
     */
    private final MainListener theListener;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    protected MainWindow() throws OceanusException {
        /* Create the Executor service */
        theExecutor = Executors.newSingleThreadExecutor();

        /* create listener */
        theListener = new MainListener();
    }

    /**
     * Get the data view.
     * @return the data view
     */
    public DataControl<T, E> getView() {
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
     * Get the status bar.
     * @return the status bar
     */
    public StatusBar getStatusBar() {
        return theStatusBar;
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
    public void buildMainWindow(final DataControl<T, E> pView,
                                final JOceanusSwingUtilitySet pUtilitySet) throws OceanusException {
        /* Store the view */
        theView = pView;
        theViewerMgr = pUtilitySet.getViewerManager();

        /* Obtain the active profile */
        MetisProfile myTask = theView.getActiveTask();
        myTask.startTask("buildGUI");

        /* Create the new status bar */
        theStatusBar = new StatusBar(this, theView);
        JPanel myProgress = theStatusBar.getProgressPanel();
        myProgress.setVisible(false);
        JPanel myStatus = theStatusBar.getStatusPanel();
        myStatus.setVisible(false);

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the frame */
        theFrame = new JFrame(getFrameName());

        /* Build the Main Panel */
        JComponent myMainPanel = buildMainPanel();

        /* Create the layout */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(myStatus);
        thePanel.add(myProgress);
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
        JMenuBar myMainMenu = new JMenuBar();

        /* Add Data Menu Items */
        theDataMenu = new JMenu(MENU_DATA);
        addDataMenuItems(theDataMenu);
        myMainMenu.add(theDataMenu);

        /* Add Edit Menu Items */
        theEditMenu = new JMenu(MENU_EDIT);
        addEditMenuItems(theEditMenu);
        myMainMenu.add(theEditMenu);

        /* Add Backup Menu Items */
        theBackupMenu = new JMenu(MENU_BACKUP);
        addBackupMenuItems(theBackupMenu);
        myMainMenu.add(theBackupMenu);

        /* Add Security Menu Items */
        theSecureMenu = new JMenu(MENU_SECURITY);
        addSecurityMenuItems(theSecureMenu);
        myMainMenu.add(theSecureMenu);

        /* Add Help Menu items */
        JMenu myHelpMenu = new JMenu(MENU_HELP);
        addHelpMenuItems(myHelpMenu);
        myMainMenu.add(Box.createHorizontalGlue());
        myMainMenu.add(myHelpMenu);

        /* Add the Menu bar */
        theFrame.setJMenuBar(myMainMenu);
    }

    /**
     * Add Data Menu items.
     * @param pMenu the menu
     */
    protected void addDataMenuItems(final JMenu pMenu) {
        /* Add Standard Data Menu items */
        theLoadDBase = new JMenuItem(ITEM_LOADDB);
        theLoadDBase.addActionListener(theListener);
        pMenu.add(theLoadDBase);
        theSaveDBase = new JMenuItem(ITEM_STOREDB);
        theSaveDBase.addActionListener(theListener);
        pMenu.add(theSaveDBase);
        theCreateDBase = new JMenuItem(ITEM_CREATEDB);
        theCreateDBase.addActionListener(theListener);
        pMenu.add(theCreateDBase);
        thePurgeDBase = new JMenuItem(ITEM_PURGEDB);
        thePurgeDBase.addActionListener(theListener);
        pMenu.add(thePurgeDBase);
    }

    /**
     * Add EditMenu items.
     * @param pMenu the menu
     */
    protected void addEditMenuItems(final JMenu pMenu) {
        /* Add Standard Edit Menu items */
        theUndoEdit = new JMenuItem(ITEM_UNDO);
        theUndoEdit.addActionListener(theListener);
        pMenu.add(theUndoEdit);
        theResetEdit = new JMenuItem(ITEM_RESET);
        theResetEdit.addActionListener(theListener);
        pMenu.add(theResetEdit);
    }

    /**
     * Add Backup Menu items.
     * @param pMenu the menu
     */
    protected void addBackupMenuItems(final JMenu pMenu) {
        /* Add Standard Backup menu items */
        theWriteBackup = new JMenuItem(ITEM_MAKEBACKUP);
        theWriteBackup.addActionListener(theListener);
        pMenu.add(theWriteBackup);
        theLoadBackup = new JMenuItem(ITEM_RESTOREBACK);
        theLoadBackup.addActionListener(theListener);
        pMenu.add(theLoadBackup);
        theCreateXml = new JMenuItem(ITEM_CREATEXML);
        theCreateXml.addActionListener(theListener);
        pMenu.add(theCreateXml);
        theCreateXtract = new JMenuItem(ITEM_CREATEXTRACT);
        theCreateXtract.addActionListener(theListener);
        pMenu.add(theCreateXtract);
        theLoadXml = new JMenuItem(ITEM_LOADXML);
        theLoadXml.addActionListener(theListener);
        pMenu.add(theLoadXml);
    }

    /**
     * Add Security Menu items.
     * @param pMenu the menu
     */
    protected void addSecurityMenuItems(final JMenu pMenu) {
        /* Add Standard Security menu items */
        theUpdatePass = new JMenuItem(ITEM_CHGPASS);
        theUpdatePass.addActionListener(theListener);
        pMenu.add(theUpdatePass);
        theRenewSec = new JMenuItem(ITEM_RENEWSEC);
        theRenewSec.addActionListener(theListener);
        pMenu.add(theRenewSec);
    }

    /**
     * Add Help Menu items.
     * @param pMenu the menu
     */
    protected void addHelpMenuItems(final JMenu pMenu) {
        /* Create the menu items */
        theHelpMgr = new JMenuItem(ITEM_HELP);
        theHelpMgr.addActionListener(theListener);
        pMenu.add(theHelpMgr);
        theShowDataMgr = new JMenuItem(ITEM_DATAMGR);
        theShowDataMgr.addActionListener(theListener);
        pMenu.add(theShowDataMgr);
        pMenu.addSeparator();
        theShowAbout = new JMenuItem(ITEM_ABOUT);
        theShowAbout.addActionListener(theListener);
        pMenu.add(theShowAbout);
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
        theDataMenu.setEnabled(!hasWorker);
        theBackupMenu.setEnabled(!hasWorker);
        theSecureMenu.setEnabled(!hasWorker && hasControl);

        /* If we have changes but no updates enable the undo/reset options */
        if ((hasWorker) || (!hasControl)) {
            theEditMenu.setEnabled(false);
        } else {
            theEditMenu.setEnabled(!hasUpdates && hasChanges);
        }

        /* If we have changes disable the create backup options */
        theWriteBackup.setEnabled(!hasChanges && !hasUpdates && hasControl);
        theCreateXtract.setEnabled(!hasChanges && !hasUpdates && hasControl);
        theCreateXml.setEnabled(!hasChanges && !hasUpdates && hasControl);

        /* If we have changes disable the security options */
        theUpdatePass.setEnabled(!hasChanges && !hasUpdates);
        theRenewSec.setEnabled(!hasChanges && !hasUpdates);

        /* If we have updates disable the load backup/database option */
        theLoadBackup.setEnabled(!hasUpdates);
        theLoadXml.setEnabled(!hasUpdates);
        theLoadDBase.setEnabled(!hasUpdates);

        /* If we have updates or no changes disable the save database */
        theSaveDBase.setEnabled(!hasUpdates && hasChanges);
    }

    @Override
    public void finishThread() {
        theThread = null;
        setVisibility();
    }

    @Override
    public void performCancel() {
        if (theThread != null) {
            theThread.cancel(false);
        }
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
        return theThread != null;
    }

    /**
     * Start a thread.
     * @param pThread the thread to start
     */
    protected void startThread(final WorkerThread<?> pThread) {
        /* Execute the thread and record it */
        theExecutor.execute(pThread);
        theThread = pThread;

        /* Adjust visible threads */
        setVisibility();
    }

    /**
     * Load Database.
     */
    private void loadDatabase() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        LoadDatabase<T, E> myThread = new LoadDatabase<T, E>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Store Database.
     */
    private void storeDatabase() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        StoreDatabase<T, E> myThread = new StoreDatabase<T, E>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Create Database.
     */
    private void createDatabase() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        CreateDatabase<T> myThread = new CreateDatabase<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Purge Database.
     */
    private void purgeDatabase() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        PurgeDatabase<T> myThread = new PurgeDatabase<T>(myStatus);
        myStatus.registerThread(myThread);
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
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        CreateBackup<T, E> myThread = new CreateBackup<T, E>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Restore Backup.
     */
    private void restoreBackup() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        LoadBackup<T, E> myThread = new LoadBackup<T, E>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Create XML Backup file.
     */
    private void createXmlBackup() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, getStatusBar());

        /* Create the worker thread */
        CreateXmlFile<T, E> myThread = new CreateXmlFile<T, E>(myStatus, true);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Create XML Xtract file.
     */
    private void createXmlXtract() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, getStatusBar());

        /* Create the worker thread */
        CreateXmlFile<T, E> myThread = new CreateXmlFile<T, E>(myStatus, false);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Load XML Backup file.
     */
    private void loadXmlFile() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, getStatusBar());

        /* Create the worker thread */
        LoadXmlFile<T, E> myThread = new LoadXmlFile<T, E>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Update password.
     */
    private void updatePassword() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        UpdatePassword<T, E> myThread = new UpdatePassword<T, E>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * ReNew Security.
     */
    private void reNewSecurity() {
        /* Allocate the status */
        ThreadStatus<T, E> myStatus = new ThreadStatus<T, E>(theView, theStatusBar);

        /* Create the worker thread */
        RenewSecurity<T, E> myThread = new RenewSecurity<T, E>(myStatus);
        myStatus.registerThread(myThread);
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
        theShowDataMgr.setEnabled(false);

        /* Display it */
        theDataWdw.showDialog();
    }

    /**
     * Display Help.
     */
    private void displayHelp() {
        try {
            /* Create the help window */
            theHelpWdw = new TethysSwingHelpWindow(theFrame, getHelpModule());

            /* Listen for its closure */
            theHelpWdw.addWindowListener(theListener);

            /* Disable the menu item */
            theHelpMgr.setEnabled(false);

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
            extends WindowAdapter
            implements ActionListener {
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
                theExecutor.shutdown();

                /* Dispose of the data/help Windows if they exist */
                if (theDataWdw != null) {
                    theDataWdw.dispose();
                }
                if (theHelpWdw != null) {
                    theHelpWdw.dispose();
                }

                /* Dispose of the frame */
                theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                theFrame.dispose();

                /* else if this is the Data Window shutting down */
            } else if (o.equals(theDataWdw)) {
                /* Re-enable the help menu item */
                theShowDataMgr.setEnabled(true);
                theDataWdw.dispose();
                theDataWdw = null;

                /* Notify viewer manager */
                theViewerMgr.declareWindow(null);

                /* else if this is the Help Window shutting down */
            } else if (o.equals(theHelpWdw)) {
                /* Re-enable the help menu item */
                theHelpMgr.setEnabled(true);
                theHelpWdw.dispose();
                theHelpWdw = null;
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Write Backup item */
            if (theWriteBackup.equals(o)) {
                /* Start a write backup operation */
                writeBackup();

                /* If this event relates to the Save Database item */
            } else if (theSaveDBase.equals(o)) {
                /* Start a store database operation */
                storeDatabase();

                /* If this event relates to the Load Database item */
            } else if (theLoadDBase.equals(o)) {
                /* Start a load database operation */
                loadDatabase();

                /* If this event relates to the Create Database item */
            } else if (theCreateDBase.equals(o)) {
                /* Start a load database operation */
                createDatabase();

                /* If this event relates to the Undo Edit item */
            } else if (theUndoEdit.equals(o)) {
                /* Undo the last edit */
                undoLastEdit();

                /* If this event relates to the Reset Edit item */
            } else if (theResetEdit.equals(o)) {
                /* Reset the edit */
                resetEdit();

                /* If this event relates to the Purge Database item */
            } else if (thePurgeDBase.equals(o)) {
                /* Start a load database operation */
                purgeDatabase();

                /* If this event relates to the Load backup item */
            } else if (theLoadBackup.equals(o)) {
                /* Start a restore backup operation */
                restoreBackup();

                /* If this event relates to the Create Xml Backup item */
            } else if (theCreateXml.equals(o)) {
                /* Start a createXmlBackup operation */
                createXmlBackup();

                /* If this event relates to the Create Xml Xtract item */
            } else if (theCreateXtract.equals(o)) {
                /* Start a createXmlXtract operation */
                createXmlXtract();

                /* If this event relates to the Load Xml item */
            } else if (theLoadXml.equals(o)) {
                /* Start a loadXml operation */
                loadXmlFile();

                /* If this event relates to the Update Password item */
            } else if (theUpdatePass.equals(o)) {
                /* Start an Update Password operation */
                updatePassword();

                /* If this event relates to the Renew Security item */
            } else if (theRenewSec.equals(o)) {
                /* Start a reNew Security operation */
                reNewSecurity();

                /* If this event relates to the Display Data item */
            } else if (theShowDataMgr.equals(o)) {
                /* Open the ViewerMgr window */
                displayViewerMgr();

                /* If this event relates to the Display Help item */
            } else if (theHelpMgr.equals(o)) {
                /* Open the help window */
                displayHelp();

                /* If this event relates to the Display About item */
            } else if (theShowAbout.equals(o)) {
                /* Open the help window */
                displayAbout();
            }
        }
    }
}
