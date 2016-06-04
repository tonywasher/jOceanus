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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.ui.TethysArrowIconId;

/**
 * Arrow Icons.
 */
public enum TethysFXArrowIcon {
    /**
     * Up Arrow.
     */
    UP(new Point(1, 9), new Point(5, 1), new Point(9, 9)),

    /**
     * Down Arrow.
     */
    DOWN(new Point(1, 1), new Point(5, 9), new Point(9, 1)),

    /**
     * Left Arrow.
     */
    LEFT(new Point(1, 5), new Point(9, 1), new Point(9, 9)),

    /**
     * Right Arrow.
     */
    RIGHT(new Point(1, 1), new Point(1, 9), new Point(9, 5)),

    /**
     * DoubleUp Arrow.
     */
    DOUBLEUP(new Point(1, 5), new Point(5, 1), new Point(9, 5), new Point(5, 5), new Point(9, 9), new Point(1, 9), new Point(5, 5)),

    /**
     * DoubleDown Arrow.
     */
    DOUBLEDOWN(new Point(1, 1), new Point(1, 9), new Point(5, 5), new Point(9, 5), new Point(5, 9), new Point(1, 5), new Point(5, 5)),

    /**
     * DoubleLeft Arrow.
     */
    DOUBLELEFT(new Point(1, 5), new Point(5, 1), new Point(5, 5), new Point(9, 1), new Point(9, 9), new Point(5, 5), new Point(5, 9)),

    /**
     * DoubleRight Arrow.
     */
    DOUBLERIGHT(new Point(1, 1), new Point(1, 9), new Point(5, 5), new Point(5, 9), new Point(9, 5), new Point(5, 1), new Point(5, 5));

    /**
     * The Icon Map.
     */
    private static final Map<TethysArrowIconId, Node> ICON_MAP = buildIconMap();

    /**
     * locations of points.
     */
    private final Double[] thePoints;

    /**
     * Constructor.
     * @param pPoints the icon points
     */
    TethysFXArrowIcon(final Point... pPoints) {
        /* Allocate arrays */
        int myNumPoints = pPoints.length;
        thePoints = new Double[myNumPoints << 1];

        /* Loop through the points */
        for (int i = 0, j = 0; i < myNumPoints; i++, j += 2) {
            /* Store locations */
            thePoints[j] = Double.valueOf(pPoints[i].x);
            thePoints[j + 1] = Double.valueOf(pPoints[i].y);
        }
    }

    /**
     * Obtain a polygon for the arrow.
     * @return a new polygon
     */
    public Polygon getArrow() {
        /* Allocate new polygon */
        Polygon myArrow = new Polygon();

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
    private static Map<TethysArrowIconId, Node> buildIconMap() {
        /* Create the map and return it */
        Map<TethysArrowIconId, Node> myMap = new EnumMap<>(TethysArrowIconId.class);
        myMap.put(TethysArrowIconId.UP, UP.getArrow());
        myMap.put(TethysArrowIconId.DOWN, DOWN.getArrow());
        myMap.put(TethysArrowIconId.LEFT, LEFT.getArrow());
        myMap.put(TethysArrowIconId.RIGHT, RIGHT.getArrow());
        myMap.put(TethysArrowIconId.DOUBLEUP, DOUBLEUP.getArrow());
        myMap.put(TethysArrowIconId.DOUBLEDOWN, DOUBLEDOWN.getArrow());
        myMap.put(TethysArrowIconId.DOUBLELEFT, DOUBLELEFT.getArrow());
        myMap.put(TethysArrowIconId.DOUBLERIGHT, DOUBLERIGHT.getArrow());
        return myMap;
    }

    /**
     * Obtain icon for id.
     * @param pId the id
     * @return the icon
     */
    protected static Node getIconForId(final TethysArrowIconId pId) {
        return TethysResourceBuilder.getIconForEnum(ICON_MAP, pId);
    }
}
