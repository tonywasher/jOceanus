/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataWindow;
import net.sourceforge.JHelpManager.HelpModule;
import net.sourceforge.JHelpManager.HelpWindow;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.threads.CreateBackup;
import uk.co.tolcroft.models.threads.CreateDatabase;
import uk.co.tolcroft.models.threads.CreateExtract;
import uk.co.tolcroft.models.threads.LoadBackup;
import uk.co.tolcroft.models.threads.LoadDatabase;
import uk.co.tolcroft.models.threads.LoadExtract;
import uk.co.tolcroft.models.threads.PurgeDatabase;
import uk.co.tolcroft.models.threads.RenewSecurity;
import uk.co.tolcroft.models.threads.StoreDatabase;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.threads.UpdatePassword;
import uk.co.tolcroft.models.threads.WorkerThread;
import uk.co.tolcroft.models.ui.StatusBar;
import uk.co.tolcroft.models.ui.ThreadControl;
import uk.co.tolcroft.models.views.DataControl;

/**
 * Main window for application.
 * @author Tony Washer
 * @param <T> the data set type
 */
public abstract class MainWindow<T extends DataSet<T>> implements ThreadControl, ActionListener {
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
     * Get the data manager.
     * @return the data manager
     */
    public JDataManager getDataMgr() {
        return theDataMgr;
    }

    /**
     * Build the main panel.
     * @return the main panel
     */
    protected abstract JComponent buildMainPanel();

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
        /* Create the data manager */
        theDataMgr = new JDataManager();

        /* Create the Executor service */
        theExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Build the main window.
     * @param pView the Data view
     * @throws JDataException on error
     */
    public void buildMainWindow(final DataControl<T> pView) throws JDataException {
        JPanel myProgress;
        JPanel myStatus;
        JComponent myMainPanel;

        /* Store the view */
        theView = pView;

        /* Create the new status bar */
        theStatusBar = new StatusBar(this, theView);
        myProgress = theStatusBar.getProgressPanel();
        myProgress.setVisible(false);
        myStatus = theStatusBar.getStatusPanel();
        myStatus.setVisible(false);

        /* Create the panel */
        thePanel = new JPanel();

        /* Build the Main Panel */
        myMainPanel = buildMainPanel();

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                         false)
                                                    .addComponent(myMainPanel, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(myProgress, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(myStatus, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          myLayout.createSequentialGroup().addComponent(myStatus)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(myProgress)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(myMainPanel).addContainerGap()));

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
        theDataMenu = new JMenu("Data");
        addDataMenuItems(theDataMenu);
        myMainMenu.add(theDataMenu);

        /* Add Backup Menu Items */
        theBackupMenu = new JMenu("Backup");
        addBackupMenuItems(theBackupMenu);
        myMainMenu.add(theBackupMenu);

        /* Add Security Menu Items */
        theSecureMenu = new JMenu("Security");
        addSecurityMenuItems(theSecureMenu);
        myMainMenu.add(theSecureMenu);

        /* Add Help Menu items */
        JMenu myHelpMenu = new JMenu("Help");
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
        theLoadDBase = new JMenuItem("Load Database");
        theLoadDBase.addActionListener(this);
        pMenu.add(theLoadDBase);
        theSaveDBase = new JMenuItem("Store to Database");
        theSaveDBase.addActionListener(this);
        pMenu.add(theSaveDBase);
        theCreateDBase = new JMenuItem("Create Database Tables");
        theCreateDBase.addActionListener(this);
        pMenu.add(theCreateDBase);
        thePurgeDBase = new JMenuItem("Purge Database");
        thePurgeDBase.addActionListener(this);
        pMenu.add(thePurgeDBase);
    }

    /**
     * Add Backup Menu items.
     * @param pMenu the menu
     */
    protected void addBackupMenuItems(final JMenu pMenu) {
        /* Add Standard Backup menu items */
        theWriteBackup = new JMenuItem("Create Backup");
        theWriteBackup.addActionListener(this);
        pMenu.add(theWriteBackup);
        theLoadBackup = new JMenuItem("Restore from Backup");
        theLoadBackup.addActionListener(this);
        pMenu.add(theLoadBackup);
        theWriteExtract = new JMenuItem("Create Extract");
        theWriteExtract.addActionListener(this);
        pMenu.add(theWriteExtract);
        theLoadExtract = new JMenuItem("Load from Extract");
        theLoadExtract.addActionListener(this);
        pMenu.add(theLoadExtract);
    }

    /**
     * Add Security Menu items.
     * @param pMenu the menu
     */
    protected void addSecurityMenuItems(final JMenu pMenu) {
        /* Add Standard Security menu items */
        theUpdatePass = new JMenuItem("Update Password");
        theUpdatePass.addActionListener(this);
        pMenu.add(theUpdatePass);
        theRenewSec = new JMenuItem("Renew Security");
        theRenewSec.addActionListener(this);
        pMenu.add(theRenewSec);
    }

    /**
     * Add Help Menu items.
     * @param pMenu the menu
     */
    protected void addHelpMenuItems(final JMenu pMenu) {
        /* Create the menu items */
        theHelpMgr = new JMenuItem("Help");
        theHelpMgr.addActionListener(this);
        pMenu.add(theHelpMgr);
        theShowDataMgr = new JMenuItem("Data Manager");
        theShowDataMgr.addActionListener(this);
        pMenu.add(theShowDataMgr);
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

        /* Load data from the database */
        loadDatabase();
    }

    /**
     * Window Close Adapter.
     */
    private class WindowClose extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent evt) {
            Object o = evt.getSource();

            /* If this is the frame that is closing down */
            if (o == theFrame) {
                /* If we have updates or changes */
                if ((hasUpdates()) || (hasChanges())) {
                    /* Ask whether to continue */
                    int myOption = JOptionPane.showConfirmDialog(theFrame, "Discard unsaved data changes?",
                                                                 "Confirm Close", JOptionPane.YES_NO_OPTION);

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
                theFrame.dispose();

                /* Exit the application */
                System.exit(0);

                /* else if this is the Data Window shutting down */
            } else if (o == theDataWdw) {
                /* Re-enable the help menu item */
                theShowDataMgr.setEnabled(true);
                theDataWdw.dispose();
                theDataWdw = null;

                /* Notify data manager */
                theDataMgr.declareWindow(null);

                /* else if this is the Help Window shutting down */
            } else if (o == theHelpWdw) {
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
        if (o == theWriteBackup) {
            /* Start a write backup operation */
            writeBackup();

            /* If this event relates to the Write Extract item */
        } else if (o == theWriteExtract) {
            /* Start a write extract operation */
            writeExtract();

            /* If this event relates to the Save Database item */
        } else if (o == theSaveDBase) {
            /* Start a store database operation */
            storeDatabase();

            /* If this event relates to the Load Database item */
        } else if (o == theLoadDBase) {
            /* Start a load database operation */
            loadDatabase();

            /* If this event relates to the Create Database item */
        } else if (o == theCreateDBase) {
            /* Start a load database operation */
            createDatabase();

            /* If this event relates to the Purge Database item */
        } else if (o == thePurgeDBase) {
            /* Start a load database operation */
            purgeDatabase();

            /* If this event relates to the Load backup item */
        } else if (o == theLoadBackup) {
            /* Start a restore backup operation */
            restoreBackup();

            /* If this event relates to the Load extract item */
        } else if (o == theLoadExtract) {
            /* Start a load backup operation */
            loadExtract();

            /* If this event relates to the Update Password item */
        } else if (o == theUpdatePass) {
            /* Start an Update Password operation */
            updatePassword();

            /* If this event relates to the Renew Security item */
        } else if (o == theRenewSec) {
            /* Start a reNew Security operation */
            reNewSecurity();

            /* If this event relates to the Display Data item */
        } else if (o == theShowDataMgr) {
            /* Open the DataMgr window */
            displayDataMgr();

            /* If this event relates to the Display Help item */
        } else if (o == theHelpMgr) {
            /* Open the help window */
            displayHelp();
        }
    }

    /**
     * Set visibility.
     */
    public void setVisibility() {
        boolean hasUpdates;
        boolean hasChanges;
        boolean hasWorker;

        /* Determine whether we have any updates */
        hasUpdates = hasUpdates();
        hasChanges = hasChanges();

        /* Note whether we have a worker thread */
        hasWorker = hasWorker();

        /* Disable menus if we have a worker thread */
        theDataMenu.setEnabled(!hasWorker);
        theBackupMenu.setEnabled(!hasWorker);
        theSecureMenu.setEnabled(!hasWorker);

        /* Enable/Disable the debug menu item */
        // theShowDebug.setVisible(theProperties.doShowDebug());

        /* If we have changes disable the create backup options */
        theWriteBackup.setEnabled(!hasChanges && !hasUpdates);
        theWriteExtract.setEnabled(!hasChanges && !hasUpdates);

        /* If we have changes disable the security options */
        theUpdatePass.setEnabled(!hasChanges && !hasUpdates);
        theRenewSec.setEnabled(!hasChanges && !hasUpdates);

        /* If we have updates disable the load backup/database option */
        theLoadBackup.setEnabled(!hasUpdates);
        theLoadExtract.setEnabled(!hasUpdates);
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
    protected boolean hasChanges() {
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
    protected boolean hasWorker() {
        return (theThread != null);
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
        startThread(myThread);
    }

    /**
     * Write Backup.
     */
    private void writeBackup() {
        /* Allocate the status */
        ThreadStatus<T> myStatus = new ThreadStatus<T>(theView, theStatusBar);

        /* Create the worker thread */
        CreateBackup<T> myThread = new CreateBackup<T>(myStatus);
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
        startThread(myThread);
    }

    /**
     * Display DataMgr.
     */
    private void displayDataMgr() {
        try {
            /* Create the data window */
            theDataWdw = new JDataWindow(theFrame, theDataMgr);

            /* Listen for its closure */
            theDataWdw.addWindowListener(new WindowClose());

            /* Disable the menu item */
            theShowDataMgr.setEnabled(false);

            /* Display it */
            theDataWdw.showDialog();
        } catch (Exception e) {
            theDataWdw = null;
        }
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
        } catch (Exception e) {
            theHelpWdw = null;
        }
    }
}
