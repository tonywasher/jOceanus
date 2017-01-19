package net.sourceforge.joceanus.jtethys.ui;

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
public final class TethysSplash {
    /**
     * Splash Pitch.
     */
    private static final int SPLASH_PITCH = 16;

    /**
     * private constructor.
     */
    private TethysSplash() {
    }

    /**
     * Add text to the splash screen.
     * @param pName the program name
     * @param pVersion the program version
     */
    public static void renderSplashFrame(final String pName,
                                         final String pVersion) {
        /* Access the splash screen */
        final SplashScreen mySplash = SplashScreen.getSplashScreen();
        if (mySplash != null) {
            /* Access the graphics */
            Graphics2D myGraphics = mySplash.createGraphics();
            if (myGraphics != null) {
                /* Access the splash screen dimensions */
                Dimension mySize = mySplash.getSize();

                /* Set up for painting */
                Font myFont = new Font("Courier", Font.BOLD, SPLASH_PITCH);
                myGraphics.setComposite(AlphaComposite.Clear);
                myGraphics.setPaintMode();
                myGraphics.setFont(myFont);
                FontMetrics myMetrics = myGraphics.getFontMetrics();

                /* Determine X and Y for name */
                Rectangle2D myBounds = myMetrics.getStringBounds(pName, myGraphics);
                double myX = (mySize.width - myBounds.getWidth()) / 2d;
                double myY = mySize.height - (mySize.height >> 2);
                double myHeight = myBounds.getHeight();
                int myDescent = myMetrics.getDescent();

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
