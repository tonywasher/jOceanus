/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.ui;

import java.util.HashMap;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueKey;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueSet;

/**
 * Preferences for colours.
 */
public interface MetisFieldColours {
    /**
     * colourPreferenceKeys.
     */
    enum MetisColorPreferenceKey implements MetisPreferenceKey {
        /**
         * Standard Colour.
         */
        STANDARD("ColorStandard", MetisColorResource.FIELDCOLOR_STANDARD),

        /**
         * Background Colour.
         */
        BACKGROUND("ColorBackground", MetisColorResource.FIELDCOLOR_BACKGROUND),

        /**
         * Error Colour.
         */
        ERROR("ColorError", MetisColorResource.FIELDCOLOR_ERROR),

        /**
         * Changed Colour.
         */
        CHANGED("ColorChanged", MetisColorResource.FIELDCOLOR_CHANGED),

        /**
         * Disabled Colour.
         */
        DISABLED("ColorDisabled", MetisColorResource.FIELDCOLOR_DISABLED),

        /**
         * Zebra Colour.
         */
        ZEBRA("ColorZebra", MetisColorResource.FIELDCOLOR_ZEBRA),

        /**
         * Progress Colour.
         */
        PROGRESS("ColorProgress", MetisColorResource.FIELDCOLOR_PROGRESS),

        /**
         * Link colour.
         */
        LINK("Link", MetisColorResource.FIELDCOLOR_LINK),

        /**
         * Value colour.
         */
        VALUE("ColorValue", MetisColorResource.FIELDCOLOR_VALUE),

        /**
         * Negative Value colour.
         */
        NEGATIVE("ColorNegative", MetisColorResource.FIELDCOLOR_NEGATIVE),

        /**
         * Security Changed colour.
         */
        SECURITY("ColorSecurity", MetisColorResource.FIELDCOLOR_SECURITY),

        /**
         * Header colour.
         */
        HEADER("ColorHeader", MetisColorResource.FIELDCOLOR_HEADER);

        /**
         * The name of the Preference.
         */
        private final String theName;

        /**
         * The display string.
         */
        private final String theDisplay;

        /**
         * Constructor.
         * @param pName the name
         * @param pDisplay the display string resource
         */
        MetisColorPreferenceKey(final String pName,
                                final MetisColorResource pDisplay) {
            theName = pName;
            theDisplay = pDisplay.getValue();
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public String getDisplay() {
            return theDisplay;
        }
    }

    /**
     * MetisColorPreferences.
     */
    class MetisColorPreferences
            extends MetisPreferenceSet {
        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public MetisColorPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MetisColorResource.FIELDCOLOR_PREFS);
        }

        /**
         * Update the valueSet.
         * @param pValueSet the value set
         */
        public void updateValueSet(final TethysUIValueSet pValueSet) {
            /* Create the Colour Map */
            final HashMap<String, String> myMap = new HashMap<>();

            /* Populate the map */
            myMap.put(TethysUIValueKey.COLOR_STANDARD, getStringValue(MetisColorPreferenceKey.STANDARD));
            myMap.put(TethysUIValueKey.COLOR_BACKGROUND, getStringValue(MetisColorPreferenceKey.BACKGROUND));
            myMap.put(TethysUIValueKey.COLOR_ERROR, getStringValue(MetisColorPreferenceKey.ERROR));
            myMap.put(TethysUIValueKey.COLOR_CHANGED, getStringValue(MetisColorPreferenceKey.CHANGED));
            myMap.put(TethysUIValueKey.COLOR_DISABLED, getStringValue(MetisColorPreferenceKey.DISABLED));
            myMap.put(TethysUIValueKey.COLOR_ZEBRA, getStringValue(MetisColorPreferenceKey.ZEBRA));
            myMap.put(TethysUIValueKey.COLOR_LINK, getStringValue(MetisColorPreferenceKey.LINK));
            myMap.put(TethysUIValueKey.COLOR_VALUE, getStringValue(MetisColorPreferenceKey.VALUE));
            myMap.put(TethysUIValueKey.COLOR_NEGATIVE, getStringValue(MetisColorPreferenceKey.NEGATIVE));
            myMap.put(TethysUIValueKey.COLOR_SECURITY, getStringValue(MetisColorPreferenceKey.SECURITY));
            myMap.put(TethysUIValueKey.COLOR_HEADER, getStringValue(MetisColorPreferenceKey.HEADER));
            myMap.put(TethysUIValueKey.COLOR_PROGRESS, getStringValue(MetisColorPreferenceKey.PROGRESS));

            /* Apply settings */
            pValueSet.applyColorMapping(myMap);
        }

        @Override
        protected void definePreferences() {
            defineColorPreference(MetisColorPreferenceKey.STANDARD);
            defineColorPreference(MetisColorPreferenceKey.BACKGROUND);
            defineColorPreference(MetisColorPreferenceKey.ERROR);
            defineColorPreference(MetisColorPreferenceKey.CHANGED);
            defineColorPreference(MetisColorPreferenceKey.DISABLED);
            defineColorPreference(MetisColorPreferenceKey.ZEBRA);
            defineColorPreference(MetisColorPreferenceKey.LINK);
            defineColorPreference(MetisColorPreferenceKey.VALUE);
            defineColorPreference(MetisColorPreferenceKey.NEGATIVE);
            defineColorPreference(MetisColorPreferenceKey.SECURITY);
            defineColorPreference(MetisColorPreferenceKey.HEADER);
            defineColorPreference(MetisColorPreferenceKey.PROGRESS);
        }

        @Override
        public void autoCorrectPreferences() {
            defaultColour(MetisColorPreferenceKey.STANDARD, TethysUIValueKey.DEFAULT_COLOR_STANDARD);
            defaultColour(MetisColorPreferenceKey.BACKGROUND, TethysUIValueKey.DEFAULT_COLOR_BACKGROUND);
            defaultColour(MetisColorPreferenceKey.ERROR, TethysUIValueKey.DEFAULT_COLOR_ERROR);
            defaultColour(MetisColorPreferenceKey.CHANGED, TethysUIValueKey.DEFAULT_COLOR_CHANGED);
            defaultColour(MetisColorPreferenceKey.DISABLED, TethysUIValueKey.DEFAULT_COLOR_DISABLED);
            defaultColour(MetisColorPreferenceKey.ZEBRA, TethysUIValueKey.DEFAULT_COLOR_ZEBRA);
            defaultColour(MetisColorPreferenceKey.PROGRESS, TethysUIValueKey.DEFAULT_COLOR_PROGRESS);
            defaultColour(MetisColorPreferenceKey.LINK, TethysUIValueKey.DEFAULT_COLOR_LINK);
            defaultColour(MetisColorPreferenceKey.VALUE, TethysUIValueKey.DEFAULT_COLOR_VALUE);
            defaultColour(MetisColorPreferenceKey.NEGATIVE, TethysUIValueKey.DEFAULT_COLOR_NEGATIVE);
            defaultColour(MetisColorPreferenceKey.SECURITY, TethysUIValueKey.DEFAULT_COLOR_SECURITY);
            defaultColour(MetisColorPreferenceKey.HEADER, TethysUIValueKey.DEFAULT_COLOR_HEADER);
        }

        /**
         * Set default colour.
         * @param pKey the key
         * @param pDefault the default colour
         */
        private void defaultColour(final MetisColorPreferenceKey pKey,
                                   final String pDefault) {
            final MetisStringPreference myPref = getStringPreference(pKey);
            if (!myPref.isAvailable()) {
                myPref.setValue(pDefault);
            }
        }
    }
}
