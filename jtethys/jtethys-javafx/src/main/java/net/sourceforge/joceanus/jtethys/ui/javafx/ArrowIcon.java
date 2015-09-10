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
 * $URL: http://localhost/svn/Finance/JDateButton/trunk/jdatebutton-javafx/src/main/java/net/sourceforge/jdatebutton/javafx/ArrowIcon.java $
 * $Revision: 573 $
 * $Author: Tony $
 * $Date: 2015-03-03 17:54:12 +0000 (Tue, 03 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.javafx;

import java.awt.Point;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Arrow Icons.
 */
public enum ArrowIcon {
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
     * locations of points.
     */
    private final Double[] thePoints;

    /**
     * Constructor.
     * @param pPoints the icon points
     */
    ArrowIcon(final Point... pPoints) {
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
}
