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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.help;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.swing.TethysSwingHelpManager;

/**
 * Help Window.
 */
public class TestSwingHelpWindow
        extends JApplet {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1073083122991090117L;

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSwingHelpWindow.class);

    /**
     * The HelpButton.
     */
    private final JButton theButton;

    /**
     * The HelpManager.
     */
    private final TethysSwingHelpManager theHelpWindow;

    /**
     * Constructor.
     */
    public TestSwingHelpWindow() {
        theButton = new JButton("Help");
        theHelpWindow = new TethysSwingHelpManager();
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

    @Override
    public void init() {
        // Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    /* Create the Test Panel */
                    JPanel myPanel = buildPanel();
                    setContentPane(myPanel);
                }
            });
        } catch (InvocationTargetException e) {
            LOGGER.error("Failed to invoke thread", e);
        } catch (InterruptedException e) {
            LOGGER.error("Thread was interrupted", e);
        }
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Create the frame */
            JFrame myFrame = new JFrame("HelpWindow Test");

            /* Access the panel */
            TestSwingHelpWindow myWindow = new TestSwingHelpWindow();
            JPanel myPanel = myWindow.buildPanel();

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
        /* Create a BorderPane for the fields */
        JPanel myPane = new JPanel();
        myPane.setLayout(new BorderLayout());
        myPane.add(theButton, BorderLayout.LINE_START);

        /* Add listener for the button */
        theButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                theHelpWindow.showDialog();
            }
        });

        /* Protect against exceptions */
        try {
            theHelpWindow.setModule(new TestHelp());
        } catch (OceanusException e) {
            LOGGER.error("failed to build HelpModule", e);
        }
        return myPane;
    }

    /**
     * Help system.
     */
    public class TestHelp
            extends TethysHelpModule {
        /**
         * Constructor.
         * @throws TethysHelpException on error
         */
        public TestHelp() throws TethysHelpException {
            /* Initialise the underlying module */
            super(TestSwingHelpWindow.class, "Test Help System");

            /* Create accounts tree */
            TethysHelpEntry myAccounts = addRootEntry(defineContentsEntry("Accounts"));
            myAccounts.addChildEntry(defineHelpEntry("Deposits", "Deposits.html"));
            myAccounts.addChildEntry(defineHelpEntry("Loans", "Loans.html"));

            /* Create static tree */
            TethysHelpEntry myStatic = addRootEntry(defineContentsEntry("StaticData"));
            myStatic.addChildEntry(defineHelpEntry("AccountTypes", "AccountTypes.html"));
            myStatic.addChildEntry(defineHelpEntry("TransactionTypes", "TransactionTypes.html"));

            /* Load pages */
            loadHelpPages();
        }
    }
}
