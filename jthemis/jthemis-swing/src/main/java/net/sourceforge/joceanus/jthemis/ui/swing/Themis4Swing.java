/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jthemis.ui.swing;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jmetis.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingSplash;
import net.sourceforge.joceanus.jthemis.ui.ThemisApp;

/**
 * Themis Swing entryPoint.
 */
public final class Themis4Swing {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(Themis4Swing.class);

    /**
     * Private constructor.
     */
    private Themis4Swing() {
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

            /* Create the SvnManager program */
            new ThemisSwingSvnManager(myToolkit);

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
            final MetisProgram myInfo = new MetisProgram(ThemisApp.class);

            /* Obtain program details */
            final TethysProgram myApp = myInfo.getProgramDefinitions();

            /* Sort out splash frame */
            TethysSwingSplash.renderSplashFrame(myApp.getName(), myApp.getVersion());

            /* Start up the GUI */
            SwingUtilities.invokeLater(() -> createAndShowGUI(myInfo));

        } catch (OceanusException e) {
            LOGGER.error("main didn't complete successfully", e);
        }
    }
}
