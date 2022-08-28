/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.help;

import java.util.List;

import net.sourceforge.joceanus.jmetis.viewer.MetisViewerResource;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysChildDialog;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager.TethysStyleSheetId;
import net.sourceforge.joceanus.jtethys.ui.TethysSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager.TethysTreeItem;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;

/**
 * Help Manager class, responsible for displaying the help.
 */
public class MetisHelpWindow
        implements TethysEventProvider<TethysXUIEvent> {
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
    private final TethysGuiFactory theFactory;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysXUIEvent> theEventManager;

    /**
     * The split tree.
     */
    private final TethysSplitTreeManager<MetisHelpEntry> theSplitTree;

    /**
     * The tree manager.
     */
    private final TethysTreeManager<MetisHelpEntry> theTree;

    /**
     * The help dialog.
     */
    private TethysChildDialog theDialog;

    /**
     * The HTML manager.
     */
    private final TethysHTMLManager theHtml;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public MetisHelpWindow(final TethysGuiFactory pFactory) {
        /* Store parameters */
        theFactory = pFactory;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the splitTree manager and obtain details */
        theSplitTree = pFactory.newSplitTreeManager();
        theTree = theSplitTree.getTreeManager();
        theHtml = theSplitTree.getHTMLManager();

        /* Listen to the TreeManager */
        theSplitTree.getEventRegistrar().addEventListener(this::handleSplitTreeAction);
    }

    @Override
    public TethysEventRegistrar<TethysXUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the SplitTree Manager.
     * @return the tree manager
     */
    public TethysSplitTreeManager<MetisHelpEntry> getSplitTreeManager() {
        return theSplitTree;
    }

    /**
     * Obtain the Tree Manager.
     * @return the tree manager
     */
    public TethysTreeManager<MetisHelpEntry> getTreeManager() {
        return theTree;
    }

    /**
     * Obtain the HTML Manager.
     * @return the HTML manager
     */
    public TethysHTMLManager getHTMLManager() {
        return theHtml;
    }

    /**
     * Fire event.
     * @param pEventId the eventId
     * @param pValue the relevant value
     */
    protected void fireEvent(final TethysXUIEvent pEventId,
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
            theDialog = theFactory.newChildDialog();
            theDialog.setTitle(MetisHelpResource.TITLE.getValue());

            /* Create the help panel */
            final TethysBorderPaneManager myPanel = theFactory.newBorderPane();
            myPanel.setCentre(theSplitTree);
            myPanel.setPreferredWidth(WINDOW_WIDTH);
            myPanel.setPreferredHeight(WINDOW_HEIGHT);
            theDialog.setContent(myPanel);

            /* Set listener */
            theDialog.getEventRegistrar().addEventListener(TethysXUIEvent.WINDOWCLOSED, e -> {
                theTree.setVisible(false);
                fireEvent(TethysXUIEvent.WINDOWCLOSED, null);
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
        final TethysStyleSheetId myCSS = pModule.getCSS();
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
    protected void handleSplitTreeAction(final TethysEvent<TethysXUIEvent> pEvent) {
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
    private TethysTreeItem<MetisHelpEntry> createTree(final String pTitle,
                                                      final List<MetisHelpEntry> pEntries) {
        /* Obtain the root node */
        final TethysTreeItem<MetisHelpEntry> myRoot = theTree.getRoot();
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
    private void addHelpEntries(final TethysTreeItem<MetisHelpEntry> pParent,
                                final List<MetisHelpEntry> pEntries) {
        /* Loop through the entries */
        for (MetisHelpEntry myEntry : pEntries) {
            /* Create the entry */
            final TethysTreeItem<MetisHelpEntry> myItem = theTree.addChildItem(pParent, myEntry.getName(), myEntry);

            /* If we have children */
            final List<MetisHelpEntry> myChildren = myEntry.getChildren();
            if (myChildren != null) {
                /* Add the children into the tree */
                addHelpEntries(myItem, myChildren);
            }
        }
    }
}
