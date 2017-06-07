/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions;

import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Quicken Preferences.
 */
public final class QIFPreference {
    /**
     * Constructor.
     */
    private QIFPreference() {
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
         * @param pName the name
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
            extends MetisPreferenceSet<MoneyWiseQIFPreferenceKey> {
        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public MoneyWiseQIFPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MoneyWiseQIFPreferenceKey.class, "QIF Preferences");
        }

        @Override
        protected void definePreferences() throws OceanusException {
            defineDirectoryPreference(MoneyWiseQIFPreferenceKey.QIFDIR);
            defineEnumPreference(MoneyWiseQIFPreferenceKey.QIFTYPE, QIFType.class);
            defineDatePreference(MoneyWiseQIFPreferenceKey.LASTEVENT);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the directory is specified */
            MetisStringPreference<MoneyWiseQIFPreferenceKey> myDirPref = getStringPreference(MoneyWiseQIFPreferenceKey.QIFDIR);
            if (!myDirPref.isAvailable()) {
                myDirPref.setValue(System.getProperty("user.home"));
            }

            /* Make sure that the QIFType is specified */
            MetisEnumPreference<MoneyWiseQIFPreferenceKey, QIFType> myTypePref = getEnumPreference(MoneyWiseQIFPreferenceKey.QIFTYPE, QIFType.class);
            if (!myTypePref.isAvailable()) {
                myTypePref.setValue(QIFType.ACEMONEY);
            }

            /* Make sure that the eventDate is specified */
            MetisDatePreference<MoneyWiseQIFPreferenceKey> myPref = getDatePreference(MoneyWiseQIFPreferenceKey.LASTEVENT);
            if (!myPref.isAvailable()) {
                myPref.setValue(new TethysDate());
            }
        }
    }
}