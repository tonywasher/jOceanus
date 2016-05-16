/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.newviewer;

import java.util.Iterator;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.help.TethysHelpEntry;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager.TethysTreeItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * Viewer Manager class, responsible for displaying the debug view.
 * @param <N> the Node type
 * @param <I> the Icon type
 */
public abstract class MetisViewerManager<N, I>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The Height of the window.
     */
    protected static final int WINDOW_WIDTH = 900;

    /**
     * The Height of the window.
     */
    protected static final int WINDOW_HEIGHT = 600;

    /**
     * The Data Manager.
     */
    private final MetisViewerDataManager theDataManager;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The split tree.
     */
    private final TethysSplitTreeManager<MetisViewerEntry, N, I> theSplitTree;

    /**
     * The tree manager.
     */
    private final TethysTreeManager<MetisViewerEntry, N> theTree;

    /**
     * The HTML manager.
     */
    private final TethysHTMLManager<N, I> theHtml;

    /**
     * Constructor.
     * @param pSplitManager the split tree manager
     * @param pDataManager the viewer data manager
     * @throws OceanusException on error
     */
    protected MetisViewerManager(final TethysSplitTreeManager<MetisViewerEntry, N, I> pSplitManager,
                                 final MetisViewerDataManager pDataManager) throws OceanusException {
        /* Record the data manager */
        theDataManager = pDataManager;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Obtain details about the SplitTree */
        theSplitTree = pSplitManager;
        theTree = theSplitTree.getTreeManager();
        theHtml = theSplitTree.getHTMLManager();

        /* initialise the tree */
        initialiseTree();

        /* Listen to the TreeManager */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);

        /* Listen to the data manager */
        TethysEventRegistrar<MetisViewerEvent> myRegistrar = theDataManager.getEventRegistrar();
        myRegistrar.addEventListener(MetisViewerEvent.FOCUS, this::handleFocusEvent);
        myRegistrar.addEventListener(MetisViewerEvent.VISIBILITY, this::handleVisibilityEvent);
        myRegistrar.addEventListener(MetisViewerEvent.VALUE, this::handleValueEvent);
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the SplitTree Manager.
     * @return the tree manager
     */
    public TethysSplitTreeManager<MetisViewerEntry, N, I> getSplitTreeManager() {
        return theSplitTree;
    }

    /**
     * Obtain the Tree Manager.
     * @return the tree manager
     */
    public TethysTreeManager<MetisViewerEntry, N> getTreeManager() {
        return theTree;
    }

    /**
     * Obtain the HTML Manager.
     * @return the HTML manager
     */
    public TethysHTMLManager<N, I> getHTMLManager() {
        return theHtml;
    }

    /**
     * Initialise tree.
     */
    private void initialiseTree() {
        /* Loop through the root children */
        Iterator<MetisViewerEntry> myIterator = theDataManager.rootIterator();
        while (myIterator.hasNext()) {
            MetisViewerEntry myEntry = myIterator.next();

            /* Create a new root entry */
            TethysTreeItem<MetisViewerEntry, N> myTreeItem = theTree.addRootItem(myEntry.getUniqueName(), myEntry);

            /* Create child entries */
            createChildEntries(myTreeItem);
        }
    }

    /**
     * create child items.
     * @param pItem the parent of the child items
     */
    private void createChildEntries(final TethysTreeItem<MetisViewerEntry, N> pItem) {
        /* Access the item */
        MetisViewerEntry myItem = pItem.getItem();

        /* Loop through the children */
        Iterator<MetisViewerEntry> myIterator = myItem.childIterator();
        while (myIterator.hasNext()) {
            MetisViewerEntry myEntry = myIterator.next();

            /* Create a new child entry */
            TethysTreeItem<MetisViewerEntry, N> myTreeItem = theTree.addChildItem(pItem, myEntry.getUniqueName(), myEntry);

            /* Create child entries */
            createChildEntries(myTreeItem);
        }
    }

    /**
     * Handle focus event.
     * @param pEvent the event
     */
    private void handleFocusEvent(final TethysEvent<MetisViewerEvent> pEvent) {
        /* Look up item and select it */
        MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        theTree.lookUpAndSelectItem(myEntry.getUniqueName());
    }

    /**
     * Handle visibility event.
     * @param pEvent the event
     */
    private void handleVisibilityEvent(final TethysEvent<MetisViewerEvent> pEvent) {
        /* Look up item and set visibility */
        MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        TethysTreeItem<MetisViewerEntry, N> myTreeItem = theTree.lookUpItem(myEntry.getUniqueName());
        myTreeItem.setVisible(myEntry.isVisible());
    }

    /**
     * Handle value event.
     * @param pEvent the event
     */
    private void handleValueEvent(final TethysEvent<MetisViewerEvent> pEvent) {
        /* Look up item and set visibility */
        MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        TethysTreeItem<MetisViewerEntry, N> myTreeItem = theTree.lookUpItem(myEntry.getUniqueName());

        /* Remove the children of the item and rebuild them */
        myTreeItem.removeChildren();
        createChildEntries(myTreeItem);
    }

    /**
     * Fire event.
     * @param pEventId the eventId
     * @param pValue the relevant value
     */
    protected void fireEvent(final TethysUIEvent pEventId,
                             final Object pValue) {
        theEventManager.fireEvent(pEventId, pValue);
    }

    /**
     * show the dialog.
     */
    public abstract void showDialog();

    /**
     * Hide the dialog.
     */
    public abstract void hideDialog();

    /**
     * Handle the split tree action event.
     * @param pEvent the event
     */
    protected void handleSplitTreeAction(final TethysEvent<TethysUIEvent> pEvent) {
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                handleNewTreeItem(pEvent.getDetails(TethysHelpEntry.class));
                break;
            case BUILDPAGE:
            default:
                break;
        }
    }

    /**
     * Handle the new tree item.
     * @param pEntry the new entry
     */
    private void handleNewTreeItem(final TethysHelpEntry pEntry) {
        if (pEntry != null) {
            String myHtml = pEntry.getHtml();
            if (myHtml != null) {
                theHtml.setHTMLContent(myHtml, pEntry.getName());
            }
        }
    }
}
