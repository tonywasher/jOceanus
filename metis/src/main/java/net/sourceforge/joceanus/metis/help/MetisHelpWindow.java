/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.help;

import java.util.List;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager.TethysUIStyleSheetId;
import net.sourceforge.joceanus.tethys.api.control.TethysUISplitTreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager.TethysUITreeItem;
import net.sourceforge.joceanus.tethys.api.dialog.TethysUIChildDialog;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBorderPaneManager;

/**
 * Help Manager class, responsible for displaying the help.
 */
public class MetisHelpWindow
        implements OceanusEventProvider<TethysUIEvent> {
    /**
     * The Height of the window.
     */
    protected static final int WINDOW_WIDTH = 900;

    /**
     * The Height of the window.
     */
    protected static final int WINDOW_HEIGHT = 600;

    /**
     * The Factory.
     */
    private final TethysUIFactory<?> theFactory;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The split tree.
     */
    private final TethysUISplitTreeManager<MetisHelpEntry> theSplitTree;

    /**
     * The tree manager.
     */
    private final TethysUITreeManager<MetisHelpEntry> theTree;

    /**
     * The help dialog.
     */
    private TethysUIChildDialog theDialog;

    /**
     * The HTML manager.
     */
    private final TethysUIHTMLManager theHtml;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public MetisHelpWindow(final TethysUIFactory<?> pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the splitTree manager and obtain details */
        theSplitTree = pFactory.controlFactory().newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHtml = theSplitTree.getHTMLManager();

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
    public TethysUISplitTreeManager<MetisHelpEntry> getSplitTreeManager() {
        return theSplitTree;
    }

    /**
     * Obtain the Tree Manager.
     * @return the tree manager
     */
    public TethysUITreeManager<MetisHelpEntry> getTreeManager() {
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
            theDialog.setTitle(MetisHelpResource.TITLE.getValue());

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
     * Set the help module.
     * @param pModule the helpModule
     * @throws OceanusException on error
     */
    public void setModule(final MetisHelpModule pModule) throws OceanusException {
        /* Access the Help entries and list */
        final List<MetisHelpEntry> myEntries = pModule.getHelpEntries();

        /* Declare CSS */
        final TethysUIStyleSheetId myCSS = pModule.getCSS();
        if (myCSS != null) {
            theHtml.setCSSContent(myCSS);
        }

        /* Create the tree */
        createTree(pModule.getTitle(), myEntries);
    }

    /**
     * Handle the split tree action event.
     * @param pEvent the event
     */
    protected void handleSplitTreeAction(final OceanusEvent<TethysUIEvent> pEvent) {
        switch (pEvent.getEventId()) {
            case NEWVALUE:
                handleNewTreeItem(pEvent.getDetails(MetisHelpEntry.class));
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
    private void handleNewTreeItem(final MetisHelpEntry pEntry) {
        if (pEntry != null) {
            final String myHtml = pEntry.getHtml();
            if (myHtml != null) {
                theHtml.setHTMLContent(myHtml, pEntry.getName());
            }
        }
    }

    /**
     * Construct a top level Tree Node from a set of help entries.
     * @param pTitle the title for the tree
     * @param pEntries the help entries
     * @return the Tree node
     */
    private TethysUITreeItem<MetisHelpEntry> createTree(final String pTitle,
                                                        final List<MetisHelpEntry> pEntries) {
        /* Obtain the root node */
        final TethysUITreeItem<MetisHelpEntry> myRoot = theTree.getRoot();
        theTree.setRootName(pTitle);
        theTree.setRootVisible();

        /* Clear existing children */
        myRoot.removeChildren();

        /* Add the entries into the node */
        addHelpEntries(myRoot, pEntries);

        /* Return the root */
        return myRoot;
    }

    /**
     * Add array of Help entries.
     * @param pParent the parent to add to
     * @param pEntries the entries to add
     */
    private void addHelpEntries(final TethysUITreeItem<MetisHelpEntry> pParent,
                                final List<MetisHelpEntry> pEntries) {
        /* Loop through the entries */
        for (MetisHelpEntry myEntry : pEntries) {
            /* Create the entry */
            final TethysUITreeItem<MetisHelpEntry> myItem = theTree.addChildItem(pParent, myEntry.getName(), myEntry);

            /* If we have children */
            final List<MetisHelpEntry> myChildren = myEntry.getChildren();
            if (myChildren != null) {
                /* Add the children into the tree */
                addHelpEntries(myItem, myChildren);
            }
        }
    }
}
