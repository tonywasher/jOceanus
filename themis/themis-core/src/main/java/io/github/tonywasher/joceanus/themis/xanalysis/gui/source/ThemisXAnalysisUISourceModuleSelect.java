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

package io.github.tonywasher.joceanus.themis.xanalysis.gui.source;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventManager;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIScrollButtonManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUILabel;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollItem;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisModule;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;

import java.util.List;

/**
 * Button to select module from project.
 */
public class ThemisXAnalysisUISourceModuleSelect
        implements OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The scroll button.
     */
    private final TethysUIScrollButtonManager<ThemisXAnalysisModule> theButton;

    /**
     * The module menu.
     */
    private final TethysUIScrollMenu<ThemisXAnalysisModule> theModuleMenu;

    /**
     * The package select.
     */
    private final ThemisXAnalysisUISourcePackageSelect thePackageSelect;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The current project.
     */
    private ThemisXAnalysisProject theProject;

    /**
     * The current module.
     */
    private ThemisXAnalysisModule theModule;

    /**
     * Constructor.
     *
     * @param pFactory       the factory
     * @param pPackageSelect the package selection button
     */
    ThemisXAnalysisUISourceModuleSelect(final TethysUIFactory<?> pFactory,
                                        final ThemisXAnalysisUISourcePackageSelect pPackageSelect) {
        /* Store the package select */
        thePackageSelect = pPackageSelect;

        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the label */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myPromptLabel = myControls.newLabel("Module:");

        /* Create the button */
        theButton = pFactory.buttonFactory().newScrollButton(ThemisXAnalysisModule.class);
        theModuleMenu = theButton.getMenu();

        /* Create the panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        thePanel = myPanes.newHBoxPane();
        thePanel.addNode(myPromptLabel);
        thePanel.addNode(theButton);

        /* Set listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewModule());
        theButton.setMenuConfigurator(e -> buildModuleMenu());
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    /**
     * Obtain the current module.
     *
     * @return the current module
     */
    ThemisXAnalysisModule getCurrentModule() {
        return theModule;
    }

    /**
     * Set the current project.
     *
     * @param pProject the current project
     */
    void setCurrentProject(final ThemisXAnalysisProject pProject) {
        /* Store the project */
        theProject = pProject;

        /* Set the default module */
        final List<ThemisXAnalysisModule> myModules = theProject == null ? null : theProject.getModules();
        final ThemisXAnalysisModule myModule = (myModules == null || myModules.isEmpty()) ? null : myModules.getFirst();
        theButton.setValue(myModule);
    }

    /**
     * Build the package menu.
     */
    private void buildModuleMenu() {
        /* Reset the popUp menu */
        theModuleMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<ThemisXAnalysisModule> myActive = null;
        final ThemisXAnalysisModule myCurr = theButton.getValue();

        /* Loop through the available modules */
        for (ThemisXAnalysisModule myModule : theProject.getModules()) {
            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<ThemisXAnalysisModule> myItem = theModuleMenu.addItem(myModule);

            /* If this is the active module */
            if (myModule.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
    }

    /**
     * Handle new Module.
     */
    private void handleNewModule() {
        /* Select the new module */
        theModule = theButton.getValue();
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE);
        thePackageSelect.setCurrentModule(theModule);
    }
}
