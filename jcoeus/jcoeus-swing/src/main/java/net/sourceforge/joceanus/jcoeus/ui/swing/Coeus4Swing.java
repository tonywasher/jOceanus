/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui.swing;

import net.sourceforge.joceanus.jcoeus.ui.launch.CoeusApp;
import net.sourceforge.joceanus.jmetis.launch.swing.MetisSwingState;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

import javax.swing.SwingUtilities;

/**
 * Coeus Swing StartUp.
 */
public final class Coeus4Swing {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(Coeus4Swing.class);

    /**
     * Private constructor.
     */
    private Coeus4Swing() {
    }

    /**
     * Create and show the GUI.
     * @param pState the program state
     */
    private static void createAndShowGUI(final MetisSwingState pState) {
        try {
            /* Create the main panel */
            pState.createMain();

        } catch (OceanusException e) {
            LOGGER.error("createMain didn't complete successfully", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        try {
            /* Create a timer */
            final MetisSwingState myState = new MetisSwingState(CoeusApp.class);

            /* Create the splash */
            myState.createSplash();

            /* Build the GUI */
            SwingUtilities.invokeLater(() -> createAndShowGUI(myState));

        } catch (OceanusException e) {
            LOGGER.error("main didn't complete successfully", e);
        }
    }
}
