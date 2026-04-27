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

package io.github.tonywasher.joceanus.themis.gui.source;

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
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIStyleSheet;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeCompilationUnit;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisFile;
import io.github.tonywasher.joceanus.themis.parser.proj.ThemisProject;

/**
 * Source Panel.
 */
public class ThemisUISourcePanel
        implements TethysUIComponent {
    /**
     * The SplitTree Manager.
     */
    private final TethysUISplitTreeManager<ThemisUISourceEntry> theSplitTree;

    /**
     * The Tree Manager.
     */
    private final TethysUITreeManager<ThemisUISourceEntry> theTree;

    /**
     * The HTML Manager.
     */
    private final TethysUIHTMLManager theHTML;

    /**
     * The Document Builder.
     */
    private final ThemisUISourceDocument theDoc;

    /**
     * The FileSelector.
     */
    private final ThemisUISourceFileSelect theFileSelect;

    /**
     * The ModuleSelector.
     */
    private final ThemisUISourceModuleSelect theModuleSelect;

    /**
     * The panel.
     */
    private final TethysUIBorderPaneManager thePanel;

    /**
     * The Current root.
     */
    private TethysUITreeItem<ThemisUISourceEntry> theRoot;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisUISourcePanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Create the splitTree Manager */
        theSplitTree = pFactory.controlFactory().newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHTML = theSplitTree.getHTMLManager();
        theHTML.setCSSContent(ThemisUIStyleSheet.CSS);
        theTree.setVisible(true);

        /* Create the document builder */
        theDoc = new ThemisUISourceDocument();

        /* Create the selectors */
        theFileSelect = new ThemisUISourceFileSelect(pFactory);
        final ThemisUISourcePackageSelect myPackageSelect = new ThemisUISourcePackageSelect(pFactory, theFileSelect);
        theModuleSelect = new ThemisUISourceModuleSelect(pFactory, myPackageSelect);

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

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    /**
     * Set the current project.
     *
     * @param pProject the current project
     */
    public void setCurrentProject(final ThemisProject pProject) {
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
    private void initialiseTree(final ThemisFile pFile) {
        /* If there is currently a root item */
        if (theRoot != null) {
            /* Hide it */
            theRoot.setVisible(false);
        }

        /* Create root item for project */
        final ThemisNodeCompilationUnit myUnit = pFile.getContents();
        final ThemisUISourceEntry myEntry = new ThemisUISourceEntry(myUnit);
        theRoot = theTree.addRootItem(myEntry.getUniqueName(), myEntry);
        theRoot.setIcon(myEntry.getIcon());
        theRoot.setVisible(true);
        handleNewTreeItem(myEntry);

        /* Create child entries */
        createChildEntries(theRoot, myUnit);
    }

    /**
     * Create child entries tree.
     *
     * @param pParent  the parent
     * @param pElement the element
     */
    private void createChildEntries(final TethysUITreeItem<ThemisUISourceEntry> pParent,
                                    final ThemisInstance pElement) {
        /* Loop through the root children */
        for (ThemisInstance myChild : pElement.getChildren()) {
            /* Create a new root entry */
            final ThemisUISourceEntry myEntry = new ThemisUISourceEntry(pParent.getItem(), myChild);
            final TethysUITreeItem<ThemisUISourceEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
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
            handleNewTreeItem(pEvent.getDetails(ThemisUISourceEntry.class));
        }
    }

    /**
     * Handle the new tree item.
     *
     * @param pEntry the new entry
     */
    private void handleNewTreeItem(final ThemisUISourceEntry pEntry) {
        /* If we have an entry */
        if (pEntry != null) {
            /* Access the Element */
            final ThemisInstance myElement = pEntry.getObject();

            /* Build report */
            final String myDoc = theDoc.formatElement(myElement);
            theHTML.setHTMLContent(myDoc, "");
        }
    }
}
