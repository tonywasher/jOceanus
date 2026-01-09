/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.metis.viewer;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistration;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUISplitTreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager.TethysUITreeItem;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIChildDialog;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Viewer Manager class, responsible for displaying the debug view.
 */
public class MetisViewerWindow
        implements OceanusEventProvider<TethysUIEvent> {
    /**
     * The Name of the current page.
     */
    private static final String NAME_CURRENT = "Current";

    /**
     * The Height of the window.
     */
    private static final int WINDOW_WIDTH = 900;

    /**
     * The Height of the window.
     */
    private static final int WINDOW_HEIGHT = 600;

    /**
     * The Factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * The Data Manager.
     */
    private final MetisViewerManager theDataManager;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The split tree.
     */
    private final TethysUISplitTreeManager<MetisViewerEntry> theSplitTree;

    /**
     * The tree manager.
     */
    private final TethysUITreeManager<MetisViewerEntry> theTree;

    /**
     * The HTML manager.
     */
    private final TethysUIHTMLManager theHtml;

    /**
     * The Formatter.
     */
    private final MetisViewerFormatter theFormatter;

    /**
     * The Registrations.
     */
    private final List<OceanusEventRegistration<MetisViewerEvent>> theRegistrations;

    /**
     * The Control Window.
     */
    private final MetisViewerControl theControl;

    /**
     * The viewer dialog.
     */
    private TethysUIChildDialog theDialog;

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
    public MetisViewerWindow(final TethysUIFactory<?> pFactory,
                             final MetisViewerManager pDataManager) throws OceanusException {
        /* Record the parameters */
        theFactory = pFactory;
        theDataManager = pDataManager;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the splitTree and obtain details */
        theSplitTree = pFactory.controlFactory().newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHtml = theSplitTree.getHTMLManager();

        /* Create the Control */
        theControl = new MetisViewerControl(pFactory, this);
        theSplitTree.setControlPane(theControl);

        /* Create the registration lists */
        theRegistrations = new ArrayList<>();

        /* Create the formatter */
        theFormatter = new MetisViewerFormatter(pFactory.getDataFormatter());

        /* Load the CSS */
        theHtml.setCSSContent(MetisViewerStyleSheet.CSS_VIEWER);

        /* Initialise the tree */
        initialiseTree();

        /* Listen to the TreeManager */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the SplitTree Manager.
     * @return the tree manager
     */
    public TethysUISplitTreeManager<MetisViewerEntry> getSplitTreeManager() {
        return theSplitTree;
    }

    /**
     * Obtain the Tree Manager.
     * @return the tree manager
     */
    public TethysUITreeManager<MetisViewerEntry> getTreeManager() {
        return theTree;
    }

    /**
     * Obtain the HTML Manager.
     * @return the HTML manager
     */
    public TethysUIHTMLManager getHTMLManager() {
        return theHtml;
    }

    /**
     * Initialise tree.
     */
    private void initialiseTree() {
        /* Set up root */
        final TethysUITreeItem<MetisViewerEntry> myRoot = theTree.getRoot();
        theTree.setRootName(MetisViewerResource.VIEWER_ROOT.getValue());
        theTree.setRootVisible();

        /* Loop through the root children */
        final Iterator<MetisViewerEntry> myIterator = theDataManager.rootIterator();
        while (myIterator.hasNext()) {
            final MetisViewerEntry myEntry = myIterator.next();

            /* Create a new root entry */
            final TethysUITreeItem<MetisViewerEntry> myTreeItem = theTree.addChildItem(myRoot, myEntry.getUniqueName(), myEntry);
            myTreeItem.setVisible(myEntry.isVisible());

            /* Create child entries */
            createChildEntries(myTreeItem);
        }

        /* Listen to the data manager */
        final OceanusEventRegistrar<MetisViewerEvent> myRegistrar = theDataManager.getEventRegistrar();
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
        final OceanusEventRegistrar<MetisViewerEvent> myRegistrar = theDataManager.getEventRegistrar();
        final Iterator<OceanusEventRegistration<MetisViewerEvent>> myIterator = theRegistrations.iterator();
        while (myIterator.hasNext()) {
            final OceanusEventRegistration<MetisViewerEvent> myRegistration = myIterator.next();

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
    private void createChildEntries(final TethysUITreeItem<MetisViewerEntry> pItem) {
        /* Access the item */
        final MetisViewerEntry myItem = pItem.getItem();

        /* Loop through the children */
        final Iterator<MetisViewerEntry> myIterator = myItem.childIterator();
        while (myIterator.hasNext()) {
            final MetisViewerEntry myEntry = myIterator.next();

            /* Create a new child entry */
            final TethysUITreeItem<MetisViewerEntry> myTreeItem = theTree.addChildItem(pItem, myEntry.getUniqueName(), myEntry);
            myTreeItem.setVisible(myEntry.isVisible());

            /* Create child entries */
            createChildEntries(myTreeItem);
        }
    }

    /**
     * Handle focus event.
     * @param pEvent the event
     */
    private void handleFocusEvent(final OceanusEvent<MetisViewerEvent> pEvent) {
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
    private void handleVisibilityEvent(final OceanusEvent<MetisViewerEvent> pEvent) {
        /* Look up item and set visibility */
        final MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        final TethysUITreeItem<MetisViewerEntry> myTreeItem = theTree.lookUpItem(myEntry.getUniqueName());
        if (myTreeItem != null) {
            myTreeItem.setVisible(myEntry.isVisible());
        }
    }

    /**
     * Handle value event.
     * @param pEvent the event
     */
    private void handleValueEvent(final OceanusEvent<MetisViewerEvent> pEvent) {
        /* Look up item and rebuild */
        final MetisViewerEntry myEntry = pEvent.getDetails(MetisViewerEntry.class);
        final boolean isSelected = myEntry.equals(theTree.getSelectedItem());
        final TethysUITreeItem<MetisViewerEntry> myTreeItem = theTree.lookUpItem(myEntry.getUniqueName());

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
    private void handleEntryEvent(final OceanusEvent<MetisViewerEvent> pEvent) {
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
            final TethysUITreeItem<MetisViewerEntry> myTreeItem = theTree.lookUpItem(myParent.getUniqueName());
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
    public void showDialog() {
        /* If the dialog does not exist */
        if (theDialog == null) {
            /* Create a new dialog */
            theDialog = theFactory.dialogFactory().newChildDialog();
            theDialog.setTitle(MetisViewerResource.VIEWER_TITLE.getValue());

            /* Create the help panel */
            final TethysUIBorderPaneManager myPanel = theFactory.paneFactory().newBorderPane();
            myPanel.setCentre(theSplitTree);
            myPanel.setPreferredWidth(WINDOW_WIDTH);
            myPanel.setPreferredHeight(WINDOW_HEIGHT);
            theDialog.setContent(myPanel);

            /* Set listener */
            theDialog.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> {
                theTree.setVisible(false);
                fireEvent(TethysUIEvent.WINDOWCLOSED, null);
            });
        }

        /* If the dialog is not showing */
        if (!theDialog.isShowing()) {
            /* Make sure that the dialog is showing */
            theTree.setVisible(true);
            theDialog.showDialog();
        }
    }

    /**
     * Hide the dialog.
     */
    public void hideDialog() {
        /* If the dialog exists */
        if (theDialog != null
            && theDialog.isShowing()) {
            /* Make sure that the dialog is hidden */
            theDialog.hideDialog();

            /* Terminate the tree */
            terminateTree();
        }
    }

    /**
     * CloseWindow on parent termination.
     */
    public void closeWindow() {
        hideDialog();
        if (theDialog != null) {
            theDialog.closeDialog();
        }
    }

    /**
     * Handle the split tree action event.
     * @param pEvent the event
     */
    protected void handleSplitTreeAction(final OceanusEvent<TethysUIEvent> pEvent) {
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
