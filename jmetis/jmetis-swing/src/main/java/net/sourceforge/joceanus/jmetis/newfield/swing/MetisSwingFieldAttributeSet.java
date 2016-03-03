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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.newfield.swing;

import java.awt.Color;
import java.awt.Font;

import net.sourceforge.joceanus.jmetis.newfield.MetisFieldAttributeSet;

/**
 * Swing AttributeSet.
 */
public class MetisSwingFieldAttributeSet
        extends MetisFieldAttributeSet<Color, Font> {
    /**
     * Constructor.
     */
    public MetisSwingFieldAttributeSet() {
        super(new MetisSwingFieldFontSet(), new MetisSwingFieldColorSet());
    }

    /**
     * Colour Set.
     */
    private static class MetisSwingFieldFontSet
            extends MetisFieldFontSet<Font> {
        /**
         * The standard font.
         */
        private Font theStandard;

        /**
         * The standard numeric font.
         */
        private Font theNumeric;

        /**
         * The changed font.
         */
        private Font theChanged;

        /**
         * The changed numeric font.
         */
        private Font theChangedNumeric;

        /**
         * The bold standard font.
         */
        private Font theBoldStandard;

        /**
         * The bold changed font.
         */
        private Font theBoldChanged;

        /**
         * The bold standard numeric font.
         */
        private Font theBoldNumeric;

        /**
         * The bold changed numeric font.
         */
        private Font theBoldChangedNumeric;

        @Override
        protected void generateFonts(final String pStandard,
                                     final String pNumeric,
                                     final int pPitch) {
            theStandard = new Font(pStandard, Font.PLAIN, pPitch);
            theNumeric = new Font(pNumeric, Font.PLAIN, pPitch);
            theChanged = new Font(pStandard, Font.ITALIC, pPitch);
            theChangedNumeric = new Font(pNumeric, Font.ITALIC, pPitch);
            theBoldStandard = new Font(pStandard, Font.BOLD, pPitch + 2);
            theBoldNumeric = new Font(pNumeric, Font.BOLD, pPitch + 2);
            theBoldChanged = new Font(pStandard, Font.BOLD + Font.ITALIC, pPitch + 2);
            theBoldChangedNumeric = new Font(pNumeric, Font.BOLD + Font.ITALIC, pPitch + 2);
        }

        @Override
        public Font getStandardFont() {
            return theStandard;
        }

        @Override
        public Font getNumericFont() {
            return theNumeric;
        }

        @Override
        public Font getStandardChangedFont() {
            return theChanged;
        }

        @Override
        public Font getNumericChangedFont() {
            return theChangedNumeric;
        }

        @Override
        public Font getBoldStandardFont() {
            return theBoldStandard;
        }

        @Override
        public Font getBoldNumericFont() {
            return theBoldNumeric;
        }

        @Override
        public Font getBoldStandardChangedFont() {
            return theBoldChanged;
        }

        @Override
        public Font getBoldNumericChangedFont() {
            return theBoldChangedNumeric;
        }
    }

    /**
     * Colour Set.
     */
    private static class MetisSwingFieldColorSet
            extends MetisFieldColorSet<Color> {
        /**
         * The standard colour.
         */
        private Color theStandard;

        /**
         * The changed colour.
         */
        private Color theChanged;

        /**
         * The error colour.
         */
        private Color theError;

        /**
         * The zebra colour.
         */
        private Color theZebra;

        /**
         * The disabled colour.
         */
        private Color theDisabled;

        @Override
        protected void generateColors(final String pStandard,
                                      final String pChanged,
                                      final String pError,
                                      final String pZebra,
                                      final String pDisabled) {
            theStandard = Color.decode(pStandard);
            theChanged = Color.decode(pChanged);
            theError = Color.decode(pError);
            theZebra = Color.decode(pZebra);
            theDisabled = Color.decode(pDisabled);
        }

        @Override
        public Color getStandardColor() {
            return theStandard;
        }

        @Override
        public Color getChangedColor() {
            return theChanged;
        }

        @Override
        public Color getErrorColor() {
            return theError;
        }

        @Override
        public Color getZebraColor() {
            return theZebra;
        }

        @Override
        public Color getDisabledColor() {
            return theDisabled;
        }
    }
}
