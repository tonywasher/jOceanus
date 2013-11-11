/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdatamodels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jdatamanager.JDataWindow;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.threads.CreateBackup;
import net.sourceforge.joceanus.jdatamodels.threads.CreateDatabase;
import net.sourceforge.joceanus.jdatamodels.threads.CreateExtract;
import net.sourceforge.joceanus.jdatamodels.threads.LoadBackup;
import net.sourceforge.joceanus.jdatamodels.threads.LoadDatabase;
import net.sourceforge.joceanus.jdatamodels.threads.LoadExtract;
import net.sourceforge.joceanus.jdatamodels.threads.PurgeDatabase;
import net.sourceforge.joceanus.jdatamodels.threads.RenewSecurity;
import net.sourceforge.joceanus.jdatamodels.threads.StoreDatabase;
import net.sourceforge.joceanus.jdatamodels.threads.ThreadStatus;
import net.sourceforge.joceanus.jdatamodels.threads.UpdatePassword;
import net.sourceforge.joceanus.jdatamodels.threads.WorkerThread;
import net.sourceforge.joceanus.jdatamodels.ui.StatusBar;
import net.sourceforge.joceanus.jdatamodels.ui.ThreadControl;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jhelpmanager.HelpModule;
import net.sourceforge.joceanus.jhelpmanager.HelpWindow;

/**
 * Main window for application.
 * @author Tony Washer
 * @param <T> the data set type
 */
public abstract class MainWindow<T extends DataSet<T>>
        implements ThreadControl, ActionListener {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MainWindow.class.getName());

    /**
     * Data menu title.
     */
    private static final String MENU_DATA = NLS_BUNDLE.getString("MenuData");

    /**
     * Edit menu title.
     */
    private static final String MENU_EDIT = NLS_BUNDLE.getString("MenuEdit");

    /**
     * Backup menu title.
     */
    private static final String MENU_BACKUP = NLS_BUNDLE.getString("MenuBackup");

    /**
     * Security menu title.
     */
    private static final String MENU_SECURITY = NLS_BUNDLE.getString("MenuSecurity");

    /**
     * Help menu title.
     */
    private static final String MENU_HELP = NLS_BUNDLE.getString("MenuHelp");

    /**
     * Load Database menu item.
     */
    private static final String ITEM_LOADDB = NLS_BUNDLE.getString("ItemLoadDB");

    /**
     * Store Database menu item.
     */
    private static final String ITEM_STOREDB = NLS_BUNDLE.getString("ItemStoreDB");

    /**
     * Create Database menu item.
     */
    private static final String ITEM_CREATEDB = NLS_BUNDLE.getString("ItemCreateDB");

    /**
     * Purge Database menu item.
     */
    private static final String ITEM_PURGEDB = NLS_BUNDLE.getString("ItemPurgeDB");

    /**
     * Undo Edit menu item.
     */
    private static final String ITEM_UNDO = NLS_BUNDLE.getString("ItemUnDo");

    /**
     * Reset Edit menu item.
     */
    private static final String ITEM_RESET = NLS_BUNDLE.getString("ItemReset");

    /**
     * Create Backup menu item.
     */
    private static final String ITEM_MAKEBACKUP = NLS_BUNDLE.getString("ItemCreateBackup");

    /**
     * Restore Backup menu item.
     */
    private static final String ITEM_RESTOREBACK = NLS_BUNDLE.getString("ItemRestoreBackup");

    /**
     * Create Extract menu item.
     */
    private static final String ITEM_MAKEXTRACT = NLS_BUNDLE.getString("ItemCreateXtract");

    /**
     * Load Extract menu item.
     */
    private static final String ITEM_LOADXTRACT = NLS_BUNDLE.getString("ItemLoadXtract");

    /**
     * Renew Security.
     */
    private static final String ITEM_RENEWSEC = NLS_BUNDLE.getString("ItemRenewSecurity");

    /**
     * Change Password menu item.
     */
    private static final String ITEM_CHGPASS = NLS_BUNDLE.getString("ItemChangePass");

    /**
     * Help menu item.
     */
    private static final String ITEM_HELP = NLS_BUNDLE.getString("ItemHelp");

    /**
     * Data Manager menu item.
     */
    private static final String ITEM_DATAMGR = NLS_BUNDLE.getString("ItemDataMgr");

    /**
     * About menu item.
     */
    private static final String ITEM_ABOUT = NLS_BUNDLE.getString("ItemAbout");

    /**
     * Discard prompt.
     */
    private static final String PROMPT_DISCARD = NLS_BUNDLE.getString("PromptDiscard");

    /**
     * Close Dialog title.
     */
    private static final String TITLE_CLOSE = NLS_BUNDLE.getString("TitleClose");

    /**
     * The data view.
     */
    private DataControl<T> theView = null;

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
     * The Write Extract menu item.
     */
    private JMenuItem theWriteExtract = null;

    /**
     * The Load Extract menu item.
     */
    private JMenuItem theLoadExtract = null;

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
    private ExecutorService theExecutor = null;

    /**
     * The Started Help window.
     */
    private HelpWindow theHelpWdw = null;

    /**
     * The Data Manager.
     */
    private JDataManager theDataMgr = null;

    /**
     * The Started data window.
     */
    private JDataWindow theDataWdw = null;

    /**
     * Get the data view.
     * @return the data view
     */
    public DataControl<T> getView() {
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
     * @throws JDataException on error
     */
    protected abstract JComponent buildMainPanel() throws JDataException;

    /**
     * Obtain the frame name.
     * @return the frame name
     */
    protected abstract String getFrameName();

    /**
     * Obtain the Help Module.
     * @return the help module
     * @throws JDataException on error
     */
    protected abstract HelpModule getHelpModule() throws JDataException;

    /**
     * Constructor.
     * @throws JDataException on error
     */
    protected MainWindow() throws JDataException {
        /* Create the Executor service */
        theExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Build the main window.
     * @param pView the Data view
     * @throws JDataException on error
     */
    public void buildMainWindow(final DataControl<T> pView) throws JDataException {
        /* Store the view */
        theView = pView;
        theDataMgr = theView.getDataMgr();

        /* Create the new status bar */
        theStatusBar = new StatusBar(this, theView);
        JPanel myProgress = theStatusBar.getProgressPanel();
        myProgress.setVisible(false);
        JPanel myStatus = theStatusBar.getStatusPanel();
        myStatus.setVisible(false);

        /* Create the panel */
        thePanel = new JPanel();

        /* Build the Main Panel */
        JComponent myMainPanel = buildMainPanel();

        /* Create the layout */
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(myStatus);
        thePanel.add(myProgress);
        thePanel.add(myMainPanel);

        /* Create the frame */
        theFrame = new JFrame(getFrameName());

        /* Attach the panel to the frame */
        thePanel.setOpaque(true);
        theFrame.setContentPane(thePanel);

        /* Build the Main Menu */
        buildMainMenu();
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
        theLoadDBase.addActionListener(this);
        pMenu.add(theLoadDBase);
        theSaveDBase = new JMenuItem(ITEM_STOREDB);
        theSaveDBase.addActionListener(this);
        pMenu.add(theSaveDBase);
        theCreateDBase = new JMenuItem(ITEM_CREATEDB);
        theCreateDBase.addActionListener(this);
        pMenu.add(theCreateDBase);
        thePurgeDBase = new JMenuItem(ITEM_PURGEDB);
        thePurgeDBase.addActionListener(this);
        pMenu.add(thePurgeDBase);
    }

    /**
     * Add EditMenu items.
     * @param pMenu the menu
     */
    protected void addEditMenuItems(final JMenu pMenu) {
        /* Add Standard Edit Menu items */
        theUndoEdit = new JMenuItem(ITEM_UNDO);
        theUndoEdit.addActionListener(this);
        pMenu.add(theUndoEdit);
        theResetEdit = new JMenuItem(ITEM_RESET);
        theResetEdit.addActionListener(this);
        pMenu.add(theResetEdit);
    }

    /**
     * Add Backup Menu items.
     * @param pMenu the menu
     */
    protected void addBackupMenuItems(final JMenu pMenu) {
        /* Add Standard Backup menu items */
        theWriteBackup = new JMenuItem(ITEM_MAKEBACKUP);
        theWriteBackup.addActionListener(this);
        pMenu.add(theWriteBackup);
        theLoadBackup = new JMenuItem(ITEM_RESTOREBACK);
        theLoadBackup.addActionListener(this);
        pMenu.add(theLoadBackup);
        theWriteExtract = new JMenuItem(ITEM_MAKEXTRACT);
        theWriteExtract.addActionListener(this);
        pMenu.add(theWriteExtract);
        theLoadExtract = new JMenuItem(ITEM_LOADXTRACT);
        theLoadExtract.addActionListener(this);
        pMenu.add(theLoadExtract);
    }

    /**
     * Add Security Menu items.
     * @param pMenu the menu
     */
    protected void addSecurityMenuItems(final JMenu pMenu) {
        /* Add Standard Security menu items */
        theUpdatePass = new JMenuItem(ITEM_CHGPASS);
        theUpdatePass.addActionListener(this);
        pMenu.add(theUpdatePass);
        theRenewSec = new JMenuItem(ITEM_RENEWSEC);
        theRenewSec.addActionListener(this);
        pMenu.add(theRenewSec);
    }

    /**
     * Add Help Menu items.
     * @param pMenu the menu
     */
    protected void addHelpMenuItems(final JMenu pMenu) {
        /* Create the menu items */
        theHelpMgr = new JMenuItem(ITEM_HELP);
        theHelpMgr.addActionListener(this);
        pMenu.add(theHelpMgr);
        theShowDataMgr = new JMenuItem(ITEM_DATAMGR);
        theShowDataMgr.addActionListener(this);
        pMenu.add(theShowDataMgr);
        pMenu.addSeparator();
        theShowAbout = new JMenuItem(ITEM_ABOUT);
        theShowAbout.addActionListener(this);
        pMenu.add(theShowAbout);
    }

    /**
     * Make the frame.
     * @throws JDataException on error
     */
    public void makeFrame() throws JDataException {
        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Add a window listener */
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(new WindowClose());
        theView.setFrame(theFrame);

        /* Set visibility */
        setVisibility();
    }

    /**
     * Window Close Adapter.
     */
    private class WindowClose
            extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent evt) {
            Object o = evt.getSource();

            /* If this is the frame that is closing down */
            if (theFrame.equals(o)) {
                /* If we have updates or changes */
                if ((hasUpdates())
                    || (hasChanges())) {
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

                /* Notify data manager */
                theDataMgr.declareWindow(null);

                /* else if this is the Help Window shutting down */
            } else if (o.equals(theHelpWdw)) {
                /* Re-enable the help menu item */
                theHelpMgr.setEnabled(true);
                theHelpWdw.dispose();
                theHelpWdw = null;
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the Write Backup item */
        if (theWriteBackup.equals(o)) {
            /* Start a write backup operation */
            writeBackup();

            /* If this event relates to the Write Extract item */
        } else if (theWriteExtract.equals(o)) {
            /* Start a write extract operation */
            writeExtract();

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

            /* If this event relates to the Load extract item */
        } else if (theLoadExtract.equals(o)) {
            /* Start a load backup operation */
            loadExtract();

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
            /* Open the DataMgr window */
            displayDataMgr();

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
        theSecureMenu.setEnabled(!hasWorker
                                 && hasControl);

        /* If we have changes but no updates enable the undo/reset options */
        if ((hasWorker)
            || (!hasControl)) {
            theEditMenu.setEnabled(false);
        } else {
            theEditMenu.setEnabled(!hasUpdates
                                   && hasChanges);
        }

        /* If we have changes disable the create backup options */
        theWriteBackup.setEnabled(!hasChanges
                                  && !hasUpdates
                                  && hasControl);
        theWriteExtract.setEnabled(!hasChanges
                                   && !hasUpdates
                                   && hasControl);

        /* If we have changes disable the security options */
        theUpdatePass.setEnabled(!hasChanges
                                 && !hasUpdates);
        theRenewSec.setEnabled(!hasChanges
                               && !hasUpdates);

        /* If we have updates disable the load backup/database option */
        theLoadBackup.setEnabled(!hasUpdates);
        theLoadExtract.setEnabled(!hasUpdates);
        theLoadDBase.setEnabled(!hasUpdates);

        /* If we have updates or no changes disable the save database */
        theSaveDBase.setEnabled(!hasUpdates
                                && hasChanges);
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
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        LoadDatabase<T> myThread = new LoadDatabase<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Store Database.
     */
    private void storeDatabase() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        StoreDatabase<T> myThread = new StoreDatabase<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Create Database.
     */
    private void createDatabase() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

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
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        PurgeDatabase<T> myThread = new PurgeDatabase<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * UnDo last edit.
     */
    private void undoLastEdit() {
        /* Undo the last edit */
        theView.undoLastChange();

        /* Adjust visibility */
        setVisibility();
    }

    /**
     * reset Edit changes.
     */
    private void resetEdit() {
        /* Reset the edit View */
        theView.resetChanges();

        /* Adjust visibility */
        setVisibility();
    }

    /**
     * Write Backup.
     */
    private void writeBackup() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        CreateBackup<T> myThread = new CreateBackup<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Restore Backup.
     */
    private void restoreBackup() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        LoadBackup<T> myThread = new LoadBackup<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Write Extract.
     */
    private void writeExtract() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        CreateExtract<T> myThread = new CreateExtract<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Load Extract.
     */
    private void loadExtract() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        LoadExtract<T> myThread = new LoadExtract<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Update password.
     */
    private void updatePassword() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        UpdatePassword<T> myThread = new UpdatePassword<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * ReNew Security.
     */
    private void reNewSecurity() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        RenewSecurity<T> myThread = new RenewSecurity<T>(myStatus);
        myStatus.registerThread(myThread);
        startThread(myThread);
    }

    /**
     * Display DataMgr.
     */
    private void displayDataMgr() {
        /* Create the data window */
        theDataWdw = new JDataWindow(theFrame, theDataMgr);

        /* Listen for its closure */
        theDataWdw.addWindowListener(new WindowClose());

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
            theHelpWdw = new HelpWindow(theFrame, getHelpModule());

            /* Listen for its closure */
            theHelpWdw.addWindowListener(new WindowClose());

            /* Disable the menu item */
            theHelpMgr.setEnabled(false);

            /* Display it */
            theHelpWdw.showDialog();
        } catch (JDataException e) {
            theHelpWdw = null;
        }
    }

    /**
     * Display About Box.
     */
    protected abstract void displayAbout();
}
