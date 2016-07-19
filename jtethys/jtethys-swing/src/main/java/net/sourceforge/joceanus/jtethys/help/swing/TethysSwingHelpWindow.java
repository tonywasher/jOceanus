/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.help.swing;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.help.TethysHelpEntry;
import net.sourceforge.joceanus.jtethys.help.TethysHelpResource;
import net.sourceforge.joceanus.jtethys.help.TethysHelpWindow;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingHTMLManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSplitTreeManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingTreeManager;

/**
 * Help Window class, responsible for displaying the help.
 */
public class TethysSwingHelpWindow
        extends TethysHelpWindow<JComponent, Icon> {
    /**
     * The frame.
     */
    private final JFrame theBaseFrame;

    /**
     * The help dialog.
     */
    private HelpDialog theDialog;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    public TethysSwingHelpWindow(final TethysSwingGuiFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);
        theBaseFrame = pFactory.getFrame();
    }

    @Override
    public TethysSwingSplitTreeManager<TethysHelpEntry> getSplitTreeManager() {
        return (TethysSwingSplitTreeManager<TethysHelpEntry>) super.getSplitTreeManager();
    }

    @Override
    public TethysSwingTreeManager<TethysHelpEntry> getTreeManager() {
        return (TethysSwingTreeManager<TethysHelpEntry>) super.getTreeManager();
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
            theDialog = new HelpDialog();
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
     * Dialog class.
     */
    private final class HelpDialog {
        /**
         * The frame.
         */
        private final JFrame theFrame;

        /**
         * Constructor.
         */
        private HelpDialog() {
            /* Create the frame */
            theFrame = new JFrame();

            /* Set the title */
            theFrame.setTitle(TethysHelpResource.TITLE.getValue());

            /* Create the help panel */
            JPanel myPanel = new JPanel();
            JComponent mySplit = getSplitTreeManager().getNode();
            myPanel.add(mySplit);
            mySplit.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

            /* Set this to be the main panel */
            theFrame.add(myPanel);
            theFrame.pack();

            /* Change visibility of tree when hiding */
            theFrame.addWindowListener(new HelpWindowAdapter());
        }

        /**
         * show the dialog.
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
        private class HelpWindowAdapter
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
