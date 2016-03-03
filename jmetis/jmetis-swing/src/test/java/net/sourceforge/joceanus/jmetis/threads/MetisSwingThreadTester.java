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
package net.sourceforge.joceanus.jmetis.threads;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldConfig;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingThreadManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisSwingViewerManager;

/**
 * Thread Manager Tester.
 */
public class MetisSwingThreadTester {
    /**
     * The default height.
     */
    private static final int DEFAULT_HEIGHT = 620;

    /**
     * The default width.
     */
    private static final int DEFAULT_WIDTH = 400;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MetisSwingThreadTester.class);

    /**
     * Frame.
     */
    private final JFrame theFrame;

    /**
     * ViewerManager.
     */
    private final MetisSwingViewerManager theViewerMgr;

    /**
     * ThreadManager.
     */
    private final MetisSwingThreadManager theThreadMgr;

    /**
     * Launch button.
     */
    private final JButton theLaunchButton;

    /**
     * Debug button.
     */
    private final JButton theDebugButton;

    /**
     * the main panel.
     */
    private final JComponent theStatusPanel;

    /**
     * Constructor.
     */
    public MetisSwingThreadTester() {
        /* Create button */
        theLaunchButton = new JButton("Launch");
        theDebugButton = new JButton("Debug");

        /* Create the Managers */
        theFrame = new JFrame("MetisSwingThread Demo");
        MetisFieldManager myFieldMgr = new MetisFieldManager(new MetisFieldConfig());
        theViewerMgr = new MetisSwingViewerManager(myFieldMgr);
        theThreadMgr = new MetisSwingThreadManager(theFrame, theViewerMgr);
        theStatusPanel = theThreadMgr.getNode();
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
            JPanel myPanel = myThread.buildPanel();

            /* Attach the panel to the frame */
            myPanel.setOpaque(true);
            myFrame.setContentPane(myPanel);
            myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);
        } catch (HeadlessException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Build the panel.
     * @return the panel
     */
    private JPanel buildPanel() {
        /* Create borderPane for the window */
        JPanel myButtons = new JPanel();
        myButtons.setLayout(new BoxLayout(myButtons, BoxLayout.X_AXIS));
        myButtons.add(theLaunchButton);
        myButtons.add(Box.createHorizontalGlue());
        myButtons.add(theDebugButton);

        /* Create borderPane for the window */
        JPanel myMain = new JPanel();
        myMain.setLayout(new BorderLayout());
        myMain.add(myButtons, BorderLayout.CENTER);
        myMain.add(theStatusPanel, BorderLayout.PAGE_START);
        myMain.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        theStatusPanel.setVisible(false);

        /* Create thread status change handler */
        theThreadMgr.getEventRegistrar().addEventListener(e -> handleThreadChange());

        /* handle launch thread */
        theLaunchButton.addActionListener(e -> launchThread());

        /* Return the panel */
        return myMain;
    }

    /**
     * handle ThreadStatus change.
     */
    private void handleThreadChange() {
        if (theThreadMgr.hasWorker()) {
            theLaunchButton.setEnabled(false);
            theStatusPanel.setVisible(true);
        } else {
            theLaunchButton.setEnabled(true);
            theStatusPanel.setVisible(false);
        }
    }

    /**
     * launch thread.
     */
    private void launchThread() {
        theThreadMgr.startThread(new MetisTestThread());
    }
}
