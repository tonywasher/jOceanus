/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
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
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;

/**
 * Simple UI Utilities for Swing.
 */
public final class TethysSwingGuiUtils {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingGuiUtils.class);

    /**
     * Height adjustment for field.
     */
    private static final int PADDING_HEIGHT = 4;

    /**
     * private constructor.
     */
    private TethysSwingGuiUtils() {
    }

    /**
     * Restrict field.
     * @param pComponent the component to restrict
     * @param pWidth field width in characters
     */
    public static void restrictField(final JComponent pComponent,
                                     final int pWidth) {
        /* Calculate the character width */
        final Font myFont = pComponent.getFont();
        final FontMetrics myMetrics = pComponent.getFontMetrics(myFont);
        final int myCharWidth = myMetrics.stringWidth("w");
        final int myCharHeight = myMetrics.getHeight() + PADDING_HEIGHT;

        /* Allocate Dimensions */
        final Dimension myPrefDims = new Dimension(pWidth * myCharWidth, myCharHeight);
        final Dimension myMaxDims = new Dimension(Integer.MAX_VALUE, myCharHeight);
        final Dimension myMinDims = new Dimension(1, myCharHeight);

        /* Restrict the field */
        pComponent.setPreferredSize(myPrefDims);
        pComponent.setMaximumSize(myMaxDims);
        pComponent.setMinimumSize(myMinDims);
    }

    /**
     * create wrapper pane.
     * @param pTitle the title
     * @param pPadding the padding
     * @param pNode the node
     * @return the new pane
     */
    public static JComponent addPanelBorder(final String pTitle,
                                            final Integer pPadding,
                                            final JComponent pNode) {
        if (pPadding == null
            && pTitle == null) {
            return pNode;
        } else {
            final JComponent myNode = new JPanel(new BorderLayout());
            myNode.add(pNode, BorderLayout.CENTER);
            setPanelBorder(pTitle, pPadding, myNode);
            return myNode;
        }
    }

    /**
     * Apply titled and padded borders around panel.
     * @param pTitle the title
     * @param pPadding the padding
     * @param pNode the node
     */
    protected static void setPanelBorder(final String pTitle,
                                         final Integer pPadding,
                                         final JComponent pNode) {
        /* Access contents */
        final boolean hasTitle = pTitle != null;
        final boolean hasPadding = pPadding != null;

        /* Create borders */
        final Border myPaddedBorder = hasPadding
                                                 ? BorderFactory.createEmptyBorder(pPadding, pPadding, pPadding, pPadding)
                                                 : null;
        final Border myTitleBorder = hasTitle
                                              ? BorderFactory.createTitledBorder(pTitle)
                                              : null;

        /* Create compound border */
        final Border myBorder = hasPadding
                                           ? hasTitle
                                                      ? BorderFactory.createCompoundBorder(myPaddedBorder, myTitleBorder)
                                                      : myPaddedBorder
                                           : hasTitle
                                                      ? myTitleBorder
                                                      : BorderFactory.createEmptyBorder();

        /* Set the border */
        pNode.setBorder(myBorder);
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
        final GraphicsDevice myScreen = getScreenForComponent(pAnchor);

        /* Next obtain the fully qualified location */
        final Point myLocation = getLocationForComponent(pAnchor, pLocation);

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
        final GraphicsDevice myScreen = getScreenForComponent(pAnchor);

        /* Next obtain the fully qualified location */
        final Point myLocation = pAnchor.getLocationOnScreen();

        /* determine the display rectangle */
        Rectangle myArea = new Rectangle(myLocation.x, myLocation.y,
                pSize.width, pSize.height);
        myArea = adjustDisplayLocation(myArea, pAnchor.getBounds(), pSide, myScreen);

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
    public static Point obtainDisplayPoint(final Rectangle pAnchor,
                                           final int pSide,
                                           final Dimension pSize) {
        /* First of all determine the display screen for the anchor node */
        final GraphicsDevice myScreen = getScreenForRectangle(pAnchor);

        /* determine the display rectangle */
        Rectangle myArea = new Rectangle(pAnchor.x, pAnchor.y,
                pSize.width, pSize.height);
        myArea = adjustDisplayLocation(myArea, pAnchor.getBounds(), pSide, myScreen);

        /* Return the location */
        return new Point(myArea.x, myArea.y);
    }

    /**
     * Obtain the screen that best contains the anchor node.
     * @param pAnchor the anchor node.
     * @return the relevant screen
     */
    private static GraphicsDevice getScreenForComponent(final Component pAnchor) {
        /* Obtain full-qualified origin of node */
        final Point myOrigin = pAnchor.getLocationOnScreen();

        /* Build fully-qualified bounds */
        final Rectangle myLocalBounds = pAnchor.getBounds();
        final Rectangle myBounds = new Rectangle(myOrigin.x,
                myOrigin.y,
                myLocalBounds.width,
                myLocalBounds.height);

        /* Look for rectangle */
        return getScreenForRectangle(myBounds);
    }

    /**
     * Obtain the screen that best contains the rectangle.
     * @param pAnchor the anchor node.
     * @return the relevant screen
     */
    private static GraphicsDevice getScreenForRectangle(final Rectangle pAnchor) {
        /* Access the list of screens */
        final GraphicsEnvironment myEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice[] myDevices = myEnv.getScreenDevices();

        /* Set values */
        double myBest = 0;
        GraphicsDevice myBestDevice = null;

        /* Look for a device that contains the point */
        for (final GraphicsDevice myDevice : myDevices) {
            /* Only deal with screens */
            if (myDevice.getType() == GraphicsDevice.TYPE_RASTER_SCREEN) {
                /* Access configuration */
                final GraphicsConfiguration myConfig = myDevice.getDefaultConfiguration();
                final Rectangle myDevBounds = myConfig.getBounds();

                /* Calculate intersection and record best */
                final double myIntersection = getIntersection(pAnchor, myDevBounds);
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
        final double myMinX = Math.max(pBounds.getMinX(), pScreen.getMinX());
        final double myMaxX = Math.min(pBounds.getMaxX(), pScreen.getMaxX());
        final double myMinY = Math.max(pBounds.getMinY(), pScreen.getMinY());
        final double myMaxY = Math.min(pBounds.getMaxY(), pScreen.getMaxY());

        /* Calculate intersection lengths */
        final double myX = Math.max(myMaxX - myMinX, 0);
        final double myY = Math.max(myMaxY - myMinY, 0);

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
        final Point myOrigin = pAnchor.getLocationOnScreen();

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
        final Rectangle myScreenBounds = pScreen.getDefaultConfiguration().getBounds();
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
                                                   final Rectangle pAnchor,
                                                   final int pSide,
                                                   final GraphicsDevice pScreen) {
        /* Access Screen bounds */
        final Rectangle myScreenBounds = pScreen.getDefaultConfiguration().getBounds();
        double myAdjustX = 0;
        double myAdjustY = 0;

        /* Determine initial adjustment */
        switch (pSide) {
            case SwingConstants.RIGHT:
                myAdjustX = pAnchor.getWidth();
                if (pSource.getMaxX() + myAdjustX > myScreenBounds.getMaxX()) {
                    myAdjustX = -pSource.getWidth();
                }
                break;
            case SwingConstants.LEFT:
                myAdjustX = -pSource.getWidth();
                if (pSource.getMinX() + myAdjustX < myScreenBounds.getMinX()) {
                    myAdjustX = pAnchor.getWidth();
                }
                break;
            case SwingConstants.BOTTOM:
                myAdjustY = pAnchor.getHeight();
                if (pSource.getMaxY() + myAdjustY > myScreenBounds.getMaxY()) {
                    myAdjustY = -pSource.getHeight();
                }
                break;
            case SwingConstants.TOP:
                myAdjustY = -pSource.getHeight();
                if (pSource.getMinY() + myAdjustY < myScreenBounds.getMinY()) {
                    myAdjustY = pAnchor.getHeight();
                }
                break;
            default:
                break;
        }

        /* Calculate new rectangle */
        final Rectangle myArea = (Double.doubleToRawLongBits(myAdjustX) != 0)
                                 || (Double.doubleToRawLongBits(myAdjustY) != 0)
                                                                                 ? new Rectangle((int) (pSource.getMinX() + myAdjustX),
                                                                                         (int) (pSource.getMinY() + myAdjustY),
                                                                                         pSource.width,
                                                                                         pSource.height)
                                                                                 : pSource;
        return adjustDisplayLocation(myArea, pScreen);
    }

    /**
     * format a colour as a hexadecimal string.
     * @param pValue the long value
     * @return the string
     */
    public static String colorToHexString(final Color pValue) {
        /* Access the RGB value */
        int myValue = pValue.getRGB();
        myValue &= TethysDataConverter.COLOR_MASK;

        /* Allocate the string builder */
        final StringBuilder myBuilder = new StringBuilder();

        /* While we have digits to format */
        while (myValue > 0) {
            /* Access the digit and move to next one */
            final int myDigit = myValue & TethysDataConverter.NYBBLE_MASK;
            final char myChar = Character.forDigit(myDigit, TethysDataConverter.HEX_RADIX);
            myBuilder.insert(0, myChar);
            myValue >>>= TethysDataConverter.NYBBLE_SHIFT;
        }

        /* Add zeros to front if less than 6 digits */
        while (myBuilder.length() < TethysDataConverter.RGB_LENGTH) {
            myBuilder.insert(0, '0');
        }

        /* Insert a # sign */
        myBuilder.insert(0, '#');

        /* Return the string */
        return myBuilder.toString();
    }

    /**
     * Obtain the raw icon.
     * @param pId the icon Id
     * @return the icon
     */
    public static TethysSwingIcon getIcon(final TethysIconId pId) {
        try {
            final ImageIcon myIcon = new ImageIcon(pId.loadResourceToBytes());
            return new TethysSwingIcon(myIcon);
        } catch (IOException e) {
            LOGGER.error("Failed to load Icon " + pId.getSourceName(), e);
            return null;
        }
    }

    /**
     * Obtain raw icons.
     * @param pIds the icon Id
     * @return the icon
     */
    public static Image[] getIcons(final TethysIconId[] pIds) {
        final Image[] myIcons = new Image[pIds.length];
        for (int i = 0; i < pIds.length; i++) {
            final TethysSwingIcon myIcon = getIcon(pIds[i]);
            myIcons[i] = myIcon == null
                         ? null
                         : myIcon.getImage();
        }
        return myIcons;
    }

    /**
     * Obtain the reSized icon.
     * @param pId the icon Id
     * @param pWidth the new width for the icon
     * @return the icon
     */
    public static TethysSwingIcon getIconAtSize(final TethysIconId pId,
                                                final int pWidth) {
        final TethysSwingIcon mySource = getIcon(pId);
        if (mySource == null) {
            return null;
        }
        final Image myImage = mySource.getImage();
        final Image myNewImage = myImage.getScaledInstance(pWidth,
                pWidth,
                Image.SCALE_SMOOTH);
        final ImageIcon myIcon = new ImageIcon(myNewImage);
        return new TethysSwingIcon(myIcon);
    }
}
