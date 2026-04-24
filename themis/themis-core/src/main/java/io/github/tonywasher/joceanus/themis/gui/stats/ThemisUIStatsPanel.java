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

package io.github.tonywasher.joceanus.themis.gui.stats;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEvent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIHTMLManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUISplitTreeManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUITreeManager;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUITreeManager.TethysUITreeItem;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIStyleSheet;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsClass;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsElement;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsFile;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsMethod;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsModule;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsPackage;
import io.github.tonywasher.joceanus.themis.stats.ThemisStatsProject;

public class ThemisUIStatsPanel
        implements TethysUIComponent {
    /**
     * The SplitTree Manager.
     */
    private final TethysUISplitTreeManager<ThemisUIStatsEntry> theSplitTree;

    /**
     * The Tree Manager.
     */
    private final TethysUITreeManager<ThemisUIStatsEntry> theTree;

    /**
     * The HTML Manager.
     */
    private final TethysUIHTMLManager theHTML;

    /**
     * The Document Builder.
     */
    private final ThemisUIStatsDocument theDoc;

    /**
     * The Current root.
     */
    private TethysUITreeItem<ThemisUIStatsEntry> theRoot;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisUIStatsPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Create the splitTree Manager */
        theSplitTree = pFactory.controlFactory().newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHTML = theSplitTree.getHTMLManager();
        theHTML.setCSSContent(ThemisUIStyleSheet.CSS);
        theTree.setVisible(true);

        /* Create the document builder */
        theDoc = new ThemisUIStatsDocument();

        /* Create listeners */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return theSplitTree;
    }

    /**
     * Set the current project.
     *
     * @param pProject the current project
     */
    public void setCurrentProject(final ThemisStatsProject pProject) {
        /* Initialise the tree */
        initialiseTree(pProject);
    }

    /**
     * Initialise tree.
     *
     * @param pProject the stats project
     */
    private void initialiseTree(final ThemisStatsProject pProject) {
        /* If there is currently a root item */
        if (theRoot != null) {
            /* Hide it */
            theRoot.setVisible(false);
        }

        /* If we have a project */
        if (pProject != null) {
            final ThemisUIStatsEntry myEntry = new ThemisUIStatsEntry(pProject);
            theRoot = theTree.addRootItem(myEntry.getUniqueName(), myEntry);
            theRoot.setVisible(true);

            /* Loop through the modules */
            for (ThemisStatsModule myModule : pProject.getModules()) {
                addModule(theRoot, myModule);
            }
        }
    }

    /**
     * Create moduleNode.
     *
     * @param pParent the parent
     * @param pModule the module
     */
    private void addModule(final TethysUITreeItem<ThemisUIStatsEntry> pParent,
                           final ThemisStatsModule pModule) {
        /* Create a new root entry */
        final ThemisUIStatsEntry myEntry = new ThemisUIStatsEntry(pParent.getItem(), pModule);
        final TethysUITreeItem<ThemisUIStatsEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
        myTreeItem.setVisible(true);

        /* Loop through the packages */
        for (ThemisStatsPackage myPackage : pModule.getPackages()) {
            if (!myPackage.getUnderlying().isPlaceHolder()) {
                addPackage(myTreeItem, myPackage);
            }
        }
    }

    /**
     * Create packageNode.
     *
     * @param pParent  the parent
     * @param pPackage the package
     */
    private void addPackage(final TethysUITreeItem<ThemisUIStatsEntry> pParent,
                            final ThemisStatsPackage pPackage) {
        /* Create a new root entry */
        final ThemisUIStatsEntry myEntry = new ThemisUIStatsEntry(pParent.getItem(), pPackage);
        final TethysUITreeItem<ThemisUIStatsEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
        myTreeItem.setVisible(true);

        /* Loop through the packages */
        for (ThemisStatsFile myFile : pPackage.getFiles()) {
            addFile(myTreeItem, myFile);
        }
    }

    /**
     * Create fileNode.
     *
     * @param pParent the parent
     * @param pFile   the file
     */
    private void addFile(final TethysUITreeItem<ThemisUIStatsEntry> pParent,
                         final ThemisStatsFile pFile) {
        /* Create a new root entry */
        final ThemisUIStatsEntry myEntry = new ThemisUIStatsEntry(pParent.getItem(), pFile);
        final TethysUITreeItem<ThemisUIStatsEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
        myTreeItem.setVisible(true);

        /* Loop through the classes */
        for (ThemisStatsClass myClass : pFile.getClasses()) {
            addClass(myTreeItem, myClass);
        }
    }

    /**
     * Create classNode.
     *
     * @param pParent the parent
     * @param pClass  the class
     */
    private void addClass(final TethysUITreeItem<ThemisUIStatsEntry> pParent,
                          final ThemisStatsClass pClass) {
        /* Create a new root entry */
        final ThemisUIStatsEntry myEntry = new ThemisUIStatsEntry(pParent.getItem(), pClass);
        final TethysUITreeItem<ThemisUIStatsEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
        myTreeItem.setVisible(true);

        /* Loop through the classes */
        for (ThemisStatsClass myClass : pClass.getClasses()) {
            addClass(myTreeItem, myClass);
        }

        /* Loop through the methods */
        for (ThemisStatsMethod myMethod : pClass.getMethods()) {
            addMethod(myTreeItem, myMethod);
        }
    }

    /**
     * Create methodNode.
     *
     * @param pParent the parent
     * @param pMethod the method
     */
    private void addMethod(final TethysUITreeItem<ThemisUIStatsEntry> pParent,
                           final ThemisStatsMethod pMethod) {
        /* Create a new root entry */
        final ThemisUIStatsEntry myEntry = new ThemisUIStatsEntry(pParent.getItem(), pMethod);
        final TethysUITreeItem<ThemisUIStatsEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
        myTreeItem.setVisible(true);

        /* Loop through the classes */
        for (ThemisStatsClass myClass : pMethod.getClasses()) {
            addClass(myTreeItem, myClass);
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
            handleNewTreeItem(pEvent.getDetails(ThemisUIStatsEntry.class));
        }
    }

    /**
     * Handle the new tree item.
     *
     * @param pEntry the new entry
     */
    private void handleNewTreeItem(final ThemisUIStatsEntry pEntry) {
        /* If we have an entry */
        if (pEntry != null) {
            /* Access the Element */
            final ThemisStatsElement myElement = pEntry.getObject();

            /* Build report */
            final String myDoc = theDoc.formatElement(myElement);
            theHTML.setHTMLContent(myDoc, "");
        }
    }
}
