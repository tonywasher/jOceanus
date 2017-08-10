/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.lethe.preference;

import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DataList preferences.
 * @author Tony Washer
 */
public final class PrometheusDataList {
    /**
     * Constructor.
     */
    private PrometheusDataList() {
    }

    /**
     * DataListPreferences.
     */
    public enum PrometheusDataListPreferenceKey implements MetisPreferenceKey {
        /**
         * Granularity.
         */
        GRANULARITY("Granularity", PrometheusPreferenceResource.DLPREF_GRANULARITY);

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
        PrometheusDataListPreferenceKey(final String pName,
                                        final PrometheusPreferenceResource pDisplay) {
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
     * PrometheusDataListPreferences.
     */
    public static class PrometheusDataListPreferences
            extends MetisPreferenceSet<PrometheusDataListPreferenceKey> {
        /**
         * Minimum Granularity.
         */
        private static final int MIN_GRANULARITY = 3;

        /**
         * Maximum Granularity.
         */
        private static final int MAX_GRANULARITY = 10;

        /**
         * Default Granularity.
         */
        private static final int DEFAULT_GRANULARITY = 5;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public PrometheusDataListPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, PrometheusDataListPreferenceKey.class, PrometheusPreferenceResource.DLPREF_PREFNAME);
        }

        @Override
        protected void definePreferences() {
            defineIntegerPreference(PrometheusDataListPreferenceKey.GRANULARITY);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the value is specified */
            final MetisIntegerPreference<PrometheusDataListPreferenceKey> myPref = getIntegerPreference(PrometheusDataListPreferenceKey.GRANULARITY);
            if (!myPref.isAvailable()) {
                myPref.setValue(DEFAULT_GRANULARITY);
            }

            /* Define the range */
            myPref.setRange(MIN_GRANULARITY, MAX_GRANULARITY);
            if (!myPref.validate()) {
                myPref.setValue(DEFAULT_GRANULARITY);
            }
        }
    }
}
