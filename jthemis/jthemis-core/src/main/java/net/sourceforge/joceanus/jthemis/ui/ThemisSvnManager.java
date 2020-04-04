/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.ui;

import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadEvent;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysAbout;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager;
import net.sourceforge.joceanus.jtethys.ui.TethysMenuBarManager.TethysMenuSubMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitPreference.ThemisGitPreferences;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;
import net.sourceforge.joceanus.jthemis.jira.data.ThemisJiraPreference.ThemisJiraPreferences;
import net.sourceforge.joceanus.jthemis.sf.data.ThemisSfPreference.ThemisSfPreferences;
import net.sourceforge.joceanus.jthemis.threads.ThemisDiscoverData;
import net.sourceforge.joceanus.jthemis.threads.ThemisThreadId;

/**
 * Top level SvnManager window.
 */
public abstract class ThemisSvnManager {
    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory theGuiFactory;

    /**
     * The Preference Manager.
     */
    private final MetisPreferenceManager thePrefMgr;

    /**
     * The Viewer Manager.
     */
    private final MetisViewerManager theViewerMgr;

    /**
     * The Password Manager.
     */
    private final GordianPasswordManager thePasswordMgr;

    /**
     * The Thread Manager.
     */
    private final MetisThreadManager theThreadMgr;

    /**
     * The Date entry.
     */
    private final MetisViewerEntry theDataEntry;

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
    private final TethysTabPaneManager theTabs;

    /**
     * The data window.
     */
    private final MetisViewerWindow theDataWdw;

    /**
     * The about box.
     */
    private final TethysAbout theAboutBox;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected ThemisSvnManager(final PrometheusToolkit pToolkit) throws OceanusException {
        /* Access GuiFactory/Preference Manager */
        theGuiFactory = pToolkit.getGuiFactory();
        thePrefMgr = pToolkit.getPreferenceManager();

        /* Access the Password/Viewer Manager */
        thePasswordMgr = pToolkit.getPasswordManager();
        theViewerMgr = pToolkit.getViewerManager();

        /* Access the thread manager */
        theThreadMgr = pToolkit.getThreadManager();

        /* Access error entry */
        theErrorEntry = theViewerMgr.getStandardEntry(MetisViewerStandardEntry.ERROR);
        theErrorEntry.setVisible(false);

        /* Access data entry */
        theDataEntry = theViewerMgr.getStandardEntry(MetisViewerStandardEntry.DATA);

        /* Create the Tabbed Pane */
        theTabs = theGuiFactory.newTabPane();

        /* Create the Preferences Tab */
        final MetisPreferenceView myPrefPanel = new MetisPreferenceView(theGuiFactory, thePrefMgr);
        theTabs.addTabItem("Status", theThreadMgr.getStatusManager());
        theTabs.addTabItem("Preferences", myPrefPanel);

        /* Add interesting preferences */
        thePrefMgr.getPreferenceSet(ThemisJiraPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisGitPreferences.class);
        thePrefMgr.getPreferenceSet(ThemisSfPreferences.class);

        /* Create the menu bar */
        theMenuBar = theGuiFactory.newMenuBar();

        /* Create the Tasks menu */
        final TethysMenuSubMenu<ThemisSvnMenuItem> myTasks = theMenuBar.newSubMenu(ThemisSvnMenuItem.TASKS);

        /* Create the Help menu */
        final TethysMenuSubMenu<ThemisSvnMenuItem> myHelp = theMenuBar.newSubMenu(ThemisSvnMenuItem.HELP);

        /* Create the Viewer menuItem */
        myHelp.newMenuItem(ThemisSvnMenuItem.DATAVIEWER, e -> handleDataViewer());
        myHelp.newMenuItem(ThemisSvnMenuItem.ABOUT, e -> handleAboutBox());

        /* Create the menuItems */
        myTasks.newSubMenu(ThemisThreadId.CREATEGITREPO);
        theMenuBar.setEnabled(ThemisThreadId.CREATEGITREPO, false);

        /* Create the data window */
        theDataWdw = pToolkit.getToolkit().newViewerWindow();
        theDataWdw.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theMenuBar.setEnabled(ThemisSvnMenuItem.DATAVIEWER, true));

        /* Create the aboutBox */
        theAboutBox = theGuiFactory.newAboutBox();

        /* Listen for thread completion */
        theThreadMgr.getEventRegistrar().addEventListener(MetisThreadEvent.THREADEND, e -> completeTask(e.getDetails()));

        /* Create and run discoverData thread */
        final ThemisDiscoverData myThread = new ThemisDiscoverData();
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
     * Obtain password manager.
     * @return the password manager
     */
    protected GordianPasswordManager getPasswordMgr() {
        return thePasswordMgr;
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
    protected TethysTabPaneManager getTabs() {
        return theTabs;
    }

    /**
     * Declare subversion data.
     * @param pData the discover thread
     */
    protected void setSubversionData(final ThemisDiscoverData pData) {
        /* TODO clear data entries */

        /* Declare Git repositories to data manager */
        final MetisViewerEntry myGitEntry = theViewerMgr.newEntry(theDataEntry, "GitRepository");
        final ThemisGitRepository myGitRepository = pData.getGitRepository();
        myGitEntry.setObject(myGitRepository);


        /* Access the git menu */
        final TethysMenuSubMenu<?> myMenu = theMenuBar.lookUpSubMenu(ThemisThreadId.CREATEGITREPO);
        myMenu.clearItems();

        /* Enable the GIT menu if we have components */
        theMenuBar.setEnabled(ThemisThreadId.CREATEGITREPO, myMenu.countItems() > 0);
    }

    /**
     * Complete thread task.
     * @param pTask the task that has completed
     */
    public void completeTask(final Object pTask) {
        /* If this is the discoverData thread */
        if (pTask instanceof ThemisDiscoverData) {
            /* Access correctly */
            final ThemisDiscoverData myThread = (ThemisDiscoverData) pTask;

            /* Report data to manager */
            setSubversionData(myThread);
        }

        /* Enable other tasks */
        theMenuBar.setEnabled(ThemisSvnMenuItem.TASKS, true);
    }

    /**
     * Run thread.
     * @param pThread the thread
     */
    private void runThread(final MetisThread<?> pThread) {
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
     * Handle AboutBox.
     */
    private void handleAboutBox() {
        theAboutBox.showDialog();
    }

    /**
     * Handle WindowClosed.
     */
    public void handleWindowClosed() {
        theThreadMgr.shutdown();
        theDataWdw.closeWindow();
    }
}
