/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.ui.swing;

import java.awt.Image;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysLogConfig;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiUtils;
import net.sourceforge.joceanus.jthemis.ui.ThemisApp;
import net.sourceforge.joceanus.jthemis.ui.ThemisIcon;

/**
 * Themis Swing entryPoint.
 */
public final class Themis4Swing {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Themis4Swing.class);

    /**
     * Private constructor.
     */
    private Themis4Swing() {
    }

    /**
     * Create and show the GUI.
     * @param pProfile the startup profile
     * @param pApp the application details
     */
    private static void createAndShowGUI(final MetisProfile pProfile,
                                         final ThemisApp pApp) {
        try {
            /* Create the Toolkit */
            MetisSwingToolkit myToolkit = new MetisSwingToolkit(pProfile, pApp, false);

            /* Create the frame and declare it */
            JFrame myFrame = new JFrame(pApp.getName());
            myToolkit.getGuiFactory().setFrame(myFrame);

            /* Create the SvnManager program */
            new ThemisSwingSvnManager(myToolkit);

            /* Add the icons to the frame */
            ThemisIcon[] myIds = pApp.getIcons();
            Image[] myIcons = TethysSwingGuiUtils.getIcons(myIds);
            myFrame.setIconImages(Arrays.asList(myIcons));

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);

            /* Record startUp completion */
            pProfile.end();

        } catch (OceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Create a timer */
        MetisProfile myProfile = new MetisProfile("StartUp");

        /* Configure log4j */
        TethysLogConfig.configureLog4j();

        /* Obtain program details */
        ThemisApp myApp = new ThemisApp();

        /* Sort out splash frame */
        TethysSwingGuiUtils.renderSplashFrame(myApp.getName(), myApp.getVersion());

        /* Start up the GUI */
        SwingUtilities.invokeLater(() -> createAndShowGUI(myProfile, myApp));
    }
}
