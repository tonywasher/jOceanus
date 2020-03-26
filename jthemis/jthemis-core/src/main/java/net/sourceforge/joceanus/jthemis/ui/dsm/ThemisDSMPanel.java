/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jthemis.ui.dsm;

import java.io.File;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDirectorySelector;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMModule;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMPackage;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMProject;
import net.sourceforge.joceanus.jthemis.dsm.ThemisDSMReport;

/**
 * DSMPanel.
 */
public class ThemisDSMPanel {
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
     * Constructor.
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    protected ThemisDSMPanel(final TethysGuiFactory pFactory) throws OceanusException {
        /* Access GuiFactory */
        theGuiFactory = pFactory;

        /* Create the HTML Panes */
        theDependencyHTML = theGuiFactory.newHTMLManager();
        theDependencyHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theMatrixHTML = theGuiFactory.newHTMLManager();
        theMatrixHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theMatrixHTML.getEventRegistrar().addEventListener(TethysUIEvent.BUILDPAGE, e -> {
            processReference(e.getDetails(String.class));
            e.consume();
        });

        /* Create the module selection panel */
        final TethysBoxPaneManager myModuleSelect = theGuiFactory.newHBoxPane();
        myModuleSelect.addNode(theGuiFactory.newLabel("Module:"));
        theModuleButton = theGuiFactory.newScrollButton();
        myModuleSelect.addNode(theModuleButton);
        theModuleButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewModule());
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
        theFromButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewFrom());
        theFromButton.setMenuConfigurator(e -> buildFromMenu());

        /* Create the project selection panel */
        final TethysBoxPaneManager myToSelect = theGuiFactory.newHBoxPane();
        myToSelect.addNode(theGuiFactory.newLabel("To:"));
        theToButton = theGuiFactory.newScrollButton();
        myToSelect.addNode(theToButton);
        theToButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewTo());
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

        /* Initialise status */
        theProjectButton.setText("None");
        theDependencyTab.setVisible(false);
        theModuleButton.setEnabled(false);
        theFromButton.setEnabled(false);
        theToButton.setEnabled(false);
    }

    /**
     * Obtain tabs.
     * @return the tabs
     */
    protected TethysTabPaneManager getTabs() {
        return theTabPane;
    }

    /**
     * Handle select project.
     */
    void selectProject() {
        /* Determine the name of the directory to load */
        final TethysDirectorySelector myDialog = theGuiFactory.newDirectorySelector();
        myDialog.setTitle("Select Project");
        myDialog.setInitialDirectory(new File(System.getProperty("user.home")));
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
            /* Store details */
            theProject = myProject;
            theProjectButton.setText(theProject.toString());

            /* Set the new module */
            processNewModule(theProject.getDefaultModule());
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
            myBuilder.addItem(myPackage);
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
}
