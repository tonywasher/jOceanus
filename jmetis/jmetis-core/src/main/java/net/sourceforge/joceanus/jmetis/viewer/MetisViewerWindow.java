/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.viewer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
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
public abstract class MetisViewerWindow<N, I>
        implements TethysEventProvider<TethysUIEvent> {
    /**
     * The Name of the current page.
     */
    protected static final String NAME_CURRENT = "Current";

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
    private final MetisViewerManager theDataManager;

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
    private final TethysTreeManager<MetisViewerEntry, N, I> theTree;

    /**
     * The HTML manager.
     */
    private final TethysHTMLManager<N, I> theHtml;

    /**
     * The Formatter.
     */
    private final MetisViewerFormatter theFormatter;

    /**
     * The Registrations.
     */
    private final List<TethysEventRegistration<MetisViewerEvent>> theRegistrations;

    /**
     * The Control Window.
     */
    private final MetisViewerControl<N, I> theControl;

    /**
     * The Active page.
     */
    private MetisViewerPage theActive;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pDataManager the viewer data manager
     * @throws OceanusException on error
     */
    protected MetisViewerWindow(final TethysGuiFactory<N, I> pFactory,
                                final MetisViewerManager pDataManager) throws OceanusException {
        /* Record the data manager */
        theDataManager = pDataManager;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the splitTree and obtain details */
        theSplitTree = pFactory.newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHtml = theSplitTree.getHTMLManager();

        /* Create the Control */
        theControl = new MetisViewerControl<>(pFactory, this);
        theSplitTree.setControlPane(theControl);

        /* Create the registration lists */
        theRegistrations = new ArrayList<>();

        /* Create the formatter */
        theFormatter = new MetisViewerFormatter(pFactory.getDataFormatter());

        /* Load the CSS */
        loadCSS("MetisViewer.css");

        /* Initialise the tree */
        initialiseTree();

        /* Listen to the TreeManager */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
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
    public TethysTreeManager<MetisViewerEntry, N, I> getTreeManager() {
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
     * CloseWindow on parent termination.
     */
    public abstract void closeWindow();

    /**
     * Load CSS.
     * @param pName the name of the CSS
     * @throws OceanusException on error
     */
    private void loadCSS(final String pName) throws OceanusException {
        final String myCSS = TethysResourceBuilder.loadResourceToString(MetisViewerWindow.class, pName);
        theHtml.setCSSContent(myCSS);
    }

    /**
     * Initialise tree.
     */
    private void initialiseTree() {
        /* Loop through the root children */
        final Iterator<MetisViewerEntry> myIterator = theDataManager.rootIterator();
        while (myIterator.hasNext()) {
            final MetisViewerEntry myEntry = myIterator.next();

            /* Create a new root entry */
            final TethysTreeItem<MetisViewerEntry, N, I> myTreeItem = theTree.addRootItem(myEntry.getUniqueName(), myEntry);
            myTreeItem.setVisible(myEntry.isVisible());

            /* Create child entries */
            createChildEntries(myTreeItem);
        }

        /* Listen to the data manager */
        final TethysEventRegistrar<MetisViewerEvent> myRegistrar = theDataManager.getEventRegistrar();
        theRegistrations.add(myRegistrar.addEventListener(MetisViewerEvent.FOCUS, this::handleFocusEvent));
        theRegistrations.add(myRegistrar.addEventListener(MetisViewerEvent.VISIBILITY, this::handleVisibilityEvent));
        theRegistrations.add(myRegistrar.addEventListener(MetisViewerEvent.VALUE, this::handleValueEvent));
        theRegistrations.add(myRegistrar.addEventListener(MetisViewerEvent.ENTRY, this::handleEntryEvent));

        /* Select the focused item */
        final MetisViewerEntry myEntry = theDataManager.getFocus();
        if (myEntry != null) {
            theTree.lookUpAndSelectItem(myEntry.getUniqueName());
        }
    }

    /**
     * Terminate tree.
     */
    protected void terminateTree() {
        /* Loop through registrations */
        final TethysEventRegistrar<MetisViewerEvent> myRegistrar = theDataManager.getEventRegistrar();
        final Iterator<TethysEventRegistration<MetisViewerEvent>> myIterator = theRegistrations.iterator();
        while (myIterator.hasNext()) {
            final TethysEventRegistration<MetisViewerEvent> myRegistration = myIterator.next();

            /* Remove the registration */
            myRegistrar.removeEventListener(myRegistration);
            myIterator.remove();
        }

        /* Remove all tree entries */
        theTree.getRoot().removeChildren();
    }

    /**
     * create child items.
     * @param pItem the parent of the child items
     */
    private void createChildEntries(final TethysTreeItem<MetisViewerEntry, N, I> pItem) {
        /* Access the item */
        final MetisViewerEntry myItem = pItem.getItem();

        /* Loop through the children */
        final Iterator<MetisViewerEntry> myIterator = myItem.childIterator();
        while (myIterator.hasNext()) {
            final MetisViewerEntry myEntry = myIterator.next();

            /* Create a new child entry */
            final TethysTreeItem<MetisViewerEntry, N, I> myTreeItem = theTree.addChildItem(pItem, myEntry.getUniqueName(), myEntry);
            myTreeItem.setVisible(myEntry.isVisible());

            /* Create child entries */
            createChildEntries(myTreeItem);
        }
    }

    /**
     * Handle focus event.
     * @param pEvent the event
     */
    private void handleFocusEvent(final TethysEvent<MetisViewerEvent> pEvent) {
        /* Access item and check whether it is the currently selected item */
        final MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        final boolean isSelected = myEntry.equals(theTree.getSelectedItem());

        /* If it is not the selected item */
        if (!isSelected) {
            /* Select the item */
            theTree.lookUpAndSelectItem(myEntry.getUniqueName());
        }
    }

    /**
     * Handle visibility event.
     * @param pEvent the event
     */
    private void handleVisibilityEvent(final TethysEvent<MetisViewerEvent> pEvent) {
        /* Look up item and set visibility */
        final MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        final TethysTreeItem<MetisViewerEntry, N, I> myTreeItem = theTree.lookUpItem(myEntry.getUniqueName());
        if (myTreeItem != null) {
            myTreeItem.setVisible(myEntry.isVisible());
        }
    }

    /**
     * Handle value event.
     * @param pEvent the event
     */
    private void handleValueEvent(final TethysEvent<MetisViewerEvent> pEvent) {
        /* Look up item and rebuild */
        final MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        final boolean isSelected = myEntry.equals(theTree.getSelectedItem());
        final TethysTreeItem<MetisViewerEntry, N, I> myTreeItem = theTree.lookUpItem(myEntry.getUniqueName());

        /* Remove the children of the item and rebuild them */
        myTreeItem.removeChildren();
        createChildEntries(myTreeItem);

        /* If we are selected */
        if (isSelected) {
            /* Refresh the page */
            handleNewTreeItem(myEntry);
        }
    }

    /**
     * Handle entry event.
     * @param pEvent the event
     */
    private void handleEntryEvent(final TethysEvent<MetisViewerEvent> pEvent) {
        /* Look up parent item */
        final MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        final MetisViewerEntry myParent = myEntry.getParent();

        /* If there is no parent */
        if (myParent == null) {
            /* Add as root Item */
            theTree.addRootItem(myEntry.getUniqueName(), myEntry);

            /* else we are a child */
        } else {
            /* Look up the parent and add child */
            final TethysTreeItem<MetisViewerEntry, N, I> myTreeItem = theTree.lookUpItem(myParent.getUniqueName());
            theTree.addChildItem(myTreeItem, myEntry.getUniqueName(), myEntry);
        }
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
                handleNewTreeItem(pEvent.getDetails(MetisViewerEntry.class));
                break;
            case BUILDPAGE:
                handleLink(pEvent.getDetails(String.class));
                pEvent.consume();
                break;
            default:
                break;
        }
    }

    /**
     * Handle the new tree item.
     * @param pEntry the new entry
     */
    private void handleNewTreeItem(final MetisViewerEntry pEntry) {
        if (pEntry != null) {
            /* Create the page and ensure that we remember the focus */
            theActive = new MetisViewerPage(pEntry);
            updatePage();
            pEntry.setFocus();
        }
    }

    /**
     * Handle the parent page.
     */
    protected void handleParentPage() {
        /* Update the page */
        theActive = theActive.getParent();
        updatePage();
    }

    /**
     * Handle the next page.
     */
    protected void handleNextPage() {
        /* Update the page */
        theActive.next();
        updatePage();
    }

    /**
     * Handle the previous page.
     */
    protected void handlePreviousPage() {
        /* Update the page */
        theActive.previous();
        updatePage();
    }

    /**
     * Handle the explicit page.
     * @param pIndex the index of the page
     */
    protected void handleExplicitPage(final int pIndex) {
        /* Update the page */
        theActive.setPageNo(pIndex);
        updatePage();
    }

    /**
     * Handle the mode.
     * @param pMode the new mode
     */
    protected void handleMode(final MetisViewerMode pMode) {
        /* Update the page */
        theActive.setMode(pMode);
        updatePage();
    }

    /**
     * Handle the link.
     * @param pLink the name of the link
     */
    private void handleLink(final String pLink) {
        /* Update the page */
        theActive = theActive.newPage(pLink);
        updatePage();
    }

    /**
     * Update the page.
     */
    private void updatePage() {
        theFormatter.formatPage(theActive);
        theHtml.setHTMLContent(theActive.getHtml(), NAME_CURRENT);
        theControl.updateState(theActive);
    }
}
