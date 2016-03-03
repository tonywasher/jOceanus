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
package net.sourceforge.joceanus.jmetis.newviewer.swing;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTreeManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTreeManager.TethysSwingTreeItem;

/**
 * JavaSwing Data Viewer Manager.
 */
public class MetisSwingViewerManager
        extends MetisViewerManager<MetisSwingViewerEntry, JComponent> {
    /**
     * The frame.
     */
    private JFrame theBaseFrame;

    /**
     * The help dialog.
     */
    private ViewerDialog theDialog;

    /**
     * Constructor.
     */
    public MetisSwingViewerManager() {
        /* Initialise underlying class */
        super(new TethysSwingSplitTreeManager<>());
    }

    @Override
    public TethysSwingSplitTreeManager<MetisSwingViewerEntry> getSplitTreeManager() {
        return (TethysSwingSplitTreeManager<MetisSwingViewerEntry>) super.getSplitTreeManager();
    }

    @Override
    public TethysSwingTreeManager<MetisSwingViewerEntry> getTreeManager() {
        return (TethysSwingTreeManager<MetisSwingViewerEntry>) super.getTreeManager();
    }

    @Override
    public TethysSwingHTMLManager getHTMLManager() {
        return (TethysSwingHTMLManager) super.getHTMLManager();
    }

    @Override
    public void showDialog() {
        /* If the dialog does not exist */
        if (theDialog == null) {
            /* Create a new dialog */
            theDialog = new ViewerDialog();
        }

        /* Make sure that the dialog is showing */
        theDialog.showDialog();
    }

    @Override
    public void hideDialog() {
        /* If the dialog exists */
        if (theDialog != null) {
            /* Make sure that the dialog is hidden */
            theDialog.hideDialog();
        }
    }

    /**
     * Set the frame.
     * @param pFrame the frame
     */
    public void setFrame(final JFrame pFrame) {
        theBaseFrame = pFrame;
    }

    @Override
    public MetisSwingViewerEntry newEntry(final String pName) throws OceanusException {
        /* Create the entry */
        MetisSwingViewerEntry myEntry = new MetisSwingViewerEntry(this, pName);

        /* Define and set the tree entry */
        TethysSwingTreeManager<MetisSwingViewerEntry> myManager = getTreeManager();
        TethysSwingTreeItem<MetisSwingViewerEntry> myTreeItem = new TethysSwingTreeItem<>(myManager,
                myManager.getRoot(), pName, myEntry);
        myEntry.setTreeItem(myTreeItem);

        /* Return the new entry */
        return myEntry;
    }

    @Override
    public MetisSwingViewerEntry newEntry(final MetisViewerEntry<MetisSwingViewerEntry, JComponent> pParent,
                                          final String pName) throws OceanusException {
        /* Access parent and create the entry */
        MetisSwingViewerEntry myParent = MetisSwingViewerEntry.class.cast(pParent);
        MetisSwingViewerEntry myEntry = new MetisSwingViewerEntry(this, pName);

        /* Build the new name */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myParent.getName());
        myBuilder.append('.');
        myBuilder.append(pName);
        String myName = myBuilder.toString();

        /* Define and set the tree entry */
        TethysSwingTreeItem<MetisSwingViewerEntry> myTreeItem = new TethysSwingTreeItem<>(getTreeManager(),
                myParent.getTreeItem(), myName, myEntry);
        myEntry.setTreeItem(myTreeItem);

        /* Return the new entry */
        return myEntry;
    }

    /**
     * Dialog class.
     */
    private final class ViewerDialog {
        /**
         * The frame.
         */
        private final JFrame theFrame;

        /**
         * Constructor.
         */
        private ViewerDialog() {
            /* Create the frame */
            theFrame = new JFrame();

            /* Set the title */
            theFrame.setTitle("Data Manager");

            /* Create the help panel */
            JPanel myPanel = new JPanel();
            JComponent mySplit = getSplitTreeManager().getNode();
            myPanel.add(mySplit);
            mySplit.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            /* Set this to be the main panel */
            theFrame.add(myPanel);
            theFrame.pack();

            /* Change visibility of tree when hiding */
            theFrame.addWindowListener(new ViewerWindowAdapter());
        }

        /**
         * Show the dialog.
         */
        private void showDialog() {
            /* If the dialog is not currently showing */
            if (!theFrame.isShowing()) {
                /* Set the relative location */
                theFrame.setLocationRelativeTo(theBaseFrame);

                /* Set the tree as visible */
                getTreeManager().setVisible(true);

                /* Show the dialog */
                theFrame.setVisible(true);
            }
        }

        /**
         * Hide the dialog.
         */
        private void hideDialog() {
            /* If the dialog is visible */
            if (theFrame.isShowing()) {
                /* hide it */
                theFrame.setVisible(false);
            }
        }

        /**
         * handleWindow Closing.
         */
        private void handleWindowClosing() {
            getTreeManager().setVisible(false);
            theFrame.dispose();
            fireEvent(TethysUIEvent.WINDOWCLOSED, null);
        }

        /**
         * Window adapter class.
         */
        private class ViewerWindowAdapter
                extends WindowAdapter {
            @Override
            public void windowClosing(final WindowEvent e) {
                handleWindowClosing();
            }
        }
    }
}
