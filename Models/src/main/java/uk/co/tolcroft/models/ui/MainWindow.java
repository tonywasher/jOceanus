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
package uk.co.tolcroft.models.ui;

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

import net.sourceforge.JDataWalker.DebugManager;
import net.sourceforge.JDataWalker.DebugWindow;
import net.sourceforge.JDataWalker.ModelException;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.help.HelpModule;
import uk.co.tolcroft.models.help.HelpWindow;
import uk.co.tolcroft.models.threads.CreateBackup;
import uk.co.tolcroft.models.threads.CreateDatabase;
import uk.co.tolcroft.models.threads.CreateExtract;
import uk.co.tolcroft.models.threads.LoadBackup;
import uk.co.tolcroft.models.threads.LoadDatabase;
import uk.co.tolcroft.models.threads.LoadExtract;
import uk.co.tolcroft.models.threads.PurgeDatabase;
import uk.co.tolcroft.models.threads.RenewSecurity;
import uk.co.tolcroft.models.threads.StoreDatabase;
import uk.co.tolcroft.models.threads.UpdatePassword;
import uk.co.tolcroft.models.threads.WorkerThread;
import uk.co.tolcroft.models.views.DataControl;

public abstract class MainWindow<T extends DataSet<T>> implements ActionListener {
    private DataControl<T> theView = null;
    private JFrame theFrame = null;
    private JPanel thePanel = null;
    private StatusBar theStatusBar = null;
    private JMenuBar theMainMenu = null;
    private JMenu theDataMenu = null;
    private JMenu theBackupMenu = null;
    private JMenu theSecureMenu = null;
    private JMenu theHelpMenu = null;
    private JMenuItem theCreateDBase = null;
    private JMenuItem thePurgeDBase = null;
    private JMenuItem theLoadDBase = null;
    private JMenuItem theSaveDBase = null;
    private JMenuItem theWriteBackup = null;
    private JMenuItem theLoadBackup = null;
    private JMenuItem theWriteExtract = null;
    private JMenuItem theLoadExtract = null;
    private JMenuItem theUpdatePass = null;
    private JMenuItem theRenewSec = null;
    private JMenuItem theShowDebug = null;
    private JMenuItem theHelpMgr = null;
    private WorkerThread<?> theThread = null;
    private ExecutorService theExecutor = null;
    private HelpWindow theHelpWdw = null;
    private DebugManager theDebugMgr = null;
    private DebugWindow theDebugWdw = null;

    /* Access methods */
    public DataControl<T> getView() {
        return theView;
    }

    public JFrame getFrame() {
        return theFrame;
    }

    public JPanel getPanel() {
        return thePanel;
    }

    public StatusBar getStatusBar() {
        return theStatusBar;
    }

    public DebugManager getDebugMgr() {
        return theDebugMgr;
    }

    /**
     * Build the main panel
     * @return the main panel
     */
    protected abstract JComponent buildMainPanel();

    /**
     * Obtain the frame name
     * @return the frame name
     */
    protected abstract String getFrameName();

    /**
     * Obtain the Help Module
     * @return the help module
     * @throws ModelException
     */
    protected abstract HelpModule getHelpModule() throws ModelException;

    /**
     * Constructor
     * @throws ModelException
     */
    protected MainWindow() throws ModelException {
        /* Create the debug manager */
        theDebugMgr = new DebugManager();

        /* Create the Executor service */
        theExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * Build the main window
     * @param pView the Data view
     * @throws ModelException
     */
    public void buildMainWindow(DataControl<T> pView) throws ModelException {
        JPanel myProgress;
        JPanel myStatus;
        JComponent myMainPanel;

        /* Store the view */
        theView = pView;

        /* Create the new status bar */
        theStatusBar = new StatusBar(this);
        myProgress = theStatusBar.getProgressPanel();
        myProgress.setVisible(false);
        myStatus = theStatusBar.getStatusPanel();
        myStatus.setVisible(false);
        theView.setStatusBar(theStatusBar);

        /* Create the panel */
        thePanel = new JPanel();

        /* Build the Main Panel */
        myMainPanel = buildMainPanel();

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                myLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(myMainPanel, GroupLayout.Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(myProgress, GroupLayout.Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)
                                        .addComponent(myStatus, GroupLayout.Alignment.LEADING,
                                                GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
                                                Short.MAX_VALUE)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(
                GroupLayout.Alignment.TRAILING,
                myLayout.createSequentialGroup().addComponent(myStatus)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(myProgress)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(myMainPanel)
                        .addContainerGap()));

        /* Create the frame */
        theFrame = new JFrame(getFrameName());

        /* Attach the panel to the frame */
        thePanel.setOpaque(true);
        theFrame.setContentPane(thePanel);

        /* Build the Main Menu */
        buildMainMenu();
    }

    /**
     * Build Main Menu
     */
    protected void buildMainMenu() {
        /* Create the menu bar */
        theMainMenu = new JMenuBar();

        /* Add Data Menu Items */
        theDataMenu = new JMenu("Data");
        addDataMenuItems(theDataMenu);
        theMainMenu.add(theDataMenu);

        /* Add Backup Menu Items */
        theBackupMenu = new JMenu("Backup");
        addBackupMenuItems(theBackupMenu);
        theMainMenu.add(theBackupMenu);

        /* Add Security Menu Items */
        theSecureMenu = new JMenu("Security");
        addSecurityMenuItems(theSecureMenu);
        theMainMenu.add(theSecureMenu);

        /* Add Help Menu items */
        theHelpMenu = new JMenu("Help");
        addHelpMenuItems(theHelpMenu);
        theMainMenu.add(Box.createHorizontalGlue());
        theMainMenu.add(theHelpMenu);

        /* Add the Menu bar */
        theFrame.setJMenuBar(theMainMenu);
    }

    /**
     * Add Data Menu items
     * @param pMenu the menu
     */
    protected void addDataMenuItems(JMenu pMenu) {
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
     * Add Backup Menu items
     * @param pMenu the menu
     */
    protected void addBackupMenuItems(JMenu pMenu) {
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
     * Add Security Menu items
     * @param pMenu the menu
     */
    protected void addSecurityMenuItems(JMenu pMenu) {
        /* Add Standard Security menu items */
        theUpdatePass = new JMenuItem("Update Password");
        theUpdatePass.addActionListener(this);
        pMenu.add(theUpdatePass);
        theRenewSec = new JMenuItem("Renew Security");
        theRenewSec.addActionListener(this);
        pMenu.add(theRenewSec);
    }

    /**
     * Add Help Menu items
     * @param pMenu the menu
     */
    protected void addHelpMenuItems(JMenu pMenu) {
        /* Create the menu items */
        theHelpMgr = new JMenuItem("Help");
        theHelpMgr.addActionListener(this);
        pMenu.add(theHelpMgr);
        theShowDebug = new JMenuItem("Debug");
        theShowDebug.addActionListener(this);
        pMenu.add(theShowDebug);
    }

    /* Make the frame */
    public void makeFrame() throws ModelException {
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
     * Window Close Adapter
     */
    private class WindowClose extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent evt) {
            Object o = evt.getSource();

            /* If this is the frame that is closing down */
            if (o == theFrame) {
                /* If we have updates or changes */
                if ((hasUpdates()) || (hasChanges())) {
                    /* Ask whether to continue */
                    int myOption = JOptionPane.showConfirmDialog(theFrame, "Discard unsaved data changes?",
                            "Confirm Close", JOptionPane.YES_NO_OPTION);

                    /* Ignore if no was responded */
                    if (myOption != JOptionPane.YES_OPTION)
                        return;
                }

                /* terminate the executor */
                theExecutor.shutdown();

                /* Dispose of the debug/help Windows if they exist */
                if (theDebugWdw != null)
                    theDebugWdw.dispose();
                if (theHelpWdw != null)
                    theHelpWdw.dispose();

                /* Dispose of the frame */
                theFrame.dispose();

                /* Exit the application */
                System.exit(0);
            }

            /* else if this is the Debug Window shutting down */
            else if (o == theDebugWdw) {
                /* Re-enable the help menu item */
                theShowDebug.setEnabled(true);
                theDebugWdw.dispose();
                theDebugWdw = null;

                /* Notify debug manager */
                theDebugMgr.declareWindow(null);
            }

            /* else if this is the Help Window shutting down */
            else if (o == theHelpWdw) {
                /* Re-enable the help menu item */
                theHelpMgr.setEnabled(true);
                theHelpWdw.dispose();
                theHelpWdw = null;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        Object o = evt.getSource();

        /* If this event relates to the Write Backup item */
        if (o == theWriteBackup) {
            /* Start a write backup operation */
            writeBackup();
        }

        /* If this event relates to the Write Extract item */
        else if (o == theWriteExtract) {
            /* Start a write extract operation */
            writeExtract();
        }

        /* If this event relates to the Save Database item */
        else if (o == theSaveDBase) {
            /* Start a store database operation */
            storeDatabase();
        }

        /* If this event relates to the Load Database item */
        else if (o == theLoadDBase) {
            /* Start a load database operation */
            loadDatabase();
        }

        /* If this event relates to the Create Database item */
        else if (o == theCreateDBase) {
            /* Start a load database operation */
            createDatabase();
        }

        /* If this event relates to the Purge Database item */
        else if (o == thePurgeDBase) {
            /* Start a load database operation */
            purgeDatabase();
        }

        /* If this event relates to the Load backup item */
        else if (o == theLoadBackup) {
            /* Start a restore backup operation */
            restoreBackup();
        }

        /* If this event relates to the Load extract item */
        else if (o == theLoadExtract) {
            /* Start a load backup operation */
            loadExtract();
        }

        /* If this event relates to the Update Password item */
        else if (o == theUpdatePass) {
            /* Start an Update Password operation */
            updatePassword();
        }

        /* If this event relates to the Renew Security item */
        else if (o == theRenewSec) {
            /* Start a reNew Security operation */
            reNewSecurity();
        }

        /* If this event relates to the Display Debug item */
        else if (o == theShowDebug) {
            /* Open the debug window */
            displayDebug();
        }

        /* If this event relates to the Display Help item */
        else if (o == theHelpMgr) {
            /* Open the help window */
            displayHelp();
        }
    }

    /* Set visibility */
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

    /* Finish Thread */
    public void finishThread() {
        theThread = null;
        setVisibility();
    }

    /* Handle cancel command */
    public void performCancel() {
        if (theThread != null)
            theThread.cancel(false);
    }

    /**
     * Has the underlying data got changes
     * @return true/false
     */
    protected boolean hasChanges() {
        return theView.getData().hasUpdates();
    }

    /**
     * Has the window got updates
     * @return true/false
     */
    protected abstract boolean hasUpdates();

    /**
     * Is a worker active
     * @return true/false
     */
    protected boolean hasWorker() {
        return (theThread != null);
    }

    /**
     * Start a thread
     * @param pThread the thread to start
     */
    protected void startThread(WorkerThread<?> pThread) {
        /* Execute the thread and record it */
        theExecutor.execute(pThread);
        theThread = pThread;

        /* Adjust visible threads */
        setVisibility();
    }

    /* Load Database */
    private void loadDatabase() {
        LoadDatabase<T> myThread;

        /* Create the worker thread */
        myThread = new LoadDatabase<T>(theView);
        startThread(myThread);
    }

    /* Store Database */
    private void storeDatabase() {
        StoreDatabase<T> myThread;

        /* Create the worker thread */
        myThread = new StoreDatabase<T>(theView);
        startThread(myThread);
    }

    /* Create Database */
    private void createDatabase() {
        CreateDatabase<T> myThread;

        /* Create the worker thread */
        myThread = new CreateDatabase<T>(theView);
        startThread(myThread);
    }

    /* Purge Database */
    private void purgeDatabase() {
        PurgeDatabase<T> myThread;

        /* Create the worker thread */
        myThread = new PurgeDatabase<T>(theView);
        startThread(myThread);
    }

    /* Write Backup */
    private void writeBackup() {
        CreateBackup<T> myThread;

        /* Create the worker thread */
        myThread = new CreateBackup<T>(theView);
        startThread(myThread);
    }

    /* Restore Backup */
    private void restoreBackup() {
        LoadBackup<T> myThread;

        /* Create the worker thread */
        myThread = new LoadBackup<T>(theView);
        startThread(myThread);
    }

    /* Write Extract */
    private void writeExtract() {
        CreateExtract<T> myThread;

        /* Create the worker thread */
        myThread = new CreateExtract<T>(theView);
        startThread(myThread);
    }

    /* Load Extract */
    private void loadExtract() {
        LoadExtract<T> myThread;

        /* Create the worker thread */
        myThread = new LoadExtract<T>(theView);
        startThread(myThread);
    }

    /* Update Password */
    private void updatePassword() {
        UpdatePassword<T> myThread;

        /* Create the worker thread */
        myThread = new UpdatePassword<T>(theView);
        startThread(myThread);
    }

    /* Load Extract */
    private void reNewSecurity() {
        RenewSecurity<T> myThread;

        /* Create the worker thread */
        myThread = new RenewSecurity<T>(theView);
        startThread(myThread);
    }

    /* Display Debug */
    private void displayDebug() {
        try {
            /* Create the debug window */
            theDebugWdw = new DebugWindow(theFrame, theDebugMgr);

            /* Listen for its closure */
            theDebugWdw.addWindowListener(new WindowClose());

            /* Disable the menu item */
            theShowDebug.setEnabled(false);

            /* Display it */
            theDebugWdw.showDialog();
        } catch (Throwable e) {
        }
    }

    /* Display Help */
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
        } catch (Throwable e) {
        }
    }
}
