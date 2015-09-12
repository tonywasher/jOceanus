/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/ui/swing/GridBagUtilities.java $
 * $Revision: 642 $
 * $Author: Tony $
 * $Date: 2015-08-20 05:56:10 +0100 (Thu, 20 Aug 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.swing;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * Simple UI Utilities for Swing.
 */
public final class GuiUtils {
    /**
     * Height adjustment for field.
     */
    private static final int PADDING_HEIGHT = 4;

    /**
     * private constructor.
     */
    private GuiUtils() {
    }

    /**
     * Restrict field.
     * @param pComponent the component to restrict
     * @param pWidth field width in characters
     */
    public static void restrictField(final JComponent pComponent,
                                     final int pWidth) {
        /* Calculate the character width */
        Font myFont = pComponent.getFont();
        FontMetrics myMetrics = pComponent.getFontMetrics(myFont);
        int myCharWidth = myMetrics.stringWidth("w");
        int myCharHeight = myMetrics.getHeight() + PADDING_HEIGHT;

        /* Allocate Dimensions */
        Dimension myPrefDims = new Dimension(pWidth * myCharWidth, myCharHeight);
        Dimension myMaxDims = new Dimension(Integer.MAX_VALUE, myCharHeight);
        Dimension myMinDims = new Dimension(1, myCharHeight);

        /* Restrict the field */
        pComponent.setPreferredSize(myPrefDims);
        pComponent.setMaximumSize(myMaxDims);
        pComponent.setMinimumSize(myMinDims);
    }

    /**
     * Resize an icon to the width.
     * @param pSource the source icon
     * @param pWidth the width
     * @return the resized icon
     */
    public static Icon resizeImage(final ImageIcon pSource,
                                   final int pWidth) {
        Image myImage = pSource.getImage();
        Image myNewImage = myImage.getScaledInstance(pWidth,
                pWidth,
                Image.SCALE_SMOOTH);
        return new ImageIcon(myNewImage);
    }

    /**
     * Obtain display point for dialog.
     * @param pAnchor the anchor node
     * @param pLocation the preferred location relative to node
     * @param pSize the size of the dialog
     * @return the (adjusted) rectangle
     */
    public static Point obtainDisplayPoint(final Component pAnchor,
                                           final Point pLocation,
                                           final Dimension pSize) {
        /* First of all determine the display screen for the anchor component */
        GraphicsDevice myScreen = getScreenForComponent(pAnchor);

        /* Next obtain the fully qualified location */
        Point myLocation = getLocationForComponent(pAnchor, pLocation);

        /* determine the display rectangle */
        Rectangle myArea = new Rectangle(myLocation.x, myLocation.y,
                pSize.width, pSize.height);
        myArea = adjustDisplayLocation(myArea, myScreen);

        /* Return the location */
        return new Point(myArea.x, myArea.y);
    }

    /**
     * Obtain display point for dialog.
     * @param pAnchor the anchor node
     * @param pSide the preferred side to display on
     * @param pSize the size of the dialog
     * @return the (adjusted) rectangle
     */
    public static Point obtainDisplayPoint(final Component pAnchor,
                                           final int pSide,
                                           final Dimension pSize) {
        /* First of all determine the display screen for the anchor node */
        GraphicsDevice myScreen = getScreenForComponent(pAnchor);

        /* Next obtain the fully qualified location */
        Point myLocation = pAnchor.getLocationOnScreen();

        /* determine the display rectangle */
        Rectangle myArea = new Rectangle(myLocation.x, myLocation.y,
                pSize.width, pSize.height);
        myArea = adjustDisplayLocation(myArea, pAnchor, pSide, myScreen);

        /* Return the location */
        return new Point(myArea.x, myArea.y);
    }

    /**
     * Obtain the screen that best contains the anchor node.
     * @param pAnchor the anchor node.
     * @return the relevant screen
     */
    private static GraphicsDevice getScreenForComponent(final Component pAnchor) {
        /* Access the list of screens */
        GraphicsEnvironment myEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] myDevices = myEnv.getScreenDevices();

        /* Obtain full-qualified origin of node */
        Point myOrigin = pAnchor.getLocationOnScreen();

        /* Build fully-qualified bounds */
        Rectangle myLocalBounds = pAnchor.getBounds();
        Rectangle myBounds = new Rectangle(myOrigin.x,
                myOrigin.y,
                myLocalBounds.width,
                myLocalBounds.height);

        /* Set values */
        double myBest = 0;
        GraphicsDevice myBestDevice = null;

        /* Look for a device that contains the point */
        for (final GraphicsDevice myDevice : myDevices) {
            /* Only deal with screens */
            if (myDevice.getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                /* Access configuration */
                GraphicsConfiguration myConfig = myDevice.getDefaultConfiguration();
                Rectangle myDevBounds = myConfig.getBounds();

                /* Calculate intersection and record best */
                double myIntersection = getIntersection(myBounds, myDevBounds);
                if (myIntersection > myBest) {
                    myBest = myIntersection;
                    myBestDevice = myDevice;
                }
            }
        }

        /* If none found then default to primary */
        return myBestDevice == null
                                    ? myEnv.getDefaultScreenDevice()
                                    : myBestDevice;
    }

    /**
     * Calculate the intersection of bounds and screen.
     * @param pBounds the bounds.
     * @param pScreen the screen
     * @return the intersection
     */
    private static double getIntersection(final Rectangle pBounds,
                                          final Rectangle pScreen) {

        /* Calculate intersection coordinates */
        double myMinX = Math.max(pBounds.getMinX(), pScreen.getMinX());
        double myMaxX = Math.min(pBounds.getMaxX(), pScreen.getMaxX());
        double myMinY = Math.max(pBounds.getMinY(), pScreen.getMinY());
        double myMaxY = Math.min(pBounds.getMaxY(), pScreen.getMaxY());

        /* Calculate intersection lengths */
        double myX = Math.max(myMaxX - myMinX, 0);
        double myY = Math.max(myMaxY - myMinY, 0);

        /* Calculate intersection */
        return myX * myY;
    }

    /**
     * Obtain the fully-qualified node location.
     * @param pAnchor the node.
     * @param pLocation the location relative to the n
     * @return the origin
     */
    private static Point getLocationForComponent(final Component pAnchor,
                                                 final Point pLocation) {
        /* Access node origin */
        Point myOrigin = pAnchor.getLocationOnScreen();

        /* Calculate fully-qualified location */
        return new Point(myOrigin.x + pLocation.x,
                myOrigin.y + pLocation.y);
    }

    /**
     * Adjust display location to fit on screen.
     * @param pSource the proposed location
     * @param pScreen the screen
     * @return the (adjusted) location
     */
    private static Rectangle adjustDisplayLocation(final Rectangle pSource,
                                                   final GraphicsDevice pScreen) {
        /* Access Screen bounds */
        Rectangle myScreenBounds = pScreen.getDefaultConfiguration().getBounds();
        double myAdjustX = 0;
        double myAdjustY = 0;

        /* Adjust for too far right */
        if (pSource.getMaxX() > myScreenBounds.getMaxX()) {
            myAdjustX = myScreenBounds.getMaxX() - pSource.getMaxX();
        }

        /* Adjust for too far down */
        if (pSource.getMaxY() > myScreenBounds.getMaxY()) {
            myAdjustY = myScreenBounds.getMaxY() - pSource.getMaxY();
        }

        /* Adjust for too far left */
        if (pSource.getMinX() + myAdjustX < myScreenBounds.getMinX()) {
            myAdjustX = myScreenBounds.getMinX() - pSource.getMinX();
        }

        /* Adjust for too far down */
        if (pSource.getMinY() + myAdjustY < myScreenBounds.getMinY()) {
            myAdjustY = myScreenBounds.getMinY() - pSource.getMinY();
        }

        /* Calculate new rectangle */
        return (Double.doubleToRawLongBits(myAdjustX) != 0)
               || (Double.doubleToRawLongBits(myAdjustY) != 0)
                                                               ? new Rectangle((int) (pSource.getX() + myAdjustX),
                                                                       (int) (pSource.getY() + myAdjustY),
                                                                       pSource.width,
                                                                       pSource.height)
                                                               : pSource;
    }

    /**
     * Adjust display location to fit at side of node.
     * @param pSource the proposed location
     * @param pAnchor the anchor node
     * @param pSide the preferred side to display on
     * @param pScreen the screen
     * @return the (adjusted) location
     */
    private static Rectangle adjustDisplayLocation(final Rectangle pSource,
                                                   final Component pAnchor,
                                                   final int pSide,
                                                   final GraphicsDevice pScreen) {
        /* Access Screen bounds */
        Rectangle myScreenBounds = pScreen.getDefaultConfiguration().getBounds();
        Rectangle myBounds = pAnchor.getBounds();
        double myAdjustX = 0;
        double myAdjustY = 0;

        /* Determine initial adjustment */
        switch (pSide) {
            case SwingConstants.RIGHT:
                myAdjustX = myBounds.getWidth();
                if (pSource.getMaxX() + myAdjustX > myScreenBounds.getMaxX()) {
                    myAdjustX = -pSource.getWidth();
                }
                break;
            case SwingConstants.LEFT:
                myAdjustX = -pSource.getWidth();
                if (pSource.getMinX() + myAdjustX < myScreenBounds.getMinX()) {
                    myAdjustX = myBounds.getWidth();
                }
                break;
            case SwingConstants.BOTTOM:
                myAdjustY = myBounds.getHeight();
                if (pSource.getMaxY() + myAdjustY > myScreenBounds.getMaxY()) {
                    myAdjustY = -pSource.getHeight();
                }
                break;
            case SwingConstants.TOP:
                myAdjustY = -pSource.getHeight();
                if (pSource.getMinY() + myAdjustY < myScreenBounds.getMinY()) {
                    myAdjustY = myBounds.getHeight();
                }
                break;
            default:
                break;
        }

        /* Calculate new rectangle */
        Rectangle myArea = (Double.doubleToRawLongBits(myAdjustX) != 0)
                           || (Double.doubleToRawLongBits(myAdjustY) != 0)
                                                                           ? new Rectangle((int) (pSource.getMinX() + myAdjustX),
                                                                                   (int) (pSource.getMinY() + myAdjustY),
                                                                                   pSource.width,
                                                                                   pSource.height)
                                                                           : pSource;
        return adjustDisplayLocation(myArea, pScreen);
    }
}
