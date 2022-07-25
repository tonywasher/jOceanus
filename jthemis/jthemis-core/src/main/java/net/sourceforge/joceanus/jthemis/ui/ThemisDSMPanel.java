/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2022 Tony Washer
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

import java.io.File;
import java.nio.file.Path;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.joceanus.jmetis.launch.MetisMainPanel;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.TethysDirectorySelector;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysLogTextArea;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMModule;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMPackage;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMProject;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMReport;
import net.sourceforge.joceanus.jthemis.sourcemeter.ThemisSMStatistics;
import net.sourceforge.joceanus.jthemis.statistics.ThemisStatsParser;
import net.sourceforge.joceanus.jthemis.statistics.ThemisStatsProject;

/**
 * DSMPanel.
 */
public class ThemisDSMPanel
    implements MetisMainPanel {
    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory theGuiFactory;

    /**
     * The Tab Manager Pane.
     */
    private final TethysTabPaneManager theTabPane;

    /**
     * The Matrix HTML Pane.
     */
    private final TethysHTMLManager theMatrixHTML;

    /**
     * The Dependency HTML Pane.
     */
    private final TethysHTMLManager theDependencyHTML;

    /**
     * The Dependency Tab.
     */
    private final TethysTabItem theDependencyTab;

    /**
     * The Statistics Panel.
     */
    private final ThemisStatsPanel theStatsPanel;

    /**
     * The Source Panel.
     */
    private final ThemisSourcePanel theSourcePanel;

    /**
     * The Log Tab.
     */
    private final TethysTabItem theLogTab;

    /**
     * The ProjectButton.
     */
    private final TethysButton theProjectButton;

    /**
     * The ModuleButton.
     */
    private final TethysScrollButtonManager<ThemisDSMModule> theModuleButton;

    /**
     * The FromButton.
     */
    private final TethysScrollButtonManager<ThemisDSMPackage> theFromButton;

    /**
     * The ToButton.
     */
    private final TethysScrollButtonManager<ThemisDSMPackage> theToButton;

    /**
     * The current project.
     */
    private ThemisDSMProject theProject;

    /**
     * The current module.
     */
    private ThemisDSMModule theModule;

    /**
     * The current from package.
     */
    private ThemisDSMPackage theFrom;

    /**
     * The error.
     */
    private OceanusException theError;

    /**
     * Constructor.
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisDSMPanel(final TethysGuiFactory pFactory) throws OceanusException {
        /* Access GuiFactory */
        theGuiFactory = pFactory;

        /* Create the HTML Panes */
        theDependencyHTML = theGuiFactory.newHTMLManager();
        theDependencyHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theMatrixHTML = theGuiFactory.newHTMLManager();
        theMatrixHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theMatrixHTML.getEventRegistrar().addEventListener(TethysXUIEvent.BUILDPAGE, e -> {
            processReference(e.getDetails(String.class));
            e.consume();
        });

        /* Create the module selection panel */
        final TethysBoxPaneManager myModuleSelect = theGuiFactory.newHBoxPane();
        myModuleSelect.addNode(theGuiFactory.newLabel("Module:"));
        theModuleButton = theGuiFactory.newScrollButton();
        myModuleSelect.addNode(theModuleButton);
        theModuleButton.getEventRegistrar().addEventListener(TethysXUIEvent.NEWVALUE, e -> handleNewModule());
        theModuleButton.setMenuConfigurator(e -> buildModuleMenu());

        /* Create the project selection panel */
        final TethysBoxPaneManager myProjectSelect = theGuiFactory.newHBoxPane();
        myProjectSelect.addNode(theGuiFactory.newLabel("Project:"));
        theProjectButton = theGuiFactory.newButton();
        theProjectButton.setTextOnly();
        myProjectSelect.addNode(theProjectButton);
        theProjectButton.getEventRegistrar().addEventListener(e -> selectProject());

        /* create the overall matrix status panel */
        final TethysBoxPaneManager myMatrixControl = theGuiFactory.newHBoxPane();
        myMatrixControl.addSpacer();
        myMatrixControl.addNode(myProjectSelect);
        myMatrixControl.addSpacer();
        myMatrixControl.addNode(myModuleSelect);
        myMatrixControl.addSpacer();

        /* create the matrix panel */
        final TethysBorderPaneManager myMatrixPanel = theGuiFactory.newBorderPane();
        myMatrixPanel.setCentre(theMatrixHTML);
        myMatrixPanel.setNorth(myMatrixControl);

        /* Create the from selection panel */
        final TethysBoxPaneManager myFromSelect = theGuiFactory.newHBoxPane();
        myFromSelect.addNode(theGuiFactory.newLabel("From:"));
        theFromButton = theGuiFactory.newScrollButton();
        myFromSelect.addNode(theFromButton);
        theFromButton.getEventRegistrar().addEventListener(TethysXUIEvent.NEWVALUE, e -> handleNewFrom());
        theFromButton.setMenuConfigurator(e -> buildFromMenu());

        /* Create the project selection panel */
        final TethysBoxPaneManager myToSelect = theGuiFactory.newHBoxPane();
        myToSelect.addNode(theGuiFactory.newLabel("To:"));
        theToButton = theGuiFactory.newScrollButton();
        myToSelect.addNode(theToButton);
        theToButton.getEventRegistrar().addEventListener(TethysXUIEvent.NEWVALUE, e -> handleNewTo());
        theToButton.setMenuConfigurator(e -> buildToMenu());

        /* create the overall matrix status panel */
        final TethysBoxPaneManager myDependencyControl = theGuiFactory.newHBoxPane();
        myDependencyControl.addSpacer();
        myDependencyControl.addNode(myFromSelect);
        myDependencyControl.addSpacer();
        myDependencyControl.addNode(myToSelect);
        myDependencyControl.addSpacer();

        /* create the matrix panel */
        final TethysBorderPaneManager myDependencyPanel = theGuiFactory.newBorderPane();
        myDependencyPanel.setCentre(theDependencyHTML);
        myDependencyPanel.setNorth(myDependencyControl);

        /* Create the TabPane */
        theTabPane = theGuiFactory.newTabPane();
        theTabPane.addTabItem("Matrix", myMatrixPanel);
        theDependencyTab = theTabPane.addTabItem("Dependencies", myDependencyPanel);

        /* Create the Stats panel */
        theStatsPanel = new ThemisStatsPanel(theGuiFactory);
        theTabPane.addTabItem("Stats", theStatsPanel);

        /* Create the Source panel */
        theSourcePanel = new ThemisSourcePanel(theGuiFactory);
        theTabPane.addTabItem("Source", theSourcePanel);

        /* Create the log tab */
        final TethysLogTextArea myLog = theGuiFactory.getLogSink();
        theLogTab = theTabPane.addTabItem("Log", myLog);
        myLog.getEventRegistrar().addEventListener(TethysXUIEvent.NEWVALUE, e -> theLogTab.setVisible(true));
        myLog.getEventRegistrar().addEventListener(TethysXUIEvent.WINDOWCLOSED, e -> theLogTab.setVisible(false));
        theLogTab.setVisible(myLog.isActive());

        /* Initialise status */
        theProjectButton.setText("None");
        theDependencyTab.setVisible(false);
        theModuleButton.setEnabled(false);
        theFromButton.setEnabled(false);
        theToButton.setEnabled(false);

        /* Handle the default location */
        final File myLocation = getDefaultLocation();
        if (myLocation != null) {
            handleNewProject(myLocation);
        }
    }

    @Override
    public TethysTabPaneManager getComponent() {
        return theTabPane;
    }

    /**
     * Handle select project.
     */
    void selectProject() {
        /* Determine initial directory */
        File myInit = getDefaultLocation();
        if (myInit == null) {
            myInit = new File(System.getProperty("user.home"));
        }

        /* Determine the name of the directory to load */
        final TethysDirectorySelector myDialog = theGuiFactory.newDirectorySelector();
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
     * @param pProjectDir the new project directory
     */
    private void handleNewProject(final File pProjectDir) {
        /* Parse the project*/
        final ThemisDSMProject myProject  = new ThemisDSMProject(pProjectDir);
        if (myProject.hasModules()) {
            /* Save details */
            storeDefaultLocation(pProjectDir);

            /* Store details */
            theProject = myProject;
            theProjectButton.setText(theProject.toString());

            /* Set the new module */
            processNewModule(theProject.getDefaultModule());

            /* Load statistics */
            handleNewStats(pProjectDir);
        }
    }

    /**
     * Handle the new project.
     * @param pProjectDir the new project directory
     */
    private void handleNewStats(final File pProjectDir) {
        try {
            /* Analyse source of project */
            final ThemisAnalysisProject myProj = new ThemisAnalysisProject(pProjectDir);

            /* Parse sourceMeter statistics */
            final ThemisSMStatistics myStats = new ThemisSMStatistics(new TethysDataFormatter());
            final Path myPath = ThemisSMStatistics.getRecentStats(theProject.toString());
            myStats.parseStatistics(myPath, theProject.toString());

            /* Parse the base project */
            final ThemisStatsParser myParser = new ThemisStatsParser(myStats);
            final ThemisStatsProject myProject = myParser.parseProject(myProj);
            theStatsPanel.initialiseTree(myProject);
            theSourcePanel.initialiseTree(myProj);

            /* Catch exceptions */
        } catch (OceanusException e) {
            theError = e;
        }
    }

    /**
     * Handle the new module.
     */
    private void handleNewModule() {
        processNewModule(theModuleButton.getValue());
    }

    /**
     * Process new module.
     * @param pModule the new Module
     */
    private void processNewModule(final ThemisDSMModule pModule) {
        /* Store the module */
        theModule = pModule;
        theModuleButton.setValue(pModule);
        theModuleButton.setEnabled(true);

        /* Build matrix report */
        final String myDoc = ThemisDSMReport.reportOnModule(theModule);
        theMatrixHTML.setHTMLContent(myDoc, "");

        /* Process the new from Package */
        processNewFrom(theModule.getDefaultPackage());
    }

    /**
     * Build the module menu.
     */
    private void buildModuleMenu() {
        /* Access builder */
        final TethysScrollMenu<ThemisDSMModule> myBuilder = theModuleButton.getMenu();
        myBuilder.removeAllItems();

        /* Loop through to add each module */
        for (ThemisDSMModule myModule : theProject.listModules()) {
            myBuilder.addItem(myModule);
        }
    }

    /**
     * Handle the new fromPackage.
     */
    private void handleNewFrom() {
        processNewFrom(theFromButton.getValue());
    }

    /**
     * Process new module.
     * @param pPackage the new fromPackage
     */
    private void processNewFrom(final ThemisDSMPackage pPackage) {
        /* Store the module */
        theFrom = pPackage;
        theFromButton.setValue(pPackage);
        theFromButton.setEnabled(true);
        theDependencyTab.setVisible(true);

        /* Process the new from Package */
        processNewTo(theFrom.getDefaultReference());
    }

    /**
     * Build the from menu.
     */
    private void buildFromMenu() {
        /* Access builder */
        final TethysScrollMenu<ThemisDSMPackage> myBuilder = theFromButton.getMenu();
        myBuilder.removeAllItems();

        /* Loop through to add each package */
        for (ThemisDSMPackage myPackage : theModule.listPackages()) {
            if (myPackage.hasReferences()) {
                myBuilder.addItem(myPackage);
            }
        }
    }

    /**
     * Handle the new toPackage.
     */
    private void handleNewTo() {
        processNewTo(theToButton.getValue());
    }

    /**
     * Process new toPackage.
     * @param pPackage the new toPackage
     */
    private void processNewTo(final ThemisDSMPackage pPackage) {
        /* Store the package */
        theToButton.setValue(pPackage);
        theToButton.setEnabled(true);

        /* Build matrix report */
        final String myDoc = ThemisDSMReport.reportOnPackageLinks(theFrom, pPackage);
        theDependencyHTML.setHTMLContent(myDoc, "");
    }

    /**
     * Build the to menu.
     */
    private void buildToMenu() {
        /* Access builder */
        final TethysScrollMenu<ThemisDSMPackage> myBuilder = theToButton.getMenu();
        myBuilder.removeAllItems();

        /* Loop through to add each package */
        for (ThemisDSMPackage myPackage : theFrom.listReferences()) {
            myBuilder.addItem(myPackage);
        }
    }

    /**
     * Process the reference.
     * @param pReference the reference
     */
    private void processReference(final String pReference) {
        /* Split on the "-" */
        final String[] myTokens = pReference.split(ThemisDSMReport.SEP_REF);
        if (myTokens.length == 2) {
            /* Access the relevant packages */
            final ThemisDSMPackage myFrom = theModule.getIndexedPackage(ThemisDSMReport.getIndexForKey(myTokens[0]));
            final ThemisDSMPackage myTo = theModule.getIndexedPackage(ThemisDSMReport.getIndexForKey(myTokens[1]));

            /* Set the new details */
            processNewFrom(myFrom);
            processNewTo(myTo);

            /* Select the tab */
            theDependencyTab.selectItem();
        }
    }

    /**
     * Obtain the default location.
     * @return the default location
     */
    private File getDefaultLocation() {
        final Preferences myPreferences = deriveHandle();
        final String myLocation = myPreferences.get("DefaultProject", null);
        return myLocation == null ? null : new File(myLocation);
    }

    /**
     * Store the default location.
     * @param pLocation the default location
     */
    private void storeDefaultLocation(final File pLocation) {
        /* Protect against exceptions */
        try {
            final Preferences myPreferences = deriveHandle();
            myPreferences.put("DefaultProject", pLocation.getAbsolutePath());
            myPreferences.flush();
        } catch (BackingStoreException e) {
            theError = new ThemisIOException("Failed to save preference", e);
        }
    }

    /**
     * Derive handle for node.
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
