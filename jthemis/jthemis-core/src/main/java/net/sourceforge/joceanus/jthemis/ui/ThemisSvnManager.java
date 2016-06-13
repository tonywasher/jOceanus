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
package net.sourceforge.joceanus.jthemis.ui;

import java.io.File;
import java.util.Iterator;

import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerWindow;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadEvent;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager.TethysMenuSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
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
import net.sourceforge.joceanus.jthemis.threads.ThemisCreateBranchTags;
import net.sourceforge.joceanus.jthemis.threads.ThemisCreateGitRepo;
import net.sourceforge.joceanus.jthemis.threads.ThemisCreateNewBranch;
import net.sourceforge.joceanus.jthemis.threads.ThemisCreateTagExtract;
import net.sourceforge.joceanus.jthemis.threads.ThemisCreateWorkingCopy;
import net.sourceforge.joceanus.jthemis.threads.ThemisDiscoverData;
import net.sourceforge.joceanus.jthemis.threads.ThemisRevertWorkingCopy;
import net.sourceforge.joceanus.jthemis.threads.ThemisSubversionBackup;
import net.sourceforge.joceanus.jthemis.threads.ThemisSubversionRestore;
import net.sourceforge.joceanus.jthemis.threads.ThemisThreadId;
import net.sourceforge.joceanus.jthemis.threads.ThemisUpdateWorkingCopy;

/**
 * Top level SvnManager window.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class ThemisSvnManager<N, I> {
    /**
     * The Base component.
     */
    private static final String BASE_COMP = "jOceanus";

    /**
     * The Base version.
     */
    private static final String BASE_BRANCH = "v1.2.1";

    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

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
     * The Thread Manager.
     */
    private final MetisThreadManager<N, I> theThreadMgr;

    /**
     * Preferences.
     */
    private final ThemisSvnPreferences thePreferences;

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
     * The menuBar.
     */
    private final TethysMenuBarManager theMenuBar;

    /**
     * The tabs.
     */
    private final TethysTabPaneManager<N, I> theTabs;

    /**
     * The data window.
     */
    private final MetisViewerWindow<N, I> theDataWdw;

    /**
     * The repository.
     */
    private ThemisSvnRepository theRepository;

    /**
     * The working copy set.
     */
    private SvnWorkingCopySet theWorkingSet;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected ThemisSvnManager(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access GuiFactory/Preference Manager */
        theGuiFactory = pToolkit.getGuiFactory();
        thePrefMgr = pToolkit.getPreferenceManager();
        thePreferences = thePrefMgr.getPreferenceSet(ThemisSvnPreferences.class);

        /* Access the Security/Viewer Manager */
        theSecureMgr = pToolkit.getSecurityManager();
        theViewerMgr = pToolkit.getViewerManager();

        /* Access the thread manager */
        theThreadMgr = pToolkit.getThreadManager();

        /* Access error entry */
        theErrorEntry = theViewerMgr.getStandardEntry(MetisViewerStandardEntry.ERROR);
        theErrorEntry.setVisible(false);

        /* Access date entry */
        theDataEntry = theViewerMgr.getStandardEntry(MetisViewerStandardEntry.DATA);
        theGitEntry = theViewerMgr.newEntry(theDataEntry, "GitRepo");
        theGitEntry.setVisible(false);

        /* Create the Tabbed Pane */
        theTabs = theGuiFactory.newTabPane();

        /* Create the Preferences Tab */
        MetisPreferenceView<N, I> myPrefPanel = new MetisPreferenceView<>(theGuiFactory, thePrefMgr);
        theTabs.addTabItem("Status", theThreadMgr.getStatusManager());
        theTabs.addTabItem("Preferences", myPrefPanel);

        /* Add interesting preferences */
        thePrefMgr.getPreferenceSet(PrometheusBackupPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisJiraPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisSvnPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisGitPreferences.class);

        /* Create the menu bar */
        theMenuBar = theGuiFactory.newMenuBar();

        /* Create the Tasks menu */
        TethysMenuSubMenu<ThemisSvnMenuItem> myTasks = theMenuBar.newSubMenu(ThemisSvnMenuItem.TASKS);

        /* Create the Help menu */
        TethysMenuSubMenu<ThemisSvnMenuItem> myHelp = theMenuBar.newSubMenu(ThemisSvnMenuItem.HELP);

        /* Create the Viewer menuItem */
        myHelp.newMenuItem(ThemisSvnMenuItem.DATAVIEWER, e -> handleDataViewer());

        /* Create the menuItems */
        myTasks.newMenuItem(ThemisThreadId.CREATEWORKINGCOPY, e -> runCheckOutWC());
        myTasks.newMenuItem(ThemisThreadId.EXTRACTTAG, e -> runCreateTagExtract());
        myTasks.newMenuItem(ThemisThreadId.UPDATEWORKINGCOPY, e -> runUpdateWC());
        myTasks.newMenuItem(ThemisThreadId.REVERTWORKINGCOPY, e -> runRevertWC());
        myTasks.newMenuItem(ThemisThreadId.CREATETAG, e -> runCreateBranchTags());
        myTasks.newMenuItem(ThemisThreadId.CREATEBRANCH, e -> runCreateNewBranch());
        myTasks.newSubMenu(ThemisThreadId.CREATEGITREPO);
        myTasks.newMenuItem(ThemisThreadId.BACKUPSVN, e -> backupSubversion());
        myTasks.newMenuItem(ThemisThreadId.RESTORESVN, e -> restoreSubversion());
        theMenuBar.setEnabled(ThemisThreadId.CREATEGITREPO, false);

        /* Create the data window */
        theDataWdw = pToolkit.newViewerWindow();
        theDataWdw.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theMenuBar.setEnabled(ThemisSvnMenuItem.DATAVIEWER, true));

        /* Listen for thread completion */
        theThreadMgr.getEventRegistrar().addEventListener(MetisThreadEvent.THREADEND, e -> completeTask(e.getDetails()));

        /* Create and run discoverData thread */
        ThemisDiscoverData<N, I> myThread = new ThemisDiscoverData<>();
        runThread(myThread);
    }

    /**
     * Obtain preference manager.
     * @return the preference manager
     */
    protected MetisPreferenceManager getPreferenceMgr() {
        return thePrefMgr;
    }

    /**
     * Obtain secure manager.
     * @return the secure manager
     */
    protected GordianHashManager getSecureMgr() {
        return theSecureMgr;
    }

    /**
     * Obtain menuBar.
     * @return the menuBar
     */
    protected TethysMenuBarManager getMenuBar() {
        return theMenuBar;
    }

    /**
     * Obtain tabs.
     * @return the tabs
     */
    protected TethysTabPaneManager<N, I> getTabs() {
        return theTabs;
    }

    /**
     * Declare subversion data.
     * @param pData the discover thread
     */
    protected void setSubversionData(final ThemisDiscoverData<?, ?> pData) {
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

        /* Access the git menu */
        TethysMenuSubMenu<?> myMenu = theMenuBar.lookUpSubMenu(ThemisThreadId.CREATEGITREPO);
        myMenu.clearItems();

        /* If we have a repository */
        if (theRepository != null) {
            /* Loop through the components */
            Iterator<ThemisSvnComponent> myIterator = theRepository.getComponents().iterator();
            while (myIterator.hasNext()) {
                ThemisSvnComponent myComp = myIterator.next();

                /* Create a new menu item for the component */
                myMenu.newMenuItem(myComp, e -> createGitRepo(myComp));
            }
        }

        /* Enable the GIT menu if we have components */
        theMenuBar.setEnabled(ThemisThreadId.CREATEGITREPO, myMenu.countItems() > 0);
    }

    /**
     * Declare git data.
     * @param pGit the git thread
     */
    protected void setGitData(final ThemisCreateGitRepo<?, ?> pGit) {
        /* Declare repository to data manager */
        ThemisGitRepository myRepo = pGit.getGitRepo();
        theGitEntry.setObject(myRepo);
        theGitEntry.setVisible(true);
        theGitEntry.setFocus();
    }

    /**
     * Complete thread task.
     * @param pTask the task that has completed
     */
    public void completeTask(final Object pTask) {
        /* If this is the discoverData thread */
        if (pTask instanceof ThemisDiscoverData) {
            /* Access correctly */
            ThemisDiscoverData<?, ?> myThread = (ThemisDiscoverData<?, ?>) pTask;

            /* Report data to manager */
            setSubversionData(myThread);
        }

        /* If this is the discoverData thread */
        if (pTask instanceof ThemisCreateGitRepo) {
            /* Access correctly */
            ThemisCreateGitRepo<?, ?> myThread = (ThemisCreateGitRepo<?, ?>) pTask;

            /* Report data to manager */
            setGitData(myThread);
        }

        /* Enable other tasks */
        theMenuBar.setEnabled(ThemisSvnMenuItem.TASKS, true);
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
        ThemisCreateWorkingCopy<N, I> myThread = new ThemisCreateWorkingCopy<>(myList, SVNRevision.HEAD, new File(myPath + "TestWC"));
        runThread(myThread);
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
        ThemisCreateTagExtract<N, I> myThread = new ThemisCreateTagExtract<>(myList, new File(myPath + "TestXT"));
        runThread(myThread);
    }

    /**
     * Create and run the UpdateWC thread.
     */
    private void runUpdateWC() {
        /* Create and run updateWorkingCopy thread */
        ThemisUpdateWorkingCopy<N, I> myThread = new ThemisUpdateWorkingCopy<>(theWorkingSet);
        runThread(myThread);
    }

    /**
     * Create and run the RevertWC thread.
     */
    private void runRevertWC() {
        /* Create and run revertWorkingCopy thread */
        ThemisRevertWorkingCopy<N, I> myThread = new ThemisRevertWorkingCopy<>(theWorkingSet);
        runThread(myThread);
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
        ThemisCreateBranchTags<N, I> myThread = new ThemisCreateBranchTags<>(myList, new File(myPath + "TestBT"));
        runThread(myThread);
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
        ThemisCreateNewBranch<N, I> myThread = new ThemisCreateNewBranch<>(myList, ScmBranchOpType.MAJOR, new File(myPath + "TestNB"));
        runThread(myThread);
    }

    /**
     * Backup subversion.
     */
    private void backupSubversion() {
        /* Create the worker thread */
        ThemisSubversionBackup<N, I> myThread = new ThemisSubversionBackup<>();
        runThread(myThread);
    }

    /**
     * Restore subversion.
     */
    private void restoreSubversion() {
        /* Create the worker thread */
        ThemisSubversionRestore<N, I> myThread = new ThemisSubversionRestore<>();
        runThread(myThread);
    }

    /**
     * Run create GitRepo.
     * @param pSource the source component
     */
    private void createGitRepo(final ThemisSvnComponent pSource) {
        /* Create the worker thread */
        ThemisCreateGitRepo<N, I> myThread = new ThemisCreateGitRepo<>(pSource);
        runThread(myThread);
    }

    /**
     * Run thread.
     * @param pThread the thread
     */
    private void runThread(final MetisThread<?, N, I> pThread) {
        theMenuBar.setEnabled(ThemisSvnMenuItem.TASKS, false);
        theThreadMgr.startThread(pThread);
    }

    /**
     * Handle ViewerClosed.
     */
    private void handleDataViewer() {
        theMenuBar.setEnabled(ThemisSvnMenuItem.DATAVIEWER, false);
        theDataWdw.showDialog();
    }

    /**
     * Handle WindowClosed.
     */
    protected void handleWindowClosed() {
        theThreadMgr.shutdown();
    }
}
