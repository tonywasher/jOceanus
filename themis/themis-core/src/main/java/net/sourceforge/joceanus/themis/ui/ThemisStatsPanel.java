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

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUISplitTreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager.TethysUITreeItem;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.themis.statistics.ThemisStatsBase;
import net.sourceforge.joceanus.themis.statistics.ThemisStatsProject;
import net.sourceforge.joceanus.themis.statistics.ThemisStatsReport;

/**
 * Statistics Panel.
 */
public class ThemisStatsPanel {
    /**
     * The Next entryId.
     */
    private static final AtomicInteger NEXT_ENTRY_ID = new AtomicInteger(1);

    /**
     * The SplitTree Manager.
     */
    private final TethysUISplitTreeManager<ThemisStatsEntry> theSplitTree;

    /**
     * The Tree Manager.
     */
    private final TethysUITreeManager<ThemisStatsEntry> theTree;

    /**
     * The HTML Manager.
     */
    private final TethysUIHTMLManager theHTML;

    /**
     * The Current root.
     */
    private TethysUITreeItem<ThemisStatsEntry> theRoot;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisStatsPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Create the splitTree Manager */
        theSplitTree = pFactory.controlFactory().newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHTML = theSplitTree.getHTMLManager();
        theHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theTree.setVisible(true);

        /* Listen to the TreeManager */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
    }

    /**
     * Obtain the component.
     * @return the component
     */
    public TethysUIComponent getComponent() {
        return theSplitTree;
    }

    /**
     * Get NextId.
     * @return the next id
     */
    static int getNextId() {
        return NEXT_ENTRY_ID.getAndIncrement();
    }

    /**
     * Initialise tree.
     * @param pProject the project
     */
    void initialiseTree(final ThemisStatsProject pProject) {
        /* If there is currently a root item */
        if (theRoot != null) {
            /* Hide it */
            theRoot.setVisible(false);
        }

        /* Create root item for project */
        final ThemisStatsEntry myEntry = new ThemisStatsEntry(pProject);
        theRoot = theTree.addRootItem(myEntry.getUniqueName(), myEntry);
        theRoot.setVisible(true);

        /* Create child entries */
        createChildEntries(theRoot, pProject);
    }

    /**
     * Create child entries tree.
     * @param pParent the parent
     * @param pChild the child
     */
    void createChildEntries(final TethysUITreeItem<ThemisStatsEntry> pParent,
                            final ThemisStatsBase pChild) {
        /* Loop through the root children */
        final Iterator<ThemisStatsBase> myIterator = pChild.childIterator();
        while (myIterator.hasNext()) {
            final ThemisStatsBase myChild = myIterator.next();

            /* Create a new root entry */
            final ThemisStatsEntry myEntry = new ThemisStatsEntry(pParent.getItem(), myChild);
            final TethysUITreeItem<ThemisStatsEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
            myTreeItem.setVisible(true);

            /* Create child entries */
            createChildEntries(myTreeItem, myChild);
        }
    }

    /**
     * Handle the split tree action event.
     * @param pEvent the event
     */
    protected void handleSplitTreeAction(final OceanusEvent<TethysUIEvent> pEvent) {
        /* If this is a new value */
        if (pEvent.getEventId() == TethysUIEvent.NEWVALUE) {
            handleNewTreeItem(pEvent.getDetails(ThemisStatsEntry.class));
        }
    }

    /**
     * Handle the new tree item.
     * @param pEntry the new entry
     */
    private void handleNewTreeItem(final ThemisStatsEntry pEntry) {
        /* If we have an entry */
        if (pEntry != null) {
            /* Access the StatsHolder */
            final ThemisStatsBase myHolder = pEntry.getObject();

            /* Build report */
            final String myDoc = ThemisStatsReport.reportOnStats(myHolder);
            theHTML.setHTMLContent(myDoc, "");
        }
    }
}
