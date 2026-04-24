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
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIResource;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisPackage;

import java.util.List;

/**
 * Button to select file from package.
 */
public class ThemisXAnalysisUISourceFileSelect
        implements OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The scroll button.
     */
    private final TethysUIScrollButtonManager<ThemisXAnalysisFile> theButton;

    /**
     * The file menu.
     */
    private final TethysUIScrollMenu<ThemisXAnalysisFile> theFileMenu;

    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The current package.
     */
    private ThemisXAnalysisPackage thePackage;

    /**
     * The current file.
     */
    private ThemisXAnalysisFile theFile;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    ThemisXAnalysisUISourceFileSelect(final TethysUIFactory<?> pFactory) {
        /* Create Event Manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the label */
        final TethysUIControlFactory myControls = pFactory.controlFactory();
        final TethysUILabel myPromptLabel = myControls.newLabel(ThemisXAnalysisUIResource.PROMPT_FILE.getValue());

        /* Create the button */
        theButton = pFactory.buttonFactory().newScrollButton(ThemisXAnalysisFile.class);
        theFileMenu = theButton.getMenu();

        /* Create the panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        thePanel = myPanes.newHBoxPane();
        thePanel.addNode(myPromptLabel);
        thePanel.addNode(theButton);

        /* Set listeners */
        final OceanusEventRegistrar<TethysUIEvent> myRegistrar = theButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewFile());
        theButton.setMenuConfigurator(e -> buildFileMenu());
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
     * Obtain the current file.
     *
     * @return the current file
     */
    ThemisXAnalysisFile getCurrentFile() {
        return theFile;
    }

    /**
     * Set the current package.
     *
     * @param pPackage the current package
     */
    void setCurrentPackage(final ThemisXAnalysisPackage pPackage) {
        /* Store the package */
        thePackage = pPackage;

        /* Set the default file */
        final List<ThemisXAnalysisFile> myFiles = thePackage == null ? null : thePackage.getFiles();
        final ThemisXAnalysisFile myFile = (myFiles == null || myFiles.isEmpty()) ? null : myFiles.getFirst();
        theButton.setValue(myFile);
    }

    /**
     * Build the file menu.
     */
    private void buildFileMenu() {
        /* Reset the popUp menu */
        theFileMenu.removeAllItems();

        /* Record active item */
        TethysUIScrollItem<ThemisXAnalysisFile> myActive = null;
        final ThemisXAnalysisFile myCurr = theButton.getValue();

        /* Loop through the available files */
        for (ThemisXAnalysisFile myFile : thePackage.getFiles()) {
            /* Create a new MenuItem and add it to the popUp */
            final TethysUIScrollItem<ThemisXAnalysisFile> myItem = theFileMenu.addItem(myFile);

            /* If this is the active file */
            if (myFile.equals(myCurr)) {
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
     * Handle new File.
     */
    private void handleNewFile() {
        /* Select the new file */
        theFile = theButton.getValue();
        theEventManager.fireEvent(TethysUIEvent.NEWVALUE);
    }
}
