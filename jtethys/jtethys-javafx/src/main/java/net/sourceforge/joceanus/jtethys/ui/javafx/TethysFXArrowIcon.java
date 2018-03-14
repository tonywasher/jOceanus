/*******************************************************************************
* jTethys: Java Utilities
 * Copyright 2012,2017 Tony Washer
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

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import net.sourceforge.joceanus.jtethys.ui.TethysArrowIconId;

/**
 * Arrow Icons.
 */
public enum TethysFXArrowIcon {
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
     * locations of points.
     */
    private final Double[] thePoints;

    /**
     * Constructor.
     * @param pPoints the icon points
     */
    TethysFXArrowIcon(final Point2D... pPoints) {
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
     * Obtain icon for id.
     * @param pId the id
     * @return the icon
     */
    protected static Node getIconForId(final TethysArrowIconId pId) {
        switch (pId) {
            case UP:
                return UP.getArrow();
            case DOWN:
                return DOWN.getArrow();
            case LEFT:
                return LEFT.getArrow();
            case RIGHT:
                return RIGHT.getArrow();
            case DOUBLEUP:
                return DOUBLEUP.getArrow();
            case DOUBLEDOWN:
                return DOUBLEDOWN.getArrow();
            case DOUBLELEFT:
                return DOUBLELEFT.getArrow();
            case DOUBLERIGHT:
                return DOUBLERIGHT.getArrow();
            default:
                return null;
        }
    }
}
