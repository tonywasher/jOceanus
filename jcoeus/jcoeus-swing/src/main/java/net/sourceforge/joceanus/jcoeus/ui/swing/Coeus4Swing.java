/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.ui.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jcoeus.ui.CoeusApp;
import net.sourceforge.joceanus.jmetis.atlas.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.atlas.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.TethysSplash;

/**
 * Coeus Swing StartUp.
 */
public final class Coeus4Swing {
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
     * @param pInfo the program info
     */
    private static void createAndShowGUI(final MetisProgram pInfo) {
        try {
            /* Create the Toolkit */
            final MetisSwingToolkit myToolkit = new MetisSwingToolkit(pInfo, false);

            /* Create the frame and declare it */
            final JFrame myFrame = new JFrame();
            myToolkit.getGuiFactory().setFrame(myFrame);

            /* Create the Coeus program */
            new CoeusSwingMainPanel(myToolkit);

        } catch (OceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        try {
            /* Create a timer */
            final MetisProgram myInfo = new MetisProgram(CoeusApp.class);

            /* Obtain program details */
            final TethysProgram myApp = myInfo.getProgramDefinitions();

            /* Sort out splash frame */
            TethysSplash.renderSplashFrame(myApp.getName(), myApp.getVersion());

            /* Build the GUI */
            SwingUtilities.invokeLater(() -> createAndShowGUI(myInfo));

        } catch (OceanusException e) {
            LOGGER.error("main didn't complete successfully", e);
        }
    }
}
