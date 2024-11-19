/*******************************************************************************
 * Coeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.coeus.ui;

import java.io.File;

import net.sourceforge.joceanus.metis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * Preferences for Coeus.
 */
public interface CoeusPreference {
    /**
     * svnPreferenceKeys.
     */
    enum CoeusPreferenceKey implements MetisPreferenceKey {
        /**
         * Statement Repository Base.
         */
        BASE("StatementRoot", CoeusUIResource.PREFERENCE_BASE),

        /**
         * Calendar Years.
         */
        CALENDARYEAR("useCalendarYear", CoeusUIResource.PREFERENCE_CALENDAR);

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
        CoeusPreferenceKey(final String pName,
                           final CoeusUIResource pDisplay) {
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
     * CoeusPreferences.
     */
    class CoeusPreferences
            extends MetisPreferenceSet {
        /**
         * Base value for directories.
         */
        private static final String BASE_DIR = System.getProperty("user.home") + File.separator;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public CoeusPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, "Coeus Preferences");
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineDirectoryPreference(CoeusPreferenceKey.BASE);
            defineBooleanPreference(CoeusPreferenceKey.CALENDARYEAR);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the server is specified */
            final MetisStringPreference myPref = getStringPreference(CoeusPreferenceKey.BASE);
            if (!myPref.isAvailable()) {
                myPref.setValue(BASE_DIR);
            }

            /* Make sure that the name is specified */
            final MetisBooleanPreference myBoolPref = getBooleanPreference(CoeusPreferenceKey.CALENDARYEAR);
            if (!myBoolPref.isAvailable()) {
                myBoolPref.setValue(Boolean.FALSE);
            }
        }
    }
}
