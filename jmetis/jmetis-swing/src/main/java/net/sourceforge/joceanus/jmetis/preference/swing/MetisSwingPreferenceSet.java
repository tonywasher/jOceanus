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
package net.sourceforge.joceanus.jmetis.preference.swing;

import java.awt.Color;
import java.awt.Font;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiUtils;

/**
 * Wrapper class for java preferences.
 * @author Tony Washer
 */
public abstract class MetisSwingPreferenceSet
        extends MetisPreferenceSet {
    /**
     * Font separator.
     */
    private static final String FONT_SEPARATOR = ":";

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public MetisSwingPreferenceSet() throws OceanusException {
    }

    /**
     * Define new Colour preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected ColorPreference defineColorPreference(final String pName,
                                                    final Color pDefault) {
        /* Define the preference */
        ColorPreference myPref = new ColorPreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Define new Font preference.
     * @param pName the name of the preference
     * @param pDefault the default value of the preference
     * @return the preference item
     */
    protected FontPreference defineFontPreference(final String pName,
                                                  final Font pDefault) {
        /* Define the preference */
        FontPreference myPref = new FontPreference(pName, pDefault);

        /* Add it to the list of preferences */
        definePreference(myPref);

        /* Return the preference */
        return myPref;
    }

    /**
     * Obtain Colour value.
     * @param pName the name of the preference
     * @return the Colour
     */
    public Color getColorValue(final String pName) {
        /* Access preference */
        MetisPreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof ColorPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access preference */
        ColorPreference myColorPref = (ColorPreference) myPref;

        /* Return the value */
        return myColorPref.getValue();
    }

    /**
     * Obtain Font value.
     * @param pName the name of the preference
     * @return the Font
     */
    public Font getFontValue(final String pName) {
        /* Access preference */
        MetisPreferenceItem myPref = getPreference(pName);

        /* Reject if not found */
        if (myPref == null) {
            throw new IllegalArgumentException(ERROR_UNKNOWN
                                               + pName);
        }

        /* Reject if wrong type */
        if (!(myPref instanceof FontPreference)) {
            throw new IllegalArgumentException(ERROR_INVALID
                                               + pName);
        }

        /* Access preference */
        FontPreference myFontPref = (FontPreference) myPref;

        /* Return the value */
        return myFontPref.getValue();
    }

    /**
     * Colour preference.
     */
    public class ColorPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public ColorPreference(final String pName,
                               final Color pDefault) {
            /* Store name */
            super(pName, pDefault, MetisPreferenceType.COLOR);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = readTheValue();
                if (myValue == null) {
                    bExists = false;
                } else {
                    /* Parse the Colour */
                    Color myColor = Color.decode(myValue);

                    /* Set as initial value */
                    setTheValue(myColor);
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
        }

        @Override
        public Color getValue() {
            return (Color) super.getValue();
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Color pNewValue) {
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            storeTheValue(TethysSwingGuiUtils.colorToHexString((Color) pNewValue));
        }
    }

    /**
     * Font preference.
     */
    public class FontPreference
            extends MetisPreferenceItem {
        /**
         * Constructor.
         * @param pName the name of the preference
         * @param pDefault the default value
         */
        public FontPreference(final String pName,
                              final Font pDefault) {
            /* Store name */
            super(pName, pDefault, MetisPreferenceType.FONT);

            /* Check whether we have an existing value */
            boolean bExists = checkExists(pName);

            /* If it exists */
            if (bExists) {
                /* Access the value */
                String myValue = readTheValue();
                if (myValue == null) {
                    bExists = false;
                } else {
                    /* Parse the value */
                    int myPos = myValue.indexOf(FONT_SEPARATOR);
                    bExists = myPos > 0;

                    /* If we look good */
                    if (bExists) {
                        /* Access parts */
                        int mySize = Integer.parseInt(myValue.substring(myPos + 1));
                        String myName = myValue.substring(0, myPos);

                        /* Create the font */
                        Font myFont = new Font(myName, Font.PLAIN, mySize);

                        /* Set as initial value */
                        setTheValue(myFont);
                    }
                }
            }

            /* if value does not exist or is invalid */
            if (!bExists) {
                /* Use default as a changed value */
                super.setNewValue(pDefault);
            }
        }

        @Override
        public Font getValue() {
            return (Font) super.getValue();
        }

        /**
         * Set value.
         * @param pNewValue the new value
         */
        public void setValue(final Font pNewValue) {
            /* Set the new value */
            super.setNewValue(pNewValue);
        }

        @Override
        protected void storeThePreference(final Object pNewValue) {
            /* Store the value */
            Font myFont = (Font) pNewValue;
            storeTheValue(myFont.getFontName()
                          + FONT_SEPARATOR
                          + myFont.getSize());
        }
    }
}
