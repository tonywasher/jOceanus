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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIHTMLManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUISplitTreeManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUITreeManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUITreeManager.TethysUITreeItem;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBorderPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.themis.xanalysis.gui.base.ThemisXAnalysisUIStyleSheet;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeCompilationUnit;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisFile;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.proj.ThemisXAnalysisProject;

/**
 * Source Panel.
 */
public class ThemisXAnalysisUISourcePanel {
    /**
     * The SplitTree Manager.
     */
    private final TethysUISplitTreeManager<ThemisXAnalysisUISourceEntry> theSplitTree;

    /**
     * The Tree Manager.
     */
    private final TethysUITreeManager<ThemisXAnalysisUISourceEntry> theTree;

    /**
     * The HTML Manager.
     */
    private final TethysUIHTMLManager theHTML;

    /**
     * The Document Builder.
     */
    private final ThemisXAnalysisUISourceDocument theDoc;

    /**
     * The FileSelector.
     */
    private final ThemisXAnalysisUISourceFileSelect theFileSelect;

    /**
     * The ModuleSelector.
     */
    private final ThemisXAnalysisUISourceModuleSelect theModuleSelect;

    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The Current root.
     */
    private TethysUITreeItem<ThemisXAnalysisUISourceEntry> theRoot;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisXAnalysisUISourcePanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Create the splitTree Manager */
        theSplitTree = pFactory.controlFactory().newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHTML = theSplitTree.getHTMLManager();
        theHTML.setCSSContent(ThemisXAnalysisUIStyleSheet.CSS);
        theTree.setVisible(true);

        /* Create the document builder */
        theDoc = new ThemisXAnalysisUISourceDocument();

        /* Create the selectors */
        theFileSelect = new ThemisXAnalysisUISourceFileSelect(pFactory);
        final ThemisXAnalysisUISourcePackageSelect myPackageSelect = new ThemisXAnalysisUISourcePackageSelect(pFactory, theFileSelect);
        theModuleSelect = new ThemisXAnalysisUISourceModuleSelect(pFactory, myPackageSelect);

        /* Create the selector panel */
        final TethysUIPaneFactory myPanes = pFactory.paneFactory();
        final TethysUIBoxPaneManager mySelect = myPanes.newHBoxPane();
        mySelect.addNode(theModuleSelect);
        mySelect.addSpacer();
        mySelect.addNode(myPackageSelect);
        mySelect.addSpacer();
        mySelect.addNode(theFileSelect);

        /* Create the main panel */
        thePanel = myPanes.newBorderPane();
        thePanel.setNorth(mySelect);
        thePanel.setCentre(theSplitTree);

        /* Create listeners */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
        theFileSelect.getEventRegistrar().addEventListener(this::handleFileSelect);
    }

    /**
     * Obtain the component.
     *
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return thePanel;
    }

    /**
     * Set the current project.
     *
     * @param pProject the current project
     */
    public void setCurrentProject(final ThemisXAnalysisProject pProject) {
        /* Declare project to the moduleSelector */
        theModuleSelect.setCurrentProject(pProject);
    }

    /**
     * Handle the fileSelect action event.
     *
     * @param pEvent the event
     */
    private void handleFileSelect(final OceanusEvent<TethysUIEvent> pEvent) {
        /* If this is a new value */
        if (pEvent.getEventId() == TethysUIEvent.NEWVALUE) {
            initialiseTree(theFileSelect.getCurrentFile());
        }
    }

    /**
     * Initialise tree.
     *
     * @param pFile the project file
     */
    private void initialiseTree(final ThemisXAnalysisFile pFile) {
        /* If there is currently a root item */
        if (theRoot != null) {
            /* Hide it */
            theRoot.setVisible(false);
        }

        /* Create root item for project */
        final ThemisXAnalysisNodeCompilationUnit myUnit = pFile.getContents();
        final ThemisXAnalysisUISourceEntry myEntry = new ThemisXAnalysisUISourceEntry(myUnit);
        theRoot = theTree.addRootItem(myEntry.getUniqueName(), myEntry);
        theRoot.setIcon(myEntry.getIcon());
        theRoot.setVisible(true);

        /* Create child entries */
        createChildEntries(theRoot, myUnit);
    }

    /**
     * Create child entries tree.
     *
     * @param pParent  the parent
     * @param pElement the element
     */
    private void createChildEntries(final TethysUITreeItem<ThemisXAnalysisUISourceEntry> pParent,
                                    final ThemisXAnalysisInstance pElement) {
        /* Loop through the root children */
        for (ThemisXAnalysisInstance myChild : pElement.getChildren()) {
            /* Create a new root entry */
            final ThemisXAnalysisUISourceEntry myEntry = new ThemisXAnalysisUISourceEntry(pParent.getItem(), myChild);
            final TethysUITreeItem<ThemisXAnalysisUISourceEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
            myTreeItem.setIcon(myEntry.getIcon());
            myTreeItem.setVisible(true);

            /* Create child entries */
            createChildEntries(myTreeItem, myChild);
        }
    }

    /**
     * Handle the split tree action event.
     *
     * @param pEvent the event
     */
    private void handleSplitTreeAction(final OceanusEvent<TethysUIEvent> pEvent) {
        /* If this is a new value */
        if (pEvent.getEventId() == TethysUIEvent.NEWVALUE) {
            handleNewTreeItem(pEvent.getDetails(ThemisXAnalysisUISourceEntry.class));
        }
    }

    /**
     * Handle the new tree item.
     *
     * @param pEntry the new entry
     */
    private void handleNewTreeItem(final ThemisXAnalysisUISourceEntry pEntry) {
        /* If we have an entry */
        if (pEntry != null) {
            /* Access the Element */
            final ThemisXAnalysisInstance myElement = pEntry.getObject();

            /* Build report */
            final String myDoc = theDoc.formatElement(myElement);
            theHTML.setHTMLContent(myDoc, "");
        }
    }
}
