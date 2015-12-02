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
package net.sourceforge.joceanus.jmetis.viewer.swing;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;

/**
 * Data Window display class.
 * @author Tony Washer
 */
public class MetisViewerWindow
        extends JFrame {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 3055614623371854422L;

    /**
     * Panel Width.
     */
    private static final int PANEL_WIDTH = 900;

    /**
     * Panel Height.
     */
    private static final int PANEL_HEIGHT = 600;

    /**
     * The JTree component.
     */
    private final JTree theTree;

    /**
     * The Item pane.
     */
    private final transient MetisViewerItem theItemPane;

    /**
     * Constructor.
     * @param pParent the parent frame
     * @param pManager the viewer manager
     */
    public MetisViewerWindow(final JFrame pParent,
                             final MetisSwingViewerManager pManager) {
        /* Store the parameters */
        theTree = new JTree(pManager.getModel());

        /* Notify viewer manager */
        pManager.declareWindow(this);

        /* Set the title */
        setTitle(pManager.getTitle());

        /* Make sure that we have single selection model */
        theTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        /* Hide the root selection */
        theTree.setRootVisible(false);
        theTree.setShowsRootHandles(true);
        theTree.setScrollsOnExpand(true);
        theTree.setExpandsSelectedPaths(true);

        /* Access the initial id */
        MetisSwingViewerEntry myEntry = pManager.getFocus();

        /* Declare listener */
        new ViewerListener();

        /* Create a scroll-pane for the tree */
        JScrollPane myTreeScroll = new JScrollPane(theTree);

        /* Create the item panel */
        theItemPane = new MetisViewerItem(pManager.getHTMLFormatter());

        /* Create the split pane */
        JSplitPane mySplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, myTreeScroll, theItemPane.getPanel());
        mySplit.setOneTouchExpandable(true);
        mySplit.setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        /* Create the panel */
        JPanel myPanel = new JPanel();
        myPanel.add(mySplit);

        /* Set this to be the main panel */
        getContentPane().add(myPanel);
        pack();

        /* Set the relative location */
        setLocationRelativeTo(pParent);
        displayData(myEntry);
    }

    /**
     * Set new formatter.
     * @param pFormatter the formatter
     */
    protected void setFormatter(final MetisViewerHTML pFormatter) {
        /* Use the new formatter */
        theItemPane.setFormatter(pFormatter);
    }

    /**
     * display the dialog.
     */
    public void showDialog() {
        /* Display the window */
        setVisible(true);
    }

    /**
     * Display the data.
     * @param pEntry the data entry
     */
    protected final void displayData(final MetisSwingViewerEntry pEntry) {
        /* Ignore null entry */
        if (pEntry == null) {
            return;
        }

        /* Sort out the tree */
        TreePath myPath = pEntry.getPath();
        theTree.setSelectionPath(myPath);
        theTree.scrollPathToVisible(myPath);

        /* display the entry in the panel */
        theItemPane.displayData(pEntry);
    }

    /**
     * Update debug.
     * @param pEntry the debug entry
     */
    protected void updateData(final MetisViewerEntry pEntry) {
        /* update debug if required */
        theItemPane.updateData(pEntry);
    }

    /**
     * Listener class.
     */
    private final class ViewerListener
            implements TreeSelectionListener {
        /**
         * Constructor.
         */
        private ViewerListener() {
            /* Add the listener for the tree */
            theTree.addTreeSelectionListener(this);
        }

        @Override
        public void valueChanged(final TreeSelectionEvent e) {
            /* Access selected node */
            DefaultMutableTreeNode myNode = (DefaultMutableTreeNode) theTree.getLastSelectedPathComponent();

            /* Ignore if there is no selection */
            if (myNode == null) {
                return;
            }

            /* Access the Data Entry */
            MetisSwingViewerEntry myData = (MetisSwingViewerEntry) myNode.getUserObject();

            /* display the node */
            displayData(myData);
        }
    }
}
