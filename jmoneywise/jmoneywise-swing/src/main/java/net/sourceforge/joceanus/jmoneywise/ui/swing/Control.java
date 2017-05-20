/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.swing;

import java.awt.Image;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.lethe.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jmoneywise.swing.SwingView;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseApp;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.TethysSplash;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiUtils;

/**
 * Main entry point for program.
 */
public final class Control {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Control.class);

    /**
     * Private constructor to avoid instantiation.
     */
    private Control() {
    }

    /**
     * Create and show the GUI.
     * @param pInfo the program info
     */
    private static void createAndShowGUI(final MetisProgram pInfo) {
        try {
            /* Create the view */
            SwingView myView = new SwingView(pInfo);
            MetisSwingToolkit myToolkit = myView.getToolkit();

            /* Obtain program details */
            TethysProgram myApp = pInfo.getProgramDefinitions();

            /* Create the frame and declare it */
            JFrame myFrame = new JFrame(myApp.getName());
            myToolkit.getGuiFactory().setFrame(myFrame);

            /* Create the window */
            MainTab myWindow = new MainTab(myView);
            myWindow.makeFrame();

            /* Add the icons to the frame */
            TethysIconId[] myIds = myApp.getIcons();
            Image[] myIcons = TethysSwingGuiUtils.getIcons(myIds);
            myFrame.setIconImages(Arrays.asList(myIcons));

            /* Show the frame */
            myFrame.pack();
            myFrame.setLocationRelativeTo(null);
            myFrame.setVisible(true);

            /* Record startUp completion */
            pInfo.getProfile().end();

        } catch (OceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        try {
            /* Create a timer */
            MetisProgram myInfo = new MetisProgram(MoneyWiseApp.class);

            /* Obtain program details */
            TethysProgram myApp = myInfo.getProgramDefinitions();

            /* Sort out splash frame */
            TethysSplash.renderSplashFrame(myApp.getName(), myApp.getVersion());

            /* Build the GUI */
            SwingUtilities.invokeLater(() -> createAndShowGUI(myInfo));

        } catch (OceanusException e) {
            LOGGER.error("main didn't complete successfully", e);
        }
    }
}
