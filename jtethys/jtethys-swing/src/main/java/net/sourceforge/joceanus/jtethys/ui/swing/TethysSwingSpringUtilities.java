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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Component;

import javax.swing.JPanel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

/**
 * SpringUtilities class derived from Oracle tutorial.
 * @author Tony Washer
 */
public final class TethysSwingSpringUtilities {
    /**
     * Private constructor to prevent instantiation.
     */
    private TethysSwingSpringUtilities() {
    }

    /**
     * Aligns the first <code>rows</code> * <code>cols</code> components of <code>parent</code> in a
     * grid. Each component in a column is as wide as the maximum preferred width of the components
     * in that column; height is similarly determined for each row. The parent is made just big
     * enough to fit them all.
     * @param pParent the parent panel
     * @param pLayout the layout of the panel
     * @param pRows number of rows
     * @param pCols number of columns
     * @param pMargin margin between cells and edges
     */
    public static void makeCompactGrid(final JPanel pParent,
                                       final SpringLayout pLayout,
                                       final int pRows,
                                       final int pCols,
                                       final int pMargin) {
        /* Align all cells in each column and make them the same width. */
        Spring myX = Spring.constant(pMargin);
        for (int c = 0; c < pCols; c++) {
            /* Start with zero width */
            Spring myWidth = Spring.constant(0);

            /* Calculate the maximum width for the column */
            for (int r = 0, i = c; r < pRows; r++, i += pCols) {
                /* Adjust total width as required */
                Component myComponent = pParent.getComponent(i);
                Constraints myConstraints = pLayout.getConstraints(myComponent);
                myWidth = Spring.max(myWidth, myConstraints.getWidth());
            }

            /* Apply x-position and width for column */
            for (int r = 0, i = c; r < pRows; r++, i += pCols) {
                /* Set x-position and width for each cell */
                Component myComponent = pParent.getComponent(i);
                Constraints myConstraints = pLayout.getConstraints(myComponent);
                myConstraints.setX(myX);
                myConstraints.setWidth(myWidth);
            }

            /* Adjust x-position */
            myX = Spring.sum(myX, Spring.sum(myWidth, Spring.constant(pMargin)));
        }

        /* Align all cells in each row and make them the same height. */
        Spring myY = Spring.constant(pMargin);
        for (int r = 0, i = 0; r < pRows; r++, i += pCols) {
            /* Start with zero height */
            Spring myHeight = Spring.constant(0);

            /* Calculate the maximum height for each row */
            for (int c = 0; c < pCols; c++) {
                /* Adjust total width as required */
                Component myComponent = pParent.getComponent(i
                                                             + c);
                Constraints myConstraints = pLayout.getConstraints(myComponent);
                myHeight = Spring.max(myHeight, myConstraints.getHeight());
            }

            /* Apply y-position and height for row */
            for (int c = 0; c < pCols; c++) {
                /* Set y-position and height for each cell */
                Component myComponent = pParent.getComponent(i
                                                             + c);
                Constraints myConstraints = pLayout.getConstraints(myComponent);
                myConstraints.setY(myY);
                myConstraints.setHeight(myHeight);
            }

            /* Adjust y-position */
            myY = Spring.sum(myY, Spring.sum(myHeight, Spring.constant(pMargin)));
        }

        /* Set the parent's size. */
        Constraints myCons = pLayout.getConstraints(pParent);
        myCons.setConstraint(SpringLayout.SOUTH, myY);
        myCons.setConstraint(SpringLayout.EAST, myX);
    }
}
