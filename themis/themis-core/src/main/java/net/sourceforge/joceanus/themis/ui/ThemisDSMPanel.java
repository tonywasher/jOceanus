/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.themis.ui;

import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButton;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDirectorySelector;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUILogTextArea;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIMainPanel;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUITabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUITabPaneManager.TethysUITabItem;
import net.sourceforge.joceanus.themis.ThemisIOException;
import net.sourceforge.joceanus.themis.analysis.ThemisAnalysisProject;
import net.sourceforge.joceanus.themis.dsm.ThemisDSMModule;
import net.sourceforge.joceanus.themis.dsm.ThemisDSMPackage;
import net.sourceforge.joceanus.themis.dsm.ThemisDSMProject;
import net.sourceforge.joceanus.themis.dsm.ThemisDSMReport;
import net.sourceforge.joceanus.themis.statistics.ThemisStatsParser;
import net.sourceforge.joceanus.themis.statistics.ThemisStatsProject;

/**
 * DSMPanel.
 */
public class ThemisDSMPanel
    implements TethysUIMainPanel {
    /**
     * The GUI Factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * The Tab Manager Pane.
     */
    private final TethysUITabPaneManager theTabPane;

    /**
     * The Matrix HTML Pane.
     */
    private final TethysUIHTMLManager theMatrixHTML;

    /**
     * The Dependency HTML Pane.
     */
    private final TethysUIHTMLManager theDependencyHTML;

    /**
     * The Dependency Tab.
     */
    private final TethysUITabItem theDependencyTab;

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
     * The ModuleButton.
     */
    private final TethysUIScrollButtonManager<ThemisDSMModule> theModuleButton;

    /**
     * The FromButton.
     */
    private final TethysUIScrollButtonManager<ThemisDSMPackage> theFromButton;

    /**
     * The ToButton.
     */
    private final TethysUIScrollButtonManager<ThemisDSMPackage> theToButton;

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
    public ThemisDSMPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Access GuiFactory */
        theGuiFactory = pFactory;

        /* Create the HTML Panes */
        final TethysUIControlFactory myControls = theGuiFactory.controlFactory();
        final TethysUIButtonFactory<?> myButtons = theGuiFactory.buttonFactory();
        theDependencyHTML = myControls.newHTMLManager();
        theDependencyHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theMatrixHTML = myControls.newHTMLManager();
        theMatrixHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theMatrixHTML.getEventRegistrar().addEventListener(TethysUIEvent.BUILDPAGE, e -> {
            processReference(e.getDetails(String.class));
            e.consume();
        });

        /* Create the module selection panel */
        final TethysUIPaneFactory myPanes = theGuiFactory.paneFactory();
        final TethysUIBoxPaneManager myModuleSelect = myPanes.newHBoxPane();
        myModuleSelect.addNode(myControls.newLabel("Module:"));
        theModuleButton = myButtons.newScrollButton(ThemisDSMModule.class);
        myModuleSelect.addNode(theModuleButton);
        theModuleButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewModule());
        theModuleButton.setMenuConfigurator(e -> buildModuleMenu());

        /* Create the project selection panel */
        final TethysUIBoxPaneManager myProjectSelect = myPanes.newHBoxPane();
        myProjectSelect.addNode(myControls.newLabel("Project:"));
        theProjectButton = myButtons.newButton();
        theProjectButton.setTextOnly();
        myProjectSelect.addNode(theProjectButton);
        theProjectButton.getEventRegistrar().addEventListener(e -> selectProject());

        /* create the overall matrix status panel */
        final TethysUIBoxPaneManager myMatrixControl = myPanes.newHBoxPane();
        myMatrixControl.addSpacer();
        myMatrixControl.addNode(myProjectSelect);
        myMatrixControl.addSpacer();
        myMatrixControl.addNode(myModuleSelect);
        myMatrixControl.addSpacer();

        /* create the matrix panel */
        final TethysUIBorderPaneManager myMatrixPanel = myPanes.newBorderPane();
        myMatrixPanel.setCentre(theMatrixHTML);
        myMatrixPanel.setNorth(myMatrixControl);

        /* Create the from selection panel */
        final TethysUIBoxPaneManager myFromSelect = myPanes.newHBoxPane();
        myFromSelect.addNode(myControls.newLabel("From:"));
        theFromButton = myButtons.newScrollButton(ThemisDSMPackage.class);
        myFromSelect.addNode(theFromButton);
        theFromButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewFrom());
        theFromButton.setMenuConfigurator(e -> buildFromMenu());

        /* Create the project selection panel */
        final TethysUIBoxPaneManager myToSelect = myPanes.newHBoxPane();
        myToSelect.addNode(myControls.newLabel("To:"));
        theToButton = myButtons.newScrollButton(ThemisDSMPackage.class);
        myToSelect.addNode(theToButton);
        theToButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewTo());
        theToButton.setMenuConfigurator(e -> buildToMenu());

        /* create the overall matrix status panel */
        final TethysUIBoxPaneManager myDependencyControl = myPanes.newHBoxPane();
        myDependencyControl.addSpacer();
        myDependencyControl.addNode(myFromSelect);
        myDependencyControl.addSpacer();
        myDependencyControl.addNode(myToSelect);
        myDependencyControl.addSpacer();

        /* create the matrix panel */
        final TethysUIBorderPaneManager myDependencyPanel = myPanes.newBorderPane();
        myDependencyPanel.setCentre(theDependencyHTML);
        myDependencyPanel.setNorth(myDependencyControl);

        /* Create the TabPane */
        theTabPane = myPanes.newTabPane();
        theTabPane.addTabItem("Matrix", myMatrixPanel);
        theDependencyTab = theTabPane.addTabItem("Dependencies", myDependencyPanel);

        /* Create the Stats panel */
        theStatsPanel = new ThemisStatsPanel(theGuiFactory);
        theTabPane.addTabItem("Stats", theStatsPanel.getComponent());

        /* Create the Source panel */
        theSourcePanel = new ThemisSourcePanel(theGuiFactory);
        theTabPane.addTabItem("Source", theSourcePanel.getComponent());

        /* Create the log tab */
        theLogSink = theGuiFactory.getLogSink();
        theLogTab = theTabPane.addTabItem("Log", theLogSink);
        theLogSink.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theLogTab.setVisible(true));
        theLogSink.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theLogTab.setVisible(false));
        theLogTab.setVisible(theLogSink.isActive());

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
    public TethysUITabPaneManager getComponent() {
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
     * @param pProjectDir the new project directory
     */
    private void handleNewProject(final File pProjectDir) {
        /* Parse the project*/
        theError = null;
        final ThemisDSMProject myProject  = new ThemisDSMProject(pProjectDir);
        if (myProject.getError()  != null) {
            theError = myProject.getError();

        } else if (myProject.hasModules()) {
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

        /* Display the error */
        if (theError != null) {
            theLogSink.writeLogMessage(theError.getMessage());
            theLogTab.setVisible(true);
        }
    }

    /**
     * Handle the new project.
     * @param pProjectDir the new project directory
     */
    private void handleNewStats(final File pProjectDir) {
        /* Analyse source of project */
        final ThemisAnalysisProject myProj = new ThemisAnalysisProject(pProjectDir);
        if (myProj.getError() != null) {
            theError = myProj.getError();
        } else {
            /* Parse the base project */
            final ThemisStatsParser myParser = new ThemisStatsParser();
            final ThemisStatsProject myProject = myParser.parseProject(myProj);
            theStatsPanel.initialiseTree(myProject);
            theSourcePanel.initialiseTree(myProj);
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
        final TethysUIScrollMenu<ThemisDSMModule> myBuilder = theModuleButton.getMenu();
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
        final TethysUIScrollMenu<ThemisDSMPackage> myBuilder = theFromButton.getMenu();
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
        final TethysUIScrollMenu<ThemisDSMPackage> myBuilder = theToButton.getMenu();
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
