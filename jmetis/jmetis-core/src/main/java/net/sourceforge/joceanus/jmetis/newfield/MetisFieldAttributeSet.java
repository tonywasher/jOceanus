/*******************************************************************************
 * jMetis: Java Data Framework
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/field/package-info.java $
 * $Revision: 587 $
 * $Author: Tony $
 * $Date: 2015-03-31 14:44:28 +0100 (Tue, 31 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.newfield;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetItem;

/**
 * AttributeSet.
 * @param <C> the color type
 * @param <F> the font type
 */
public abstract class MetisFieldAttributeSet<C, F> {
    /**
     * Standard Font pitch.
     */
    private static final int PITCH_STD = 12;

    /**
     * Value Font.
     */
    private static final String FONTFACE_VALUE = "Arial";

    /**
     * Numeric Font.
     */
    private static final String FONTFACE_NUMERIC = "Courier";

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
     * Diabled default.
     */
    private static final String DEFAULT_DISABLED = "#d3d3d3";

    /**
     * Font Set.
     */
    private final MetisFieldFontSet<F> theFontSet;

    /**
     * Colour Set.
     */
    private final MetisFieldColorSet<C> theColorSet;

    /**
     * Constructor.
     * @param pFonts the fontSet
     * @param pColors the colorSet
     */
    protected MetisFieldAttributeSet(final MetisFieldFontSet<F> pFonts,
                                     final MetisFieldColorSet<C> pColors) {
        /* store parameters */
        theFontSet = pFonts;
        theColorSet = pColors;

        /* Generate defaults */
        generateFonts(FONTFACE_VALUE, FONTFACE_NUMERIC, PITCH_STD);
        generateColors(DEFAULT_STANDARD, DEFAULT_CHANGED, DEFAULT_ERROR, DEFAULT_ZEBRA, DEFAULT_DISABLED);
    }

    /**
     * Generate fonts.
     * @param pStandard the name of the standard font
     * @param pNumeric the name of the numeric font
     * @param pPitch the pitch of the fonts
     */
    protected void generateFonts(final String pStandard,
                                 final String pNumeric,
                                 final int pPitch) {
        theFontSet.generateFonts(pStandard, pNumeric, pPitch);
    }

    /**
     * Generate colours.
     * @param pStandard the standard colour
     * @param pChanged the changed colour
     * @param pError the error colour
     * @param pZebra the zebra colour
     * @param pDisabled the disabled colour
     */
    protected void generateColors(final String pStandard,
                                  final String pChanged,
                                  final String pError,
                                  final String pZebra,
                                  final String pDisabled) {
        theColorSet.generateColors(pStandard, pChanged, pError, pZebra, pDisabled);
    }

    /**
     * Obtain font for field.
     * @param pItem the item
     * @param pField the field
     * @param pNumeric is the field numeric?
     * @return the font
     */
    public F getFontForField(final MetisFieldSetItem pItem,
                             final MetisField pField,
                             final boolean pNumeric) {
        return getFontForField(pNumeric, pItem.getFieldState(pField).isChanged());
    }

    /**
     * Obtain bold font for field.
     * @param pItem the item
     * @param pField the field
     * @param pNumeric is the field numeric?
     * @return the font
     */
    public F getBoldFontForField(final MetisFieldSetItem pItem,
                                 final MetisField pField,
                                 final boolean pNumeric) {
        return getFontForField(pNumeric, pItem.getFieldState(pField).isChanged());
    }

    /**
     * Obtain font for field.
     * @param pNumeric is the field numeric?
     * @param pChanged is the field changed?
     * @return the font
     */
    public F getFontForField(final boolean pNumeric,
                             final boolean pChanged) {
        return pNumeric
                        ? pChanged
                                   ? theFontSet.getNumericChangedFont()
                                   : theFontSet.getNumericFont()
                        : pChanged
                                   ? theFontSet.getStandardChangedFont()
                                   : theFontSet.getStandardFont();
    }

    /**
     * Obtain bold font for field.
     * @param pNumeric is the field numeric?
     * @param pChanged is the field changed?
     * @return the font
     */
    public F getBoldFontForField(final boolean pNumeric,
                                 final boolean pChanged) {
        return pNumeric
                        ? pChanged
                                   ? theFontSet.getBoldNumericChangedFont()
                                   : theFontSet.getBoldNumericFont()
                        : pChanged
                                   ? theFontSet.getBoldStandardChangedFont()
                                   : theFontSet.getBoldStandardFont();
    }

    /**
     * Obtain the standard colour.
     * @param pChanged is the field changed
     * @return the colour
     */
    public C getStandardColor(final boolean pChanged) {
        return pChanged
                        ? theColorSet.getStandardColor()
                        : theColorSet.getChangedColor();
    }

    /**
     * Obtain the error colour.
     * @return the colour
     */
    public C getErrorColor() {
        return theColorSet.getErrorColor();
    }

    /**
     * Obtain the zebra colour.
     * @return the colour
     */
    public C getZebraColor() {
        return theColorSet.getZebraColor();
    }

    /**
     * Obtain the disabled colour.
     * @return the colour
     */
    public C getDisabledColor() {
        return theColorSet.getDisabledColor();
    }

    /**
     * Font Set.
     * @param <X> the font type
     */
    protected abstract static class MetisFieldFontSet<X> {
        /**
         * Initialise fontSet.
         * @param pStandard the name of the standard font
         * @param pNumeric the name of the numeric font
         * @param pPitch the pitch of the fonts
         */
        protected abstract void generateFonts(final String pStandard,
                                              final String pNumeric,
                                              final int pPitch);

        /**
         * Obtain the standard font.
         * @return the font
         */
        public abstract X getStandardFont();

        /**
         * Obtain the numeric font.
         * @return the font
         */
        public abstract X getNumericFont();

        /**
         * Obtain the standard changed font.
         * @return the font
         */
        public abstract X getStandardChangedFont();

        /**
         * Obtain the numeric changed font.
         * @return the font
         */
        public abstract X getNumericChangedFont();

        /**
         * Obtain the bold standard font.
         * @return the font
         */
        public abstract X getBoldStandardFont();

        /**
         * Obtain the bold numeric font.
         * @return the font
         */
        public abstract X getBoldNumericFont();

        /**
         * Obtain the bold standard changed font.
         * @return the font
         */
        public abstract X getBoldStandardChangedFont();

        /**
         * Obtain the numeric changed font.
         * @return the font
         */
        public abstract X getBoldNumericChangedFont();
    }

    /**
     * Colour Set.
     * @param <X> the colour type
     */
    protected abstract static class MetisFieldColorSet<X> {
        /**
         * Generate colours.
         * @param pStandard the standard colour
         * @param pChanged the changed colour
         * @param pError the error colour
         * @param pZebra the zebra colour
         * @param pDisabled the disabled colour
         */
        protected abstract void generateColors(final String pStandard,
                                               final String pChanged,
                                               final String pError,
                                               final String pZebra,
                                               final String pDisabled);

        /**
         * Obtain the standard colour.
         * @return the colour
         */
        public abstract X getStandardColor();

        /**
         * Obtain the changed colour.
         * @return the colour
         */
        public abstract X getChangedColor();

        /**
         * Obtain the error colour.
         * @return the colour
         */
        public abstract X getErrorColor();

        /**
         * Obtain the zebra colour.
         * @return the colour
         */
        public abstract X getZebraColor();

        /**
         * Obtain the disabled colour.
         * @return the colour
         */
        public abstract X getDisabledColor();
    }
}
