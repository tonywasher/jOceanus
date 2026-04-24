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
import io.github.tonywasher.joceanus.themis.exc.ThemisIOException;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIResource;
import io.github.tonywasher.joceanus.themis.gui.reference.ThemisUIRefPanel;
import io.github.tonywasher.joceanus.themis.gui.source.ThemisUISourcePanel;
import io.github.tonywasher.joceanus.themis.gui.stats.ThemisUIStatsPanel;
import io.github.tonywasher.joceanus.themis.parser.ThemisParser;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisProject;
import io.github.tonywasher.joceanus.themis.solver.ThemisSolver;
import io.github.tonywasher.joceanus.themis.solver.proj.ThemisSolverProject;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsProject;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Main panel.
 */
public class ThemisUIMainPanel
        implements TethysUIMainPanel {
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
     * The ProjectButton.
     */
    private final TethysUIBorderPaneManager thePanel;

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
        theProjectButton.setText("None");

        /* create the overall project select panel */
        final TethysUIBoxPaneManager myProjectControl = myPanes.newHBoxPane();
        myProjectControl.addSpacer();
        myProjectControl.addNode(myProjectSelect);
        myProjectControl.addSpacer();

        /* create the overall panel */
        thePanel = myPanes.newBorderPane();
        thePanel.setNorth(myProjectControl);
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
        /* Hide tabs */
        theSourceTab.setVisible(false);
        theRefsTab.setVisible(false);
        theStatsTab.setVisible(false);

        /* Save details */
        OceanusException myError = storeDefaultLocation(pProjectDir);
        if (myError != null) {
            writeErrorToLog(myError);
            return;
        }

        /* Parse the project */
        final ThemisParser myParser = new ThemisParser(pProjectDir);
        myError = myParser.getError();
        if (myError == null) {
            final ThemisProject myProject = myParser.getProject();
            theSource.setCurrentProject(myProject);
            theProjectButton.setText(myProject.toString());
            theSourceTab.setVisible(true);
        }

        /* If we parsed successfully */
        if (myError == null) {
            /* Resolve references */
            final ThemisSolverProject myProject = new ThemisSolverProject(myParser);
            final ThemisSolver mySolver = new ThemisSolver(myProject);
            myError = mySolver.getError();
            if (myError == null) {
                theRefs.setCurrentProject(myProject);
                theRefsTab.setVisible(true);
            }
        }

        /* If we parsed successfully */
        if (myError == null) {
            /* Resolve stats */
            final ThemisStatsProject myStats = new ThemisStatsProject(myParser);
            myError = myStats.getError();
            if (myError == null) {
                theStats.setCurrentProject(myStats);
                theStatsTab.setVisible(true);
            }
        }

        /* Display any error */
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
