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

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Utilities to ease building of GridBag panels.
 */
public final class GridBagUtilities {
    /**
     * Inset depth.
     */
    private static final int INSET_DEPTH = 5;

    /**
     * Private constructor to prevent instantiation.
     */
    private GridBagUtilities() {
    }

    /**
     * Set constraints for a label.
     * @param pConstraints the constraints
     * @param pRow the row number
     * @param pFill the fill constraint
     */
    public static void setPanelLabel(final GridBagConstraints pConstraints,
                                     final int pRow,
                                     final int pFill) {
        pConstraints.gridx = 0;
        pConstraints.gridy = pRow;
        pConstraints.gridwidth = 1;
        pConstraints.fill = pFill;
        pConstraints.weightx = 0.0;
        pConstraints.anchor = GridBagConstraints.LINE_END;
        pConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
    }

    /**
     * Set panel field.
     * @param pConstraints the constraints
     * @param pRow the row number
     * @param pCol the column number
     * @param pWidth the width constraint
     */
    public static void setPanelField(final GridBagConstraints pConstraints,
                                     final int pRow,
                                     final int pCol,
                                     final int pWidth) {
        pConstraints.gridx = pCol;
        pConstraints.gridy = pRow;
        pConstraints.gridwidth = pWidth;
        pConstraints.anchor = GridBagConstraints.LINE_START;
        pConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
        if (pWidth == GridBagConstraints.REMAINDER) {
            pConstraints.fill = GridBagConstraints.HORIZONTAL;
            pConstraints.weightx = 1.0;
        } else {
            pConstraints.gridwidth = 1;
            pConstraints.fill = GridBagConstraints.NONE;
            pConstraints.weightx = 0.0;
        }
    }

    /**
     * Set Panel Row.
     * @param pConstraints the constraints
     * @param pRow the row number
     */
    public static void setPanelRow(final GridBagConstraints pConstraints,
                                   final int pRow) {
        /* Add the Label into the first slot */
        pConstraints.gridx = 0;
        pConstraints.gridy = pRow;
        pConstraints.gridwidth = GridBagConstraints.REMAINDER;
        pConstraints.fill = GridBagConstraints.HORIZONTAL;
        pConstraints.weightx = 1.0;
        pConstraints.anchor = GridBagConstraints.LINE_START;
        pConstraints.insets = new Insets(INSET_DEPTH, INSET_DEPTH, INSET_DEPTH, INSET_DEPTH);
    }
}
