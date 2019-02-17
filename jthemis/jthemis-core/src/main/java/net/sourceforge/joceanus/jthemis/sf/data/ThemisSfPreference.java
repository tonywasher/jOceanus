/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jthemis.sf.data;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SourceForge preference keys.
 */
public interface ThemisSfPreference {
    /**
     * sfPreferenceKeys.
     */
    enum ThemisSfPreferenceKey implements MetisPreferenceKey {
        /**
         * SourceForge User.
         */
        USER("User", "User Name"),

        /**
         * SourceForge TicketSet.
         */
        TICKETSET("TicketSet", "TicketSet Name"),

        /**
         * SourceForge Bearer Access Token.
         */
        BEARER("BearerToken", "Bearer Access Token");

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
         * @param pDisplay the display string;
         */
        ThemisSfPreferenceKey(final String pName,
                              final String pDisplay) {
            theName = pName;
            theDisplay = pDisplay;
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
     * ThemisSfPreferences.
     */
    class ThemisSfPreferences
            extends MetisPreferenceSet<ThemisSfPreferenceKey> {
        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public ThemisSfPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, ThemisSfPreferenceKey.class, "SourceForge Preferences");
        }

        @Override
        protected void definePreferences() {
            defineStringPreference(ThemisSfPreferenceKey.USER);
            defineStringPreference(ThemisSfPreferenceKey.TICKETSET);
            defineStringPreference(ThemisSfPreferenceKey.BEARER);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the project is specified */
            MetisStringPreference<ThemisSfPreferenceKey> myPref = getStringPreference(ThemisSfPreferenceKey.USER);
            if (!myPref.isAvailable()) {
                myPref.setValue("User");
            }

            /* Make sure that the ticketSet is specified */
            myPref = getStringPreference(ThemisSfPreferenceKey.TICKETSET);
            if (!myPref.isAvailable()) {
                myPref.setValue("Tickets");
            }

            /* Make sure that the bearer token is specified */
            myPref = getStringPreference(ThemisSfPreferenceKey.BEARER);
            if (!myPref.isAvailable()) {
                myPref.setValue("00000000000000000000");
            }
        }
    }
}
