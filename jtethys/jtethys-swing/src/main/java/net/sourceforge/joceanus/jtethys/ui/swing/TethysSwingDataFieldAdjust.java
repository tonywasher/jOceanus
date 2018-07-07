/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
        final boolean isNumeric = pDataField.isAttributeSet(TethysFieldAttribute.NUMERIC);
        final boolean isSelected = pDataField.isAttributeSet(TethysFieldAttribute.SELECTED);
        final boolean isChanged = pDataField.isAttributeSet(TethysFieldAttribute.CHANGED);
        final boolean isDisabled = pDataField.isAttributeSet(TethysFieldAttribute.DISABLED);
        final boolean isAlternate = pDataField.isAttributeSet(TethysFieldAttribute.ALTERNATE);

        /* Obtain the label and the edit control */
        final JLabel myLabel = pDataField.getLabel();
        final JComponent myControl = pDataField.getEditControl();

        /* Determine the font */
        final Font myFont = isNumeric
                                      ? isChanged
                                                  ? isSelected
                                                               ? theFontSet.getBoldChangedNumeric()
                                                               : theFontSet.getChangedNumeric()
                                                  : isSelected
                                                               ? theFontSet.getBoldNumeric()
                                                               : theFontSet.getNumeric()
                                      : isChanged
                                                  ? isSelected
                                                               ? theFontSet.getBoldChanged()
                                                               : theFontSet.getChanged()
                                                  : isSelected
                                                               ? theFontSet.getBoldStandard()
                                                               : theFontSet.getStandard();
        myLabel.setFont(myFont);
        myControl.setFont(myFont);

        /* Determine the foreground */
        final Color myForeground = isChanged
                                             ? theColorSet.getChanged()
                                             : isDisabled
                                                          ? theColorSet.getDisabled()
                                                          : theColorSet.getStandard();
        myLabel.setForeground(myForeground);
        myControl.setForeground(myForeground);

        /* Determine the background (don't set the control) */
        final Color myBackground = isAlternate
                                               ? theColorSet.getZebra()
                                               : theColorSet.getBackground();
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
        final JComponent myBox = TethysSwingNode.getComponent(pCheckBox);

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
    protected Color getErrorColor() {
        return theColorSet.getError();
    }

    /**
     * Get progress colour.
     * @return the progress colour
     */
    protected Color getProgressColor() {
        return theColorSet.getProgress();
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
        TethysSwingFontSet(final TethysValueSet pValueSet) {
            /* Access values */
            final String myValueFont = pValueSet.getValueForKey(TethysValueSet.TETHYS_FONT_STANDARD);
            final String myNumericFont = pValueSet.getValueForKey(TethysValueSet.TETHYS_FONT_NUMERIC);
            final String myPitch = pValueSet.getValueForKey(TethysValueSet.TETHYS_FONT_PITCH);

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
        Font getStandard() {
            return theStandard;
        }

        /**
         * Obtain the Changed font.
         * @return the font
         */
        Font getChanged() {
            return theChanged;
        }

        /**
         * Obtain the Numeric font.
         * @return the font
         */
        Font getNumeric() {
            return theNumeric;
        }

        /**
         * Obtain the ChangedNumeric font.
         * @return the font
         */
        Font getChangedNumeric() {
            return theChangedNumeric;
        }

        /**
         * Obtain the Bold font.
         * @return the font
         */
        Font getBoldStandard() {
            return theBoldStandard;
        }

        /**
         * Obtain the BoldChanged font.
         * @return the font
         */
        Font getBoldChanged() {
            return theBoldChanged;
        }

        /**
         * Obtain the BoldNumeric font.
         * @return the font
         */
        Font getBoldNumeric() {
            return theBoldNumeric;
        }

        /**
         * Obtain the BoldChangedNumeric font.
         * @return the font
         */
        Font getBoldChangedNumeric() {
            return theBoldChangedNumeric;
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
        TethysSwingColorSet(final TethysValueSet pValueSet) {
            theStandard = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_STANDARD));
            theChanged = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_CHANGED));
            theError = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_ERROR));
            theZebra = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_ZEBRA));
            theDisabled = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_DISABLED));
            theBackground = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_BACKGROUND));
            theProgress = Color.decode(pValueSet.getValueForKey(TethysValueSet.TETHYS_COLOR_PROGRESS));
        }

        /**
         * Obtain the Standard colour.
         * @return the colour
         */
        Color getStandard() {
            return theStandard;
        }

        /**
         * Obtain the Changed colour.
         * @return the colour
         */
        Color getChanged() {
            return theChanged;
        }

        /**
         * Obtain the Error colour.
         * @return the colour
         */
        Color getError() {
            return theError;
        }

        /**
         * Obtain the Zebra colour.
         * @return the colour
         */
        Color getZebra() {
            return theZebra;
        }

        /**
         * Obtain the Disabled colour.
         * @return the colour
         */
        Color getDisabled() {
            return theDisabled;
        }

        /**
         * Obtain the Background colour.
         * @return the colour
         */
        Color getBackground() {
            return theBackground;
        }

        /**
         * Obtain the Standard colour.
         * @return the colour
         */
        Color getProgress() {
            return theProgress;
        }
    }
}
