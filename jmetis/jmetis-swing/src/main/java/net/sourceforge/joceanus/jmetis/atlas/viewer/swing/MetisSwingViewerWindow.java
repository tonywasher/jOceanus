/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.viewer.swing;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerResource;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTreeManager;

/**
 * JavaSwing Data Viewer Manager.
 */
public class MetisSwingViewerWindow
        extends MetisViewerWindow<JComponent, Icon> {
    /**
     * The frame.
     */
    private final JFrame theBaseFrame;

    /**
     * The help dialog.
     */
    private ViewerDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pDataManager the viewer data manager
     * @throws OceanusException on error
     */
    public MetisSwingViewerWindow(final TethysSwingGuiFactory pFactory,
                                  final MetisViewerManager pDataManager) throws OceanusException {
        /* Initialise underlying class */
        super(pFactory, pDataManager);
        theBaseFrame = pFactory.getFrame();
    }

    @Override
    public TethysSwingSplitTreeManager<MetisViewerEntry> getSplitTreeManager() {
        return (TethysSwingSplitTreeManager<MetisViewerEntry>) super.getSplitTreeManager();
    }

    @Override
    public TethysSwingTreeManager<MetisViewerEntry> getTreeManager() {
        return (TethysSwingTreeManager<MetisViewerEntry>) super.getTreeManager();
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

        /* If the dialog is not showing */
        if (!theDialog.isShowing()) {
            /* Make sure that the dialog is showing */
            theDialog.showDialog();
        }
    }

    @Override
    public void hideDialog() {
        /* If the dialog exists */
        if ((theDialog != null)
            && theDialog.isShowing()) {
            /* Make sure that the dialog is hidden */
            theDialog.hideDialog();

            /* Terminate the tree */
            terminateTree();
        }
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
            theFrame.setTitle(MetisViewerResource.VIEWER_TITLE.getValue());

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
         * Is the dialog showing?
         * @return true/false
         */
        private boolean isShowing() {
            return theFrame.isShowing();
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
         * Window adapter class.
         */
        private class ViewerWindowAdapter
                extends WindowAdapter {
            @Override
            public void windowClosing(final WindowEvent e) {
                handleWindowClosing();
            }

            /**
             * handleWindow Closing.
             */
            private void handleWindowClosing() {
                getTreeManager().setVisible(false);
                theFrame.dispose();
                fireEvent(TethysUIEvent.WINDOWCLOSED, null);
            }
        }
    }
}
