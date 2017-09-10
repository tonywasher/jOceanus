/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.help.swing;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.help.TethysHelpEntry;
import net.sourceforge.joceanus.jtethys.help.TethysHelpModule;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Help Window.
 */
public class TestSwingHelpWindow {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSwingHelpWindow.class);

    /**
     * Constructor.
     */
    private TestSwingHelpWindow() {
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
    static void createAndShowGUI() {
        try {
            /* Create the frame */
            final JFrame myFrame = new JFrame("HelpWindow Test");

            /* Create the guiFactory */
            final TethysSwingGuiFactory myFactory = new TethysSwingGuiFactory();
            myFactory.setFrame(myFrame);

            /* Create the help window */
            final TethysSwingHelpWindow myWindow = new TethysSwingHelpWindow(myFactory);

            /* Access the panel */
            final JPanel myPanel = buildPanel(myWindow);

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
     * @param pWindow the window
     * @return the panel
     */
    private static JPanel buildPanel(final TethysSwingHelpWindow pWindow) {
        /* Create a button */
        final JButton myButton = new JButton("Help");

        /* Create a BorderPane for the fields */
        final JPanel myPane = new JPanel();
        myPane.setLayout(new BorderLayout());
        myPane.add(myButton, BorderLayout.LINE_START);

        /* Add listener for the button */
        myButton.addActionListener(e -> pWindow.showDialog());

        /* Protect against exceptions */
        try {
            pWindow.setModule(new TestHelp());
        } catch (OceanusException e) {
            LOGGER.error("failed to build HelpModule", e);
        }
        return myPane;
    }

    /**
     * Help system.
     */
    public static class TestHelp
            extends TethysHelpModule {
        /**
         * Constructor.
         * @throws OceanusException on error
         */
        public TestHelp() throws OceanusException {
            /* Initialise the underlying module */
            super(TethysHelpModule.class, "Test Help System");

            /* Create accounts tree */
            final TethysHelpEntry myAccounts = addRootEntry(defineContentsEntry("Accounts"));
            myAccounts.addChildEntry(defineHelpEntry("Deposits", "Deposits.html"));
            myAccounts.addChildEntry(defineHelpEntry("Loans", "Loans.html"));

            /* Create static tree */
            final TethysHelpEntry myStatic = addRootEntry(defineContentsEntry("StaticData"));
            myStatic.addChildEntry(defineHelpEntry("AccountTypes", "AccountTypes.html"));
            myStatic.addChildEntry(defineHelpEntry("TransactionTypes", "TransactionTypes.html"));

            /* Load pages */
            loadHelpPages();

            /* Load the CSS */
            loadCSS("TethysHelp.css");
        }
    }
}
