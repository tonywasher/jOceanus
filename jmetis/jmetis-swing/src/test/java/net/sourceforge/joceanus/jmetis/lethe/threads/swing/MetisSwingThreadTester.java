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
package net.sourceforge.joceanus.jmetis.lethe.threads.swing;

import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.atlas.viewer.swing.MetisSwingViewerWindow;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisTestThread;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Thread Manager Tester.
 */
public class MetisSwingThreadTester {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisSwingThreadTester.class);

    /**
     * Toolkit.
     */
    private final MetisSwingToolkit theToolkit;

    /**
     * GUI factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * ThreadManager.
     */
    private final MetisSwingThreadManager theThreadMgr;

    /**
     * Launch button.
     */
    private final TethysSwingButton theLaunchButton;

    /**
     * Debug button.
     */
    private final TethysSwingButton theDebugButton;

    /**
     * the status panel.
     */
    private final MetisThreadStatusManager<JComponent> theStatusPanel;

    /**
     * the main panel.
     */
    private final TethysSwingBorderPaneManager theMainPanel;

    /**
     * Frame.
     */
    private final JFrame theFrame;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisSwingThreadTester() throws OceanusException {
        /* Create toolkit */
        theToolkit = new MetisSwingToolkit();

        /* Access components */
        theGuiFactory = theToolkit.getGuiFactory();
        theThreadMgr = theToolkit.getThreadManager();

        /* Create buttons */
        theLaunchButton = theGuiFactory.newButton();
        theLaunchButton.setTextOnly();
        theLaunchButton.setText("Launch");
        theDebugButton = theGuiFactory.newButton();
        theDebugButton.setTextOnly();
        theDebugButton.setText("Debug");

        /* Create the Panels */
        theFrame = new JFrame("MetisSwingThread Demo");
        theStatusPanel = theThreadMgr.getStatusManager();
        theMainPanel = theGuiFactory.newBorderPane();
        theGuiFactory.setFrame(theFrame);
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the UI */
            MetisSwingThreadTester myThread = new MetisSwingThreadTester();
            JFrame myFrame = myThread.theFrame;

            /* Build the panel */
            JComponent myPanel = myThread.buildPanel();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (OceanusException
                | HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Build the panel.
     * @return the panel
     */
    private JComponent buildPanel() {
        TethysSwingBoxPaneManager myButtons = theGuiFactory.newHBoxPane();
        myButtons.addNode(theLaunchButton);
        myButtons.addSpacer();
        myButtons.addNode(theDebugButton);

        /* Create boxPane for the window */
        TethysSwingBoxPaneManager myBox = theGuiFactory.newVBoxPane();
        myBox.addSpacer();
        myBox.addNode(myButtons);
        myBox.addSpacer();

        /* Create borderPane for the window */
        theMainPanel.setCentre(myBox);
        theMainPanel.setBorderPadding(5);

        /* Set the status panel */
        theMainPanel.setNorth(theStatusPanel);
        theGuiFactory.setNodeVisible(theStatusPanel, false);

        /* Create thread status change handler */
        theThreadMgr.getEventRegistrar().addEventListener(e -> handleThreadChange());

        /* handle launch thread */
        theLaunchButton.getEventRegistrar().addEventListener(e -> launchThread());

        /* handle debug */
        theDebugButton.getEventRegistrar().addEventListener(e -> showDebug());

        /* Return the node */
        return theMainPanel.getNode();
    }

    /**
     * handle ThreadStatus change.
     */
    private void handleThreadChange() {
        if (theThreadMgr.hasWorker()) {
            theLaunchButton.setEnabled(false);
            theGuiFactory.setNodeVisible(theStatusPanel, true);
        } else {
            theLaunchButton.setEnabled(true);
            theGuiFactory.setNodeVisible(theStatusPanel, false);
        }
        theFrame.pack();
    }

    /**
     * launch thread.
     */
    private void launchThread() {
        theThreadMgr.startThread(new MetisTestThread<>());
    }

    /**
     * show debug.
     */
    private void showDebug() {
        try {
            MetisSwingViewerWindow myWindow = theToolkit.newViewerWindow();
            theDebugButton.setEnabled(false);
            myWindow.getEventRegistrar().addEventListener(TethysUIEvent.WINDOWCLOSED, e -> theDebugButton.setEnabled(true));
            myWindow.showDialog();
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }
}
