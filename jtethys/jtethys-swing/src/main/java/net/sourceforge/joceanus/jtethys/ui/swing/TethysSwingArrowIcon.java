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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.Icon;

import net.sourceforge.joceanus.jtethys.resource.TethysResourceLoader;
import net.sourceforge.joceanus.jtethys.ui.TethysArrowIconId;

/**
 * Arrow Icons.
 */
public enum TethysSwingArrowIcon implements Icon {
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
    private static final Map<TethysArrowIconId, TethysSwingIcon> ICON_MAP = buildIconMap();

    /**
     * The size of the icon.
     */
    private static final int ICON_SIZE = 10;

    /**
     * The number of points.
     */
    private final int theNumPoints;

    /**
     * X locations of points.
     */
    private final transient int[] theXPoints;

    /**
     * Y Locations of points.
     */
    private final transient int[] theYPoints;

    /**
     * Constructor.
     * @param pPoints the icon points
     */
    TethysSwingArrowIcon(final Point... pPoints) {
        /* Allocate arrays */
        theNumPoints = pPoints.length;
        theXPoints = new int[theNumPoints];
        theYPoints = new int[theNumPoints];

        /* Loop through the points */
        for (int i = 0; i < theNumPoints; i++) {
            /* Store locations */
            theXPoints[i] = pPoints[i].x;
            theYPoints[i] = pPoints[i].y;
        }
    }

    @Override
    public void paintIcon(final Component c,
                          final Graphics g,
                          final int x,
                          final int y) {
        /* Allocate new graphics context */
        final Graphics g2 = g.create(x, y, ICON_SIZE, ICON_SIZE);

        /* Initialise graphics */
        g2.setColor(Color.GRAY);
        g2.drawPolygon(theXPoints, theYPoints, theNumPoints);

        /* If the component is enabled */
        if (c.isEnabled()) {
            /* Colour in the polygon */
            g2.setColor(Color.BLACK);
            g2.fillPolygon(theXPoints, theYPoints, theNumPoints);
        }

        /* Dispose of the graphics context */
        g2.dispose();
    }

    @Override
    public int getIconWidth() {
        return ICON_SIZE;
    }

    @Override
    public int getIconHeight() {
        return ICON_SIZE;
    }

    /**
     * Build icon map.
     * @return the map
     */
    private static Map<TethysArrowIconId, TethysSwingIcon> buildIconMap() {
        /* Create the map and return it */
        final Map<TethysArrowIconId, TethysSwingIcon> myMap = new EnumMap<>(TethysArrowIconId.class);
        myMap.put(TethysArrowIconId.UP, new TethysSwingIcon(UP));
        myMap.put(TethysArrowIconId.DOWN, new TethysSwingIcon(DOWN));
        myMap.put(TethysArrowIconId.LEFT, new TethysSwingIcon(LEFT));
        myMap.put(TethysArrowIconId.RIGHT, new TethysSwingIcon(RIGHT));
        myMap.put(TethysArrowIconId.DOUBLEUP, new TethysSwingIcon(DOUBLEUP));
        myMap.put(TethysArrowIconId.DOUBLEDOWN, new TethysSwingIcon(DOUBLEDOWN));
        myMap.put(TethysArrowIconId.DOUBLELEFT, new TethysSwingIcon(DOUBLELEFT));
        myMap.put(TethysArrowIconId.DOUBLERIGHT, new TethysSwingIcon(DOUBLERIGHT));
        return myMap;
    }

    /**
     * Obtain icon for id.
     * @param pId the id
     * @return the icon
     */
    protected static TethysSwingIcon getIconForId(final TethysArrowIconId pId) {
        return TethysResourceLoader.getIconForEnum(ICON_MAP, pId);
    }
}
