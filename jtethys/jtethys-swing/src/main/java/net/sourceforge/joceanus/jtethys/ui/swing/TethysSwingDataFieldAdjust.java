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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysValueSet;

/**
 * Class to set Font, Colour, and Background for label according to the FieldAttributes.
 */
public class TethysSwingDataFieldAdjust {
    /**
     * The valueSet.
     */
    private final TethysValueSet theValueSet;

    /**
     * Font Set.
     */
    private TethysSwingFontSet theFontSet;

    /**
     * Colour Set.
     */
    private TethysSwingColorSet theColorSet;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingDataFieldAdjust(final TethysSwingGuiFactory pFactory) {
        /* Access the value set */
        theValueSet = pFactory.getValueSet();
        theValueSet.getEventRegistrar().addEventListener(e -> buildSets());

        /* build the initial sets */
        buildSets();
    }

    /**
     * Adjust field.
     * @param pDataField the dataField
     */
    protected void adjustField(final TethysSwingDataTextField<?> pDataField) {
        /* Determine the flags */
        boolean isNumeric = pDataField.isAttributeSet(TethysFieldAttribute.NUMERIC);
        boolean isSelected = pDataField.isAttributeSet(TethysFieldAttribute.SELECTED);
        boolean isChanged = pDataField.isAttributeSet(TethysFieldAttribute.CHANGED);
        boolean isDisabled = pDataField.isAttributeSet(TethysFieldAttribute.DISABLED);
        boolean isAlternate = pDataField.isAttributeSet(TethysFieldAttribute.ALTERNATE);

        /* Obtain the label and the edit control */
        JLabel myLabel = pDataField.getLabel();
        JComponent myControl = pDataField.getEditControl();

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
        myLabel.setFont(myFont);
        myControl.setFont(myFont);

        /* Determine the foreground */
        Color myForeground = isChanged
                                       ? theColorSet.theChanged
                                       : isDisabled
                                                    ? theColorSet.theDisabled
                                                    : theColorSet.theStandard;
        myLabel.setForeground(myForeground);
        myControl.setForeground(myForeground);

        /* Determine the background (don't set the control) */
        Color myBackground = isAlternate
                                         ? theColorSet.theZebra
                                         : theColorSet.theBackground;
        myLabel.setBackground(myBackground);
    }

    /**
     * Adjust checkBox.
     * @param pCheckBox the checkBox
     * @param pChanged is the checkBox changed?
     */
    protected void adjustCheckBox(final TethysSwingCheckBox pCheckBox,
                                  final boolean pChanged) {
        /* Access the component */
        JComponent myBox = pCheckBox.getNode();

        /* Determine the font */
        Font myFont = pChanged
                               ? theFontSet.theChanged
                               : theFontSet.theStandard;
        myBox.setFont(myFont);

        /* Determine the foreground */
        Color myForeground = pChanged
                                      ? theColorSet.theChanged
                                      : theColorSet.theStandard;
        myBox.setForeground(myForeground);
    }

    /**
     * Get error colour.
     * @return the error colour
     */
    protected Color getErrorColor() {
        return theColorSet.theError;
    }

    /**
     * Get progress colour.
     * @return the progress colour
     */
    protected Color getProgressColor() {
        return theColorSet.theProgress;
    }

    /**
     * Build the Sets.
     */
    private void buildSets() {
        theFontSet = new TethysSwingFontSet(theValueSet);
        theColorSet = new TethysSwingColorSet(theValueSet);
    }

    /**
     * Font Set.
     */
    private static final class TethysSwingFontSet {
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
        private TethysSwingFontSet(final TethysValueSet pValueSet) {
            /* Access values */
            String myValueFont = pValueSet.getValueForKey(TethysValueSet.TETHYS_FONT_STANDARD);
            String myNumericFont = pValueSet.getValueForKey(TethysValueSet.TETHYS_FONT_NUMERIC);
            String myPitch = pValueSet.getValueForKey(TethysValueSet.TETHYS_FONT_PITCH);

            /* Determine pitches */
            int myBasePitch = Integer.parseInt(myPitch);
            int myBoldPitch = myBasePitch + 2;

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
    }

    /**
     * Colour Set.
     */
    private static final class TethysSwingColorSet {
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
        private TethysSwingColorSet(final TethysValueSet pValueSet) {
            theStandard = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_STANDARD));
            theChanged = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_CHANGED));
            theError = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_ERROR));
            theZebra = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_ZEBRA));
            theDisabled = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_DISABLED));
            theBackground = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_BACKGROUND));
            theProgress = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_PROGRESS));
        }
    }
}
