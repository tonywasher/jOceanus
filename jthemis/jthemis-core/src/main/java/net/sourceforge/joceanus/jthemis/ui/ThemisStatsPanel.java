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

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager.TethysTreeItem;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;
import net.sourceforge.joceanus.jthemis.statistics.ThemisStatsBase;
import net.sourceforge.joceanus.jthemis.statistics.ThemisStatsProject;
import net.sourceforge.joceanus.jthemis.statistics.ThemisStatsReport;

/**
 * Statistics Panel.
 */
public class ThemisStatsPanel
        implements TethysComponent {
    /**
     * The Next entryId.
     */
    private static final AtomicInteger NEXT_ENTRY_ID = new AtomicInteger(1);

    /**
     * The SplitTree Manager.
     */
    private final TethysSplitTreeManager<ThemisStatsEntry> theSplitTree;

    /**
     * The Tree Manager.
     */
    private final TethysTreeManager<ThemisStatsEntry> theTree;

    /**
     * The HTML Manager.
     */
    private final TethysHTMLManager theHTML;

    /**
     * The Current root.
     */
    private TethysTreeItem<ThemisStatsEntry> theRoot;

    /**
     * Constructor.
     *
     * @param pFactory the GuiFactory
     * @throws OceanusException on error
     */
    public ThemisStatsPanel(final TethysGuiFactory pFactory) throws OceanusException {
        /* Create the splitTree Manager */
        theSplitTree = pFactory.newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHTML = theSplitTree.getHTMLManager();
        theHTML.setCSSContent(ThemisDSMStyleSheet.CSS_DSM);
        theTree.setVisible(true);

        /* Listen to the TreeManager */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
    }

    @Override
    public TethysNode getNode() {
        return theSplitTree.getNode();
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theSplitTree.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theSplitTree.setVisible(pVisible);
    }

    @Override
    public Integer getId() {
        return theSplitTree.getId();
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
    void createChildEntries(final TethysTreeItem<ThemisStatsEntry> pParent,
                            final ThemisStatsBase pChild) {
        /* Loop through the root children */
        final Iterator<ThemisStatsBase> myIterator = pChild.childIterator();
        while (myIterator.hasNext()) {
            final ThemisStatsBase myChild = myIterator.next();

            /* Create a new root entry */
            final ThemisStatsEntry myEntry = new ThemisStatsEntry(pParent.getItem(), myChild);
            final TethysTreeItem<ThemisStatsEntry> myTreeItem = theTree.addChildItem(pParent, myEntry.getUniqueName(), myEntry);
            myTreeItem.setVisible(true);

            /* Create child entries */
            createChildEntries(myTreeItem, myChild);
        }
    }

    /**
     * Handle the split tree action event.
     * @param pEvent the event
     */
    protected void handleSplitTreeAction(final TethysEvent<TethysXUIEvent> pEvent) {
        /* If this is a new value */
        if (pEvent.getEventId() == TethysXUIEvent.NEWVALUE) {
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
