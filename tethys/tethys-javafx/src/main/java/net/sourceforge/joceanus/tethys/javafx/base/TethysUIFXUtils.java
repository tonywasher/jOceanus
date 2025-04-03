/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.base;

import javafx.geometry.Bounds;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Window;
import net.sourceforge.joceanus.oceanus.convert.OceanusDataConverter;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;

import java.util.List;

/**
 * Simple UI Utilities for javaFX.
 */
public final class TethysUIFXUtils {
    /**
     * Base StyleSheet Class.
     */
    public static final String CSS_STYLE_BASE = "-jtethys";

    /**
     * The titled style.
     */
    private static final String STYLE_TITLED = CSS_STYLE_BASE + "-titled";

    /**
     * The title style.
     */
    private static final String STYLE_TITLE = STYLE_TITLED + "-title";

    /**
     * The border style.
     */
    private static final String STYLE_BORDER = STYLE_TITLED + "-border";

    /**
     * The content style.
     */
    private static final String STYLE_CONTENT = STYLE_TITLED + "-content";

    /**
     * RGB header.
     */
    private static final String RGB_HDR = "#";

    /**
     * private constructor.
     */
    private TethysUIFXUtils() {
    }

    /**
     * format a colour as a hexadecimal string.
     * @param pValue the long value
     * @return the string
     */
    public static String colorToHexString(final Color pValue) {
        /* Return the string */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(RGB_HDR);
        appendColorPart(myBuilder, pValue.getRed());
        appendColorPart(myBuilder, pValue.getGreen());
        appendColorPart(myBuilder, pValue.getBlue());
        return myBuilder.toString();
    }

    /**
     * format a colour part.
     * @param pBuilder the string builder
     * @param pValue the value
     */
    private static void appendColorPart(final StringBuilder pBuilder,
                                        final double pValue) {
        /* Convert to integer */
        final int myMax = OceanusDataConverter.BYTE_MASK + 1;
        int myValue = (int) (pValue * myMax);

        /* Handle boundary issue */
        if (myValue == myMax) {
            myValue--;
        }

        /* Format the high nibble */
        int myDigit = myValue >>> OceanusDataConverter.NYBBLE_SHIFT;
        char myChar = Character.forDigit(myDigit, OceanusDataConverter.HEX_RADIX);
        pBuilder.append(myChar);

        /* Access the low nibble */
        myDigit = myValue
                & OceanusDataConverter.NYBBLE_MASK;
        myChar = Character.forDigit(myDigit, OceanusDataConverter.HEX_RADIX);
        pBuilder.append(myChar);
    }

    /**
     * Create titled/padded border around pane.
     * @param pTitle the title
     * @param pPadding the padding
     * @param pNode the node
     * @return the titled pane
     */
    static Pane getBorderedPane(final String pTitle,
                                final Integer pPadding,
                                final Node pNode) {
        /* Access the Node */
        final Pane myPane;
        if (!(pNode instanceof Pane)) {
            /* Create an HBox for the content */
            final HBox myBox = new HBox();
            myBox.getChildren().add(pNode);
            myPane = myBox;

            /* Set the HBox to fill the pane */
            HBox.setHgrow(pNode, Priority.ALWAYS);
        } else {
            myPane = (Pane) pNode;
        }

        /* Return the pane if we have no title or padding */
        if (pTitle == null
                && pPadding == null) {
            return myPane;
        }

        /* Create the stack pane */
        final StackPane myPanel = new StackPane();

        /* If we have a title */
        if (pTitle != null) {
            final Label myTitle = new Label(pTitle);
            StackPane.setAlignment(myTitle, Pos.TOP_LEFT);
            StackPane.setAlignment(myPane, Pos.CENTER);
            myPanel.getChildren().add(myTitle);

            /* Set the styles */
            myPane.getStyleClass().add(STYLE_CONTENT);
            myTitle.getStyleClass().add(STYLE_TITLE);
            myPanel.getStyleClass().add(STYLE_BORDER);
        }

        /* Set padding if required */
        if (pPadding != null) {
            myPanel.setPadding(new Insets(pPadding, pPadding, pPadding, pPadding));
        }

        /* Add the node */
        myPanel.getChildren().add(myPane);

        /* Return the panel */
        return myPanel;
    }

    /**
     * Obtain display point for dialog.
     * @param pAnchor the anchor node
     * @param pLocation the preferred location relative to node
     * @param pSize the size of the dialog
     * @return the (adjusted) rectangle
     */
    public static Point2D obtainDisplayPoint(final Node pAnchor,
                                             final Point2D pLocation,
                                             final Dimension2D pSize) {
        /* First of all determine the display screen for the anchor node */
        final Screen myScreen = getScreenForNode(pAnchor);

        /* Next obtain the fully qualified location */
        final Point2D myLocation = getLocationForNode(pAnchor, pLocation);

        /* determine the display rectangle */
        Rectangle2D myArea = new Rectangle2D(myLocation.getX(), myLocation.getY(),
                pSize.getWidth(), pSize.getHeight());
        myArea = adjustDisplayLocation(myArea, myScreen);

        /* Return the location */
        return new Point2D(myArea.getMinX(), myArea.getMinY());
    }

    /**
     * Obtain display point for dialog.
     * @param pAnchor the anchor node
     * @param pSide the preferred side to display on
     * @param pSize the size of the dialog
     * @return the (adjusted) rectangle
     */
    public static Point2D obtainDisplayPoint(final Node pAnchor,
                                             final Side pSide,
                                             final Dimension2D pSize) {
        /* First of all determine the display screen for the anchor node */
        final Screen myScreen = getScreenForNode(pAnchor);

        /* Next obtain the fully qualified location */
        final Point2D myLocation = getOriginForNode(pAnchor);

        /* determine the display rectangle */
        Rectangle2D myArea = new Rectangle2D(myLocation.getX(), myLocation.getY(),
                pSize.getWidth(), pSize.getHeight());
        myArea = adjustDisplayLocation(myArea, pAnchor, pSide, myScreen);

        /* Return the location */
        return new Point2D(myArea.getMinX(), myArea.getMinY());
    }

    /**
     * Obtain the screen that best contains the anchor node.
     * @param pAnchor the anchor node.
     * @return the relevant screen
     */
    private static Screen getScreenForNode(final Node pAnchor) {
        /* Access the list of screens */
        final List<Screen> myScreens = Screen.getScreens();

        /* Obtain full-qualified origin of node */
        final Point2D myOrigin = getOriginForNode(pAnchor);

        /* Build fully-qualified bounds */
        final Bounds myLocalBounds = pAnchor.getBoundsInLocal();
        final Rectangle2D myBounds = new Rectangle2D(myOrigin.getX(),
                myOrigin.getY(),
                myLocalBounds.getWidth(),
                myLocalBounds.getHeight());

        /* Set values */
        double myBest = 0;
        Screen myBestScreen = null;

        /* Look for a screen that contains the point */
        for (final Screen myScreen : myScreens) {
            final Rectangle2D myScreenBounds = myScreen.getBounds();

            /* Calculate intersection and record best */
            final double myIntersection = getIntersection(myBounds, myScreenBounds);
            if (myIntersection > myBest) {
                myBest = myIntersection;
                myBestScreen = myScreen;
            }
        }

        /* If none found then default to primary */
        return myBestScreen == null
                ? Screen.getPrimary()
                : myBestScreen;
    }

    /**
     * Obtain the fully-qualified node origin.
     * @param pNode the node.
     * @return the origin
     */
    private static Point2D getOriginForNode(final Node pNode) {
        /* Access scene and window details */
        final Scene myScene = pNode.getScene();
        final Window myWindow = myScene == null
                ? null
                : myScene.getWindow();
        final boolean bVisible = myScene != null && myWindow != null;

        /* Determine base of scene */
        final double mySceneX = bVisible
                ? myWindow.getX() + myScene.getX()
                : 0;
        final double mySceneY = bVisible
                ? myWindow.getY() + myScene.getY()
                : 0;

        /* Determine node bounds in scene */
        final Bounds myLocalBounds = pNode.getBoundsInLocal();
        final Bounds myNodeBounds = pNode.localToScene(myLocalBounds);

        /* Build fully-qualified location */
        return new Point2D(myNodeBounds.getMinX() + mySceneX,
                myNodeBounds.getMinY() + mySceneY);
    }

    /**
     * Obtain the fully-qualified node location.
     * @param pAnchor the node.
     * @param pLocation the location relative to the n
     * @return the origin
     */
    private static Point2D getLocationForNode(final Node pAnchor,
                                              final Point2D pLocation) {
        /* Access node origin */
        final Point2D myOrigin = getOriginForNode(pAnchor);

        /* Calculate fully-qualified location */
        return new Point2D(myOrigin.getX() + pLocation.getX(),
                myOrigin.getY() + pLocation.getY());
    }

    /**
     * Calculate the intersection of bounds and screen.
     * @param pBounds the bounds.
     * @param pScreen the screen
     * @return the intersection
     */
    private static double getIntersection(final Rectangle2D pBounds,
                                          final Rectangle2D pScreen) {

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
     * Adjust display location to fit on screen.
     * @param pSource the proposed location
     * @param pScreen the screen
     * @return the (adjusted) location
     */
    private static Rectangle2D adjustDisplayLocation(final Rectangle2D pSource,
                                                     final Screen pScreen) {
        /* Access Screen bounds */
        final Rectangle2D myScreenBounds = pScreen.getBounds();
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
                ? new Rectangle2D(pSource.getMinX() + myAdjustX,
                pSource.getMinY() + myAdjustY,
                pSource.getWidth(),
                pSource.getHeight())
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
    private static Rectangle2D adjustDisplayLocation(final Rectangle2D pSource,
                                                     final Node pAnchor,
                                                     final Side pSide,
                                                     final Screen pScreen) {
        /* Access Screen bounds */
        final Rectangle2D myScreenBounds = pScreen.getBounds();
        final Bounds myBounds = pAnchor.getBoundsInLocal();
        double myAdjustX = 0;
        double myAdjustY = 0;

        /* Determine initial adjustment */
        switch (pSide) {
            case RIGHT:
                myAdjustX = myBounds.getWidth();
                if (pSource.getMaxX() + myAdjustX > myScreenBounds.getMaxX()) {
                    myAdjustX = -pSource.getWidth();
                }
                break;
            case LEFT:
                myAdjustX = -pSource.getWidth();
                if (pSource.getMinX() + myAdjustX < myScreenBounds.getMinX()) {
                    myAdjustX = myBounds.getWidth();
                }
                break;
            case BOTTOM:
                myAdjustY = myBounds.getHeight();
                if (pSource.getMaxY() + myAdjustY > myScreenBounds.getMaxY()) {
                    myAdjustY = -pSource.getHeight();
                }
                break;
            case TOP:
                myAdjustY = -pSource.getHeight();
                if (pSource.getMinY() + myAdjustY < myScreenBounds.getMinY()) {
                    myAdjustY = myBounds.getHeight();
                }
                break;
            default:
                break;
        }

        /* Calculate new rectangle */
        final Rectangle2D myArea = (Double.doubleToRawLongBits(myAdjustX) != 0)
                || (Double.doubleToRawLongBits(myAdjustY) != 0)
                ? new Rectangle2D(pSource.getMinX() + myAdjustX,
                pSource.getMinY() + myAdjustY,
                pSource.getWidth(),
                pSource.getHeight())
                : pSource;
        return adjustDisplayLocation(myArea, pScreen);
    }

    /**
     * Obtain the raw icon.
     * @param pId the icon Id
     * @return the icon
     */
    public static TethysUIFXIcon getIcon(final TethysUIIconId pId) {
        final Image myImage = new Image(pId.getInputStream());
        final ImageView myView = new ImageView(myImage);
        return new TethysUIFXIcon(myView);
    }

    /**
     * Obtain raw icons.
     * @param pIds the icon Id
     * @return the icon
     */
    public static Image[] getIcons(final TethysUIIconId[] pIds) {
        final Image[] myIcons = new Image[pIds.length];
        for (int i = 0; i < pIds.length; i++) {
            myIcons[i] = getIcon(pIds[i]).getImage();
        }
        return myIcons;
    }

    /**
     * Obtain the reSized icon.
     * @param pId the icon Id
     * @param pSize the new size for the icon
     * @return the icon
     */
    public static TethysUIFXIcon getIconAtSize(final TethysUIIconId pId,
                                               final int pSize) {
        final ImageView myNewImage = getIcon(pId).getImageView();
        myNewImage.setFitWidth(pSize);
        myNewImage.setPreserveRatio(true);
        myNewImage.setSmooth(true);
        myNewImage.setCache(true);
        return new TethysUIFXIcon(myNewImage);
    }
}
