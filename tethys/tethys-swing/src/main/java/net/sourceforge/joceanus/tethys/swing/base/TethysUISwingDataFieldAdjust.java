/*******************************************************************************
 * Tethys: GUI Utilities
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
package net.sourceforge.joceanus.tethys.swing.base;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;

import net.sourceforge.joceanus.tethys.api.base.TethysUIValueKey;
import net.sourceforge.joceanus.tethys.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.tethys.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Class to set Font, Colour, and Background for label according to the FieldAttributes.
 */
public class TethysUISwingDataFieldAdjust {
    /**
     * AdjusterSupplier interface.
     */
    public interface TethysUISwingFieldAdjustSupplier {
        /**
         * Obtain adjuster.
         * @return the adjuster
         */
        TethysUISwingDataFieldAdjust getFieldAdjuster();
    }

    /**
     * The valueSet.
     */
    private final TethysUIValueSet theValueSet;

    /**
     * Font Set.
     */
    private TethysUISwingFontSet theFontSet;

    /**
     * Colour Set.
     */
    private TethysUISwingColorSet theColorSet;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    public TethysUISwingDataFieldAdjust(final TethysUICoreFactory<?> pFactory) {
        /* Access the value set */
        theValueSet = pFactory.getValueSet();
        theValueSet.getEventRegistrar().addEventListener(e -> buildSets());

        /* build the initial sets */
        buildSets();
    }

    /**
     * Obtain the fontSet.
     * @return the fontSet
     */
    public TethysUISwingFontSet getFontSet() {
        return theFontSet;
    }

    /**
     * Obtain the colorSet.
     * @return the colorSet
     */
    public TethysUISwingColorSet getColorSet() {
        return theColorSet;
    }

    /**
     * Adjust checkBox.
     * @param pCheckBox the checkBox
     * @param pChanged is the checkBox changed?
     */
    public void adjustCheckBox(final TethysUICheckBox pCheckBox,
                               final boolean pChanged) {
        /* Access the component */
        final JComponent myBox = TethysUISwingNode.getComponent(pCheckBox);

        /* Determine the font */
        final Font myFont = pChanged
                ? theFontSet.getChanged()
                : theFontSet.getStandard();
        myBox.setFont(myFont);

        /* Determine the foreground */
        final Color myForeground = pChanged
                ? theColorSet.getChanged()
                : theColorSet.getStandard();
        myBox.setForeground(myForeground);
    }

    /**
     * Get error colour.
     * @return the error colour
     */
    public Color getErrorColor() {
        return theColorSet.getError();
    }

    /**
     * Get progress colour.
     * @return the progress colour
     */
    public Color getProgressColor() {
        return theColorSet.getProgress();
    }

    /**
     * Build the Sets.
     */
    private void buildSets() {
        theFontSet = new TethysUISwingFontSet(theValueSet);
        theColorSet = new TethysUISwingColorSet(theValueSet);
    }

    /**
     * Font Set.
     */
    public static final class TethysUISwingFontSet {
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
         * @param pValueSet the value Set
         */
        TethysUISwingFontSet(final TethysUIValueSet pValueSet) {
            /* Access values */
            final String myValueFont = pValueSet.getValueForKey(TethysUIValueKey.FONT_STANDARD);
            final String myNumericFont = pValueSet.getValueForKey(TethysUIValueKey.FONT_NUMERIC);
            final String myPitch = pValueSet.getValueForKey(TethysUIValueKey.FONT_PITCH);

            /* Determine pitches */
            final int myBasePitch = Integer.parseInt(myPitch);
            final int myBoldPitch = myBasePitch + 2;

            /* Build fonts */
            theStandard = new Font(myValueFont, Font.PLAIN, myBasePitch);
            theNumeric = new Font(myNumericFont, Font.PLAIN, myBasePitch);
            theChanged = new Font(myValueFont, Font.ITALIC, myBasePitch);
            theChangedNumeric = new Font(myNumericFont, Font.ITALIC, myBasePitch);
            theBoldStandard = new Font(myValueFont, Font.BOLD, myBoldPitch);
            theBoldNumeric = new Font(myNumericFont, Font.BOLD, myBoldPitch);
            theBoldChanged = new Font(myValueFont, Font.BOLD + Font.ITALIC, myBoldPitch);
            theBoldChangedNumeric = new Font(myNumericFont, Font.BOLD + Font.ITALIC, myBoldPitch);
        }

        /**
         * Obtain the Standard font.
         * @return the font
         */
        public Font getStandard() {
            return theStandard;
        }

        /**
         * Obtain the Changed font.
         * @return the font
         */
        public Font getChanged() {
            return theChanged;
        }

        /**
         * Obtain the Numeric font.
         * @return the font
         */
        public Font getNumeric() {
            return theNumeric;
        }

        /**
         * Obtain the ChangedNumeric font.
         * @return the font
         */
        public Font getChangedNumeric() {
            return theChangedNumeric;
        }

        /**
         * Obtain the Bold font.
         * @return the font
         */
        public Font getBoldStandard() {
            return theBoldStandard;
        }

        /**
         * Obtain the BoldChanged font.
         * @return the font
         */
        public Font getBoldChanged() {
            return theBoldChanged;
        }

        /**
         * Obtain the BoldNumeric font.
         * @return the font
         */
        public Font getBoldNumeric() {
            return theBoldNumeric;
        }

        /**
         * Obtain the BoldChangedNumeric font.
         * @return the font
         */
        public Font getBoldChangedNumeric() {
            return theBoldChangedNumeric;
        }
    }

    /**
     * Colour Set.
     */
    public static final class TethysUISwingColorSet {
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
         * The progress colour.
         */
        private final Color theProgress;

        /**
         * Constructor.
         * @param pValueSet the value Set
         */
        TethysUISwingColorSet(final TethysUIValueSet pValueSet) {
            theStandard = Color.decode(pValueSet.getValueForKey(TethysUIValueKey.COLOR_STANDARD));
            theChanged = Color.decode(pValueSet.getValueForKey(TethysUIValueKey.COLOR_CHANGED));
            theError = Color.decode(pValueSet.getValueForKey(TethysUIValueKey.COLOR_ERROR));
            theZebra = Color.decode(pValueSet.getValueForKey(TethysUIValueKey.COLOR_ZEBRA));
            theDisabled = Color.decode(pValueSet.getValueForKey(TethysUIValueKey.COLOR_DISABLED));
            theBackground = Color.decode(pValueSet.getValueForKey(TethysUIValueKey.COLOR_BACKGROUND));
            theProgress = Color.decode(pValueSet.getValueForKey(TethysUIValueKey.COLOR_PROGRESS));
        }

        /**
         * Obtain the Standard colour.
         * @return the colour
         */
        public Color getStandard() {
            return theStandard;
        }

        /**
         * Obtain the Changed colour.
         * @return the colour
         */
        public Color getChanged() {
            return theChanged;
        }

        /**
         * Obtain the Error colour.
         * @return the colour
         */
        public Color getError() {
            return theError;
        }

        /**
         * Obtain the Zebra colour.
         * @return the colour
         */
        public Color getZebra() {
            return theZebra;
        }

        /**
         * Obtain the Disabled colour.
         * @return the colour
         */
        public Color getDisabled() {
            return theDisabled;
        }

        /**
         * Obtain the Background colour.
         * @return the colour
         */
        public Color getBackground() {
            return theBackground;
        }

        /**
         * Obtain the Standard colour.
         * @return the colour
         */
        public Color getProgress() {
            return theProgress;
        }
    }
}
