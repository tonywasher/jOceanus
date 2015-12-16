/*******************************************************************************
 * jTethys: Java Utilities
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/help/swing/TethysSwingHelpWindow.java $
 * $Revision: 652 $
 * $Author: Tony $
 * $Date: 2015-11-24 10:46:21 +0000 (Tue, 24 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.help;

import java.util.List;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysActionEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.TethysSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTreeManager.TethysTreeItem;

/**
 * Help Manager class, responsible for displaying the help.
 * @param <N> the Node type
 */
public abstract class TethysHelpManager<N>
        implements TethysEventProvider {
    /**
     * Window closed.
     */
    public static final int ACTION_WINDOW_CLOSED = 100;

    /**
     * The Height of the window.
     */
    protected static final int WINDOW_WIDTH = 900;

    /**
     * The Height of the window.
     */
    protected static final int WINDOW_HEIGHT = 600;

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * The split tree.
     */
    private final TethysSplitTreeManager<TethysHelpEntry, N> theSplitTree;

    /**
     * The tree manager.
     */
    private final TethysTreeManager<TethysHelpEntry, N> theTree;

    /**
     * The HTML manager.
     */
    private final TethysHTMLManager<N> theHtml;

    /**
     * Constructor.
     * @param pSplitManager the split tree manager
     */
    public TethysHelpManager(final TethysSplitTreeManager<TethysHelpEntry, N> pSplitManager) {
        /* Create the event manager */
        theEventManager = new TethysEventManager();

        /* Create the SplitTree */
        theSplitTree = pSplitManager;
        theTree = theSplitTree.getTreeManager();
        theHtml = theSplitTree.getHTMLManager();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the SplitTree Manager.
     * @return the tree manager
     */
    public TethysSplitTreeManager<TethysHelpEntry, N> getSplitTreeManager() {
        return theSplitTree;
    }

    /**
     * Obtain the Tree Manager.
     * @return the tree manager
     */
    public TethysTreeManager<TethysHelpEntry, N> getTreeManager() {
        return theTree;
    }

    /**
     * Obtain the HTML Manager.
     * @return the HTML manager
     */
    public TethysHTMLManager<N> getHTMLManager() {
        return theHtml;
    }

    /**
     * Fire event.
     * @param pActionId the actionId
     * @param pValue the relevant value
     */
    protected void fireEvent(final int pActionId, final Object pValue) {
        theEventManager.fireActionEvent(pActionId, pValue);
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
     * Set the help module.
     * @param pModule the helpModule
     * @throws OceanusException on error
     */
    public void setModule(final TethysHelpModule pModule) throws OceanusException {
        /* Access the Help entries and list */
        List<TethysHelpEntry> myEntries = pModule.getHelpEntries();

        /* Create the tree */
        createTree(pModule.getTitle(), myEntries);
    }

    /**
     * Handle the split tree action event.
     * @param pEvent the event
     */
    protected void handleSplitTreeAction(final TethysActionEvent pEvent) {
        switch (pEvent.getActionId()) {
            case TethysTreeManager.ACTION_NEW_VALUE:
                handleNewTreeItem(pEvent.getDetails(TethysHelpEntry.class));
                break;
            case TethysHTMLManager.ACTION_PAGE_BUILD:
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

    /**
     * Construct a top level Tree Node from a set of help entries.
     * @param pTitle the title for the tree
     * @param pEntries the help entries
     * @return the Tree node
     * @throws OceanusException on error
     */
    private TethysTreeItem<TethysHelpEntry, N> createTree(final String pTitle,
                                                          final List<TethysHelpEntry> pEntries) throws OceanusException {
        /* Obtain the root node */
        TethysTreeItem<TethysHelpEntry, N> myRoot = theTree.getRoot();
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
     * @throws OceanusException on error
     */
    private void addHelpEntries(final TethysTreeItem<TethysHelpEntry, N> pParent,
                                final List<TethysHelpEntry> pEntries) throws OceanusException {
        /* Loop through the entries */
        for (TethysHelpEntry myEntry : pEntries) {
            /* Create the entry */
            TethysTreeItem<TethysHelpEntry, N> myItem = theTree.addChildItem(pParent, myEntry.getName(), myEntry);

            /* If we have children */
            List<TethysHelpEntry> myChildren = myEntry.getChildren();
            if (myChildren != null) {
                /* Add the children into the tree */
                addHelpEntries(myItem, myChildren);
            }
        }
    }
}
