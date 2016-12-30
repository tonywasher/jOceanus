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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.SplashScreen;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysLogConfig;

/**
 * Main entry point for program.
 * @author Tony Washer
 */
public final class Control {
    /**
     * The Splash Font.
     */
    private static final int SPLASH_PITCH = 16;

    /**
     * The Splash Character Width.
     */
    private static final int SPLASH_CHARWIDTH = 10;

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
     * @param pProfile the startup profile
     */
    private static void createAndShowGUI(final MetisProfile pProfile) {
        try {
            /* Configure log4j */
            TethysLogConfig.configureLog4j();

            /* Create the window */
            MainTab myWindow = new MainTab(pProfile);
            myWindow.makeFrame();

        } catch (OceanusException e) {
            LOGGER.error("createGUI didn't complete successfully", e);
        }
    }

    /**
     * Add text to the splash screen.
     */
    private static void renderSplashFrame() {
        /* Access the splash screen */
        final SplashScreen mySplash = SplashScreen.getSplashScreen();
        if (mySplash != null) {
            /* Access the graphics */
            Graphics2D myGraphics = mySplash.createGraphics();
            if (myGraphics != null) {
                /* Access the names */
                String myName = ProgramResource.PROGRAM_NAME.getValue();
                String myVersion = ProgramResource.PROGRAM_VERSION.getValue();

                /* Determine width of the box */
                int myNameLen = myName.length();
                int myVerLen = myVersion.length();

                /* Access the splash screen dimensions */
                Dimension mySize = mySplash.getSize();
                int myWidth = Math.max(myNameLen, myVerLen + 1) * SPLASH_CHARWIDTH;
                int myX = (mySize.width - myWidth) >> 1;
                int myY = mySize.height - (mySize.height >> 2);

                /* Set up for painting */
                Font myFont = new Font("Courier", Font.BOLD, SPLASH_PITCH);
                myGraphics.setComposite(AlphaComposite.Clear);
                myGraphics.setPaintMode();
                myGraphics.setFont(myFont);
                myGraphics.setColor(Color.BLUE);
                myGraphics.fillRect(myX, myY - SPLASH_PITCH + (SPLASH_PITCH >> 2), myWidth, SPLASH_PITCH << 1);
                myGraphics.setColor(Color.WHITE);

                /* Write text */
                myGraphics.drawString(myName, myX, myY);
                myGraphics.drawString(myVersion, myX + SPLASH_CHARWIDTH, myY + SPLASH_PITCH);

                /* Update the screen */
                mySplash.update();
            }
        }
    }

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        /* Create a timer */
        MetisProfile myProfile = new MetisProfile("StartUp");

        /* Sort out splash frame */
        renderSplashFrame();

        /* Build the GUI */
        SwingUtilities.invokeLater(() -> createAndShowGUI(myProfile));
    }
}
