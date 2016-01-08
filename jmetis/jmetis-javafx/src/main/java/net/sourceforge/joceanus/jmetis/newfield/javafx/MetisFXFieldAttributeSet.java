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
package net.sourceforge.joceanus.jmetis.newfield.javafx;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import net.sourceforge.joceanus.jmetis.newfield.MetisFieldAttributeSet;

/**
 * JavaFX AttributeSet.
 */
public class MetisFXFieldAttributeSet
        extends MetisFieldAttributeSet<Font, Color> {
    /**
     * Constructor.
     */
    public MetisFXFieldAttributeSet() {
        super(new MetisFXFieldFontSet(), new MetisFXFieldColorSet());
    }

    /**
     * Colour Set.
     */
    private static class MetisFXFieldFontSet
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
            theStandard = Font.font(pStandard, pPitch);
            theNumeric = Font.font(pNumeric, pPitch);
            theChanged = Font.font(pStandard, FontPosture.ITALIC, pPitch);
            theChangedNumeric = Font.font(pNumeric, FontPosture.ITALIC, pPitch);
            theBoldStandard = Font.font(pStandard, FontWeight.BOLD, pPitch + 2);
            theBoldNumeric = Font.font(pNumeric, FontWeight.BOLD, pPitch + 2);
            theBoldChanged = Font.font(pStandard, FontWeight.BOLD, FontPosture.ITALIC, pPitch + 2);
            theBoldChangedNumeric = Font.font(pNumeric, FontWeight.BOLD, FontPosture.ITALIC, pPitch + 2);
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
    private static class MetisFXFieldColorSet
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
            theStandard = Color.valueOf(pStandard);
            theChanged = Color.valueOf(pChanged);
            theError = Color.valueOf(pError);
            theZebra = Color.valueOf(pZebra);
            theDisabled = Color.valueOf(pDisabled);
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
