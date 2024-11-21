/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.swing.dialog;

import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.core.dialog.TethysUICoreChildDialog;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingNode;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Swing Child Dialog.
 */
public class TethysUISwingChildDialog
        extends TethysUICoreChildDialog {
    /**
     * The parent frame.
     */
    private final JFrame theParent;

    /**
     * The frame.
     */
    private final JFrame theFrame;

    /**
     * The Container.
     */
    private final JPanel theContainer;

    /**
     * Constructor.
     * @param pParent the parent frame
     */
    TethysUISwingChildDialog(final JFrame pParent) {
        /* Store parameter */
        theParent = pParent;

        /* Create the frame */
        theFrame = new JFrame();

        /* Create the help panel */
        theContainer = new JPanel();
        theContainer.setLayout(new BorderLayout());

        /* Set this to be the main panel */
        theFrame.add(theContainer);

        /* Change visibility of tree when hiding */
        theFrame.addWindowListener(new ViewerWindowAdapter());
    }

    @Override
    public void setTitle(final String pTitle) {
        theFrame.setTitle(pTitle);
    }

    @Override
    public void setContent(final TethysUIComponent pContent) {
        theContainer.add(TethysUISwingNode.getComponent(pContent), BorderLayout.CENTER);
    }

    @Override
    public void showDialog() {
        /* If the dialog is not currently showing */
        if (!isShowing()) {
            /* Set the relative location */
            theFrame.setLocationRelativeTo(theParent);

            /* Show the dialog */
            theFrame.pack();
            theFrame.setVisible(true);
        }
    }

    @Override
    public void hideDialog() {
        /* If the dialog is visible */
        if (isShowing()) {
            /* hide it */
            theFrame.setVisible(false);
        }
    }

    @Override
    public boolean isShowing() {
        return theFrame.isShowing();
    }

    @Override
    public void closeDialog() {
        /* close the dialog */
        theFrame.dispose();
    }

    /**
     * Window adapter class.
     */
    private final class ViewerWindowAdapter
            extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent e) {
            handleWindowClosing();
        }

        /**
         * handleWindow Closing.
         */
        private void handleWindowClosing() {
            theFrame.dispose();
            fireEvent(TethysUIEvent.WINDOWCLOSED, null);
        }
    }
}
