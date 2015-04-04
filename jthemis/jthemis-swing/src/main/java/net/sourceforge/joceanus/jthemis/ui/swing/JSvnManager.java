/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.ui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jgordianknot.manager.SecureManager;
import net.sourceforge.joceanus.jgordianknot.manager.swing.SwingSecureManager;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.swing.PreferencesPanel;
import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.swing.SwingViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.ViewerWindow;
import net.sourceforge.joceanus.jprometheus.preference.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.preference.SecurityPreferences;
import net.sourceforge.joceanus.jprometheus.preference.swing.JFieldPreferences;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnableTabbed;
import net.sourceforge.joceanus.jthemis.git.data.GitPreferences;
import net.sourceforge.joceanus.jthemis.git.data.GitRepository;
import net.sourceforge.joceanus.jthemis.jira.data.JiraPreferences;
import net.sourceforge.joceanus.jthemis.scm.data.ScmBranch.ScmBranchOpType;
import net.sourceforge.joceanus.jthemis.svn.data.SubVersionPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.SvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.SvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.SvnTag;
import net.sourceforge.joceanus.jthemis.svn.data.SvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.threads.swing.CreateBranchTags;
import net.sourceforge.joceanus.jthemis.threads.swing.CreateGitRepo;
import net.sourceforge.joceanus.jthemis.threads.swing.CreateNewBranch;
import net.sourceforge.joceanus.jthemis.threads.swing.CreateTagExtract;
import net.sourceforge.joceanus.jthemis.threads.swing.CreateWorkingCopy;
import net.sourceforge.joceanus.jthemis.threads.swing.DiscoverData;
import net.sourceforge.joceanus.jthemis.threads.swing.RevertWorkingCopy;
import net.sourceforge.joceanus.jthemis.threads.swing.SubversionBackup;
import net.sourceforge.joceanus.jthemis.threads.swing.SubversionRestore;
import net.sourceforge.joceanus.jthemis.threads.swing.UpdateWorkingCopy;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Top level JSvnManager window.
 */
public final class JSvnManager {
    /**
     * The Base component.
     */
    private static final String BASE_COMP = "jmoneywise";

    /**
     * The Base version.
     */
    private static final String BASE_BRANCH = "v1.2.1";

    /**
     * The Frame.
     */
    private final JFrame theFrame;

    /**
     * The Preference Manager.
     */
    private final PreferenceManager thePrefMgr;

    /**
     * The Data Manager.
     */
    private final SwingViewerManager theDataMgr;

    /**
     * The Security Manager.
     */
    private final SecureManager theSecureMgr;

    /**
     * Preferences.
     */
    private final SubVersionPreferences thePreferences;

    /**
     * The DataManager menuItem.
     */
    private final JMenuItem theShowDataMgr;

    /**
     * The Tasks menu.
     */
    private final JMenu theTasks;

    /**
     * The ExtractTag menuItem.
     */
    private final JMenuItem theExtractTag;

    /**
     * The CreateWorkingCopy menuItem.
     */
    private final JMenuItem theCreateWC;

    /**
     * The UpdateWorkingCopy menuItem.
     */
    private final JMenuItem theUpdateWC;

    /**
     * The RevertWorkingCopy menuItem.
     */
    private final JMenuItem theRevertWC;

    /**
     * The CreateBranchTags menuItem.
     */
    private final JMenuItem theCreateBrnTags;

    /**
     * The CreateNewBranch menuItem.
     */
    private final JMenuItem theCreateNewBrn;

    /**
     * The createGit menu.
     */
    private final JMenu theCreateGit;

    /**
     * The BackupSvn menuItem.
     */
    private final JMenuItem theBackupSvn;

    /**
     * The RestoreSvn menuItem.
     */
    private final JMenuItem theRestoreSvn;

    /**
     * The Started data window.
     */
    private ViewerWindow theDataWdw = null;

    /**
     * The Window Close handler.
     */
    private WindowClose theCloseHandler = new WindowClose();

    /**
     * The repository.
     */
    private SvnRepository theRepository;

    /**
     * The working copy set.
     */
    private SvnWorkingCopySet theWorkingSet;

    /**
     * Status panel.
     */
    private final JSvnStatusWindow theStatusPanel;

    /**
     * Constructor.
     * @throws JOceanusException on error
     */
    protected JSvnManager() throws JOceanusException {
        /* Create the data manager */
        theDataMgr = new SwingViewerManager();

        /* Create the Tabbed Pane */
        JEnableTabbed myTabs = new JEnableTabbed();

        /* Create the preference manager */
        thePrefMgr = new PreferenceManager();
        thePreferences = thePrefMgr.getPreferenceSet(SubVersionPreferences.class);

        /* Access the Security Preferences */
        SecurityPreferences mySecurity = thePrefMgr.getPreferenceSet(SecurityPreferences.class);

        /* Create the Secure Manager */
        theSecureMgr = new SwingSecureManager(mySecurity.getParameters());

        /* Create the frame */
        theFrame = new JFrame(JSvnManager.class.getSimpleName());

        /* Create the panel */
        theStatusPanel = new JSvnStatusWindow(this);

        /* Create the Preferences Tab */
        JFieldPreferences myFieldPrefs = thePrefMgr.getPreferenceSet(JFieldPreferences.class);
        JFieldManager myFieldMgr = new JFieldManager(theDataMgr, myFieldPrefs.getConfiguration());
        ViewerEntry myMaintEntry = theDataMgr.newEntry("Maintenance");
        PreferencesPanel myPrefPanel = new PreferencesPanel(thePrefMgr, myFieldMgr, theDataMgr, myMaintEntry);
        myTabs.addTab("Status", theStatusPanel);
        myTabs.addTab("Preferences", myPrefPanel);

        /* Add interesting preferences */
        thePrefMgr.getPreferenceSet(BackupPreferences.class);
        thePrefMgr.getPreferenceSet(JiraPreferences.class);
        thePrefMgr.getPreferenceSet(SubVersionPreferences.class);
        thePrefMgr.getPreferenceSet(GitPreferences.class);

        /* Create the menu bar and listener */
        JMenuBar myMainMenu = new JMenuBar();
        MenuListener myMenuListener = new MenuListener();

        /* Create the Tasks menu */
        theTasks = new JMenu("Tasks");
        myMainMenu.add(theTasks);

        /* Create the JDataWindow menuItem */
        theShowDataMgr = new JMenuItem("DataViewer");
        theShowDataMgr.addActionListener(myMenuListener);
        myMainMenu.add(theShowDataMgr);

        /* Create the CreateWC menuItem */
        theCreateWC = new JMenuItem("CreateWorkingCopy");
        theCreateWC.addActionListener(myMenuListener);
        theTasks.add(theCreateWC);

        /* Create the ExtractTag menuItem */
        theExtractTag = new JMenuItem("CreateTagExtract");
        theExtractTag.addActionListener(myMenuListener);
        theTasks.add(theExtractTag);

        /* Create the UpdateWC menuItem */
        theUpdateWC = new JMenuItem("UpdateWorkingCopy");
        theUpdateWC.addActionListener(myMenuListener);
        theTasks.add(theUpdateWC);

        /* Create the RevertWC menuItem */
        theRevertWC = new JMenuItem("RevertWorkingCopy");
        theRevertWC.addActionListener(myMenuListener);
        theTasks.add(theRevertWC);

        /* Create the CreateBranchTags menuItem */
        theCreateBrnTags = new JMenuItem("CreateBranchTags");
        theCreateBrnTags.addActionListener(myMenuListener);
        theTasks.add(theCreateBrnTags);

        /* Create the CreateNewBranch menuItem */
        theCreateNewBrn = new JMenuItem("CreateNewBranch");
        theCreateNewBrn.addActionListener(myMenuListener);
        theTasks.add(theCreateNewBrn);

        /* Create the GitRepository */
        theCreateGit = new JMenu("CreateGitRepo");
        theCreateGit.setEnabled(false);
        theTasks.add(theCreateGit);

        /* Create the BackupSvn menuItem */
        theBackupSvn = new JMenuItem("BackupSubVersion");
        theBackupSvn.addActionListener(myMenuListener);
        theTasks.add(theBackupSvn);

        /* Create the RestoreSvn menuItem */
        theRestoreSvn = new JMenuItem("RestoreSubVersion");
        theRestoreSvn.addActionListener(myMenuListener);
        theTasks.add(theRestoreSvn);

        /* Add the Menu bar */
        theFrame.setJMenuBar(myMainMenu);

        /* Attach the panel to the frame */
        theStatusPanel.setOpaque(true);
        theFrame.setContentPane(myTabs);
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(theCloseHandler);

        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Create and run discoverData thread */
        DiscoverData myThread = new DiscoverData(theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Obtain preference manager.
     * @return the preference manager
     */
    protected PreferenceManager getPreferenceMgr() {
        return thePrefMgr;
    }

    /**
     * Obtain secure manager.
     * @return the secure manager
     */
    protected SecureManager getSecureMgr() {
        return theSecureMgr;
    }

    /**
     * Obtain frame.
     * @return the frame
     */
    protected JFrame getFrame() {
        return theFrame;
    }

    /**
     * Declare subversion data.
     * @param pData the discover thread
     */
    protected void setSubversionData(final DiscoverData pData) {
        /* Declare repository to data manager */
        ViewerEntry myRepEntry = theDataMgr.newEntry("SvnRepository");
        myRepEntry.addAsRootChild();
        theRepository = pData.getRepository();
        myRepEntry.setObject(theRepository);
        myRepEntry.setFocus();

        /* Declare WorkingCopySet to data manager */
        ViewerEntry mySetEntry = theDataMgr.newEntry("WorkingSet");
        mySetEntry.addAsRootChild();
        theWorkingSet = pData.getWorkingCopySet();
        mySetEntry.setObject(theWorkingSet);

        /* Declare Extract Plans to data manager */
        ViewerEntry myPlanEntry = theDataMgr.newEntry("ExtractPlans");
        myPlanEntry.addAsRootChild();
        pData.declareExtractPlans(theDataMgr, myPlanEntry);

        /* Enable the git menu */
        theCreateGit.setEnabled(true);

        /* If we have a repository */
        if (theRepository != null) {
            /* Loop through the components */
            Iterator<SvnComponent> myIterator = theRepository.getComponents().iterator();
            while (myIterator.hasNext()) {
                SvnComponent myComp = myIterator.next();

                /* Create a new menu item for the component */
                ItemAction myAction = new ItemAction(myComp);
                JMenuItem myItem = new JMenuItem(myAction);
                theCreateGit.add(myItem);
            }
        }

        /* Enable the git menu if we have components */
        if (theCreateGit.getItemCount() > 0) {
            theCreateGit.setEnabled(true);
        }

        /* If we have an error */
        JOceanusException myError = pData.getError();
        if (myError != null) {
            ViewerEntry myErrorEntry = theDataMgr.newEntry("Error");
            myErrorEntry.addAsRootChild();
            myErrorEntry.setObject(myError);
            myErrorEntry.setFocus();
        }
    }

    /**
     * Declare git data.
     * @param pGit the git thread
     */
    protected void setGitData(final CreateGitRepo pGit) {
        /* Declare repository to data manager */
        ViewerEntry myRepEntry = theDataMgr.newEntry("GitRepo");
        myRepEntry.addAsRootChild();
        GitRepository myRepo = pGit.getGitRepo();
        myRepEntry.setObject(myRepo);
        myRepEntry.setFocus();
    }

    /**
     * Complete thread task.
     * @param pTask the task that has completed
     */
    public void completeTask(final Object pTask) {
        /* If this is the discoverData thread */
        if (pTask instanceof DiscoverData) {
            /* Access correctly */
            DiscoverData myThread = (DiscoverData) pTask;

            /* Report data to manager */
            setSubversionData(myThread);
        }

        /* If this is the discoverData thread */
        if (pTask instanceof CreateGitRepo) {
            /* Access correctly */
            CreateGitRepo myThread = (CreateGitRepo) pTask;

            /* Report data to manager */
            setGitData(myThread);
        }

        /* Enable other tasks */
        theTasks.setEnabled(true);
    }

    /**
     * Create and run the CheckOutWC thread.
     */
    private void runCheckOutWC() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.separator;

        /* Create and run createWorkingCopy thread */
        SvnBranch myBranch = theRepository.locateBranch(BASE_COMP, "v1.2.0");
        SvnBranch[] myList = new SvnBranch[]
        { myBranch };
        CreateWorkingCopy myThread = new CreateWorkingCopy(myList, SVNRevision.HEAD, new File(myPath + "TestWC"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the TagExtract thread.
     */
    private void runCreateTagExtract() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.separator;

        /* Create and run createTagExtract thread */
        SvnTag myTag = theRepository.locateTag(BASE_COMP, BASE_BRANCH, 1);
        SvnTag[] myList = new SvnTag[]
        { myTag };
        CreateTagExtract myThread = new CreateTagExtract(myList, new File(myPath + "TestXT"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the UpdateWC thread.
     */
    private void runUpdateWC() {
        /* Create and run updateWorkingCopy thread */
        UpdateWorkingCopy myThread = new UpdateWorkingCopy(theWorkingSet, theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the RevertWC thread.
     */
    private void runRevertWC() {
        /* Create and run revertWorkingCopy thread */
        RevertWorkingCopy myThread = new RevertWorkingCopy(theWorkingSet, theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the CreateBranchTags thread.
     */
    private void runCreateBranchTags() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.separator;

        /* Create and run createBranchTags thread */
        SvnBranch myBranch = theRepository.locateBranch(BASE_COMP, BASE_BRANCH);
        SvnBranch[] myList = new SvnBranch[]
        { myBranch };
        CreateBranchTags myThread = new CreateBranchTags(myList, new File(myPath + "TestBT"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the CreateNewBranch thread.
     */
    private void runCreateNewBranch() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.separator;

        /* Create and run createBranchTags thread */
        SvnTag myTag = theRepository.locateTag(BASE_COMP, "v1.0.0", 1);
        SvnTag[] myList = new SvnTag[]
        { myTag };
        CreateNewBranch myThread = new CreateNewBranch(myList, ScmBranchOpType.MAJOR, new File(myPath + "TestNB"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Backup subversion.
     */
    private void backupSubversion() {
        /* Create the worker thread */
        SubversionBackup myThread = new SubversionBackup(theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Restore subversion.
     */
    private void restoreSubversion() {
        /* Create the worker thread */
        SubversionRestore myThread = new SubversionRestore(theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Run create GitRepo.
     * @param pSource the source component
     */
    private void createGitRepo(final SvnComponent pSource) {
        /* Create the worker thread */
        CreateGitRepo myThread = new CreateGitRepo(theStatusPanel, pSource);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * MenuListener class.
     */
    private final class MenuListener
            implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this is the DataManager window */
            if (theShowDataMgr.equals(o)) {
                /* Create the data window */
                theDataWdw = new ViewerWindow(theFrame, theDataMgr);

                /* Listen for its closure */
                theDataWdw.addWindowListener(theCloseHandler);

                /* Disable the menu item */
                theShowDataMgr.setEnabled(false);

                /* Display it */
                theDataWdw.showDialog();

                /* If this is the CreateWC task */
            } else if (theCreateWC.equals(o)) {
                /* run the thread */
                runCheckOutWC();

                /* If this is the ExtractTag task */
            } else if (theExtractTag.equals(o)) {
                /* run the thread */
                runCreateTagExtract();

                /* If this is the UpdateWC task */
            } else if (theUpdateWC.equals(o)) {
                /* run the thread */
                runUpdateWC();

                /* If this is the RevertWC task */
            } else if (theRevertWC.equals(o)) {
                /* run the thread */
                runRevertWC();

                /* If this is the CreateBranchTags task */
            } else if (theCreateBrnTags.equals(o)) {
                /* run the thread */
                runCreateBranchTags();

                /* If this is the CreateNewBranch task */
            } else if (theCreateNewBrn.equals(o)) {
                /* run the thread */
                runCreateNewBranch();

                /* If this event relates to the Subversion backup item */
            } else if (theBackupSvn.equals(o)) {
                /* Start a write backup operation */
                backupSubversion();

                /* If this event relates to the Subversion restore item */
            } else if (theRestoreSvn.equals(o)) {
                /* Start a restore backup operation */
                restoreSubversion();
            }
        }
    }

    /**
     * Item action class.
     */
    private final class ItemAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 1093025312352957763L;

        /**
         * Item.
         */
        private final transient SvnComponent theSource;

        /**
         * Constructor.
         * @param pSource the source component
         */
        private ItemAction(final SvnComponent pSource) {
            super(pSource.getName());
            theSource = pSource;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* run the thread */
            createGitRepo(theSource);
        }
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
                /* terminate the executor */
                theStatusPanel.shutdown();

                /* Dispose of the data/help Windows if they exist */
                if (theDataWdw != null) {
                    theDataWdw.dispose();
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
            }
        }
    }
}
