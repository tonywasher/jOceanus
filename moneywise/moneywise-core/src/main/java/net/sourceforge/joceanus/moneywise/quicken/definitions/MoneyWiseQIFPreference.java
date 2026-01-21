/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.quicken.definitions;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet;

/**
 * Quicken Preferences.
 */
public final class MoneyWiseQIFPreference {
    /**
     * Constructor.
     */
    private MoneyWiseQIFPreference() {
    }

    /**
     * QIFPreferenceKeys.
     */
    public enum MoneyWiseQIFPreferenceKey implements MetisPreferenceKey {
        /**
         * QIF directory.
         */
        QIFDIR("QIFDir", "Output Directory"),

        /**
         * QIF Type.
         */
        QIFTYPE("QIFType", "Output Type"),

        /**
         * Database Driver.
         */
        LASTEVENT("LastEvent", "Last Event");

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
         *
         * @param pName    the name
         * @param pDisplay the display string;
         */
        MoneyWiseQIFPreferenceKey(final String pName,
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
     * QIF Preferences.
     */
    public static class MoneyWiseQIFPreferences
            extends MetisPreferenceSet {
        /**
         * Constructor.
         *
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public MoneyWiseQIFPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, "QIF Preferences");
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineDirectoryPreference(MoneyWiseQIFPreferenceKey.QIFDIR);
            defineEnumPreference(MoneyWiseQIFPreferenceKey.QIFTYPE, MoneyWiseQIFType.class);
            defineDatePreference(MoneyWiseQIFPreferenceKey.LASTEVENT);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the directory is specified */
            final MetisStringPreference myDirPref = getStringPreference(MoneyWiseQIFPreferenceKey.QIFDIR);
            if (!myDirPref.isAvailable()) {
                myDirPref.setValue(System.getProperty("user.home"));
            }

            /* Make sure that the QIFType is specified */
            final MetisEnumPreference<MoneyWiseQIFType> myTypePref = getEnumPreference(MoneyWiseQIFPreferenceKey.QIFTYPE, MoneyWiseQIFType.class);
            if (!myTypePref.isAvailable()) {
                myTypePref.setValue(MoneyWiseQIFType.ACEMONEY);
            }

            /* Make sure that the eventDate is specified */
            final MetisDatePreference myPref = getDatePreference(MoneyWiseQIFPreferenceKey.LASTEVENT);
            if (!myPref.isAvailable()) {
                myPref.setValue(new OceanusDate());
            }
        }
    }
}
