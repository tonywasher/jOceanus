/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.swing.launch;

import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUILaunchProgram;

/**
 * Swing StartUp.
 */
public final class TethysUISwingLaunch {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysUISwingLaunch.class);

    /**
     * Private constructor.
     */
    private TethysUISwingLaunch() {
    }

    /**
     * Create and show the GUI.
     * @param pState the program state
     */
    private static void createAndShowGUI(final TethysUISwingLaunchState pState) {
        try {
            /* Create the main panel */
            pState.createMain();

        } catch (OceanusException e) {
            LOGGER.error("createMain didn't complete successfully", e);
        }
    }

    /**
     * launch program.
     * @param pProgram the arguments
     */
    public static void launch(final TethysUILaunchProgram pProgram) {
        try {
            /* Create a timer */
            final TethysUISwingLaunchState myState = new TethysUISwingLaunchState(pProgram);

            /* Create the splash */
            myState.createSplash();

            /* Build the GUI */
            SwingUtilities.invokeLater(() -> createAndShowGUI(myState));

        } catch (OceanusException e) {
            LOGGER.error("main didn't complete successfully", e);
        }
    }
}
