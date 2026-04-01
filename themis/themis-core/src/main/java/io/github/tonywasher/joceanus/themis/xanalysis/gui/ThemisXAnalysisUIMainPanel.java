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

package io.github.tonywasher.joceanus.themis.xanalysis.gui;

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
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUITabPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUITabPaneManager.TethysUITabItem;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.source.ThemisXAnalysisUISourcePanel;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.stats.ThemisXAnalysisUIStatsPanel;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.ThemisXAnalysisParser;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.ThemisXAnalysisSolver;
import io.github.tonywasher.joceanus.themis.xanalysis.solver.proj.ThemisXAnalysisSolverProject;
import io.github.tonywasher.joceanus.themis.xanalysis.stats.ThemisXAnalysisStatsProject;

import java.io.File;

/**
 * Main panel.
 */
public class ThemisXAnalysisUIMainPanel
        implements TethysUIComponent {
    /**
     * The GUI Factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * Source Panel.
     */
    private final ThemisXAnalysisUISourcePanel theSource;

    /**
     * Stats Panel.
     */
    private final ThemisXAnalysisUIStatsPanel theStats;

    /**
     * The Source Tab.
     */
    private final TethysUITabItem theSourceTab;

    /**
     * The Stats Tab.
     */
    private final TethysUITabItem theStatsTab;

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
    public ThemisXAnalysisUIMainPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Store guiFactory */
        theGuiFactory = pFactory;

        /* Create the subPanels */
        theSource = new ThemisXAnalysisUISourcePanel(theGuiFactory);
        theStats = new ThemisXAnalysisUIStatsPanel(theGuiFactory);

        /* Create the tabs */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        final TethysUITabPaneManager myTabs = myPanes.newTabPane();
        theSourceTab = myTabs.addTabItem("Source", theSource);
        theStatsTab = myTabs.addTabItem("Stats", theStats);

        /* Hide tabs */
        theSourceTab.setVisible(false);
        theStatsTab.setVisible(false);

        /* Create the log tab */
        theLogSink = theGuiFactory.getLogSink();
        theLogTab = myTabs.addTabItem("Log", theLogSink);
        theLogSink.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theLogTab.setVisible(true));
        theLogSink.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theLogTab.setVisible(false));
        theLogTab.setVisible(theLogSink.isActive());

        /* Create the project selection panel */
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
        final TethysUIButtonFactory<?> myButtons = theGuiFactory.buttonFactory();
        final TethysUIBoxPaneManager myProjectSelect = myPanes.newHBoxPane();
        myProjectSelect.addNode(myControls.newLabel("Project:"));
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
    }

    @Override
    public TethysUIComponent getUnderlying() {
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
        myDialog.setTitle("Select Project");
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
        theStatsTab.setVisible(false);

        /* Parse the project */
        final ThemisXAnalysisParser myParser = new ThemisXAnalysisParser(pProjectDir);
        OceanusException myError = myParser.getError();
        if (myError == null) {
            final ThemisXAnalysisProject myProject = myParser.getProject();
            theSource.setCurrentProject(myProject);
            theProjectButton.setText(myProject.toString());
            theSourceTab.setVisible(true);
        }

        /* If we parsed successfully */
        if (myError == null) {
            /* Resolve references */
            final ThemisXAnalysisSolverProject myProject = new ThemisXAnalysisSolverProject(myParser);
            final ThemisXAnalysisSolver mySolver = new ThemisXAnalysisSolver(myProject);
            myError = mySolver.getError();
        }

        /* If we parsed successfully */
        if (myError == null) {
            /* Resolve stats */
            final ThemisXAnalysisStatsProject myStats = new ThemisXAnalysisStatsProject(myParser);
            myError = myStats.getError();
            if (myError == null) {
                theStats.setCurrentProject(myStats);
                theStatsTab.setVisible(true);
            }
        }

        /* Display the error */
        if (myError != null) {
            theLogSink.writeLogMessage(myError.getMessage());
            theLogTab.setVisible(true);
        }
    }
}
