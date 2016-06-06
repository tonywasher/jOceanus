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
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.newviewer.swing.MetisSwingViewerWindow;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTabPaneManager;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitPreference.ThemisGitPreferences;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraPreference.ThemisJiraPreferences;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch.ScmBranchOpType;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnTag;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisCreateBranchTags;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisCreateGitRepo;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisCreateNewBranch;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisCreateTagExtract;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisCreateWorkingCopy;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisDiscoverData;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisRevertWorkingCopy;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisSubversionBackup;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisSubversionRestore;
import net.sourceforge.joceanus.jthemis.threads.swing.ThemisUpdateWorkingCopy;

/**
 * Top level JSvnManager window.
 */
public final class ThemisSvnManager {
    /**
     * The Base component.
     */
    private static final String BASE_COMP = "jmoneywise";

    /**
     * The Base version.
     */
    private static final String BASE_BRANCH = "v1.2.1";

    /**
     * The GUI Factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * The Frame.
     */
    private final JFrame theFrame;

    /**
     * The Preference Manager.
     */
    private final MetisPreferenceManager thePrefMgr;

    /**
     * The Viewer Manager.
     */
    private final MetisViewerManager theViewerMgr;

    /**
     * The Security Manager.
     */
    private final GordianHashManager theSecureMgr;

    /**
     * Preferences.
     */
    private final ThemisSvnPreferences thePreferences;

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
     * The Date entry.
     */
    private final MetisViewerEntry theDataEntry;

    /**
     * The GitRepo entry.
     */
    private final MetisViewerEntry theGitEntry;

    /**
     * The Error entry.
     */
    private final MetisViewerEntry theErrorEntry;

    /**
     * The data window.
     */
    private final MetisSwingViewerWindow theDataWdw;

    /**
     * The Window Close handler.
     */
    private WindowClose theCloseHandler = new WindowClose();

    /**
     * The repository.
     */
    private ThemisSvnRepository theRepository;

    /**
     * The working copy set.
     */
    private SvnWorkingCopySet theWorkingSet;

    /**
     * Status panel.
     */
    private final ThemisSvnStatusWindow theStatusPanel;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    protected ThemisSvnManager() throws OceanusException {
        /* Create the Toolkit */
        MetisSwingToolkit myToolkit = new MetisSwingToolkit();

        /* Access GuiFactory/Preference Manager */
        theGuiFactory = myToolkit.getGuiFactory();
        thePrefMgr = myToolkit.getPreferenceManager();
        thePreferences = thePrefMgr.getPreferenceSet(ThemisSvnPreferences.class);

        /* Access the Security/Viewer Manager */
        theSecureMgr = myToolkit.getSecurityManager();
        theViewerMgr = myToolkit.getViewerManager();

        /* Access error entry */
        theErrorEntry = theViewerMgr.getStandardEntry(MetisViewerStandardEntry.ERROR);
        theErrorEntry.setVisible(false);

        /* Access date entry */
        theDataEntry = theViewerMgr.getStandardEntry(MetisViewerStandardEntry.DATA);
        theGitEntry = theViewerMgr.newEntry(theDataEntry, "GitRepo");
        theGitEntry.setVisible(false);

        /* Create the frame */
        theFrame = new JFrame(ThemisSvnManager.class.getSimpleName());
        theGuiFactory.setFrame(theFrame);

        /* Create the Tabbed Pane */
        TethysSwingTabPaneManager myTabs = theGuiFactory.newTabPane();

        /* Create the panel */
        theStatusPanel = new ThemisSvnStatusWindow(this);

        /* Create the Preferences Tab */
        MetisPreferenceView<JComponent, Icon> myPrefPanel = new MetisPreferenceView<>(theGuiFactory, thePrefMgr);
        myTabs.addTabItem("Status", theStatusPanel);
        myTabs.addTabItem("Preferences", myPrefPanel);

        /* Add interesting preferences */
        thePrefMgr.getPreferenceSet(PrometheusBackupPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisJiraPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisSvnPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisGitPreferences.class);

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
        theFrame.setContentPane(myTabs.getNode());
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(theCloseHandler);

        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Create the data window */
        theDataWdw = new MetisSwingViewerWindow(theGuiFactory, theViewerMgr);
        theDataWdw.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theShowDataMgr.setEnabled(true));

        /* Create and run discoverData thread */
        ThemisDiscoverData myThread = new ThemisDiscoverData(theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Obtain preference manager.
     * @return the preference manager
     */
    protected MetisPreferenceManager getPreferenceMgr() {
        return thePrefMgr;
    }

    /**
     * Obtain GUI factory.
     * @return the factory
     */
    protected TethysSwingGuiFactory getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain secure manager.
     * @return the secure manager
     */
    protected GordianHashManager getSecureMgr() {
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
    protected void setSubversionData(final ThemisDiscoverData pData) {
        /* Declare repository to data manager */
        MetisViewerEntry myRepEntry = theViewerMgr.newEntry(theDataEntry, "SvnRepository");
        theRepository = pData.getRepository();
        myRepEntry.setObject(theRepository);
        myRepEntry.setFocus();

        /* Declare WorkingCopySet to data manager */
        MetisViewerEntry mySetEntry = theViewerMgr.newEntry(theDataEntry, "WorkingSet");
        theWorkingSet = pData.getWorkingCopySet();
        mySetEntry.setObject(theWorkingSet);

        /* Declare Extract Plans to data manager */
        MetisViewerEntry myPlanEntry = theViewerMgr.newEntry(theDataEntry, "ExtractPlans");
        pData.declareExtractPlans(theViewerMgr, myPlanEntry);

        /* Enable the git menu */
        theCreateGit.setEnabled(true);

        /* If we have a repository */
        if (theRepository != null) {
            /* Loop through the components */
            Iterator<ThemisSvnComponent> myIterator = theRepository.getComponents().iterator();
            while (myIterator.hasNext()) {
                ThemisSvnComponent myComp = myIterator.next();

                /* Create a new menu item for the component */
                ItemAction myAction = new ItemAction(myComp);
                JMenuItem myItem = new JMenuItem(myAction);
                theCreateGit.add(myItem);
            }
        }

        /* Enable the GIT menu if we have components */
        if (theCreateGit.getItemCount() > 0) {
            theCreateGit.setEnabled(true);
        }

        /* process any error */
        processError(pData.getError());
    }

    /**
     * Declare git data.
     * @param pGit the git thread
     */
    protected void setGitData(final ThemisCreateGitRepo pGit) {
        /* Declare repository to data manager */
        ThemisGitRepository myRepo = pGit.getGitRepo();
        theGitEntry.setObject(myRepo);
        theGitEntry.setVisible(true);
        theGitEntry.setFocus();

        /* process any error */
        processError(pGit.getError());
    }

    /**
     * process error.
     * @param pError the error
     */
    private void processError(final OceanusException pError) {
        /* If we have an error */
        if (pError != null) {
            /* Set data and focus */
            theErrorEntry.setObject(pError);
            theErrorEntry.setVisible(true);
            theErrorEntry.setFocus();

            /* else hide any error entry */
        } else if (theErrorEntry != null) {
            theErrorEntry.setVisible(false);
        }
    }

    /**
     * Complete thread task.
     * @param pTask the task that has completed
     */
    public void completeTask(final Object pTask) {
        /* If this is the discoverData thread */
        if (pTask instanceof ThemisDiscoverData) {
            /* Access correctly */
            ThemisDiscoverData myThread = (ThemisDiscoverData) pTask;

            /* Report data to manager */
            setSubversionData(myThread);
        }

        /* If this is the discoverData thread */
        if (pTask instanceof ThemisCreateGitRepo) {
            /* Access correctly */
            ThemisCreateGitRepo myThread = (ThemisCreateGitRepo) pTask;

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
        String myPath = thePreferences.getStringValue(ThemisSvnPreferenceKey.BUILD) + File.separator;

        /* Create and run createWorkingCopy thread */
        ThemisSvnBranch myBranch = theRepository.locateBranch(BASE_COMP, "v1.2.0");
        ThemisSvnBranch[] myList = new ThemisSvnBranch[]
        { myBranch };
        ThemisCreateWorkingCopy myThread = new ThemisCreateWorkingCopy(myList, SVNRevision.HEAD, new File(myPath + "TestWC"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the TagExtract thread.
     */
    private void runCreateTagExtract() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(ThemisSvnPreferenceKey.BUILD) + File.separator;

        /* Create and run createTagExtract thread */
        ThemisSvnTag myTag = theRepository.locateTag(BASE_COMP, BASE_BRANCH, 1);
        ThemisSvnTag[] myList = new ThemisSvnTag[]
        { myTag };
        ThemisCreateTagExtract myThread = new ThemisCreateTagExtract(myList, new File(myPath + "TestXT"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the UpdateWC thread.
     */
    private void runUpdateWC() {
        /* Create and run updateWorkingCopy thread */
        ThemisUpdateWorkingCopy myThread = new ThemisUpdateWorkingCopy(theWorkingSet, theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the RevertWC thread.
     */
    private void runRevertWC() {
        /* Create and run revertWorkingCopy thread */
        ThemisRevertWorkingCopy myThread = new ThemisRevertWorkingCopy(theWorkingSet, theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the CreateBranchTags thread.
     */
    private void runCreateBranchTags() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(ThemisSvnPreferenceKey.BUILD) + File.separator;

        /* Create and run createBranchTags thread */
        ThemisSvnBranch myBranch = theRepository.locateBranch(BASE_COMP, BASE_BRANCH);
        ThemisSvnBranch[] myList = new ThemisSvnBranch[]
        { myBranch };
        ThemisCreateBranchTags myThread = new ThemisCreateBranchTags(myList, new File(myPath + "TestBT"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the CreateNewBranch thread.
     */
    private void runCreateNewBranch() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(ThemisSvnPreferenceKey.BUILD) + File.separator;

        /* Create and run createBranchTags thread */
        ThemisSvnTag myTag = theRepository.locateTag(BASE_COMP, "v1.0.0", 1);
        ThemisSvnTag[] myList = new ThemisSvnTag[]
        { myTag };
        ThemisCreateNewBranch myThread = new ThemisCreateNewBranch(myList, ScmBranchOpType.MAJOR, new File(myPath + "TestNB"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Backup subversion.
     */
    private void backupSubversion() {
        /* Create the worker thread */
        ThemisSubversionBackup myThread = new ThemisSubversionBackup(theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Restore subversion.
     */
    private void restoreSubversion() {
        /* Create the worker thread */
        ThemisSubversionRestore myThread = new ThemisSubversionRestore(theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Run create GitRepo.
     * @param pSource the source component
     */
    private void createGitRepo(final ThemisSvnComponent pSource) {
        /* Create the worker thread */
        ThemisCreateGitRepo myThread = new ThemisCreateGitRepo(theStatusPanel, pSource);
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
        private final transient ThemisSvnComponent theSource;

        /**
         * Constructor.
         * @param pSource the source component
         */
        private ItemAction(final ThemisSvnComponent pSource) {
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

                /* Dispose of the frame */
                theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                theFrame.dispose();
            }
        }
    }
}
