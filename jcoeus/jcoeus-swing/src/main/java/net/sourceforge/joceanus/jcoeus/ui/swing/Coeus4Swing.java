/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.ui.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jcoeus.ui.panels.CoeusMainPanel;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysLogConfig;

/**
 * Coeus Swing StartUp.
 */
public class Coeus4Swing {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Coeus4Swing.class);

    /**
     * Private constructor.
     */
    private Coeus4Swing() {
    }

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        try {
            /* Configure log4j */
            TethysLogConfig.configureLog4j();

            /* Create the Toolkit */
            MetisSwingToolkit myToolkit = new MetisSwingToolkit(null, false);

            /* Create the frame and declare it */
            JFrame myFrame = new JFrame(CoeusMainPanel.class.getSimpleName());
            myToolkit.getGuiFactory().setFrame(myFrame);

            /* Create the Coeus program */
            new CoeusSwingMainPanel(myFrame, myToolkit);

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);

        } catch (OceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
}
