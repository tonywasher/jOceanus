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
package net.sourceforge.joceanus.jtethys.ui.javafx.base;

import java.util.EnumMap;
import java.util.Map;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;

/**
 * Arrow Icons.
 */
public enum TethysUIFXArrowIcon {
    /**
     * Up Arrow.
     */
    UP(new Point2D(1, 9), new Point2D(5, 1), new Point2D(9, 9)),

    /**
     * Down Arrow.
     */
    DOWN(new Point2D(1, 1), new Point2D(5, 9), new Point2D(9, 1)),

    /**
     * Left Arrow.
     */
    LEFT(new Point2D(1, 5), new Point2D(9, 1), new Point2D(9, 9)),

    /**
     * Right Arrow.
     */
    RIGHT(new Point2D(1, 1), new Point2D(1, 9), new Point2D(9, 5)),

    /**
     * DoubleUp Arrow.
     */
    DOUBLEUP(new Point2D(1, 5), new Point2D(5, 1), new Point2D(9, 5), new Point2D(5, 5), new Point2D(9, 9), new Point2D(1, 9), new Point2D(5, 5)),

    /**
     * DoubleDown Arrow.
     */
    DOUBLEDOWN(new Point2D(1, 1), new Point2D(1, 9), new Point2D(5, 5), new Point2D(9, 5), new Point2D(5, 9), new Point2D(1, 5), new Point2D(5, 5)),

    /**
     * DoubleLeft Arrow.
     */
    DOUBLELEFT(new Point2D(1, 5), new Point2D(5, 1), new Point2D(5, 5), new Point2D(9, 1), new Point2D(9, 9), new Point2D(5, 5), new Point2D(5, 9)),

    /**
     * DoubleRight Arrow.
     */
    DOUBLERIGHT(new Point2D(1, 1), new Point2D(1, 9), new Point2D(5, 5), new Point2D(5, 9), new Point2D(9, 5), new Point2D(5, 1), new Point2D(5, 5));

    /**
     * The Icon Map.
     */
    private static final Map<TethysUIArrowIconId, TethysUIFXArrowIcon> ICON_MAP = buildIconMap();

    /**
     * locations of points.
     */
    private final Double[] thePoints;

    /**
     * Constructor.
     * @param pPoints the icon points
     */
    TethysUIFXArrowIcon(final Point2D... pPoints) {
        /* Allocate arrays */
        final int myNumPoints = pPoints.length;
        thePoints = new Double[myNumPoints << 1];

        /* Loop through the points */
        for (int i = 0, j = 0; i < myNumPoints; i++, j += 2) {
            /* Store locations */
            thePoints[j] = pPoints[i].getX();
            thePoints[j + 1] = pPoints[i].getY();
        }
    }

    /**
     * Obtain a polygon for the arrow.
     * @return a new polygon
     */
    public Polygon getArrow() {
        /* Allocate new polygon */
        final Polygon myArrow = new Polygon();

        /* Initialise graphics */
        myArrow.setStroke(Color.GRAY);
        myArrow.setFill(Color.BLACK);

        /* Add the points */
        myArrow.getPoints().addAll(thePoints);

        /* Return the arrow */
        return myArrow;
    }

    /**
     * Build icon map.
     * @return the map
     */
    private static Map<TethysUIArrowIconId, TethysUIFXArrowIcon> buildIconMap() {
        /* Create the map and return it */
        final Map<TethysUIArrowIconId, TethysUIFXArrowIcon> myMap = new EnumMap<>(TethysUIArrowIconId.class);
        myMap.put(TethysUIArrowIconId.UP, UP);
        myMap.put(TethysUIArrowIconId.DOWN, DOWN);
        myMap.put(TethysUIArrowIconId.LEFT, LEFT);
        myMap.put(TethysUIArrowIconId.RIGHT, RIGHT);
        myMap.put(TethysUIArrowIconId.DOUBLEUP, DOUBLEUP);
        myMap.put(TethysUIArrowIconId.DOUBLEDOWN, DOUBLEDOWN);
        myMap.put(TethysUIArrowIconId.DOUBLELEFT, DOUBLELEFT);
        myMap.put(TethysUIArrowIconId.DOUBLERIGHT, DOUBLERIGHT);
        return myMap;
    }

    /**
     * Obtain icon for id.
     * @param pId the id
     * @return the icon
     */
    public static TethysUIFXIcon getIconForId(final TethysUIArrowIconId pId) {
        final TethysUIFXArrowIcon myArrow = ICON_MAP.get(pId);
        return myArrow == null
                ? null
                : new TethysUIFXIcon(myArrow.getArrow());
    }
}
