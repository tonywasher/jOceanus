/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.gui.launch;

import io.github.tonywasher.joceanus.metis.ui.MetisIcon;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButton;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButtonFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIDialogFactory;
import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIDirectorySelector;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUILogTextArea;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIMainPanel;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUITabPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUITabPaneManager.TethysUITabItem;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadEvent;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadManager;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusManager;
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIResource;
import io.github.tonywasher.joceanus.themis.gui.launch.ThemisUIThread.ThemisUIThreadData;
import io.github.tonywasher.joceanus.themis.gui.reference.ThemisUIRefPanel;
import io.github.tonywasher.joceanus.themis.gui.source.ThemisUISourcePanel;
import io.github.tonywasher.joceanus.themis.gui.stats.ThemisUIStatsPanel;
import io.github.tonywasher.joceanus.themis.parser.project.ThemisProject;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsProject;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Main panel.
 */
public class ThemisUIMainPanel
        implements TethysUIMainPanel, ThemisUIThreadData {
    /**
     * Default Project Preference.
     */
    private static final String PREF_PROJECT = "DefaultLocation";

    /**
     * The GUI Factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * Source Panel.
     */
    private final ThemisUISourcePanel theSource;

    /**
     * Stats Panel.
     */
    private final ThemisUIStatsPanel theStats;

    /**
     * Reference Panel.
     */
    private final ThemisUIRefPanel theRefs;

    /**
     * The Source Tab.
     */
    private final TethysUITabItem theSourceTab;

    /**
     * The Stats Tab.
     */
    private final TethysUITabItem theStatsTab;

    /**
     * The Refs Tab.
     */
    private final TethysUITabItem theRefsTab;

    /**
     * The Log Tab.
     */
    private final TethysUITabItem theLogTab;

    /**
     * The log sink.
     */
    private final TethysUILogTextArea theLogSink;

    /**
     * The ProjectButton.
     */
    private final TethysUIButton theProjectButton;

    /**
     * The Refresh button.
     */
    private final TethysUIButton theRefreshButton;

    /**
     * The ProjectButton.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The Thread Manager.
     */
    private final TethysUIThreadManager theThreadMgr;

    /**
     * The Status window.
     */
    private final TethysUIThreadStatusManager theStatusBar;

    /**
     * The Project control.
     */
    private final TethysUIBoxPaneManager theProjectControl;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisUIMainPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Store guiFactory */
        theGuiFactory = pFactory;

        /* Create the subPanels */
        theSource = new ThemisUISourcePanel(theGuiFactory);
        theStats = new ThemisUIStatsPanel(theGuiFactory);
        theRefs = new ThemisUIRefPanel(theGuiFactory);

        /* Create the tabs */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        final TethysUITabPaneManager myTabs = myPanes.newTabPane();
        theSourceTab = myTabs.addTabItem(ThemisUIResource.TAB_SOURCE.getValue(), theSource);
        theRefsTab = myTabs.addTabItem(ThemisUIResource.TAB_REFERENCES.getValue(), theRefs);
        theStatsTab = myTabs.addTabItem(ThemisUIResource.TAB_STATS.getValue(), theStats);

        /* Hide tabs */
        theSourceTab.setVisible(false);
        theStatsTab.setVisible(false);
        theRefsTab.setVisible(false);

        /* Create the log tab */
        theLogSink = theGuiFactory.getLogSink();
        theLogTab = myTabs.addTabItem(ThemisUIResource.TAB_LOG.getValue(), theLogSink);
        theLogSink.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theLogTab.setVisible(true));
        theLogSink.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theLogTab.setVisible(false));
        theLogTab.setVisible(theLogSink.isActive());

        /* Create the project selection panel */
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
        final TethysUIButtonFactory<?> myButtons = theGuiFactory.buttonFactory();
        final TethysUIBoxPaneManager myProjectSelect = myPanes.newHBoxPane();
        myProjectSelect.addNode(myControls.newLabel(ThemisUIResource.PROMPT_PROJECT.getValue()));
        theProjectButton = myButtons.newButton();
        theProjectButton.setTextOnly();
        myProjectSelect.addNode(theProjectButton);
        theProjectButton.getEventRegistrar().addEventListener(e -> selectProject());
        theProjectButton.setText(ThemisUIResource.PROMPT_NONE.getValue());

        /* Configure refresh button */
        theRefreshButton = myButtons.newButton();
        MetisIcon.configureButton(theRefreshButton);
        theRefreshButton.setIcon(ThemisUIIcon.REFRESH);
        theRefreshButton.setToolTip(ThemisUIResource.TOOLTIP_REFRESH.getValue());
        theRefreshButton.getEventRegistrar().addEventListener(TethysUIEvent.PRESSED, e -> handleNewProject(getDefaultLocation()));

        /* create the overall project select panel */
        theProjectControl = myPanes.newHBoxPane();
        theProjectControl.addSpacer();
        theProjectControl.addNode(myProjectSelect);
        theProjectControl.addSpacer();
        theProjectControl.addNode(theRefreshButton);
        theProjectControl.addStrut();

        /* Access the status bar and set to invisible */
        theThreadMgr = pFactory.threadFactory().newThreadManager();
        theStatusBar = theThreadMgr.getStatusManager();
        theStatusBar.setVisible(false);
        theThreadMgr.getEventRegistrar().addEventListener(TethysUIThreadEvent.THREADEND, e -> setVisibility(false));

        /* create the overall project select panel */
        final TethysUIBoxPaneManager myBanner = myPanes.newVBoxPane();
        myBanner.addNode(theProjectControl);
        myBanner.addNode(theStatusBar);

        /* create the overall panel */
        thePanel = myPanes.newBorderPane();
        thePanel.setNorth(myBanner);
        thePanel.setCentre(myTabs);

        /* Handle the default location */
        final File myLocation = getDefaultLocation();
        if (myLocation != null) {
            handleNewProject(myLocation);
        }
    }

    @Override
    public TethysUIComponent getComponent() {
        return thePanel;
    }

    /**
     * Handle select project.
     */
    void selectProject() {
        /* Determine initial directory */
        final File myInit = new File(System.getProperty("user.home"));

        /* Determine the name of the directory to load */
        final TethysUIDialogFactory myDialogs = theGuiFactory.dialogFactory();
        final TethysUIDirectorySelector myDialog = myDialogs.newDirectorySelector();
        myDialog.setTitle(ThemisUIResource.SELECT_PROJECT.getValue());
        myDialog.setInitialDirectory(myInit);
        final File myFile = myDialog.selectDirectory();

        /* If we selected a file */
        if (myFile != null) {
            /* Handle the new project */
            handleNewProject(myFile);
        }
    }

    /**
     * Handle the new project.
     *
     * @param pProjectDir the new project directory
     */
    private void handleNewProject(final File pProjectDir) {
        /* Adjust visibility */
        setVisibility(true);

        /* Create and start thread */
        final ThemisUIThread myLoader = new ThemisUIThread(this, pProjectDir);
        theThreadMgr.startThread(myLoader);
    }

    /**
     * Set panel visibility.
     *
     * @param pLoading are we loading?
     */
    private void setVisibility(final boolean pLoading) {
        theSourceTab.setVisible(!pLoading);
        theRefsTab.setVisible(!pLoading);
        theStatsTab.setVisible(!pLoading);
        theProjectControl.setVisible(!pLoading);
        theStatusBar.setVisible(pLoading);
    }

    @Override
    public void setNewData(final ThemisUIData pData) {
        /* Update source */
        final ThemisProject myProject = pData.getParsedProject();
        theSource.setCurrentProject(myProject);
        theProjectButton.setText(myProject.toString());

        /* Update references */
        final ThemisSolverProject mySolved = pData.getSolvedProject();
        theRefs.setCurrentProject(mySolved);

        /* Resolve stats */
        final ThemisStatsProject myStats = pData.getProjectStats();
        theStats.setCurrentProject(myStats);

        /* Save details */
        OceanusException myError = storeDefaultLocation(pData.getProjectDir());
        if (myError != null) {
            writeErrorToLog(myError);
        }
    }

    /**
     * Write error to log.
     *
     * @param pError the error
     */
    private void writeErrorToLog(final OceanusException pError) {
        theLogSink.writeLogMessage(pError.getMessage());
        theLogTab.setVisible(true);
    }

    /**
     * Obtain the default location.
     *
     * @return the default location
     */
    private File getDefaultLocation() {
        final Preferences myPreferences = deriveHandle();
        final String myLocation = myPreferences.get(PREF_PROJECT, null);
        return myLocation == null ? null : new File(myLocation);
    }

    /**
     * Store the default location.
     *
     * @param pLocation the default location
     * @return exception or null
     */
    private OceanusException storeDefaultLocation(final File pLocation) {
        /* Protect against exceptions */
        try {
            final Preferences myPreferences = deriveHandle();
            myPreferences.put(PREF_PROJECT, pLocation.getAbsolutePath());
            myPreferences.flush();
            return null;
        } catch (BackingStoreException e) {
            return new ThemisIOException("Failed to save preference", e);
        }
    }

    /**
     * Derive handle for node.
     *
     * @return the class name
     */
    private Preferences deriveHandle() {
        /* Obtain the class name */
        final Class<?> myClass = this.getClass();
        String myName = myClass.getCanonicalName();

        /* Obtain the package name */
        final String myPackage = myClass.getPackage().getName();

        /* Strip off the package name */
        myName = myName.substring(myPackage.length() + 1);

        /* Derive the handle */
        final Preferences myHandle = Preferences.userNodeForPackage(myClass);
        return myHandle.node(myName);
    }
}
