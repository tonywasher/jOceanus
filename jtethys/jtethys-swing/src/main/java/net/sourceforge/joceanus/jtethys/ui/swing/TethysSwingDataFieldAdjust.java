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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;

/**
 * Class to set Font, Colour, and Background for label according to the FieldAttributes.
 */
public class TethysSwingDataFieldAdjust {
    /**
     * Font Set.
     */
    private final TethysSwingFontSet theFontSet = new TethysSwingFontSet();

    /**
     * Colour Set.
     */
    private final TethysSwingColorSet theColorSet = new TethysSwingColorSet();

    /**
     * Adjust field.
     * @param pDataField the dataField
     * @param pLabel the label to adjust
     */
    public void adjustField(final TethysSwingDataTextField<?> pDataField,
                            final JLabel pLabel) {
        /* Determine the flags */
        boolean isNumeric = pDataField.isAttributeSet(TethysFieldAttribute.NUMERIC);
        boolean isSelected = pDataField.isAttributeSet(TethysFieldAttribute.SELECTED);
        boolean isChanged = pDataField.isAttributeSet(TethysFieldAttribute.CHANGED);
        boolean isDisabled = pDataField.isAttributeSet(TethysFieldAttribute.DISABLED);
        boolean isAlternate = pDataField.isAttributeSet(TethysFieldAttribute.ALTERNATE);

        /* Determine the font */
        Font myFont = isNumeric
                                ? isChanged
                                            ? isSelected
                                                         ? theFontSet.theBoldChangedNumeric
                                                         : theFontSet.theChangedNumeric
                                            : isSelected
                                                         ? theFontSet.theBoldNumeric
                                                         : theFontSet.theNumeric
                                : isChanged
                                            ? isSelected
                                                         ? theFontSet.theBoldChanged
                                                         : theFontSet.theChanged
                                            : isSelected
                                                         ? theFontSet.theBoldStandard
                                                         : theFontSet.theStandard;
        pLabel.setFont(myFont);

        /* Determine the foreground */
        Color myForeground = isChanged
                                       ? theColorSet.theChanged
                                       : isDisabled
                                                    ? theColorSet.theDisabled
                                                    : theColorSet.theStandard;
        pLabel.setForeground(myForeground);

        /* Determine the background */
        Color myBackground = isAlternate
                                         ? theColorSet.theZebra
                                         : theColorSet.theBackground;
        pLabel.setBackground(myBackground);
    }

    /**
     * Get error colour.
     * @return the error colour
     */
    public Color getErrorColor() {
        return theColorSet.theError;
    }

    /**
     * Font Set.
     */
    private static final class TethysSwingFontSet {
        /**
         * Standard Font pitch.
         */
        private static final int PITCH_STD = 12;

        /**
         * Bold Font pitch.
         */
        private static final int PITCH_BOLD = PITCH_STD + 2;

        /**
         * Value Font.
         */
        private static final String FONTFACE_VALUE = "Arial";

        /**
         * Numeric Font.
         */
        private static final String FONTFACE_NUMERIC = "Courier";

        /**
         * The standard font.
         */
        private final Font theStandard;

        /**
         * The standard numeric font.
         */
        private final Font theNumeric;

        /**
         * The changed font.
         */
        private final Font theChanged;

        /**
         * The changed numeric font.
         */
        private final Font theChangedNumeric;

        /**
         * The bold standard font.
         */
        private final Font theBoldStandard;

        /**
         * The bold changed font.
         */
        private final Font theBoldChanged;

        /**
         * The bold standard numeric font.
         */
        private final Font theBoldNumeric;

        /**
         * The bold changed numeric font.
         */
        private final Font theBoldChangedNumeric;

        /**
         * Constructor.
         */
        private TethysSwingFontSet() {
            theStandard = new Font(FONTFACE_VALUE, Font.PLAIN, PITCH_STD);
            theNumeric = new Font(FONTFACE_NUMERIC, Font.PLAIN, PITCH_STD);
            theChanged = new Font(FONTFACE_VALUE, Font.ITALIC, PITCH_STD);
            theChangedNumeric = new Font(FONTFACE_NUMERIC, Font.ITALIC, PITCH_STD);
            theBoldStandard = new Font(FONTFACE_VALUE, Font.BOLD, PITCH_BOLD);
            theBoldNumeric = new Font(FONTFACE_NUMERIC, Font.BOLD, PITCH_BOLD);
            theBoldChanged = new Font(FONTFACE_VALUE, Font.BOLD + Font.ITALIC, PITCH_BOLD);
            theBoldChangedNumeric = new Font(FONTFACE_NUMERIC, Font.BOLD + Font.ITALIC, PITCH_BOLD);
        }
    }

    /**
     * Colour Set.
     */
    private static final class TethysSwingColorSet {
        /**
         * Standard default.
         */
        private static final String DEFAULT_STANDARD = "#000000";

        /**
         * Changed default.
         */
        private static final String DEFAULT_CHANGED = "#8b008b";

        /**
         * Error default.
         */
        private static final String DEFAULT_ERROR = "#ff0000";

        /**
         * Zebra default.
         */
        private static final String DEFAULT_ZEBRA = "#e3e4fa";

        /**
         * Disabled default.
         */
        private static final String DEFAULT_DISABLED = "#778899";

        /**
         * Background default.
         */
        private static final String DEFAULT_BACKGROUND = "#f5f5f5";

        /**
         * The standard colour.
         */
        private final Color theStandard;

        /**
         * The changed colour.
         */
        private final Color theChanged;

        /**
         * The error colour.
         */
        private final Color theError;

        /**
         * The zebra colour.
         */
        private final Color theZebra;

        /**
         * The disabled colour.
         */
        private final Color theDisabled;

        /**
         * The background colour.
         */
        private final Color theBackground;

        /**
         * Constructor.
         */
        private TethysSwingColorSet() {
            theStandard = Color.decode(DEFAULT_STANDARD);
            theChanged = Color.decode(DEFAULT_CHANGED);
            theError = Color.decode(DEFAULT_ERROR);
            theZebra = Color.decode(DEFAULT_ZEBRA);
            theDisabled = Color.decode(DEFAULT_DISABLED);
            theBackground = Color.decode(DEFAULT_BACKGROUND);
        }
    }
}
