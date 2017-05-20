/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.threads;

import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Thread Preferences.
 */
public class MetisThreadPreference {
    /**
     * ThreadPreferences.
     */
    public enum MetisThreadPreferenceKey implements MetisPreferenceKey {
        /**
         * Granularity.
         */
        REPSTEPS("ReportingSteps", MetisThreadResource.THDPREF_REPORTING);

        /**
         * The name of the Preference.
         */
        private final String theName;

        /**
         * The display name of the Preference.
         */
        private final String theDisplay;

        /**
         * Constructor.
         * @param pName the name
         * @param pDisplay the display name
         */
        MetisThreadPreferenceKey(final String pName,
                                 final MetisThreadResource pDisplay) {
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
     * MetisThreadPreferences.
     */
    public static class MetisThreadPreferences
            extends MetisPreferenceSet<MetisThreadPreferenceKey> {
        /**
         * Default Reporting Steps.
         */
        private static final Integer MINIMUM_REPSTEPS = 1;

        /**
         * Maximum Reporting Steps.
         */
        private static final Integer MAXIMUM_REPSTEPS = 100;

        /**
         * Default Reporting Steps.
         */
        private static final Integer DEFAULT_REPSTEPS = 10;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public MetisThreadPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MetisThreadPreferenceKey.class, MetisThreadResource.THDPREF_PREFNAME);
        }

        @Override
        protected void definePreferences() {
            defineIntegerPreference(MetisThreadPreferenceKey.REPSTEPS);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the value is specified */
            MetisIntegerPreference<MetisThreadPreferenceKey> myPref = getIntegerPreference(MetisThreadPreferenceKey.REPSTEPS);
            if (!myPref.isAvailable()) {
                myPref.setValue(DEFAULT_REPSTEPS);
            }

            /* Define the range */
            myPref.setRange(MINIMUM_REPSTEPS, MAXIMUM_REPSTEPS);
            if (!myPref.validate()) {
                myPref.setValue(DEFAULT_REPSTEPS);
            }
        }
    }
}
