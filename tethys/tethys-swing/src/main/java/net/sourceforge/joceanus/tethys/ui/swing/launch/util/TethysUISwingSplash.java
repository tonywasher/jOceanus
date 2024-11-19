/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.ui.swing.launch.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.geom.Rectangle2D;

/**
 * SplashScreen Utilities.
 */
public final class TethysUISwingSplash {
    /**
     * Splash Pitch.
     */
    private static final int SPLASH_PITCH = 16;

    /**
     * private constructor.
     */
    private TethysUISwingSplash() {
    }

    /**
     * Add text to the splash screen.
     * @param pName the program name
     * @param pVersion the program version
     */
    static void renderSplashFrame(final String pName,
                                  final String pVersion) {
        /* Access the splash screen */
        final SplashScreen mySplash = SplashScreen.getSplashScreen();
        if (mySplash != null) {
            /* Access the graphics */
            final Graphics2D myGraphics = mySplash.createGraphics();
            if (myGraphics != null) {
                /* Access the splash screen dimensions */
                final Dimension mySize = mySplash.getSize();

                /* Set up for painting */
                final Font myFont = new Font("Courier", Font.BOLD, SPLASH_PITCH);
                myGraphics.setComposite(AlphaComposite.Clear);
                myGraphics.setPaintMode();
                myGraphics.setFont(myFont);
                final FontMetrics myMetrics = myGraphics.getFontMetrics();

                /* Determine X and Y for name */
                Rectangle2D myBounds = myMetrics.getStringBounds(pName, myGraphics);
                double myX = (mySize.width - myBounds.getWidth()) / 2d;
                final double myY = mySize.height - (double) (mySize.height >> 2);
                double myHeight = myBounds.getHeight();
                final int myDescent = myMetrics.getDescent();

                /* Write the name */
                myGraphics.setColor(Color.BLUE);
                myGraphics.fillRect((int) myX, (int) (myY - myHeight), (int) myBounds.getWidth(), (int) myHeight);
                myGraphics.setColor(Color.WHITE);
                myGraphics.drawString(pName, (int) myX, (int) (myY - myDescent));

                /* Determine X and Y for version */
                myBounds = myMetrics.getStringBounds(pVersion, myGraphics);
                myX = (mySize.width - myBounds.getWidth()) / 2d;
                myHeight = myBounds.getHeight();

                /* Write the name */
                myGraphics.setColor(Color.BLUE);
                myGraphics.fillRect((int) myX, (int) myY, (int) myBounds.getWidth(), (int) myHeight);
                myGraphics.setColor(Color.WHITE);
                myGraphics.drawString(pVersion, (int) myX, (int) (myY + myHeight - myDescent));

                /* Update the screen */
                mySplash.update();
            }
        }
    }
}

